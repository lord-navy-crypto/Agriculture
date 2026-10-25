import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain;
import io.github.lordnavycrypto.gugugaga.ai.ObserverBrain.Phase;

/** Standalone Java 21 regression for the logic that drives the in-world observer. */
public final class ObserverBrainSmoke {
    private static void check(boolean okay, String message) {
        if (!okay) throw new AssertionError(message);
    }

    public static void main(String[] ignored) {
        check(ObserverBrain.next(Phase.DORMANT, false, 40 * 40, 0) == Phase.DORMANT,
                "observer must never stalk players outside the 32-block evaluation radius");
        check(ObserverBrain.next(Phase.DORMANT, false, 25 * 25, 0) == Phase.OBSERVE,
                "nearby observer must begin observing");
        check(ObserverBrain.next(Phase.OBSERVE, true, 12 * 12, 500) == Phase.OBSERVE,
                "observer must not approach while watched");
        check(ObserverBrain.next(Phase.OBSERVE, false, 12 * 12, 50) == Phase.OBSERVE,
                "observer must wait for deliberate suspense cooldown");
        check(ObserverBrain.next(Phase.OBSERVE, false, 12 * 12, 60) == Phase.APPROACH,
                "observer should begin slow approach after cooldown when not watched");
        check(ObserverBrain.next(Phase.APPROACH, true, 8 * 8, 10) == Phase.RETREAT,
                "being watched at close range must break pursuit");
        check(ObserverBrain.next(Phase.APPROACH, false, 3 * 3, 10) == Phase.RETREAT,
                "close pursuit must retreat to preserve player agency");
        check(ObserverBrain.next(Phase.RETREAT, false, 12 * 12, 59) == Phase.RETREAT,
                "retreat must last until cooldown completes");
        check(ObserverBrain.next(Phase.RETREAT, false, 12 * 12, 60) == Phase.OBSERVE,
                "retreat must eventually recover to observation");
        check(ObserverBrain.next(Phase.APPROACH, false, 33 * 33, 10) == Phase.DORMANT,
                "far-away observer must stop seeking the player");
        System.out.println("ObserverBrainSmoke: 10 transitions passed");
    }
}
