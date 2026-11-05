package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.item.FlashlightItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.UUID;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Level 0's Watcher is a bounded, locally coordinated predator.
 *
 * It does not have omniscient tracking: a position is recorded only while
 * the player is actually visible, and nearby Watchers may relay that immutable
 * last-known snapshot with its original timestamp. Confidence decays with age.
 * Its four observation styles use different stalking and flanking patterns.
 *
 * Jump scares are deliberately brief and limited: a fast approach can either
 * pass the player or deliver one low-damage swipe, then the Watcher withdraws.
 * A held flashlight aimed at the entity interrupts the encounter.
 */
public final class ThresholdWatcherEntity extends PathfinderMob implements GeoEntity {
    private enum EncounterState {
        OBSERVE, OBSERVED, STALK, SEARCH, RUSH, STRIKE, RETREAT
    }

    private enum ObservationStyle {
        PERIPHERAL, FLANKER, MIRROR, LISTENER
    }

    /**
     * A truthful observation snapshot. Relays preserve observedAt rather than
     * refreshing the timestamp, so confidence cannot be renewed by forwarding.
     */
    private record ObservationSnapshot(
            UUID playerId, long observedAt, Vec3 position, Vec3 velocity) {
        double confidence(long now) {
            long age = now - observedAt;
            if (age < 0L || age >= MEMORY_TICKS) return 0.0D;
            return 1.0D - age / (double) MEMORY_TICKS;
        }
    }

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
    private static final RawAnimation LUNGE =
            RawAnimation.begin().thenLoop("animation.threshold_watcher.lunge");
    private static final RawAnimation STRIKE =
            RawAnimation.begin().thenPlay("animation.threshold_watcher.strike");

    private static final int NATURAL_LIFETIME_TICKS = 600;
    private static final int SHOWCASE_LIFETIME_TICKS = 600;
    private static final int TEST_ENCOUNTER_LIFETIME_TICKS = 1200;
    private static final int PERCEPTION_INTERVAL_TICKS = 4;
    private static final int MEMORY_TICKS = 120;
    private static final int RUSH_COOLDOWN_TICKS = 240;
    private static final int RUSH_MAX_TICKS = 32;
    private static final double MAX_PLAYER_DISTANCE = 36.0D;
    private static final double PEER_RELAY_RADIUS = 24.0D;
    private static final double DIRECT_LOOK_DOT = 0.86D;
    private static final double FLASHLIGHT_LOOK_DOT = 0.58D;
    private static final double STRIKE_RANGE_SQR = 3.5D;
    private static final double NEAR_MISS_RANGE_SQR = 6.25D;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ObservationStyle observationStyle;
    private EncounterState encounterState = EncounterState.OBSERVE;
    private ObservationSnapshot observation;
    private Vec3 retreatDestination;
    private int stateTicks;
    private int stareTicks;
    private int playerNoiseTicks;
    private int rushCooldown;
    private int rushTicks;
    private int flickerTicks;
    private int nextFlickerTick;
    private boolean nearMiss;
    private boolean attackConsumed;
    private boolean flankerRole;
    private boolean showcaseMode;
    private boolean testEncounterMode;

