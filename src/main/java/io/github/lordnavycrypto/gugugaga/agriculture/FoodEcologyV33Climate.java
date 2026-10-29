package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Separate gameplay climate categories for eight v3.3 crops. The original
 * century-scale crop climate classification remains completely unchanged.
 * Biome labels act as game proxies and not real soil temperature data.
 */
public final class FoodEcologyV33Climate {
    private FoodEcologyV33Climate() {}

    private static final Map<String, String> BANDS = Map.of(
            "black_pepper_seeds", "WARM",
            "lemongrass_seeds", "WET",
            "wasabi_seeds", "COOL",
            "cumin_seeds", "DRY",
            "cardamom_seeds", "WET",
            "fenugreek_seeds", "DRY",
            "sugar_beet_seeds", "COOL",
            "daikon_seeds", "COOL");

    public static boolean isNewCrop(String seedName) {
        return BANDS.containsKey(seedName);
    }

    public static boolean allowGrowth(String seedName, ServerLevel level,
                                      BlockPos pos, RandomSource random) {
        if (level.dimension() != Level.OVERWORLD) return random.nextInt(6) == 0;
        String profile = BANDS.get(seedName);
        if (!LunarSeasonsV36.allowsGrowth(
                profile.toLowerCase(java.util.Locale.ROOT), level, pos, random)) return false;
        String biome = level.getBiome(pos).unwrapKey()
                .map(key -> key.location().getPath()).orElse("plains");
        boolean cold = biome.contains("snowy") || biome.contains("frozen")
                || biome.contains("taiga") || biome.contains("grove")
                || biome.contains("peaks");
        boolean dry = biome.contains("desert") || biome.contains("savanna")
                || biome.contains("badlands");
        boolean wet = biome.contains("jungle") || biome.contains("swamp")
                || biome.equals("river");
        boolean suitable = switch (profile) {
            case "COOL" -> cold || (!dry && !wet);
            case "DRY" -> dry || (!cold && !wet);
            case "WET" -> wet || (!cold && !dry);
            case "WARM" -> dry || wet || (!cold && !dry);
            default -> false;
        };
        return suitable || random.nextInt(cold || dry ? 4 : 2) == 0;
    }
}
