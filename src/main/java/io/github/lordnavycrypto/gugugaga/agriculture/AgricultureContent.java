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

/** Registry for plantable crop lines, their propagules, and harvested produce. */
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


    /** v1.0 twelve common culinary, vegetable and edible herb crops. */
    public static final DeferredBlock<AgricultureCropBlock> PARSNIP_CROP = crop("parsnip_crop", "parsnip_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> PARSNIP_SEEDS = seed("parsnip_seeds", PARSNIP_CROP);
    public static final DeferredItem<Item> PARSNIP = food("parsnip", 3, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> ARUGULA_CROP = crop("arugula_crop", "arugula_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> ARUGULA_SEEDS = seed("arugula_seeds", ARUGULA_CROP);
    public static final DeferredItem<Item> ARUGULA = food("arugula", 1, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> SWISS_CHARD_CROP = crop("swiss_chard_crop", "swiss_chard_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> SWISS_CHARD_SEEDS = seed("swiss_chard_seeds", SWISS_CHARD_CROP);
    public static final DeferredItem<Item> SWISS_CHARD = food("swiss_chard", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> OKRA_CROP = crop("okra_crop", "okra_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> OKRA_SEEDS = seed("okra_seeds", OKRA_CROP);
    public static final DeferredItem<Item> OKRA = food("okra", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> FENNEL_CROP = crop("fennel_crop", "fennel_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> FENNEL_SEEDS = seed("fennel_seeds", FENNEL_CROP);
    public static final DeferredItem<Item> FENNEL = food("fennel", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> DILL_CROP = crop("dill_crop", "dill_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> DILL_SEEDS = seed("dill_seeds", DILL_CROP);
    public static final DeferredItem<Item> DILL = food("dill", 1, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> CILANTRO_CROP = crop("cilantro_crop", "cilantro_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CILANTRO_SEEDS = seed("cilantro_seeds", CILANTRO_CROP);
    public static final DeferredItem<Item> CILANTRO = food("cilantro", 1, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> PARSLEY_CROP = crop("parsley_crop", "parsley_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> PARSLEY_SEEDS = seed("parsley_seeds", PARSLEY_CROP);
    public static final DeferredItem<Item> PARSLEY = food("parsley", 1, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> COLLARD_GREENS_CROP = crop("collard_greens_crop", "collard_greens_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> COLLARD_GREENS_SEEDS = seed("collard_greens_seeds", COLLARD_GREENS_CROP);
    public static final DeferredItem<Item> COLLARD_GREENS = food("collard_greens", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> GREEN_BEAN_CROP = crop("green_bean_crop", "green_bean_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> GREEN_BEAN_SEEDS = seed("green_bean_seeds", GREEN_BEAN_CROP);
    public static final DeferredItem<Item> GREEN_BEAN = food("green_bean", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> CHILI_PEPPER_CROP = crop("chili_pepper_crop", "chili_pepper_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> CHILI_PEPPER_SEEDS = seed("chili_pepper_seeds", CHILI_PEPPER_CROP);
    public static final DeferredItem<Item> CHILI_PEPPER = food("chili_pepper", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> BRUSSELS_SPROUTS_CROP = crop("brussels_sprouts_crop", "brussels_sprouts_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> BRUSSELS_SPROUTS_SEEDS = seed("brussels_sprouts_seeds", BRUSSELS_SPROUTS_CROP);
    public static final DeferredItem<Item> BRUSSELS_SPROUTS = food("brussels_sprouts", 2, 0.25F);

    /** v1.3 edible buds, leafy brassicas, legumes and tropical rhizomes. */
    public static final DeferredBlock<AgricultureCropBlock> CAULIFLOWER_CROP = crop("cauliflower_crop", "cauliflower_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CAULIFLOWER_SEEDS = seed("cauliflower_seeds", CAULIFLOWER_CROP);
    public static final DeferredItem<Item> CAULIFLOWER = food("cauliflower", 3, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> BOK_CHOY_CROP = crop("bok_choy_crop", "bok_choy_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> BOK_CHOY_SEEDS = seed("bok_choy_seeds", BOK_CHOY_CROP);
    public static final DeferredItem<Item> BOK_CHOY = food("bok_choy", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> SHALLOT_CROP = crop("shallot_crop", "shallot_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> SHALLOT_SEEDS = seed("shallot_seeds", SHALLOT_CROP);
    public static final DeferredItem<Item> SHALLOT = food("shallot", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> GINGER_CROP = crop("ginger_crop", "ginger_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> GINGER_SEEDS = seed("ginger_seeds", GINGER_CROP);
    public static final DeferredItem<Item> GINGER = food("ginger", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> TURMERIC_CROP = crop("turmeric_crop", "turmeric_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> TURMERIC_SEEDS = seed("turmeric_seeds", TURMERIC_CROP);
    public static final DeferredItem<Item> TURMERIC = food("turmeric", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> BLACK_BEAN_CROP = crop("black_bean_crop", "black_bean_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> BLACK_BEAN_SEEDS = seed("black_bean_seeds", BLACK_BEAN_CROP);
    public static final DeferredItem<Item> BLACK_BEAN = food("black_bean", 3, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> QUINOA_CROP = crop("quinoa_crop", "quinoa_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> QUINOA_SEEDS = seed("quinoa_seeds", QUINOA_CROP);
    public static final DeferredItem<Item> QUINOA = food("quinoa", 3, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> ARTICHOKE_CROP = crop("artichoke_crop", "artichoke_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> ARTICHOKE_SEEDS = seed("artichoke_seeds", ARTICHOKE_CROP);
    public static final DeferredItem<Item> ARTICHOKE = food("artichoke", 3, 0.25F);

    /** v1.7 crop diversity: perennial spears, tropical fruit, berries, grains and culinary alliums. */
    public static final DeferredBlock<AgricultureCropBlock> ASPARAGUS_CROP = crop("asparagus_crop", "asparagus_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> ASPARAGUS_SEEDS = seed("asparagus_seeds", ASPARAGUS_CROP);
    public static final DeferredItem<Item> ASPARAGUS = food("asparagus", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> PUMPKIN_CROP = crop("pumpkin_crop", "pumpkin_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> PUMPKIN_SEEDS = seed("pumpkin_seeds", PUMPKIN_CROP);
    public static final DeferredItem<Item> PUMPKIN = food("pumpkin", 3, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> PINEAPPLE_CROP = crop("pineapple_crop", "pineapple_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> PINEAPPLE_SEEDS = seed("pineapple_seeds", PINEAPPLE_CROP);
    public static final DeferredItem<Item> PINEAPPLE = food("pineapple", 4, 0.4F);
    public static final DeferredBlock<AgricultureCropBlock> BLACKBERRY_CROP = crop("blackberry_crop", "blackberry_seeds", AgricultureCropBlock.PollinationGroup.BERRY);
    public static final DeferredItem<Item> BLACKBERRY_SEEDS = seed("blackberry_seeds", BLACKBERRY_CROP);
    public static final DeferredItem<Item> BLACKBERRY = food("blackberry", 2, 0.35F);
    public static final DeferredBlock<AgricultureCropBlock> BLACKCURRANT_CROP = crop("blackcurrant_crop", "blackcurrant_seeds", AgricultureCropBlock.PollinationGroup.BERRY);
    public static final DeferredItem<Item> BLACKCURRANT_SEEDS = seed("blackcurrant_seeds", BLACKCURRANT_CROP);
    public static final DeferredItem<Item> BLACKCURRANT = food("blackcurrant", 2, 0.35F);
    public static final DeferredBlock<AgricultureCropBlock> AMARANTH_CROP = crop("amaranth_crop", "amaranth_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> AMARANTH_SEEDS = seed("amaranth_seeds", AMARANTH_CROP);
    public static final DeferredItem<Item> AMARANTH = food("amaranth", 3, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> CHIVES_CROP = crop("chives_crop", "chives_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CHIVES_SEEDS = seed("chives_seeds", CHIVES_CROP);
    public static final DeferredItem<Item> CHIVES = food("chives", 1, 0.2F);
    public static final DeferredBlock<AgricultureCropBlock> SWEET_CORN_CROP = crop("sweet_corn_crop", "sweet_corn_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> SWEET_CORN_SEEDS = seed("sweet_corn_seeds", SWEET_CORN_CROP);
    public static final DeferredItem<Item> SWEET_CORN = food("sweet_corn", 3, 0.4F);

    /** v1.8: root crops, a fine-seeded grain, and cool/wet leafy crops. */
    public static final DeferredBlock<AgricultureCropBlock> CASSAVA_CROP = crop("cassava_crop", "cassava_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CASSAVA_SEEDS = seed("cassava_seeds", CASSAVA_CROP);
    public static final DeferredItem<Item> CASSAVA = material("cassava");
    public static final DeferredBlock<AgricultureCropBlock> TARO_CROP = crop("taro_crop", "taro_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> TARO_SEEDS = seed("taro_seeds", TARO_CROP);
    public static final DeferredItem<Item> TARO = material("taro");
    public static final DeferredBlock<AgricultureCropBlock> YAM_CROP = crop("yam_crop", "yam_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> YAM_SEEDS = seed("yam_seeds", YAM_CROP);
    public static final DeferredItem<Item> YAM = material("yam");
    public static final DeferredBlock<AgricultureCropBlock> TEFF_CROP = crop("teff_crop", "teff_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> TEFF_SEEDS = seed("teff_seeds", TEFF_CROP);
    public static final DeferredItem<Item> TEFF = food("teff", 3, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> WATERCRESS_CROP = crop("watercress_crop", "watercress_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> WATERCRESS_SEEDS = seed("watercress_seeds", WATERCRESS_CROP);
    public static final DeferredItem<Item> WATERCRESS = food("watercress", 1, 0.2F);
    public static final DeferredBlock<AgricultureCropBlock> ENDIVE_CROP = crop("endive_crop", "endive_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> ENDIVE_SEEDS = seed("endive_seeds", ENDIVE_CROP);
    public static final DeferredItem<Item> ENDIVE = food("endive", 1, 0.2F);
    public static final DeferredBlock<AgricultureCropBlock> KOHLRABI_CROP = crop("kohlrabi_crop", "kohlrabi_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> KOHLRABI_SEEDS = seed("kohlrabi_seeds", KOHLRABI_CROP);
    public static final DeferredItem<Item> KOHLRABI = food("kohlrabi", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> RUTABAGA_CROP = crop("rutabaga_crop", "rutabaga_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> RUTABAGA_SEEDS = seed("rutabaga_seeds", RUTABAGA_CROP);
    public static final DeferredItem<Item> RUTABAGA = food("rutabaga", 2, 0.25F);

    /** v1.9 additional pulses, dryland oilseed, roots and repeat-cut greens. */
    public static final DeferredBlock<AgricultureCropBlock> JICAMA_CROP = crop("jicama_crop", "jicama_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> JICAMA_SEEDS = seed("jicama_seeds", JICAMA_CROP);
    public static final DeferredItem<Item> JICAMA = food("jicama", 3, 0.4F);
    public static final DeferredBlock<AgricultureCropBlock> ADZUKI_BEAN_CROP = crop("adzuki_bean_crop", "adzuki_bean_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> ADZUKI_BEAN_SEEDS = seed("adzuki_bean_seeds", ADZUKI_BEAN_CROP);
    public static final DeferredItem<Item> ADZUKI_BEAN = food("adzuki_bean", 3, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> MUNG_BEAN_CROP = crop("mung_bean_crop", "mung_bean_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> MUNG_BEAN_SEEDS = seed("mung_bean_seeds", MUNG_BEAN_CROP);
    public static final DeferredItem<Item> MUNG_BEAN = food("mung_bean", 3, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> COWPEA_CROP = crop("cowpea_crop", "cowpea_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> COWPEA_SEEDS = seed("cowpea_seeds", COWPEA_CROP);
    public static final DeferredItem<Item> COWPEA = food("cowpea", 3, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> SAFFLOWER_CROP = crop("safflower_crop", "safflower_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> SAFFLOWER_SEEDS = seed("safflower_seeds", SAFFLOWER_CROP);
    public static final DeferredItem<Item> SAFFLOWER = material("safflower");
    public static final DeferredBlock<AgricultureCropBlock> CHICORY_CROP = crop("chicory_crop", "chicory_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CHICORY_SEEDS = seed("chicory_seeds", CHICORY_CROP);
    public static final DeferredItem<Item> CHICORY = food("chicory", 1, 0.2F);
    public static final DeferredBlock<AgricultureCropBlock> MUSTARD_GREENS_CROP = crop("mustard_greens_crop", "mustard_greens_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> MUSTARD_GREENS_SEEDS = seed("mustard_greens_seeds", MUSTARD_GREENS_CROP);
    public static final DeferredItem<Item> MUSTARD_GREENS = food("mustard_greens", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> WATER_SPINACH_CROP = crop("water_spinach_crop", "water_spinach_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> WATER_SPINACH_SEEDS = seed("water_spinach_seeds", WATER_SPINACH_CROP);
    public static final DeferredItem<Item> WATER_SPINACH = food("water_spinach", 2, 0.25F);

    /** v2.0: cool-season roots and greens, plus vine and shrub crops. */
    public static final DeferredBlock<AgricultureCropBlock> CELERIAC_CROP = crop("celeriac_crop", "celeriac_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CELERIAC_SEEDS = seed("celeriac_seeds", CELERIAC_CROP);
    public static final DeferredItem<Item> CELERIAC = food("celeriac", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> PURSLANE_CROP = crop("purslane_crop", "purslane_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> PURSLANE_SEEDS = seed("purslane_seeds", PURSLANE_CROP);
    public static final DeferredItem<Item> PURSLANE = food("purslane", 2, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> CHAYOTE_CROP = crop("chayote_crop", "chayote_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> CHAYOTE_SEEDS = seed("chayote_seeds", CHAYOTE_CROP);
    public static final DeferredItem<Item> CHAYOTE = food("chayote", 2, 0.3F);
    public static final DeferredBlock<AgricultureCropBlock> GRAPE_CROP = crop("grape_crop", "grape_seeds", AgricultureCropBlock.PollinationGroup.BERRY);
    public static final DeferredItem<Item> GRAPE_SEEDS = seed("grape_seeds", GRAPE_CROP);
    public static final DeferredItem<Item> GRAPE = food("grape", 3, 0.35F);
    public static final DeferredBlock<AgricultureCropBlock> TEA_CROP = crop("tea_crop", "tea_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> TEA_SEEDS = seed("tea_seeds", TEA_CROP);
    public static final DeferredItem<Item> TEA = material("tea");
    public static final DeferredBlock<AgricultureCropBlock> COFFEE_CROP = crop("coffee_crop", "coffee_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> COFFEE_SEEDS = seed("coffee_seeds", COFFEE_CROP);
    public static final DeferredItem<Item> COFFEE = material("coffee");
    public static final DeferredBlock<AgricultureCropBlock> RAPINI_CROP = crop("rapini_crop", "rapini_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> RAPINI_SEEDS = seed("rapini_seeds", RAPINI_CROP);
    public static final DeferredItem<Item> RAPINI = food("rapini", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> NAPA_CABBAGE_CROP = crop("napa_cabbage_crop", "napa_cabbage_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> NAPA_CABBAGE_SEEDS = seed("napa_cabbage_seeds", NAPA_CABBAGE_CROP);
    public static final DeferredItem<Item> NAPA_CABBAGE = food("napa_cabbage", 2, 0.25F);

    /** v2.1: cucurbit fruits, a cool-season pulse, and a repeat-cut leafy vegetable. */
    public static final DeferredBlock<AgricultureCropBlock> MELON_CROP = crop("melon_crop", "melon_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> MELON_SEEDS = seed("melon_seeds", MELON_CROP);
    public static final DeferredItem<Item> MELON = food("melon", 3, 0.30F);
    public static final DeferredBlock<AgricultureCropBlock> WATERMELON_CROP = crop("watermelon_crop", "watermelon_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> WATERMELON_SEEDS = seed("watermelon_seeds", WATERMELON_CROP);
    public static final DeferredItem<Item> WATERMELON = food("watermelon", 3, 0.30F);
    public static final DeferredBlock<AgricultureCropBlock> FAVA_BEAN_CROP = crop("fava_bean_crop", "fava_bean_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> FAVA_BEAN_SEEDS = seed("fava_bean_seeds", FAVA_BEAN_CROP);
    public static final DeferredItem<Item> FAVA_BEAN = food("fava_bean", 3, 0.35F);
    public static final DeferredBlock<AgricultureCropBlock> JUTE_MALLOW_CROP = crop("jute_mallow_crop", "jute_mallow_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> JUTE_MALLOW_SEEDS = seed("jute_mallow_seeds", JUTE_MALLOW_CROP);
    public static final DeferredItem<Item> JUTE_MALLOW = food("jute_mallow", 2, 0.25F);

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
            event.accept(PARSNIP_SEEDS.get());
            event.accept(ARUGULA_SEEDS.get());
            event.accept(SWISS_CHARD_SEEDS.get());
            event.accept(OKRA_SEEDS.get());
            event.accept(FENNEL_SEEDS.get());
            event.accept(DILL_SEEDS.get());
            event.accept(CILANTRO_SEEDS.get());
            event.accept(PARSLEY_SEEDS.get());
            event.accept(COLLARD_GREENS_SEEDS.get());
            event.accept(GREEN_BEAN_SEEDS.get());
            event.accept(CHILI_PEPPER_SEEDS.get());
            event.accept(BRUSSELS_SPROUTS_SEEDS.get());
            event.accept(CAULIFLOWER_SEEDS.get());
            event.accept(BOK_CHOY_SEEDS.get());
            event.accept(SHALLOT_SEEDS.get());
            event.accept(GINGER_SEEDS.get());
            event.accept(TURMERIC_SEEDS.get());
            event.accept(BLACK_BEAN_SEEDS.get());
            event.accept(QUINOA_SEEDS.get());
            event.accept(ARTICHOKE_SEEDS.get());
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
            event.accept(ASPARAGUS_SEEDS.get());
            event.accept(PUMPKIN_SEEDS.get());
            event.accept(PINEAPPLE_SEEDS.get());
            event.accept(BLACKBERRY_SEEDS.get());
            event.accept(BLACKCURRANT_SEEDS.get());
            event.accept(AMARANTH_SEEDS.get());
            event.accept(CHIVES_SEEDS.get());
            event.accept(SWEET_CORN_SEEDS.get());
            event.accept(CASSAVA_SEEDS.get());
            event.accept(TARO_SEEDS.get());
            event.accept(YAM_SEEDS.get());
            event.accept(TEFF_SEEDS.get());
            event.accept(WATERCRESS_SEEDS.get());
            event.accept(ENDIVE_SEEDS.get());
            event.accept(KOHLRABI_SEEDS.get());
            event.accept(RUTABAGA_SEEDS.get());
            event.accept(JICAMA_SEEDS.get());
            event.accept(ADZUKI_BEAN_SEEDS.get());
            event.accept(MUNG_BEAN_SEEDS.get());
            event.accept(COWPEA_SEEDS.get());
            event.accept(SAFFLOWER_SEEDS.get());
            event.accept(CHICORY_SEEDS.get());
            event.accept(MUSTARD_GREENS_SEEDS.get());
            event.accept(WATER_SPINACH_SEEDS.get());
            event.accept(CELERIAC_SEEDS.get());
            event.accept(PURSLANE_SEEDS.get());
            event.accept(CHAYOTE_SEEDS.get());
            event.accept(GRAPE_SEEDS.get());
            event.accept(TEA_SEEDS.get());
            event.accept(COFFEE_SEEDS.get());
            event.accept(RAPINI_SEEDS.get());
            event.accept(NAPA_CABBAGE_SEEDS.get());
            event.accept(MELON_SEEDS.get());
            event.accept(WATERMELON_SEEDS.get());
            event.accept(FAVA_BEAN_SEEDS.get());
            event.accept(JUTE_MALLOW_SEEDS.get());
            event.accept(TEA.get());
            event.accept(COFFEE.get());
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(BASIL.get());
            event.accept(MINT.get());
            event.accept(CELERY.get());
            event.accept(LEEK.get());
            event.accept(BeeContent.CLOVER_HONEY_BOTTLE.get());
            event.accept(BeeContent.LAVENDER_HONEY_BOTTLE.get());
            event.accept(BeeContent.BUCKWHEAT_HONEY_BOTTLE.get());
            event.accept(BeeContent.SUNFLOWER_HONEY_BOTTLE.get());
            event.accept(BeeContent.DANDELION_HONEY_BOTTLE.get());
            event.accept(BeeContent.POPPY_HONEY_BOTTLE.get());
            event.accept(BeeContent.ALLIUM_HONEY_BOTTLE.get());
            event.accept(BeeContent.CORNFLOWER_HONEY_BOTTLE.get());
            event.accept(BeeContent.AZURE_BLUET_HONEY_BOTTLE.get());
            event.accept(BeeContent.BLUE_ORCHID_HONEY_BOTTLE.get());
            event.accept(BeeContent.LILY_OF_THE_VALLEY_HONEY_BOTTLE.get());
            event.accept(BeeContent.OXEYE_DAISY_HONEY_BOTTLE.get());
            event.accept(BeeContent.TORCHFLOWER_HONEY_BOTTLE.get());
            event.accept(BeeContent.RED_TULIP_HONEY_BOTTLE.get());
            event.accept(BeeContent.ORANGE_TULIP_HONEY_BOTTLE.get());
            event.accept(BeeContent.WHITE_TULIP_HONEY_BOTTLE.get());
            event.accept(BeeContent.PINK_TULIP_HONEY_BOTTLE.get());
            event.accept(BeeContent.WITHER_ROSE_HONEY_BOTTLE.get());
            event.accept(BeeContent.BLACKBERRY_HONEY_BOTTLE.get());
            event.accept(BeeContent.BLACKCURRANT_HONEY_BOTTLE.get());
            event.accept(BeeContent.CHIVE_HONEY_BOTTLE.get());
            event.accept(BeeContent.PUMPKIN_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.WILDFLOWER_HONEY_BOTTLE.get());
            event.accept(BeeContent.CASSAVA_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.TARO_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.YAM_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.WATERCRESS_HONEY_BOTTLE.get());
            event.accept(BeeContent.ENDIVE_HONEY_BOTTLE.get());
            event.accept(BeeContent.KOHLRABI_HONEY_BOTTLE.get());
            event.accept(BeeContent.RUTABAGA_HONEY_BOTTLE.get());
            event.accept(BeeContent.JICAMA_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.ADZUKI_BEAN_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.MUNG_BEAN_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.COWPEA_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.SAFFLOWER_HONEY_BOTTLE.get());
            event.accept(BeeContent.CHICORY_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.MUSTARD_GREENS_HONEY_BOTTLE.get());
            event.accept(BeeContent.WATER_SPINACH_HONEY_BOTTLE.get());
            event.accept(BeeContent.CELERIAC_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.PURSLANE_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.CHAYOTE_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.GRAPE_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.TEA_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.COFFEE_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.RAPINI_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.NAPA_CABBAGE_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.MELON_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.WATERMELON_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.FAVA_BEAN_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(BeeContent.JUTE_MALLOW_BLOSSOM_HONEY_BOTTLE.get());
            event.accept(PARSNIP.get());
            event.accept(ARUGULA.get());
            event.accept(SWISS_CHARD.get());
            event.accept(OKRA.get());
            event.accept(FENNEL.get());
            event.accept(DILL.get());
            event.accept(CILANTRO.get());
            event.accept(PARSLEY.get());
            event.accept(COLLARD_GREENS.get());
            event.accept(GREEN_BEAN.get());
            event.accept(CHILI_PEPPER.get());
            event.accept(BRUSSELS_SPROUTS.get());
            event.accept(CAULIFLOWER.get());
            event.accept(BOK_CHOY.get());
            event.accept(SHALLOT.get());
            event.accept(GINGER.get());
            event.accept(TURMERIC.get());
            event.accept(BLACK_BEAN.get());
            event.accept(QUINOA.get());
            event.accept(ARTICHOKE.get());
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
            event.accept(ASPARAGUS.get());
            event.accept(PUMPKIN.get());
            event.accept(PINEAPPLE.get());
            event.accept(BLACKBERRY.get());
            event.accept(BLACKCURRANT.get());
            event.accept(AMARANTH.get());
            event.accept(CHIVES.get());
            event.accept(SWEET_CORN.get());
            event.accept(CASSAVA.get());
            event.accept(TARO.get());
            event.accept(YAM.get());
            event.accept(TEFF.get());
            event.accept(WATERCRESS.get());
            event.accept(ENDIVE.get());
            event.accept(KOHLRABI.get());
            event.accept(RUTABAGA.get());
            event.accept(JICAMA.get());
            event.accept(ADZUKI_BEAN.get());
            event.accept(MUNG_BEAN.get());
            event.accept(COWPEA.get());
            event.accept(SAFFLOWER.get());
            event.accept(CHICORY.get());
            event.accept(MUSTARD_GREENS.get());
            event.accept(WATER_SPINACH.get());
            event.accept(CELERIAC.get());
            event.accept(PURSLANE.get());
            event.accept(CHAYOTE.get());
            event.accept(GRAPE.get());
            event.accept(RAPINI.get());
            event.accept(NAPA_CABBAGE.get());
            event.accept(MELON.get());
            event.accept(WATERMELON.get());
            event.accept(FAVA_BEAN.get());
            event.accept(JUTE_MALLOW.get());
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
