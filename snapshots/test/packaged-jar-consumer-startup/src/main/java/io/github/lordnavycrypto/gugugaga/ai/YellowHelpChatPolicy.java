package io.github.lordnavycrypto.gugugaga.ai;

/**
 * A deliberately exact, player-authored uppercase chat word.
 * Never intercept "help" lowercase, sentences, or normal conversation.
 */
public final class YellowHelpChatPolicy {
    private YellowHelpChatPolicy() {}
    public static boolean isHelpRequest(String message) {
        return message!=null && message.trim().equals("HELP");
    }
}
