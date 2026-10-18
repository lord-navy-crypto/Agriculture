"""Verify complete client/server natural-gameplay wiring and old contracts."""
import pathlib
import unittest
ROOT=pathlib.Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
class NaturalInteractions(unittest.TestCase):
    def test_chat_is_exact_server_event_cancellable_and_remains_private(self):
        s=(SRC/"ai/YellowHelpChatEvents.java").read_text()
        for k in ("ServerChatEvent","getRawText()","YellowHelpChatPolicy.isHelpRequest(",
                  "event.setCanceled(true)","HauntGameplayRuntime.requestSmilerHelp(player)",
                  "NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()",
                  "player.serverLevel().getServer().execute("):
            self.assertIn(k,s)
        policy=(SRC/"ai/YellowHelpChatPolicy.java").read_text()
        self.assertIn('message.trim().equals("HELP")',policy)
        self.assertNotIn("help yellow face",policy)
        self.assertNotIn("toLowerCase",policy)
        self.assertNotIn("contains(\"help\")",policy)
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        self.assertIn("SmilerTrustPolicy.canRequestAid(",runtime)
        self.assertIn("best.respondToHelpCall(player)",runtime)
    def test_listen_is_client_only_remappable_key_with_server_validation(self):
        k=(SRC/"client/NightwatchListenKey.java").read_text()
        for marker in ("Dist.CLIENT","EventBusSubscriber.Bus.MOD",
                       "RegisterKeyMappingsEvent","ClientTickEvent.Post",
                       "event.register(LISTEN)","GLFW.GLFW_KEY_V",
                       "LISTEN.consumeClick()","mc.player.isCrouching()",
                       'sendCommand("gugugaga play listen")'):
            self.assertIn(marker,k)
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        self.assertIn("HorrorListeningPolicy.mayListen(true,crouching",runtime)
        self.assertIn("public static boolean listenFromPanel(ServerPlayer player)",runtime)
        self.assertIn('Commands.literal("listen-panel")',(SRC/"command/GuguPlayCommands.java").read_text())
        menu=(SRC/"client/NightwatchScreen.java").read_text()
        self.assertIn('screen.gugugaga.nightwatch.join',menu)
        self.assertIn('screen.gugugaga.nightwatch.help',menu)
        self.assertIn('mc.getConnection().sendCommand(command)',menu)
        self.assertIn("GLFW.GLFW_KEY_H",k)
        self.assertIn("event.register(MENU)",k)
        self.assertIn("player.displayClientMessage(Component.literal(\"[Nightwatch] \"+clue),true)",runtime)
        self.assertIn("entry.lastListenTick=now",runtime)
        self.assertIn("HorrorListeningPolicy.hint(source,direction,nearest)",runtime)
        self.assertIn("getEntitiesOfClass(",runtime)
        for lang in ("en_us.json","zh_cn.json"):
            import json
            j=json.loads((ROOT/"src/main/resources/assets/gugugaga/lang"/lang).read_text())
            self.assertIn("key.gugugaga.listen",j)
            self.assertIn("key.categories.gugugaga",j)
    def test_gaze_advances_real_five_act_loop_without_chat(self):
        runtime=(SRC/"ai/HauntGameplayRuntime.java").read_text()
        for marker in ("focusedEvidence(player.serverLevel(),player,e.act)",
                       "GazeInvestigationPolicy.nextTicks(", "GazeInvestigationPolicy.complete(",
                       "investigate(player)", "focusedNearest(", "entity.getEyePosition()",
                       "player.hasLineOfSight(entity)","GazeInvestigationPolicy.aimedAt(",
                       "e.gazeFocusTicks=0","entry.gazeFocusTicks=0",
                       "HauntGameplayPolicy.Act.RESPITE","HauntGameplayPolicy.Act.STILLNESS"):
            self.assertIn(marker,runtime)
        self.assertIn("investigate(ctx.getSource())",
                      (SRC/"command/GuguPlayCommands.java").read_text())
    def test_rc_package_is_versioned_single_jar(self):
        p=(ROOT/"qa/package_sacred_rc.py").read_text()
        self.assertIn('VERSION="0.21.3-rc.1"',p)
        self.assertIn('NATURAL_PLAYTEST_MANIFEST.json',p)
        self.assertIn("all_three_actor_classes_verified",p)
        self.assertIn("all_6_texture_png_verified",p)
if __name__=="__main__":unittest.main()
