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

    /** Shared protected corridor mask for later architectural decoration. */
    public static boolean isGuaranteedCorridor(long seed, int x, int z, boolean level0) {
        return isGuaranteedSpine(seed, x, z,
                level0 ? LEVEL_0_SPACING : LEVEL_1_SPACING,
                level0 ? LEVEL_0_JITTER : LEVEL_1_JITTER,
                level0 ? LEVEL_0_SALT : LEVEL_1_SALT);
    }

    private static void refine(WorldGenLevel world, int chunkX, int chunkZ, long seed,
                               BlockPos.MutableBlockPos pos, boolean level0) {
        int spacing = level0 ? LEVEL_0_SPACING : LEVEL_1_SPACING;
        int jitter = level0 ? LEVEL_0_JITTER : LEVEL_1_JITTER;
        int legacySize = level0 ? LEVEL_0_LEGACY_SIZE : LEVEL_1_LEGACY_SIZE;
        int maxWallY = level0 ? LEVEL_0_MAX_WALL_Y : LEVEL_1_MAX_WALL_Y;
        long salt = level0 ? LEVEL_0_SALT : LEVEL_1_SALT;
        // Small rooms and long intact walls are the default; openings, not
        // wholesale room fusion, create the familiar liminal circulation.
        int openRate = level0 ? 54 : 46;
        // Merged halls are rare landmarks, never the dominant architecture.
        int mergeRate = level0 ? 1 : 4;
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

                Sample sample = sample(seed, x, z, spacing, jitter, salt, openRate, mergeRate, level0);
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

    /** Confirms each floor keeps narrow, medium and genuinely wide doorways. */
    public static boolean hasDoorwayWidthVariety(long seed, boolean level0) {
        int spacing = level0 ? LEVEL_0_SPACING : LEVEL_1_SPACING;
        int jitter = level0 ? LEVEL_0_JITTER : LEVEL_1_JITTER;
        long salt = level0 ? LEVEL_0_SALT : LEVEL_1_SALT;
        boolean narrow = false;
        boolean medium = false;
        boolean broad = false;
        for (int gx = -6; gx <= 6; gx++) {
            for (int gz = -6; gz <= 6; gz++) {
                Cell a = cellCenter(seed, gx, gz, spacing, jitter, salt);
                Cell b = cellCenter(seed, gx + 1, gz, spacing, jitter, salt);
                int width = doorwayWidth(edgeHash(seed, a, b, salt), level0);
                narrow |= width <= (level0 ? 2 : 3);
                medium |= width >= (level0 ? 3 : 4) && width <= (level0 ? 5 : 6);
                broad |= width >= (level0 ? 7 : 8);
            }
        }
        return narrow && medium && broad;
    }

    /** Checks that the room grammar creates both partitions and open floor area. */
    public static boolean hasInteriorPartitionVariety(long seed, boolean level0) {
        int spacing = level0 ? LEVEL_0_SPACING : LEVEL_1_SPACING;
        int jitter = level0 ? LEVEL_0_JITTER : LEVEL_1_JITTER;
        long salt = level0 ? LEVEL_0_SALT : LEVEL_1_SALT;
        int walls = 0;
        int open = 0;
        for (int gx = -3; gx <= 3; gx++) {
            for (int gz = -3; gz <= 3; gz++) {
                Cell cell = cellCenter(seed, gx, gz, spacing, jitter, salt);
                for (int lx = -spacing / 2 + 1; lx < spacing / 2; lx++) {
                    for (int lz = -spacing / 2 + 1; lz < spacing / 2; lz++) {
                        if (isInteriorPartition(seed, cell, cell.x + lx, cell.z + lz,
                                level0, salt)) walls++;
                        else open++;
                    }
                }
            }
        }
        return walls >= 80 && open >= 1200;
    }

    /** Both floors contain compact side-room pockets and open cells. */
    public static boolean hasNestedAlcoveVariety(long seed, boolean level0) {
        long salt = level0 ? LEVEL_0_SALT : LEVEL_1_SALT;
        int pockets=0, open=0;
        for(int gx=-5;gx<=5;gx++) for(int gz=-5;gz<=5;gz++) {
            long h=mix(seed,gx,gz,salt ^ 0x1A7E10L);
            int roll=(int)Math.floorMod(h,100L);
            boolean pocket=level0 ? roll>=40 && roll<64 : roll>=36 && roll<69;
            if(pocket) pockets++; else open++;
        }
        return pockets>=8 && open>=8;
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
                                 long salt, int openRate, int mergeRate, boolean level0) {
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
        boolean merged = isMergedEdge(first, second, edge, mergeRate);
        boolean open = isOpenEdge(first, second, edge, openRate);
        // A merged edge removes the shared partition completely. Remaining
        // edges retain narrow doors, sealed dead ends, and spine guarantees.
        return new Sample(first, second, true,
                merged || (open && isDoorway(x, z, first, second, edge, level0)), edge);
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

    private static boolean isMergedEdge(Cell a, Cell b, long edgeHash, int mergeRate) {
        // Only true axis-neighbors merge. Diagonal/three-way junctions keep
        // their separator to preserve smaller rooms and readable silhouettes.
        if (Math.abs(a.gx - b.gx) + Math.abs(a.gz - b.gz) != 1) return false;
        return Math.floorMod(edgeHash >>> 37, 100L) < mergeRate;
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

    /**
     * A break in the wall, not a Minecraft door block. A few connections are
     * broad 7-10 block openings; many are two to four blocks. Each shared
     * edge samples its width and offset once, so chunk borders agree.
     */
    private static boolean isDoorway(int x, int z, Cell a, Cell b,
                                     long edgeHash, boolean level0) {
        double vx = b.x - a.x;
        double vz = b.z - a.z;
        double length = Math.sqrt(vx * vx + vz * vz);
        if (length < 1.0D) return false;
        double midX = (a.x + b.x) * 0.5D;
        double midZ = (a.z + b.z) * 0.5D;
        double along = ((x - midX) * -vz + (z - midZ) * vx) / length;
        int offset = (int) Math.floorMod(edgeHash >>> 21, 13L) - 6;
        int width = doorwayWidth(edgeHash, level0);
        return Math.abs(along - offset) <= width * 0.5D;
    }

    private static int doorwayWidth(long edgeHash, boolean level0) {
        int roll = (int) Math.floorMod(edgeHash >>> 29, 100L);
        if (level0) {
            if (roll < 12) return 1;
            if (roll < 46) return 2;
            if (roll < 74) return 3;
            if (roll < 90) return 4;
            if (roll < 96) return 5;
            if (roll < 99) return 7;
            return 9;
        }
        if (roll < 9) return 2;
        if (roll < 35) return 3;
        if (roll < 65) return 4;
        if (roll < 82) return 5;
        if (roll < 92) return 6;
        if (roll < 98) return 8;
        return 10;
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
            // Most cells receive one long, slightly jogged divider. A minority
            // add a cross-wall or short return to form alcoves and side rooms.
            if (roll >= 72) return false;
            int lineX = (int) Math.floorMod(h >>> 8, 11L) - 5;
            int lineZ = (int) Math.floorMod(h >>> 16, 11L) - 5;
            int gapZ = (int) Math.floorMod(h >>> 24, 17L) - 8;
            int gapX = (int) Math.floorMod(h >>> 32, 17L) - 8;
            int width = 1 + 2 * (int) Math.floorMod(h >>> 40, 3L);
            int jogX = Math.floorMod(Math.floorDiv(lz + 12, 6), 2);
            int jogZ = Math.floorMod(Math.floorDiv(lx + 12, 6), 2);
            boolean mainDivider = Math.abs(lx - lineX - jogX) <= (roll < 18 ? 1 : 0)
                    && Math.abs(lz) <= 10 && !insideDoorGap(lz, gapZ, width);
            boolean crossDivider = roll < 36
                    && Math.abs(lz - lineZ + jogZ) == 0
                    && Math.abs(lx) <= 9 && !insideDoorGap(lx, gapX, width);
            int returnX = lineX + (Math.floorMod((int) (h >>> 47), 2) == 0 ? 4 : -4);
            int returnGap = (int) Math.floorMod(h >>> 52, 13L) - 6;
            boolean sideReturn = roll < 27 && Math.abs(lx - returnX) == 0
                    && lz >= -8 && lz <= 8 && !insideDoorGap(lz, returnGap, 3);
            int returnZ = lineZ + (Math.floorMod((int) (h >>> 49), 2) == 0 ? 4 : -4);
            boolean shortCrossReturn = roll < 19 && Math.abs(lz - returnZ) == 0
                    && lx >= -7 && lx <= 7 && !insideDoorGap(lx, gapX, 3);
            int pocket=roll>=40 && roll<64 ? nestedAlcoveTile(h,lx,lz,true) : 0;
            // A pocket doorway must also interrupt intersecting old dividers.
            if(pocket==2)return false;
            return mainDivider || crossDivider || sideReturn || shortCrossReturn || pocket==1;
        }

        // Level 1 uses warehouse bay walls, offset rack dividers and occasional
        // paired partitions. Doorways are wider to read as loading/service bays.
        if (roll >= 80) return false;
        int lineX = (int) Math.floorMod(h >>> 8, 17L) - 8;
        int lineZ = (int) Math.floorMod(h >>> 16, 17L) - 8;
        int gapZ = (int) Math.floorMod(h >>> 24, 21L) - 10;
        int gapX = (int) Math.floorMod(h >>> 32, 21L) - 10;
        int width = 3 + 2 * (int) Math.floorMod(h >>> 40, 3L);
        int jogX = Math.floorMod(Math.floorDiv(lz + 14, 7), 2);
        int jogZ = Math.floorMod(Math.floorDiv(lx + 14, 7), 2);
        boolean bayDivider = Math.abs(lx - lineX - jogX) <= (roll < 16 ? 1 : 0)
                && Math.abs(lz) <= 13 && !insideDoorGap(lz, gapZ, width);
        boolean crossAisle = roll < 43 && Math.abs(lz - lineZ + jogZ) == 0
                && Math.abs(lx) <= 12 && !insideDoorGap(lx, gapX, width);
        int rackX = lineX + (Math.floorMod((int) (h >>> 48), 2) == 0 ? 5 : -5);
        int rackGap = (int) Math.floorMod(h >>> 53, 17L) - 8;
        boolean rackReturn = roll < 34 && Math.abs(lx - rackX) == 0
                && lz >= -10 && lz <= 10 && !insideDoorGap(lz, rackGap, 5);
        int bayZ = lineZ + (Math.floorMod((int) (h >>> 50), 2) == 0 ? 5 : -5);
        boolean shortBay = roll < 25 && Math.abs(lz - bayZ) == 0
                && lx >= -9 && lx <= 9 && !insideDoorGap(lx, gapX, 5);
        int pocket=roll>=36 && roll<69 ? nestedAlcoveTile(h,lx,lz,false) : 0;
        if(pocket==2)return false;
        return bayDivider || crossAisle || rackReturn || shortBay || pocket==1;
    }

    /**
     * Inset side-room with wide openings on both axes. Returns 0 for other
     * floor tiles, 1 for the wall and 2 for an entrance. The caller clears
     * older intersecting dividers on entrance tiles, preventing fake doors.
     * The geometry is deterministic even at chunk boundaries.
     */
    private static int nestedAlcoveTile(long hash,int lx,int lz,boolean level0) {
        int ox=(int)Math.floorMod(hash >>> 10,5L)-2;
        int oz=(int)Math.floorMod(hash >>> 18,5L)-2;
        int hx=(level0?3:5)+(int)Math.floorMod(hash >>> 25,2L);
        int hz=(level0?4:5)+(int)Math.floorMod(hash >>> 34,2L);
        int dx=lx-ox,dz=lz-oz;
        boolean vertical=Math.abs(dx)==hx && Math.abs(dz)<=hz;
        boolean horizontal=Math.abs(dz)==hz && Math.abs(dx)<=hx;
        if(!vertical && !horizontal)return 0;
        int northGap=(int)Math.floorMod(hash >>> 43,5L)-2;
        int sideGap=(int)Math.floorMod(hash >>> 51,5L)-2;
        int width=level0?3:5;
        if(vertical && insideDoorGap(dz,sideGap,width))return 2;
        if(horizontal && insideDoorGap(dx,northGap,width))return 2;
        return 1;
    }

    private static boolean insideDoorGap(int along, int center, int width) {
        return Math.abs(along - center) <= (width - 1) / 2;
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
        int materialRoll = (int) Math.floorMod(h >>> 33, 100L);
        BlockState wall;
        int topRoll = (int) Math.floorMod(h >>> 39, 100L);
        int top;
        if (level0) {
            wall = materialRoll < 4 ? Blocks.GRAY_CONCRETE.defaultBlockState()
                    : materialRoll < 17
                    ? ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState()
                    : ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState();
            top = topRoll < 82 ? 71 : topRoll < 95 ? 69 : 67;
        } else {
            wall = materialRoll < 17 ? Blocks.GRAY_CONCRETE.defaultBlockState()
                    : materialRoll < 28 ? Blocks.POLISHED_ANDESITE.defaultBlockState()
                    : ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState();
            top = topRoll < 76 ? 73 : topRoll < 93 ? 70 : 68;
        }
        set(world, pos, x, 64, z, wall);
        for (int y = 65; y <= top; y++) set(world, pos, x, y, z, wall);
    }

    private static BlockState wallState(long seed, Cell cell, long edgeHash, boolean level0) {
        int roll = (int) Math.floorMod(edgeHash, 100L);
        if (level0) {
            // Mono-yellow dominates; unusual red and gray fragments are
            // genuinely rare rather than every third hallway.
            if (roll < 1) return Blocks.RED_TERRACOTTA.defaultBlockState();
            if (roll < 12) return ThresholdEnvironmentBlocks.STAINED_WALLPAPER.get().defaultBlockState();
            if (roll < 15) return Blocks.GRAY_CONCRETE.defaultBlockState();
            return ThresholdEnvironmentBlocks.FADED_WALLPAPER.get().defaultBlockState();
        }
        if (roll < 12) return Blocks.GRAY_CONCRETE.defaultBlockState();
        if (roll < 22) return Blocks.POLISHED_ANDESITE.defaultBlockState();
        return ThresholdEnvironmentBlocks.WAREHOUSE_WALL.get().defaultBlockState();
    }

    private static int wallTop(long edgeHash, boolean level0) {
        // Ordinary partitions meet the ceiling. A minority stop short to
        // produce unnerving overhead sightlines or industrial half walls.
        int roll = (int) Math.floorMod(edgeHash >>> 18, 100L);
        if (level0) return roll < 77 ? 71 : roll < 94 ? 69 : 67;
        return roll < 68 ? 73 : roll < 91 ? 70 : 68;
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
