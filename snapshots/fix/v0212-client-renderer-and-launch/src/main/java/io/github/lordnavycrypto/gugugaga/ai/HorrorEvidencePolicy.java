package io.github.lordnavycrypto.gugugaga.ai;

/** Real creature observations, not story revelations or fake world changes. */
public final class HorrorEvidencePolicy {
    public enum Trace {
        NONE, WATCHING_BIRD, PURSUING_BIRD,
        BLUE_DISGUISE, UNCOVERED_REDACTOR, FRIENDLY_SMILER, UNCOVERED_SMILER
    }
    private HorrorEvidencePolicy() {}

    public static Trace bird(boolean following) {
        return following ? Trace.PURSUING_BIRD : Trace.WATCHING_BIRD;
    }
    public static Trace redactor(boolean revealed) {
        return revealed ? Trace.UNCOVERED_REDACTOR : Trace.BLUE_DISGUISE;
    }
    public static Trace smiler(boolean revealed) {
        return revealed ? Trace.UNCOVERED_SMILER : Trace.FRIENDLY_SMILER;
    }
    public static String description(Trace trace) {
        return switch(trace) {
            case NONE -> "No reliable sighting. Watch before concluding.";
            case WATCHING_BIRD -> "The strange bird stands still, yet follows your movement with its gaze.";
            case PURSUING_BIRD -> "Its small steps follow a deliberate hunting rhythm.";
            case BLUE_DISGUISE -> "The blue figure holds its pose a little too perfectly.";
            case UNCOVERED_REDACTOR -> "The blue disguise has fallen; the figure's movements no longer match.";
            case FRIENDLY_SMILER -> "The smiling stranger offers help but repeatedly watches the penguin.";
            case UNCOVERED_SMILER -> "Even without the friendly mask, the stranger's attention stays on the penguin.";
        };
    }
}
