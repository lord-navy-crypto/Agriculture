# 志怪异闻录 / Folklore Horror Anthology — v0 foundation

**Project direction (2026-10-19):** Reimagine the Gugu-Gaga Minecraft horror mod as a **collection of independent supernatural cases** inspired by historical Chinese fantastic literature and authored original creatures.

## Branch boundary

This **new anthology branch** intentionally starts from the frozen, UI-first `v0.10.0-alpha.3` codebase. It must **not** mutate or merge the frozen PR #29, #30 or #31, and it must preserve all original Gugu-Gaga/Lapis/Yellow Smiler code, art and canon. The existing v0.10 release metadata remains unchanged until the anthology has real gameplay in a separate build. This first commit series is a **mechanics foundation**; it is not a finished creature pack.

## Creative and cultural principles

1. **Respect sources:** 《山海经》 and 《聊斋志异》 are collections of different genres and perspectives, not a single unified "monster wiki". Cite which chapter inspired each creature, and **clearly label invented mechanics** rather than attributing every ability to the texts.
2. **No flattened morality:** Strangeness does not always equal an enemy. Players may calm, investigate, help, pacify, repel or fictionally seal a creature depending on its case.
3. **Player agency:** No terminal command is needed for normal gameplay. Native remappable controls, items, right-click, an investigation notebook and meaningful cues are the planned interface. Legacy commands are optional diagnostics only.
4. **Procedural mystery:** Encounter a case spontaneously but only ONE case/group may be active in a dimension at once. Groups may later consist of related creatures (e.g., a focal creature, illusionary doubles or environmental witnesses). Related actors belong to one case, not stacked unrelated events.
5. **Fictional in-game ritual props:** compass-like `罗盘`, a `铜铃`, and a `照妖镜` are stylized gameplay sensors. Symbolic seals are Minecraft puzzle items, **not representations of real supernatural detection or real-world ritual instructions**.
6. **No borrowed game assets:** Investigation/identification pacing may be inspired by ghost-hunting games, but the individual creatures, textures, behavior and puzzles must have independently authored implementation and Chinese folkloric storytelling.

## First three source-based prototypes

| Case | Original source evidence | Designed behavior **(new fiction, not literal text)** | Characteristic clues | In-game ending |
|---|---|---|---|---|
| **长右 / Changyou** | 《山海经·南山经》: a four-eared creature whose appearance is associated with great water | A still water surface begins making four separated calls, distant reflections lag behind movement, a listening player sees misleading ripples | **水脉共鸣**, **四重回响**, **镜中水纹** | **平息水脉** (calm and release rather than destroy) |
| **狸力 / Lili** | 《山海经·南山经》: porcine-looking creature with digging-related image and association with earthworks | Hollow percussion below stone paths, displaced soil, brief glimpses of a burrowing creature; may be shy rather than malicious | **土脉共鸣**, **四重回响** (intentionally shared), **空心足迹** | **镇土地脉** |
| **画皮之影 / Painted Spirit** | 《聊斋志异·画皮》: a supernatural imposture/disguise narrative | A seemingly ordinary visitor occasionally speaks with a displaced echo, its reflection does not agree with its appearance, false witness trails | **空心足迹** (intentionally shared), **异声回应**, **镜中水纹** (intentionally shared) | **破除幻相** |

References:
- 《山海经·南山经》: https://zh.wikisource.org/zh-hans/%E5%B1%B1%E6%B5%B7%E7%BB%8F/%E5%8D%97%E5%B1%B1%E7%BB%8F
- 《聊斋志异》 catalogue and chapter "画皮": https://zh.wikisource.org/zh-hans/%E8%81%8A%E9%BD%8B%E5%BF%97%E7%95%B0

The Chinese evidence labels and fictional material effects are designed for the mod; ancient books do not describe our Minecraft instruments, evidence table, cooldown or seals.

## Probe table — tool signals are deliberately not one-to-one with a monster

| Creature | 罗盘 (luopan) | 铜铃 (copper bell) | 照妖镜 (reveal mirror) |
|---|---|---|---|
| 长右 | 水脉共鸣 | 四重回响 | 镜中水纹 |
| 狸力 | 土脉共鸣 | 四重回响 | 空心足迹 |
| 画皮之影 | 空心足迹 | 异声回应 | 镜中水纹 |

