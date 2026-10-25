package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Server-owned, reversible threat states for the original Gugu-Gaga.
 * Hostile actions require a world-owner opt-in; local conversation,
 * the original four-phase observer and quiet-mode always remain available.
 *
 * Neither an angry nor corrupted observer can force chunks, break blocks,
 * teleport, or affect creative/spectator/non-consenting players.
 */
public final class ThreatPolicy {
    public enum Mood { CALM, ANGRY, CORRUPTED }
    public static final int ANGER_LIFETIME_TICKS = 20*65;
    public static final int CORRUPTION_LIFETIME_TICKS = 20*110;
    public static final int ATTACK_TELEGRAPH_TICKS = 30;
    public static final int ATTACK_COOLDOWN_TICKS = 75;
    public static final double ATTACK_RANGE_SQ = 2.6*2.6;
    public static final double CHASE_RANGE_SQ = 24.0*24.0;
    public static final float ANGRY_DAMAGE = 1.0f;
    public static final float CORRUPTED_DAMAGE = 2.0f;
    private ThreatPolicy(){}

    public static Mood fromOrdinal(int ordinal) {
        return ordinal>=0 && ordinal<Mood.values().length
                ? Mood.values()[ordinal] : Mood.CALM;
    }

    public static Mood afterTime(Mood current,int activeTicks) {
        if(current==Mood.ANGRY && activeTicks>=ANGER_LIFETIME_TICKS)
            return Mood.CALM;
        if(current==Mood.CORRUPTED && activeTicks>=CORRUPTION_LIFETIME_TICKS)
            return Mood.CALM;
        return current;
    }

    public static boolean canChase(Mood mood,boolean enabled,boolean validTarget,
            boolean optedOut,double distanceSquared) {
        return mood!=Mood.CALM && enabled && validTarget && !optedOut
                && Double.isFinite(distanceSquared) && distanceSquared>=0
                && distanceSquared<=CHASE_RANGE_SQ;
    }

    public static boolean canStrike(Mood mood,boolean enabled,
            boolean validTarget,boolean optedOut,boolean hasSight,
            double distanceSquared,int windupTicks,int sinceAttackTicks) {
        return canChase(mood,enabled,validTarget,optedOut,distanceSquared)
                && hasSight
                && distanceSquared<=ATTACK_RANGE_SQ
                && windupTicks>=ATTACK_TELEGRAPH_TICKS
                && sinceAttackTicks>=ATTACK_COOLDOWN_TICKS;
    }

    public static float damage(Mood mood) {
        return switch(mood) {
            case CALM -> 0.0f;
            case ANGRY -> ANGRY_DAMAGE;
            case CORRUPTED -> CORRUPTED_DAMAGE;
        };
    }
}
