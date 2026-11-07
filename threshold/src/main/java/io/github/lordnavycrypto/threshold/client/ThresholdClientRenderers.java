package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.threshold.block.ThresholdBlockEntities;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = GuguGagaMod.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ThresholdClientRenderers {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ThresholdBlockEntities.CEILING_VENT.get(),
                context -> new ThresholdVentRenderer());
        event.registerEntityRenderer(ThresholdEntities.WATCHER.get(), ThresholdWatcherRenderer::new);
        event.registerEntityRenderer(ThresholdEntities.HOUND.get(), ThresholdHoundRenderer::new);
        event.registerEntityRenderer(ThresholdEntities.PARTYGOER.get(), ThresholdPartygoerRenderer::new);
    }

    private ThresholdClientRenderers() {}
}
