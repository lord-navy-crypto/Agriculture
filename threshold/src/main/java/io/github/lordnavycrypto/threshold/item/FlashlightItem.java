package io.github.lordnavycrypto.threshold.item;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Rechargeable exploration flashlight. Charge is stored as item durability,
 * so it persists in inventories and saves. The first-pass beam is represented
 * by a short night-vision pulse rather than changing the world light map.
 */
public final class FlashlightItem extends Item {
    /** True only while the player is actively holding the flashlight on. */
    public static boolean isActive(Player player) {
        return player.isUsingItem() && player.getUseItem().getItem() instanceof FlashlightItem;
    }

    public static final int MAX_CHARGE = 480; // eight minutes at one unit per second
    public static final int CHARGE_INTERVAL_TICKS = 20;
    public static final int RECHARGE_AMOUNT = 180; // three minutes
    private static final int USE_DURATION = 72_000;

    public FlashlightItem() {
        super(new Item.Properties().durability(MAX_CHARGE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            level.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.LEVER_CLICK,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.45F, 1.7F);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) return;

        if (remainingUseDuration % CHARGE_INTERVAL_TICKS == 0) {
            int nextDamage = stack.getDamageValue() + 1;
            stack.setDamageValue(Math.min(nextDamage, stack.getMaxDamage()));
            if (nextDamage >= stack.getMaxDamage()) {
                entity.stopUsingItem();
                return;
            }
        }

        if (entity instanceof Player player && level.getGameTime() % CHARGE_INTERVAL_TICKS == 0) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION, 30, 0, true, false, false));
        }
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float remaining = 1.0F - stack.getDamageValue() / (float) stack.getMaxDamage();
        return Math.round(13.0F * Mth.clamp(remaining, 0.0F, 1.0F));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float remaining = 1.0F - stack.getDamageValue() / (float) stack.getMaxDamage();
        return Mth.hsvToRgb(Mth.clamp(remaining, 0.0F, 1.0F) * 0.33F, 1.0F, 1.0F);
    }
}
