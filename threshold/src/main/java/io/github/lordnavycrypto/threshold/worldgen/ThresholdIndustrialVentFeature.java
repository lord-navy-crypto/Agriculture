package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Second pass in Lithostitched's ordered composite, after room geometry.
 * Always writes inside the current chunk and never modifies old chunks.
 */
public final class ThresholdIndustrialVentFeature extends Feature<NoneFeatureConfiguration> {
    public ThresholdIndustrialVentFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        if (!world.getLevel().dimension().equals(ThresholdDimensions.LEVEL_1)) return false;

        int chunkX = context.origin().getX() >> 4;
        int chunkZ = context.origin().getZ() >> 4;
        if (Math.floorMod(chunkX, 3) != 0 || Math.floorMod(chunkZ, 3) != 0) return false;

        int roomX = Math.floorDiv(chunkX, 3);
        int roomZ = Math.floorDiv(chunkZ, 3);
        // A repeatable placement cadence, with a guaranteed vent near spawn.
        if ((roomX != 0 || roomZ != 0)
                && Math.floorMod(roomX * 17 + roomZ * 29, 3) != 0) return false;

        BlockPos pos = new BlockPos((chunkX << 4) + 12, 73, (chunkZ << 4) + 12);
        if (!world.ensureCanWrite(pos)) return false;
        world.setBlock(pos, ThresholdEnvironmentBlocks.CEILING_VENT.get().defaultBlockState(), 2);
        return true;
    }
}
