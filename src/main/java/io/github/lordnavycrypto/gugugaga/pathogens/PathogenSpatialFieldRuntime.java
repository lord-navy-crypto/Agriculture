package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Minecraft runtime wrapper for the spatial scalar-field game.
 *
 * Existing scanner enrollments can initialize one local field channel.
 * None of the 15 pathogen labels determines a real-world spread coefficient.
 * No automatic animal/player infection, item contamination or chunk forcing.
 */
public final class PathogenSpatialFieldRuntime {
    private PathogenSpatialFieldRuntime(){}
    private static final int INTERVAL=80;

    public static PathogenSpatialFieldMath.Cell cell(BlockPos pos){
        return PathogenSpatialFieldMath.fromBlock(pos.getX(),pos.getZ());
    }
    public static boolean set(ServerLevel world,BlockPos pos,String taxonId,int gamePressure){
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get())return false;
        return PathogenSpatialFieldSavedData.forLevel(world).set(
                cell(pos),PathogenSpatialFieldMath.channel(taxonId),gamePressure);
    }
    public static int sample(ServerLevel world,BlockPos pos,String taxonId){
        return PathogenSpatialFieldSavedData.forLevel(world).get(
                cell(pos),PathogenSpatialFieldMath.channel(taxonId));
    }
    public static boolean pauseCrop(ServerLevel world,BlockPos pos,String blockId){
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get()
                ||!NightwatchConfig.PATHOGEN_FIELD_CROP_CUES.get())return false;
        var fields=PathogenSpatialFieldSavedData.forLevel(world);
        var at=cell(pos);
        for(int i=0;i<PathogenRoster15.FIRST_FIFTEEN.size();i++){
            var taxon=PathogenRoster15.FIRST_FIFTEEN.get(i);
            if(fields.get(at,i)>=12
                    &&VanillaHostMapping15.supportsBlock(taxon.id(),blockId))
                return true;
        }
        return false;
    }
    public static String summary(ServerLevel world,BlockPos pos){
        var field=PathogenSpatialFieldSavedData.forLevel(world);
        var cell=cell(pos);
        String name="none";
        int max=0,active=0;
        for(int i=0;i<15;i++){
            int level=field.get(cell,i);
            if(level>0)active++;
            if(level>max){
                max=level;
                name=PathogenRoster15.FIRST_FIFTEEN.get(i).id();
            }
        }
        return "field cell="+cell.x()+","+cell.z()+"; channels="+active
                +"; highest="+name+"; game-pressure="+max+"/15; cells="+field.size();
    }
    private static boolean active(ServerLevel world,
            List<ServerPlayer> players,PathogenSpatialFieldMath.Cell at){
        int cx=at.x()>>2,cz=at.z()>>2;
        if(!world.getChunkSource().hasChunk(cx,cz))return false;
        double centerX=at.x()*4.0+2,centerZ=at.z()*4.0+2;
        for(ServerPlayer player:players){
            double x=player.getX()-centerX,z=player.getZ()-centerZ;
            if(x*x+z*z<=48*48)return true;
        }
        return false;
    }
    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get())return;
        var server=event.getServer();
        long worldTime=server.overworld().getGameTime();
        if(worldTime%INTERVAL!=0)return;
        for(ServerLevel world:server.getAllLevels()){
            List<ServerPlayer> players=world.players().stream()
                    .filter(p->p instanceof ServerPlayer)
                    .map(p->(ServerPlayer)p).toList();
            if(players.isEmpty())continue;
            var field=PathogenSpatialFieldSavedData.forLevel(world);
            if(field.size()==0)continue;
            field.step(worldTime/INTERVAL,c->active(world,players,c));
            // Opt-in HUD is handled by PathogenFieldHudRuntime.
            // Nothing is rendered for players who did not request it.
        }
    }
}
