package io.github.lordnavycrypto.gugugaga.agriculture;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/** The default N key opens a server-authored, textual nutrition dashboard. */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class NutritionKeyV34 {
    private NutritionKeyV34() {}
    public static final KeyMapping SHOW=new KeyMapping(
            "key.gugugaga.nutrition",InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,"key.categories.gugugaga");
    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(SHOW);
    }
}
