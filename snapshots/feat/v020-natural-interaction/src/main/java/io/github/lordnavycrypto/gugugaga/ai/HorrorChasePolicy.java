package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Side-effect free bounded chase / scare tuning. Real gameplay is strictly
 * opt-in, pursuit does not teleport, edit blocks, or load unloaded chunks.
 */
public final class HorrorChasePolicy {
    public static final int CHASE_TICKS = 20 * 14;
    public static final int COOLDOWN_TICKS = 20 * 42;
    public static final int LOST_SIGHT_TICKS = 20 * 4;
    public static final int SCARE_COOLDOWN_TICKS = 20 * 55;
    public static final double DETECTION_RADIUS_SQ = 19.0 * 19.0;
    public static final double ESCAPE_RADIUS_SQ = 27.0 * 27.0;
    public static final double SCARE_RADIUS_SQ = 5.0 * 5.0;
    private HorrorChasePolicy(){}

    public static boolean canStart(boolean joinedInChaseAct,boolean chasingEnabled,
            boolean visible,double distanceSq,int cooldown) {
        return joinedInChaseAct && chasingEnabled && visible && cooldown<=0
                && valid(distanceSq) && distanceSq<=DETECTION_RADIUS_SQ
                && distanceSq>=3.0*3.0;
    }
    public static boolean canContinue(boolean activeParticipant,boolean chasingEnabled,
            double distanceSq,int remainingTicks,int unseenTicks) {
        return activeParticipant && chasingEnabled && remainingTicks>0
                && valid(distanceSq) && distanceSq<=ESCAPE_RADIUS_SQ
                && unseenTicks<LOST_SIGHT_TICKS;
    }
    public static boolean canSurprise(boolean inChaseAct,boolean scareAllowed,
            boolean visible,double distanceSq,int sincePrevious) {
        return inChaseAct && scareAllowed && visible && valid(distanceSq)
                && distanceSq<=SCARE_RADIUS_SQ
                && sincePrevious>=SCARE_COOLDOWN_TICKS;
    }
    private static boolean valid(double distanceSq) {
        return Double.isFinite(distanceSq) && distanceSq>=0;
    }
}
