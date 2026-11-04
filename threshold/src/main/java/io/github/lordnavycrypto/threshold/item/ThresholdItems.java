package io.github.lordnavycrypto.threshold.item;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ThresholdItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThresholdMod.MOD_ID);

    public static final DeferredItem<Item> ALMOND_WATER =
            ITEMS.register("almond_water", AlmondWaterItem::new);
    public static final DeferredItem<Item> FLASHLIGHT =
            ITEMS.register("flashlight", FlashlightItem::new);
    public static final DeferredItem<Item> BATTERY =
            ITEMS.register("battery", BatteryItem::new);

    private ThresholdItems() {}
}
