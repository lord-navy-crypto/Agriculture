"""Reject regressions that make v0.10 gameplay dependent on typed commands."""
import json
from pathlib import Path
import unittest

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
LANG=ROOT/"src/main/resources/assets/gugugaga/lang"


class NativeGameplayUITests(unittest.TestCase):
    def get(self,path):
        return (SRC/path).read_text(encoding="utf-8")

    def test_keybinding_and_native_screen(self):
        keys=self.get("client/NightwatchKey.java")
        ui=self.get("client/NightwatchScreen.java")
        self.assertIn("RegisterKeyMappingsEvent",keys)
        self.assertIn("GLFW.GLFW_KEY_H",keys)
        self.assertIn("Minecraft.getInstance()",ui)
        self.assertIn("Button.builder",ui)
        self.assertIn("PacketDistributor.sendToServer(new NightwatchActionPacket(action))",ui)
        for forbidden in ("sendCommand(", "sendChat(", "performCommand(", "sendChatCommand("):
            self.assertNotIn(forbidden,ui)
            self.assertNotIn(forbidden,keys)
        for act in ("JOIN", "LEAVE", "STATUS", "QUIET_ON", "QUIET_OFF",
                    "SERVER_ON", "SERVER_OFF","JOURNAL"):
            self.assertIn("NightwatchActionPacket."+act,ui)

    def test_packet_is_server_authoritative(self):
        payload=self.get("network/NightwatchActionPacket.java")
        mod=self.get("GuguGagaMod.java")
        self.assertIn("RegisterPayloadHandlersEvent",payload)
        self.assertIn("playToServer(TYPE,STREAM_CODEC",payload)
        self.assertIn("ctx.player() instanceof ServerPlayer",payload)
        self.assertIn("createCommandSourceStack().hasPermission(2)",payload)
        self.assertIn("case JOIN",payload)
        self.assertIn("case LEAVE",payload)
        self.assertIn("case SERVER_ON, SERVER_OFF",payload)
        self.assertIn("WitnessJournal.show(player)",payload)
        self.assertIn("modBus.addListener(NightwatchActionPacket::register)",mod)

    def test_real_entities_are_interactable_and_no_required_commands(self):
        runtime=self.get("ai/HauntGameplayRuntime.java")
        self.assertIn("investigate(ServerPlayer player,Entity target)",runtime)
        self.assertIn("player.hasLineOfSight(target)",runtime)
        self.assertIn("player.distanceToSqr(target)",runtime)
        self.assertIn("target.level()!=level",runtime)
        self.assertIn("right-click it to investigate.",runtime)
        self.assertNotIn("then use /gugugaga investigate",runtime)
        self.assertNotIn("Use /gugugaga play leave",runtime)
        for creature in ("GugugagaEntity.java","RedactorEntity.java"):
            data=self.get("entity/"+creature)
            self.assertIn("protected InteractionResult mobInteract",data)
            self.assertIn("HauntGameplayRuntime.investigate(participant,this)",data)
        yellow=self.get("entity/YellowSmilerEntity.java")
        self.assertIn("HauntGameplayRuntime.investigate(recipient,this)",yellow)

    def test_translations_and_enabled_new_world(self):
        cfg=self.get("config/NightwatchConfig.java")
        self.assertIn('define("playable_loop",true)',cfg)
        for lang in ("en_us.json","zh_cn.json"):
            data=json.loads((LANG/lang).read_text(encoding="utf-8"))
            for key in ("key.gugugaga.menu","screen.gugugaga.nightwatch.join",
                        "screen.gugugaga.nightwatch.leave",
                        "screen.gugugaga.nightwatch.status",
                        "screen.gugugaga.nightwatch.journal",
                        "screen.gugugaga.nightwatch.server_on",
                        "screen.gugugaga.nightwatch.hint"):
                self.assertIn(key,data)


if __name__=="__main__":
    unittest.main()
