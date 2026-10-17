package io.github.lordnavycrypto.gugugaga.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Server-owned opt-in; config is OFF by default and never auto-connects. */
public final class NightwatchConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue OLLAMA_ENABLED;
    public static final ModConfigSpec.ConfigValue<String> OLLAMA_MODEL;
    public static final ModConfigSpec.BooleanValue ENCOUNTERS_ENABLED;
    public static final ModConfigSpec.IntValue ENCOUNTER_COOLDOWN_TICKS;
    public static final ModConfigSpec.BooleanValue HAUNTING_AUDIO_ENABLED;
    public static final ModConfigSpec.BooleanValue HAUNTING_PARTICLES_ENABLED;
    public static final ModConfigSpec.BooleanValue JOURNAL_ENABLED;
    public static final ModConfigSpec.BooleanValue JOURNAL_HINTS_ENABLED;
    public static final ModConfigSpec.BooleanValue VARIED_EVENTS_ENABLED;
    public static final ModConfigSpec.IntValue DIRECTOR_INTENSITY;
    public static final ModConfigSpec.BooleanValue DIALOGUE_ENABLED;
    public static final ModConfigSpec.BooleanValue DIALOGUE_LOCAL_AI_ENABLED;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("local_ollama");
        OLLAMA_ENABLED = BUILDER.comment(
                "Optional, explicitly enabled local Ollama shadow advisor. No auto network calls.")
                .define("enabled", false);
        OLLAMA_MODEL = BUILDER.comment("Exact name from local Ollama /api/tags")
                .define("model", "");
        BUILDER.pop();
        BUILDER.push("encounters");
        ENCOUNTERS_ENABLED = BUILDER.comment(
                "Allow rare nonviolent observer encounters. OFF by default. No chunk forcing.")
                .define("enabled", false);
        ENCOUNTER_COOLDOWN_TICKS = BUILDER.comment(
                "Minimum cooldown per player, ticks. Never below 8 minutes; default 10 minutes.")
                .defineInRange("cooldown_ticks", 12000, 9600, 72000);
        HAUNTING_AUDIO_ENABLED = BUILDER.comment(
                "Use subtle, volume-capped vanilla directional cues. OFF disables all custom observer cue sounds.")
                .define("haunting_audio", true);
        HAUNTING_PARTICLES_ENABLED = BUILDER.comment(
                "Allow a few subtle soul particles during retreat; OFF disables horror particles.")
                .define("haunting_particles", true);
        VARIED_EVENTS_ENABLED = BUILDER.comment(
                "When encounters are opt-in enabled, vary them with calm intervals, distant clues and rare sightings. " +
                "OFF makes director use the original sighting-only algorithm.")
                .define("varied_events", true);
        DIRECTOR_INTENSITY = BUILDER.comment(
                "How often a generated encounter can be a visible observer: 0 gentle, 1 balanced, 2 eerie. " +
                "Does NOT reduce cooldown or increase sound volume, damage or monster count.")
                .defineInRange("director_intensity", 1, 0, 2);
        BUILDER.pop();
        BUILDER.push("witness_journal");
        JOURNAL_ENABLED = BUILDER.comment(
                "Keep a three-clue in-game witness notebook in the player's own save data.")
                .define("enabled", true);
        JOURNAL_HINTS_ENABLED = BUILDER.comment(
                "One quiet chat hint per new clue. OFF means journal-only discovery.")
                .define("hint_messages", true);
        BUILDER.pop();
        BUILDER.push("dialogue");
        DIALOGUE_ENABLED=BUILDER.comment(
                "Allow players to talk to the fictional penguin with /gugugaga talk. "
                + "Uses deterministic OFFLINE replies unless local AI is explicitly opted in.")
                .define("enabled",true);
        DIALOGUE_LOCAL_AI_ENABLED=BUILDER.comment(
                "Optional localhost Ollama for /gugugaga talk. OFF by default. "
                + "Must also enable local_ollama.enabled and select a local model. "
                + "Only an explicitly entered question is sent to local server AI.")
                .define("local_ollama_dialogue",false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private NightwatchConfig() {}
}