**Why this is playable deduction:** One sensor is insufficient by policy, and repeated use of the same sensor earns no extra evidence. Two distinct sensors identify exactly one of the three prototype cases. A wrong identification or wrong in-world resolution mechanism does not magically end the event. The use of real names and certain visual motifs is source-inspired; the evidence puzzle is authored for gameplay.

## Future vertical slice (NOT implemented by this foundation)

A future gameplay implementation needs all steps below before claiming this anthology pack playable:

1. Register three **distinct real EntityTypes**, native original models/textures/animations and meaningful AI for each, without recycling a penguin texture or a disguised vanilla zombie. Each creature group has its own spawn budget and natural behavior.
2. Register craftable inventory items 罗盘、铜铃、照妖镜 and symbolic Minecraft-only calm/seal props; add genuine item models, UI readouts and recipe discovery (never obtain-only-via-command).
3. On server tick: enforce an 8-minute minimum cooldown; roll a small chance at a bounded interval only while eligible players are exploring at night; activate a **single group** per world/dimension. Never force-load chunks, change world blocks unexpectedly or violate quiet/opt-out settings.
4. Play an environmental clue before showing the actor. Players approach and use their items in-world, gathering TWO independent clues to propose an identity using a visual journal.
5. Case progression and recovery: wrong hypotheses produce harmless feedback; correct response calms or dispels the encounter; expired/abandoned cases release the slot and use the next cooldown. Full save/reload & 2-player consent tests are required.
6. Add a genuine **in-world** acceptance harness: no commands in the player's flow, original visible model, item-in-hand network interactions, >1 simultaneous player and one-case limit, bad terrain, and a complete case ending.

## Future biome and structure distribution — SPEC ONLY, NOT IMPLEMENTED

The player proposed that **biomes and structures each acquire their own horror lore.** This is intentionally deferred so species are not forced into an inaccurate single habitat and the initial case loop can be validated first.

Tentative hooks rather than requirements:
- **雪原、冰川、寒冷针叶林** → original Gugu-Gaga / Lapis / Yellow Smiler case family, requiring an explicitly defined colder-than-threshold biome policy.
- **河岸、潮湿山地** → 长右 water-omen case.
- **丘陵、土石洞窟** → 狸力 excavation and soil anomalies.
- **村庄、荒宅、古庙等结构** → 画皮之影's imposture/household tale.
- Future 《聊斋志异》 episodes can include witnesses or morally neutral beings; they should not all be auto-aggressive monsters.

Whether to key to exact Minecraft biome tags, vanilla tags, terrain temperature, worldgen structures, or another custom map is **deferred**. Existing Gugu code should not be rewritten until those cases are separately playtested.

## What is NOW implemented

- `FolkloreEncounterPolicy`: three text-source-labeled creature profiles, deliberate cross-probe deduction, correct resolutions, single-event eligibility/cooldown, time limits and opt-out checks.
- `FolkloreCaseDirector`: a pure Java **single case slot** with probe deduplication, identification, case resolution, expiry and cooldown, for later server-side binding.
- `qa/java/FolkloreEncounterPolicySmoke.java` and `FolkloreCaseDirectorSmoke.java`: automatically executable Java21 unit smoke tests, including 18 ordered two-instrument combinations.
- CI step compiles and executes the policy/director independently before building the NeoForge mod.

**Not yet implemented:** live entity rendering, in-world natural spawn, craftable detector items, UI deduction panel, item-server packet integration, case persistence, real graphical gameplay, multiplayer and biome bindings. Do not distribute this code as a complete anthology release.

## Real-world safety and compatibility

No real-world occult claims, rituals, substances or instructions; all puzzles use fictional Minecraft mechanics. No dangerous content. The base Gugu-Gaga v0.10 remains Minecraft 1.21.1, Java 21, NeoForge 21.1.259, using native renderers without GeckoLib; the separate v0.21.3 experimental branch has different dependencies. Do not mix them.