    public ThresholdWatcherEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.observationStyle = ObservationStyle.values()[
                Math.floorMod(getUUID().hashCode(), ObservationStyle.values().length)];
    }

    /** A harmless model/animation preview for artists and operators. */
    public void enableShowcaseMode() {
        showcaseMode = true;
        setNoAi(true);
        setNoGravity(true);
        setInvulnerable(true);
        setSilent(true);
        noPhysics = true;
    }

    /** An extended active encounter used by the operator test command. */
    public void enableTestEncounterMode() {
        testEncounterMode = true;
        encounterState = EncounterState.STALK;
        stateTicks = 0;
        rushCooldown = 20;
    }

    /** The second member of a local pair takes the flanking role. */
    public void setFlankerRole(boolean value) {
        flankerRole = value;
    }

    public boolean isRushing() {
        return encounterState == EncounterState.RUSH
                || encounterState == EncounterState.STRIKE;
    }

    public String getObservationStyleName() {
        return flankerRole ? ObservationStyle.FLANKER.name() : observationStyle.name();
    }

    public String getEncounterStateName() {
        return encounterState.name();
    }

    public double getObservationConfidence() {
        return observation == null ? 0.0D : observation.confidence(level().getGameTime());
    }

    public long getObservationAgeTicks() {
        return observation == null ? -1L : Math.max(0L, level().getGameTime() - observation.observedAt());
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) return;

        int lifetimeLimit = showcaseMode ? SHOWCASE_LIFETIME_TICKS
                : testEncounterMode ? TEST_ENCOUNTER_LIFETIME_TICKS
                : NATURAL_LIFETIME_TICKS;
        if (tickCount > lifetimeLimit) {
            discard();
            return;
        }

        if (rushCooldown > 0 && tickCount % PERCEPTION_INTERVAL_TICKS == 0) {
            rushCooldown = Math.max(0, rushCooldown - PERCEPTION_INTERVAL_TICKS);
        }
        updateFlicker();

        // Perception, peer relays and replanning are bounded to five updates/sec.
        if (tickCount % PERCEPTION_INTERVAL_TICKS != 0) return;

        Player player = findNearestValidPlayer(server);
        relayLocalObservation(server, player);

        if (showcaseMode) {
            if (player != null) lookAtPlayer(player);
            return;
        }

        if (player == null) {
            updateLostTarget(server);
            return;
        }

        boolean visible = player.hasLineOfSight(this);
        if (visible) {
            observation = new ObservationSnapshot(
                    player.getUUID(), server.getGameTime(), player.position(), player.getDeltaMovement());
        } else if (observation == null
                || !observation.playerId().equals(player.getUUID())
                || observation.confidence(server.getGameTime()) <= 0.0D) {
            beginRetreat(null);
            return;
        }

        // A lit, aimed flashlight interrupts even an in-progress rush.
        if (visible && FlashlightItem.isActive(player) && isInsideFlashlightCone(player)) {
            beginRetreat(player);
            return;
        }

        if (encounterState == EncounterState.RUSH) {
            updateRush(server, player, visible);
            return;
        }
        if (encounterState == EncounterState.STRIKE) {
            updateStrike(player);
            return;
        }
        if (encounterState == EncounterState.RETREAT) {
            updateRetreat();
            return;
        }

        boolean directlyObserved = visible && isBeingWatchedBy(player);

        if (directlyObserved) {
            stareTicks += PERCEPTION_INTERVAL_TICKS;
            encounterState = EncounterState.OBSERVED;
            stateTicks = 0;
            setAggressive(false);
            getNavigation().stop();
            lookAtPlayer(player);
            // A sustained stare makes it fold away; a brief glance only freezes it.
            if (stareTicks >= 40) beginRetreat(player);
            return;
        }
        stareTicks = Math.max(0, stareTicks - PERCEPTION_INTERVAL_TICKS);

        if (!visible) {
            updateSearch(server);
            return;
        }

        if (isPlayerNoisy(player)) {
            playerNoiseTicks = 80;
        } else {
            playerNoiseTicks = Math.max(0, playerNoiseTicks - PERCEPTION_INTERVAL_TICKS);
        }

        if (encounterState == EncounterState.OBSERVE
                || encounterState == EncounterState.OBSERVED) {
            stateTicks += PERCEPTION_INTERVAL_TICKS;
            lookAtPlayer(player);
            if (stateTicks < 24) return;
            encounterState = EncounterState.STALK;
            stateTicks = 0;
        }

        updateStalk(server, player);
    }

    private Player findNearestValidPlayer(ServerLevel server) {
        Player nearest = null;
        double nearestDistance = MAX_PLAYER_DISTANCE * MAX_PLAYER_DISTANCE;
        for (Player candidate : server.players()) {
            if (!isValidTarget(candidate)) continue;
            double distance = distanceToSqr(candidate);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private static boolean isValidTarget(Player player) {
        return player != null && player.isAlive()
                && !player.isSpectator() && !player.isCreative();
    }

    private boolean isBeingWatchedBy(Player player) {
        if (player == null || !player.hasLineOfSight(this)) return false;
        Vec3 toWatcher = position().subtract(player.getEyePosition()).normalize();
        return player.getLookAngle().dot(toWatcher) > DIRECT_LOOK_DOT;
    }

    private boolean isInsideFlashlightCone(Player player) {
        Vec3 toWatcher = position().subtract(player.getEyePosition()).normalize();
        return player.getLookAngle().dot(toWatcher) > FLASHLIGHT_LOOK_DOT;
    }

    private static boolean isPlayerNoisy(Player player) {
        return player.isSprinting() || player.isSwimming() || player.isFallFlying()
                || player.getDeltaMovement().horizontalDistanceSqr() > 0.0225D
                || (!player.onGround() && Math.abs(player.getDeltaMovement().y) > 0.12D);
    }

    private void lookAtPlayer(Player player) {
        getLookControl().setLookAt(player, 18.0F, 18.0F);
        float targetYaw = (float) (Mth.atan2(
                player.getZ() - getZ(), player.getX() - getX()) * (180.0D / Math.PI)) - 90.0F;
        float yaw = Mth.rotLerp(0.16F, getYRot(), targetYaw);
        setYRot(yaw);
        setYHeadRot(yaw);
        setYBodyRot(yaw);
    }

    private void updateStalk(ServerLevel server, Player player) {
        lookAtPlayer(player);
        double distance = distanceToSqr(player);
        boolean peerAlreadyRushing = isPeerRushing(server, player);

        // A scare is possible only while the player is not looking directly at it.
        // Noise makes the Watcher more willing to commit, but never grants wall vision.
        if (!peerAlreadyRushing && rushCooldown == 0 && stareTicks == 0
                && distance <= 18.0D * 18.0D && distance >= 2.0D * 2.0D) {
            int chance = playerNoiseTicks > 0 ? 12 : 24;
            if (server.getRandom().nextInt(chance) == 0) {
                beginRush(server, player);
                return;
            }
        }

        ObservationStyle style = flankerRole ? ObservationStyle.FLANKER : observationStyle;
        if (style == ObservationStyle.LISTENER && playerNoiseTicks == 0
                && distance > 6.0D * 6.0D) {
            getNavigation().stop();
            return;
        }

        Vec3 destination = observationPoint(player, style);
        double speed = style == ObservationStyle.FLANKER ? 0.92D
                : style == ObservationStyle.LISTENER ? 0.82D : 0.76D;
        if (distance > 5.0D * 5.0D
                && (getNavigation().isDone() || tickCount % 20 == 0)) {
            getNavigation().moveTo(destination.x, destination.y, destination.z, speed);
        } else if (distance <= 5.0D * 5.0D) {
            getNavigation().stop();
        }
    }

    private Vec3 observationPoint(Player player, ObservationStyle style) {
        Vec3 forward = player.getLookAngle();
        forward = new Vec3(forward.x, 0.0D, forward.z);
        if (forward.lengthSqr() < 1.0E-4D) forward = new Vec3(0.0D, 0.0D, 1.0D);
        forward = forward.normalize();
        double sideSign = Math.floorMod(getUUID().hashCode(), 2) == 0 ? 1.0D : -1.0D;
        Vec3 side = new Vec3(-forward.z, 0.0D, forward.x).scale(sideSign);

        return switch (style) {
            case PERIPHERAL -> player.position().add(side.scale(8.0D)).subtract(forward.scale(2.5D));
            case FLANKER -> player.position().subtract(forward.scale(7.0D)).add(side.scale(4.5D));
            case MIRROR -> player.getDeltaMovement().horizontalDistanceSqr() > 0.004D
                    ? player.position().subtract(forward.scale(5.0D)).add(side.scale(5.0D))
                    : position();
            case LISTENER -> playerNoiseTicks > 0
                    ? player.position().add(side.scale(6.0D)).subtract(forward.scale(1.0D))
                    : observation != null ? observation.position() : position();
        };
    }

    private void beginRush(ServerLevel server, Player player) {
        encounterState = EncounterState.RUSH;
        stateTicks = 0;
        rushTicks = 0;
        nearMiss = server.getRandom().nextInt(4) == 0;
        attackConsumed = false;
        rushCooldown = RUSH_COOLDOWN_TICKS;
        setAggressive(true);
        setInvisible(false);
        // The swoop is the audio tell; the short charge is the visual jump scare.
        playSound(SoundEvents.PHANTOM_SWOOP, 1.0F, 0.72F);

        Vec3 destination = player.position();
        if (nearMiss) {
            Vec3 towardPlayer = player.position().subtract(position());
            towardPlayer = new Vec3(towardPlayer.x, 0.0D, towardPlayer.z);
            if (towardPlayer.lengthSqr() < 1.0E-4D) towardPlayer = new Vec3(0.0D, 0.0D, 1.0D);
            towardPlayer = towardPlayer.normalize();
            Vec3 side = new Vec3(-towardPlayer.z, 0.0D, towardPlayer.x)
                    .scale(server.getRandom().nextBoolean() ? 1.35D : -1.35D);
            destination = player.position().add(side);
        }
        getNavigation().moveTo(destination.x, destination.y, destination.z, 1.62D);
    }

    private void updateRush(ServerLevel server, Player player, boolean visible) {
        rushTicks += PERCEPTION_INTERVAL_TICKS;
        setAggressive(true);
        setInvisible(false);

        if (visible && !nearMiss && !attackConsumed
                && distanceToSqr(player) <= STRIKE_RANGE_SQR) {
            performStrike(player);
            return;
        }
        if (visible && nearMiss && distanceToSqr(player) <= NEAR_MISS_RANGE_SQR) {
            beginRetreat(player);
            return;
        }
        if (!visible && (observation == null
                || observation.confidence(server.getGameTime()) < 0.35D)) {
            beginRetreat(null);
            return;
        }
        if (rushTicks >= RUSH_MAX_TICKS) {
            beginRetreat(visible ? player : null);
            return;
        }

        if (getNavigation().isDone() || tickCount % 8 == 0) {
            Vec3 destination = visible ? player.position() : predictedSearchPosition(server.getGameTime());
            if (nearMiss && visible) {
                Vec3 towardPlayer = player.position().subtract(position());
                towardPlayer = new Vec3(towardPlayer.x, 0.0D, towardPlayer.z);
                if (towardPlayer.lengthSqr() > 1.0E-4D) {
                    towardPlayer = towardPlayer.normalize();
                    destination = player.position().add(
                            new Vec3(-towardPlayer.z, 0.0D, towardPlayer.x)
                                    .scale(1.35D));
                }
            }
            getNavigation().moveTo(destination.x, destination.y, destination.z, 1.62D);
        }
    }

    private void performStrike(Player player) {
        if (attackConsumed) return;
        attackConsumed = true;
        encounterState = EncounterState.STRIKE;
        stateTicks = 0;
        setAggressive(true);
        swing(InteractionHand.MAIN_HAND);

        // One short, low-damage hit per scare. The vanilla hurt response supplies
        // the brief red flash and camera jolt; the Watcher never chains attacks.
        if (player.hurt(damageSources().mobAttack(this), 2.0F)) {
            player.knockback(0.22D, getX() - player.getX(), getZ() - player.getZ());
        }
        playSound(SoundEvents.ENDERMAN_SCREAM, 0.88F, 0.82F);
    }

    private void updateStrike(Player player) {
        stateTicks += PERCEPTION_INTERVAL_TICKS;
        lookAtPlayer(player);
        if (stateTicks >= 8) beginRetreat(player);
    }

    private void beginRetreat(Player player) {
        if (encounterState == EncounterState.RETREAT) return;
        encounterState = EncounterState.RETREAT;
        stateTicks = 0;
        setAggressive(false);
        setInvisible(false);

        Vec3 origin = player != null ? player.position()
                : observation != null ? observation.position() : position().add(1.0D, 0.0D, 0.0D);
        Vec3 away = position().subtract(origin);
        away = new Vec3(away.x, 0.0D, away.z);
        if (away.lengthSqr() < 1.0E-4D) away = new Vec3(1.0D, 0.0D, 0.0D);
        away = away.normalize();
        retreatDestination = position().add(away.scale(10.0D));
        getNavigation().moveTo(
                retreatDestination.x, retreatDestination.y, retreatDestination.z, 1.28D);
    }

    private void updateRetreat() {
        stateTicks += PERCEPTION_INTERVAL_TICKS;
        if (retreatDestination != null && getNavigation().isDone() && stateTicks < 24) {
            getNavigation().moveTo(
                    retreatDestination.x, retreatDestination.y, retreatDestination.z, 1.18D);
        }
        if (stateTicks >= 24) setInvisible(true);
        if (stateTicks >= 40) discard();
    }

    private void updateLostTarget(ServerLevel server) {
        if (showcaseMode) return;
        if (observation == null || observation.confidence(server.getGameTime()) <= 0.0D) {
            discard();
            return;
        }
        updateSearch(server);
    }

    private void updateSearch(ServerLevel server) {
        if (observation == null) {
            beginRetreat(null);
            return;
        }
        double confidence = observation.confidence(server.getGameTime());
        if (confidence <= 0.12D || stateTicks > 60) {
            beginRetreat(null);
            return;
        }

        encounterState = EncounterState.SEARCH;
        stateTicks += PERCEPTION_INTERVAL_TICKS;
        Vec3 searchPoint = predictedSearchPosition(server.getGameTime());
        getLookControl().setLookAt(searchPoint.x, searchPoint.y, searchPoint.z, 12.0F, 12.0F);
        if (getNavigation().isDone() || tickCount % 20 == 0) {
            getNavigation().moveTo(searchPoint.x, searchPoint.y, searchPoint.z, 0.78D);
        }
        if (distanceToSqr(searchPoint) < 4.0D && stateTicks > 24) {
            beginRetreat(null);
        }
    }

    /**
     * Uses only a short, bounded extrapolation of the last observed velocity.
     * It is cleared naturally when confidence reaches zero and never reads a
     * hidden live player position.
     */
    private Vec3 predictedSearchPosition(long now) {
        if (observation == null) return position();
        long age = Math.max(0L, now - observation.observedAt());
        double predictionTicks = Math.min(age, 6L);
        Vec3 velocity = observation.velocity();
        Vec3 offset = new Vec3(
                velocity.x * predictionTicks, 0.0D, velocity.z * predictionTicks);
        if (offset.length() > 2.5D) offset = offset.normalize().scale(2.5D);
        return observation.position().add(offset);
    }

    /** Relay a fresh observation only to a nearby peer tracking the same player. */
    private void relayLocalObservation(ServerLevel server, Player candidate) {
        if (tickCount % 20 != 0) return;
        long now = server.getGameTime();
        for (ThresholdWatcherEntity peer : server.getEntitiesOfClass(
                ThresholdWatcherEntity.class, getBoundingBox().inflate(PEER_RELAY_RADIUS))) {
            if (peer == this || peer.observation == null) continue;
            if (candidate != null && !peer.observation.playerId().equals(candidate.getUUID())) continue;
            if (observation != null
                    && !peer.observation.playerId().equals(observation.playerId())) continue;
            if (peer.observation.confidence(now) < 0.25D) continue;
            if (observation == null || peer.observation.observedAt() > observation.observedAt()) {
                // Keep the source timestamp: a relay is not a fresh observation.
                observation = peer.observation;
            }
        }
    }

    private boolean isPeerRushing(ServerLevel server, Player player) {
        for (ThresholdWatcherEntity peer : server.getEntitiesOfClass(
                ThresholdWatcherEntity.class, getBoundingBox().inflate(PEER_RELAY_RADIUS))) {
            if (peer == this || !peer.isRushing() || peer.observation == null) continue;
            if (peer.observation.playerId().equals(player.getUUID())) return true;
        }
        return false;
    }

    private void updateFlicker() {
        if (encounterState == EncounterState.RUSH
                || encounterState == EncounterState.STRIKE
                || encounterState == EncounterState.RETREAT) {
            if (encounterState != EncounterState.RETREAT) setInvisible(false);
            return;
        }
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
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "presence", 3, state -> {
            if (isAggressive()) {
                if (getAttackAnim(0.0F) > 0.12F) return state.setAndContinue(STRIKE);
                return state.setAndContinue(LUNGE);
            }
            if (state.isMoving()) return state.setAndContinue(SHROUD_DRIFT);

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
