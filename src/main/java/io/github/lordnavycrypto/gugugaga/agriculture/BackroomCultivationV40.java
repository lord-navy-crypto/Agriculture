package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * Closed-loop Level 1 greenhouse gameplay. Vanilla farmland hydration is the
 * fast path; explicit bounded source-water checks are only used while soil is
 * dry. A supplied composter can provide a modest, non-stacking growth bonus.
 * Nothing loads neighboring chunks or ticks an idle greenhouse.
 */
public final class BackroomCultivationV40 {
    private static final int WATER_RADIUS = 4;
    private static final int COMPOST_RADIUS = 3;
    private static final int MIN_LIGHT = 9;
    private static final int COMPOST_LEVEL_FOR_BONUS = 4;

    private BackroomCultivationV40() {}

    public static boolean isLevel1(ServerLevel level) {
        return level.dimension().equals(ThresholdDimensions.LEVEL_1);
    }

    /** A single vanilla crop random tick; the caller retains native age and yield. */
    public static boolean allowGrowth(String seedName, ServerLevel level,
                                      BlockPos cropPos, RandomSource random) {
        if (!isLevel1(level)) return false;
        BlockState farmland = level.getBlockState(cropPos.below());
        if (!farmland.is(Blocks.FARMLAND)) return false;
        if (level.getMaxLocalRawBrightness(cropPos) < MIN_LIGHT) return false;

        // FarmBlock already maintains a local hydration state. Avoid examining
        // 9x9x3 fluid states for every single crop growth roll.
        if (farmland.getValue(FarmBlock.MOISTURE) == 0
                && !hasNearbyFreshWater(level, cropPos)) return false;

        // Unassisted greenhouse crops grow on half their normal random rolls.
        if (random.nextInt(2) == 0) return true;

        // Filled composters support the remaining rolls at 1/3 probability:
        // 50% -> 66.7% effective growth. No free bone meal is created.
        return hasActiveComposterNearby(level, cropPos) && random.nextInt(3) == 0;
    }

    private static boolean hasActiveComposterNearby(ServerLevel level, BlockPos cropPos) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        int y = cropPos.getY() - 1;
        for (int dx = -COMPOST_RADIUS; dx <= COMPOST_RADIUS; dx++) {
            for (int dz = -COMPOST_RADIUS; dz <= COMPOST_RADIUS; dz++) {
                probe.set(cropPos.getX() + dx, y, cropPos.getZ() + dz);
                if (!level.hasChunkAt(probe)) continue;
                BlockState state = level.getBlockState(probe);
                if (state.is(Blocks.COMPOSTER)
                        && state.getValue(ComposterBlock.LEVEL) >= COMPOST_LEVEL_FOR_BONUS)
                    return true;
            }
        }
        return false;
    }

    private static boolean hasNearbyFreshWater(ServerLevel level, BlockPos cropPos) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int dx = -WATER_RADIUS; dx <= WATER_RADIUS; dx++) {
            for (int dz = -WATER_RADIUS; dz <= WATER_RADIUS; dz++) {
                for (int dy = -2; dy <= 0; dy++) {
                    probe.set(cropPos.getX() + dx, cropPos.getY() + dy, cropPos.getZ() + dz);
                    if (!level.hasChunkAt(probe)) continue;
                    FluidState fluid = level.getFluidState(probe);
                    if (fluid.is(FluidTags.WATER) && fluid.isSource()) return true;
                }
            }
        }
        return false;
    }
}
