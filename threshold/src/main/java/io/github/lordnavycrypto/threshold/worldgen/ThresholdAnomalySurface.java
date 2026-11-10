package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared placement contract for all six Backrooms dimensions.
 *
 * Every floor's usable surface cell is Y=64:
 * - Level 0 replaces its carpet at Y=64; the solid concrete backing is Y=63.
 * - Levels 1-5 place into air at Y=64 above their authored floor at Y=63.
 *
 * Level 0 and Level 1 keep the complete anomaly library, but deterministic
 * feature attempts are one quarter as frequent as Levels 2-5. The original
 * maze routes, floors, walls and existing fluids remain protected.
 */
public final class ThresholdAnomalySurface {
    public static final int SURFACE_Y = 64;
    public static final int LOW_FREQUENCY_MULTIPLIER = 4;

    private ThresholdAnomalySurface() {}

    public static boolean isBackroomsDimension(String dimension) {
        return switch (dimension) {
            case "level_0", "level_1", "level_2", "level_3", "level_4", "level_5" -> true;
            default -> false;
        };
    }

    public static boolean isLevel0(String dimension) {
        return "level_0".equals(dimension);
    }

    public static boolean isLowFrequency(String dimension) {
        return "level_0".equals(dimension) || "level_1".equals(dimension);
    }

    public static int surfaceY(String dimension) {
        return SURFACE_Y;
    }

    /** Cave passages occupy player-height wall cells, not the floor surface cell. */
    public static int caveFootY(String dimension) {
        return isLevel0(dimension) ? 65 : 64;
    }

    public static int roomSize(String dimension) {
        return ("level_1".equals(dimension) || "level_4".equals(dimension)) ? 48 : 32;
    }

    public static int rarityDenominator(String dimension, int normalDenominator) {
        if (normalDenominator < 1) {
            throw new IllegalArgumentException("normalDenominator must be positive");
        }
        return normalDenominator * (isLowFrequency(dimension) ? LOW_FREQUENCY_MULTIPLIER : 1);
    }

    public static boolean shouldGenerate(String dimension, long site, int normalDenominator) {
        return Math.floorMod(site, (long) rarityDenominator(dimension, normalDenominator)) == 0L;
    }

    public static boolean isLevel0Carpet(BlockState state) {
        return state.is(Blocks.YELLOW_CARPET) || state.is(Blocks.GRAY_CARPET)
                || state.is(Blocks.RED_CARPET) || state.is(Blocks.BROWN_CARPET);
    }

    public static boolean isNativeFloor(BlockState state) {
        return state.is(Blocks.GRAY_CONCRETE) || state.is(Blocks.LIGHT_GRAY_CONCRETE)
                || state.is(Blocks.WHITE_CONCRETE) || state.is(Blocks.DEEPSLATE_TILES)
                || state.is(Blocks.POLISHED_DEEPSLATE) || state.is(Blocks.COBBLED_DEEPSLATE)
                || state.is(Blocks.SMOOTH_BASALT) || state.is(Blocks.SMOOTH_STONE)
                || state.is(Blocks.DARK_OAK_PLANKS) || state.is(Blocks.SPRUCE_PLANKS)
                || state.is(Blocks.POLISHED_ANDESITE) || state.is(Blocks.ANDESITE)
                || state.is(Blocks.CALCITE) || state.is(Blocks.POLISHED_BLACKSTONE)
                || state.is(Blocks.GRAVEL);
    }

    public static boolean isNativeGroundBlock(WorldGenLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return isNativeFloor(state)
                && state.isCollisionShapeFullBlock(world, pos)
                && world.getFluidState(pos).isEmpty();
    }

    /**
     * True only for the intended walk-surface cell: Level 0 carpet, or air
     * above a recognized solid floor on Levels 1-5. Never accepts wall cells,
     * existing structures, water, crops, stairs or decorative floor objects.
     */
    public static boolean isReplaceableSurface(WorldGenLevel world, BlockPos surfacePos) {
        if (surfacePos.getY() != SURFACE_Y) return false;
        String dimension = world.getLevel().dimension().location().getPath();
        if (!isNativeGroundBlock(world, surfacePos.below())
                || !world.getFluidState(surfacePos).isEmpty()) return false;
        BlockState surface = world.getBlockState(surfacePos);
        return isLevel0(dimension) ? isLevel0Carpet(surface) : surface.isAir();
    }

    public static BlockState supportingBlock(WorldGenLevel world, BlockPos surfacePos) {
        return world.getBlockState(surfacePos.below());
    }
}
