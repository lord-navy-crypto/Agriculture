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
        boolean scaresAllowed=true;
        long lastScareTick=-1000000L;
        long lastEvidenceTick=-1000000L;
        boolean smilerAidAllowed=true;
        int smilerFavors;
        long lastSmilerFavorTick=-1000000L;
        long lastSmilerCallTick=-1000000L;
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
                +", chases="+NightwatchConfig.CHASES_ENABLED.get()
                +", jumpScares="+(e!=null&&e.scaresAllowed)
                +", smilerAid="+(e!=null&&e.smilerAidAllowed)
                +", smilerFavors="+(e==null?0:e.smilerFavors)
                +", next clue=/gugugaga investigate; "
                +"original Nightwatch observers retain 8-minute minimum cooldown.";
    }
    /** v0.12 threats require real opt-in and valid game context. */
    public static boolean isActiveParticipant(ServerPlayer player) {
        return isJoined(player.serverLevel().getServer(),player.getUUID())
                && eligible(player,true);
    }

    public static boolean inChaseAct(ServerPlayer player) {
        Entry entry=entries(player.serverLevel().getServer()).get(player.getUUID());
        return entry!=null && isActiveParticipant(player)
                && (entry.act==HauntGameplayPolicy.Act.WITNESS
                    || entry.act==HauntGameplayPolicy.Act.FALSE_SAFETY);
    }

    public static void setScaresAllowed(ServerPlayer player,boolean enabled) {
        Entry entry=entries(player.serverLevel().getServer()).get(player.getUUID());
        if(entry==null) {
            player.sendSystemMessage(Component.literal("[Nightwatch] Join the game first."));
            return;
        }
        entry.scaresAllowed=enabled;
        player.sendSystemMessage(Component.literal(
                "[Nightwatch] Personal surprise cues "+(enabled?"enabled":"disabled")+"."));
    }

    /** Gameplay help is opt-out per player, not a hidden AI/player-control hook. */
    public static boolean smilerAidEnabled(ServerPlayer player) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        return e!=null && e.smilerAidAllowed && inChaseAct(player);
    }

    public static void setSmilerAid(ServerPlayer player,boolean allowed) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        if(e==null) {
            player.sendSystemMessage(Component.literal("[Nightwatch] Join the game first."));
            return;
        }
        e.smilerAidAllowed=allowed;
        player.sendSystemMessage(Component.literal(
                "[Nightwatch] Smiler help and persuasion "+(allowed?"enabled":"disabled")+"."));
    }

    /** 0..5 optional fictional favors, stored only while the session is online. */
    public static int recordSmilerFavor(ServerPlayer player) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        if(e==null || !e.smilerAidAllowed || !isActiveParticipant(player))
            return 0;
        long now=player.serverLevel().getGameTime();
        int since=(int)Math.min(Integer.MAX_VALUE,Math.max(0,now-e.lastSmilerFavorTick));
        int next=SmilerTrustPolicy.afterFavor(e.smilerFavors,since);
        if(next>e.smilerFavors) {
            e.smilerFavors=next;
            e.lastSmilerFavorTick=now;
        }
        return e.smilerFavors;
    }

    public static int smilerFavors(ServerPlayer player) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        return e==null?0:e.smilerFavors;
    }

    /** A player deliberately calls only a VISIBLE, nearby, existing Smiler. */
    public static boolean requestSmilerHelp(ServerPlayer player) {
        if(!smilerAidEnabled(player)) {
            player.sendSystemMessage(Component.literal(
                    "[Nightwatch] Join an active chase act and enable Smiler aid."));
            return false;
        }
        ServerLevel level=player.serverLevel();
        Entry e=entries(level.getServer()).get(player.getUUID());
        var yellows=level.getEntitiesOfClass(YellowSmilerEntity.class,
                player.getBoundingBox().inflate(12),Entity::isAlive);
        var birds=level.getEntitiesOfClass(GugugagaEntity.class,
                player.getBoundingBox().inflate(22),Entity::isAlive);
        YellowSmilerEntity best=null;
        double bestSq=Double.POSITIVE_INFINITY;
        for(YellowSmilerEntity yellow:yellows) {
            double squared=player.distanceToSqr(yellow);
            if(squared<bestSq && player.hasLineOfSight(yellow)) {
                best=yellow;
                bestSq=squared;
            }
        }
        double birdSq=Double.POSITIVE_INFINITY;
        for(GugugagaEntity bird:birds)
            birdSq=Math.min(birdSq,player.distanceToSqr(bird));
        long now=level.getGameTime();
        int since=(int)Math.min(Integer.MAX_VALUE,Math.max(0,now-e.lastSmilerCallTick));
        if(best==null || !SmilerTrustPolicy.canRequestAid(true,true,true,
                bestSq,birdSq,since) || !best.respondToHelpCall(player)) {
            player.sendSystemMessage(Component.literal(
                    "[Nightwatch] No nearby Smiler can reach the penguin, or the call is cooling down."));
            return false;
        }
        e.lastSmilerCallTick=now;
        player.sendSystemMessage(Component.literal(
                "[Smiler] I hear you. The bird, not you, is my priority."));
        return true;
    }

    /** Targeted, volume-capped cue, no flashes, camera seizure or forced input. */
    public static boolean tryScare(ServerPlayer player,Entity source) {
        Entry entry=entries(player.serverLevel().getServer()).get(player.getUUID());
        if(entry==null || !entry.scaresAllowed
                || !NightwatchConfig.JUMPSCARES_ENABLED.get() || !inChaseAct(player))
            return false;
        long now=player.serverLevel().getGameTime();
        int since=(int)Math.min(Integer.MAX_VALUE,Math.max(0L,now-entry.lastScareTick));
        if(!HorrorChasePolicy.canSurprise(true,true,
                player.hasLineOfSight(source),player.distanceToSqr(source),since))
            return false;
        entry.lastScareTick=now;
        if(NightwatchConfig.HAUNTING_AUDIO_ENABLED.get())
            player.playNotifySound(SoundEvents.ENDERMAN_STARE,
                    SoundSource.HOSTILE,0.36f,0.82f);
        player.displayClientMessage(Component.literal(
                source instanceof RedactorEntity
                    ? "[Nightwatch] Lapis was not what it seemed."
                    : "[Nightwatch] IT SAW YOU."),true);
        return true;
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
            } else {
                // Individually addressed cinematic cues. They never broadcast
                // to nearby unjoined players or force camera/screen effects.
                CinematicSuspensePolicy.Cue cue=DepthSuspensePolicy.adapt(
                        CinematicSuspensePolicy.choose(
                                e.act,e.activeTicks,e.loops,id.getLeastSignificantBits()),
                        !player.serverLevel().canSeeSky(player.blockPosition()),
                        id.getMostSignificantBits() ^ e.activeTicks);
                if(cue!=CinematicSuspensePolicy.Cue.NONE) {
                    if(cue==CinematicSuspensePolicy.Cue.SILENCE) {
                        player.displayClientMessage(Component.literal(
                                "[Nightwatch] Everything goes quiet."),true);
                    } else {
                        if(NightwatchConfig.HAUNTING_AUDIO_ENABLED.get()) {
                            var sound=switch(cue) {
                                case FALSE_STEP -> SoundEvents.AMETHYST_BLOCK_STEP;
                                case HOLLOW_TAP, DISTANT_CHIME -> SoundEvents.AMETHYST_BLOCK_CHIME;
                                case NONE,SILENCE -> SoundEvents.AMETHYST_BLOCK_CHIME;
                            };
                            float pitch=switch(cue) {
                                case FALSE_STEP -> 0.56f;
                                case HOLLOW_TAP -> 0.48f;
                                case DISTANT_CHIME -> 0.82f;
                                case NONE,SILENCE -> 1.0f;
                            };
                            player.playNotifySound(sound,SoundSource.AMBIENT,
                                    CinematicSuspensePolicy.volume(cue),pitch);
                        }
                        if(cue==CinematicSuspensePolicy.Cue.FALSE_STEP)
                            player.displayClientMessage(Component.literal(
                                    "[Nightwatch] A step... then nothing."),true);
                    }
                }
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
            GugugagaEntity bird=visibleNearest(level,player,GugugagaEntity.class);
            if(bird!=null) {
                evidence=HauntGameplayPolicy.Evidence.GUGU_GAGA;
                reportEvidence(player,e,HorrorEvidencePolicy.bird(
                        bird.phase()==ObserverBrain.Phase.APPROACH));
            }
        } else if(e.act==HauntGameplayPolicy.Act.FALSE_SAFETY) {
            RedactorEntity redactor=visibleNearest(level,player,RedactorEntity.class);
            if(redactor!=null) {
                evidence=HauntGameplayPolicy.Evidence.LAPIS_REDACTOR;
                reportEvidence(player,e,HorrorEvidencePolicy.redactor(redactor.revealed()));
            } else {
                YellowSmilerEntity yellow=visibleNearest(level,player,YellowSmilerEntity.class);
                if(yellow!=null) {
                    evidence=HauntGameplayPolicy.Evidence.YELLOW_SMILER;
                    reportEvidence(player,e,HorrorEvidencePolicy.smiler(yellow.revealed()));
                }
            }
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
    private static void reportEvidence(ServerPlayer player,Entry entry,
            HorrorEvidencePolicy.Trace trace) {
        if(!NightwatchConfig.EVIDENCE_CLUES_ENABLED.get())return;
        long now=player.serverLevel().getGameTime();
        if(now-entry.lastEvidenceTick<20*3)return;
        entry.lastEvidenceTick=now;
        player.sendSystemMessage(Component.literal("[Nightwatch Observation] "
                +HorrorEvidencePolicy.description(trace)));
    }

    /** Actual currently loaded and visible entities only, nearest first. */
    private static <T extends Entity> T visibleNearest(
            ServerLevel level,ServerPlayer player,Class<T> clazz) {
        AABB search=player.getBoundingBox().inflate(12);
        T closest=null;
        double best=HauntGameplayPolicy.EVIDENCE_RADIUS_SQ+0.001;
        for(T other:level.getEntitiesOfClass(clazz,search,Entity::isAlive)) {
            double squared=player.distanceToSqr(other);
            if(squared<best && HauntGameplayPolicy.nearEvidence(squared,
                    player.hasLineOfSight(other))) {
                best=squared;
                closest=other;
            }
        }
        return closest;
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
                if(spawned instanceof RedactorEntity redactor)
                    redactor.markStageEncounter();
                if(spawned instanceof YellowSmilerEntity yellow)
                    yellow.markStageEncounter();
                if(!level.noCollision(spawned))continue;
                return level.addFreshEntity(spawned);
            }
        }
        return false;
    }
}
