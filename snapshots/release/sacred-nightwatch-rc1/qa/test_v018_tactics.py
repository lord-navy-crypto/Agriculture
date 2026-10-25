"""Integration invariants: real threat, no forced world manipulation, per-player context."""
import pathlib,unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class V018Tactics(unittest.TestCase):
    def test_running_has_tactical_cost_but_stealth_is_fair(self):
        bird=(SRC/"entity/GugugagaEntity.java").read_text()
        hearing=(SRC/"ai/HorrorHearingPolicy.java").read_text()
        for value in ("HorrorHearingPolicy.hearsRunner(","player.isSprinting()",
                      "player.isCrouching()","horizontalDistanceSqr()",
                      "lastHeardChasePosition=player.position()",
                      "setSearching(searchTicks>0)","NightwatchConfig.NOISE_HUNT_ENABLED.get()"):
            self.assertIn(value,bird)
        self.assertIn("&& !crouching",hearing)
        self.assertIn("MAX_VERTICAL_DISTANCE=7.0",hearing)
        self.assertIn("HEAR_RADIUS_SQ=18.0*18.0",hearing)
        self.assertIn("lastHeardChasePosition=null",bird)
        self.assertIn("lastSeenChasePosition=player.position()",bird)
        self.assertNotIn('tag.putDouble("lastHeard',bird)
        self.assertNotIn('tag.putUUID("chaseTarget',bird)
        for wrong in ("teleportTo(","getChunk(","forceChunk(","setBlock("):
            self.assertNotIn(wrong,bird)
            self.assertNotIn(wrong,hearing)
    def test_player_crouch_listen_consumes_cooldown_on_empty_result(self):
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        commands=(SRC/"command/GuguPlayCommands.java").read_text()
        listening=(SRC/"ai/HorrorListeningPolicy.java").read_text()
        for marker in ("Commands.literal(\"listen\")",
                       "HauntGameplayRuntime.listen(src.getPlayerOrException())"):
            self.assertIn(marker,commands)
        for marker in ("public static boolean listen(ServerPlayer player)",
                       "HorrorListeningPolicy.mayListen(true,player.isCrouching()",
                       "entry.lastListenTick=now",
                       "getEntitiesOfClass(", "Entity::isAlive",
                       "HorrorListeningPolicy.hint(source,direction,nearest)",
                       "player.sendSystemMessage(Component.literal(\"[Nightwatch Listening] \""):
            self.assertIn(marker,runtime)
        self.assertIn("COOLDOWN_TICKS=20L*12L",listening)
        self.assertIn("MAX_DISTANCE_SQ=20.0*20.0",listening)
    def test_cave_audio_is_truly_monster_grounded(self):
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        pressure=(SRC/"ai/CaveThreatSoundPolicy.java").read_text()
        config=(SRC/"config/NightwatchConfig.java").read_text()
        self.assertIn("CaveThreatSoundPolicy.due(e.activeTicks)",runtime)
        self.assertIn("!player.serverLevel().canSeeSky(player.blockPosition())",runtime)
        self.assertIn("GugugagaEntity.class,area,Entity::isAlive",runtime)
        self.assertIn("RedactorEntity.class,area,Entity::isAlive",runtime)
        self.assertIn("CaveThreatSoundPolicy.classify(true,true,",runtime)
        self.assertIn("Double.isFinite(closestSq)",runtime)
        self.assertIn("CaveThreatSoundPolicy.volume(pressure)",runtime)
        self.assertIn("PERIOD_TICKS=20*12",pressure)
        self.assertIn("MAX_VOLUME=0.16f",pressure)
        self.assertIn('define("horror_noise_hunt",true)',config)
        self.assertIn('define("horror_cave_threat_audio",true)',config)
        self.assertIn("NightwatchConfig.HAUNTING_AUDIO_ENABLED.get()",runtime)
        # Existing five acts, original evidence gating and opt-out stay.
        for required in ("STILLNESS","WHISPERS","WITNESS","FALSE_SAFETY",
                         "RESPITE","!eligible(player,true)"):
            self.assertIn(required,runtime)
if __name__=="__main__": unittest.main()
