"""v0.10 integration boundaries: avoid stage softlocks and opt-out regressions.

These source guards complement (but cannot replace) a real NeoForge client test.
"""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"


class V010StageSafetyTests(unittest.TestCase):
    def read(self, relative):
        return (SRC / relative).read_text(encoding="utf-8")

    def test_scripted_witness_is_only_for_joined_night_players(self):
        runtime = self.read("ai/HauntGameplayRuntime.java")
        policy = self.read("ai/HauntGameplayPolicy.java")
        self.assertIn("if(e==null || !eligible(player,true))continue;", runtime)
        self.assertIn("shouldSeekStageActor(e.act,e.activeTicks)", runtime)
        self.assertIn("GuguGagaMod.OBSERVER.get()", runtime)
        self.assertIn("GuguGagaMod.REDACTOR.get()", runtime)
        self.assertIn("gugu.markAmbientEncounter()", runtime)
        self.assertIn("redactor.markStageEncounter()", runtime)
        self.assertIn("STAGE_ACTOR_FIRST_TICKS=20*8", policy)
        self.assertIn("STAGE_ACTOR_RETRY_TICKS=20*20", policy)
        self.assertIn("active.keySet().retainAll(online)", runtime)

    def test_no_force_loading_unbounded_spawns_or_unwanted_targets(self):
        runtime = self.read("ai/HauntGameplayRuntime.java")
        self.assertIn("getChunkSource().hasChunk", runtime)
        self.assertIn("level.noCollision(spawned)", runtime)
        self.assertIn("global>=6", runtime)
        self.assertIn("player.hasLineOfSight(other)", runtime)
        self.assertIn("return !NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()", runtime)
        self.assertIn("|| isJoined(player.serverLevel().getServer(),player.getUUID())", runtime)
        for mob in ("GugugagaEntity.java","RedactorEntity.java","YellowSmilerEntity.java"):
            source = self.read("entity/" + mob)
            self.assertIn("HauntGameplayRuntime.canTargetPlayer(p)", source, mob)


if __name__ == "__main__":
    unittest.main()
