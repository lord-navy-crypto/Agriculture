package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Sparse real-monster-only cave pressure. Sound never indicates imaginary
 * nearby threats. A quiet cue is sent only to the joined player, not the room.
 */
public final class CaveThreatSoundPolicy {
    public enum Pressure { NONE, DISTANT, NEAR, IMMEDIATE }
    public static final int PERIOD_TICKS=20*12;
    public static final float MAX_VOLUME=0.16f;
    private CaveThreatSoundPolicy(){}
    public static Pressure classify(boolean participant,boolean underCover,
            boolean realThreat,double nearestSq) {
        if(!participant||!underCover||!realThreat
                ||!Double.isFinite(nearestSq)||nearestSq<0||nearestSq>18*18)
            return Pressure.NONE;
        return nearestSq<=5*5?Pressure.IMMEDIATE
                :nearestSq<=10*10?Pressure.NEAR:Pressure.DISTANT;
    }
    public static boolean due(int stageTicks) {
        return stageTicks>0 && stageTicks%PERIOD_TICKS==0;
    }
    public static float volume(Pressure pressure){
        return switch(pressure){
            case NONE -> 0f;
            case DISTANT -> 0.055f;
            case NEAR -> 0.11f;
            case IMMEDIATE -> MAX_VOLUME;
        };
    }
    public static float pitch(Pressure pressure){
        return switch(pressure){
            case NONE -> 1f;
            case DISTANT -> 0.77f;
            case NEAR -> 0.62f;
            case IMMEDIATE -> 0.50f;
        };
    }
}
