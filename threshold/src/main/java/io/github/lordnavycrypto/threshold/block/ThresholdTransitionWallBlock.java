package io.github.lordnavycrypto.threshold.block;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** A special wall leading from Level 0 to Level 1 when activated. */
public final class ThresholdTransitionWallBlock extends Block {
    public ThresholdTransitionWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        boolean fromLevel0 = level.dimension().equals(ThresholdDimensions.LEVEL_0);
        boolean fromLevel1 = level.dimension().equals(ThresholdDimensions.LEVEL_1);
        if (!fromLevel0 && !fromLevel1) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        ServerLevel destination = serverPlayer.getServer().getLevel(
                fromLevel0 ? ThresholdDimensions.LEVEL_1 : ThresholdDimensions.LEVEL_0);
        if (destination == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("The passage is unavailable."), true);
            return InteractionResult.FAIL;
        }

        double targetY = fromLevel0 ? 64.0 : 65.0;
        String label = fromLevel0
                ? "Level 1 — The Vulnerable Shelter"
                : "Level 0 — Isolation";
        serverPlayer.teleportTo(destination, 4.5, targetY, 4.5,
                serverPlayer.getYRot(), serverPlayer.getXRot());
        serverPlayer.displayClientMessage(Component.literal(label), true);
        return InteractionResult.SUCCESS;
    }
}
