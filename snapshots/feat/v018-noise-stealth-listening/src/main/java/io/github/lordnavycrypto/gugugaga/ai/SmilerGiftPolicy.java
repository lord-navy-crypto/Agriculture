package io.github.lordnavycrypto.gugugaga.ai;

/** Player-requested harmless help, globally bounded per player. */
public final class SmilerGiftPolicy {
    public static final long HELP_COOLDOWN_TICKS=20L*180L;
    public static final int TORCH_COUNT=2;
    private SmilerGiftPolicy(){}
    public static boolean mayHelp(boolean revealed,boolean validPlayer,long now,
            long lastHelp) {
        return !revealed && validPlayer && now>=0
                && (lastHelp<0 || now<lastHelp
                || now-lastHelp>=HELP_COOLDOWN_TICKS);
    }
}
