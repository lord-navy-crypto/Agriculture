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
- Random-tick fluorescent panels emit light and occasionally blink. Level 0 uses only the non-combat Observer: a distant silhouette with no AI, collision, damage or attack behavior; it vanishes when approached or directly noticed.
- Atmospheric cave ambience used as a provisional sound bed.

## Level 1 — The Vulnerable Shelter

**Primary fear motif:** Cleithrophobia (fear of being trapped), supported by Nyctophobia (fear of darkness). The shelter offers supplies, but its partitions and uncertain exits undermine safety.
- Native dimension `threshold:level_1`; custom biome and dimension type.
- Chunk-aligned 48x48 concrete warehouse layout with halls, storage bays, pillars and partitions.
- A guaranteed starter barrel plus scarcer additional supply barrels use separate starter, pantry and maintenance tables with real Agriculture crops, prepared meals, water bottles, seeds and occasional salt.
- The entry room guarantees a small watered, lit planter with six different mature Agriculture crops (barley, corn, soybean, tomato, lettuce and spinach); other storage rooms have rare planters.
- The central water source is a real freshwater source and can refill the Agriculture empty water flask.
- One ceiling panel in the entry warehouse flickers; partitioned storage rooms can have similar faults that remain dark longer than Level 0's office lights.
- The first real predator is the Hound: it uses GeckoLib movement animation and ordinary pathfinding/melee AI. It spawns only in darker Level 1 areas after the player leaves the 48x48 entry shelter; it cannot break blocks and has no special drops.
- Agriculture owns hydration/nutrition/water/food systems. The horror mod does not duplicate them. The entry greenhouse is lit by five overhead sea lanterns so crops can grow without skylight. The warehouse ambience uses a intermittent mechanical vault crackle, contrasting with Level 0's cave-like emptiness.
- This prototype does not yet guarantee Agriculture crop re-growth in the no-sky biome; harvestable patches are an initial resource source.

## Current scope / limitations
- A paired Unstable Wall passage connects Level 0 and Level 1 in both directions; each entry room has a guaranteed return wall. Interact with an empty hand.
- Rooms are deterministic and static on reload; Peripheral Shift is a future event system, not present yet.
- No Level 0 combat mobs. Level 0's Observer is a harmless visual presence; Level 1's Hound is the first real hostile entity. No sound jump scares or client-only shaders are added in this pass.
- Six original 16×16 pixel textures are included for wallpaper, stained wallpaper, warehouse concrete and both light-panel states.
- World generation must be verified with the real dedicated-server smoke test before claiming playability.

## Test

