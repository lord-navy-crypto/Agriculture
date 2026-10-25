package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A careful honey-collection tool for vanilla hives.
 *
 * It preserves vanilla honey-level storage and requires both a full hive and
 * campfire smoke. The player supplies an empty glass bottle from inventory;
 * this tool does not create honey or force-load neighboring chunks.
 */
public final class HoneyHarvesterItem extends Item {
    public HoneyHarvesterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);

        if (player == null
                || (!state.is(Blocks.BEEHIVE) && !state.is(Blocks.BEE_NEST))
                || !state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
            return InteractionResult.PASS;
        }

        if (state.getValue(BeehiveBlock.HONEY_LEVEL) < 5) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.gugugaga.honey_harvester_not_ready"), true);
            }
            return InteractionResult.FAIL;
        }

        if (!hasClearCampfireSmoke(level, pos)) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.gugugaga.honey_harvester_smoke_required"), true);
            }
            return InteractionResult.FAIL;
        }

        // An offhand pair of shears selects honeycomb mode; otherwise the tool bottles
        // honey as before. Both modes require a full hive and a lit, clear smoke column.
        boolean combMode = player.getOffhandItem().is(Items.SHEARS);
        ItemStack bottle = (combMode || player.getAbilities().instabuild)
                ? ItemStack.EMPTY : findGlassBottle(player);
        if (!combMode && !player.getAbilities().instabuild && bottle.isEmpty()) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.gugugaga.honey_harvester_bottle_required"), true);
            }
            return InteractionResult.FAIL;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack harvest;
        if (combMode) {
            // Shears are reusable tools: spending durability prevents free honeycomb output.
            if (!player.getAbilities().instabuild) {
                player.getOffhandItem().hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
            }
            harvest = new ItemStack(Items.HONEYCOMB, 3);
        } else {
            if (!player.getAbilities().instabuild) {
                bottle.shrink(1);
            }
            harvest = NectarHoneySource.selectHoneyBottle(level, pos);
        }

        if (!player.getInventory().add(harvest)) {
            level.addFreshEntity(new ItemEntity(
                    level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, harvest));
        }

        level.setBlock(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, 0), 3);
        level.playSound(null, pos, combMode ? SoundEvents.BEEHIVE_SHEAR : SoundEvents.BOTTLE_FILL,
                SoundSource.BLOCKS, 1.0F, 1.0F);
        player.swing(context.getHand());
        player.displayClientMessage(
                Component.translatable(combMode
                        ? "message.gugugaga.honey_harvester_combs_collected"
                        : "message.gugugaga.honey_harvester_collected"), true);
        return InteractionResult.SUCCESS;
    }

    private static ItemStack findGlassBottle(Player player) {
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(Items.GLASS_BOTTLE)) {
            return offhand;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.GLASS_BOTTLE)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Reuse Minecraft's native smoke test so vanilla nest, hive and harvester
     * agree on when a nearby lit campfire safely calms bees.
     * The loaded-chunk guard prevents a modded world boundary from force-loading.
     */
    private static boolean hasClearCampfireSmoke(Level level, BlockPos hivePos) {
        BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos();
        for (int distance = 1; distance <= 5; distance++) {
            candidate.set(hivePos.getX(), hivePos.getY() - distance, hivePos.getZ());
            if (!level.hasChunkAt(candidate)) {
                return false;
            }
        }
        return CampfireBlock.isSmokeyPos(level, hivePos);
    }
}
