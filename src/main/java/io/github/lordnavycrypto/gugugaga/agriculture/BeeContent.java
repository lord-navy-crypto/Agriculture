package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/** Pollinator habitats, brood rewards, and a safe honey-harvesting tool. */
public final class BeeContent {
    private BeeContent() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredItem<Item> MEADOW_BEE_TAG = ITEMS.register(
            "meadow_bee_tag", () -> new BeeEcotypeItem("meadow", new Item.Properties()));
    public static final DeferredItem<Item> ORCHARD_BEE_TAG = ITEMS.register(
            "orchard_bee_tag", () -> new BeeEcotypeItem("orchard", new Item.Properties()));
    public static final DeferredItem<Item> BERRY_BEE_TAG = ITEMS.register(
            "berry_bee_tag", () -> new BeeEcotypeItem("berry", new Item.Properties()));
    public static final DeferredItem<Item> LARGE_APIARY_KIT = ITEMS.register(
            "large_apiary_kit", () -> new LargeApiaryKitItem(new Item.Properties()));
    public static final DeferredItem<Item> BEEKEEPER_BRUSH = ITEMS.register(
            "bee_keeper_brush", () -> new BeeKeeperBrushItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BUMBLEBEE_NECTAR = ITEMS.register("bumblebee_nectar", () ->
            new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build())));
    public static final DeferredItem<Item> MASON_BEE_COCOON = ITEMS.register(
            "mason_bee_cocoon", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SQUASH_BEE_COCOON = ITEMS.register(
            "squash_bee_cocoon", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LEAFCUTTER_BEE_COCOON = ITEMS.register(
            "leafcutter_bee_cocoon", () -> new Item(new Item.Properties()));

    public static final DeferredBlock<BeeHabitatBlock> BUMBLEBEE_NEST = BLOCKS.register(
            "bumblebee_nest", () -> new BeeHabitatBlock("bumblebee", BUMBLEBEE_NECTAR, false,
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD)));
    public static final DeferredBlock<BeeHabitatBlock> MASON_BEE_HOUSE = BLOCKS.register(
            "mason_bee_house", () -> new BeeHabitatBlock("mason_bee", MASON_BEE_COCOON, false,
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD)));
    public static final DeferredBlock<BeeHabitatBlock> SQUASH_BEE_BURROW = BLOCKS.register(
            "squash_bee_burrow", () -> new BeeHabitatBlock("squash_bee", SQUASH_BEE_COCOON, true,
                    BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.6F).sound(SoundType.GRAVEL)));
    public static final DeferredBlock<BeeHabitatBlock> LEAFCUTTER_BEE_HOUSE = BLOCKS.register(
            "leafcutter_bee_house", () -> new BeeHabitatBlock("leafcutter_bee", LEAFCUTTER_BEE_COCOON, false,
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD)));

    public static final DeferredItem<Item> BUMBLEBEE_NEST_ITEM = blockItem("bumblebee_nest", BUMBLEBEE_NEST);
    public static final DeferredItem<Item> MASON_BEE_HOUSE_ITEM = blockItem("mason_bee_house", MASON_BEE_HOUSE);
    public static final DeferredItem<Item> SQUASH_BEE_BURROW_ITEM = blockItem("squash_bee_burrow", SQUASH_BEE_BURROW);
    public static final DeferredItem<Item> LEAFCUTTER_BEE_HOUSE_ITEM = blockItem("leafcutter_bee_house", LEAFCUTTER_BEE_HOUSE);

    private static DeferredItem<Item> blockItem(String name, DeferredBlock<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(LARGE_APIARY_KIT.get());
            event.accept(BUMBLEBEE_NEST_ITEM.get());
            event.accept(MASON_BEE_HOUSE_ITEM.get());
            event.accept(SQUASH_BEE_BURROW_ITEM.get());
            event.accept(LEAFCUTTER_BEE_HOUSE_ITEM.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(BEEKEEPER_BRUSH.get());
            event.accept(MEADOW_BEE_TAG.get());
            event.accept(ORCHARD_BEE_TAG.get());
            event.accept(BERRY_BEE_TAG.get());
            event.accept(BUMBLEBEE_NECTAR.get());
            event.accept(MASON_BEE_COCOON.get());
            event.accept(SQUASH_BEE_COCOON.get());
            event.accept(LEAFCUTTER_BEE_COCOON.get());
        }
    }
}
