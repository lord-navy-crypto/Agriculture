package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Backrooms adaptation of the 15 RealScale GrandEcology habitats.
 * Each is a rare, chunk-local pocket built from vanilla blocks; no feature
 * changes the maze's reserved routes or creates uncontained water.
 */
public final class ThresholdGrandEcologyFeature extends Feature<NoneFeatureConfiguration> {
    public enum Kind {
        TEMPERATE_RAINFOREST, SAVANNA_CANOPY, CHAPARRAL_SCRUB,
        GRASSLAND_MOSAIC, ALPINE_TUNDRA, FLOODPLAIN_BANK,
        TROPICAL_RAINFOREST, TEMPERATE_SEASONAL_FOREST, BOREAL_TAIGA,
        DESERT_OUTCROPS, ARCTIC_TUNDRA,
        FRESHWATER_BED, COASTAL_SEAGRASS, CORAL_REEF_FLOOR, ESTUARY_SHALLOWS
    }

    private final Kind kind;

    public ThresholdGrandEcologyFeature(Kind kind) {
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
        return switch (kind) {
            case FRESHWATER_BED, COASTAL_SEAGRASS, CORAL_REEF_FLOOR, ESTUARY_SHALLOWS ->
                    level.equals("level_5");
            default -> level.equals("level_4");
        };
    }

    private static int roomSize(String level) {
        return level.equals("level_4") ? 48 : 32;
    }

    private static boolean reserved(WorldGenLevel level, BlockPos pos, String dimension) {
        int size = roomSize(dimension);
        int rx = Math.floorDiv(pos.getX(), size), rz = Math.floorDiv(pos.getZ(), size);
        return ThresholdRoomLayoutFeature.isReservedRoomPath(
                level.getSeed(), size, rx, rz,
                Math.floorMod(pos.getX(), size), Math.floorMod(pos.getZ(), size), 2, true);
    }

    private static boolean floor(BlockState state) {
        return state.is(Blocks.SMOOTH_STONE) || state.is(Blocks.DARK_OAK_PLANKS)
                || state.is(Blocks.DEEPSLATE_TILES) || state.is(Blocks.GRAY_CONCRETE)
                || state.is(Blocks.LIGHT_GRAY_CONCRETE) || state.is(Blocks.WHITE_CONCRETE)
                || state.is(Blocks.POLISHED_DEEPSLATE) || state.is(Blocks.COBBLED_DEEPSLATE)
                || state.is(Blocks.SMOOTH_BASALT) || state.is(Blocks.SPRUCE_PLANKS)
                || state.is(Blocks.POLISHED_ANDESITE) || state.is(Blocks.CALCITE)
                || state.is(Blocks.POLISHED_BLACKSTONE) || state.is(Blocks.GRAVEL);
    }

    private static BlockState leaves(BlockState state) {
        return state.setValue(LeavesBlock.PERSISTENT, true);
    }

    private static boolean commit(WorldGenLevel level, int chunkX, int chunkZ,
                                   String dimension, Map<BlockPos, BlockState> plan) {
        for (Map.Entry<BlockPos, BlockState> entry : plan.entrySet()) {
            BlockPos p = entry.getKey();
            if (Math.floorDiv(p.getX(), 16) != chunkX || Math.floorDiv(p.getZ(), 16) != chunkZ)
                return false;
            if (reserved(level, p, dimension) || !level.getFluidState(p).isEmpty())
                return false;
            BlockState existing = level.getBlockState(p);
            if (p.getY() == 64) {
                if (!floor(existing)) return false;
            } else if (!existing.isAir()) {
                return false;
            }
        }
        plan.forEach((pos, state) -> level.setBlock(pos, state, 2));
        return !plan.isEmpty();
    }

    private static void addSoilPatch(Map<BlockPos, BlockState> plan, BlockPos origin,
                                     BlockState soil) {
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                plan.put(origin.offset(dx, -1, dz), soil);
    }

