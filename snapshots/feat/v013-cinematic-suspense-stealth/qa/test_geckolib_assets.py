"""Asset + integration invariants. Client/dedicated-server acceptance remains manual."""
import json
import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[1]
ASSETS = ROOT / "src/main/resources/assets/gugugaga"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"

CASES = {
    "observer": {
        "models": ["observer"],
        "bones": {"body", "head", "left_wing", "right_wing"},
        "animations": {"idle", "watch", "walk", "retreat", "angry", "corrupted", "blackened", "lunge"},
        "entity": "GugugagaEntity",
        "renderer": "ObserverRenderer",
    },
    "redactor": {
        "models": ["redactor_lapis", "redactor_true"],
        "bones": {"lapis", "revealed", "mask", "left_arm", "right_arm"},
        "animations": {"lapis_idle", "true_idle"},
        "entity": "RedactorEntity",
        "renderer": "RedactorRenderer",
    },
    "smiler": {
        "models": ["yellow_smiler"],
        "bones": {"body", "head", "left_arm", "right_arm"},
        "animations": {"friendly", "exposed", "help"},
        "entity": "YellowSmilerEntity",
        "renderer": "YellowSmilerRenderer",
    },
}

class GeckoLibAssetTests(unittest.TestCase):
    def test_required_loader_and_version(self):
        props = (ROOT / "gradle.properties").read_text()
        meta = (ROOT / "src/main/templates/META-INF/neoforge.mods.toml").read_text()
        gradle = (ROOT / "build.gradle").read_text()
        self.assertIn("geckolib_version=4.9.3", props)
        self.assertIn('modId="geckolib"', meta)
        self.assertIn('side="BOTH"', meta)
        self.assertIn("software.bernie.geckolib:geckolib-neoforge-", gradle)

    def test_geometry_matches_animated_bones(self):
        for character, spec in CASES.items():
            with self.subTest(character=character):
                all_bones = set()
                for filename in spec["models"]:
                    data = json.loads((ASSETS / f"geo/{filename}.geo.json").read_text())
                    self.assertEqual(data["format_version"], "1.12.0")
                    geometry = data["minecraft:geometry"][0]
                    self.assertEqual(geometry["description"]["texture_width"], 64)
                    self.assertEqual(geometry["description"]["texture_height"], 64)
                    names = [bone["name"] for bone in geometry["bones"]]
                    self.assertEqual(len(names), len(set(names)))
                    self.assertTrue(all(bone.get("cubes") for bone in geometry["bones"]))
                    all_bones.update(names)
                self.assertTrue(spec["bones"] <= all_bones)
                data = json.loads((ASSETS / f"animations/{'yellow_smiler' if character=='smiler' else character}.animation.json").read_text())
                self.assertEqual(data["format_version"], "1.8.0")
                clips = data["animations"]
                self.assertEqual(spec["animations"], {name.rsplit(".", 1)[-1] for name in clips})
                for name, clip in clips.items():
                    self.assertGreater(clip["animation_length"], 0)
                    self.assertTrue(set(clip["bones"]) <= all_bones, name)
                    for anim_bone in clip["bones"].values():
                        self.assertIn("rotation", anim_bone)

    def test_entity_geo_renderers_and_synced_gameplay_states(self):
        for spec in CASES.values():
            entity = (JAVA / f"entity/{spec['entity']}.java").read_text()
            renderer = (JAVA / f"client/{spec['renderer']}.java").read_text()
            self.assertIn("GeoEntity", entity)
            self.assertIn("registerControllers(", entity)
            self.assertIn("getAnimatableInstanceCache()", entity)
            self.assertIn("GeoEntityRenderer", renderer)
        self.assertIn("corruptionStage()", (JAVA / "client/ObserverGeoModel.java").read_text())
        self.assertIn("revealed()", (JAVA / "client/RedactorGeoModel.java").read_text())
        self.assertIn("revealed()", (JAVA / "client/YellowSmilerGeoModel.java").read_text())
        self.assertIn('triggerAnim("gift_gesture", "help")',
                      (JAVA / "entity/YellowSmilerEntity.java").read_text())

if __name__ == "__main__":
    unittest.main()
