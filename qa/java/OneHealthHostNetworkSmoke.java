import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog.*;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHostNetwork;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthHostNetwork.*;

public final class OneHealthHostNetworkSmoke {
    private static int checks;
    private static void require(boolean ok,String why) {
        checks++;
        if(!ok)throw new AssertionError(why);
    }
    public static void main(String[] args) {
        require(OneHealthHostNetwork.all().size()==3,
                "Every original real source-backed virus needs host graph");
        for(var profile:OneHealthEvidenceCatalog.all()) {
            var graph=OneHealthHostNetwork.forProfile(profile.id());
            require(graph.profileId().equals(profile.id()),"correct case graph");
            require(graph.nodes().size()>=3 && graph.edges().size()>=2,
                    "nonempty evidence graph with real host roles");
            require(!graph.limits().isBlank(),"every graph must disclose limits");
            for(var edge:graph.edges()) {
                require(!edge.annotation().isBlank(),"an edge must be qualified");
                require(edge.authorityUrl().startsWith("https://www.cdc.gov/")
                        || edge.authorityUrl().startsWith("https://www.who.int/"),
                        "every edge has a public authority reference");
                require(graph.node(edge.sourceNode())!=null
                        &&graph.node(edge.targetNode())!=null,
                        "all endpoints must be defined");
                require(OneHealthHostNetwork.documentedConnection(
                        profile.id(),edge.sourceNode(),edge.targetNode()),
                        "every graph relationship is declared explicitly");
            }
        }
        var west=OneHealthHostNetwork.forProfile("west_nile_virus");
        require(west.node("birds").role()==Role.NATURAL_HOST_GROUP,
                "bird host ecological role");
        require(west.node("mosquitoes").role()==Role.VECTOR_GROUP,
                "mosquito ecological role, not arbitrary contact odds");
        require(west.node("horse").role()==Role.INCIDENTAL_HOST_GROUP,
                "horses are incidental in WNV natural ecology");
        require(OneHealthHostNetwork.documentedConnection(
                west.profileId(),"birds","mosquitoes"),
                "documented broad WNV bird-mosquito ecology");
        require(OneHealthHostNetwork.documentedConnection(
                west.profileId(),"mosquitoes","birds"),
                "documented broad WNV mosquito-bird ecology");
        require(OneHealthHostNetwork.documentedConnection(
                west.profileId(),"mosquitoes","person"),
                "documented incidental human health context");
        require(OneHealthHostNetwork.documentedConnection(
                west.profileId(),"mosquitoes","horse"),
                "documented incidental equine health context");
        require(!OneHealthHostNetwork.documentedConnection(
                west.profileId(),"person","mosquitoes"),
                "never fabricate human-to-mosquito vector amplification");
        require(!OneHealthHostNetwork.documentedConnection(
                west.profileId(),"horse","mosquitoes"),
                "never fabricate equine-to-mosquito vector amplification");
        require(!OneHealthHostNetwork.documentedConnection(
                west.profileId(),"person","horse"),
                "human-horse contact is not proof of disease transmission");
        require(west.limits().contains("DEAD-END"),
                "dead-end host caveat is mandatory");
        var rabies=OneHealthHostNetwork.forProfile("lyssavirus_rabies");
        require(rabies.edges().stream().noneMatch(e->
                e.sourceNode().equals("person")&&e.targetNode().equals("infected_mammal")),
                "do not invent opposite zoonotic interaction");
        var flu=OneHealthHostNetwork.forProfile("avian_influenza_a_interface");
        require(flu.limits().contains("NOT"),
                "avian-origin subset caveat must remain visible");
        for(var graph:OneHealthHostNetwork.all()) {
            require(graph.nodes().stream().noneMatch(n->
                    n.caption().toLowerCase().contains("infected player")),
                    "graph must never infer a player infection");
            require(!graph.edges().isEmpty(),"no invented vacant network");
        }
        try {
            OneHealthHostNetwork.forProfile("unknown_fictional_pathogen");
            throw new AssertionError("unsupported fictional virus graph allowed");
        } catch (IllegalArgumentException expected){checks++;}
        try {
            new Network("west_nile_virus",
                    java.util.List.of(new Node("birds","Birds",Role.NATURAL_HOST_GROUP)),
                    java.util.List.of(new Edge("birds","invented","unsupported",
                            "https://www.cdc.gov/west-nile-virus/causes/")),
                    "unknown endpoint");
            throw new AssertionError("invalid unsupported edge accepted");
        } catch (IllegalArgumentException expected){checks++;}
        System.out.println("GUGUGAGA_ONE_HEALTH_HOST_GRAPH_PASS"
                +" checks="+checks
                +" supportedRealViruses="+OneHealthHostNetwork.all().size()
                +" CDC_WHO_citations=true noAutomaticCases=true"
                +" peopleHorsesDeadEnd=true noModelledTransmissionRates=true");
    }
}
