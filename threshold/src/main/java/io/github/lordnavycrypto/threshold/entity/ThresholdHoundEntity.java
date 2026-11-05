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
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int lightRepelledTicks;

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
        super.tick();
        if (level().isClientSide) return;

        // The Level 1 Hound avoids an actively used flashlight. It retreats
        // briefly rather than being defeated or deleted, preserving tension.
        if (lightRepelledTicks > 0) {
            lightRepelledTicks--;
            if (getTarget() != null) setTarget(null);
            return;
        }

        Player illuminated = level().getNearestPlayer(this, 12.0D);
        if (illuminated == null || !FlashlightItem.isActive(illuminated)
                || distanceToSqr(illuminated) > 144.0D) return;

        Vec3 away = position().subtract(illuminated.position());
        if (away.lengthSqr() < 1.0E-4D) away = new Vec3(0.0D, 0.0D, 1.0D);
        away = away.normalize();
        setTarget(null);
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
        controllers.add(new AnimationController<>(this, "movement", 4, state ->
                !state.isMoving() ? state.setAndContinue(IDLE)
                        : getTarget() != null ? state.setAndContinue(RUN) : state.setAndContinue(WALK)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
