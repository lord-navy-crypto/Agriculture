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
    private static final int[] WATER_CHECK_DY_ORDER = {0, -1, 1};

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
                 "collard_greens_seeds", "brussels_sprouts_seeds", "cauliflower_seeds",
                 "bok_choy_seeds", "shallot_seeds", "quinoa_seeds", "asparagus_seeds", "blackcurrant_seeds", "chives_seeds",
                 "endive_seeds", "kohlrabi_seeds", "rutabaga_seeds", "chicory_seeds", "mustard_greens_seeds",
                 "celeriac_seeds", "rapini_seeds", "napa_cabbage_seeds", "fava_bean_seeds" -> Band.COOL;
            case "corn_seeds", "tomato_seeds", "soybean_seeds", "cucumber_seeds",
                 "bell_pepper_seeds", "eggplant_seeds", "cotton_seeds", "peanut_seeds",
                 "sunflower_seed_seeds", "sweet_potato_seeds", "zucchini_seeds",
                 "basil_seeds", "okra_seeds", "green_bean_seeds", "chili_pepper_seeds",
                 "black_bean_seeds", "pumpkin_seeds", "sweet_corn_seeds", "cassava_seeds", "yam_seeds", "jicama_seeds", "adzuki_bean_seeds", "mung_bean_seeds", "cowpea_seeds",
                 "purslane_seeds", "chayote_seeds", "coffee_seeds", "melon_seeds", "watermelon_seeds", "jute_mallow_seeds" -> Band.WARM;
            case "sorghum_seeds", "millet_seeds", "sesame_seeds", "lavender_seeds", "amaranth_seeds", "teff_seeds", "safflower_seeds" -> Band.DRY;
            case "rice_seeds", "ginger_seeds", "turmeric_seeds", "pineapple_seeds", "taro_seeds", "watercress_seeds", "water_spinach_seeds" -> Band.WET;
            case "chickpea_seeds", "clover_seeds", "mint_seeds", "artichoke_seeds", "blackberry_seeds", "grape_seeds", "tea_seeds" -> Band.TEMPERATE;
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
        if (FoodEcologyV33Climate.isNewCrop(seedName)) {
            return FoodEcologyV33Climate.allowGrowth(seedName, level, pos, random);
        }
        // The shared lunar calendar gates only immature crop growth. Existing
        // rice irrigation, crop bands and the prior 100-crop climate logic stay.
        if (!LunarSeasonsV36.allowsGrowth(
                cropBand(seedName).name().toLowerCase(java.util.Locale.ROOT),
                level, pos, random)) return false;
        // Rice, taro and watercress use bounded freshwater checks as gameplay proxies;
        // these are not claims that every real cultivation system requires flooding.
        if ("rice_seeds".equals(seedName) && !hasNearbyFreshWater(level, pos, 3, -1, 0)) return false;
        if (("taro_seeds".equals(seedName) || "watercress_seeds".equals(seedName))
                && !hasNearbyFreshWater(level, pos, 2, -1, 1)) return false;
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
     * Finds nearby freshwater without forcing neighboring chunks to load.
     * Rice uses a wider irrigation radius; taro and watercress use a compact wet-bed radius.
     */
    private static boolean hasNearbyFreshWater(ServerLevel level, BlockPos cropPos,
                                               int radius, int minDy, int maxDy) {
        // Probe the center and nearby columns first: normal irrigated plots usually
        // find water quickly. Each x/z column shares the same chunk-loaded result.
        // The square and vertical bounds are unchanged; this only changes probe order.
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int ring = 0; ring <= radius; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (ring != 0 && dx != -ring && dx != ring
                            && dz != -ring && dz != ring) {
                        continue;
                    }
                    int x = cropPos.getX() + dx;
                    int z = cropPos.getZ() + dz;
                    candidate.set(x, cropPos.getY(), z);
                    if (!level.hasChunkAt(candidate)) {
                        continue;
                    }
                    for (int dy : WATER_CHECK_DY_ORDER) {
                        if (dy < minDy || dy > maxDy) {
                            continue;
                        }
                        candidate.set(x, cropPos.getY() + dy, z);
                        if (level.getFluidState(candidate).is(FluidTags.WATER)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
