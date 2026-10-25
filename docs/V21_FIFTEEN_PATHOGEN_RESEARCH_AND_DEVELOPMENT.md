# V21 — 15 real pathogen identities: responsible scientific simulation roadmap

## What V21 does and does not do
The mod retains **all 15 real disease agents** exactly as registered by `PathogenRoster15` (8 viruses, 6 bacteria, 1 oomycete). They are *not* renamed as benign microbes. Their classifications, source links, suitable types of public observations and vanilla host representation limits are available in `ScientificTaxonProfiles15` and through two game commands:

```text
/gugugaga field research list
/gugugaga field research potato_virus_y
```

This release provides a complete, **read-only** research registry, not 15 fitted transmission models. Epidemiological simulations, organism loads, actual environmental concentrations and infection probabilities must not be inferred from the current 0–15 Minecraft game signal, particles or visual host placeholders.

## All 15 identities: public evidence to prioritize

| Scientific taxon / existing registry ID | Health domain | Suitable public observational evidence | Important mapping caveat |
| --- | --- | --- | --- |
| Rabies virus — `lyssavirus_rabies` | Veterinary/public health | Animal surveillance case counts, jurisdiction and reporting period | Wolf/fox/cat game entities are visual contexts, not clinical observations |
| Influenza A virus — `influenza_a_virus` | Human/animal One Health | Laboratory surveillance with explicit virus type/subtype, tests and specimen scope | Existing bundled CDC *overall influenza* clinical-lab series cannot identify subtype A by itself |
| West Nile virus — `west_nile_virus` | Human/animal/vector health | Separate public surveillance streams by birds, mosquitoes, horses, humans | Horses and people are usually incidental dead-end hosts, not sustaining vector cycles |
| Foot-and-mouth disease virus — `foot_and_mouth_disease_virus` | Veterinary health | Official reportable livestock outbreak summaries | Animal movement is not a confirmed event or a measured force of infection |
| African swine fever virus — `african_swine_fever_virus` | Veterinary health | Official pig outbreak reports, location and status | Pig disease, *not a human infection* |
| Salmonella enterica — `salmonella_enterica` | Food safety and public health | Laboratory-positive food/animal specimens; separately human surveillance cases | Food positives ≠ infected people |
| Listeria monocytogenes — `listeria_monocytogenes` | Food safety and public health | Routine food/environment sampling and human clinical cases as distinct tables | Environmental positive ≠ human listeriosis |
| Campylobacter jejuni — `campylobacter_jejuni` | Food safety and public health | Food/animal detection and reported human campylobacteriosis | Sampled poultry is not automatically infected |
| Staphylococcus aureus — `staphylococcus_aureus` | Human and veterinary health | Specimen-based lab detections and clinical case definitions | Colonization, detection and disease are different labels |
| Tobacco mosaic virus — `tobacco_mosaic_virus` | Plant pathology | Plant disease surveys and microscopy/lab-confirmed records | Vanilla beetroot is a **non-host visual proxy** |
| Cucumber mosaic virus — `cucumber_mosaic_virus` | Plant pathology | Crop survey counts, tested specimens and plant identification | Vanilla melon/pumpkin stems are appearance proxies; no diagnosis |
| Potato virus Y — `potato_virus_y` | Plant pathology | Potato crop/seed survey detections and cultivar-specific reports | Visual mosaic alone does not prove PVY |
| Phytophthora infestans — `phytophthora_infestans` | Plant pathology | Potato late-blight crop reports and agronomic observations | **Oomycete**, not fungus; field rain is not measured disease occurrence |
| Xanthomonas campestris — `xanthomonas_campestris` | Plant pathology | Brassica black-rot surveillance with pathovar noted | Vanilla beetroot is **not** a scientifically supported brassica host |
| Erwinia amylovora — `erwinia_amylovora` | Plant pathology | Apple/pear fire-blight plant survey records | Vanilla oak/other leaves do **not** stand for real apple/pear hosts |

## Developer priorities and appropriate validation

**Phase A: species-accurate observation.** Store the original official observation metadata alongside each import: pathogen ID, reporting authority, source URL, retrieval date, observation date/window, geography, host category, unit (laboratory specimens, clinical cases, animal outbreaks, affected plant samples), denominator where available, reporting delays, missingness, revision version and source-data checksum. Never silently merge distinct surveillance denominators or human/animal/plant streams.

**Phase B: evidence and visualization.** Expose each taxon's references and known data coverage in game, plus warnings for vanilla stand-ins. Render *observed* regional aggregate cases, samples and test positivity through separate read-only overlays without converting them into individual infected Minecraft mobs. Use categorical host/region labels for educational exploration, and explicitly distinguish published measurements from simulated scenarios. A report about one country/year must not turn into local data at an unrelated Minecraft block.

**Phase C: responsible computational research.** Validate time-series transformations, numerical accounting, missing-week handling and uncertainty on public aggregate surveillance data. For ecological process simulation, use bounded, non-agent-specific demonstrators and evaluation methods; do not build or calibrate agent-specific high-fidelity infection chains, optimal dispersal, environmental pathogen persistence or dose-response models. A source-linked *qualitative* disease ecology description is not an infectiousness coefficient.

**Phase D: QA.** Ensure all 15 taxa are accounted for; do not confuse humans with pigs or horses with sustaining West Nile hosts; classify late blight as oomycete; disclose the three vanilla plant visual proxies. Run Java21/Python QA, NeoForge JAR compile, real server/client boot and old SavedData restart. Verify all commands in a disposable Minecraft save using a real graphical client; CI boot alone does not demonstrate readability/performance in gameplay.

## Scientific references
- CDC Rabies: https://www.cdc.gov/rabies/about/
- WHO zoonotic influenza: https://www.who.int/en/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29
- CDC West Nile: https://www.cdc.gov/west-nile-virus/causes/
- WOAH foot-and-mouth: https://www.woah.org/en/disease/foot-and-mouth-disease/
- WOAH African swine fever: https://www.woah.org/en/disease/african-swine-fever/
- CDC Salmonella: https://www.cdc.gov/salmonella/spread/
- CDC Listeria: https://www.cdc.gov/listeria/causes/index.html
- CDC Campylobacter: https://www.cdc.gov/campylobacter/index.html
- CDC staph: https://www.cdc.gov/staph-infection/about/index.html
- UMD extension plant viruses: https://extension.umd.edu/resource/viruses-vegetables
- RHS cucumber mosaic virus: https://www.rhs.org.uk/disease/cucumber-mosaic-virus
- Cornell potato virus: https://www.vegetables.cornell.edu/pest-management/disease-factsheets/virus-and-viroid-diseases-of-potato/
- PNW potato late blight: https://pnwhandbooks.org/node/3325/print
- AHDB brassica black rot: https://horticulture.ahdb.org.uk/knowledge-library/black-rot-of-brassicas-prevention-and-control
- NC State fire blight: https://content.ces.ncsu.edu/fire-blight

Keep PR #44 **Draft**, do not merge until exact HEAD CI and real in-world verification. Original NBT file identifiers remain unchanged.
