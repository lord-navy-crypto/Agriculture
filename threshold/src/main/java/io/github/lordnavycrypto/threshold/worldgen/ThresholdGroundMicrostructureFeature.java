package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Rare, compact, ground-supported ecological structures for existing vanilla
 * biomes. These decorations NEVER dig, alter fluids or modify river channels.
 *
 * <p>The entire footprint is validated before any block is placed. Invalid
 * sites leave the world unchanged, including when crossing a chunk border.</p>
 */
public final class ThresholdGroundMicrostructureFeature extends Feature<NoneFeatureConfiguration> {
    public enum Kind { OAK_LOG, SPRUCE_LOG, FOREST_HUMMOCK, UPLAND_SCREE, COASTAL_DRIFTWOOD }
    private final Kind kind;

    public GroundMicrostructureFeature(Kind kind) {
        super(NoneFeatureConfiguration.CODEC);
        this.kind = kind;
    }

    private boolean allowedLevel(String level) {
        return switch (kind) {
            case OAK_LOG, SPRUCE_LOG, FOREST_HUMMOCK -> level.equals("level_4");
            case UPLAND_SCREE -> level.equals("level_3");
            case COASTAL_DRIFTWOOD -> level.equals("level_5");
        };
    }

    private static int roomSizeForLevel(String level) {
        return level.equals("level_4") ? 48 : 32;
    }

    private static boolean reservedRoute(WorldGenLevel level, BlockPos pos) {
        String dimension = level.getLevel().dimension().location().getPath();
        int roomSize = roomSizeForLevel(dimension);
        int roomX = Math.floorDiv(pos.getX(), roomSize);
        int roomZ = Math.floorDiv(pos.getZ(), roomSize);
        return ThresholdRoomLayoutFeature.isReservedRoomPath(
                level.getSeed(), roomSize, roomX, roomZ,
                Math.floorMod(pos.getX(), roomSize), Math.floorMod(pos.getZ(), roomSize), 2, true);
    }

    private static boolean backroomsFloor(BlockState state) {
        return state.is(Blocks.GRAY_CONCRETE) || state.is(Blocks.DEEPSLATE_TILES)
                || state.is(Blocks.SMOOTH_STONE) || state.is(Blocks.DARK_OAK_PLANKS)
                || state.is(Blocks.POLISHED_ANDESITE);
    }

    private static long mix(long value) {
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        String dimension = level.getLevel().dimension().location().getPath();
        if (!allowedLevel(dimension)) return false;
        int minX = Math.floorDiv(context.origin().getX(), 16) * 16;
        int minZ = Math.floorDiv(context.origin().getZ(), 16) * 16;
        long site = mix(level.getSeed() ^ ((long) minX * 0x9E3779B97F4A7C15L)
                ^ ((long) minZ * 0xC2B2AE3D27D4EB4FL) ^ ((long) kind.ordinal() * 0x165667B19E3779F9L));
        if (Math.floorMod(site, 19L) != 0L) return false;
        BlockPos origin = new BlockPos(minX + 2 + (int) Math.floorMod(site >>> 8, 12L),
                65, minZ + 2 + (int) Math.floorMod(site >>> 24, 12L));
        RandomSource random = context.random();
        return switch (kind) {
            case OAK_LOG -> fallenLog(level, origin, random, Blocks.OAK_LOG.defaultBlockState());
            case SPRUCE_LOG -> fallenLog(level, origin, random, Blocks.SPRUCE_LOG.defaultBlockState());
            case FOREST_HUMMOCK -> smallHummock(level, origin, random);
            case UPLAND_SCREE -> smallScree(level, origin, random);
            case COASTAL_DRIFTWOOD -> coastalDriftwood(level, origin, random);
        };
    }

    private static boolean solidDrySupport(WorldGenLevel level, BlockPos ground) {
        BlockState state = level.getBlockState(ground);
        return state.isCollisionShapeFullBlock(level, ground)
                && level.getFluidState(ground).isEmpty()
                && backroomsFloor(state)
                && !reservedRoute(level, ground.above());
    }

    private static boolean emptyDrySpace(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // Replace only air or common undergrowth. Never overwrite flowers, roots,
        // water, leaf canopy, logs, structures, or block entities.
        boolean free = state.isAir();
        return free && level.getFluidState(pos).isEmpty()
                && level.getBlockState(pos.above()).isAir()
                && !reservedRoute(level, pos);
    }

