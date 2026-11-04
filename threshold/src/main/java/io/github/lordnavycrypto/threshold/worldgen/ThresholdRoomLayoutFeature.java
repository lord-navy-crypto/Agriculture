package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.CropBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Builds a deterministic 16x16 chunk slice of Level 0 or Level 1.
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

    private static final int LEVEL_0_ROOM_SIZE = 32;
    private static final int LEVEL_0_CARPET_Y = 64;
    private static final int LEVEL_0_WALL_BOTTOM = 65;
    private static final int LEVEL_0_WALL_TOP = 71;
    private static final int LEVEL_0_CEILING_Y = 72;

    private static final int LEVEL_1_ROOM_SIZE = 48;
    private static final int LEVEL_1_WALL_BOTTOM = 64;
    private static final int LEVEL_1_WALL_TOP = 73;
    private static final int LEVEL_1_CEILING_Y = 74;

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
                        seed, LEVEL_0_ROOM_SIZE, 1, roomX, roomZ, localX, localZ, false);
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
                        && localX == 6 && localZ == 6
                        && Math.floorMod(mix(seed, roomX, roomZ, 0xCAFE0L), 11) == 0) {
                    // Replace the carpet under the barrel; no floating prop.
                    placeLootBarrel(world, pos, x, LEVEL_0_CARPET_Y, z,
                            FORGOTTEN_LOOT, mix(seed, x, z, 0xF00DL));
                }
            }
        }
        buildLevel0ArchitecturalDetails(world, chunkX, chunkZ, seed, pos);
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
                int style = level1Style(seed, roomX, roomZ);

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
                        seed, LEVEL_1_ROOM_SIZE, 2, roomX, roomZ, localX, localZ, true);
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

                if (style == 1 && isLevel1CratePosition(seed, roomX, roomZ, localX, localZ)) {
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
            }
        }
        buildLevel1ArchitecturalDetails(world, chunkX, chunkZ, seed, pos);
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
                boolean protectedRoute = isReservedRoomPath(
                        seed, LEVEL_0_ROOM_SIZE, roomX, roomZ, localX, localZ, 1, false);

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
                        // Phantom/echo office: the cubicle grammar is mirrored
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
                boolean protectedRoute = isReservedRoomPath(
                        seed, LEVEL_1_ROOM_SIZE, roomX, roomZ, localX, localZ, 2, true);
                boolean protectedResource = isLevel1ResourceFootprint(localX, localZ);
                boolean protectedTile = protectedRoute || protectedResource;

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
                    default -> { }
                }
            }
        }
    }

    private static int level0Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0;
        return Math.floorMod((int) mix(seed, roomX, roomZ, 0xA2C0L), 6);
    }

    private static int level1Architecture(long seed, int roomX, int roomZ) {
        if (roomX == 0 && roomZ == 0) return 0;
        return 1 + Math.floorMod((int) mix(seed, roomX, roomZ, 0xA2C1L), 6);
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
                roomX, roomZ, westCenter, guaranteeRoomExit)
                && (nearHorizontalSegment(localX, localZ, 0, mid, westCenter, radius)
                || nearVerticalSegment(localX, localZ, mid, westCenter, mid, radius))) return true;

        int eastCenter = doorwayCenterAlongZ(seed, roomSize, roomX + 1, roomZ);
        if (doorwayAlongZ(seed, roomSize, radius == 1 ? 1 : 2,
                roomX + 1, roomZ, eastCenter, guaranteeRoomExit)
                && (nearHorizontalSegment(localX, localZ, mid, roomSize - 1, eastCenter, radius)
                || nearVerticalSegment(localX, localZ, mid, eastCenter, mid, radius))) return true;

        int northCenter = doorwayCenterAlongX(seed, roomSize, roomX, roomZ);
        if (doorwayAlongX(seed, roomSize, radius == 1 ? 1 : 2,
                roomX, roomZ, northCenter, guaranteeRoomExit)
                && (nearVerticalSegment(localX, localZ, northCenter, 0, mid, radius)
                || nearHorizontalSegment(localX, localZ, northCenter, mid, mid, radius))) return true;

        int southCenter = doorwayCenterAlongX(seed, roomSize, roomX, roomZ + 1);
        if (doorwayAlongX(seed, roomSize, radius == 1 ? 1 : 2,
                roomX, roomZ + 1, southCenter, guaranteeRoomExit)
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
        if (roll < 68) return 0;
        if (roll < 82) return 1;
        if (roll < 94) return 2;
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
            int roomX, int roomZ, int localX, int localZ, boolean guaranteeRoomExit) {
        if (localX == 0) {
            return doorwayAlongZ(seed, roomSize, radius, roomX, roomZ, localZ, guaranteeRoomExit);
        }
        if (localX == roomSize - 1) {
            return doorwayAlongZ(seed, roomSize, radius, roomX + 1, roomZ, localZ, guaranteeRoomExit);
        }
        if (localZ == 0) {
            return doorwayAlongX(seed, roomSize, radius, roomX, roomZ, localX, guaranteeRoomExit);
        }
        if (localZ == roomSize - 1) {
            return doorwayAlongX(seed, roomSize, radius, roomX, roomZ + 1, localX, guaranteeRoomExit);
        }
        return false;
    }

    private static boolean doorwayAlongZ(
            long seed, int roomSize, int radius,
            int boundaryX, int roomZ, int along, boolean guaranteeRoomExit) {
        long edge = mix(seed, boundaryX, roomZ, 0xE451L);
        boolean primaryConnector = guaranteeRoomExit
                && (isPrimaryExit(seed, boundaryX - 1, roomZ, 1)
                || isPrimaryExit(seed, boundaryX, roomZ, 3));
        if (Math.floorMod(edge, 6) == 0 && !primaryConnector) return false;
        int margin = roomSize / 4;
        int center = margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
        return Math.abs(along - center) <= radius;
    }

    private static boolean doorwayAlongX(
            long seed, int roomSize, int radius,
            int roomX, int boundaryZ, int along, boolean guaranteeRoomExit) {
        long edge = mix(seed, roomX, boundaryZ, 0xE452L);
        boolean primaryConnector = guaranteeRoomExit
                && (isPrimaryExit(seed, roomX, boundaryZ - 1, 2)
                || isPrimaryExit(seed, roomX, boundaryZ, 0));
        if (Math.floorMod(edge, 6) == 0 && !primaryConnector) return false;
        int margin = roomSize / 4;
        int center = margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
        return Math.abs(along - center) <= radius;
    }

    /** Each Level 1 room chooses one deterministic exit, preventing sealed cells. */
    private static boolean isPrimaryExit(long seed, int roomX, int roomZ, int direction) {
        return Math.floorMod(mix(seed, roomX, roomZ, 0xE453L), 4) == direction;
    }

    private static boolean hasLevel0Divider(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        long hash = mix(seed, roomX, roomZ, 0xD1A0L);
        if (Math.floorMod(hash, 4) != 0) return false;
        return (hash & 1L) == 0L
                ? localX == 16 && (localZ < 13 || localZ > 18)
                : localZ == 16 && (localX < 13 || localX > 18);
    }

    private static boolean hasLevel1Divider(
            long seed, int roomX, int roomZ, int localX, int localZ, int style) {
        if (style != 2) return false;
        long hash = mix(seed, roomX, roomZ, 0xD1A1L);
        return (hash & 1L) == 0L
                ? localX == 24 && (localZ < 21 || localZ > 27)
                : localZ == 24 && (localX < 21 || localX > 27);
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
        return (roomX == 0 && roomZ == 0)
                || Math.floorMod(mix(seed, roomX, roomZ, 0xFA77L), 4) == 0;
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

    private static void set(
            WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int y, int z, BlockState state) {
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos)) {
            world.setBlock(pos, state, 2);
        }
    }
}
