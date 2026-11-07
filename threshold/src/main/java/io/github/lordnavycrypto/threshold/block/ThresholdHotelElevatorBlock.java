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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A compact hotel elevator panel. Sneak-use selects floors 1 through 12;
 * ordinary use travels to another wing of Level 5 on the same dimension plane.
 * Every destination remains inside the generated hotel; no destructive floor
 * or out-of-bounds button is exposed.
 */
public final class ThresholdHotelElevatorBlock extends Block {
    public static final IntegerProperty FLOOR = IntegerProperty.create("floor", 1, 12);

    public ThresholdHotelElevatorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FLOOR, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLOOR);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        int selectedFloor = state.getValue(FLOOR);
        if (player.isCrouching()) {
            int nextFloor = selectedFloor == 12 ? 1 : selectedFloor + 1;
            level.setBlock(pos, state.setValue(FLOOR, nextFloor), 3);
            serverPlayer.displayClientMessage(
                    Component.literal("Hotel elevator: floor " + nextFloor
                            + " selected. Use normally to travel; sneak-use to cycle."),
                    true);
            return InteractionResult.SUCCESS;
        }

        ServerLevel hotel = serverPlayer.getServer().getLevel(ThresholdDimensions.LEVEL_5);
        if (hotel == null) {
            serverPlayer.displayClientMessage(
                    Component.literal("The hotel elevator is temporarily out of service."), true);
            return InteractionResult.FAIL;
        }

        // Each numbered floor is a distinct wing in the same infinite hotel.
        // Coordinates are deterministic and aligned to the 32x32 room grid.
        double targetX = (selectedFloor - 1) * 32.0 + 4.5;
        serverPlayer.teleportTo(hotel, targetX, 64.0, 4.5,
                serverPlayer.getYRot(), serverPlayer.getXRot());
        serverPlayer.displayClientMessage(
                Component.literal("Level 5 — Hotel wing " + selectedFloor + "."), true);
        return InteractionResult.SUCCESS;
    }
}
