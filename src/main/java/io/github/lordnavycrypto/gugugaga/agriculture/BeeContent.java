package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Bee ecotype tags and the multi-hive apiary placement kit. */
public final class BeeContent {
    private BeeContent() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredBlock<BeeNestHabitatBlock> BUMBLEBEE_HABITAT =
            habitat("bumblebee_habitat", "bumblebee", false);
    public static final DeferredBlock<BeeNestHabitatBlock> MASON_BEE_HABITAT =
            habitat("mason_bee_habitat", "mason_bee", false);
    public static final DeferredBlock<BeeNestHabitatBlock> SQUASH_BEE_HABITAT =
            habitat("squash_bee_habitat", "squash_bee", true);
    public static final DeferredBlock<BeeNestHabitatBlock> LEAFCUTTER_BEE_HABITAT =
            habitat("leafcutter_bee_habitat", "leafcutter_bee", false);

    public static final DeferredItem<Item> BUMBLEBEE_HABITAT_ITEM = habitatItem("bumblebee_habitat", BUMBLEBEE_HABITAT);
    public static final DeferredItem<Item> MASON_BEE_HABITAT_ITEM = habitatItem("mason_bee_habitat", MASON_BEE_HABITAT);
    public static final DeferredItem<Item> SQUASH_BEE_HABITAT_ITEM = habitatItem("squash_bee_habitat", SQUASH_BEE_HABITAT);
    public static final DeferredItem<Item> LEAFCUTTER_BEE_HABITAT_ITEM = habitatItem("leafcutter_bee_habitat", LEAFCUTTER_BEE_HABITAT);

    private static DeferredBlock<BeeNestHabitatBlock> habitat(String id, String beeId, boolean ground) {
        return BLOCKS.register(id, () -> new BeeNestHabitatBlock(beeId, ground,
                BlockBehaviour.Properties.of().mapColor(ground ? MapColor.DIRT : MapColor.WOOD)
                        .strength(ground ? 0.6F : 1.5F).sound(ground ? SoundType.GRAVEL : SoundType.WOOD)));
    }

    private static DeferredItem<Item> habitatItem(String id, DeferredBlock<? extends Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final DeferredItem<Item> MEADOW_BEE_TAG = ITEMS.register(
            "meadow_bee_tag", () -> new BeeEcotypeItem("meadow", new Item.Properties()));
    public static final DeferredItem<Item> ORCHARD_BEE_TAG = ITEMS.register(
            "orchard_bee_tag", () -> new BeeEcotypeItem("orchard", new Item.Properties()));
    public static final DeferredItem<Item> BERRY_BEE_TAG = ITEMS.register(
            "berry_bee_tag", () -> new BeeEcotypeItem("berry", new Item.Properties()));
    public static final DeferredItem<Item> LARGE_APIARY_KIT = ITEMS.register(
            "large_apiary_kit", () -> new LargeApiaryKitItem(new Item.Properties()));

    /** Distinct nectar-source bottles use the native drinkable honey item behavior. */
    public static final DeferredItem<HoneyBottleItem> CLOVER_HONEY_BOTTLE = floralHoney("clover_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> LAVENDER_HONEY_BOTTLE = floralHoney("lavender_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> BUCKWHEAT_HONEY_BOTTLE = floralHoney("buckwheat_honey_bottle");
    public static final DeferredItem<HoneyBottleItem> SUNFLOWER_HONEY_BOTTLE = floralHoney("sunflower_honey_bottle");

    private static DeferredItem<HoneyBottleItem> floralHoney(String name) {
        // HoneyBottleItem provides drinking, poison removal and glass-bottle return.
        return ITEMS.register(name, () -> new HoneyBottleItem(new Item.Properties()
                .stacksTo(16).food(new FoodProperties.Builder()
                        .nutrition(6).saturationModifier(0.1F).alwaysEdible().build())));
    }

    public static final DeferredItem<Item> HONEY_HARVESTER = ITEMS.register(
            "honey_harvester", () -> new HoneyHarvesterItem(new Item.Properties()));
}
