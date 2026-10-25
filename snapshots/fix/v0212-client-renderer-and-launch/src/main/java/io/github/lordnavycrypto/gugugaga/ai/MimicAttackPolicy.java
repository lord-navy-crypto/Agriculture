package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Lapis shell stays harmless. Revealed Redactor may only give a small,
 * telegraphed hit if its SEPARATE hostility toggle was explicitly enabled.
 * Smiler fear always overrides any player combat behavior.
 */
public final class MimicAttackPolicy {
    public static final int WARNING_TICKS=20*2;
    public static final int STRIKE_COOLDOWN_TICKS=20*5;
    public static final double REVEAL_PLAYER_RANGE_SQ=4.5*4.5;
    public static final double ATTACK_RANGE_SQ=2.4*2.4;
    public static final float DAMAGE=1.0f;
    private MimicAttackPolicy(){}
    public static boolean shouldRevealToPlayer(boolean masked,double distanceSq,
            boolean hasSight,boolean validTarget,boolean smilerNearby) {
        return masked && !smilerNearby && validTarget && hasSight
                && Double.isFinite(distanceSq) && distanceSq>=0
                && distanceSq<=REVEAL_PLAYER_RANGE_SQ;
    }
    public static boolean mayHit(boolean revealed,boolean enabled,
            boolean smilerNearby,boolean validTarget,boolean quiet,
            boolean hasSight,double distanceSq,int warningTicks,int sinceStrike) {
        return revealed && enabled && !smilerNearby && validTarget && !quiet && hasSight
                && Double.isFinite(distanceSq) && distanceSq>=0
                && distanceSq<=ATTACK_RANGE_SQ && warningTicks>=WARNING_TICKS
                && sinceStrike>=STRIKE_COOLDOWN_TICKS;
    }
}
