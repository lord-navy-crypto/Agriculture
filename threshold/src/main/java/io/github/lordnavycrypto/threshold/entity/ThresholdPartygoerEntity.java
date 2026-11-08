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
 * A predator disguised as a cheerful party mascot. Its natural encounter
 * remains in the Overworld, while operator test summons may place it in any
 * dimension. It shadows quiet players, commits to a fast pursuit when noticed
 * or provoked, and uses ordinary melee combat. It never teleports or captures
 * the player.
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
    private Vec3 lastVisiblePlayerPosition;
    private int sightMemoryTicks;
    private int lostSightTicks;

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
        goalSelector.addGoal(3, new InvestigateLastSightingGoal(this));
        goalSelector.addGoal(4, new StalkPlayerGoal(this));
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
            if (hasLineOfSight(player)) {
                lastVisiblePlayerPosition = player.position();
                sightMemoryTicks = 100;
                lostSightTicks = 0;
            } else if (++lostSightTicks > 30) {
                // Return to the last seen location rather than pursuing an
                // unseen player's live coordinates through several walls.
                setTarget(null);
                lostSightTicks = 0;
            }
        } else {
            lostSightTicks = 0;
        }
        if (getTarget() == null && sightMemoryTicks > 0) {
            sightMemoryTicks--;
            if (sightMemoryTicks == 0) lastVisiblePlayerPosition = null;
        }

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
     * Investigate a true prior sighting for a bounded time, then give up.
     * This takes precedence over random strolling but yields to a real attack.
     */
    private static final class InvestigateLastSightingGoal extends Goal {
        private final ThresholdPartygoerEntity partygoer;
        private int repathTicks;

        private InvestigateLastSightingGoal(ThresholdPartygoerEntity partygoer) {
            this.partygoer = partygoer;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return partygoer.getTarget() == null
                    && partygoer.lastVisiblePlayerPosition != null
                    && partygoer.sightMemoryTicks > 0;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            repathTicks = 0;
        }

        @Override
        public void tick() {
            Vec3 sighting = partygoer.lastVisiblePlayerPosition;
            if (sighting == null) return;
            partygoer.getLookControl().setLookAt(
                    sighting.x, sighting.y, sighting.z, 14.0F, 14.0F);
            if (--repathTicks <= 0) {
                repathTicks = 20;
                partygoer.getNavigation().moveTo(
                        sighting.x, sighting.y, sighting.z, 0.96D);
            }
            // Once the search reaches the remembered spot, remaining memory
            // fades instead of extending indefinitely or acquiring wall vision.
            if (partygoer.distanceToSqr(sighting) < 4.0D) {
                partygoer.sightMemoryTicks = Math.min(
                        partygoer.sightMemoryTicks, 18);
            }
        }

        @Override
        public void stop() {
            partygoer.getNavigation().stop();
        }
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
                    || !partygoer.hasLineOfSight(candidate)
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
                    && partygoer.hasLineOfSight(observedPlayer)
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
            if (partygoer.hasLineOfSight(observedPlayer)) {
                partygoer.lastVisiblePlayerPosition = observedPlayer.position();
                partygoer.sightMemoryTicks = 100;
            }
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
