package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * V4.13 deep architecture pass for Backrooms Levels 2-5.
 *
 * This is a genuine spatial pass, not a palette pass: it subdivides large
 * rooms into irregular subrooms, adds multi-height structural frames and
 * equipment bays, and gives selected rooms an accessible two-wide mezzanine.
 * It follows the Level 0/1 pattern of seed-stable room archetypes, route-aware
 * partitions, environmental dressing and a connected vertical network.
 *
 * All writes are chunk-local and air-only. The existing floor at Y=63,
 * ceiling at Y=71, doorways, transit blocks, containers, water, previous
 * landmark rooms and Level 0/1 generators remain untouched.
 */
public final class ThresholdDeepLevelDepthV413 {
    private static final int FLOOR_Y = 63;
    private static final int CEILING_Y = 71;
    private static final int DECK_Y = 67;

    private ThresholdDeepLevelDepthV413() {}

    public static void build(WorldGenLevel world, int chunkX, int chunkZ, int level) {
        if (level < 2 || level > 5) {
            throw new IllegalArgumentException("Deep architecture supports Levels 2-5 only");
        }

        final int roomSize = level == 4 ? 48 : 32;
        final int minX = chunkX << 4;
        final int minZ = chunkZ << 4;
        final int roomX = Math.floorDiv(minX, roomSize);
        final int roomZ = Math.floorDiv(minZ, roomSize);
        final long seed = world.getSeed();
        final boolean entry = roomX == 0 && roomZ == 0;
        final boolean authoredLandmark = isAuthoredLandmark(level, roomX, roomZ);
        final int module = roomModule(seed, level, roomX, roomZ);
        final int splitX = roomSize / 2 + jitter(seed, roomX, roomZ, level, 0x4131L);
        final int splitZ = roomSize / 2 + jitter(seed, roomX, roomZ, level, 0x4132L);
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            final int x = minX + dx;
            final int localX = Math.floorMod(x, roomSize);
            for (int dz = 0; dz < 16; dz++) {
                final int z = minZ + dz;
                final int localZ = Math.floorMod(z, roomSize);
                final boolean protectedGround = protectedGround(seed, roomSize,
                        roomX, roomZ, localX, localZ);

                // Preserve the original entry rooms and authored landmark
                // geometry; the ordinary room field receives the new grammar.
                if (!entry && !authoredLandmark) {
                    if (!protectedGround) {
                        if (isPartitionX(localX, localZ, splitX, roomSize, seed,
                                roomX, roomZ, level)) {
                            placePartitionColumn(world, pos, x, z, minX, minZ, level, module);
                        }
                        if (isPartitionZ(localX, localZ, splitZ, roomSize, seed,
                                roomX, roomZ, level)) {
                            placePartitionColumn(world, pos, x, z, minX, minZ, level, module);
                        }
                        if (isReturnPartition(localX, localZ, splitX, splitZ,
                                roomSize, module, seed, roomX, roomZ, level)
                                && !protectedGround) {
                            placePartitionColumn(world, pos, x, z, minX, minZ, level, module);
                        }
                        if (isStructuralPier(localX, localZ, roomSize)) {
                            placePier(world, pos, x, z, minX, minZ, level);
                        }
                        placeEquipmentAndFurniture(world, pos, x, z, minX, minZ,
                                localX, localZ, roomSize, level, module);
                    }

                    // A second architectural layer above normal head height:
                    // offset trusses, service ribs and room-specific fixtures.
                    if ((localX == splitX || localZ == splitZ)
                            && Math.floorMod(localX + localZ + module, 4) == 0) {
                        putAir(world, pos, x, 69, z, minX, minZ, beamBlock(level));
                    }
                    if (Math.floorMod(localX + module * 3, 12) == 0
                            && Math.floorMod(localZ + module * 5, 12) == 0) {
                        putAir(world, pos, x, 70, z, minX, minZ, lightBlock(level, module));
                    }
                }
            }
        }

