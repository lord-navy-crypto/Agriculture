package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scientific identity and observation registry for 15 real disease agents.
 * Qualitative documentation only: no disease rates, incubation parameters,
 * exposure doses, pathogen survival or infectious transport instructions.
 *
 * The historical 0..15 Minecraft game signal is never a pathogen measurement.
 */
public final class ScientificTaxonProfiles15 {
    private ScientificTaxonProfiles15() {}

    public enum HealthDomain {
        ANIMAL_AND_HUMAN, ANIMAL, HUMAN_AND_FOOD, PLANT
    }
    public enum DataClass {
        ANIMAL_DISEASE_REPORTS,
        HUMAN_LAB_SURVEILLANCE,
        VECTOR_AND_ANIMAL_SURVEILLANCE,
        FOOD_AND_ENVIRONMENTAL_LAB,
        PLANT_DISEASE_SURVEILLANCE
    }
    public record Profile(
            String taxonId,
            HealthDomain healthDomain,
            DataClass evidenceType,
            String verifiedContext,
            String interpretationCaution) {
        public Profile {
            PathogenRoster15.byId(taxonId);
            if(verifiedContext==null||verifiedContext.isBlank()
                    ||interpretationCaution==null||interpretationCaution.isBlank())
                throw new IllegalArgumentException("Research facts require context and caveat");
        }
        public String sourceUrl() {
            return PathogenRoutePolicy15.forTaxon(taxonId).evidenceUrl();
        }
        public Set<String> displayEntities() {
            return VanillaHostMapping15.byId(taxonId).entityIds();
        }
        public Set<String> displayBlocks() {
            return VanillaHostMapping15.byId(taxonId).blockIds();
        }
        public boolean usesVisualProxy() {
            return VanillaHostMapping15.byId(taxonId).visualProxy();
        }
        public String dataStatus() {
            // Existing CDC FluView aggregate lab data are NOT species or
            // subtype-confirmed host-level infection records for this taxon.
            return "No agent-specific calibrated timeseries imported";
        }
        public String description() {
            var identity=PathogenRoster15.byId(taxonId);
            return identity.scientificName()+" | "+identity.type()
                +" | health="+healthDomain
                +" | suitable evidence="+evidenceType
                +" | scientific context="+verifiedContext
                +" | Minecraft display entities="+displayEntities()
                +" blocks="+displayBlocks()
                +" | visual-proxy="+usesVisualProxy()
                +" | data="+dataStatus()
                +" | caution="+interpretationCaution
                +" | source="+sourceUrl();
        }
    }

