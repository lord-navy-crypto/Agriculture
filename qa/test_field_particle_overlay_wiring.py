"""Strict source check that virtual signals render as opt-in vanilla particles."""
from pathlib import Path
import unittest
R=Path(__file__).resolve().parents[1]
J=R/"src/main/java/io/github/lordnavycrypto/gugugaga"
class OptInParticles(unittest.TestCase):
 def test_isolating_true_data_field_and_client(self):
  runtime=(J/"pathogens/FieldParticleOverlayRuntime.java").read_text()
  math=(J/"pathogens/FieldParticleSampling.java").read_text()
  cmds=(J/"command/PathogenSpatialFieldCommands.java").read_text()
  config=(J/"config/NightwatchConfig.java").read_text()
  main=(J/"GuguGagaMod.java").read_text()
  self.assertIn("FieldParticleOverlayRuntime::onTick",main)
  self.assertIn("FieldParticleOverlayRuntime::onLogout",main)
  self.assertIn("FieldParticleOverlayRuntime::onServerStopped",main)
  self.assertIn("getChunkSource().hasChunk(",runtime)
  self.assertIn("level.sendParticles(player,",runtime)
  self.assertIn("new WeakHashMap<>()",runtime)
  self.assertIn("MAX_PARTICLES_PER_VIEWER=12",runtime)
  self.assertIn("MAX_VIEWERS_PER_FRAME=12",runtime)
  self.assertIn("MAX_MARKERS=6",math)
  self.assertIn("MAX_VISITS=49",math)
  self.assertIn('Commands.literal("particles")',cmds)
  self.assertIn('Commands.literal("select")',cmds)
  self.assertIn("FIELD_PARTICLE_OVERLAY_ENABLED",config)
  self.assertIn("FieldParticleSamplingSmoke",
                (R/".github/workflows/build.yml").read_text())
  for dangerous in ("setBlock(", "forceChunk(", "EntityType.Builder",
                    "DeferredRegister.Blocks", "addEffect(", "setHealth("):
   self.assertNotIn(dangerous,math+runtime)
 def test_original_saved_files_are_untouched(self):
  core=(J/"pathogens/PathogenSpatialFieldSavedData.java").read_text()
  other=(J/"pathogens/EcologyExtendedSavedData.java").read_text()
  self.assertIn('FILE_ID="gugugaga_spatial_pathogen_field_v1"',core)
  self.assertIn('FILE="gugugaga_ecology_extension_v1"',other)
if __name__=="__main__":unittest.main()
