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

/** Reusable manual processing equipment, intermediates, dining ware, and dishes. */
public final class KitchenContent {
    private KitchenContent() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<KitchenProcessingBlock> CHOPPING_BOARD = station(
            "chopping_board", KitchenProcessingBlock.Kind.CHOP, MapColor.WOOD, SoundType.WOOD);
    public static final DeferredBlock<KitchenProcessingBlock> HAND_MILL = station(
            "hand_mill", KitchenProcessingBlock.Kind.MILL, MapColor.STONE, SoundType.STONE);
    public static final DeferredBlock<KitchenProcessingBlock> FRUIT_PRESS = station(
            "fruit_press", KitchenProcessingBlock.Kind.PRESS, MapColor.WOOD, SoundType.WOOD);
    public static final DeferredBlock<KitchenProcessingBlock> MIXING_BASIN = station(
            "mixing_basin", KitchenProcessingBlock.Kind.MIX, MapColor.TERRACOTTA_LIGHT_GRAY, SoundType.STONE);
    public static final DeferredBlock<KitchenProcessingBlock> SMOKING_RACK = station(
            "smoking_rack", KitchenProcessingBlock.Kind.SMOKE, MapColor.WOOD, SoundType.WOOD);

    public static final DeferredItem<Item> CHOPPING_BOARD_ITEM = stationItem("chopping_board", CHOPPING_BOARD);
    public static final DeferredItem<Item> HAND_MILL_ITEM = stationItem("hand_mill", HAND_MILL);
    public static final DeferredItem<Item> FRUIT_PRESS_ITEM = stationItem("fruit_press", FRUIT_PRESS);
    public static final DeferredItem<Item> MIXING_BASIN_ITEM = stationItem("mixing_basin", MIXING_BASIN);
    public static final DeferredItem<Item> SMOKING_RACK_ITEM = stationItem("smoking_rack", SMOKING_RACK);

    // Tableware: the wooden spoon is required in the offhand to operate the mixer.
    public static final DeferredItem<Item> WOODEN_SPOON = material("wooden_spoon");
    public static final DeferredItem<Item> CERAMIC_PLATE = material("ceramic_plate");

    // Cutting board
    public static final DeferredItem<Item> DICED_TOMATO = material("diced_tomato");
    public static final DeferredItem<Item> CHOPPED_ONION = material("chopped_onion");
    public static final DeferredItem<Item> SLICED_CUCUMBER = material("sliced_cucumber");
    public static final DeferredItem<Item> SHREDDED_CABBAGE = material("shredded_cabbage");

    // Hand mill
    public static final DeferredItem<Item> RICE_FLOUR = material("rice_flour");
    public static final DeferredItem<Item> BUCKWHEAT_FLOUR = material("buckwheat_flour");
    public static final DeferredItem<Item> WHEAT_FLOUR = material("wheat_flour");
    public static final DeferredItem<Item> SESAME_MEAL = material("sesame_meal");

    // Fruit press
    public static final DeferredItem<Item> GRAPE_JUICE = material("grape_juice");
    public static final DeferredItem<Item> STRAWBERRY_JUICE = material("strawberry_juice");
    public static final DeferredItem<Item> PINEAPPLE_JUICE = material("pineapple_juice");
    public static final DeferredItem<Item> TOMATO_JUICE = material("tomato_juice");

    // Mixing basin
    public static final DeferredItem<Item> TOMATO_PUREE = material("tomato_puree");
    public static final DeferredItem<Item> CUCUMBER_RELISH = material("cucumber_relish");
    public static final DeferredItem<Item> BERRY_COMPOTE_BASE = material("berry_compote_base");
    public static final DeferredItem<Item> FLATBREAD_DOUGH = material("flatbread_dough");

