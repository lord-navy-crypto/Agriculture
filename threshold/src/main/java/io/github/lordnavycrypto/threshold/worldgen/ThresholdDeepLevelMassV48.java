package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Adds chunk-local structural mass below and above Levels 2-5.
 * The existing Y=63 walking surfaces, Y=64..70 rooms, Y=71 ceilings,
 * source-backed exits, and Level 0/1 architecture are not touched.
 *
 * This is deliberately a solid shell, not a second density generator:
 * it never carves a route, reaches across chunk borders, or rewrites a
 * previously generated chunk. Generated rooms gain a five-block
 * foundation, four-block roof and sparse interior corner supports.
 */
public final class ThresholdDeepLevelMassV48 {
    private static final int FOUNDATION_BOTTOM = 58;
    private static final int FOUNDATION_TOP = 62;
    private static final int ROOF_BOTTOM = 72;
    private static final int ROOF_TOP = 75;

    private ThresholdDeepLevelMassV48() {}

    public static void build(WorldGenLevel world, int chunkX, int chunkZ, int level) {
        if (level < 2 || level > 5) {
            throw new IllegalArgumentException("Only Levels 2-5 have deep-level shells");
        }
        int roomSize = level == 4 ? 48 : 32;
        int x0 = chunkX << 4;
        int z0 = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < 16; dx++) {
            int x = x0 + dx;
            int roomX = Math.floorDiv(x, roomSize);
            int localX = Math.floorMod(x, roomSize);
            for (int dz = 0; dz < 16; dz++) {
                int z = z0 + dz;
                int roomZ = Math.floorDiv(z, roomSize);
                int localZ = Math.floorMod(z, roomSize);
                boolean rib = Math.floorMod(localX, 8) == 0
                        || Math.floorMod(localZ, 8) == 0;
                boolean boiler = level == 5 && roomX == 0 && roomZ == 1;
                boolean ballroom = level == 5 && roomX == 1 && roomZ == 0;

                // All blocks below the existing walking surface: do not
                // disturb floor finishes, water channels or crop/loot anchors.
                for (int y = FOUNDATION_BOTTOM; y <= FOUNDATION_TOP; y++) {
                    set(world, pos, x, y, z, foundation(level, y, rib, boiler));
                }
                // The old ceiling stays at Y=71. Build the entire roof above
                // it instead of replacing lamp, chandelier or pipe fixtures.
                for (int y = ROOF_BOTTOM; y <= ROOF_TOP; y++) {
                    set(world, pos, x, y, z, roof(level, y, rib, boiler, ballroom));
                }

                // Four very sparse supports in NON-ENTRY rooms. They sit near
                // cell corners, well away from shared-edge doors and central
                // navigation routes; never overwrite existing room content.
                boolean cornerX = localX == 3 || localX == roomSize - 4;
                boolean cornerZ = localZ == 3 || localZ == roomSize - 4;
                if ((roomX != 0 || roomZ != 0) && cornerX && cornerZ) {
                    for (int y = 64; y <= 70; y++) {
                        pos.set(x, y, z);
                        if (world.ensureCanWrite(pos) && world.getBlockState(pos).isAir()) {
                            world.setBlock(pos, support(level, y, boiler), 2);
                        }
                    }
                }
            }
        }
    }

    private static BlockState foundation(int level, int y, boolean rib, boolean boiler) {
        return switch (level) {
            // Level 2: heavy concrete utility tunnels with a service-frame grid.
            case 2 -> y <= 59 ? Blocks.COBBLED_DEEPSLATE.defaultBlockState()
                    : y == 60 ? Blocks.STONE_BRICKS.defaultBlockState()
                    : y == 61 && rib ? Blocks.IRON_BLOCK.defaultBlockState()
                    : y == 61 ? Blocks.ANDESITE.defaultBlockState()
                    : Blocks.GRAY_CONCRETE.defaultBlockState();
            // Level 3: brick and deepslate electrical-station foundations.
            case 3 -> y <= 59 ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                    : y == 60 ? Blocks.BRICKS.defaultBlockState()
                    : y == 61 && rib ? Blocks.COPPER_BLOCK.defaultBlockState()
                    : Blocks.POLISHED_DEEPSLATE.defaultBlockState();
            // Level 4: continuous bright office foundation with stone ribs.
            case 4 -> y <= 59 ? Blocks.STONE_BRICKS.defaultBlockState()
                    : y == 60 && rib ? Blocks.POLISHED_DIORITE.defaultBlockState()
                    : y <= 61 ? Blocks.SMOOTH_STONE.defaultBlockState()
                    : Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
            // Level 5: hotel timber over masonry; boiler bay remains stone.
            default -> boiler ? (y <= 60 ? Blocks.DEEPSLATE_BRICKS.defaultBlockState()
                    : Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState())
                    : y <= 59 ? Blocks.STONE_BRICKS.defaultBlockState()
                    : y == 60 && rib ? Blocks.DARK_OAK_LOG.defaultBlockState()
                    : y <= 61 ? Blocks.DARK_OAK_PLANKS.defaultBlockState()
                    : Blocks.SPRUCE_PLANKS.defaultBlockState();
        };
    }

    private static BlockState roof(
            int level, int y, boolean rib, boolean boiler, boolean ballroom) {
        return switch (level) {
            case 2 -> y == 72 ? Blocks.GRAY_CONCRETE.defaultBlockState()
                    : y == 73 ? Blocks.STONE_BRICKS.defaultBlockState()
                    : rib ? Blocks.COPPER_BLOCK.defaultBlockState()
                    : Blocks.POLISHED_ANDESITE.defaultBlockState();
            case 3 -> y == 72 ? Blocks.GRAY_CONCRETE.defaultBlockState()
                    : y == 73 ? Blocks.BRICKS.defaultBlockState()
                    : rib ? Blocks.IRON_BLOCK.defaultBlockState()
                    : Blocks.DEEPSLATE_BRICKS.defaultBlockState();
            case 4 -> y == 72 ? Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState()
                    : y == 73 ? Blocks.SMOOTH_STONE.defaultBlockState()
                    : rib ? Blocks.POLISHED_DIORITE.defaultBlockState()
                    : Blocks.WHITE_CONCRETE.defaultBlockState();
            default -> boiler ? (y == 72 ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                    : rib ? Blocks.COPPER_BLOCK.defaultBlockState()
                    : Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState())
                    : y == 72 ? Blocks.SPRUCE_PLANKS.defaultBlockState()
                    : ballroom && rib ? Blocks.GOLD_BLOCK.defaultBlockState()
                    : rib ? Blocks.DARK_OAK_LOG.defaultBlockState()
                    : Blocks.DARK_OAK_PLANKS.defaultBlockState();
        };
    }

    private static BlockState support(int level, int y, boolean boiler) {
        return switch (level) {
            case 2 -> y == 67 ? Blocks.COPPER_BLOCK.defaultBlockState()
                    : Blocks.POLISHED_ANDESITE.defaultBlockState();
            case 3 -> y == 68 ? Blocks.IRON_BLOCK.defaultBlockState()
                    : Blocks.DEEPSLATE_BRICKS.defaultBlockState();
            case 4 -> y == 70 ? Blocks.QUARTZ_BLOCK.defaultBlockState()
                    : Blocks.POLISHED_DIORITE.defaultBlockState();
            default -> boiler ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState()
                    : Blocks.DARK_OAK_LOG.defaultBlockState();
        };
    }

    private static void set(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                            int x, int y, int z, BlockState state) {
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos)) {
            world.setBlock(pos, state, 2);
        }
    }
}
