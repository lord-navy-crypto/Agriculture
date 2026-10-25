package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Fictional persuasion is a finite per-session game counter, NOT a forced
 * allegiance/ending. Each player can refuse Smiler aid and advice.
 */
public final class SmilerTrustPolicy {
    public static final int MAX_FAVORS=5;
    public static final int FAVOR_COOLDOWN_TICKS=20*65;
    public static final int CALL_COOLDOWN_TICKS=20*15;
    public static final double CALL_RANGE_SQ=12.0*12.0;
    public static final double BIRD_RANGE_SQ=22.0*22.0;
    private SmilerTrustPolicy(){}

    public static boolean canRequestAid(boolean participant,boolean aidAllowed,
            boolean canSeeSmiler,double smilerSq,double birdSq,int sinceCall) {
        return participant && aidAllowed && canSeeSmiler
                && Double.isFinite(smilerSq) && smilerSq>=0
                && Double.isFinite(birdSq) && birdSq>=0
                && smilerSq<=CALL_RANGE_SQ && birdSq<=BIRD_RANGE_SQ
                && sinceCall>=CALL_COOLDOWN_TICKS;
    }

    public static int afterFavor(int prior,int sinceLastFavor) {
        return sinceLastFavor<FAVOR_COOLDOWN_TICKS ? clamp(prior)
                : Math.min(MAX_FAVORS,clamp(prior)+1);
    }
    public static int clamp(int n){return Math.max(0,Math.min(MAX_FAVORS,n));}

    public static String advice(int favors) {
        return switch(clamp(favors)) {
            case 0 -> "I could help you. Keep your eyes on that bird.";
            case 1 -> "Remember who offered you light when you had none.";
            case 2 -> "The penguin keeps returning. I think it wants you.";
            case 3 -> "The blue stranger is no ally, either. Look closely.";
            case 4 -> "I have driven the penguin away before. Trust your own eyes.";
            default -> "I can stand against the bird. You must decide what comes next.";
        };
    }
}
