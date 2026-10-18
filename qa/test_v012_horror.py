import pathlib
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"

class V012Horror(unittest.TestCase):
    def test_personal_opt_in_and_accessibility(self):
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        commands=(SRC/"command/GuguPlayCommands.java").read_text()
        config=(SRC/"config/NightwatchConfig.java").read_text()
        self.assertIn("inChaseAct(ServerPlayer player)",runtime)
        self.assertIn("isActiveParticipant(ServerPlayer player)",runtime)
        self.assertIn("entry.scaresAllowed",runtime)
        self.assertIn("HorrorChasePolicy.canSurprise(",runtime)
        self.assertIn('Commands.literal("scares")',commands)
        self.assertIn('define("horror_chases",true)',config)
        self.assertIn('define("horror_jumpscares",true)',config)
        for bad in ("setGameMode(", "teleportTo(", "forceChunk(", "System.exit("):
            self.assertNotIn(bad,runtime)

    def test_live_triads_and_pursuits(self):
        bird=(SRC/"entity/GugugagaEntity.java").read_text()
        redactor=(SRC/"entity/RedactorEntity.java").read_text()
        yellow=(SRC/"entity/YellowSmilerEntity.java").read_text()
        for marker in ("handleHorrorPursuit(", "HorrorChasePolicy.canStart(",
                       "HorrorChasePolicy.canContinue(", "retreatFromSmiler("):
            self.assertIn(marker,bird)
        self.assertIn("HauntGameplayRuntime.inChaseAct",yellow)
        self.assertIn("getBoundingBox().inflate(22.0)",redactor)
        self.assertIn("bird.retreatFromSmiler(this)",yellow)
        self.assertIn("bird.hurt(",yellow)
        self.assertIn("ThreeEntityReactionPolicy.redactor(",redactor)
        self.assertIn("case WATCH_PLAYER",redactor)
        self.assertIn("HauntGameplayRuntime.inChaseAct(player)",redactor)
        self.assertIn('triggerAnim("shock","lunge")',bird)
        for entity in (bird,redactor,yellow):
            for bad in ("forceChunk(", "getChunk(", "teleportTo(", "setBlock(",
                        "explode(", "net.minecraft.client"):
                self.assertNotIn(bad,entity)

if __name__=="__main__":
    unittest.main()
