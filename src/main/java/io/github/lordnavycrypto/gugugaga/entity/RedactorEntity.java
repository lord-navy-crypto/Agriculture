package io.github.lordnavycrypto.gugugaga.entity;

import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy.RedactorAction;
import io.github.lordnavycrypto.gugugaga.ai.ThreatPolicy.Mood;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/**
 * ONE Redactor entity type, TWO reversible render forms: Lapis and revealed.
 * Targets Gugu-Gaga, NOT players. Flees Yellow Smiler above all priorities.
 * It cannot attack, grief, load chunks or modify server permissions.
 */
public final class RedactorEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> REVEALED =
            SynchedEntityData.defineId(RedactorEntity.class,EntityDataSerializers.BOOLEAN);
    private int corruptionCooldown=ThreeEntityReactionPolicy.CORRUPT_INTERVAL_TICKS;
    private int whisperCooldown;
    private RedactorAction action=RedactorAction.IDLE_AS_LAPIS;

    public RedactorEntity(EntityType<? extends PathfinderMob> type,Level level) {
        super(type,level);
        setPersistenceRequired(); // manually summoned investigation prototype
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

    public boolean revealed() {
        return getEntityData().get(REVEALED);
    }

    public void setRevealed(boolean exposed) {
        getEntityData().set(REVEALED,exposed);
    }

    private static <T extends net.minecraft.world.entity.Entity> T closest(
            RedactorEntity observer,Class<T> type,double radius) {
        return observer.level().getEntitiesOfClass(type,
                        observer.getBoundingBox().inflate(radius),
                        net.minecraft.world.entity.Entity::isAlive)
                .stream().min(Comparator.comparingDouble(observer::distanceToSqr))
                .orElse(null);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide)return;
        if(tickCount%20!=0 || !(level() instanceof ServerLevel world))return;
        corruptionCooldown=Math.min(ThreeEntityReactionPolicy.CORRUPT_INTERVAL_TICKS,
                corruptionCooldown+20);
        whisperCooldown=Math.max(0,whisperCooldown-20);
        YellowSmilerEntity smiler=closest(this,YellowSmilerEntity.class,24);
        GugugagaEntity bird=closest(this,GugugagaEntity.class,24);
        double smilerSq=smiler==null?Double.POSITIVE_INFINITY:distanceToSqr(smiler);
        double guguSq=bird==null?Double.POSITIVE_INFINITY:distanceToSqr(bird);
        action=ThreeEntityReactionPolicy.redactor(smilerSq,guguSq);
        switch(action) {
            case FLEE_SMILER -> {
                // The Redactor's one overriding fear; cannot corrupt while
                // any living Smiler is within 24 blocks.
                if(smiler!=null) {
                    Vec3 away=position().subtract(smiler.position());
                    if(away.lengthSqr()<0.01)away=new Vec3(1,0,0);
                    Vec3 exit=position().add(away.normalize().scale(10));
                    if(tickCount%40==0)
                        getNavigation().moveTo(exit.x,exit.y,exit.z,1.0);
                    getLookControl().setLookAt(smiler,10f,10f);
                }
            }
            case REVEAL_AND_APPROACH -> {
                if(!revealed())setRevealed(true);
                if(bird!=null) {
                    getLookControl().setLookAt(bird,12f,12f);
                    if(guguSq>ThreeEntityReactionPolicy.CORRUPT_RADIUS_SQ
                            && tickCount%40==0)getNavigation().moveTo(bird,0.68);
                    else getNavigation().stop();
                    if(ThreeEntityReactionPolicy.mayCorrupt(
                            false,revealed(),guguSq,corruptionCooldown)) {
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
                // After the guardian moves away, the mask can return.
                if(revealed() && tickCount%200==0)setRevealed(false);
                if(!revealed() && whisperCooldown==0) {
                    for(ServerPlayer player:world.getEntitiesOfClass(
                            ServerPlayer.class,getBoundingBox().inflate(9.0),
                            p->p.isAlive()&&!p.isCreative()&&!p.isSpectator()
                                && !NightwatchDirector.isQuiet(
                                    world.getServer(),p.getUUID()))) {
                        player.sendSystemMessage(Component.literal("[Lapis] Lapis..."));
                        whisperCooldown=20*35;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("RedactorRevealed",revealed());
        tag.putInt("RedactorCorruptCooldown",corruptionCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setRevealed(tag.getBoolean("RedactorRevealed"));
        corruptionCooldown=Math.max(0,Math.min(
                ThreeEntityReactionPolicy.CORRUPT_INTERVAL_TICKS,
                tag.getInt("RedactorCorruptCooldown")));
    }
}
