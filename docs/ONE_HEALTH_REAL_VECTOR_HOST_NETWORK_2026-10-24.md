# Real One Health Host Networks · 动物—媒介—人类真实生态关系

**New experimental Minecraft 1.21.1 NeoForge feature, 24 October 2026.** Stacked on source-backed human/animal evidence PR #36. No historical horror or folklore content is overwritten and stable main remains untouched.

## Actual source-backed scope

This is a **documented ecological and public-health concept graph**, not a real-time prediction or a mathematical outbreak simulator. There are no fictional pathogens, invented infection probabilities, pathogen growth rates, exposure doses, mutation rules, treatment recipes, automated infection events, player health changes or real-world specimen instructions.

| Profile | NCBI taxon | Evidence graph | Important limitation |
|---|---:|---|---|
| Rabies virus (*Lyssavirus rabies*) | 11292 | affected mammal → documented public-health bite/scratch context → person | Minecraft wolf/dog/bat sightings never imply rabies in an individual |
| Avian-origin *Influenza A virus* groups | 11320 (broad parent) | wild aquatic birds / poultry → documented affected-animal groups → human health context | WHO's zoonotic avian evidence describes **some** animal-origin influenza viruses, not all influenza A subtypes/strains |
| West Nile virus (*West Nile virus*; lineage *Orthoflavivirus nilense*) | 11082 | birds ↔ mosquitoes as an established high-level ecology; mosquitoes → incidental people or horses | people and horses ordinarily do **not** maintain this natural bird–mosquito cycle; observing a mosquito never proves infection |

Real scientific sources:

- [NCBI Taxonomy: West Nile virus 11082](https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=11082&mode=Info)
- [CDC, West Nile: Causes and How It Spreads, 15 July 2026](https://www.cdc.gov/west-nile-virus/causes/)
- [CDC, About West Nile, 16 June 2026](https://www.cdc.gov/west-nile-virus/about/index.html)
- [WHO, West Nile virus](https://www.who.int/news-room/fact-sheets/detail/west-nile-virus)
- [NCBI Taxonomy: Lyssavirus rabies 11292](https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=11292&mode=Info)
- [CDC, About Rabies](https://www.cdc.gov/rabies/about/)
- [NCBI Taxonomy: Influenza A virus 11320](https://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=11320&mode=Info)
- [WHO, Influenza (avian and other zoonotic)](https://www.who.int/news-room/fact-sheets/detail/influenza-%28avian-and-other-zoonotic%29)
- [WHO, One Health, 8 May 2026](https://www.who.int/news-room/fact-sheets/detail/one-health)

Graph arrows mean only **documented broad ecological or public-health relationships**, *never an event of infection or automatic disease spread inside Minecraft.* They cannot be used to infer an animal's infection state, human symptoms or a local outbreak. No reverse 'human → mosquito → bird' or 'horse → mosquito' arrow is present in the West Nile ecology model.

## New code actually delivered

- `src/main/java/io/github/lordnavycrypto/gugugaga/microbes/OneHealthEvidenceCatalog.java`: third genuine virus record, West Nile virus 11082, real bird/mosquito/human/horse documented scope and source URLs.
- `OneHealthHostNetwork.java`: immutable, typed nodes and arrows for all three natural virus case studies. Every relationship stores a CDC or WHO authority URL; all endpoint IDs must exist in the selected graph.
- `client/OneHealthScreen.java`: the fourth details page of the native existing H-key One Health UI now displays the case-specific directed evidence graph. It **only reads locally bundled knowledge**, has no automatic animal scan, side effects or extra networking.
- `en_us.json` and `zh_cn.json`: bilingual ecology network and 'contact is not infection' disclaimer.
- `qa/java/OneHealthHostNetworkSmoke.java`: tests all documented graph links, exact NCBI identifiers, strict host roles, no unsupported edge, no incorrect human/horse reverse vector pathway, and the influenza subgroup caveat.
- `qa/java/OneHealthEvidenceSmoke.java`: existing real virus tests expanded from two to three records, including documented human health interface and negative controls.
- `qa/test_one_health_ui_contract.py`: verifies the fourth UI page is real native data visualization, contains bilingual keys and never spawns infections or sends packets.

## Acceptance gates

1. `javac` + smoke must show `GUGUGAGA_ONE_HEALTH_HOST_GRAPH_PASS` and the previous `GUGUGAGA_ONEHEALTH_REAL_ANIMAL_HUMAN_PASS`.
2. Full `gradle build` compiles the actual Minecraft client UI, including the four-page native display.
3. Actual dedicated server boot and graphical client launch checks remain enabled. UI click-path screenshot verification is separate and is **not implied merely by booting the client**.
4. No additional permanent per-tick tasks, chunk loading, networking, world mutation, player infection, or health-care equipment.

## Meaningful next stages

- More genuine biological groups, prioritizing observable **animal/plant/microbe relationships** and publicly documented nonhuman reservoirs; each record must include its authority and limits.
- A user-toggleable, read-only in-game field guide for these relationships that never labels unspecified local prevalence as known.
- Histories may be shown from **actual documented public surveillance datasets with a fixed date and geography**, but without inventing individual infection events or a dynamic propagation probability.
- Do not claim this is a true pathogen-spread computation, clinical forecast, medical recommendation or reproducible biological experiment. It is a scientifically sourced ecological **visualization and educational simulator foundation**.
