# GUGU-GAGA HORROR — GLOBAL PROJECT FREEZE (2026-10-19)
**Status: PAUSED at user's explicit request. Preserve code, do not develop, merge or release.**

This file is the **authoritative latest-branch freeze checkpoint** and supersedes earlier incomplete v0.10-only freeze assessments. Minecraft target remains **1.21.1 / Java 21**. It is a *development pause*, not a Github repository archived/read-only toggle. This document does not stop any separate pre-existing cloud-work sessions or external agents; those must be paused at their own controls.

## 1. Exact latest GitHub status (verified at freeze)

- **Latest observed development branch:** `fix/horror-combat-resilience` at commit `cee9af471343a806832f3a8bc23ca021a18d4a3b`, GitHub [draft PR #29](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/pull/29). Version in `gradle.properties`: **0.21.3-rc.1**.
- **Latest CI run on that exact pre-freeze code commit:** [39140542336](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/actions/runs/39140542336) = **OVERALL FAILURE**, because the **"REAL in-world gameplay — real entities, chat HELP, V, and five-act cycle"** job failed at **"Enter real survival world and exercise in-game controls"**.
- The SAME run had successful **Gradle build**, actual dedicated server launch (NeoForge 21.1.150/200/259 combinations with GeckoLib 4.x), a clean packaged-JAR server install, graphical client initialization, combat resilience tests and API compatibility checks. Those passes **do not prove the real-world interaction experience works**.
- [PR #28](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/pull/28), branch `test/packaged-jar-consumer-startup`, previously passed its workflow [39130253531](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/actions/runs/39130253531), including installing the *compiled JAR* into a clean NeoForge server. This is a narrower compatibility gate, not a verified multiplayer/client gameplay release.
- User previously reported that v0.21.x did not work in their actual client. Do **not** assume that issue was definitively fixed by GitHub CI without their Mac Minecraft `logs/latest.log` and a real gameplay check.
- Every examined feature PR **#1, #5–#29** remains a **draft open unmerged PR** at this checkpoint, including the integrated [v0.19 RC PR #21](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/pull/21) targeting main. Nothing has been merged into `main` as part of this freeze.

## 2. Versions and why an older freeze note said v0.9

This project's revisions form multiple stacked branches:
- v0.2–v0.9: observer, witness notes, six-event director, private speech, penguin corruption, Lapis/Redactor disguises, Yellow Smiler and original skins. These are early verified alpha milestones; v0.9's previous green CI is **NOT the latest repo CI**.
- v0.10 PR #12: voluntary repeatable five-act suspense loop and investigation; its branch contains earlier WIP code. Older [v0.10-only freeze note](https://github.com/lord-navy-crypto/Gugu-Gaga-horror/blob/feat/nightwatch-v010-playable-horror-loop/docs/PROJECT_FREEZE_2026-10-19.md) is a **historical local checkpoint**, not a full-repository status document.
- v0.11 PR #13: GeckoLib animation brought in as a **REAL REQUIRED runtime dependency**, superseding earlier GeckoLib-not-integrated statements.
- v0.12–v0.18 PRs #14–#20: chases, cinematic atmosphere, clue detection, stealth and player choices.
- v0.19 RC PR #21: experimental integrated candidate from main, still draft.
- v0.20–0.20.1 PRs #22–#23: chat HELP, V listen, world interaction.
- v0.21–v0.21.2 PRs #24–#28: NeoForge/GeckoLib version range, real dedicated server boots, MOD event-bus client renderer fix, graphical client tests and clean installed-JAR test.
- v0.21.3 PR #29: H-key in-game UI, automatic Nightwatch enrollment, three-entity interaction updates and survival weapon resilience — **real in-world gameplay acceptance still FAILING**.

**All of the above are preserved, unmerged, draft history. No changes to gameplay/source or automatic work continue after freeze request.**

## 3. Dependencies — important correction for the most recent branch

- For **the latest 0.21.3-rc.1 branch**, `gradle.properties` declares `neo_version=21.1.150`, `neo_min_version=21.1.150`, `geckolib_version=4.8.2`, `geckolib_min_version=4.8.2`, Minecraft 1.21.1 and Java 21. GeckoLib NeoForge 4.x **is required** for that branch. The build/tests also exercised newer 4.9.3 with newer loader versions.
- Earlier v0.9 used native animations **without GeckoLib**. Do not mix its dependencies or JAR with v0.21.3, and do not blindly install dependencies into the user's current game instance while project is frozen.
- Other mods such as Sound Physics, AmbientSounds, Entity Culling, FerriteCore or Embeddium are *optional compatibility candidates*, not universally mandatory dependencies in this archive. The exact compatible combinations need testing.
- The latest runtime specification should be verified against the exact JAR's `META-INF/neoforge.mods.toml` before any future installation. **No release is certified by this freeze action.**

## 4. Canon preserved for future resumption

Three custom creature identities plus vanilla player:
1. **Gugu-Gaga**: strange cold-biome penguin/guardian, reversible corrupt/blackened states, looking, following, dialogue, opt-in combat.
2. **Redactor**, disguised as original blue **Lapis** form, changing to true appearance, focused on Gugu-Gaga, flees Yellow.
3. **Yellow Smiler**: apparently friendly, original square yellow smiling NPC, helps with supplies and misleads, eventually true antagonist, while actual late-stage boss and endings remain planned features.
4. The player has no new custom fourth EntityType.

Five **unimplemented as final playable chapters** story endings are preserved: **A Perfect Smile, B False Victory, C Last Witness, D True Lapis hot-biome sanctuary, E Beyond the Creator** simulated command-block/portal fourth-wall ending. No actual OS/files/other-game access or real permission escalation.

Main historical planning sources:
- `docs/GUGUGAGA_CANON_AND_LONG_TERM_ARCHIVE.md`
- `docs/HORROR_FEATURE_MASTERPLAN.md`
- `docs/V08_THREE_ENTITY_BEHAVIOR_AND_ASSET_SPEC.md`
- `docs/V09_THREE_UNIQUE_MODES_SPEC.md`

## 5. Freeze rules and next steps (ONLY if user explicitly resumes)

- **Stop now**: no new features, bug fixes, new downloadable JARs, dependency installation, merges, branch rebases, artifact repackaging, scheduled follow-ups or timeline commitments.
- Keep feature PRs draft, code and long-term story documents untouched; `main` unmerged.
- If user later requests resume: first choose which branch is authoritative, review PR #29's in-world acceptance failure and the user's actual NeoForge/Mac logs, then reproduce survival-world interactions; only then stabilize a playable release. Real-world functional verification outweighs compile-only passes.
- Avoid breaking the cumulative content by merging unrelated branches out of order. Resolve the draft PR stack deliberately.
- All cutscenes and supernatural 'creator powers' remain harmless Minecraft in-world fiction only.
- **Separate ongoing cloud agent sessions are outside this conversation's cancellation control**. If any separate workspaces are still working on this repository, they must be explicitly paused by the user to guarantee a repository-wide operational freeze.

### Frozen status at recording time
**Latest observed code**: draft v0.21.3 PR #29, recent CI overall **failed in-world gameplay job**, no stable release assertion. **Best tested compatibility reference**: green PR #28 clean server/JAR workflow, but the user's actual client needs further verification. All code retained, work paused.
