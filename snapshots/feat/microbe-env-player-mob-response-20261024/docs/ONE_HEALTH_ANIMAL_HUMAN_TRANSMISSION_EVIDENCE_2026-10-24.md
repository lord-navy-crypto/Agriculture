# One Health: real animal viruses, human effects and evidence-based transmission contexts

**Experimental extension of [real natural microbe ecology PR #35](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/pull/35). Minecraft 1.21.1, NeoForge 21.1.x, Java 21. October 24, 2026.**

## Scope

Minecraft can present **real, scientifically documented** viruses and bacteria together with naturally occurring animal hosts and human health consequences. It cannot turn a Minecraft animal sighting or pixel contact into an actual epidemiological infection. This project distinguishes:

1. **Taxonomy** — scientifically accepted pathogen classification and IDs.
2. **Host and environment** — which hosts or broad settings are documented for the *specified strain/group context*.
3. **Public-health interface** — authority-supported broad categories of animal-human contact.
4. **Observation** — an in-game sighting, reported contact or symptom entry, **not** proof of infection.
5. **Documented case** — evidence from a specific external record, to be checked independently. Generic species references and game proximity are never sufficient.
6. **Dynamic transmission** — NOT implemented, because no source-validated per-population rates, host susceptibility, contextual uncertainty or compatible source dataset has yet been provided. We will not silently invent those values.

This approach uses real taxonomy and real documented natural disease, with **no fictional pathogen names** and no invented spread probability, incubation, infectious dose, lab handling steps, mutation mechanics, treatment prescription or medical equipment dependency.

## Real human-animal virus records (implemented)

| Case study | Real taxonomic identification | Documented public-health scope | Human and animal consequences |
|---|---|---|---|
| Rabies virus | *Lyssavirus rabies*, NCBI **11292** (ICTV accepted) | Rabies in mammals and people; infection associated with infected-animal bites or scratches. Do not infer individual disease from the presence of a wolf, fox, dog or bat in Minecraft. | Rabies affects susceptible mammals and can cause serious neurological illness in people. |
| Avian-origin influenza A case group | *Influenza A virus*, NCBI **11320**; broad taxonomic classification, NOT an individual bird flu strain | WHO describes wild aquatic bird reservoirs, disease in birds and some mammals, and occasional zoonotic human cases in specified animal-origin viral groups. Scope MUST remain **avian/zoonotic subset**, not every influenza A subtype. | Some avian-origin viruses can cause eye or respiratory illness in people; severity varies widely. No claim of sustained human-to-human transmission for the currently assessed zoonotic subgroup. |

**Sources checked:**

- NCBI Taxonomy, *Lyssavirus rabies*: https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=11292&mode=Info
- CDC, *About Rabies*, 27 Jan 2026: https://www.cdc.gov/rabies/about/
- NCBI Taxonomy, *Influenza A virus*: https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=11320&mode=Info
- WHO, *Influenza (avian and other zoonotic)*, 21 Jan 2026: https://www.who.int/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29
- WHO, *One Health*, 8 May 2026: https://www.who.int/news-room/fact-sheets/detail/one-health
- CDC, *About Zoonotic Diseases*, 7 Apr 2025: https://www.cdc.gov/one-health/about/about-zoonotic-diseases.html

**Important scientific distinction:** `11320` encompasses many influenza A viruses. WHO's zoonotic avian source data cannot be treated as one uniform host range, human transmission pattern or disease outcome across every subtype. A contact event may be compatible with a documented *category*, but cannot infer actual infection, a chain of spread, or the clinical condition of an individual person/animal.

## Implemented (P1 vertical slice)

- `OneHealthEvidenceCatalog.java`: true NCBI IDs, accepted scientific names, host categories, CDC/WHO-sourced broad exposure evidence, documented human/animal impacts, explicit source URLs and scope/uncertainty.
- `OneHealthObservationPolicy.java`: read-only classification of four evidence types: animal presence, reported contact, unverified human symptoms and external case references. Never changes health, adds infection, creates new cases or loads chunks.
- `OneHealthScreen.java`: **native Minecraft GUI**, opened from the existing remappable **H** key's `NightwatchScreen`. Previous/next organism and details pages show real NCBI ID, scope, host groups, human/animal effects and limitations. All information local/read-only: no fake slash command, no new network packet, no player NBT/health change.
- `en_us.json` and `zh_cn.json`: bilingual controls and explicit "contact is not infection" message.
- `qa/java/OneHealthEvidenceSmoke.java`: exhaustive route/category contract, source/host checks, virus subset restrictions and no auto-infection, plus observation evidence semantics.
- `qa/test_one_health_ui_contract.py`: verifies H-menu entry, native no-network atlas, source material and bilingual UI keys.
- Existing NeoForge build, client Mesa/Xvfb launch and dedicated server smoke remain mandatory. The atlas's rendered appearance still requires real client acceptance; Java compilation alone is not a screenshot pass.

## Future simulation and verification (not implemented)

- A species-by-species **observation database**, with each host/site record dated and attributed to an actual citable observation dataset. An externally supplied case timeline could then be animated in-world without inventing any infection events.
- Animal/host ecology as **documented relationships**, distinct from arbitrary game-defined spread. Source-verified events can be displayed as a narrated animation; unknowns must remain unknown rather than random propagation.
- World graphics can show microscope-like representation of a scientifically identified species, clearly labeled as a *visual model* and not a real-scale, real-time biological prediction.
- Any mathematical epidemic model would have to label its parameters, source population, collection period and uncertainty separately. This branch contains no user-facing "real-world danger score", predictive disease calculator or operational propagation optimizer.

## Functional acceptance

1. Press existing H key in real Minecraft 1.21.1 NeoForge client; choose "One Health". The bilingual GUI opens without commands or network requests.
2. Navigate to rabies and avian influenza pages. Confirm correct real name, NCBI ID, broad human/animal effects, uncertainty and CDC/WHO source.
3. UI shows no medical prescriptions, no infection probabilities and **no hidden player infection**.
4. Build runs original old Nightwatch client/server regressions successfully; do not overwrite existing saved worlds or frozen original features.
5. Check native Minecraft GUI screenshots and client interactions separately from pure Java smoke and source audit.

**Status:** implemented code and automated tests, pending newest-head full CI and real client graphical acceptance. Stable `main` remains unchanged.
