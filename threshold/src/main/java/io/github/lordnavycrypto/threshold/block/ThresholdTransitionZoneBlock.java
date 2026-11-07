package io.github.lordnavycrypto.threshold.block;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Invisible, non-colliding seam used for a gradual Level 1 -> Level 2 transition.
 * It is placed inside the corridor rather than presented as a conventional portal.
 */
public final class ThresholdTransitionZoneBlock extends Block {
    public ThresholdTransitionZoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) return;
        ServerLevel destination = player.getServer().getLevel(ThresholdDimensions.LEVEL_2);
        if (destination == null) {
            player.displayClientMessage(
                    Component.literal("The utility corridor does not resolve yet."), true);
            return;
        }
        player.teleportTo(destination, 4.5, 64.0, 4.5, player.getYRot(), player.getXRot());
        player.displayClientMessage(
                Component.literal("The pipes continue beyond the warehouse. Level 2 — Abandoned Utility Halls."),
                true);
    }
}
