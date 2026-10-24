package io.github.lordnavycrypto.gugugaga.microbes;

import java.util.List;
import java.util.Objects;
import io.github.lordnavycrypto.gugugaga.microbes.OneHealthEvidenceCatalog.HostContext;

/**
 * Source-linked host associations, not contagion edges. A host category may
 * document disease history without supporting transmission for a game entity.
 */
public final class OneHealthHostEvidenceCatalog {
    private OneHealthHostEvidenceCatalog() {}
    public enum EvidenceKind {
        GENERAL_HOST_DOCUMENTED, ANIMAL_DETECTION_REPORTED,
        HUMAN_DISEASE_DOCUMENTED, HUMAN_CASE_REPORTED
    }
    public record Relation(String id, String profileId, HostContext hostContext,
            EvidenceKind evidenceKind, String hostEn, String hostZh,
            String scopeEn, String scopeZh, String caveatEn, String caveatZh,
            String sourceName, String sourceUrl) {
        public Relation {
            var profile=OneHealthEvidenceCatalog.byId(
                Objects.requireNonNull(profileId));
            Objects.requireNonNull(evidenceKind);
            Objects.requireNonNull(hostContext);
            if(id == null || !id.matches("[a-z0-9_]{3,64}")
                || !profile.documentedContexts().contains(hostContext)
                || hostEn==null || hostEn.isBlank()
                || hostZh==null || hostZh.isBlank()
                || scopeEn==null || scopeEn.isBlank()
                || scopeZh==null || scopeZh.isBlank()
                || caveatEn==null || caveatEn.isBlank()
                || caveatZh==null || caveatZh.isBlank()
                || sourceName==null || sourceName.isBlank()
                || sourceUrl==null || !(
                    sourceUrl.startsWith("https://www.cdc.gov/")
                    || sourceUrl.startsWith("https://www.who.int/")))
                throw new IllegalArgumentException("Unverified host evidence");
        }
        public String host(boolean zh) {return zh?hostZh:hostEn;}
        public String scope(boolean zh) {return zh?scopeZh:scopeEn;}
        public String caveat(boolean zh) {return zh?caveatZh:caveatEn;}
    }
    private static final List<Relation> RELATIONS=List.of(
        new Relation("rabies_mammal_hosts","lyssavirus_rabies",HostContext.MAMMALS,
            EvidenceKind.GENERAL_HOST_DOCUMENTED,"Mammals","哺乳动物",
            "CDC documents rabies in susceptible mammal hosts.",
            "CDC 记录了易感哺乳动物中的狂犬病。",
            "A Minecraft fox, wolf, or bat is not diagnosed by species or proximity.",
            "游戏中的狐狸、狼或蝙蝠不能凭物种或距离判断是否感染。",
            "CDC — About Rabies","https://www.cdc.gov/rabies/about/"),
        new Relation("rabies_human_illness","lyssavirus_rabies",HostContext.PEOPLE,
            EvidenceKind.HUMAN_DISEASE_DOCUMENTED,"Humans","人类",
            "Rabies can cause severe human neurological disease.",
            "狂犬病可能导致严重的人类神经系统疾病。",
            "A known human health outcome is not proof of any game character's infection.",
            "已知的人体健康影响不代表游戏角色受到感染。",
            "CDC — About Rabies","https://www.cdc.gov/rabies/about/"),
        new Relation("avian_wild_birds","avian_influenza_a_interface",
            HostContext.WILD_AQUATIC_BIRDS,EvidenceKind.GENERAL_HOST_DOCUMENTED,
            "Wild aquatic birds","野生水鸟",
            "WHO describes wild aquatic birds in avian-origin influenza A ecology.",
            "WHO 资料介绍了野生水鸟在禽源甲型流感生态中的角色。",
            "Applies to the avian-origin subset, not every influenza A virus.",
            "只适用于相关禽源病毒，并非所有甲型流感病毒。",
            "WHO — Zoonotic Influenza",
            "https://www.who.int/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29"),
        new Relation("avian_domestic_birds","avian_influenza_a_interface",
            HostContext.DOMESTIC_BIRDS,EvidenceKind.GENERAL_HOST_DOCUMENTED,
            "Domestic poultry","家禽",
            "WHO documents poultry involvement in avian influenza events.",
            "WHO 资料记录了家禽涉及禽流感疫情的情况。",
            "This describes a host group; it does not label every domestic bird infected.",
            "这只是宿主群体资料，不能据此认定每只家禽感染。",
            "WHO — Zoonotic Influenza",
            "https://www.who.int/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29"),
        new Relation("h5n1_us_dairy_cattle","avian_influenza_a_interface",
            HostContext.MAMMALS,EvidenceKind.ANIMAL_DETECTION_REPORTED,
            "Dairy cattle in U.S. 2024 reports","2024 年美国报告中的奶牛",
            "CDC MMWR documents A(H5N1) detections in affected dairy herds in 2024.",
            "CDC 的 MMWR 记录了 2024 年部分美国奶牛群体中的 H5N1 检出。",
            "Time and place limited; this does not assign infection to arbitrary cattle.",
            "仅适用于特定时空，不意味着任意奶牛都会被感染。",
            "CDC MMWR — 2024 dairy cattle report",
            "https://www.cdc.gov/mmwr/volumes/73/wr/mm7321e1.htm"),
        new Relation("h5n1_who_human_case","avian_influenza_a_interface",
            HostContext.PEOPLE,EvidenceKind.HUMAN_CASE_REPORTED,
            "Confirmed human case in U.S. WHO report, April 2024",
            "世卫组织 2024 年 4 月报告中的美国人类确诊病例",
            "WHO reports one confirmed case and a history of exposure to presumed affected cattle.",
            "世卫组织报告了一例确诊病例以及接触疑似受感染奶牛的记录。",
            "This does not establish every step in a chain of transmission.",
            "这不能证明每一个传播环节。",
            "WHO DON512 — 9 April 2024",
            "https://www.who.int/emergencies/disease-outbreak-news/item/2024-DON512")
    );
    public static List<Relation> all() {return RELATIONS;}
    public static List<Relation> byProfile(String id) {
        OneHealthEvidenceCatalog.byId(id);
        return RELATIONS.stream().filter(e->e.profileId().equals(id)).toList();
    }
}
