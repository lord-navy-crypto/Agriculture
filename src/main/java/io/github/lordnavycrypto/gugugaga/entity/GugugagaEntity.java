package io.github.lordnavycrypto.gugugaga.entity;

import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain;
import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain.Phase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GugugagaEntity extends PathfinderMob {
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(GugugagaEntity.class, EntityDataSerializers.INT);
    private int phaseTicks;

    public GugugagaEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
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
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.55));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public Phase phase() {
        int value = getEntityData().get(PHASE);
        return value >= 0 && value < Phase.values().length
                ? Phase.values()[value] : Phase.DORMANT;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || tickCount % 10 != 0) return;
        Player player = level().getNearestPlayer(this, 32.0);
        if (player == null || player.isSpectator() || player.isCreative()) {
            changePhase(Phase.DORMANT);
            getNavigation().stop();
            return;
        }

        boolean watched = player.hasLineOfSight(this) && isLookingAt(player);
        Phase next = ObserverBrain.next(phase(), watched, distanceToSqr(player), phaseTicks);
        if (next != phase()) changePhase(next);
        else phaseTicks += 10;

        switch (phase()) {
            case DORMANT -> getNavigation().stop();
            case OBSERVE -> {
                getNavigation().stop();
                getLookControl().setLookAt(player, 15f, 15f);
            }
            case APPROACH -> {
                if (!watched) getNavigation().moveTo(player, 0.70);
                else getNavigation().stop();
                getLookControl().setLookAt(player, 15f, 15f);
            }
            case RETREAT -> {
                Vec3 away = position().subtract(player.position());
                if (away.lengthSqr() < 0.01) away = new Vec3(1, 0, 0);
                Vec3 target = position().add(away.normalize().scale(7.0));
                getNavigation().moveTo(target.x, target.y, target.z, 0.85);
            }
        }
    }

    private boolean isLookingAt(Player player) {
        Vec3 toObserver = getEyePosition().subtract(player.getEyePosition());
        return toObserver.lengthSqr() > 0.01
                && player.getLookAngle().dot(toObserver.normalize()) > 0.90;
    }

    private void changePhase(Phase newPhase) {
        getEntityData().set(PHASE, newPhase.ordinal());
        phaseTicks = 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        // Vanilla placeholder; no third-party voice or meme audio redistributed.
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ObserverPhase", phase().ordinal());
        tag.putInt("ObserverPhaseTicks", phaseTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        int saved = tag.getInt("ObserverPhase");
        getEntityData().set(PHASE, (saved < 0 || saved >= Phase.values().length)
                ? Phase.DORMANT.ordinal() : saved);
        phaseTicks = Math.max(0, Math.min(1200, tag.getInt("ObserverPhaseTicks")));
    }
}
