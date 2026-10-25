> **CORRECTION — THIS IS A HISTORICAL v0.10 BRANCH-ONLY SNAPSHOT.** The full GitHub repo contains newer v0.11–v0.21.3 draft branches. The authoritative **global repository freeze checkpoint** is now [docs/PROJECT_FREEZE_2026-10-19.md on PR #29's latest branch](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/blob/fix/horror-combat-resilience/docs/PROJECT_FREEZE_2026-10-19.md). In particular, v0.9 is NOT the latest repo test milestone; v0.21.3's in-world gameplay gate was failing and GeckoLib has become a required dependency on the newer chain. The statements below describe ONLY the status of this earlier v0.10 branch.

# PROJECT FREEZE / 项目冻结档案
**Date:** 2026-10-19 (Asia/Shanghai).  
**State:** **FROZEN BY USER REQUEST**. No new implementation, refactors, releases, merges, automatic tasks, or expansion until the user explicitly resumes the project.  
**Repository:** https://github.com/lord-navy-crypto/Gugu-Gaga-horror  
**Archive scope:** Gugu-Gaga Horror Mode (Minecraft 1.21.1, NeoForge 21.1.259, Java 21).

## 冻结决议 / What this means

- The horror project and all planned narrative endings are **paused**. Preserve all prior code, art-generation scripts, plans and draft PRs in place, without deleting/rebasing/merging.
- Do **not** confuse a passed CI build with live client or multiplayer acceptance.
- Do **not** treat the unfinished v0.10 horror-loop branch as a stable release or distribute it as an installable new version.
- No Minecraft server permissions, external disk files, browser or non-Minecraft worlds may be affected by fictional horror mechanics.
- This is a **development freeze**, NOT GitHub's repository-wide archive/read-only switch. Everything is left available for review and a controlled future restart.

## 已保存版本与精确边界

| Version | Branch / GitHub PR | Freeze-day status |
|---|---|---|
| v0.2 | PR #1, base observer | Basic observer, original prototype assets, opt-in night AI, optional local Ollama |
| v0.3 | PR #5 | Three-level sparse atmosphere cues, visual head/wing motion, temporary night encounters |
| v0.4 | PR #6 | Per-player witness journal (Chinese and English) |
| v0.5 | PR #7 | Six-event suspense director with deliberate silence and tension |
| v0.6 | PR #8 | Chinese/English offline dialogue and optional local Ollama; colder-biome sightings |
| v0.7 | PR #9 | Reversible angry/corrupted Gugu, optional close attacks, first faction/Redactor story |
| v0.8 | PR #10 | Three actual registered entity types, original art, Lapis-mask Redactor and Yellow Smiler interactions |
| **v0.9.0-alpha.1** | **PR #11** (`feat/nightwatch-v09-moods-mimic-smiler`) | **Latest previously confirmed green GitHub Actions + distributable test JAR**; three different character modes. NOT real-game-accepted |
| **v0.10 WIP** | **`feat/nightwatch-v010-playable-horror-loop`**, branched from v0.9 | **INCOMPLETE, NOT BUILD-VERIFIED**; partial gameplay loop source saved on GitHub. Frozen; not for installation |

All previously opened feature pull requests remain **DRAFT** and **unmerged**. The default `main` branch is **not** the combined v0.9 mod; most recent functional state is in stacked feature branches. Do not claim main is stable/comprehensive, and do not mass-merge the PR chain without new review.

## v0.10 — precisely what had been started before freezing

Already committed to this branch before the freeze:
- `ai/HauntGameplayPolicy.java`: pure, reusable suspense loop policy with stages STILLNESS, WHISPERS, WITNESS, FALSE_SAFETY, RESPITE; no story ending mechanics.
- `ai/HauntGameplayRuntime.java`: optional server-tick loop with nearby actual entity evidence, quiet cues and bounded, player-triggered future encounters.
- `command/GuguPlayCommands.java`: `/gugugaga play server on|off`, `play join|leave|status`, and `/gugugaga investigate`.
- `config/NightwatchConfig.java`: default-off `PLAYABLE_LOOP_ENABLED` switch.
- `GuguGagaMod.java`: registration of commands and gameplay ticks.
- `ai/NightwatchDirector.java`: when playable mode is enabled, prevent ordinary automatic sightings for players who have not voluntarily joined.

**None of the v0.10 work received a full CI test/JAR verification by freeze time.** The gameplay experience, mechanics and user consent need compilation, bug fixes and real field QA before any future v0.10 use. Its partial state is deliberately preserved, not backported to v0.9, and should not be installed as a JAR.

## 三个角色与未完成内容

1. **Gugu-Gaga**: original A-design-target penguin (current art is acknowledged prototype), watches players, prefers cold biomes, private offline/optional local dialogue, anger/corruption/blackening (reversible). Angry attacks separately opt-in.
2. **Lapis / Redactor**: one character + one EntityType with friendly blue disguise and taller true form; prioritizes Gugu corruption and fleeing from Yellow. Limited hostile hits separately disabled by default.
3. **Yellow Smiler**: friendly yellow figure, voluntary two-torch help at rate-limited intervals, reveals a more sinister appearance, causes Redactor to flee. Final boss/actual betrayal **not implemented**.
4. **Player**: vanilla participant, not a fourth custom entity.

FUTURE/NOT SHIPPED: mature multi-stage horror acceptance, original final A visual fidelity, more original recordings/animations, evidence structures, automatic environmental biome spawns of the two new NPCs, cinematic revelations, the proper final Redactor/Yellow boss encounters, four main/secret endings + fifth beyond-creator ending, desert/warm-ocean 32–40-block high sanctuary, safe visual-only command-block portal homage.

**Historical canon and all five endings** remain fully preserved in:
- [Canon + Long-Term Archive](GUGUGAGA_CANON_AND_LONG_TERM_ARCHIVE.md)
- [Three-entity Behavior and Original Assets](V08_THREE_ENTITY_BEHAVIOR_AND_ASSET_SPEC.md)
- [Three Character Advanced Modes](V09_THREE_UNIQUE_MODES_SPEC.md)
- [Historical Horror Feature Masterplan](HORROR_FEATURE_MASTERPLAN.md)

## Dependencies — do not install any new libraries while frozen

- **Required runtime** for the last confirmed experimental test artifact: **Minecraft 1.21.1, NeoForge 21.1.259, Java 21.**
- Current `build.gradle` uses NeoForge's ModDevGradle and custom native entity models. **GeckoLib has NOT been integrated as a required dependency** in v0.9 or untested v0.10. Avoid claiming it is mandatory until a documented version-compatible implementation is built.
- Sound Physics, AmbientSounds, Entity Culling, FerriteCore and similar performance/ambience mods are only **optional future compatibility/testing candidates**; no new dependency requirements have been added.
- Optional local Ollama integration is not needed to start the test mod and is default off.

## 恢复开发时的严格顺序 (future, not an automatic task)

1. Ask the user to explicitly **resume**; do not auto-start work.
2. Reinspect the PR stack, compare frozen v0.9 vs v0.10 branch, and check correct NeoForge 1.21.1 APIs.
3. Preserve the **v0.9 CI-verified alpha as a baseline** and avoid mixing incomplete v0.10 code into it prematurely.
4. Only if user wants to continue v0.10, run full CI, pure Java state-machine tests, manual NeoForge client, NBT/relog, dedicated server, two-player tests, player opt-in and manual opt-out. Fix defects on the experimental branch.
5. Do not add GeckoLib/other dependencies unless version verified for **NeoForge 1.21.1** and compatibility tests are green; never mistake Fabric/Forge build for NeoForge.
6. Only merge PRs after code review, tests and explicit user approval. No destructive cleanup.
7. Resume **playable multi-stage horror** ahead of story endings, as the user prioritized before freezing.

**Final freeze summary:** Code and story safely stored in GitHub draft branches. Last build-confirmed test release is v0.9 alpha. The newer v0.10 is a saved, unfinished work-in-progress. All further work intentionally stopped.
