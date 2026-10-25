package io.github.lordnavycrypto.gugugaga.microbes;

import java.util.List;
import java.util.Objects;

/**
 * Non-predictive "transmission investigation" viewer for a science game.
 * Contact observation, host range, actual infection and onward spread are
 * DIFFERENT types of evidence. No invented infection chance or process.
 */
public final class OneHealthObservationPolicy {
    private OneHealthObservationPolicy(){}

    public enum ObservationKind {
        ANIMAL_PRESENT,
        ANIMAL_HUMAN_CONTACT_REPORTED,
        HUMAN_ILLNESS_REPORTED,
        EXTERNAL_CASE_REFERENCE
    }

    public enum Assessment {
        OBSERVATION_ONLY,
        DOCUMENTED_GENERAL_CONTACT_CATEGORY,
        NO_DOCUMENTED_CATEGORY_IN_THIS_SCOPE,
        HUMAN_ILLNESS_NOT_DIAGNOSED,
        EXTERNAL_RECORD_REQUIRES_INDEPENDENT_VERIFICATION
    }

    /** Player observation: NEVER treated as a confirmed infection event. */
    public record Observation(String profileId,ObservationKind kind,
            OneHealthEvidenceCatalog.HumanInterface possibleContact,
            String observationLabel) {
        public Observation {
            Objects.requireNonNull(profileId,"profileId");
            Objects.requireNonNull(kind,"kind");
            Objects.requireNonNull(observationLabel,"observationLabel");
            OneHealthEvidenceCatalog.byId(profileId);
            if(observationLabel.isBlank() || observationLabel.length()>120)
                throw new IllegalArgumentException("Invalid observation");
            if(kind==ObservationKind.ANIMAL_HUMAN_CONTACT_REPORTED
                    && possibleContact==null)
                throw new IllegalArgumentException("Contact category required");
            if(kind!=ObservationKind.ANIMAL_HUMAN_CONTACT_REPORTED
                    && possibleContact!=null)
                throw new IllegalArgumentException(
                        "Noncontact reports cannot imply an exposure");
        }
    }

    public record Finding(Observation report, Assessment assessment,
            String scientificCaveat,String source) {
        public Finding {
            Objects.requireNonNull(report);
            Objects.requireNonNull(assessment);
            Objects.requireNonNull(scientificCaveat);
            Objects.requireNonNull(source);
        }
    }

    public static Finding evaluate(Observation report) {
        Objects.requireNonNull(report,"report");
        var profile=OneHealthEvidenceCatalog.byId(report.profileId());
        Assessment result=switch(report.kind()) {
            case ANIMAL_PRESENT -> Assessment.OBSERVATION_ONLY;
            case HUMAN_ILLNESS_REPORTED ->
                    Assessment.HUMAN_ILLNESS_NOT_DIAGNOSED;
            case EXTERNAL_CASE_REFERENCE ->
                    Assessment.EXTERNAL_RECORD_REQUIRES_INDEPENDENT_VERIFICATION;
            case ANIMAL_HUMAN_CONTACT_REPORTED ->
                    OneHealthEvidenceCatalog.documentedGeneralInterface(
                            profile.id(),report.possibleContact())
                    ?Assessment.DOCUMENTED_GENERAL_CONTACT_CATEGORY
                    :Assessment.NO_DOCUMENTED_CATEGORY_IN_THIS_SCOPE;
        };
        return new Finding(report,result,
                "A Minecraft sighting, contact or report does not confirm "
                +"a real infection or future spread. The referenced broad "
                +"route is not an individual-level prediction.",
                profile.publicHealthSource());
    }

    /** Immutable report list, no contagious-game-state or automatic spread. */
    public static List<Finding> review(List<Observation> observations) {
        Objects.requireNonNull(observations);
        if(observations.size()>1000)
            throw new IllegalArgumentException("Bounded review batch only");
        return observations.stream().map(OneHealthObservationPolicy::evaluate)
                .toList();
    }
}
