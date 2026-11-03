package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.agriculture.GeckoKneadingBenchContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Client-only renderer registration; this class is never loaded on a dedicated server. */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GeckoKneadingBenchClient {
    private GeckoKneadingBenchClient() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GeckoKneadingBenchContent.DOUGH_BENCH.get(),
                GeckoKneadingBenchRenderer::new);
    }
}
