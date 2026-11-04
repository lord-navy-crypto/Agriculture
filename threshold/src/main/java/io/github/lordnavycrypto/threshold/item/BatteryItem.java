package io.github.lordnavycrypto.threshold.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Spare cell used to reset the flashlight's short activation cooldown. */
public final class BatteryItem extends Item {
    public BatteryItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player.getCooldowns().isOnCooldown(ThresholdItems.FLASHLIGHT.get())) {
            player.getCooldowns().removeCooldown(ThresholdItems.FLASHLIGHT.get());
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
