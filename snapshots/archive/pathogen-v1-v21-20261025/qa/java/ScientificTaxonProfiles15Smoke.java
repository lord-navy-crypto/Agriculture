import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class ScientificTaxonProfiles15Smoke {
    static int checks=0;
    static void ok(boolean condition,String why){
        checks++; if(!condition)throw new AssertionError(why);
    }
    public static void main(String[] args){
        var all=ScientificTaxonProfiles15.ALL;
        ok(all.size()==15,"all existing pathogenic taxa represented");
        Set<String> names=new HashSet<>();
        int virus=0,bacterium=0,oomycete=0,plant=0,animal=0,proxy=0;
        for(var p:all){
            var identity=PathogenRoster15.byId(p.taxonId());
            ok(names.add(p.taxonId()),"no repeated pathogen label");
            ok(p.sourceUrl().startsWith("https://"),
               "official referenced evidence URL present");
            ok(!p.displayEntities().isEmpty()||!p.displayBlocks().isEmpty(),
               "existing vanilla visual target");
            ok(p.dataStatus().contains("No agent-specific"),
               "no invented calibrated organism-specific dataset");
            ok(p.description().contains("visual-proxy="),
               "every identity discloses Minecraft proxy status");
            ok(p.description().contains("source=https://"),
               "every species inspection includes evidence");
            switch(identity.type()){
                case VIRUS -> virus++;
                case BACTERIUM -> bacterium++;
                case OOMYCETE -> oomycete++;
            }
            if(p.healthDomain()==ScientificTaxonProfiles15.HealthDomain.PLANT)plant++;
            else animal++;
            if(p.usesVisualProxy())proxy++;
        }
        ok(virus==8&&bacterium==6&&oomycete==1,
            "scientific classification: 8 viruses, 6 bacteria, 1 oomycete");
        ok(plant==6&&animal==9,"six crop, nine animal/human disease agents");
        ok(proxy==3,"three species explicitly use vanilla crop/tree substitutes");
        var west=ScientificTaxonProfiles15.byId("west_nile_virus");
        ok(west.interpretationCaution().contains("Horses"),
           "horse is not treated as sustaining the wild cycle");
        var asf=ScientificTaxonProfiles15.byId("african_swine_fever_virus");
        ok(asf.verifiedContext().contains("no human infection"),
           "pigs only and no human infection");
        var flu=ScientificTaxonProfiles15.byId("influenza_a_virus");
        ok(flu.interpretationCaution().contains("Aggregate influenza"),
           "bundled aggregate influenza reports not falsely influenza A subtype");
        var lateBlight=ScientificTaxonProfiles15.byId("phytophthora_infestans");
        ok(PathogenRoster15.byId(lateBlight.taxonId()).type()==
           PathogenRoster15.Type.OOMYCETE,
           "late blight agent is oomycete not fungus");
        ok(ScientificTaxonProfiles15.byId("xanthomonas_campestris").usesVisualProxy(),
           "beetroot does not imply brassica host");
        ok(ScientificTaxonProfiles15.byId("erwinia_amylovora").usesVisualProxy(),
           "oak leaves do not imply apple or pear hosts");
        ok(ScientificTaxonProfiles15.byId("tobacco_mosaic_virus").usesVisualProxy(),
           "beetroot is a visual proxy for TMV");
        System.out.println("V21_ALL_15_DISEASE_AGENTS_PASS checks="+checks+
           " sourced=true visualProxiesHonest=true noRealTransmissionCoefficients=true");
    }
}
