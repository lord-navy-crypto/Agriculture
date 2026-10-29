package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Three stages of a reusable drinking-water route. No alcohol or chemicals. */
public final class WaterV37Content {
    private WaterV37Content() {}
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(GuguGagaMod.MOD_ID);
    public static final DeferredItem<Item> EMPTY_WATER_FLASK =
            ITEMS.register("empty_water_flask", () -> new EmptyWaterFlaskV37(new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> RAW_WATER_BOTTLE =
            ITEMS.register("raw_water_bottle", () -> new DrinkWaterV37(new Item.Properties()
                    .stacksTo(16).food(new FoodProperties.Builder().nutrition(0)
                            .saturationModifier(0).alwaysEdible().build())));
    public static final DeferredItem<Item> BOILED_WATER_BOTTLE =
            ITEMS.register("boiled_water_bottle", () -> new DrinkWaterV37(new Item.Properties()
                    .stacksTo(16).food(new FoodProperties.Builder().nutrition(0)
                            .saturationModifier(0).alwaysEdible().build())));
    public static final DeferredItem<Item> FILTERED_WATER_BOTTLE =
            ITEMS.register("filtered_water_bottle", () -> new DrinkWaterV37(new Item.Properties()
                    .stacksTo(16).food(new FoodProperties.Builder().nutrition(0)
                            .saturationModifier(0).alwaysEdible().build())));
}
