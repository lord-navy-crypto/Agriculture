package io.github.lordnavycrypto.threshold.item;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThresholdItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThresholdMod.MOD_ID);

    public static final DeferredItem<AlmondWaterItem> ALMOND_WATER =
            ITEMS.register("almond_water", AlmondWaterItem::new);
    public static final DeferredItem<FlashlightItem> FLASHLIGHT =
            ITEMS.register("flashlight", FlashlightItem::new);
    public static final DeferredItem<BatteryItem> BATTERY =
            ITEMS.register("battery", BatteryItem::new);

    private ThresholdItems() {}
}
