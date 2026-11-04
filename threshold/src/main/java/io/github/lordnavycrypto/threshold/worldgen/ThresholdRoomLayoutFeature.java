package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
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
                BlockState wall = switch (style) {
                    case 2 -> Blocks.GRAY_CONCRETE.defaultBlockState();
                    case 3 -> Blocks.RED_TERRACOTTA.defaultBlockState();
                    default -> Blocks.YELLOW_TERRACOTTA.defaultBlockState();
                };

                set(world, pos, x, FLOOR_Y, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                set(world, pos, x, LEVEL_0_CARPET_Y, z, carpet);

                boolean boundary = isBoundary(localX, localZ, LEVEL_0_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_0_ROOM_SIZE, 1, roomX, roomZ, localX, localZ);

                for (int y = LEVEL_0_WALL_BOTTOM; y <= LEVEL_0_WALL_TOP; y++) {
                    if (boundary && !(y <= 69 && doorway)) {
                        set(world, pos, x, y, z, wall);
                    } else if (!boundary && hasLevel0Divider(seed, roomX, roomZ, localX, localZ)
                            && y <= 68) {
                        set(world, pos, x, y, z, Blocks.YELLOW_TERRACOTTA.defaultBlockState());
                    } else if (!boundary && style == 1 && isPillar(localX, localZ, LEVEL_0_ROOM_SIZE)) {
                        set(world, pos, x, y, z, Blocks.YELLOW_CONCRETE.defaultBlockState());
                    }
                }

                BlockState ceiling = style == 2
                        ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                        : Blocks.WHITE_CONCRETE.defaultBlockState();
                if (style != 2 && isLevel0LightPosition(localX, localZ)) {
                    ceiling = Blocks.SEA_LANTERN.defaultBlockState();
                }
                set(world, pos, x, LEVEL_0_CEILING_Y, z, ceiling);
            }
        }
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
                BlockState wall = style == 3
                        ? Blocks.GRAY_CONCRETE.defaultBlockState()
                        : Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();

                set(world, pos, x, FLOOR_Y, z, floor);

                boolean boundary = isBoundary(localX, localZ, LEVEL_1_ROOM_SIZE);
                boolean doorway = boundary && isDoorway(
                        seed, LEVEL_1_ROOM_SIZE, 2, roomX, roomZ, localX, localZ);

                for (int y = LEVEL_1_WALL_BOTTOM; y <= LEVEL_1_WALL_TOP; y++) {
                    if (boundary && !(y <= 70 && doorway)) {
                        set(world, pos, x, y, z, wall);
                    } else if (!boundary && hasLevel1Divider(
                            seed, roomX, roomZ, localX, localZ, style) && y <= 71) {
                        set(world, pos, x, y, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                    } else if (!boundary && style == 0 && isLevel1Pillar(localX, localZ)) {
                        set(world, pos, x, y, z, Blocks.POLISHED_ANDESITE.defaultBlockState());
                    }
                }

                if (style == 1 && isLevel1CratePosition(seed, roomX, roomZ, localX, localZ)) {
                    set(world, pos, x, LEVEL_1_WALL_BOTTOM, z, Blocks.BARREL.defaultBlockState());
                }

                BlockState ceiling = style == 3
                        ? Blocks.GRAY_CONCRETE.defaultBlockState()
                        : Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
                if (style != 3 && isLevel1LightPosition(localX, localZ)) {
                    ceiling = Blocks.SEA_LANTERN.defaultBlockState();
                }
                set(world, pos, x, LEVEL_1_CEILING_Y, z, ceiling);
            }
        }
    }

    private static int level0Style(long seed, int roomX, int roomZ) {
        int roll = Math.floorMod((int) mix(seed, roomX, roomZ, 0x10A0L), 100);
        if (roll < 68) return 0;
        if (roll < 82) return 1;
        if (roll < 94) return 2;
        return 3;
    }

    private static int level1Style(long seed, int roomX, int roomZ) {
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
            int roomX, int roomZ, int localX, int localZ) {
        if (localX == 0) {
            return doorwayAlongZ(seed, roomSize, radius, roomX, roomZ, localZ);
        }
        if (localX == roomSize - 1) {
            return doorwayAlongZ(seed, roomSize, radius, roomX + 1, roomZ, localZ);
        }
        if (localZ == 0) {
            return doorwayAlongX(seed, roomSize, radius, roomX, roomZ, localX);
        }
        if (localZ == roomSize - 1) {
            return doorwayAlongX(seed, roomSize, radius, roomX, roomZ + 1, localX);
        }
        return false;
    }

    private static boolean doorwayAlongZ(
            long seed, int roomSize, int radius, int boundaryX, int roomZ, int along) {
        long edge = mix(seed, boundaryX, roomZ, 0xE451L);
        if (Math.floorMod(edge, 6) == 0) return false;
        int margin = roomSize / 4;
        int center = margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
        return Math.abs(along - center) <= radius;
    }

    private static boolean doorwayAlongX(
            long seed, int roomSize, int radius, int roomX, int boundaryZ, int along) {
        long edge = mix(seed, roomX, boundaryZ, 0xE452L);
        if (Math.floorMod(edge, 6) == 0) return false;
        int margin = roomSize / 4;
        int center = margin + Math.floorMod((int) (edge >>> 16), roomSize - margin * 2);
        return Math.abs(along - center) <= radius;
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
        return (localX == 8 || localX == 24 || localX == 40)
                && (localZ == 8 || localZ == 24 || localZ == 40);
    }

    private static boolean isLevel1CratePosition(
            long seed, int roomX, int roomZ, int localX, int localZ) {
        boolean xAnchor = localX == 8 || localX == 24 || localX == 40;
        boolean zAnchor = localZ == 8 || localZ == 24 || localZ == 40;
        if (!xAnchor || !zAnchor) return false;
        return Math.floorMod(mix(
                seed, roomX * 3 + localX, roomZ * 3 + localZ, 0xC8A7L), 3) != 0;
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

    private static void set(
            WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int y, int z, BlockState state) {
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos)) {
            world.setBlock(pos, state, 2);
        }
    }
}
