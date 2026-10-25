# Gugu Gaga: Nightwatch

An experimental **Minecraft 1.21.1 / NeoForge** psychological-horror mod, focused on a single penguin-like observer, gradual suspense, and fair survival gameplay.

## Status
**v0.3 pre-alpha / haunted observer prototype under development.** The code is being built and verified through pull requests; do not treat it as a released mod.

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
