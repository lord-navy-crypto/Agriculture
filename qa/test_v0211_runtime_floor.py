"""Protect against v0.21.0's demonstrated broken NeoForge/GeckoLib floor."""
import pathlib,unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
class RuntimeFloor(unittest.TestCase):
    def test_loader_minimum_and_packager_are_coherent(self):
        props=(ROOT/"gradle.properties").read_text()
        self.assertIn("neo_version=21.1.150",props)
        self.assertIn("neo_min_version=21.1.150",props)
        self.assertNotIn("neo_min_version=21.1.100",props)
        self.assertIn("geckolib_min_version=4.8.2",props)
        self.assertIn("mod_version=0.21.2-rc.1",props)
        pack=(ROOT/"qa/package_sacred_rc.py").read_text()
        self.assertIn('versionRange="[21.1.150,)"',pack)
        self.assertIn("NeoForge 21.1.150+",pack)
        self.assertIn('VERSION="0.21.2-rc.1"',pack)
    def test_reliably_runs_minecraft_not_just_compiles_java(self):
        workflow=(ROOT/".github/workflows/build.yml").read_text()
        self.assertIn("runtime-startup:",workflow)
        self.assertIn("smoke_server_boot.py",workflow)
        for v in ("21.1.150","21.1.200","21.1.259","4.8.2","4.8.4","4.9.3"):
            self.assertIn(v,workflow)
        boot=(ROOT/"qa/smoke_server_boot.py").read_text()
        self.assertIn("GUGUGAGA_RUNTIME_INIT_OK",boot)
        self.assertIn("SERVER_READY",boot)
        self.assertIn('command.append("runServer")',boot)
if __name__=="__main__":unittest.main()
