package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Five qualitative, source-labelled GAME EVENT ROUTES, physically embodied
 * only as integers in pre-existing per-dimension SavedData.
 *
 * Minecraft water, rain, sky, crops and vanilla animal types are observed
 * from already-loaded regions. They are NOT real infectious sources,
 * biological survival modifiers, airborne equations or organism quantities.
 * We never modify a BlockState, Entity, player health or chunk load state.
 */
public final class FocusFiveTransportRuntime {
    private FocusFiveTransportRuntime(){}
    private static final int INTERVAL=120;
    private static final int MAX_ANIMALS_PER_PROBE=16;

    private static boolean active(ServerLevel world,List<ServerPlayer> viewers,
            PathogenSpatialFieldMath.Cell cell){
        if(!world.getChunkSource().hasChunk(cell.x()>>2,cell.z()>>2))
            return false;
        double x=cell.x()*4.0+2,z=cell.z()*4.0+2;
        for(ServerPlayer p:viewers){
            double dx=p.getX()-x,dz=p.getZ()-z;
            if(dx*dx+dz*dz<=48*48)return true;
        }
        return false;
    }

    private static FivePathGameRoutes.GameTags inspectGameWorld(
            ServerLevel world,List<ServerPlayer> viewers,
            PathogenSpatialFieldMath.Cell cell){
        int x=cell.x()*4,z=cell.z()*4;
        ServerPlayer nearest=null;
        double best=Double.POSITIVE_INFINITY;
        for(ServerPlayer p:viewers){
            double dx=p.getX()-(x+2.0),dz=p.getZ()-(z+2.0);
            double d2=dx*dx+dz*dz;
            if(d2<best){best=d2;nearest=p;}
        }
        // Don't inspect far-away game chunks or other dimensions.
        if(nearest==null||best>48*48||!active(world,viewers,cell))
            return new FivePathGameRoutes.GameTags(
                false,false,false,false,false,false,false,false);
        int y=nearest.blockPosition().getY();
        boolean potato=false,wheat=false,water=false,sky=false,rain=false;
        for(int dx=0;dx<4;dx++){
            for(int dz=0;dz<4;dz++){
                for(int dy=-1;dy<=1;dy++){
                    BlockPos p=new BlockPos(x+dx,y+dy,z+dz);
                    if(!world.hasChunkAt(p))continue;
                    var block=world.getBlockState(p).getBlock();
                    if(block==Blocks.POTATOES)potato=true;
                    if(block==Blocks.WHEAT)wheat=true;
                    if(block==Blocks.WATER)water=true;
                    if(!sky && world.canSeeSky(p))sky=true;
                    if(!rain && world.isRainingAt(p))rain=true;
                }
            }
        }
        boolean bird=false,farmAnimal=false,bee=false;
        AABB nearby=new AABB(x,y-4,z,x+4,y+5,z+4);
        int seen=0;
        for(Animal animal:world.getEntitiesOfClass(Animal.class,nearby,Animal::isAlive)){
            if(seen++>=MAX_ANIMALS_PER_PROBE)break;
            String id=BuiltInRegistries.ENTITY_TYPE.getKey(animal.getType()).toString();
            if("minecraft:chicken".equals(id)||"minecraft:parrot".equals(id))
                bird=true;
            if("minecraft:chicken".equals(id)||"minecraft:cow".equals(id)||
                    "minecraft:pig".equals(id))farmAnimal=true;
            if("minecraft:bee".equals(id))bee=true;
        }
        return new FivePathGameRoutes.GameTags(
            bird,potato,farmAnimal,water,wheat,bee,sky,rain);
    }

    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.FOCUS_FIVE_TRANSPORT_ENABLED.get())return;
        var server=event.getServer();
        long tick=server.overworld().getGameTime();
        if(tick%INTERVAL!=0L)return;
        for(ServerLevel level:server.getAllLevels()){
            var viewers=new ArrayList<ServerPlayer>();
            for(var p:level.players())if(p instanceof ServerPlayer player)
                viewers.add(player);
            if(viewers.isEmpty())continue;
            boolean coreOn=NightwatchConfig.PATHOGEN_FIELD_ENABLED.get();
            boolean extraOn=NightwatchConfig.ECOLOGY_EXTENSIONS_ENABLED.get();
            if(!coreOn&&!extraOn)continue;
            var core=PathogenSpatialFieldSavedData.forLevel(level);
            var extra=EcologyExtendedSavedData.forLevel(level);
            if(core.size()==0&&extra.size()==0)continue;
            // Bounded cached world observations, once per target region.
            var tags=new HashMap<PathogenSpatialFieldMath.Cell,
                FivePathGameRoutes.GameTags>();
            var result=FivePathGameTransport.plan(
                coreOn?core.snapshot():Map.of(),
                extraOn?extra.snapshot():Map.of(),
                tick/INTERVAL,
                cell->active(level,viewers,cell),
                (profile,target)->tags.computeIfAbsent(target,
                    c->inspectGameWorld(level,viewers,c)).accepts(profile.route()));
            for(var plan:result.proposals()){
                var focus=FocusFiveProfiles.byId(plan.profile().id());
                if(focus.store()==FocusFiveProfiles.Store.ORIGINAL_15){
                    int current=core.get(plan.destination(),focus.channel());
                    if(plan.newDestination()>current)
                        core.set(plan.destination(),focus.channel(),plan.newDestination());
                }else{
                    int current=extra.get(plan.destination(),focus.channel());
                    if(plan.newDestination()>current)
                        extra.set(plan.destination(),focus.channel(),plan.newDestination());
                }
            }
        }
    }
}
