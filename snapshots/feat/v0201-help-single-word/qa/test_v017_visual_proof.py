"""Gaze geometry, server-only chase knowledge, deterministic generated visual proof."""
import importlib.util
import json
import pathlib
import subprocess
import sys
import tempfile
import unittest

ROOT=pathlib.Path(__file__).resolve().parents[1]
ASSETS=ROOT/"src/main/resources/assets/gugugaga"
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/entity/GugugagaEntity.java"

class V017VisualReview(unittest.TestCase):
    def test_parented_eyes_do_not_separate_from_head(self):
        j=json.loads((ASSETS/"geo/observer.geo.json").read_text())
        bones={b["name"]:b for b in j["minecraft:geometry"][0]["bones"]}
        self.assertTrue({"head","left_eye","right_eye","beak"} <= bones.keys())
        for name in ("left_eye","right_eye","beak"):
            self.assertEqual(bones[name]["parent"],"head")
            self.assertEqual(len(bones[name]["cubes"]),1)
        self.assertEqual(len(bones["head"]["cubes"]),3)
        self.assertEqual(len(set(bones)),len(bones))

    def test_search_pose_and_private_target_memory(self):
        j=json.loads((ASSETS/"animations/observer.animation.json").read_text())
        self.assertIn("animation.gugugaga.search",j["animations"])
        self.assertTrue(j["animations"]["animation.gugugaga.search"]["loop"])
        for clip in ("watch","search"):
            keys=j["animations"]["animation.gugugaga."+clip]["bones"]
            self.assertIn("left_eye",keys)
            self.assertIn("right_eye",keys)
            self.assertIn("position",keys["left_eye"])
        mob=JAVA.read_text()
        for k in ("EntityDataAccessor<Boolean> SEARCHING","builder.define(SEARCHING,false)",
                  "if (searching()) return state.setAndContinue(SEARCH)",
                  "setSearching(searchTicks>0)", "setSearching(false)",
                  "lastSeenChasePosition=player.position()"):
            self.assertIn(k,mob)
        self.assertNotIn('tag.putDouble("lastSeenChase',mob)
        self.assertNotIn('tag.putUUID("chaseTarget',mob)

    def test_two_independent_proofs_match(self):
        generator=ROOT/"qa/generate_character_sprites.py"
        review=ROOT/"qa/render_visual_review.py"
        with tempfile.TemporaryDirectory() as temp:
            root=pathlib.Path(temp)
            out=root/"generated"
            subprocess.run([sys.executable,str(generator),str(out)],check=True,capture_output=True)
            hashes=[]
            for run in ("A","B"):
                folder=root/run
                subprocess.run([sys.executable,str(review),str(out),str(folder)],
                               check=True,capture_output=True)
                manifest=json.loads((folder/"gugugaga-v017-material-manifest.json").read_text())
                self.assertFalse(manifest["minecraft_screenshot"])
                self.assertEqual(len(manifest["textures"]),5)
                self.assertEqual(manifest["width"],1412)
                self.assertEqual(manifest["height"],390)
                self.assertGreater((folder/"gugugaga-v017-material-review.png").stat().st_size,20000)
                hashes.append(manifest["sheet_sha256"])
            self.assertEqual(hashes[0],hashes[1])

if __name__=="__main__":
    unittest.main()
