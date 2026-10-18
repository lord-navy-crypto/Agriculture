package io.github.lordnavycrypto.gugugaga.ai;

import java.util.Locale;

/**
 * Deliberate local roleplay phrase, NOT keyword surveillance of player chats.
 * Whole-message exact match avoids interpreting unrelated chat as a summon.
 */
public final class YellowHelpChatPolicy {
    private YellowHelpChatPolicy() {}
    public static boolean isHelpRequest(String message) {
        if(message==null || message.length()>64)return false;
        String text=message.trim().replaceAll("\\s+"," ")
                .toLowerCase(Locale.ROOT);
        return text.equals("help yellow face")
                || text.equals("help yellowface")
                || text.equals("yellow face help");
    }
}
