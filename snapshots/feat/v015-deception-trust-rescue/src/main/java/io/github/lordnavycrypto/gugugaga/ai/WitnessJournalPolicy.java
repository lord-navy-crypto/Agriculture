package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Pure Java clue progression for the in-game Nightwatch witness journal.
 *
 * Only three bits of evidence exist. No coordinates, names, chat content,
 * inventories, player IPs or external services are recorded.
 * An evidence entry can unlock only once and never changes the world.
 */
public final class WitnessJournalPolicy {
    public enum Evidence {
        NONE(0),
        THE_GAZE(1),
        THE_FOOTSTEPS(2),
        THE_RETREAT(4);
        public final int bit;
        Evidence(int bit) {this.bit=bit;}
    }

    public static final int ALL_BITS=7;
    private WitnessJournalPolicy(){}

    /** See, lose track of, and finally watch the figure retreat. */
    public static Evidence observe(ObserverBrain.Phase phase,
            boolean watched,double distanceSquared,boolean justRetreated) {
        if(phase==null || distanceSquared>32.0*32.0
                || distanceSquared<0 || Double.isNaN(distanceSquared))
            return Evidence.NONE;
        if(justRetreated && phase==ObserverBrain.Phase.RETREAT
                && distanceSquared<=14.0*14.0)
            return Evidence.THE_RETREAT;
        if(phase==ObserverBrain.Phase.APPROACH && !watched
                && distanceSquared>=5.0*5.0
                && distanceSquared<=24.0*24.0)
            return Evidence.THE_FOOTSTEPS;
        if(phase==ObserverBrain.Phase.OBSERVE && watched
                && distanceSquared>=8.0*8.0
                && distanceSquared<=28.0*28.0)
            return Evidence.THE_GAZE;
        return Evidence.NONE;
    }

    /** Only new evidence returns a new mask. Repeated sightings are free. */
    public static int unlock(int mask,Evidence evidence) {
        return (mask & ALL_BITS) | evidence.bit;
    }

    public static boolean isNew(int mask,Evidence evidence) {
        return evidence!=Evidence.NONE && (mask&evidence.bit)==0;
    }

    public static int discovered(int mask) {
        return Integer.bitCount(mask&ALL_BITS);
    }

    public static String name(Evidence evidence) {
        return switch(evidence) {
            case NONE -> "";
            case THE_GAZE -> "The Gaze";
            case THE_FOOTSTEPS -> "The Unseen Steps";
            case THE_RETREAT -> "The Empty Footprints";
        };
    }

    public static String description(Evidence evidence) {
        return switch(evidence) {
            case NONE -> "";
            case THE_GAZE -> "It faced you before you knew where to look.";
            case THE_FOOTSTEPS -> "The soft steps stopped when you turned.";
            case THE_RETREAT -> "It withdrew. Nothing had touched the ground.";
        };
    }
}
