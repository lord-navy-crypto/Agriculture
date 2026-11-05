package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntityDirector;
import io.github.lordnavycrypto.threshold.entity.ThresholdWatcherEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
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
        ServerLevel level2 = server.getLevel(ThresholdDimensions.LEVEL_2);
        ServerLevel level3 = server.getLevel(ThresholdDimensions.LEVEL_3);
        ServerLevel level4 = server.getLevel(ThresholdDimensions.LEVEL_4);
        ServerLevel level5 = server.getLevel(ThresholdDimensions.LEVEL_5);

        if (level0 == null || level1 == null || level2 == null || level3 == null || level4 == null || level5 == null) {
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
        level1.getChunk(3, 1);
        level1.getChunk(4, 1);
        level2.getChunk(0, 0);
        level2.getChunk(0, 1);
        level2.getChunk(1, 1);
        level3.getChunk(0, 0);
        level3.getChunk(0, 1);
        level3.getChunk(1, 1);
        level4.getChunk(0, 0);
        level4.getChunk(0, 1);
        level4.getChunk(2, 1);
        level5.getChunk(0, 0);
        level5.getChunk(1, 0);
        level5.getChunk(0, 1);
        level5.getChunk(1, 1);
        level5.getChunk(3, 1);
        level5.getChunk(1, 3);

        System.out.println("THRESHOLD_LEVEL0_SAMPLE floor=" +
                level0.getBlockState(new BlockPos(4, 63, 4)) +
                " carpet=" + level0.getBlockState(new BlockPos(4, 64, 4)) +
                " biome=" + level0.getBiome(new BlockPos(4, 65, 4)).unwrapKey());
        System.out.println("THRESHOLD_LEVEL1_SAMPLE floor=" +
                level1.getBlockState(new BlockPos(4, 63, 4)) +
                " biome=" + level1.getBiome(new BlockPos(4, 65, 4)).unwrapKey());

        System.out.println("THRESHOLD_LEVEL2_SAMPLE floor=" +
                level2.getBlockState(new BlockPos(4, 63, 4)) +
                " biome=" + level2.getBiome(new BlockPos(4, 65, 4)).unwrapKey());

        System.out.println("THRESHOLD_LEVEL3_SAMPLE floor=" +
                level3.getBlockState(new BlockPos(4, 63, 4)) +
                " biome=" + level3.getBiome(new BlockPos(4, 65, 4)).unwrapKey());
        System.out.println("THRESHOLD_LEVEL4_SAMPLE floor=" +
                level4.getBlockState(new BlockPos(4, 63, 4)) +
                " biome=" + level4.getBiome(new BlockPos(4, 65, 4)).unwrapKey());

        System.out.println("THRESHOLD_LEVEL5_SAMPLE floor=" +
                level5.getBlockState(new BlockPos(4, 63, 4)) +
                " biome=" + level5.getBiome(new BlockPos(4, 65, 4)).unwrapKey());

        boolean level2Built = level2.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.GRAY_CONCRETE);
        System.out.println(level2Built ? "THRESHOLD_LEVEL2_WORLDGEN_OK" : "THRESHOLD_LEVEL2_WORLDGEN_FAIL");
        boolean level3Built = level3.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.DEEPSLATE_TILES);
        boolean level4Built = level4.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.SMOOTH_STONE);
        System.out.println(level3Built ? "THRESHOLD_LEVEL3_WORLDGEN_OK" : "THRESHOLD_LEVEL3_WORLDGEN_FAIL");
        System.out.println(level4Built ? "THRESHOLD_LEVEL4_WORLDGEN_OK" : "THRESHOLD_LEVEL4_WORLDGEN_FAIL");

        boolean level5MainHallBuilt = level5.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.DARK_OAK_PLANKS);
        boolean level5BeverlyBuilt = level5.getBlockState(new BlockPos(48, 63, 16)).is(Blocks.CALCITE);
        boolean level5BoilerBuilt = level5.getBlockState(new BlockPos(16, 63, 48)).is(Blocks.POLISHED_BLACKSTONE);
        System.out.println(level5MainHallBuilt ? "THRESHOLD_LEVEL5_MAIN_HALL_WORLDGEN_OK" : "THRESHOLD_LEVEL5_MAIN_HALL_WORLDGEN_FAIL");
        System.out.println(level5BeverlyBuilt ? "THRESHOLD_LEVEL5_BEVERLY_ROOM_WORLDGEN_OK" : "THRESHOLD_LEVEL5_BEVERLY_ROOM_WORLDGEN_FAIL");
        System.out.println(level5BoilerBuilt ? "THRESHOLD_LEVEL5_BOILER_ROOM_WORLDGEN_OK" : "THRESHOLD_LEVEL5_BOILER_ROOM_WORLDGEN_FAIL");

        boolean route1To2 = level1.getBlockState(new BlockPos(64, 64, 23))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_1_TO_2.get());
        boolean route2FireExit = level2.getBlockState(new BlockPos(0, 64, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_2_FIRE_EXIT.get());
        boolean route2To3 = level2.getBlockState(new BlockPos(31, 64, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_2_TO_3.get());
        boolean route2Branch = level2.getBlockState(new BlockPos(16, 64, 31))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_2_BRANCH_DOOR.get());
        boolean route3To2 = level3.getBlockState(new BlockPos(0, 64, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_3_TO_2.get());
        boolean route3To4 = level3.getBlockState(new BlockPos(31, 64, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_3_TO_4.get());
        boolean route4To3 = level4.getBlockState(new BlockPos(0, 64, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_4_TO_3.get());
        System.out.println(route1To2 ? "THRESHOLD_LEVEL1_TO_LEVEL2_PASSAGE_OK" : "THRESHOLD_LEVEL1_TO_LEVEL2_PASSAGE_FAIL");
        System.out.println(route2FireExit ? "THRESHOLD_LEVEL2_FIRE_EXIT_ROUTE_OK" : "THRESHOLD_LEVEL2_FIRE_EXIT_ROUTE_FAIL");
        System.out.println(route2To3 ? "THRESHOLD_LEVEL2_TO_LEVEL3_PASSAGE_OK" : "THRESHOLD_LEVEL2_TO_LEVEL3_PASSAGE_FAIL");
        System.out.println(route2Branch ? "THRESHOLD_LEVEL2_WEIGHTED_BRANCH_OK" : "THRESHOLD_LEVEL2_WEIGHTED_BRANCH_FAIL");
        System.out.println(route3To2 ? "THRESHOLD_LEVEL3_TO_LEVEL2_PASSAGE_OK" : "THRESHOLD_LEVEL3_TO_LEVEL2_PASSAGE_FAIL");
        System.out.println(route3To4 ? "THRESHOLD_LEVEL3_TO_LEVEL4_PASSAGE_OK" : "THRESHOLD_LEVEL3_TO_LEVEL4_PASSAGE_FAIL");
        System.out.println(route4To3 ? "THRESHOLD_LEVEL4_TO_LEVEL3_PASSAGE_OK" : "THRESHOLD_LEVEL4_TO_LEVEL3_PASSAGE_FAIL");

        boolean route3Elevator = level3.getBlockState(new BlockPos(16, 64, 31))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_3_ELEVATOR.get());
        boolean route4To5 = level4.getBlockState(new BlockPos(47, 68, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_4_TO_5.get());
        boolean route5To3 = level5.getBlockState(new BlockPos(0, 64, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_5_TO_3.get());
        boolean route5To4 = level5.getBlockState(new BlockPos(31, 64, 16))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_5_TO_4.get());
        boolean hotelElevatorPanel = level5.getBlockState(new BlockPos(28, 65, 8))
                .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.LEVEL_5_ELEVATOR_PANEL.get())
                && level5.getBlockState(new BlockPos(28, 65, 8))
                    .getValue(io.github.lordnavycrypto.threshold.block.ThresholdHotelElevatorBlock.FLOOR) == 1;
        System.out.println(route3Elevator ? "THRESHOLD_LEVEL3_ELEVATOR_TO_LEVEL5_ROUTE_OK" : "THRESHOLD_LEVEL3_ELEVATOR_TO_LEVEL5_ROUTE_FAIL");
        System.out.println(route4To5 ? "THRESHOLD_LEVEL4_STAIRWAY_TO_LEVEL5_ROUTE_OK" : "THRESHOLD_LEVEL4_STAIRWAY_TO_LEVEL5_ROUTE_FAIL");
        System.out.println(route5To3 ? "THRESHOLD_LEVEL5_TO_LEVEL3_ELEVATOR_RETURN_OK" : "THRESHOLD_LEVEL5_TO_LEVEL3_ELEVATOR_RETURN_FAIL");
        System.out.println(route5To4 ? "THRESHOLD_LEVEL5_TO_LEVEL4_STAIRWAY_RETURN_OK" : "THRESHOLD_LEVEL5_TO_LEVEL4_STAIRWAY_RETURN_FAIL");
        System.out.println(hotelElevatorPanel ? "THRESHOLD_LEVEL5_INTERNAL_ELEVATOR_PANEL_OK" : "THRESHOLD_LEVEL5_INTERNAL_ELEVATOR_PANEL_FAIL");

        boolean level0MusicReady = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.containsKey(
                ResourceLocation.fromNamespaceAndPath("threshold", "level_0_ambient"));
        boolean level1MusicReady = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.containsKey(
                ResourceLocation.fromNamespaceAndPath("threshold", "level_1_ambient"));
        System.out.println(level0MusicReady ? "THRESHOLD_LEVEL0_MUSIC_REGISTRY_OK" : "THRESHOLD_LEVEL0_MUSIC_REGISTRY_FAIL");
        System.out.println(level1MusicReady ? "THRESHOLD_LEVEL1_MUSIC_REGISTRY_OK" : "THRESHOLD_LEVEL1_MUSIC_REGISTRY_FAIL");

        boolean level0Built =
                level0.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.GRAY_CONCRETE)
                && level0.getBlockState(new BlockPos(4, 64, 4)).is(Blocks.YELLOW_CARPET);
        boolean level1Built =
                level1.getBlockState(new BlockPos(4, 63, 4)).is(Blocks.SMOOTH_STONE);

        System.out.println(level0Built
                ? "THRESHOLD_LEVEL0_WORLDGEN_OK" : "THRESHOLD_LEVEL0_WORLDGEN_FAIL");

        boolean level0NoOtherCreatures = level0.getEntitiesOfClass(
                LivingEntity.class, new AABB(-32, 0, -32, 32, 128, 32)).stream()
                .noneMatch(living -> !(living instanceof Player)
                        && !(living instanceof ThresholdWatcherEntity));
        System.out.println(level0NoOtherCreatures
                ? "THRESHOLD_LEVEL0_NO_OTHER_CREATURES_OK"
                : "THRESHOLD_LEVEL0_NO_OTHER_CREATURES_FAIL");

        boolean level1EntryShelterClear = level1.getEntitiesOfClass(
                Mob.class, new AABB(0, 0, 0, 48, 128, 48)).isEmpty();
        System.out.println(level1EntryShelterClear
                ? "THRESHOLD_LEVEL1_STARTER_SHELTER_CLEAR_OK"
                : "THRESHOLD_LEVEL1_STARTER_SHELTER_CLEAR_FAIL");
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

        // Entity admission is intentionally universal: ordinary mobs and
        // functional entities must be able to join every dimension. These
        // short-lived probes cover living mobs, dropped items, projectiles,
        // minecarts and item frames.
        boolean entityCompatibility =
                supportsEntityCompatibility(level0)
                && supportsEntityCompatibility(level1)
                && supportsEntityCompatibility(level2)
                && supportsEntityCompatibility(level3)
                && supportsEntityCompatibility(level4)
                && supportsEntityCompatibility(level5);
        System.out.println(entityCompatibility
                ? "THRESHOLD_ENTITY_COMPATIBILITY_ALL_DIMENSIONS_OK"
                : "THRESHOLD_ENTITY_COMPATIBILITY_ALL_DIMENSIONS_FAIL");

        // Natural spawning has a separate explicit whitelist. The Partygoer
        // keeps its pre-existing night encounter in the Overworld; it is not
        // part of Level 0 or Level 1's natural spawn profile.
        boolean naturalSpawnPolicy =
                naturalProfile(ThresholdDimensions.LEVEL_0,
                        ThresholdEntities.WATCHER.get(), true)
                && naturalProfile(ThresholdDimensions.LEVEL_0,
                        ThresholdEntities.HOUND.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_0,
                        ThresholdEntities.PARTYGOER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_1,
                        ThresholdEntities.WATCHER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_1,
                        ThresholdEntities.HOUND.get(), true)
                && naturalProfile(ThresholdDimensions.LEVEL_1,
                        ThresholdEntities.PARTYGOER.get(), false)
                && naturalProfile(net.minecraft.world.level.Level.OVERWORLD,
                        ThresholdEntities.PARTYGOER.get(), true)
                && naturalProfile(ThresholdDimensions.LEVEL_2,
                        ThresholdEntities.WATCHER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_2,
                        ThresholdEntities.HOUND.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_2,
                        ThresholdEntities.PARTYGOER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_3,
                        ThresholdEntities.WATCHER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_3,
                        ThresholdEntities.HOUND.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_3,
                        ThresholdEntities.PARTYGOER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_4,
                        ThresholdEntities.WATCHER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_4,
                        ThresholdEntities.HOUND.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_4,
                        ThresholdEntities.PARTYGOER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_5,
                        ThresholdEntities.WATCHER.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_5,
                        ThresholdEntities.HOUND.get(), false)
                && naturalProfile(ThresholdDimensions.LEVEL_5,
                        ThresholdEntities.PARTYGOER.get(), false);
        System.out.println(naturalSpawnPolicy
                ? "THRESHOLD_NATURAL_SPAWN_POLICY_OK"
                : "THRESHOLD_NATURAL_SPAWN_POLICY_FAIL");
    }

    private static boolean naturalProfile(
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,
            EntityType<?> type, boolean expected) {
        return ThresholdEntityDirector.isNaturalSpawnAllowed(dimension, type) == expected;
    }

    private static boolean supportsEntityCompatibility(ServerLevel level) {
        return acceptsEntityProbe(level, EntityType.COW, 6.5D)
                && acceptsEntityProbe(level, EntityType.ZOMBIE, 8.5D)
                && acceptsEntityProbe(level, EntityType.ITEM, 10.5D)
                && acceptsEntityProbe(level, EntityType.ARROW, 12.5D)
                && acceptsEntityProbe(level, EntityType.TRIDENT, 14.5D)
                && acceptsEntityProbe(level, EntityType.MINECART, 16.5D)
                && acceptsEntityProbe(level, EntityType.ITEM_FRAME, 18.5D)
                && acceptsEntityProbe(level, ThresholdEntities.WATCHER.get(), 20.5D)
                && acceptsEntityProbe(level, ThresholdEntities.HOUND.get(), 22.5D)
                && acceptsEntityProbe(level, ThresholdEntities.PARTYGOER.get(), 24.5D);
    }

    private static boolean acceptsEntityProbe(
            ServerLevel level, EntityType<?> type, double x) {
        Entity probe = type.create(level);
        if (probe == null) return false;
        probe.moveTo(x, 80.0D, 8.5D, 0.0F, 0.0F);
        if (!level.addFreshEntity(probe)) {
            probe.discard();
            return false;
        }
        probe.discard();
        return true;
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
