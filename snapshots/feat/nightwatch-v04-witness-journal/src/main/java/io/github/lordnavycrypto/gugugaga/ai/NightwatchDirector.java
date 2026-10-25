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

        // One bounded pass, after applying a server-owned opt-in.
        var players=server.getPlayerList().getPlayers();
        state.lastAttempt.keySet().removeIf(id->players.stream()
                .noneMatch(player->player.getUUID().equals(id)));
        state.quietPlayers.removeIf(id->players.stream()
                .noneMatch(player->player.getUUID().equals(id)));

        int global=0;
        for(ServerLevel level:server.getAllLevels()) {
            for(var entity:level.getAllEntities()) {
                if(entity instanceof GugugagaEntity && ++global>=EncounterPolicy.MAX_GLOBAL_OBSERVERS)
                    return;
            }
        }

        for(ServerPlayer player:players) {
            if(global>=EncounterPolicy.MAX_GLOBAL_OBSERVERS)break;
            ServerLevel level=player.serverLevel();
            // First encounter waits a full cooldown from joining/enabling.
            long last=state.lastAttempt.computeIfAbsent(player.getUUID(),id->state.ticks);
            // Only count local loaded observers, never request more chunks.
            int nearby=level.getEntitiesOfClass(GugugagaEntity.class,
                    player.getBoundingBox().inflate(EncounterPolicy.NEARBY_RADIUS)).size();
            long day=level.getDayTime()%24000L;
            boolean night=day>=13000&&day<=23000;
            if(!EncounterPolicy.eligible(true,
                    level.dimension()==Level.OVERWORLD,
                    !player.isCreative()&&!player.isSpectator(),
                    state.quietPlayers.contains(player.getUUID()),
                    night,nearby,global,state.ticks-last,
                    EncounterPolicy.boundedCooldown(
                            NightwatchConfig.ENCOUNTER_COOLDOWN_TICKS.get())))continue;
            // A failed attempt also consumes cooldown. Never retry each tick.
            state.lastAttempt.put(player.getUUID(),state.ticks);
            if(trySpawn(level,player,state.ticks))global++;
        }
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
