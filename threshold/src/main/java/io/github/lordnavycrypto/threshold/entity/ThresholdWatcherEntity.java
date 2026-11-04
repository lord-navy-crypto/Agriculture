package io.github.lordnavycrypto.threshold.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Level 0's observer is an environmental apparition, not a combatant.
 * It has no AI, collision, damage, drops, or persistent save data. It vanishes
 * when approached or directly noticed, preserving Level 0's uncertain presence.
 */
public final class ThresholdWatcherEntity extends PathfinderMob implements GeoEntity {
    private static final RawAnimation STILL =
            RawAnimation.begin().thenLoop("animation.threshold_watcher.still");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ThresholdWatcherEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        setNoAi(true);
        setNoGravity(true);
        setInvulnerable(true);
        setSilent(true);
        noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel)) return;

        // Reassert the non-interactive presentation contract after every tick.
        noPhysics = true;
        setNoGravity(true);
        setNoAi(true);

        Player nearest = level().getNearestPlayer(this, 32.0D);
        if (nearest == null || tickCount > 110 || distanceToSqr(nearest) < 49.0D) {
            discard();
            return;
        }

        Vec3 toWatcher = position().subtract(nearest.getEyePosition()).normalize();
        boolean directlyNoticed = nearest.hasLineOfSight(this)
                && nearest.getLookAngle().dot(toWatcher) > 0.985D;
        if (directlyNoticed) discard();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "presence", 5,
                state -> state.setAndContinue(STILL)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
