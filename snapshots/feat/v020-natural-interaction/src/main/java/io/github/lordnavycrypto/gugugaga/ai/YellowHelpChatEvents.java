package io.github.lordnavycrypto.gugugaga.ai;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

/**
 * Server-authoritative chat interop. No slash command needed for HELP Yellow
 * Face. Exact phrase is kept private; no chat interception if mode is off.
 */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID)
public final class YellowHelpChatEvents {
    private YellowHelpChatEvents(){}
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        if(!NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()
                || !YellowHelpChatPolicy.isHelpRequest(event.getRawText()))
            return;
        event.setCanceled(true); // roleplay command never broadcasts publicly
        var player=event.getPlayer();
        // Schedule on logical server even if a chat-processing implementation
        // invokes the chat event off-thread. No client-side trust shortcut.
        player.serverLevel().getServer().execute(
                () -> HauntGameplayRuntime.requestSmilerHelp(player));
    }
}
