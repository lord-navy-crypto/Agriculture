"""Cross-version release-candidate integration gates for all three actors."""
import pathlib
import re
import unittest

ROOT=pathlib.Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"

class SacredNightwatchIntegration(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.rt=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        cls.cmd=(SRC/"command/GuguPlayCommands.java").read_text()
        cls.mod=(SRC/"GuguGagaMod.java").read_text()
        cls.bird=(SRC/"entity/GugugagaEntity.java").read_text()
        cls.redactor=(SRC/"entity/RedactorEntity.java").read_text()
        cls.smiler=(SRC/"entity/YellowSmilerEntity.java").read_text()

    def test_all_three_registered_with_real_commands_and_server_logic(self):
        for marker in ("ENTITIES.register(\"observer\"",
                       "ENTITIES.register(\"redactor\"",
                       "ENTITIES.register(\"yellow_smiler\"",
                       "HauntGameplayRuntime::onTick",
                       "NightwatchDirector::onTick"):
            self.assertIn(marker,self.mod)
        for marker in ("Commands.literal(\"join\")","Commands.literal(\"leave\")",
                       "Commands.literal(\"status\")","Commands.literal(\"listen\")",
                       "Commands.literal(\"help\")","Commands.literal(\"test\")",
                       "Commands.literal(\"witness\")","Commands.literal(\"triad\")",
                       "Commands.literal(\"investigate\")"):
            self.assertIn(marker,self.cmd)

    def test_operator_scene_requires_opted_in_night_survival(self):
        self.assertIn("requires(src->src.hasPermission(2))",self.cmd)
        self.assertIn("startOperatorTestScene(ServerPlayer player,",self.rt)
        self.assertIn("if(!isActiveParticipant(player))",self.rt)
        self.assertIn("entry.act=allThree?HauntGameplayPolicy.Act.FALSE_SAFETY",self.rt)
        for type_id in ("OBSERVER","REDACTOR","YELLOW_SMILER"):
            self.assertIn(f"ensureNearby(level,player,GuguGagaMod.{type_id}.get())",self.rt)
        self.assertIn("Missing actors retry twice",self.rt)

    def test_repeatable_stage_timing_recovers_from_obstructed_spawn(self):
        for state in ("STILLNESS","WHISPERS","WITNESS","FALSE_SAFETY","RESPITE"):
            self.assertIn(state,self.rt)
        for part in ("e.witnessSpawnAttempts<3",
                     "e.redactorSpawnAttempts<3",
                     "e.smilerSpawnAttempts<3",
                     "e.activeTicks%(20*20)==0"):
            self.assertIn(part,self.rt)
        self.assertIn("for(int i=0;i<8;i++)",self.rt)
        self.assertIn("long speciesSalt=",self.rt)
        self.assertIn("GuguGagaMod.REDACTOR.get()",self.rt)
        self.assertIn("GuguGagaMod.YELLOW_SMILER.get()",self.rt)
        self.assertIn("level.noCollision(spawned)",self.rt)
        self.assertIn("getChunkSource().hasChunk",self.rt)
        self.assertIn("global>=6",self.rt)

    def test_triad_behavior_and_visual_connections_survive(self):
        self.assertIn("MimicDeceptionPolicy.shouldCreep(",self.redactor)
        self.assertIn("MimicDeceptionPolicy.canShowTell(",self.redactor)
        self.assertIn('triggerAnim("tell_gesture","tell")',self.redactor)
        self.assertIn("bird.retreatFromSmiler(this)",self.smiler)
        self.assertIn("respondToHelpCall(ServerPlayer caller)",self.smiler)
        self.assertIn("HauntGameplayRuntime.smilerAidEnabled(p)",self.smiler)
        self.assertIn('triggerAnim("gift_gesture","ward")',self.smiler)
        self.assertIn("HorrorHearingPolicy.hearsRunner(",self.bird)
        self.assertIn("LastSeenSearchPolicy.beginSearch(",self.bird)
        self.assertIn("SEARCHING",self.bird)
        self.assertIn("CaveThreatSoundPolicy.classify(true,true,",self.rt)
        self.assertIn("HorrorListeningPolicy.mayListen(",self.rt)
        self.assertIn("HorrorEvidencePolicy.description(trace)",self.rt)

    def test_no_player_teleport_or_chunk_forcing_and_no_unneeded_external_dependencies(self):
        for file in (self.rt,self.bird,self.redactor,self.smiler):
            for prohibited in ("forceChunk(","teleportTo(","setBlock(","System.exit("):
                self.assertNotIn(prohibited,file)
        self.assertIn("NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()",self.rt)
        self.assertIn("player.hasLineOfSight(other)",self.rt)
        self.assertIn("isActiveParticipant(player)",self.rt)
        self.assertIn("Math.min(Integer.MAX_VALUE-20,e.activeTicks+20)",self.rt)
        props=(ROOT/"gradle.properties").read_text()
        self.assertIn("mod_version=0.20.1-alpha.1",props)
        self.assertIn("geckolib_version=4.9.3",props)

if __name__=="__main__":
    unittest.main()
