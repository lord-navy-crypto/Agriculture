package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Map;
import net.minecraft.world.item.ItemStack;

/**
 * Optional specialist modes for existing kitchens. Offhand tools control the
 * alternative 1:1 recipe without introducing extra decorative workstations.
 * No ticking or client-only code; no legacy recipe or ID is replaced.
 */
public final class KitchenV32Modes {
    private KitchenV32Modes() {}

    private static final Map<String, String> KNIFE = Map.ofEntries(
            Map.entry("minecraft:beef", "raw_beef_strips"),
            Map.entry("minecraft:chicken", "raw_chicken_strips"),
            Map.entry("minecraft:potato", "potato_wedges"),
            Map.entry("minecraft:apple", "apple_slices"),
            Map.entry("minecraft:cod", "raw_cod_fillets"),
            Map.entry("minecraft:salmon", "raw_salmon_fillets")
    );

    private static final Map<String, String> GRATER = Map.ofEntries(
            Map.entry("minecraft:carrot", "finely_grated_carrot"),
            Map.entry("minecraft:potato", "hash_brown_shreds"),
            Map.entry("minecraft:beetroot", "beetroot_shavings"),
            Map.entry("minecraft:pumpkin", "grated_pumpkin"),
            Map.entry("gugugaga:cucumber", "grated_cucumber"),
            Map.entry("minecraft:apple", "grated_apple")
    );

    private static final Map<String, String> TONGS = Map.ofEntries(
            Map.entry("gugugaga:raw_beef_strips", "smoked_beef_strips"),
            Map.entry("gugugaga:raw_chicken_strips", "smoked_chicken_strips"),
            Map.entry("gugugaga:raw_cod_fillets", "smoked_cod_fillets"),
            Map.entry("gugugaga:raw_salmon_fillets", "smoked_salmon_fillets"),
            Map.entry("gugugaga:sliced_brown_mushroom", "smoked_mushroom_slices")
    );

    private static final Map<String, String> MEASURE = Map.ofEntries(
            Map.entry("gugugaga:apple_juice", "measured_apple_drink"),
            Map.entry("gugugaga:grape_juice", "measured_grape_drink"),
            Map.entry("gugugaga:cocoa_mix", "measured_cocoa_base"),
            Map.entry("gugugaga:pumpkin_puree", "measured_pumpkin_base"),
            Map.entry("gugugaga:carrot_juice", "measured_carrot_drink")
    );

    private static final Map<String, String> SKIM = Map.ofEntries(
            Map.entry("gugugaga:mushroom_stock", "clear_mushroom_stock"),
            Map.entry("gugugaga:cod_stock", "clear_cod_stock"),
            Map.entry("gugugaga:onion_stock", "clear_onion_stock"),
            Map.entry("gugugaga:pumpkin_soup_base", "silky_pumpkin_soup"),
            Map.entry("gugugaga:potato_soup_base", "creamy_potato_soup")
    );

    public static String resolve(KitchenProcessingBlock.Kind kind,
                                 ItemStack offhand, String input) {
        if (kind == KitchenProcessingBlock.Kind.CHOP
                && offhand.is(KitchenV3Content.CHEF_KNIFE.get())) {
            return KNIFE.get(input);
        }
        if (kind == KitchenProcessingBlock.Kind.CHOP
                && offhand.is(KitchenV3Content.BOX_GRATER.get())) {
            return GRATER.get(input);
        }
        if (kind == KitchenProcessingBlock.Kind.SMOKE
                && offhand.is(KitchenV3Content.KITCHEN_TONGS.get())) {
            return TONGS.get(input);
        }
        if (kind == KitchenProcessingBlock.Kind.MIX
                && offhand.is(KitchenV3Content.MEASURING_CUP.get())) {
            return MEASURE.get(input);
        }
        if (kind == KitchenProcessingBlock.Kind.SIMMER
                && offhand.is(KitchenV3Content.SKIMMER_SPOON.get())) {
            return SKIM.get(input);
        }
        return null;
    }
}
