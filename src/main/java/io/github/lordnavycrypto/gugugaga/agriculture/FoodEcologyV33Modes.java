package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Map;
import net.minecraft.world.item.ItemStack;

/** Additive flavor and seafood recipes on existing equipment. */
public final class FoodEcologyV33Modes {
    private FoodEcologyV33Modes() {}
    private static final Map<String, String> SPICES = Map.ofEntries(
            Map.entry("gugugaga:black_pepper", "cracked_black_pepper"),
            Map.entry("gugugaga:lemongrass", "lemongrass_flakes"),
            Map.entry("gugugaga:wasabi", "wasabi_paste"),
            Map.entry("gugugaga:cumin", "ground_cumin"),
            Map.entry("gugugaga:cardamom", "ground_cardamom"),
            Map.entry("gugugaga:fenugreek", "ground_fenugreek"),
            Map.entry("gugugaga:sugar_beet", "sugar_beet_pulp"),
            Map.entry("gugugaga:daikon", "daikon_relish")
    );
    private static final Map<String, String> SMOKED_FISH = Map.ofEntries(
            Map.entry("gugugaga:saltwater_anchovy", "smoked_anchovy"),
            Map.entry("gugugaga:saltwater_sardine", "smoked_sardine"),
            Map.entry("gugugaga:saltwater_herring", "smoked_herring"),
            Map.entry("gugugaga:saltwater_mackerel", "smoked_mackerel"),
            Map.entry("gugugaga:saltwater_sea_bass", "smoked_sea_bass"),
            Map.entry("gugugaga:saltwater_sea_bream", "smoked_sea_bream"),
            Map.entry("gugugaga:saltwater_shrimp", "smoked_shrimp"),
            Map.entry("gugugaga:saltwater_crab", "smoked_crab")
    );
    public static String resolve(KitchenProcessingBlock.Kind kind, ItemStack offhand, String input) {
        if (kind == KitchenProcessingBlock.Kind.MORTAR
                && offhand.is(KitchenV3Content.MORTAR_PESTLE.get()))
            return SPICES.get(input);
        if (kind == KitchenProcessingBlock.Kind.SMOKE
                && offhand.is(KitchenV3Content.KITCHEN_TONGS.get()))
            return SMOKED_FISH.get(input);
        return null;
    }
}
