package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.agriculture.AgricultureContent;
import io.github.lordnavycrypto.gugugaga.agriculture.AgricultureCuisineContent;
import io.github.lordnavycrypto.gugugaga.agriculture.BeeContent;
import io.github.lordnavycrypto.gugugaga.agriculture.BeeWildlife;
import io.github.lordnavycrypto.gugugaga.agriculture.MarineFungiContent;
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
