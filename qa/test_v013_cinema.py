"""Runtime integration invariants; never confuse CI with actual client playtests."""
import pathlib
import unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"

class CinematicHorror(unittest.TestCase):
    def test_private_multiplayer_sound(self):
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        self.assertIn("CinematicSuspensePolicy.choose(",runtime)
        self.assertIn("player.playNotifySound(sound,SoundSource.AMBIENT",runtime)
        self.assertNotIn("level.playSound(null,player.blockPosition()",runtime)
        self.assertIn("NightwatchConfig.HAUNTING_AUDIO_ENABLED.get()",runtime)
        self.assertIn("source instanceof RedactorEntity",runtime)
        assert "Math.floorMod" in (SRC/"ai/CinematicSuspensePolicy.java").read_text()
    def test_chase_uses_last_seen_not_hidden_player_location(self):
        bird=(SRC/"entity/GugugagaEntity.java").read_text()
        self.assertIn("chaseTargetId=player.getUUID()",bird)
        self.assertIn("lastSeenChasePosition=player.position()",bird)
        self.assertIn("LastSeenSearchPolicy.beginSearch(",bird)
        self.assertIn("LastSeenSearchPolicy.useLastSeen(",bird)
        self.assertIn("getNavigation().moveTo(last.x,last.y,last.z",bird)
        self.assertIn("java.util.UUID chaseTargetId",bird)
        self.assertIn("chaseTargetId=null;",bird)
        self.assertIn("HauntGameplayRuntime.inChaseAct(player)",bird)
        for bad in ("getChunk(", "forceChunk(", "teleportTo(", "setBlock(",
                    "setGameMode(", "System.exit("):
            self.assertNotIn(bad,bird)
    def test_previous_entities_and_five_acts_intact(self):
        logic=(SRC/"ai/HauntGameplayPolicy.java").read_text()
        for act in ("STILLNESS","WHISPERS","WITNESS","FALSE_SAFETY","RESPITE"):
            self.assertIn(act,logic)
        roles=(SRC/"ai/ThreeEntityReactionPolicy.java").read_text()
        for field in ("FOLLOW_PLAYER","WATCH_PLAYER","CONFRONT_GUGU","PROTECT_PLAYER"):
            self.assertIn(field,roles)

if __name__=="__main__":
    unittest.main()
