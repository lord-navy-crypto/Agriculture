package io.github.lordnavycrypto.gugugaga.ai;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * A single, gentle discovery hint, without force-opening a screen or
 * enrolling a player in a horror session on login.
 */
public final class NightwatchOnboarding {
    private static final String SEEN="GuguGagaUIIntroduced";
    private NightwatchOnboarding(){}

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player))return;
        if(player.getPersistentData().getBoolean(SEEN))return;
        player.getPersistentData().putBoolean(SEEN,true);
        player.sendSystemMessage(Component.literal(
                "[Nightwatch] Press H to open the Nightwatch panel. "
                +"Join only if you want to play; investigate by right-clicking creatures."));
    }
}
