package io.github.lordnavycrypto.gugugaga.microbes;

/**
 * Coarse Minecraft environmental ART classifications for non-pathogenic scenery.
 * No infection, actual microbial kinetics, reproduction or survival parameters.
 */
public final class MicrobeEnvironmentPolicy {
    private MicrobeEnvironmentPolicy() {}
    public enum Signal { ENRICH, HOLD, FADE }
    public static Signal decide(MicrobeLifecyclePolicy.Exhibit species,
            MicrobeLifecyclePolicy.Substrate substrate,
            boolean dampInMinecraft, boolean brightInMinecraft,
            boolean adjacentWater) {
        if(species==null||substrate==null)
            throw new IllegalArgumentException("Missing environment input");
        return switch(species) {
            case SOIL_BACILLUS, SOIL_NITROSOMONAS ->
                substrate!=MicrobeLifecyclePolicy.Substrate.SOIL
                ?Signal.FADE:(dampInMinecraft?Signal.ENRICH:Signal.HOLD);
            case PLANT_LACTIPLANTIBACILLUS ->
                substrate==MicrobeLifecyclePolicy.Substrate.LEAF
                ?Signal.ENRICH:Signal.FADE;
            case AQUATIC_CHLORELLA ->
                adjacentWater&&brightInMinecraft?Signal.ENRICH:Signal.HOLD;
            case ECOLI,T4_PHAGE -> Signal.HOLD;
        };
    }
}
