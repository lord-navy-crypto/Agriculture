package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** CI runtime-only evidence that the physical client really started ticking. */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
public final class ClientTickProbe {
    private static boolean reported;
    private ClientTickProbe(){}
    @SubscribeEvent
    public static void afterClientTick(ClientTickEvent.Post event) {
        if(!reported) {
            reported=true;
            System.out.println("GUGUGAGA_CLIENT_TICK_OK");
        }
    }
}
