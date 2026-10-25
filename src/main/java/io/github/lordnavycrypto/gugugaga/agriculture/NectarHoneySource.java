package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Approximate botanical source for existing managed honeybee hives.
 * Only consulted when a fully stocked and safely smoked hive is harvested:
 * one bounded local forage scan, no background block/entity polling.
 *
 * This cannot certify real-world monofloral purity or infer historical bee flights.
 */
public final class NectarHoneySource {
    private NectarHoneySource() {}

    // This order is intentionally stable for tie handling.
    private static final String[] SOURCES = {
            "clover_crop", "lavender_crop", "buckwheat_crop", "sunflower_seed_crop",
            "minecraft:dandelion", "minecraft:poppy", "minecraft:allium", "minecraft:cornflower",
            "minecraft:azure_bluet", "minecraft:blue_orchid", "minecraft:lily_of_the_valley", "minecraft:oxeye_daisy",
            "minecraft:torchflower", "minecraft:red_tulip", "minecraft:orange_tulip", "minecraft:white_tulip",
            "minecraft:pink_tulip", "minecraft:wither_rose"
    };

    public static ItemStack selectHoneyBottle(Level level, BlockPos hivePos) {
        int[] sourceCounts = new int[SOURCES.length];
        int otherFlowers = 0;
        BlockPos.MutableBlockPos sample = new BlockPos.MutableBlockPos();

        // Radius five represents nearby accessible plots, not bees' actual flight radius.
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                for (int dy = -2; dy <= 1; dy++) {
                    sample.set(hivePos.getX() + dx, hivePos.getY() + dy,
                            hivePos.getZ() + dz);
                    // Never force terrain generation at a chunk boundary.
                    if (!level.hasChunkAt(sample)) continue;
                    BlockState state = level.getBlockState(sample);
                    if (state.getBlock() instanceof AgricultureCropBlock) {
                        // Late crop stages stand in for flowering only; the selected source
                        // can be overridden by the surrounding mixed nectar forage.
                        if (state.getValue(AgricultureCropBlock.AGE) < 5) continue;
                        String cropId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
                        boolean matched = false;
                        for (int i = 0; i < SOURCES.length; i++) {
                            if (SOURCES[i].equals(cropId)) {
                                sourceCounts[i]++;
                                matched = true;
                                break;
                            }
                        }
                        if (!matched && isOtherNectarCrop(cropId)) otherFlowers++;
                    } else if (state.is(BlockTags.FLOWERS)) {
                        // Vanilla flowers can now produce distinct honey types too.
                        // A modded bloom with a matching path must not impersonate
                        // a Minecraft vanilla flower.
                        var flowerId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                        String identifier = flowerId.toString();
                        boolean matched = false;
                        for (int i = 0; i < SOURCES.length; i++) {
                            if (SOURCES[i].equals(identifier)) {
                                sourceCounts[i]++;
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) otherFlowers++;
                    }
                }
            }
        }

        int dominantIndex = -1;
        int dominantCount = 0;
        int totalFlowers = otherFlowers;
        for (int i = 0; i < sourceCounts.length; i++) {
            totalFlowers += sourceCounts[i];
            if (sourceCounts[i] > dominantCount) {
                dominantCount = sourceCounts[i];
                dominantIndex = i;
            }
        }

        // Require a sizable, >=two-thirds dominant local floral resource.
        // Otherwise preserve normal vanilla honey output (mixed or unknown source).
        if (dominantCount < 6 || dominantCount * 3 < totalFlowers * 2) {
            return new ItemStack(Items.HONEY_BOTTLE);
        }
        Item chosen = switch (dominantIndex) {
            case 0 -> BeeContent.CLOVER_HONEY_BOTTLE.get();
            case 1 -> BeeContent.LAVENDER_HONEY_BOTTLE.get();
            case 2 -> BeeContent.BUCKWHEAT_HONEY_BOTTLE.get();
            case 3 -> BeeContent.SUNFLOWER_HONEY_BOTTLE.get();
            case 4 -> BeeContent.DANDELION_HONEY_BOTTLE.get();
            case 5 -> BeeContent.POPPY_HONEY_BOTTLE.get();
            case 6 -> BeeContent.ALLIUM_HONEY_BOTTLE.get();
            case 7 -> BeeContent.CORNFLOWER_HONEY_BOTTLE.get();
            case 8 -> BeeContent.AZURE_BLUET_HONEY_BOTTLE.get();
            case 9 -> BeeContent.BLUE_ORCHID_HONEY_BOTTLE.get();
            case 10 -> BeeContent.LILY_OF_THE_VALLEY_HONEY_BOTTLE.get();
            case 11 -> BeeContent.OXEYE_DAISY_HONEY_BOTTLE.get();
            case 12 -> BeeContent.TORCHFLOWER_HONEY_BOTTLE.get();
            case 13 -> BeeContent.RED_TULIP_HONEY_BOTTLE.get();
            case 14 -> BeeContent.ORANGE_TULIP_HONEY_BOTTLE.get();
            case 15 -> BeeContent.WHITE_TULIP_HONEY_BOTTLE.get();
            case 16 -> BeeContent.PINK_TULIP_HONEY_BOTTLE.get();
            case 17 -> BeeContent.WITHER_ROSE_HONEY_BOTTLE.get();
            default -> Items.HONEY_BOTTLE;
        };
        return new ItemStack(chosen);
    }

    private static boolean isOtherNectarCrop(String cropId) {
        // These currently grow in the mod and may compete as nectar sources.
        return switch (cropId) {
            case "canola_crop", "mustard_crop", "basil_crop", "mint_crop",
                    "cucumber_crop", "zucchini_crop", "blueberry_crop",
                    "raspberry_crop", "strawberry_crop", "cranberry_crop",
                    "sunflower_crop", "sesame_crop", "okra_crop",
                    "chili_pepper_crop", "dill_crop", "cilantro_crop" -> true;
            default -> false;
        };
    }
}
