package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Original penguin-inspired biome preference, using the actual vanilla
 * biome base temperature. This adjusts only the odds of a rare sighting,
 * not the eight-minute cooldown, mob cap, game rules or chunk loading.
 *
 * Temperature scores are relative Minecraft climate values, not degrees C.
 */
public final class ColdBiomePolicy {
    private ColdBiomePolicy() {}

    /** Relative probability in per-mille. Cold is common; desert is rare. */
    public static int sightingPermille(double baseTemperature) {
        if (!Double.isFinite(baseTemperature)) return 0;
        if (baseTemperature<=0.0) return 950;
        if (baseTemperature<=0.3) return 820;
        if (baseTemperature<=0.8) return 500;
        if (baseTemperature<=1.3) return 160;
        if (baseTemperature<=1.8) return 40;
        return 15;
    }

    /** Deterministic independent roll; no allocation, seeded randomness or RNG state. */
    public static boolean allow(double temp,long playerSeed,long eventTick) {
        long mixed=mix(playerSeed^(eventTick*0x9e3779b97f4a7c15L));
        return Math.floorMod(mixed,1000L)<sightingPermille(temp);
    }

    private static long mix(long z) {
        z^=z>>>30;z*=0xbf58476d1ce4e5b9L;
        z^=z>>>27;z*=0x94d049bb133111ebL;
        return z^(z>>>31);
    }
}
