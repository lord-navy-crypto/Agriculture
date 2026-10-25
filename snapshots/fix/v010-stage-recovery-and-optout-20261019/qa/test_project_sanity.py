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

    def test_observer_is_player_opt_in_and_bounded(self):
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
        self.assertIn("NightwatchConfig.HOSTILITY_ENABLED.get()",mob)
        self.assertIn("ThreatPolicy.canStrike(",mob)
        self.assertIn("ATTACK_TELEGRAPH_TICKS", (SRC / "ai/ThreatPolicy.java").read_text())
        self.assertIn("player.hurt(world.damageSources().mobAttack(this)",mob)
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
                          "setBlock(", "explode("):
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

    def test_v05_director_is_loaded_only_and_nonintrusive(self):
        director=(SRC / "ai/NightwatchDirector.java").read_text()
        policy=(SRC / "ai/HauntDirectorPolicy.java").read_text()
        config=(SRC / "config/NightwatchConfig.java").read_text()
        commands=(SRC / "command/NightwatchCommands.java").read_text()
        self.assertIn("class HauntDirectorPolicy",policy)
        self.assertIn("FALSE_FOOTSTEPS",policy)
        self.assertIn("AFTERIMAGE",policy)
        self.assertIn("SILENCE",policy)
        self.assertIn("SIGHTING",policy)
        self.assertIn("Math.floorMod",policy)
        self.assertIn("HauntDirectorPolicy.choose",director)
        self.assertIn("HauntDirectorPolicy.nextTension",director)
        self.assertIn("HauntDirectorPolicy.volume(chosen)",director)
        self.assertIn("lastAttempt.put(id,state.ticks)",director)
        self.assertIn("getChunkSource().hasChunk(cx,cz)",director)
        self.assertIn("player.getPersistentData()", (SRC / "ai/WitnessJournal.java").read_text())
        self.assertIn('define("varied_events", true)',config)
        self.assertIn('defineInRange("director_intensity", 1, 0, 2)',config)
        self.assertIn('Commands.literal("director")',commands)
        self.assertIn('requires(src->src.hasPermission(2))',commands)
        for bad in ("getChunk(", "forceChunk(", "setBlock(", "explode(",
                    "teleportTo(", "HttpClient", "Runtime.getRuntime()"):
            self.assertNotIn(bad,director)
            self.assertNotIn(bad,policy)

    def test_v06_penguin_climate_and_private_dialogue(self):
        bird=(SRC / "ai/ColdBiomePolicy.java").read_text()
        dialogue=(SRC / "ai/GuguDialoguePolicy.java").read_text()
        talk=(SRC / "command/GuguTalkCommands.java").read_text()
        director=(SRC / "ai/NightwatchDirector.java").read_text()
        settings=(SRC / "config/NightwatchConfig.java").read_text()
        client=(SRC / "ai/ollama/OllamaClient.java").read_text()
        self.assertIn("sightingPermille",bird)
        self.assertIn("getBaseTemperature()",director)
        self.assertIn("ColdBiomePolicy.allow(",director)
        self.assertIn('Commands.literal("talk")',talk)
        self.assertIn("getPlayerOrException()",talk)
        self.assertIn("GuguDialoguePolicy.reply(",talk)
        self.assertIn("COOLDOWN_TICKS",talk)
        self.assertIn("OllamaEndpointPolicy.DEFAULT_ENDPOINT",client)
        self.assertIn("DIALOGUE_LOCAL_AI_ENABLED",settings)
        self.assertIn("DIALOGUE_LOCAL_AI_ENABLED.get()",talk)
        self.assertIn("DIALOGUE_ENABLED.get()",talk)
        self.assertIn("safeAnswer",dialogue)
        for forbidden in ("ChatEvent", "setBlock(", "forceChunk(", "getChunk("):
            self.assertNotIn(forbidden,talk)
        self.assertNotIn("getPlayerList().broadcast",talk)

    def test_v07_villain_and_aggression_are_opt_in_and_reversible(self):
        threat=(SRC / "ai/ThreatPolicy.java").read_text()
        bird=(SRC / "entity/GugugagaEntity.java").read_text()
        story=(SRC / "ai/StoryLedger.java").read_text()
        commands=(SRC / "command/GuguStoryCommands.java").read_text()
        config=(SRC / "config/NightwatchConfig.java").read_text()
        model=(SRC / "client/ObserverModel.java").read_text()
        dialogue=(SRC / "ai/GuguDialoguePolicy.java").read_text()
        self.assertIn("enum Mood { CALM, ANGRY, CORRUPTED }",threat)
        self.assertIn("ATTACK_TELEGRAPH_TICKS = 30",threat)
        self.assertIn("ATTACK_COOLDOWN_TICKS = 75",threat)
        self.assertIn("!optedOut",threat)
        self.assertIn("hasSight",threat)
        self.assertIn("ThreatPolicy.afterTime",bird)
        self.assertIn("getEntityData().set(MOOD",bird)
        self.assertIn("NightwatchDirector.isQuiet",bird)
        self.assertIn('define("hostility_enabled",false)',config)
        for literal in ("temper","angry","corrupted","calm","reassure",
                        "hostility","trust","redactor","guardian"):
            self.assertIn(f'Commands.literal("{literal}")',commands)
        self.assertIn('requires(src->src.hasPermission(2))',commands)
        self.assertIn("getPersistentData()",story)
        self.assertIn("FactionPolicy.Side",story)
        self.assertIn("storyReply(",dialogue)
        self.assertIn("observer.mood()",model)
        self.assertIn("head.zRot",model)
        self.assertNotIn("net.minecraft.client",bird)
        for bad in ("forceChunk(", "setBlock(", "explode(", "teleportTo("):
            self.assertNotIn(bad,bird)
            self.assertNotIn(bad,commands)

    def test_v08_three_entity_registration_and_relationships(self):
        main=(SRC / "GuguGagaMod.java").read_text()
        client=(SRC / "client/ClientEvents.java").read_text()
        redactor=(SRC / "entity/RedactorEntity.java").read_text()
        yellow=(SRC / "entity/YellowSmilerEntity.java").read_text()
        guardian=(SRC / "entity/GugugagaEntity.java").read_text()
        policy=(SRC / "ai/ThreeEntityReactionPolicy.java").read_text()
        build=(ROOT / "build.gradle").read_text()
        for entity in ('"observer"','"redactor"','"yellow_smiler"'):
            self.assertIn("ENTITIES.register("+entity,main)
        self.assertIn("RedactorRenderer::new",client)
        self.assertIn("YellowSmilerRenderer::new",client)
        self.assertIn("RedactorModel::createBodyLayer",client)
        self.assertIn("YellowSmilerModel::createBodyLayer",client)
        self.assertIn("REVEALED",redactor)
        self.assertIn("RedactorAction.FLEE_SMILER",policy)
        self.assertIn("RedactorAction.REVEAL_AND_APPROACH",policy)
        self.assertIn("ThreeEntityReactionPolicy.mayCorrupt",redactor)
        self.assertIn("bird.setTemperament(Mood.CORRUPTED)",redactor)
        self.assertIn("ThreeEntityReactionPolicy.smiler(",yellow)
        self.assertIn("getLookControl().setLookAt(nearest",yellow)
        self.assertIn("ThreeEntityReactionPolicy.gugu(",guardian)
        self.assertIn("generateOriginalCharacterSprites",build)
        for sprite in ("lapis_mask.png","redactor_true.png","yellow_smiler.png"):
            self.assertIn(sprite,(ROOT / "qa/generate_character_sprites.py").read_text())
        for cls in ("RedactorEntity.java","YellowSmilerEntity.java"):
            body=(SRC / "entity" / cls).read_text()
            self.assertNotIn("getChunk(",body)
            self.assertNotIn("forceChunk(",body)
            self.assertNotIn("setBlock(",body)
            self.assertNotIn("teleportTo(",body)
            self.assertNotIn("net.minecraft.client",body)
        archive=(ROOT / "docs/GUGUGAGA_CANON_AND_LONG_TERM_ARCHIVE.md").read_text()
        for important in ("Beyond the Creator","True Lapis","Yellow Smiler",
                          "Lapis","Redactor","five endings"):
            self.assertIn(important,archive)

    def test_v09_three_unique_advanced_modes_remain_bounded(self):
        bird=(SRC / "entity/GugugagaEntity.java").read_text()
        redactor=(SRC / "entity/RedactorEntity.java").read_text()
        yellow=(SRC / "entity/YellowSmilerEntity.java").read_text()
        config=(SRC / "config/NightwatchConfig.java").read_text()
        commands=(SRC / "command/GuguAdvancedCommands.java").read_text()
        main=(SRC / "GuguGagaMod.java").read_text()
        sprites=(ROOT / "qa/generate_character_sprites.py").read_text()
        renderer=(SRC / "client/ObserverRenderer.java").read_text()
        yrenderer=(SRC / "client/YellowSmilerRenderer.java").read_text()
        for item in ("CORRUPTION_STAGE","testBlackenedCorruption",
                     "CorruptionArcPolicy.stage(", "setTemperament(Mood.CORRUPTED)"):
            self.assertIn(item,bird)
        for item in ("MimicAttackPolicy.shouldRevealToPlayer",
                     "MimicAttackPolicy.mayHit", "NightwatchDirector.isQuiet",
                     "RedactorAction.FLEE_SMILER","getNavigation().stop()"):
            self.assertIn(item,redactor)
        self.assertIn("REDACTOR_HOSTILITY_ENABLED.get()",redactor)
        self.assertIn('define("redactor_hostility",false)',config)
        for item in ("mobInteract(", "SmilerGiftPolicy.mayHelp",
                     "recipient.addItem(", "setRevealed(", "LAST_HELP"):
            self.assertIn(item,yellow)
        self.assertIn("GuguAdvancedCommands::register",main)
        for cmd in ("mimic","smiler","blacken","reveal","hostility","mask","disguise"):
            self.assertIn(f'Commands.literal("{cmd}")',commands)
        self.assertIn("requires(src->src.hasPermission(2))",commands)
        self.assertIn("observer_blackened.png",renderer)
        self.assertIn("yellow_smiler_true.png",yrenderer)
        for tex in ("observer_blackened.png","yellow_smiler_true.png"):
            self.assertIn(tex,sprites)
        for body in (bird,redactor,yellow):
            for forbidden in ("forceChunk(", "teleportTo(", "setBlock(", "explode("):
                self.assertNotIn(forbidden,body)

    def test_v010_playable_loop_is_player_owned_and_story_free(self):
        logic=(SRC / "ai/HauntGameplayPolicy.java").read_text()
        runtime=(SRC / "ai/HauntGameplayRuntime.java").read_text()
        commands=(SRC / "command/GuguPlayCommands.java").read_text()
        config=(SRC / "config/NightwatchConfig.java").read_text()
        director=(SRC / "ai/NightwatchDirector.java").read_text()
        redactor=(SRC / "entity/RedactorEntity.java").read_text()
        smiler=(SRC / "entity/YellowSmilerEntity.java").read_text()
        main=(SRC / "GuguGagaMod.java").read_text()
        for k in ("STILLNESS", "WHISPERS", "WITNESS", "FALSE_SAFETY",
                  "RESPITE", "nearEvidence", "playerEligible"):
            self.assertIn(k,logic)
        for k in ("HauntGameplayPolicy.advance(", "getEntitiesOfClass(",
                  "player.hasLineOfSight(other)", "player.getUUID()",
                  "NightwatchDirector.isQuiet", "REDACTOR.get()",
                  "YELLOW_SMILER.get()", "getChunkSource().hasChunk",
                  "level.noCollision(spawned)", "global>=6"):
            self.assertIn(k,runtime)
        for k in ("Commands.literal(\"play\")",
                  "Commands.literal(\"investigate\")",
                  "Commands.literal(\"join\")",
                  "Commands.literal(\"leave\")",
                  "Commands.literal(\"server\")"):
            self.assertIn(k,commands)
        self.assertIn("src.hasPermission(2)",commands)
        self.assertIn('define("playable_loop",false)',config)
        self.assertIn("HauntGameplayRuntime.isJoined(server,id)",director)
        self.assertIn("markStageEncounter()",redactor)
        self.assertIn("markStageEncounter()",smiler)
        self.assertIn("GuguPlayCommands::register",main)
        self.assertIn("HauntGameplayRuntime::onTick",main)
        self.assertIn("NEOFORGE_1211_ANIMATION_AND_HORROR_DEPENDENCIES.md",
                      (ROOT / "README.md").read_text())
        for body in (runtime,logic,commands):
            for wrong in ("forceChunk(", "setBlock(", "explode(",
                          "teleportTo(", "getChunk(", "Runtime.getRuntime()"):
                self.assertNotIn(wrong,body)
        self.assertNotIn("System.exit(",runtime)
        self.assertNotIn("setGameMode(",runtime)

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