    public static final List<Profile> ALL=List.of(
        new Profile("lyssavirus_rabies",HealthDomain.ANIMAL_AND_HUMAN,
            DataClass.ANIMAL_DISEASE_REPORTS,
            "Rabies is a viral disease of mammals; public health and animal health surveillance are distinct.",
            "Appearance or a Minecraft wolf particle does not establish rabies."),
        new Profile("influenza_a_virus",HealthDomain.ANIMAL_AND_HUMAN,
            DataClass.HUMAN_LAB_SURVEILLANCE,
            "Influenza A includes distinct lineages and animal settings; laboratory reporting must retain subtype and sampling scope.",
            "Aggregate influenza positives cannot be attributed to a specific influenza A lineage."),
        new Profile("west_nile_virus",HealthDomain.ANIMAL_AND_HUMAN,
            DataClass.VECTOR_AND_ANIMAL_SURVEILLANCE,
            "Birds and mosquitoes are involved in its natural ecology; people and horses are usually incidental dead-end hosts.",
            "Horses are not evidence of routine onward virus cycling."),
        new Profile("foot_and_mouth_disease_virus",HealthDomain.ANIMAL,
            DataClass.ANIMAL_DISEASE_REPORTS,
            "A livestock disease associated with cloven-hoofed animals, including cattle, sheep, goats and pigs.",
            "Do not interpret Minecraft animal movement as confirmed disease spread."),
        new Profile("african_swine_fever_virus",HealthDomain.ANIMAL,
            DataClass.ANIMAL_DISEASE_REPORTS,
            "A disease of pigs and wild suids, with no human infection.",
            "Never label human or non-pig Minecraft populations as ASF cases."),
        new Profile("salmonella_enterica",HealthDomain.HUMAN_AND_FOOD,
            DataClass.FOOD_AND_ENVIRONMENTAL_LAB,
            "A bacterial group considered in food safety and animal-environment monitoring.",
            "A food sample or positive laboratory result is not a clinical infection count."),
        new Profile("listeria_monocytogenes",HealthDomain.HUMAN_AND_FOOD,
            DataClass.FOOD_AND_ENVIRONMENTAL_LAB,
            "A bacterial food-safety concern and target of public-health laboratory monitoring.",
            "Environmental detection is different from confirmed human listeriosis."),
        new Profile("campylobacter_jejuni",HealthDomain.HUMAN_AND_FOOD,
            DataClass.FOOD_AND_ENVIRONMENTAL_LAB,
            "A bacterium associated with foodborne disease and animal production.",
            "Chicken presence in a world is not a positive laboratory result."),
        new Profile("staphylococcus_aureus",HealthDomain.HUMAN_AND_FOOD,
            DataClass.HUMAN_LAB_SURVEILLANCE,
            "A bacterium that may occur as colonization or disease; interpretation depends on specimen and diagnosis.",
            "Detection, colonization and clinical infection must not be conflated."),
        new Profile("tobacco_mosaic_virus",HealthDomain.PLANT,
            DataClass.PLANT_DISEASE_SURVEILLANCE,
            "A plant virus associated with mosaic disease of susceptible plants.",
            "Vanilla beetroot is only a visual stand-in, not a documented true host."),
        new Profile("cucumber_mosaic_virus",HealthDomain.PLANT,
            DataClass.PLANT_DISEASE_SURVEILLANCE,
            "A plant virus affecting a broad range of susceptible crops and ornamentals.",
            "Minecraft melon or pumpkin stem graphics are not a disease test."),
        new Profile("potato_virus_y",HealthDomain.PLANT,
            DataClass.PLANT_DISEASE_SURVEILLANCE,
            "A documented potato virus affecting crop health.",
            "Mosaic-like leaf appearance alone cannot confirm potato virus Y."),
        new Profile("phytophthora_infestans",HealthDomain.PLANT,
            DataClass.PLANT_DISEASE_SURVEILLANCE,
            "An oomycete rather than a fungus, associated with potato late blight.",
            "Minecraft rain and humidity proxies cannot establish confirmed potato blight."),
        new Profile("xanthomonas_campestris",HealthDomain.PLANT,
            DataClass.PLANT_DISEASE_SURVEILLANCE,
            "Some forms, including X. campestris pv. campestris, are associated with black rot of brassicas.",
            "Vanilla beetroot is a display proxy, not a validated brassica host."),
        new Profile("erwinia_amylovora",HealthDomain.PLANT,
            DataClass.PLANT_DISEASE_SURVEILLANCE,
            "The bacterium that causes fire blight in apples, pears and related plants.",
            "Vanilla oak and other tree leaves represent scenery, not verified apple or pear hosts.")
    );

    private static final Map<String,Profile> BY_ID=ALL.stream()
        .collect(Collectors.toUnmodifiableMap(Profile::taxonId,x->x));
    static {
        if(ALL.size()!=15||BY_ID.size()!=15||
           !BY_ID.keySet().equals(PathogenRoster15.FIRST_FIFTEEN.stream()
             .map(PathogenRoster15.Entry::id).collect(Collectors.toSet())))
            throw new IllegalStateException("Every original agent needs research metadata");
        for(var entry:ALL)if(!entry.sourceUrl().startsWith("https://"))
            throw new IllegalStateException("Source link missing: "+entry.taxonId());
    }
    public static Profile byId(String id) {
        var result=BY_ID.get(id);
        if(result==null)throw new IllegalArgumentException("Unknown taxon: "+id);
        return result;
    }
}
