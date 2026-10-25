# Gugu-Gaga Natural Microbe Ecology — 真实自然微生物生态模拟

**Minecraft 1.21.1 / NeoForge 21.1.x / Java 21 — independent experimental branch, 2026-10-24.**

## Corrected goal

The project is no longer an invented-virus disease mini-game with hospitals, cure recipes, medical devices, fictional pathogens or made-up epidemiological values. The user requested **real natural microorganisms and real naturally occurring viruses**. The new direction is a microbe/virus ecosystem: species identification, documented niches, phage/host relationships, and later observable ecological changes.

This branch starts from historical v0.10 H-key UI source, independently of the folklore anthology; the old main and both frozen experiment branches remain unchanged.

### Existing first four actual taxa

| Actual scientific taxon | NCBI taxonomy ID | Type | Carefully bounded real fact |
|---|---:|---|---|
| *Escherichia coli* | 562 | Bacterium | Naturally associated with intestines; not every strain is pathogenic. |
| *Bacillus subtilis* | 1423 | Bacterium | Natural soil/water-associated bacterium. |
| *Lactiplantibacillus plantarum* | 1590 | Bacterium | Plant-associated lactic-acid bacterium; the taxonomic name is real. |
| *Tequatrovirus T4* | 10665 | Virus (bacteriophage) | A naturally occurring virus with an established *E. coli* host relationship. It is **not** a human-infecting virus. |

All names and exact IDs are traced to NCBI Taxonomy. The phage–host relation is supported by NCBI's *Bacteriophage T4* entry and UniProt's T4 host taxonomy. This establishes a **species-level relation**, not universal susceptibility of all *E. coli* strains and **not** proof that mere presence of phage/host caused infection.

### Authoritative citations

- NCBI Taxonomy: [E. coli 562](https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=562&mode=Info)
- NCBI Taxonomy: [B. subtilis 1423](https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=1423&mode=Info)
- NCBI Taxonomy: [L. plantarum 1590](https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=1590&mode=Info)
- NCBI Taxonomy (ICTV-accepted species): [Tequatrovirus T4 10665](https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=10665&mode=Info)
- NCBI MeSH: [Bacteriophage T4](https://www.ncbi.nlm.nih.gov/mesh?Cmd=DetailsSearch&Db=mesh&Term=%22Bacteriophage+T4%22%5BMeSH+Terms%5D)
- NCBI PubChem taxonomy: [B. subtilis natural soil/water biology](https://pubchem.ncbi.nlm.nih.gov/taxonomy/1423); [L. plantarum](https://pubchem.ncbi.nlm.nih.gov/taxonomy/1590)

## Strict scientific data policy

1. **Real species only.** New entries require an actual stable taxonomy identifier, verified scientific name and source URL. No `mist_fever`, `rust_bloom` or other fictional organisms.
2. **No invented transmission, growth or death numbers.** Do not synthesize disease R0, pathogen dose, aerosol range, environmental survival time, incubation, culture temperature, replication speed, mutation rate, player infection chance or medical effects. IDs such as 562 are taxonomy identifiers, not biological measurements.
3. **No false certainty.** A soil biome is not evidence that a given bacterium was observed at that block. Do not auto-spawn an identified organism solely because a Minecraft biome looks like its source habitat.
4. **Factual interactions only.** The T4–E. coli documented species relation is listed in the simulation, but does not mean each bacterial strain is compatible or a sampled cell was actually infected.
5. **Visible unknown states.** When actual quantitative or site-specific data is unavailable, label it `unknown / not modeled` rather than introducing a random percentage.
6. **No healthcare gameplay dependency.** No hospital, treatment items, blood/infection mechanic, diagnostic lab recipes or medical equipment are required by this project. Future education can include high-level biological distinctions without real-world handling protocols.
7. **Real biological observation versus game representation.** Minecraft's blocks and ticks cannot directly measure real-world microorganisms. The rules engine explicitly distinguishes supplied observations from habitat descriptions and mathematical assumptions. Any future rendered microorganisms will be **visual representation**, not a calibrated laboratory predictor.

## What the repository now implements (P0)

- `NaturalMicrobeCatalog`: the four genuine source-linked taxa, documented habitat associations, T4 → *E. coli* host-relationship graph. Every taxon has a real ID; no infection timing or rates.
- `NaturalMicrobeEcosystem`: an evidence-led observational ecosystem. It accepts **explicitly observed species** at a given sample site and reports taxonomy, source-supported habitat information and conditional phage–host relations. It does not invent species abundance, blood contacts or automatic host infection.
- `NaturalMicrobeEcosystemSmoke`: executable Java21 taxonomy-ID and relationship graph regression plus tests proving that empty soil does **not** fabricate organisms and unknown locations remain explicitly unverified.
- CI: source, legacy horror functionality, dedicated server and real client launch remain active; no automatic world alteration introduced by the new model.

## Next safe implementation milestones (not yet completed)

**P1 — In-world observational overlay.** Reuse the existing Minecraft UI to show a real species list, the cited source and ecological role, rather than a health-treatment screen. The player views specimens as a science atlas. Only actual data entered/verified by the player (or a separately sourced dataset) should show as `present`; Minecraft biomes may only suggest `possible habitat`.

**P2 — Data provenance files.** Store publicly verifiable taxon IDs, taxonomy source, observations and revision metadata in versioned JSON. Differentiate real species facts from game graphics. Expand taxa gradually and check references before adding them.

**P3 — Ecological simulation.** Add only qualitative outcomes that are unambiguously supported by ecology (e.g. a bacteriophage's documented host class). Any quantitative simulated growth/population dynamics require actual provenance, uncertainties and a separately reviewed domain model. Do **not** silently treat toy game values as real biology.

**P4 — Full game acceptance.** Validate Minecraft 1.21.1 NeoForge client GUI, multiplayer isolation and dedicated-server boot, player save compatibility, no chunk-wide scans and a reproducible in-game screenshot. Existing frozen Nightwatch and folklore branches are not merged.

This is an **initial real-taxonomy/ecological evidence engine**, not yet a working in-world virus animation or microscopic life population simulator. There is no medical hardware or fictional transmission system in this branch.
