import io.github.lordnavycrypto.gugugaga.microbes.PathogenGameplayPolicy;
import io.github.lordnavycrypto.gugugaga.microbes.PathogenGameplayPolicy.*;
public final class PathogenGameplaySmoke{
    private static int count;
    private static void check(boolean cond,String msg){count++;if(!cond)throw new AssertionError(msg);}
    public static void main(String[] args){
        CaseState rabies=PathogenGameplayPolicy.report(Pathogen.RABIES);
        check(rabies.stage()==Stage.CONTACT_RECORDED,"report is not an infection");
        check(PathogenGameplayPolicy.care(rabies)==Care.EARLY_ASSESSMENT,"rabies needs early care");
        check(PathogenGameplayPolicy.startIllnessScenario(rabies)==rabies,"no automatic rabies");
        check(!PathogenGameplayPolicy.hasGameFatigue(rabies),"no false debuff");
        check(PathogenGameplayPolicy.visitClinic(rabies)==null,"early assessment closes case");
        CaseState flu=PathogenGameplayPolicy.report(Pathogen.AVIAN_INFLUENZA);
        check(PathogenGameplayPolicy.advance(flu,200).stage()==Stage.CONTACT_RECORDED,"no inferred flu infection");
        CaseState active=PathogenGameplayPolicy.startIllnessScenario(flu);
        check(active.stage()==Stage.SIMULATED_ILLNESS,"illness is opt-in game stage");
        check(PathogenGameplayPolicy.hasGameFatigue(active),"game has symptoms");
        check(PathogenGameplayPolicy.care(active)==Care.SUPPORTIVE_CARE,"differential care");
        CaseState recovery=PathogenGameplayPolicy.visitClinic(active);
        check(recovery.stage()==Stage.RECOVERING,"clinic changes state");
        check(!PathogenGameplayPolicy.hasGameFatigue(recovery),"game recovery");
        check(PathogenGameplayPolicy.advance(recovery,200).stage()==Stage.RECOVERING,"partial recovery");
        check(PathogenGameplayPolicy.advance(
                PathogenGameplayPolicy.advance(recovery,200),200)==null,"bounded recovery");
        CaseState aged=active;
        for(int i=0;i<5;i++)aged=PathogenGameplayPolicy.advance(aged,200);
        check(aged.stage()==Stage.RECOVERING,"finite scripted illness");
        try{PathogenGameplayPolicy.advance(active,201);
            throw new AssertionError("unbounded tick accepted");
        }catch(IllegalArgumentException expected){count++;}
        System.out.println("GUGUGAGA_PATHOGEN_MEDICAL_PASS checks="+count
                +" noContactEqualsInfection=true caseSpecificCare=true");
    }
}
