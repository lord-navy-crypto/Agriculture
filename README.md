> **ONE HEALTH vNext — 真实动物／媒介／人类关系图（2026-10-24）**  
> Built on the prior real microbe and human–animal evidence branches: **real West Nile virus (NCBI 11082)** joins rabies and avian-origin influenza A. The existing Minecraft **H key → One Health → Details** now includes an annotated ecological network with CDC/WHO-backed bird–mosquito relations, incidental human/horse health context and explicit scope limitations.  
> Every graph edge is scientific source metadata, **not automatic infection, an invented spread rate or a prediction**. No phantom human→mosquito transmission. The original main/old horror/folklore code is not overwritten.  
> [One Health vector-host graph plan and authoritative references](docs/ONE_HEALTH_REAL_VECTOR_HOST_NETWORK_2026-10-24.md).

---

> **ONE HEALTH EXPERIMENT — 真正的动物病毒、人类影响与传播证据图鉴 (2026-10-24)**  
> Continuing the **real natural microbe ecology** foundation, this stacked Draft adds real *Lyssavirus rabies* (NCBI 11292) and animal-origin *Influenza A virus* (NCBI 11320, strictly limited to the avian/zoonotic subset in WHO's evidence). Native Minecraft **H-key → One Health** GUI has 2 detailed bilingual-accessible species pages, documented animal/human impacts, high-level zoonotic interface categories and scientific uncertainties.  
> **Contact is not infection**: no fabricated transmission rates, player/animal contagious state, automatic disease spread, human symptoms diagnosis, mutation or medical action. Host and transmission categories are educational background facts with links to CDC/WHO.  
> [One Health development notes and real source references](docs/ONE_HEALTH_ANIMAL_HUMAN_TRANSMISSION_EVIDENCE_2026-10-24.md). In-game visual/graphical acceptance remains subject to the PR's full NeoForge client test. Stable main and unrelated frozen branches untouched.

---

> **NEW NATURAL MICROBIOLOGY BRANCH — 真实细菌与病毒生态模拟 (2026-10-24)**  
> User-directed correction: **no fictional pathogens, no invented biological reproduction/transmission rates and NO mandatory medical devices or treatment systems**. This new experimental branch builds an NCBI-sourced ecology of genuinely existing species: *Escherichia coli* (taxid 562), *Bacillus subtilis* (1423), *Lactiplantibacillus plantarum* (1590) and the natural bacterial virus *Tequatrovirus T4* (10665). The T4–E. coli relation is documented, but bacterial strain-level susceptibility and site-specific occurrence are NOT assumed.  
> Initial delivery: pure Java 21 source-backed real-species catalog and evidence-only ecological relationships + automated tests. **It is not yet a native Minecraft in-world microorganism visualization or population simulator.** The old frozen historical horror/folklore branches and stable `main` are untouched.  
> [Detailed natural microbial ecology plan](docs/REAL_NATURAL_MICROBE_ECOLOGY_2026-10-24.md).

---

> **v0.10 restart branch / v0.10 恢复开发分支（2026-10-19）**  
> This branch was created following the user's explicit request to complete the older v0.10 gameplay loop. The historical [freeze archive](docs/PROJECT_FREEZE_2026-10-19.md), original v0.10 branch, v0.9 baseline, newer experimental branches and `main` are preserved. Prior v0.10 CI was green, but a green CI is **not** proof of real client/multiplayer gameplay. v0.10.0-alpha.3 is a **UI-first experimental test candidate**, not a stable release. Consult [v0.10 acceptance plan](docs/V010_COMPLETION_PLAYTEST.md).
>
> ---
>
## v0.10 playable survival horror — NO endings, NO extra mandatory mods

The earlier v0.2–v0.9 entities and canon are preserved. v0.10 adds
a replayable five-act NIGHT horror game loop: **Stillness → Whispers →
Witness → False Safety → Respite**. A player investigates actual visible
Gugu-Gaga and then the Redactor / Smiler to unlock each stage.
This is repeatable gameplay, **not** the five planned story endings.

**NO COMMANDS REQUIRED FOR NORMAL PLAY.** Install the mod and press **H**
(the new, remappable Nightwatch key). Click **Join / Resume** to opt in,
**Pause / Leave** to opt out, or use the panel for chapter status, quiet mode,
the private witness journal, and optional world-wide server controls
(operator-only, server permission checked). In WITNESS, approach a real
Gugu-Gaga and **right-click** it; then right-click the real Redactor/Lapis
or Yellow Smiler in FALSE_SAFETY. The five acts then continue automatically.
World gameplay is ON by default for fresh installs, but is inactive for
nonparticipants. Existing configs which disabled gameplay can be re-enabled
through the H panel's operator-only World On button. Slash commands are
legacy diagnostics only; the GUI sends validated custom packets, NOT commands. It runs only at night
in Survival Overworld, keeps the existing minimum 8-minute **natural** penguin
sighting cooldown, and independently offers a temporary Gugu in WITNESS
(8-second initial attempt, then 20-second rate-limited retries). Blocked
Redactor arrivals are retried in FALSE_SAFETY. Existing actor/collision
caps and loaded-chunk-only checks still apply. The Redactor/Yellow
remain bounded, and scripted Redactor/Yellow guests expire in six minutes;
scripted Gugu sightings expire after 90 seconds. No world edits, forced loading, jump scare flashes, boss endings,
external network dependency or player admin permission changes.

**Animation/dependency guidance**: See the verified official NeoForge
1.21.1 versions in [animation and horror dependency guide]
(docs/NEOFORGE_1211_ANIMATION_AND_HORROR_DEPENDENCIES.md).
Current project uses NATIVE Java ModelPart renderers and requires **zero
additional third-party animation mods**. GeckoLib 4.9.3 NeoForge 1.21.1
is planned for a later, explicitly tested keyframe animation migration,
NOT a working integration in v0.10. Sound Physics, AmbientSounds+CreativeCore,
Embeddium and Iris are optional client enhancements.

The README and docs continue to preserve A/B/C/D/E endings **as FUTURE**
long-term worldbuilding and never claim those endings are implemented.

---

## v0.9 — Distinct corruption, Lapis ambush and Yellow helping/true form

New, EXPERIMENTAL (no story quests or ending triggers):
- Gugu-Gaga's reversible corruption visibly progresses through fractured,
  possessed and final dark/blackened appearance. Admin `/gugugaga blacken`
  previews the final phase; default hostile attacks are still OFF.
- Lapis is the Redactor's false-safe mask. It can reveal near a player or
  Gugu-Gaga; a revealed Redactor can give tiny warning-based melee strikes
  **only** after an admin explicitly sets `/gugugaga mimic hostility on`.
  An encountered Yellow Smiler ALWAYS makes it flee first.
- The masked Yellow Smiler now gives **two torches** on voluntary player
  right-click with a **three-minute per-player cooldown**, but **stops
  helping** when revealed. It unveils its distinct darker original texture
  when hit by a player (or admin uses `/gugugaga smiler reveal`); it cannot
  attack or steal anything in this alpha.
- The new two original PNG files `observer_blackened.png` and
  `yellow_smiler_true.png` are generated during CI/Gradle. No borrowed art.
- Admin test controls: `/gugugaga mimic status|reveal|disguise`,
  `/gugugaga mimic hostility on|off`,
  `/gugugaga smiler reveal|mask`, `/gugugaga blacken`.
- Details and manual acceptance: [v0.9 modes spec](docs/V09_THREE_UNIQUE_MODES_SPEC.md).

v0.9 has not yet passed actual singleplayer, dedicated-server and modded-client
visual QA; the five full endings are *long-term plans*, not implemented.

---

## v0.8: Three Entities (Alpha)

The canonical three-reversal story, Lapis/Redactor disguise, Yellow Smiler, five planned endings (including creator ending), and development decisions are permanently archived in [Canon & Long-Term Archive](docs/GUGUGAGA_CANON_AND_LONG_TERM_ARCHIVE.md).
Development scope now: three registered entity types (observer, redactor, yellow_smiler), and their in-world local reactions. Lapis is a mask state of the Redactor, not a separate fourth mob. The user remains the vanilla player.
The Redactor flees from a Yellow Smiler, otherwise watches and can temporarily corrupt Gugu-Gaga. Yellow watches Gugu and does not attack; Gugu notices the Redactor. No quests, ending triggers, world edits, copied skins or automatic spawns for the two new mobs.
Original placeholder PNG assets are generated by qa/generate_character_sprites.py and bundled with the mod build. See [v0.8 Behavior and Asset Specification](docs/V08_THREE_ENTITY_BEHAVIOR_AND_ASSET_SPEC.md) for tests. Real client and multiplayer QA are still needed.

---
# Gugu Gaga: Nightwatch

An experimental **Minecraft 1.21.1 / NeoForge** psychological-horror mod, focused on a single penguin-like observer, gradual suspense, and fair survival gameplay.

## Status
**v0.7 pre-alpha / the Lost Watcher and the Redactor under development.** The code is being built and verified through pull requests; do not treat it as a released mod.

## Gameplay direction
- The entity initially **observes** instead of attacking.
- A server-side state machine manages **dormant → observe → approach → retreat**.
- A bounded **opt-in Nightwatch encounter director** controls rare, night-only appearance in already-loaded chunks; it is OFF by default and never modifies blocks or forces chunks.
- Player control: operator-only auto-spawn switch, a configurable 8–60-minute cooldown, and per-player session opt-out using `/gugugaga quiet on`. Audio/visual accessibility remains planned.
- Single-player first, then dedicated-server and multiplayer QA.

## Optional local Ollama AI (SWARM-MOBS inspired)
The deterministic observer runs fully offline. The optional server-side local Ollama shadow adviser is **disabled by default** and only makes requests after explicit commands. Use `/gugugaga ai detect` to list local models and `/gugugaga ai shadow` for a one-off pacing recommendation. It never controls per-tick AI or sends coordinates. See [docs/OLLAMA_OPTIONAL.md](docs/OLLAMA_OPTIONAL.md).

## Build
Java 21, Minecraft 1.21.1, NeoForge 21.1.x. See `docs/SETUP.md` after the first source commit.

## Assets and licensing
Code authored for this project is intended to be MIT-licensed (see LICENSE when added). **The words “inspired by” or “fan project” and the MIT code license do not grant permission to reuse third-party images, voices, or character designs.** Online meme images are references only; do not redistribute them. The prototype ships its own placeholder artwork. Asset provenance and publication clearance are tracked in `docs/ASSET_PROVENANCE.md`.

Unofficial fan project; not endorsed by Arknights: Endfield, Hypergryph, Gryphline, BanG Dream!, or relevant creators.

## Optional v0.2 encounters (disabled by default)
```text
/gugugaga encounters status
/gugugaga encounters on
/gugugaga encounters off
/gugugaga quiet on
/gugugaga quiet off
/gugugaga quiet status
```
Operators may enable automatic, nonviolent nighttime encounters. Only survival players in the Overworld are eligible, at most one observer may be near any player and eight may exist across all loaded dimensions. A failed placement attempt consumes cooldown, and an unloaded chunk is never requested. Use a disposable world while this is still pre-alpha. The previously implemented local Ollama adviser remains optional, OFF and advice-only.

## v0.3 unsettling observer (experimental)
- Its face and wings now animate with **observe / approach / retreat** state: restrained tracking, not flashing or screen hijacking.
- Extremely sparse and quiet spatial audio, using standard Minecraft sounds (no copied meme recordings); it can be switched off via `encounters.haunting_audio=false`.
- Retreats leave at most two ambient particles if enabled; can be switched off via `encounters.haunting_particles=false`.
- Nightwatch director sightings last a maximum of 90 active seconds and disappear cleanly; entities summoned with `/summon` remain persistent.
- The player `/gugugaga quiet on` opt-out now also prevents **existing observers from targeting that player**, not just new automatic encounters.
- Repeated pathfinding is throttled (at most once per 30 ticks when pursuing/retreating), reducing server overhead.
- Nightwatch still performs **no attacks, block editing, chunk forcing or global camera effects**. The opt-in director remains OFF by default. Ollama remains optional and shadow-only.
- This is **not yet released**: dedicated-server gameplay acceptance remains necessary even after a green CI build.

### Live atmosphere controls (operators)
```text
/gugugaga atmosphere status
/gugugaga atmosphere on
/gugugaga atmosphere off
```
These controls mute/unmute custom sound and particles without restarting. Players can always adjust the existing Minecraft Ambient sound slider. `/gugugaga quiet on` stops targeting that player, including observers that already exist.

## v0.4 The Witness Journal (experimental)
The observer now leaves **memories rather than altered terrain**. In a survival test world, earn three independent clues by meeting its gaze, noticing its quiet approach, and witnessing its withdrawal. Your own private in-game journal keeps a tiny 3-bit progress record and unlocks a final line when all evidence is found. No player locations, inventories, chat or real-world data are recorded.

```text
/gugugaga journal
/gugugaga journal on
/gugugaga journal off
/gugugaga journal clear
```

Recording and optional gentle one-time clue notifications can be disabled server-side in `witness_journal.enabled` / `witness_journal.hint_messages`. Each player can opt out or clear their journal without operator permissions. `/gugugaga quiet on` continues to stop observers targeting that player. Automatic night encounters remain OFF by default, and Ollama remains optional. This is **v0.4 alpha, not a production release**; verify actual client, dedicated server and player NBT save behavior before treating it as stable.

## v0.5 The Gugu-Gaga Haunting Director (experimental)
The mod now has **two layers of AI**, inspired by the general design principle of dramatic horror directors: the global director decides WHEN and WHAT KIND of encounter to offer, while individual Gugu-Gaga observers handle watching, hiding, approaching and retreating. **Six** bounded outcomes are available: deliberate silence, distant chime, false footsteps, fading echo, two-particle afterimage, or an actual loaded-chunk observer sighting. The director remembers only the last type, a bounded number of tries and a tension stage for the player's current online session; it never records location history.

Night encounters remain **OFF by default**, never closer together than the existing 8-minute safety floor and usually at least 10 minutes. They are still Overworld-night-only, survival-only, opt-out respecting, with at most one observer near each player and eight globally. The new non-mob events don't load chunks, modify blocks, deal damage or use HTTP. Ollama remains optional and advice-only.

Operator controls after explicitly enabling encounters:
```text
/gugugaga encounters on
/gugugaga director status
/gugugaga director gentle
/gugugaga director balanced
/gugugaga director eerie
/gugugaga encounters off
```

These three intensity choices adjust the **mix** of events, not event cooldown, mob caps or volume. `encounters.varied_events=false` reverts to the original observer-only encounter choice, without removing v0.4 journal. Any player can use `/gugugaga quiet on`; operators can mute all added ambience with `/gugugaga atmosphere off`. Test in a disposable game world: compiling does not establish every mechanic works in multiplayer.

## v0.7 — The Lost Watcher vs The Redactor
The odd-looking penguin is NOT automatically the villain. It has three reversible moods: CALM, ANGRY, and CORRUPTED. The real story villain is a still-unseen narrator called **the Redactor**, who rewrites the meaning of the clues. The Redactor's in-world mob/boss is **not implemented**.

Optional server-hostile test mode (OFF by default):
```text
/gugugaga hostility on
/gugugaga temper angry
/gugugaga temper corrupted
/gugugaga temper calm
/gugugaga hostility off
```

Only server operators can toggle hostility or change nearby entity mood. In an angry/corrupted state with hostility enabled, the bird slowly pursues eligible survival players, gives at least a 30-tick warning before one modest close-range hit, and waits at least 75 ticks between attempts. It never attacks opted-out `/gugugaga quiet on` players, breaks blocks, forces chunks or teleports. Anger and corruption always time out into CALM.

Voluntary investigation for all players:
```text
/gugugaga story
/gugugaga trust guardian
/gugugaga trust redactor
/gugugaga trust neutral
/gugugaga journal
/gugugaga reassure
```

Three genuine witness clues unlock the ability to reassure a nearby angry/corrupted bird, and canonical `/gugugaga talk` replies react to a player's current investigation position. Choices may be changed freely. The original odd penguin material is retained; slow gaze and uneven wing/head postures convey the uncanny without copied, flashing, or excessively loud effects. See `docs/V07_DUAL_NATURE_STORY_AND_VISUALS.md` for the four-chapter plan, shipped/unshipped boundary and visual design rules.
