package io.github.lordnavycrypto.threshold.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * An Overworld predator disguised as a cheerful party mascot.
 * It shadows quiet players, commits to a fast pursuit when noticed or provoked,
 * and uses ordinary melee combat. It never teleports or captures the player.
 */
public final class ThresholdPartygoerEntity extends Monster implements GeoEntity {
    private static final double ACQUIRE_RANGE = 48.0D;
    private static final double COMMIT_RANGE_SQR = 100.0D;
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("animation.threshold_partygoer.idle");
    private static final RawAnimation STALK =
            RawAnimation.begin().thenLoop("animation.threshold_partygoer.stalk");
    private static final RawAnimation RUN =
            RawAnimation.begin().thenLoop("animation.threshold_partygoer.run");
    private static final RawAnimation ALERT =
            RawAnimation.begin().thenLoop("animation.threshold_partygoer.alert");
    private static final RawAnimation ATTACK =
            RawAnimation.begin().thenPlay("animation.threshold_partygoer.attack");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Player announcedTarget;

    public ThresholdPartygoerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setPersistenceRequired();
        xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.7D)
                .add(Attributes.FOLLOW_RANGE, ACQUIRE_RANGE)
                .add(Attributes.ARMOR, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.28D, true));
        goalSelector.addGoal(3, new StalkPlayerGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.58D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 24.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                Player candidate = level().getNearestPlayer(ThresholdPartygoerEntity.this, ACQUIRE_RANGE);
                if (!isValidTarget(candidate)) return false;

                boolean noisy = candidate.isSprinting() || candidate.isSwimming()
                        || candidate.isFallFlying()
                        || candidate.getDeltaMovement().horizontalDistanceSqr() > 0.018D;
                boolean staring = isBeingWatchedBy(candidate);
                boolean close = ThresholdPartygoerEntity.this.distanceToSqr(candidate)
                        <= COMMIT_RANGE_SQR;
                return (noisy || staring || close) && super.canUse();
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        if (getTarget() instanceof Player player && isValidTarget(player)) {
            setAggressive(true);
            if (player != announcedTarget) {
                announcedTarget = player;
                // A sudden, low-pitched sting announces the moment the disguise drops.
                playSound(SoundEvents.ENDERMAN_SCREAM, 1.15F, 0.62F);
            }
        } else {
            setAggressive(false);
            announcedTarget = null;
        }
    }

    private static boolean isValidTarget(Player player) {
        return player != null && player.isAlive()
                && !player.isSpectator() && !player.isCreative();
    }

    private boolean isBeingWatchedBy(Player player) {
        if (player == null || !player.hasLineOfSight(this)) return false;
        Vec3 toPartygoer = position().subtract(player.getEyePosition()).normalize();
        return player.getLookAngle().dot(toPartygoer) > 0.76D;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 3, state -> {
            if (isAggressive()) {
                if (getAttackAnim(0.0F) > 0.12F) return state.setAndContinue(ATTACK);
                return state.isMoving()
                        ? state.setAndContinue(RUN)
                        : state.setAndContinue(ALERT);
            }
            return state.isMoving()
                    ? state.setAndContinue(STALK)
                    : state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /**
     * Quiet players are shadowed at a measured pace. Looking directly at the
     * Partygoer, moving loudly, or getting within ten blocks hands control to
     * the target goal and turns the encounter into a committed hunt.
     */
    private static final class StalkPlayerGoal extends Goal {
        private final ThresholdPartygoerEntity partygoer;
        private Player observedPlayer;

        private StalkPlayerGoal(ThresholdPartygoerEntity partygoer) {
            this.partygoer = partygoer;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (partygoer.getTarget() != null) return false;
            Player candidate = partygoer.level().getNearestPlayer(partygoer, 32.0D);
            if (!isValidTarget(candidate) || candidate.isSprinting()
                    || candidate.isSwimming() || candidate.isFallFlying()
                    || partygoer.isBeingWatchedBy(candidate)) return false;

            double distance = partygoer.distanceToSqr(candidate);
            if (distance <= COMMIT_RANGE_SQR || distance > 32.0D * 32.0D) return false;
            observedPlayer = candidate;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return observedPlayer != null && isValidTarget(observedPlayer)
                    && partygoer.getTarget() == null
                    && !observedPlayer.isSprinting() && !observedPlayer.isSwimming()
                    && !observedPlayer.isFallFlying()
                    && !partygoer.isBeingWatchedBy(observedPlayer)
                    && partygoer.distanceToSqr(observedPlayer) > COMMIT_RANGE_SQR
                    && partygoer.distanceToSqr(observedPlayer) < 32.0D * 32.0D;
        }

        @Override
        public void start() {
            partygoer.getNavigation().moveTo(observedPlayer, 0.88D);
        }

        @Override
        public void tick() {
            if (observedPlayer == null) return;
            partygoer.getLookControl().setLookAt(observedPlayer, 12.0F, 12.0F);
            double distance = partygoer.distanceToSqr(observedPlayer);
            if (distance > 196.0D) {
                if (partygoer.getNavigation().isDone()) {
                    partygoer.getNavigation().moveTo(observedPlayer, 0.90D);
                }
            } else if (distance > COMMIT_RANGE_SQR) {
                if (partygoer.getNavigation().isDone()) {
                    partygoer.getNavigation().moveTo(observedPlayer, 0.72D);
                }
            } else {
                partygoer.getNavigation().stop();
            }
        }

        @Override
        public void stop() {
            observedPlayer = null;
            partygoer.getNavigation().stop();
        }
    }
}
