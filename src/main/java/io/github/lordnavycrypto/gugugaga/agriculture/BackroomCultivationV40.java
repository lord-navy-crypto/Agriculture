package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;

/** Stable, dimension-specific greenhouse rules for the Level 1 agriculture loop. */
public final class BackroomCultivationV40 {
    private static final int WATER_RADIUS = 4;
    private static final int MIN_LIGHT = 9;

    private BackroomCultivationV40() {}

    public static boolean isLevel1(ServerLevel level) {
        return level.dimension().equals(ThresholdDimensions.LEVEL_1);
    }

    /** Level 1 crops need farmland, block light, and nearby source water. */
    public static boolean allowGrowth(String seedName, ServerLevel level,
                                      BlockPos cropPos, RandomSource random) {
        if (!isLevel1(level)) return false;
        if (!level.getBlockState(cropPos.below()).is(Blocks.FARMLAND)) return false;
        if (level.getMaxLocalRawBrightness(cropPos) < MIN_LIGHT) return false;
        if (!hasNearbyFreshWater(level, cropPos)) return false;
        return random.nextInt(2) == 0;
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
