package io.github.lordnavycrypto.gugugaga.ai;

/**
 * A film-inspired tension score, NOT scripted guaranteed jumpscares.
 * Deterministic and side-effect-free; no player coordinates or global scan.
 * Silence, misleading sounds and escalating motifs are separate from AI sight.
 */
public final class CinematicSuspensePolicy {
    public enum Cue { NONE, FALSE_STEP, HOLLOW_TAP, DISTANT_CHIME, SILENCE }
    public static final int CUE_PERIOD_TICKS = 20 * 28;
    public static final float MAX_VOLUME = 0.18f;
    private CinematicSuspensePolicy() {}

    public static Cue choose(HauntGameplayPolicy.Act act,int actTicks,
            int completedLoops,long playerSeed) {
        if(act==null || act==HauntGameplayPolicy.Act.STILLNESS
                || act==HauntGameplayPolicy.Act.RESPITE
                || actTicks<=0 || actTicks%CUE_PERIOD_TICKS!=0)
            return Cue.NONE;
        // Stable across servers, independently staggered by player and round.
        long key = (playerSeed >>> 11) ^ (long)completedLoops * 0x9E3779B97F4A7C15L
                ^ ((long)actTicks / CUE_PERIOD_TICKS) * 0xBF58476D1CE4E5B9L
                ^ act.ordinal();
        int roll=(int)Math.floorMod(key,8L);
        return switch(act) {
            case WHISPERS -> roll<3 ? Cue.FALSE_STEP
                    : roll<5 ? Cue.SILENCE : roll<7 ? Cue.HOLLOW_TAP : Cue.NONE;
            case WITNESS -> roll<2 ? Cue.SILENCE
                    : roll<5 ? Cue.DISTANT_CHIME : roll<7 ? Cue.FALSE_STEP : Cue.NONE;
            case FALSE_SAFETY -> roll<2 ? Cue.HOLLOW_TAP
                    : roll<5 ? Cue.FALSE_STEP : roll<7 ? Cue.SILENCE : Cue.NONE;
            case STILLNESS,RESPITE -> Cue.NONE;
        };
    }

    public static float volume(Cue cue) {
        return switch(cue) {
            case NONE,SILENCE -> 0f;
            case FALSE_STEP -> 0.09f;
            case HOLLOW_TAP -> 0.13f;
            case DISTANT_CHIME -> MAX_VOLUME;
        };
    }
}