        if (!entry && shouldHaveMezzanine(seed, level, roomX, roomZ)
                && isSelectedMezzanineChunk(seed, level, roomSize, roomX, roomZ, chunkX, chunkZ)) {
            DeckPlan plan = findDeckPlan(world, pos, seed, level, roomSize,
                    roomX, roomZ, minX, minZ);
            if (plan != null) {
                buildMezzanine(world, pos, plan, seed, roomSize,
                        roomX, roomZ, minX, minZ, level);
            }
        }
    }

    /**
     * Stable first-ring modules ensure the player can encounter different
     * spatial grammars without depending on a lucky random room roll.
     */
    private static int roomModule(long seed, int level, int roomX, int roomZ) {
        if (level == 2) {
            if (roomX == 1 && roomZ == 0) return 0; // Pressure-pump bays
            if (roomX == 0 && roomZ == 1) return 1; // Filter and sump chambers
            if (roomX == 1 && roomZ == 1) return 2; // Offset pipe galleries
            if (roomX == 2 && roomZ == 0) return 3; // Maintenance workshop
        } else if (level == 3) {
            if (roomX == 1 && roomZ == 0) return 0; // Transformer bays
            if (roomX == 0 && roomZ == 1) return 1; // Switchgear partitions
            if (roomX == 1 && roomZ == 1) return 2; // Cable gantry rooms
            if (roomX == 2 && roomZ == 0) return 3; // Relay control annex
        } else if (level == 4) {
            if (roomX == 1 && roomZ == 0) return 0; // Records storage
            if (roomX == 0 && roomZ == 1) return 1; // Cubicle labyrinth
            if (roomX == 2 && roomZ == 0) return 2; // Meeting suite
            if (roomX == 2 && roomZ == 1) return 3; // Reception and archive wing
        } else {
            if (roomX == 1 && roomZ == 1) return 0; // Guest-suite annex
            if (roomX == 2 && roomZ == 1) return 1; // Lounge and service wing
            if (roomX == 0 && roomZ == 2) return 2; // Boiler support rooms
            if (roomX == 1 && roomZ == 2) return 3; // Staff corridor
        }
        return Math.floorMod((int) mix(seed, roomX, roomZ, 0x4130L + level * 31L), 4);
    }

    private static boolean isAuthoredLandmark(int level, int x, int z) {
        if (level == 2) return x == 2 && z == 1;
        if (level == 3) return x == 1 && z == 2;
        if (level == 4) return x == 1 && z == 1;
        // The hotel Main Hall, Beverly ballroom, Boiler Room and v4.12
        // guest-wing overlook retain their authored room geometry.
        return (x == 0 && z == 0) || (x == 1 && z == 0)
                || (x == 0 && z == 1) || (x == 2 && z == 0);
    }

    private static boolean protectedGround(long seed, int size,
            int roomX, int roomZ, int localX, int localZ) {
        return ThresholdRoomLayoutFeature.isReservedRoomPath(
                seed, size, roomX, roomZ, localX, localZ, 2, true);
    }

    private static int jitter(long seed, int roomX, int roomZ, int level, long salt) {
        return Math.floorMod((int) mix(seed, roomX, roomZ, salt + level * 101L), 7) - 3;
    }

    private static boolean isPartitionX(int lx, int lz, int splitX, int size,
            long seed, int roomX, int roomZ, int level) {
        return lx == splitX && lz >= 5 && lz <= size - 6
                && !isDoorGap(lz, size, seed, roomX, roomZ, level, 0x4133L);
    }

    private static boolean isPartitionZ(int lx, int lz, int splitZ, int size,
            long seed, int roomX, int roomZ, int level) {
        return lz == splitZ && lx >= 5 && lx <= size - 6
                && !isDoorGap(lx, size, seed, roomX, roomZ, level, 0x4134L);
    }

    private static boolean isDoorGap(int along, int size, long seed,
            int roomX, int roomZ, int level, long salt) {
        int shift = Math.floorMod((int) mix(seed, roomX, roomZ, salt + level * 17L), 5) - 2;
        int first = size / 3 + shift;
        int second = (size * 2) / 3 - shift;
        return Math.abs(along - first) <= 1 || Math.abs(along - second) <= 1;
    }

    /** Short return walls create L-shaped alcoves instead of a perfect grid. */
    private static boolean isReturnPartition(int lx, int lz, int splitX, int splitZ,
            int size, int module, long seed, int roomX, int roomZ, int level) {
        int returnX = Math.max(5, Math.min(size - 6, splitX + (module % 2 == 0 ? 3 : -3)));
        int start = size / 4 + Math.floorMod((int) mix(seed, roomX, roomZ, 0x4135L), 4);
        int end = Math.min(size - 6, start + size / 5);
        boolean returnA = lx == returnX && lz >= start && lz <= end
                && !isDoorGap(lz, size, seed, roomX, roomZ, level, 0x4136L);
        int returnZ = Math.max(5, Math.min(size - 6, splitZ + (module % 2 == 0 ? -3 : 3)));
        int xStart = size / 4 + Math.floorMod((int) mix(seed, roomX, roomZ, 0x4137L), 4);
        int xEnd = Math.min(size - 6, xStart + size / 5);
        boolean returnB = lz == returnZ && lx >= xStart && lx <= xEnd
                && !isDoorGap(lx, size, seed, roomX, roomZ, level, 0x4138L);
        return returnA || returnB;
    }

    private static void placePartitionColumn(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int z, int minX, int minZ, int level, int module) {
        for (int y = 64; y <= 68; y++) {
            if (!airAt(world, pos, x, y, z, minX, minZ)) return;
        }
        putAir(world, pos, x, 64, z, minX, minZ, partitionBase(level, module));
        for (int y = 65; y <= 67; y++)
            putAir(world, pos, x, y, z, minX, minZ, partitionWindow(level));
        putAir(world, pos, x, 68, z, minX, minZ, partitionTop(level));
    }

    private static BlockState partitionBase(int level, int module) {
        return switch (level) {
            case 2 -> module == 1 ? Blocks.COPPER_BLOCK.defaultBlockState()
                    : Blocks.DEEPSLATE_BRICKS.defaultBlockState();
            case 3 -> module == 1 ? Blocks.POLISHED_DEEPSLATE.defaultBlockState()
                    : Blocks.DEEPSLATE_BRICKS.defaultBlockState();
            case 4 -> module == 1 ? Blocks.WHITE_CONCRETE.defaultBlockState()
                    : Blocks.SMOOTH_STONE.defaultBlockState();
            default -> module == 1 ? Blocks.RED_NETHER_BRICKS.defaultBlockState()
                    : Blocks.DARK_OAK_PLANKS.defaultBlockState();
        };
    }

    private static BlockState partitionWindow(int level) {
        return switch (level) {
            case 2 -> Blocks.IRON_BARS.defaultBlockState();
            case 3 -> Blocks.BLACK_STAINED_GLASS.defaultBlockState();
            case 4 -> Blocks.WHITE_STAINED_GLASS.defaultBlockState();
            default -> Blocks.RED_STAINED_GLASS.defaultBlockState();
        };
    }

    private static BlockState partitionTop(int level) {
        return switch (level) {
            case 2 -> Blocks.IRON_BARS.defaultBlockState();
            case 3 -> Blocks.IRON_BARS.defaultBlockState();
            case 4 -> Blocks.GLASS_PANE.defaultBlockState();
            default -> Blocks.DARK_OAK_FENCE.defaultBlockState();
        };
    }

    private static boolean isStructuralPier(int lx, int lz, int size) {
        return (lx == 5 || lx == size - 6)
                && (lz == 5 || lz == size - 6);
    }

    private static void placePier(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int z, int minX, int minZ, int level) {
        for (int y = 64; y <= 69; y++) {
            BlockState state = y == 69 ? beamBlock(level) : pierBlock(level);
            putAir(world, pos, x, y, z, minX, minZ, state);
        }
    }

    private static BlockState pierBlock(int level) {
        return switch (level) {
            case 2 -> Blocks.POLISHED_ANDESITE.defaultBlockState();
            case 3 -> Blocks.POLISHED_DEEPSLATE.defaultBlockState();
            case 4 -> Blocks.POLISHED_DIORITE.defaultBlockState();
            default -> Blocks.DARK_OAK_LOG.defaultBlockState();
        };
    }

    private static BlockState beamBlock(int level) {
        return switch (level) {
            case 2 -> Blocks.COPPER_BLOCK.defaultBlockState();
            case 3 -> Blocks.IRON_BLOCK.defaultBlockState();
            case 4 -> Blocks.WHITE_CONCRETE.defaultBlockState();
            default -> Blocks.DARK_OAK_PLANKS.defaultBlockState();
        };
    }

    private static BlockState lightBlock(int level, int module) {
        if (level == 3 && module % 2 == 0) return Blocks.REDSTONE_LAMP.defaultBlockState();
        return Blocks.SEA_LANTERN.defaultBlockState();
    }

    private static void placeEquipmentAndFurniture(WorldGenLevel world,
            BlockPos.MutableBlockPos pos, int x, int z, int minX, int minZ,
            int lx, int lz, int size, int level, int module) {
        boolean anchor = (lx == 8 || lx == size - 9)
                && (lz == 8 || lz == size - 9);
        if (anchor) {
            if (level == 2) {
                putAir(world, pos, x, 64, z, minX, minZ, Blocks.IRON_BLOCK.defaultBlockState());
                putAir(world, pos, x, 65, z, minX, minZ, Blocks.COPPER_BLOCK.defaultBlockState());
                putAir(world, pos, x, 66, z, minX, minZ, Blocks.PISTON.defaultBlockState());
                if (module % 2 == 0)
                    putAir(world, pos, x, 68, z, minX, minZ, Blocks.CHAIN.defaultBlockState());
            } else if (level == 3) {
                putAir(world, pos, x, 64, z, minX, minZ, Blocks.IRON_BLOCK.defaultBlockState());
                putAir(world, pos, x, 65, z, minX, minZ, Blocks.COPPER_BLOCK.defaultBlockState());
                putAir(world, pos, x, 66, z, minX, minZ, Blocks.REDSTONE_LAMP.defaultBlockState());
                if (module % 2 == 1)
                    putAir(world, pos, x, 68, z, minX, minZ, Blocks.IRON_BARS.defaultBlockState());
            } else if (level == 5) {
                putAir(world, pos, x, 64, z, minX, minZ, Blocks.DARK_OAK_LOG.defaultBlockState());
                putAir(world, pos, x, 65, z, minX, minZ, Blocks.DARK_OAK_PLANKS.defaultBlockState());
                putAir(world, pos, x, 66, z, minX, minZ, Blocks.GOLD_BLOCK.defaultBlockState());
            }
        }

        if (level == 2 && !anchor && (lx == 10 || lx == size - 11)
                && (lz == 10 || lz == size - 11)) {
            putAir(world, pos, x, 64, z, minX, minZ, Blocks.IRON_TRAPDOOR.defaultBlockState());
            putAir(world, pos, x, 68, z, minX, minZ, Blocks.CHAIN.defaultBlockState());
        } else if (level == 3 && (lx == 7 || lx == size - 8)
                && Math.floorMod(lz + module, 7) == 0) {
            putAir(world, pos, x, 69, z, minX, minZ, Blocks.COPPER_BLOCK.defaultBlockState());
        } else if (level == 4) {
            if ((lx == 7 || lx == size - 8) && Math.floorMod(lz + module, 6) <= 2) {
                putAir(world, pos, x, 64, z, minX, minZ, Blocks.BOOKSHELF.defaultBlockState());
                putAir(world, pos, x, 65, z, minX, minZ, Blocks.BOOKSHELF.defaultBlockState());
            }
            if (Math.floorMod(lx + module * 3, 13) == 0
                    && Math.floorMod(lz + module * 5, 11) == 0) {
                putAir(world, pos, x, 64, z, minX, minZ, Blocks.OAK_SLAB.defaultBlockState());
            }
        } else if (level == 5 && !anchor
                && (lx == 10 || lx == size - 11) && Math.floorMod(lz + module, 8) < 3) {
            putAir(world, pos, x, 64, z, minX, minZ, Blocks.RED_CARPET.defaultBlockState());
            if (Math.floorMod(lz, 2) == 0)
                putAir(world, pos, x, 65, z, minX, minZ, Blocks.BOOKSHELF.defaultBlockState());
        }
    }

    private static boolean shouldHaveMezzanine(long seed, int level, int roomX, int roomZ) {
        if (isAuthoredLandmark(level, roomX, roomZ)
                || (roomX == 0 && roomZ == 0)) return false;
        if (level == 2 && roomX == 1 && roomZ == 0) return true;
        if (level == 3 && roomX == 1 && roomZ == 0) return true;
        if (level == 4 && roomX == 2 && roomZ == 0) return true;
        if (level == 5 && roomX == 1 && roomZ == 1) return true;
        return Math.floorMod(mix(seed, roomX, roomZ, 0x4139L + level * 41L), 4) == 0;
    }

    private static boolean isSelectedMezzanineChunk(long seed, int level, int roomSize,
            int roomX, int roomZ, int chunkX, int chunkZ) {
        int span = roomSize / 16;
        int selected = Math.floorMod((int) mix(seed, roomX, roomZ, 0x4140L + level), span * span);
        int current = Math.floorMod(chunkX, span)
                + span * Math.floorMod(chunkZ, span);
        return current == selected;
    }

    private static DeckPlan findDeckPlan(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            long seed, int level, int roomSize, int roomX, int roomZ, int minX, int minZ) {
        long hash = mix(seed, roomX, roomZ, 0x4141L + level * 53L);
        for (int attempt = 0; attempt < 128; attempt++) {
            int orientation = (int) Math.floorMod(hash + attempt * 13L, 4L);
            int cross = 2 + (int) Math.floorMod((hash >>> 12) + attempt * 7L, 11L);
            int axis = 2 + (int) Math.floorMod((hash >>> 24) + attempt * 5L, 3L);
            DeckPlan candidate = switch (orientation) {
                case 0 -> new DeckPlan(cross, axis, Direction.SOUTH, 0, 1);
                case 1 -> new DeckPlan(cross, 13 - (axis - 2), Direction.NORTH, 0, -1);
                case 2 -> new DeckPlan(axis, cross, Direction.EAST, 1, 0);
                default -> new DeckPlan(13 - (axis - 2), cross, Direction.WEST, -1, 0);
            };
            if (canBuildDeck(world, pos, candidate, roomSize, roomX, roomZ, minX, minZ, seed)) {
                return candidate;
            }
        }
        return null;
    }

    private static boolean canBuildDeck(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            DeckPlan plan, int roomSize, int roomX, int roomZ,
            int minX, int minZ, long seed) {
        for (int t = 0; t <= 3; t++) {
            int y = 64 + t;
            for (int w = 0; w < 2; w++) {
                int x = plan.x() + plan.dx() * t + plan.wx() * w;
                int z = plan.z() + plan.dz() * t + plan.wz() * w;
                int worldX = minX + x;
                int worldZ = minZ + z;
                int localX = Math.floorMod(worldX, roomSize);
                int localZ = Math.floorMod(worldZ, roomSize);
                if (protectedGround(seed, roomSize, roomX, roomZ, localX, localZ)) return false;
                if (!airAt(world, pos, worldX, y, worldZ, minX, minZ)
                        || !airAt(world, pos, worldX, y + 1, worldZ, minX, minZ)
                        || !airAt(world, pos, worldX, y + 2, worldZ, minX, minZ)) return false;
                if (t > 0 && !airAt(world, pos, worldX, y - 1, worldZ, minX, minZ)) return false;
            }
        }
        for (int t = 4; t <= 9; t++) {
            for (int w = 0; w < 2; w++) {
                int x = minX + plan.x() + plan.dx() * t + plan.wx() * w;
                int z = minZ + plan.z() + plan.dz() * t + plan.wz() * w;
                for (int y = DECK_Y; y <= 70; y++) {
                    if (!airAt(world, pos, x, y, z, minX, minZ)) return false;
                }
            }
            for (int side : new int[]{-1, 2}) {
                int x = minX + plan.x() + plan.dx() * t + plan.wx() * side;
                int z = minZ + plan.z() + plan.dz() * t + plan.wz() * side;
                if (!airAt(world, pos, x, 67, z, minX, minZ)
                        || !airAt(world, pos, x, 68, z, minX, minZ)) return false;
                if (t == 5 || t == 8) {
                    int localX = Math.floorMod(x, roomSize);
                    int localZ = Math.floorMod(z, roomSize);
                    if (protectedGround(seed, roomSize, roomX, roomZ, localX, localZ)) return false;
                    for (int y = 64; y <= 66; y++)
                        if (!airAt(world, pos, x, y, z, minX, minZ)) return false;
                }
            }
            if (t == 5 || t == 8) {
                for (int w = 0; w < 2; w++) {
                    int x = minX + plan.x() + plan.dx() * t + plan.wx() * w;
                    int z = minZ + plan.z() + plan.dz() * t + plan.wz() * w;
                    for (int y = 64; y <= 66; y++)
                        if (!airAt(world, pos, x, y, z, minX, minZ)) return false;
                }
            }
        }
        return true;
    }

    private static void buildMezzanine(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            DeckPlan plan, long seed, int roomSize, int roomX, int roomZ,
            int minX, int minZ, int level) {
        BlockState stairs = stairBlock(level).setValue(StairBlock.FACING, plan.facing());
        BlockState deck = deckBlock(level);
        BlockState rail = railBlock(level);
        BlockState support = pierBlock(level);

        for (int t = 0; t <= 3; t++) {
            for (int w = 0; w < 2; w++) {
                int x = minX + plan.x() + plan.dx() * t + plan.wx() * w;
                int z = minZ + plan.z() + plan.dz() * t + plan.wz() * w;
                // Solid risers keep every stair physically supported.
                if (t > 0) putAir(world, pos, x, 63 + t, z, minX, minZ, support);
                putAir(world, pos, x, 64 + t, z, minX, minZ, stairs);
            }
        }
        for (int t = 4; t <= 9; t++) {
            for (int w = 0; w < 2; w++) {
                int x = minX + plan.x() + plan.dx() * t + plan.wx() * w;
                int z = minZ + plan.z() + plan.dz() * t + plan.wz() * w;
                putAir(world, pos, x, DECK_Y, z, minX, minZ, deck);
                if (t == 5 || t == 8) {
                    for (int y = 64; y <= 66; y++)
                        putAir(world, pos, x, y, z, minX, minZ, support);
                }
            }
            // Solid side curbs support rails and make the narrow catwalk safe.
            for (int side : new int[]{-1, 2}) {
                int x = minX + plan.x() + plan.dx() * t + plan.wx() * side;
                int z = minZ + plan.z() + plan.dz() * t + plan.wz() * side;
                putAir(world, pos, x, DECK_Y, z, minX, minZ, deck);
                putAir(world, pos, x, 68, z, minX, minZ, rail);
                if (t == 5 || t == 8) {
                    for (int y = 64; y <= 66; y++)
                        putAir(world, pos, x, y, z, minX, minZ, support);
                }
            }
        }
        for (int w = 0; w < 2; w++) {
            int x = minX + plan.x() + plan.dx() * 9 + plan.wx() * w;
            int z = minZ + plan.z() + plan.dz() * 9 + plan.wz() * w;
            putAir(world, pos, x, 68, z, minX, minZ, rail);
        }
    }

    private static BlockState stairBlock(int level) {
        return switch (level) {
            case 2 -> Blocks.STONE_BRICK_STAIRS.defaultBlockState();
            case 3 -> Blocks.POLISHED_DEEPSLATE_STAIRS.defaultBlockState();
            case 4 -> Blocks.POLISHED_DIORITE_STAIRS.defaultBlockState();
            default -> Blocks.SPRUCE_STAIRS.defaultBlockState();
        };
    }

    private static BlockState deckBlock(int level) {
        return switch (level) {
            case 2 -> Blocks.IRON_BLOCK.defaultBlockState();
            case 3 -> Blocks.POLISHED_DEEPSLATE.defaultBlockState();
            case 4 -> Blocks.SMOOTH_STONE.defaultBlockState();
            default -> Blocks.DARK_OAK_PLANKS.defaultBlockState();
        };
    }

    private static BlockState railBlock(int level) {
        return switch (level) {
            case 2, 3 -> Blocks.IRON_BARS.defaultBlockState();
            case 4 -> Blocks.WHITE_STAINED_GLASS_PANE.defaultBlockState();
            default -> Blocks.DARK_OAK_FENCE.defaultBlockState();
        };
    }

    private static boolean airAt(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int y, int z, int minX, int minZ) {
        if (!inChunk(x, z, minX, minZ) || y < 0 || y >= world.getMaxBuildHeight()) return false;
        pos.set(x, y, z);
        return world.ensureCanWrite(pos) && world.getBlockState(pos).isAir();
    }

    private static void putAir(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int y, int z, int minX, int minZ, BlockState state) {
        if (y <= FLOOR_Y || y >= CEILING_Y || !inChunk(x, z, minX, minZ)) return;
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos) && world.getBlockState(pos).isAir()) {
            world.setBlock(pos, state, 2);
        }
    }

    private static boolean inChunk(int x, int z, int minX, int minZ) {
        return x >= minX && x < minX + 16 && z >= minZ && z < minZ + 16;
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

    private record DeckPlan(int x, int z, Direction facing, int dx, int dz) {
        private int wx() { return dx == 0 ? 1 : 0; }
        private int wz() { return dz == 0 ? 1 : 0; }
    }
}
