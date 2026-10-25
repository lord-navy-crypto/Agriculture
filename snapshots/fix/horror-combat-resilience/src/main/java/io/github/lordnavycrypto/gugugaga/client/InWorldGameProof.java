package io.github.lordnavycrypto.gugugaga.client;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.ai.E2EGameplayProof;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Only reports the fact that an actual client is controlling a player in a world. */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
public final class InWorldGameProof {
    private static boolean seen;
    private InWorldGameProof(){}
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if(!seen) {
            Minecraft mc=Minecraft.getInstance();
            if(mc.player!=null && mc.level!=null
                    && mc.getSingleplayerServer()!=null && mc.screen==null) {
                seen=true;
                E2EGameplayProof.record("WORLD_PLAYER_READY");
            }
        }
    }
}
