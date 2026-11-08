package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import io.github.lordnavycrypto.threshold.block.FlickeringPanelBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StairBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Builds deterministic 16x16 chunk slices for Threshold Levels 0 through 5.
 * Room dimensions are multiples of 16, and every block write stays inside
 * the chunk currently being decorated.
 */
public final class ThresholdRoomLayoutFeature extends Feature<NoneFeatureConfiguration> {
    private static final int FLOOR_Y = 63;
    private static final ResourceKey<LootTable> SUPPLY_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("threshold", "chests/level_1_supply"));
    private static final ResourceKey<LootTable> STARTER_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("threshold", "chests/level_1_starter"));
    private static final ResourceKey<LootTable> MAINTENANCE_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("threshold", "chests/level_1_maintenance"));
    private static final ResourceKey<LootTable> FORGOTTEN_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("threshold", "chests/level_0_forgotten"));
    private static final ResourceKey<LootTable> LEVEL_3_SUPPLY_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("threshold", "chests/level_3_supply"));
    private static final ResourceKey<LootTable> LEVEL_4_SUPPLY_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("threshold", "chests/level_4_supply"));
    private static final ResourceKey<LootTable> LEVEL_5_SUPPLY_LOOT = ResourceKey.create(
            Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("threshold", "chests/level_5_supply"));

    private static final int LEVEL_0_ROOM_SIZE = 32;
    private static final int LEVEL_0_CARPET_Y = 64;
    private static final int LEVEL_0_WALL_BOTTOM = 65;
    private static final int LEVEL_0_WALL_TOP = 71;
    private static final int LEVEL_0_CEILING_Y = 72;

    private static final int LEVEL_1_ROOM_SIZE = 48;
    private static final int LEVEL_1_WALL_BOTTOM = 64;
    private static final int LEVEL_1_WALL_TOP = 73;
    private static final int LEVEL_1_CEILING_Y = 74;
    private static final int LEVEL_2_ROOM_SIZE = 32;
    private static final int LEVEL_2_WALL_BOTTOM = 64;
    private static final int LEVEL_2_WALL_TOP = 70;
    private static final int LEVEL_2_CEILING_Y = 71;
    private static final int LEVEL_3_ROOM_SIZE = 32;
    private static final int LEVEL_3_WALL_BOTTOM = 64;
    private static final int LEVEL_3_WALL_TOP = 70;
    private static final int LEVEL_3_CEILING_Y = 71;
    private static final int LEVEL_4_ROOM_SIZE = 48;
    private static final int LEVEL_4_WALL_BOTTOM = 64;
    private static final int LEVEL_4_WALL_TOP = 70;
    private static final int LEVEL_4_CEILING_Y = 71;
    private static final int LEVEL_5_ROOM_SIZE = 32;
    private static final int LEVEL_5_WALL_BOTTOM = 64;
    private static final int LEVEL_5_WALL_TOP = 70;
    private static final int LEVEL_5_CEILING_Y = 71;

    public ThresholdRoomLayoutFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        ServerLevel serverLevel = world.getLevel();
        int chunkX = context.origin().getX() >> 4;
        int chunkZ = context.origin().getZ() >> 4;

        if (serverLevel.dimension().equals(ThresholdDimensions.LEVEL_0)) {
            buildLevel0Chunk(world, chunkX, chunkZ);
            return true;
        }
        if (serverLevel.dimension().equals(ThresholdDimensions.LEVEL_1)) {
            buildLevel1Chunk(world, chunkX, chunkZ);
            return true;
        }
        if (serverLevel.dimension().equals(ThresholdDimensions.LEVEL_2)) {
            buildLevel2Chunk(world, chunkX, chunkZ);
            return true;
        }
        if (serverLevel.dimension().equals(ThresholdDimensions.LEVEL_3)) {
            buildLevel3Chunk(world, chunkX, chunkZ);
            return true;
        }
        if (serverLevel.dimension().equals(ThresholdDimensions.LEVEL_4)) {
            buildLevel4Chunk(world, chunkX, chunkZ);
            return true;
        }
        if (serverLevel.dimension().equals(ThresholdDimensions.LEVEL_5)) {
            buildLevel5Chunk(world, chunkX, chunkZ);
            return true;
        }
        return false;
    }

    private static void buildLevel0Chunk(WorldGenLevel world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_0_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_0_ROOM_SIZE);

            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_0_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_0_ROOM_SIZE);
                int style = level0Style(seed, roomX, roomZ);

                BlockState carpet = switch (style) {
                    case 2 -> Blocks.GRAY_CARPET.defaultBlockState();
                    case 3 -> Blocks.RED_CARPET.defaultBlockState();
                    default -> Blocks.YELLOW_CARPET.defaultBlockState();
                };
                // Rare damp-looking carpet patches amplify emptiness without
                // spawning entities or changing walking/collision rules.
                if (style == 0 && (roomX != 0 || roomZ != 0)
                        && Math.floorMod(mix(seed, x, z, 0xDAA0L), 43) == 0) {
                    carpet = Blocks.BROWN_CARPET.defaultBlockState();
                }
                BlockState wall = switch (style) {
                    case 2 -> Blocks.GRAY_CONCRETE.defaultBlockState();
                    case 3 -> Blocks.RED_TERRACOTTA.defaultBlockState();
                    default -> ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState();
                };

                boolean boundary = isBoundary(localX, localZ, LEVEL_0_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_0_ROOM_SIZE, 1, roomX, roomZ, localX, localZ, false, true);
                boolean transition = boundary && isTransitionWall(seed, roomX, roomZ, localX, localZ);
                boolean divider = !boundary && hasLevel0Divider(seed, roomX, roomZ, localX, localZ);
                boolean pillar = !boundary && style == 1 && isPillar(localX, localZ, LEVEL_0_ROOM_SIZE);

                // Voxel-correct footing: a full block at Y=65 must not float on
                // top of a 1/16-high carpet at Y=64. Build walls, dividers,
                // pillars and transition walls starting at Y=64; carpet ONLY
                // fills actual walkable space and the open doorways.
                BlockState footing = transition
                        ? ThresholdEnvironmentBlocks.TRANSITION_WALL.get().defaultBlockState()
                        : boundary && !doorway
                        ? wall
                        : divider
                        ? ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState()
                        : pillar
                        ? Blocks.YELLOW_CONCRETE.defaultBlockState()
                        : carpet;

                set(world, pos, x, FLOOR_Y, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                set(world, pos, x, LEVEL_0_CARPET_Y, z, footing);

                for (int y = LEVEL_0_WALL_BOTTOM; y <= LEVEL_0_WALL_TOP; y++) {
                    if (transition && y <= 68) {
                        set(world, pos, x, y, z,
                                ThresholdEnvironmentBlocks.TRANSITION_WALL.get().defaultBlockState());
                    } else if (boundary && !(y <= 69 && doorway)) {
                        set(world, pos, x, y, z, wall);
                    } else if (divider && y <= 68) {
                        set(world, pos, x, y, z,
                                ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState());
                    } else if (pillar) {
                        set(world, pos, x, y, z, Blocks.YELLOW_CONCRETE.defaultBlockState());
                    }
                }

                BlockState ceiling = style == 2
                        ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                        : Blocks.WHITE_CONCRETE.defaultBlockState();
                if (style != 2 && isLevel0LightPosition(localX, localZ)) {
                    ceiling = ThresholdEnvironmentBlocks.FLICKERING_PANEL.get().defaultBlockState();
                }
                set(world, pos, x, LEVEL_0_CEILING_Y, z, ceiling);

                // Exceptionally rare forgotten provisions. Level 0 remains
                // fundamentally isolated; this isn't a reliable food source.
                if ((style == 2 || style == 3) && (roomX != 0 || roomZ != 0)
                        && !ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, true)
                        && localX == 6 && localZ == 6
                        && Math.floorMod(mix(seed, roomX, roomZ, 0xCAFE0L), 11) == 0) {
                    // Replace the carpet under the barrel; no floating prop.
                    placeLootBarrel(world, pos, x, LEVEL_0_CARPET_Y, z,
                            FORGOTTEN_LOOT, mix(seed, x, z, 0xF00DL));
                }
            }
        }
        ThresholdIrregularMazeLayout.refineLevel0(world, chunkX, chunkZ, seed, pos);
        buildLevel0ArchitecturalDetails(world, chunkX, chunkZ, seed, pos);
        buildLevel0EnvironmentalDressing(world, chunkX, chunkZ, seed, pos);
        buildLevel0VerticalNetwork(world, chunkX, chunkZ, seed, pos);
        clearGuaranteedSpine(world, chunkX, chunkZ, seed, pos, true);
    }

    private static void buildLevel1Chunk(WorldGenLevel world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_1_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_1_ROOM_SIZE);

            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_1_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_1_ROOM_SIZE);
                int style = roomX == 1 && roomZ == 0 ? 3 : level1Style(seed, roomX, roomZ);

                BlockState floor = style == 3
                        ? Blocks.GRAY_CONCRETE.defaultBlockState()
                        : Blocks.SMOOTH_STONE.defaultBlockState();
                // Low-cost worn patches on distant warehouse floors.
                if ((roomX != 0 || roomZ != 0) && style != 3
                        && Math.floorMod(mix(seed, x, z, 0xF100L), 37) == 0) {
                    floor = Blocks.ANDESITE.defaultBlockState();
                }
                BlockState wall = style == 3
                        ? Blocks.GRAY_CONCRETE.defaultBlockState()
                        : ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState();

                set(world, pos, x, FLOOR_Y, z, floor);

                boolean boundary = isBoundary(localX, localZ, LEVEL_1_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_1_ROOM_SIZE, 2, roomX, roomZ, localX, localZ, true, true);
                boolean returnPassage = boundary
                        && isLevel1ReturnWall(seed, roomX, roomZ, localX, localZ);

                for (int y = LEVEL_1_WALL_BOTTOM; y <= LEVEL_1_WALL_TOP; y++) {
                    if (returnPassage && y <= 68) {
                        set(world, pos, x, y, z,
                                ThresholdEnvironmentBlocks.TRANSITION_WALL.get().defaultBlockState());
                    } else if (boundary && !(y <= 70 && doorway)) {
                        set(world, pos, x, y, z, wall);
                    } else if (!boundary && hasLevel1Divider(
                            seed, roomX, roomZ, localX, localZ, style) && y <= 71) {
                        set(world, pos, x, y, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                    } else if (!boundary && style == 0 && isLevel1Pillar(localX, localZ)) {
                        set(world, pos, x, y, z, Blocks.POLISHED_ANDESITE.defaultBlockState());
                    } else if (!boundary && y == 73 && style == 2
                            && (localX == 12 || localX == 36)
                            && (localZ == 8 || localZ == 24 || localZ == 40)) {
                        // Overhead maintenance fittings: overhead only, so
                        // corridors stay walkable and biome-safe.
                        set(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                    }
                }

                if (style == 1 && isLevel1CratePosition(seed, roomX, roomZ, localX, localZ)
                        && (ThresholdIrregularMazeLayout.isProtectedPassageOrEntry(x, z, false)
                        || !ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, false))) {
                    // Starting cache always includes a safe drink and varied
                    // Agriculture nutrition. Other caches keep uncertainty.
                    ResourceKey<LootTable> table = roomX == 0 && roomZ == 0
                            && localX == 8 && localZ == 8
                            ? STARTER_LOOT
                            : (localX == 40 || localZ == 40 ? MAINTENANCE_LOOT : SUPPLY_LOOT);
                    placeLootBarrel(world, pos, x, LEVEL_1_WALL_BOTTOM, z,
                            table, mix(seed, x, z, 0xCACE1L));
                }

                // A small minority of storage rooms contain irrigated,
                // harvestable Agriculture crops. The mod is a required
                // runtime dependency; this lookup avoids compiling it into our JAR.
                if (style == 1 && hasLevel1PlanterPatch(seed, roomX, roomZ)
                        && localX >= 14 && localX <= 18 && localZ >= 14 && localZ <= 18) {
                    if (localX == 16 && localZ == 16) {
                        set(world, pos, x, FLOOR_Y, z, Blocks.WATER.defaultBlockState());
                    } else {
                        set(world, pos, x, FLOOR_Y, z, Blocks.FARMLAND.defaultBlockState()
                                .setValue(net.minecraft.world.level.block.FarmBlock.MOISTURE, 7));
                        String[] crops = {
                                "barley_crop", "corn_crop", "soybean_crop",
                                "tomato_crop", "lettuce_crop", "spinach_crop"
                        };
                        int cropIndex = Math.floorMod(
                                localX + localZ - 28 + roomX * 3 + roomZ * 5, crops.length);
                        String id = crops[cropIndex];
                        net.minecraft.world.level.block.Block cropBlock = BuiltInRegistries.BLOCK.get(
                                ResourceLocation.fromNamespaceAndPath("gugugaga", id));
                        if (cropBlock instanceof CropBlock crop) {
                            // Start with real harvestable produce; Agriculture owns subsequent regrowth.
                            set(world, pos, x, FLOOR_Y + 1, z, crop.getStateForAge(7));
                        }
                    }
                    // A compact five-lamp array keeps every crop tile above the
                    // vanilla light threshold while preserving the warehouse's
                    // otherwise uneven, low-light atmosphere.
                    if ((localX == 14 || localX == 18) && (localZ == 14 || localZ == 18)
                            || (localX == 16 && localZ == 16)) {
                        set(world, pos, x, 67, z, Blocks.SEA_LANTERN.defaultBlockState());
                    }
                }

                BlockState ceiling = style == 3
                        ? Blocks.GRAY_CONCRETE.defaultBlockState()
                        : Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
                if (style != 3 && isLevel1LightPosition(localX, localZ)) {
                    boolean damagedFixture = localX == 24 && localZ == 24
                            && ((roomX == 0 && roomZ == 0) || style == 2);
                    ceiling = damagedFixture
                            ? ThresholdEnvironmentBlocks.FLICKERING_PANEL.get().defaultBlockState()
                            : Blocks.SEA_LANTERN.defaultBlockState();
                }
                set(world, pos, x, LEVEL_1_CEILING_Y, z, ceiling);

                // Keep the starter shelter's composter near the planter, but
                // move it deterministically when the original tile overlaps a route.
                boolean composterCell = roomX == 0 && roomZ == 0
                        ? isLevel1StarterComposterCell(seed, localX, localZ)
                        : localX == 20 && localZ == 16
                        && !isLevel1ComposterRouteProtected(seed, roomX, roomZ, localX, localZ);
                if (style == 1 && hasLevel1PlanterPatch(seed, roomX, roomZ) && composterCell) {
                    set(world, pos, x, FLOOR_Y, z, Blocks.COMPOSTER.defaultBlockState());
                }

                // Smooth threshold toward Level 2: warehouse fittings gradually
                // become utility pipes before the corridor seamlessly changes dimension.
                if (roomX == 1 && roomZ == 0) {
                    if (localX >= 6 && localX <= 42 && (localZ == 18 || localZ == 28)
                            && Math.floorMod(localX, 3) == 0) {
                        set(world, pos, x, 70, z, Blocks.COPPER_BLOCK.defaultBlockState());
                    }
                    if (localX >= 18 && localX <= 42 && localZ == 23
                            && Math.floorMod(localX, 6) == 0) {
                        set(world, pos, x, 69, z, Blocks.CHAIN.defaultBlockState());
                    }
                    if (localX >= 30 && localX <= 42 && (localZ == 17 || localZ == 29)
                            && Math.floorMod(localX, 4) == 2) {
                        set(world, pos, x, 65, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                    }
                    if (localX == 16 && localZ == 23) {
                        set(world, pos, x, 64, z,
                                ThresholdEnvironmentBlocks.LEVEL_1_TO_2.get().defaultBlockState());
                    }
                }
            }
        }
        ThresholdIrregularMazeLayout.refineLevel1(world, chunkX, chunkZ, seed, pos);
        buildLevel1ArchitecturalDetails(world, chunkX, chunkZ, seed, pos);
        buildLevel1EnvironmentalDressing(world, chunkX, chunkZ, seed, pos);
        buildLevel1VerticalNetwork(world, chunkX, chunkZ, seed, pos);
        clearGuaranteedSpine(world, chunkX, chunkZ, seed, pos, false);
    }




    /**
     * Level 2 — Abandoned Utility Halls. A dense network of service corridors,
     * pipe galleries, maintenance bays and occasional crawlspace-like passages.
     * The 32x32 room grid is deterministic, while route doors use a separate
     * seed-stable weighted destination draw.
     */
    private static void buildLevel2Chunk(WorldGenLevel world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_2_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_2_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_2_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_2_ROOM_SIZE);
                int style = level2Architecture(seed, roomX, roomZ);

                BlockState floor = switch (style) {
                    case 1 -> Blocks.POLISHED_ANDESITE.defaultBlockState();
                    case 2 -> Blocks.DEEPSLATE_TILES.defaultBlockState();
                    case 3 -> Blocks.GRAVEL.defaultBlockState();
                    case 4 -> Blocks.SMOOTH_BASALT.defaultBlockState();
                    case 5 -> Blocks.COBBLED_DEEPSLATE.defaultBlockState();
                    default -> Blocks.GRAY_CONCRETE.defaultBlockState();
                };
                BlockState wall = switch (style) {
                    case 2, 5 -> Blocks.DEEPSLATE_BRICKS.defaultBlockState();
                    case 3 -> Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
                    default -> Blocks.GRAY_CONCRETE.defaultBlockState();
                };
                set(world, pos, x, FLOOR_Y, z, floor);

                boolean boundary = isBoundary(localX, localZ, LEVEL_2_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_2_ROOM_SIZE, 2, roomX, roomZ, localX, localZ, true, false);
                boolean protectedRoute = isReservedRoomPath(
                        seed, LEVEL_2_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true);

                for (int y = LEVEL_2_WALL_BOTTOM; y <= LEVEL_2_WALL_TOP; y++) {
                    if (boundary && !doorway) {
                        set(world, pos, x, y, z, wall);
                    } else if (!boundary && y == 69
                            && (localX == 4 || localX == 27 || localZ == 4 || localZ == 27)) {
                        set(world, pos, x, y, z, Blocks.COPPER_BLOCK.defaultBlockState());
                    } else if (!boundary && y == 70
                            && (localX == 4 || localX == 27)
                            && Math.floorMod(localZ, 3) == 0) {
                        set(world, pos, x, y, z, Blocks.CHAIN.defaultBlockState());
                    }
                }
                set(world, pos, x, LEVEL_2_CEILING_Y, z,
                        style == 5 ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                                : Blocks.GRAY_CONCRETE.defaultBlockState());

                if (!protectedRoute) {
                    switch (style) {
                        case 1 -> {
                            // Paired pipe racks along the service-bay walls.
                            if ((localX == 7 || localX == 24) && localZ >= 7 && localZ <= 24) {
                                set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                                set(world, pos, x, 65, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            }
                        }
                        case 2 -> {
                            // Narrow cable gallery; overhead lines preserve a clear walking path.
                            if ((localZ == 9 || localZ == 22) && localX >= 8 && localX <= 23) {
                                set(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
                                if (Math.floorMod(localX, 4) == 0) {
                                    set(world, pos, x, 69, z, Blocks.REDSTONE_LAMP.defaultBlockState());
                                }
                            }
                        }
                        case 3 -> {
                            // Low maintenance trench, covered with flush trapdoors.
                            if ((localX == 10 || localX == 21) && localZ >= 8 && localZ <= 23) {
                                set(world, pos, x, 64, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            }
                        }
                        case 4 -> {
                            // Damp sump edge: a shallow water channel beside the route.
                            if (localX == 5 && localZ >= 8 && localZ <= 23) {
                                set(world, pos, x, FLOOR_Y, z, Blocks.WATER.defaultBlockState());
                            }
                            if (localX == 6 && localZ >= 8 && localZ <= 23) {
                                set(world, pos, x, 64, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        case 5 -> {
                            // Repeating valve columns and mechanical service panels.
                            if ((localX == 8 || localX == 23)
                                    && (localZ == 8 || localZ == 23)) {
                                set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                                set(world, pos, x, 65, z, Blocks.COPPER_BLOCK.defaultBlockState());
                                set(world, pos, x, 66, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            }
                        }
                        default -> { }
                    }
                }

                // The first cell preserves a readable entrance and a stable return.
                if (roomX == 0 && roomZ == 0 && localX == 0 && localZ == 16) {
                    set(world, pos, x, 64, z,
                            ThresholdEnvironmentBlocks.LEVEL_2_FIRE_EXIT.get().defaultBlockState());
                    set(world, pos, x, 65, z,
                            ThresholdEnvironmentBlocks.LEVEL_2_FIRE_EXIT.get().defaultBlockState());
                }
                if (roomX == 0 && roomZ == 0 && localX == LEVEL_2_ROOM_SIZE - 1 && localZ == 16) {
                    set(world, pos, x, 64, z,
                            ThresholdEnvironmentBlocks.LEVEL_2_TO_3.get().defaultBlockState());
                    set(world, pos, x, 65, z,
                            ThresholdEnvironmentBlocks.LEVEL_2_TO_3.get().defaultBlockState());
                }

                // Fandom/Wikidot both document anomalous doors and non-linear
                // exits here. Each branch is assigned once per world/position.
                boolean guaranteedBranch = roomX == 0 && roomZ == 0
                        && localX == 16 && localZ == LEVEL_2_ROOM_SIZE - 1;
                boolean rareBranch = (roomX != 0 || roomZ != 0)
                        && localX == 16 && localZ == LEVEL_2_ROOM_SIZE - 1
                        && Math.floorMod(mix(seed, roomX, roomZ, 0x2B4A77L), 5) == 0;
                if (guaranteedBranch || rareBranch) {
                    set(world, pos, x, 64, z,
                            ThresholdEnvironmentBlocks.LEVEL_2_BRANCH_DOOR.get().defaultBlockState());
                    set(world, pos, x, 65, z,
                            ThresholdEnvironmentBlocks.LEVEL_2_BRANCH_DOOR.get().defaultBlockState());
                }
            }
        }
    }

    private static int level2Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0;
        return Math.floorMod((int) mix(seed, roomX, roomZ, 0x2A20L), 6);
    }

    /** Level 3: a brick-lined electrical station with narrow, linked service rooms. */
    private static void buildLevel3Chunk(WorldGenLevel world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_3_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_3_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_3_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_3_ROOM_SIZE);
                int style = level3Architecture(seed, roomX, roomZ);
                BlockState floor = style == 1
                        ? Blocks.POLISHED_DEEPSLATE.defaultBlockState()
                        : Blocks.DEEPSLATE_TILES.defaultBlockState();
                BlockState wall = style == 2
                        ? Blocks.DEEPSLATE_BRICKS.defaultBlockState()
                        : Blocks.BRICKS.defaultBlockState();
                set(world, pos, x, FLOOR_Y, z, floor);

                boolean boundary = isBoundary(localX, localZ, LEVEL_3_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_3_ROOM_SIZE, 2, roomX, roomZ, localX, localZ, true, false);
                boolean protectedRoute = isReservedRoomPath(
                        seed, LEVEL_3_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true);

                for (int y = LEVEL_3_WALL_BOTTOM; y <= LEVEL_3_WALL_TOP; y++) {
                    if (boundary && !doorway) {
                        set(world, pos, x, y, z, wall);
                    } else if (!boundary && y == 69
                            && (localX == 4 || localX == 27 || localZ == 4 || localZ == 27)) {
                        set(world, pos, x, y, z, Blocks.COPPER_BLOCK.defaultBlockState());
                    } else if (!boundary && y == 70
                            && (localX == 4 || localX == 27) && Math.floorMod(localZ, 4) == 0) {
                        set(world, pos, x, y, z, Blocks.CHAIN.defaultBlockState());
                    }
                }
                set(world, pos, x, LEVEL_3_CEILING_Y, z,
                        Blocks.GRAY_CONCRETE.defaultBlockState());

                if (!protectedRoute) {
                    if (style == 1) {
                        boolean equipmentBay = localX >= 12 && localX <= 19
                                && localZ >= 12 && localZ <= 19;
                        if (equipmentBay && (localX == 12 || localX == 19
                                || localZ == 12 || localZ == 19)) {
                            set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.COPPER_BLOCK.defaultBlockState());
                        }
                    } else if (style == 2) {
                        if ((localX == 7 || localX == 8 || localX == 23 || localX == 24)
                                && localZ >= 7 && localZ <= 24) {
                            set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.REDSTONE_LAMP.defaultBlockState());
                        }
                    } else if (style == 3) {
                        if ((localX == 14 || localX == 17)
                                && localZ >= 8 && localZ <= 23) {
                            set(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.IRON_BARS.defaultBlockState());
                        }
                    } else if (style == 4) {
                        if ((localX == 10 || localX == 21) && localZ >= 8 && localZ <= 23) {
                            set(world, pos, x, 64, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                        if ((localZ == 10 || localZ == 21) && localX >= 8 && localX <= 23) {
                            set(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
                        }
                    } else if (style == 5) {
                        if ((localX == 8 || localX == 23) && (localZ == 8 || localZ == 23)) {
                            for (int y = 64; y <= 68; y++) {
                                set(world, pos, x, y, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            }
                        }
                    }
                }

                if (style == 1 && localX == 6 && localZ == 6
                        && Math.floorMod(mix(seed, roomX, roomZ, 0x3A77L), 3) == 0
                        && !protectedRoute) {
                    placeLootBarrel(world, pos, x, 64, z,
                            LEVEL_3_SUPPLY_LOOT, mix(seed, x, z, 0x3A78L));
                }

                if (roomX == 0 && roomZ == 0 && localX == 0 && localZ == 16) {
                    set(world, pos, x, 64, z, ThresholdEnvironmentBlocks.LEVEL_3_TO_2.get().defaultBlockState());
                    set(world, pos, x, 65, z, ThresholdEnvironmentBlocks.LEVEL_3_TO_2.get().defaultBlockState());
                }
                if (roomX == 0 && roomZ == 0 && localX == LEVEL_3_ROOM_SIZE - 1 && localZ == 16) {
                    set(world, pos, x, 64, z, ThresholdEnvironmentBlocks.LEVEL_3_TO_4.get().defaultBlockState());
                    set(world, pos, x, 65, z, ThresholdEnvironmentBlocks.LEVEL_3_TO_4.get().defaultBlockState());
                }
                // The service elevator usually serves Level 4; a rarer draw reaches Level 5.
                if (roomX == 0 && roomZ == 0 && localX == 16 && localZ == LEVEL_3_ROOM_SIZE - 1) {
                    set(world, pos, x, 64, z, ThresholdEnvironmentBlocks.LEVEL_3_ELEVATOR.get().defaultBlockState());
                    set(world, pos, x, 65, z, ThresholdEnvironmentBlocks.LEVEL_3_ELEVATOR.get().defaultBlockState());
                }
            }
        }
    }

    private static int level3Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0;
        return Math.floorMod((int) mix(seed, roomX, roomZ, 0x3A30L), 6);
    }


    /** Level 4: quiet, mostly empty offices with occasional service areas. */
    private static void buildLevel4Chunk(WorldGenLevel world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_4_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_4_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_4_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_4_ROOM_SIZE);
                int style = level4Architecture(seed, roomX, roomZ);
                BlockState floor = style == 4
                        ? Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState()
                        : Blocks.SMOOTH_STONE.defaultBlockState();
                BlockState wall = style == 3
                        ? Blocks.WHITE_CONCRETE.defaultBlockState()
                        : Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
                set(world, pos, x, FLOOR_Y, z, floor);

                boolean boundary = isBoundary(localX, localZ, LEVEL_4_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_4_ROOM_SIZE, 2, roomX, roomZ, localX, localZ, true, false);
                boolean protectedRoute = isReservedRoomPath(
                        seed, LEVEL_4_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true);

                for (int y = LEVEL_4_WALL_BOTTOM; y <= LEVEL_4_WALL_TOP; y++) {
                    if (boundary && !doorway) {
                        set(world, pos, x, y, z, wall);
                    }
                }
                set(world, pos, x, LEVEL_4_CEILING_Y, z,
                        Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState());

                if (!protectedRoute) {
                    if (style == 1) {
                        // Sparse desk and chair in an otherwise empty office.
                        if (localX >= 15 && localX <= 19 && localZ == 18) {
                            set(world, pos, x, 64, z, Blocks.OAK_SLAB.defaultBlockState());
                        }
                        if (localX == 14 && localZ == 18) {
                            set(world, pos, x, 64, z, Blocks.OAK_STAIRS.defaultBlockState());
                        }
                    } else if (style == 2) {
                        // Reception counter and a static glass-front cabinet.
                        if (localZ >= 12 && localZ <= 15 && localX >= 17 && localX <= 29) {
                            set(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                        }
                        if (localX == 32 && localZ >= 12 && localZ <= 16) {
                            set(world, pos, x, 64, z, Blocks.BLACK_CONCRETE.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                        }
                    } else if (style == 3) {
                        // Blacked-out window panels; no openings or unsafe drops.
                        if ((localX == 12 || localX == 35) && localZ >= 10 && localZ <= 22) {
                            set(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                            set(world, pos, x, 67, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                            if (Math.floorMod(localZ, 4) == 0) {
                                set(world, pos, x, 68, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                    } else if (style == 4) {
                        // A sealed decorative water cooler and nearby supply cabinet.
                        if (localX == 8 && localZ == 8) {
                            set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.BLUE_STAINED_GLASS.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                        if (localX == 10 && localZ == 8) {
                            placeLootBarrel(world, pos, x, 64, z,
                                    LEVEL_4_SUPPLY_LOOT, mix(seed, x, z, 0x4A77L));
                        }
                    } else if (style == 5) {
                        // Long meeting table and only a couple of chairs.
                        if (localZ == 24 && localX >= 15 && localX <= 31) {
                            set(world, pos, x, 64, z, Blocks.OAK_SLAB.defaultBlockState());
                        }
                        if ((localX == 14 || localX == 32) && localZ == 24) {
                            set(world, pos, x, 64, z, Blocks.OAK_STAIRS.defaultBlockState());
                        }
                    }
                }

                if (roomX == 0 && roomZ == 0 && localX == 10 && localZ == 8) {
                    placeLootBarrel(world, pos, x, 64, z,
                            LEVEL_4_SUPPLY_LOOT, mix(seed, x, z, 0x4A78L));
                }
                if ((localX == 8 || localX == 24 || localX == 40)
                        && (localZ == 8 || localZ == 24 || localZ == 40)) {
                    set(world, pos, x, 70, z, Blocks.SEA_LANTERN.defaultBlockState());
                }
                // An old-fashioned stairwell climbs to the hotel entrance.
                if (roomX == 0 && roomZ == 0 && localZ == 16 && localX >= 40 && localX <= 46) {
                    int stepHeight = (localX - 40) / 2;
                    for (int y = 64; y <= 64 + stepHeight; y++) {
                        set(world, pos, x, y, z, Blocks.SPRUCE_PLANKS.defaultBlockState());
                    }
                }
                if (roomX == 0 && roomZ == 0 && localX == LEVEL_4_ROOM_SIZE - 1 && localZ == 16) {
                    set(world, pos, x, 68, z, ThresholdEnvironmentBlocks.LEVEL_4_TO_5.get().defaultBlockState());
                    set(world, pos, x, 69, z, ThresholdEnvironmentBlocks.LEVEL_4_TO_5.get().defaultBlockState());
                }
                if (roomX == 0 && roomZ == 0 && localX == 0 && localZ == 16) {
                    set(world, pos, x, 64, z, ThresholdEnvironmentBlocks.LEVEL_4_TO_3.get().defaultBlockState());
                    set(world, pos, x, 65, z, ThresholdEnvironmentBlocks.LEVEL_4_TO_3.get().defaultBlockState());
                }
            }
        }
    }

    private static int level4Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0;
        return 1 + Math.floorMod((int) mix(seed, roomX, roomZ, 0x4A40L), 5);
    }


    /**
     * Level 5 — Terror Hotel. The three accessible zones are deliberately
     * distinct: a furnished Main Hall, the Beverly ballroom and a dense Boiler
     * Room. Rare guest wings and the unconfirmed Lobby Room are seed-stable
     * variations. No casino or wagering mechanics are implemented.
     */
    private static void buildLevel5Chunk(WorldGenLevel world, int chunkX, int chunkZ) {
        long seed = world.getSeed();
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_5_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_5_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_5_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_5_ROOM_SIZE);
                int style = level5Architecture(seed, roomX, roomZ);

                BlockState floor = switch (style) {
                    case 1 -> Blocks.CALCITE.defaultBlockState();
                    case 2 -> Blocks.POLISHED_BLACKSTONE.defaultBlockState();
                    case 3 -> Blocks.SPRUCE_PLANKS.defaultBlockState();
                    case 4 -> Blocks.POLISHED_DEEPSLATE.defaultBlockState();
                    case 5 -> Blocks.WHITE_CONCRETE.defaultBlockState();
                    default -> Blocks.DARK_OAK_PLANKS.defaultBlockState();
                };
                BlockState wall = switch (style) {
                    case 1 -> Blocks.RED_NETHER_BRICKS.defaultBlockState();
                    case 2 -> Blocks.DEEPSLATE_BRICKS.defaultBlockState();
                    case 3 -> Blocks.DARK_OAK_PLANKS.defaultBlockState();
                    case 4 -> Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
                    case 5 -> Blocks.GRAY_CONCRETE.defaultBlockState();
                    default -> Blocks.RED_TERRACOTTA.defaultBlockState();
                };
                set(world, pos, x, FLOOR_Y, z, floor);

                boolean boundary = isBoundary(localX, localZ, LEVEL_5_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_5_ROOM_SIZE, 2, roomX, roomZ, localX, localZ, true, false);
                boolean protectedRoute = isReservedRoomPath(
                        seed, LEVEL_5_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true);

                for (int y = LEVEL_5_WALL_BOTTOM; y <= LEVEL_5_WALL_TOP; y++) {
                    if (boundary && !doorway) {
                        set(world, pos, x, y, z, wall);
                    } else if (style == 0 && y == 69
                            && (localX == 2 || localX == 29)
                            && Math.floorMod(localZ, 4) == 0) {
                        // Gold detailing frames the otherwise quiet red Main Hall.
                        set(world, pos, x, y, z, Blocks.GOLD_BLOCK.defaultBlockState());
                    } else if (style == 2 && y == 69
                            && (localX == 4 || localX == 27 || localZ == 4 || localZ == 27)) {
                        // Exposed copper mains in the Boiler Room.
                        set(world, pos, x, y, z, Blocks.COPPER_BLOCK.defaultBlockState());
                    } else if (style == 2 && y == 70
                            && (localX == 4 || localX == 27) && Math.floorMod(localZ, 4) == 0) {
                        set(world, pos, x, y, z, Blocks.CHAIN.defaultBlockState());
                    }
                }

                BlockState ceiling = style == 2
                        ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                        : style == 1 ? Blocks.RED_NETHER_BRICKS.defaultBlockState()
                        : Blocks.BROWN_CONCRETE.defaultBlockState();
                if ((localX == 8 || localX == 24) && (localZ == 8 || localZ == 24)) {
                    ceiling = Blocks.SEA_LANTERN.defaultBlockState();
                }
                set(world, pos, x, LEVEL_5_CEILING_Y, z, ceiling);

                if (!protectedRoute) {
                    switch (style) {
                        case 0 -> {
                            // Red-and-gold carpet runners and restrained antique furniture.
                            if (localZ >= 14 && localZ <= 17) {
                                set(world, pos, x, 64, z, Blocks.RED_CARPET.defaultBlockState());
                            } else if ((localZ == 12 || localZ == 19)
                                    && localX >= 8 && localX <= 23) {
                                set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                            }
                            if ((localX == 7 || localX == 24) && localZ >= 8 && localZ <= 11) {
                                set(world, pos, x, 64, z, Blocks.SPRUCE_STAIRS.defaultBlockState());
                            }
                        }
                        case 1 -> {
                            // Beverly Room: a broad ballroom with a central table and chandelier.
                            if (localX >= 14 && localX <= 17
                                    && localZ >= 14 && localZ <= 17) {
                                set(world, pos, x, 64, z, Blocks.OAK_SLAB.defaultBlockState());
                            }
                            if ((localX == 12 || localX == 19) && localZ == 16) {
                                set(world, pos, x, 64, z, Blocks.OAK_STAIRS.defaultBlockState());
                            }
                            if (localX == 16 && localZ == 16) {
                                set(world, pos, x, 67, z, Blocks.CHAIN.defaultBlockState());
                                set(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
                                set(world, pos, x, 69, z, Blocks.SEA_LANTERN.defaultBlockState());
                            }
                            if ((localX == 8 || localX == 23) && localZ >= 8 && localZ <= 23
                                    && Math.floorMod(localZ, 5) == 0) {
                                set(world, pos, x, 65, z, Blocks.GOLD_BLOCK.defaultBlockState());
                            }
                        }
                        case 2 -> {
                            // Boiler Room: cramped machinery, pipework and shallow leaking channels.
                            if ((localX == 7 || localX == 8 || localX == 23 || localX == 24)
                                    && localZ >= 7 && localZ <= 24) {
                                set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                                set(world, pos, x, 65, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            }
                            if (localX == 6 && localZ >= 9 && localZ <= 22) {
                                set(world, pos, x, FLOOR_Y, z, Blocks.WATER.defaultBlockState());
                            }
                            if (localX == 10 && localZ == 8) {
                                set(world, pos, x, 64, z, Blocks.BLAST_FURNACE.defaultBlockState());
                            }
                            if (localX == 21 && localZ == 8) {
                                set(world, pos, x, 64, z, Blocks.CAULDRON.defaultBlockState());
                            }
                            if ((localX == 10 || localX == 21)
                                    && localZ >= 12 && localZ <= 22) {
                                set(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        case 3 -> {
                            // Guest suites: quiet rooms with bookshelves and simple furniture.
                            if (localX >= 8 && localX <= 13 && localZ == 8) {
                                set(world, pos, x, 64, z, Blocks.BOOKSHELF.defaultBlockState());
                            }
                            if (localZ == 20 && localX >= 14 && localX <= 18) {
                                set(world, pos, x, 64, z, Blocks.SPRUCE_PLANKS.defaultBlockState());
                            }
                            if ((localX == 13 || localX == 19) && localZ == 20) {
                                set(world, pos, x, 64, z, Blocks.SPRUCE_STAIRS.defaultBlockState());
                            }
                        }
                        case 4 -> {
                            // Impossible corridor variant: repeated dark panels and compressed sightlines.
                            if ((localX == 10 || localX == 21)
                                    && localZ >= 6 && localZ <= 25) {
                                set(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                                set(world, pos, x, 66, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                                set(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        case 5 -> {
                            // Rare, unconfirmed Lobby Room: an empty reception desk and isolated light.
                            if (localZ == 22 && localX >= 10 && localX <= 21) {
                                set(world, pos, x, 64, z, Blocks.QUARTZ_SLAB.defaultBlockState());
                            }
                            if (localX == 16 && localZ == 22) {
                                set(world, pos, x, 65, z, Blocks.GOLD_BLOCK.defaultBlockState());
                            }
                            if (localX == 16 && localZ == 10) {
                                set(world, pos, x, 69, z, Blocks.SEA_LANTERN.defaultBlockState());
                            }
                        }
                        default -> { }
                    }
                }

                if (roomX == 0 && roomZ == 0 && localX == 28 && localZ == 8) {
                    // Fixed location keeps the first hotel's elevator panel guaranteed and reachable.
                    set(world, pos, x, 65, z,
                            ThresholdEnvironmentBlocks.LEVEL_5_ELEVATOR_PANEL.get().defaultBlockState());
                }

                // Supply caches are scarce and use Agriculture's real hydration and food items.
                if ((style == 0 || style == 2) && localX == 8 && localZ == 8
                        && !protectedRoute
                        && ((roomX == 0 && roomZ == 0)
                        || Math.floorMod(mix(seed, roomX, roomZ, 0x5A77L), 7) == 0)) {
                    placeLootBarrel(world, pos, x, 64, z,
                            LEVEL_5_SUPPLY_LOOT, mix(seed, x, z, 0x5A78L));
                }

                // The two source-backed exits sit on opposite sides of the entry Main Hall.
                if (roomX == 0 && roomZ == 0 && localX == 0 && localZ == 16) {
                    set(world, pos, x, 64, z,
                            ThresholdEnvironmentBlocks.LEVEL_5_TO_3.get().defaultBlockState());
                    set(world, pos, x, 65, z,
                            ThresholdEnvironmentBlocks.LEVEL_5_TO_3.get().defaultBlockState());
                }
                if (roomX == 0 && roomZ == 0 && localX == LEVEL_5_ROOM_SIZE - 1 && localZ == 16) {
                    set(world, pos, x, 64, z,
                            ThresholdEnvironmentBlocks.LEVEL_5_TO_4.get().defaultBlockState());
                    set(world, pos, x, 65, z,
                            ThresholdEnvironmentBlocks.LEVEL_5_TO_4.get().defaultBlockState());
                }
            }
        }
    }

    private static int level5Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0; // Main Hall / entry wing
        if (roomX == 1 && roomZ == 0) return 1; // Beverly Room
        if (roomX == 0 && roomZ == 1) return 2; // Boiler Room
        int roll = Math.floorMod((int) mix(seed, roomX, roomZ, 0x5A50L), 100);
        if (roll < 45) return 0;
        if (roll < 67) return 2;
        if (roll < 82) return 3;
        if (roll < 97) return 4;
        return 5; // rare Lobby Room anomaly
    }

    /*
     * Architectural overlays add authored room archetypes without changing
     * the established 32x32 / 48x48 room grid, entry rooms, exits or floor Y.
     * Every overlay is evaluated from world seed + room coordinates, so the
     * same chunk is stable across reloads and safe to generate out of order.
     */

    private static void buildLevel0ArchitecturalDetails(
            WorldGenLevel world, int chunkX, int chunkZ, long seed, BlockPos.MutableBlockPos pos) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_0_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_0_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_0_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_0_ROOM_SIZE);
                if (roomX == 0 && roomZ == 0) continue; // preserve the iconic entry room

                int archetype = level0Architecture(seed, roomX, roomZ);
                if (archetype == 0) continue;
                // Common office furniture and dividers must not undo the
                // connected irregular maze carved in the base geometry.
                // Landmark rooms (12+) retain their authored stairs and pits.
                if (archetype <= 11 && ThresholdIrregularMazeLayout
                        .isGuaranteedCorridor(seed, x, z, true)) continue;
                boolean protectedRoute = ThresholdIrregularMazeLayout
                        .isGuaranteedCorridor(seed, x, z, true)
                        || isReservedRoomPath(
                        seed, LEVEL_0_ROOM_SIZE, roomX, roomZ, localX, localZ, 1, false);
                boolean upperWalkway = hasLevel0UpperDeck(seed, roomX, roomZ)
                        && isLevel0UpperPath(seed, roomX, roomZ, localX, localZ);

                switch (archetype) {
                    case 1 -> {
                        // Open-plan office cubicles: two-height glass partitions,
                        // with deliberate gaps and a preserved route to every exit.
                        if (!protectedRoute && isLevel0CubiclePanel(localX, localZ, false)) {
                            set(world, pos, x, 65, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                        }
                        if (!protectedRoute && isLevel0Desk(localX, localZ)) {
                            set(world, pos, x, 65, z, Blocks.OAK_SLAB.defaultBlockState());
                        }
                    }
                    case 2 -> {
                        // Offset partition offices: short stained walls create
                        // multiple sightline breaks without sealing the room.
                        if (!protectedRoute && isLevel0Partition(localX, localZ)) {
                            for (int y = 65; y <= 67; y++) {
                                set(world, pos, x, y, z,
                                        ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState());
                            }
                        }
                    }
                    case 3 -> {
                        // Repeated structural columns and cross-beams evoke a
                        // larger office block hidden inside the same room cell.
                        if (!protectedRoute && isLevel0Column(localX, localZ)) {
                            for (int y = 65; y <= 70; y++) {
                                set(world, pos, x, y, z, Blocks.YELLOW_CONCRETE.defaultBlockState());
                            }
                        }
                        if ((localX == 8 || localX == 24) && localZ >= 8 && localZ <= 24
                                && !isLevel0LightPosition(localX, localZ)) {
                            set(world, pos, x, 70, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                        }
                    }
                    case 4 -> {
                        // Suspended service ducts and recessed ceiling strips.
                        if ((localX == 10 || localX == 21) && localZ >= 5 && localZ <= 26
                                || (localZ == 10 || localZ == 21) && localX >= 5 && localX <= 26) {
                            set(world, pos, x, 69, z, Blocks.IRON_BLOCK.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 4) == 0) {
                                set(world, pos, x, 70, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            }
                        }
                    }
                    case 5 -> {
                        // Echo office: the cubicle grammar is mirrored
                        // and recoloured, but its exit topology remains original.
                        if (!protectedRoute && isLevel0CubiclePanel(localX, localZ, true)) {
                            set(world, pos, x, 65, z, Blocks.GRAY_STAINED_GLASS_PANE.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.GRAY_STAINED_GLASS_PANE.defaultBlockState());
                        }
                        if ((localX == 7 || localX == 25) && localZ >= 7 && localZ <= 25
                                && Math.floorMod(localZ, 3) == 0) {
                            set(world, pos, x, 70, z, Blocks.DEEPSLATE_TILES.defaultBlockState());
                        }
                    }
                    case 6 -> {
                        // Abandoned meeting-room fragments: scattered desks,
                        // overturned chairs and paper clutter, never across routes.
                        boolean desk = (localX == 9 || localX == 10 || localX == 22 || localX == 23)
                                && (localZ == 12 || localZ == 20);
                        if (!protectedRoute && desk) {
                            set(world, pos, x, 65, z, Blocks.OAK_SLAB.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 2) == 0) {
                                set(world, pos, x, 64, z, Blocks.OAK_FENCE.defaultBlockState());
                            }
                        }
                        boolean chair = (localX == 7 || localX == 12 || localX == 20 || localX == 25)
                                && (localZ == 10 || localZ == 22);
                        if (!protectedRoute && chair) {
                            set(world, pos, x, 65, z, Blocks.OAK_STAIRS.defaultBlockState());
                        }
                        if (!protectedRoute && localX == 16 && localZ == 16) {
                            set(world, pos, x, 65, z, Blocks.COBWEB.defaultBlockState());
                        }
                        if ((localX == 8 || localX == 24) && (localZ == 8 || localZ == 24)) {
                            set(world, pos, x, 70, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                    }
                    case 7 -> {
                        // Rare blackout annex: dark-glass partitions and unsettling ceiling details.
                        boolean darkDivider = (localX == 11 || localX == 12 || localX == 23 || localX == 24)
                                && localZ >= 7 && localZ <= 25;
                        if (!protectedRoute && darkDivider) {
                            set(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS_PANE.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.BLACK_STAINED_GLASS_PANE.defaultBlockState());
                            set(world, pos, x, 67, z, Blocks.BLACK_STAINED_GLASS_PANE.defaultBlockState());
                        }
                        if (!protectedRoute && localX == 16 && localZ == 16) {
                            set(world, pos, x, 64, z, Blocks.RED_CARPET.defaultBlockState());
                        }
                        if ((localX == 8 || localX == 24) && (localZ == 8 || localZ == 24)) {
                            set(world, pos, x, 70, z, Blocks.BLACK_CONCRETE.defaultBlockState());
                        }
                        if (localX == 16 && (localZ == 8 || localZ == 24)) {
                            set(world, pos, x, 69, z, Blocks.CHAIN.defaultBlockState());
                        }
                    }
                    case 8 -> {
                        // Pale arch gallery: two repeated openings create a calmer,
                        // more stable-looking landmark inside the otherwise uncertain maze.
                        boolean archLine = localZ == 8 || localZ == 24;
                        boolean archPier = archLine && (localX == 8 || localX == 16 || localX == 24);
                        boolean archSpan = archLine && localX >= 8 && localX <= 24
                                && Math.floorMod(localX, 8) != 0;
                        if (!protectedRoute && archPier) {
                            for (int y = 65; y <= 69; y++) {
                                set(world, pos, x, y, z, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
                            }
                        }
                        if (!protectedRoute && archSpan) {
                            set(world, pos, x, 69, z, Blocks.CUT_SANDSTONE.defaultBlockState());
                        }
                        if (!protectedRoute && (localX == 8 || localX == 24)
                                && (localZ == 12 || localZ == 20)) {
                            set(world, pos, x, 65, z, Blocks.SANDSTONE.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.SANDSTONE.defaultBlockState());
                        }
                    }
                    case 9 -> {
                        // False-ceiling anomaly: suspended dark panels break the ceiling rhythm
                        // while remaining well above the player's normal head clearance.
                        boolean ceilingRail = (localX == 8 || localX == 16 || localX == 24)
                                && localZ >= 7 && localZ <= 25;
                        if (!protectedRoute && ceilingRail) {
                            set(world, pos, x, 70, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                            if (Math.floorMod(localZ + localX, 5) == 0) {
                                set(world, pos, x, 71, z, Blocks.BLACK_CONCRETE.defaultBlockState());
                            }
                        }
                        if (!protectedRoute && (localX == 11 || localX == 21)
                                && (localZ == 11 || localZ == 21)) {
                            set(world, pos, x, 69, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                        if (localX == 16 && localZ == 16) {
                            set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                        }
                    }
                    case 10 -> {
                        // Crimson bleed: a rare red-room fragment, not a sealed trap.
                        // Its red carpet and wall panels stop at the protected route mask.
                        boolean crimsonWall = (localX == 9 || localX == 22)
                                && localZ >= 8 && localZ <= 23;
                        if (!protectedRoute && crimsonWall) {
                            for (int y = 65; y <= 67; y++) {
                                set(world, pos, x, y, z, Blocks.RED_CONCRETE.defaultBlockState());
                            }
                            if (Math.floorMod(localZ, 4) == 0) {
                                set(world, pos, x, 68, z, Blocks.RED_STAINED_GLASS_PANE.defaultBlockState());
                            }
                        }
                        boolean crimsonCarpet = localX >= 12 && localX <= 19
                                && localZ >= 11 && localZ <= 20
                                && Math.floorMod(localX + localZ, 3) != 0;
                        if (!protectedRoute && crimsonCarpet) {
                            set(world, pos, x, 64, z, Blocks.RED_CARPET.defaultBlockState());
                        }
                        if (!protectedRoute && (localX == 10 || localX == 21)
                                && (localZ == 10 || localZ == 21)) {
                            set(world, pos, x, 69, z, Blocks.BLACK_CONCRETE.defaultBlockState());
                        }
                    }
                    case 11 -> {
                        // A true micro-maze: the room is subdivided into smaller
                        // office cells, with the reserved route and a second
                        // deterministic opening preserving loops and dead ends.
                        boolean verticalWall = (localX == 8 || localX == 24)
                                && localZ >= 4 && localZ <= 27;
                        boolean horizontalWall = (localZ == 8 || localZ == 24)
                                && localX >= 4 && localX <= 27;
                        int secondaryVerticalGap = 5 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA11A0L), 22);
                        int secondaryHorizontalGap = 5 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA11A1L), 22);
                        boolean secondaryGap = verticalWall
                                && Math.abs(localZ - secondaryVerticalGap) <= 1
                                || horizontalWall
                                && Math.abs(localX - secondaryHorizontalGap) <= 1;
                        if (!protectedRoute && !secondaryGap && (verticalWall || horizontalWall)) {
                            for (int y = 65; y <= 68; y++) {
                                set(world, pos, x, y, z,
                                        ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState());
                            }
                            if (Math.floorMod(localX + localZ, 7) == 0) {
                                set(world, pos, x, 70, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            }
                        }
                        // A few wall-mounted dead-end details make the small
                        // cells read as abandoned offices rather than empty boxes.
                        if (!protectedRoute && (localX == 6 || localX == 26)
                                && (localZ == 6 || localZ == 26)) {
                            set(world, pos, x, 65, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.STONE_BUTTON.defaultBlockState());
                        }
                    }
                    case 12 -> {
                        // A rare sub-floor yellow annex. The open shaft and
                        // six-step stair make the lower room genuinely reachable.
                        boolean annex = localX >= 20 && localX <= 27
                                && localZ >= 5 && localZ <= 12;
                        if (annex) {
                            boolean inner = localX >= 21 && localX <= 26
                                    && localZ >= 6 && localZ <= 11;
                            boolean wall = localX == 20 || localX == 27
                                    || localZ == 5 || localZ == 12;
                            boolean stairOpening = localZ == 12
                                    && (localX == 23 || localX == 24);
                            set(world, pos, x, 58, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                            for (int y = 60; y <= 71; y++) {
                                set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
                            }
                            if (wall && !stairOpening) {
                                for (int y = 59; y <= 63; y++) {
                                    set(world, pos, x, y, z,
                                            ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState());
                                }
                            } else {
                                for (int y = 59; y <= 63; y++) {
                                    set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
                                }
                                if (inner || stairOpening) {
                                    set(world, pos, x, 59, z, Blocks.YELLOW_CARPET.defaultBlockState());
                                }
                            }
                            if (localX == 24 && localZ == 8) {
                                set(world, pos, x, 62, z,
                                        ThresholdEnvironmentBlocks.FLICKERING_PANEL.get().defaultBlockState());
                            }
                            if (localX == 23 && localZ == 8) {
                                set(world, pos, x, 61, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        if ((localX == 23 || localX == 24) && localZ >= 11 && localZ <= 16) {
                            int step = 16 - localZ;
                            int stairY = 63 - step;
                            set(world, pos, x, stairY, z,
                                    Blocks.OAK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                    }
                    case 13 -> {
                        // Records archive: dense shelf banks form narrow, readable aisles.
                        int westAisleGap = 8 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA13A0L), 17);
                        int eastAisleGap = 8 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA13A1L), 17);
                        boolean shelfBank = (localX == 8 || localX == 9
                                || localX == 22 || localX == 23)
                                && localZ >= 6 && localZ <= 25;
                        boolean shelfGap = (localX == 8 || localX == 9)
                                && Math.abs(localZ - westAisleGap) <= 1
                                || (localX == 22 || localX == 23)
                                && Math.abs(localZ - eastAisleGap) <= 1;
                        if (!protectedRoute && shelfBank && !shelfGap) {
                            set(world, pos, x, 65, z, Blocks.BOOKSHELF.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.BOOKSHELF.defaultBlockState());
                            if (Math.floorMod(localZ, 6) == 0) {
                                set(world, pos, x, 67, z, Blocks.BARREL.defaultBlockState());
                            }
                        }
                        if (!protectedRoute && (localX == 6 || localX == 25)
                                && (localZ == 7 || localZ == 24)) {
                            set(world, pos, x, 65, z, Blocks.LECTERN.defaultBlockState());
                        }
                        if (!upperWalkway && (localZ == 7 || localZ == 24)
                                && localX >= 7 && localX <= 24) {
                            set(world, pos, x, 70, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                        }
                    }
                    case 14 -> {
                        // Offset false-corridor walls create a second route illusion,
                        // with deterministic door-sized breaks rather than sealed cells.
                        boolean westWall = (localX == 10 || localX == 11)
                                && localZ >= 7 && localZ <= 24;
                        boolean northWall = (localZ == 10 || localZ == 11)
                                && localX >= 11 && localX <= 24;
                        boolean eastWall = (localX == 21 || localX == 22)
                                && localZ >= 8 && localZ <= 25;
                        boolean southWall = (localZ == 21 || localZ == 22)
                                && localX >= 8 && localX <= 21;
                        int westGap = 8 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA14A0L), 15);
                        int northGap = 12 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA14A1L), 9);
                        boolean gap = (westWall && Math.abs(localZ - westGap) <= 1)
                                || (northWall && Math.abs(localX - northGap) <= 1)
                                || (eastWall && localZ >= 15 && localZ <= 17)
                                || (southWall && localX >= 14 && localX <= 16);
                        if (!protectedRoute && !gap
                                && (westWall || northWall || eastWall || southWall)) {
                            for (int y = 65; y <= 68; y++) {
                                set(world, pos, x, y, z,
                                        ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState());
                            }
                            if (Math.floorMod(localX + localZ, 5) == 0) {
                                set(world, pos, x, 69, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            }
                        }
                        if (!protectedRoute && (localX == 9 || localX == 23)
                                && (localZ == 9 || localZ == 23)) {
                            set(world, pos, x, 65, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                    }
                    case 15 -> {
                        // Broken ceiling grid: missing panels and dangling service runs
                        // make the room feel larger without obstructing the upper gallery.
                        boolean ceilingBeam = (localX == 8 || localX == 16 || localX == 24)
                                && localZ >= 5 && localZ <= 26
                                || (localZ == 8 || localZ == 24)
                                && localX >= 5 && localX <= 26;
                        int missingPanel = Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA15A0L), 5);
                        if (!upperWalkway && ceilingBeam
                                && Math.floorMod(localX + localZ, 7) != missingPanel) {
                            set(world, pos, x, 70, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 4) == 0) {
                                set(world, pos, x, 71, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            }
                        }
                        if (!upperWalkway && !protectedRoute
                                && (localX == 8 || localX == 24)
                                && (localZ == 8 || localZ == 24)) {
                            set(world, pos, x, 69, z, Blocks.CHAIN.defaultBlockState());
                            set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                        }
                    }
                    case 16 -> {
                        // A small raised office mezzanine creates a second walkable
                        // plane without replacing the Level 0 upper-gallery network.
                        boolean platform = localX >= 7 && localX <= 14
                                && localZ >= 21 && localZ <= 28;
                        boolean stair = (localX == 9 || localX == 10)
                                && localZ >= 18 && localZ <= 20;
                        boolean sideStair = localX >= 4 && localX <= 6
                                && (localZ == 24 || localZ == 25);
                        if (!upperWalkway && platform) {
                            set(world, pos, x, 66, z, Blocks.OAK_PLANKS.defaultBlockState());
                            if (!protectedRoute && Math.floorMod(localX + localZ, 4) == 0) {
                                set(world, pos, x, 67, z, Blocks.YELLOW_CARPET.defaultBlockState());
                            }
                            boolean edge = localX == 7 || localX == 14
                                    || localZ == 21 || localZ == 28;
                            boolean stairOpening = localZ == 21
                                    && (localX == 9 || localX == 10)
                                    || localX == 7 && (localZ == 24 || localZ == 25);
                            if (edge && !stairOpening) {
                                set(world, pos, x, 67, z,
                                        Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            }
                        }
                        if (!upperWalkway && stair) {
                            int stairY = 64 + localZ - 18;
                            set(world, pos, x, stairY, z,
                                    Blocks.OAK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.SOUTH));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                        if (!upperWalkway && !protectedRoute && sideStair) {
                            int stairY = 64 + localX - 4;
                            set(world, pos, x, stairY, z,
                                    Blocks.OAK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.EAST));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                        if (!upperWalkway && !protectedRoute && platform
                                && (localX == 7 || localX == 14)
                                && (localZ == 21 || localZ == 28)) {
                            for (int y = 64; y <= 65; y++) {
                                set(world, pos, x, y, z, Blocks.OAK_LOG.defaultBlockState());
                            }
                        }
                        if (!upperWalkway && localX == 11 && localZ == 24) {
                            set(world, pos, x, 69, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    case 17 -> {
                        // Two nested rings of offset partitions create loops, side
                        // rooms and misleading doorways rather than one dead-end grid.
                        boolean outerVertical = (localX == 7 || localX == 24)
                                && localZ >= 7 && localZ <= 24;
                        boolean outerHorizontal = (localZ == 7 || localZ == 24)
                                && localX >= 7 && localX <= 24;
                        boolean innerVertical = (localX == 12 || localX == 19)
                                && localZ >= 12 && localZ <= 19;
                        boolean innerHorizontal = (localZ == 12 || localZ == 19)
                                && localX >= 12 && localX <= 19;
                        int outerGapA = 9 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA17A0L), 14);
                        int outerGapB = 9 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA17A1L), 14);
                        int innerGapA = 13 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA17A2L), 6);
                        int innerGapB = 13 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA17A3L), 6);
                        boolean outerGap = outerVertical
                                && Math.abs(localZ - outerGapA) <= 1
                                || outerHorizontal && Math.abs(localX - outerGapB) <= 1;
                        boolean innerGap = innerVertical
                                && Math.abs(localZ - innerGapA) <= 1
                                || innerHorizontal && Math.abs(localX - innerGapB) <= 1;
                        if (!upperWalkway && !protectedRoute && !outerGap
                                && (outerVertical || outerHorizontal)) {
                            for (int y = 65; y <= 68; y++) {
                                set(world, pos, x, y, z,
                                        ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState());
                            }
                            if (Math.floorMod(localX + localZ, 6) == 0) {
                                set(world, pos, x, 69, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            }
                        }
                        if (!upperWalkway && !protectedRoute && !innerGap
                                && (innerVertical || innerHorizontal)) {
                            for (int y = 65; y <= 67; y++) {
                                set(world, pos, x, y, z,
                                        ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState());
                            }
                        }
                        if (!protectedRoute && (localX == 7 || localX == 24)
                                && (localZ == 7 || localZ == 24)) {
                            set(world, pos, x, 65, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                    }
                    case 18 -> {
                        // A sunken conference pit is cut below the office floor and
                        // reached by a four-step stair, with a guarded rim and a table.
                        // This authored landmark owns its pit and stairs: the
                        // legacy room route mask cannot silently erase either.
                        boolean pit = localX >= 22 && localX <= 28
                                && localZ >= 21 && localZ <= 28;
                        boolean inner = localX >= 23 && localX <= 27
                                && localZ >= 22 && localZ <= 27;
                        boolean wall = pit && (localX == 22 || localX == 28
                                || localZ == 21 || localZ == 28);
                        boolean stairOpening = (localX == 22 || localX == 28)
                                && (localZ == 23 || localZ == 24);
                        if (pit && !upperWalkway) {
                            set(world, pos, x, 60, z, Blocks.SMOOTH_STONE.defaultBlockState());
                            for (int y = 61; y <= 64; y++) {
                                set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
                            }
                            if (wall && !stairOpening) {
                                for (int y = 61; y <= 63; y++) {
                                    set(world, pos, x, y, z,
                                            ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState());
                                }
                                set(world, pos, x, 64, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            } else if (inner || stairOpening) {
                                set(world, pos, x, 61, z, Blocks.YELLOW_CARPET.defaultBlockState());
                            }
                        }
                        if (!upperWalkway
                                && (localZ == 23 || localZ == 24)
                                && localX >= 18 && localX <= 21) {
                            int stairY = 63 - (localX - 18);
                            set(world, pos, x, stairY, z,
                                    Blocks.OAK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.WEST));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                        if (!upperWalkway
                                && (localZ == 23 || localZ == 24)
                                && localX >= 29 && localX <= 31) {
                            int stairY = 60 + localX - 28;
                            set(world, pos, x, stairY, z,
                                    Blocks.OAK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.EAST));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                        if (inner && !upperWalkway
                                && localX == 25 && localZ == 24) {
                            set(world, pos, x, 61, z, Blocks.OAK_SLAB.defaultBlockState());
                            set(world, pos, x, 63, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                        if (inner && !upperWalkway
                                && (localX == 24 || localX == 26) && localZ == 24) {
                            set(world, pos, x, 61, z,
                                    Blocks.OAK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.NORTH));
                        }
                    }
                    case 19 -> {
                        // Pillar forest: an irregular column grid changes sightlines and
                        // perceived scale while leaving the seed-stable route network open.
                        boolean pillarAnchor = (localX == 6 || localX == 14 || localX == 22 || localX == 26)
                                && (localZ == 6 || localZ == 14 || localZ == 22 || localZ == 26);
                        boolean guaranteedPillar = pillarAnchor
                                && isGuaranteedLevel0PillarCell(seed, roomX, roomZ, localX, localZ);
                        boolean pillar = pillarAnchor && (guaranteedPillar
                                || Math.floorMod((int) mix(seed, roomX, roomZ, 0xA19A0L), 5) != 0);
                        if (!protectedRoute && !upperWalkway && pillar) {
                            int height = 66 + Math.floorMod(
                                    (int) mix(seed, roomX * 32 + localX, roomZ * 32 + localZ, 0xA19A1L), 6);
                            BlockState column = Math.floorMod(localX + localZ, 3) == 0
                                    ? Blocks.SMOOTH_SANDSTONE.defaultBlockState()
                                    : Blocks.YELLOW_CONCRETE.defaultBlockState();
                            for (int y = 65; y <= height; y++) {
                                set(world, pos, x, y, z, column);
                            }
                            if (height >= 69) {
                                set(world, pos, x, height, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                            }
                        }
                        boolean crossBeam = (localZ == 16 || localZ == 24)
                                && localX >= 8 && localX <= 24
                                && Math.floorMod(localX, 8) != 0;
                        if (!protectedRoute && !upperWalkway && crossBeam) {
                            set(world, pos, x, 70, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                        }
                        if (!protectedRoute && !upperWalkway
                                && (localX == 8 || localX == 24)
                                && (localZ == 8 || localZ == 24)) {
                            set(world, pos, x, 71, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                    }
                    case 20 -> {
                        // Floor-fold field: several shallow recessed pockets sit at
                        // different offsets. Each has a stair entrance and a guarded rim.
                        boolean pocketA = localX >= 6 && localX <= 11
                                && localZ >= 7 && localZ <= 12;
                        boolean pocketB = localX >= 20 && localX <= 26
                                && localZ >= 7 && localZ <= 13;
                        boolean pocketC = localX >= 20 && localX <= 26
                                && localZ >= 21 && localZ <= 27;
                        boolean pocket = pocketA || pocketB || pocketC;
                        Direction stairFacing = pocket
                                ? level0FloorFoldStairFacing(seed, roomX, roomZ, localX, localZ) : null;
                        boolean stairCell = stairFacing != null;
                        if (pocket && !protectedRoute && !upperWalkway) {
                            set(world, pos, x, 62, z, Blocks.SMOOTH_STONE.defaultBlockState());
                            set(world, pos, x, 63, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, 64, z, Blocks.AIR.defaultBlockState());
                            boolean edge = pocketA && (localX == 6 || localX == 11
                                    || localZ == 7 || localZ == 12)
                                    || pocketB && (localX == 20 || localX == 26
                                    || localZ == 7 || localZ == 13)
                                    || pocketC && (localX == 20 || localX == 26
                                    || localZ == 21 || localZ == 27);
                            if (edge && !stairCell) {
                                set(world, pos, x, 63, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            } else if (!stairCell) {
                                set(world, pos, x, 63, z,
                                        Math.floorMod(localX + localZ, 4) == 0
                                                ? Blocks.YELLOW_CARPET.defaultBlockState()
                                                : Blocks.GRAY_CARPET.defaultBlockState());
                            }
                            if (stairCell) {
                                set(world, pos, x, 62, z,
                                        Blocks.OAK_STAIRS.defaultBlockState()
                                                .setValue(StairBlock.FACING, stairFacing));
                            }
                        }
                    }
                    case 21 -> {
                        // A compact stair tower climbs from the office floor to a
                        // raised landing. It is a real route, not a decorative shaft.
                        boolean tower = localX >= 4 && localX <= 11
                                && localZ >= 4 && localZ <= 11;
                        boolean wall = tower && (localX == 4 || localX == 11
                                || localZ == 4 || localZ == 11);
                        boolean doorway = localZ == 4 && localX == 7;
                        if (tower && !protectedRoute && !upperWalkway) {
                            if (wall && !doorway) {
                                for (int y = 65; y <= 70; y++) {
                                    set(world, pos, x, y, z,
                                            Math.floorMod(localX + localZ, 2) == 0
                                                    ? Blocks.YELLOW_CONCRETE.defaultBlockState()
                                                    : ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState());
                                }
                            }
                            boolean landing = localX >= 8 && localX <= 10
                                    && localZ >= 7 && localZ <= 9;
                            if (landing) {
                                set(world, pos, x, 67, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                                if (Math.floorMod(localX + localZ, 3) == 0) {
                                    set(world, pos, x, 68, z, Blocks.YELLOW_CARPET.defaultBlockState());
                                }
                            }
                            boolean rail = (localX == 8 || localX == 10)
                                    && localZ >= 7 && localZ <= 9;
                            if (rail) {
                                set(world, pos, x, 68, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                            }
                            if (localX == 9 && localZ == 8) {
                                set(world, pos, x, 70, z, Blocks.SEA_LANTERN.defaultBlockState());
                            }
                        }
                        if (!protectedRoute && !upperWalkway
                                && localX == 7 && localZ >= 4 && localZ <= 7) {
                            int stairY = 64 + localZ - 4;
                            set(world, pos, x, stairY, z,
                                    Blocks.OAK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.SOUTH));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                    }
                    case 22 -> {
                        // Blackout corridor: dark partitions, dead ceiling bays and
                        // unlit flickering panels create a local light-failure pocket.
                        boolean darkDivider = (localX == 9 || localX == 10 || localX == 22 || localX == 23)
                                && localZ >= 7 && localZ <= 25;
                        boolean crossDivider = (localZ == 9 || localZ == 23)
                                && localX >= 7 && localX <= 25;
                        int doorGap = 10 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA22A0L), 13);
                        boolean doorway = darkDivider && Math.abs(localZ - doorGap) <= 1
                                || crossDivider && Math.abs(localX - doorGap) <= 1;
                        if (!protectedRoute && !upperWalkway && !doorway
                                && (darkDivider || crossDivider)) {
                            for (int y = 65; y <= 69; y++) {
                                set(world, pos, x, y, z,
                                        y == 68 ? Blocks.BLACK_STAINED_GLASS.defaultBlockState()
                                                : Blocks.BLACK_CONCRETE.defaultBlockState());
                            }
                        }
                        boolean darkCeiling = localX >= 8 && localX <= 24
                                && (localZ == 8 || localZ == 16 || localZ == 24)
                                || localZ >= 8 && localZ <= 24
                                && (localX == 8 || localX == 16 || localX == 24)
                                || (localX == 12 || localX == 20)
                                && (localZ == 12 || localZ == 20);
                        if (!upperWalkway && darkCeiling) {
                            set(world, pos, x, 71, z, Blocks.BLACK_CONCRETE.defaultBlockState());
                        }
                        boolean deadPanel = (localX == 12 || localX == 20)
                                && (localZ == 12 || localZ == 20);
                        if (!upperWalkway && deadPanel) {
                            set(world, pos, x, 72, z,
                                    ThresholdEnvironmentBlocks.FLICKERING_PANEL.get().defaultBlockState()
                                            .setValue(FlickeringPanelBlock.LIT, false));
                        }
                        if (!protectedRoute && !upperWalkway
                                && localX >= 13 && localX <= 18
                                && localZ >= 13 && localZ <= 18
                                && Math.floorMod(localX + localZ, 3) != 0) {
                            set(world, pos, x, 64, z, Blocks.BLACK_CARPET.defaultBlockState());
                        }
                        if (!upperWalkway && (localX == 14 || localX == 18)
                                && (localZ == 14 || localZ == 18)) {
                            for (int y = 68; y <= 70; y++) {
                                set(world, pos, x, y, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                    }
                    default -> { }
                }
            }
        }
    }

    private static void buildLevel1ArchitecturalDetails(
            WorldGenLevel world, int chunkX, int chunkZ, long seed, BlockPos.MutableBlockPos pos) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_1_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_1_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_1_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_1_ROOM_SIZE);
                if (roomX == 0 && roomZ == 0) continue; // protect starter shelter and its greenhouse

                int archetype = level1Architecture(seed, roomX, roomZ);
                if (archetype == 0) continue;
                // The common warehouse overlays may not reseal guaranteed
                // corridors. Elevated authored landmarks remain exempt.
                if (archetype <= 10 && ThresholdIrregularMazeLayout
                        .isGuaranteedCorridor(seed, x, z, false)) continue;
                boolean protectedRoute = ThresholdIrregularMazeLayout
                        .isGuaranteedCorridor(seed, x, z, false)
                        || isReservedRoomPath(
                        seed, LEVEL_1_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true);
                boolean protectedResource = isLevel1ResourceFootprint(localX, localZ);
                boolean protectedTile = protectedRoute || protectedResource;
                boolean upperWalkway = hasLevel1UpperDeck(seed, roomX, roomZ)
                        && isLevel1UpperPath(seed, roomX, roomZ, localX, localZ);

                switch (archetype) {
                    case 1, 6 -> {
                        // Heavy pallet-rack aisles. The sixth archetype is a
                        // mirrored echo of the same warehouse grammar.
                        int rackX = archetype == 6 ? localZ : localX;
                        int rackZ = archetype == 6 ? localX : localZ;
                        boolean post = (rackX == 9 || rackX == 18 || rackX == 30 || rackX == 39)
                                && rackZ >= 7 && rackZ <= 41 && Math.floorMod(rackZ, 8) <= 1;
                        boolean shelf = (rackZ >= 12 && rackZ <= 36
                                && Math.floorMod(rackZ, 8) == 4)
                                && (rackX >= 9 && rackX <= 18 || rackX >= 30 && rackX <= 39);
                        if (!protectedTile && post) {
                            for (int y = 64; y <= 69; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        if (!protectedTile && shelf) {
                            set(world, pos, x, 67, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 69, z, Blocks.IRON_BLOCK.defaultBlockState());
                        }
                    }
                    case 2 -> {
                        // Copper service mains, suspended above head height.
                        if (localX == 11 || localX == 24 || localX == 37) {
                            set(world, pos, x, 70, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            if (Math.floorMod(localZ, 8) == 0) {
                                set(world, pos, x, 69, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        if (localZ == 11 || localZ == 37) {
                            set(world, pos, x, 71, z, Blocks.CUT_COPPER.defaultBlockState());
                        }
                    }
                    case 3 -> {
                        // Loading platforms occupy the side bays only. The
                        // central circulation paths and resource footprints stay clear.
                        boolean dock = (localX >= 6 && localX <= 12 || localX >= 35 && localX <= 41)
                                && localZ >= 8 && localZ <= 40;
                        if (!protectedTile && dock && Math.floorMod(localZ, 7) != 0) {
                            set(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                        }
                        if (!protectedTile && dock && (localZ == 8 || localZ == 40)) {
                            set(world, pos, x, 65, z, Blocks.IRON_BARS.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.IRON_BARS.defaultBlockState());
                        }
                    }
                    case 4 -> {
                        // Machine islands: static industrial equipment blocks,
                        // not ticking block entities or redstone contraptions.
                        if (!protectedTile && isLevel1MachineIsland(localX, localZ)) {
                            BlockState machine = switch (Math.floorMod(localX + localZ, 4)) {
                                case 0 -> Blocks.IRON_BLOCK.defaultBlockState();
                                case 1 -> Blocks.PISTON.defaultBlockState();
                                case 2 -> Blocks.FURNACE.defaultBlockState();
                                default -> Blocks.COPPER_BLOCK.defaultBlockState();
                            };
                            set(world, pos, x, 64, z, machine);
                            if (Math.floorMod(localX + localZ, 3) == 0) {
                                set(world, pos, x, 65, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            }
                        }
                    }
                    case 5 -> {
                        // Elevated service gantry: overhead walkway and rails;
                        // it adds vertical scale without blocking floor-level routes.
                        if (localZ == 23 || localZ == 24) {
                            set(world, pos, x, 69, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 70, z, Blocks.IRON_BARS.defaultBlockState());
                        }
                        if (!protectedTile && (localX == 8 || localX == 40)
                                && (localZ == 22 || localZ == 25)) {
                            for (int y = 64; y <= 68; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BLOCK.defaultBlockState());
                            }
                        }
                    }
                    case 7 -> {
                        // Cold-storage loading bay: sealed cargo stacks, rails
                        // and overhead warning fittings make this warehouse feel operational.
                        boolean cargo = (localX >= 6 && localX <= 10 || localX >= 37 && localX <= 41)
                                && (localZ == 13 || localZ == 18 || localZ == 31 || localZ == 36);
                        if (!protectedTile && cargo) {
                            set(world, pos, x, 64, z, Blocks.SMOOTH_STONE.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.BARREL.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 3) == 0) {
                                set(world, pos, x, 66, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        if (localX == 7 || localX == 40) {
                            set(world, pos, x, 72, z, Blocks.LANTERN.defaultBlockState());
                            set(world, pos, x, 73, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                    }
                    case 8 -> {
                        // Abandoned conveyor aisle with overhead copper service lines.
                        boolean belt = (localZ == 18 || localZ == 19)
                                && localX >= 7 && localX <= 40;
                        if (!protectedTile && belt) {
                            set(world, pos, x, 64, z, Blocks.POLISHED_ANDESITE_SLAB.defaultBlockState());
                            if (Math.floorMod(localX, 8) == 0) {
                                set(world, pos, x, 65, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        if ((localX == 8 || localX == 24 || localX == 40)
                                && localZ >= 10 && localZ <= 38 && Math.floorMod(localZ, 6) == 0) {
                            set(world, pos, x, 70, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            set(world, pos, x, 69, z, Blocks.CHAIN.defaultBlockState());
                        }
                        if (!protectedTile && (localX == 10 || localX == 37)
                                && (localZ == 14 || localZ == 24 || localZ == 34)) {
                            set(world, pos, x, 64, z, Blocks.BARREL.defaultBlockState());
                        }
                    }
                    case 9 -> {
                        // Parking-deck bays: painted floor lanes and concrete supports
                        // create a broad industrial space unlike Level 0's office maze.
                        boolean lane = (localX == 9 || localX == 10 || localX == 37 || localX == 38)
                                && localZ >= 7 && localZ <= 41;
                        if (!protectedTile && lane) {
                            set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                        }
                        boolean bayMark = (localZ == 10 || localZ == 38)
                                && (localX >= 11 && localX <= 18 || localX >= 29 && localX <= 36);
                        if (!protectedTile && bayMark) {
                            set(world, pos, x, 64, z, Blocks.WHITE_CARPET.defaultBlockState());
                        }
                        boolean support = (localX == 15 || localX == 33)
                                && (localZ == 14 || localZ == 34);
                        if (!protectedTile && support) {
                            for (int y = 65; y <= 71; y++) {
                                set(world, pos, x, y, z, Blocks.POLISHED_ANDESITE.defaultBlockState());
                            }
                        }
                        if (localX == 24 && (localZ == 8 || localZ == 40)) {
                            set(world, pos, x, 72, z, Blocks.CHAIN.defaultBlockState());
                            set(world, pos, x, 73, z, Blocks.LANTERN.defaultBlockState());
                        }
                    }
                    case 10 -> {
                        // Narrow maintenance spine: pipe runs and guarded service
                        // channels create short claustrophobic sub-areas, not the whole floor.
                        boolean sideRail = (localX == 13 || localX == 14 || localX == 33 || localX == 34)
                                && localZ >= 8 && localZ <= 40;
                        if (!protectedTile && sideRail) {
                            for (int y = 65; y <= 68; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        if ((localX == 12 || localX == 35) && localZ >= 8 && localZ <= 40
                                && Math.floorMod(localZ, 8) == 0) {
                            set(world, pos, x, 70, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            set(world, pos, x, 69, z, Blocks.CHAIN.defaultBlockState());
                        }
                        boolean serviceWalk = (localZ == 22 || localZ == 23)
                                && localX >= 16 && localX <= 31;
                        if (!protectedTile && serviceWalk) {
                            set(world, pos, x, 64, z, Blocks.POLISHED_ANDESITE_SLAB.defaultBlockState());
                        }
                    }
                    case 11 -> {
                        // Freight loading bay: pallet stacks, dock markings and suspended
                        // service chains make this feel like a working logistics facility.
                        boolean dockLane = (localX >= 7 && localX <= 14 || localX >= 33 && localX <= 40)
                                && localZ >= 8 && localZ <= 40;
                        if (!protectedTile && dockLane && Math.floorMod(localZ, 6) == 0) {
                            set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                        }
                        boolean pallet = !protectedTile && dockLane
                                && (localX == 9 || localX == 12 || localX == 35 || localX == 38)
                                && (localZ == 12 || localZ == 20 || localZ == 28 || localZ == 36);
                        if (pallet) {
                            set(world, pos, x, 65, z, Blocks.BARREL.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 2) == 0) {
                                set(world, pos, x, 66, z, Blocks.BARREL.defaultBlockState());
                            }
                        }
                        boolean dockPost = (localX == 7 || localX == 14 || localX == 33 || localX == 40)
                                && (localZ == 10 || localZ == 22 || localZ == 34);
                        if (!protectedTile && dockPost) {
                            for (int y = 65; y <= 69; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        if ((localX == 8 || localX == 39) && localZ >= 10 && localZ <= 38
                                && Math.floorMod(localZ, 8) == 0) {
                            set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                            set(world, pos, x, 71, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    case 12 -> {
                        // Multi-storey parking circulation: painted turning lanes,
                        // heavy supports and a short raised inspection deck.
                        boolean lane = (localX >= 8 && localX <= 12 || localX >= 35 && localX <= 39)
                                && localZ >= 8 && localZ <= 40;
                        if (!protectedTile && lane && Math.floorMod(localZ, 5) == 0) {
                            set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                        }
                        boolean bay = (localZ == 10 || localZ == 38)
                                && (localX >= 13 && localX <= 19 || localX >= 28 && localX <= 34);
                        if (!protectedTile && bay) {
                            set(world, pos, x, 64, z, Blocks.WHITE_CARPET.defaultBlockState());
                        }
                        boolean support = (localX == 16 || localX == 32)
                                && (localZ == 14 || localZ == 34);
                        if (!protectedTile && support) {
                            for (int y = 65; y <= 71; y++) {
                                set(world, pos, x, y, z, Blocks.POLISHED_ANDESITE.defaultBlockState());
                            }
                        }
                        boolean deck = localX >= 20 && localX <= 27 && localZ >= 12 && localZ <= 32;
                        if (!protectedTile && deck) {
                            set(world, pos, x, 68, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            if (localX == 20 || localX == 27) {
                                set(world, pos, x, 69, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        boolean deckSupport = (localX == 20 || localX == 27)
                                && (localZ == 16 || localZ == 24 || localZ == 32);
                        if (!protectedTile && deckSupport) {
                            for (int y = 65; y <= 67; y++) {
                                set(world, pos, x, y, z, Blocks.POLISHED_ANDESITE.defaultBlockState());
                            }
                        }
                        // A five-step concrete access ramp joins the lower deck to the raised platform.
                        if (!protectedTile && localX >= 22 && localX <= 25
                                && localZ >= 28 && localZ <= 32) {
                            int step = localZ - 28;
                            for (int y = 64; y <= 64 + step; y++) {
                                set(world, pos, x, y, z, Blocks.SMOOTH_STONE.defaultBlockState());
                            }
                        }
                    }
                    case 13 -> {
                        // Rare concrete garden: artificial-light overgrowth interrupts
                        // the warehouse palette without turning the whole level into a forest.
                        boolean gardenPatch = localX >= 8 && localX <= 18 && localZ >= 8 && localZ <= 18
                                || localX >= 29 && localX <= 39 && localZ >= 29 && localZ <= 39;
                        boolean mossTile = gardenPatch && Math.floorMod(localX * 7 + localZ * 11, 5) != 0;
                        if (!protectedTile && mossTile) {
                            set(world, pos, x, 64, z, Blocks.MOSS_BLOCK.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 4) == 0) {
                                set(world, pos, x, 65, z, Blocks.MOSS_CARPET.defaultBlockState());
                            }
                        }
                        boolean overgrowth = !protectedTile && gardenPatch
                                && (localX == 10 || localX == 16 || localX == 31 || localX == 37)
                                && (localZ == 10 || localZ == 16 || localZ == 31 || localZ == 37);
                        if (overgrowth) {
                            for (int y = 65; y <= 67; y++) {
                                set(world, pos, x, y, z, Blocks.MOSSY_STONE_BRICKS.defaultBlockState());
                            }
                            set(world, pos, x, 68, z, Blocks.AZALEA_LEAVES.defaultBlockState());
                        }
                        boolean fern = !protectedTile && gardenPatch
                                && Math.floorMod((int) mix(seed, x, z, 0x6A12L), 13) == 0;
                        if (fern && mossTile) {
                            set(world, pos, x, 65, z, Blocks.FERN.defaultBlockState());
                        }
                        if (localX == 13 && localZ == 13 || localX == 34 && localZ == 34) {
                            set(world, pos, x, 71, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    case 14 -> {
                        // Electrical switchgear bay: transformer cabinets, overhead
                        // copper busbars and warning lanes create a distinct machine sector.
                        boolean transformer = (localX == 10 || localX == 15 || localX == 32 || localX == 37)
                                && (localZ == 13 || localZ == 35);
                        if (!protectedTile && transformer) {
                            for (int y = 64; y <= 66; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BLOCK.defaultBlockState());
                            }
                            set(world, pos, x, 67, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            set(world, pos, x, 68, z, Blocks.LIGHTNING_ROD.defaultBlockState());
                        }
                        boolean warningLane = (localZ == 11 || localZ == 37)
                                && (localX >= 8 && localX <= 17 || localX >= 30 && localX <= 39);
                        if (!protectedTile && warningLane) {
                            set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                        }
                        boolean busbar = (localZ == 9 || localZ == 39)
                                && localX >= 8 && localX <= 40;
                        if (busbar) {
                            set(world, pos, x, 71, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            if (Math.floorMod(localX, 6) == 0) {
                                set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        boolean breakerCabinet = (localX == 22 || localX == 27)
                                && (localZ == 14 || localZ == 34);
                        if (!protectedTile && breakerCabinet) {
                            set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                        if (localX == 24 && (localZ == 12 || localZ == 36)) {
                            set(world, pos, x, 72, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    case 15 -> {
                        // Narrow warehouse labyrinth: concrete partitions split
                        // the large bay into storage cells and service aisles.
                        boolean verticalWall = (localX == 12 || localX == 36)
                                && localZ >= 6 && localZ <= 41;
                        boolean horizontalWall = (localZ == 12 || localZ == 36)
                                && localX >= 6 && localX <= 41;
                        int secondaryVerticalGap = 7 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA1510L), 34);
                        int secondaryHorizontalGap = 7 + Math.floorMod(
                                (int) mix(seed, roomX, roomZ, 0xA1511L), 34);
                        boolean secondaryGap = verticalWall
                                && Math.abs(localZ - secondaryVerticalGap) <= 1
                                || horizontalWall
                                && Math.abs(localX - secondaryHorizontalGap) <= 1;
                        if (!protectedTile && !secondaryGap && (verticalWall || horizontalWall)) {
                            for (int y = 65; y <= 70; y++) {
                                set(world, pos, x, y, z,
                                        ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState());
                            }
                            if (Math.floorMod(localX + localZ, 9) == 0) {
                                set(world, pos, x, 72, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        if (!protectedTile && (localX == 10 || localX == 38)
                                && (localZ == 10 || localZ == 38)) {
                            set(world, pos, x, 64, z, Blocks.BARREL.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.CHAIN.defaultBlockState());
                        }
                    }
                    case 16 -> {
                        // Sunken maintenance channel: a five-block lower service
                        // level with a six-step stair entrance, retaining walls and pipework.
                        boolean trench = localX >= 29 && localX <= 38
                                && localZ >= 15 && localZ <= 21;
                        if (trench) {
                            boolean inner = localX >= 30 && localX <= 37
                                    && localZ >= 16 && localZ <= 20;
                            boolean wall = localX == 29 || localX == 38
                                    || localZ == 15 || localZ == 21;
                            boolean stairOpening = localZ == 21
                                    && localX >= 32 && localX <= 35;
                            set(world, pos, x, 58, z, Blocks.SMOOTH_STONE.defaultBlockState());
                            for (int y = 60; y <= 73; y++) {
                                set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
                            }
                            if (wall && !stairOpening) {
                                for (int y = 59; y <= 63; y++) {
                                    set(world, pos, x, y, z,
                                            ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState());
                                }
                                set(world, pos, x, 64, z, Blocks.IRON_BARS.defaultBlockState());
                            } else {
                                for (int y = 59; y <= 63; y++) {
                                    set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
                                }
                                if (inner || stairOpening) {
                                    set(world, pos, x, 59, z, Blocks.GRAY_CARPET.defaultBlockState());
                                }
                            }
                            if (inner && (localZ == 18 || localZ == 19)
                                    && Math.floorMod(localX, 3) == 0) {
                                set(world, pos, x, 62, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            }
                            if (localX == 34 && localZ == 18) {
                                set(world, pos, x, 63, z,
                                        ThresholdEnvironmentBlocks.CEILING_VENT.get().defaultBlockState());
                            }
                        }
                        if ((localX == 33 || localX == 34) && localZ >= 21 && localZ <= 26) {
                            int step = 26 - localZ;
                            int stairY = 63 - step;
                            set(world, pos, x, stairY, z,
                                    Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.SOUTH));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                    }
                    case 17 -> {
                        // Overhead freight gantry: paired rails, cross-members and
                        // suspended hooks form a tall machine hall without blocking walkways.
                        boolean rail = (localX == 9 || localX == 38)
                                && localZ >= 8 && localZ <= 40;
                        boolean crossMember = (localZ == 12 || localZ == 36)
                                && localX >= 9 && localX <= 38;
                        if (!upperWalkway && (rail || crossMember)) {
                            set(world, pos, x, 72, z, Blocks.IRON_BLOCK.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 7) == 0) {
                                set(world, pos, x, 71, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        boolean trolley = (localX == 23 || localX == 24)
                                && (localZ == 16 || localZ == 24 || localZ == 32);
                        if (!protectedTile && !upperWalkway && trolley) {
                            set(world, pos, x, 69, z, Blocks.IRON_BARS.defaultBlockState());
                            set(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
                            set(world, pos, x, 67, z, Blocks.CHAIN.defaultBlockState());
                        }
                        if (!protectedTile && (localX == 12 || localX == 35)
                                && (localZ == 10 || localZ == 38)) {
                            set(world, pos, x, 64, z, Blocks.BARREL.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.CHAIN.defaultBlockState());
                        }
                    }
                    case 18 -> {
                        // Compressor hall: paired machine plinths, service lanes and
                        // overhead headers distinguish this sector from ordinary racks.
                        boolean plinth = (localX >= 10 && localX <= 15
                                || localX >= 32 && localX <= 37)
                                && (localZ >= 12 && localZ <= 17
                                || localZ >= 30 && localZ <= 35);
                        if (!protectedTile && plinth) {
                            set(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            if ((localX == 10 || localX == 15 || localX == 32 || localX == 37)
                                    && (localZ == 12 || localZ == 17 || localZ == 30 || localZ == 35)) {
                                set(world, pos, x, 65, z, Blocks.IRON_BLOCK.defaultBlockState());
                                set(world, pos, x, 66, z, Blocks.COPPER_BLOCK.defaultBlockState());
                                set(world, pos, x, 67, z, Blocks.LIGHTNING_ROD.defaultBlockState());
                            }
                        }
                        boolean serviceLane = (localZ == 20 || localZ == 27)
                                && localX >= 8 && localX <= 39;
                        if (!protectedTile && serviceLane) {
                            set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                        }
                        if (!upperWalkway && (localX == 8 || localX == 39)
                                && localZ >= 8 && localZ <= 40) {
                            set(world, pos, x, 71, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            if (Math.floorMod(localZ, 6) == 0) {
                                set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                    }
                    case 19 -> {
                        // Sorting conveyors: low slab belts, side guides and transfer
                        // nodes make long industrial sightlines with walkable crossings.
                        boolean belt = (localZ == 16 || localZ == 17
                                || localZ == 31 || localZ == 32)
                                && localX >= 8 && localX <= 40;
                        if (!protectedTile && belt) {
                            set(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            if ((localZ == 16 || localZ == 31)
                                    && (localX == 8 || localX == 40)) {
                                set(world, pos, x, 65, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                            if (Math.floorMod(localX, 8) == 0) {
                                set(world, pos, x, 65, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            }
                        }
                        boolean transferHub = (localX == 24 || localX == 25)
                                && (localZ == 16 || localZ == 17 || localZ == 31 || localZ == 32);
                        if (!protectedTile && transferHub) {
                            set(world, pos, x, 66, z, Blocks.CHAIN.defaultBlockState());
                        }
                    }
                    case 20 -> {
                        // Raised loading-office mezzanine: a three-step stair reaches
                        // a guarded platform below the main cross-room catwalk.
                        boolean platform = localX >= 7 && localX <= 15
                                && localZ >= 31 && localZ <= 39;
                        boolean stair = (localX == 10 || localX == 11)
                                && localZ >= 28 && localZ <= 30;
                        boolean sideStair = localX >= 4 && localX <= 6
                                && (localZ == 35 || localZ == 36);
                        if (!upperWalkway && platform) {
                            // Overhead flooring at Y=66 leaves the full ground-floor passage
                            // below it accessible, even where the old grid reserves an exit path.
                            {
                                set(world, pos, x, 66, z, Blocks.IRON_BLOCK.defaultBlockState());
                                if (Math.floorMod(localX + localZ, 5) == 0) {
                                    set(world, pos, x, 67, z, Blocks.YELLOW_CARPET.defaultBlockState());
                                }
                            }
                            boolean edge = localX == 7 || localX == 15
                                    || localZ == 31 || localZ == 39;
                            boolean stairOpening = localZ == 31
                                    && (localX == 10 || localX == 11)
                                    || localX == 7 && (localZ == 35 || localZ == 36);
                            if (edge && !stairOpening && !protectedTile) {
                                set(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        if (!upperWalkway && !protectedTile && stair) {
                            int stairY = 64 + localZ - 28;
                            set(world, pos, x, stairY, z,
                                    Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.SOUTH));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                        if (!upperWalkway && !protectedTile && sideStair) {
                            int stairY = 64 + localX - 4;
                            set(world, pos, x, stairY, z,
                                    Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.EAST));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                        if (!upperWalkway && !protectedTile && platform
                                && (localX == 7 || localX == 15)
                                && (localZ == 31 || localZ == 39)) {
                            for (int y = 64; y <= 65; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BLOCK.defaultBlockState());
                            }
                        }
                        if (!upperWalkway && localX == 12 && localZ == 35) {
                            set(world, pos, x, 69, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    case 21 -> {
                        // A compact pump sump descends five blocks below the warehouse
                        // floor. Its stair, retaining walls and pipework make the
                        // lower service route physically walkable and visually legible.
                        boolean sump = localX >= 7 && localX <= 12
                                && localZ >= 17 && localZ <= 22;
                        boolean inner = localX >= 8 && localX <= 11
                                && localZ >= 18 && localZ <= 21;
                        boolean wall = sump && (localX == 7 || localX == 12
                                || localZ == 17 || localZ == 22);
                        boolean stairOpening = localZ == 17
                                && (localX == 9 || localX == 10)
                                || localX == 12 && (localZ == 20 || localZ == 21);
                        if (sump && !protectedTile && !upperWalkway) {
                            set(world, pos, x, 58, z, Blocks.SMOOTH_STONE.defaultBlockState());
                            for (int y = 59; y <= 73; y++) {
                                set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
                            }
                            if (wall && !stairOpening) {
                                for (int y = 59; y <= 63; y++) {
                                    set(world, pos, x, y, z,
                                            ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState());
                                }
                                set(world, pos, x, 64, z, Blocks.IRON_BARS.defaultBlockState());
                            } else if (inner || stairOpening) {
                                set(world, pos, x, 59, z, Blocks.GRAY_CARPET.defaultBlockState());
                            }
                            if (inner && localZ == 19 && Math.floorMod(localX, 2) == 0) {
                                set(world, pos, x, 62, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            }
                            if (localX == 8 && localZ == 19) {
                                set(world, pos, x, 62, z, Blocks.SEA_LANTERN.defaultBlockState());
                            }
                            if (localX == 11 && localZ == 20) {
                                set(world, pos, x, 61, z, Blocks.LIGHTNING_ROD.defaultBlockState());
                            }
                        }
                        if (!protectedTile && !upperWalkway
                                && (localX == 9 || localX == 10)
                                && localZ >= 12 && localZ <= 16) {
                            int stairY = 63 - (localZ - 12);
                            set(world, pos, x, stairY, z,
                                    Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.NORTH));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                        if (!protectedTile && !upperWalkway
                                && localX >= 13 && localX <= 17
                                && (localZ == 20 || localZ == 21)) {
                            int stairY = 59 + localX - 13;
                            set(world, pos, x, stairY, z,
                                    Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                                            .setValue(StairBlock.FACING, Direction.EAST));
                            set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                            set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                        }
                    }
                    case 22 -> {
                        // Tall pallet-rack canyon: multi-tier shelves and vertical
                        // frames create a stacked warehouse volume with clear aisles.
                        boolean rackProtected = protectedTile || isLevel1MachineIsland(localX, localZ);
                        boolean post = (localX == 8 || localX == 14
                                || localX == 32 || localX == 38)
                                && localZ >= 8 && localZ <= 40
                                && Math.floorMod(localZ, 8) <= 1;
                        if (!upperWalkway && !rackProtected && post) {
                            for (int y = 64; y <= 69; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        boolean shelf = (localZ == 12 || localZ == 20
                                || localZ == 28 || localZ == 36)
                                && (localX >= 8 && localX <= 14
                                || localX >= 32 && localX <= 38);
                        if (!upperWalkway && !rackProtected && shelf) {
                            set(world, pos, x, 66, z, Blocks.IRON_BLOCK.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 3) == 0) {
                                set(world, pos, x, 68, z, Blocks.IRON_BLOCK.defaultBlockState());
                            }
                        }
                        boolean cargo = (localX == 10 || localX == 12
                                || localX == 34 || localX == 36)
                                && (localZ == 13 || localZ == 21 || localZ == 29 || localZ == 37);
                        if (!upperWalkway && !rackProtected && cargo) {
                            set(world, pos, x, 67, z, Blocks.BARREL.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 2) == 0) {
                                set(world, pos, x, 69, z, Blocks.BARREL.defaultBlockState());
                            }
                        }
                        if (!upperWalkway && !rackProtected
                                && (localX == 11 || localX == 35)
                                && (localZ == 16 || localZ == 32)) {
                            set(world, pos, x, 69, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    case 23 -> {
                        // Gothic arcade: repeated heavy piers and high stone arches
                        // turn one warehouse cell into a long, monumental hall.
                        boolean pier = (localX == 10 || localX == 11 || localX == 36 || localX == 37)
                                && (localZ == 10 || localZ == 11 || localZ == 24 || localZ == 25
                                || localZ == 37 || localZ == 38);
                        if (!protectedTile && !upperWalkway && pier) {
                            for (int y = 64; y <= 70; y++) {
                                set(world, pos, x, y, z,
                                        y >= 69 ? Blocks.SMOOTH_STONE.defaultBlockState()
                                                : Blocks.POLISHED_ANDESITE.defaultBlockState());
                            }
                        }
                        boolean archSpan = (localZ == 12 || localZ == 26 || localZ == 39)
                                && localX >= 12 && localX <= 35;
                        if (!protectedTile && !upperWalkway && archSpan) {
                            set(world, pos, x, 70, z, Blocks.STONE_BRICKS.defaultBlockState());
                            if (localX == 13 || localX == 34) {
                                set(world, pos, x, 69, z,
                                        Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                                                .setValue(StairBlock.FACING, Direction.NORTH));
                            }
                        }
                        if (!protectedTile && !upperWalkway
                                && (localX == 14 || localX == 34)
                                && (localZ == 16 || localZ == 32)) {
                            set(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.IRON_BARS.defaultBlockState());
                        }
                        if (!upperWalkway && localX == 24
                                && (localZ == 12 || localZ == 26 || localZ == 39)) {
                            set(world, pos, x, 72, z, Blocks.LANTERN.defaultBlockState());
                        }
                    }
                    case 24 -> {
                        // Cross-dock crane hall: high tracks, a travelling bridge,
                        // suspended chain hook and side loading-control booth.
                        boolean track = (localZ == 18 || localZ == 30)
                                && localX >= 8 && localX <= 40;
                        if (!upperWalkway && track) {
                            set(world, pos, x, 71, z, Blocks.IRON_BLOCK.defaultBlockState());
                            if (Math.floorMod(localX, 8) == 0) {
                                set(world, pos, x, 72, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        boolean support = (localX == 8 || localX == 40)
                                && (localZ == 18 || localZ == 30);
                        if (!protectedTile && !upperWalkway && support) {
                            for (int y = 64; y <= 70; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        boolean bridge = localX >= 22 && localX <= 26
                                && localZ >= 23 && localZ <= 25;
                        if (!protectedTile && !upperWalkway && bridge) {
                            set(world, pos, x, 70, z, Blocks.IRON_BLOCK.defaultBlockState());
                            if (localZ == 24 && (localX == 23 || localX == 25)) {
                                set(world, pos, x, 69, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        if (!protectedTile && !upperWalkway
                                && (localX == 18 || localX == 19)
                                && (localZ == 9 || localZ == 10)) {
                            set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            set(world, pos, x, 67, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                        if (!upperWalkway && localX == 24 && localZ == 18) {
                            set(world, pos, x, 73, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    case 25 -> {
                        // Ventilation exchange hall: large static air ducts,
                        // inspection stacks and overhead copper mains.
                        boolean duct = (localX == 10 || localX == 24 || localX == 38)
                                && localZ >= 8 && localZ <= 40;
                        if (!upperWalkway && duct) {
                            set(world, pos, x, 71, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            if (Math.floorMod(localZ, 8) == 0) {
                                set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        boolean crossDuct = (localZ == 10 || localZ == 38)
                                && localX >= 10 && localX <= 38;
                        if (!upperWalkway && crossDuct) {
                            set(world, pos, x, 72, z, Blocks.CUT_COPPER.defaultBlockState());
                        }
                        boolean stack = isLevel1VentilationStackCell(
                                seed, roomX, roomZ, localX, localZ, 10, 10)
                                || isLevel1VentilationStackCell(
                                seed, roomX, roomZ, localX, localZ, 38, 10)
                                || isLevel1VentilationStackCell(
                                seed, roomX, roomZ, localX, localZ, 10, 38)
                                || isLevel1VentilationStackCell(
                                seed, roomX, roomZ, localX, localZ, 38, 38);
                        if (stack) {
                            for (int y = 64; y <= 69; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BLOCK.defaultBlockState());
                            }
                            set(world, pos, x, 70, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                        }
                        boolean inspectionBay = localX >= 20 && localX <= 28
                                && localZ >= 20 && localZ <= 28;
                        if (!protectedTile && !upperWalkway && inspectionBay
                                && (localX == 20 || localX == 28 || localZ == 20 || localZ == 28)) {
                            set(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            if (Math.floorMod(localX + localZ, 4) == 0) {
                                set(world, pos, x, 65, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                    }
                    case 26 -> {
                        // Generator plant: four static engine assemblies, exhaust
                        // stacks, overhead copper busbars and protected breaker bays.
                        // Primary engines are mandatory fixtures; a legacy
                        // navigation mask must not erase their actual cores.
                        boolean generatorCore = (localX == 10 || localX == 38)
                                && (localZ == 10 || localZ == 38);
                        if (!upperWalkway && generatorCore) {
                            set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.FURNACE.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            set(world, pos, x, 67, z, Blocks.LIGHTNING_ROD.defaultBlockState());
                            for (int y = 68; y <= 71; y++) {
                                set(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        boolean engineBase = (localX >= 9 && localX <= 11
                                || localX >= 37 && localX <= 39)
                                && (localZ >= 9 && localZ <= 11
                                || localZ >= 37 && localZ <= 39);
                        if (!protectedTile && !upperWalkway && engineBase && !generatorCore) {
                            if (Math.floorMod(localX + localZ, 2) == 0) {
                                set(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
                            }
                            if (localX == 9 || localX == 11 || localX == 37 || localX == 39) {
                                set(world, pos, x, 65, z, Blocks.IRON_BARS.defaultBlockState());
                            }
                        }
                        boolean busbar = (localZ == 14 || localZ == 34)
                                && localX >= 10 && localX <= 38;
                        if (!upperWalkway && busbar) {
                            set(world, pos, x, 71, z, Blocks.COPPER_BLOCK.defaultBlockState());
                            if (Math.floorMod(localX, 6) == 0) {
                                set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                            }
                        }
                        boolean breaker = (localX == 20 || localX == 28)
                                && (localZ == 20 || localZ == 28);
                        if (!protectedTile && !upperWalkway && breaker) {
                            set(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                            set(world, pos, x, 65, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                            set(world, pos, x, 66, z, Blocks.REDSTONE_LAMP.defaultBlockState());
                            set(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());
                        }
                        boolean warningLane = (localZ == 12 || localZ == 36)
                                && localX >= 8 && localX <= 40;
                        if (!protectedTile && warningLane) {
                            set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                        }
                        if (!upperWalkway && localX == 24
                                && (localZ == 14 || localZ == 34)) {
                            set(world, pos, x, 72, z, Blocks.SEA_LANTERN.defaultBlockState());
                        }
                    }
                    default -> { }
                }
            }
        }
    }

    /**
     * Small environmental details make ordinary Level 0 rooms feel inhabited
     * by the building rather than empty test cells: worn carpet, irregular
     * fluorescent panels, vents and a few hanging cable runs.
     */
    private static void buildLevel0EnvironmentalDressing(
            WorldGenLevel world, int chunkX, int chunkZ, long seed, BlockPos.MutableBlockPos pos) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_0_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_0_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_0_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_0_ROOM_SIZE);
                if (roomX == 0 && roomZ == 0) continue;
                int archetype = level0Architecture(seed, roomX, roomZ);
                if (archetype >= 10) continue;
                int detail = Math.floorMod((int) mix(seed, x, z, 0xA0E5L), 100);
                boolean reserved = ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, true)
                        || isReservedRoomPath(
                        seed, LEVEL_0_ROOM_SIZE, roomX, roomZ, localX, localZ, 1, false);

                if (!reserved && localX % 4 == 1 && localZ % 4 == 1 && detail < 9) {
                    set(world, pos, x, 64, z, Blocks.BROWN_CARPET.defaultBlockState());
                }
                if (localX % 8 == 4 && localZ % 8 == 4 && detail < 18) {
                    set(world, pos, x, 72, z,
                            ThresholdEnvironmentBlocks.FLICKERING_PANEL.get().defaultBlockState());
                } else if (localX % 12 == 6 && localZ % 12 == 6 && detail < 10) {
                    set(world, pos, x, 72, z,
                            ThresholdEnvironmentBlocks.CEILING_VENT.get().defaultBlockState());
                }
                if (!hasLevel0UpperDeck(seed, roomX, roomZ)
                        && localX % 10 == 5 && localZ % 10 == 5 && detail < 6) {
                    set(world, pos, x, 70, z, Blocks.CHAIN.defaultBlockState());
                    set(world, pos, x, 71, z, Blocks.CHAIN.defaultBlockState());
                }
            }
        }
    }

    /**
     * Level 1 dressing is more functional: irregular ventilation, suspended
     * cable/pipe runs and worn warning-floor patches. It avoids upper catwalk
     * walking surfaces, resource footprints and the untouched entrance shelter.
     */
    private static void buildLevel1EnvironmentalDressing(
            WorldGenLevel world, int chunkX, int chunkZ, long seed, BlockPos.MutableBlockPos pos) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_1_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_1_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_1_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_1_ROOM_SIZE);
                if (roomX == 0 && roomZ == 0) continue;
                int archetype = level1Architecture(seed, roomX, roomZ);
                if (archetype >= 15) continue;
                int detail = Math.floorMod((int) mix(seed, x, z, 0xA1E5L), 100);
                boolean protectedTile = isLevel1ResourceFootprint(localX, localZ)
                        || ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, false)
                        || isReservedRoomPath(
                        seed, LEVEL_1_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true);
                boolean upperWalkway = hasLevel1UpperDeck(seed, roomX, roomZ)
                        && isLevel1UpperPath(seed, roomX, roomZ, localX, localZ);

                if (!protectedTile && localX % 6 == 2 && localZ % 6 == 2 && detail < 8) {
                    set(world, pos, x, 64, z, Blocks.YELLOW_CARPET.defaultBlockState());
                }
                if (localX % 12 == 6 && localZ % 12 == 6 && detail < 24 && !upperWalkway) {
                    set(world, pos, x, 73, z,
                            ThresholdEnvironmentBlocks.CEILING_VENT.get().defaultBlockState());
                } else if (localX % 12 == 3 && localZ % 12 == 3 && detail < 13 && !upperWalkway) {
                    set(world, pos, x, 73, z,
                            ThresholdEnvironmentBlocks.FLICKERING_PANEL.get().defaultBlockState());
                }
                if (!upperWalkway && localX % 10 == 5 && localZ % 10 == 5 && detail < 9) {
                    set(world, pos, x, 71, z, Blocks.CHAIN.defaultBlockState());
                    set(world, pos, x, 72, z, Blocks.CHAIN.defaultBlockState());
                }
                if (!upperWalkway && localX % 16 == 8 && localZ % 16 == 8 && detail < 12) {
                    set(world, pos, x, 71, z, Blocks.COPPER_BLOCK.defaultBlockState());
                }
            }
        }
    }

    /**
     * Level 0's upper galleries are connected to the ordinary room graph by
     * a stair flight. Shared edge decisions make neighbouring upper decks line up
     * even when chunks are generated in a different order.
     */
    private static void buildLevel0VerticalNetwork(
            WorldGenLevel world, int chunkX, int chunkZ, long seed, BlockPos.MutableBlockPos pos) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_0_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_0_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_0_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_0_ROOM_SIZE);
                if (!hasLevel0UpperDeck(seed, roomX, roomZ)) continue;

                boolean stair = isLevel0UpperStair(localX, localZ);
                boolean upperPath = isLevel0UpperPath(seed, roomX, roomZ, localX, localZ);
                if (upperPath && !stair) {
                    set(world, pos, x, 68, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                    set(world, pos, x, 69, z,
                            Math.floorMod(localX + localZ, 11) == 0
                                    ? Blocks.BROWN_CARPET.defaultBlockState()
                                    : Blocks.YELLOW_CARPET.defaultBlockState());
                    boolean boundary = isBoundary(localX, localZ, LEVEL_0_ROOM_SIZE);
                    boolean connected = hasLevel0UpperNeighbour(seed, roomX, roomZ,
                            localX, localZ);
                    if (!boundary || connected) {
                        set(world, pos, x, 70, z, Blocks.AIR.defaultBlockState());
                        set(world, pos, x, 71, z, Blocks.AIR.defaultBlockState());
                    }
                }

                boolean rail = isReservedRoomPath(
                        seed, LEVEL_0_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, false)
                        && !isReservedRoomPath(
                        seed, LEVEL_0_ROOM_SIZE, roomX, roomZ, localX, localZ, 1, false)
                        && !isBoundary(localX, localZ, LEVEL_0_ROOM_SIZE)
                        && !stair;
                if (rail) {
                    set(world, pos, x, 69, z, Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState());
                }

                if (stair) {
                    int step = localZ - 12;
                    int stairY = 64 + step;
                    set(world, pos, x, stairY, z,
                            Blocks.OAK_STAIRS.defaultBlockState()
                                    .setValue(StairBlock.FACING, Direction.SOUTH));
                    set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                    set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                }

                if (localX == 16 && localZ == 16
                        && Math.floorMod((int) mix(seed, roomX, roomZ, 0xA0F0L), 3) == 0) {
                    set(world, pos, x, 72, z,
                            ThresholdEnvironmentBlocks.CEILING_VENT.get().defaultBlockState());
                }
            }
        }
    }

    private static boolean hasLevel0UpperDeck(long seed, int roomX, int roomZ) {
        // Landmark rooms 16-22 own their vertical architecture. Their
        // mezzanines, blackout corridor and sub-floor structures must not be
        // overlaid by the generic upper-gallery network.
        if (level0Architecture(seed, roomX, roomZ) >= 16) return false;
        return !(roomX == 0 && roomZ == 0)
                && ((roomX == 1 && roomZ == 0)
                || Math.floorMod((int) mix(seed, roomX, roomZ, 0xA0D0L), 100) < 38);
    }

    private static boolean isLevel0UpperPath(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        return Math.abs(localX - 16) <= 2 && Math.abs(localZ - 16) <= 2
                || isReservedRoomPath(seed, LEVEL_0_ROOM_SIZE,
                roomX, roomZ, localX, localZ, 1, false);
    }

    private static boolean isLevel0UpperStair(int localX, int localZ) {
        return (localX == 16 || localX == 17) && localZ >= 12 && localZ <= 16;
    }

    private static boolean hasLevel0UpperNeighbour(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        if (localX == 0) return hasLevel0UpperDeck(seed, roomX - 1, roomZ);
        if (localX == LEVEL_0_ROOM_SIZE - 1) return hasLevel0UpperDeck(seed, roomX + 1, roomZ);
        if (localZ == 0) return hasLevel0UpperDeck(seed, roomX, roomZ - 1);
        if (localZ == LEVEL_0_ROOM_SIZE - 1) return hasLevel0UpperDeck(seed, roomX, roomZ + 1);
        return false;
    }

    /**
     * Level 1 uses a higher industrial catwalk network plus rare lower
     * maintenance channels. Both are connected to the ground-floor circulation.
     */
    private static void buildLevel1VerticalNetwork(
            WorldGenLevel world, int chunkX, int chunkZ, long seed, BlockPos.MutableBlockPos pos) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            int roomX = Math.floorDiv(x, LEVEL_1_ROOM_SIZE);
            int localX = Math.floorMod(x, LEVEL_1_ROOM_SIZE);
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                int roomZ = Math.floorDiv(z, LEVEL_1_ROOM_SIZE);
                int localZ = Math.floorMod(z, LEVEL_1_ROOM_SIZE);
                if (!hasLevel1UpperDeck(seed, roomX, roomZ)) continue;

                boolean stair = isLevel1UpperStair(localX, localZ);
                boolean upperPath = isLevel1UpperPath(seed, roomX, roomZ, localX, localZ);
                if (upperPath && !stair) {
                    set(world, pos, x, 70, z, Blocks.IRON_BLOCK.defaultBlockState());
                    set(world, pos, x, 71, z,
                            Math.floorMod(localX + localZ, 9) == 0
                                    ? Blocks.YELLOW_CARPET.defaultBlockState()
                                    : Blocks.GRAY_CARPET.defaultBlockState());
                    boolean boundary = isBoundary(localX, localZ, LEVEL_1_ROOM_SIZE);
                    boolean connected = hasLevel1UpperNeighbour(seed, roomX, roomZ,
                            localX, localZ);
                    if (!boundary || connected) {
                        set(world, pos, x, 72, z, Blocks.AIR.defaultBlockState());
                        set(world, pos, x, 73, z, Blocks.AIR.defaultBlockState());
                    }
                }

                boolean rail = isReservedRoomPath(
                        seed, LEVEL_1_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true)
                        && !isReservedRoomPath(
                        seed, LEVEL_1_ROOM_SIZE, roomX, roomZ, localX, localZ, 1, true)
                        && !isBoundary(localX, localZ, LEVEL_1_ROOM_SIZE)
                        && !stair;
                if (rail) {
                    set(world, pos, x, 71, z, Blocks.IRON_BARS.defaultBlockState());
                }

                if (stair) {
                    int step = localZ - 18;
                    int stairY = 64 + step;
                    set(world, pos, x, stairY, z,
                            Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                                    .setValue(StairBlock.FACING, Direction.SOUTH));
                    set(world, pos, x, stairY + 1, z, Blocks.AIR.defaultBlockState());
                    set(world, pos, x, stairY + 2, z, Blocks.AIR.defaultBlockState());
                }

                if (localX == 24 && localZ == 24
                        && Math.floorMod((int) mix(seed, roomX, roomZ, 0xA1F0L), 2) == 0) {
                    set(world, pos, x, 74, z,
                            ThresholdEnvironmentBlocks.CEILING_VENT.get().defaultBlockState());
                }
            }
        }
    }

    private static boolean hasLevel1UpperDeck(long seed, int roomX, int roomZ) {
        // The new landmark sectors provide their own overhead structures;
        // suppressing the generic catwalk here prevents ceiling collisions.
        // The authored loading-office mezzanine (20) also has its own stair and deck.
        // A generic upper catwalk can mask its stairway and make the landmark unreachable.
        int landmark = level1Architecture(seed, roomX, roomZ);
        if (landmark == 20 || landmark >= 23) return false;
        return !(roomX == 0 && roomZ == 0)
                && ((roomX == 1 && roomZ == 1)
                || Math.floorMod((int) mix(seed, roomX, roomZ, 0xA1D0L), 100) < 44);
    }

    private static boolean isLevel1UpperPath(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        return Math.abs(localX - 24) <= 2 && Math.abs(localZ - 24) <= 2
                || isReservedRoomPath(seed, LEVEL_1_ROOM_SIZE,
                roomX, roomZ, localX, localZ, 1, true);
    }

    private static boolean isLevel1UpperStair(int localX, int localZ) {
        return (localX == 24 || localX == 25) && localZ >= 18 && localZ <= 24;
    }

    private static boolean hasLevel1UpperNeighbour(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        if (localX == 0) return hasLevel1UpperDeck(seed, roomX - 1, roomZ);
        if (localX == LEVEL_1_ROOM_SIZE - 1) return hasLevel1UpperDeck(seed, roomX + 1, roomZ);
        if (localZ == 0) return hasLevel1UpperDeck(seed, roomX, roomZ - 1);
        if (localZ == LEVEL_1_ROOM_SIZE - 1) return hasLevel1UpperDeck(seed, roomX, roomZ + 1);
        return false;
    }

    private static int level0Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0;
        // A deterministic first-ring discovery guarantees one connected
        // sub-floor annex for every world seed, without altering the entry room.
        if (roomX == 1 && roomZ == 0) return 12;
        if (roomX == 0 && roomZ == 1) return 16;
        if (roomX == 1 && roomZ == 1) return 17;
        if (roomX == 2 && roomZ == 0) return 18;
        if (roomX == 2 && roomZ == 2) return 19;
        if (roomX == 3 && roomZ == 0) return 20;
        if (roomX == 0 && roomZ == 2) return 21;
        if (roomX == 3 && roomZ == 2) return 22;
        int roll = Math.floorMod((int) mix(seed, roomX, roomZ, 0xA2C0L), 100);
        // Empty office space is the dominant visual. Architectural fragments
        // become progressively rarer so the Lobby never reads like a furnished dungeon.
        if (roll < 26) return 0;
        if (roll < 38) return 1;
        if (roll < 49) return 2;
        if (roll < 58) return 3;
        if (roll < 64) return 4;
        if (roll < 69) return 5;
        if (roll < 73) return 6;
        if (roll < 76) return 7;
        if (roll < 78) return 8;
        if (roll < 80) return 9;
        if (roll < 82) return 10;
        if (roll < 85) return 11;
        if (roll < 87) return 12;
        if (roll < 90) return 13;
        if (roll < 92) return 14;
        if (roll < 93) return 15;
        if (roll < 94) return 16;
        if (roll < 95) return 17;
        if (roll < 96) return 18;
        if (roll < 97) return 19;
        if (roll < 98) return 20;
        if (roll < 99) return 21;
        return 22;
    }

    private static int level1Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0;
        // A distant maintenance sector guarantees a real lower service route
        // for exploration and runtime validation in every world seed.
        if (roomX == 1 && roomZ == 1) return 16;
        if (roomX == 0 && roomZ == 1) return 20;
        if (roomX == 1 && roomZ == 0) return 21;
        if (roomX == 2 && roomZ == 1) return 22;
        if (roomX == 2 && roomZ == 2) return 23;
        if (roomX == 3 && roomZ == 0) return 24;
        if (roomX == 0 && roomZ == 2) return 25;
        if (roomX == 2 && roomZ == 3) return 26;
        int roll = Math.floorMod((int) mix(seed, roomX, roomZ, 0xA2C1L), 100);
        // A warehouse grammar: racks and service infrastructure are common;
        // parking-deck and narrow maintenance variants remain uncommon discoveries.
        if (roll < 9) return 1;
        if (roll < 15) return 6;
        if (roll < 24) return 2;
        if (roll < 33) return 3;
        if (roll < 41) return 4;
        if (roll < 48) return 5;
        if (roll < 54) return 7;
        if (roll < 60) return 8;
        if (roll < 65) return 9;
        if (roll < 69) return 10;
        if (roll < 73) return 11;
        if (roll < 77) return 12;
        if (roll < 81) return 13;
        if (roll < 83) return 14;
        if (roll < 85) return 15;
        if (roll < 87) return 17;
        if (roll < 89) return 18;
        if (roll < 90) return 19;
        if (roll < 92) return 20;
        if (roll < 93) return 21;
        if (roll < 94) return 22;
        if (roll < 95) return 16;
        if (roll < 96) return 23;
        if (roll < 97) return 24;
        if (roll < 98) return 25;
        return 26;
    }

    private static boolean isLevel0CubiclePanel(int localX, int localZ, boolean mirrored) {
        int x = mirrored ? localZ : localX;
        int z = mirrored ? localX : localZ;
        return (x == 8 || x == 23) && (z >= 6 && z <= 13 || z >= 18 && z <= 25)
                || (z == 8 || z == 23) && (x >= 6 && x <= 13 || x >= 18 && x <= 25);
    }

    private static boolean isLevel0Desk(int localX, int localZ) {
        return (localX == 10 || localX == 21) && (localZ == 10 || localZ == 21);
    }

    private static boolean isLevel0Partition(int localX, int localZ) {
        return (localX == 10 || localX == 21) && localZ >= 6 && localZ <= 25
                && Math.floorMod(localZ, 6) != 0
                || (localZ == 10 || localZ == 21) && localX >= 6 && localX <= 25
                && Math.floorMod(localX, 6) != 0;
    }

    private static boolean isLevel0Column(int localX, int localZ) {
        return (localX == 8 || localX == 9 || localX == 22 || localX == 23)
                && (localZ == 8 || localZ == 9 || localZ == 22 || localZ == 23);
    }

    /**
     * Choose a seed-stable floor-fold entrance that does not intersect the
     * guaranteed spine or a reserved room exit. Each pocket searches all four
     * edges, so a corridor crossing one side cannot erase every way down.
     */
    /**
     * The starter composter uses a deterministic nearby tile that is not part
     * of either the physical maze spine or the reserved room-exit route.
     */
    /**
     * Select one deterministic pillar-forest anchor outside both the physical
     * spine and the reserved room-exit route. The landmark always retains at
     * least one column without placing a pillar in a guaranteed passage.
     */
    private static boolean isGuaranteedLevel0PillarCell(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        int[] anchors = {6, 14, 22, 26};
        int total = anchors.length * anchors.length;
        int start = Math.floorMod((int) mix(seed, roomX, roomZ, 0xA19A2L), total);
        for (int offset = 0; offset < total; offset++) {
            int index = (start + offset) % total;
            int candidateX = anchors[index / anchors.length];
            int candidateZ = anchors[index % anchors.length];
            int x = roomX * LEVEL_0_ROOM_SIZE + candidateX;
            int z = roomZ * LEVEL_0_ROOM_SIZE + candidateZ;
            boolean protectedRoute = ThresholdIrregularMazeLayout
                    .isGuaranteedCorridor(seed, x, z, true)
                    || isReservedRoomPath(seed, LEVEL_0_ROOM_SIZE,
                            roomX, roomZ, candidateX, candidateZ, 1, false);
            if (!protectedRoute) {
                return localX == candidateX && localZ == candidateZ;
            }
        }
        return false;
    }

    private static boolean isLevel1StarterComposterCell(
            long seed, int localX, int localZ) {
        int minX = 20;
        int maxX = 31;
        int minZ = 10;
        int maxZ = 30;
        if (localX < minX || localX > maxX || localZ < minZ || localZ > maxZ) return false;
        int height = maxZ - minZ + 1;
        int total = (maxX - minX + 1) * height;
        int start = Math.floorMod((int) mix(seed, 0, 0, 0xC0A7E57L), total);
        for (int offset = 0; offset < total; offset++) {
            int index = (start + offset) % total;
            int candidateX = minX + index / height;
            int candidateZ = minZ + index % height;
            if (!isLevel1ComposterRouteProtected(
                    seed, 0, 0, candidateX, candidateZ)) {
                return localX == candidateX && localZ == candidateZ;
            }
        }
        return false;
    }

    private static boolean isLevel1ComposterRouteProtected(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        int x = roomX * LEVEL_1_ROOM_SIZE + localX;
        int z = roomZ * LEVEL_1_ROOM_SIZE + localZ;
        return ThresholdIrregularMazeLayout.isProtectedPassageOrEntry(x, z, false)
                || ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, false)
                || isReservedRoomPath(seed, LEVEL_1_ROOM_SIZE,
                        roomX, roomZ, localX, localZ, 2, true)
                || isLevel1ResourceFootprint(localX, localZ)
                || isLevel1CratePosition(seed, roomX, roomZ, localX, localZ);
    }

    private static Direction level0FloorFoldStairFacing(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        Direction facing = findFloorFoldStairFacing(
                seed, roomX, roomZ, localX, localZ, 6, 11, 7, 12, 0xA20A0L);
        if (facing != null) return facing;
        facing = findFloorFoldStairFacing(
                seed, roomX, roomZ, localX, localZ, 20, 26, 7, 13, 0xA20B0L);
        if (facing != null) return facing;
        return findFloorFoldStairFacing(
                seed, roomX, roomZ, localX, localZ, 20, 26, 21, 27, 0xA20C0L);
    }

    private static Direction findFloorFoldStairFacing(
            long seed, int roomX, int roomZ, int localX, int localZ,
            int minX, int maxX, int minZ, int maxZ, long salt) {
        int horizontalPairs = maxX - minX - 2;
        int verticalPairs = maxZ - minZ - 2;
        int horizontalTotal = horizontalPairs * 2;
        int total = horizontalTotal + verticalPairs * 2;
        int start = Math.floorMod((int) mix(seed, roomX, roomZ, salt), total);
        for (int offset = 0; offset < total; offset++) {
            int candidate = (start + offset) % total;
            int firstX;
            int firstZ;
            int secondX;
            int secondZ;
            Direction facing;
            if (candidate < horizontalPairs) {
                firstX = minX + 1 + candidate;
                firstZ = minZ;
                secondX = firstX + 1;
                secondZ = firstZ;
                facing = Direction.SOUTH;
            } else if (candidate < horizontalTotal) {
                int index = candidate - horizontalPairs;
                firstX = minX + 1 + index;
                firstZ = maxZ;
                secondX = firstX + 1;
                secondZ = firstZ;
                facing = Direction.NORTH;
            } else if (candidate < horizontalTotal + verticalPairs) {
                int index = candidate - horizontalTotal;
                firstX = minX;
                firstZ = minZ + 1 + index;
                secondX = firstX;
                secondZ = firstZ + 1;
                facing = Direction.EAST;
            } else {
                int index = candidate - horizontalTotal - verticalPairs;
                firstX = maxX;
                firstZ = minZ + 1 + index;
                secondX = firstX;
                secondZ = firstZ + 1;
                facing = Direction.WEST;
            }

            if (isFloorFoldProtectedRoute(seed, roomX, roomZ, firstX, firstZ)
                    || isFloorFoldProtectedRoute(seed, roomX, roomZ, secondX, secondZ)) {
                continue;
            }
            if ((localX == firstX && localZ == firstZ)
                    || (localX == secondX && localZ == secondZ)) {
                return facing;
            }
            return null;
        }
        return null;
    }

    private static boolean isFloorFoldProtectedRoute(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        int x = roomX * LEVEL_0_ROOM_SIZE + localX;
        int z = roomZ * LEVEL_0_ROOM_SIZE + localZ;
        return ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, true)
                || isReservedRoomPath(seed, LEVEL_0_ROOM_SIZE,
                        roomX, roomZ, localX, localZ, 1, false);
    }

    private static boolean isLevel1VentilationStackCell(
            long seed, int roomX, int roomZ, int localX, int localZ,
            int anchorX, int anchorZ) {
        int[][] offsets = {
                {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-2, 0}, {2, 0}, {0, -2}, {0, 2},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1},
                {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
                {-1, -2}, {1, -2}, {-1, 2}, {1, 2}
        };
        for (int[] offset : offsets) {
            int candidateX = anchorX + offset[0];
            int candidateZ = anchorZ + offset[1];
            if (candidateX < 1 || candidateX > LEVEL_1_ROOM_SIZE - 2
                    || candidateZ < 1 || candidateZ > LEVEL_1_ROOM_SIZE - 2) continue;
            int x = roomX * LEVEL_1_ROOM_SIZE + candidateX;
            int z = roomZ * LEVEL_1_ROOM_SIZE + candidateZ;
            boolean protectedTile = isLevel1ResourceFootprint(candidateX, candidateZ)
                    || ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, false)
                    || isReservedRoomPath(seed, LEVEL_1_ROOM_SIZE, roomX, roomZ,
                            candidateX, candidateZ, 2, true);
            boolean upperWalkway = hasLevel1UpperDeck(seed, roomX, roomZ)
                    && isLevel1UpperPath(seed, roomX, roomZ, candidateX, candidateZ);
            if (!protectedTile && !upperWalkway) {
                return localX == candidateX && localZ == candidateZ;
            }
        }
        return false;
    }

    private static boolean isLevel1ResourceFootprint(int localX, int localZ) {
        boolean planter = localX >= 13 && localX <= 19 && localZ >= 13 && localZ <= 19;
        boolean crate = (localX == 8 || localX == 24 || localX == 40)
                && (localZ == 8 || localZ == 24 || localZ == 40);
        return planter || crate;
    }

    private static boolean isLevel1MachineIsland(int localX, int localZ) {
        boolean west = localX >= 11 && localX <= 14 && localZ >= 11 && localZ <= 14;
        boolean east = localX >= 33 && localX <= 36 && localZ >= 11 && localZ <= 14;
        boolean southWest = localX >= 11 && localX <= 14 && localZ >= 33 && localZ <= 36;
        boolean southEast = localX >= 33 && localX <= 36 && localZ >= 33 && localZ <= 36;
        return west || east || southWest || southEast;
    }

    private static boolean isReservedRoomPath(
            long seed, int roomSize, int roomX, int roomZ,
            int localX, int localZ, int radius, boolean guaranteeRoomExit) {
        int mid = roomSize / 2;
        int westCenter = doorwayCenterAlongZ(seed, roomSize, roomX, roomZ);
        if (doorwayAlongZ(seed, roomSize, radius == 1 ? 1 : 2,
                roomX, roomZ, westCenter, guaranteeRoomExit, false)
                && (nearHorizontalSegment(localX, localZ, 0, mid, westCenter, radius)
                || nearVerticalSegment(localX, localZ, mid, westCenter, mid, radius))) return true;

        int eastCenter = doorwayCenterAlongZ(seed, roomSize, roomX + 1, roomZ);
        if (doorwayAlongZ(seed, roomSize, radius == 1 ? 1 : 2,
                roomX + 1, roomZ, eastCenter, guaranteeRoomExit, false)
                && (nearHorizontalSegment(localX, localZ, mid, roomSize - 1, eastCenter, radius)
                || nearVerticalSegment(localX, localZ, mid, eastCenter, mid, radius))) return true;

        int northCenter = doorwayCenterAlongX(seed, roomSize, roomX, roomZ);
        if (doorwayAlongX(seed, roomSize, radius == 1 ? 1 : 2,
                roomX, roomZ, northCenter, guaranteeRoomExit, false)
                && (nearVerticalSegment(localX, localZ, northCenter, 0, mid, radius)
                || nearHorizontalSegment(localX, localZ, northCenter, mid, mid, radius))) return true;

        int southCenter = doorwayCenterAlongX(seed, roomSize, roomX, roomZ + 1);
        if (doorwayAlongX(seed, roomSize, radius == 1 ? 1 : 2,
                roomX, roomZ + 1, southCenter, guaranteeRoomExit, false)
                && (nearVerticalSegment(localX, localZ, southCenter, mid, roomSize - 1, radius)
                || nearHorizontalSegment(localX, localZ, mid, southCenter, mid, radius))) return true;
        return false;
    }

    private static int doorwayCenterAlongZ(long seed, int roomSize, int boundaryX, int roomZ) {
        long edge = mix(seed, boundaryX, roomZ, 0xE451L);
        int margin = roomSize / 4;
        return margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
    }

    private static int doorwayCenterAlongX(long seed, int roomSize, int roomX, int boundaryZ) {
        long edge = mix(seed, roomX, boundaryZ, 0xE452L);
        int margin = roomSize / 4;
        return margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
    }

    private static boolean nearHorizontalSegment(
            int x, int z, int startX, int endX, int centerZ, int radius) {
        return x >= Math.min(startX, endX) - radius && x <= Math.max(startX, endX) + radius
                && Math.abs(z - centerZ) <= radius;
    }

    private static boolean nearVerticalSegment(
            int x, int z, int centerX, int startZ, int endZ, int radius) {
        return z >= Math.min(startZ, endZ) - radius && z <= Math.max(startZ, endZ) + radius
                && Math.abs(x - centerX) <= radius;
    }

    /** The first room always has an accessible wall; rare later ones vary by world seed. */
    private static boolean isTransitionWall(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        return localX == 0 && (localZ == 15 || localZ == 16)
                && ((roomX == 0 && roomZ == 0)
                || Math.floorMod(mix(seed, roomX, roomZ, 0xE181L), 73) == 0);
    }

    private static int level0Style(long seed, int roomX, int roomZ) {
        // The first room establishes the iconic empty yellow-office baseline.
        if (roomX == 0 && roomZ == 0) return 0;
        int roll = Math.floorMod((int) mix(seed, roomX, roomZ, 0x10A0L), 100);
        // Preserve the original yellow identity: grey and red anomalies are rare.
        if (roll < 82) return 0;
        if (roll < 94) return 1;
        if (roll < 99) return 2;
        return 3;
    }

    private static int level1Style(long seed, int roomX, int roomZ) {
        // The entry room is deliberately a stocked shelter, not a dead spawn.
        if (roomX == 0 && roomZ == 0) return 1;
        int roll = Math.floorMod((int) mix(seed, roomX, roomZ, 0x10A1L), 100);
        if (roll < 52) return 0;
        if (roll < 75) return 1;
        if (roll < 91) return 2;
        return 3;
    }

    private static boolean isBoundary(int localX, int localZ, int size) {
        return localX == 0 || localX == size - 1 || localZ == 0 || localZ == size - 1;
    }

    private static boolean isDoorway(
            long seed, int roomSize, int radius,
            int roomX, int roomZ, int localX, int localZ,
            boolean guaranteeRoomExit, boolean variableWidths) {
        if (localX == 0) {
            return doorwayAlongZ(seed, roomSize, radius, roomX, roomZ, localZ, guaranteeRoomExit, variableWidths);
        }
        if (localX == roomSize - 1) {
            return doorwayAlongZ(seed, roomSize, radius, roomX + 1, roomZ, localZ, guaranteeRoomExit, variableWidths);
        }
        if (localZ == 0) {
            return doorwayAlongX(seed, roomSize, radius, roomX, roomZ, localX, guaranteeRoomExit, variableWidths);
        }
        if (localZ == roomSize - 1) {
            return doorwayAlongX(seed, roomSize, radius, roomX, roomZ + 1, localX, guaranteeRoomExit, variableWidths);
        }
        return false;
    }

    private static boolean doorwayAlongZ(
            long seed, int roomSize, int radius,
            int boundaryX, int roomZ, int along, boolean guaranteeRoomExit,
            boolean variableWidths) {
        long edge = mix(seed, boundaryX, roomZ, 0xE451L);
        boolean primaryConnector = guaranteeRoomExit
                && (isPrimaryExit(seed, boundaryX - 1, roomZ, 1)
                || isPrimaryExit(seed, boundaryX, roomZ, 3));
        if (Math.floorMod(edge, 6) == 0 && !primaryConnector) return false;
        int margin = roomSize / 4;
        int center = margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
        int openingRadius = variableWidths ? variableDoorwayRadius(edge, radius, roomSize) : radius;
        return Math.abs(along - center) <= openingRadius;
    }

    private static boolean doorwayAlongX(
            long seed, int roomSize, int radius,
            int roomX, int boundaryZ, int along, boolean guaranteeRoomExit,
            boolean variableWidths) {
        long edge = mix(seed, roomX, boundaryZ, 0xE452L);
        boolean primaryConnector = guaranteeRoomExit
                && (isPrimaryExit(seed, roomX, boundaryZ - 1, 2)
                || isPrimaryExit(seed, roomX, boundaryZ, 0));
        if (Math.floorMod(edge, 6) == 0 && !primaryConnector) return false;
        int margin = roomSize / 4;
        int center = margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
        int openingRadius = variableWidths ? variableDoorwayRadius(edge, radius, roomSize) : radius;
        return Math.abs(along - center) <= openingRadius;
    }

    /**
     * Convert the deterministic edge hash into a real opening width. Level 0
     * favors 1/3/5-block breaks with occasional broad arches; Level 1 has
     * larger loading/service openings. Adjacent room faces and reserved-route
     * masks retain a conservative centerline; adjacent room faces use the
     * same edge hash, so chunk order cannot change the actual doorway.
     */
    private static int variableDoorwayRadius(long edge, int baseRadius, int roomSize) {
        int roll = (int) Math.floorMod(edge >>> 40, 100L);
        if (roomSize == LEVEL_0_ROOM_SIZE) {
            int selected = roll < 12 ? 0 : roll < 42 ? 1 : roll < 70 ? 2
                    : roll < 88 ? 3 : roll < 97 ? 4 : 5;
            return Math.max(Math.max(0, baseRadius - 1), selected);
        }
        int selected = roll < 10 ? 1 : roll < 34 ? 2 : roll < 60 ? 3
                : roll < 82 ? 4 : roll < 95 ? 5 : 6;
        return Math.max(Math.max(1, baseRadius - 1), selected);
    }

    /** Level 1 and Level 2 rooms choose deterministic exits, preventing sealed cells. */
    private static boolean isPrimaryExit(long seed, int roomX, int roomZ, int direction) {
        return Math.floorMod(mix(seed, roomX, roomZ, 0xE453L), 4) == direction;
    }

    private static boolean hasLevel0Divider(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        // Legacy room-aligned dividers are replaced by the irregular maze overlay.
        return false;
    }

    private static boolean hasLevel1Divider(
            long seed, int roomX, int roomZ, int localX, int localZ, int style) {
        // Level 1 uses offset warehouse aisles from ThresholdIrregularMazeLayout.
        return false;
    }

    private static boolean isPillar(int localX, int localZ, int roomSize) {
        int[] anchors = {7, 8, roomSize - 9, roomSize - 8};
        boolean xAnchor = false;
        boolean zAnchor = false;
        for (int anchor : anchors) {
            xAnchor |= localX == anchor;
            zAnchor |= localZ == anchor;
        }
        return xAnchor && zAnchor;
    }

    private static boolean isLevel0LightPosition(int localX, int localZ) {
        return (localX == 8 || localX == 24) && (localZ == 8 || localZ == 24);
    }

    private static boolean isLevel1Pillar(int localX, int localZ) {
        boolean xAnchor = localX == 10 || localX == 11 || localX == 36 || localX == 37;
        boolean zAnchor = localZ == 10 || localZ == 11 || localZ == 36 || localZ == 37;
        return xAnchor && zAnchor;
    }

    private static boolean isLevel1LightPosition(int localX, int localZ) {
        // Long suspended light panels create stable islands of safety.
        return (Math.abs(localX - 8) <= 1 || Math.abs(localX - 24) <= 1
                || Math.abs(localX - 40) <= 1)
                && (localZ == 8 || localZ == 24 || localZ == 40);
    }

    private static boolean isLevel1ReturnWall(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        return localX == 0 && (localZ == 23 || localZ == 24)
                && ((roomX == 0 && roomZ == 0)
                || Math.floorMod(mix(seed, roomX, roomZ, 0xE182L), 89) == 0);
    }

    private static boolean hasLevel1PlanterPatch(long seed, int roomX, int roomZ) {
        // The starter shelter always keeps its complete 5x5 planter. In other
        // rooms, do not place a resource patch across a guaranteed corridor:
        // either the whole patch exists intact or the room simply has no patch.
        if (roomX == 0 && roomZ == 0) return true;
        if (Math.floorMod(mix(seed, roomX, roomZ, 0xFA77L), 4) != 0) return false;
        for (int localX = 14; localX <= 18; localX++) {
            for (int localZ = 14; localZ <= 18; localZ++) {
                int x = roomX * LEVEL_1_ROOM_SIZE + localX;
                int z = roomZ * LEVEL_1_ROOM_SIZE + localZ;
                if (!ThresholdIrregularMazeLayout.isProtectedPassageOrEntry(x, z, false)
                        && ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isLevel1CratePosition(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        if (roomX == 0 && roomZ == 0 && localX == 8 && localZ == 8) return true;
        boolean xAnchor = localX == 8 || localX == 24 || localX == 40;
        boolean zAnchor = localZ == 8 || localZ == 24 || localZ == 40;
        if (!xAnchor || !zAnchor) return false;
        // One in three ordinary anchor positions; the entry cache remains guaranteed.
        return Math.floorMod(mix(
                seed, roomX * 3 + localX, roomZ * 3 + localZ, 0xC8A7L), 3) == 0;
    }

    private static long mix(long seed, int x, int z, long salt) {
        long value = seed ^ salt
                ^ ((long) x * 0x9E3779B97F4A7C15L)
                ^ ((long) z * 0xC2B2AE3D27D4EB4FL);
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    private static void placeLootBarrel(
            WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int y, int z, ResourceKey<LootTable> table, long lootSeed) {
        set(world, pos, x, y, z, Blocks.BARREL.defaultBlockState());
        pos.set(x, y, z);
        if (world.getBlockEntity(pos) instanceof RandomizableContainerBlockEntity container) {
            container.setLootTable(table);
            container.setLootTableSeed(lootSeed);
            container.setChanged();
        }
    }

    /**
     * Final post-decoration pass for the seed-stable ground-floor route.
     * It runs after architectural dressing and vertical features so no later
     * landmark can leave a wall, railing, prop or stair inside a guaranteed
     * two-block passage. Resource anchors and dimension entrances stay intact.
     */
    private static boolean isLevel1GeneratorCore(long seed, int x, int z) {
        int roomX = Math.floorDiv(x, LEVEL_1_ROOM_SIZE);
        int roomZ = Math.floorDiv(z, LEVEL_1_ROOM_SIZE);
        int localX = Math.floorMod(x, LEVEL_1_ROOM_SIZE);
        int localZ = Math.floorMod(z, LEVEL_1_ROOM_SIZE);
        return level1Architecture(seed, roomX, roomZ) == 26
                && (localX == 10 || localX == 38)
                && (localZ == 10 || localZ == 38);
    }

    private static void clearGuaranteedSpine(
            WorldGenLevel world, int chunkX, int chunkZ, long seed,
            BlockPos.MutableBlockPos pos, boolean level0) {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        int feetY = level0 ? 65 : 64;
        int headY = feetY + 1;
        BlockState floor = level0
                ? Blocks.GRAY_CONCRETE.defaultBlockState()
                : Blocks.SMOOTH_STONE.defaultBlockState();
        for (int dx = 0; dx < 16; dx++) {
            int x = minX + dx;
            for (int dz = 0; dz < 16; dz++) {
                int z = minZ + dz;
                if (!ThresholdIrregularMazeLayout.isGuaranteedCorridor(seed, x, z, level0)
                        || ThresholdIrregularMazeLayout.isProtectedPassageOrEntry(x, z, level0)
                        || ThresholdIrregularMazeLayout.isResourceAnchor(world, pos, x, z)
                        || (!level0 && isLevel1GeneratorCore(seed, x, z))) {
                    continue;
                }
                BlockState existingFloor = world.getBlockState(pos.set(x, FLOOR_Y, z));
                if (existingFloor.isAir()) {
                    set(world, pos, x, FLOOR_Y, z, floor);
                }
                if (level0) {
                    BlockState carpetOrStep = world.getBlockState(pos.set(x, LEVEL_0_CARPET_Y, z));
                    if (!(carpetOrStep.getBlock() instanceof StairBlock)) {
                        set(world, pos, x, LEVEL_0_CARPET_Y, z,
                                Blocks.YELLOW_CARPET.defaultBlockState());
                    }
                }
                BlockState feet = world.getBlockState(pos.set(x, feetY, z));
                if (!(feet.getBlock() instanceof StairBlock)) {
                    set(world, pos, x, feetY, z, Blocks.AIR.defaultBlockState());
                }
                BlockState head = world.getBlockState(pos.set(x, headY, z));
                if (!(head.getBlock() instanceof StairBlock)) {
                    set(world, pos, x, headY, z, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static void set(
            WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int y, int z, BlockState state) {
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos)) {
            world.setBlock(pos, state, 2);
        }
    }
}
