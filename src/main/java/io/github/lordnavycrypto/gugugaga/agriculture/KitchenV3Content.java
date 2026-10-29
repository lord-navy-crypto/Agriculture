package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Kitchen v3 content: entirely additive, preserving all earlier registry IDs. */
public final class KitchenV3Content {
    private KitchenV3Content() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<KitchenV3AnimatedBlock> DOUGH_BENCH =
            station("dough_bench", KitchenProcessingBlock.Kind.KNEAD);
    public static final DeferredBlock<KitchenV3AnimatedBlock> SKILLET_STATION =
            station("skillet_station", KitchenProcessingBlock.Kind.FRY);
    public static final DeferredBlock<KitchenV3AnimatedBlock> BAMBOO_STEAMER =
            station("bamboo_steamer", KitchenProcessingBlock.Kind.STEAM);
    public static final DeferredItem<Item> DOUGH_BENCH_ITEM =
            ITEMS.register("dough_bench", () -> new BlockItem(DOUGH_BENCH.get(), new Item.Properties()));
    public static final DeferredItem<Item> SKILLET_STATION_ITEM =
            ITEMS.register("skillet_station", () -> new BlockItem(SKILLET_STATION.get(), new Item.Properties()));
    public static final DeferredItem<Item> BAMBOO_STEAMER_ITEM =
            ITEMS.register("bamboo_steamer", () -> new BlockItem(BAMBOO_STEAMER.get(), new Item.Properties()));

