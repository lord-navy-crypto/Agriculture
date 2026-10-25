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

/** A native remappable Minecraft key; never simulates chat or commands. */
public final class NightwatchKey {
    private NightwatchKey(){}
    public static final KeyMapping MENU=new KeyMapping(
            "key.gugugaga.menu",InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,"key.categories.gugugaga");

    @EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT,
            bus=EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent event) {
            event.register(MENU);
            System.out.println("GUGUGAGA_V010_UI_KEY_REGISTERED");
        }
    }

    @EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
    public static final class Input {
        @SubscribeEvent
        public static void tick(ClientTickEvent.Post event) {
            while(MENU.consumeClick()) {
                Minecraft client=Minecraft.getInstance();
                if(client.screen==null && client.player!=null
                        && client.getConnection()!=null)
                    client.setScreen(new NightwatchScreen());
            }
        }
    }
}
