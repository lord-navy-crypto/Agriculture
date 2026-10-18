package io.github.lordnavycrypto.gugugaga.ai;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import io.github.lordnavycrypto.gugugaga.entity.RedactorEntity;
import io.github.lordnavycrypto.gugugaga.entity.YellowSmilerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
 * Tiny repeatable horror game loop, with no quests, bosses or endings.
 * The session is per online server player and fully voluntary.
 *
 * STILLNESS -> WHISPERS -> WITNESS (investigate real Gugu)
 * -> FALSE_SAFETY (investigate real Redactor/Yellow)
 * -> RESPITE -> STILLNESS; after player opts in.
 *
 * All ambience is sparse. Scripted Lapis/Yellow arrivals require explicit
 * investigation, nearby loaded, unobstructed space, a per-type cap and
 * valid server survival-night context. No forced chunk load or block edits.
 */
public final class HauntGameplayRuntime {
    private HauntGameplayRuntime(){}
    private static final class Entry {
        HauntGameplayPolicy.Act act=HauntGameplayPolicy.Act.STILLNESS;
        int activeTicks;
        int loops;
    }
    private static final Map<MinecraftServer,Map<UUID,Entry>> SESSIONS=
            new WeakHashMap<>();
    private static Map<UUID,Entry> entries(MinecraftServer server){
        return SESSIONS.computeIfAbsent(server,x->new HashMap<>());
    }
    public static boolean isJoined(MinecraftServer server,UUID id) {
        return entries(server).containsKey(id);
    }
    public static void join(ServerPlayer player) {
        entries(player.serverLevel().getServer()).put(player.getUUID(),new Entry());
        player.sendSystemMessage(Component.literal(
                "[Nightwatch] Joined the five-act survival horror loop. "
                +"Wait for a real witness, then /gugugaga investigate. "
                +"Use /gugugaga play leave to stop."));
    }
    public static void leave(ServerPlayer player) {
        entries(player.serverLevel().getServer()).remove(player.getUUID());
        player.sendSystemMessage(Component.literal(
                "[Nightwatch] Left the horror loop. No progress or player position saved."));
    }
    public static String status(ServerPlayer player) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        return "Server="+NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()+
                ", joined="+(e!=null)+", act="+(e==null?"NONE":e.act)
                +", next clue=/gugugaga investigate; "
                +"original Nightwatch observers retain 8-minute minimum cooldown.";
    }
    private static boolean eligible(ServerPlayer player,boolean joined) {
        ServerLevel level=player.serverLevel();
        long day=Math.floorMod(level.getDayTime(),24000L);
        return HauntGameplayPolicy.playerEligible(
                player.isAlive()&&!player.isCreative()&&!player.isSpectator(),
                day>=13000&&day<=23000,
                NightwatchDirector.isQuiet(level.getServer(),player.getUUID()),
                level.dimension()==Level.OVERWORLD,
                joined,NightwatchConfig.PLAYABLE_LOOP_ENABLED.get());
    }

    public static void onTick(ServerTickEvent.Post event) {
        MinecraftServer server=event.getServer();
        if(!NightwatchConfig.PLAYABLE_LOOP_ENABLED.get())return;
        long ticks=server.overworld().getGameTime();
        if(Math.floorMod(ticks,20L)!=0L)return;
        Map<UUID,Entry> active=entries(server);
        Set<UUID> online=new HashSet<>();
        for(ServerPlayer player:server.getPlayerList().getPlayers()) {
            UUID id=player.getUUID();
            online.add(id);
            Entry e=active.get(id);
            if(e==null || !eligible(player,true))continue;
            e.activeTicks=Math.min(24000,e.activeTicks+20);
            HauntGameplayPolicy.Act next=HauntGameplayPolicy.advance(
                    e.act,e.activeTicks,HauntGameplayPolicy.Evidence.NONE,false);
            if(next!=e.act) {
                e.act=next;
                e.activeTicks=0;
                if(next==HauntGameplayPolicy.Act.STILLNESS)e.loops++;
                player.sendSystemMessage(Component.literal(stageMessage(next)));
            } else if(HauntGameplayPolicy.shouldCue(e.act,e.activeTicks,
                    id.getLeastSignificantBits())
                    && NightwatchConfig.HAUNTING_AUDIO_ENABLED.get()) {
                ServerLevel level=player.serverLevel();
                level.playSound(null,player.blockPosition(),SoundEvents.AMETHYST_BLOCK_CHIME,
                        SoundSource.AMBIENT,HauntGameplayPolicy.cueVolume(e.act),0.65f);
            }
        }
        // No UUID, encounter attempts, or player position history after logout.
        active.keySet().retainAll(online);
    }
    private static String stageMessage(HauntGameplayPolicy.Act act) {
        return switch(act) {
            case STILLNESS -> "[Nightwatch] The forest is quiet again. It may not last.";
            case WHISPERS -> "[Nightwatch] A faint sound answers another. Keep watching.";
            case WITNESS -> "[Nightwatch] Something is out there. Find Gugu-Gaga, "
                    +"then use /gugugaga investigate within 12 blocks.";
            case FALSE_SAFETY -> "[Nightwatch] The bird noticed something blue. "
                    +"Find Lapis or the smiling stranger and investigate.";
            case RESPITE -> "[Nightwatch] Your investigation is complete for now. "
                    +"The world is quiet... but the encounter may return.";
        };
    }
    public static boolean investigate(ServerPlayer player) {
        Map<UUID,Entry> all=entries(player.serverLevel().getServer());
        Entry e=all.get(player.getUUID());
        if(e==null || !eligible(player,true)) {
            player.sendSystemMessage(Component.literal(
                    "[Nightwatch] Join via /gugugaga play join, then investigate "
                    +"at night in survival when the server enabled the loop."));
            return false;
        }
        ServerLevel level=player.serverLevel();
        HauntGameplayPolicy.Evidence evidence=HauntGameplayPolicy.Evidence.NONE;
        if(e.act==HauntGameplayPolicy.Act.WITNESS) {
            if(visible(level,player,GugugagaEntity.class))
                evidence=HauntGameplayPolicy.Evidence.GUGU_GAGA;
        } else if(e.act==HauntGameplayPolicy.Act.FALSE_SAFETY) {
            if(visible(level,player,RedactorEntity.class))
                evidence=HauntGameplayPolicy.Evidence.LAPIS_REDACTOR;
            else if(visible(level,player,YellowSmilerEntity.class))
                evidence=HauntGameplayPolicy.Evidence.YELLOW_SMILER;
        }
        HauntGameplayPolicy.Act next=HauntGameplayPolicy.advance(e.act,
                e.activeTicks,evidence,true);
        if(next==e.act) {
            player.sendSystemMessage(Component.literal(
                    "[Nightwatch] No new clue yet. Explore, watch and listen. "
                    +"Stage="+e.act));
            return false;
        }
        e.act=next;
        e.activeTicks=0;
        player.sendSystemMessage(Component.literal(stageMessage(next)));
        if(next==HauntGameplayPolicy.Act.FALSE_SAFETY)
            summonOne(level,player,GuguGagaMod.REDACTOR.get());
        else if(next==HauntGameplayPolicy.Act.RESPITE)
            summonOne(level,player,GuguGagaMod.YELLOW_SMILER.get());
        return true;
    }
    private static <T extends Entity> boolean visible(
            ServerLevel level,ServerPlayer player,Class<T> clazz) {
        AABB search=player.getBoundingBox().inflate(12);
        for(T other:level.getEntitiesOfClass(clazz,search,Entity::isAlive)) {
            if(HauntGameplayPolicy.nearEvidence(player.distanceToSqr(other),
                    player.hasLineOfSight(other)))return true;
        }
        return false;
    }
    private static <T extends Entity> boolean summonOne(ServerLevel level,
            ServerPlayer player,EntityType<T> kind) {
        // Do not manufacture an army: only one nearby, six maximum per level.
        int global=0;
        for(Entity entity:level.getAllEntities())
            if(entity.getType()==kind) {
                global++;
                if(global>=6)return false;
            }
        if(!level.getEntitiesOfClass(Entity.class,
                player.getBoundingBox().inflate(48),
                e->e.getType()==kind).isEmpty())return false;
        long seed=player.getUUID().getLeastSignificantBits()^level.getGameTime();
        for(int i=0;i<4;i++) {
            double a=HauntDirectorPolicy.bearing(seed,i);
            int x=(int)Math.floor(player.getX()+Math.cos(a)*(12+i));
            int z=(int)Math.floor(player.getZ()+Math.sin(a)*(12+i));
            if(!level.getChunkSource().hasChunk(x>>4,z>>4))continue;
            // Require a full chunk margin for entity collision checks.
            if((x&15)<2||(x&15)>13||(z&15)<2||(z&15)>13)continue;
            int top=Math.min(level.getMaxBuildHeight()-3,player.blockPosition().getY()+5);
            int floorY=Math.max(level.getMinBuildHeight()+3,player.blockPosition().getY()-7);
            for(int y=top;y>=floorY;y--) {
                BlockPos pos=new BlockPos(x,y,z);
                BlockPos under=pos.below();
                BlockState ground=level.getBlockState(under);
                if(!ground.isSolidRender(level,under)
                        || !level.getBlockState(pos).isAir()
                        || !level.getBlockState(pos.above()).isAir())continue;
                T spawned=kind.create(level);
                if(spawned==null)return false;
                spawned.moveTo(x+0.5,y,z+0.5,level.random.nextFloat()*360f,0);
                if(!level.noCollision(spawned))continue;
                return level.addFreshEntity(spawned);
            }
        }
        return false;
    }
}
