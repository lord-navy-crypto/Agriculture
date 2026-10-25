package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Adapt *existing* sparse cues to caves or fully covered interiors.
 * There are no new cue rolls, forced chunk loads or extra sound volume.
 */
public final class DepthSuspensePolicy {
    private DepthSuspensePolicy() {}
    public static CinematicSuspensePolicy.Cue adapt(
            CinematicSuspensePolicy.Cue original,boolean underCover,long seed) {
        if(original==null)return CinematicSuspensePolicy.Cue.NONE;
        if(!underCover)return original;
        return switch(original) {
            case DISTANT_CHIME -> CinematicSuspensePolicy.Cue.HOLLOW_TAP;
            case FALSE_STEP -> Math.floorMod(seed,4L)==0L
                    ? CinematicSuspensePolicy.Cue.SILENCE
                    : CinematicSuspensePolicy.Cue.FALSE_STEP;
            case HOLLOW_TAP -> CinematicSuspensePolicy.Cue.HOLLOW_TAP;
            case SILENCE -> CinematicSuspensePolicy.Cue.SILENCE;
            case NONE -> CinematicSuspensePolicy.Cue.NONE;
        };
    }
}
