import pathlib,unittest,json
ROOT=pathlib.Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
ASSETS=ROOT/"src/main/resources/assets/gugugaga/animations"

class V015Regression(unittest.TestCase):
    def test_optional_player_owned_rescue(self):
        commands=(JAVA/"command/GuguPlayCommands.java").read_text()
        runtime=(JAVA/"ai/HauntGameplayRuntime.java").read_text()
        smiler=(JAVA/"entity/YellowSmilerEntity.java").read_text()
        for key in ('Commands.literal("help")','Commands.literal("aid")',
                    'Commands.literal("on")','Commands.literal("off")'):
            self.assertIn(key,commands)
        self.assertIn("requestSmilerHelp(ServerPlayer player)",runtime)
        self.assertIn("player.hasLineOfSight(yellow)",runtime)
        self.assertIn("SmilerTrustPolicy.canRequestAid(",runtime)
        self.assertIn("smilerAidAllowed",runtime)
        self.assertIn("smilerFavors",runtime)
        self.assertIn("public boolean respondToHelpCall(ServerPlayer caller)",smiler)
        self.assertIn("HauntGameplayRuntime.smilerAidEnabled(p)",smiler)
        self.assertIn("hasLineOfSight(bird)",smiler)
        self.assertIn("bird.retreatFromSmiler(this)",smiler)
        self.assertIn("HauntGameplayRuntime.recordSmilerFavor",smiler)
        self.assertIn("triggerAnim(\"gift_gesture\",\"ward\")",smiler)
        for bad in ("forceChunk(", "getChunk(", "teleportTo(", "setBlock("):
            self.assertNotIn(bad,smiler)
            self.assertNotIn(bad,runtime)

    def test_staged_sightings_are_replayable_and_bounded(self):
        runtime=(JAVA/"ai/HauntGameplayRuntime.java").read_text()
        self.assertIn("next==HauntGameplayPolicy.Act.WITNESS",runtime)
        self.assertIn("GuguGagaMod.OBSERVER.get()",runtime)
        self.assertIn("e.witnessSpawnAttempts<3",runtime)
        self.assertIn("observer.markAmbientEncounter()",runtime)
        self.assertIn("summonOne(level,player,GuguGagaMod.REDACTOR.get())",runtime)
        self.assertIn("summonOne(level,player,GuguGagaMod.YELLOW_SMILER.get())",runtime)
        self.assertIn("getChunkSource().hasChunk",runtime)
        self.assertIn("global>=6",runtime)
        self.assertNotIn("forceChunk(",runtime)

    def test_disguise_with_observable_tells(self):
        redactor=(JAVA/"entity/RedactorEntity.java").read_text()
        for key in ("MimicDeceptionPolicy.shouldCreep(", "MimicDeceptionPolicy.canShowTell(",
                    "isPlayerStaring(ServerPlayer player)", "visiblePlayerSq",
                    "visibleBirdSq", "player.playNotifySound(", "tell_gesture"):
            self.assertIn(key,redactor)
        self.assertIn("HauntGameplayRuntime.inChaseAct(player)",redactor)
        self.assertNotIn("teleportTo(",redactor)
        for filename,clip in (("redactor","animation.redactor.mask_tell"),
                              ("yellow_smiler","animation.smiler.ward")):
            data=json.loads((ASSETS/(filename+".animation.json")).read_text())
            self.assertIn(clip,data["animations"])
            self.assertFalse(data["animations"][clip]["loop"])

if __name__=="__main__":
    unittest.main()
