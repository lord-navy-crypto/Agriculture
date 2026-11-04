# THRESHOLD — Liminal Horror Mod (Development)

**Independent NeoForge 1.21.1 mod subproject in the Agriculture repository**

- Mod ID: `threshold`; independent JAR and resource namespace.
- Java 21 / NeoForge 21.1.259.
- **Gugu-Gaga Agriculture 3.9+ is a required runtime dependency** for this experiment.
- Required additions: GeckoLib 4.9.3 and Lithostitched 1.7.11 (NeoForge 1.21.1). Libraries are installed as separate mods, never embedded.
- The existing Agriculture module and main branch stay unchanged by this draft PR.

## Libraries (Phase II)

- GeckoLib 4.9.3: `threshold:ceiling_vent` is a real BlockEntity animated via GeckoLib 4, with a rotating fan model and a client-side renderer. It is presentation-only.
- Lithostitched 1.7.11: `threshold:room_sequence` is a `lithostitched:composite` that places base room geometry first, then a second, chunk-safe `threshold:industrial_vent` feature.
- Gugu-Gaga Agriculture remains a required survival mod.
- No copy of any library is bundled inside the Threshold JAR.

## Level 0 — Isolation

**Primary fear motif:** Kenophobia (empty-space dread), supported by Monophobia (fear of being alone). These are creative themes, not clinical labels applied to players.
- Native dimension `threshold:level_0`; custom biome and dimension type.
- Chunk-aligned deterministic yellow office grid, 32x32 room cells with shared door decisions. The entry room is guaranteed to use the classic yellow-office variant.
- Faded wallpaper and stained wallpaper blocks, carpet, column rooms, dark rooms and rare red rooms.
- Random-tick fluorescent panels emit light and occasionally blink. No entity AI in this release.
- Atmospheric cave ambience used as a provisional sound bed.

## Level 1 — The Vulnerable Shelter

**Primary fear motif:** Cleithrophobia (fear of being trapped), supported by Nyctophobia (fear of darkness). The shelter offers supplies, but its partitions and uncertain exits undermine safety.
- Native dimension `threshold:level_1`; custom biome and dimension type.
- Chunk-aligned 48x48 concrete warehouse layout with halls, storage bays, pillars and partitions.
- A guaranteed starter barrel plus scarce additional supply barrels use `threshold:chests/level_1_supply` loot drawing from real `gugugaga` foods.
- The entry room guarantees a small watered, lit planter of actual Agriculture crops; other storage rooms have rare planters.
- Agriculture owns hydration/nutrition/water/food systems. The horror mod does not duplicate them. The entry greenhouse is lit by five overhead sea lanterns so crops can grow without skylight. The warehouse ambience uses a intermittent mechanical vault crackle, contrasting with Level 0's cave-like emptiness.
- This prototype does not yet guarantee Agriculture crop re-growth in the no-sky biome; harvestable patches are an initial resource source.

## Current scope / limitations
- A paired Unstable Wall passage connects Level 0 and Level 1 in both directions; each entry room has a guaranteed return wall. Interact with an empty hand.
- Rooms are deterministic and static on reload; Peripheral Shift is a future event system, not present yet.
- No new mobs, no AI hunters, no sound jump scares or client-only shaders.
- Six original 16×16 pixel textures are included for wallpaper, stained wallpaper, warehouse concrete and both light-panel states.
- World generation must be verified with the real dedicated-server smoke test before claiming playability.

## Test

```bash
gradle --no-daemon --stacktrace :threshold:build
python3 qa/validate_threshold_dimensions.py threshold/build/libs/threshold-0.1.0-alpha.2.jar
python3 qa/smoke_threshold_server.py
```

Test commands (requires OP permissions):
```
/threshold travel level_0
/threshold travel level_1
/threshold travel overworld
/threshold whereami
```

## Animated industrial details
- Level 1 has ceiling ventilation units distributed deterministically. The entrance room guarantees one at `(12, 73, 12)`.
- No powered mechanics are simulated; GeckoLib animates the vent fan without changing block collision or other AI.
- Additional tests verify the generated vent is backed by the expected GeckoLib BlockEntity.

## Composite worldgen biome-check invariant

The registered outer `threshold:room_sequence` feature is biome-filtered by NeoForge.
Lithostitched executes its nested `threshold:room_layout` and `threshold:industrial_vent`
placed features inside that biome-filtered context. **Neither nested feature may contain
a `minecraft:biome` placement modifier**: such a modifier checks an unregistered
nested feature against the outer biome generation step and crashes chunk generation
with `IllegalStateException: Tried to biome check an unregistered feature`.
The packaged-JAR validator enforces this invariant.
