package io.github.lordnavycrypto.gugugaga.pathogen;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * A PURE GAMEPLAY model. No real organisms, clinical protocols, exposure
 * calibration, chemistry, lab recipes, external network or world mutation.
 *
 * The names and tick values below are invented for Minecraft balance.
 * Infection can happen ONLY from a verified, explicit in-game contact event;
 * proximity alone, ordinary combat damage and background scans never infect.
 * Minecraft/NeoForge integration, UI and persistence are separate work.
 */
public final class PathogenGamePolicy {
    private PathogenGamePolicy() {}

    public enum Family { VIRUS, BACTERIUM }
    public enum Contact { DROPLET_EVENT, BLOOD_CONTACT_EVENT, SHARED_OBJECT_EVENT }
    public enum Protection { NONE, FACE_COVERING, GLOVES, FULL_PROTECTION }
    public enum Stage { CLEAR, LATENT, SYMPTOMATIC, RECOVERING, RECOVERED }
    public enum Care {
        TEST_KIT, SUPPORTIVE_CARE, TARGETED_BACTERIAL_CARE, ISOLATE, END_ISOLATION
    }

    /** A fictional encounter profile, not a simulation of a real disease. */
    public record FictionalStrain(
            String id,
            Family family,
            Set<Contact> contactEvents,
            int latentTicks,
            int symptomaticTicks,
            int recoveryTicks,
            int gameSeverity,
            boolean respondsToTargetedCare) {
        public FictionalStrain {
            Objects.requireNonNull(id,"id");
            Objects.requireNonNull(family,"family");
            Objects.requireNonNull(contactEvents,"contactEvents");
            if(!id.matches("[a-z0-9_]{3,48}"))
                throw new IllegalArgumentException("Unsafe fictional ID");
            if(contactEvents.isEmpty())
                throw new IllegalArgumentException("At least one game event required");
            contactEvents=Set.copyOf(contactEvents);
            if(latentTicks<1 || symptomaticTicks<1 || recoveryTicks<1 ||
                    latentTicks>24000 || symptomaticTicks>24000 ||
                    recoveryTicks>24000)
                throw new IllegalArgumentException("Gameplay durations outside bounds");
            if(gameSeverity<1 || gameSeverity>4)
                throw new IllegalArgumentException("Gameplay severity 1..4");
            if(family==Family.VIRUS && respondsToTargetedCare)
                throw new IllegalArgumentException(
                        "Bacterial-only game item must not cure the viral scenario");
        }
    }

    /** The ONLY two original fictional launch candidates. Not real strains. */
    public static final FictionalStrain MIST_FEVER = new FictionalStrain(
            "mist_fever",Family.VIRUS,
            EnumSet.of(Contact.DROPLET_EVENT,Contact.SHARED_OBJECT_EVENT),
            200,400,180,2,false);
    public static final FictionalStrain RUST_BLOOM = new FictionalStrain(
            "rust_bloom",Family.BACTERIUM,
            EnumSet.of(Contact.BLOOD_CONTACT_EVENT,Contact.SHARED_OBJECT_EVENT),
            240,460,200,3,true);

    /** No item, command, ambient tick or normal collision creates exposure. */
    public record VerifiedEvent(Contact contact, boolean explicitlyConfirmed,
                                boolean playerParticipating,
                                boolean survivalPlayer) {
        public VerifiedEvent {
            Objects.requireNonNull(contact,"contact");
        }
    }

    /** Immutable player-local state; never scans or edits other entities. */
    public record Patient(String strainId,Stage stage,int remainingTicks,
                          int gameSeverity,boolean tested,boolean isolated) {
        public Patient {
            Objects.requireNonNull(stage,"stage");
            if(remainingTicks<0 || gameSeverity<0 || gameSeverity>4)
                throw new IllegalArgumentException("Invalid game patient state");
            if(stage==Stage.CLEAR && (strainId!=null || remainingTicks!=0
                    || gameSeverity!=0))
                throw new IllegalArgumentException("CLEAR must be empty");
            if(stage!=Stage.CLEAR && strainId==null)
                throw new IllegalArgumentException("Active scenario requires ID");
        }
        public static Patient healthy() {
            return new Patient(null,Stage.CLEAR,0,0,false,false);
        }
    }

    /** Player-consented events only; blood contact is NOT ordinary damage. */
    public static boolean canExpose(FictionalStrain strain,VerifiedEvent event,
                                    Protection protectiveItem,boolean enabled,
                                    boolean quietMode) {
        Objects.requireNonNull(strain,"strain");
        Objects.requireNonNull(event,"event");
        Objects.requireNonNull(protectiveItem,"protectiveItem");
        if(!enabled || quietMode || !event.explicitlyConfirmed()
                || !event.playerParticipating() || !event.survivalPlayer()
                || !strain.contactEvents().contains(event.contact()))
            return false;
        if(protectiveItem==Protection.FULL_PROTECTION)return false;
        return switch(event.contact()) {
            case DROPLET_EVENT -> protectiveItem!=Protection.FACE_COVERING;
            case BLOOD_CONTACT_EVENT,SHARED_OBJECT_EVENT ->
                    protectiveItem!=Protection.GLOVES;
        };
    }

