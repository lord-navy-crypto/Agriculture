import io.github.lordnavycrypto.gugugaga.microbes.MicrobeLifecyclePolicy;
import io.github.lordnavycrypto.gugugaga.microbes.MicrobeLifecyclePolicy.*;

public final class MicrobeLifecycleSmoke {
    private static int checks;
    private static void check(boolean good,String why){
        checks++;if(!good)throw new AssertionError(why);
    }
    public static void main(String[] args) {
        for(Exhibit e:Exhibit.values()){
            for(Substrate b:Substrate.values()){
                for(int phase=0;phase<3;phase++){
                    for(boolean satellite:new boolean[]{false,true}){
                        var out=MicrobeLifecyclePolicy.advance(e,phase,satellite,b,false);
                        check(out.nextView()>=0&&out.nextView()<=2,"bounded visual frames");
                        if(e==Exhibit.ECOLI || e==Exhibit.T4_PHAGE){
                            check(!out.attemptSatellite(),"viral and gut display never spread");
                            check(!out.hostLinked(),"no host event without documented neighbor");
                        }
                        if(satellite)check(!out.attemptSatellite(),"child cannot branch");
                        if(out.attemptSatellite()){
                            check(phase==2&&!satellite,"one branching opportunity");
                            check(MicrobeLifecyclePolicy.canBranch(e,b),"substrate required");
                        }
                    }
                }
            }
        }
        check(MicrobeLifecyclePolicy.canBranch(Exhibit.SOIL_BACILLUS,Substrate.SOIL),
              "soil microhabitat artwork may branch");
        check(!MicrobeLifecyclePolicy.canBranch(Exhibit.SOIL_BACILLUS,Substrate.LEAF),
              "soil ecology cannot branch into leaf");
        check(MicrobeLifecyclePolicy.canBranch(
              Exhibit.PLANT_LACTIPLANTIBACILLUS,Substrate.LEAF),
              "plant association to leaf only");
        check(!MicrobeLifecyclePolicy.canBranch(
              Exhibit.PLANT_LACTIPLANTIBACILLUS,Substrate.SOIL),
              "plant association cannot branch on soil");
        check(MicrobeLifecyclePolicy.advance(Exhibit.T4_PHAGE,2,false,
              Substrate.OTHER,true).hostLinked(),"T4 host cue requires supplied host");
        check(!MicrobeLifecyclePolicy.advance(Exhibit.T4_PHAGE,2,false,
              Substrate.OTHER,false).hostLinked(),"no viral cue without host");
        check(!MicrobeLifecyclePolicy.advance(Exhibit.T4_PHAGE,2,false,
              Substrate.OTHER,true).attemptSatellite(),"virus cannot branch itself");
        check(!MicrobeLifecyclePolicy.advance(Exhibit.ECOLI,2,false,
              Substrate.OTHER,true).attemptSatellite(),"gut-context display cannot branch");
        try{
            MicrobeLifecyclePolicy.advance(Exhibit.SOIL_BACILLUS,3,false,Substrate.SOIL,false);
            throw new AssertionError("invalid frame accepted");
        }catch(IllegalArgumentException expected){checks++;}
        System.out.println("GUGUGAGA_MICROBE_LIFECYCLE_PASS checks="+checks
            +" maxOneSatellite=true noViralAutoSpread=true noPathogenInfection=true");
    }
}
