package io.github.lordnavycrypto.gugugaga.entity;

import io.github.lordnavycrypto.gugugaga.ai.MimicAttackPolicy;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.RedactorAction;
import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy.Mood;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/**
 * Lapis = Redactor's public, harmless-looking mask, NOT a separate NPC.
 *
 * Yellow Smiler flight always overrides: revealing, attacks, and corruption.
 * Revealed Redactor can give a small telegraphed strike only if the server
 * operator separately enables redactor_mimic.redactor_hostility.
 * Does not load chunks, edit blocks, teleport or send network requests.
 */
public final class RedactorEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> REVEALED =
            SynchedEntityData.defineId(RedactorEntity.class,EntityDataSerializers.BOOLEAN);
    private boolean stageEncounter;
    private int stageLifetimeTicks;
    public void markStageEncounter() {
        stageEncounter=true;
        stageLifetimeTicks=0;
    }

    private int corruptionCooldown=ThreeEntityReactionPolicy.CORRUPT_INTERVAL_TICKS;
    private int whisperCooldown;
    private int warningTicks;
    private int attackCooldown=MimicAttackPolicy.STRIKE_COOLDOWN_TICKS;
    private int quietTicks;
    private RedactorAction action=RedactorAction.IDLE_AS_LAPIS;

    public RedactorEntity(EntityType<? extends PathfinderMob> type,Level level) {
        super(type,level);
        setPersistenceRequired();
    }
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,26.0)
                .add(Attributes.MOVEMENT_SPEED,0.22)
                .add(Attributes.FOLLOW_RANGE,28.0);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REVEALED,false);
    }
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(8,new RandomLookAroundGoal(this));
    }
    public boolean revealed() {return getEntityData().get(REVEALED);}
    public void setRevealed(boolean exposed) {
        if(revealed()==exposed)return;
        getEntityData().set(REVEALED,exposed);
        warningTicks=0;
        quietTicks=0;
        getNavigation().stop();
    }
    private static <T extends Entity> T closest(RedactorEntity src,Class<T> type,
            double radius) {
        return src.level().getEntitiesOfClass(type,
                src.getBoundingBox().inflate(radius),Entity::isAlive).stream()
                .min(Comparator.comparingDouble(src::distanceToSqr)).orElse(null);
    }
    private ServerPlayer eligibleNearestPlayer(ServerLevel world) {
        return world.getEntitiesOfClass(ServerPlayer.class,
                getBoundingBox().inflate(10.0),
                p->p.isAlive()&&!p.isCreative()&&!p.isSpectator()
                    && !NightwatchDirector.isQuiet(
                            world.getServer(),p.getUUID())).stream()
                .min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
    }
    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide && stageEncounter && ++stageLifetimeTicks>=20*360) {
            // Scripted horror actors cannot accumulate in old saves forever.
            discard();
            return;
        }
        if(level().isClientSide || !(level() instanceof ServerLevel world)
                || tickCount%20!=0)return;
        corruptionCooldown=Math.min(ThreeEntityReactionPolicy.CORRUPT_INTERVAL_TICKS,
                corruptionCooldown+20);
        attackCooldown=Math.min(MimicAttackPolicy.STRIKE_COOLDOWN_TICKS,
                attackCooldown+20);
        whisperCooldown=Math.max(0,whisperCooldown-20);
        YellowSmilerEntity smiler=closest(this,YellowSmilerEntity.class,24);
        GugugagaEntity bird=closest(this,GugugagaEntity.class,24);
        double smilerSq=smiler==null?Double.POSITIVE_INFINITY:distanceToSqr(smiler);
        double guguSq=bird==null?Double.POSITIVE_INFINITY:distanceToSqr(bird);
        action=ThreeEntityReactionPolicy.redactor(smilerSq,guguSq);
        if(action==RedactorAction.FLEE_SMILER) {
            warningTicks=0;
            quietTicks=0;
            if(smiler!=null) {
                Vec3 away=position().subtract(smiler.position());
                if(away.lengthSqr()<0.01)away=new Vec3(1,0,0);
                Vec3 exit=position().add(away.normalize().scale(12));
                if(tickCount%40==0)
                    getNavigation().moveTo(exit.x,exit.y,exit.z,1.08);
                getLookControl().setLookAt(smiler,10f,10f);
            }
            return;
        }
        ServerPlayer player=eligibleNearestPlayer(world);
        boolean canSeePlayer=player!=null && hasLineOfSight(player);
        double playerSq=player==null?Double.POSITIVE_INFINITY:distanceToSqr(player);
        switch(action) {
            case REVEAL_AND_APPROACH -> {
                if(!revealed())reveal(world);
                if(bird!=null) {
                    getLookControl().setLookAt(bird,12f,12f);
                    if(guguSq>ThreeEntityReactionPolicy.CORRUPT_RADIUS_SQ
                            && tickCount%40==0)getNavigation().moveTo(bird,0.68);
                    else getNavigation().stop();
                    if(ThreeEntityReactionPolicy.mayCorrupt(false,revealed(),
                            guguSq,corruptionCooldown)) {
                        if(bird.mood()!=Mood.CORRUPTED)
                            bird.setTemperament(Mood.CORRUPTED);
                        corruptionCooldown=0;
                    }
                }
            }
            case WATCH_GUGU -> {
                getNavigation().stop();
                if(bird!=null)getLookControl().setLookAt(bird,8f,8f);
            }
            case IDLE_AS_LAPIS -> {
                getNavigation().stop();
                if(MimicAttackPolicy.shouldRevealToPlayer(!revealed(),playerSq,
                        canSeePlayer,player!=null,false)) {
                    reveal(world);
                } else if(revealed()) {
                    quietTicks=Math.min(20*15,quietTicks+20);
                    if((player==null || playerSq>8*8)&&quietTicks>=20*12)
                        setRevealed(false);
                }
                if(!revealed() && whisperCooldown==0 && player!=null
                        && playerSq<=9*9) {
                    player.sendSystemMessage(Component.literal("[Lapis] Lapis..."));
                    whisperCooldown=20*35;
                }
            }
            case FLEE_SMILER -> { /* already returned above */ }
        }
        // Combat is separate from the in-world guardian corruption mechanic.
        // A disguised Lapis cannot attack; no attack if Yellow is near.
        if(revealed() && NightwatchConfig.REDACTOR_HOSTILITY_ENABLED.get()
                && player!=null && canSeePlayer && playerSq<=6*6) {
            if(warningTicks==0 && NightwatchConfig.HAUNTING_AUDIO_ENABLED.get())
                world.playSound(null,getX(),getY()+0.8,getZ(),
                        SoundEvents.AMETHYST_BLOCK_CHIME,SoundSource.HOSTILE,0.18f,0.52f);
            warningTicks=Math.min(MimicAttackPolicy.WARNING_TICKS,warningTicks+20);
            if(MimicAttackPolicy.mayHit(true,true,false,true,false,
                    canSeePlayer,playerSq,warningTicks,attackCooldown)) {
                player.hurt(world.damageSources().mobAttack(this),
                        MimicAttackPolicy.DAMAGE);
                attackCooldown=0;
                warningTicks=0;
            }
        } else warningTicks=0;
    }
    private void reveal(ServerLevel world) {
        setRevealed(true);
        if(NightwatchConfig.HAUNTING_PARTICLES_ENABLED.get())
            world.sendParticles(ParticleTypes.ENCHANT,getX(),getY()+0.7,getZ(),
                    3,0.12,0.16,0.12,0.005);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("RedactorRevealed",revealed());
        tag.putBoolean("NightwatchStageGuest",stageEncounter);
        tag.putInt("NightwatchStageGuestLifetime",stageLifetimeTicks);
        tag.putInt("RedactorCorruptCooldown",corruptionCooldown);
        tag.putInt("RedactorAttackCooldown",attackCooldown);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        stageEncounter=tag.getBoolean("NightwatchStageGuest");
        stageLifetimeTicks=Math.max(0,Math.min(20*360,
                tag.getInt("NightwatchStageGuestLifetime")));
        setRevealed(tag.getBoolean("RedactorRevealed"));
        corruptionCooldown=Math.max(0,Math.min(
                ThreeEntityReactionPolicy.CORRUPT_INTERVAL_TICKS,
                tag.getInt("RedactorCorruptCooldown")));
        // Fresh windup after reload prevents instant strikes against relogging players.
        attackCooldown=0;
        warningTicks=0;
    }
}
