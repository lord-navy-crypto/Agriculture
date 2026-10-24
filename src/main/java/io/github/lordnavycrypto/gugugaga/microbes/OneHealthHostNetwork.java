package io.github.lordnavycrypto.gugugaga.microbes;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A STATIC, SOURCE-BACKED One Health concept graph.
 *
 * An edge means its annotated broad ecology/public-health association is
 * documented; it is NEVER a simulated infection, epidemiological rate,
 * animal/player health state, or a location-based transmission prediction.
 */
public final class OneHealthHostNetwork {
    private OneHealthHostNetwork() {}

    public enum Role {
        NATURAL_HOST_GROUP,
        VECTOR_GROUP,
        INCIDENTAL_HOST_GROUP,
        HUMAN_HEALTH_CONTEXT,
        DOCUMENTED_PUBLIC_HEALTH_INTERFACE,
        DOCUMENTED_GENERAL_ECOLOGICAL_CYCLE
    }

    public record Node(String id,String caption,Role role) {
        public Node {
            Objects.requireNonNull(id);
            Objects.requireNonNull(caption);
            Objects.requireNonNull(role);
            if(!id.matches("[a-z_]{3,40}")||caption.isBlank())
                throw new IllegalArgumentException("Bad source-backed concept node");
        }
    }

    public record Edge(String sourceNode,String targetNode,String annotation,
                       String authorityUrl) {
        public Edge {
            Objects.requireNonNull(sourceNode);
            Objects.requireNonNull(targetNode);
            Objects.requireNonNull(annotation);
            Objects.requireNonNull(authorityUrl);
            if(annotation.isBlank() ||
                    !(authorityUrl.startsWith("https://www.cdc.gov/")
                    ||authorityUrl.startsWith("https://www.who.int/")))
                throw new IllegalArgumentException("No trusted evidence link");
        }
    }

    public record Network(String profileId,List<Node> nodes,List<Edge> edges,
                          String limits) {
        public Network {
            OneHealthEvidenceCatalog.byId(profileId);
            Objects.requireNonNull(nodes);
            Objects.requireNonNull(edges);
            Objects.requireNonNull(limits);
            if(nodes.isEmpty()||edges.isEmpty()||limits.isBlank())
                throw new IllegalArgumentException("Incomplete evidence network");
            nodes=List.copyOf(nodes);
            edges=List.copyOf(edges);
            HashSet<String> seen=new HashSet<>();
            for(Node n:nodes)
                if(!seen.add(n.id()))
                    throw new IllegalArgumentException("Duplicate host node");
            for(Edge e:edges)
                if(!seen.contains(e.sourceNode())
                        ||!seen.contains(e.targetNode())
                        ||e.sourceNode().equals(e.targetNode()))
                    throw new IllegalArgumentException("Unverified graph endpoint");
        }
        public Node node(String id) {
            return nodes.stream().filter(n->n.id().equals(id)).findFirst()
                    .orElseThrow(()->new IllegalArgumentException(
                            "Unknown graph node "+id));
        }
    }

    private static Node node(String id,String label,Role role) {
        return new Node(id,label,role);
    }
    private static Edge link(String a,String b,String label,String url) {
        return new Edge(a,b,label,url);
    }

    private static final String CDC_RABIES="https://www.cdc.gov/rabies/about/";
    private static final String WHO_AVIAN=
            "https://www.who.int/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29";
    private static final String CDC_WEST_NILE=
            "https://www.cdc.gov/west-nile-virus/causes/";

    private static final Network RABIES=new Network(
            "lyssavirus_rabies",
            List.of(
                node("infected_mammal","Affected mammal",
                        Role.NATURAL_HOST_GROUP),
                node("person","Person (human health)",
                        Role.HUMAN_HEALTH_CONTEXT),
                node("animal_contact","Reported bite or scratch",
                        Role.DOCUMENTED_PUBLIC_HEALTH_INTERFACE)),
            List.of(
                link("infected_mammal","animal_contact",
                        "CDC public-health contact context",CDC_RABIES),
                link("animal_contact","person",
                        "Potential exposure context, NOT diagnosed infection",
                        CDC_RABIES)),
            "No Minecraft animal sighting confirms rabies. This describes"
                    +" public-health categories, not a prediction.");

    private static final Network AVIAN_INFLUENZA=new Network(
            "avian_influenza_a_interface",
            List.of(
                node("wild_birds","Wild aquatic birds",
                        Role.NATURAL_HOST_GROUP),
                node("poultry","Domestic birds",
                        Role.NATURAL_HOST_GROUP),
                node("affected_animals","Affected animal groups",
                        Role.NATURAL_HOST_GROUP),
                node("person","Person (human health)",
                        Role.HUMAN_HEALTH_CONTEXT)),
            List.of(
                link("wild_birds","affected_animals",
                        "Avian-origin influenza ecology, subtype dependent",
                        WHO_AVIAN),
                link("poultry","affected_animals",
                        "Avian-origin animal influenza scope",WHO_AVIAN),
                link("affected_animals","person",
                        "Documented occasional zoonotic cases; not automatic",
                        WHO_AVIAN)),
            "This case concerns SOME avian-origin influenza A viruses, NOT"
                    +" every virus in NCBI 11320. No contact implies a case.");

    private static final Network WEST_NILE=new Network(
            "west_nile_virus",
            List.of(
                node("birds","Wild birds (reservoir groups)",
                        Role.NATURAL_HOST_GROUP),
                node("mosquitoes","Mosquito vectors",
                        Role.VECTOR_GROUP),
                node("person","Person (incidental)",
                        Role.HUMAN_HEALTH_CONTEXT),
                node("horse","Horse (incidental)",
                        Role.INCIDENTAL_HOST_GROUP)),
            List.of(
                link("birds","mosquitoes",
                        "Documented natural bird-vector circulation",
                        CDC_WEST_NILE),
                link("mosquitoes","birds",
                        "Documented natural vector-bird circulation",
                        CDC_WEST_NILE),
                link("mosquitoes","person",
                        "Incidental human health context; not an infection event",
                        CDC_WEST_NILE),
                link("mosquitoes","horse",
                        "Incidental horse health context",CDC_WEST_NILE)),
            "Humans and horses are usually DEAD-END hosts in this natural"
                    +" ecology. Never infer human-to-vector amplification,"
                    +" local virus presence, or a current infection.");

    private static final List<Network> ALL=
            List.of(RABIES,AVIAN_INFLUENZA,WEST_NILE);

    public static List<Network> all(){return ALL;}
    public static Network forProfile(String profileId) {
        Objects.requireNonNull(profileId);
        OneHealthEvidenceCatalog.byId(profileId);
        return ALL.stream().filter(n->n.profileId().equals(profileId))
                .findFirst().orElseThrow(()->new IllegalArgumentException(
                        "Missing independently sourced host graph: "+profileId));
    }

    /**
     * Real, researched graph connectivity; DOES NOT mean real transmission.
     * This predicate has no Minecraft location, exposure, time, strain or
     * infection state inputs.
     */
    public static boolean documentedConnection(String profileId,
                                               String sourceNode,
                                               String targetNode) {
        return forProfile(profileId).edges().stream().anyMatch(e->
                e.sourceNode().equals(sourceNode)
                &&e.targetNode().equals(targetNode));
    }
}
