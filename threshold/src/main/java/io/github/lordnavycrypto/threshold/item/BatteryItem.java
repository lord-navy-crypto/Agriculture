package io.github.lordnavycrypto.threshold.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A spare cell that restores three minutes of charge to a held flashlight. */
public final class BatteryItem extends Item {
    public BatteryItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack battery = player.getItemInHand(hand);
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ItemStack flashlight = mainHand.getItem() instanceof FlashlightItem
                ? mainHand
                : offHand.getItem() instanceof FlashlightItem ? offHand : ItemStack.EMPTY;

        if (flashlight.isEmpty() || flashlight.getDamageValue() <= 0) {
            return InteractionResultHolder.fail(battery);
        }

        if (!level.isClientSide) {
            flashlight.setDamageValue(Math.max(
                    0, flashlight.getDamageValue() - FlashlightItem.RECHARGE_AMOUNT));
            if (!player.getAbilities().instabuild) battery.shrink(1);
            level.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK,
                    SoundSource.PLAYERS, 0.65F, 1.35F);
        }
        return InteractionResultHolder.sidedSuccess(battery, level.isClientSide());
    }
}
