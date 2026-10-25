package io.github.lordnavycrypto.gugugaga.ai;

/** Deliberate crouched listening; never detects unloaded or nonexistent mobs. */
public final class HorrorListeningPolicy {
    public static final long COOLDOWN_TICKS=20L*12L;
    public static final double MAX_DISTANCE_SQ=20.0*20.0;
    public enum Source { NONE, WATCHER, BLUE, SMILER }
    public enum Direction { AHEAD, BEHIND, LEFT, RIGHT }
    private HorrorListeningPolicy(){}

    public static boolean mayListen(boolean optedIn,boolean crouching,
            long now,long previousTick) {
        return optedIn && crouching && now>=0
                && (previousTick<0 || now<previousTick
                || now-previousTick>=COOLDOWN_TICKS);
    }
    public static boolean audible(double distanceSq) {
        return Double.isFinite(distanceSq) && distanceSq>=0
                && distanceSq<=MAX_DISTANCE_SQ;
    }
    public static Direction direction(double lookX,double lookZ,double dx,double dz) {
        double forward=lookX*dx+lookZ*dz;
        double right=lookZ*dx-lookX*dz;
        if(Math.abs(forward)>=Math.abs(right))
            return forward>=0?Direction.AHEAD:Direction.BEHIND;
        return right>=0?Direction.RIGHT:Direction.LEFT;
    }
    public static String hint(Source src,Direction direction,double distanceSq) {
        if(src==Source.NONE || !audible(distanceSq))
            return "Nothing nearby answers your silence.";
        String range=distanceSq<=8*8?"Close: ":"Distant: ";
        String sound=switch(src){
            case WATCHER -> "short, uneven footfalls";
            case BLUE -> "a brittle mineral scrape";
            case SMILER -> "two perfectly spaced taps";
            case NONE -> "no clear movement";
        };
        String side=switch(direction){
            case AHEAD -> "ahead";
            case BEHIND -> "behind you";
            case LEFT -> "to the left";
            case RIGHT -> "to the right";
        };
        return range+sound+" "+side+".";
    }
}
