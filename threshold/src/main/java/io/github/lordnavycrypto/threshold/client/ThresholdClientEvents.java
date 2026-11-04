package io.github.lordnavycrypto.threshold.client;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Client-only entity renderer registration.
 *
 * The entity types and spawn rules are common/server-side, but GeckoLib entity
 * renderers must be registered on the MOD event bus on the physical client.
 * Keeping this subscriber client-gated avoids loading Minecraft client classes
 * on a dedicated server.
 */
@EventBusSubscriber(
        modid = ThresholdMod.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ThresholdClientEvents {
    private ThresholdClientEvents() {
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ThresholdEntities.HOUND.get(), ThresholdHoundRenderer::new);
        event.registerEntityRenderer(ThresholdEntities.WATCHER.get(), ThresholdWatcherRenderer::new);
        event.registerEntityRenderer(ThresholdEntities.PARTYGOER.get(), ThresholdPartygoerRenderer::new);
        System.out.println("THRESHOLD_HOUND_WATCHER_PARTYGOER_RENDERERS_REGISTERED");
    }
}
