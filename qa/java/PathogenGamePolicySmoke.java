import io.github.lordnavycrypto.gugugaga.pathogen.PathogenGamePolicy;
import io.github.lordnavycrypto.gugugaga.pathogen.PathogenGamePolicy.*;

public final class PathogenGamePolicySmoke {
    private static int checks;
    private static void require(boolean ok,String why) {
        checks++;
        if(!ok)throw new AssertionError(why);
    }
    private static VerifiedEvent contact(Contact type,boolean confirmed) {
        return new VerifiedEvent(type,confirmed,true,true);
    }
    private static Patient begin(FictionalStrain s, Contact route) {
        return PathogenGamePolicy.expose(Patient.healthy(),s,contact(route,true),
                Protection.NONE,true,false);
    }

    public static void main(String[] args) {
        var virus=PathogenGamePolicy.MIST_FEVER;
        var bacteria=PathogenGamePolicy.RUST_BLOOM;
        require(virus.family()==Family.VIRUS,"distinct viral family");
        require(bacteria.family()==Family.BACTERIUM,"distinct bacterial family");
        require(!virus.contactEvents().contains(Contact.BLOOD_CONTACT_EVENT),
                "a viral game profile must not magically gain a blood contact route");
        require(bacteria.contactEvents().contains(Contact.BLOOD_CONTACT_EVENT),
                "fictional blood contact exists only for defined strain");
        for(var strain:new FictionalStrain[]{virus,bacteria})
            for(var route:Contact.values())
                for(var gear:Protection.values())
                    for(boolean confirmed:new boolean[]{false,true})
                        for(boolean participating:new boolean[]{false,true})
                            for(boolean survival:new boolean[]{false,true})
                                for(boolean enabled:new boolean[]{false,true})
                                    for(boolean quiet:new boolean[]{false,true}) {
            var event=new VerifiedEvent(route,confirmed,participating,survival);
            boolean eligible=PathogenGamePolicy.canExpose(
                    strain,event,gear,enabled,quiet);
            boolean expected=confirmed && participating && survival && enabled
                    && !quiet && strain.contactEvents().contains(route)
                    && gear!=Protection.FULL_PROTECTION
                    && !(gear==Protection.FACE_COVERING
                         && route==Contact.DROPLET_EVENT)
                    && !(gear==Protection.GLOVES
                         && route!=Contact.DROPLET_EVENT);
            require(eligible==expected,"contact/gear/consent gate "+strain.id());
            var patient=PathogenGamePolicy.expose(Patient.healthy(),strain,
                    event,gear,enabled,quiet);
            require((patient.stage()==Stage.LATENT)==eligible,
                    "only verified in-game contact may begin infection");
        }
        var noConsent=new VerifiedEvent(Contact.BLOOD_CONTACT_EVENT,true,false,true);
        require(!PathogenGamePolicy.canExpose(bacteria,noConsent,Protection.NONE,
                true,false),"opt-out never exposes a player");
        require(!PathogenGamePolicy.canExpose(bacteria,contact(
                Contact.BLOOD_CONTACT_EVENT,false),Protection.NONE,true,false),
                "mere proximity or normal Minecraft damage is NOT explicit exposure");
        require(!PathogenGamePolicy.canExpose(bacteria,contact(
                Contact.BLOOD_CONTACT_EVENT,true),Protection.GLOVES,true,false),
                "gloves prevent fictional blood-contact event exposure");

        var viral=begin(virus,Contact.DROPLET_EVENT);
        require(viral.stage()==Stage.LATENT && viral.remainingTicks()==200,
                "viral game progression begins latent");
        viral=PathogenGamePolicy.advance(viral,virus,199);
        require(viral.stage()==Stage.LATENT && viral.remainingTicks()==1,
                "no early stage transition");
        viral=PathogenGamePolicy.advance(viral,virus,1);
        require(viral.stage()==Stage.SYMPTOMATIC && viral.gameSeverity()==2,
                "game-only symptom transition");
        var observed=PathogenGamePolicy.care(viral,virus,Care.TEST_KIT);
        require(observed.tested() && observed.stage()==Stage.SYMPTOMATIC,
                "in-game test does not cure");
        require(PathogenGamePolicy.care(observed,virus,
                Care.TARGETED_BACTERIAL_CARE).equals(observed),
                "fictional bacteria-only care does not treat viruses");
        var isolated=PathogenGamePolicy.care(observed,virus,Care.ISOLATE);
        require(isolated.isolated(),"isolation is visible player local state");
        var supported=PathogenGamePolicy.care(isolated,virus,Care.SUPPORTIVE_CARE);
        require(supported.gameSeverity()==1
                && supported.stage()==Stage.SYMPTOMATIC,
                "supportive care eases game severity but is not instant cure");
        var recovered=PathogenGamePolicy.advance(supported,virus,400+180);
        require(recovered.stage()==Stage.RECOVERED
                && recovered.gameSeverity()==0,"finite self-contained game recovery");
        require(PathogenGamePolicy.expose(recovered,virus,
                contact(Contact.DROPLET_EVENT,true),Protection.NONE,true,false)
                .equals(recovered),"no immediate same scenario reinfection");
        require(PathogenGamePolicy.care(isolated,virus,Care.END_ISOLATION)
                .isolated()==false,"clear isolation through UI action");

        var patient=begin(bacteria,Contact.BLOOD_CONTACT_EVENT);
        patient=PathogenGamePolicy.advance(patient,bacteria,240);
        require(patient.stage()==Stage.SYMPTOMATIC
                && patient.remainingTicks()==460,"bacterial game phase");
        require(PathogenGamePolicy.care(patient,bacteria,
                Care.TARGETED_BACTERIAL_CARE).equals(patient),
                "no untested targeted care");
        var tested=PathogenGamePolicy.care(patient,bacteria,Care.TEST_KIT);
        var targeted=PathogenGamePolicy.care(tested,bacteria,
                Care.TARGETED_BACTERIAL_CARE);
        require(targeted.stage()==Stage.SYMPTOMATIC
                && targeted.remainingTicks()==380
                && targeted.gameSeverity()==2,
                "tested responsive fictional bacteria has bounded care effect");
        require(PathogenGamePolicy.advance(targeted,bacteria,580).stage()
                ==Stage.RECOVERED,"treated fictional bacterial scenario completes");

        require(PathogenGamePolicy.advance(Patient.healthy(),virus,1000)
                .equals(Patient.healthy()),"healthy player unchanged");
        require(PathogenGamePolicy.expose(patient,virus,
                contact(Contact.DROPLET_EVENT,true),Protection.NONE,true,false)
                .equals(patient),"do not stack infections");
        try {
            new FictionalStrain("bad viral",Family.VIRUS,
                    java.util.Set.of(Contact.DROPLET_EVENT),1,1,1,1,false);
            throw new AssertionError("unsafe scenario ID accepted");
        }catch(IllegalArgumentException expected) {checks++;}
        try {
            new FictionalStrain("impossible_viral",Family.VIRUS,
                    java.util.Set.of(Contact.DROPLET_EVENT),10,20,10,2,true);
            throw new AssertionError("fictional viral cure with antibacterial item accepted");
        }catch(IllegalArgumentException expected) {checks++;}
        try {
            PathogenGamePolicy.advance(viral,virus,-1);
            throw new AssertionError("negative tick work accepted");
        }catch(IllegalArgumentException expected) {checks++;}
        System.out.println("GUGUGAGA_PATHOGEN_MEDICAL_POLICY_PASS checks="+checks+
                " separateFictionalVirusAndBacterium=true"+
                " noAmbientInfection=true noRealTransmissionCalibration=true"+
                " testedTargetedCareOnly=true stagesAndOptOut=true");
    }
}
