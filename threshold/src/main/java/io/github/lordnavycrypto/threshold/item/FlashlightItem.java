package io.github.lordnavycrypto.threshold.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A compact exploration flashlight. In this first pass its beam is represented
 * by a short night-vision pulse; it does not alter the world's light map.
 */
public final class FlashlightItem extends Item {
    public FlashlightItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, false, false, true));
            player.getCooldowns().addCooldown(this, 80);
            level.playSound(null, player.blockPosition(), SoundEvents.LEVER_CLICK,
                    SoundSource.PLAYERS, 0.65F, 1.35F);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
