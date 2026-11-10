package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Backrooms adaptation of the two RealScale cave-wonder networks. The
 * continuous centerline cuts only authored wall material, never floors,
 * ceilings, transition gates, water or reserved maze routes.
 */
public final class ThresholdCaveWonderNetworkFeature extends Feature<NoneFeatureConfiguration> {
    public enum Kind { LAVA_TUBE, KARST_PASSAGE }

    private static final int CELL = 256;
    private final Kind kind;

    public ThresholdCaveWonderNetworkFeature(Kind kind) {
        super(NoneFeatureConfiguration.CODEC);
        this.kind = kind;
    }

    private static long mix(long value) {
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    private boolean allowedLevel(String level) {
        return kind == Kind.LAVA_TUBE
                ? level.equals("level_2") || level.equals("level_3")
                : level.equals("level_3") || level.equals("level_4");
    }

    private boolean acceptable(BlockState state) {
        if (kind == Kind.LAVA_TUBE) {
            return state.is(Blocks.GRAY_CONCRETE) || state.is(Blocks.DEEPSLATE_TILES)
                    || state.is(Blocks.DEEPSLATE_BRICKS) || state.is(Blocks.SMOOTH_BASALT)
                    || state.is(Blocks.POLISHED_ANDESITE) || state.is(Blocks.CRACKED_STONE_BRICKS)
                    || state.is(Blocks.BRICKS);
        }
        return state.is(Blocks.DEEPSLATE_TILES) || state.is(Blocks.DEEPSLATE_BRICKS)
                || state.is(Blocks.SMOOTH_STONE) || state.is(Blocks.POLISHED_ANDESITE)
                || state.is(Blocks.GRAY_CONCRETE) || state.is(Blocks.WHITE_CONCRETE)
                || state.is(Blocks.LIGHT_GRAY_CONCRETE) || state.is(Blocks.BRICKS);
    }

    private static boolean reserved(WorldGenLevel world, BlockPos pos, String level) {
        int size = level.equals("level_4") ? 48 : 32;
        int rx = Math.floorDiv(pos.getX(), size), rz = Math.floorDiv(pos.getZ(), size);
        return ThresholdRoomLayoutFeature.isReservedRoomPath(
                world.getSeed(), size, rx, rz,
                Math.floorMod(pos.getX(), size), Math.floorMod(pos.getZ(), size), 2, true);
    }

    private static boolean dry(WorldGenLevel world, BlockPos pos) {
        if (!world.getFluidState(pos).isEmpty()) return false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) != 1) continue;
                    if (!world.getFluidState(pos.offset(dx, dy, dz)).isEmpty()) return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        String level = world.getLevel().dimension().location().getPath();
        if (!allowedLevel(level)) return false;

        int minX = Math.floorDiv(context.origin().getX(), 16) * 16;
        int minZ = Math.floorDiv(context.origin().getZ(), 16) * 16;
        int gridX = (int) Math.floorDiv((long) minX + 8 + CELL / 2, CELL);
        int gridZ = (int) Math.floorDiv((long) minZ + 8 + CELL / 2, CELL);
        boolean changed = false;

        for (int gx = gridX - 1; gx <= gridX + 1; gx++) {
            for (int gz = gridZ - 1; gz <= gridZ + 1; gz++) {
                long salt = kind == Kind.LAVA_TUBE ? 0x9876AFL : 0x72CA51L;
                long hash = mix(world.getSeed() ^ (long) gx * 0x9E3779B97F4A7C15L
                        ^ (long) gz * 0xC2B2AE3D27D4EB4FL ^ salt);
                if (Math.floorMod(hash, 31L) != 0L) continue;

                int cx = gx * CELL + (int) Math.floorMod(hash >>> 13, 65L) - 32;
                int cz = gz * CELL + (int) Math.floorMod(hash >>> 25, 65L) - 32;
                boolean horizontal = (hash & 1L) == 0L;
                int length = 56 + (int) Math.floorMod(hash >>> 18, 32L);
                if (cx - length > minX + 16 || cx + length < minX
                        || cz - length > minZ + 16 || cz + length < minZ) continue;

                for (int x = minX; x < minX + 16; x++) {
                    for (int z = minZ; z < minZ + 16; z++) {
                        int along = horizontal ? x - cx : z - cz;
                        int side = horizontal ? z - cz : x - cx;
                        if (Math.abs(along) > length) continue;
                        double deviation = Math.sin(along * 0.080 + ((hash >>> 8) % 15)) * 1.5;
                        if (Math.abs(side - deviation) > 1.55) continue;

                        int y = 65;
                        BlockPos foot = new BlockPos(x, y, z);
                        BlockPos head = foot.above();
                        BlockPos roof = foot.above(4);
                        if (!acceptable(world.getBlockState(roof))) continue;
                        if (reserved(world, foot, level) || reserved(world, head, level)) continue;

                        BlockState f = world.getBlockState(foot);
                        BlockState h = world.getBlockState(head);
                        if ((!f.isAir() && !acceptable(f)) || (!h.isAir() && !acceptable(h))) continue;
                        if (!dry(world, foot) || !dry(world, head)) continue;

                        if (!f.isAir()) changed |= world.setBlock(foot,
                                Blocks.CAVE_AIR.defaultBlockState(), 2);
                        if (!h.isAir()) changed |= world.setBlock(head,
                                Blocks.CAVE_AIR.defaultBlockState(), 2);

                        if (Math.floorMod((long) x * 29 + z * 17, 11) == 0) {
                            BlockPos lining = foot.above(2);
                            BlockState state = world.getBlockState(lining);
                            if (acceptable(state) && dry(world, lining)) {
                                world.setBlock(lining, kind == Kind.LAVA_TUBE
                                        ? Blocks.SMOOTH_BASALT.defaultBlockState()
                                        : Blocks.CALCITE.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }
}
