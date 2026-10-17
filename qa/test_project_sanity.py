import json
import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"

class ProjectSanity(unittest.TestCase):
    def test_neoforge_metadata(self):
        props = (ROOT / "gradle.properties").read_text()
        self.assertIn("minecraft_version=1.21.1", props)
        self.assertIn("mod_id=gugugaga", props)
        self.assertIn("mod_license=MIT", props)
        self.assertIn("neo_version=21.1.", props)
        self.assertIn('modLoader="javafml"', (ROOT / "src/main/templates/META-INF/neoforge.mods.toml").read_text())

    def test_source_and_client_separation(self):
        source = (SRC / "GuguGagaMod.java").read_text()
        self.assertIn("DeferredRegister.create(Registries.ENTITY_TYPE", source)
        self.assertIn("EntityType.Builder", source)
        self.assertNotIn("net.minecraft.client", source)
        client = (SRC / "client/ClientEvents.java").read_text()
        self.assertIn("Dist.CLIENT", client)
        self.assertIn("registerEntityRenderer", client)

    def test_observer_is_nonviolent_and_bounded(self):
        brain = (SRC / "ai/ObserverBrain.java").read_text()
        mob = (SRC / "entity/GugugagaEntity.java").read_text()
        for key in ("DORMANT", "OBSERVE", "APPROACH", "RETREAT"):
            self.assertIn(key, brain)
        self.assertIn("tickCount%10", mob)
        self.assertIn("eligibleNearestPlayer", mob)
        self.assertIn("getEntitiesOfClass(ServerPlayer.class,bounds", mob)
        self.assertIn("NightwatchDirector.isQuiet", mob)
        self.assertIn("markAmbientEncounter", mob)
        self.assertIn("tickCount%30==0", mob)
        self.assertIn("getNavigation().stop()", mob)
        self.assertNotIn("hurt(", mob)
        self.assertNotIn("teleportTo(", mob)
        self.assertNotIn("getChunk(", mob)

    def test_v03_haunting_is_bounded_and_does_not_force_chunks(self):
        sound=(SRC / "ai/HauntingCuePolicy.java").read_text()
        director=(SRC / "ai/NightwatchDirector.java").read_text()
        config=(SRC / "config/NightwatchConfig.java").read_text()
        mob=(SRC / "entity/GugugagaEntity.java").read_text()
        model=(SRC / "client/ObserverModel.java").read_text()
        for literal in ("CHIME_PERIOD_TICKS = 240", "STEP_PERIOD_TICKS = 160",
                        "PULSE_PERIOD_TICKS = 300", "cappedVolume"):
            self.assertIn(literal,sound)
        self.assertIn('define("haunting_audio", true)',config)
        self.assertIn('define("haunting_particles", true)',config)
        self.assertIn("observer.markAmbientEncounter();",director)
        self.assertIn("ambientLifetimeTicks>=AUTO_LIFETIME_TICKS",mob)
        self.assertIn("getAmbientSound()",mob)
        self.assertIn("return null;",mob)
        self.assertIn('root.getChild("head")',model)
        self.assertIn('head.addOrReplaceChild("left_eye"',model)
        self.assertIn("NightwatchDirector.isQuiet",mob)
        for forbidden in ("forceChunk(", "getChunk(", "teleportTo(",
                          "setBlock(", "explode(", "hurt("):
            self.assertNotIn(forbidden,mob)

    def test_live_atmosphere_controls_remain_operator_only(self):
        commands=(SRC / "command/NightwatchCommands.java").read_text()
        self.assertIn('Commands.literal("atmosphere")',commands)
        self.assertIn('Commands.literal("on")',commands)
        self.assertIn('Commands.literal("off")',commands)
        self.assertIn('requires(src->src.hasPermission(2))',commands)
        self.assertIn("HAUNTING_AUDIO_ENABLED.set(enabled)",commands)
        self.assertIn("HAUNTING_PARTICLES_ENABLED.set(enabled)",commands)

    def test_v04_private_witness_journal_is_bounded_and_opt_out_friendly(self):
        mob=(SRC / "entity/GugugagaEntity.java").read_text()
        journal=(SRC / "ai/WitnessJournal.java").read_text()
        policy=(SRC / "ai/WitnessJournalPolicy.java").read_text()
        commands=(SRC / "command/NightwatchCommands.java").read_text()
        config=(SRC / "config/NightwatchConfig.java").read_text()
        self.assertIn("WitnessJournal.record(player,phase(),watched,distance",mob)
        self.assertIn('Commands.literal("journal")',commands)
        for op in ("on","off","clear"):
            self.assertIn(f'Commands.literal("{op}")',commands)
        self.assertIn("getPlayerOrException()",commands)
        self.assertIn('define("hint_messages", true)',config)
        self.assertIn('define("enabled", true)',config)
        self.assertIn("player.getPersistentData()",journal)
        self.assertIn("JournalDisabled",journal)
        self.assertIn("EvidenceBits",journal)
        self.assertIn("getInt(EVIDENCE)",journal)
        self.assertIn("state.remove(EVIDENCE)",journal)
        self.assertIn("ALL_BITS=7",policy)
        self.assertIn("Double.isNaN",policy)
        self.assertNotIn("HttpClient",journal)
        self.assertNotIn("getChunk(",journal)
        self.assertNotIn("getBlockState(",journal)
        self.assertNotIn("player.position()",journal)
        self.assertNotIn("net.minecraft.client",journal)

    def test_local_ai_is_optional_and_non_authoritative(self):
        config = (SRC / "config/NightwatchConfig.java").read_text()
        client = (SRC / "ai/ollama/OllamaClient.java").read_text()
        endpoint = (SRC / "ai/ollama/OllamaEndpointPolicy.java").read_text()
        commands = (SRC / "command/OllamaCommands.java").read_text()
        self.assertIn('.define("enabled", false)', config)
        self.assertIn('"/api/tags"', client)
        self.assertIn('"/api/chat"', client)
        self.assertIn("sendAsync", client)
        self.assertIn("Redirect.NEVER", client)
        self.assertIn('"127.0.0.1"', endpoint)
        self.assertIn('"/api/chat"', endpoint)
        self.assertIn("src.hasPermission(2)", commands)
        self.assertIn("not applied", commands)
        self.assertNotIn("getNavigation()", commands)
        self.assertNotIn("teleportTo(", commands)

    def test_locale_and_art_provenance(self):
        for locale in ("en_us", "zh_cn"):
            data = json.loads((ROOT / f"src/main/resources/assets/gugugaga/lang/{locale}.json").read_text())
            self.assertIn("entity.gugugaga.observer", data)
            for key in ("header","gaze","footsteps","retreat","hidden",
                        "ending","status","enabled","disabled","hint",
                        "on","off","clear"):
                self.assertIn("message.gugugaga.journal."+key,data)
        self.assertTrue((ROOT / "docs/ASSET_PROVENANCE.md").exists())

if __name__ == "__main__":
    unittest.main()
