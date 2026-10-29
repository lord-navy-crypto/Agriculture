package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Additive 8-crop, ocean catches, food-ferment and seasoning content. */
public final class FoodEcologyV33Content {
    private FoodEcologyV33Content() {}
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<FoodFermentationVatBlock> FERMENTATION_VAT =
            BLOCKS.register("fermentation_vat", () -> new FoodFermentationVatBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE)
                            .strength(2.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredItem<Item> FERMENTATION_VAT_ITEM =
            ITEMS.register("fermentation_vat", () -> new net.minecraft.world.item.BlockItem(
                    FERMENTATION_VAT.get(), new Item.Properties()));

    public static final DeferredBlock<AgricultureCropBlock> BLACK_PEPPER_CROP = crop("black_pepper_crop", "black_pepper_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> BLACK_PEPPER_SEEDS = seed("black_pepper_seeds", BLACK_PEPPER_CROP);
    public static final DeferredItem<Item> BLACK_PEPPER = food("black_pepper", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> LEMONGRASS_CROP = crop("lemongrass_crop", "lemongrass_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> LEMONGRASS_SEEDS = seed("lemongrass_seeds", LEMONGRASS_CROP);
    public static final DeferredItem<Item> LEMONGRASS = food("lemongrass", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> WASABI_CROP = crop("wasabi_crop", "wasabi_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> WASABI_SEEDS = seed("wasabi_seeds", WASABI_CROP);
    public static final DeferredItem<Item> WASABI = food("wasabi", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> CUMIN_CROP = crop("cumin_crop", "cumin_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> CUMIN_SEEDS = seed("cumin_seeds", CUMIN_CROP);
    public static final DeferredItem<Item> CUMIN = food("cumin", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> CARDAMOM_CROP = crop("cardamom_crop", "cardamom_seeds", AgricultureCropBlock.PollinationGroup.ORCHARD);
    public static final DeferredItem<Item> CARDAMOM_SEEDS = seed("cardamom_seeds", CARDAMOM_CROP);
    public static final DeferredItem<Item> CARDAMOM = food("cardamom", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> FENUGREEK_CROP = crop("fenugreek_crop", "fenugreek_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> FENUGREEK_SEEDS = seed("fenugreek_seeds", FENUGREEK_CROP);
    public static final DeferredItem<Item> FENUGREEK = food("fenugreek", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> SUGAR_BEET_CROP = crop("sugar_beet_crop", "sugar_beet_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> SUGAR_BEET_SEEDS = seed("sugar_beet_seeds", SUGAR_BEET_CROP);
    public static final DeferredItem<Item> SUGAR_BEET = food("sugar_beet", 2, 0.25F);
    public static final DeferredBlock<AgricultureCropBlock> DAIKON_CROP = crop("daikon_crop", "daikon_seeds", AgricultureCropBlock.PollinationGroup.NONE);
    public static final DeferredItem<Item> DAIKON_SEEDS = seed("daikon_seeds", DAIKON_CROP);
    public static final DeferredItem<Item> DAIKON = food("daikon", 2, 0.25F);
    public static final DeferredItem<Item> SALTWATER_ANCHOVY = material("saltwater_anchovy");
    public static final DeferredItem<Item> SALTWATER_SARDINE = material("saltwater_sardine");
    public static final DeferredItem<Item> SALTWATER_HERRING = material("saltwater_herring");
    public static final DeferredItem<Item> SALTWATER_MACKEREL = material("saltwater_mackerel");
    public static final DeferredItem<Item> SALTWATER_SEA_BASS = material("saltwater_sea_bass");
    public static final DeferredItem<Item> SALTWATER_SEA_BREAM = material("saltwater_sea_bream");
    public static final DeferredItem<Item> SALTWATER_SHRIMP = material("saltwater_shrimp");
    public static final DeferredItem<Item> SALTWATER_CRAB = material("saltwater_crab");
    public static final DeferredItem<Item> CRACKED_BLACK_PEPPER = material("cracked_black_pepper");
    public static final DeferredItem<Item> LEMONGRASS_FLAKES = material("lemongrass_flakes");
    public static final DeferredItem<Item> WASABI_PASTE = material("wasabi_paste");
    public static final DeferredItem<Item> GROUND_CUMIN = material("ground_cumin");
    public static final DeferredItem<Item> GROUND_CARDAMOM = material("ground_cardamom");
    public static final DeferredItem<Item> GROUND_FENUGREEK = material("ground_fenugreek");
    public static final DeferredItem<Item> SUGAR_BEET_PULP = material("sugar_beet_pulp");
    public static final DeferredItem<Item> DAIKON_RELISH = material("daikon_relish");
    public static final DeferredItem<Item> FERMENTED_SOYBEANS = material("fermented_soybeans");
    public static final DeferredItem<Item> CULTURED_CABBAGE = material("cultured_cabbage");
    public static final DeferredItem<Item> FERMENTED_CUCUMBER = material("fermented_cucumber");
    public static final DeferredItem<Item> FERMENTED_DAIKON = material("fermented_daikon");
    public static final DeferredItem<Item> AGED_PEPPER_PASTE = material("aged_pepper_paste");
    public static final DeferredItem<Item> FERMENTED_LEMONGRASS = material("fermented_lemongrass");
    public static final DeferredItem<Item> FERMENTED_SHIITAKE = material("fermented_shiitake");
    public static final DeferredItem<Item> CULTURED_BEET_RELISH = material("cultured_beet_relish");
    public static final DeferredItem<Item> PICKLED_BEETROOT = material("pickled_beetroot");
    public static final DeferredItem<Item> CULTURED_CARROT = material("cultured_carrot");
    public static final DeferredItem<Item> SMOKED_ANCHOVY = material("smoked_anchovy");
    public static final DeferredItem<Item> SMOKED_SARDINE = material("smoked_sardine");
    public static final DeferredItem<Item> SMOKED_HERRING = material("smoked_herring");
    public static final DeferredItem<Item> SMOKED_MACKEREL = material("smoked_mackerel");
    public static final DeferredItem<Item> SMOKED_SEA_BASS = material("smoked_sea_bass");
    public static final DeferredItem<Item> SMOKED_SEA_BREAM = material("smoked_sea_bream");
    public static final DeferredItem<Item> SMOKED_SHRIMP = material("smoked_shrimp");
    public static final DeferredItem<Item> SMOKED_CRAB = material("smoked_crab");
    public static final DeferredItem<Item> SPICED_MACKEREL_RICE = food("spiced_mackerel_rice", 7, 0.70F);
    public static final DeferredItem<Item> LEMONGRASS_SARDINE_SOUP = food("lemongrass_sardine_soup", 7, 0.70F);
    public static final DeferredItem<Item> WASABI_SEABASS_PLATE = food("wasabi_seabass_plate", 7, 0.70F);
    public static final DeferredItem<Item> CUMIN_ANCHOVY_TOAST = food("cumin_anchovy_toast", 7, 0.70F);
    public static final DeferredItem<Item> CARDAMOM_BREAM_BOWL = food("cardamom_bream_bowl", 7, 0.70F);
    public static final DeferredItem<Item> FENUGREEK_SHRIMP_STEW = food("fenugreek_shrimp_stew", 7, 0.70F);
    public static final DeferredItem<Item> PEPPER_CRAB_PLATE = food("pepper_crab_plate", 7, 0.70F);
    public static final DeferredItem<Item> HERRING_SEAWEED_BOWL = food("herring_seaweed_bowl", 7, 0.70F);
    public static final DeferredItem<Item> FERMENTED_SOY_RICE = food("fermented_soy_rice", 7, 0.70F);
    public static final DeferredItem<Item> CULTURED_CABBAGE_BOWL = food("cultured_cabbage_bowl", 7, 0.70F);
    public static final DeferredItem<Item> FERMENTED_CUCUMBER_PLATE = food("fermented_cucumber_plate", 7, 0.70F);
    public static final DeferredItem<Item> DAIKON_FISH_SOUP = food("daikon_fish_soup", 7, 0.70F);
    public static final DeferredItem<Item> AGED_PEPPER_BEEF = food("aged_pepper_beef", 7, 0.70F);
    public static final DeferredItem<Item> SHIITAKE_FERMENT_NOODLES = food("shiitake_ferment_noodles", 7, 0.70F);
    public static final DeferredItem<Item> SWEET_BEET_RICE_CAKE = food("sweet_beet_rice_cake", 7, 0.70F);
    public static final DeferredItem<Item> PICKLED_BEETROOT_BOWL = food("pickled_beetroot_bowl", 7, 0.70F);
    public static final DeferredItem<Item> CULTURED_CARROT_WRAP = food("cultured_carrot_wrap", 7, 0.70F);
    public static final DeferredItem<Item> SPICED_VEGETABLE_BOWL = food("spiced_vegetable_bowl", 7, 0.70F);

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
    private static DeferredItem<Item> material(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }
    private static DeferredItem<Item> food(String id, int nutrition, float sat) {
        return ITEMS.register(id, () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().nutrition(nutrition).saturationModifier(sat).build())));
    }
}
