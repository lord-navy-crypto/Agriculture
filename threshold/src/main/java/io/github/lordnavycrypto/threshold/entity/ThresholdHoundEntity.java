package io.github.lordnavycrypto.threshold.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import io.github.lordnavycrypto.threshold.item.FlashlightItem;
import java.util.EnumSet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * A first-pass Backrooms Hound for Level 1. It uses ordinary pathfinding and
 * melee behavior, cannot break blocks, and is never permitted in Level 0.
 */
public final class ThresholdHoundEntity extends Monster implements GeoEntity {
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("animation.threshold_hound.idle");
    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("animation.threshold_hound.walk");
    private static final RawAnimation RUN =
            RawAnimation.begin().thenLoop("animation.threshold_hound.run");
    private static final RawAnimation SNIFF =
            RawAnimation.begin().thenLoop("animation.threshold_hound.sniff");
    private static final RawAnimation ALERT =
            RawAnimation.begin().thenLoop("animation.threshold_hound.alert");
    private static final RawAnimation STALK =
            RawAnimation.begin().thenLoop("animation.threshold_hound.stalk");
    private static final RawAnimation RETREAT =
            RawAnimation.begin().thenLoop("animation.threshold_hound.retreat");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int lightRepelledTicks;
    private Vec3 lastKnownPlayerPosition;
    private int searchTicks;
    private int searchStepTicks;

