package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Bee ecotype tags and the multi-hive apiary placement kit. */
public final class BeeContent {
    private BeeContent() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredItem<Item> MEADOW_BEE_TAG = ITEMS.register(
            "meadow_bee_tag", () -> new BeeEcotypeItem("meadow", new Item.Properties()));
    public static final DeferredItem<Item> ORCHARD_BEE_TAG = ITEMS.register(
            "orchard_bee_tag", () -> new BeeEcotypeItem("orchard", new Item.Properties()));
    public static final DeferredItem<Item> BERRY_BEE_TAG = ITEMS.register(
            "berry_bee_tag", () -> new BeeEcotypeItem("berry", new Item.Properties()));
    public static final DeferredItem<Item> LARGE_APIARY_KIT = ITEMS.register(
            "large_apiary_kit", () -> new LargeApiaryKitItem(new Item.Properties()));

    public static final DeferredItem<Item> HONEY_HARVESTER = ITEMS.register(
            "honey_harvester", () -> new HoneyHarvesterItem(new Item.Properties()));
}
