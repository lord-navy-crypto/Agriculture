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
    public enum RedactorAction { IDLE_AS_LAPIS, WATCH_GUGU, REVEAL_AND_APPROACH, FLEE_SMILER }
    public enum SmilerAction { STAND_BY, WATCH_GUGU }
    public enum GuguReaction { NORMAL, NOTICE_REDACTOR }
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

    public static boolean mayCorrupt(boolean escaped,boolean revealed,
            double guguDistanceSq,int sinceLastCorruption) {
        return !escaped && revealed && close(guguDistanceSq,CORRUPT_RADIUS_SQ)
                && sinceLastCorruption>=CORRUPT_INTERVAL_TICKS;
    }

    private static boolean close(double squared,double max) {
        return Double.isFinite(squared) && squared>=0 && squared<=max;
    }
}
