package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Small side-effect-free relationship priority machine for the three
 * *character identities*: Gugu-Gaga, Redactor (Lapis mask / true form),
 * and Yellow Smiler. Used only on the server at bounded cadence.
 *
 * No player information, map scan, world changes or global storyline
 * state is read here.
 */
public final class ThreeEntityReactionPolicy {
    public enum RedactorAction { IDLE_AS_LAPIS, WATCH_GUGU, WATCH_PLAYER, REVEAL_AND_APPROACH, FLEE_SMILER }
    public enum SmilerAction { STAND_BY, WATCH_GUGU, PROTECT_PLAYER, CONFRONT_GUGU }
    public enum GuguReaction { NORMAL, NOTICE_REDACTOR, FOLLOW_PLAYER }
    public static final double SMILER_TERROR_RADIUS_SQ = 24.0*24.0;
    public static final double GUGU_INTEREST_RADIUS_SQ = 22.0*22.0;
    public static final double REVEAL_RADIUS_SQ = 8.0*8.0;
    public static final double CORRUPT_RADIUS_SQ = 3.0*3.0;
    public static final int CORRUPT_INTERVAL_TICKS = 20*35;
    private ThreeEntityReactionPolicy() {}

    public static RedactorAction redactor(double nearestSmilerSq,
            double nearestGuguSq) {
        if(close(nearestSmilerSq, SMILER_TERROR_RADIUS_SQ))
            return RedactorAction.FLEE_SMILER; // dominates every action
        if(close(nearestGuguSq, REVEAL_RADIUS_SQ))
            return RedactorAction.REVEAL_AND_APPROACH;
        if(close(nearestGuguSq, GUGU_INTEREST_RADIUS_SQ))
            return RedactorAction.WATCH_GUGU;
        return RedactorAction.IDLE_AS_LAPIS;
    }

    public static SmilerAction smiler(double nearestGuguSq) {
        return close(nearestGuguSq,GUGU_INTEREST_RADIUS_SQ)
                ? SmilerAction.WATCH_GUGU : SmilerAction.STAND_BY;
    }

    public static GuguReaction gugu(double nearestRedactorSq) {
        return close(nearestRedactorSq,GUGU_INTEREST_RADIUS_SQ)
                ? GuguReaction.NOTICE_REDACTOR : GuguReaction.NORMAL;
    }

    /**
     * v0.12 player-priority policy. A visible or nearby participating player
     * outranks Redactor; the old two-argument method remains for v0.8 compatibility.
     */
    public static GuguReaction gugu(double playerSq,double redactorSq) {
        if(close(playerSq,32.0*32.0)) return GuguReaction.FOLLOW_PLAYER;
        return gugu(redactorSq);
    }

    /**
     * Equal-interest Redactor: player and Gugu compete by squared distance.
     * Only a REVEALED Smiler causes the established flight response.
     */
    public static RedactorAction redactor(double smilerSq,double guguSq,
            double playerSq,boolean smilerRevealed) {
        if(smilerRevealed && close(smilerSq,SMILER_TERROR_RADIUS_SQ))
            return RedactorAction.FLEE_SMILER;
        if(close(playerSq,REVEAL_RADIUS_SQ)
                || close(guguSq,REVEAL_RADIUS_SQ))
            return RedactorAction.REVEAL_AND_APPROACH;
        if(close(playerSq,GUGU_INTEREST_RADIUS_SQ)
                && playerSq<=guguSq) return RedactorAction.WATCH_PLAYER;
        if(close(guguSq,GUGU_INTEREST_RADIUS_SQ)) return RedactorAction.WATCH_GUGU;
        return RedactorAction.IDLE_AS_LAPIS;
    }

    /** Smiler prioritizes the penguin, supports the player, and does not fear Redactor. */
    public static SmilerAction smiler(double guguSq,double playerSq,double redactorSq) {
        if(close(guguSq,GUGU_INTEREST_RADIUS_SQ)) return SmilerAction.CONFRONT_GUGU;
        if(close(playerSq,12.0*12.0)) return SmilerAction.PROTECT_PLAYER;
        return SmilerAction.STAND_BY;
    }

    public static boolean smilerCanWardOff(double guguSq,int ticksSinceWard) {
        return close(guguSq,3.0*3.0) && ticksSinceWard>=20*8;
    }

    public static boolean mayCorrupt(boolean escaped,boolean revealed,
            double guguDistanceSq,int sinceLastCorruption) {
        return !escaped && revealed && close(guguDistanceSq,CORRUPT_RADIUS_SQ)
                && sinceLastCorruption>=CORRUPT_INTERVAL_TICKS;
    }

    private static boolean close(double squared,double max) {
        return Double.isFinite(squared) && squared>=0 && squared<=max;
    }
}
