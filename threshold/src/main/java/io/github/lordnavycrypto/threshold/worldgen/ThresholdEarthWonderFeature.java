package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Natural Wonders manufacturing batch B/C: eight small, real-world-inspired
 * geological structures. The larger landform shape still belongs to the
 * existing density, hydrology and biome layers; do not mistake these accents
 * for large-scale basin, glacier, delta or lava-tube simulations.
 *
 * Enabled by the Backrooms room sequence on all six dimensions. Placement is
 * chunk-local and deterministic; it never carves floors or overwrites water.
 */
public final class ThresholdEarthWonderFeature extends Feature<NoneFeatureConfiguration> {
    public enum Kind {
        HOODOO_SPIRES, TRAVERTINE_TERRACES, MANGROVE_TIDAL_ROOTS,
        COOLED_LAVA_FIELD, LAVA_TUBE_LINING, GLACIAL_MORAINE,
        DELTA_SEDIMENT, KARST_SINKHOLE_RIM
    }
    private final Kind kind;

    public ThresholdEarthWonderFeature(Kind kind) {
        super(NoneFeatureConfiguration.CODEC);
        this.kind = kind;
    }

    private static boolean nativeFloor(BlockState state) {
        return ThresholdAnomalySurface.isNativeFloor(state);
    }

    private static int roomSizeForLevel(String level) {
        return ThresholdAnomalySurface.roomSize(level);
    }

    private static boolean reservedRoute(WorldGenLevel world, BlockPos pos, String level) {
        int roomSize = roomSizeForLevel(level);
        int roomX = Math.floorDiv(pos.getX(), roomSize);
        int roomZ = Math.floorDiv(pos.getZ(), roomSize);
        int localX = Math.floorMod(pos.getX(), roomSize);
        int localZ = Math.floorMod(pos.getZ(), roomSize);
        return ThresholdRoomLayoutFeature.isReservedRoomPath(
                world.getSeed(), roomSize, roomX, roomZ, localX, localZ, 2, true);
    }

    private boolean allowedLevel(String level) {
        return ThresholdAnomalySurface.isBackroomsDimension(level);
    }

    private boolean compatible(BlockState base) {
        return nativeFloor(base);
    }

