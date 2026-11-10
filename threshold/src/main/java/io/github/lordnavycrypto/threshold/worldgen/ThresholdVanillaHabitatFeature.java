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
 * Backrooms adaptation of the two RealScale vanilla-habitat structures.
 * All edits are chunk-local, deterministic, floor-supported and route-aware.
 */
public final class ThresholdVanillaHabitatFeature extends Feature<NoneFeatureConfiguration> {
    public enum Habitat { FALLEN_TIMBER, RIPARIAN_REEDS }

    private final Habitat habitat;

    public ThresholdVanillaHabitatFeature(Habitat habitat) {
        super(NoneFeatureConfiguration.CODEC);
        this.habitat = habitat;
    }

    private static boolean floor(BlockState state) {
        return ThresholdAnomalySurface.isNativeFloor(state);
    }

    private static boolean reserved(WorldGenLevel level, BlockPos pos) {
        String dimension = level.getLevel().dimension().location().getPath();
        int size = ThresholdAnomalySurface.roomSize(dimension);
        int rx = Math.floorDiv(pos.getX(), size), rz = Math.floorDiv(pos.getZ(), size);
        return ThresholdRoomLayoutFeature.isReservedRoomPath(
                level.getSeed(), size, rx, rz,
                Math.floorMod(pos.getX(), size), Math.floorMod(pos.getZ(), size), 2, true);
    }

    private static boolean fallenTimber(WorldGenLevel level, BlockPos origin, RandomSource random) {
        Direction.Axis axis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        int length = 3 + random.nextInt(3);
        int dx = axis == Direction.Axis.X ? 1 : 0;
        int dz = axis == Direction.Axis.Z ? 1 : 0;
        BlockPos[] logs = new BlockPos[length];
        for (int i = 0; i < length; i++) {
            BlockPos p = origin.offset((i - length / 2) * dx, 0, (i - length / 2) * dz);
            if (!ThresholdAnomalySurface.isReplaceableSurface(level,p) || reserved(level, p)
                    || !level.getBlockState(p.above()).isAir()) return false;
            logs[i] = p;
        }
        BlockState wood = Blocks.SPRUCE_LOG.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, axis);
        for (BlockPos p : logs) level.setBlock(p, wood, 2);
        for (BlockPos p : logs) {
            BlockPos top = p.above();
            if (random.nextInt(3) == 0 && level.getBlockState(top).isAir())
                level.setBlock(top, Blocks.MOSS_CARPET.defaultBlockState(), 2);
        }
        return true;
    }

    /**
     * A sealed five-by-five wetland pocket. Water sources sit inside a clay/mud
     * perimeter; reeds grow on the inner bank. No water column can escape.
     */
    private static boolean riparianReeds(WorldGenLevel level, BlockPos center, RandomSource random) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos surface = center.offset(dx, -1, dz);
                BlockPos reedSpace = center.offset(dx, 0, dz);
                if (!ThresholdAnomalySurface.isReplaceableSurface(level,surface)
                        || reserved(level,reedSpace)
                        || !level.getBlockState(reedSpace).isAir()
                        || !level.getFluidState(reedSpace).isEmpty()
                        || !level.getBlockState(reedSpace.above()).isAir()) return false;
            }
        }
        // A sealed 5x5 pool: replace only the supported surface cells and retain
        // a solid mud foundation beneath every water source.
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos surface = center.offset(dx, -1, dz);
                boolean edge = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                level.setBlock(surface.below(), Blocks.MUD.defaultBlockState(), 2);
                level.setBlock(surface, edge ? Blocks.MUD.defaultBlockState()
                        : Blocks.WATER.defaultBlockState(), 2);
            }
        }
        int placed = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (Math.abs(dx) + Math.abs(dz) != 1) continue;
                BlockPos bank = center.offset(dx * 2, -1, dz * 2);
                BlockPos reed = bank.above();
                if (level.getBlockState(reed).isAir()
                        && level.getBlockState(bank).is(Blocks.MUD)
                        && Blocks.SUGAR_CANE.defaultBlockState().canSurvive(level, reed)) {
                    level.setBlock(reed, Blocks.SUGAR_CANE.defaultBlockState(), 2);
                    placed++;
                }
            }
        }
        return placed > 0;
    }

    private boolean allowedLevel(String level) {
        return ThresholdAnomalySurface.isBackroomsDimension(level);
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
                ^ ((long) minZ * 0xC2B2AE3D27D4EB4FL) ^ ((long) habitat.ordinal() * 0x165667B19E3779F9L));
        if (!ThresholdAnomalySurface.shouldGenerate(dimension,site,23)) return false;
        BlockPos origin = new BlockPos(minX + 2 + (int) Math.floorMod(site >>> 8, 12L),
                ThresholdAnomalySurface.surfaceY(dimension),
                minZ + 2 + (int) Math.floorMod(site >>> 24, 12L));
        return habitat == Habitat.FALLEN_TIMBER
                ? fallenTimber(level, origin, context.random())
                : riparianReeds(level, origin.above(), context.random());
    }
}
