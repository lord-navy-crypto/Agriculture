package io.github.lordnavycrypto.gugugaga.ai;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Optional server-side suspense director. Disabled by default.
 * Only examines already loaded chunks. No tickets, dimension scans,
 * structures, block changes, damage, network requests or forced chunks.
 */
public final class NightwatchDirector {
    private NightwatchDirector() {}

    private static final class Session {
        long ticks;
        final Map<UUID,Long> lastAttempt=new HashMap<>();
        final Map<UUID,Integer> pastAttempts=new HashMap<>();
        final Map<UUID,Integer> tension=new HashMap<>();
        final Map<UUID,HauntDirectorPolicy.Event> previous=new HashMap<>();
        final Set<UUID> quietPlayers=new HashSet<>();
    }

    // No cross-world static save data: weak-keyed session and bounded cleanup.
    // NeoForge delivers these callbacks on the logical server thread.
    private static final Map<MinecraftServer,Session> SESSIONS=new WeakHashMap<>();

    private static Session session(MinecraftServer server) {
        return SESSIONS.computeIfAbsent(server,ignored->new Session());
    }

    public static boolean isQuiet(MinecraftServer server,UUID player) {
        return session(server).quietPlayers.contains(player);
    }

    public static void setQuiet(MinecraftServer server,UUID player,boolean quiet) {
        Session state=session(server);
        if(quiet)state.quietPlayers.add(player);
        else state.quietPlayers.remove(player);
    }

    public static void onTick(ServerTickEvent.Post event) {
        MinecraftServer server=event.getServer();
        if(!NightwatchConfig.ENCOUNTERS_ENABLED.get())return;
        Session state=session(server);
        state.ticks++;
        if(state.ticks % EncounterPolicy.DIRECTOR_INTERVAL_TICKS!=0)return;

        var players=server.getPlayerList().getPlayers();
        // Bounded online-only memory. No movement coordinates, long-term
        // tracking, chat, inventory, or worldgen queries are stored.
        Set<UUID> online=new HashSet<>();
        for(ServerPlayer player:players)online.add(player.getUUID());
        state.lastAttempt.keySet().retainAll(online);
        state.pastAttempts.keySet().retainAll(online);
        state.previous.keySet().retainAll(online);
        state.tension.keySet().retainAll(online);
        state.quietPlayers.retainAll(online);

        // The pre-existing hard quota counts only already-loaded entities.
        int global=0;
        for(ServerLevel level:server.getAllLevels()) {
            for(var entity:level.getAllEntities()) {
                if(entity instanceof GugugagaEntity)global++;
                if(global>=EncounterPolicy.MAX_GLOBAL_OBSERVERS)break;
            }
            if(global>=EncounterPolicy.MAX_GLOBAL_OBSERVERS)break;
        }

        for(ServerPlayer player:players) {
            ServerLevel level=player.serverLevel();
            UUID id=player.getUUID();
            long last=state.lastAttempt.computeIfAbsent(id,key->state.ticks);
            int nearby=level.getEntitiesOfClass(GugugagaEntity.class,
                    player.getBoundingBox().inflate(EncounterPolicy.NEARBY_RADIUS)).size();
            long day=level.getDayTime()%24000L;
            boolean night=day>=13000&&day<=23000;
            if(!EncounterPolicy.eligible(true,
                    level.dimension()==Level.OVERWORLD,
                    !player.isCreative()&&!player.isSpectator(),
                    state.quietPlayers.contains(id),
                    night,nearby,global,state.ticks-last,
                    EncounterPolicy.boundedCooldown(
                            NightwatchConfig.ENCOUNTER_COOLDOWN_TICKS.get())))continue;

            // All candidate events, including intentional silence and failed
            // placements, consume the same strict >=8-minute cooldown.
            state.lastAttempt.put(id,state.ticks);
            int attempt=state.pastAttempts.getOrDefault(id,0);
            int tension=state.tension.getOrDefault(id,0);
            HauntDirectorPolicy.Event previous=state.previous.getOrDefault(
                    id,HauntDirectorPolicy.Event.SILENCE);
            long seed=id.getMostSignificantBits()^id.getLeastSignificantBits();
            HauntDirectorPolicy.Event chosen=NightwatchConfig.VARIED_EVENTS_ENABLED.get()
                    ? HauntDirectorPolicy.choose(seed,state.ticks,attempt,tension,
                        NightwatchConfig.DIRECTOR_INTENSITY.get(),previous)
                    : HauntDirectorPolicy.Event.SIGHTING;
            HauntDirectorPolicy.Event delivered=perform(level,player,
                    state.ticks,seed,chosen,global);
            state.pastAttempts.put(id,Math.min(1_000_000,attempt+1));
            state.previous.put(id,delivered);
            state.tension.put(id,HauntDirectorPolicy.nextTension(tension,delivered));
            if(delivered==HauntDirectorPolicy.Event.SIGHTING)global++;
        }
    }