    private static long mix(long value) {
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
    private static boolean inChunk(int x,int z,int cx,int cz) {
        return Math.floorDiv(x,16)==cx && Math.floorDiv(z,16)==cz;
    }
    private static boolean waterNearby(WorldGenLevel world,BlockPos pos,int cx,int cz) {
        final int[] dx={1,-1,0,0},dz={0,0,1,-1};
        for (int i=0;i<4;i++) {
            int x=pos.getX()+dx[i],z=pos.getZ()+dz[i];
            if (inChunk(x,z,cx,cz) &&
                    (!world.getFluidState(new BlockPos(x,pos.getY(),z)).isEmpty()
                    || !world.getFluidState(new BlockPos(x,pos.getY()-1,z)).isEmpty()))
                return true;
        }
        return false;
    }
    private static boolean emptyColumn(WorldGenLevel world,BlockPos p,int height) {
        for (int y=0;y<height;y++) {
            BlockPos at=p.above(y);
            if (!world.getBlockState(at).isAir()
                    || !world.getFluidState(at).isEmpty())return false;
        }
        return true;
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world=context.level();
        String level=world.getLevel().dimension().location().getPath();
        if (!allowedLevel(level)) return false;
        int minX=Math.floorDiv(context.origin().getX(),16)*16;
        int minZ=Math.floorDiv(context.origin().getZ(),16)*16;
        long site=mix(world.getSeed() ^ ((long)minX*0x9E3779B97F4A7C15L)
                ^ ((long)minZ*0xC2B2AE3D27D4EB4FL) ^ ((long)kind.ordinal()*0x165667B19E3779F9L));
        if (!ThresholdAnomalySurface.shouldGenerate(level, site, 29)) return false;
        int cx=minX+2+(int)Math.floorMod(site>>>9,12L);
        int cz=minZ+2+(int)Math.floorMod(site>>>25,12L);
        int y=ThresholdAnomalySurface.surfaceY(level);
        BlockPos center=new BlockPos(cx,y,cz);
        int chunkX=Math.floorDiv(center.getX(),16),chunkZ=Math.floorDiv(center.getZ(),16);

        if (kind==Kind.LAVA_TUBE_LINING) {
            // Existing cave only: line an air-filled recess, never excavate.
            if (!centerIsReady(world,center) || reservedRoute(world,center,level)) return false;
            return world.setBlock(center,Blocks.SMOOTH_BASALT.defaultBlockState(),2);
        }

        BlockState floor=ThresholdAnomalySurface.supportingBlock(world,center);
        if (!centerIsReady(world,center) || !compatible(floor) || reservedRoute(world,center,level))
            return false;
        int radius=switch(kind) {
            case HOODOO_SPIRES -> 5;
            case TRAVERTINE_TERRACES -> 4;
            case COOLED_LAVA_FIELD -> 6;
            default -> 3;
        };
        boolean placed=false;
        for (int dx=-radius;dx<=radius;dx++) for(int dz=-radius;dz<=radius;dz++) {
            int distance=dx*dx+dz*dz;
            if (distance>radius*radius)continue;
            int x=center.getX()+dx,z=center.getZ()+dz;
            if (!inChunk(x,z,chunkX,chunkZ))continue;
            int pattern=Math.floorMod(x*31+z*17,11);
            if (pattern==0 || pattern==7)continue;
            BlockPos p=new BlockPos(x,y,z);
            if (!centerIsReady(world,p) || reservedRoute(world,p,level))continue;
            BlockState under=world.getBlockState(p.below());
            if (!compatible(under))continue;
            BlockState material=null;
            int height=1;
            switch(kind) {
                case HOODOO_SPIRES -> {
                    // Erosion remnants: sparse clustered, height-varied spires.
                    if ((dx*3+dz*5)%4!=0)continue;
                    height=2+Math.floorMod(x*13+z*19,5);
                    material=(pattern%3==0?Blocks.RED_SANDSTONE:Blocks.SANDSTONE)
                            .defaultBlockState();
                }
                case TRAVERTINE_TERRACES -> {
                    height=distance<=2?3:distance<=8?2:1;
                    material=Blocks.CALCITE.defaultBlockState();
                }
                case MANGROVE_TIDAL_ROOTS -> {
                    // Roots on naturally muddy banks, never fill tidal water.
                    if (pattern>4)continue;
                    material=Blocks.MANGROVE_ROOTS.defaultBlockState();
                }
                case COOLED_LAVA_FIELD -> {
                    material=(pattern<4?Blocks.BASALT:
                              pattern<8?Blocks.SMOOTH_BASALT:Blocks.TUFF)
                              .defaultBlockState();
                }
                case GLACIAL_MORAINE -> {
                    if (Math.abs(dx+dz)>2)continue;
                    material=(pattern<8?Blocks.GRAVEL:Blocks.PACKED_ICE)
                            .defaultBlockState();
                }
                case DELTA_SEDIMENT -> {
                    if (Math.floorMod(dx-2*dz,5)>2)continue;
                    material=(pattern<6?Blocks.GRAVEL:Blocks.CLAY)
                            .defaultBlockState();
                }
                case KARST_SINKHOLE_RIM -> {
                    if (distance<3 || distance>9)continue;
                    material=(pattern<8?Blocks.CALCITE:Blocks.TUFF)
                            .defaultBlockState();
                }
                case LAVA_TUBE_LINING -> { continue; }
            }
            if (material==null || !emptyColumn(world,p,height))continue;
            for (int h=0;h<height;h++)
                placed |= world.setBlock(p.above(h),material,2);
        }
        return placed;
    }
    private static boolean centerIsReady(WorldGenLevel world,BlockPos p) {
        return ThresholdAnomalySurface.isReplaceableSurface(world,p);
    }
}
