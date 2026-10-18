package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Strict tiny-budget cosmetic hints for real monster actions.
 * Never spawns actors, changes world blocks, adds screen effects or forces
 * chunk loading. Particles are entirely optional in server config.
 */
public final class CinematicVfxPolicy {
    public enum Event { LAPIS_TELL, SMILER_WARD, BIRD_RETREAT }
    public static final int MAX_PARTICLES_PER_EVENT=4;
    private CinematicVfxPolicy(){}

    public static int count(Event event,boolean particlesEnabled,
            boolean liveAction,boolean realLineOfSight) {
        if(event==null || !particlesEnabled || !liveAction || !realLineOfSight)
            return 0;
        return switch(event) {
            case LAPIS_TELL,BIRD_RETREAT -> 2;
            case SMILER_WARD -> MAX_PARTICLES_PER_EVENT;
        };
    }
}
