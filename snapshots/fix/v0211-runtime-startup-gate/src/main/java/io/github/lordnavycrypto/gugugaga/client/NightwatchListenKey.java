package io.github.lordnavycrypto.gugugaga.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/** Real remappable Controls binding, not hard-coded key polling. */
public final class NightwatchListenKey {
    private NightwatchListenKey(){}
    public static final KeyMapping LISTEN=new KeyMapping(
            "key.gugugaga.listen",InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,"key.categories.gugugaga");

    @EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT,
            bus=EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent event) {
            event.register(LISTEN);
        }
    }

    @EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
    public static final class Input {
        @SubscribeEvent
        public static void tick(ClientTickEvent.Post event) {
            while(LISTEN.consumeClick()) {
                Minecraft mc=Minecraft.getInstance();
                // Screens/chat should never unexpectedly initiate a listen.
                if(mc.screen==null && mc.player!=null
                        && mc.player.isCrouching() && mc.getConnection()!=null)
                    mc.getConnection().sendCommand("gugugaga play listen");
            }
        }
    }
}
