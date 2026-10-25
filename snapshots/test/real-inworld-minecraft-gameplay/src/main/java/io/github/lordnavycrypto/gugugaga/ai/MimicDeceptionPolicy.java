package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Disguised Redactor is imperfect: it watches its watcher, and creeps only
 * while ignored. Both actions require actual line of sight to an opted-in
 * survival player. No teleports, knowledge-through-walls or fake objects.
 */
public final class MimicDeceptionPolicy {
    public static final double CREEP_MAX_SQ=20.0*20.0;
    public static final double CREEP_MIN_SQ=8.0*8.0;
    public static final double TELL_RANGE_SQ=15.0*15.0;
    public static final int TELL_COOLDOWN_TICKS=20*42;
    public static final int CREEP_PATH_INTERVAL_TICKS=40;
    private MimicDeceptionPolicy(){}

    public static boolean shouldCreep(boolean masked,boolean participant,
            boolean lineOfSight,boolean playerStaring,double distanceSq,int tickCount) {
        return masked && participant && lineOfSight && !playerStaring
                && valid(distanceSq) && distanceSq>=CREEP_MIN_SQ
                && distanceSq<=CREEP_MAX_SQ
                && tickCount%CREEP_PATH_INTERVAL_TICKS==0;
    }

    public static boolean canShowTell(boolean masked,boolean participant,
            boolean lineOfSight,boolean playerStaring,double distanceSq,
            int ticksSinceTell) {
        return masked && participant && lineOfSight && playerStaring
                && valid(distanceSq) && distanceSq<=TELL_RANGE_SQ
                && ticksSinceTell>=TELL_COOLDOWN_TICKS;
    }

    private static boolean valid(double d){return Double.isFinite(d)&&d>=0;}
}
