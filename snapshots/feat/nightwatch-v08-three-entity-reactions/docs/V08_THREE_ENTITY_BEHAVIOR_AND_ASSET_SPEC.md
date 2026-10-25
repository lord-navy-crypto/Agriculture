# Gugu-Gaga Horror v0.8 — 三实体行为与素材设计书 v0.1

**Strict scope**: write three living EntityTypes and their first-order interactions.
**NOT in this milestone**: endings, chapters, achievements, forced scary cutscenes,
Smiler gifts or betrayal, Redactor boss combat, automatic world spawning.

## 1. Character identity vs game entity IDs

| Fictional identity | Minecraft EntityType | Distinct visual forms | Alpha role |
|---|---|---|---|
| Gugu-Gaga / 迷失守望者 | `gugugaga:observer` | Calm/Angry/Corrupted poses | existing penguin |
| Redactor / 遗忘校对者 | `gugugaga:redactor` | Lapis mask + revealed true form | fears Smiler, seeks penguin |
| Yellow Smiler / 黄色微笑者 | `gugugaga:yellow_smiler` | fixed blocky grin | observes penguin, friendly persona |
| Player | `minecraft:player` | vanilla player skin | participant, NOT fourth new entity |

Lapis is **one state** of the Redactor, NOT a fourth registered monster.

## 2. Live behavior matrix and priority

**Redactor (server AI every 20 ticks, all queries only loaded entities within 24 blocks)**

1. If Yellow Smiler <=24 blocks: immediately override all other behaviors -> FLEE, don't corrupt.
2. Else if Gugu-Gaga <=8 blocks: reveal genuine tall crystal/ink form, stare, bounded navigation toward guardian.
3. Else if Gugu-Gaga <=22 blocks: watch, stop movement; Lapis mask still present.
4. Else: remain harmless-looking masked Lapis. One "[Lapis] Lapis..." near-player hint at most every 35 seconds. May remask after being alone.
5. Only if revealed and within 3 blocks of guardian, with 35-second cooldown, induce the **already existing reversible CORRUPTED mood**. No player attacks from Redactor.
6. As a manually summoned investigation prototype the entity persists. Later encounter director quotas and despawn rules are separate future work.

**Yellow Smiler (every 20 ticks)**

1. Remains stationary and uses its tiny symmetrical head motion.
2. When Gugu-Gaga is <=22 blocks, looks at guardian rather than player. The player's usual first impression remains friendly.
3. Otherwise quietly looks toward nearest nearby player. Does not target, attack, mislead with actual item gifts, scan chat or manipulate server commands.
4. Mere presence makes Redactor flee; no direct attacks or power effects necessary.
5. Yellow identity/true intent is **narrative canon**, not yet a playable boss mode.

**Gugu-Gaga (existing local AI retained)**

1. If nearby Redactor <=22 blocks and not already corrupted, briefly stop and stare at the threat instead of normal player attention.
2. If touched by revealed Redactor at bounded cadence, enters existing **reversible** CORRUPTED mood (does not override the server-owner-only hostility switch).
3. The guardian has no secret prior knowledge of Yellow Smiler. Nearby Yellow alone must not cause special automatic aggression.
4. If neither target nearby, existing player watch/approach/retreat, cold biome chance, local dialogue, witness journal, hostility opt-in, quiet opt-out operate as before.

## 3. Visual/asset specs

All graphics are **original programmatic alpha placeholders**. This repository
ships a Python 3 standard-library generator at `qa/generate_character_sprites.py`.
Gradle `processResources` runs it automatically and packages exactly:
- `assets/gugugaga/textures/entity/lapis_mask.png`: 64x64 lapis teal-blue stone figure with a softly smiling mask.
- `assets/gugugaga/textures/entity/redactor_true.png`: 64x64 dark crystal/ink archivist with pale rigid mask.
- `assets/gugugaga/textures/entity/yellow_smiler.png`: 64x64 mustard-yellow person with unusually regular black grin.

Custom client-only `RedactorModel/Renderer` and `YellowSmilerModel/Renderer`
ensure both have real shapes distinct from Gugu's canonical A placeholder. The
Redactor's form is synced via vanilla SynchedEntityData and persisted in NBT,
so its body changes without creating or deleting a separate boss entity.
No use of copied Speed voice/video, Verity texture, Lovecraft creature skins,
Demon Slayer model, Herobrine pixel assets or other games' screenshots.

## 4. Performance/security boundaries

- No automatic spawning of the new two entities in v0.8.
- No getChunk, forceChunk, setBlock, explosions or remote HTTP in relation AI.
- Scans only existing loaded entities every 20 ticks (instead of every tick).
- No newly added player damage; existing owner-only Gugu anger remains
  independent and OFF by default.
- No camera hijacking, forced spectator mode or magical operator escalation.
- All references to Yellow Smiler endgame E are STORY, not gameplay code.

## 5. Manual test protocol

1. Create a disposable MC 1.21.1 NeoForge 21.1.259 Java21 world.
2. `/summon gugugaga:observer ~ ~ ~` — confirm penguin model renders normally.
3. `/summon gugugaga:redactor ~ ~ ~` at 15+ blocks from guardian:
   confirm short blue Lapis mask; near Gugu it should switch to taller form.
4. Approach Redactor to within 3 blocks of Gugu and wait about 35 seconds;
   inspect guardian's temporary corrupted animation.
5. `/summon gugugaga:yellow_smiler ~ ~ ~` within 24 blocks of Redactor;
   it should take priority and flee rather than keep corrupting. Yellow should
   stand still and appear to watch Gugu.
6. Move Yellow away, confirm Redactor can again look for the guardian.
7. Remove all new entities, confirm Gugu continues its prior four-phase behavior,
   dialogue, journal and ordinary gameplay.
8. Save/reload and check Redactor's mask state and no cross-side class-loading
   error; repeat on dedicated server with two players.
9. Compare server MSPT with 0/1/10 of each new NPC in loaded chunks before
   claiming that this feature is efficient in multiplayer.

**Release status**: CI compilation is necessary, but the above practical
render/save/dedicated-server checks remain required before main merge.
