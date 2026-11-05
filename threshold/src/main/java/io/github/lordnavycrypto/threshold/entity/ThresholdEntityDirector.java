package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Owns natural encounter spawning and its explicit per-dimension profile.
 * Non-combat utility entities and operator-summoned Threshold mobs are kept
 * separate from that profile; a narrowly scoped entity-join rule is maintained
 * independently from the natural mob-spawn placement check.
 *
 * Vanilla biome spawn lists are empty in Threshold dimensions, so ordinary
 * animals and monsters do not naturally spawn there. Items, vehicles, and
 * other non-combat functional entities remain admitted independently.
 */
public final class ThresholdEntityDirector {
    private static final int LEVEL_1_ROOM_SIZE = 48;
    private static final int WATCHER_SPAWN_INTERVAL_TICKS = 300;
    private static final double WATCHER_NEARBY_RADIUS = 42.0D;
    private static final int MAX_LOCAL_WATCHERS = 2;

    private ThresholdEntityDirector() {}

    /**
     * Natural-spawn whitelist. This is intentionally not an entity-join
     * restriction: commands, spawn eggs, item drops, vehicles, and other
     * functional entities are admitted independently.
     */
    public static boolean isNaturalSpawnAllowed(
            ResourceKey<Level> dimension, EntityType<?> entityType) {
        if (dimension.equals(ThresholdDimensions.LEVEL_0)) {
            return entityType == ThresholdEntities.WATCHER.get();
        }
        if (dimension.equals(ThresholdDimensions.LEVEL_1)) {
            return entityType == ThresholdEntities.HOUND.get();
        }
        if (dimension.equals(Level.OVERWORLD)) {
            return entityType == ThresholdEntities.PARTYGOER.get();
        }
        return false;
    }

    /**
     * Returns true only for automatic natural-world mob spawn attempts in
     * Threshold dimensions that are outside the dimension's authored whitelist.
     * Commands, spawn eggs, breeding, spawners, and direct entity creation are
     * intentionally not classified as natural spawning.
     */
    public static boolean shouldBlockNaturalSpawn(
            ResourceKey<Level> dimension, EntityType<?> entityType, MobSpawnType spawnType) {
        if (spawnType != MobSpawnType.NATURAL
                && spawnType != MobSpawnType.CHUNK_GENERATION) {
            return false;
        }
        if (!isThresholdDimension(dimension)) return false;
        return !isNaturalSpawnAllowed(dimension, entityType);
    }

    private static boolean isThresholdDimension(ResourceKey<Level> dimension) {
        return dimension.equals(ThresholdDimensions.LEVEL_0)
                || dimension.equals(ThresholdDimensions.LEVEL_1)
                || dimension.equals(ThresholdDimensions.LEVEL_2)
                || dimension.equals(ThresholdDimensions.LEVEL_3)
                || dimension.equals(ThresholdDimensions.LEVEL_4)
                || dimension.equals(ThresholdDimensions.LEVEL_5);
    }

