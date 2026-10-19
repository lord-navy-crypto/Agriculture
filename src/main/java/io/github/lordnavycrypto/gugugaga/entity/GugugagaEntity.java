package io.github.lordnavycrypto.gugugaga.entity;

import io.github.lordnavycrypto.gugugaga.ai.HauntingCuePolicy;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayRuntime;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournal;
import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain;
import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy;
import io.github.lordnavycrypto.gugugaga.ai.CorruptionArcPolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy.Mood;
import net.minecraft.world.damagesource.DamageSource;
import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain.Phase;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Server-owned Nightwatch observer. World/AI evaluation every ten ticks,
 * path requests no faster than once per 30 ticks, with player opt-out,
 * bounded ambient lifetime and sparse original sound/particle storytelling.
 *
 * Never attacks, alters terrain, loads new chunks or requires Ollama.
 */
public class GugugagaEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(GugugagaEntity.class, EntityDataSerializers.INT);
    private static final int AUTO_LIFETIME_TICKS = 20 * 90;
    private static final EntityDataAccessor<Integer> MOOD =
            SynchedEntityData.defineId(GugugagaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CORRUPTION_STAGE =
            SynchedEntityData.defineId(GugugagaEntity.class,EntityDataSerializers.INT);
    private int moodTicks;
    private int strikeWindupTicks;
    private int ticksSinceStrike=ThreatPolicy.ATTACK_COOLDOWN_TICKS;
    private int phaseTicks;
    private boolean ambientEncounter;
    private int ambientLifetimeTicks;

    public GugugagaEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        // Manual /summon encounters are intentionally persistent. Automatic
        // sightings still expire after their separate 90-second lifetime.
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PHASE, Phase.DORMANT.ordinal());
        builder.define(MOOD, Mood.CALM.ordinal());
        builder.define(CORRUPTION_STAGE,0);
    }

    @Override
    protected void registerGoals() {
        // A watching creature must be still when dormant. Unneeded random
        // pathfinding in its old RandomStrollGoal fought our own AI.
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    /** Only the opt-in director marks ambient encounters; /summon stays persistent. */
    public void markAmbientEncounter() {
        ambientEncounter = true;
        ambientLifetimeTicks = 0;
        setPersistenceRequired();
    }

    public Mood mood() {
        return ThreatPolicy.fromOrdinal(getEntityData().get(MOOD));
    }

    public int corruptionStage() {
        return CorruptionArcPolicy.clampStage(getEntityData().get(CORRUPTION_STAGE));
    }

    public void setTemperament(Mood requested) {
        Mood selected=requested==null ? Mood.CALM : requested;
        getEntityData().set(MOOD,selected.ordinal());
        moodTicks=0;
        getEntityData().set(CORRUPTION_STAGE,0);
        strikeWindupTicks=0;
        ticksSinceStrike=ThreatPolicy.ATTACK_COOLDOWN_TICKS;
        getNavigation().stop();
    }

    @Override
    public boolean hurt(DamageSource source,float damage) {
        boolean applied=super.hurt(source,damage);
        // The bird becomes upset when disturbed; by default it still will
        // not attack. Only the server owner's explicit hostility toggle
        // permits chase/strike actions.
        if(applied && !level().isClientSide && source.getEntity() instanceof ServerPlayer
                && mood()==Mood.CALM)
            setTemperament(Mood.ANGRY);
        return applied;
    }

    /** Operator-only alpha test shortcut for the final visible arc stage. */
    public void testBlackenedCorruption() {
        setTemperament(Mood.CORRUPTED);
        moodTicks=CorruptionArcPolicy.BLACKEN_TICKS;
        getEntityData().set(CORRUPTION_STAGE,
                CorruptionArcPolicy.Stage.BLACKENED.ordinal());
    }

    public Phase phase() {
        int value=getEntityData().get(PHASE);
        return value>=0 && value<Phase.values().length
                ? Phase.values()[value] : Phase.DORMANT;
    }

    private ServerPlayer eligibleNearestPlayer(ServerLevel world) {
        AABB bounds=getBoundingBox().inflate(32.0);
        ServerPlayer closest=null;
        double closestDistance=32.0*32.0;
        // This queries only loaded entities around THIS observer, not other
        // chunks or a server-wide player/world scan.
        for(ServerPlayer candidate:world.getEntitiesOfClass(ServerPlayer.class,bounds,
                p->p.isAlive()&&!p.isSpectator()&&!p.isCreative()
                    && !NightwatchDirector.isQuiet(world.getServer(),p.getUUID())
                    && HauntGameplayRuntime.canTargetPlayer(p))) {
            double distance=distanceToSqr(candidate);
            if(distance<=closestDistance) {
                closestDistance=distance;
                closest=candidate;
            }
        }
        return closest;
    }

    /** Inspect a real entity directly; no text command or admin rights needed. */
    @Override
    protected InteractionResult mobInteract(Player player,InteractionHand hand) {
        if(hand!=InteractionHand.MAIN_HAND)return InteractionResult.PASS;
        if(level().isClientSide)return InteractionResult.SUCCESS;
        if(player instanceof ServerPlayer participant) {
            if(!HauntGameplayRuntime.investigate(participant,this)) {
                String key=switch(mood()) {
                    case ANGRY -> "message.gugugaga.interact.angry";
                    case CORRUPTED -> "message.gugugaga.interact.corrupted";
                    default -> "message.gugugaga.interact.calm";
                };
                participant.displayClientMessage(Component.translatable(key),true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide)return;
        moodTicks=Math.min(ThreatPolicy.CORRUPTION_LIFETIME_TICKS,moodTicks+1);
        if(tickCount%10==0) {
            int newStage=CorruptionArcPolicy.stage(
                    mood()==Mood.CORRUPTED,moodTicks).ordinal();
            if(getEntityData().get(CORRUPTION_STAGE)!=newStage)
                getEntityData().set(CORRUPTION_STAGE,newStage);
        }
        ticksSinceStrike=Math.min(ThreatPolicy.ATTACK_COOLDOWN_TICKS,ticksSinceStrike+1);
        if(ThreatPolicy.afterTime(mood(),moodTicks)!=mood())
            setTemperament(Mood.CALM);
        if(ambientEncounter && ++ambientLifetimeTicks>=AUTO_LIFETIME_TICKS) {
            // Automatically spawned sightings dissipate after 90 seconds,
            // preventing permanent accumulation. Manually summoned mobs stay.
            if(NightwatchConfig.HAUNTING_PARTICLES_ENABLED.get()
                    && level() instanceof ServerLevel server)
                server.sendParticles(ParticleTypes.SOUL,getX(),getY()+1.0,getZ(),
                        2,0.22,0.2,0.22,0.01);
            discard();
            return;
        }
        if(tickCount%10!=0 || !(level() instanceof ServerLevel world))return;
        // The Lost Watcher notices the Redactor before ordinary player
        // curiosity. It does NOT know the Yellow Smiler's true purpose and
        // will not detect him through walls or across unloaded dimensions.
        if(tickCount%20==0 && mood()!=Mood.CORRUPTED) {
            var redactors=world.getEntitiesOfClass(RedactorEntity.class,
                    getBoundingBox().inflate(22.0),
                    net.minecraft.world.entity.Entity::isAlive);
            RedactorEntity nearest=redactors.stream().min(
                    java.util.Comparator.comparingDouble(this::distanceToSqr))
                    .orElse(null);
            if(nearest!=null
                    && io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.gugu(
                            distanceToSqr(nearest))
                    ==io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.GuguReaction.NOTICE_REDACTOR) {
                getNavigation().stop();
                getLookControl().setLookAt(nearest,9f,9f);
                if(phase()!=Phase.OBSERVE)changePhase(Phase.OBSERVE);
                return;
            }
        }
        ServerPlayer player=eligibleNearestPlayer(world);
        if(player==null) {
            if(phase()!=Phase.DORMANT)changePhase(Phase.DORMANT);
            getNavigation().stop();
            return;
        }

        boolean watched=player.hasLineOfSight(this)&&isLookingAt(player);
        double distance=distanceToSqr(player);
        if(handleTemperament(world,player,distance))return;
        Phase prior=phase();
        Phase next=ObserverBrain.next(prior,watched,distance,phaseTicks);
        if(next!=prior) {
            changePhase(next);
            if(next==Phase.RETREAT && NightwatchConfig.HAUNTING_PARTICLES_ENABLED.get())
                world.sendParticles(ParticleTypes.SOUL,getX(),getY()+1.0,getZ(),
                        2,0.15,0.18,0.15,0.01);
        } else phaseTicks+=10;

        // A three-bit witness journal grows only when a player experiences
        // a new aspect of the observer. It never stores player coordinates,
        // scans chunks, or changes the underlying AI behavior.
        WitnessJournal.record(player,phase(),watched,distance,
                prior!=Phase.RETREAT && phase()==Phase.RETREAT);

        switch(phase()) {
            case DORMANT -> getNavigation().stop();
            case OBSERVE -> {
                getNavigation().stop();
                getLookControl().setLookAt(player,15f,15f);
            }
            case APPROACH -> {
                if(watched)getNavigation().stop();
                // Reissuing an A* path every 10 ticks wastes CPU and
                // sometimes causes path thrashing; 30 ticks is responsive.
                else if(tickCount%30==0)getNavigation().moveTo(player,0.70);
                getLookControl().setLookAt(player,15f,15f);
            }
            case RETREAT -> {
                if(tickCount%30==0) {
                    Vec3 away=position().subtract(player.position());
                    if(away.lengthSqr()<0.01)away=new Vec3(1,0,0);
                    Vec3 target=position().add(away.normalize().scale(7.0));
                    getNavigation().moveTo(target.x,target.y,target.z,0.85);
                }
            }
        }
        playSparseCue(world,watched,distance);
    }

    /**
     * Reversible warning, pursuit and brief melee behavior. Only enabled by
     * the server owner. One modest strike after an observable 30-tick windup,
     * at most once per 75 ticks, then another long waiting period.
     */
    private boolean handleTemperament(ServerLevel world,ServerPlayer player,
            double distanceSquared) {
        if(mood()==Mood.CALM || !NightwatchConfig.HOSTILITY_ENABLED.get()) {
            strikeWindupTicks=0;
            return false;
        }
        if(!ThreatPolicy.canChase(mood(),true,true,false,distanceSquared)) {
            strikeWindupTicks=0;
            getNavigation().stop();
            return true;
        }
        getLookControl().setLookAt(player,15f,15f);
        if(tickCount%30==0)
            getNavigation().moveTo(player,mood()==Mood.CORRUPTED?0.80:0.73);
        // The player gets an audible and visible warning at close range.
        // No global camera effect, no flashes and no forced player inputs.
        if(distanceSquared<=8.0*8.0 && player.hasLineOfSight(this)) {
            if(strikeWindupTicks==0 && NightwatchConfig.HAUNTING_AUDIO_ENABLED.get())
                world.playSound(null,getX(),getY()+0.7,getZ(),
                        SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.HOSTILE,
                        0.23f,mood()==Mood.CORRUPTED?0.55f:0.72f);
            strikeWindupTicks=Math.min(ThreatPolicy.ATTACK_TELEGRAPH_TICKS,
                    strikeWindupTicks+10);
        } else strikeWindupTicks=0;
        if(ThreatPolicy.canStrike(mood(),true,true,false,
                player.hasLineOfSight(this),distanceSquared,
                strikeWindupTicks,ticksSinceStrike)) {
            player.hurt(world.damageSources().mobAttack(this),
                    ThreatPolicy.damage(mood()));
            ticksSinceStrike=0;
            strikeWindupTicks=0;
            getNavigation().stop();
        }
        return true;
    }

    private void playSparseCue(ServerLevel world,boolean watched,double distance) {
        var cue=HauntingCuePolicy.choose(phase(),
                NightwatchConfig.HAUNTING_AUDIO_ENABLED.get(),
                true,watched,distance,phaseTicks,tickCount,
                getUUID().getLeastSignificantBits());
        if(cue==HauntingCuePolicy.Cue.NONE)return;
        SoundEvent sound=switch(cue) {
            case DISTANT_CHIME -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case SOFT_STEP -> SoundEvents.AMETHYST_BLOCK_STEP;
            case FAINT_PULSE -> SoundEvents.AMETHYST_BLOCK_CHIME;
            case NONE -> SoundEvents.AMETHYST_BLOCK_CHIME;
        };
        float pitch=switch(cue) {
            case DISTANT_CHIME -> 0.66f;
            case SOFT_STEP -> 0.59f;
            case FAINT_PULSE -> 0.80f;
            case NONE -> 1.0f;
        };
        world.playSound(null,getX(),getY()+0.7,getZ(),sound,
                SoundSource.AMBIENT,HauntingCuePolicy.cappedVolume(cue),pitch);
    }

    private boolean isLookingAt(ServerPlayer player) {
        Vec3 toObserver=getEyePosition().subtract(player.getEyePosition());
        return toObserver.lengthSqr()>0.01
                && player.getLookAngle().dot(toObserver.normalize())>0.90;
    }

    private void changePhase(Phase newPhase) {
        getEntityData().set(PHASE,newPhase.ordinal());
        phaseTicks=0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        // Eliminate random parrot sounds: cues now carry deliberate timing.
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ObserverPhase",phase().ordinal());
        tag.putInt("ObserverPhaseTicks",phaseTicks);
        tag.putInt("ObserverMood",mood().ordinal());
        tag.putInt("ObserverMoodTicks",moodTicks);
        tag.putInt("ObserverStrikeWait",ticksSinceStrike);
        tag.putBoolean("NightwatchAmbient",ambientEncounter);
        tag.putInt("NightwatchLifetime",ambientLifetimeTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int saved=tag.getInt("ObserverPhase");
        getEntityData().set(PHASE,(saved<0||saved>=Phase.values().length)
                ? Phase.DORMANT.ordinal() : saved);
        phaseTicks=Math.max(0,Math.min(1200,tag.getInt("ObserverPhaseTicks")));
        getEntityData().set(MOOD,ThreatPolicy.fromOrdinal(
                tag.getInt("ObserverMood")).ordinal());
        moodTicks=Math.max(0,Math.min(ThreatPolicy.CORRUPTION_LIFETIME_TICKS,
                tag.getInt("ObserverMoodTicks")));
        getEntityData().set(CORRUPTION_STAGE,
                CorruptionArcPolicy.stage(mood()==Mood.CORRUPTED,moodTicks).ordinal());
        // Use a full cooldown when loaded from disk; never surprise-hit a
        // player immediately after a world reload.
        ticksSinceStrike=0;
        strikeWindupTicks=0;
        ambientEncounter=tag.getBoolean("NightwatchAmbient");
        ambientLifetimeTicks=Math.max(0,Math.min(AUTO_LIFETIME_TICKS,
                tag.getInt("NightwatchLifetime")));
    }
}
