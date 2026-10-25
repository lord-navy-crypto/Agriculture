import io.github.lordnavycrypto.gugugaga.ai.ColdBiomePolicy;
public final class ColdBiomePolicySmoke {
    private static void yes(boolean b,String msg){if(!b)throw new AssertionError(msg);}
    public static void main(String[] args){
        double[] climate={-2,-0.5,0,0.15,0.3,0.6,0.8,1.0,1.3,1.5,1.8,2,3};
        int previous=1001;
        for(double t:climate){
            int p=ColdBiomePolicy.sightingPermille(t);
            yes(p<=previous,"Warm climates cannot be more common than cold");
            yes(p>=0&&p<=1000,"Clamp probabilities");
            previous=p;
        }
        yes(ColdBiomePolicy.sightingPermille(-0.6)>=900,"Snow-biome penguins prevalent");
        yes(ColdBiomePolicy.sightingPermille(0.5)>=400,"Temperate occasional");
        yes(ColdBiomePolicy.sightingPermille(2.0)<=40,"Desert extremely rare");
        yes(ColdBiomePolicy.sightingPermille(Double.NaN)==0,"NaN rejected");
        long seed=0x770caafeL;
        int cold=0,warm=0,desert=0;
        for(int i=0;i<120_000;i++){
            long time=(long)i*12000;
            boolean c=ColdBiomePolicy.allow(-0.3,seed,time);
            boolean w=ColdBiomePolicy.allow(0.6,seed,time);
            boolean d=ColdBiomePolicy.allow(2.0,seed,time);
            yes(c==ColdBiomePolicy.allow(-0.3,seed,time),"Same event replay");
            cold+=c?1:0;warm+=w?1:0;desert+=d?1:0;
        }
        yes(cold>warm&&warm>desert*10,
                "Cold>temperate>desert in deterministic simulated attempts");
        System.out.println("GUGUGAGA_COLD_CLIMATE_PASS cold="+cold
                +" temperate="+warm+" desert="+desert);
    }
}
