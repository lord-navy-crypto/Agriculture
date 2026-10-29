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

/** Additive seasonings, salt geology, and an aging crock for food culture. */
public final class FoodV34Content {
    private FoodV34Content() {}
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<Block> SALT_DEPOSIT =
            BLOCKS.register("salt_deposit", () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ).strength(1.5F).sound(SoundType.CALCITE)));
    public static final DeferredItem<BlockItem> SALT_DEPOSIT_ITEM =
            ITEMS.register("salt_deposit", () -> new BlockItem(SALT_DEPOSIT.get(), new Item.Properties()));

    public static final DeferredBlock<Block> SALT_BLOCK =
            BLOCKS.register("salt_block", () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.QUARTZ).strength(1.1F).sound(SoundType.CALCITE)));
    public static final DeferredItem<BlockItem> SALT_BLOCK_ITEM =
            ITEMS.register("salt_block", () -> new BlockItem(SALT_BLOCK.get(), new Item.Properties()));

    public static final DeferredBlock<SeasoningAgingCrockBlock> SEASONING_CROCK =
            BLOCKS.register("seasoning_crock", () -> new SeasoningAgingCrockBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN)
                            .strength(2.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredItem<BlockItem> SEASONING_CROCK_ITEM =
            ITEMS.register("seasoning_crock", () -> new BlockItem(SEASONING_CROCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> SEA_SALT =
            ingredient("sea_salt");
    public static final DeferredItem<Item> BRINE =
            ingredient("brine");
    public static final DeferredItem<Item> STARTER_CULTURE =
            ingredient("starter_culture");
    public static final DeferredItem<Item> SOY_SAUCE =
            ingredient("soy_sauce");
    public static final DeferredItem<Item> RICE_VINEGAR =
            ingredient("rice_vinegar");
    public static final DeferredItem<Item> APPLE_VINEGAR =
            ingredient("apple_vinegar");
    public static final DeferredItem<Item> MUSHROOM_UMAMI_SAUCE =
            ingredient("mushroom_umami_sauce");
    public static final DeferredItem<Item> PICKLED_CUCUMBER_V34 =
            ingredient("pickled_cucumber_v34");
    public static final DeferredItem<Item> PICKLED_DAIKON_V34 =
            ingredient("pickled_daikon_v34");
    public static final DeferredItem<Item> PICKLED_CABBAGE_V34 =
            ingredient("pickled_cabbage_v34");
    public static final DeferredItem<Item> PICKLED_BEETROOT_V34 =
            ingredient("pickled_beetroot_v34");
    public static final DeferredItem<Item> CULTURED_CHILI_SAUCE =
            ingredient("cultured_chili_sauce");
    public static final DeferredItem<Item> SEASONED_FISH_SAUCE =
            ingredient("seasoned_fish_sauce");
    public static final DeferredItem<Item> HERB_SALT =
            ingredient("herb_salt");
    public static final DeferredItem<Item> SEA_SALT_COD_RICE =
            food("sea_salt_cod_rice", 7, 0.70F);
    public static final DeferredItem<Item> SOY_SAUCE_MUSHROOM_RICE =
            food("soy_sauce_mushroom_rice", 7, 0.70F);
    public static final DeferredItem<Item> RICE_VINEGAR_SALAD =
            food("rice_vinegar_salad", 7, 0.70F);
    public static final DeferredItem<Item> APPLE_VINEGAR_VEGETABLE_BOWL =
            food("apple_vinegar_vegetable_bowl", 7, 0.70F);
    public static final DeferredItem<Item> SPICED_FERMENTED_BEANS =
            food("spiced_fermented_beans", 7, 0.70F);
    public static final DeferredItem<Item> PICKLED_CUCUMBER_RICE_ROLL =
            food("pickled_cucumber_rice_roll", 7, 0.70F);
    public static final DeferredItem<Item> PICKLED_DAIKON_FISH_BOWL =
            food("pickled_daikon_fish_bowl", 7, 0.70F);
    public static final DeferredItem<Item> PICKLED_CABBAGE_NOODLE_BOWL =
            food("pickled_cabbage_noodle_bowl", 7, 0.70F);
    public static final DeferredItem<Item> BEETROOT_PICKLE_PLATE =
            food("beetroot_pickle_plate", 7, 0.70F);
    public static final DeferredItem<Item> CHILI_SOY_NOODLES =
            food("chili_soy_noodles", 7, 0.70F);
    public static final DeferredItem<Item> MUSHROOM_HERB_SOUP =
            food("mushroom_herb_soup", 7, 0.70F);
    public static final DeferredItem<Item> SEASONED_MACKEREL_BOWL =
            food("seasoned_mackerel_bowl", 7, 0.70F);

    private static DeferredItem<Item> ingredient(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }
    private static DeferredItem<Item> food(String id, int nutrition, float sat) {
        return ITEMS.register(id, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition).saturationModifier(sat).build())));
    }
}
