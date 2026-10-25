package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.agriculture.AgricultureContent;
import io.github.lordnavycrypto.gugugaga.agriculture.BeeContent;
import io.github.lordnavycrypto.gugugaga.agriculture.BeeWildlife;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/** Agriculture-focused development line. */
@Mod(GuguGagaMod.MOD_ID)
public final class GuguGagaMod {
    public static final String MOD_ID = "gugugaga";

    public GuguGagaMod(IEventBus modBus, ModContainer container) {
        AgricultureContent.BLOCKS.register(modBus);
        AgricultureContent.ITEMS.register(modBus);
        BeeContent.BLOCKS.register(modBus);
        BeeContent.ITEMS.register(modBus);
        BeeWildlife.ENTITIES.register(modBus);
        modBus.addListener(BeeWildlife::onAttributes);
        modBus.addListener(BeeWildlife::onSpawnPlacements);
        modBus.addListener(AgricultureContent::onCreativeTab);
        modBus.addListener(BeeContent::onCreativeTab);
        System.out.println("GUGUGAGA_AGRICULTURE_RUNTIME_INIT_OK");
    }
}
