package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.agriculture.GeckoKitchenStationContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Client-only renderer registration shared by the three animated kitchen stations. */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GeckoKitchenStationClient {
    private GeckoKitchenStationClient() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GeckoKitchenStationContent.DOUGH_BENCH.get(),
                GeckoKitchenStationRenderer::new);
        event.registerBlockEntityRenderer(GeckoKitchenStationContent.SKILLET_STATION.get(),
                GeckoKitchenStationRenderer::new);
        event.registerBlockEntityRenderer(GeckoKitchenStationContent.BAMBOO_STEAMER.get(),
                GeckoKitchenStationRenderer::new);
    }
}
