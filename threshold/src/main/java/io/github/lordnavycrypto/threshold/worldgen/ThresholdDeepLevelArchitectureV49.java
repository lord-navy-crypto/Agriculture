package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Theme-specific architectural pass for new Level 2-5 chunks.
 *
 * Unlike the structural shell, these are visible room treatments: overhead
 * services, machine bays, office partition grammars, hotel ornament and a
 * partial raised ballroom balcony. Every column is addressed from world seed
 * and room coordinates and written only within the owning 16x16 chunk.
 *
 * Original walking floors (Y=63), ceilings (Y=71), doorway blocks, loot and
 * existing architectural detail are not overwritten. The central cross and
 * room borders are kept open for existing transit and irregular doorway paths.
 */
public final class ThresholdDeepLevelArchitectureV49 {
    private ThresholdDeepLevelArchitectureV49() {}

    public static void build(WorldGenLevel world, int chunkX, int chunkZ, int level) {
        if (level < 2 || level > 5) {
            throw new IllegalArgumentException("Theme pass supports Levels 2-5 only");
        }
        final int size = level == 4 ? 48 : 32;
        final int originX = chunkX << 4;
        final int originZ = chunkZ << 4;
        final long seed = world.getSeed();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < 16; dx++) {
            int x = originX + dx;
            int roomX = Math.floorDiv(x, size);
            int lx = Math.floorMod(x, size);
            for (int dz = 0; dz < 16; dz++) {
                int z = originZ + dz;
                int roomZ = Math.floorDiv(z, size);
                int lz = Math.floorMod(z, size);
                int variant = themeVariant(seed, roomX, roomZ, level);
                boolean entry = roomX == 0 && roomZ == 0;
                boolean inside = lx >= 5 && lx <= size - 6
                        && lz >= 5 && lz <= size - 6;
                // Shared-edge doors may vary from room to room. Avoid the
                // entire 5-block perimeter rather than assuming a door center.
                boolean walk = Math.abs(lx - size / 2) <= 4
                        || Math.abs(lz - size / 2) <= 4;
                boolean alcove = inside && !walk;

                switch (level) {
                    case 2 -> utilityHalls(world, pos, x, z, lx, lz, size,
                            variant, entry, inside, alcove);
                    case 3 -> electricalStation(world, pos, x, z, lx, lz, size,
                            variant, entry, inside, alcove);
                    case 4 -> officeComplex(world, pos, x, z, lx, lz, size,
                            variant, entry, inside, alcove);
                    case 5 -> terrorHotel(world, pos, x, z, lx, lz, size,
                            roomX, roomZ, variant, entry, inside, alcove);
                    default -> throw new IllegalStateException("Unsupported level");
                }
            }
        }
    }

    /** Level 2: utility tunnel steel arches, pipe banks, isolated pressure rooms. */
    private static void utilityHalls(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int z, int lx, int lz, int size, int variant,
            boolean entry, boolean inside, boolean alcove) {
        if (!inside) return;
        // Continuous-looking overhead mains stay several blocks above players.
        if ((lz == 7 || lz == size - 8) && lx >= 6 && lx <= size - 7) {
            air(world, pos, x, 69, z, variant == 2
                    ? Blocks.IRON_BLOCK.defaultBlockState()
                    : Blocks.COPPER_BLOCK.defaultBlockState());
        }
        if ((lx == 7 || lx == size - 8) && lz >= 7 && lz <= size - 8
                && (lz % 4 == 0)) {
            air(world, pos, x, 68, z, Blocks.IRON_BARS.defaultBlockState());
        }
        // Service catwalk spans the central cross at Y=68, leaving four
        // blocks of clearance below; the low stair is outside that cross.
        if (!entry && variant == 1 && (lx == 9 || lx == 10)
                && lz >= 7 && lz <= size - 8) {
            int deckY = lz <= 11 ? 64 + (lz - 7) : 68;
            air(world, pos, x, deckY, z, lz <= 11
                    ? Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                            .setValue(StairBlock.FACING, Direction.SOUTH)
                    : Blocks.IRON_BLOCK.defaultBlockState());
        }
        // Guard both raised catwalk edges; ground passage remains open.
        if (!entry && variant == 1 && (lx == 8 || lx == 11)
                && lz >= 12 && lz <= size - 8)
            air(world, pos, x, 69, z, Blocks.IRON_BARS.defaultBlockState());
        if (!alcove) return;

        // Rows of columns physically connect floor to a suspended pipe arch,
        // unlike the old single-block ceiling ornament.
        if ((lx == 6 || lx == size - 7) && (lz == 7 || lz == size - 8)) {
            for (int y = 64; y <= 68; y++)
                air(world, pos, x, y, z, y == 67
                        ? Blocks.COPPER_BLOCK.defaultBlockState()
                        : Blocks.POLISHED_ANDESITE.defaultBlockState());
        }
        if (entry) return; // Starter room keeps clear low-level circulation.
        switch (variant) {
            case 0 -> {
                // Deep maintenance niche with isolated valves and inspection grilles.
                if ((lx == 9 || lx == size - 10) && (lz == 9 || lz == size - 10)) {
                    air(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.COPPER_BLOCK.defaultBlockState());
                    air(world, pos, x, 66, z, Blocks.IRON_TRAPDOOR.defaultBlockState());
                }
                if ((lx == 10 || lx == size - 11) && (lz == 8 || lz == size - 9))
                    air(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());
            }
            case 1 -> {
                // Two-sided service catwalk. Staggered steps connect Y=64
                // to an overhead bridge at Y=68, with Y=69..70 clear.
                if ((lx == 9 || lx == 10) && (lz == 12 || lz == size - 9)) {
                    for (int y = 64; y <= 67; y++)
                        air(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                }
            }
            case 2 -> {
                // Oblique utility cable frames around the old central routes.
                if ((lx == 11 || lx == size - 12)
                        && lz >= 7 && lz <= size - 8 && lz % 5 == 0) {
                    air(world, pos, x, 66, z, Blocks.DEEPSLATE_BRICKS.defaultBlockState());
                    air(world, pos, x, 67, z, Blocks.IRON_BARS.defaultBlockState());
                }
            }
            default -> {
                // Dark sealed sump galleries and suspended machinery supports.
                if ((lx == 8 || lx == size - 9) && (lz == 8 || lz == size - 9)) {
                    for (int y = 64; y <= 67; y++)
                        air(world, pos, x, y, z, Blocks.SMOOTH_BASALT.defaultBlockState());
                }
                if ((lx == 10 || lx == size - 11) && lz % 6 == 0)
                    air(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
            }
        }
    }

    /** Level 3: active-looking transformer bay, cable tray and switch room. */
    private static void electricalStation(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int z, int lx, int lz, int size, int variant,
            boolean entry, boolean inside, boolean alcove) {
        if (!inside) return;
        if ((lx == 7 || lx == size - 8) && lz >= 7 && lz <= size - 8) {
            air(world, pos, x, 69, z, Blocks.IRON_BLOCK.defaultBlockState());
            if (lz % 6 == 0) air(world, pos, x, 68, z,
                    Blocks.COPPER_BLOCK.defaultBlockState());
        }
        if ((lz == 7 || lz == size - 8) && lx % 8 == 0) {
            air(world, pos, x, 69, z, Blocks.SEA_LANTERN.defaultBlockState());
        }
        if (!alcove) return;
        if ((lx == 6 || lx == size - 7) && (lz == 6 || lz == size - 7)) {
            for (int y = 64; y <= 69; y++) {
                air(world, pos, x, y, z, y == 68
                        ? Blocks.COPPER_BLOCK.defaultBlockState()
                        : Blocks.DEEPSLATE_BRICKS.defaultBlockState());
            }
        }
        if (entry) return;
        switch (variant) {
            case 0 -> {
                // Two transformer banks separated by the original central aisle.
                if ((lx == 9 || lx == size - 10)
                        && lz >= 8 && lz <= 11) {
                    air(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.COPPER_BLOCK.defaultBlockState());
                    air(world, pos, x, 66, z, Blocks.IRON_BLOCK.defaultBlockState());
                }
                if ((lx == 8 || lx == size - 9) && lz >= 8 && lz <= 11)
                    air(world, pos, x, 65, z, Blocks.IRON_BARS.defaultBlockState());
            }
            case 1 -> {
                // Switchgear wall and fault-warning windows.
                if ((lx == 9 || lx == size - 10) && lz >= 7 && lz <= 12) {
                    air(world, pos, x, 64, z, Blocks.GRAY_CONCRETE.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                    air(world, pos, x, 66, z, Blocks.REDSTONE_LAMP.defaultBlockState());
                }
            }
            case 2 -> {
                // Wire cage around isolated machine islands, not across exits.
                if ((lx == 10 || lx == size - 11)
                        && (lz == 8 || lz == size - 9)) {
                    for (int y = 64; y <= 66; y++)
                        air(world, pos, x, y, z, Blocks.IRON_BARS.defaultBlockState());
                    air(world, pos, x, 67, z, Blocks.COPPER_BLOCK.defaultBlockState());
                }
                if ((lx == 11 || lx == size - 12) && lz % 5 == 0)
                    air(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
            }
            default -> {
                // Red emergency-lit plant room / switchbay columns.
                if ((lx == 8 || lx == size - 9) && (lz == 10 || lz == size - 11)) {
                    for (int y = 64; y <= 67; y++)
                        air(world, pos, x, y, z, Blocks.POLISHED_DEEPSLATE.defaultBlockState());
                    air(world, pos, x, 68, z, Blocks.REDSTONE_LAMP.defaultBlockState());
                }
            }
        }
    }

    /** Level 4: pale coffer ceilings, readable office pods and restrained fixtures. */
    private static void officeComplex(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int z, int lx, int lz, int size, int variant,
            boolean entry, boolean inside, boolean alcove) {
        if (!inside) return;
        // False-ceiling grid is always above the player's normal headspace.
        if (lx % 8 == 0 || lz % 8 == 0) {
            air(world, pos, x, 69, z, Blocks.WHITE_CONCRETE.defaultBlockState());
        }
        if (lx % 16 == 8 && lz % 16 == 8) {
            air(world, pos, x, 70, z, Blocks.SEA_LANTERN.defaultBlockState());
        }
        if (!alcove) return;
        // Wide clear cross separates quadrants: no enclosed pods or blocked doors.
        if (entry) {
            if ((lx == 6 || lx == size - 7) && (lz == 6 || lz == size - 7))
                for (int y = 64; y <= 68; y++)
                    air(world, pos, x, y, z, Blocks.QUARTZ_BLOCK.defaultBlockState());
            return;
        }
        switch (variant) {
            case 0 -> {
                // Repeated half-height cubicle partitions with glass top edges.
                if ((lx == 10 || lx == size - 11)
                        && lz >= 7 && lz <= size - 8
                        && lz % 9 != 0) {
                    air(world, pos, x, 64, z, Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.WHITE_STAINED_GLASS.defaultBlockState());
                }
                if ((lx == 8 || lx == size - 9) && lz % 9 == 3)
                    air(world, pos, x, 64, z, Blocks.OAK_SLAB.defaultBlockState());
            }
            case 1 -> {
                // Archive shelves kept against side walls, without crossing a route.
                if ((lz == 7 || lz == size - 8) && lx % 5 <= 2) {
                    air(world, pos, x, 64, z, Blocks.BOOKSHELF.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.BOOKSHELF.defaultBlockState());
                }
                if ((lx == 7 || lx == size - 8) && lz % 7 == 0)
                    air(world, pos, x, 64, z, Blocks.POTTED_FERN.defaultBlockState());
            }
            case 2 -> {
                // Conference booths with pale glass sections and long counters.
                if ((lz == 10 || lz == size - 11)
                        && lx >= 8 && lx <= size - 9) {
                    air(world, pos, x, 64, z, Blocks.POLISHED_DIORITE.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.WHITE_STAINED_GLASS.defaultBlockState());
                }
                if ((lz == 8 || lz == size - 9) && lx % 4 == 0)
                    air(world, pos, x, 64, z, Blocks.SMOOTH_STONE_SLAB.defaultBlockState());
            }
            default -> {
                // Unnaturally blank copy-room modules with a few black monitors.
                if ((lx == 8 || lx == size - 9) && (lz == 8 || lz == size - 9)) {
                    air(world, pos, x, 64, z, Blocks.WHITE_CONCRETE.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                }
                if ((lz == 10 || lz == size - 11) && lx % 5 == 0)
                    air(world, pos, x, 64, z, Blocks.QUARTZ_SLAB.defaultBlockState());
            }
        }
    }

    /**
     * Level 5: distinguish red Main Hall, Beverly ballroom, boiler service
     * plant and guest wings; no changes to elevator/door coordinates.
     */
    private static void terrorHotel(WorldGenLevel world, BlockPos.MutableBlockPos pos,
            int x, int z, int lx, int lz, int size, int roomX, int roomZ,
            int variant, boolean entry, boolean inside, boolean alcove) {
        if (!inside) return;
        boolean beverly = roomX == 1 && roomZ == 0;
        boolean boiler = roomX == 0 && roomZ == 1;
        // Dark decorative ceiling coffer lines, plus period-style corner trim.
        if (lx % 8 == 0 || lz % 8 == 0) {
            air(world, pos, x, 69, z, boiler
                    ? Blocks.COPPER_BLOCK.defaultBlockState()
                    : beverly ? Blocks.RED_NETHER_BRICKS.defaultBlockState()
                    : Blocks.DARK_OAK_PLANKS.defaultBlockState());
        }
        if ((lx == 7 || lx == size - 8) && (lz == 7 || lz == size - 8)) {
            for (int y = 64; y <= 69; y++)
                air(world, pos, x, y, z, boiler
                        ? Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState()
                        : y == 68 ? Blocks.GOLD_BLOCK.defaultBlockState()
                        : Blocks.DARK_OAK_LOG.defaultBlockState());
        }
        // The Beverly balcony is deliberately continuous across the center
        // of the room, but only at Y=68, above all ground-level door routes.
        // The approach steps occupy the safe side wall zone.
        if (beverly && (lx == 5 || lx == 6)
                && lz >= 6 && lz <= size - 7) {
            int landing = lz <= 10 ? 64 + lz - 6 : 68;
            air(world, pos, x, landing, z, lz <= 10
                    ? Blocks.SPRUCE_STAIRS.defaultBlockState()
                            .setValue(StairBlock.FACING, Direction.SOUTH)
                    : Blocks.DARK_OAK_PLANKS.defaultBlockState());
        }
        // Raised balcony guardrail stays above the ground-level walkways.
        if (beverly && lx == 7 && lz >= 12 && lz <= size - 8)
            air(world, pos, x, 69, z, Blocks.DARK_OAK_FENCE.defaultBlockState());
        if (!alcove) return;
        if (entry) return; // Original Main Hall elevator and exits stay clear.

        if (boiler) {
            // Wide boiler banks, pipe elbows and overhead maintenance chains.
            if ((lx == 9 || lx == size - 10) && (lz == 9 || lz == size - 10)) {
                air(world, pos, x, 64, z, Blocks.IRON_BLOCK.defaultBlockState());
                air(world, pos, x, 65, z, Blocks.COPPER_BLOCK.defaultBlockState());
                air(world, pos, x, 66, z, Blocks.IRON_BLOCK.defaultBlockState());
            }
            if ((lx == 11 || lx == size - 12) && lz % 5 == 0)
                air(world, pos, x, 68, z, Blocks.CHAIN.defaultBlockState());
            return;
        }
        if (beverly) {
            // A partial two-wide raised ballroom balcony (Y=68), approached
            // by five short steps from the existing Y=63 floor. Its platform
            // stays outside the central chandelier and travel cross.
            if (lx == 7 && (lz == 11 || lz == size - 8)) {
                for (int y = 64; y <= 67; y++)
                    air(world, pos, x, y, z, Blocks.DARK_OAK_LOG.defaultBlockState());
            }
            if ((lz == 9 || lz == size - 10) && (lx == 9 || lx == size - 10))
                air(world, pos, x, 68, z, Blocks.GOLD_BLOCK.defaultBlockState());
            return;
        }

        switch (variant) {
            case 0 -> {
                // Red corridor gallery with rhythmic antique timber buttresses.
                if ((lx == 9 || lx == size - 10)
                        && (lz == 9 || lz == size - 10)) {
                    for (int y = 64; y <= 68; y++)
                        air(world, pos, x, y, z, Blocks.DARK_OAK_LOG.defaultBlockState());
                    air(world, pos, x, 69, z, Blocks.GOLD_BLOCK.defaultBlockState());
                }
            }
            case 1 -> {
                // Guest-suite sleeping niches in the side quadrants.
                if ((lz == 9 || lz == size - 10)
                        && lx >= 7 && lx <= 12)
                    air(world, pos, x, 64, z, Blocks.RED_CARPET.defaultBlockState());
                if ((lx == 8 || lx == size - 9) && (lz == 10 || lz == size - 11))
                    air(world, pos, x, 64, z, Blocks.BOOKSHELF.defaultBlockState());
            }
            case 2 -> {
                // Repeating mirror-like corridor partitions, deliberately sparse.
                if ((lx == 9 || lx == size - 10)
                        && lz % 7 >= 1 && lz % 7 <= 3) {
                    air(world, pos, x, 64, z, Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                    air(world, pos, x, 66, z, Blocks.BLACK_STAINED_GLASS.defaultBlockState());
                }
            }
            default -> {
                // Rare abandoned lobby / white stone and dim gilded trims.
                if ((lx == 9 || lx == size - 10) && lz % 6 == 0) {
                    air(world, pos, x, 64, z, Blocks.QUARTZ_BLOCK.defaultBlockState());
                    air(world, pos, x, 65, z, Blocks.GOLD_BLOCK.defaultBlockState());
                }
            }
        }
    }

    /**
     * Guarantee theme discoveries in the near-entry room ring while
     * retaining seed-deterministic variation elsewhere. Hotel's fixed
     * Beverly and Boiler sectors stay assigned by the original generator.
     */
    private static int themeVariant(long seed, int roomX, int roomZ, int level) {
        if (roomX == 0 && roomZ == 0) return 0;
        if (level == 5) {
            if (roomX == 2 && roomZ == 0) return 0;
            if (roomX == 0 && roomZ == 2) return 1;
            if (roomX == 2 && roomZ == 1) return 2;
            if (roomX == 1 && roomZ == 1) return 3;
        } else {
            if (roomX == 1 && roomZ == 0) return 0;
            if (roomX == 0 && roomZ == 1) return 1;
            if (roomX == 1 && roomZ == 1) return 2;
            if (roomX == 2 && roomZ == 0) return 3;
        }
        return Math.floorMod(mix(seed, roomX, roomZ, 0x49A11L + level * 29L), 4);
    }

    /** Stable room selection with no chunk-order or client randomness. */
    private static long mix(long seed, int roomX, int roomZ, long salt) {
        long n = seed ^ salt
                ^ ((long) roomX * 0x9E3779B97F4A7C15L)
                ^ ((long) roomZ * 0xC2B2AE3D27D4EB4FL);
        n ^= n >>> 30;
        n *= 0xBF58476D1CE4E5B9L;
        n ^= n >>> 27;
        n *= 0x94D049BB133111EBL;
        return n ^ (n >>> 31);
    }

    private static void air(WorldGenLevel world, BlockPos.MutableBlockPos pos,
                            int x, int y, int z, BlockState state) {
        // Add only in existing air. Avoid replacing all original props,
        // transit doors, lights and material-driven floor architecture.
        pos.set(x, y, z);
        if (world.ensureCanWrite(pos) && world.getBlockState(pos).isAir()) {
            world.setBlock(pos, state, 2);
        }
    }
}
