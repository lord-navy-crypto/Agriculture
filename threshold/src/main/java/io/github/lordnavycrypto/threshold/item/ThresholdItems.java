package io.github.lordnavycrypto.threshold.item;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
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

    public static final DeferredItem<SpawnEggItem> WATCHER_SPAWN_EGG =
            ITEMS.register("watcher_spawn_egg", () -> new SpawnEggItem(
                    ThresholdEntities.WATCHER.get(), 0x282627, 0xC6C0B6, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> HOUND_SPAWN_EGG =
            ITEMS.register("hound_spawn_egg", () -> new SpawnEggItem(
                    ThresholdEntities.HOUND.get(), 0x171513, 0x786E63, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> PARTYGOER_SPAWN_EGG =
            ITEMS.register("partygoer_spawn_egg", () -> new SpawnEggItem(
                    ThresholdEntities.PARTYGOER.get(), 0xF0D23C, 0xA91E24, new Item.Properties()));

    private ThresholdItems() {}
}
