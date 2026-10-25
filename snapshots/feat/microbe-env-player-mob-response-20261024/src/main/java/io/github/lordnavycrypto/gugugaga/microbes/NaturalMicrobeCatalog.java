package io.github.lordnavycrypto.gugugaga.microbes;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Scientific-source-backed microbe / virus catalog.
 *
 * Taxonomy IDs identify ACTUAL natural organisms, not fictional strains.
 * Facts here are qualitative. No invented growth constants, probabilities,
 * incubation times, spread radius, doses, exposure frequencies or lab methods.
 * This class performs NO world spawning, contamination or player damage.
 */
public final class NaturalMicrobeCatalog {
    private NaturalMicrobeCatalog() {}

    public enum Kind { BACTERIUM, VIRUS, MICROALGA }
    public enum Habitat {
        SOIL, WATER, FRESHWATER, PLANT_MATERIAL, ANIMAL_INTESTINE, BACTERIAL_HOST
    }

    public record Organism(String id, int ncbiTaxonId, String scientificName,
                           Kind kind, Set<Habitat> documentedHabitats,
                           Set<Integer> documentedHosts, String source) {
        public Organism {
            Objects.requireNonNull(id,"id");
            Objects.requireNonNull(scientificName,"scientificName");
            Objects.requireNonNull(kind,"kind");
            Objects.requireNonNull(documentedHabitats,"documentedHabitats");
            Objects.requireNonNull(documentedHosts,"documentedHosts");
            Objects.requireNonNull(source,"source");
            if(!id.matches("[a-z0-9_]{3,48}") || ncbiTaxonId<=0)
                throw new IllegalArgumentException("Invalid scientific record");
            if(scientificName.isBlank()
                    ||!source.startsWith("https://www.ncbi.nlm.nih.gov/"))
                throw new IllegalArgumentException("Requires trusted taxonomy source");
            documentedHabitats=Set.copyOf(documentedHabitats);
            documentedHosts=Set.copyOf(documentedHosts);
            if(kind==Kind.BACTERIUM && !documentedHosts.isEmpty())
                throw new IllegalArgumentException("Bacterial host relation belongs to virus");
            if(kind==Kind.VIRUS && documentedHosts.isEmpty())
                throw new IllegalArgumentException("Virus needs documented host");
        }
    }

    /** E. coli is a species, not a synonym for a single dangerous strain. */
    public static final Organism E_COLI = new Organism(
            "escherichia_coli",562,"Escherichia coli",Kind.BACTERIUM,
            EnumSet.of(Habitat.ANIMAL_INTESTINE),Set.of(),
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=562&mode=Info");

    /** Commonly present in soil; NO fictional Minecraft spawning frequency. */
    public static final Organism B_SUBTILIS = new Organism(
            "bacillus_subtilis",1423,"Bacillus subtilis",Kind.BACTERIUM,
            EnumSet.of(Habitat.SOIL,Habitat.WATER),Set.of(),
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=1423&mode=Info");

    /** Plant-associated real lactic-acid bacterium; NOT a disease label. */
    public static final Organism L_PLANTARUM = new Organism(
            "lactiplantibacillus_plantarum",1590,
            "Lactiplantibacillus plantarum",Kind.BACTERIUM,
            EnumSet.of(Habitat.PLANT_MATERIAL),Set.of(),
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=1590&mode=Info");

    /**
     * A REAL, natural bacterial virus, Tequatrovirus T4, not a human virus.
     * Association with E. coli is scientifically documented. Species-level
     * association must NOT be interpreted as universal strain susceptibility.
     */
    public static final Organism T4_PHAGE = new Organism(
            "tequatrovirus_t4",10665,"Tequatrovirus T4",Kind.VIRUS,
            EnumSet.of(Habitat.BACTERIAL_HOST),Set.of(E_COLI.ncbiTaxonId()),
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=10665&mode=Info");

    /** A real ammonia-oxidizing soil/freshwater bacterial species (taxon 915). */
    public static final Organism N_EUROPAEA = new Organism(
            "nitrosomonas_europaea",915,"Nitrosomonas europaea",Kind.BACTERIUM,
            EnumSet.of(Habitat.SOIL,Habitat.FRESHWATER),Set.of(),
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=915&mode=Info");

    /** Freshwater green microalga, explicitly not a bacterium or pathogen. */
    public static final Organism C_VULGARIS = new Organism(
            "chlorella_vulgaris",3077,"Chlorella vulgaris",Kind.MICROALGA,
            EnumSet.of(Habitat.FRESHWATER),Set.of(),
            "https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=3077&mode=Info");

    private static final Map<String,Organism> BY_ID;
    static {
        LinkedHashMap<String,Organism> records=new LinkedHashMap<>();
        for(Organism item:new Organism[]{E_COLI,B_SUBTILIS,L_PLANTARUM,T4_PHAGE,N_EUROPAEA,C_VULGARIS}) {
            if(records.put(item.id(),item)!=null)
                throw new IllegalStateException("Duplicate scientific species ID");
        }
        BY_ID=Map.copyOf(records);
    }

    public static Map<String,Organism> all() {return BY_ID;}

    public static Organism find(String id) {
        return Objects.requireNonNull(BY_ID.get(id),"Unknown real taxonomy ID");
    }

    /** A documented biome association is NOT proof of organism occupancy. */
    public static boolean documentedInHabitat(String speciesId,Habitat habitat) {
        return find(speciesId).documentedHabitats().contains(
                Objects.requireNonNull(habitat));
    }

    /**
     * Information retrieval only: which bacterium is a DOCUMENTED host
     * for a real phage. Not a survival odds, propagation or infection model.
     */
    public static boolean documentedHostRelation(String virusId,
                                                 String bacterialSpeciesId) {
        Organism virus=find(virusId),bacterium=find(bacterialSpeciesId);
        return virus.kind()==Kind.VIRUS
                && bacterium.kind()==Kind.BACTERIUM
                && virus.documentedHosts().contains(bacterium.ncbiTaxonId());
    }
}
