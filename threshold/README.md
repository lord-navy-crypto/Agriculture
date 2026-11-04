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
python3 qa/validate_threshold_dimensions.py threshold/build/libs/threshold-0.1.0-alpha.3.jar
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

## Survival Horror integration (Agriculture required)

- No duplicate nutrition bar or thirst/sodium mechanics: Agriculture owns the existing nutrition, hydration and salt physiology.
- The Level 1 start cache **guarantees** a boiled drinking-water bottle and several kinds of real Agriculture foods/seeds, so players can begin exploring without immediate scarcity softlocks.
- Other Level 1 storerooms use distinct pantry and maintenance loot tables with varied cereal, vegetable, legume, raw/boiled/filtered water, and occasional sea salt. Safe drinking water is valuable and not guaranteed in each ordinary cache.
- Level 0 stays isolated: some distant dark/red offices contain a very rare forgotten cache, never in the entry room.
- Floor wear, low-light industrial fittings, and extended overhead light panels reinforce the two contrasting environments without entities or surprise attacks.
- New room generation applies only to newly explored chunks. For existing Level 0 test saves, OPs can repair the old carpet seam around themselves with `/threshold repair_floor`. This only touches loaded nearby chunks, never creates new ones.

## Upgrade notes: 0.1.0-alpha.3

- Floor contact: Level 0 walls, pillars, dividers and transition walls now start at Y=64 with full-height blocks; walkable areas retain carpet.
- Existing worlds: OP command /threshold repair_floor repairs carpet-under-wall seams in a bounded 5x5 region of already-loaded Level 0 chunks. Move and repeat as necessary, after backing up the save.
- Survival: rare forgotten caches in distant dark Level 0 rooms; Level 1 has separate Agriculture food, maintenance and starter caches with real nutrient profiles, water treatment and salt supplies.
- Atmosphere: carpet wear, warehouse floor patches, overhead fittings, and extended lighting panels.
- CI checks actual Agriculture item IDs in loot tables, plus real registry presence in the dedicated-server smoke test.
