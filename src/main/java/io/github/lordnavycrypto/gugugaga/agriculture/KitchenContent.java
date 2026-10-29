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

    public static final DeferredItem<Item> CHOPPING_BOARD_ITEM = stationItem("chopping_board", CHOPPING_BOARD);
    public static final DeferredItem<Item> HAND_MILL_ITEM = stationItem("hand_mill", HAND_MILL);
    public static final DeferredItem<Item> FRUIT_PRESS_ITEM = stationItem("fruit_press", FRUIT_PRESS);
    public static final DeferredItem<Item> MIXING_BASIN_ITEM = stationItem("mixing_basin", MIXING_BASIN);

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
