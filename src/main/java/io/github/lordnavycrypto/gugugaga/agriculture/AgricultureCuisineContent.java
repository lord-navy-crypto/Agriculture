package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Cooked dishes and processed cooking ingredients made from the agriculture crop catalog. */
public final class AgricultureCuisineContent {
    private AgricultureCuisineContent() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredItem<Item> GARDEN_SALAD = food("garden_salad", 5, 0.50F);
    public static final DeferredItem<Item> VEGETABLE_STEW = food("vegetable_stew", 6, 0.65F);
    public static final DeferredItem<Item> ROASTED_ROOT_VEGETABLES = food("roasted_root_vegetables", 6, 0.65F);
    public static final DeferredItem<Item> CROP_STIR_FRY = food("crop_stir_fry", 6, 0.65F);
    public static final DeferredItem<Item> GRAIN_PORRIDGE = food("grain_porridge", 5, 0.55F);
    public static final DeferredItem<Item> BARLEY_VEGETABLE_SOUP = food("barley_vegetable_soup", 6, 0.65F);
    public static final DeferredItem<Item> RICE_VEGETABLE_BOWL = food("rice_vegetable_bowl", 7, 0.75F);
    public static final DeferredItem<Item> BEAN_STEW = food("bean_stew", 7, 0.75F);
    public static final DeferredItem<Item> STUFFED_PEPPER = food("stuffed_pepper", 7, 0.75F);
    public static final DeferredItem<Item> EGGPLANT_CURRY = food("eggplant_curry", 7, 0.75F);
    public static final DeferredItem<Item> PUMPKIN_SOUP = food("pumpkin_soup", 6, 0.65F);
    public static final DeferredItem<Item> BERRY_PIE = food("berry_pie", 7, 0.75F);
    public static final DeferredItem<Item> FRUIT_SALAD = food("fruit_salad", 5, 0.60F);
    public static final DeferredItem<Item> CORNBREAD = food("cornbread", 6, 0.65F);
    public static final DeferredItem<Item> CABBAGE_ROLLS = food("cabbage_rolls", 7, 0.75F);
    public static final DeferredItem<Item> TOMATO_SAUCE = food("tomato_sauce", 4, 0.45F);
    public static final DeferredItem<Item> HERB_BREAD = food("herb_bread", 5, 0.55F);
    public static final DeferredItem<Item> SWEET_POTATO_ROAST = food("sweet_potato_roast", 6, 0.65F);
    public static final DeferredItem<Item> CASSAVA_FLATBREAD = food("cassava_flatbread", 6, 0.65F);
    public static final DeferredItem<Item> TARO_DUMPLINGS = food("taro_dumplings", 6, 0.65F);
    public static final DeferredItem<Item> YAM_CAKES = food("yam_cakes", 6, 0.65F);
    public static final DeferredItem<Item> CHICKPEA_HUMMUS = food("chickpea_hummus", 5, 0.55F);
    public static final DeferredItem<Item> PEANUT_SAUCE = food("peanut_sauce", 4, 0.45F);
    public static final DeferredItem<Item> ROASTED_SEED_MIX = food("roasted_seed_mix", 5, 0.55F);
    public static final DeferredItem<Item> HERBAL_TEA = food("herbal_tea", 3, 0.35F);
    public static final DeferredItem<Item> TEA_CUP = food("tea_cup", 3, 0.35F);
    public static final DeferredItem<Item> COFFEE_CUP = food("coffee_cup", 4, 0.45F);
    public static final DeferredItem<Item> HONEY_MUSTARD_DRESSING = food("honey_mustard_dressing", 3, 0.35F);
    public static final DeferredItem<Item> FARMERS_SKILLET = food("farmers_skillet", 7, 0.75F);
    public static final DeferredItem<Item> BUCKWHEAT_PANCAKES = food("buckwheat_pancakes", 6, 0.65F);
    public static final DeferredItem<Item> QUINOA_SALAD = food("quinoa_salad", 6, 0.65F);
    public static final DeferredItem<Item> LENTIL_CURRY = food("lentil_curry", 7, 0.75F);
    public static final DeferredItem<Item> SWEET_CORN_FRITTERS = food("sweet_corn_fritters", 6, 0.65F);
    public static final DeferredItem<Item> TOMATO_EGG_STIR_FRY = food("tomato_egg_stir_fry", 6, 0.65F);
    public static final DeferredItem<Item> BASIL_TOMATO_FLATBREAD = food("basil_tomato_flatbread", 6, 0.65F);
    public static final DeferredItem<Item> PUMPKIN_RICE = food("pumpkin_rice", 7, 0.75F);
    public static final DeferredItem<Item> SPINACH_OMELETTE = food("spinach_omelette", 6, 0.65F);
    public static final DeferredItem<Item> OAT_BERRY_PORRIDGE = food("oat_berry_porridge", 6, 0.65F);
    public static final DeferredItem<Item> CHILI_BEAN_CHILI = food("chili_bean_chili", 7, 0.75F);
    public static final DeferredItem<Item> SESAME_NOODLES = food("sesame_noodles", 7, 0.75F);
    public static final DeferredItem<Item> GARLIC_ROASTED_POTATOES = food("garlic_roasted_potatoes", 6, 0.65F);
    public static final DeferredItem<Item> BEETROOT_SOUP = food("beetroot_soup", 6, 0.65F);
    public static final DeferredItem<Item> SEAWEED_MUSHROOM_SOUP = food("seaweed_mushroom_soup", 6, 0.65F);
    public static final DeferredItem<Item> FAVA_BEAN_SOUP = food("fava_bean_soup", 6, 0.65F);
    public static final DeferredItem<Item> CHAYOTE_STIR_FRY = food("chayote_stir_fry", 6, 0.65F);
    public static final DeferredItem<Item> HONEY_BERRY_TOAST = food("honey_berry_toast", 6, 0.65F);
    public static final DeferredItem<Item> SOYBEAN_PATTIES = food("soybean_patties", 7, 0.75F);
    public static final DeferredItem<Item> ROASTED_CHICKPEAS = food("roasted_chickpeas", 5, 0.60F);
    public static final DeferredItem<Item> BAKED_SWEET_CORN = food("baked_sweet_corn", 5, 0.60F);
    public static final DeferredItem<Item> ADZUKI_BEAN_SWEET_SOUP = food("adzuki_bean_sweet_soup", 6, 0.65F);
    public static final DeferredItem<Item> MUNG_BEAN_CONGEE = food("mung_bean_congee", 6, 0.65F);
    public static final DeferredItem<Item> NAPA_CABBAGE_BRAISE = food("napa_cabbage_braise", 6, 0.65F);
    public static final DeferredItem<Item> GARLIC_WATER_SPINACH = food("garlic_water_spinach", 6, 0.65F);
    public static final DeferredItem<Item> TOMATO_OKRA_STEW = food("tomato_okra_stew", 6, 0.65F);
    public static final DeferredItem<Item> JUTE_MALLOW_SOUP = food("jute_mallow_soup", 6, 0.65F);
    public static final DeferredItem<Item> PINEAPPLE_TART = food("pineapple_tart", 6, 0.65F);
    public static final DeferredItem<Item> GRAPE_BERRY_COMPOTE = food("grape_berry_compote", 5, 0.55F);
    public static final DeferredItem<Item> RAPINI_GARLIC_SAUTE = food("rapini_garlic_saute", 6, 0.65F);
    public static final DeferredItem<Item> ROOT_VEGETABLE_HASH = food("root_vegetable_hash", 7, 0.75F);
    public static final DeferredItem<Item> MILLET_FENNEL_PILAF = food("millet_fennel_pilaf", 7, 0.75F);
    public static final DeferredItem<Item> MUSHROOM_BARLEY_RISOTTO = food("mushroom_barley_risotto", 7, 0.75F);
    public static final DeferredItem<Item> CUCUMBER_MINT_SALAD = food("cucumber_mint_salad", 5, 0.55F);
    public static final DeferredItem<Item> SEAWEED_POTATO_CHOWDER = food("seaweed_potato_chowder", 7, 0.75F);
    public static final DeferredItem<Item> BLACKCURRANT_OAT_CRUMBLE = food("blackcurrant_oat_crumble", 6, 0.65F);
    public static final DeferredItem<Item> BAKED_TARO = food("baked_taro", 5, 0.60F);
    public static final DeferredItem<Item> ROASTED_YAM = food("roasted_yam", 5, 0.60F);
    public static final DeferredItem<Item> CANOLA_OIL = material("canola_oil");
    public static final DeferredItem<Item> SAFFLOWER_OIL = material("safflower_oil");

