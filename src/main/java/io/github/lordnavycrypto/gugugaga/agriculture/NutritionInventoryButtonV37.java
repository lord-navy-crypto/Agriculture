package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Fixed, clickable nutrition button on the player's inventory screen. */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,value=Dist.CLIENT)
public final class NutritionInventoryButtonV37 {
    private NutritionInventoryButtonV37(){}
    @SubscribeEvent
    public static void onInventoryInit(ScreenEvent.Init.Post event) {
        if(!(event.getScreen() instanceof InventoryScreen screen))return;
        // The old +91 offset places the 72px button partly outside a
        // 320px-wide GUI (common at high Minecraft GUI Scale).
        int x=Math.max(2,Math.min(screen.width-74,screen.width/2+91));
        int y=Math.max(2,Math.min(screen.height-22,screen.height/2-82));
        event.addListener(Button.builder(Component.translatable(
                "button.gugugaga.nutrition"),button ->
                    PacketDistributor.sendToServer(new NutritionNetworkV34.Request()))
                .bounds(x,y,72,20).build());
    }
}
