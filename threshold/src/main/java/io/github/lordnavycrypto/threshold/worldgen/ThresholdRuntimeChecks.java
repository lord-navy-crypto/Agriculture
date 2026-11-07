package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntityDirector;
import io.github.lordnavycrypto.threshold.entity.ThresholdWatcherEntity;
import io.github.lordnavycrypto.threshold.entity.ThresholdHoundEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

        ThresholdWatcherEntity watcherProbe = ThresholdEntities.WATCHER.get().create(level0);
        boolean watcherAdvancedAiReady = watcherProbe != null
                && watcherProbe.getNavigation() != null
                && watcherProbe.getAttribute(Attributes.MAX_HEALTH) != null
                && watcherProbe.getAttribute(Attributes.MAX_HEALTH).getBaseValue() >= 14.0D
                && watcherProbe.getAttribute(Attributes.MOVEMENT_SPEED) != null
                && watcherProbe.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() >= 0.30D
                && watcherProbe.getAttribute(Attributes.ATTACK_DAMAGE) != null
                && watcherProbe.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() >= 2.0D
                && watcherProbe.isPickable();
        if (watcherProbe != null) watcherProbe.discard();
        System.out.println(watcherAdvancedAiReady
                ? "THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_OK"
                : "THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_FAIL");

        ThresholdHoundEntity houndProbe = ThresholdEntities.HOUND.get().create(level1);
        boolean houndAdvancedAiReady = houndProbe != null
                && houndProbe.getNavigation() != null
                && houndProbe.getAttribute(Attributes.MAX_HEALTH) != null
                && houndProbe.getAttribute(Attributes.MAX_HEALTH).getBaseValue() >= 20.0D
                && houndProbe.getAttribute(Attributes.MOVEMENT_SPEED) != null
                && houndProbe.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() >= 0.285D
                && houndProbe.getAttribute(Attributes.ATTACK_DAMAGE) != null
                && houndProbe.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() >= 3.0D
                && houndProbe.getAttribute(Attributes.FOLLOW_RANGE) != null
                && houndProbe.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() >= 26.0D
                && houndProbe.isPickable();
        if (houndProbe != null) houndProbe.discard();
        System.out.println(houndAdvancedAiReady
                ? "THRESHOLD_LEVEL1_HOUND_ADVANCED_AI_READY_OK"
                : "THRESHOLD_LEVEL1_HOUND_ADVANCED_AI_READY_FAIL");

        level0.getChunk(0, 0);
        level0.getChunk(0, 1);
        level0.getChunk(1, 0);
        level0.getChunk(1, 1);
        level0.getChunk(3, 0);
        level0.getChunk(3, 1);
        // Expanded Level 0 landmarks: raised mezzanine, nested maze and sunken pit.
        level0.getChunk(0, 3);
        level0.getChunk(2, 2);
        level0.getChunk(2, 3);
        level0.getChunk(3, 2);
        level0.getChunk(3, 3);
        level0.getChunk(5, 1);
        // Alpha 16 landmark chunks: pillar grove, floor-fold pockets and stair tower.
        level0.getChunk(4, 4);
        level0.getChunk(6, 0);
        level0.getChunk(0, 4);
        // Alpha 17: guaranteed Level 0 blackout corridor.
        level0.getChunk(6, 4);
        level1.getChunk(0, 0);
        level1.getChunk(0, 1);
        level1.getChunk(1, 0);
        level1.getChunk(1, 1);
        level1.getChunk(3, 1);
        level1.getChunk(4, 1);
        level1.getChunk(4, 4);
        level1.getChunk(5, 4);
        // Expanded Level 1 landmarks: raised loading mezzanine and stacked rack canyon.
        level1.getChunk(0, 4);
        level1.getChunk(0, 5);
        level1.getChunk(6, 3);
        level1.getChunk(6, 4);
        level1.getChunk(6, 5);
        // Alpha 16 Level 1 landmarks: Gothic arcade, crane hall and ventilation exchange.
        level1.getChunk(6, 6);
        level1.getChunk(10, 1);
        level1.getChunk(0, 6);
        // Alpha 17: guaranteed Level 1 generator plant.
        level1.getChunk(6, 9);
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

        boolean level0VerticalLayout =
                level0.getBlockState(new BlockPos(48, 64, 12)).is(Blocks.OAK_STAIRS)
                && level0.getBlockState(new BlockPos(48, 68, 16)).is(Blocks.OAK_STAIRS)
                && level0.getBlockState(new BlockPos(50, 68, 16)).is(Blocks.WHITE_CONCRETE)
                && level0.getBlockState(new BlockPos(55, 63, 16)).is(Blocks.OAK_STAIRS)
                && level0.getBlockState(new BlockPos(55, 58, 11)).is(Blocks.OAK_STAIRS)
                && level0.getBlockState(new BlockPos(53, 58, 8)).is(Blocks.GRAY_CONCRETE)
                && level0.getBlockState(new BlockPos(53, 59, 8)).is(Blocks.YELLOW_CARPET)
                && level0.getBlockState(new BlockPos(56, 62, 8))
                        .is(ThresholdEnvironmentBlocks.FLICKERING_PANEL.get());
        System.out.println(level0VerticalLayout
                ? "THRESHOLD_LEVEL0_VERTICAL_LAYOUT_OK"
                : "THRESHOLD_LEVEL0_VERTICAL_LAYOUT_FAIL");

        boolean level1VerticalLayout =
                level1.getBlockState(new BlockPos(72, 64, 66)).is(Blocks.STONE_BRICK_STAIRS)
                && level1.getBlockState(new BlockPos(72, 70, 72)).is(Blocks.STONE_BRICK_STAIRS)
                && level1.getBlockState(new BlockPos(74, 70, 72)).is(Blocks.IRON_BLOCK)
                && level1.getBlockState(new BlockPos(81, 63, 74)).is(Blocks.STONE_BRICK_STAIRS)
                && level1.getBlockState(new BlockPos(81, 58, 69)).is(Blocks.STONE_BRICK_STAIRS)
                && level1.getBlockState(new BlockPos(78, 58, 64)).is(Blocks.SMOOTH_STONE)
                && level1.getBlockState(new BlockPos(78, 59, 64)).is(Blocks.GRAY_CARPET)
                && level1.getBlockState(new BlockPos(82, 63, 66))
                        .is(ThresholdEnvironmentBlocks.CEILING_VENT.get());
        System.out.println(level1VerticalLayout
                ? "THRESHOLD_LEVEL1_VERTICAL_LAYOUT_OK"
                : "THRESHOLD_LEVEL1_VERTICAL_LAYOUT_FAIL");

        boolean level0BlackoutBuilt =
                level0.getBlockState(new BlockPos(108, 71, 76)).is(Blocks.BLACK_CONCRETE)
                && level0.getBlockState(new BlockPos(108, 72, 76))
                        .is(ThresholdEnvironmentBlocks.FLICKERING_PANEL.get())
                && !level0.getBlockState(new BlockPos(108, 72, 76))
                        .getValue(io.github.lordnavycrypto.threshold.block.FlickeringPanelBlock.LIT);
        System.out.println(level0BlackoutBuilt
                ? "THRESHOLD_LEVEL0_BLACKOUT_CORRIDOR_OK"
                : "THRESHOLD_LEVEL0_BLACKOUT_CORRIDOR_FAIL");

        boolean level1GeneratorBuilt =
                level1.getBlockState(new BlockPos(106, 65, 154)).is(Blocks.FURNACE)
                && level1.getBlockState(new BlockPos(106, 66, 154)).is(Blocks.COPPER_BLOCK)
                && level1.getBlockState(new BlockPos(106, 68, 154)).is(Blocks.IRON_BARS);
        System.out.println(level1GeneratorBuilt
                ? "THRESHOLD_LEVEL1_GENERATOR_PLANT_OK"
                : "THRESHOLD_LEVEL1_GENERATOR_PLANT_FAIL");

        boolean level0PillarForestBuilt =
                level0.getBlockState(new BlockPos(70, 65, 70)).is(Blocks.YELLOW_CONCRETE)
                || level0.getBlockState(new BlockPos(70, 65, 70)).is(Blocks.SMOOTH_SANDSTONE);
        System.out.println(level0PillarForestBuilt
                ? "THRESHOLD_LEVEL0_PILLAR_FOREST_OK" : "THRESHOLD_LEVEL0_PILLAR_FOREST_FAIL");

        boolean level0FloorFoldBuilt = false;
        for (int x = 102; x <= 107; x++) {
            for (int z = 7; z <= 12; z++) {
                if (level0.getBlockState(new BlockPos(x, 62, z)).is(Blocks.SMOOTH_STONE)) {
                    level0FloorFoldBuilt = true;
                }
            }
        }
        System.out.println(level0FloorFoldBuilt
                ? "THRESHOLD_LEVEL0_FLOOR_FOLD_OK" : "THRESHOLD_LEVEL0_FLOOR_FOLD_FAIL");

        boolean level0StairTowerBuilt = false;
        for (int x = 4; x <= 11; x++) {
            for (int z = 68; z <= 75; z++) {
                if (level0.getBlockState(new BlockPos(x, 65, z)).is(Blocks.YELLOW_CONCRETE)
                        || level0.getBlockState(new BlockPos(x, 65, z))
                                .is(ThresholdEnvironmentBlocks.FADED_WALLPAPER.get())) {
                    level0StairTowerBuilt = true;
                }
            }
        }
        for (int y = 64; y <= 67; y++) {
            if (level0.getBlockState(new BlockPos(7, y, 68)).is(Blocks.OAK_STAIRS)
                    || level0.getBlockState(new BlockPos(7, y, 71)).is(Blocks.OAK_STAIRS)) {
                level0StairTowerBuilt = true;
            }
        }
        System.out.println(level0StairTowerBuilt
                ? "THRESHOLD_LEVEL0_STAIR_TOWER_OK" : "THRESHOLD_LEVEL0_STAIR_TOWER_FAIL");

        boolean level1GothicArcadeBuilt =
                level1.getBlockState(new BlockPos(106, 65, 106)).is(Blocks.POLISHED_ANDESITE);
        System.out.println(level1GothicArcadeBuilt
                ? "THRESHOLD_LEVEL1_GOTHIC_ARCADE_OK" : "THRESHOLD_LEVEL1_GOTHIC_ARCADE_FAIL");

        boolean level1CraneHallBuilt =
                level1.getBlockState(new BlockPos(168, 71, 18)).is(Blocks.IRON_BLOCK);
        System.out.println(level1CraneHallBuilt
                ? "THRESHOLD_LEVEL1_CRANE_HALL_OK" : "THRESHOLD_LEVEL1_CRANE_HALL_FAIL");

        boolean level1VentilationHallBuilt =
                level1.getBlockState(new BlockPos(10, 65, 106)).is(Blocks.IRON_BLOCK);
        System.out.println(level1VentilationHallBuilt
                ? "THRESHOLD_LEVEL1_VENTILATION_HALL_OK" : "THRESHOLD_LEVEL1_VENTILATION_HALL_FAIL");

        boolean level0MezzanineBuilt = false;
        for (int x = 7; x <= 14; x++) {
            for (int z = 53; z <= 60; z++) {
                if (level0.getBlockState(new BlockPos(x, 66, z)).is(Blocks.OAK_PLANKS)) {
                    level0MezzanineBuilt = true;
                }
            }
        }
        boolean level0MezzanineStairBuilt = false;
        for (int x = 9; x <= 10; x++) {
            for (int z = 50; z <= 52; z++) {
                for (int y = 64; y <= 66; y++) {
                    if (level0.getBlockState(new BlockPos(x, y, z)).is(Blocks.OAK_STAIRS)) {
                        level0MezzanineStairBuilt = true;
                    }
                }
            }
        }
        for (int x = 4; x <= 6; x++) {
            for (int z = 56; z <= 57; z++) {
                for (int y = 64; y <= 66; y++) {
                    if (level0.getBlockState(new BlockPos(x, y, z)).is(Blocks.OAK_STAIRS)) {
                        level0MezzanineStairBuilt = true;
                    }
                }
            }
        }
        level0MezzanineBuilt &= level0MezzanineStairBuilt;
        System.out.println(level0MezzanineBuilt
                ? "THRESHOLD_LEVEL0_MEZZANINE_OK" : "THRESHOLD_LEVEL0_MEZZANINE_FAIL");

        boolean level0NestedMazeBuilt = false;
        for (int x = 39; x <= 56; x++) {
            for (int z = 39; z <= 56; z++) {
                if (level0.getBlockState(new BlockPos(x, 65, z))
                        .is(ThresholdEnvironmentBlocks.FADED_WALLPAPER.get())) {
                    level0NestedMazeBuilt = true;
                }
            }
        }
        System.out.println(level0NestedMazeBuilt
                ? "THRESHOLD_LEVEL0_NESTED_MAZE_OK" : "THRESHOLD_LEVEL0_NESTED_MAZE_FAIL");

        boolean level0SunkenPitBuilt = false;
        for (int x = 86; x <= 92; x++) {
            for (int z = 21; z <= 28; z++) {
                if (level0.getBlockState(new BlockPos(x, 60, z)).is(Blocks.SMOOTH_STONE)) {
                    level0SunkenPitBuilt = true;
                }
            }
        }
        boolean level0PitStairBuilt = false;
        for (int x = 82; x <= 85; x++) {
            for (int z = 23; z <= 24; z++) {
                for (int y = 60; y <= 63; y++) {
                    if (level0.getBlockState(new BlockPos(x, y, z)).is(Blocks.OAK_STAIRS)) {
                        level0PitStairBuilt = true;
                    }
                }
            }
        }
        for (int x = 93; x <= 95; x++) {
            for (int z = 23; z <= 24; z++) {
                for (int y = 61; y <= 63; y++) {
                    if (level0.getBlockState(new BlockPos(x, y, z)).is(Blocks.OAK_STAIRS)) {
                        level0PitStairBuilt = true;
                    }
                }
            }
        }
        level0SunkenPitBuilt &= level0PitStairBuilt;
        System.out.println(level0SunkenPitBuilt
                ? "THRESHOLD_LEVEL0_SUNKEN_PIT_OK" : "THRESHOLD_LEVEL0_SUNKEN_PIT_FAIL");

        boolean level1MezzanineBuilt = false;
        for (int x = 7; x <= 15; x++) {
            for (int z = 79; z <= 83; z++) {
                if (level1.getBlockState(new BlockPos(x, 66, z)).is(Blocks.IRON_BLOCK)) {
                    level1MezzanineBuilt = true;
                }
            }
        }
        boolean level1MezzanineStairBuilt = false;
        for (int x = 10; x <= 11; x++) {
            for (int z = 76; z <= 78; z++) {
                for (int y = 64; y <= 66; y++) {
                    if (level1.getBlockState(new BlockPos(x, y, z)).is(Blocks.STONE_BRICK_STAIRS)) {
                        level1MezzanineStairBuilt = true;
                    }
                }
            }
        }
        for (int x = 4; x <= 6; x++) {
            for (int z = 83; z <= 84; z++) {
                for (int y = 64; y <= 66; y++) {
                    if (level1.getBlockState(new BlockPos(x, y, z)).is(Blocks.STONE_BRICK_STAIRS)) {
                        level1MezzanineStairBuilt = true;
                    }
                }
            }
        }
        if (!level1MezzanineBuilt || !level1MezzanineStairBuilt) {
            System.err.println("THRESHOLD_LEVEL1_MEZZANINE_DEBUG platform="
                    + level1MezzanineBuilt + " stairs=" + level1MezzanineStairBuilt);
        }
        level1MezzanineBuilt &= level1MezzanineStairBuilt;
        System.out.println(level1MezzanineBuilt
                ? "THRESHOLD_LEVEL1_MEZZANINE_OK" : "THRESHOLD_LEVEL1_MEZZANINE_FAIL");

        boolean level1PumpSumpBuilt = false;
        for (int x = 55; x <= 60; x++) {
            for (int z = 17; z <= 22; z++) {
                if (level1.getBlockState(new BlockPos(x, 58, z)).is(Blocks.SMOOTH_STONE)) {
                    level1PumpSumpBuilt = true;
                }
            }
        }
        boolean level1PumpSumpStairBuilt = false;
        for (int x = 57; x <= 58; x++) {
            for (int z = 12; z <= 16; z++) {
                for (int y = 59; y <= 63; y++) {
                    if (level1.getBlockState(new BlockPos(x, y, z)).is(Blocks.STONE_BRICK_STAIRS)) {
                        level1PumpSumpStairBuilt = true;
                    }
                }
            }
        }
        for (int x = 61; x <= 65; x++) {
            for (int z = 20; z <= 21; z++) {
                for (int y = 59; y <= 63; y++) {
                    if (level1.getBlockState(new BlockPos(x, y, z)).is(Blocks.STONE_BRICK_STAIRS)) {
                        level1PumpSumpStairBuilt = true;
                    }
                }
            }
        }
        level1PumpSumpBuilt &= level1PumpSumpStairBuilt;
        System.out.println(level1PumpSumpBuilt
                ? "THRESHOLD_LEVEL1_PUMP_SUMP_OK" : "THRESHOLD_LEVEL1_PUMP_SUMP_FAIL");

        boolean level1RackCanyonBuilt = false;
        for (int x = 104; x <= 110; x++) {
            for (int z = 60; z <= 84; z += 8) {
                if (level1.getBlockState(new BlockPos(x, 66, z)).is(Blocks.IRON_BLOCK)) {
                    level1RackCanyonBuilt = true;
                }
            }
        }
        System.out.println(level1RackCanyonBuilt
                ? "THRESHOLD_LEVEL1_RACK_CANYON_OK" : "THRESHOLD_LEVEL1_RACK_CANYON_FAIL");
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

        // The irregular maze intentionally carves occasional openings in the
        // old 32x32 entry perimeter. Verify that its original wallpaper still
        // survives elsewhere instead of requiring one specific wall voxel.
        boolean level0PerimeterWallpaper = false;
        for (int z = 1; z < 32; z++) {
            if (level0.getBlockState(new BlockPos(0, 65, z))
                    .is(io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks.FADED_WALLPAPER.get())) {
                level0PerimeterWallpaper = true;
                break;
            }
        }
        boolean level0HorrorIdentity =
                level0PerimeterWallpaper
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
        boolean composterBuilt = level1.getBlockState(new BlockPos(20, 63, 16)).is(Blocks.COMPOSTER);
        System.out.println(composterBuilt ? "THRESHOLD_LEVEL1_COMPOSTER_OK" : "THRESHOLD_LEVEL1_COMPOSTER_FAIL");
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
        // short-lived probes cover living mobs, dropped items, minecarts,
        // item frames, and Threshold's own custom entities.
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

        // Level 0 remains Watcher-only. Level 1 now has Hounds plus rare
        // peripheral Partygoer encounters outside the 20x20 starter shelter.
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
                        ThresholdEntities.PARTYGOER.get(), true)
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
        boolean level1EntityVariety =
                naturalProfile(ThresholdDimensions.LEVEL_1, ThresholdEntities.HOUND.get(), true)
                && naturalProfile(ThresholdDimensions.LEVEL_1, ThresholdEntities.PARTYGOER.get(), true);
        System.out.println(level1EntityVariety
                ? "THRESHOLD_LEVEL1_ENTITY_VARIETY_OK"
                : "THRESHOLD_LEVEL1_ENTITY_VARIETY_FAIL");
        boolean irregularMazeGeometry =
                ThresholdIrregularMazeLayout.hasVariableCellSpacing(0L, true)
                && ThresholdIrregularMazeLayout.hasVariableCellSpacing(0L, false)
                && ThresholdIrregularMazeLayout.hasDoorwayWidthVariety(0L, true)
                && ThresholdIrregularMazeLayout.hasDoorwayWidthVariety(0L, false)
                && ThresholdIrregularMazeLayout.hasInteriorPartitionVariety(0L, true)
                && ThresholdIrregularMazeLayout.hasInteriorPartitionVariety(0L, false);
        System.out.println(irregularMazeGeometry
                ? "THRESHOLD_LEVEL0_LEVEL1_IRREGULAR_MAZE_OK"
                : "THRESHOLD_LEVEL0_LEVEL1_IRREGULAR_MAZE_FAIL");
        System.out.println(ThresholdIrregularMazeLayout.hasDoorwayWidthVariety(0L, true)
                ? "THRESHOLD_LEVEL0_VARIABLE_DOORWAYS_OK"
                : "THRESHOLD_LEVEL0_VARIABLE_DOORWAYS_FAIL");
        System.out.println(ThresholdIrregularMazeLayout.hasDoorwayWidthVariety(0L, false)
                ? "THRESHOLD_LEVEL1_VARIABLE_DOORWAYS_OK"
                : "THRESHOLD_LEVEL1_VARIABLE_DOORWAYS_FAIL");
        System.out.println(ThresholdIrregularMazeLayout.hasInteriorPartitionVariety(0L, true)
                ? "THRESHOLD_LEVEL0_MICRO_ROOM_GRAMMAR_OK"
                : "THRESHOLD_LEVEL0_MICRO_ROOM_GRAMMAR_FAIL");
        System.out.println(ThresholdIrregularMazeLayout.hasInteriorPartitionVariety(0L, false)
                ? "THRESHOLD_LEVEL1_STORAGE_BAY_GRAMMAR_OK"
                : "THRESHOLD_LEVEL1_STORAGE_BAY_GRAMMAR_FAIL");

        boolean spawnReasonPolicy =
                ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_0, EntityType.COW, MobSpawnType.NATURAL)
                && ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_0, EntityType.ZOMBIE, MobSpawnType.CHUNK_GENERATION)
                && !ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_0, ThresholdEntities.WATCHER.get(), MobSpawnType.NATURAL)
                && ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_1, EntityType.COW, MobSpawnType.NATURAL)
                && ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_1, EntityType.ZOMBIE, MobSpawnType.NATURAL)
                && !ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_1, ThresholdEntities.HOUND.get(), MobSpawnType.NATURAL)
                && !ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_1, ThresholdEntities.PARTYGOER.get(), MobSpawnType.NATURAL)
                && !ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_1, EntityType.ZOMBIE, MobSpawnType.SPAWN_EGG)
                && !ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_1, EntityType.ZOMBIE, MobSpawnType.COMMAND)
                && !ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_1, EntityType.COW, MobSpawnType.BREEDING)
                && !ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        net.minecraft.world.level.Level.OVERWORLD, EntityType.ZOMBIE, MobSpawnType.NATURAL)
                && ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_2, EntityType.ZOMBIE, MobSpawnType.NATURAL)
                && ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_3, EntityType.ZOMBIE, MobSpawnType.NATURAL)
                && ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_4, EntityType.ZOMBIE, MobSpawnType.NATURAL)
                && ThresholdEntityDirector.shouldBlockNaturalSpawn(
                        ThresholdDimensions.LEVEL_5, EntityType.ZOMBIE, MobSpawnType.NATURAL);
        System.out.println(spawnReasonPolicy
                ? "THRESHOLD_NATURAL_SPAWN_REASON_POLICY_OK"
                : "THRESHOLD_NATURAL_SPAWN_REASON_POLICY_FAIL");
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
                && acceptsEntityProbe(level, EntityType.MINECART, 12.5D)
                && acceptsEntityProbe(level, EntityType.ITEM_FRAME, 14.5D)
                && acceptsEntityProbe(level, EntityType.ARMOR_STAND, 16.5D)
                && acceptsEntityProbe(level, ThresholdEntities.WATCHER.get(), 18.5D)
                && acceptsEntityProbe(level, ThresholdEntities.HOUND.get(), 20.5D)
                && acceptsEntityProbe(level, ThresholdEntities.PARTYGOER.get(), 22.5D);
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
