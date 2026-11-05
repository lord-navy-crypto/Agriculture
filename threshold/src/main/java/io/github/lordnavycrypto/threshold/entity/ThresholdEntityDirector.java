package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Controls Threshold-authored encounters and keeps custom horror entities in
 * their intended dimensions. Vanilla mobs, dropped items, vehicles, and
 * player-created projectiles are not filtered here.
 *
 * Natural vanilla biome spawning is configured separately; an empty biome
 * spawn table does not prevent commands, spawn eggs, or projectiles from working.
 */
public final class ThresholdEntityDirector {
    private static final int LEVEL_1_ROOM_SIZE = 48;
    private static final int WATCHER_SPAWN_INTERVAL_TICKS = 300;
    private static final double WATCHER_NEARBY_RADIUS = 42.0D;

    private ThresholdEntityDirector() {}

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        // Never blanket-cancel vanilla entities, player-created projectiles,
        // dropped items, vehicles, or modded entities in a Threshold dimension.
        // The dimension director only constrains Threshold's own custom mobs.
        if (event.getEntity() instanceof ThresholdWatcherEntity
                && !level.dimension().equals(ThresholdDimensions.LEVEL_0)) {
            event.setCanceled(true);
            return;
        }
        if (event.getEntity() instanceof ThresholdHoundEntity
                && !level.dimension().equals(ThresholdDimensions.LEVEL_1)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        long gameTime = level.getGameTime();

        if (level.dimension().equals(ThresholdDimensions.LEVEL_0)) {
            if (gameTime % WATCHER_SPAWN_INTERVAL_TICKS == 0L) spawnObservers(level);
        } else if (level.dimension().equals(ThresholdDimensions.LEVEL_1)) {
            if (gameTime % 480L == 0L) spawnHounds(level);
        } else if (level.dimension().equals(Level.OVERWORLD)) {
            if (gameTime % 600L == 0L) spawnPartygoers(level);
        }
    }

    private static void spawnObservers(ServerLevel level) {
        for (Player player : level.players()) {
            if (!player.isAlive() || player.isSpectator()) continue;
            if (hasNearbyWatcher(level, player)) continue;
            if (level.getRandom().nextInt(4) != 0) continue;

            for (int attempt = 0; attempt < 10; attempt++) {
                double angle = level.getRandom().nextDouble() * (Math.PI * 2.0D);
                int distance = 17 + level.getRandom().nextInt(11);
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
        for (int distance = 8; distance <= 16; distance += 2) {
            BlockPos pos = player.blockPosition().relative(player.getDirection(), distance);
            if (!isSafeStandingSpace(level, pos)) continue;
            BlockHitResult sight = level.clip(new ClipContext(
                    player.getEyePosition(),
                    new Vec3(pos.getX() + 0.5D, pos.getY() + 1.25D, pos.getZ() + 0.5D),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (sight.getType() == HitResult.Type.MISS) return pos;
        }
        return null;
    }

    /**
     * Keeps one observer per local encounter area. This is shared by natural
     * spawning and the operator summon command, so testing cannot stack them.
     */
    public static boolean hasNearbyWatcher(ServerLevel level, Player player) {
        return !level.getEntitiesOfClass(
                ThresholdWatcherEntity.class,
                player.getBoundingBox().inflate(WATCHER_NEARBY_RADIUS)).isEmpty();
    }

    private static void spawnPartygoers(ServerLevel level) {
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
