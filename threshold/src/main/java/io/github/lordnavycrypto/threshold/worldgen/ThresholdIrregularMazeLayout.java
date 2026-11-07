package io.github.lordnavycrypto.threshold.worldgen;

import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Multi-scale, jittered Voronoi room layout for Backrooms Levels 0 and 1.
 * The former 32/48-block grid is retained only for legacy landmark placement;
 * its straight boundary walls are replaced with irregular rooms, variable
 * doorways, short partitions, and level-specific vertical details.
 */
public final class ThresholdIrregularMazeLayout {
    private static final int LEVEL_0_SPACING = 22;
    private static final int LEVEL_1_SPACING = 28;
    private static final int LEVEL_0_JITTER = 6;
    private static final int LEVEL_1_JITTER = 7;
    private static final int LEVEL_0_LEGACY_SIZE = 32;
    private static final int LEVEL_1_LEGACY_SIZE = 48;
    private static final int LEVEL_0_MAX_WALL_Y = 71;
    private static final int LEVEL_1_MAX_WALL_Y = 73;
    private static final long LEVEL_0_SALT = 0x4D415A45304CL;
    private static final long LEVEL_1_SALT = 0x4D415A45314CL;

    private ThresholdIrregularMazeLayout() {}

    public static void refineLevel0(WorldGenLevel world, int chunkX, int chunkZ,
                                    long seed, BlockPos.MutableBlockPos pos) {
        refine(world, chunkX, chunkZ, seed, pos, true);
    }

    public static void refineLevel1(WorldGenLevel world, int chunkX, int chunkZ,
                                    long seed, BlockPos.MutableBlockPos pos) {
        refine(world, chunkX, chunkZ, seed, pos, false);
    }

