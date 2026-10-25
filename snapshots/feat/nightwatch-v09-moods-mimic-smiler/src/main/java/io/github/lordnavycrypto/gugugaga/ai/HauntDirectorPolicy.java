package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Pure Java "dramatic director" for rare Nightwatch encounters.
 *
 * A global director only chooses the style/timing of a potential event;
 * local observer AI still decides its own movement. This distinction keeps
 * the monster unpredictable without omniscient teleports or chunk scans.
 *
 * Choices are bounded, varied and reproducible from server-owned inputs.
 * Neither player position nor sensitive content is persisted or transmitted.
 */
public final class HauntDirectorPolicy {
    public enum Event {
        SILENCE,            // intentional respite, no world or client effect
        DISTANT_CHIME,      // soft directional environmental sound
        FALSE_FOOTSTEPS,    // a footstep with no monster attached
        FADING_ECHO,        // another low-volume cue
        AFTERIMAGE,         // up to two atmospheric particles
        SIGHTING            // one ordinary bounded observer, if placement safe
    }

    private HauntDirectorPolicy() {}

    /** Three bounded encounter flavors: restrained, balanced, mysterious. */
    public static int intensity(int config) {
        return Math.max(0,Math.min(2,config));
    }

    /** Dramatic tension increases gently, and resets after an actual sighting. */
    public static int nextTension(int old,Event delivered) {
        if(delivered==Event.SIGHTING) return 0;
        if(delivered==Event.SILENCE) return Math.max(0,Math.min(3,old-1));
        return Math.min(3,Math.max(0,old)+1);
    }

    /**
     * Select an event for an ALREADY-authorized player, after the existing
     * Nightwatch 8-minute minimum cooldown, 200-tick scheduler, survivor,
     * night, dimension and quiet-mode gates have all passed.
     *
     * A previous event never repeats consecutively (except SILENCE where
     * a deliberate quiet interval is harmless). The policy does not spawn,
     * read blocks, generate chunks or perform any network calls.
     */
    public static Event choose(long playerSeed,long tick,int encounterNumber,
            int tension,int intensity,Event previous) {
        int level=intensity(intensity);
        long h=mix(playerSeed ^ tick*0x9e3779b97f4a7c15L
                ^ ((long)encounterNumber*0xc2b2ae3d27d4eb4fL)
                ^ ((long)tension<<28));
        int chance=(int)Math.floorMod(h,100L);
        // Quiet events remain possible even at the highest intensity.
        Event pick;
        if(chance<18) pick=Event.SILENCE;
        else if(chance<37) pick=Event.DISTANT_CHIME;
        else if(chance<56) pick=Event.FALSE_FOOTSTEPS;
        else if(chance<73) pick=Event.FADING_ECHO;
        else if(chance<84) pick=Event.AFTERIMAGE;
        else pick=Event.SIGHTING;

        // Increase visibility, not attacks or audio loudness, when engaged.
        if(level==2 && tension>=2 && chance>=70)
            pick=Event.SIGHTING;
        else if(level==0 && pick==Event.SIGHTING && tension<2)
            pick=Event.AFTERIMAGE;

        // Consecutive identical audio patterns make horror predictable.
        if(pick==previous && pick!=Event.SILENCE) {
            pick=switch(pick) {
                case DISTANT_CHIME -> Event.FALSE_FOOTSTEPS;
                case FALSE_FOOTSTEPS -> Event.FADING_ECHO;
                case FADING_ECHO -> Event.AFTERIMAGE;
                case AFTERIMAGE -> Event.DISTANT_CHIME;
                case SIGHTING -> Event.DISTANT_CHIME;
                case SILENCE -> Event.SILENCE;
            };
        }
        return pick;
    }

    /** Deterministic small positioning jitter, never exposes a player path. */
    public static double bearing(long eventSeed,int salt) {
        long h=mix(eventSeed+salt*0x9e3779b97f4a7c15L);
        return Math.floorMod(h,65536L)*(Math.PI*2.0/65536.0);
    }

    public static float volume(Event event) {
        return switch(event) {
            case SILENCE, SIGHTING -> 0f;
            case DISTANT_CHIME -> 0.18f;
            case FALSE_FOOTSTEPS -> 0.16f;
            case FADING_ECHO -> 0.21f;
            case AFTERIMAGE -> 0f;
        };
    }

    private static long mix(long z) {
        z^=z>>>30;z*=0xbf58476d1ce4e5b9L;
        z^=z>>>27;z*=0x94d049bb133111ebL;
        return z^(z>>>31);
    }
}
