package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.minecraft.network.chat.Component;

/** Small always-visible pointer to the N key; clickable access is in inventory. */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
public final class NutritionHudV37 {
    private NutritionHudV37(){}
    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft=Minecraft.getInstance();
        if(minecraft.player==null ||minecraft.options.hideGui ||minecraft.screen!=null)return;
        var graphics=event.getGuiGraphics();
        int x=graphics.guiWidth()-112;
        graphics.fill(x,8,x+104,23,0xA72A3530);
        graphics.drawString(minecraft.font,Component.translatable(
                "hud.gugugaga.nutrition_hint"),x+5,12,0xFFDAF0D8,false);
    }
}
