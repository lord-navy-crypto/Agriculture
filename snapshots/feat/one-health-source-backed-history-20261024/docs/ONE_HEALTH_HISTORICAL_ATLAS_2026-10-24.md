# One Health historical record viewer: stacked draft

This extension adds an offline, read-only, bilingual historical time line to the existing H-key One Health atlas on Minecraft 1.21.1 NeoForge. It is an education/reference viewer, not an infection simulation or a medical diagnostic tool.

## New gameplay
- H -> One Health -> Timeline. Previous/Next changes a historical event.
- Virus cycles: All / Rabies / Avian-origin influenza A. Category cycles: All / Animal / Human / Public health.
- A horizontal chronology highlights the selected source record. The Source button shows the official publication title and URL.
- All controls and source narratives have English and Chinese translations. Empty filters display a clear no-records message rather than inventing outbreaks.
- Dates reflect the original announcement, report publication, or stated historical year (1885), not invented dates of illness onset.
- Events are not generated from Minecraft mobs, contacts or proximity. No infection state, parameters or transmission model is included.

## Historical sources
- 1885 (year only): WHO vaccination history, rabies prevention milestone: https://www.who.int/news-room/spotlight/history-of-vaccination/a-brief-history-of-vaccination
- 1997-08-20 (announcement): Hong Kong Department of Health human A(H5N1) discovery: https://www.dh.gov.hk/textonly/english/press/1997/20_8_97.html
- 2024-03-25 (announcement): PAHO/WHO timeline records USDA dairy cow findings: https://www.paho.org/en/timeline-influenza-ah5n1-americas-region
- 2024-04-09 (report publication): WHO DON512 confirmed U.S. human case and exposure history: https://www.who.int/emergencies/disease-outbreak-news/item/2024-DON512
- 2024-05-30 (MMWR publication): CDC report on dairy cattle and two human cases in the reporting period: https://www.cdc.gov/mmwr/volumes/73/wr/mm7321e1.htm

## Validation
Run the Python QA suite, the Java 21 OneHealthHistorySmoke, the NeoForge build, and the existing real dedicated-server and graphical-client startup tests. A real game-session H-key button/small-window Chinese/English visual screenshot remains outstanding; a successful CI boot alone is not that test. Keep both original PR #36 and stable main unchanged pending acceptance.

## Evidence-association graph extension
- H -> One Health -> Hosts opens six source-backed, bilingual host/virus/evidence cards, with host group, real viral profile, documented statement, source reference and explicit limitations.
- Rabies: susceptible mammal groups and human disease (CDC). Avian-origin influenza A subset: wild birds and domestic poultry (WHO); 2024 U.S. dairy-cattle detection (CDC); one documented U.S. human case and reported cattle exposure history (WHO).
- The connecting lines are GRAPHIC evidence associations, not reconstructed transmission pathways. No game health state changes, health scores or player infections.
- The Hosts page links back to the historical timeline with the matching viral profile filter.
- Separate Java 21 smoke and Python UI regression tests are mandatory; manual in-game graphical acceptance still remains.
