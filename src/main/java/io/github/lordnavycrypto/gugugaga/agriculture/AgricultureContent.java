package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registry for the first ten plantable crop lines and their harvested produce. */
public final class AgricultureContent {
    private AgricultureContent() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<AgricultureCropBlock> CORN_CROP = crop("corn_crop", "corn_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> TOMATO_CROP = crop("tomato_crop", "tomato_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredBlock<AgricultureCropBlock> ONION_CROP = crop("onion_crop", "onion_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredBlock<AgricultureCropBlock> RICE_CROP = crop("rice_crop", "rice_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> SOYBEAN_CROP = crop("soybean_crop", "soybean_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> STRAWBERRY_CROP = crop("strawberry_crop", "strawberry_seeds", AgricultureCropBlock.PollinationGroup.BERRY);
    public static final DeferredBlock<AgricultureCropBlock> CUCUMBER_CROP = crop("cucumber_crop", "cucumber_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredBlock<AgricultureCropBlock> BLUEBERRY_CROP = crop("blueberry_crop", "blueberry_seeds", AgricultureCropBlock.PollinationGroup.BERRY);
    public static final DeferredBlock<AgricultureCropBlock> BELL_PEPPER_CROP = crop("bell_pepper_crop", "bell_pepper_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredBlock<AgricultureCropBlock> GARLIC_CROP = crop("garlic_crop", "garlic_seeds", AgricultureCropBlock.PollinationGroup.NONE);

    public static final DeferredItem<Item> CORN_SEEDS = seed("corn_seeds", CORN_CROP);
    public static final DeferredItem<Item> TOMATO_SEEDS = seed("tomato_seeds", TOMATO_CROP);
    public static final DeferredItem<Item> ONION_SEEDS = seed("onion_seeds", ONION_CROP);
    public static final DeferredItem<Item> RICE_SEEDS = seed("rice_seeds", RICE_CROP);
    public static final DeferredItem<Item> SOYBEAN_SEEDS = seed("soybean_seeds", SOYBEAN_CROP);
    public static final DeferredItem<Item> STRAWBERRY_SEEDS = seed("strawberry_seeds", STRAWBERRY_CROP);
    public static final DeferredItem<Item> CUCUMBER_SEEDS = seed("cucumber_seeds", CUCUMBER_CROP);
    public static final DeferredItem<Item> BLUEBERRY_SEEDS = seed("blueberry_seeds", BLUEBERRY_CROP);
    public static final DeferredItem<Item> BELL_PEPPER_SEEDS = seed("bell_pepper_seeds", BELL_PEPPER_CROP);
    public static final DeferredItem<Item> GARLIC_SEEDS = seed("garlic_seeds", GARLIC_CROP);

    public static final DeferredItem<Item> CORN = food("corn", 3, 0.4F);
    public static final DeferredItem<Item> TOMATO = food("tomato", 2, 0.3F);
    public static final DeferredItem<Item> ONION = food("onion", 2, 0.3F);
    public static final DeferredItem<Item> RICE = food("rice", 2, 0.3F);
    public static final DeferredItem<Item> SOYBEAN = food("soybean", 2, 0.35F);
    public static final DeferredItem<Item> STRAWBERRY = food("strawberry", 2, 0.4F);
    public static final DeferredItem<Item> CUCUMBER = food("cucumber", 2, 0.3F);
    public static final DeferredItem<Item> BLUEBERRY = food("blueberry", 2, 0.4F);
    public static final DeferredItem<Item> BELL_PEPPER = food("bell_pepper", 2, 0.3F);
    public static final DeferredItem<Item> GARLIC = food("garlic", 1, 0.2F);

    private static DeferredBlock<AgricultureCropBlock> crop(
            String name, String seedName, AgricultureCropBlock.PollinationGroup group) {
        return BLOCKS.register(name, () -> new AgricultureCropBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                        .noCollission().randomTicks().instabreak().sound(SoundType.CROP),
                seedName, group));
    }

    private static DeferredItem<Item> seed(String name, DeferredBlock<? extends Block> crop) {
        return ITEMS.register(name, () -> new ItemNameBlockItem(crop.get(), new Item.Properties()));
    }

    private static DeferredItem<Item> food(String name, int nutrition, float saturation) {
        FoodProperties properties = new FoodProperties.Builder()
                .nutrition(nutrition).saturationModifier(saturation).build();
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(properties)));
    }
}
