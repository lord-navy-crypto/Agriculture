package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Search-at-last-seen-position. Never aim at an obscured player's live coords.
 * Owns only transient target memory; it does not change pathfinding or chunks.
 */
public final class LastSeenSearchPolicy {
    public static final int SEARCH_TICKS=20*3;
    private LastSeenSearchPolicy() {}
    public static boolean useLastSeen(boolean currentlyVisible,boolean hasLastSeen,
            int unseenTicks,boolean stillChasing) {
        return stillChasing && !currentlyVisible && hasLastSeen
                && unseenTicks>0 && unseenTicks<HorrorChasePolicy.LOST_SIGHT_TICKS;
    }
    public static boolean beginSearch(boolean sightLost,int unseenTicks,
            boolean hasLastSeen,boolean stillEligible) {
        return stillEligible && sightLost && hasLastSeen
                && unseenTicks>=HorrorChasePolicy.LOST_SIGHT_TICKS;
    }
}
