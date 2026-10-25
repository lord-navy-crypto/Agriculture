package io.github.lordnavycrypto.gugugaga.ai;

/**
 * Three voluntary investigation positions: trust the lost Guardian,
 * trust the Redactor's misleading account, or reserve judgment.
 *
 * Choosing a side never changes real-world settings, mob hostility,
 * player inventory or player access to investigation clues.
 */
public final class FactionPolicy {
    public enum Side { UNDECIDED, GUARDIAN, REDACTOR }
    private FactionPolicy(){}

    public static Side decode(int ordinal) {
        return ordinal>=0&&ordinal<Side.values().length
                ? Side.values()[ordinal] : Side.UNDECIDED;
    }

    public static String name(Side side) {
        return switch(side) {
            case UNDECIDED -> "Uncommitted witness";
            case GUARDIAN -> "Guardian's witness";
            case REDACTOR -> "Redactor's witness";
        };
    }

    public static String reveal(Side side,int clues) {
        if(clues<3) return "Three pieces of evidence remain before the contradiction becomes clear.";
        return switch(side) {
            case UNDECIDED ->
                    "The figure in the snow was watching the thing that followed you.";
            case GUARDIAN ->
                    "Gugu-Gaga wanted to warn you: the Redactor has been altering the clues.";
            case REDACTOR ->
                    "The Redactor called the penguin the villain. But the notes disagree.";
        };
    }
}
