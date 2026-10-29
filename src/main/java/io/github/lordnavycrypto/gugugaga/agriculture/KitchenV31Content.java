package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Additive v3.1 specialist kitchen, reusing four existing v3.0 tools. */
public final class KitchenV31Content {
    private KitchenV31Content() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<KitchenV3AnimatedBlock> WHISKING_STATION =
            BLOCKS.register("whisking_station", () -> new KitchenV3AnimatedBlock(
                    KitchenProcessingBlock.Kind.WHISK,
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                            .strength(1.7F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<KitchenV3AnimatedBlock> SPICE_MORTAR_STATION =
            BLOCKS.register("spice_mortar_station", () -> new KitchenV3AnimatedBlock(
                    KitchenProcessingBlock.Kind.MORTAR,
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                            .strength(1.7F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<KitchenV3AnimatedBlock> STRAINING_STATION =
            BLOCKS.register("straining_station", () -> new KitchenV3AnimatedBlock(
                    KitchenProcessingBlock.Kind.STRAIN,
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                            .strength(1.7F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<KitchenV3AnimatedBlock> STOCK_POT =
            BLOCKS.register("stock_pot", () -> new KitchenV3AnimatedBlock(
                    KitchenProcessingBlock.Kind.SIMMER,
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                            .strength(1.7F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredItem<Item> WHISKING_STATION_ITEM =
            ITEMS.register("whisking_station", () -> new BlockItem(WHISKING_STATION.get(), new Item.Properties()));
    public static final DeferredItem<Item> SPICE_MORTAR_STATION_ITEM =
            ITEMS.register("spice_mortar_station", () -> new BlockItem(SPICE_MORTAR_STATION.get(), new Item.Properties()));
    public static final DeferredItem<Item> STRAINING_STATION_ITEM =
            ITEMS.register("straining_station", () -> new BlockItem(STRAINING_STATION.get(), new Item.Properties()));
    public static final DeferredItem<Item> STOCK_POT_ITEM =
            ITEMS.register("stock_pot", () -> new BlockItem(STOCK_POT.get(), new Item.Properties()));

    public static final DeferredItem<Item> BEATEN_EGG =
            material("beaten_egg");
    public static final DeferredItem<Item> WHIPPED_COCOA =
            material("whipped_cocoa");
    public static final DeferredItem<Item> GLOW_BERRY_FROTH =
            material("glow_berry_froth");
    public static final DeferredItem<Item> AERATED_APPLE_MIX =
            material("aerated_apple_mix");
    public static final DeferredItem<Item> WHIPPED_PUMPKIN =
            material("whipped_pumpkin");
    public static final DeferredItem<Item> WHIPPED_BERRY_TOPPING =
            material("whipped_berry_topping");
    public static final DeferredItem<Item> CRUSHED_COCOA_NIBS =
            material("crushed_cocoa_nibs");
    public static final DeferredItem<Item> SESAME_PASTE =
            material("sesame_paste");
    public static final DeferredItem<Item> PUMPKIN_SEED_PASTE =
            material("pumpkin_seed_paste");
    public static final DeferredItem<Item> SEAWEED_SEASONING =
            material("seaweed_seasoning");
    public static final DeferredItem<Item> ONION_PASTE =
            material("onion_paste");
    public static final DeferredItem<Item> BERRY_MASH =
            material("berry_mash");
    public static final DeferredItem<Item> CLEAR_APPLE_JUICE =
            material("clear_apple_juice");
    public static final DeferredItem<Item> CLEAR_GRAPE_JUICE =
            material("clear_grape_juice");
    public static final DeferredItem<Item> STRAINED_TOMATO_JUICE =
            material("strained_tomato_juice");
    public static final DeferredItem<Item> CLEAR_BEET_JUICE =
            material("clear_beet_juice");
    public static final DeferredItem<Item> KELP_BROTH =
            material("kelp_broth");
    public static final DeferredItem<Item> SIFTED_RICE_FLOUR =
            material("sifted_rice_flour");
    public static final DeferredItem<Item> PUMPKIN_SOUP_BASE =
            material("pumpkin_soup_base");
    public static final DeferredItem<Item> MUSHROOM_STOCK =
            material("mushroom_stock");
    public static final DeferredItem<Item> ONION_STOCK =
            material("onion_stock");
    public static final DeferredItem<Item> COD_STOCK =
            material("cod_stock");
    public static final DeferredItem<Item> BEETROOT_SOUP_BASE =
            material("beetroot_soup_base");
    public static final DeferredItem<Item> POTATO_SOUP_BASE =
            material("potato_soup_base");
    public static final DeferredItem<Item> EGG_RICE_BREAKFAST =
            food("egg_rice_breakfast", 7, 0.75F);
    public static final DeferredItem<Item> CHOCOLATE_CREAM_BISCUIT =
            food("chocolate_cream_biscuit", 6, 0.60F);
    public static final DeferredItem<Item> GLOW_BERRY_MOUSSE =
            food("glow_berry_mousse", 6, 0.60F);
    public static final DeferredItem<Item> APPLE_FROTH_CUP =
            food("apple_froth_cup", 5, 0.55F);
    public static final DeferredItem<Item> PUMPKIN_CREAM_PIE =
            food("pumpkin_cream_pie", 7, 0.70F);
    public static final DeferredItem<Item> BERRY_CREAM_TART =
            food("berry_cream_tart", 7, 0.70F);
    public static final DeferredItem<Item> COCOA_SEED_BRITTLE =
            food("cocoa_seed_brittle", 6, 0.55F);
    public static final DeferredItem<Item> SESAME_RICE_BALLS =
            food("sesame_rice_balls", 7, 0.65F);
    public static final DeferredItem<Item> PUMPKIN_SEED_DIP =
            food("pumpkin_seed_dip", 5, 0.55F);
    public static final DeferredItem<Item> KELP_SPICED_COD =
            food("kelp_spiced_cod", 8, 0.80F);
    public static final DeferredItem<Item> ONION_FLATBREAD =
            food("onion_flatbread", 7, 0.70F);
    public static final DeferredItem<Item> BERRY_MASH_BREAD =
            food("berry_mash_bread", 6, 0.65F);
    public static final DeferredItem<Item> CLEAR_APPLE_JELLY =
            food("clear_apple_jelly", 5, 0.55F);
    public static final DeferredItem<Item> GARDEN_TOMATO_BROTH =
            food("garden_tomato_broth", 6, 0.60F);
    public static final DeferredItem<Item> MUSHROOM_RICE_SOUP =
            food("mushroom_rice_soup", 7, 0.75F);
    public static final DeferredItem<Item> BEETROOT_WINTER_STEW =
            food("beetroot_winter_stew", 7, 0.70F);
    public static final DeferredItem<Item> COD_NOODLE_BROTH =
            food("cod_noodle_broth", 8, 0.80F);
    public static final DeferredItem<Item> POTATO_ONION_SOUP =
            food("potato_onion_soup", 7, 0.75F);
    public static final DeferredItem<Item> PUMPKIN_SEASONAL_SOUP =
            food("pumpkin_seasonal_soup", 7, 0.70F);
    public static final DeferredItem<Item> KELP_RICE_SOUP =
            food("kelp_rice_soup", 6, 0.65F);
    public static final DeferredItem<Item> SIFTED_RICE_CAKE =
            food("sifted_rice_cake", 7, 0.65F);
    public static final DeferredItem<Item> CLEAR_GRAPE_CUP =
            food("clear_grape_cup", 5, 0.55F);
    public static final DeferredItem<Item> BEETROOT_GARDEN_SALAD =
            food("beetroot_garden_salad", 6, 0.60F);

    private static DeferredItem<Item> material(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }

    private static DeferredItem<Item> food(String id, int nutrition, float saturation) {
        return ITEMS.register(id, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition)
                        .saturationModifier(saturation).build())));
    }
}
