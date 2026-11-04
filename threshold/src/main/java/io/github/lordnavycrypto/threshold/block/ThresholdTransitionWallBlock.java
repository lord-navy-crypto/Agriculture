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
        if (!level.dimension().equals(ThresholdDimensions.LEVEL_0)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        ServerLevel destination = serverPlayer.getServer().getLevel(ThresholdDimensions.LEVEL_1);
        if (destination == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("Level 1 is unavailable."), true);
            return InteractionResult.FAIL;
        }
        serverPlayer.teleportTo(destination, 4.5, 64.0, 4.5,
                serverPlayer.getYRot(), serverPlayer.getXRot());
        serverPlayer.displayClientMessage(
                Component.literal("Level 1 — The Vulnerable Shelter"), true);
        return InteractionResult.SUCCESS;
    }
}
