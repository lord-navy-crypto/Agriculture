package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/** Shows the shipped per-item game profile directly on edible item tooltips. */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID)
public final class NutritionTooltipV40 {
    private NutritionTooltipV40() {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.has(DataComponents.FOOD)) return;

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        boolean catalogued = NutritionProfilesV35.hasProfile(itemId);
        int[] values = NutritionProfilesV35.contribution(itemId);
        List<Component> tooltip = event.getToolTip();

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_profile")
                .withStyle(ChatFormatting.GOLD));
        if (!catalogued) {
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_generic")
                    .withStyle(ChatFormatting.GRAY));
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] <= 0) continue;
            String nutrient = NutritionProfilesV35.NUTRIENTS[i];
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrient_line",
                    Component.translatable("nutrition.gugugaga." + nutrient), values[i])
                    .withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_note")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