    private static HauntDirectorPolicy.Event perform(ServerLevel level,
            ServerPlayer player,long tick,long seed,
            HauntDirectorPolicy.Event chosen,int global) {
        if(chosen==HauntDirectorPolicy.Event.SILENCE)
            return HauntDirectorPolicy.Event.SILENCE;
        if(chosen==HauntDirectorPolicy.Event.SIGHTING)
            return global<EncounterPolicy.MAX_GLOBAL_OBSERVERS
                    && trySpawn(level,player,tick)
                    ? HauntDirectorPolicy.Event.SIGHTING
                    : HauntDirectorPolicy.Event.SILENCE;
        if(chosen==HauntDirectorPolicy.Event.AFTERIMAGE) {
            if(!NightwatchConfig.HAUNTING_PARTICLES_ENABLED.get())
                return HauntDirectorPolicy.Event.SILENCE;
            // Two visual flecks in a nearby, already-player-loaded area.
            // No block reads, no chunk loads and no world alteration.
            double angle=HauntDirectorPolicy.bearing(seed^tick,17);
            double x=player.getX()+Math.cos(angle)*8.0;
            double z=player.getZ()+Math.sin(angle)*8.0;
            if(level.hasChunkAt(new BlockPos(
                    (int)Math.floor(x),(int)Math.floor(player.getY()),
                    (int)Math.floor(z))))
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL,
                        x,player.getY()+1.4,z,2,0.12,0.10,0.12,0.005);
            return HauntDirectorPolicy.Event.AFTERIMAGE;
        }
        if(!NightwatchConfig.HAUNTING_AUDIO_ENABLED.get())
            return HauntDirectorPolicy.Event.SILENCE;
        double angle=HauntDirectorPolicy.bearing(seed^tick,chosen.ordinal());
        double range=9.0+Math.floorMod(tick+chosen.ordinal(),5);
        double x=player.getX()+Math.cos(angle)*range;
        double z=player.getZ()+Math.sin(angle)*range;
        net.minecraft.sounds.SoundEvent sound=switch(chosen) {
            case DISTANT_CHIME -> net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME;
            case FALSE_FOOTSTEPS -> net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_STEP;
            case FADING_ECHO -> net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME;
            default -> net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_CHIME;
        };
        float pitch=switch(chosen) {
            case DISTANT_CHIME -> 0.58f;
            case FALSE_FOOTSTEPS -> 0.68f;
            case FADING_ECHO -> 0.86f;
            default -> 1.0f;
        };
        level.playSound(null,x,player.getY()+0.6,z,sound,
                net.minecraft.sounds.SoundSource.AMBIENT,
                HauntDirectorPolicy.volume(chosen),pitch);
        return chosen;
    }

    private static boolean trySpawn(ServerLevel level,ServerPlayer player,long ticks) {
        // The four candidates are never closer than 12 blocks or farther than
        // 18. No generation/chunk loading: check all candidate chunk corners
        // before any block reads or entity creation.
        long seed=player.getUUID().getMostSignificantBits()
                ^player.getUUID().getLeastSignificantBits()^ticks;
        for(int i=0;i<4;i++) {
            double angle=(((seed>>>i*11)&1023)/1024.0)*Math.PI*2+i*1.57;
            int x=(int)Math.floor(player.getX()+Math.cos(angle)*16);
            int z=(int)Math.floor(player.getZ()+Math.sin(angle)*16);
            int cx=x>>4,cz=z>>4;
            if(!level.getChunkSource().hasChunk(cx,cz))continue;
            int top=Math.min(level.getMaxBuildHeight()-3,player.blockPosition().getY()+5);
            int bottom=Math.max(level.getMinBuildHeight()+3,player.blockPosition().getY()-7);
            for(int y=top;y>=bottom;y--) {
                BlockPos feet=new BlockPos(x,y,z);
                BlockPos ground=feet.below();
                // At x/z edges the entity bounding box can extend into an
                // adjacent chunk. Require a full one-chunk margin first.
                if((x&15)<2 || (x&15)>13 || (z&15)<2 || (z&15)>13)break;
                BlockState floor=level.getBlockState(ground);
                if(!floor.isSolidRender(level,ground))continue;
                if(!level.getBlockState(feet).isAir()
                        || !level.getBlockState(feet.above()).isAir())continue;
                if(!level.getFluidState(feet).isEmpty()
                        || !level.getFluidState(feet.above()).isEmpty())continue;
                GugugagaEntity observer=GuguGagaMod.OBSERVER.get().create(level);
                if(observer==null)return false;
                observer.moveTo(x+.5,y,z+.5,level.random.nextFloat()*360f,0);
                if(!level.noCollision(observer))continue;
                // Ambient sightings expire automatically; player-summoned
                // observers are never given this lifetime flag.
                observer.markAmbientEncounter();
                return level.addFreshEntity(observer);
            }
        }
        return false;
    }
}
