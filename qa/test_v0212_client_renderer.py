"""Client GeckoLib renderer bus and live client smoke must never regress."""
import pathlib
import unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
CLIENT=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/client"
class ClientRenderer(unittest.TestCase):
    def test_renderers_are_registered_on_client_mod_bus(self):
        src=(CLIENT/"ClientEvents.java").read_text()
        self.assertIn("value = Dist.CLIENT",src)
        self.assertIn("bus = EventBusSubscriber.Bus.MOD",src)
        self.assertIn("EntityRenderersEvent.RegisterRenderers",src)
        self.assertIn("EntityRenderersEvent.RegisterLayerDefinitions",src)
        for name in ("GuguGagaMod.OBSERVER","GuguGagaMod.REDACTOR","GuguGagaMod.YELLOW_SMILER"):
            self.assertIn(name,src)
        self.assertIn("GUGUGAGA_CLIENT_RENDERERS_REGISTERED",src)
    def test_key_input_does_not_cross_physical_side(self):
        s=(CLIENT/"NightwatchListenKey.java").read_text()
        self.assertIn("bus=EventBusSubscriber.Bus.MOD",s)
        self.assertIn("RegisterKeyMappingsEvent",s)
        self.assertIn("GUGUGAGA_CLIENT_TICK_OK",s)
        self.assertIn("value=Dist.CLIENT",s)
    def test_ci_executes_real_client(self):
        w=(ROOT/".github/workflows/build.yml").read_text()
        self.assertIn("client-startup:",w)
        self.assertIn("smoke_client_boot.py",w)
        smoke=(ROOT/"qa/smoke_client_boot.py").read_text()
        for x in ("runClient","xvfb-run","LIBGL_ALWAYS_SOFTWARE",
                  "GUGUGAGA_CLIENT_RENDERERS_REGISTERED","GUGUGAGA_CLIENT_TICK_OK"):
            self.assertIn(x,smoke)
    def test_version_matches_package(self):
        p=(ROOT/"qa/package_sacred_rc.py").read_text()
        self.assertIn('VERSION="0.21.3-rc.1"',p)
if __name__=="__main__":unittest.main()
