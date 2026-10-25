import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog.*;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthObservationPolicy;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthObservationPolicy.*;
import java.util.ArrayList;
import java.util.List;

public final class OneHealthEvidenceSmoke {
    private static int checks;
    private static void check(boolean yes,String message) {
        checks++;
        if(!yes)throw new AssertionError(message);
    }
    private static Observation contact(String id,HumanInterface category) {
        return new Observation(id,ObservationKind.ANIMAL_HUMAN_CONTACT_REPORTED,
                category,"Minecraft research observation (not a diagnosis)");
    }
    public static void main(String[] args) {
        check(OneHealthEvidenceCatalog.all().size()==2,"two original source-backed case studies");
        check(OneHealthEvidenceCatalog.RABIES.ncbiTaxonId()==11292,
                "Rabies current NCBI taxon 11292");
        check(OneHealthEvidenceCatalog.AVIAN_INFLUENZA_A.ncbiTaxonId()==11320,
                "Influenza A NCBI taxon 11320");
        check(OneHealthEvidenceCatalog.RABIES.scientificName()
                .equals("Lyssavirus rabies"),"current ICTV Rabies taxon");
        check(OneHealthEvidenceCatalog.AVIAN_INFLUENZA_A.scientificName()
                .equals("Influenza A virus"),"real influenza A taxon");
        check(OneHealthEvidenceCatalog.AVIAN_INFLUENZA_A.scope()
                .contains("NOT every influenza A virus"),
                "broad Influenza A taxon must NOT apply subset to entire group");
        check(OneHealthEvidenceCatalog.AVIAN_INFLUENZA_A.uncertainty()
                .contains("not establish sustained"),"avian zoonotic human-spread caveat");
        check(OneHealthEvidenceCatalog.RABIES.documentedContexts().contains(
                HostContext.MAMMALS),"rabies in mammals");
        check(OneHealthEvidenceCatalog.RABIES.documentedContexts().contains(
                HostContext.PEOPLE),"documented human rabies");
        check(OneHealthEvidenceCatalog.AVIAN_INFLUENZA_A.documentedContexts()
                .contains(HostContext.WILD_AQUATIC_BIRDS),"avian source context");
        for(var profile:OneHealthEvidenceCatalog.all()){
            check(profile.taxonomySource().startsWith(
                    "https://www.ncbi.nlm.nih.gov/"),"traceable NCBI ID");
            check(profile.publicHealthSource().startsWith("https://www."),
                    "traceable WHO/CDC public health link");
            check(!profile.uncertainty().isBlank(),
                    "each real disease profile must explicitly limit inference");
            for(var category:HumanInterface.values()) {
                boolean expected=profile.documentedInterfaceCategories()
                        .contains(category);
                check(OneHealthEvidenceCatalog.documentedGeneralInterface(
                        profile.id(),category)==expected,
                        "no invented cross-species contact class");
                var finding=OneHealthObservationPolicy.evaluate(
                        contact(profile.id(),category));
                check(finding.assessment()==(expected
                        ?Assessment.DOCUMENTED_GENERAL_CONTACT_CATEGORY
                        :Assessment.NO_DOCUMENTED_CATEGORY_IN_THIS_SCOPE),
                        "a reported contact only yields evidence-class annotation");
                check(finding.scientificCaveat().contains(
                        "does not confirm"),"no real infection inferred");
            }
            for(var kind:ObservationKind.values()){
                if(kind==ObservationKind.ANIMAL_HUMAN_CONTACT_REPORTED)continue;
                var finding=OneHealthObservationPolicy.evaluate(new Observation(
                        profile.id(),kind,null,"unverified local observation"));
                check(finding.assessment()!=
                        Assessment.DOCUMENTED_GENERAL_CONTACT_CATEGORY,
                        "sighting or symptoms cannot fabricate transmission");
                check(!finding.source().isBlank(),"preserve original authority link");
            }
        }
        check(OneHealthEvidenceCatalog.documentedGeneralInterface(
                "lyssavirus_rabies",
                HumanInterface.INFECTED_ANIMAL_BITE_OR_SCRATCH),
                "CDC rabies category documented");
        check(!OneHealthEvidenceCatalog.documentedGeneralInterface(
                "lyssavirus_rabies",
                HumanInterface.CONTACT_WITH_ANIMAL_ASSOCIATED_ENVIRONMENT),
                "do not generalize influenza/environment claims to rabies");
        check(!OneHealthEvidenceCatalog.documentedGeneralInterface(
                "avian_influenza_a_interface",
                HumanInterface.INFECTED_ANIMAL_BITE_OR_SCRATCH),
                "do not generalize rabies category to all influenza A");
        check(OneHealthEvidenceCatalog.documentedGeneralInterface(
                "avian_influenza_a_interface",
                HumanInterface.CONTACT_WITH_ANIMAL_ASSOCIATED_ENVIRONMENT),
                "WHO broad animal influenza environmental interface");

        var entries=List.of(
                new Observation("lyssavirus_rabies",
                        ObservationKind.ANIMAL_PRESENT,null,"animal spotted"),
                contact("lyssavirus_rabies",
                        HumanInterface.INFECTED_ANIMAL_BITE_OR_SCRATCH),
                new Observation("avian_influenza_a_interface",
                        ObservationKind.HUMAN_ILLNESS_REPORTED,null,
                        "unverified player illness"));
        var findings=OneHealthObservationPolicy.review(entries);
        check(findings.size()==3,"all observational evidence retained");
        check(findings.get(0).assessment()==Assessment.OBSERVATION_ONLY,
                "animal observation is not viral infection");
        check(findings.get(1).assessment()==
                Assessment.DOCUMENTED_GENERAL_CONTACT_CATEGORY,
                "route reference is not an individual transmission event");
        check(findings.get(2).assessment()==
                Assessment.HUMAN_ILLNESS_NOT_DIAGNOSED,
                "game illness report is not a lab-confirmed pathogen");
        check(OneHealthObservationPolicy.review(List.of()).isEmpty(),
                "empty Minecraft ecosystem must not fabricate cases");
        try {
            OneHealthEvidenceCatalog.byId("fictional_pathogen");
            throw new AssertionError("unverified pathogen accepted");
        }catch(IllegalArgumentException expected){checks++;}
        try {
            new Observation("lyssavirus_rabies",
                    ObservationKind.ANIMAL_HUMAN_CONTACT_REPORTED,null,
                    "untyped contact");
            throw new AssertionError("untyped contact accepted");
        }catch(IllegalArgumentException expected){checks++;}
        try {
            OneHealthObservationPolicy.review(new ArrayList<>(
                    java.util.Collections.nCopies(1001,entries.get(0))));
            throw new AssertionError("unbounded event scan accepted");
        }catch(IllegalArgumentException expected){checks++;}
        System.out.println("GUGUGAGA_ONEHEALTH_REAL_ANIMAL_HUMAN_PASS checks="
                +checks+" realVirusTaxonomy=true CDC_WHO_sources=true"
                +" humanAnimalEffects=true noFabricatedInfection=true"
                +" avianSubsetNotAllInfluenza=true noRatesOrAutoSpread=true");
    }
}
