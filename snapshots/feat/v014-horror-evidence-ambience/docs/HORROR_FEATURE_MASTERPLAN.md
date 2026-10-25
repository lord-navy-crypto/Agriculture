> Current authoritative continuation is [the Canon & Long-Term Archive](GUGUGAGA_CANON_AND_LONG_TERM_ARCHIVE.md) and [v0.8 Behavior/Asset Spec](V08_THREE_ENTITY_BEHAVIOR_AND_ASSET_SPEC.md). This document is an earlier v0.5 planning snapshot; do not treat proposed features as shipped.

# Gugu-Gaga Horror — master feature plan (A-shape canonical, 2026-10-17)

This is a long-term **scope register**, not a list of shipped code.
All new artwork/audio must be original or licensed; the primary Gugu-Gaga
entity must preserve the previously chosen **A reference identity**,
not substitute a penguin or reproduce third-party horror entities.

## Design pillars

1. FEEL SEEN: suspense through observation, concealment, misdirection,
   environmental sound, brief sightings and genuine silences.
2. DIRECTOR + CREATURE: a global director controls pacing while each
   observer's own locally sensed AI decides its movement. It must not
   have omnipotent knowledge, force chunks, damage builds or fake OS issues.
3. REMEMBER WITHOUT SPYING: track limited anonymous game-state,
   never real identity, location history, voice, microphone or chat.
4. ASK, NEVER FORCE: automatic events default OFF, manual /summon works,
   player quiet opt-out, accessible separate sound/particles settings.
5. PLAYABLE IN SURVIVAL: no world corruption, aggressive griefing,
   involuntary item deletion, external network dependency or deadlocks.
6. REAL PERFORMANCE: strict caps, timers, spatial hash, per-worker caches,
   loaded-only event checks; real dedicated-server profiling before release.

## Confirmed implemented (as of experimental v0.5 branch)

| Feature | Status | Notes |
|---|---|---|
| A-identity Observer placeholder | alpha | Current model is an ORIGINAL placeholder; final exact A art not yet faithful |
| Observer four-phase local AI | alpha v0.2 | Dormant / Observe / Approach / Retreat |
| Optional night appearance | alpha v0.2 | Explicit administrator opt-in, loaded chunks only |
| Local-only Ollama adviser | alpha v0.2 | Shadow suggestions only, disabled by default |
| Player quiet on/off/status | alpha v0.2 | Session-only, affects existing observer targeting |
| Visual head, eye and wing poses | alpha v0.3 | Native code, needs in-game acceptance |
| Sparse directional atmospheric audio | alpha v0.3 | Low-volume vanilla cues; no original OGG files yet |
| Short ambient encounter lifespan | alpha v0.3 | 90 seconds, manual summoned persistence |
| Witness journal | alpha v0.4 | Three clues, English+Chinese, per-player opt-out/erase |
| Six-event adaptive dramatic director | alpha v0.5 | Silence/Chime/Footsteps/Echo/Afterimage/Sighting |
| Three operator-selectable pacing styles | alpha v0.5 | Gentle/Balanced/Eerie; caps and cooldown unchanged |
| CI pure-Java replay and NeoForge build | alpha v0.5 | Real client+dedicated server acceptance still required |

## Proposed, NOT implemented — narrative & encounters

- [ ] F1 — Woodline watcher: non-interfering creature appears behind trees with
      real line-of-sight constraints; player may approach and watch it retreat.
- [ ] F2 — Cave echo: a long-time-underground flag influences local sound
      selection; only samples player position and existing loaded caves.
- [ ] F3 — Village edge: watches distant streets without attacking villagers,
      touching beds or affecting iron golems.
- [ ] F4 — Window visitor: outside a player's base, NEVER modifies home blocks.
- [ ] F5 — False alarm: sound/no creature encounter; already v0.5 groundwork,
      future biome- and weather-specific enrichment.
- [ ] F6 — Long quiet stretches: tension can decay over nights; future actual
      slow history beyond current session's bounded counter.
- [ ] F7 — Witness silhouette: cheap render-only apparition tied to an
      already-loaded server event, no damage/camera grabs.
