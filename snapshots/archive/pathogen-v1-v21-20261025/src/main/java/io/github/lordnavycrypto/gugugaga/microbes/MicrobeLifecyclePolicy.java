package io.github.lordnavycrypto.gugugaga.microbes;

/**
 * Pure-Java policy for GAME-ART colony stages, strictly not a biological model.
 * Biological replication, transmission coefficients, host sensitivity and
 * environmental pathogen persistence are NOT inferred by this class.
 */
public final class MicrobeLifecyclePolicy {
    private MicrobeLifecyclePolicy(){}

    public enum Exhibit { SOIL_BACILLUS, PLANT_LACTIPLANTIBACILLUS, SOIL_NITROSOMONAS, AQUATIC_CHLORELLA, ECOLI, T4_PHAGE }
    public enum Substrate { SOIL, LEAF, OTHER }

    /** nextView is a purely visual progression. attemptSatellite is max ONE local decoration. */
    public record Outcome(int nextView,boolean attemptSatellite,boolean hostLinked) {}

    public static boolean canBranch(Exhibit species,Substrate below) {
        return switch(species) {
            case SOIL_BACILLUS, SOIL_NITROSOMONAS -> below==Substrate.SOIL;
            case PLANT_LACTIPLANTIBACILLUS -> below==Substrate.LEAF;
            case ECOLI,T4_PHAGE,AQUATIC_CHLORELLA -> false;
        };
    }

    public static Outcome advance(Exhibit species,int view,boolean satellite,
            Substrate substrate,boolean nearDocumentedHost) {
        if(species==null || substrate==null || view<0 || view>2)
            throw new IllegalArgumentException("Invalid ecology art state");
        if(canBranch(species,substrate)) {
            // A three-frame Minecraft animation, with a strictly capped one-child branch.
            return new Outcome(Math.min(2,view+1),view==2&&!satellite,false);
        }
        if(species==Exhibit.AQUATIC_CHLORELLA) {
            return new Outcome(Math.min(2,view+1),false,false);
        }
        if(species==Exhibit.T4_PHAGE || species==Exhibit.ECOLI) {
            // Host relationship cues only in a player-built exhibit.
            // Neither taxa auto-create world blocks or simulate infections.
            return new Outcome(nearDocumentedHost?(view+1)%3:0,
                    false,nearDocumentedHost);
        }
        return new Outcome(view,false,false);
    }
}
