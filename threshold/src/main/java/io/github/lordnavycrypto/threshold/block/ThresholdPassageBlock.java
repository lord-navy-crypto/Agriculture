package io.github.lordnavycrypto.threshold.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** A fixed, authored transition between two Threshold dimensions. */
public final class ThresholdPassageBlock extends Block {
    private final ResourceKey<Level> destinationKey;
    private final double spawnX;
    private final double spawnY;
    private final double spawnZ;
    private final String destinationLabel;

    public ThresholdPassageBlock(
            ResourceKey<Level> destinationKey,
            double spawnX, double spawnY, double spawnZ,
            String destinationLabel,
            Properties properties) {
        super(properties);
        this.destinationKey = destinationKey;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.destinationLabel = destinationLabel;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        ServerLevel destination = serverPlayer.getServer().getLevel(destinationKey);
        if (destination == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("The passage is unavailable."), true);
            return InteractionResult.FAIL;
        }

        serverPlayer.teleportTo(destination, spawnX, spawnY, spawnZ,
                serverPlayer.getYRot(), serverPlayer.getXRot());
        serverPlayer.displayClientMessage(
                Component.literal(destinationLabel), true);
        return InteractionResult.SUCCESS;
    }
}
