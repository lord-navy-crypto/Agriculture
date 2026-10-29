package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** V3.2 extends inventory recipes without adding blocks or changing old IDs. */
public final class KitchenV32Content {
    private KitchenV32Content() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredItem<Item> RAW_BEEF_STRIPS =
            material("raw_beef_strips");
    public static final DeferredItem<Item> RAW_CHICKEN_STRIPS =
            material("raw_chicken_strips");
    public static final DeferredItem<Item> POTATO_WEDGES =
            material("potato_wedges");
    public static final DeferredItem<Item> APPLE_SLICES =
            material("apple_slices");
    public static final DeferredItem<Item> RAW_COD_FILLETS =
            material("raw_cod_fillets");
    public static final DeferredItem<Item> RAW_SALMON_FILLETS =
            material("raw_salmon_fillets");
    public static final DeferredItem<Item> FINELY_GRATED_CARROT =
            material("finely_grated_carrot");
    public static final DeferredItem<Item> HASH_BROWN_SHREDS =
            material("hash_brown_shreds");
    public static final DeferredItem<Item> BEETROOT_SHAVINGS =
            material("beetroot_shavings");
    public static final DeferredItem<Item> GRATED_PUMPKIN =
            material("grated_pumpkin");
    public static final DeferredItem<Item> GRATED_CUCUMBER =
            material("grated_cucumber");
    public static final DeferredItem<Item> GRATED_APPLE =
            material("grated_apple");
    public static final DeferredItem<Item> SMOKED_BEEF_STRIPS =
            material("smoked_beef_strips");
    public static final DeferredItem<Item> SMOKED_CHICKEN_STRIPS =
            material("smoked_chicken_strips");
    public static final DeferredItem<Item> SMOKED_COD_FILLETS =
            material("smoked_cod_fillets");
    public static final DeferredItem<Item> SMOKED_SALMON_FILLETS =
            material("smoked_salmon_fillets");
    public static final DeferredItem<Item> SMOKED_MUSHROOM_SLICES =
            material("smoked_mushroom_slices");
    public static final DeferredItem<Item> MEASURED_APPLE_DRINK =
            material("measured_apple_drink");
    public static final DeferredItem<Item> MEASURED_GRAPE_DRINK =
            material("measured_grape_drink");
    public static final DeferredItem<Item> MEASURED_COCOA_BASE =
            material("measured_cocoa_base");
    public static final DeferredItem<Item> MEASURED_PUMPKIN_BASE =
            material("measured_pumpkin_base");
    public static final DeferredItem<Item> MEASURED_CARROT_DRINK =
            material("measured_carrot_drink");
    public static final DeferredItem<Item> CLEAR_MUSHROOM_STOCK =
            material("clear_mushroom_stock");
    public static final DeferredItem<Item> CLEAR_COD_STOCK =
            material("clear_cod_stock");
    public static final DeferredItem<Item> CLEAR_ONION_STOCK =
            material("clear_onion_stock");
    public static final DeferredItem<Item> SILKY_PUMPKIN_SOUP =
            material("silky_pumpkin_soup");
    public static final DeferredItem<Item> CREAMY_POTATO_SOUP =
            material("creamy_potato_soup");
    public static final DeferredItem<Item> HERB_BEEF_WRAP =
            food("herb_beef_wrap", 8, 0.75F);
    public static final DeferredItem<Item> SMOKED_CHICKEN_SALAD =
            food("smoked_chicken_salad", 8, 0.75F);
    public static final DeferredItem<Item> SMOKED_COD_RICE =
            food("smoked_cod_rice", 8, 0.80F);
    public static final DeferredItem<Item> SALMON_APPLE_PLATE =
            food("salmon_apple_plate", 8, 0.75F);
    public static final DeferredItem<Item> SMOKED_MUSHROOM_BOWL =
            food("smoked_mushroom_bowl", 7, 0.70F);
    public static final DeferredItem<Item> GOLDEN_HASH_BROWNS =
            food("golden_hash_browns", 7, 0.75F);
    public static final DeferredItem<Item> BEETROOT_RIBBON_SALAD =
            food("beetroot_ribbon_salad", 6, 0.60F);
    public static final DeferredItem<Item> PUMPKIN_SHRED_FRITTERS =
            food("pumpkin_shred_fritters", 7, 0.70F);
    public static final DeferredItem<Item> APPLE_CINNAMON_PORRIDGE =
            food("apple_cinnamon_porridge", 7, 0.70F);
    public static final DeferredItem<Item> POTATO_WEDGE_PLATTER =
            food("potato_wedge_platter", 8, 0.75F);
    public static final DeferredItem<Item> APPLE_MEASURE_CUP =
            food("apple_measure_cup", 5, 0.55F);
    public static final DeferredItem<Item> GRAPE_MEASURE_CUP =
            food("grape_measure_cup", 5, 0.55F);
    public static final DeferredItem<Item> COCOA_MORNING_CAKE =
            food("cocoa_morning_cake", 7, 0.70F);
    public static final DeferredItem<Item> PUMPKIN_BREAKFAST_CAKE =
            food("pumpkin_breakfast_cake", 7, 0.70F);
    public static final DeferredItem<Item> CARROT_SUNRISE_DRINK =
            food("carrot_sunrise_drink", 5, 0.55F);
    public static final DeferredItem<Item> MUSHROOM_CLEAR_SOUP =
            food("mushroom_clear_soup", 7, 0.70F);
    public static final DeferredItem<Item> COD_CLEAR_NOODLE_SOUP =
            food("cod_clear_noodle_soup", 8, 0.80F);
    public static final DeferredItem<Item> ONION_CLEAR_STEW =
            food("onion_clear_stew", 7, 0.70F);
    public static final DeferredItem<Item> SILKY_PUMPKIN_BOWL =
            food("silky_pumpkin_bowl", 7, 0.75F);
    public static final DeferredItem<Item> CREAMY_POTATO_POT =
            food("creamy_potato_pot", 8, 0.80F);
    private static DeferredItem<Item> material(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }

    private static DeferredItem<Item> food(String id, int nutrition, float saturation) {
        return ITEMS.register(id, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition)
                        .saturationModifier(saturation).build())));
    }
}
