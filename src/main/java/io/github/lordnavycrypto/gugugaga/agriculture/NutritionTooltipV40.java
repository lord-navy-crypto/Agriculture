package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Per-food client hint; resolves the very same server nutrition catalog.
 * The main nutrient panel processes 25 reserves per game day. Hydration and
 * sodium are updated immediately from their own independent v3.7 catalog.
 */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, value = Dist.CLIENT)
public final class NutritionTooltipV40 {
    private static final int MAX_PREVIEW_ROWS = 3;

    private NutritionTooltipV40() {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.has(DataComponents.FOOD)) return;

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        List<Component> tooltip = event.getToolTip();
        if (NutritionProfilesV35.isHydrationOnly(itemId)) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.gugugaga.hydration_only")
                    .withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_balance",
                    WaterSaltProfilesV37.water(itemId), WaterSaltProfilesV37.sodium(itemId))
                    .withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_note")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        boolean catalogued = NutritionProfilesV35.hasProfile(itemId);
        int[] values = NutritionProfilesV35.contribution(itemId);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            // Never display water/sodium from a nutrient profile: those
            // gameplay meters are owned by WaterSaltProfilesV37.
            if (NutritionPhysiologyV39.linked(i)) continue;
            if (values[i] > 0) indices.add(i);
        }
        // Rank by fraction of the abstract game-day target covered, not
        // raw points alone. This avoids hiding a smaller but more targeted
        // micronutrient contribution behind a large macro score.
        indices.sort((a, b) -> Integer.compare(
                values[b] * NutritionPhysiologyV39.dailyTarget(a),
                values[a] * NutritionPhysiologyV39.dailyTarget(b)));

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_profile")
                .withStyle(ChatFormatting.GOLD));
        if (!catalogued) {
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_generic")
                    .withStyle(ChatFormatting.GRAY));
        }

        boolean expanded = Screen.hasShiftDown();
        int rows = expanded ? indices.size() : Math.min(MAX_PREVIEW_ROWS, indices.size());
        for (int row = 0; row < rows; row++) {
            int i = indices.get(row);
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrient_target_line",
                    Component.translatable("nutrition.gugugaga." + NutritionProfilesV35.NUTRIENTS[i]),
                    values[i], NutritionPhysiologyV39.dailyTarget(i))
                    .withStyle(ChatFormatting.GRAY));
        }
        if (!expanded && indices.size() > rows) {
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_expand",
                    indices.size() - rows).withStyle(ChatFormatting.YELLOW));
        }
        if (WaterSaltProfilesV37.hasProfile(itemId)) {
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_balance",
                    WaterSaltProfilesV37.water(itemId), WaterSaltProfilesV37.sodium(itemId))
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_balance_fallback",
                    WaterSaltProfilesV37.water(itemId), WaterSaltProfilesV37.sodium(itemId))
                    .withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_daily")
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.gugugaga.nutrition_note")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
