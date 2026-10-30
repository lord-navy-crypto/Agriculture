package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.minecraft.network.chat.Component;

/**
 * Crisp, single-line pointer to the nutrition panel. Width is based on the
 * translated text rather than a hardcoded 104px, and fits the scaled viewport.
 */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
public final class NutritionHudV37 {
    private NutritionHudV37(){}

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft=Minecraft.getInstance();
        if(minecraft.player==null ||minecraft.options.hideGui ||minecraft.screen!=null)return;
        var graphics=event.getGuiGraphics();
        int maxTextWidth=Math.max(0,graphics.guiWidth()-24);
        if(maxTextWidth<24)return;
        String full=Component.translatable("hud.gugugaga.nutrition_hint").getString();
        String label=minecraft.font.plainSubstrByWidth(full,maxTextWidth);
        int panelWidth=minecraft.font.width(label)+10;
        int x=Math.max(4,graphics.guiWidth()-panelWidth-8);
        graphics.fill(x,8,x+panelWidth,23,0xDE1A3027);
        graphics.drawString(minecraft.font,label,x+5,12,0xFFEDFBE6,false);
    }
}
