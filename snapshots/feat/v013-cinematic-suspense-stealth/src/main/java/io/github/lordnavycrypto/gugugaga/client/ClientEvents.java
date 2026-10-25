package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ObserverRenderer.LAYER, ObserverModel::createBodyLayer);
        event.registerLayerDefinition(RedactorRenderer.LAYER,RedactorModel::createBodyLayer);
        event.registerLayerDefinition(YellowSmilerRenderer.LAYER,YellowSmilerModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GuguGagaMod.OBSERVER.get(), ObserverRenderer::new);
        event.registerEntityRenderer(GuguGagaMod.REDACTOR.get(),RedactorRenderer::new);
        event.registerEntityRenderer(GuguGagaMod.YELLOW_SMILER.get(),YellowSmilerRenderer::new);
    }
}
