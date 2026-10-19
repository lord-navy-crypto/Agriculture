package io.github.lordnavycrypto.gugugaga.folklore;

/**
 * One ORIGINAL Gugu-Gaga event is a THREE-ACTOR narrative, not three
 * independent creature cases. This pure policy is for game/server integration.
 *
 * 1) Gugu, the uneasy witness
 * 2) Lapis/Redactor, the blue false-safe mask
 * 3) Yellow Smiler, the apparent helper with a suspicious second nature
 *
 * Players reveal evidence by using fictional Minecraft tools and by
 * interacting with real actors, never by issuing commands. The runtime
 * must first validate player consent, proximity and entity identity.
 */
public final class OriginalTrioCasePolicy {
    private OriginalTrioCasePolicy() {}

    public enum Actor { GUGU_GAGA, LAPIS_REDACTOR, YELLOW_SMILER }
    public enum Beat { FIRST_SIGHTING, FALSE_SAFETY, STRANGER_SMILES, TRUTH_READY }

    public static Actor expectedActor(Beat beat) {
        return switch(beat) {
            case FIRST_SIGHTING -> Actor.GUGU_GAGA;
            case FALSE_SAFETY -> Actor.LAPIS_REDACTOR;
            case STRANGER_SMILES -> Actor.YELLOW_SMILER;
            case TRUTH_READY -> null;
        };
    }

    /** Wrong-order clicks cause no irreversible scene transition. */
    public static Beat witness(Beat current, Actor actualEntity) {
        if(expectedActor(current)!=actualEntity)return current;
        return switch(current) {
            case FIRST_SIGHTING -> Beat.FALSE_SAFETY;
            case FALSE_SAFETY -> Beat.STRANGER_SMILES;
            case STRANGER_SMILES,TRUTH_READY -> Beat.TRUTH_READY;
        };
    }

    public static boolean mayConclude(Beat beat) {
        return beat==Beat.TRUTH_READY;
    }
}