    public static Patient expose(Patient player,FictionalStrain strain,
                                 VerifiedEvent event,Protection protection,
                                 boolean enabled,boolean quietMode) {
        Objects.requireNonNull(player,"player");
        Objects.requireNonNull(strain,"strain");
        if(!canExpose(strain,event,protection,enabled,quietMode))
            return player;
        if(player.stage()!=Stage.CLEAR) {
            // No infectious stacking or looping of same strain.
            // A recovered player has per-scenario protection in this
            // fictional mini-game; real-world immunity is more complex.
            if(player.stage()!=Stage.RECOVERED
                    ||player.strainId().equals(strain.id()))
                return player;
        }
        return new Patient(strain.id(),Stage.LATENT,strain.latentTicks(),
                0,false,player.isolated());
    }

    /** Simple, bounded game phases; NOT medical incubation estimates. */
    public static Patient advance(Patient patient,FictionalStrain strain,
                                  int elapsedTicks) {
        Objects.requireNonNull(patient,"patient");
        Objects.requireNonNull(strain,"strain");
        if(elapsedTicks<0 || elapsedTicks>24000)
            throw new IllegalArgumentException("Bound tick work");
        if(patient.stage()==Stage.CLEAR ||patient.stage()==Stage.RECOVERED)
            return patient;
        if(!strain.id().equals(patient.strainId()))
            throw new IllegalArgumentException("Mismatched fictional strain");
        Patient current=patient;
        int left=elapsedTicks;
        for(int transitions=0;transitions<3 && left>=0;transitions++) {
            if(left<current.remainingTicks())
                return new Patient(current.strainId(),current.stage(),
                        current.remainingTicks()-left,current.gameSeverity(),
                        current.tested(),current.isolated());
            left-=current.remainingTicks();
            current=switch(current.stage()) {
                case LATENT -> new Patient(strain.id(),Stage.SYMPTOMATIC,
                        strain.symptomaticTicks(),strain.gameSeverity(),
                        current.tested(),current.isolated());
                case SYMPTOMATIC -> new Patient(strain.id(),Stage.RECOVERING,
                        strain.recoveryTicks(),Math.max(0,current.gameSeverity()-1),
                        current.tested(),current.isolated());
                case RECOVERING -> new Patient(strain.id(),Stage.RECOVERED,
                        0,0,current.tested(),current.isolated());
                case CLEAR,RECOVERED -> current;
            };
            if(current.stage()==Stage.RECOVERED)return current;
        }
        return current;
    }

    /** Fictional equipment interactions, not real diagnosis/treatment advice. */
    public static Patient care(Patient patient,FictionalStrain strain,
                               Care action) {
        Objects.requireNonNull(patient,"patient");
        Objects.requireNonNull(strain,"strain");
        Objects.requireNonNull(action,"action");
        if(patient.stage()==Stage.CLEAR)return patient;
        if(!strain.id().equals(patient.strainId()))
            throw new IllegalArgumentException("Wrong fictional scenario");
        return switch(action) {
            case TEST_KIT -> new Patient(patient.strainId(),patient.stage(),
                    patient.remainingTicks(),patient.gameSeverity(),
                    true,patient.isolated());
            case ISOLATE -> new Patient(patient.strainId(),patient.stage(),
                    patient.remainingTicks(),patient.gameSeverity(),
                    patient.tested(),true);
            case END_ISOLATION -> new Patient(patient.strainId(),patient.stage(),
                    patient.remainingTicks(),patient.gameSeverity(),
                    patient.tested(),false);
            case SUPPORTIVE_CARE -> new Patient(patient.strainId(),patient.stage(),
                    patient.remainingTicks(),Math.max(0,patient.gameSeverity()-1),
                    patient.tested(),patient.isolated());
            case TARGETED_BACTERIAL_CARE -> {
                // Simplified fictional targeted medicine: only after
                // in-game test, only for responsive fictional bacterial
                // scenarios; NEVER cures a virus or instantly deletes disease.
                if(patient.stage()!=Stage.SYMPTOMATIC
                        || !patient.tested()
                        || strain.family()!=Family.BACTERIUM
                        || !strain.respondsToTargetedCare())yield patient;
                yield new Patient(patient.strainId(),patient.stage(),
                        Math.max(1,patient.remainingTicks()-80),
                        Math.max(0,patient.gameSeverity()-1),
                        patient.tested(),patient.isolated());
            }
        };
    }
}
