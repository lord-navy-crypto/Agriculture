import io.github.lordnavycrypto.gugugaga.microbes.NaturalMicrobeCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.NaturalMicrobeCatalog.*;
import io.github.lordnavycrypto.gugugaga.microbes.NaturalMicrobeEcosystem;
import io.github.lordnavycrypto.gugugaga.microbes.NaturalMicrobeEcosystem.*;
import java.util.Set;

public final class NaturalMicrobeEcosystemSmoke {
    private static int passed;
    private static void check(boolean condition,String why){
        passed++;
        if(!condition)throw new AssertionError(why);
    }
    private static long count(java.util.List<Finding> facts,Evidence kind) {
        return facts.stream().filter(x->x.evidence()==kind).count();
    }
    public static void main(String[] args) {
        check(NaturalMicrobeCatalog.all().size()==6,"six documented taxa");
        check(NaturalMicrobeCatalog.E_COLI.ncbiTaxonId()==562,"real E. coli taxon ID");
        check(NaturalMicrobeCatalog.B_SUBTILIS.ncbiTaxonId()==1423,"real soil bacterium");
        check(NaturalMicrobeCatalog.L_PLANTARUM.ncbiTaxonId()==1590,"real plant bacterium");
        check(NaturalMicrobeCatalog.T4_PHAGE.ncbiTaxonId()==10665,"real T4 phage");
        check(NaturalMicrobeCatalog.N_EUROPAEA.ncbiTaxonId()==915,"real soil/freshwater bacterium");
        check(NaturalMicrobeCatalog.C_VULGARIS.ncbiTaxonId()==3077,"real freshwater green microalga");
        check(NaturalMicrobeCatalog.C_VULGARIS.kind()==Kind.MICROALGA,
                "algae are not bacteria or viruses");
        check(NaturalMicrobeCatalog.T4_PHAGE.kind()==Kind.VIRUS
                &&NaturalMicrobeCatalog.E_COLI.kind()==Kind.BACTERIUM,
                "real virus versus bacterial taxonomy");
        for(Organism taxon:NaturalMicrobeCatalog.all().values()) {
            check(taxon.scientificName()!=null&&!taxon.scientificName().isBlank(),
                    "real scientific name required");
            check(taxon.source().startsWith("https://www.ncbi.nlm.nih.gov/"),
                    "trace every listed species to taxonomy");
            check(!taxon.id().contains("mist_fever")
                    &&!taxon.id().contains("rust_bloom"),"no fictional strains");
        }

        check(NaturalMicrobeCatalog.documentedInHabitat("bacillus_subtilis",
                Habitat.SOIL),"verified natural soil association");
        check(NaturalMicrobeCatalog.documentedInHabitat("bacillus_subtilis",
                Habitat.WATER),"verified natural water association");
        check(NaturalMicrobeCatalog.documentedInHabitat(
                "lactiplantibacillus_plantarum",Habitat.PLANT_MATERIAL),
                "verified plant material association");
        check(!NaturalMicrobeCatalog.documentedInHabitat(
                "tequatrovirus_t4",Habitat.SOIL),
                "do not invent arbitrary T4 soil occurrence from Minecraft blocks");
        check(!NaturalMicrobeCatalog.documentedInHabitat(
                "escherichia_coli",Habitat.PLANT_MATERIAL),
                "absence of documented association is not a made-up spawn rule");

        for(String phage:NaturalMicrobeCatalog.all().keySet())
            for(String target:NaturalMicrobeCatalog.all().keySet()) {
                boolean allowed=NaturalMicrobeCatalog.documentedHostRelation(
                        phage,target);
                check(allowed==(phage.equals("tequatrovirus_t4")
                        &&target.equals("escherichia_coli")),
                        "host graph must have ONE documented T4 -> E.coli link");
            }

        var observed=new Observation("documented samples",Habitat.BACTERIAL_HOST,
                Set.of("tequatrovirus_t4","escherichia_coli"));
        var findings=NaturalMicrobeEcosystem.inspect(observed);
        check(count(findings,Evidence.DOCUMENTED_TAXONOMY)==2,
                "species info observed only for supplied taxa");
        check(count(findings,
                Evidence.DOCUMENTED_SPECIES_LEVEL_PHAGE_HOST_RELATION)==1,
                "T4-E.coli is documented association not guaranteed infection");
        check(findings.stream().anyMatch(x->
                x.evidence()==Evidence.DOCUMENTED_SPECIES_LEVEL_PHAGE_HOST_RELATION
                &&x.explanatoryNote().contains("NOT established")),
                "must never infer viral replication or susceptibility rates");
        var empty=NaturalMicrobeEcosystem.inspect(
                new Observation("empty soil",Habitat.SOIL,Set.of()));
        check(empty.isEmpty(),"Minecraft soil must not fabricate visible bacteria");
        var realSoil=NaturalMicrobeEcosystem.inspect(
                new Observation("observed soil",Habitat.SOIL,
                        Set.of("bacillus_subtilis")));
        check(count(realSoil,Evidence.DOCUMENTED_HABITAT_ASSOCIATION)==1,
                "soil association appears only when organism is supplied");
        check(count(realSoil,Evidence.DOCUMENTED_SPECIES_LEVEL_PHAGE_HOST_RELATION)==0,
                "no phantom phage from soil association");
        var unsupported=NaturalMicrobeEcosystem.inspect(
                new Observation("minecraft snow",Habitat.SOIL,
                        Set.of("tequatrovirus_t4")));
        check(count(unsupported,Evidence.UNVERIFIED_LOCAL_OCCURRENCE)==1,
                "unknown local virus occurrence must remain unknown");
        try {
            new Observation("unverified",Habitat.SOIL,Set.of("fictional_virus"));
            throw new AssertionError("unknown fictional organism allowed");
        }catch(NullPointerException expected) {passed++;}
        try {
            new Organism("invented",10000,"Some made up virus",Kind.VIRUS,
                    Set.of(Habitat.WATER),Set.of(),
                    "https://www.ncbi.nlm.nih.gov/example");
            throw new AssertionError("no documented virus host admitted");
        }catch(IllegalArgumentException expected) {passed++;}
        System.out.println("GUGUGAGA_REAL_NATURAL_MICROBE_ECOLOGY_PASS"
                +" checks="+passed
                +" realNCBITaxonomy=true documentedHostOnly=true"
                +" noFictionalSpecies=true noInventedGrowthParameters=true"
                +" noAmbientAutospawnOrTransmission=true");
    }
}
