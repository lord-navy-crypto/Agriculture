package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Gentle collection for vanilla beehives and bee nests. With a glass bottle in the offhand,
 * collects a honey bottle; otherwise collects three honeycombs. It resets honey level before the
 * vanilla defensive harvest handler can run. Standard bottle/shears interactions remain unchanged.
 */
public final class BeeKeeperBrushItem extends Item {
    public BeeKeeperBrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.is(Blocks.BEEHIVE) || state.is(Blocks.BEE_NEST))
                || state.getValue(BeehiveBlock.HONEY_LEVEL) < 5) return InteractionResult.PASS;
        if (!hasCampfireSmoke(level, pos)) return InteractionResult.FAIL;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        boolean bottled = player.getOffhandItem().is(Items.GLASS_BOTTLE);
        ItemStack output;
        if (bottled) {
            if (!player.getAbilities().instabuild) player.getOffhandItem().shrink(1);
            output = new ItemStack(Items.HONEY_BOTTLE);
        } else {
            output = new ItemStack(Items.HONEYCOMB, 3);
        }
        level.setBlock(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, 0), 3);
        if (!player.getInventory().add(output)) player.drop(output, false);
        player.displayClientMessage(Component.translatable(
                bottled ? "message.gugugaga.gentle_honey_bottle" : "message.gugugaga.gentle_honeycomb"), true);
        return InteractionResult.SUCCESS;
    }

    /** A lit campfire and a clear smoke column are required for a gentle harvest. */
    private static boolean hasCampfireSmoke(Level level, BlockPos hivePos) {
        for (int depth = 1; depth <= 5; depth++) {
            BlockPos belowPos = hivePos.below(depth);
            if (!level.hasChunkAt(belowPos)) return false;
            BlockState below = level.getBlockState(belowPos);
            if (below.is(Blocks.CAMPFIRE) || below.is(Blocks.SOUL_CAMPFIRE)) {
                return below.getValue(BlockStateProperties.LIT);
            }
            if (!below.isAir()) return false;
        }
        return false;
    }
}
