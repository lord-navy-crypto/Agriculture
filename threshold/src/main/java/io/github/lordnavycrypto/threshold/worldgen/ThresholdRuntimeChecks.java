package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        // Regression for the carpet seam visible in Level 0: a wall base
        // must be full-height and match the block directly above it.
        BlockState wallFooting = level0.getBlockState(new BlockPos(0, 64, 5));
        BlockState wallAbove = level0.getBlockState(new BlockPos(0, 65, 5));
        boolean wallFloorConnected = wallFooting.is(
                io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FADED_WALLPAPER.get())
                && wallAbove.is(
                io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FADED_WALLPAPER.get());
        System.out.println(wallFloorConnected
                ? "THRESHOLD_LEVEL0_WALL_FOOTING_OK" : "THRESHOLD_LEVEL0_WALL_FOOTING_FAIL");

        boolean transitionFooting = level0.getBlockState(new BlockPos(0, 64, 15))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.TRANSITION_WALL.get());
        System.out.println(transitionFooting
                ? "THRESHOLD_LEVEL0_PASSAGE_FOOTING_OK" : "THRESHOLD_LEVEL0_PASSAGE_FOOTING_FAIL");
        System.out.println(level1Built
                ? "THRESHOLD_LEVEL1_WORLDGEN_OK" : "THRESHOLD_LEVEL1_WORLDGEN_FAIL");
        boolean wallBuilt = level0.getBlockState(new BlockPos(0, 66, 15))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.TRANSITION_WALL.get());
        System.out.println(wallBuilt
                ? "THRESHOLD_LEVEL0_TRANSITION_OK" : "THRESHOLD_LEVEL0_TRANSITION_FAIL");
        boolean level0LightingBuilt = level0.getBlockState(new BlockPos(8, 72, 8))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FLICKERING_PANEL.get());
        System.out.println(level0LightingBuilt
                ? "THRESHOLD_LEVEL0_LIGHTING_OK" : "THRESHOLD_LEVEL0_LIGHTING_FAIL");
        boolean returnWallBuilt = level1.getBlockState(new BlockPos(0, 66, 23))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.TRANSITION_WALL.get());
        System.out.println(returnWallBuilt
                ? "THRESHOLD_LEVEL1_RETURN_OK" : "THRESHOLD_LEVEL1_RETURN_FAIL");

        boolean supplyBuilt = level1.getBlockState(new BlockPos(8, 64, 8)).is(Blocks.BARREL);
        System.out.println(supplyBuilt
                ? "THRESHOLD_LEVEL1_STARTER_CACHE_OK" : "THRESHOLD_LEVEL1_STARTER_CACHE_FAIL");

        boolean cropBuilt = level1.getBlockState(new BlockPos(14, 64, 14)).is(
                net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                "gugugaga", "barley_crop")));
        boolean planterBuilt = level1.getBlockState(new BlockPos(14, 63, 14)).is(Blocks.FARMLAND)
                && level1.getBlockState(new BlockPos(16, 63, 16)).is(Blocks.WATER)
                && level1.getBlockState(new BlockPos(14, 64, 14)).is(
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                        "gugugaga", "barley_crop")))
                && level1.getBlockState(new BlockPos(14, 67, 14)).is(Blocks.SEA_LANTERN);
        System.out.println(planterBuilt
                ? "THRESHOLD_LEVEL1_LIT_PLANTER_OK" : "THRESHOLD_LEVEL1_LIT_PLANTER_FAIL");
        System.out.println(cropBuilt
                ? "THRESHOLD_LEVEL1_AGRICULTURE_CROP_OK" : "THRESHOLD_LEVEL1_AGRICULTURE_CROP_FAIL");
        // Lithostitched's composite feature must have run its second stage.
        boolean ventBuilt = level1.getBlockState(new BlockPos(12, 73, 12))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.CEILING_VENT.get())
                && level1.getBlockEntity(new BlockPos(12, 73, 12))
                    instanceof io.github.lordnavycrypto.threshold.block.ThresholdCeilingVentBlockEntity;
        System.out.println(ventBuilt
                ? "THRESHOLD_LITHOSTITCHED_VENT_OK" : "THRESHOLD_LITHOSTITCHED_VENT_FAIL");
        // Missing registry entries must not silently become empty loot.
        String[] survivalItems = {
                "barley", "soybean", "tomato", "barley_seeds", "spinach",
                "sea_salt", "raw_water_bottle", "boiled_water_bottle",
                "filtered_water_bottle", "empty_water_flask"
        };
        boolean agricultureItemsReady = true;
        for (String id : survivalItems) {
            if (!net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                            "gugugaga", id))) {
                System.err.println("THRESHOLD_MISSING_AGRICULTURE_ITEM: " + id);
                agricultureItemsReady = false;
            }
        }
        System.out.println(agricultureItemsReady
                ? "THRESHOLD_AGRICULTURE_SURVIVAL_ITEMS_OK"
                : "THRESHOLD_AGRICULTURE_SURVIVAL_ITEMS_FAIL");
    }
}