    // New recipes consume prepared ingredients, not merely raw crops.
    public static final DeferredItem<Item> TOMATO_BASIL_BRUSCHETTA = food("tomato_basil_bruschetta", 6, 0.6F);
    public static final DeferredItem<Item> CUCUMBER_ONION_SALAD = food("cucumber_onion_salad", 5, 0.5F);
    public static final DeferredItem<Item> CABBAGE_RICE_BOWL = food("cabbage_rice_bowl", 7, 0.7F);
    public static final DeferredItem<Item> BUCKWHEAT_FLATBREAD = food("buckwheat_flatbread", 6, 0.65F);
    public static final DeferredItem<Item> SESAME_VEGETABLE_NOODLES = food("sesame_vegetable_noodles", 7, 0.7F);
    public static final DeferredItem<Item> BERRY_BREAKFAST_BOWL = food("berry_breakfast_bowl", 6, 0.65F);
    public static final DeferredItem<Item> PINEAPPLE_GLAZED_RICE = food("pineapple_glazed_rice", 7, 0.7F);
    public static final DeferredItem<Item> GRAPE_FRUIT_CUP = food("grape_fruit_cup", 5, 0.55F);
    public static final DeferredItem<Item> TOMATO_VEGETABLE_SOUP = food("tomato_vegetable_soup", 6, 0.65F);
    public static final DeferredItem<Item> CUCUMBER_PICKLE_PLATE = food("cucumber_pickle_plate", 5, 0.55F);
    public static final DeferredItem<Item> FOREST_BERRY_PANCAKES = food("forest_berry_pancakes", 7, 0.7F);
    public static final DeferredItem<Item> RUSTIC_FLATBREAD = food("rustic_flatbread", 7, 0.7F);