```bash
gradle --no-daemon --stacktrace :threshold:build
python3 qa/validate_threshold_dimensions.py threshold/build/libs/threshold-0.1.0-alpha.7.jar
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
- The Level 1 start cache **guarantees** a boiled drinking-water bottle, a prepared Agriculture meal, raw produce, seeds and an empty flask, so players can begin without an immediate scarcity softlock.
- The first planter now starts with six mature crop species ready for harvest; Agriculture handles subsequent regrowth and nutrition.
- Other Level 1 storerooms use distinct pantry and maintenance loot tables with varied cereal, vegetable, legume, prepared meals, raw/boiled/filtered water, and occasional sea salt. Ordinary locker density is reduced and clean water is not guaranteed in each pantry cache.
- Level 0 stays isolated: some distant dark/red offices contain a very rare forgotten cache, never in the entry room.
- Floor wear, low-light industrial fittings, and extended overhead light panels reinforce the two contrasting environments without introducing extra entities or surprise attacks.
- New room generation applies only to newly explored chunks. For existing Level 0 test saves, OPs can repair the old carpet seam around themselves with `/threshold repair_floor`. This only touches loaded nearby chunks, never creates new ones.

## Upgrade notes: 0.1.0-alpha.3

- Floor contact: Level 0 walls, pillars, dividers and transition walls now start at Y=64 with full-height blocks; walkable areas retain carpet.
- Existing worlds: OP command /threshold repair_floor repairs carpet-under-wall seams in a bounded 5x5 region of already-loaded Level 0 chunks. Move and repeat as necessary, after backing up the save.
- Survival: rare forgotten caches in distant dark Level 0 rooms; Level 1 has separate Agriculture food, maintenance and starter caches with real nutrient profiles, water treatment and salt supplies.
- Atmosphere: carpet wear, warehouse floor patches, overhead fittings, and extended lighting panels.
- CI checks actual Agriculture item IDs in loot tables, plus real registry presence in the dedicated-server smoke test.

## Alpha 4 — survival and geometry pass

- Level 0's existing footing design is checked across the complete 32×32 entry room: every wall-like block at Y=65 must have a matching base at Y=64 and solid concrete directly below at Y=63. The check covers dividers, pillars and the transition wall.
- The `/threshold repair_floor` command remains available for already-generated Level 0 chunks from earlier builds; it only touches loaded chunks in a bounded 5×5 region.
- Level 1 planters contain six mature, harvestable Agriculture species instead of a single immature crop type.
- The Level 1 entry warehouse and partitioned storage rooms can contain a flickering ceiling panel; its recovery interval is longer than the Level 0 office panels.
- Pantry loot now includes prepared meals that carry Agriculture's actual nutrient, hydration and sodium profiles. Ordinary supply lockers are less dense and their water pool is not guaranteed; the entry cache remains protected from a survival softlock.



## Alpha 5 — dimension-specific entity ecology

### Level 0: Observer only
- The Observer is a temporary, distant silhouette. It has no AI, target selector, attack damage, collision, push behavior, selectable hitbox, drops or persistent save data.
- It appears only in Level 0, from a visible and unobstructed position at a distance. It disappears after a short interval, when the player approaches, or when the player directly looks at it.
- Level 0 rejects every other living creature and all projectiles joining the dimension, including vanilla and third-party mobs; players can still traverse normally. It is not a combat or chase level.
- The Observer is atmosphere, not a threat: its purpose is to make the player question whether something is watching from the far end of an otherwise empty corridor.

### Level 1: first predator
- The Hound is a real Monster with player targeting, pathfinding and melee behavior. It has explicit health, movement, damage and follow-range attributes.
- Hounds are spawned by the Threshold entity director, not generic biome spawner tables. They are restricted to Level 1, prefer darker areas, and cannot spawn while the player remains inside the first 48×48 entry shelter.
- In this first pass, the Hound is the only permitted Level 1 living creature besides players. Vanilla animals/monsters and unrelated modded creatures are rejected until they receive an explicit Backrooms design and whitelist entry.
- A nearby active Hound suppresses another encounter, keeping the first pass controlled rather than turning the warehouse into a mob farm.
- The Hound does not break blocks or alter the validated room graph. It uses a dedicated no-drop loot table so it does not create an unintended food/material loop.
- The current Hound is the first original Backrooms-inspired predator prototype, not a claim that every Fandom/Wikidot entity list is one unified canon. Additional creature types should be added individually after visual, AI and balance tests.

### Shared rules
- Level 0's Observer cannot attack, damage, chase or kill the player. Its disappearance is the only response to being noticed.
- Level 1 may contain hostile entities and real survival risk, but the starter shelter, exits, room geometry and Agriculture systems remain unchanged.
- Both biomes retain empty generic spawners and spawn_costs maps. The entity director enforces the dimension rules explicitly.
- The independent JAR packages both GeckoLib models, animations and textures. No extra model pack is required.


## Alpha 6 — deterministic architectural complexes

### Level 0 office structures
- The original 32×32 room grid, entry office, carpet/floor elevations, doorway decisions and transition wall remain unchanged.
- Distant rooms now select deterministic architectural archetypes from the world seed and room coordinates: open-plan cubicle clusters, staggered office partitions, repeated column bays, suspended service ducts and mirrored/echo office layouts.
- Cubicle and partition geometry uses gaps and route reservations rather than sealing corridors. Every actual generated doorway reserves a continuous route back to the room hub.
- The entry room is deliberately excluded from overlays so the first impression remains the familiar empty yellow office.

### Level 1 industrial structures
- The original 48×48 warehouse cells, starter shelter, planter footprint, supply caches, floor elevation and return passage remain unchanged.
- Distant rooms can become pallet-rack aisles, copper service galleries, raised loading platforms, static machine bays, overhead gantries or mirrored rack layouts.
- Rack spans, dock platforms and machine islands avoid the reserved circulation routes and the Agriculture resource footprints. Gantries remain above normal walking height; their supports also respect protected routes.
- These are deterministic block assemblies, not ticking machinery or redstone contraptions. They add architectural scale without introducing extra server load from block entities.

### Phantom-inspired spatial variation
- Some rooms preserve their signature architecture while mirroring its internal arrangement or changing its material/lighting rhythm. This borrows the broad design idea of a Phantom variant, not another creator's exact room blueprint or assets.
- Exit topology, entry rooms and inter-level transitions are not mirrored or relocated. The variation is architectural and perceptual, not a silent route rewrite.


## Alpha 7 — four-level traversal and original horror audio

### Original Level 0 and Level 1 music
- Level 0 now has an original, low-volume eerie office drone: slow detuned fluorescent hum, soft spectral movement and a distant tonal layer.
- Level 1 has a separate original industrial ambience: low transformer resonance, rotating machinery rhythm and restrained metallic overtones.
- Both tracks are original generated audio, packaged inside the independent JAR and selected by their own biome music entries. They do not replace the established flickering lights, cave/vault ambience or room layouts.

### Level 3 — Electrical Station
- A separate dimension built around narrow brick service corridors, overhead copper mains, cable runs, static electrical rooms and occasional barred black-glass observation slits.
- Deterministic room archetypes include plain pipe tunnels, equipment bays, switchgear galleries, window-like observation rooms, cable trenches and transformer chambers.
- Scarce supply barrels use real Agriculture hydration and food items. Generic biome spawning remains disabled; entity ecology can be added in a later pass.

### Level 4 — Abandoned Office
- A separate, quieter dimension of broad empty office cells, pale concrete, sparse desks, reception counters, meeting rooms, blacked-out windows and sealed water-cooler props.
- Occasional caches provide more reliable Agriculture hydration and food than Level 3, while most rooms remain intentionally empty.
- The ambient profile is softer and brighter than Level 3; windows are opaque and do not create unsafe openings.

### Traversal network
- Level 0 ↔ Level 1: the original validated transition remains unchanged.
- Level 1 → Level 3: a fixed industrial service lift in a distant warehouse cell. This is an explicit bridge while Level 2 is not yet implemented.
- Level 3 → Level 1: a return lift near the Level 3 entry sector.
- Level 3 ↔ Level 4: an Office Sector door and a return elevator, matching the documented electrical-station/office-sector connection.
- Each passage is an interactable in-world door block that transfers the player to a safe, authored arrival point. Operator travel commands are also available: /threshold travel level_3 and /threshold travel level_4.
- The route graph is deliberately non-linear at the level of room layouts, but its authored inter-level links remain stable and discoverable. Level 2 is not silently fabricated; its future entrances can be added without moving these links.

### Canon and spatial design
- Wikidot remains the primary reference. Fandom contributes optional details such as Level 3's pipe-lined, shifting corridors, repeated machinery rooms and barred windows.
- Level 3 keeps the electrical-facility identity; Level 4 keeps the mostly empty office identity and its lower entity density.
- Phantom-inspired variation is limited to original mirrored room interiors and altered spatial rhythm. No external map, texture, model or exact room blueprint is copied.
- Existing Level 0 / Level 1 room sizes, entry rooms, carpet/floor elevations, lighting character, Agriculture systems and validated transition coordinates remain protected.

## Horror identity preservation

**Survival Horror extends the original horror; it does not replace it.** The established Level 0 and Level 1 themes, room scale, routes, architecture, lighting character and ambience are protected design invariants.

- Level 0 remains the sparse 32×32 yellow-office maze: faded wallpaper, carpet, empty space, cave-like ambience, brief fluorescent flicker and occasional spatially isolating boundaries. It is not a farm or a reliable supply hub.
- Level 1 remains the 48×48 industrial warehouse: concrete, partitions, storage bays, ventilation, vault-like ambience and longer industrial lighting faults. Its planter and starter cache stay localized.
- Existing door decisions, the Level 0 → Level 1 transition and the Level 1 return passage are preserved. Survival balancing must not resize rooms, move routes or rewrite already generated spaces.
- Level 0 has no hostile entities; only its harmless Observer is allowed. Level 1 now supports the controlled Hound predator. No combat behavior is added to Level 0.
- Agriculture remains the only source of food, nutrition, hydration and sodium mechanics. Threshold only distributes real Agriculture content.

The CI preservation gate runs the following command:

    python3 qa/validate_threshold_horror_preservation.py

It protects room dimensions and vertical profiles, entry-room identity, transition positions, distinct lighting behavior, biome ambience, architecture markers and the Level 0 observer-only / Level 1 controlled-predator scope. Intentional design changes require an explicit update to the design contract and its tests.
