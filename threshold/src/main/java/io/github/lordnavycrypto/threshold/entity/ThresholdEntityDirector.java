package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Keeps the two dimensions' entity rules separate.
 * Level 0 allows only short-lived, harmless observers. Level 1 can spawn Hounds
 * outside the entry shelter; no vanilla biome spawner tables are added.
 */
public final class ThresholdEntityDirector {
    private static final int LEVEL_1_ROOM_SIZE = 48;

    private ThresholdEntityDirector() {}

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (level.dimension().equals(ThresholdDimensions.LEVEL_0)) {
            // Level 0 admits no living creature or projectile except the
            // harmless Observer. Players remain free to traverse the dimension.
            if ((event.getEntity() instanceof LivingEntity living
                    && !(living instanceof Player)
                    && !(living instanceof ThresholdWatcherEntity))
                    || event.getEntity() instanceof Projectile) {
                event.setCanceled(true);
            }
            return;
        }

        if (event.getEntity() instanceof ThresholdWatcherEntity
                && !level.dimension().equals(ThresholdDimensions.LEVEL_0)) {
            event.setCanceled(true);
        }
        if (event.getEntity() instanceof ThresholdHoundEntity
                && !level.dimension().equals(ThresholdDimensions.LEVEL_1)) {
            event.setCanceled(true);
        }

        if (level.dimension().equals(ThresholdDimensions.LEVEL_1)
                && event.getEntity() instanceof Mob mob
                && !(mob instanceof ThresholdHoundEntity)) {
            // Level 1's first pass uses authored Backrooms creatures only;
            // vanilla animals/monsters and unrelated modded mobs stay out.
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        long gameTime = level.getGameTime();

        if (level.dimension().equals(ThresholdDimensions.LEVEL_0)) {
            if (gameTime % 240L == 0L) spawnObservers(level);
        } else if (level.dimension().equals(ThresholdDimensions.LEVEL_1)) {
            if (gameTime % 360L == 0L) spawnHounds(level);
        }
    }

    private static void spawnObservers(ServerLevel level) {
        for (Player player : level.players()) {
            if (!player.isAlive() || player.isSpectator()) continue;
            if (!level.getEntitiesOfClass(ThresholdWatcherEntity.class,
                    player.getBoundingBox().inflate(36.0D)).isEmpty()) continue;
            if (level.getRandom().nextInt(4) != 0) continue;

            for (int attempt = 0; attempt < 10; attempt++) {
                double angle = level.getRandom().nextDouble() * (Math.PI * 2.0D);
                int distance = 13 + level.getRandom().nextInt(9);
                int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
                int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
                BlockPos pos = new BlockPos(x, player.blockPosition().getY(), z);

                if (!isSafeStandingSpace(level, pos)) continue;
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

    private static void spawnHounds(ServerLevel level) {
        for (Player player : level.players()) {
            if (!player.isAlive() || player.isSpectator()) continue;

            // The first 48x48 room remains a protected onboarding shelter.
            int playerRoomX = Math.floorDiv(player.blockPosition().getX(), LEVEL_1_ROOM_SIZE);
            int playerRoomZ = Math.floorDiv(player.blockPosition().getZ(), LEVEL_1_ROOM_SIZE);
            if (playerRoomX == 0 && playerRoomZ == 0) continue;

            AABB nearby = player.getBoundingBox().inflate(48.0D);
            if (!level.getEntitiesOfClass(ThresholdHoundEntity.class, nearby).isEmpty()) continue;
            if (level.getRandom().nextInt(3) != 0) continue;

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

                ThresholdHoundEntity hound = ThresholdEntities.HOUND.get().create(level);
                if (hound == null) continue;
                hound.moveTo(x + 0.5D, pos.getY(), z + 0.5D,
                        level.getRandom().nextFloat() * 360.0F, 0.0F);
                hound.setTarget(player);
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
