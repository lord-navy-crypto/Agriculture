package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
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
    public static final DeferredBlock<AgricultureCropBlock> ONION_CROP = crop("onion_crop", "onion_seeds", AgricultureCropBlock.PollinationGroup.NONE);
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

    public static final DeferredBlock<AgricultureCropBlock> BARLEY_CROP = crop("barley_crop", "barley_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> OAT_CROP = crop("oat_crop", "oat_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> RYE_CROP = crop("rye_crop", "rye_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> PEA_CROP = crop("pea_crop", "pea_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> LETTUCE_CROP = crop("lettuce_crop", "lettuce_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> CABBAGE_CROP = crop("cabbage_crop", "cabbage_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> SPINACH_CROP = crop("spinach_crop", "spinach_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> BROCCOLI_CROP = crop("broccoli_crop", "broccoli_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> EGGPLANT_CROP = crop("eggplant_crop", "eggplant_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredBlock<AgricultureCropBlock> COTTON_CROP = crop("cotton_crop", "cotton_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> FLAX_CROP = crop("flax_crop", "flax_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> PEANUT_CROP = crop("peanut_crop", "peanut_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> SORGHUM_CROP = crop("sorghum_crop", "sorghum_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> SUNFLOWER_SEED_CROP = crop("sunflower_seed_crop", "sunflower_seed_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);

    public static final DeferredItem<Item> BARLEY_SEEDS = seed("barley_seeds", BARLEY_CROP);
    public static final DeferredItem<Item> OAT_SEEDS = seed("oat_seeds", OAT_CROP);
    public static final DeferredItem<Item> RYE_SEEDS = seed("rye_seeds", RYE_CROP);
    public static final DeferredItem<Item> PEA_SEEDS = seed("pea_seeds", PEA_CROP);
    public static final DeferredItem<Item> LETTUCE_SEEDS = seed("lettuce_seeds", LETTUCE_CROP);
    public static final DeferredItem<Item> CABBAGE_SEEDS = seed("cabbage_seeds", CABBAGE_CROP);
    public static final DeferredItem<Item> SPINACH_SEEDS = seed("spinach_seeds", SPINACH_CROP);
    public static final DeferredItem<Item> BROCCOLI_SEEDS = seed("broccoli_seeds", BROCCOLI_CROP);
    public static final DeferredItem<Item> EGGPLANT_SEEDS = seed("eggplant_seeds", EGGPLANT_CROP);
    public static final DeferredItem<Item> COTTON_SEEDS = seed("cotton_seeds", COTTON_CROP);
    public static final DeferredItem<Item> FLAX_SEEDS = seed("flax_seeds", FLAX_CROP);
    public static final DeferredItem<Item> PEANUT_SEEDS = seed("peanut_seeds", PEANUT_CROP);
    public static final DeferredItem<Item> SORGHUM_SEEDS = seed("sorghum_seeds", SORGHUM_CROP);
    public static final DeferredItem<Item> SUNFLOWER_SEED_SEEDS = seed("sunflower_seed_seeds", SUNFLOWER_SEED_CROP);

    public static final DeferredItem<Item> BARLEY = food("barley", 3, 0.4F);
    public static final DeferredItem<Item> OAT = food("oat", 3, 0.4F);
    public static final DeferredItem<Item> RYE = food("rye", 3, 0.4F);
    public static final DeferredItem<Item> PEA = food("pea", 2, 0.3F);
    public static final DeferredItem<Item> LETTUCE = food("lettuce", 2, 0.25F);
    public static final DeferredItem<Item> CABBAGE = food("cabbage", 2, 0.25F);
    public static final DeferredItem<Item> SPINACH = food("spinach", 2, 0.25F);
    public static final DeferredItem<Item> BROCCOLI = food("broccoli", 2, 0.3F);
    public static final DeferredItem<Item> EGGPLANT = food("eggplant", 2, 0.25F);
    public static final DeferredItem<Item> COTTON = material("cotton");
    public static final DeferredItem<Item> FLAX = material("flax");
    public static final DeferredItem<Item> PEANUT = food("peanut", 2, 0.3F);
    public static final DeferredItem<Item> SORGHUM = food("sorghum", 3, 0.4F);
    public static final DeferredItem<Item> SUNFLOWER_SEED = food("sunflower_seed", 2, 0.3F);

    public static final DeferredBlock<WildSeedPlantBlock> WILD_BARLEY = BLOCKS.register("wild_barley", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_OAT = BLOCKS.register("wild_oat", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_PEA = BLOCKS.register("wild_pea", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_LETTUCE = BLOCKS.register("wild_lettuce", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_FLAX = BLOCKS.register("wild_flax", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_SUNFLOWER_SEED = BLOCKS.register("wild_sunflower_seed", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_BLUEBERRY = BLOCKS.register("wild_blueberry", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_STRAWBERRY = BLOCKS.register("wild_strawberry", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));

    public static final DeferredBlock<AgricultureCropBlock> MILLET_CROP = crop("millet_crop", "millet_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> BUCKWHEAT_CROP = crop("buckwheat_crop", "buckwheat_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredBlock<AgricultureCropBlock> CHICKPEA_CROP = crop("chickpea_crop", "chickpea_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> LENTIL_CROP = crop("lentil_crop", "lentil_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> RADISH_CROP = crop("radish_crop", "radish_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> TURNIP_CROP = crop("turnip_crop", "turnip_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> SWEET_POTATO_CROP = crop("sweet_potato_crop", "sweet_potato_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> KALE_CROP = crop("kale_crop", "kale_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredBlock<AgricultureCropBlock> ZUCCHINI_CROP = crop("zucchini_crop", "zucchini_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredBlock<AgricultureCropBlock> RASPBERRY_CROP = crop("raspberry_crop", "raspberry_seeds", AgricultureCropBlock.PollinationGroup.BERRY);
    public static final DeferredBlock<AgricultureCropBlock> CRANBERRY_CROP = crop("cranberry_crop", "cranberry_seeds", AgricultureCropBlock.PollinationGroup.BERRY);
    public static final DeferredBlock<AgricultureCropBlock> SESAME_CROP = crop("sesame_crop", "sesame_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);

    public static final DeferredItem<Item> MILLET_SEEDS = seed("millet_seeds", MILLET_CROP);
    public static final DeferredItem<Item> BUCKWHEAT_SEEDS = seed("buckwheat_seeds", BUCKWHEAT_CROP);
    public static final DeferredItem<Item> CHICKPEA_SEEDS = seed("chickpea_seeds", CHICKPEA_CROP);
    public static final DeferredItem<Item> LENTIL_SEEDS = seed("lentil_seeds", LENTIL_CROP);
    public static final DeferredItem<Item> RADISH_SEEDS = seed("radish_seeds", RADISH_CROP);
    public static final DeferredItem<Item> TURNIP_SEEDS = seed("turnip_seeds", TURNIP_CROP);
    public static final DeferredItem<Item> SWEET_POTATO_SEEDS = seed("sweet_potato_seeds", SWEET_POTATO_CROP);
    public static final DeferredItem<Item> KALE_SEEDS = seed("kale_seeds", KALE_CROP);
    public static final DeferredItem<Item> ZUCCHINI_SEEDS = seed("zucchini_seeds", ZUCCHINI_CROP);
    public static final DeferredItem<Item> RASPBERRY_SEEDS = seed("raspberry_seeds", RASPBERRY_CROP);
    public static final DeferredItem<Item> CRANBERRY_SEEDS = seed("cranberry_seeds", CRANBERRY_CROP);
    public static final DeferredItem<Item> SESAME_SEEDS = seed("sesame_seeds", SESAME_CROP);

    public static final DeferredItem<Item> MILLET = food("millet", 3, 0.3F);
    public static final DeferredItem<Item> BUCKWHEAT = food("buckwheat", 3, 0.3F);
    public static final DeferredItem<Item> CHICKPEA = food("chickpea", 2, 0.3F);
    public static final DeferredItem<Item> LENTIL = food("lentil", 2, 0.3F);
    public static final DeferredItem<Item> RADISH = food("radish", 2, 0.3F);
    public static final DeferredItem<Item> TURNIP = food("turnip", 2, 0.3F);
    public static final DeferredItem<Item> SWEET_POTATO = food("sweet_potato", 3, 0.3F);
    public static final DeferredItem<Item> KALE = food("kale", 2, 0.3F);
    public static final DeferredItem<Item> ZUCCHINI = food("zucchini", 2, 0.3F);
    public static final DeferredItem<Item> RASPBERRY = food("raspberry", 2, 0.3F);
    public static final DeferredItem<Item> CRANBERRY = food("cranberry", 2, 0.3F);
    public static final DeferredItem<Item> SESAME = food("sesame", 2, 0.3F);

    public static final DeferredBlock<WildSeedPlantBlock> WILD_MILLET = BLOCKS.register("wild_millet", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_RADISH = BLOCKS.register("wild_radish", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_RASPBERRY = BLOCKS.register("wild_raspberry", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));
    public static final DeferredBlock<WildSeedPlantBlock> WILD_SESAME = BLOCKS.register("wild_sesame", () -> new WildSeedPlantBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)));

    /** v0.9 diversified herbs, floral forage, oilseeds and cool-season vegetables. */
    public static final DeferredBlock<AgricultureCropBlock> LAVENDER_CROP = crop("lavender_crop", "lavender_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> LAVENDER_SEEDS = seed("lavender_seeds", LAVENDER_CROP);
    public static final DeferredItem<Item> LAVENDER = material("lavender");

    public static final DeferredBlock<AgricultureCropBlock> CLOVER_CROP = crop("clover_crop", "clover_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CLOVER_SEEDS = seed("clover_seeds", CLOVER_CROP);
    public static final DeferredItem<Item> CLOVER = material("clover");

    public static final DeferredBlock<AgricultureCropBlock> BASIL_CROP = crop("basil_crop", "basil_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> BASIL_SEEDS = seed("basil_seeds", BASIL_CROP);
    public static final DeferredItem<Item> BASIL = food("basil", 1, 0.2F);

    public static final DeferredBlock<AgricultureCropBlock> MINT_CROP = crop("mint_crop", "mint_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> MINT_SEEDS = seed("mint_seeds", MINT_CROP);
    public static final DeferredItem<Item> MINT = food("mint", 1, 0.2F);

    public static final DeferredBlock<AgricultureCropBlock> MUSTARD_CROP = crop("mustard_crop", "mustard_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> MUSTARD_SEEDS = seed("mustard_seeds", MUSTARD_CROP);
    public static final DeferredItem<Item> MUSTARD = material("mustard");

    public static final DeferredBlock<AgricultureCropBlock> CANOLA_CROP = crop("canola_crop", "canola_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> CANOLA_SEEDS = seed("canola_seeds", CANOLA_CROP);
    public static final DeferredItem<Item> CANOLA = material("canola");

    public static final DeferredBlock<AgricultureCropBlock> CELERY_CROP = crop("celery_crop", "celery_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CELERY_SEEDS = seed("celery_seeds", CELERY_CROP);
    public static final DeferredItem<Item> CELERY = food("celery", 2, 0.2F);

    public static final DeferredBlock<AgricultureCropBlock> LEEK_CROP = crop("leek_crop", "leek_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> LEEK_SEEDS = seed("leek_seeds", LEEK_CROP);
    public static final DeferredItem<Item> LEEK = food("leek", 2, 0.2F);

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

    private static DeferredItem<Item> material(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(LAVENDER_SEEDS.get());
            event.accept(CLOVER_SEEDS.get());
            event.accept(BASIL_SEEDS.get());
            event.accept(MINT_SEEDS.get());
            event.accept(MUSTARD_SEEDS.get());
            event.accept(CANOLA_SEEDS.get());
            event.accept(CELERY_SEEDS.get());
            event.accept(LEEK_SEEDS.get());
            event.accept(CORN_SEEDS.get());
            event.accept(TOMATO_SEEDS.get());
            event.accept(ONION_SEEDS.get());
            event.accept(RICE_SEEDS.get());
            event.accept(SOYBEAN_SEEDS.get());
            event.accept(STRAWBERRY_SEEDS.get());
            event.accept(CUCUMBER_SEEDS.get());
            event.accept(BLUEBERRY_SEEDS.get());
            event.accept(BELL_PEPPER_SEEDS.get());
            event.accept(GARLIC_SEEDS.get());
            event.accept(BARLEY_SEEDS.get());
            event.accept(OAT_SEEDS.get());
            event.accept(RYE_SEEDS.get());
            event.accept(PEA_SEEDS.get());
            event.accept(LETTUCE_SEEDS.get());
            event.accept(CABBAGE_SEEDS.get());
            event.accept(SPINACH_SEEDS.get());
            event.accept(BROCCOLI_SEEDS.get());
            event.accept(EGGPLANT_SEEDS.get());
            event.accept(COTTON_SEEDS.get());
            event.accept(FLAX_SEEDS.get());
            event.accept(PEANUT_SEEDS.get());
            event.accept(SORGHUM_SEEDS.get());
            event.accept(MILLET_SEEDS.get());
            event.accept(BUCKWHEAT_SEEDS.get());
            event.accept(CHICKPEA_SEEDS.get());
            event.accept(LENTIL_SEEDS.get());
            event.accept(RADISH_SEEDS.get());
            event.accept(TURNIP_SEEDS.get());
            event.accept(SWEET_POTATO_SEEDS.get());
            event.accept(KALE_SEEDS.get());
            event.accept(ZUCCHINI_SEEDS.get());
            event.accept(RASPBERRY_SEEDS.get());
            event.accept(CRANBERRY_SEEDS.get());
            event.accept(SESAME_SEEDS.get());
            event.accept(LAVENDER.get());
            event.accept(CLOVER.get());
            event.accept(MUSTARD.get());
            event.accept(CANOLA.get());
            event.accept(COTTON.get());
            event.accept(FLAX.get());
            event.accept(SUNFLOWER_SEED_SEEDS.get());
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(BASIL.get());
            event.accept(MINT.get());
            event.accept(CELERY.get());
            event.accept(LEEK.get());
            event.accept(BeeContent.CLOVER_HONEY_BOTTLE.get());
            event.accept(BeeContent.LAVENDER_HONEY_BOTTLE.get());
            event.accept(BeeContent.BUCKWHEAT_HONEY_BOTTLE.get());
            event.accept(BeeContent.SUNFLOWER_HONEY_BOTTLE.get());
            event.accept(CORN.get());
            event.accept(TOMATO.get());
            event.accept(ONION.get());
            event.accept(RICE.get());
            event.accept(SOYBEAN.get());
            event.accept(STRAWBERRY.get());
            event.accept(CUCUMBER.get());
            event.accept(BLUEBERRY.get());
            event.accept(BELL_PEPPER.get());
            event.accept(GARLIC.get());
            event.accept(BARLEY.get());
            event.accept(OAT.get());
            event.accept(RYE.get());
            event.accept(PEA.get());
            event.accept(LETTUCE.get());
            event.accept(CABBAGE.get());
            event.accept(SPINACH.get());
            event.accept(BROCCOLI.get());
            event.accept(EGGPLANT.get());
            event.accept(PEANUT.get());
            event.accept(SORGHUM.get());
            event.accept(MILLET.get());
            event.accept(BUCKWHEAT.get());
            event.accept(CHICKPEA.get());
            event.accept(LENTIL.get());
            event.accept(RADISH.get());
            event.accept(TURNIP.get());
            event.accept(SWEET_POTATO.get());
            event.accept(KALE.get());
            event.accept(ZUCCHINI.get());
            event.accept(RASPBERRY.get());
            event.accept(CRANBERRY.get());
            event.accept(SESAME.get());
            event.accept(SUNFLOWER_SEED.get());
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(BeeContent.MEADOW_BEE_TAG.get());
            event.accept(BeeContent.ORCHARD_BEE_TAG.get());
            event.accept(BeeContent.BERRY_BEE_TAG.get());
            event.accept(BeeContent.LARGE_APIARY_KIT.get());
            event.accept(BeeContent.HONEY_HARVESTER.get());
            event.accept(BeeContent.BUMBLEBEE_HABITAT_ITEM.get());
            event.accept(BeeContent.MASON_BEE_HABITAT_ITEM.get());
            event.accept(BeeContent.SQUASH_BEE_HABITAT_ITEM.get());
            event.accept(BeeContent.LEAFCUTTER_BEE_HABITAT_ITEM.get());
        }
    }

}
