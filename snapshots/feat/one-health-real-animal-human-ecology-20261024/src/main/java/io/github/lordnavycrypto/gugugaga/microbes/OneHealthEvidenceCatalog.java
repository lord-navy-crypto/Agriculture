package io.github.lordnavycrypto.gugugaga.microbes;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Documented One Health profiles, not laboratory or clinical instructions.
 * Animal-human pathways here are broad authoritative PUBLIC HEALTH categories.
 * No engineered organisms, strain adaptation, infectious dose, replication,
 * transmissibility parameters, incubation times or in-world contagion.
 */
public final class OneHealthEvidenceCatalog {
    private OneHealthEvidenceCatalog(){}

    public enum HostContext {
        MAMMALS, PEOPLE, WILD_AQUATIC_BIRDS, DOMESTIC_BIRDS
    }

    public enum HumanInterface {
        INFECTED_ANIMAL_BITE_OR_SCRATCH,
        CLOSE_CONTACT_WITH_AFFECTED_ANIMALS,
        CONTACT_WITH_ANIMAL_ASSOCIATED_ENVIRONMENT
    }

    public record Profile(String id,
                          int ncbiTaxonId,
                          String scientificName,
                          String scope,
                          Set<HostContext> documentedContexts,
                          Set<HumanInterface> documentedInterfaceCategories,
                          String humanImpact,
                          String animalImpact,
                          String uncertainty,
                          String taxonomySource,
                          String publicHealthSource) {
        public Profile {
            Objects.requireNonNull(id);
            Objects.requireNonNull(scientificName);
            Objects.requireNonNull(scope);
            Objects.requireNonNull(documentedContexts);
            Objects.requireNonNull(documentedInterfaceCategories);
            Objects.requireNonNull(humanImpact);
            Objects.requireNonNull(animalImpact);
            Objects.requireNonNull(uncertainty);
            Objects.requireNonNull(taxonomySource);
            Objects.requireNonNull(publicHealthSource);
            if(!id.matches("[a-z0-9_]{3,48}") || ncbiTaxonId<=0
                    ||scientificName.isBlank()||scope.isBlank()
                    ||humanImpact.isBlank()||animalImpact.isBlank()
                    ||uncertainty.isBlank()
                    ||documentedContexts.isEmpty()
                    ||documentedInterfaceCategories.isEmpty()
                    ||!taxonomySource.startsWith("https://www.ncbi.nlm.nih.gov/")
                    ||!(publicHealthSource.startsWith("https://www.cdc.gov/")
                        ||publicHealthSource.startsWith("https://www.who.int/")))
                throw new IllegalArgumentException("Incomplete verified One Health record");
            documentedContexts=Set.copyOf(documentedContexts);
            documentedInterfaceCategories=Set.copyOf(documentedInterfaceCategories);
        }
    }

    /** Rabies virus (current accepted ICTV name Lyssavirus rabies). */
    public static final Profile RABIES = new Profile(
            "lyssavirus_rabies",
            11292,
            "Lyssavirus rabies",
            "Rabies virus in infected mammals and documented human disease",
            Set.of(HostContext.MAMMALS,HostContext.PEOPLE),
            Set.of(HumanInterface.INFECTED_ANIMAL_BITE_OR_SCRATCH),
            "Can cause severe neurological disease in humans.",
            "Infects mammals and causes rabies in susceptible animals.",
            "A species name and an animal sighting cannot establish that an "
                    +"individual animal is infected. This is not an exposure diagnosis.",
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=11292&mode=Info",
            "https://www.cdc.gov/rabies/about/");

    /**
     * This broad NCBI taxon includes SEVERAL distinct influenza A viruses.
     * Public-health interface claims below apply to the AVIAN / ZOONOTIC
     * SUBSET only, not uniformly to every influenza A subtype or strain.
     */
    public static final Profile AVIAN_INFLUENZA_A = new Profile(
            "avian_influenza_a_interface",
            11320,
            "Influenza A virus",
            "Avian-origin influenza A viruses at the animal-human interface, "
                    +"NOT every influenza A virus",
            Set.of(HostContext.WILD_AQUATIC_BIRDS,
                   HostContext.DOMESTIC_BIRDS,
                   HostContext.MAMMALS,
                   HostContext.PEOPLE),
            Set.of(HumanInterface.CLOSE_CONTACT_WITH_AFFECTED_ANIMALS,
                   HumanInterface.CONTACT_WITH_ANIMAL_ASSOCIATED_ENVIRONMENT),
            "Some avian-origin strains have caused human illness, ranging "
                    +"from mild symptoms to serious respiratory disease.",
            "Avian-origin influenza A affects birds and can affect some mammals.",
            "Cross-species infection is context- and strain-dependent. "
                    +"Documented zoonotic cases do not establish sustained "
                    +"human-to-human spread of current zoonotic strains.",
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=11320&mode=Info",
            "https://www.who.int/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29");

    private static final List<Profile> ALL=List.of(RABIES,AVIAN_INFLUENZA_A);
    public static List<Profile> all(){return ALL;}
    public static Profile byId(String id) {
        Objects.requireNonNull(id);
        return ALL.stream().filter(profile->profile.id().equals(id))
                .findFirst().orElseThrow(()->new IllegalArgumentException(
                        "Unknown documented pathogen profile: "+id));
    }

    /**
     * Is the queried broad category documented in this SPECIFIC scope?
     * TRUE means only that authoritative literature describes a route;
     * it does NOT mean contact happened, infection occurred or probability.
     */
    public static boolean documentedGeneralInterface(
            String id,HumanInterface category) {
        return byId(id).documentedInterfaceCategories().contains(
                Objects.requireNonNull(category));
    }
}
