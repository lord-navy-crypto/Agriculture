package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Convenience kit for placing a 2x2 apiary made of four native vanilla beehives.
 * Each hive keeps vanilla bee storage, AI compatibility, honey levels, and behavior.
 */
public final class LargeApiaryKitItem extends Item {
    public LargeApiaryKitItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        BlockPos anchor = context.getClickedPos().relative(context.getClickedFace());
        BlockPos[] positions = new BlockPos[] {
                anchor, anchor.east(), anchor.south(), anchor.east().south()
        };

        BlockState[] originalStates = new BlockState[positions.length];
        for (int i = 0; i < positions.length; i++) {
            BlockPos pos = positions[i];
            // Do not query or place into a neighboring chunk that is not already loaded.
            if (!level.hasChunkAt(pos)) {
                if (player != null) {
                    player.displayClientMessage(
                            Component.translatable("message.gugugaga.apiary_space_needed"), true);
                }
                return InteractionResult.FAIL;
            }
            BlockState existing = level.getBlockState(pos);
            if (!existing.canBeReplaced() || level.getBlockEntity(pos) != null) {
                if (player != null) {
                    player.displayClientMessage(
                            Component.translatable("message.gugugaga.apiary_space_needed"), true);
                }
                return InteractionResult.FAIL;
            }
            originalStates[i] = existing;
        }

        Direction facing = player == null ? Direction.NORTH : player.getDirection().getOpposite();
        BlockState hive = Blocks.BEEHIVE.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        for (int i = 0; i < positions.length; i++) {
            if (!level.setBlock(positions[i], hive, 3)) {
                // Avoid leaving a partial 2x2 apiary if a placement is rejected midway.
                for (int rollback = i - 1; rollback >= 0; rollback--) {
                    level.setBlock(positions[rollback], originalStates[rollback], 3);
                }
                if (player != null) {
                    player.displayClientMessage(
                            Component.translatable("message.gugugaga.apiary_space_needed"), true);
                }
                return InteractionResult.FAIL;
            }
        }

        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable("message.gugugaga.apiary_placed"), true);
        }
        return InteractionResult.SUCCESS;
    }
}