    /**
     * Keeps weapon-like projectile entities outside the Threshold encounter
     * space. This is deliberately separate from mob natural-spawn filtering;
     * ordinary item, vehicle, display, and custom mob entities are unaffected.
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (isThresholdDimension(level.dimension())
                && event.getEntity() instanceof Projectile) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (shouldBlockNaturalSpawn(
                level.dimension(), event.getEntityType(), event.getSpawnType())) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        long gameTime = level.getGameTime();

        if (gameTime % WATCHER_SPAWN_INTERVAL_TICKS == 0L
                && isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.WATCHER.get())) {
            spawnObservers(level);
        }
        if (gameTime % 480L == 0L
                && isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.HOUND.get())) {
            spawnHounds(level);
        }
        if (gameTime % 600L == 0L
                && isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.PARTYGOER.get())) {
            spawnPartygoers(level);
        }
    }

    private static void spawnObservers(ServerLevel level) {
        if (!isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.WATCHER.get())) return;
        for (Player player : level.players()) {
            if (!player.isAlive() || player.isSpectator() || player.isCreative()) continue;
            int nearbyWatchers = countNearbyWatchers(level, player);
            if (nearbyWatchers >= MAX_LOCAL_WATCHERS) continue;
            // A first sighting is more likely than a second. The second entity
            // is a rare local flanker, not an unrestricted mob swarm.
            int spawnChance = nearbyWatchers == 0 ? 4 : 8;
            if (level.getRandom().nextInt(spawnChance) != 0) continue;

            for (int attempt = 0; attempt < 10; attempt++) {
                double angle = level.getRandom().nextDouble() * (Math.PI * 2.0D);
                // The second member enters closer to the player so the
                // local pair can coordinate without expanding the spawn footprint.
                int distance = nearbyWatchers == 0
                        ? 17 + level.getRandom().nextInt(11)
                        : 12 + level.getRandom().nextInt(9);
                int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
                int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
                BlockPos pos = new BlockPos(x, player.blockPosition().getY(), z);

                if (!isSafeStandingSpace(level, pos)) continue;

                // Keep the first appearance outside the player's forward view.
                // The player should discover it in the periphery, not have it pop
                // directly in front of the crosshair and vanish immediately.
                Vec3 eye = player.getEyePosition();
                Vec3 toSpawn = new Vec3(
                        pos.getX() + 0.5D - eye.x,
                        pos.getY() + 1.25D - eye.y,
                        pos.getZ() + 0.5D - eye.z).normalize();
                if (player.getLookAngle().dot(toSpawn) > 0.35D) continue;

                BlockHitResult sight = level.clip(new ClipContext(
                        player.getEyePosition(), new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (sight.getType() != HitResult.Type.MISS) continue;

                ThresholdWatcherEntity watcher = ThresholdEntities.WATCHER.get().create(level);
                if (watcher == null) continue;
                float yaw = (float) (Mth.atan2(player.getZ() - z, player.getX() - x)
                        * (180.0D / Math.PI)) - 90.0F;
                watcher.moveTo(x + 0.5D, pos.getY(), z + 0.5D, yaw, 0.0F);
                watcher.setYHeadRot(yaw);
                watcher.setYBodyRot(yaw);
                watcher.setFlankerRole(nearbyWatchers > 0);
                if (level.addFreshEntity(watcher)) return;
            }
        }
    }

    /**
     * Finds a safe, unobstructed floor position in front of the operator.
     * The explicit summon command is a visual diagnostic, so it must not hide
     * the entity behind the player or inside a wall.
     */
    public static BlockPos findWatcherShowcasePosition(ServerLevel level, Player player) {
        return findShowcasePosition(level, player, 8, 16, 2);
    }

