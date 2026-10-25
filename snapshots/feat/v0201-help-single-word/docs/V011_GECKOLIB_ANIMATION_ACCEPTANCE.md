# v0.11 GeckoLib migration — engineering and acceptance

Branch: `feat/geckolib-v011-entity-animation` based on v0.10 PR #12. Target: Minecraft 1.21.1, NeoForge 21.1.259, Java 21, GeckoLib **4.9.3 NeoForge** (not GeckoLib5).

## Installed in code
- Gradle `software.bernie.geckolib:geckolib-neoforge-1.21.1:4.9.3`; mandatory **both sides** in generated mod metadata.
- Three independent `GeoEntity` instance caches and animation controllers; `GeoEntityRenderer` and `GeoModel` replace actual native rendering.
- Entirely original geometry, approximating our existing silhouettes: observer beak / wings, Lapis masked mineral and revealed Redactor, friendly / exposed Yellow. Existing original texture generator remains in place.
- Keyframed loops: Observer dormant/watch/approach/retreat/angry/corrupted/blackened; Lapis gentle mask sway vs revealed archivist; Smiler friendly/exposed and short server-triggered **gift nod**.
- Client state is derived only from existing `SynchedEntityData` (phase/mood/corruption/revealed), not from invented client-only game logic. Game rules, multiplayer opt-in, cooldowns and no forced chunk edits remain untouched.

## Not yet implemented
Smooth **interpolated** geometry replacement during Lapis reveal (two separate geo models swap on synced state), bespoke sound bank, reactive pursuit/stealth mechanics, biome-specific episodes, endings A–E and final Boss.

## Critical runtime dependency
Download the exact official [GeckoLib 4.9.3 for NeoForge MC1.21.1](https://www.curseforge.com/minecraft/mc-mods/geckolib/files/8893490) and put it into both **client and dedicated server** `mods/` folders with this v0.11 JAR. Do not mix loaders. GeckoLib v5 is for newer MC and has different package names.

## Acceptance gates (before changing main)
1. GitHub Actions unit checks, Java21 policy checks, `gradle build` and original sprite generation all green.
2. Verify clean **client** startup in Minecraft 1.21.1/NeoForge 21.1.259/Java21 with GeckoLib present. Deliberately remove GeckoLib once to ensure loader refuses with a clear dependency message.
3. `/summon gugugaga:observer`: watch, approach, retreat, angry, corrupted, blackened visibly animate and use the correct generated texture. Test save/reload.
4. `/summon gugugaga:redactor`: Lapis disguise and reveal/re-mask correctly swap model and animation; verify eyes and face are not UV-corrupted.
5. `/summon gugugaga:yellow_smiler`: friendly/exposed postures; interact for a torch and see a single head nod (not looped).
6. In a **dedicated server** with two opted-in clients, ensure no missing GeckoLib class/linkage, scene states are consistent, opt-outs remain quiet, and the five-act loop continues.
7. Run once without external ambience/shaders, then separately with Sound Physics and optional client renderer mods. Check clipping, shadows, FPS and console logs.

A passing CI is **not** proof of a real client/multiplayer acceptance; do not merge this draft PR into v0.10 or main without those gates.
