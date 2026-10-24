package io.github.lordnavycrypto.gugugaga.microbes;
import java.util.Objects;

/** Fictionalized Minecraft disease-survival stage machine.
 * Real organism names; NO real-world infection rates, incubation, doses,
 * replication kinetics, transmission optimization or clinical protocols.
 */
public final class PathogenGameplayPolicy {
    private PathogenGameplayPolicy(){}
    public enum Pathogen { RABIES, AVIAN_INFLUENZA }
    public enum Stage { CONTACT_RECORDED, SIMULATED_ILLNESS, RECOVERING }
    public enum Care { NONE, EARLY_ASSESSMENT, SUPPORTIVE_CARE }
    public record CaseState(Pathogen pathogen,Stage stage,int gameTicks) {
        public CaseState {
            Objects.requireNonNull(pathogen);
            Objects.requireNonNull(stage);
            if(gameTicks<0||gameTicks>24000)
                throw new IllegalArgumentException("Invalid game clock");
        }
    }
    public static CaseState report(Pathogen pathogen){
        return new CaseState(Objects.requireNonNull(pathogen),Stage.CONTACT_RECORDED,0);
    }
    public static CaseState startIllnessScenario(CaseState before){
        Objects.requireNonNull(before);
        if(before.pathogen()==Pathogen.AVIAN_INFLUENZA
                &&before.stage()==Stage.CONTACT_RECORDED)
            return new CaseState(before.pathogen(),Stage.SIMULATED_ILLNESS,0);
        return before;
    }
    public static Care care(CaseState state){
        if(state==null)return Care.NONE;
        if(state.pathogen()==Pathogen.RABIES
                &&state.stage()==Stage.CONTACT_RECORDED)
            return Care.EARLY_ASSESSMENT;
        return state.stage()==Stage.SIMULATED_ILLNESS
                ?Care.SUPPORTIVE_CARE:Care.NONE;
    }
    public static CaseState visitClinic(CaseState state){
        if(state==null)return null;
        if(state.stage()==Stage.CONTACT_RECORDED)return null;
        if(state.stage()==Stage.SIMULATED_ILLNESS)
            return new CaseState(state.pathogen(),Stage.RECOVERING,0);
        return state;
    }
    public static CaseState advance(CaseState state,int gameTicks){
        if(state==null)return null;
        if(gameTicks<0||gameTicks>200)
            throw new IllegalArgumentException("Unbounded tick step");
        int age=Math.min(24000,state.gameTicks()+gameTicks);
        if(state.stage()==Stage.RECOVERING && age>=400)return null;
        if(state.stage()==Stage.SIMULATED_ILLNESS && age>=1000)
            return new CaseState(state.pathogen(),Stage.RECOVERING,0);
        return new CaseState(state.pathogen(),state.stage(),age);
    }
    public static boolean hasGameFatigue(CaseState state){
        return state!=null && state.pathogen()==Pathogen.AVIAN_INFLUENZA
            &&state.stage()==Stage.SIMULATED_ILLNESS;
    }
}
