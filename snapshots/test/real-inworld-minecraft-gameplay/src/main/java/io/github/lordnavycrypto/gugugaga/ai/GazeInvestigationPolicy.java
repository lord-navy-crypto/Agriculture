package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Hold a REAL nearby entity in visible line of sight for 4 seconds to
 * advance the opt-in encounter naturally. False sightings yield no clue.
 */
public final class GazeInvestigationPolicy {
    public static final int REQUIRED_FOCUS_TICKS=20*4;
    public static final double MIN_VIEW_DOT=0.955;
    private GazeInvestigationPolicy(){}
    public static boolean aimedAt(double dot,double distanceSq,
            boolean lineOfSight,boolean eligible) {
        return eligible && lineOfSight
                && Double.isFinite(dot) && dot>=MIN_VIEW_DOT
                && HauntGameplayPolicy.nearEvidence(distanceSq,true);
    }
    public static int nextTicks(int current,boolean focused) {
        return focused?Math.min(REQUIRED_FOCUS_TICKS,Math.max(0,current)+20):0;
    }
    public static boolean complete(int focusedTicks) {
        return focusedTicks>=REQUIRED_FOCUS_TICKS;
    }
}
