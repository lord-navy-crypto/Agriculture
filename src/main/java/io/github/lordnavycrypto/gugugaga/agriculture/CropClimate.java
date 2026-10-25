package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Map;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Real-world cool/warm season horticulture inspired BIOME ADAPTATION, not a physical
 * temperature/rainfall model. Minecraft's biome names are game proxies only.
 */
public final class CropClimate {
    private CropClimate() {}
    private enum Band { COOL, TEMPERATE, WARM, DRY, WET }

    /** Published minimum seed-germination soil temperatures, Fahrenheit, where available. */
    private static final Map<String, Integer> MIN_GERMINATION_F = Map.of(
            "corn_seeds", 50,
            "pea_seeds", 40,
            "onion_seeds", 35,
            "lettuce_seeds", 35,
            "spinach_seeds", 35,
            "tomato_seeds", 50,
            "bell_pepper_seeds", 60,
            "cucumber_seeds", 60,
            "eggplant_seeds", 60,
            "cabbage_seeds", 40
    );
    /**
     * Explicit profile for every currently registered crop. These broad bands are
     * balancing categories inspired by extension guidance, not simulated temperatures.
     */
    private static Band cropBand(String seedName) {
        return switch (seedName) {
            case "onion_seeds", "strawberry_seeds", "blueberry_seeds", "garlic_seeds",
                 "barley_seeds", "oat_seeds", "rye_seeds", "pea_seeds",
                 "lettuce_seeds", "cabbage_seeds", "spinach_seeds", "broccoli_seeds",
                 "flax_seeds", "buckwheat_seeds", "lentil_seeds", "radish_seeds",
                 "turnip_seeds", "kale_seeds", "raspberry_seeds", "cranberry_seeds",
                 "mustard_seeds", "canola_seeds", "celery_seeds", "leek_seeds",
                 "parsnip_seeds", "arugula_seeds", "swiss_chard_seeds",
                 "fennel_seeds", "dill_seeds", "cilantro_seeds", "parsley_seeds",
                 "collard_greens_seeds", "brussels_sprouts_seeds" -> Band.COOL;
            case "corn_seeds", "tomato_seeds", "soybean_seeds", "cucumber_seeds",
                 "bell_pepper_seeds", "eggplant_seeds", "cotton_seeds", "peanut_seeds",
                 "sunflower_seed_seeds", "sweet_potato_seeds", "zucchini_seeds",
                 "basil_seeds", "okra_seeds", "green_bean_seeds", "chili_pepper_seeds" -> Band.WARM;
            case "sorghum_seeds", "millet_seeds", "sesame_seeds", "lavender_seeds" -> Band.DRY;
            case "rice_seeds" -> Band.WET;
            case "chickpea_seeds", "clover_seeds", "mint_seeds" -> Band.TEMPERATE;
            default -> throw new IllegalArgumentException("Unclassified crop seed: " + seedName);
        };
    }
    private static Band biomeBand(String name) {
        if (name.equals("cherry_grove")) return Band.TEMPERATE;
        if (name.contains("jungle") || name.contains("swamp") || name.equals("river")) return Band.WET;
        if (name.contains("savanna") || name.contains("badlands") || name.equals("desert")) return Band.DRY;
        if (name.contains("snowy") || name.contains("frozen") || name.contains("taiga")
                || name.contains("grove") || name.contains("peaks") || name.contains("slopes")) return Band.COOL;
        if (name.equals("plains") || name.equals("sunflower_plains") || name.equals("meadow")
                || name.contains("forest") || name.equals("cherry_grove")) return Band.TEMPERATE;
        return Band.TEMPERATE;
    }
    /** Returns if one normal vanilla growth roll is permitted; does not force chunk loads. */
    public static boolean allowGrowth(String seedName, ServerLevel level, BlockPos pos, RandomSource random) {
        // This models irrigated lowland rice; it does not claim every rice system needs flooding.
        if ("rice_seeds".equals(seedName) && !hasPaddyWater(level, pos)) return false;
        if (level.dimension() != Level.OVERWORLD) return random.nextInt(6) == 0;
        Band crop = cropBand(seedName);
        String name = level.getBiome(pos).unwrapKey()
                .map(key -> key.location().getPath()).orElse("plains");
        Band biome = biomeBand(name);
        // Game balancing, not measured degrees C. Suitable climates keep native growth,
        // moderately different climates get 2/3 rolls; strong mismatches get 1/4.
        boolean suitable = crop == biome
                // Cranberry is a cool-season wetland crop; Minecraft swamps are its wetland proxy.
                || "cranberry_seeds".equals(seedName) && biome == Band.WET
                || biome == Band.TEMPERATE && crop != Band.WET
                || crop == Band.TEMPERATE && biome == Band.COOL
                || crop == Band.WARM && biome == Band.DRY
                || crop == Band.DRY && biome == Band.WARM
                || crop == Band.WET && biome == Band.WARM
                || crop == Band.WARM && biome == Band.WET;
        if (suitable) return true;
        // Uses real minimum germination categories as a relative cold-biome sensitivity,
        // NOT as a claim that Minecraft biome temperatures equal soil temperatures.
        if (biome == Band.COOL && MIN_GERMINATION_F.getOrDefault(seedName, 0) >= 50)
            return random.nextInt(6) == 0;
        boolean harsh = (biome == Band.COOL && (crop == Band.WARM || crop == Band.DRY))
                || (biome == Band.DRY && (crop == Band.WET || crop == Band.COOL));
        return random.nextInt(harsh ? 4 : 2) == 0;
    }
    /**
     * Finds freshwater near the crop without forcing neighboring chunks to load.
     * The vertical offsets cover water at crop height or the farmland level below.
     */
    private static boolean hasPaddyWater(ServerLevel level, BlockPos cropPos) {
        // Reuse one mutable coordinate rather than allocating up to 98 BlockPos
        // objects for every growing rice plant's random tick.
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = -1; dy <= 0; dy++) {
                    candidate.set(cropPos.getX() + dx,
                            cropPos.getY() + dy, cropPos.getZ() + dz);
                    if (level.hasChunkAt(candidate)
                            && level.getFluidState(candidate).is(FluidTags.WATER)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
