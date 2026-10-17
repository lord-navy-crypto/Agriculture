package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Low-volume, low-frequency sensory cues for the Nightwatch observer.
 *
 * One optional cue decision per 10-tick server AI step. No per-tick sound
 * spam, flashing lights, forced camera movement, block edits or player damage.
 * Gameplay is deterministic when audio is muted/unavailable.
 */
public final class HauntingCuePolicy {
    public enum Cue { NONE, DISTANT_CHIME, SOFT_STEP, FAINT_PULSE }
    public static final int CHIME_PERIOD_TICKS = 240;
    public static final int STEP_PERIOD_TICKS = 160;
    public static final int PULSE_PERIOD_TICKS = 300;

    private HauntingCuePolicy(){}

    /**
     * Only apply to an active observer with an eligible survival target.
     * Entity tick offsets stagger multiple observers to avoid sync bursts.
     */
    public static Cue choose(ObserverBrain.Phase phase, boolean enabled,
            boolean targetEligible, boolean watched, double distanceSquared,
            int phaseTicks, long ticks, long entitySeed) {
        if(!enabled || !targetEligible || phase==ObserverBrain.Phase.DORMANT
                || distanceSquared>32.0*32.0 || distanceSquared<1.0)
            return Cue.NONE;
        int stagger=(int)Math.floorMod(entitySeed,8L)*10;
        if(phase==ObserverBrain.Phase.OBSERVE && watched
                && phaseTicks>=40 && distanceSquared>=64.0
                && (ticks+stagger)%CHIME_PERIOD_TICKS==0)
            return Cue.DISTANT_CHIME;
        if(phase==ObserverBrain.Phase.APPROACH && !watched
                && distanceSquared>=25.0
                && (ticks+stagger)%STEP_PERIOD_TICKS==0)
            return Cue.SOFT_STEP;
        if(phase==ObserverBrain.Phase.RETREAT
                && (ticks+stagger)%PULSE_PERIOD_TICKS==0)
            return Cue.FAINT_PULSE;
        return Cue.NONE;
    }

    public static float cappedVolume(Cue cue) {
        return switch(cue) {
            case NONE -> 0.0f;
            case DISTANT_CHIME -> 0.22f;
            case SOFT_STEP -> 0.19f;
            case FAINT_PULSE -> 0.26f;
        };
    }
}
