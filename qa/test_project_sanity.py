import json
import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"

class ProjectSanity(unittest.TestCase):
    def test_neoforge_metadata(self):
        props = (ROOT / "gradle.properties").read_text()
        self.assertIn("minecraft_version=1.21.1", props)
        self.assertIn("mod_id=gugugaga", props)
        self.assertIn("mod_license=MIT", props)
        self.assertIn("neo_version=21.1.", props)
        self.assertIn('modLoader="javafml"', (ROOT / "src/main/templates/META-INF/neoforge.mods.toml").read_text())

    def test_source_and_client_separation(self):
        source = (SRC / "GuguGagaMod.java").read_text()
        self.assertIn("registerEntityType", source)
        self.assertNotIn("net.minecraft.client", source)
        client = (SRC / "client/ClientEvents.java").read_text()
        self.assertIn("Dist.CLIENT", client)
        self.assertIn("registerEntityRenderer", client)

    def test_observer_is_nonviolent_and_bounded(self):
        brain = (SRC / "ai/ObserverBrain.java").read_text()
        mob = (SRC / "entity/GugugagaEntity.java").read_text()
        for key in ("DORMANT", "OBSERVE", "APPROACH", "RETREAT"):
            self.assertIn(key, brain)
        self.assertIn("tickCount % 10", mob)
        self.assertIn("getNearestPlayer(this, 32.0)", mob)
        self.assertIn("getNavigation().stop()", mob)
        self.assertNotIn("hurt(", mob)
        self.assertNotIn("teleportTo(", mob)
        self.assertNotIn("getChunk(", mob)

    def test_locale_and_art_provenance(self):
        for locale in ("en_us", "zh_cn"):
            data = json.loads((ROOT / f"src/main/resources/assets/gugugaga/lang/{locale}.json").read_text())
            self.assertIn("entity.gugugaga.observer", data)
        self.assertTrue((ROOT / "docs/ASSET_PROVENANCE.md").exists())

if __name__ == "__main__":
    unittest.main()
