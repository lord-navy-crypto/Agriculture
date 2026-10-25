package io.github.lordnavycrypto.gugugaga.microbes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Read-only, source-attributed report chronology, never a contagion simulator. */
public final class OneHealthHistoryCatalog {
    private OneHealthHistoryCatalog() {}
    public enum Kind { PUBLIC_HEALTH, ANIMAL_REPORT, HUMAN_REPORT }
    public record Event(String id, String reportDate, String profileId, Kind kind,
            String region, String hostEn, String hostZh,
            String titleEn, String titleZh, String descriptionEn, String descriptionZh,
            String caveatEn, String caveatZh, String authority, String sourceUrl) {
        public Event {
            Objects.requireNonNull(kind);
            OneHealthEvidenceCatalog.byId(Objects.requireNonNull(profileId));
            if (id == null || !id.matches("[a-z0-9_]{3,64}")
                    || reportDate == null
                    || !reportDate.matches("\\d{4}(-\\d{2}(-\\d{2})?)?")
                    || Integer.parseInt(reportDate.substring(0,4)) < 1800
                    || Integer.parseInt(reportDate.substring(0,4)) > 2026
                    || region == null || region.isBlank()
                    || hostEn == null || hostEn.isBlank()
                    || hostZh == null || hostZh.isBlank()
                    || titleEn == null || titleEn.isBlank()
                    || titleZh == null || titleZh.isBlank()
                    || descriptionEn == null || descriptionEn.isBlank()
                    || descriptionZh == null || descriptionZh.isBlank()
                    || caveatEn == null || caveatEn.isBlank()
                    || caveatZh == null || caveatZh.isBlank()
                    || authority == null || authority.isBlank()
                    || sourceUrl == null || !(
                        sourceUrl.startsWith("https://www.who.int/")
                        || sourceUrl.startsWith("https://www.cdc.gov/")
                        || sourceUrl.startsWith("https://www.paho.org/")
                        || sourceUrl.startsWith("https://www.dh.gov.hk/")))
                throw new IllegalArgumentException("Incomplete sourced history record");
            if (reportDate.length() == 10) LocalDate.parse(reportDate);
            if (reportDate.length() == 7) YearMonth.parse(reportDate);
        }
        public String title(boolean zh) { return zh ? titleZh : titleEn; }
        public String description(boolean zh) { return zh ? descriptionZh : descriptionEn; }
        public String caveat(boolean zh) { return zh ? caveatZh : caveatEn; }
        public String host(boolean zh) { return zh ? hostZh : hostEn; }
    }

    private static final List<Event> EVENTS = List.of(
        new Event("rabies_vaccination_1885","1885","lyssavirus_rabies",
            Kind.PUBLIC_HEALTH,"France","Human health / rabies prevention","人类健康／狂犬病预防",
            "1885: rabies prevention milestone","1885：狂犬病预防里程碑",
            "WHO records Pasteur's successful post-exposure rabies prevention in 1885.",
            "世卫组织记载了巴斯德于1885年成功开展的狂犬病暴露后预防。",
            "Year only; historical milestone, not medical instructions or a contemporary infection count.",
            "只知道年份；历史里程碑，不是医疗指导或当代感染统计。",
            "WHO — A Brief History of Vaccination",
            "https://www.who.int/news-room/spotlight/history-of-vaccination/a-brief-history-of-vaccination"),
        new Event("h5n1_hongkong_1997","1997-08-20","avian_influenza_a_interface",
            Kind.HUMAN_REPORT,"Hong Kong","Avian A(H5N1) and a confirmed human case",
            "禽源 H5N1 与人类确诊病例",
            "1997: first human H5N1 case announced","1997：首次公布人类 H5N1 病例",
            "Hong Kong's Department of Health announced the first identified human A(H5N1) case.",
            "香港卫生署公布首例已鉴定的人类甲型 H5N1 流感病例。",
            "The date is the official announcement, not symptom onset; no inferred transmission chain.",
            "日期为官方公告日，而不是发病日；不能据此推导传播链。",
            "Hong Kong Department of Health — 20 Aug 1997",
            "https://www.dh.gov.hk/textonly/english/press/1997/20_8_97.html"),
        new Event("h5n1_us_cattle_2024","2024-03-25","avian_influenza_a_interface",
            Kind.ANIMAL_REPORT,"USA — Texas and Kansas","Cattle / A(H5N1) detections",
            "奶牛／A(H5N1) 检出",
            "2024: dairy-cattle findings announced","2024：公布奶牛检出记录",
            "PAHO/WHO records a USDA announcement of H5N1 detections in dairy cattle.",
            "泛美卫生组织／世卫组织记录了美国农业部公布的奶牛 H5N1 检出信息。",
            "A dated animal surveillance report, not proof that nearby animals are infected.",
            "这是有日期的动物监测报告，不能证明附近的动物已被感染。",
            "PAHO/WHO — Americas A(H5N1) timeline",
            "https://www.paho.org/en/timeline-influenza-ah5n1-americas-region"),
        new Event("h5n1_who_us_case_2024","2024-04-09","avian_influenza_a_interface",
            Kind.HUMAN_REPORT,"USA — Texas","Documented cattle exposure history / human case",
            "奶牛接触史／人类病例",
            "2024: WHO reports a human A(H5N1) case","2024：世卫组织通报人类 H5N1 病例",
            "WHO reported a confirmed human case notified on April 1 with a history of exposure to dairy cattle presumed infected.",
            "世卫组织通报了一例于4月1日报告的确诊病例，患者曾接触疑似受感染的奶牛。",
            "The date is WHO's report publication date; exposure history does not reconstruct the exact transmission chain.",
            "日期是世卫组织公布日；接触史不能重建精确传播链。",
            "WHO — Disease Outbreak News DON512",
            "https://www.who.int/emergencies/disease-outbreak-news/item/2024-DON512"),
        new Event("h5n1_cdc_may2024","2024-05-30","avian_influenza_a_interface",
            Kind.HUMAN_REPORT,"United States","Dairy-cattle investigation / two human cases",
            "奶牛疫情调查／两例人类病例",
            "2024: CDC reports cattle and two human cases","2024：CDC 报告奶牛疫情和两例人类病例",
            "CDC MMWR reviewed the 2024 U.S. dairy-cattle investigation and two human cases identified during that period.",
            "CDC 的 MMWR 周报回顾了美国奶牛疫情调查及调查期内发现的两例人类病例。",
            "An archived period-specific report; not today's total or evidence of human-to-human spread.",
            "这是特定时期的历史报告，不是当前总病例数，也不是人际传播证据。",
            "CDC MMWR 73(21), 30 May 2024",
            "https://www.cdc.gov/mmwr/volumes/73/wr/mm7321e1.htm")
    ).stream().sorted(Comparator.comparing(Event::reportDate)).toList();

    public static List<Event> all() { return EVENTS; }
    /** Null filter means all. No generated events, disease risk scores, or network calls. */
    public static List<Event> filter(String profileId, Kind kind) {
        if (profileId != null) OneHealthEvidenceCatalog.byId(profileId);
        return EVENTS.stream()
            .filter(e -> profileId == null || e.profileId().equals(profileId))
            .filter(e -> kind == null || e.kind() == kind).toList();
    }
}
