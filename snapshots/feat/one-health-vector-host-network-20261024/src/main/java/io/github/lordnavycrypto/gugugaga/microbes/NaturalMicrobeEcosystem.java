package io.github.lordnavycrypto.gugugaga.microbes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Evidence-first micro-ecology "simulator" foundation.
 *
 * Shows ACTUAL documented natural associations when the caller observes
 * organisms at a site. It never manufactures organisms from biomes,
 * extrapolates infection dynamics, synthesizes numerical reproduction
 * parameters, or simulates actual human infections.
 */
public final class NaturalMicrobeEcosystem {
    private NaturalMicrobeEcosystem() {}

    public enum Evidence {
        DOCUMENTED_TAXONOMY,
        DOCUMENTED_HABITAT_ASSOCIATION,
        DOCUMENTED_SPECIES_LEVEL_PHAGE_HOST_RELATION,
        UNVERIFIED_LOCAL_OCCURRENCE
    }

    public record Observation(String siteLabel,
                              NaturalMicrobeCatalog.Habitat context,
                              Set<String> actuallyObservedSpeciesIds) {
        public Observation {
            Objects.requireNonNull(siteLabel);
            Objects.requireNonNull(context);
            Objects.requireNonNull(actuallyObservedSpeciesIds);
            if(siteLabel.isBlank()||siteLabel.length()>96)
                throw new IllegalArgumentException("Invalid map label");
            actuallyObservedSpeciesIds=Set.copyOf(actuallyObservedSpeciesIds);
            for(String id:actuallyObservedSpeciesIds)
                NaturalMicrobeCatalog.find(id); // fail unknown species
        }
    }

    public record Finding(String sourceId, String subjectId,
                          Evidence evidence, String explanatoryNote) {
        public Finding {
            Objects.requireNonNull(sourceId);
            Objects.requireNonNull(subjectId);
            Objects.requireNonNull(evidence);
            Objects.requireNonNull(explanatoryNote);
        }
    }

    /**
     * Facts and conditional ecological relationships only. No infection
     * counts, rates, invented microbial weather dependencies or mutation.
     * A documented *species* host link does NOT imply the observed bacterial
     * strain is susceptible or an infection event actually took place.
     */
    public static List<Finding> inspect(Observation observation) {
        Objects.requireNonNull(observation,"observation");
        ArrayList<Finding> out=new ArrayList<>();
        List<String> ids=observation.actuallyObservedSpeciesIds().stream()
                .sorted().toList();
        for(String id:ids) {
            NaturalMicrobeCatalog.Organism organism=NaturalMicrobeCatalog.find(id);
            out.add(new Finding(id,id,Evidence.DOCUMENTED_TAXONOMY,
                    organism.scientificName()+" (NCBI:"+organism.ncbiTaxonId()+")"));
            if(organism.documentedHabitats().contains(observation.context())) {
                out.add(new Finding(id,id,Evidence.DOCUMENTED_HABITAT_ASSOCIATION,
                        "A natural habitat association is documented; "+
                        "the game does not assert actual population size."));
            } else {
                out.add(new Finding(id,id,Evidence.UNVERIFIED_LOCAL_OCCURRENCE,
                        "Local occurrence cannot be predicted from a "+
                        "Minecraft block or biome without source observations."));
            }
        }
        for(String virusId:ids)for(String bacterialId:ids) {
            if(NaturalMicrobeCatalog.documentedHostRelation(virusId,bacterialId))
                out.add(new Finding(virusId,bacterialId,
                        Evidence.DOCUMENTED_SPECIES_LEVEL_PHAGE_HOST_RELATION,
                        "A real phage-host relationship is documented at "+
                        "species level; strain compatibility and any actual "+
                        "interaction are NOT established by co-occurrence."));
        }
        return List.copyOf(out);
    }
}
