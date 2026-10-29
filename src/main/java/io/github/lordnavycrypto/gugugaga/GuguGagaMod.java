package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.agriculture.AgricultureContent;
import io.github.lordnavycrypto.gugugaga.agriculture.AgricultureCuisineContent;
import io.github.lordnavycrypto.gugugaga.agriculture.BeeContent;
import io.github.lordnavycrypto.gugugaga.agriculture.BeeWildlife;
import io.github.lordnavycrypto.gugugaga.agriculture.MarineFungiContent;
import io.github.lordnavycrypto.gugugaga.agriculture.KitchenContent;
import io.github.lordnavycrypto.gugugaga.agriculture.KitchenV3Content;
import io.github.lordnavycrypto.gugugaga.agriculture.KitchenV31Content;
import io.github.lordnavycrypto.gugugaga.agriculture.KitchenV32Content;
import io.github.lordnavycrypto.gugugaga.agriculture.FoodEcologyV33Content;
import io.github.lordnavycrypto.gugugaga.agriculture.FoodV34Content;
import io.github.lordnavycrypto.gugugaga.agriculture.WaterV37Content;
import io.github.lordnavycrypto.gugugaga.agriculture.FoodEcologyFishing;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/** Clean agriculture-focused development line. Previous gameplay experiments remain on other branches. */
@Mod(GuguGagaMod.MOD_ID)
public final class GuguGagaMod {
    public static final String MOD_ID = "gugugaga";

    public GuguGagaMod(IEventBus modBus, ModContainer container) {
        AgricultureContent.BLOCKS.register(modBus);
        AgricultureContent.ITEMS.register(modBus);
        AgricultureCuisineContent.ITEMS.register(modBus);
        KitchenContent.BLOCKS.register(modBus);
        KitchenContent.ITEMS.register(modBus);
        KitchenV3Content.BLOCKS.register(modBus);
        KitchenV3Content.ITEMS.register(modBus);
        KitchenV31Content.BLOCKS.register(modBus);
        KitchenV31Content.ITEMS.register(modBus);
        KitchenV32Content.ITEMS.register(modBus);
        FoodEcologyV33Content.BLOCKS.register(modBus);
        FoodEcologyV33Content.ITEMS.register(modBus);
        FoodV34Content.BLOCKS.register(modBus);
        FoodV34Content.ITEMS.register(modBus);
        WaterV37Content.ITEMS.register(modBus);
        NeoForge.EVENT_BUS.register(FoodEcologyFishing.class);
        BeeContent.BLOCKS.register(modBus);
        BeeContent.ITEMS.register(modBus);
        MarineFungiContent.BLOCKS.register(modBus);
        MarineFungiContent.ITEMS.register(modBus);
        BeeWildlife.ENTITIES.register(modBus);
        modBus.addListener(BeeWildlife::onAttributes);
        modBus.addListener(BeeWildlife::onSpawnPlacements);
        GuguGagaCreativeTabs.TABS.register(modBus);
        System.out.println("GUGUGAGA_AGRICULTURE_RUNTIME_INIT_OK");
        System.out.println("GUGUGAGA_MARINE_FUNGI_RUNTIME_INIT_OK");
        System.out.println("GUGUGAGA_CROP_CUISINE_RUNTIME_INIT_OK");
    }
}
