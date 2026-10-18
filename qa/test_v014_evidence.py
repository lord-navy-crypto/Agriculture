"""Static runtime linkage checks in addition to 500k standalone policy trials."""
import pathlib,unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class V014EvidenceTests(unittest.TestCase):
    def test_observations_are_real_and_bounded(self):
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        config=(SRC/"config/NightwatchConfig.java").read_text()
        self.assertIn("visibleNearest(level,player,GugugagaEntity.class)",runtime)
        self.assertIn("visibleNearest(level,player,RedactorEntity.class)",runtime)
        self.assertIn("visibleNearest(level,player,YellowSmilerEntity.class)",runtime)
        self.assertIn("player.hasLineOfSight(other)",runtime)
        self.assertIn("NightwatchConfig.EVIDENCE_CLUES_ENABLED.get()",runtime)
        self.assertIn("now-entry.lastEvidenceTick<20*3",runtime)
        self.assertIn('define("horror_evidence_clues",true)',config)
        self.assertIn("HorrorEvidencePolicy.description(trace)",runtime)
    def test_caves_are_adaptations_not_extra_threats(self):
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        cave=(SRC/"ai/DepthSuspensePolicy.java").read_text()
        self.assertIn("DepthSuspensePolicy.adapt(",runtime)
        self.assertIn("canSeeSky(player.blockPosition())",runtime)
        self.assertIn("CinematicSuspensePolicy.choose(",runtime)
        self.assertIn("CinematicSuspensePolicy.Cue.NONE",cave)
        for body in (runtime,cave):
            for unsafe in ("forceChunk(", "getChunk(", "teleportTo(", "System.exit("):
                self.assertNotIn(unsafe,body)
if __name__=="__main__":unittest.main()
