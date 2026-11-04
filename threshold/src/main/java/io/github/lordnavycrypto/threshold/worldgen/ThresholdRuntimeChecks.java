package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

public final class ThresholdRuntimeChecks {
    private ThresholdRuntimeChecks() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ServerLevel level0 = server.getLevel(ThresholdDimensions.LEVEL_0);
        ServerLevel level1 = server.getLevel(ThresholdDimensions.LEVEL_1);

        if (level0 == null || level1 == null) {
            System.err.println("THRESHOLD_DIMENSIONS_REGISTRY_FAIL");
            return;
        }
        System.out.println("THRESHOLD_DIMENSIONS_REGISTRY_READY_OK");

        level0.getChunk(0, 0);
        level0.getChunk(0, 1);
        level1.getChunk(0, 0);
        level1.getChunk(0, 1);
        level1.getChunk(1, 0);
        level1.getChunk(1, 1);

        System.out.println("THRESHOLD_LEVEL0_SAMPLE floor=" +
                level0.getBlockState(new BlockPos(4, 63, 4)) +
                " carpet=" + level0.getBlockState(new BlockPos(4, 64, 4)) +
                " biome=" + level0.getBiome(new BlockPos(4, 65, 4)).unwrapKey());
        System.out.println("THRESHOLD_LEVEL1_SAMPLE floor=" +
                level1.getBlockState(new BlockPos(4, 63, 4)) +
                " biome=" + level1.getBiome(new BlockPos(4, 65, 4)).unwrapKey());

        boolean level0Built =
                level0.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.GRAY_CONCRETE)
                && level0.getBlockState(new BlockPos(4, 64, 4)).is(Blocks.YELLOW_CARPET);
        boolean level1Built =
                level1.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.SMOOTH_STONE);

        System.out.println(level0Built
                ? "THRESHOLD_LEVEL0_WORLDGEN_OK" : "THRESHOLD_LEVEL0_WORLDGEN_FAIL");
        System.out.println(level1Built
                ? "THRESHOLD_LEVEL1_WORLDGEN_OK" : "THRESHOLD_LEVEL1_WORLDGEN_FAIL");
        boolean wallBuilt = level0.getBlockState(new BlockPos(0, 66, 15))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.TRANSITION_WALL.get());
        System.out.println(wallBuilt
                ? "THRESHOLD_LEVEL0_TRANSITION_OK" : "THRESHOLD_LEVEL0_TRANSITION_FAIL");
        boolean returnWallBuilt = level1.getBlockState(new BlockPos(0, 66, 23))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.TRANSITION_WALL.get());
        System.out.println(returnWallBuilt
                ? "THRESHOLD_LEVEL1_RETURN_OK" : "THRESHOLD_LEVEL1_RETURN_FAIL");

        boolean supplyBuilt = level1.getBlockState(new BlockPos(8, 64, 8)).is(Blocks.BARREL);
        System.out.println(supplyBuilt
                ? "THRESHOLD_LEVEL1_STARTER_CACHE_OK" : "THRESHOLD_LEVEL1_STARTER_CACHE_FAIL");

        boolean planterBuilt = level1.getBlockState(new BlockPos(14, 63, 14)).is(Blocks.FARMLAND)
                && level1.getBlockState(new BlockPos(16, 63, 16)).is(Blocks.WATER)
                && level1.getBlockState(new BlockPos(14, 67, 14)).is(Blocks.SEA_LANTERN);
        System.out.println(planterBuilt
                ? "THRESHOLD_LEVEL1_LIT_PLANTER_OK" : "THRESHOLD_LEVEL1_LIT_PLANTER_FAIL");
    }
}
