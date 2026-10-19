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
        int gazeFocusTicks;
        int loops;
        boolean scaresAllowed=true;
        long lastScareTick=-1000000L;
        long lastEvidenceTick=-1000000L;
        boolean smilerAidAllowed=true;
        int smilerFavors;
        int witnessSpawnAttempts;
        int redactorSpawnAttempts;
        int smilerSpawnAttempts;
        long lastSmilerFavorTick=-1000000L;
        long lastSmilerCallTick=-1000000L;
        long lastListenTick=-1000000L;
        boolean paused;
    }
    private static final Map<MinecraftServer,Map<UUID,Entry>> SESSIONS=
            new WeakHashMap<>();
    private static Map<UUID,Entry> entries(MinecraftServer server){
        return SESSIONS.computeIfAbsent(server,x->new HashMap<>());
    }
    public static boolean isJoined(MinecraftServer server,UUID id) {
        Entry entry=entries(server).get(id);
        return entry!=null && !entry.paused;
    }
    public static void join(ServerPlayer player) {
        Map<UUID,Entry> all=entries(player.serverLevel().getServer());
        Entry existing=all.get(player.getUUID());
        if(existing==null || existing.paused)all.put(player.getUUID(),new Entry());
        E2EGameplayProof.record("JOINED");
        player.displayClientMessage(Component.literal(
                "[Nightwatch] Your session is active. Events unfold naturally at night. "
                +"Look at a creature or right-click it to inspect. Sneak + V listens; H opens the panel."),
                true);
    }
    public static void leave(ServerPlayer player) {
        Entry entry=entries(player.serverLevel().getServer()).computeIfAbsent(
                player.getUUID(),id->new Entry());
        entry.paused=true;
        entry.act=HauntGameplayPolicy.Act.STILLNESS;
        entry.activeTicks=0;
        entry.gazeFocusTicks=0;
        player.displayClientMessage(Component.literal(
                "[Nightwatch] Your personal horror session is paused. Press H to resume."),
                true);
    }
    public static String status(ServerPlayer player) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        boolean joined=e!=null&&!e.paused;
        return "Nightwatch="+NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()
                +", session="+(joined?"ACTIVE":e!=null?"PAUSED":"WAITING FOR NIGHT")
                +", act="+(joined?e.act:"NONE")
                +", scares="+(joined&&e.scaresAllowed)
                +", Yellow aid="+(joined&&e.smilerAidAllowed)
                +", favors="+(joined?e.smilerFavors:0)
                +". Press H for the in-game panel.";
    }

    /** v0.12 threats require real opt-in and valid game context. */
    public static boolean isActiveParticipant(ServerPlayer player) {
        return isJoined(player.serverLevel().getServer(),player.getUUID())
                && eligible(player,true);
    }

    public static boolean inChaseAct(ServerPlayer player) {
        Entry entry=entries(player.serverLevel().getServer()).get(player.getUUID());
        return entry!=null && !entry.paused && isActiveParticipant(player)
                && (entry.act==HauntGameplayPolicy.Act.WITNESS
                    || entry.act==HauntGameplayPolicy.Act.FALSE_SAFETY);
    }

    public static void setScaresAllowed(ServerPlayer player,boolean enabled) {
        Entry entry=entries(player.serverLevel().getServer()).get(player.getUUID());
        if(entry==null || entry.paused) {
            player.displayClientMessage(Component.literal("[Nightwatch] Resume Nightwatch from the H panel first."),true);
            return;
        }
        entry.scaresAllowed=enabled;
        player.displayClientMessage(Component.literal(
                "[Nightwatch] Surprise cues "+(enabled?"enabled":"disabled")+"."),true);
    }

    /** Gameplay help is opt-out per player, not a hidden AI/player-control hook. */
    public static boolean smilerAidEnabled(ServerPlayer player) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        return e!=null && e.smilerAidAllowed && inChaseAct(player);
    }

    public static void setSmilerAid(ServerPlayer player,boolean allowed) {
        Entry e=entries(player.serverLevel().getServer()).get(player.getUUID());
        if(e==null || e.paused) {
            player.displayClientMessage(Component.literal("[Nightwatch] Resume Nightwatch from the H panel first."),true);
            return;
        }
        e.smilerAidAllowed=allowed;
        player.displayClientMessage(Component.literal(
                "[Nightwatch] Yellow Smiler aid "+(allowed?"enabled":"disabled")+"."),true);
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
        E2EGameplayProof.record("HELP_SUCCESS");
        player.sendSystemMessage(Component.literal(
                "[Smiler] I hear you. The bird, not you, is my priority."));
        return true;
    }

    /**
     * Active crouched listening reveals only a COARSE relative sound and range,
     * from real loaded nearby mobs. Players cannot use it as an all-seeing map.
     * No sound is generated for entities that do not exist.
     */
    /** Sneak + V is intentionally gated by the player's crouch state. */
    public static boolean listen(ServerPlayer player) {
        return listenWithIntent(player,player.isCrouching());
    }

    /** Explicit button press from the Nightwatch panel counts as deliberate intent. */
    public static boolean listenFromPanel(ServerPlayer player) {
        return listenWithIntent(player,true);
    }

    private static boolean listenWithIntent(ServerPlayer player,boolean crouching) {
        Entry entry=entries(player.serverLevel().getServer()).get(player.getUUID());
        if(entry==null || entry.paused || !isActiveParticipant(player)) {
            player.displayClientMessage(Component.literal(
                    "[Nightwatch] Nightwatch is not active. Press H to join at night."),
                    true);
            return false;
        }
        long now=player.serverLevel().getGameTime();
        if(!HorrorListeningPolicy.mayListen(true,crouching,
                now,entry.lastListenTick)) {
            player.displayClientMessage(Component.literal(
                    "[Nightwatch] Hold sneak to listen, or use the panel button. Try again after the 12-second cooldown."),
                    true);
            return false;
        }
        entry.lastListenTick=now; // empty attempts also consume one cooldown
        E2EGameplayProof.record("LISTEN_SUCCESS");
        ServerLevel level=player.serverLevel();
        AABB nearby=player.getBoundingBox().inflate(20);
        Entity closest=null;
        HorrorListeningPolicy.Source source=HorrorListeningPolicy.Source.NONE;
        double nearest=HorrorListeningPolicy.MAX_DISTANCE_SQ+0.001;
        for(GugugagaEntity bird:level.getEntitiesOfClass(
                GugugagaEntity.class,nearby,Entity::isAlive)) {
            double squared=player.distanceToSqr(bird);
            if(squared<nearest) {
                nearest=squared;closest=bird;
                source=HorrorListeningPolicy.Source.WATCHER;
            }
        }
        for(RedactorEntity redactor:level.getEntitiesOfClass(
                RedactorEntity.class,nearby,Entity::isAlive)) {
            double squared=player.distanceToSqr(redactor);
            if(squared<nearest) {
                nearest=squared;closest=redactor;
                source=HorrorListeningPolicy.Source.BLUE;
            }
        }
        for(YellowSmilerEntity yellow:level.getEntitiesOfClass(
                YellowSmilerEntity.class,nearby,Entity::isAlive)) {
            double squared=player.distanceToSqr(yellow);
            if(squared<nearest) {
                nearest=squared;closest=yellow;
                source=HorrorListeningPolicy.Source.SMILER;
            }
        }
        String clue;
        if(closest!=null && HorrorListeningPolicy.audible(nearest)) {
            var facing=player.getLookAngle();
            var relative=closest.position().subtract(player.position());
            var direction=HorrorListeningPolicy.direction(
                    facing.x,facing.z,relative.x,relative.z);
            clue=HorrorListeningPolicy.hint(source,direction,nearest);
        } else {
            clue=HorrorListeningPolicy.hint(
                    HorrorListeningPolicy.Source.NONE,
                    HorrorListeningPolicy.Direction.AHEAD,0);
        }
        player.displayClientMessage(Component.literal("[Nightwatch] "+clue),true);
        player.sendSystemMessage(Component.literal("[Nightwatch Listening] "+clue));
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
            if(e==null && eligible(player,true)) {
                e=new Entry();
                active.put(id,e);
                E2EGameplayProof.record("AUTO_JOINED");
                player.displayClientMessage(Component.literal(
                        "[Nightwatch] The night grows strange. Press H to open Nightwatch."),
                        true);
            }
            if(e==null || e.paused || !eligible(player,true))continue;
            e.activeTicks=Math.min(Integer.MAX_VALUE-20,e.activeTicks+20);
            // Natural investigation: sustained direct gaze at REAL, nearby,
            // line-of-sight visible actor. No secret stage commands needed.
            // This is evaluated only once a second, for eligible participants.
            if(e.act==HauntGameplayPolicy.Act.WITNESS
                    || e.act==HauntGameplayPolicy.Act.FALSE_SAFETY) {
                Entity focused=focusedEvidence(player.serverLevel(),player,e.act);
                e.gazeFocusTicks=GazeInvestigationPolicy.nextTicks(
                        e.gazeFocusTicks,focused!=null);
                if(focused!=null && (e.gazeFocusTicks==20
                        || e.gazeFocusTicks==60)) {
                    player.displayClientMessage(Component.literal(
                            "[Nightwatch] Observing... "
                            +(e.gazeFocusTicks/20)+"/4"),true);
                }
                if(GazeInvestigationPolicy.complete(e.gazeFocusTicks)) {
                    e.gazeFocusTicks=0;
                    // Retain the SAME server-side evidence and state transition
                    // that the old /investigate fallback uses.
                    if(investigate(player))continue;
                }
            } else e.gazeFocusTicks=0;
            HauntGameplayPolicy.Act next=HauntGameplayPolicy.advance(
                    e.act,e.activeTicks,HauntGameplayPolicy.Evidence.NONE,false);
            if(next!=e.act) {
                e.act=next;
                e.activeTicks=0;
                e.gazeFocusTicks=0;
                if(next==HauntGameplayPolicy.Act.STILLNESS) {
                    e.loops++;
                    E2EGameplayProof.record("LOOP_TO_STILLNESS");
                }
                player.sendSystemMessage(Component.literal(stageMessage(next)));
                // Act-specific scripted sighting is separate from the slow,
                // unsolicited Nightwatch ambient encounter scheduler.
                if(next==HauntGameplayPolicy.Act.WITNESS) {
                    e.witnessSpawnAttempts=1;
                    if(ensureNearby(player.serverLevel(),player,GuguGagaMod.OBSERVER.get()))
                        e.witnessSpawnAttempts=3;
                }
            } else {
                // If local terrain has no suitable room, retry twice at
                // most, every 20s, until one nearby loaded actor is found.
                if(e.act==HauntGameplayPolicy.Act.WITNESS
                        && e.activeTicks>0 && e.activeTicks%(20*20)==0
                        && e.witnessSpawnAttempts<3) {
                    e.witnessSpawnAttempts++;
                    if(ensureNearby(player.serverLevel(),player,GuguGagaMod.OBSERVER.get()))
                        e.witnessSpawnAttempts=3;
                }
                // FALSE_SAFETY requires an actor to investigate. Placement
                // may fail near cliffs/structures, so retry a bounded number
                // of times rather than leaving the encounter softlocked.
                if(e.act==HauntGameplayPolicy.Act.FALSE_SAFETY
                        && e.activeTicks>0 && e.activeTicks%(20*20)==0) {
                    if(e.redactorSpawnAttempts<3) {
                        e.redactorSpawnAttempts++;
                        if(ensureNearby(player.serverLevel(),player,
                                GuguGagaMod.REDACTOR.get()))
                            e.redactorSpawnAttempts=3;
                    }
                    if(e.smilerSpawnAttempts<3) {
                        e.smilerSpawnAttempts++;
                        if(ensureNearby(player.serverLevel(),player,
                                GuguGagaMod.YELLOW_SMILER.get()))
                            e.smilerSpawnAttempts=3;
                    }
                }
                // Real threatened-cave proximity: a restrained sound that
                // happens only if an actual Redactor/Gugu is inside 18 blocks.
                // At most once per 12s to the enrolled player, not the room.
                if((e.act==HauntGameplayPolicy.Act.WITNESS
                        || e.act==HauntGameplayPolicy.Act.FALSE_SAFETY)
                        && CaveThreatSoundPolicy.due(e.activeTicks)
                        && NightwatchConfig.CAVE_THREAT_AUDIO_ENABLED.get()
                        && NightwatchConfig.HAUNTING_AUDIO_ENABLED.get()
                        && !player.serverLevel().canSeeSky(player.blockPosition())) {
                    var area=player.getBoundingBox().inflate(18);
                    double closestSq=Double.POSITIVE_INFINITY;
                    for(GugugagaEntity bird:player.serverLevel().getEntitiesOfClass(
                            GugugagaEntity.class,area,Entity::isAlive))
                        closestSq=Math.min(closestSq,player.distanceToSqr(bird));
                    for(RedactorEntity redactor:player.serverLevel().getEntitiesOfClass(
                            RedactorEntity.class,area,Entity::isAlive))
                        closestSq=Math.min(closestSq,player.distanceToSqr(redactor));
                    var pressure=CaveThreatSoundPolicy.classify(true,true,
                            Double.isFinite(closestSq),closestSq);
                    if(pressure!=CaveThreatSoundPolicy.Pressure.NONE) {
                        player.playNotifySound(
                                pressure==CaveThreatSoundPolicy.Pressure.DISTANT
                                ?SoundEvents.AMETHYST_BLOCK_CHIME
                                :SoundEvents.AMETHYST_BLOCK_STEP,
                                SoundSource.AMBIENT,
                                CaveThreatSoundPolicy.volume(pressure),
                                CaveThreatSoundPolicy.pitch(pressure));
                    }
                }
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
            case STILLNESS -> "The forest falls quiet. Something is watching beyond the trees.";
            case WHISPERS -> "A sound answers from the dark. Follow it carefully.";
            case WITNESS -> "A figure has appeared. Keep it in sight, or approach and right-click to inspect.";
            case FALSE_SAFETY -> "A blue figure and a smiling stranger are nearby. One is not what it seems.";
            case RESPITE -> "The encounter recedes. The world grows quiet again.";
        };
    }
    public static boolean investigate(ServerPlayer player) {
        return investigate(player,null);
    }

    /** Investigation can come from a four-second gaze or a real right-click on the actor. */
    public static boolean investigate(ServerPlayer player,Entity target) {
        Map<UUID,Entry> all=entries(player.serverLevel().getServer());
        Entry e=all.get(player.getUUID());
        if(e==null || e.paused || !eligible(player,true)) {
            player.displayClientMessage(Component.literal(
                    "[Nightwatch] Nightwatch is not active. Press H to join when the world is ready."),
                    true);
            return false;
        }
        ServerLevel level=player.serverLevel();
        HauntGameplayPolicy.Evidence evidence=HauntGameplayPolicy.Evidence.NONE;
        if(e.act==HauntGameplayPolicy.Act.WITNESS) {
            GugugagaEntity bird=target instanceof GugugagaEntity candidate
                    ?visibleTarget(player,candidate)?candidate:null
                    :target==null?visibleNearest(level,player,GugugagaEntity.class):null;
            if(bird!=null) {
                evidence=HauntGameplayPolicy.Evidence.GUGU_GAGA;
                reportEvidence(player,e,HorrorEvidencePolicy.bird(
                        bird.phase()==ObserverBrain.Phase.APPROACH));
            }
        } else if(e.act==HauntGameplayPolicy.Act.FALSE_SAFETY) {
            RedactorEntity redactor=target instanceof RedactorEntity candidate
                    ?visibleTarget(player,candidate)?candidate:null
                    :target==null?visibleNearest(level,player,RedactorEntity.class):null;
            if(redactor!=null) {
                evidence=HauntGameplayPolicy.Evidence.LAPIS_REDACTOR;
                reportEvidence(player,e,HorrorEvidencePolicy.redactor(redactor.revealed()));
            } else {
                YellowSmilerEntity yellow=target instanceof YellowSmilerEntity candidate
                        ?visibleTarget(player,candidate)?candidate:null
                        :target==null?visibleNearest(level,player,YellowSmilerEntity.class):null;
                if(yellow!=null) {
                    evidence=HauntGameplayPolicy.Evidence.YELLOW_SMILER;
                    reportEvidence(player,e,HorrorEvidencePolicy.smiler(yellow.revealed()));
                }
            }
        }
        HauntGameplayPolicy.Act next=HauntGameplayPolicy.advance(e.act,
                e.activeTicks,evidence,true);
        if(next==e.act) {
            player.displayClientMessage(Component.literal(
                    "No new clue yet. Keep watching, listen, or approach the creature."),
                    true);
            return false;
        }
        e.act=next;
        e.activeTicks=0;
        e.gazeFocusTicks=0;
        E2EGameplayProof.record("STAGE_"+next);
        player.displayClientMessage(Component.literal(stageMessage(next)),true);
        if(next==HauntGameplayPolicy.Act.FALSE_SAFETY) {
            e.redactorSpawnAttempts=1;
            e.smilerSpawnAttempts=1;
            if(ensureNearby(level,player,GuguGagaMod.REDACTOR.get()))
                e.redactorSpawnAttempts=3;
            if(ensureNearby(level,player,GuguGagaMod.YELLOW_SMILER.get()))
                e.smilerSpawnAttempts=3;
        }
        else if(next==HauntGameplayPolicy.Act.RESPITE)
            ensureNearby(level,player,GuguGagaMod.YELLOW_SMILER.get());
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
    /** The player must look AT the actor, not just stand near one.
     * This does not track aim/history outside the user's current session.
     */
    private static Entity focusedEvidence(ServerLevel level,ServerPlayer player,
            HauntGameplayPolicy.Act act) {
        if(act==HauntGameplayPolicy.Act.WITNESS)
            return focusedNearest(level,player,GugugagaEntity.class);
        if(act==HauntGameplayPolicy.Act.FALSE_SAFETY) {
            Entity blue=focusedNearest(level,player,RedactorEntity.class);
            if(blue!=null)return blue;
            return focusedNearest(level,player,YellowSmilerEntity.class);
        }
        return null;
    }

    private static <T extends Entity> T focusedNearest(
            ServerLevel level,ServerPlayer player,Class<T> clazz) {
        var bounds=player.getBoundingBox().inflate(12);
        T closest=null;
        double nearest=HauntGameplayPolicy.EVIDENCE_RADIUS_SQ+0.001;
        for(T entity:level.getEntitiesOfClass(clazz,bounds,Entity::isAlive)) {
            double distance=player.distanceToSqr(entity);
            var toActor=entity.getEyePosition().subtract(player.getEyePosition());
            if(toActor.lengthSqr()<0.0001)continue;
            boolean sight=player.hasLineOfSight(entity);
            double dot=player.getLookAngle().dot(toActor.normalize());
            if(Boolean.getBoolean("gugugaga.e2e"))
                System.out.println("GUGUGAGA_E2E_GAZE_DIAGNOSTIC "+clazz.getSimpleName()
                        +" dist="+Math.sqrt(distance)+" los="+sight
                        +" dot="+String.format(java.util.Locale.ROOT,"%.3f",dot)
                        +" phase="+player.getYRot()+"/"+player.getXRot());
            if(distance>=nearest || !sight)continue;
            if(GazeInvestigationPolicy.aimedAt(dot,distance,true,true)) {
                closest=entity;
                nearest=distance;
            }
        }
        return closest;
    }

    private static boolean visibleTarget(ServerPlayer player,Entity target) {
        return target.isAlive() && HauntGameplayPolicy.nearEvidence(
                player.distanceToSqr(target),player.hasLineOfSight(target));
    }

    public static boolean isInAct(ServerPlayer player,HauntGameplayPolicy.Act act) {
        Entry entry=entries(player.serverLevel().getServer()).get(player.getUUID());
        return entry!=null && !entry.paused && entry.act==act
                && isActiveParticipant(player);
    }

    /** Contextual right-click interaction for the Guardian. */
    public static void interactWithGugu(ServerPlayer player,GugugagaEntity bird) {
        E2EGameplayProof.record("INTERACT_GUGU");
        if(isInAct(player,HauntGameplayPolicy.Act.WITNESS)) {
            if(investigate(player,bird))
                E2EGameplayProof.record("INTERACT_GUGU_STAGE_PROGRESS");
            return;
        }
        if(WitnessJournalPolicy.discovered(WitnessJournal.evidenceBits(player))>=3) {
            bird.setTemperament(ThreatPolicy.Mood.CALM);
            player.displayClientMessage(Component.literal(
                    "The penguin settles. It recognises that you have learned its signs."),
                    true);
        } else if(bird.mood()==ThreatPolicy.Mood.CORRUPTED) {
            player.displayClientMessage(Component.literal(
                    "Its feathers twitch with a dark pulse. It watches your hands, not your face."),
                    true);
        } else if(bird.mood()==ThreatPolicy.Mood.ANGRY) {
            player.displayClientMessage(Component.literal(
                    "The penguin lowers its head. A warning chime sounds from its chest."),
                    true);
        } else if(isActiveParticipant(player)) {
            player.displayClientMessage(Component.literal(
                    "The penguin studies you, then glances toward the darker trees."),
                    true);
        } else {
            player.displayClientMessage(Component.literal(
                    "The penguin holds your gaze without moving."),true);
        }
    }

    /** Lapis reveals itself through direct interaction, then records real evidence. */
    public static void interactWithRedactor(ServerPlayer player,RedactorEntity redactor) {
        E2EGameplayProof.record("INTERACT_LAPIS");
        boolean alreadyRevealed=redactor.revealed();
        redactor.setRevealed(true);
        if(isInAct(player,HauntGameplayPolicy.Act.FALSE_SAFETY)) {
            if(investigate(player,redactor)) {
                E2EGameplayProof.record("INTERACT_LAPIS_STAGE_PROGRESS");
                return;
            }
        }
        player.displayClientMessage(Component.literal(alreadyRevealed
                ?"Lapis turns toward you. The disguise is gone; it is measuring your next move."
                :"The blue figure's smile falters. Its borrowed face begins to peel away."),
                true);
    }

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
    /** Safe reusable first-cycle encounter: explicitly triggered by an OP
     * and the joined survival player, never by another player's silent entry.
     * Returns false if there is no legal loaded spawn site for any actor.
     */
    public static boolean startOperatorTestScene(ServerPlayer player,
            boolean allThree) {
        if(!isActiveParticipant(player)) {
            player.sendSystemMessage(Component.literal(
                    "[Nightwatch RC] Operator test requires world enabled, "
                    +"joined, night-time, survival and no quiet mode."));
            return false;
        }
        ServerLevel level=player.serverLevel();
        Entry entry=entries(level.getServer()).get(player.getUUID());
        entry.act=allThree?HauntGameplayPolicy.Act.FALSE_SAFETY
                :HauntGameplayPolicy.Act.WITNESS;
        entry.activeTicks=0;
        entry.gazeFocusTicks=0;
        boolean bird=ensureNearby(level,player,GuguGagaMod.OBSERVER.get());
        entry.witnessSpawnAttempts=bird?3:1;
        boolean redactor=false,smiler=false;
        if(allThree) {
            redactor=ensureNearby(level,player,GuguGagaMod.REDACTOR.get());
            smiler=ensureNearby(level,player,GuguGagaMod.YELLOW_SMILER.get());
            entry.redactorSpawnAttempts=redactor?3:1;
            entry.smilerSpawnAttempts=smiler?3:1;
        }
        player.sendSystemMessage(Component.literal(
                "[Nightwatch Sacred RC] Test scene="+entry.act
                +", nearby actual Gugu="+bird
                +", Lapis="+redactor+", Yellow="+smiler
                +". Use /gugugaga play listen while crouched; "
                +"check real entities in loaded ground near you. "
                +"Missing actors retry twice during the stage."));
        if(bird)E2EGameplayProof.record("SPAWN_GUGU");
        if(redactor)E2EGameplayProof.record("SPAWN_LAPIS");
        if(smiler)E2EGameplayProof.record("SPAWN_YELLOW");
        if(bird && redactor && smiler)E2EGameplayProof.record("TRIAD_REAL_ENTITIES");
        return bird && (!allThree || redactor || smiler);
    }

    /** Existing nearby actors count: do not spawn duplicates for retests. */
    private static <T extends Entity> boolean ensureNearby(ServerLevel level,
            ServerPlayer player,EntityType<T> kind) {
        if(!level.getEntitiesOfClass(Entity.class,
                player.getBoundingBox().inflate(48),
                e->e.isAlive() && e.getType()==kind).isEmpty())return true;
        return summonOne(level,player,kind);
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
        // Separate spawn identities: simultaneous Redactor and Smiler
        // arrivals must NOT occupy the same scripted spawn candidate.
        long speciesSalt=kind==GuguGagaMod.REDACTOR.get()
                ?0x65CE_12A5_9975L:kind==GuguGagaMod.YELLOW_SMILER.get()
                ?0x13A9_C5EA_5521L:0x35B1_DCA8_1441L;
        long seed=player.getUUID().getLeastSignificantBits()
                ^level.getGameTime()^speciesSalt;
        for(int i=0;i<8;i++) {
            double a=HauntDirectorPolicy.bearing(seed,i);
            int radius=10+i*2;
            int x=(int)Math.floor(player.getX()+Math.cos(a)*radius);
            int z=(int)Math.floor(player.getZ()+Math.sin(a)*radius);
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
                if(spawned instanceof GugugagaEntity observer)
                    observer.markAmbientEncounter(); // 90s capped stage witness
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
