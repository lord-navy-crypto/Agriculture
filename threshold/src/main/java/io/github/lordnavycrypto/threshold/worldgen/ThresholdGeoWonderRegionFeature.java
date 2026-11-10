package io.github.lordnavycrypto.threshold.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Chunk-local painter for continuous, rare multi-chunk surface wonder regions.
 * Site identity and footprint are deterministic in world coordinates, not
 * feature origin, and are independent of chunk visitation order.
 *
 * This is geological SURFACE GEOMETRY, not a replacement for the existing
 * DEM/river/cave density functions. Never modifies fluids, water columns,
 * structures or neighboring chunks. RealScale world presets only.
 */
public final class ThresholdGeoWonderRegionFeature extends Feature<NoneFeatureConfiguration> {
    public enum Kind {
        PLAYA_BASIN, ALLUVIAL_FAN, COLD_DESERT_PAVEMENT,
        HOODOO_AMPHITHEATER, TRAVERTINE_STEPS, MANGROVE_ESTUARY,
        COOLED_LAVA_FIELD, GLACIAL_MORAINE_RING, DELTA_DISTRIBUTARIES,
        KARST_DOLINE_RIM
    }
    private static final int GRID = ThresholdGeoWonderGeometry.GRID;
    private final Kind kind;

    public ThresholdGeoWonderRegionFeature(Kind kind) {
        super(NoneFeatureConfiguration.CODEC);
        this.kind=kind;
    }
    private static long mix(long n) {return ThresholdGeoWonderGeometry.mix(n);}
    private ThresholdGeoWonderGeometry.Site site(int gx,int gz) {
        return ThresholdGeoWonderGeometry.surfaceSite(gx,gz,kind.ordinal());
    }
    private boolean allowedLevel(String level) {
        return ThresholdAnomalySurface.isBackroomsDimension(level);
    }
    private static int roomSizeForLevel(String level) {
        return ThresholdAnomalySurface.roomSize(level);
    }
    private boolean naturalFloor(BlockState b) {
        return ThresholdAnomalySurface.isNativeFloor(b);
    }
    private static boolean nearWater(WorldGenLevel w,int x,int y,int z,
                                     int minX,int minZ) {
        for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++) {
            if(Math.abs(dx)+Math.abs(dz)!=1)continue;
            int nx=x+dx,nz=z+dz;
            if(nx<minX||nz<minZ||nx>=minX+16||nz>=minZ+16)continue;
            if(!w.getFluidState(new BlockPos(nx,y-1,nz)).isEmpty()
                    ||!w.getFluidState(new BlockPos(nx,y,nz)).isEmpty())
                return true;
        }
        return false;
    }
    private int heightFor(ThresholdGeoWonderGeometry.Site s,int x,int z,double u,double v,long point) {
        double r=Math.hypot(u,v);
        switch(kind) {
            case HOODOO_AMPHITHEATER -> {
                // Eroded stratified ridge with surviving pinnacle clusters.
                if(r<0.30||r>0.95 || Math.floorMod(point,15L)!=0)return 0;
                return 2+(int)Math.floorMod(point>>>12,5L);
            }
            case TRAVERTINE_STEPS -> {
                if(r>0.80)return 0;
                return r<0.22?3:r<0.48?2:1;
            }
            case GLACIAL_MORAINE_RING -> {
                return r>0.55&&r<0.77&&Math.floorMod(point,5L)!=0?2:0;
            }
            case KARST_DOLINE_RIM -> {
                // Non-destructive doline RIM, not a claimed excavated sinkhole.
                return r>0.48&&r<0.68&&Math.floorMod(point,4L)!=0?2:0;
            }
            default -> {return 1;}
        }
    }
    private BlockState material(int x,int z,long point,int layer) {
        int n=(int)Math.floorMod(point,10L);
        return switch(kind) {
            case PLAYA_BASIN -> (n<7?Blocks.CALCITE:Blocks.WHITE_TERRACOTTA)
                    .defaultBlockState();
            case ALLUVIAL_FAN -> (n<6?Blocks.GRAVEL:n<8?Blocks.COARSE_DIRT:
                    Blocks.SAND).defaultBlockState();
            case COLD_DESERT_PAVEMENT -> (n<6?Blocks.GRAVEL:Blocks.ANDESITE)
                    .defaultBlockState();
            case HOODOO_AMPHITHEATER -> (layer%3==0?Blocks.RED_SANDSTONE:
                    layer%3==1?Blocks.TERRACOTTA:Blocks.ORANGE_TERRACOTTA)
                    .defaultBlockState();
            case TRAVERTINE_STEPS -> Blocks.CALCITE.defaultBlockState();
            case MANGROVE_ESTUARY -> Blocks.MANGROVE_ROOTS.defaultBlockState();
            case COOLED_LAVA_FIELD -> (n<5?Blocks.BASALT:n<8?Blocks.SMOOTH_BASALT:
                    Blocks.TUFF).defaultBlockState();
            case GLACIAL_MORAINE_RING -> (n<8?Blocks.GRAVEL:Blocks.PACKED_ICE)
                    .defaultBlockState();
            case DELTA_DISTRIBUTARIES -> (n<5?Blocks.CLAY:Blocks.GRAVEL)
                    .defaultBlockState();
            case KARST_DOLINE_RIM -> (n<7?Blocks.CALCITE:Blocks.TUFF)
                    .defaultBlockState();
        };
    }
    private boolean paint(WorldGenLevel w,ThresholdGeoWonderGeometry.Site site,int minX,int minZ) {
        String level = w.getLevel().dimension().location().getPath();
        boolean placed=false;
        for(int x=minX;x<minX+16;x++)for(int z=minZ;z<minZ+16;z++) {
            long dx=(long)x-site.x,dz=(long)z-site.z;
            if(!ThresholdGeoWonderGeometry.contains(site,x,z))continue;
            long point=mix(site.seed ^ ((long)x*0x27d4eb2dL)
                    ^ ((long)z*0x165667b1L));
            if(Math.floorMod(point,7L)==0)continue;
            double u=(double)dx/site.r,v=(double)dz/site.r;
            // Fan-shaped runoff and braided deposits are oriented
            // consistently across chunk boundaries, not reset per chunk.
            if(kind==Kind.ALLUVIAL_FAN||kind==Kind.DELTA_DISTRIBUTARIES) {
                int sign=((site.seed&1L)==0L)?1:-1;
                double forward=sign*u;
                if(forward<0||Math.abs(v)>0.14+forward*0.78)continue;
                if(kind==Kind.DELTA_DISTRIBUTARIES
                        && Math.abs(v-Math.sin(forward*11.0)*0.09)>0.09
                        && Math.abs(v+Math.sin(forward*8.0)*0.12)>0.08)
                    continue;
            }
            if(kind==Kind.COOLED_LAVA_FIELD) {
                double lobe=Math.sin(u*12+site.seed%11)*0.10;
                if(u*u+(v+lobe)*(v+lobe)>0.80)continue;
            }
            int y=ThresholdAnomalySurface.surfaceY(level);
            if(y<=w.getMinBuildHeight()+8||y>=w.getMaxBuildHeight()-9)continue;
            BlockPos p=new BlockPos(x,y,z);
            if(!ThresholdAnomalySurface.isReplaceableSurface(w,p))continue;
            int roomSize = roomSizeForLevel(level);
            int roomX = Math.floorDiv(x, roomSize), roomZ = Math.floorDiv(z, roomSize);
            int localX = Math.floorMod(x, roomSize), localZ = Math.floorMod(z, roomSize);
            if (ThresholdRoomLayoutFeature.isReservedRoomPath(
                    w.getSeed(), roomSize, roomX, roomZ, localX, localZ, 2, true)) continue;
            int height=heightFor(site,x,z,u,v,point);
            if(height==0)continue;
            boolean clear=true;
            for(int h=0;h<height;h++) {
                BlockPos at=p.above(h);
                if(!w.getBlockState(at).isAir()||!w.getFluidState(at).isEmpty()) {
                    clear=false;break;
                }
            }
            if(!clear)continue;
            for(int h=0;h<height;h++)
                placed|=w.setBlock(p.above(h),material(x,z,point,h),2);
        }
        return placed;
    }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        String level = context.level().getLevel().dimension().location().getPath();
        if (!allowedLevel(level)) return false;
        int minX=Math.floorDiv(context.origin().getX(),16)*16;
        int minZ=Math.floorDiv(context.origin().getZ(),16)*16;
        int gx=ThresholdGeoWonderGeometry.gridAtChunk(minX);
        int gz=ThresholdGeoWonderGeometry.gridAtChunk(minZ);
        boolean placed=false;
        for(int sx=-1;sx<=1;sx++)for(int sz=-1;sz<=1;sz++) {
            ThresholdGeoWonderGeometry.Site candidate=site(gx+sx,gz+sz);
            if(candidate==null)continue;
            if(!ThresholdAnomalySurface.shouldGenerate(level,candidate.seed,1))continue;
            if(!ThresholdGeoWonderGeometry.intersects(candidate,minX,minZ)
                continue;
            placed|=paint(context.level(),candidate,minX,minZ);
        }
        return placed;
    }
}
