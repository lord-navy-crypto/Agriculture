import io.github.lordnavycrypto.gugugaga.microbes.MicrobeEnvironmentPolicy;
import io.github.lordnavycrypto.gugugaga.microbes.MicrobeEnvironmentPolicy.Signal;
import io.github.lordnavycrypto.gugugaga.microbes.MicrobeLifecyclePolicy.*;
public final class MicrobeEnvironmentSmoke {
    private static int checks;
    private static void check(boolean b,String name){checks++;if(!b)throw new AssertionError(name);}
    public static void main(String[] args){
        for(Exhibit species:Exhibit.values())for(Substrate substrate:Substrate.values())
        for(boolean wet:new boolean[]{true,false})
        for(boolean light:new boolean[]{true,false})
        for(boolean water:new boolean[]{true,false}){
            Signal s=MicrobeEnvironmentPolicy.decide(species,substrate,wet,light,water);
            check(s!=null,"total game signal mapping");
            if(species==Exhibit.ECOLI||species==Exhibit.T4_PHAGE)
                check(s==Signal.HOLD,"no environmental pathogen spread");
            if(species==Exhibit.AQUATIC_CHLORELLA&&!water)
                check(s==Signal.HOLD,"algal art needs adjacent water");
        }
        check(MicrobeEnvironmentPolicy.decide(Exhibit.SOIL_BACILLUS,Substrate.LEAF,
            true,true,true)==Signal.FADE,"unsuitable soil presentation fades");
        check(MicrobeEnvironmentPolicy.decide(Exhibit.AQUATIC_CHLORELLA,
            Substrate.SOIL,true,true,true)==Signal.ENRICH,"lit water-adjacent algae art");
        check(MicrobeEnvironmentPolicy.decide(Exhibit.SOIL_NITROSOMONAS,
            Substrate.SOIL,true,false,false)==Signal.ENRICH,"wet soil exhibit");
        System.out.println("GUGUGAGA_ENVIRONMENT_ART_PASS checks="+checks
           +" noPathogenKinetics=true noPlayerInfections=true");
    }
}