    private static DeferredItem<Item> food(String name, int nutrition, float saturation) {
        FoodProperties properties = new FoodProperties.Builder()
                .nutrition(nutrition).saturationModifier(saturation).build();
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(properties)));
    }

    private static DeferredItem<Item> material(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(GARDEN_SALAD.get());
            event.accept(VEGETABLE_STEW.get());
            event.accept(ROASTED_ROOT_VEGETABLES.get());
            event.accept(CROP_STIR_FRY.get());
            event.accept(GRAIN_PORRIDGE.get());
            event.accept(BARLEY_VEGETABLE_SOUP.get());
            event.accept(RICE_VEGETABLE_BOWL.get());
            event.accept(BEAN_STEW.get());
            event.accept(STUFFED_PEPPER.get());
            event.accept(EGGPLANT_CURRY.get());
            event.accept(PUMPKIN_SOUP.get());
            event.accept(BERRY_PIE.get());
            event.accept(FRUIT_SALAD.get());
            event.accept(CORNBREAD.get());
            event.accept(CABBAGE_ROLLS.get());
            event.accept(TOMATO_SAUCE.get());
            event.accept(HERB_BREAD.get());
            event.accept(SWEET_POTATO_ROAST.get());
            event.accept(CASSAVA_FLATBREAD.get());
            event.accept(TARO_DUMPLINGS.get());
            event.accept(YAM_CAKES.get());
            event.accept(CHICKPEA_HUMMUS.get());
            event.accept(PEANUT_SAUCE.get());
            event.accept(ROASTED_SEED_MIX.get());
            event.accept(HERBAL_TEA.get());
            event.accept(TEA_CUP.get());
            event.accept(COFFEE_CUP.get());
            event.accept(HONEY_MUSTARD_DRESSING.get());
            event.accept(FARMERS_SKILLET.get());
            event.accept(BUCKWHEAT_PANCAKES.get());
            event.accept(QUINOA_SALAD.get());
            event.accept(LENTIL_CURRY.get());
            event.accept(SWEET_CORN_FRITTERS.get());
            event.accept(TOMATO_EGG_STIR_FRY.get());
            event.accept(BASIL_TOMATO_FLATBREAD.get());
            event.accept(PUMPKIN_RICE.get());
            event.accept(SPINACH_OMELETTE.get());
            event.accept(OAT_BERRY_PORRIDGE.get());
            event.accept(CHILI_BEAN_CHILI.get());
            event.accept(SESAME_NOODLES.get());
            event.accept(GARLIC_ROASTED_POTATOES.get());
            event.accept(BEETROOT_SOUP.get());
            event.accept(SEAWEED_MUSHROOM_SOUP.get());
            event.accept(FAVA_BEAN_SOUP.get());
            event.accept(CHAYOTE_STIR_FRY.get());
            event.accept(HONEY_BERRY_TOAST.get());
            event.accept(SOYBEAN_PATTIES.get());
            event.accept(ROASTED_CHICKPEAS.get());
            event.accept(BAKED_SWEET_CORN.get());
            event.accept(ADZUKI_BEAN_SWEET_SOUP.get());
            event.accept(MUNG_BEAN_CONGEE.get());
            event.accept(NAPA_CABBAGE_BRAISE.get());
            event.accept(GARLIC_WATER_SPINACH.get());
            event.accept(TOMATO_OKRA_STEW.get());
            event.accept(JUTE_MALLOW_SOUP.get());
            event.accept(PINEAPPLE_TART.get());
            event.accept(GRAPE_BERRY_COMPOTE.get());
            event.accept(RAPINI_GARLIC_SAUTE.get());
            event.accept(ROOT_VEGETABLE_HASH.get());
            event.accept(MILLET_FENNEL_PILAF.get());
            event.accept(MUSHROOM_BARLEY_RISOTTO.get());
            event.accept(CUCUMBER_MINT_SALAD.get());
            event.accept(SEAWEED_POTATO_CHOWDER.get());
            event.accept(BLACKCURRANT_OAT_CRUMBLE.get());
            event.accept(BAKED_TARO.get());
            event.accept(ROASTED_YAM.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(CANOLA_OIL.get());
            event.accept(SAFFLOWER_OIL.get());
        }
    }
}