    private static boolean tree(WorldGenLevel level, BlockPos origin, int chunkX, int chunkZ,
                                String dimension, BlockState log, BlockState leaf,
                                int height, BlockState soil) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        addSoilPatch(plan, origin, soil);
        BlockState trunk = log.setValue(RotatedPillarBlock.AXIS, net.minecraft.core.Direction.Axis.Y);
        for (int y = 0; y < height; y++) plan.put(origin.above(y), trunk);
        for (int layer = height - 1; layer <= height; layer++) {
            int radius = layer == height + 1 ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) > radius + 1) continue;
                    BlockPos p = origin.offset(dx, layer, dz);
                    plan.putIfAbsent(p, leaves(leaf));
                }
            }
        }
        return commit(level, chunkX, chunkZ, dimension, plan);
    }

    private static boolean shrubs(WorldGenLevel level, BlockPos origin, int chunkX, int chunkZ,
                                  String dimension) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        addSoilPatch(plan, origin, Blocks.GRASS_BLOCK.defaultBlockState());
        BlockState canopy = leaves(Blocks.AZALEA_LEAVES.defaultBlockState());
        for (BlockPos p : new BlockPos[]{origin, origin.east(), origin.west(), origin.north(), origin.south()})
            plan.put(p, canopy);
        plan.put(origin.above(), canopy);
        return commit(level, chunkX, chunkZ, dimension, plan);
    }

    private static boolean grassland(WorldGenLevel level, BlockPos origin, int chunkX, int chunkZ,
                                     String dimension) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        addSoilPatch(plan, origin, Blocks.GRASS_BLOCK.defaultBlockState());
        plan.put(origin, Blocks.TALL_GRASS.defaultBlockState());
        plan.put(origin.east(), Blocks.FERN.defaultBlockState());
        plan.put(origin.west(), Blocks.SHORT_GRASS.defaultBlockState());
        plan.put(origin.north(), Blocks.DANDELION.defaultBlockState());
        plan.put(origin.south(), Blocks.POPPY.defaultBlockState());
        return commit(level, chunkX, chunkZ, dimension, plan);
    }

    private static boolean alpine(WorldGenLevel level, BlockPos origin, int chunkX, int chunkZ,
                                  String dimension) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        plan.put(origin.offset(-1, -1, 0), Blocks.CALCITE.defaultBlockState());
        plan.put(origin.offset(0, -1, 0), Blocks.STONE.defaultBlockState());
        plan.put(origin.offset(1, -1, 0), Blocks.ANDESITE.defaultBlockState());
        plan.put(origin.offset(0, -1, 1), Blocks.GRAVEL.defaultBlockState());
        plan.put(origin, Blocks.SNOW.defaultBlockState());
        plan.put(origin.east(), Blocks.SNOW_BLOCK.defaultBlockState());
        return commit(level, chunkX, chunkZ, dimension, plan);
    }

    private static boolean desert(WorldGenLevel level, BlockPos origin, int chunkX, int chunkZ,
                                  String dimension) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        addSoilPatch(plan, origin, Blocks.SAND.defaultBlockState());
        plan.put(origin, Blocks.DEAD_BUSH.defaultBlockState());
        plan.put(origin.east(), Blocks.CACTUS.defaultBlockState());
        plan.put(origin.east().above(), Blocks.CACTUS.defaultBlockState());
        return commit(level, chunkX, chunkZ, dimension, plan);
    }

    private static boolean arctic(WorldGenLevel level, BlockPos origin, int chunkX, int chunkZ,
                                  String dimension) {
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        plan.put(origin.offset(-1, -1, 0), Blocks.PACKED_ICE.defaultBlockState());
        plan.put(origin.offset(0, -1, 0), Blocks.SNOW_BLOCK.defaultBlockState());
        plan.put(origin.offset(1, -1, 0), Blocks.PACKED_ICE.defaultBlockState());
        plan.put(origin.offset(0, -1, 1), Blocks.GRAVEL.defaultBlockState());
        plan.put(origin, Blocks.SNOW.defaultBlockState());
        plan.put(origin.east(), Blocks.SNOW.defaultBlockState());
        return commit(level, chunkX, chunkZ, dimension, plan);
    }

    /**
     * A contained 5x5 pool. The water is supported by a mud foundation at Y=63,
     * and a solid retaining ring at Y=64 prevents any source from flowing away.
     */
    private static boolean waterPocket(WorldGenLevel level, BlockPos origin,
                                       int chunkX, int chunkZ, String dimension,
                                       BlockState ring, BlockState foundation,
                                       Kind kind) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos surface = origin.offset(dx, -1, dz);
                BlockPos air = origin.offset(dx, 0, dz);
                if (!floor(level.getBlockState(surface)) || !level.getBlockState(air).isAir()
                        || !level.getBlockState(surface.below()).isAir()
                        || !level.getFluidState(surface).isEmpty()
                        || reserved(level, air, dimension)) return false;
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos surface = origin.offset(dx, -1, dz);
                boolean edge = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                level.setBlock(surface.below(), foundation, 2);
                level.setBlock(surface, edge ? ring : Blocks.WATER.defaultBlockState(), 2);
            }
        }
        switch (kind) {
            case FRESHWATER_BED -> {
                level.setBlock(origin.offset(-1, -1, 0), Blocks.SEAGRASS.defaultBlockState(), 2);
                level.setBlock(origin.offset(1, -1, 0), Blocks.SEAGRASS.defaultBlockState(), 2);
            }
            case COASTAL_SEAGRASS -> {
                level.setBlock(origin.offset(0, -1, -1), Blocks.SEAGRASS.defaultBlockState(), 2);
                level.setBlock(origin.offset(0, -1, 1), Blocks.SEAGRASS.defaultBlockState(), 2);
                level.setBlock(origin.offset(1, -1, 0), Blocks.SEAGRASS.defaultBlockState(), 2);
            }
            case CORAL_REEF_FLOOR -> {
                level.setBlock(origin.offset(0, -1, 0), Blocks.BRAIN_CORAL_BLOCK.defaultBlockState(), 2);
                level.setBlock(origin.offset(1, -1, 0), Blocks.BRAIN_CORAL_FAN.defaultBlockState(), 2);
            }
            case ESTUARY_SHALLOWS -> {
                level.setBlock(origin.offset(-1, -1, 0), Blocks.MANGROVE_ROOTS.defaultBlockState(), 2);
                level.setBlock(origin.offset(1, -1, 0), Blocks.MANGROVE_ROOTS.defaultBlockState(), 2);
                level.setBlock(origin.offset(0, 0, -2), Blocks.SUGAR_CANE.defaultBlockState(), 2);
                level.setBlock(origin.offset(0, 0, 2), Blocks.SUGAR_CANE.defaultBlockState(), 2);
            }
            default -> { }
        }
        return true;
    }

    private boolean generate(WorldGenLevel level, BlockPos origin, int chunkX, int chunkZ,
                             String dimension) {
        return switch (kind) {
            case TEMPERATE_RAINFOREST -> tree(level, origin, chunkX, chunkZ, dimension,
                    Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LEAVES.defaultBlockState(), 5,
                    Blocks.MOSS_BLOCK.defaultBlockState());
            case SAVANNA_CANOPY -> tree(level, origin, chunkX, chunkZ, dimension,
                    Blocks.ACACIA_LOG.defaultBlockState(), Blocks.ACACIA_LEAVES.defaultBlockState(), 4,
                    Blocks.COARSE_DIRT.defaultBlockState());
            case CHAPARRAL_SCRUB -> shrubs(level, origin, chunkX, chunkZ, dimension);
            case GRASSLAND_MOSAIC -> grassland(level, origin, chunkX, chunkZ, dimension);
            case ALPINE_TUNDRA -> alpine(level, origin, chunkX, chunkZ, dimension);
            case FLOODPLAIN_BANK -> waterPocket(level, origin, chunkX, chunkZ, dimension,
                    Blocks.MUD.defaultBlockState(), Blocks.MUD.defaultBlockState(), Kind.ESTUARY_SHALLOWS);
            case TROPICAL_RAINFOREST -> tree(level, origin, chunkX, chunkZ, dimension,
                    Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState(), 5,
                    Blocks.MOSS_BLOCK.defaultBlockState());
            case TEMPERATE_SEASONAL_FOREST -> tree(level, origin, chunkX, chunkZ, dimension,
                    Blocks.OAK_LOG.defaultBlockState(), Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 4,
                    Blocks.GRASS_BLOCK.defaultBlockState());
            case BOREAL_TAIGA -> tree(level, origin, chunkX, chunkZ, dimension,
                    Blocks.SPRUCE_LOG.defaultBlockState(), Blocks.SPRUCE_LEAVES.defaultBlockState(), 5,
                    Blocks.PODZOL.defaultBlockState());
            case DESERT_OUTCROPS -> desert(level, origin, chunkX, chunkZ, dimension);
            case ARCTIC_TUNDRA -> arctic(level, origin, chunkX, chunkZ, dimension);
            case FRESHWATER_BED -> waterPocket(level, origin, chunkX, chunkZ, dimension,
                    Blocks.CLAY.defaultBlockState(), Blocks.MUD.defaultBlockState(), kind);
            case COASTAL_SEAGRASS -> waterPocket(level, origin, chunkX, chunkZ, dimension,
                    Blocks.SAND.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), kind);
            case CORAL_REEF_FLOOR -> waterPocket(level, origin, chunkX, chunkZ, dimension,
                    Blocks.CLAY.defaultBlockState(), Blocks.STONE.defaultBlockState(), kind);
            case ESTUARY_SHALLOWS -> waterPocket(level, origin, chunkX, chunkZ, dimension,
                    Blocks.MUD.defaultBlockState(), Blocks.MUD.defaultBlockState(), kind);
        };
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
        if (Math.floorMod(site, 67L) != 0L) return false;
        BlockPos origin = new BlockPos(minX + 3 + (int) Math.floorMod(site >>> 8, 10L),
                65, minZ + 3 + (int) Math.floorMod(site >>> 24, 10L));
        return generate(level, origin, Math.floorDiv(origin.getX(), 16),
                Math.floorDiv(origin.getZ(), 16), dimension);
    }
}
