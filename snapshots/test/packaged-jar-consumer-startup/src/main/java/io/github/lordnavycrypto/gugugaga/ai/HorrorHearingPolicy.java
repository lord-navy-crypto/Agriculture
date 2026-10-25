package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Fair hearing for the opted-in penguin chase. A concealed, MOVING runner
 * reveals only the position from which they made noise. Sneaking or stopping
 * grants an actual tactical escape. Never learns a silent hidden target.
 */
public final class HorrorHearingPolicy {
    public static final double HEAR_RADIUS_SQ=18.0*18.0;
    public static final double MAX_VERTICAL_DISTANCE=7.0;
    public static final double MIN_HORIZONTAL_SPEED_SQ=0.012;
    private HorrorHearingPolicy(){}

    public static boolean hearsRunner(boolean activeParticipant,boolean concealed,
            boolean sprinting,boolean crouching,double horizontalSpeedSq,
            double distanceSq,double verticalDistance) {
        return activeParticipant && concealed && sprinting && !crouching
                && Double.isFinite(horizontalSpeedSq)
                && horizontalSpeedSq>=MIN_HORIZONTAL_SPEED_SQ
                && Double.isFinite(distanceSq) && distanceSq>=0
                && distanceSq<=HEAR_RADIUS_SQ
                && Double.isFinite(verticalDistance)
                && Math.abs(verticalDistance)<=MAX_VERTICAL_DISTANCE;
    }
}