    public ThresholdHoundEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.285D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 26.0D)
                .add(Attributes.ARMOR, 1.0D);
    }

    @Override
    public void tick() {
        if (getTarget() instanceof Player target && hasLineOfSight(target)) {
            // Only refresh memory while the player is actually visible.
            // The target goal may retain a target briefly after LOS is broken;
            // during that interval the Hound must not track through walls.
            lastKnownPlayerPosition = target.position();
            searchTicks = 120;
        }
        super.tick();
        if (level().isClientSide) return;

        // The Level 1 Hound avoids an actively used flashlight. It retreats
        // briefly rather than being defeated or deleted, preserving tension.
        if (lightRepelledTicks > 0) {
            lightRepelledTicks--;
            if (getTarget() != null) setTarget(null);
            lastKnownPlayerPosition = null;
            searchTicks = 0;
            return;
        }

        if (searchTicks > 0 && getTarget() == null) {
            searchTicks--;
            if (searchTicks == 0) lastKnownPlayerPosition = null;
        }

        Player illuminated = level().getNearestPlayer(this, 12.0D);
        if (illuminated == null || !FlashlightItem.isActive(illuminated)
                || distanceToSqr(illuminated) > 144.0D) return;

        Vec3 away = position().subtract(illuminated.position());
        if (away.lengthSqr() < 1.0E-4D) away = new Vec3(0.0D, 0.0D, 1.0D);
        away = away.normalize();
        setTarget(null);
        lastKnownPlayerPosition = null;
        searchTicks = 0;
        lightRepelledTicks = 60;
        getNavigation().moveTo(
                getX() + away.x * 8.0D,
                getY(),
                getZ() + away.z * 8.0D,
                1.1D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.12D, true));
        goalSelector.addGoal(3, new InvestigateLastKnownPositionGoal(this));
        goalSelector.addGoal(4, new StalkPlayerGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.78D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                Player candidate = level().getNearestPlayer(ThresholdHoundEntity.this, 26.0D);
                if (candidate == null || candidate.isSpectator() || candidate.isCreative()
                        || FlashlightItem.isActive(candidate)) return false;

                // Sprinting, swimming and fast movement give the Hound a reason
                // to abandon stalking and begin a direct pursuit.
                boolean noisy = candidate.isSprinting() || candidate.isSwimming()
                        || candidate.isFallFlying()
                        || candidate.getDeltaMovement().horizontalDistanceSqr() > 0.025D;
                return (noisy || ThresholdHoundEntity.this.distanceToSqr(candidate) <= 36.0D) && super.canUse();
            }
        });
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOLF_WHINE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    /**
     * If a target breaks line of sight, the Hound searches the last known
     * position for a short interval instead of instantly returning to idle.
     * A lit flashlight cancels this memory so retreat remains authoritative.
     */
    private static final class InvestigateLastKnownPositionGoal extends Goal {
        private final ThresholdHoundEntity hound;

        private InvestigateLastKnownPositionGoal(ThresholdHoundEntity hound) {
            this.hound = hound;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return hound.getTarget() == null
                    && hound.searchTicks > 0
                    && hound.lastKnownPlayerPosition != null
                    && hound.lightRepelledTicks == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            hound.searchStepTicks = 30;
            moveTowardLastKnownPosition();
        }

        @Override
        public void tick() {
            Vec3 lastKnown = hound.lastKnownPlayerPosition;
            if (lastKnown == null) return;

            hound.getLookControl().setLookAt(lastKnown.x, lastKnown.y, lastKnown.z, 12.0F, 12.0F);
            if (hound.searchStepTicks > 0) hound.searchStepTicks--;
            if (hound.getNavigation().isDone() && hound.searchStepTicks == 0) {
                hound.searchStepTicks = 18 + hound.level().getRandom().nextInt(18);
                double angle = hound.level().getRandom().nextDouble() * (Math.PI * 2.0D);
                double radius = 1.5D + hound.level().getRandom().nextDouble() * 4.0D;
                hound.getNavigation().moveTo(
                        lastKnown.x + Math.cos(angle) * radius,
                        lastKnown.y,
                        lastKnown.z + Math.sin(angle) * radius,
                        0.74D);
            }
        }

        private void moveTowardLastKnownPosition() {
            Vec3 lastKnown = hound.lastKnownPlayerPosition;
            if (lastKnown != null) {
                hound.getNavigation().moveTo(lastKnown.x, lastKnown.y, lastKnown.z, 0.82D);
            }
        }

        @Override
        public void stop() {
            hound.getNavigation().stop();
            hound.searchStepTicks = 0;
        }
    }

    /**
     * Quiet players are stalked at walking pace before the Hound commits to an
     * attack. Sprinting or getting too close hands control to the target goal.
     */
    private static final class StalkPlayerGoal extends Goal {
        private final ThresholdHoundEntity hound;
        private Player player;

        private StalkPlayerGoal(ThresholdHoundEntity hound) {
            this.hound = hound;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (hound.getTarget() != null) return false;
            Player candidate = hound.level().getNearestPlayer(hound, 24.0D);
            if (candidate == null || !candidate.isAlive() || candidate.isSpectator()
                    || candidate.isCreative() || FlashlightItem.isActive(candidate)) return false;
            if (candidate.isSprinting() || candidate.isSwimming() || candidate.isFallFlying()) return false;
            if (hound.distanceToSqr(candidate) <= 36.0D) return false;
            player = candidate;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return player != null && player.isAlive() && !player.isSpectator()
                    && !player.isCreative() && !FlashlightItem.isActive(player)
                    && hound.getTarget() == null && !player.isSprinting()
                    && !player.isSwimming() && !player.isFallFlying()
                    && hound.distanceToSqr(player) > 36.0D
                    && hound.distanceToSqr(player) < 24.0D * 24.0D;
        }

        @Override
        public void start() {
            hound.getNavigation().moveTo(player, 0.68D);
        }

        @Override
        public void tick() {
            if (player == null) return;
            hound.getLookControl().setLookAt(player, 12.0F, 12.0F);
            if (hound.getNavigation().isDone()) {
                hound.getNavigation().moveTo(player, 0.68D);
            }
        }

        @Override
        public void stop() {
            player = null;
            hound.getNavigation().stop();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, state -> {
            if (getTarget() != null) {
                return state.isMoving() ? state.setAndContinue(RUN) : state.setAndContinue(ALERT);
            }
            if (!state.isMoving()) {
                int idlePhase = Math.floorMod(tickCount + getId() * 19, 180);
                return idlePhase < 128 ? state.setAndContinue(IDLE) : state.setAndContinue(SNIFF);
            }

            // Low horizontal speed is the crouched stalking gait; a sharp burst
            // without a target is the flashlight retreat rather than a normal walk.
            double horizontalSpeed = getDeltaMovement().horizontalDistanceSqr();
            if (horizontalSpeed > 0.06D) return state.setAndContinue(RETREAT);
            if (horizontalSpeed < 0.045D) return state.setAndContinue(STALK);
            return state.setAndContinue(WALK);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