    // v2.9: Vanilla food and material preparation with an actual campfire smoke gate.
    public static final DeferredItem<Item> CARROT_RIBBONS = material("carrot_ribbons");
    public static final DeferredItem<Item> DICED_POTATO = material("diced_potato");
    public static final DeferredItem<Item> GRATED_BEETROOT = material("grated_beetroot");
    public static final DeferredItem<Item> PUMPKIN_CHUNKS = material("pumpkin_chunks");
    public static final DeferredItem<Item> SLICED_BROWN_MUSHROOM = material("sliced_brown_mushroom");
    public static final DeferredItem<Item> SLICED_RED_MUSHROOM = material("sliced_red_mushroom");
    public static final DeferredItem<Item> MELON_CUBES = material("melon_cubes");
    public static final DeferredItem<Item> CHOPPED_SWEET_BERRIES = material("chopped_sweet_berries");
    public static final DeferredItem<Item> COCOA_POWDER = material("cocoa_powder");
    public static final DeferredItem<Item> PUMPKIN_SEED_MEAL = material("pumpkin_seed_meal");
    public static final DeferredItem<Item> MELON_SEED_MEAL = material("melon_seed_meal");
    public static final DeferredItem<Item> KELP_POWDER = material("kelp_powder");
    public static final DeferredItem<Item> WHEAT_BRAN = material("wheat_bran");
    public static final DeferredItem<Item> APPLE_JUICE = material("apple_juice");
    public static final DeferredItem<Item> MELON_JUICE = material("melon_juice");
    public static final DeferredItem<Item> SWEET_BERRY_JUICE = material("sweet_berry_juice");
    public static final DeferredItem<Item> GLOW_BERRY_JUICE = material("glow_berry_juice");
    public static final DeferredItem<Item> CARROT_JUICE = material("carrot_juice");
    public static final DeferredItem<Item> BEETROOT_JUICE = material("beetroot_juice");
    public static final DeferredItem<Item> APPLE_SYRUP = material("apple_syrup");
    public static final DeferredItem<Item> CARROT_SLAW = material("carrot_slaw");
    public static final DeferredItem<Item> PUMPKIN_PUREE = material("pumpkin_puree");
    public static final DeferredItem<Item> COCOA_MIX = material("cocoa_mix");
    public static final DeferredItem<Item> SWEET_BERRY_COMPOTE = material("sweet_berry_compote");
    public static final DeferredItem<Item> KELP_PASTE = material("kelp_paste");
    public static final DeferredItem<Item> SMOKED_COD = food("smoked_cod", 5, 0.65F);
    public static final DeferredItem<Item> SMOKED_SALMON = food("smoked_salmon", 7, 0.65F);
    public static final DeferredItem<Item> SMOKED_BEEF = food("smoked_beef", 7, 0.65F);
    public static final DeferredItem<Item> SMOKED_CHICKEN = food("smoked_chicken", 7, 0.65F);
    public static final DeferredItem<Item> APPLE_OAT_CRUMBLE = food("apple_oat_crumble", 6, 0.60F);
    public static final DeferredItem<Item> PUMPKIN_MEAT_STEW = food("pumpkin_meat_stew", 8, 0.80F);
    public static final DeferredItem<Item> APPLE_GLAZED_PORK = food("apple_glazed_pork", 8, 0.75F);
    public static final DeferredItem<Item> MUSHROOM_EGG_TOAST = food("mushroom_egg_toast", 7, 0.70F);
    public static final DeferredItem<Item> KELP_SALMON_RICE = food("kelp_salmon_rice", 8, 0.80F);
    public static final DeferredItem<Item> CRUNCHY_CARROT_SALAD = food("crunchy_carrot_salad", 6, 0.65F);
    public static final DeferredItem<Item> BEETROOT_POTATO_SOUP = food("beetroot_potato_soup", 7, 0.70F);
    public static final DeferredItem<Item> BERRY_JAM_TOAST = food("berry_jam_toast", 6, 0.65F);
    public static final DeferredItem<Item> COCOA_OAT_BITES = food("cocoa_oat_bites", 5, 0.55F);
    public static final DeferredItem<Item> PUMPKIN_SEED_CRACKERS = food("pumpkin_seed_crackers", 5, 0.55F);
    public static final DeferredItem<Item> MELON_APPLE_SALAD = food("melon_apple_salad", 5, 0.55F);
    public static final DeferredItem<Item> CARROT_CHICKEN_BOWL = food("carrot_chicken_bowl", 8, 0.75F);
    public static final DeferredItem<Item> GLOW_BERRY_CUSTARD = food("glow_berry_custard", 6, 0.65F);
    public static final DeferredItem<Item> HONEY_SALMON_PLATE = food("honey_salmon_plate", 8, 0.80F);
    public static final DeferredItem<Item> RED_MUSHROOM_STEW = food("red_mushroom_stew", 6, 0.65F);
    public static final DeferredItem<Item> MELON_SEED_BREAD = food("melon_seed_bread", 7, 0.70F);
    public static final DeferredItem<Item> SMOKED_SALMON_RICE = food("smoked_salmon_rice", 8, 0.85F);
    public static final DeferredItem<Item> SMOKED_BEEF_HASH = food("smoked_beef_hash", 8, 0.85F);
    public static final DeferredItem<Item> SMOKED_CHICKEN_CARROT_BOWL = food("smoked_chicken_carrot_bowl", 8, 0.80F);
    public static final DeferredItem<Item> SMOKED_COD_PIE = food("smoked_cod_pie", 8, 0.80F);

    private static DeferredBlock<KitchenProcessingBlock> station(
            String name, KitchenProcessingBlock.Kind kind, MapColor color, SoundType sound) {
        return BLOCKS.register(name, () -> new KitchenProcessingBlock(kind,
                BlockBehaviour.Properties.of().mapColor(color).strength(1.5F)
                        .sound(sound).noOcclusion()));
    }

    private static DeferredItem<Item> stationItem(
            String name, DeferredBlock<? extends Block> station) {
        return ITEMS.register(name, () -> new BlockItem(station.get(), new Item.Properties()));
    }

    private static DeferredItem<Item> material(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static DeferredItem<Item> food(String name, int nutrition, float saturation) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(nutrition).saturationModifier(saturation).build())));
    }
}
