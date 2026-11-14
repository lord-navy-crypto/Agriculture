package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Key press only sends a request; client never fabricates nutrition scores. */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
public final class NutritionKeyTickV34 {
    private NutritionKeyTickV34() {}
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        while (NutritionKeyV34.SHOW.consumeClick()) {
            if (Minecraft.getInstance().player!=null
                    && Minecraft.getInstance().screen==null) {
                PacketDistributor.sendToServer(new NutritionNetworkV34.Request());
            }
        }
    }
}
