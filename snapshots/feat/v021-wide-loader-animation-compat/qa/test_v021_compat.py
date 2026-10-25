"""Test honest compatibility floors and real CI version builds."""
import pathlib,unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
class Compat(unittest.TestCase):
    def test_props(self):
        p=(ROOT/"gradle.properties").read_text()
        for x in ("neo_version=21.1.100","neo_min_version=21.1.100",
                  "geckolib_version=4.8.2","geckolib_min_version=4.8.2",
                  "minecraft_version=1.21.1","mod_version=0.21.0-rc.1"):
            self.assertIn(x,p)
    def test_metadata(self):
        g=(ROOT/"build.gradle").read_text()
        t=(ROOT/"src/main/templates/META-INF/neoforge.mods.toml").read_text()
        self.assertIn("neo_min_version: neo_min_version",g)
        self.assertIn("geckolib_min_version: geckolib_min_version",g)
        for x in ("neo_min_version","geckolib_min_version","minecraft_version_range",
                  'modId="geckolib"','type="required"'):
            self.assertIn(x,t)
        self.assertNotIn("[4.9.3,5.0.0)",t)
    def test_matrix(self):
        s=(ROOT/".github/workflows/build.yml").read_text()
        self.assertIn("compatibility:",s)
        for item in ("neo: '21.1.100'","neo: '21.1.200'","neo: '21.1.259'",
                     "gecko: '4.8.2'","gecko: '4.8.4'","gecko: '4.9.3'"):
            self.assertIn(item,s)
        self.assertIn("-Pneo_version=",s)
        self.assertIn("-Pgeckolib_version=",s)
if __name__=="__main__":unittest.main()
