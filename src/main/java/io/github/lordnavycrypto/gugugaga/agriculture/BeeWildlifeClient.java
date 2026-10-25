package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.client.renderer.entity.BeeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Client-only registration keeps the dedicated server free from client renderer linkage. */
@EventBusSubscriber(modid="gugugaga", bus=EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public final class BeeWildlifeClient {
    private BeeWildlifeClient() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BeeWildlife.BUMBLEBEE.get(), BeeRenderer::new);
        event.registerEntityRenderer(BeeWildlife.MASON_BEE.get(), BeeRenderer::new);
        event.registerEntityRenderer(BeeWildlife.SQUASH_BEE.get(), BeeRenderer::new);
        event.registerEntityRenderer(BeeWildlife.LEAFCUTTER_BEE.get(), BeeRenderer::new);
        event.registerEntityRenderer(BeeWildlife.MINING_BEE.get(), BeeRenderer::new);
        event.registerEntityRenderer(BeeWildlife.SWEAT_BEE.get(), BeeRenderer::new);
    }
}