    /**
     * An occasional shoreline log fully supported by sand, red sand, gravel
     * or rock. Entire structure is validated before writing a single block.
     * Never replaces ocean water, river water, leaves or existing structures.
     */
    private static boolean coastalDriftwood(WorldGenLevel level, BlockPos origin, RandomSource random) {
        boolean alongX = random.nextBoolean();
        int length = 2 + random.nextInt(3);
        int offset = length / 2;
        BlockState wood = (random.nextBoolean() ? Blocks.OAK_LOG : Blocks.SPRUCE_LOG)
                .defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS,
                        alongX ? Direction.Axis.X : Direction.Axis.Z);
        for (int i = 0; i < length; i++) {
            BlockPos p = origin.offset(alongX ? i - offset : 0, 0,
                    alongX ? 0 : i - offset);
            BlockPos ground = p.below();
            BlockState s = level.getBlockState(ground);
            if (!backroomsFloor(s)
                    || !s.isCollisionShapeFullBlock(level, ground)
                    || !level.getFluidState(ground).isEmpty()
                    || reservedRoute(level, p)
                    || !level.getBlockState(p).isAir()
                    || !level.getFluidState(p).isEmpty()
                    || !level.getBlockState(p.above()).isAir()) return false;
        }
        for (int i = 0; i < length; i++) {
            BlockPos p = origin.offset(alongX ? i - offset : 0, 0,
                    alongX ? 0 : i - offset);
            level.setBlock(p, wood, 2);
        }
        return true;
    }

    /** Complete horizontal log supported on every segment; no hovering or partial logs. */
    private static boolean fallenLog(WorldGenLevel level, BlockPos origin,
                                     RandomSource random, BlockState wood) {
        boolean alongX = random.nextBoolean();
        int length = 3 + random.nextInt(4); // 3..6, short enough for local generation
        int left = length / 2;
        BlockState axisWood = wood.setValue(RotatedPillarBlock.AXIS,
                alongX ? Direction.Axis.X : Direction.Axis.Z);
        for (int i = 0; i < length; ++i) {
            BlockPos p = origin.offset(alongX ? i-left : 0, 0, alongX ? 0 : i-left);
            if (!solidDrySupport(level, p.below()) || !emptyDrySpace(level, p))
                return false;
        }
        for (int i = 0; i < length; ++i) {
            BlockPos p = origin.offset(alongX ? i-left : 0, 0, alongX ? 0 : i-left);
            level.setBlock(p, axisWood, 2);
        }
        return true;
    }

    /** Non-carving root-like raised patch; preserves underlying soil and caves. */
    private static boolean smallHummock(WorldGenLevel level, BlockPos origin, RandomSource random) {
        // Only two blocks: center and a random cardinal neighboring block.
        BlockPos adjacent = switch (random.nextInt(4)) {
            case 0 -> origin.north();
            case 1 -> origin.south();
            case 2 -> origin.east();
            default -> origin.west();
        };
        for (BlockPos p : new BlockPos[]{origin, adjacent}) {
            BlockState under = level.getBlockState(p.below());
            if (!backroomsFloor(under) || !solidDrySupport(level, p.below())
                    || !emptyDrySpace(level, p)) return false;
        }
        level.setBlock(origin, Blocks.ROOTED_DIRT.defaultBlockState(), 2);
        level.setBlock(adjacent, Blocks.MOSS_BLOCK.defaultBlockState(), 2);
        return true;
    }

    /** Small, entirely supported, dry stone fragments. No basin or water edits. */
    private static boolean smallScree(WorldGenLevel level, BlockPos origin, RandomSource random) {
        BlockPos b = origin.offset(random.nextBoolean() ? 1 : -1, 0, 0);
        BlockPos c = origin.offset(0, 0, random.nextBoolean() ? 1 : -1);
        for (BlockPos p : new BlockPos[]{origin, b, c}) {
            if (!solidDrySupport(level, p.below()) || !emptyDrySpace(level, p))
                return false;
        }
        level.setBlock(origin, Blocks.COBBLESTONE.defaultBlockState(), 2);
        level.setBlock(b, Blocks.GRAVEL.defaultBlockState(), 2);
        level.setBlock(c, Blocks.STONE.defaultBlockState(), 2);
        return true;
    }
}
