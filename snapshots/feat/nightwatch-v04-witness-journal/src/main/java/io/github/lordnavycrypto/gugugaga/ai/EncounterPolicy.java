package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Pure, deterministic admission gate for the optional Nightwatch director.
 * It never performs world reads, randomization, spawning or chunk loading.
 * All supplied counts are from already loaded entities/players.
 */
public final class EncounterPolicy {
    public static final int MAX_GLOBAL_OBSERVERS = 8;
    public static final int MAX_NEAR_PLAYER = 1;
    public static final int MIN_COOLDOWN_TICKS = 20 * 60 * 8;
    public static final int DIRECTOR_INTERVAL_TICKS = 200;
    public static final double NEARBY_RADIUS = 48.0;

    private EncounterPolicy() {}

    public static boolean eligible(boolean enabled, boolean overworld,
            boolean survival, boolean playerQuiet, boolean night,
            int nearby, int global, long ticksSinceLastEncounter,
            int cooldownTicks) {
        return enabled && overworld && survival && !playerQuiet && night
                && nearby < MAX_NEAR_PLAYER
                && global < MAX_GLOBAL_OBSERVERS
                && ticksSinceLastEncounter >= Math.max(MIN_COOLDOWN_TICKS,cooldownTicks);
    }

    /** No out-of-range inputs or mutable RNG can affect server safety. */
    public static int boundedCooldown(int requested) {
        return Math.max(MIN_COOLDOWN_TICKS,Math.min(requested,20*60*60));
    }
}
