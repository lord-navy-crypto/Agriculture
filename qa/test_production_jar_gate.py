"""Verify CI checks the actual downloadable JAR, not moddev development classes."""
import pathlib,unittest
R=pathlib.Path(__file__).resolve().parents[1]
class PackagedConsumer(unittest.TestCase):
 def test_uses_fresh_production_server_with_proper_dependencies(self):
  text=(R/"qa/production_jar_server_smoke.py").read_text()
  for marker in ('build/libs/gugugaga-0.21.3-rc.1.jar',
    'dl.cloudsmith.io/public/geckolib3/geckolib/maven/',
    'maven.neoforged.net/releases/',
    '--installServer','mods=OUT/"mods"','GUGUGAGA_RUNTIME_INIT_OK',
    'GUGUGAGA_PRODUCTION_JAR_SERVER_PASS','For help, type'):
   self.assertIn(marker,text)
 def test_real_ci_gate(self):
  w=(R/".github/workflows/build.yml").read_text()
  self.assertIn("production-jar-consumer:",w)
  self.assertIn("qa/production_jar_server_smoke.py",w)
if __name__=="__main__":unittest.main()
