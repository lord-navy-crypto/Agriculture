package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Lily-of-the-valley stew parity: the native HoneyBottleItem removes poison.
 * Apply the matching 1.21.1 suspicious-stew poison AFTER native consumption,
 * otherwise the vanilla antidote behavior would silently negate the effect.
 * This override is isolated to the poisonous flower variant.
 */
public final class PoisonFlowerHoneyBottleItem extends HoneyBottleItem {
    public PoisonFlowerHoneyBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        ItemStack remainder = super.finishUsingItem(stack, level, consumer);
        if (!level.isClientSide) {
            consumer.addEffect(new MobEffectInstance(MobEffects.POISON, 240, 0));
        }
        return remainder;
    }
}