    /** Deterministic geometry check used by QA and the server audit. */
    public static boolean hasVariableCellSpacing(long seed, boolean level0) {
        int spacing = level0 ? LEVEL_0_SPACING : LEVEL_1_SPACING;
        int jitter = level0 ? LEVEL_0_JITTER : LEVEL_1_JITTER;
        long salt = level0 ? LEVEL_0_SALT : LEVEL_1_SALT;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int gz = -4; gz <= 4; gz++) {
            for (int gx = -4; gx < 4; gx++) {
                int width = centerX(seed, gx + 1, gz, spacing, jitter, salt)
                        - centerX(seed, gx, gz, spacing, jitter, salt);
                min = Math.min(min, width);
                max = Math.max(max, width);
            }
        }
        return max - min >= 3;
    }

    private static void refine(WorldGenLevel world, int chunkX, int chunkZ, long seed,
                               BlockPos.MutableBlockPos pos, boolean level0) {
        int spacing = level0 ? LEVEL_0_SPACING : LEVEL_1_SPACING;
        int jitter = level0 ? LEVEL_0_JITTER : LEVEL_1_JITTER;
        int legacySize = level0 ? LEVEL_0_LEGACY_SIZE : LEVEL_1_LEGACY_SIZE;
        int maxWallY = level0 ? LEVEL_0_MAX_WALL_Y : LEVEL_1_MAX_WALL_Y;
        long salt = level0 ? LEVEL_0_SALT : LEVEL_1_SALT;
        int openRate = level0 ? 64 : 52;
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;

        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = minX + dx;
                int z = minZ + dz;
                // Keep the iconic Level 0 entrance interior and its transition
                // intact. A seed-stable maze spine may pierce perimeter walls,
                // or the entire negative-axis maze can be sealed from spawn.
                boolean spine = isGuaranteedSpine(seed, x, z, spacing, jitter, salt);
                if (isProtectedPassageOrEntry(x, z, level0)
                        && !(level0 && spine && isLevel0EntryPerimeter(x, z)
                        && !isLevel0TransitionFootprint(x, z))) continue;
                if (isResourceAnchor(world, pos, x, z)) continue;

                Sample sample = sample(seed, x, z, spacing, jitter, salt, openRate);
                boolean legacyBoundary = isLegacyBoundary(x, z, legacySize);
                // Carve a deterministic physical spine between jittered cells,
                // before adding discretionary interior walls.

                if (spine) {
                    clearWall(world, pos, x, z, maxWallY, level0, seed, sample.first, salt);
                } else if (sample.boundary && !sample.doorway) {
                    placeBoundaryWall(world, pos, x, z, sample, seed, salt, maxWallY, level0);
                } else if (legacyBoundary || sample.boundary) {
                    clearWall(world, pos, x, z, maxWallY, level0, seed, sample.first, salt);
                }

                repaintFloor(world, pos, x, z, sample.first, seed, salt, level0);
                repaintCeiling(world, pos, x, z, sample.first, seed, salt, level0);

                if (!spine && !sample.boundary
                        && isInteriorPartition(seed, sample.first, x, z, level0, salt)) {
                    placeInteriorPartition(world, pos, x, z, sample.first, seed, salt, level0);
                }
                if (!spine && !sample.boundary && level0
                        && hasLowFalseCeiling(seed, sample.first, x, z, salt)) {
                    set(world, pos, x, 70, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                }
                if (!spine && !sample.boundary && !level0
                        && hasWarehouseDuct(seed, sample.first, x, z, salt)) {
                    set(world, pos, x, 71, z, Blocks.COPPER_BLOCK.defaultBlockState());
                    set(world, pos, x, 72, z, Blocks.CUT_COPPER.defaultBlockState());
                }
            }
        }
    }

    private static boolean isLevel0EntryPerimeter(int x, int z) {
        return x >= 0 && x < LEVEL_0_LEGACY_SIZE
                && z >= 0 && z < LEVEL_0_LEGACY_SIZE
                && (x == 0 || z == 0
                || x == LEVEL_0_LEGACY_SIZE - 1
                || z == LEVEL_0_LEGACY_SIZE - 1);
    }

    private static boolean isLevel0TransitionFootprint(int x, int z) {
        // Never replace the original Level 0 to Level 1 transition.
        return x == 0 && z >= 13 && z <= 17;
    }

    private static boolean isProtectedPassageOrEntry(int x, int z, boolean level0) {
        // Keep the iconic 31x32 entry interior. The outer east wall may
        // receive one new spine doorway, connecting it to the irregular maze.
        if (level0) return x >= 0 && x < 31 && z >= 0 && z < 32;
        return (x >= 0 && x < 20 && z >= 0 && z < 20)
                || (x == 0 && (z == 23 || z == 24))
                || (Math.abs(x - 64) <= 1 && Math.abs(z - 23) <= 2);
    }

    private static boolean isResourceAnchor(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                            int x, int z) {
        BlockState floor = world.getBlockState(pos.set(x, 63, z));
        BlockState feature = world.getBlockState(pos.set(x, 64, z));
        return floor.is(Blocks.FARMLAND) || floor.is(Blocks.WATER) || floor.is(Blocks.COMPOSTER)
                || feature.is(Blocks.BARREL) || feature.is(Blocks.CHEST)
                || feature.is(Blocks.TRAPPED_CHEST);
    }

    private static boolean isLegacyBoundary(int x, int z, int size) {
        int lx = Math.floorMod(x, size);
        int lz = Math.floorMod(z, size);
        return lx == 0 || lx == size - 1 || lz == 0 || lz == size - 1;
    }

    private static Sample sample(long seed, int x, int z, int spacing, int jitter,
                                 long salt, int openRate) {
        int baseX = Math.floorDiv(x, spacing);
        int baseZ = Math.floorDiv(z, spacing);
        Cell first = null;
        Cell second = null;
        for (int gx = baseX - 1; gx <= baseX + 1; gx++) {
            for (int gz = baseZ - 1; gz <= baseZ + 1; gz++) {
                Cell center = cellCenter(seed, gx, gz, spacing, jitter, salt);
                long ox = (long) x - center.x;
                long oz = (long) z - center.z;
                Cell candidate = new Cell(gx, gz, center.x, center.z, ox * ox + oz * oz);
                if (first == null || candidate.distance < first.distance) {
                    second = first;
                    first = candidate;
                } else if (second == null || candidate.distance < second.distance) {
                    second = candidate;
                }
            }
        }
        boolean boundary = second != null && second.distance - first.distance <= spacing * 3L;
        if (!boundary) return new Sample(first, second, false, false, 0L);
        long edge = edgeHash(seed, first, second, salt);
        boolean open = isOpenEdge(first, second, edge, openRate);
        return new Sample(first, second, true, open && isDoorway(x, z, first, second, edge), edge);
    }

    private static Cell cellCenter(long seed, int gx, int gz, int spacing, int jitter, long salt) {
        long hx = mix(seed, gx, gz, salt ^ 0x51A7L);
        long hz = mix(seed, gx, gz, salt ^ 0xA73DL);
        int jx = (int) Math.floorMod(hx, jitter * 2L + 1L) - jitter;
        int jz = (int) Math.floorMod(hz, jitter * 2L + 1L) - jitter;
        return new Cell(gx, gz, gx * spacing + spacing / 2 + jx,
                gz * spacing + spacing / 2 + jz, 0L);
    }

    private static int centerX(long seed, int gx, int gz, int spacing, int jitter, long salt) {
        return cellCenter(seed, gx, gz, spacing, jitter, salt).x;
    }

    /**
     * Explicit, chunk-independent walkable spanning tree. Every jittered cell
     * is joined to one cell closer to the origin (X first, then Z). This
     * prevents graph-level "connected" rooms being sealed by voxel rounding,
     * and never loads or reads a neighboring chunk.
     */
    private static boolean isGuaranteedSpine(long seed, int x, int z,
                                             int spacing, int jitter, long salt) {
        int bx = Math.floorDiv(x, spacing);
        int bz = Math.floorDiv(z, spacing);
        // A line crossing this block can originate in any of the nine local
        // lattice cells. Nine bounded candidates keep all chunk seams stable.
        for (int gx = bx - 1; gx <= bx + 1; gx++) {
            for (int gz = bz - 1; gz <= bz + 1; gz++) {
                if (gx == 0 && gz == 0) continue;
                int px = gx == 0 ? gx : gx - Integer.signum(gx);
                int pz = gx == 0 ? gz - Integer.signum(gz) : gz;
                Cell child = cellCenter(seed, gx, gz, spacing, jitter, salt);
                Cell parent = cellCenter(seed, px, pz, spacing, jitter, salt);
                double vx = parent.x - child.x;
                double vz = parent.z - child.z;
                double denom = vx * vx + vz * vz;
                if (denom < 1.0D) continue;
                double t = ((x - child.x) * vx + (z - child.z) * vz) / denom;
                t = Math.max(0.0D, Math.min(1.0D, t));
                double offX = x - (child.x + t * vx);
                double offZ = z - (child.z + t * vz);
                if (offX * offX + offZ * offZ <= 5.0D) return true;
            }
        }
        return false;
    }

    private static boolean isOpenEdge(Cell a, Cell b, long edgeHash, int openRate) {
        int manhattan = Math.abs(a.gx - b.gx) + Math.abs(a.gz - b.gz);
        if (manhattan == 1 && (isParent(a, b) || isParent(b, a))) return true;
        int chance = manhattan == 1 ? openRate : 12;
        return Math.floorMod(edgeHash, 100L) < chance;
    }

    private static boolean isParent(Cell child, Cell parent) {
        if (child.gx != 0) {
            return parent.gx == child.gx - Integer.signum(child.gx) && parent.gz == child.gz;
        }
        return child.gz != 0 && parent.gx == child.gx
                && parent.gz == child.gz - Integer.signum(child.gz);
    }

    private static boolean isDoorway(int x, int z, Cell a, Cell b, long edgeHash) {
        double vx = b.x - a.x;
        double vz = b.z - a.z;
        double length = Math.sqrt(vx * vx + vz * vz);
        if (length < 1.0D) return false;
        double midX = (a.x + b.x) * 0.5D;
        double midZ = (a.z + b.z) * 0.5D;
        double along = ((x - midX) * -vz + (z - midZ) * vx) / length;
        int offset = (int) Math.floorMod(edgeHash >>> 21, 7L) - 3;
        int width = 2 + (int) Math.floorMod(edgeHash >>> 29, 4L);
        return Math.abs(along - offset) <= width * 0.5D;
    }

    private static long edgeHash(long seed, Cell a, Cell b, long salt) {
        Cell left = a.gx < b.gx || (a.gx == b.gx && a.gz <= b.gz) ? a : b;
        Cell right = left == a ? b : a;
        return mix(seed, left.gx * 31 + right.gx, left.gz * 31 + right.gz, salt ^ 0xED63L);
    }

    private static boolean isInteriorPartition(long seed, Cell cell, int x, int z,
                                               boolean level0, long salt) {
        long h = mix(seed, cell.gx, cell.gz, salt ^ 0x1A7E10L);
        int roll = (int) Math.floorMod(h, 100L);
        int lx = x - cell.x;
        int lz = z - cell.z;
        if (level0) {
            if (roll >= 58) return false;
            int line = (int) Math.floorMod(h >>> 8, 9L) - 4;
            int gap = (int) Math.floorMod(h >>> 16, 13L) - 6;
            boolean vertical = lx == line && Math.abs(lz) <= 9 && Math.abs(lz - gap) > 1;
            boolean horizontal = roll < 22 && lz == -line
                    && Math.abs(lx) <= 8 && Math.abs(lx - gap) > 1;
            return vertical || horizontal;
        }
        if (roll >= 68) return false;
        int lineX = (int) Math.floorMod(h >>> 8, 11L) - 5;
        int lineZ = (int) Math.floorMod(h >>> 16, 11L) - 5;
        int gapZ = (int) Math.floorMod(h >>> 24, 13L) - 6;
        int gapX = (int) Math.floorMod(h >>> 32, 13L) - 6;
        boolean rackAisle = lx == lineX && Math.abs(lz) <= 11 && Math.abs(lz - gapZ) > 2;
        boolean crossAisle = lz == lineZ && Math.abs(lx) <= 10 && Math.abs(lx - gapX) > 2;
        return rackAisle || (roll < 34 && crossAisle);
    }

    private static void placeBoundaryWall(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                          int x, int z, Sample sample, long seed, long salt,
                                          int maxWallY, boolean level0) {
        BlockState wall = wallState(seed, sample.first, sample.edgeHash, level0);
        int top = wallTop(sample.edgeHash, level0);
        set(world, pos, x, 64, z, wall);
        for (int y = 65; y <= top; y++) set(world, pos, x, y, z, wall);
        for (int y = top + 1; y <= maxWallY; y++) set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
    }

    private static void placeInteriorPartition(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                               int x, int z, Cell cell, long seed, long salt,
                                               boolean level0) {
        long h = mix(seed, cell.gx, cell.gz, salt ^ 0x1A7E10L);
        BlockState wall = level0
                ? ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState()
                : ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState();
        int top = level0 ? 67 + (int) Math.floorMod(h, 3L) : 69 + (int) Math.floorMod(h, 3L);
        set(world, pos, x, 64, z, wall);
        for (int y = 65; y <= top; y++) set(world, pos, x, y, z, wall);
    }

    private static BlockState wallState(long seed, Cell cell, long edgeHash, boolean level0) {
        int roll = (int) Math.floorMod(edgeHash, 100L);
        if (level0) {
            if (roll < 3) return Blocks.RED_TERRACOTTA.defaultBlockState();
            if (roll < 12) return ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState();
            if (roll < 19) return Blocks.GRAY_CONCRETE.defaultBlockState();
            return ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState();
        }
        if (roll < 12) return Blocks.GRAY_CONCRETE.defaultBlockState();
        if (roll < 22) return Blocks.POLISHED_ANDESITE.defaultBlockState();
        return ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState();
    }

    private static int wallTop(long edgeHash, boolean level0) {
        return level0 ? 67 + (int) Math.floorMod(edgeHash, 5L)
                : 68 + (int) Math.floorMod(edgeHash, 6L);
    }

    private static void clearWall(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                  int x, int z, int maxWallY, boolean level0,
                                  long seed, Cell cell, long salt) {
        for (int y = 64; y <= maxWallY; y++) set(world, pos, x, y, z, Blocks.AIR.defaultBlockState());
        if (level0) set(world, pos, x, 64, z, carpetState(seed, cell, salt));
    }

    private static void repaintFloor(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                     int x, int z, Cell cell, long seed, long salt,
                                     boolean level0) {
        if (level0) {
            BlockState current = world.getBlockState(pos.set(x, 64, z));
            if (current.is(Blocks.YELLOW_CARPET) || current.is(Blocks.GRAY_CARPET)
                    || current.is(Blocks.BROWN_CARPET) || current.is(Blocks.RED_CARPET)) {
                set(world, pos, x, 64, z, carpetState(seed, cell, salt));
            }
            return;
        }
        BlockState current = world.getBlockState(pos.set(x, 63, z));
        if (current.is(Blocks.SMOOTH_STONE) || current.is(Blocks.GRAY_CONCRETE)
                || current.is(Blocks.ANDESITE) || current.is(Blocks.POLISHED_ANDESITE)) {
            set(world, pos, x, 63, z, warehouseFloor(seed, cell, salt));
        }
    }

    private static BlockState carpetState(long seed, Cell cell, long salt) {
        int roll = (int) Math.floorMod(mix(seed, cell.gx, cell.gz, salt ^ 0xCA4E7L), 100L);
        if (roll < 4) return Blocks.RED_CARPET.defaultBlockState();
        if (roll < 13) return Blocks.GRAY_CARPET.defaultBlockState();
        if (roll < 22) return Blocks.BROWN_CARPET.defaultBlockState();
        return Blocks.YELLOW_CARPET.defaultBlockState();
    }

    private static BlockState warehouseFloor(long seed, Cell cell, long salt) {
        int roll = (int) Math.floorMod(mix(seed, cell.gx, cell.gz, salt ^ 0xF1004L), 100L);
        if (roll < 8) return Blocks.GRAY_CONCRETE.defaultBlockState();
        if (roll < 18) return Blocks.ANDESITE.defaultBlockState();
        if (roll < 27) return Blocks.POLISHED_ANDESITE.defaultBlockState();
        return Blocks.SMOOTH_STONE.defaultBlockState();
    }

    private static void repaintCeiling(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                       int x, int z, Cell cell, long seed, long salt,
                                       boolean level0) {
        int y = level0 ? 72 : 74;
        BlockState current = world.getBlockState(pos.set(x, y, z));
        int roll = (int) Math.floorMod(mix(seed, cell.gx, cell.gz, salt ^ 0xCE111L), 100L);
        if (level0 && (current.is(Blocks.WHITE_CONCRETE) || current.is(Blocks.DEEPSLATE_TILES)
                || current.is(Blocks.GRAY_CONCRETE))) {
            set(world, pos, x, y, z, roll < 10 ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                    : roll < 17 ? Blocks.GRAY_CONCRETE.defaultBlockState()
                    : Blocks.WHITE_CONCRETE.defaultBlockState());
        } else if (!level0 && (current.is(Blocks.LIGHT_GRAY_CONCRETE)
                || current.is(Blocks.GRAY_CONCRETE) || current.is(Blocks.SMOOTH_STONE))) {
            set(world, pos, x, y, z, roll < 12 ? Blocks.GRAY_CONCRETE.defaultBlockState()
                    : roll < 20 ? Blocks.SMOOTH_STONE.defaultBlockState()
                    : Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState());
        }
    }

    private static boolean hasLowFalseCeiling(long seed, Cell cell, int x, int z, long salt) {
        long h = mix(seed, cell.gx, cell.gz, salt ^ 0x10CE11L);
        return Math.floorMod(h, 100L) < 14
                && Math.abs(x - cell.x) <= 4 && Math.abs(z - cell.z) <= 4;
    }

    private static boolean hasWarehouseDuct(long seed, Cell cell, int x, int z, long salt) {
        long h = mix(seed, cell.gx, cell.gz, salt ^ 0xD0C7L);
        return Math.floorMod(h, 100L) < 28
                && Math.abs(x - cell.x) <= 8
                && (z - cell.z == 2 || z - cell.z == 3);
    }

    private static long mix(long seed, int x, int z, long salt) {
        long value = seed ^ salt
                ^ (long) x * 0x9E3779B97F4A7C15L
                ^ (long) z * 0xC2B2AE3D27D4EB4FL;
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    private static void set(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                            int x, int y, int z, BlockState state) {
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos)) world.setBlock(pos, state, 2);
    }

    private record Cell(int gx, int gz, int x, int z, long distance) {}
    private record Sample(Cell first, Cell second, boolean boundary, boolean doorway, long edgeHash) {}
}
