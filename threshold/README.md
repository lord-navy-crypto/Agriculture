# THRESHOLD — Liminal Horror Mod (Development)

**Independent NeoForge 1.21.1 mod subproject in the Agriculture repository**

- Mod ID: `threshold`; independent JAR and resource namespace.
- Java 21 / NeoForge 21.1.259.
- **Gugu-Gaga Agriculture 3.9+ is a required runtime dependency** for this experiment.
- No additional animation/rendering library is required for the first environment-only pass.
- The existing Agriculture module and main branch stay unchanged by this draft PR.

## Level 0 — Isolation
- Native dimension `threshold:level_0`; custom biome and dimension type.
- Chunk-aligned deterministic yellow office grid, 32x32 room cells with shared door decisions.
- Faded wallpaper and stained wallpaper blocks, carpet, column rooms, dark rooms and rare red rooms.
- Random-tick fluorescent panels emit light and occasionally blink. No entity AI in this release.
- Atmospheric cave ambience used as a provisional sound bed.

## Level 1 — The Vulnerable Shelter
- Native dimension `threshold:level_1`; custom biome and dimension type.
- Chunk-aligned 48x48 concrete warehouse layout with halls, storage bays, pillars and partitions.
- Scarce supply barrels with `threshold:chests/level_1_supply` loot drawing from real `gugugaga` foods.
- A minority of storage rooms have watered small planters of actual Agriculture crops.
- Agriculture owns hydration/nutrition/water/food systems. The horror mod does not duplicate them.
- This prototype does not yet guarantee Agriculture crop re-growth in the no-sky biome; harvestable patches are an initial resource source.

## Current scope / limitations
- Development commands are operator-only; physical exit transitions are not yet implemented.
- Rooms are deterministic and static on reload; Peripheral Shift is a future event system, not present yet.
- No new mobs, no AI hunters, no sound jump scares or client-only shaders.
- Five original 16×16 pixel textures are included for wallpaper, stained wallpaper, warehouse concrete and both light-panel states.
- World generation must be verified with the real dedicated-server smoke test before claiming playability.

## Test

```bash
gradle --no-daemon --stacktrace :threshold:build
python3 qa/validate_threshold_dimensions.py threshold/build/libs/threshold-0.1.0-alpha.1.jar
python3 qa/smoke_threshold_server.py
```

Test commands (requires OP permissions):
```
/threshold travel level_0
/threshold travel level_1
/threshold travel overworld
/threshold whereami
```
