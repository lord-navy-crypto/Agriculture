package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Pure deterministic transition rules: no world scanning or client state.
 * One server-owned entity updates at most once every 10 ticks.
 */
public final class ObserverBrain {
    private ObserverBrain() {}

    public enum Phase { DORMANT, OBSERVE, APPROACH, RETREAT }

    public static Phase next(Phase phase, boolean watched, double distanceSquared, int phaseTicks) {
        if (distanceSquared > 32 * 32) return Phase.DORMANT;
        return switch (phase) {
            case DORMANT -> Phase.OBSERVE;
            case OBSERVE -> (!watched && distanceSquared > 6 * 6 && phaseTicks >= 60)
                    ? Phase.APPROACH : Phase.OBSERVE;
            case APPROACH -> (distanceSquared <= 3.5 * 3.5
                    || (watched && distanceSquared <= 10 * 10))
                    ? Phase.RETREAT : Phase.APPROACH;
            case RETREAT -> phaseTicks >= 60 ? Phase.OBSERVE : Phase.RETREAT;
        };
    }
}
