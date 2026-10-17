# Gugu Gaga: Nightwatch

An experimental **Minecraft 1.21.1 / NeoForge** psychological-horror mod, focused on a single penguin-like observer, gradual suspense, and fair survival gameplay.

## Status
**Pre-alpha / playable prototype under development.** The code is being built and verified through pull requests; do not treat it as a released mod.

## Gameplay direction
- The entity initially **observes** instead of attacking.
- A server-side state machine manages **dormant → observe → approach → retreat**.
- A separate, bounded encounter director will eventually control pacing without scanning or changing unrelated chunks.
- Player control: adjustable encounter frequency, opt-out, audio and visual accessibility settings.
- Single-player first, then dedicated-server and multiplayer QA.

## Optional local Ollama AI (SWARM-MOBS inspired)
The deterministic observer runs fully offline. The optional server-side local Ollama shadow adviser is **disabled by default** and only makes requests after explicit commands. Use `/gugugaga ai detect` to list local models and `/gugugaga ai shadow` for a one-off pacing recommendation. It never controls per-tick AI or sends coordinates. See [docs/OLLAMA_OPTIONAL.md](docs/OLLAMA_OPTIONAL.md).

## Build
Java 21, Minecraft 1.21.1, NeoForge 21.1.x. See `docs/SETUP.md` after the first source commit.

## Assets and licensing
Code authored for this project is intended to be MIT-licensed (see LICENSE when added). **The words “inspired by” or “fan project” and the MIT code license do not grant permission to reuse third-party images, voices, or character designs.** Online meme images are references only; do not redistribute them. The prototype ships its own placeholder artwork. Asset provenance and publication clearance are tracked in `docs/ASSET_PROVENANCE.md`.

Unofficial fan project; not endorsed by Arknights: Endfield, Hypergryph, Gryphline, BanG Dream!, or relevant creators.
