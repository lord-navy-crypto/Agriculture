package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * V4.12: four accessible, seed-independent landmarks for Backrooms Levels 2-5.
 *
 * Original L0/L1 vertical-landmark grammar inspires the design: a space has
 * coherent rooms, distinct geometry and a reason to explore rather than only
 * different palettes. This pass stays within existing room envelopes, adds
 * only to air and never replaces transit doors, original floor, ceiling,
 * containers, water, lighting or the Level 0/1 dimension systems.
 *
 * All special sectors are non-entry rooms. Each uses a chunk-local pass with
 * three-block stairs to a walkable Y=66 service/gallery floor; from the Y=63
 * original floor, the underside still has two full air blocks for walking.
 */
public final class ThresholdDeepLevelDiscoveriesV412 {
    private static final int WALK_FLOOR_Y = 63;
    private static final int UPPER_DECK_Y = 66;

    private ThresholdDeepLevelDiscoveriesV412() {}

    public static void build(WorldGenLevel world, int chunkX, int chunkZ, int level) {
        final int roomSize = level == 4 ? 48 : 32;
        final int originX = chunkX << 4;
        final int originZ = chunkZ << 4;
        final int roomX = Math.floorDiv(originX, roomSize);
        final int roomZ = Math.floorDiv(originZ, roomSize);
        if (!isLandmarkRoom(level, roomX, roomZ)) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < 16; dx++) {
            int x = originX + dx;
            int lx = Math.floorMod(x, roomSize);
            for (int dz = 0; dz < 16; dz++) {
                int z = originZ + dz;
                int lz = Math.floorMod(z, roomSize);
                switch (level) {
                    case 2 -> pressureGallery(world, pos, x, z, lx, lz);
                    case 3 -> transformerGantries(world, pos, x, z, lx, lz);
                    case 4 -> recordsAtrium(world, pos, x, z, lx, lz);
                    case 5 -> guestWingGallery(world, pos, x, z, lx, lz);
                    default -> { }
                }
            }
        }
    }

    private static boolean isLandmarkRoom(int level, int x, int z) {
        return switch (level) {
            case 2 -> x == 2 && z == 1;  // Pressure-equipment gallery
            case 3 -> x == 1 && z == 2;  // Transformer gantry
            case 4 -> x == 1 && z == 1;  // Bright records atrium
            case 5 -> x == 2 && z == 0;  // Old guest-wing overlook
            default -> false;
        };
    }

    /** Level 2: tall pumps, capped observation duct, crossing service bridge. */
    private static void pressureGallery(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                        int x, int z, int lx, int lz) {
        // Two 2-wide stair approaches on the southward Z axis lead to the
        // elevated, two-wide inspection gallery, not a blind raised platform.
        if ((lx == 14 || lx == 15) && lz >= 7 && lz <= 25) {
            if (lz <= 9) {
                stairs(world, pos, x, 64 + lz - 7, z,
                        Blocks.STONE_BRICK_STAIRS.defaultBlockState(),
                        Direction.SOUTH);
            } else {
                putAir(world, pos, x, UPPER_DECK_Y, z, Blocks.IRON_BLOCK.defaultBlockState());
            }
        }
        if ((lx == 13 || lx == 16) && lz >= 10 && lz <= 25)
            putAir(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());

        // Pressurization pistons are tall enough to create an actual chamber
        // silhouette and never sit within the central four-wide cross.
        if ((lx == 8 || lx == 9 || lx == 22 || lx == 23)
                && (lz == 8 || lz == 22)) {
            for (int y = 64; y <= 67; y++)
                putAir(world, pos, x, y, z,
                        y == 66 ? Blocks.COPPER_BLOCK.defaultBlockState()
                                : Blocks.DEEPSLATE_BRICKS.defaultBlockState());
            putAir(world, pos, x, 68, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
        }
        if ((lx == 8 || lx == 23) && lz >= 10 && lz <= 20) {
            putAir(world, pos, x, 68, z, Blocks.COPPER_BLOCK.defaultBlockState());
            if (lz % 4 == 0)
                putAir(world, pos, x, 67, z, Blocks.CHAIN.defaultBlockState());
        }
        if ((lx == 10 || lx == 21) && (lz == 9 || lz == 23))
            putAir(world, pos, x, 65, z, Blocks.LEVER.defaultBlockState());
    }

    /** Level 3: a control gantry between two enclosed transformer banks. */
    private static void transformerGantries(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                            int x, int z, int lx, int lz) {
        // E-W inspection bridge, raised enough to cross ground corridors
        // with a two-air-block clearance below it.
        if ((lz == 14 || lz == 15) && lx >= 7 && lx <= 25) {
            if (lx <= 9) stairs(world, pos, x, 64 + lx - 7, z,
                    Blocks.POLISHED_DEEPSLATE_STAIRS.defaultBlockState(),
                    Direction.EAST);
            else putAir(world, pos, x, UPPER_DECK_Y, z,
                    Blocks.IRON_BLOCK.defaultBlockState());
        }
        if ((lz == 13 || lz == 16) && lx >= 10 && lx <= 25)
            putAir(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());

        boolean transformer = ((lx >= 7 && lx <= 9) || (lx >= 22 && lx <= 24))
                && ((lz >= 7 && lz <= 10) || (lz >= 21 && lz <= 24));
        if (transformer) {
            BlockState steel = (lx == 7 || lx == 24 || lz == 7 || lz == 24)
                    ? Blocks.IRON_BARS.defaultBlockState()
                    : Blocks.COPPER_BLOCK.defaultBlockState();
            for (int y = 64; y <= 67; y++)
                putAir(world, pos, x, y, z,
                        y == 67 ? Blocks.REDSTONE_LAMP.defaultBlockState() : steel);
        }
        if ((lz == 8 || lz == 23) && lx >= 10 && lx <= 21) {
            putAir(world, pos, x, 68, z, Blocks.COPPER_BLOCK.defaultBlockState());
            if (lx % 4 == 0)
                putAir(world, pos, x, 67, z, Blocks.CHAIN.defaultBlockState());
        }
        if ((lx == 12 || lx == 19) && lz == 10)
            putAir(world, pos, x, 64, z, Blocks.GRAY_CONCRETE.defaultBlockState());
        if ((lx == 12 || lx == 19) && lz == 10)
            putAir(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
    }

    /** Level 4: a genuine elevated records balcony enclosing a lightwell. */
    private static void recordsAtrium(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                      int x, int z, int lx, int lz) {
        boolean side = (lx == 14 || lx == 15 || lx == 32 || lx == 33)
                && lz >= 14 && lz <= 33;
        boolean end = (lz == 14 || lz == 15 || lz == 32 || lz == 33)
                && lx >= 14 && lx <= 33;
        if (side || end)
            putAir(world, pos, x, UPPER_DECK_Y, z,
                    Blocks.SMOOTH_STONE.defaultBlockState());
        // Steps connect the ground floor to the southwest quadrant of
        // the gallery; lower rooms retain their original walking aisles.
        if ((lx == 14 || lx == 15) && lz >= 11 && lz <= 13)
            stairs(world, pos, x, 64 + lz - 11, z,
                    Blocks.POLISHED_DIORITE_STAIRS.defaultBlockState(), Direction.SOUTH);

        // Inboard rails leave a clear two-block walkable strip on the ring.
        if (((lx == 16 || lx == 31) && lz >= 17 && lz <= 30)
                || ((lz == 16 || lz == 31) && lx >= 17 && lx <= 30))
            putAir(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());

        // Long archive stacks and dim reading desks in the wall-side wings.
        if (((lx == 8 || lx == 39) && lz >= 10 && lz <= 37 && lz % 6 < 4)
                || ((lz == 8 || lz == 39) && lx >= 10 && lx <= 37 && lx % 6 < 4)) {
            putAir(world, pos, x, 64, z, Blocks.BOOKSHELF.defaultBlockState());
            putAir(world, pos, x, 65, z, Blocks.BOOKSHELF.defaultBlockState());
        }
        if ((lx == 10 || lx == 37) && (lz == 10 || lz == 37))
            putAir(world, pos, x, 68, z, Blocks.SEA_LANTERN.defaultBlockState());
        if ((lx == 12 || lx == 35) && (lz == 10 || lz == 37))
            putAir(world, pos, x, 64, z, Blocks.OAK_SLAB.defaultBlockState());
    }

    /** Level 5: abandoned guest-wing mezzanine, lounges and corridor overlook. */
    private static void guestWingGallery(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                                         int x, int z, int lx, int lz) {
        boolean upper = ((lz == 10 || lz == 11 || lz == 20 || lz == 21)
                && lx >= 11 && lx <= 24)
                || ((lx == 23 || lx == 24) && lz >= 10 && lz <= 21);
        if (upper)
            putAir(world, pos, x, UPPER_DECK_Y, z,
                    Blocks.DARK_OAK_PLANKS.defaultBlockState());
        if (lx >= 8 && lx <= 10 && (lz == 10 || lz == 11))
            stairs(world, pos, x, 64 + lx - 8, z,
                    Blocks.SPRUCE_STAIRS.defaultBlockState(), Direction.EAST);

        if (((lz == 9 || lz == 22) && lx >= 11 && lx <= 24)
                || (lx == 25 && lz >= 10 && lz <= 21))
            putAir(world, pos, x, 67, z,
                    Blocks.DARK_OAK_FENCE.defaultBlockState());
        if ((lx == 6 || lx == 26) && (lz == 7 || lz == 24)) {
            for (int y = 64; y <= 68; y++)
                putAir(world, pos, x, y, z,
                        y == 68 ? Blocks.GOLD_BLOCK.defaultBlockState()
                                : Blocks.DARK_OAK_LOG.defaultBlockState());
        }
        if ((lx == 8 || lx == 23) && (lz == 8 || lz == 23))
            putAir(world, pos, x, 64, z,
                    Blocks.RED_CARPET.defaultBlockState());
        if ((lx == 9 || lx == 22) && (lz == 8 || lz == 23))
            putAir(world, pos, x, 64, z,
                    Blocks.OAK_SLAB.defaultBlockState());
        if (lx == 7 && lz >= 12 && lz <= 19) {
            putAir(world, pos, x, 65, z,
                    Blocks.BLACK_STAINED_GLASS.defaultBlockState());
            putAir(world, pos, x, 66, z,
                    Blocks.BLACK_STAINED_GLASS.defaultBlockState());
        }
    }

    private static void stairs(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                               int x, int y, int z, BlockState base, Direction facing) {
        putAir(world, pos, x, y, z, base.setValue(StairBlock.FACING, facing));
    }

    private static void putAir(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                               int x, int y, int z, BlockState state) {
        // Never carve or replace an existing feature, no cross-chunk write.
        // Floor Y=63, ceiling Y=71, and all door/portal blocks remain intact.
        if (y <= WALK_FLOOR_Y || y >= 71) return;
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos) && world.getBlockState(pos).isAir()) {
            world.setBlock(pos, state, 2);
        }
    }
}