- [ ] F8 — Observer personalities: curious/hesitant/wary/echoing behaviors,
      visible through different movement, not stronger damage.
- [ ] F9 — Hide-and-seek: use accurate visibility and player proximity to
      move behind geometry using bounded existing navigation.
- [ ] F10 — Cooperative evidence: multiple consenting players can pool ONLY
      in-game clues, with opt-out and no chat/voice capture.
- [ ] F11 — Evidence tools: a non-invasive diary lens/field recorder as optional
      craftable gameplay objects; no real computer camera/microphone access.
- [ ] F12 — Chapter 2 story: three new investigation outcomes and optional
      peaceful closure; no forced recurring compulsion.
- [ ] F13 — Ambiguous text in player journal: entirely fictional, clearly
      confined to in-game UI; no deceptive OS or app security notifications.
- [ ] F14 — Original environmental structures: tiny naturally generated ruins
      with worldgen-aware no-overlap and no forced chunks; currently postponed
      until worldgen compatibility/performance tests are green.
- [ ] F15 — Original secondary encounter silhouettes: not new copyrighted
      characters or animals replacing canonical A.
- [ ] F16 — Custom survival goals: finding evidence, resolving story,
      and voluntarily quieting the haunting without paid or external accounts.

## Proposed, NOT implemented — art, atmosphere, AI and system

- [ ] F17 — Replace placeholder with faithful authorized A character reference.
- [ ] F18 — Original licensed/recorded soundbank: owl-like syllables, ambiguous
      distant humming, reversed-like atmosphere, consistent low peak volume.
- [ ] F19 — Directional occlusion and muffling from PRELOADED nearby blocks,
      with tiny bounded query budgets.
- [ ] F20 — Light/weather-aware shadows (no photosensitive flashing).
- [ ] F21 — Observer memory limited to one live target/timebox, never across
      account sessions or saving movement trails.
- [ ] F22 — Visual variations: harmless rare gestures, careful facial tracking.
- [ ] F23 — Non-hostile friendly/calm mode for accessibility and younger players.
- [ ] F24 — Server/world settings GUI with clear on/off safety defaults.
- [ ] F25 — Difficulty presets: ambience-only, balanced, eerie; no faster
      forced frequency or unexpectedly high audio levels.
- [ ] F26 — Language packs and subtitles for every custom voice/ambient cue.
- [ ] F27 — Dedicated-server multi-player tests (2, 4, 8 players, 90 min).
- [ ] F28 — Profiling: mspt/AI tick budgets, path recalculation counts, no
      forced chunk API usage, entity cap checks.
- [ ] F29 — Compatibility with shader/resourcepacks/worldgen mods, NeoForge
      1.21.1 mod instances, no compile-time client-only references on server.
- [ ] F30 — Distinct visuals if Ollama is available, but every AI suggestion
      must be valid against an allowlisted in-game event grammar and run
      asynchronously; zero outbound APIs by default.
- [ ] F31 — Save migration and data-fixer tests from alpha 0.2, 0.3, 0.4.
- [ ] F32 — Player feedback and in-game opt-out, no compulsive constant prompts.

## External design references (ideas only, no code/assets copied)

- From The Fog (Minecraft): distant sightings and psychological ambiguity.
- The Man From The Fog (Minecraft): local stealth/watch dynamics.
- Cave Dweller (Minecraft): warnings and escalation with exposure time.
- Alien: Isolation: macro director versus micro entity AI.
- Frictional Games horror design lessons: avoid entirely predictable monster
  puzzles and use pacing/silence.
- Phasmophobia: in-game evidence and journal progression.

Licenses and gameplay mechanics of those projects are not imported. The mod
remains MIT for its own code and uses original or existing vanilla resources.

## Release-quality gates

A. Java 21 and NeoForge 1.21.1 build, new and legacy unit tests all green.
B. Real single-player and dedicated-server launch, log inspection and reload.
C. 30-minute A/B profiler with and without the mod on same seed/hardware.
D. Zero force-load, block tampering or surprise death; night director OFF by
   default; mute/quiet tested on all clients.
E. Old saves survive cleanly and journal data remains user erasable.
F. A-form model approved before claiming visual fidelity; original audio
   rights resolved before distribution.
