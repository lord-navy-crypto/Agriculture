package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
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

        boolean watcherRegistered = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.containsKey(
                ResourceLocation.fromNamespaceAndPath("threshold", "watcher"))
                && ThresholdEntities.WATCHER.get() != null;
        boolean houndRegistered = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.containsKey(
                ResourceLocation.fromNamespaceAndPath("threshold", "hound"))
                && ThresholdEntities.HOUND.get() != null;
        System.out.println(watcherRegistered && houndRegistered
                ? "THRESHOLD_ENTITY_REGISTRY_OK" : "THRESHOLD_ENTITY_REGISTRY_FAIL");
        System.out.println(houndRegistered
                ? "THRESHOLD_LEVEL1_HOUND_REGISTRY_OK" : "THRESHOLD_LEVEL1_HOUND_REGISTRY_FAIL");

        level0.getChunk(0, 0);
        level0.getChunk(0, 1);
        level0.getChunk(1, 0);
        level0.getChunk(1, 1);
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

        boolean level0NoHostileEntities = level0.getEntitiesOfClass(
                Monster.class, new AABB(-32, 0, -32, 32, 128, 32)).isEmpty();
        System.out.println(level0NoHostileEntities
                ? "THRESHOLD_LEVEL0_NO_HOSTILE_ENTITIES_OK"
                : "THRESHOLD_LEVEL0_NO_HOSTILE_ENTITIES_FAIL");
        // Scan every wall, divider, pillar and transition segment in the first 32x32 room.
        boolean wallFloorConnected = true;
        int wallCells = 0;
        for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
                BlockPos upperPos = new BlockPos(x, 65, z);
                BlockState upper = level0.getBlockState(upperPos);
                if (!isLevel0Wall(upper)) continue;
                wallCells++;
                BlockPos footingPos = upperPos.below();
                BlockState footing = level0.getBlockState(footingPos);
                boolean matchingFooting = footing.getBlock() == upper.getBlock();
                boolean solidFloor = level0.getBlockState(footingPos.below()).is(Blocks.GRAY_CONCRETE);
                if (!matchingFooting || !solidFloor) wallFloorConnected = false;
            }
        }
        wallFloorConnected &= wallCells > 0;
        System.out.println(wallFloorConnected
                ? "THRESHOLD_LEVEL0_ALL_WALL_FLOOR_CONTACT_OK"
                : "THRESHOLD_LEVEL0_ALL_WALL_FLOOR_CONTACT_FAIL");

        boolean transitionFooting = level0.getBlockState(new BlockPos(0, 64, 15))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.TRANSITION_WALL.get())
                && level0.getBlockState(new BlockPos(0, 63, 15)).is(Blocks.GRAY_CONCRETE);
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

        boolean level1FlickerBuilt = level1.getBlockState(new BlockPos(24, 74, 24))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FLICKERING_PANEL.get());
        System.out.println(level1FlickerBuilt
                ? "THRESHOLD_LEVEL1_FLICKERING_LIGHT_OK"
                : "THRESHOLD_LEVEL1_FLICKERING_LIGHT_FAIL");

        boolean level0HorrorIdentity =
                level0.getBlockState(new BlockPos(0, 65, 4))
                        .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FADED_WALLPAPER.get())
                && level0.getBlockState(new BlockPos(4, 64, 4)).is(Blocks.YELLOW_CARPET)
                && level0.getBlockState(new BlockPos(0, 63, 4)).is(Blocks.GRAY_CONCRETE)
                && level0.getBlockState(new BlockPos(8, 72, 8))
                        .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FLICKERING_PANEL.get());
        System.out.println(level0HorrorIdentity
                ? "THRESHOLD_LEVEL0_HORROR_IDENTITY_OK"
                : "THRESHOLD_LEVEL0_HORROR_IDENTITY_FAIL");

        boolean level1HorrorIdentity =
                level1.getBlockState(new BlockPos(0, 65, 4))
                        .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get())
                && level1.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.SMOOTH_STONE)
                && level1.getBlockState(new BlockPos(12, 73, 12))
                        .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.CEILING_VENT.get())
                && level1.getBlockState(new BlockPos(24, 74, 24))
                        .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FLICKERING_PANEL.get());
        System.out.println(level1HorrorIdentity
                ? "THRESHOLD_LEVEL1_HORROR_IDENTITY_OK"
                : "THRESHOLD_LEVEL1_HORROR_IDENTITY_FAIL");

        boolean freshwaterBuilt = level1.getBlockState(new BlockPos(16, 63, 16)).is(Blocks.WATER)
                && level1.getFluidState(new BlockPos(16, 63, 16)).isSource();
        System.out.println(freshwaterBuilt
                ? "THRESHOLD_LEVEL1_FRESHWATER_REFILL_OK"
                : "THRESHOLD_LEVEL1_FRESHWATER_REFILL_FAIL");

        boolean supplyBuilt = level1.getBlockState(new BlockPos(8, 64, 8)).is(Blocks.BARREL);
        System.out.println(supplyBuilt
                ? "THRESHOLD_LEVEL1_STARTER_CACHE_OK" : "THRESHOLD_LEVEL1_STARTER_CACHE_FAIL");

        BlockState starterCrop = level1.getBlockState(new BlockPos(14, 64, 14));
        BlockState secondCrop = level1.getBlockState(new BlockPos(15, 64, 14));
        boolean cropBuilt = starterCrop.is(
                net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                "gugugaga", "barley_crop")))
                && starterCrop.getValue(net.minecraft.world.level.block.CropBlock.AGE) == 7;
        boolean cropDiversity = secondCrop.is(
                net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                "gugugaga", "corn_crop")))
                && secondCrop.getValue(net.minecraft.world.level.block.CropBlock.AGE) == 7;
        boolean planterBuilt = level1.getBlockState(new BlockPos(14, 63, 14)).is(Blocks.FARMLAND)
                && level1.getBlockState(new BlockPos(16, 63, 16)).is(Blocks.WATER)
                && cropBuilt
                && level1.getBlockState(new BlockPos(14, 67, 14)).is(Blocks.SEA_LANTERN);
        System.out.println(planterBuilt
                ? "THRESHOLD_LEVEL1_LIT_PLANTER_OK" : "THRESHOLD_LEVEL1_LIT_PLANTER_FAIL");
        System.out.println(cropBuilt
                ? "THRESHOLD_LEVEL1_AGRICULTURE_CROP_OK" : "THRESHOLD_LEVEL1_AGRICULTURE_CROP_FAIL");
        System.out.println(cropDiversity
                ? "THRESHOLD_LEVEL1_AGRICULTURE_CROP_DIVERSITY_OK"
                : "THRESHOLD_LEVEL1_AGRICULTURE_CROP_DIVERSITY_FAIL");
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
                "filtered_water_bottle", "empty_water_flask",
                "barley_vegetable_soup", "grain_porridge", "vegetable_stew",
                "bean_stew", "garden_salad"
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

    private static boolean isLevel0Wall(BlockState state) {
        return state.is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FADED_WALLPAPER.get())
                || state.is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get())
                || state.is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.TRANSITION_WALL.get())
                || state.is(Blocks.YELLOW_CONCRETE)
                || state.is(Blocks.GRAY_CONCRETE)
                || state.is(Blocks.RED_TERRACOTTA);
    }
}