    public static final DeferredItem<Item> ROLLING_PIN = material("rolling_pin");
    public static final DeferredItem<Item> FRYING_SPATULA = material("frying_spatula");
    public static final DeferredItem<Item> STEAMER_LID = material("steamer_lid");
    public static final DeferredItem<Item> CHEF_KNIFE = material("chef_knife");
    public static final DeferredItem<Item> WIRE_WHISK = material("wire_whisk");
    public static final DeferredItem<Item> SOUP_LADLE = material("soup_ladle");
    public static final DeferredItem<Item> KITCHEN_TONGS = material("kitchen_tongs");
    public static final DeferredItem<Item> KITCHEN_STRAINER = material("kitchen_strainer");
    public static final DeferredItem<Item> BOX_GRATER = material("box_grater");
    public static final DeferredItem<Item> MORTAR_PESTLE = material("mortar_pestle");
    public static final DeferredItem<Item> MEASURING_CUP = material("measuring_cup");
    public static final DeferredItem<Item> SKIMMER_SPOON = material("skimmer_spoon");
    public static final DeferredItem<Item> WOODEN_BOWL = material("wooden_bowl");
    public static final DeferredItem<Item> CERAMIC_BOWL = material("ceramic_bowl");
    public static final DeferredItem<Item> SOUP_BOWL = material("soup_bowl");
    public static final DeferredItem<Item> SERVING_PLATTER = material("serving_platter");
    public static final DeferredItem<Item> BREAD_BASKET = material("bread_basket");
    public static final DeferredItem<Item> GLAZED_TEA_CUP = material("glazed_tea_cup");
    public static final DeferredItem<Item> GLASS_TUMBLER = material("glass_tumbler");
    public static final DeferredItem<Item> DESSERT_PLATE = material("dessert_plate");
    public static final DeferredItem<Item> LUNCH_BOX = material("lunch_box");
    public static final DeferredItem<Item> SAUCE_DISH = material("sauce_dish");
    public static final DeferredItem<Item> BAKING_TRAY = material("baking_tray");
    public static final DeferredItem<Item> BAMBOO_STEAMER_BASKET = material("bamboo_steamer_basket");
    public static final DeferredItem<Item> BREAD_DOUGH = material("bread_dough");
    public static final DeferredItem<Item> RICE_DUMPLING_DOUGH = material("rice_dumpling_dough");
    public static final DeferredItem<Item> BUCKWHEAT_NOODLE_DOUGH = material("buckwheat_noodle_dough");
    public static final DeferredItem<Item> SESAME_BUN_DOUGH = material("sesame_bun_dough");
    public static final DeferredItem<Item> PIZZA_BASE = material("pizza_base");
    public static final DeferredItem<Item> COCOA_COOKIE_DOUGH = material("cocoa_cookie_dough");
    public static final DeferredItem<Item> BRAN_CRACKER_DOUGH = material("bran_cracker_dough");
    public static final DeferredItem<Item> PUMPKIN_PASTRY_DOUGH = material("pumpkin_pastry_dough");
    public static final DeferredItem<Item> CRISPY_POTATO_HASH = material("crispy_potato_hash");
    public static final DeferredItem<Item> CARAMELIZED_ONIONS = material("caramelized_onions");
    public static final DeferredItem<Item> SAUTEED_MUSHROOMS = material("sauteed_mushrooms");
    public static final DeferredItem<Item> SEARED_RED_MUSHROOMS = material("seared_red_mushrooms");
    public static final DeferredItem<Item> GLAZED_CARROTS = material("glazed_carrots");
    public static final DeferredItem<Item> PAN_ROASTED_TOMATO = material("pan_roasted_tomato");
    public static final DeferredItem<Item> SEARED_SMOKED_BEEF = material("seared_smoked_beef");
    public static final DeferredItem<Item> TOASTED_SEAWEED = material("toasted_seaweed");
    public static final DeferredItem<Item> STEAMED_RICE = material("steamed_rice");
    public static final DeferredItem<Item> STEAMED_PUMPKIN = material("steamed_pumpkin");
    public static final DeferredItem<Item> STEAMED_CABBAGE = material("steamed_cabbage");
    public static final DeferredItem<Item> STEAMED_POTATO = material("steamed_potato");
    public static final DeferredItem<Item> STEAMED_CARROT = material("steamed_carrot");
    public static final DeferredItem<Item> STEAMED_BEETROOT = material("steamed_beetroot");
    public static final DeferredItem<Item> STEAMED_ONION = material("steamed_onion");
    public static final DeferredItem<Item> STEAMED_MUSHROOMS = material("steamed_mushrooms");
    public static final DeferredItem<Item> TOMATO_FLATBREAD_PIZZA = food("tomato_flatbread_pizza", 8, 0.75F);
    public static final DeferredItem<Item> HANDMADE_RICE_DUMPLINGS = food("handmade_rice_dumplings", 7, 0.75F);
    public static final DeferredItem<Item> BUCKWHEAT_MUSHROOM_NOODLES = food("buckwheat_mushroom_noodles", 8, 0.80F);
    public static final DeferredItem<Item> SESAME_STEAMED_BUNS = food("sesame_steamed_buns", 7, 0.70F);
    public static final DeferredItem<Item> PUMPKIN_PASTRY = food("pumpkin_pastry", 7, 0.70F);
    public static final DeferredItem<Item> COCOA_CRUNCH_COOKIES = food("cocoa_crunch_cookies", 6, 0.55F);
    public static final DeferredItem<Item> HEARTY_POTATO_PLATE = food("hearty_potato_plate", 8, 0.75F);
    public static final DeferredItem<Item> CREAMY_MUSHROOM_BOWL = food("creamy_mushroom_bowl", 7, 0.75F);
    public static final DeferredItem<Item> WARM_CARROT_SALAD = food("warm_carrot_salad", 6, 0.60F);
    public static final DeferredItem<Item> VEGETABLE_STEAMED_RICE = food("vegetable_steamed_rice", 8, 0.80F);
    public static final DeferredItem<Item> BEETROOT_GARDEN_SOUP = food("beetroot_garden_soup", 7, 0.75F);
    public static final DeferredItem<Item> SMOKED_BEEF_PANINI = food("smoked_beef_panini", 8, 0.80F);
    public static final DeferredItem<Item> TOASTED_SEAWEED_RICE = food("toasted_seaweed_rice", 8, 0.80F);
    public static final DeferredItem<Item> SWEET_BERRY_BRAN_BISCUITS = food("sweet_berry_bran_biscuits", 6, 0.60F);
    public static final DeferredItem<Item> RED_MUSHROOM_WARM_SOUP = food("red_mushroom_warm_soup", 7, 0.65F);
    public static final DeferredItem<Item> ONION_VEGETABLE_PIE = food("onion_vegetable_pie", 8, 0.80F);
    public static final DeferredItem<Item> COUNTRY_BREAD_BASKET = food("country_bread_basket", 7, 0.70F);
    public static final DeferredItem<Item> GARDEN_LUNCH_TRAY = food("garden_lunch_tray", 8, 0.80F);

    private static DeferredBlock<KitchenV3AnimatedBlock> station(
            String id, KitchenProcessingBlock.Kind kind) {
        return BLOCKS.register(id, () -> new KitchenV3AnimatedBlock(kind,
                BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                        .strength(1.8F).sound(SoundType.WOOD).noOcclusion()));
    }

    private static DeferredItem<Item> material(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }

    private static DeferredItem<Item> food(String id, int nutrition, float saturation) {
        return ITEMS.register(id, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition)
                        .saturationModifier(saturation).build())));
    }
}
