"""Budgeted, valid 64x64 original art and animation: no shaders required."""
import json
import pathlib
import struct
import subprocess
import sys
import tempfile
import unittest
import zlib

ROOT=pathlib.Path(__file__).resolve().parents[1]
ASSET=ROOT/"src/main/resources/assets/gugugaga"
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
GEO=["observer","redactor_lapis","redactor_true","yellow_smiler"]
ANIM=["observer","redactor","yellow_smiler"]

class VisualPolishQA(unittest.TestCase):
    def test_geometries_are_valid_small_and_animated(self):
        total=0
        for stem in GEO:
            j=json.loads((ASSET/"geo"/(stem+".geo.json")).read_text())
            geom=j["minecraft:geometry"][0]
            self.assertEqual(geom["description"]["texture_width"],64)
            self.assertEqual(geom["description"]["texture_height"],64)
            bones=geom["bones"]
            names=[b["name"] for b in bones]
            self.assertEqual(len(names),len(set(names)))
            for b in bones:
                for cube in b["cubes"]:
                    origin=cube["origin"]; size=cube["size"]; uv=cube["uv"]
                    self.assertEqual(len(origin),3)
                    self.assertEqual(len(size),3)
                    self.assertTrue(all(0 < float(n)<=24 for n in size))
                    self.assertTrue(all(abs(float(n))<=40 for n in origin))
                    self.assertGreaterEqual(uv[0],0)
                    self.assertGreaterEqual(uv[1],0)
                    # Bedrock box net UV may not spill beyond a 64x64 PNG.
                    self.assertLessEqual(uv[0]+2*(size[0]+size[2]),64.001)
                    self.assertLessEqual(uv[1]+size[1]+size[2],64.001)
                    total+=1
        self.assertGreaterEqual(total,40)
        for stem in ANIM:
            j=json.loads((ASSET/"animations"/(stem+".animation.json")).read_text())
            for name,clip in j["animations"].items():
                max_time=float(clip["animation_length"])
                for bone,tracks in clip["bones"].items():
                    self.assertIn("rotation",tracks,(stem,name,bone))
                    for prop,frames in tracks.items():
                        self.assertIn(prop,("rotation","position","scale"))
                        for key,vec in frames.items():
                            self.assertLessEqual(float(key),max_time+1e-6,name)
                            self.assertGreaterEqual(float(key),0)
                            self.assertEqual(len(vec),3)
                            self.assertTrue(all(abs(float(n))<=100 for n in vec))

    def test_texture_generator_builds_original_64px_sprites(self):
        with tempfile.TemporaryDirectory() as d:
            subprocess.run([sys.executable,str(ROOT/"qa/generate_character_sprites.py"),d],
                           check=True,capture_output=True,text=True)
            folder=pathlib.Path(d)/"assets/gugugaga/textures/entity"
            for stem in ["lapis_mask","redactor_true","yellow_smiler",
                         "yellow_smiler_true","observer_blackened"]:
                blob=(folder/(stem+".png")).read_bytes()
                self.assertEqual(blob[:8],b"\x89PNG\r\n\x1a\n")
                self.assertEqual(struct.unpack(">II",blob[16:24]),(64,64))
                self.assertGreater(len(blob),350,(stem,len(blob)))
                # Original per-pixel shading, not five flat/color-swapped skins.
                self.assertIn(b"IDAT",blob)

    def test_action_particles_are_bounded_and_optional(self):
        policy=(SRC/"ai/CinematicVfxPolicy.java").read_text()
        self.assertIn("MAX_PARTICLES_PER_EVENT=4",policy)
        for f in ["entity/RedactorEntity.java","entity/YellowSmilerEntity.java",
                  "entity/GugugagaEntity.java"]:
            s=(SRC/f).read_text()
            self.assertIn("CinematicVfxPolicy.count(",s)
            self.assertIn("NightwatchConfig.HAUNTING_PARTICLES_ENABLED.get()",s)
            for disallow in ("forceChunk(","getChunk(","teleportTo(",
                             "setBlock(", "explode("):
                self.assertNotIn(disallow,s)
        cfg=(SRC/"config/NightwatchConfig.java").read_text()
        self.assertIn('define("haunting_particles", true)',cfg)

if __name__=="__main__":
    unittest.main()
