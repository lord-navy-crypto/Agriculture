# v0.10.0-alpha.2 — five-act completion / actual gameplay acceptance

**Scope:** a narrowly scoped repair of the original v0.10 native-model, opt-in
horror loop. The 2026-10-19 historic freeze files, original v0.9 baseline,
newer v0.11–v0.21.3 drafts, A–E ending concepts and `main` are untouched.
**This is a test candidate, not a certified stable release.**

## What was fixed

1. The original Nightwatch ambient director still enforces its **8-minute minimum**
   sighting cooldown; that meant an opted-in player could reach WITNESS without
   meeting a single Gugu before dawn. In v0.10 playable mode, WITNESS now
   independently attempts ONE **temporary** observer after 8 eligible stage
   seconds, with one retry every 20 eligible stage seconds if the earlier
   attempt found no nearby actor or no valid spawn placement.
2. A blocked Redactor placement previously could strand FALSE_SAFETY forever.
   That act now uses the same bounded, retryable loaded-only placement rule.
3. Scripted observers expire under the existing 90-second ambient lifetime,
   and scripted Redactor/Yellow remain subject to their existing 6-minute
   lifetime. Manual `/summon` entities stay persistent.
4. While the playable mode is enabled, Gugu, Redactor and Yellow only
   voluntarily focus/attack joined participants. `play leave` excludes a
   player from subsequent AI targeting even if already-spawned actors remain.

No extra dependency: still Minecraft **1.21.1 / NeoForge 21.1.259 / Java 21**,
native renderers (not GeckoLib in this v0.10 branch). No world writes,
chunk tickets, teleporting, automatic player enrollment or forced endings.

## Install and enable

Install a JAR produced from this branch's passing CI build in a **fresh,
backed-up** Minecraft 1.21.1 NeoForge 21.1.259 profile, with Java 21.
Do not mix this older native-renderer branch with any of the newer
GeckoLib-required v0.11–v0.21.x builds under the same `mods/` folder.

In a disposable Survival **Overworld** world at night:

- Operator: `/gugugaga play server on` (must be explicitly enabled).
- Player: `/gugugaga play join`.
- `/gugugaga play status` checks join and act.
- In WITNESS, locate the visible Gugu within 12 blocks and run
  `/gugugaga investigate`.
- In FALSE_SAFETY, approach the real Redactor/Lapis or Yellow within
  12 blocks, with direct line of sight, and run `/gugugaga investigate`.
- RESPITE automatically returns to STILLNESS after 70 **eligible**
  stage seconds. All earlier ambience is quiet and below volume 0.18.
- `/gugugaga play leave` opts out; `/gugugaga play server off` stops
  automatic encounters for the world.

## Honest release gates (not satisfied merely by compilation)

| Gate | Required observation |
| --- | --- |
| Pure policy | Java state machine smoke tests must pass |
| Source boundaries | Python QA verifies actor rate-limit and per-player targeting |
| NeoForge package | Gradle `build`, metadata, and original sprites must succeed |
| Client in-world | Create fresh Survival world, witness three *rendered* actors |
| Gameplay | All five acts complete twice without debug commands or /summon |
| Difficult terrain | Cave, forest and shore placement eventually offers reachable actor |
| Consent | Two players, only one joined; the other is not chased or attacked |
| Leave/rejoin | Opted-out user immediately stops being a valid AI target |
| Server | Dedicated 1.21.1 NeoForge server boots, two players can interact |
| Persistence | Quit/relog and save/load expire scripted guests correctly |
| Compatibility | Clean profile and intended modpack client launch with no errors |

When a gate is untested, record **UNTESTED**, not PASS. Source guards
and compilation are not substitutes for an actual graphical client,
dedicated server, or multi-player session. Freeze archive remains a
historical snapshot rather than evidence of post-restart acceptance.