    /**
     * Finds a safe, loaded, visible floor position for an operator test summon.
     * It is dimension-agnostic and never changes the natural-spawn whitelist.
     */
    public static BlockPos findShowcasePosition(
            ServerLevel level, Player player, int minimumDistance, int maximumDistance, int step) {
        if (step <= 0 || minimumDistance < 1 || maximumDistance < minimumDistance) return null;
        for (int distance = minimumDistance; distance <= maximumDistance; distance += step) {
            BlockPos pos = player.blockPosition().relative(player.getDirection(), distance);
            if (!isSafeStandingSpace(level, pos)) continue;
            BlockHitResult sight = level.clip(new ClipContext(
                    player.getEyePosition(),
                    new Vec3(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (sight.getType() == HitResult.Type.MISS) return pos;
        }
        return null;
    }

    /**
     * Caps a local encounter at two Watchers. The second member is assigned
     * the flanker role and can relay only fresh, same-player observations.
     * Natural spawning and operator summons share this exact budget.
     */
    public static int countNearbyWatchers(ServerLevel level, Player player) {
        return level.getEntitiesOfClass(
                ThresholdWatcherEntity.class,
                player.getBoundingBox().inflate(WATCHER_NEARBY_RADIUS)).size();
    }

    public static boolean hasNearbyWatcher(ServerLevel level, Player player) {
        return countNearbyWatchers(level, player) >= MAX_LOCAL_WATCHERS;
    }

    private static void spawnPartygoers(ServerLevel level) {
        if (!isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.PARTYGOER.get())) return;
        // A rare encounter: one lure at a time, well away from the world spawn.
        for (Player player : level.players()) {
            if (!player.isAlive() || player.isSpectator()) continue;
            if (level.isDay()) continue;
            if (!level.getEntitiesOfClass(ThresholdPartygoerEntity.class,
                    player.getBoundingBox().inflate(64.0D)).isEmpty()) continue;
            if (level.getRandom().nextInt(5) != 0) continue;

            for (int attempt = 0; attempt < 12; attempt++) {
                double angle = level.getRandom().nextDouble() * (Math.PI * 2.0D);
                int distance = 20 + level.getRandom().nextInt(11);
                int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
                int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
                BlockPos pos = level.getHeightmapPos(
                        net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        new BlockPos(x, player.blockPosition().getY(), z));
                if (pos.distSqr(level.getSharedSpawnPos()) < 48.0D * 48.0D) continue;
                if (!isSafeStandingSpace(level, pos)) continue;

                // A Partygoer should be discovered in the periphery or behind
                // cover, never materialize in the centre of the player's view.
                Vec3 eye = player.getEyePosition();
                Vec3 toSpawn = new Vec3(
                        pos.getX() + 0.5D - eye.x,
                        pos.getY() + 1.1D - eye.y,
                        pos.getZ() + 0.5D - eye.z).normalize();
                if (player.getLookAngle().dot(toSpawn) > 0.18D) continue;

                ThresholdPartygoerEntity partygoer = ThresholdEntities.PARTYGOER.get().create(level);
                if (partygoer == null) continue;
                partygoer.moveTo(x + 0.5D, pos.getY(), z + 0.5D,
                        (float) (Mth.atan2(player.getZ() - z, player.getX() - x)
                                * (180.0D / Math.PI)) - 90.0F, 0.0F);
                // Natural encounters begin in the stalking state. Looking back,
                // sprinting, or getting close makes it commit to the hunt.
                if (level.addFreshEntity(partygoer)) return;
            }
        }
    }

    private static void spawnHounds(ServerLevel level) {
        if (!isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.HOUND.get())) return;
        for (Player player : level.players()) {
            if (!player.isAlive() || player.isSpectator()) continue;

            // The first 48x48 room remains a protected onboarding shelter.
            int playerRoomX = Math.floorDiv(player.blockPosition().getX(), LEVEL_1_ROOM_SIZE);
            int playerRoomZ = Math.floorDiv(player.blockPosition().getZ(), LEVEL_1_ROOM_SIZE);
            if (playerRoomX == 0 && playerRoomZ == 0) continue;

            AABB nearby = player.getBoundingBox().inflate(48.0D);
            if (!level.getEntitiesOfClass(ThresholdHoundEntity.class, nearby).isEmpty()) continue;
            if (level.getRandom().nextInt(4) != 0) continue;

            for (int attempt = 0; attempt < 12; attempt++) {
                double angle = level.getRandom().nextDouble() * (Math.PI * 2.0D);
                int distance = 17 + level.getRandom().nextInt(12);
                int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
                int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
                BlockPos pos = new BlockPos(x, player.blockPosition().getY(), z);

                int roomX = Math.floorDiv(x, LEVEL_1_ROOM_SIZE);
                int roomZ = Math.floorDiv(z, LEVEL_1_ROOM_SIZE);
                if (roomX == 0 && roomZ == 0) continue;
                if (!isSafeStandingSpace(level, pos)) continue;
                if (level.getMaxLocalRawBrightness(pos) > 9) continue;

                // Do not materialize a Hound directly in the player's view.
                // Its first appearance should be discovered through movement or sound.
                BlockHitResult sight = level.clip(new ClipContext(
                        player.getEyePosition(),
                        new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                if (sight.getType() == HitResult.Type.MISS) continue;

                ThresholdHoundEntity hound = ThresholdEntities.HOUND.get().create(level);
                if (hound == null) continue;
                hound.moveTo(x + 0.5D, pos.getY(), z + 0.5D,
                        level.getRandom().nextFloat() * 360.0F, 0.0F);
                // The Hound begins in a quiet stalking state; noise and proximity trigger pursuit.
                if (level.addFreshEntity(hound)) return;
            }
        }
    }

    private static boolean isSafeStandingSpace(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos) || !level.hasChunkAt(pos.above())
                || !level.hasChunkAt(pos.below())) return false;
        if (!level.isEmptyBlock(pos) || !level.isEmptyBlock(pos.above())) return false;

        BlockState floor = level.getBlockState(pos.below());
        return !floor.isAir()
                && floor.getFluidState().isEmpty()
                && !floor.getCollisionShape(level, pos.below()).isEmpty();
    }
}
