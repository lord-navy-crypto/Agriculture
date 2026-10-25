# Original horror trilogy → ONE folklore-style case / 原创冰原三影调查案

**Branch:** `feat/original-trio-investigation-case-20261019`  
**Parent:** new anthology draft PR #32 (not frozen Gugu PRs #29–#31).  
**Status:** implemented **pure Java case rules and CI tests**; native inventory tools / natural case spawning / actual scene-to-case binding **not implemented yet**.

## Non-negotiable fiction identity

Gugu-Gaga, Lapis/Redactor and Yellow Smiler are **three ORIGINAL fictional horror characters**, **not** from `山海经` or `聊斋志异`. Keeping the three original entities and existing personalities intact is more important than force-fitting them into an ancient source. Their case is named **冰原三影 (The Three Shadows of the Frostlands)**.

**A single case contains all three actors**. It occupies exactly ONE anthology case slot, and should not spawn as three independent case rolls. The base game still contains the older, independent Gugu nightwatch director, which **must eventually be made mutually exclusive** with the new anthology engine before any complete in-game release is claimed.

## Original trio vs. external folklore cases

| Story case | Inspiration classification | Distinct actors in one case | Planned world selection |
|---|---|---|---|
| Original `ORIGINAL_GUGU_TRIO` | **Original player-authored lore** | Gugu-Gaga, Lapis/Redactor, Yellow Smiler | Only in a sufficiently cold biome |
| `CHANGYOU` 长右 | `山海经·南山经` | 1 prototype | Water-related cases: future eligibility |
| `LILI` 狸力 | `山海经·南山经` | 1 prototype | Earth/stone cases: future eligibility |
| `PAINTED_SPIRIT` 画皮之影 | `聊斋志异·画皮` | 1 prototype | Villages, old homes: future eligibility |

For now, only cold-only **case selection logic** for the original trio is implemented. This does not mean the Minecraft biome resolver or automatic spawner already exists.

## Investigate the original trio using the SAME instruments

The instruments are intentionally **fictional Minecraft mystery props**, not actual measurements or external ritual tools.

| Fictional item | Original trio reading | What the story suggests |
|---|---|---|
| **罗盘** | `FROST_DISSONANCE` 冰原失谐 | Gugu's shadow moves differently from ordinary cold animals; there is an abnormal frost echo |
| **铜铃** | `FALSE_VOICE` 异声回应 | A friendly voice has copied part of its speaker's identity; this also occurs in the unrelated `画皮之影` case |
| **照妖镜** | `SMILING_AFTERIMAGE` 迟滞的笑影 | A smiling figure lingers where the actual actor has already moved |

**Two different tools minimum.** Repeat-scanning the same tool cannot count as a fresh clue. With two readings the case can be distinguished from the three source-inspired prototypes using our authored evidence table. Scans are **not the full solution**: the player must still witness the three actor scenes.

## Three-act in-world narrative / actual role order

1. **第一幕·冰原凝视 (FIRST_SIGHTING):** Gugu watches at a distance, its behavior is unsettling rather than an obligatory boss battle. Witness the real Gugu.
2. **第二幕·伪装的安全 (FALSE_SAFETY):** The apparently harmless blue Lapis appears. Approach it and discover this is the **Redactor's mask**, not an unrelated creature. Witness the real Lapis/Redactor.
3. **第三幕·微笑之后 (STRANGER_SMILES):** The polite Yellow Smiler may assist, but its presence and reflection do not agree. Witness the real Yellow Smiler.
4. **第四幕·破解关联 (TRUTH_READY):** With at least two verified tool clues, the correct case hypothesis and all three role observations, choose the case's **fictional** `REVEAL_FROST_TRUTH` resolution. The event ends without forcing combat or ending the world's survival mode.

The existing v0.10 five-act STILLNESS→WHISPERS→WITNESS→FALSE_SAFETY→RESPITE gameplay remains untouched on the base. A future full implementation must provide a **single coherent authoritative runtime**, not stack the new case over the old five-act director.

### Design boundaries and safeguards

- Wrong-order actor clicks cannot skip a narrative stage; repeated sightings of the same actor cannot unlock extra progress.
- Guessing the identity without using two genuinely different tools fails.
- Identifying the case does **not** bypass the real three-actor observations.
- Wrong fictional resolution, expired case, out-of-bounds player or nonparticipant cannot resolve it.
- ONE active anthology event in the dimension, even though this case has three members. Cooldowns, timeout and opted-out players follow common anthology rules.
- Server must verify clicked entity identity, loaded chunk, player consent, distance, dimension and the instrument held; clients can never send arbitrary "evidence" strings to claim success.
- The H-key case journal, held-item probes, original character animations, observed environmental phenomena, actor placement, failure feedback, NPC disposition, case rewards and exit screen are **future gameplay integrations**.
- No terminal/slash commands required in the intended player experience; the existing debugging commands remain *developer-only legacy paths*.
- Player's creative ownership is preserved: **never attribute original Gugu, Lapis or Yellow Smiler to the historical works**.

## Actual code delivered with this design

- `FolkloreEncounterPolicy`: `ORIGINAL_GUGU_TRIO`, 3 bespoke readings, 4th resolution, cold-only `chooseForBiome`, count this as **one case containing three actors**.
- `OriginalTrioCasePolicy`: sequential role-validated beats.
- `FolkloreCaseDirector`: joins two-tool deductive identification and all three actor observations before case resolution. Adds contextual cold-biome selection; old API is preserved and defaults to non-cold selection until a biome resolver is integrated.
- Expanded Java21 smoke tests: **4 cases × 3 tools × 2 ordered distinct tools = 24 independent probe combinations**, plus story order, skips, duplicates, wrong endings and cooldowns.
- GitHub Actions workflow now includes `OriginalTrioCasePolicy.java` in its folklore policy compilation.

**Release gate:** No stable release or distributable anthology JAR claim until players can encounter, detect, investigate and resolve at least one FULL case in a real Minecraft world without typing commands, with two-player/opt-out, long-run, blocked placement and save/reload tests.
