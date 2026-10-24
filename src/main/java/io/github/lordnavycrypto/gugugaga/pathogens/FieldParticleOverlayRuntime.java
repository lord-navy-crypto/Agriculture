package io.github.lordnavycrypto.gugugaga.pathogens;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import java.util.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Per-player, read-only, optional PARTICLE VIEW into one selected 4x4
 * Minecraft numerical field. Does not create ANY special blocks, mobs,
 * particle entities or independent pathogen state. Every request uses
 * vanilla ServerLevel#sendParticles(viewer,...) targeted at its owner.
 */
public final class FieldParticleOverlayRuntime {
    private FieldParticleOverlayRuntime(){}
    private static final int INTERVAL=60, MAX_VIEWERS_PER_FRAME=12;
    private static final int MAX_PARTICLES_PER_VIEWER=12;
    private static final Map<MinecraftServer,Map<UUID,Session>> SESSIONS=
            new WeakHashMap<>();

    private static final class Session {
        FieldParticleSampling.Choice selected=
                FieldParticleSampling.choose("potato_virus_y");
        String dimension="";
        Map<PathogenSpatialFieldMath.Cell,Integer> last=Map.of();
    }
    public static boolean isEnabled(ServerPlayer player){
        var byPlayer=SESSIONS.get(player.serverLevel().getServer());
        return byPlayer!=null && byPlayer.containsKey(player.getUUID());
    }
    public static String selection(ServerPlayer player){
        var byPlayer=SESSIONS.get(player.serverLevel().getServer());
        var session=byPlayer==null?null:byPlayer.get(player.getUUID());
        return session==null?"none":session.selected.taxon();
    }
    public static void enable(ServerPlayer player) {
        SESSIONS.computeIfAbsent(player.serverLevel().getServer(),
                ignored->new HashMap<>()).computeIfAbsent(player.getUUID(),
                ignored->new Session());
    }
    public static void select(ServerPlayer player,String taxon){
        var selected=FieldParticleSampling.choose(taxon);
        if(!selected.worldVisualAllowed())
            throw new IllegalArgumentException("Reference has no vanilla world visual");
        enable(player);
        var s=SESSIONS.get(player.serverLevel().getServer()).get(player.getUUID());
        s.selected=selected;
        s.dimension="";
        s.last=Map.of();
    }
    public static void disable(ServerPlayer player){
        MinecraftServer server=player.serverLevel().getServer();
        var membership=SESSIONS.get(server);
        if(membership==null)return;
        membership.remove(player.getUUID());
        if(membership.isEmpty())SESSIONS.remove(server);
    }
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event){
        if(event.getEntity() instanceof ServerPlayer player)disable(player);
    }
    public static void onServerStopped(ServerStoppedEvent event){
        SESSIONS.remove(event.getServer());
    }

    private static ParticleOptions style(FieldParticleSampling.Choice choice){
        // Five highlighted examples have distinct vanilla art; other IDs
        // fall back to their biological IDENTITY CATEGORY, not kinetics.
        return switch(choice.taxon()){
            case "influenza_a_virus" -> ParticleTypes.ENCHANT;
            case "potato_virus_y" -> ParticleTypes.HAPPY_VILLAGER;
            case "salmonella_enterica" -> ParticleTypes.CLOUD;
            case "puccinia_graminis" -> ParticleTypes.COMPOSTER;
            case "varroa_destructor" -> ParticleTypes.END_ROD;
            default -> switch(choice.family()){
                case VIRUS -> ParticleTypes.ENCHANT;
                case BACTERIUM -> ParticleTypes.CLOUD;
                case OOMYCETE,FUNGUS -> ParticleTypes.COMPOSTER;
                case PARASITE -> ParticleTypes.END_ROD;
                case PHAGE -> ParticleTypes.ENCHANT;
            };
        };
    }
    private static void render(ServerPlayer player,Session s){
        var level=player.serverLevel();
        String dimension=level.dimension().location().toString();
        if(!dimension.equals(s.dimension)){
            s.last=Map.of();
            s.dimension=dimension;
        }
        var center=PathogenSpatialFieldRuntime.cell(player.blockPosition());
        var choice=s.selected;
        var original=choice.store()==FieldParticleSampling.Store.ORIGINAL_15
                ?PathogenSpatialFieldSavedData.forLevel(level):null;
        var extension=choice.store()==FieldParticleSampling.Store.EXTENSION_8
                ?EcologyExtendedSavedData.forLevel(level):null;
        var scan=FieldParticleSampling.scan(center,choice,
            cell->level.getChunkSource().hasChunk(cell.x()>>2,cell.z()>>2),
            cell->original!=null?original.get(cell,choice.channel())
                    :extension.get(cell,choice.channel()),s.last);
        s.last=scan.current();
        int sent=0;
        ParticleOptions particle=style(choice);
        for(var marker:scan.markers()){
            if(sent>=MAX_PARTICLES_PER_VIEWER)break;
            double px=marker.cell().x()*4.0+2.0;
            double pz=marker.cell().z()*4.0+2.0;
            double py=player.getY()+0.9;
            // Floating 4x4-cell projections; never require a target block.
            if(level.sendParticles(player,particle,false,px,py,pz,
                    1,0.15,0.12,0.15,0.0))sent++;
            if(marker.change()==FieldParticleSampling.Change.NEW_OR_INCREASED
                    &&sent<MAX_PARTICLES_PER_VIEWER) {
                if(level.sendParticles(player,ParticleTypes.END_ROD,false,
                        px,py+0.25,pz,1,0.08,0.08,0.08,0.0))sent++;
            }
        }
    }
    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.FIELD_PARTICLE_OVERLAY_ENABLED.get())return;
        var server=event.getServer();
        long tick=server.overworld().getGameTime();
        if(tick%INTERVAL!=0)return;
        var byPlayer=SESSIONS.get(server);
        if(byPlayer==null||byPlayer.isEmpty())return;
        List<ServerPlayer> watchers=new ArrayList<>();
        for(ServerPlayer player:server.getPlayerList().getPlayers())
            if(byPlayer.containsKey(player.getUUID()))watchers.add(player);
        if(watchers.isEmpty())return;
        int begin=(int)Math.floorMod((tick/INTERVAL)*MAX_VIEWERS_PER_FRAME,
                watchers.size());
        int count=Math.min(MAX_VIEWERS_PER_FRAME,watchers.size());
        for(int i=0;i<count;i++){
            ServerPlayer player=watchers.get((begin+i)%watchers.size());
            render(player,byPlayer.get(player.getUUID()));
        }
    }
}
