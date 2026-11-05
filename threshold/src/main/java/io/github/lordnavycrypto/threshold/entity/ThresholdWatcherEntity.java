package io.github.lordnavycrypto.threshold.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
    private static final RawAnimation HEAD_TRACK =
            RawAnimation.begin().thenLoop("animation.threshold_watcher.head_track");
    private static final RawAnimation STUTTER =
            RawAnimation.begin().thenLoop("animation.threshold_watcher.stutter");
    private static final RawAnimation FOLD =
            RawAnimation.begin().thenLoop("animation.threshold_watcher.fold");
    private static final RawAnimation SHROUD_DRIFT =
            RawAnimation.begin().thenLoop("animation.threshold_watcher.shroud_drift");
    private static final int MAX_LIFETIME_TICKS = 180;
    private static final int PERCEPTION_INTERVAL_TICKS = 2;
    private static final int DIRECT_NOTICE_GRACE_CHECKS = 3;
    private static final int SHOWCASE_LIFETIME_TICKS = 600;
    private static final double MAX_PLAYER_DISTANCE = 32.0D;
    private static final double MIN_PLAYER_DISTANCE_SQR = 72.25D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int flickerTicks;
    private int nextFlickerTick;
    private int directlyObservedTicks;
    private boolean showcaseMode;

    /** Keeps an operator-spawned preview visible long enough to verify its model and animation. */
    public void enableShowcaseMode() {
        this.showcaseMode = true;
    }

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

        // A hard lifetime bound prevents stale apparitions from accumulating.
        if (tickCount > (showcaseMode ? SHOWCASE_LIFETIME_TICKS : MAX_LIFETIME_TICKS)) {
            discard();
            return;
        }

        updateFlicker();

        // Perception and line-of-sight checks run at 10 Hz, not every server tick.
        // The 50 ms response window is imperceptible for a distant, non-hostile apparition.
        if (tickCount % PERCEPTION_INTERVAL_TICKS != 0) return;

        Player nearest = level().getNearestPlayer(this, MAX_PLAYER_DISTANCE);
        if (nearest == null) {
            if (!showcaseMode) discard();
            return;
        }
        if (!showcaseMode && distanceToSqr(nearest) < MIN_PLAYER_DISTANCE_SQR) {
            discard();
            return;
        }

        // Slowly turn toward the player without walking or becoming hostile.
        float targetYaw = (float) (Mth.atan2(
                nearest.getZ() - getZ(), nearest.getX() - getX()) * (180.0D / Math.PI)) - 90.0F;
        float trackedYaw = Mth.rotLerp(0.08F, getYRot(), targetYaw);
        setYRot(trackedYaw);
        setYHeadRot(trackedYaw);
        setYBodyRot(trackedYaw);

        Vec3 toWatcher = position().subtract(nearest.getEyePosition()).normalize();
        if (showcaseMode) return;

        // Do not vanish on the very first frame the player looks at it. Give
        // the player a clear 2-second sighting, then let it fold out of view.
        boolean directlyNoticed = nearest.hasLineOfSight(this)
                && nearest.getLookAngle().dot(toWatcher) > 0.965D;
        if (directlyNoticed) {
            directlyObservedTicks++;
            if (directlyObservedTicks >= DIRECT_NOTICE_GRACE_CHECKS) discard();
        } else {
            directlyObservedTicks = 0;
        }
    }

    private void updateFlicker() {
        if (nextFlickerTick == 0) {
            nextFlickerTick = tickCount + 45 + level().getRandom().nextInt(55);
        }
        if (flickerTicks > 0) {
            flickerTicks--;
            if (flickerTicks == 0) setInvisible(false);
        } else if (tickCount >= nextFlickerTick) {
            setInvisible(true);
            flickerTicks = 1;
            nextFlickerTick = tickCount + 55 + level().getRandom().nextInt(85);
        }
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
        controllers.add(new AnimationController<>(this, "presence", 5, state -> {
            // Long quiet intervals are interrupted by brief, non-combat gestures.
            // The phase is deterministic per entity so client and server remain visually aligned.
            int phase = Math.floorMod(tickCount + getId() * 31, 420);
            if (phase < 190) return state.setAndContinue(STILL);
            if (phase < 260) return state.setAndContinue(HEAD_TRACK);
            if (phase < 278) return state.setAndContinue(STUTTER);
            if (phase < 320) return state.setAndContinue(FOLD);
            return state.setAndContinue(SHROUD_DRIFT);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
