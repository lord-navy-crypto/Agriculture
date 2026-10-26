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

/** Registry for marine plant foods, seafood dishes, mushroom harvests and renewable fungal cultures. */
public final class MarineFungiContent {
    private MarineFungiContent() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredItem<Item> SEA_LETTUCE = food("sea_lettuce", 2, 0.25F);
    public static final DeferredItem<Item> WAKAME = food("wakame", 2, 0.25F);
    public static final DeferredItem<Item> NORI_SHEET = food("nori_sheet", 2, 0.30F);
    public static final DeferredItem<Item> DULSE = food("dulse", 2, 0.25F);
    public static final DeferredItem<Item> KELP_FLAKES = ingredient("kelp_flakes");
    public static final DeferredItem<Item> FISH_FILLET = food("fish_fillet", 3, 0.35F);
    public static final DeferredItem<Item> SEAFOOD_BROTH = food("seafood_broth", 6, 0.65F);
    public static final DeferredItem<Item> SEAWEED_SALAD = food("seaweed_salad", 5, 0.55F);
    public static final DeferredItem<Item> NORI_RICE_ROLL = food("nori_rice_roll", 6, 0.65F);
    public static final DeferredItem<Item> SEAFOOD_RICE_BOWL = food("seafood_rice_bowl", 8, 0.85F);
    public static final DeferredItem<Item> GRILLED_FISH_SKEWER = food("grilled_fish_skewer", 7, 0.75F);

    public static final DeferredBlock<MarinePlantCropBlock> SEA_LETTUCE_CROP = marineCrop("sea_lettuce_crop");
    public static final DeferredBlock<MarinePlantCropBlock> WAKAME_CROP = marineCrop("wakame_crop");
    public static final DeferredBlock<MarinePlantCropBlock> NORI_CROP = marineCrop("nori_crop");
    public static final DeferredBlock<MarinePlantCropBlock> DULSE_CROP = marineCrop("dulse_crop");
    public static final DeferredItem<BlockItem> SEA_LETTUCE_CROP_ITEM = marineCropItem("sea_lettuce_crop", SEA_LETTUCE_CROP);
    public static final DeferredItem<BlockItem> WAKAME_CROP_ITEM = marineCropItem("wakame_crop", WAKAME_CROP);
    public static final DeferredItem<BlockItem> NORI_CROP_ITEM = marineCropItem("nori_crop", NORI_CROP);
    public static final DeferredItem<BlockItem> DULSE_CROP_ITEM = marineCropItem("dulse_crop", DULSE_CROP);

    public static final DeferredItem<Item> MUSHROOM_SPORES = ingredient("mushroom_spores");
    public static final DeferredItem<Item> SHIITAKE_MUSHROOM = food("shiitake_mushroom", 3, 0.35F);
    public static final DeferredItem<Item> OYSTER_MUSHROOM = food("oyster_mushroom", 2, 0.30F);
    public static final DeferredItem<Item> ENOKI_MUSHROOM = food("enoki_mushroom", 2, 0.25F);
    public static final DeferredItem<Item> PORCINI_MUSHROOM = food("porcini_mushroom", 3, 0.35F);
    public static final DeferredItem<Item> LION_MANE_MUSHROOM = food("lion_mane_mushroom", 3, 0.40F);
    public static final DeferredItem<Item> TRUFFLE = food("truffle", 2, 0.25F);
    public static final DeferredItem<Item> MUSHROOM_MEDLEY = food("mushroom_medley", 6, 0.65F);
    public static final DeferredItem<Item> MUSHROOM_RICE_BOWL = food("mushroom_rice_bowl", 7, 0.80F);
    public static final DeferredItem<Item> ENOKI_SOUP = food("enoki_soup", 5, 0.60F);
    public static final DeferredItem<Item> TRUFFLE_PASTA = food("truffle_pasta", 8, 0.90F);
    public static final DeferredItem<Item> LION_MANE_STEAK = food("lion_mane_steak", 8, 0.90F);
    public static final DeferredItem<Item> MUSHROOM_SAUCE = food("mushroom_sauce", 4, 0.50F);

    public static final DeferredBlock<FungalCultureBlock> SHIITAKE_CULTURE = culture("shiitake_culture");
    public static final DeferredItem<BlockItem> SHIITAKE_CULTURE_ITEM = cultureItem("shiitake_culture", SHIITAKE_CULTURE);
    public static final DeferredBlock<FungalCultureBlock> OYSTER_MUSHROOM_CULTURE = culture("oyster_mushroom_culture");
    public static final DeferredItem<BlockItem> OYSTER_MUSHROOM_CULTURE_ITEM = cultureItem("oyster_mushroom_culture", OYSTER_MUSHROOM_CULTURE);
    public static final DeferredBlock<FungalCultureBlock> ENOKI_CULTURE = culture("enoki_culture");
    public static final DeferredItem<BlockItem> ENOKI_CULTURE_ITEM = cultureItem("enoki_culture", ENOKI_CULTURE);
    public static final DeferredBlock<FungalCultureBlock> PORCINI_CULTURE = culture("porcini_culture");
    public static final DeferredItem<BlockItem> PORCINI_CULTURE_ITEM = cultureItem("porcini_culture", PORCINI_CULTURE);
    public static final DeferredBlock<FungalCultureBlock> LION_MANE_CULTURE = culture("lion_mane_culture");
    public static final DeferredItem<BlockItem> LION_MANE_CULTURE_ITEM = cultureItem("lion_mane_culture", LION_MANE_CULTURE);
    public static final DeferredBlock<FungalCultureBlock> TRUFFLE_CULTURE = culture("truffle_culture");
    public static final DeferredItem<BlockItem> TRUFFLE_CULTURE_ITEM = cultureItem("truffle_culture", TRUFFLE_CULTURE);

    private static DeferredItem<Item> food(String name, int nutrition, float saturation) {
        FoodProperties properties = new FoodProperties.Builder()
                .nutrition(nutrition).saturationModifier(saturation).build();
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(properties)));
    }

    private static DeferredItem<Item> ingredient(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static DeferredBlock<MarinePlantCropBlock> marineCrop(String name) {
        return BLOCKS.register(name, () -> new MarinePlantCropBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    }

    private static DeferredItem<BlockItem> marineCropItem(String name, DeferredBlock<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static DeferredBlock<FungalCultureBlock> culture(String name) {
        return BLOCKS.register(name, () -> new FungalCultureBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    }

    private static DeferredItem<BlockItem> cultureItem(String name, DeferredBlock<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
