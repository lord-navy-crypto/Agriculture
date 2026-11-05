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

**Primary fear motif:** isolation and liminal emptiness. Autophobia/Monophobia (fear of being alone) is a useful creative reference; “Kenophobia” is used descriptively for empty-space dread, not as a clinical label.
- Native dimension `threshold:level_0`; custom biome and dimension type.
- Chunk-aligned deterministic yellow office grid, 32x32 room cells with shared door decisions. The entry room is guaranteed to use the classic yellow-office variant.
- Faded wallpaper and stained wallpaper blocks, carpet, column rooms, dark rooms and rare red rooms.
- Random-tick fluorescent panels emit light and occasionally blink. Level 0's only natural entity is the Watcher: a bounded stalker with four observation styles, timestamped local memory, three-point corner-peek searches, rare path-based fake retreats, and occasional rush/near-miss scares with a single low-damage swipe. An aimed flashlight can interrupt it.
- Atmospheric cave ambience used as a provisional sound bed.

## Level 1 — The Vulnerable Shelter

**Primary fear motif:** Nyctophobia (fear of darkness), with claustrophobic pressure reserved for narrow maintenance sub-areas. The main warehouse remains broad and industrial: the shelter offers supplies, but unstable power and uncertain routes undermine safety.
- Native dimension `threshold:level_1`; custom biome and dimension type.
- Chunk-aligned 48x48 concrete warehouse layout with halls, storage bays, pillars and partitions.
- A guaranteed starter barrel plus scarcer additional supply barrels use separate starter, pantry and maintenance tables with real Agriculture crops, prepared meals, water bottles, seeds and occasional salt.
- The entry room guarantees a small watered, lit planter with six different mature Agriculture crops (barley, corn, soybean, tomato, lettuce and spinach); other storage rooms have rare planters.
- The central water source is a real freshwater source and can refill the Agriculture empty water flask.
- One ceiling panel in the entry warehouse flickers; partitioned storage rooms can have similar faults that remain dark longer than Level 0's office lights.
- The first real predator is the Hound: it stalks quiet players, hears nearby sprinting/jumping as an uncertain clue, searches with a bounded golden-angle pattern, and commits to melee only after sight or close proximity. Its bite has a dedicated GeckoLib animation. It spawns only in darker Level 1 areas after the player leaves the 48x48 entry shelter; it cannot break blocks and has no special drops. Its flashlight counter requires an active beam aimed at it with line of sight.
- Agriculture owns hydration/nutrition/water/food systems. The horror mod does not duplicate them. The entry greenhouse is lit by five overhead sea lanterns so crops can grow without skylight. The warehouse ambience uses a intermittent mechanical vault crackle, contrasting with Level 0's cave-like emptiness.
- This prototype does not yet guarantee Agriculture crop re-growth in the no-sky biome; harvestable patches are an initial resource source.

## Current scope / limitations
- A paired Unstable Wall passage connects Level 0 and Level 1 in both directions; each entry room has a guaranteed return wall. Interact with an empty hand.
- Rooms are deterministic and static on reload; Peripheral Shift is a future event system, not present yet.
- Level 0 has one authored encounter entity, the Watcher; it is not a conventional mob swarm. Local encounters are capped at two coordinated Watchers. Level 1 retains the Hound as its industrial predator. The Watcher uses short audio stings and ordinary vanilla hurt feedback rather than a custom screen shader.
- Six original 16×16 pixel textures are included for wallpaper, stained wallpaper, warehouse concrete and both light-panel states.
- World generation must be verified with the real dedicated-server smoke test before claiming playability.

## Test

```bash
gradle --no-daemon --stacktrace :threshold:build
python3 qa/validate_threshold_dimensions.py threshold/build/libs/threshold-0.1.0-alpha.19.jar
python3 qa/smoke_threshold_server.py
```

Test commands (requires OP permissions):
```
/threshold travel level_0
/threshold travel level_1
/threshold travel level_2
/threshold travel level_3
/threshold travel level_4
/threshold travel level_5
/threshold travel overworld
/threshold whereami
/threshold summon watcher
/threshold summon watcher_hunt
/threshold watcher status
/threshold summon hound
/threshold summon partygoer
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

### Level 0: original Observer prototype (superseded by Alpha 18)
- The first Observer implementation was a harmless, temporary silhouette with no AI, collision or attack behavior.
- Alpha 18 replaces that passive prototype with the active Watcher state machine described below. The natural-spawn whitelist still admits only the Watcher in Level 0; unrelated natural mob spawns remain disabled.
- The current Watcher has normal collision/path navigation, can be attacked, and may perform one limited scare attack before withdrawing. It does not break blocks or use hidden live player tracking.

### Level 1: first predator
- The Hound is a real Monster with player targeting, pathfinding and melee behavior. It has explicit health, movement, damage and follow-range attributes.
- Hounds are spawned by the Threshold entity director, not generic biome spawner tables. They are restricted to Level 1, prefer darker areas, and cannot spawn while the player remains inside the first 48×48 entry shelter.
- In this first pass, the Hound is the only permitted Level 1 living creature besides players. Vanilla animals/monsters and unrelated modded creatures are rejected until they receive an explicit Backrooms design and whitelist entry.
- A nearby active Hound suppresses another encounter, keeping the first pass controlled rather than turning the warehouse into a mob farm.
- The Hound does not break blocks or alter the validated room graph. It uses a dedicated no-drop loot table so it does not create an unintended food/material loop.
- The current Hound is the first original Backrooms-inspired predator prototype, not a claim that every Fandom/Wikidot entity list is one unified canon. Additional creature types should be added individually after visual, AI and balance tests.

### Shared rules
- The original Alpha 5 Observer could not attack, damage or chase. Alpha 18 supersedes that behavior with the bounded Watcher encounter; the current entity does not chain attacks or break blocks.
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

## Alpha 11 — entity expansion and classic survival equipment

- The pre-Alpha18 Watcher silhouette was taller and more elongated, with irregular head/arm/shroud animation. Alpha 18 adds path navigation, observation styles, rush/strike and retreat behavior.
- Level 1 Hound remains the only hostile entity inside the Backrooms.
- A rare night-time Overworld Partygoer encounter slowly follows a player and relocates them to the Level 0 entry after sustained close proximity. It does not deal damage. It has a dedicated 64×64 yellow smiling-mask texture; its first-pass geometry reuses the tall humanoid silhouette rig.
- Added `/threshold summon watcher`, `/threshold summon hound`, and `/threshold summon partygoer` test commands, each restricted to its intended dimension.
- Added rare Almond Water, Flashlight and Battery items to Level 0 forgotten caches and Level 1 supply/maintenance caches. Agriculture continues to own ordinary hydration and nutrition; Almond Water is a rare restorative utility, and the flashlight currently provides a short night-vision pulse rather than dynamic world lighting.
- Added more Level 0 abandoned meeting-room details and Level 1 cold-storage loading-bay architecture without changing room routing or the established entry layout.

## Alpha 10 — visible Level 0 and Level 1 entities

- Register the GeckoLib Hound and Watcher entity renderers on the client MOD event bus.
- The renderer subscriber is explicitly restricted to Dist.CLIENT so dedicated servers never load client-only classes.
- The packaged-JAR validator now requires the renderer event subscriber, and the source validator checks both entity-to-renderer registrations.

## Alpha 9 — source-backed six-level transit network

### Level 2 — Abandoned Utility Halls
- Level 2 is now a full playable dimension, not an invisible waypoint. Its 32×32 deterministic room cells contain narrow concrete service corridors, pipe galleries, copper mains, cable runs, maintenance trenches, valve bays and damp sump edges.
- Room variants are generated per world seed and room coordinate. Shared-edge doorway decisions keep adjacent cells connected, and the QA graph checks that no Level 2 room is sealed.
- The entry cell remains readable; deeper cells become denser and more industrial. Agriculture supplies are not duplicated or turned into a second survival system.

### Level 0 and Level 1 original music
- Level 0 keeps its original, low-volume eerie office drone: detuned fluorescent hum, soft spectral movement and a distant tonal layer.
- Level 1 keeps its separate industrial ambience: low transformer resonance, mechanical rhythm and restrained metallic overtones.
- Both OGG tracks are packaged in the independent JAR and selected by their own biome music entries. Existing flickering lights, cave/vault ambience and established room layouts remain intact.

### Level 3 — Electrical Station
- Narrow brick service corridors, copper mains, cable runs, static electrical rooms, equipment bays, switchgear galleries, black-glass observation slits, cable trenches and transformer chambers.
- Scarce supply barrels use real Agriculture hydration and food items. Generic biome spawning remains disabled in this architecture pass.

### Level 4 — Abandoned Office
- Broad, quiet office cells, pale concrete, sparse desks, reception counters, meeting rooms, blacked-out windows and sealed water-cooler props.
- Occasional caches provide more reliable Agriculture hydration and food than Level 3, while most rooms remain intentionally empty.

### Level 5 — Terror Hotel
- Level 5 is a playable hotel dimension with a 32×32 room grid and three guaranteed, distinct zones: the furnished Main Hall, the Beverly ballroom and the industrial Boiler Room.
- The Main Hall uses dark timber floors, red-and-gold detailing, carpet runners, sparse furniture, room-wing caches and a working hotel elevator panel.
- The Beverly Room is a larger ballroom cell with a central table, chandelier and many connecting doors. Its layout connects naturally to the Main Hall and Boiler Room.
- The Boiler Room uses dark stone floors, old machinery, copper mains, chains, cauldrons and narrow water-leak channels. Its deeper exit remains deferred until Level 6 exists.
- Seed-stable guest suites, compressed corridors and a rare unconfirmed Lobby Room provide additional architectural variation.
- The hotel elevator panel cycles floors 1–12 with sneak-use. Normal use travels to the selected wing within Level 5; it never sends the player to an unimplemented destination.
- A subdued, irregular vanilla piano-note mood cue supports the hotel's quiet, uncanny atmosphere. Level 0 and Level 1 original OGG music remains unchanged.
- Scarce caches use actual Agriculture water and food items; no duplicate nutrition or hydration system is introduced.

### Source-backed transit graph
- Level 0 → Level 1: preserve the original validated unstable-wall passage.
- Level 1 → Level 2: a seamless corridor transition. Warehouse fittings gradually become copper pipes, chains and utility fixtures before the player crosses an invisible trigger into Level 2. This replaces the earlier non-canonical direct Level 1 → Level 3 lift.
- Level 2 → Level 1 / Level 3: unmarked doors and fire exits. The fire-exit destination is selected from Level 1 or Level 3.
- Level 2 → Level 1 / Level 3 / Level 4: unmarked branch doors use a seed-stable weighted destination draw. Each door's destination is fixed by world seed and coordinates, so repeated use cannot reroll it.
- Level 3 → Level 2: the return maintenance corridor lets a wanderer retrace the route used to enter the electrical station. This is a gameplay return path, not a claim that every source version documents a separate Level 3 exit.
- Level 3 → Level 4: the metallic Office Sector door, based on the Fandom Level 4 entrance description.
- Level 4 → Level 3: a service elevator return, retained as a Wikidot-version route variant.
- Level 3 → Level 4 / Level 5: a service elevator uses a seed-stable 9:1 relative draw. Level 4 is common and Level 5 is rarer in the source description; 9:1 is gameplay tuning, not a canon probability.
- Level 4 → Level 5: an old-fashioned stairway, with a climbable stairwell leading to the hotel.
- Level 5 → Level 3 / Level 4: fixed elevator and stairwell returns.
- Level 5 → Level 5: the hotel elevator selects one of twelve internal wings; its floor selection is stored in the block state.

### Random route rules
- Source pages describe many routes as common, occasional, rare or random but generally do not publish numeric probabilities.
- The code uses relative gameplay weights, not asserted canon percentages. Current weights are 4:4:1 for the Level 2 branch, 1:1 for the Level 2 fire exit and 9:1 for the Level 3 elevator to Levels 4 and 5.
- The selected destination is deterministic per world seed and door position. Different doors can resolve differently, while one door remains stable after reload.
- Unimplemented destinations such as Level 6, Level 9, Level 14, Level 27 and other distant exits are documented as future route candidates, not silently redirected to an unrelated built dimension.

### Source handling
- Wikidot and Fandom are treated as separate source profiles, not flattened into one contradictory canon.
- The current Wikidot pages define the main Level 0 → 1 → 2 progression, Level 2's broad links to Levels 1, 3 and 4, and Level 5's hotel areas, stairway/elevator links and internal elevator wings.
- Fandom contributes its own fire-exit, anomalous-corridor and Office Sector details. Where a page is being rewritten or differs from the other wiki, the route remains attributed to that version.
- Only source-documented ordinary-level connections are implemented; no separate mirrored level family is generated. No copied maps, sound recordings, textures or exact room blueprints are used.

### Protected invariants
- Level 0 / Level 1 room sizes, entry rooms, floor elevations, original wall transition, lighting identity and Agriculture survival integration remain protected.
- Level 2 is additive; it does not resize or regenerate existing Level 0 / Level 1 rooms.
- Level 3 and Level 4 retain their own architectural and horror identities. Level 5 adds a hotel palette and room grammar rather than reusing their layouts.

## Horror identity preservation

**Survival Horror extends the original horror; it does not replace it.** The established Level 0 and Level 1 themes, room scale, routes, architecture, lighting character and ambience are protected design invariants.

- Level 0 remains the sparse 32×32 yellow-office maze: faded wallpaper, carpet, empty space, cave-like ambience, brief fluorescent flicker and occasional spatially isolating boundaries. It is not a farm or a reliable supply hub.
- Level 1 remains the 48×48 industrial warehouse: concrete, partitions, storage bays, ventilation, vault-like ambience and longer industrial lighting faults. Its planter and starter cache stay localized.
- Existing door decisions, the Level 0 → Level 1 transition and the Level 1 return passage are preserved. Survival balancing must not resize rooms, move routes or rewrite already generated spaces.
- Level 0 admits only the Watcher as a natural encounter entity; its active behavior is bounded and locally capped. Level 1 supports the controlled Hound predator. Neither level enables generic vanilla mob spawning.
- Agriculture remains the only source of food, nutrition, hydration and sodium mechanics. Threshold only distributes real Agriculture content.

The CI preservation gate runs the following command:

    python3 qa/validate_threshold_horror_preservation.py

It protects room dimensions and vertical profiles, entry-room identity, transition positions, distinct lighting behavior, biome ambience, architecture markers and the Level 0 bounded-Watcher / Level 1 controlled-predator scope. Intentional design changes require an explicit update to the design contract and its tests.


## Alpha 12 — entity presentation, rechargeable equipment and room variety

- The pre-Alpha18 Level 0 Observer slowly turned toward the nearest player and used rare one-tick visual dropouts. Alpha 18 supersedes its harmless behavior with the active Watcher encounter.
- The Hound now has a dedicated pursuit-run animation, distinct from its idle and walk cycles. It remains a Level 1-only hostile and still cannot break blocks.
- Partygoer now uses its own geometry and animation resources with the existing smiling-mask texture. Its capture window is longer; creative and spectator players are excluded, and it has no attack goal.
- Entity renderer registration is consolidated into one client-only MOD-bus subscriber. The former second subscriber is disabled to prevent duplicate renderer registration.
- The flashlight now has persistent 8-minute charge represented by its durability bar. Holding it active consumes charge; a Battery restores up to 3 minutes when the flashlight is held in the other hand.
- The first-pass flashlight beam is still represented by a short night-vision pulse, not a dynamic light source in the world.
- Level 0 gains a rare blackout-annex room archetype. Level 1 gains an abandoned conveyor/service-cable archetype. Both keep the existing room dimensions, entry shelter, and reserved paths intact.
- The CI resource gate and source/JAR validators now include the Partygoer model/animation and Alpha 12 artifact name.

**Keep this PR Draft. Do not merge into main.**


## Alpha 13 — distinct Level 0 / Level 1 identity pass

- Level 0 generation now strongly favours the original yellow office baseline (82% of style rolls); grey rooms are uncommon and red rooms are exceptional. Empty architectural cells are more common than furnished/partitioned cells, so the Lobby remains defined by absence rather than clutter.
- Level 1 now has weighted industrial room selection rather than uniform archetype selection. Two uncommon additions are a broad parking-deck bay with painted lanes and a narrow maintenance spine with overhead pipe runs. The reserved room routes, starter shelter and resource footprints remain protected.
- Light behavior now has intentionally different rhythms: Level 0 fixtures fail less often and recover quickly; Level 1 fixtures fail more often and stay dark longer.
- Watcher sightings remain uncommon and begin outside the player's forward view; Hound encounters are less spammy, prefer dark rooms, and no longer materialize directly in the player's view.
- The Hound retreats only when a lit flashlight beam is actually aimed at it and has line of sight; merely holding the active flashlight while looking elsewhere no longer repels it.
- Room sizes, transition walls, entry positions, return passages and all six dimension registrations are unchanged.

### Long-range design direction
- Level 0: silence/absence, repeated yellow office grammar, subtle spatial anomalies, rare Watcher sightings that can escalate into a limited encounter, and almost no dependable supplies.
- Level 1: industrial scale, low fog, parking/warehouse/maintenance sub-areas, intermittent power failures, scattered but useful caches, and Hound encounters in dark spaces.
- Planned follow-up: a generator/breaker restoration loop for Level 1, then non-combat exploration tools such as a radio, field notes, map fragments and replacement cells. These are new implementations inspired by genre conventions; no commercial game's maps, models, audio or assets are copied.

**Keep this PR Draft. Do not merge into main.**


## Alpha 14 — Hound stalking and noise escalation

- Hounds no longer receive the player as an instant target when they spawn.
- A quiet player within stalking range is approached at a slower pace, giving the encounter a readable escalation phase.
- Sprinting, swimming, fall-flying or moving rapidly can trigger direct pursuit; close proximity also escalates the encounter.
- Creative/spectator players and players actively using the Threshold flashlight are excluded from normal target acquisition.
- The existing flashlight retreat remains a short defensive window, not a permanent Hound-disable effect.
- The protected Level 1 entry shelter, Level 0 entity restrictions, room grid, transition coordinates and all six dimension registrations remain unchanged.

**Keep this PR Draft. Do not merge into main.**


## Alpha 15 — expanded Level 0/1 horror grammar and GeckoLib acting

- Watcher: Alpha 15 added head tracking, stutter, folded posture and trailing-shroud drift clips. Alpha 18 later adds active stalking, local memory, rush/strike behavior and a bounded retreat.
- Hound: added sniff, alert, low stalking and fast retreat clips. The movement controller distinguishes idle observation, target alert, slow stalking gait, normal travel and high-speed retreat.
- Level 0: added rare pale arch-gallery and suspended false-ceiling room archetypes. The entry room, room grid, floor profile and reserved circulation network remain unchanged.
- Level 1: added freight loading bays, multi-storey parking circulation details and a rare artificial-light concrete garden. Resource footprints and reserved routes remain protected.
- QA now checks that every animation clip exists, all animated bone names exist in the matching geometry, and the new room archetypes remain present.
- Inspired by genre-level architectural motifs; no commercial game maps, models, textures, audio or prose are copied.

**Keep this PR Draft. Do not merge into main.**


### Alpha 15 refinement pass — sector identity and Hound search memory

- Level 0 gains a very rare crimson-room fragment: red wall bands and carpet bleed into an otherwise familiar office cell. The fragment is not a sealed trap; the deterministic reserved-route mask remains clear.
- Level 1 gains a rare electrical switchgear sector with transformer cabinets, copper busbars, warning lanes and breaker housings.
- The Hound now investigates a short-lived last-known player position after losing sight, searching around the location before resuming its quieter patrol. An active flashlight still cancels the search and triggers the existing retreat window.
- The additions reinforce Level 0's isolation/scopophobia and Level 1's industrial/claustrophobic atmosphere without adding new dimensions, changing transitions or expanding hostile spawns into Level 0.


## Alpha 18 — coordinated Watcher and jump-scare behavior

- The Level 0 Watcher now uses a server-authoritative state machine: OBSERVE, OBSERVED, STALK, SEARCH, RUSH, STRIKE and RETREAT.
- Four observation styles give each entity a different approach: peripheral watching, assigned flanking, movement mirroring and noise-led investigation.
- Last-known player positions are recorded only when visible. Nearby Watchers can relay the original timestamped snapshot; confidence decays over 120 ticks and is never refreshed by a relay.
- A local encounter is capped at two Watchers. The second receives the flanker role, and only one nearby Watcher may rush the same player at a time.
- A rush has a 25% near-miss variant or a single 2-damage swipe with mild knockback. It then withdraws; the scare cooldown is 240 ticks. A successful player hit also makes it flee. A held flashlight aimed at the Watcher interrupts even an active rush.
- Added GeckoLib lunge/strike clips and audio stings. No custom shader is required; the swipe uses vanilla damage feedback.
- Test/inspection commands: /threshold summon watcher (harmless preview), /threshold summon watcher_hunt (active test encounter), and /threshold watcher status (local role/state/memory telemetry).
- Static QA and dedicated-server startup markers now check the active Watcher profile. A new JAR is not considered ready until NeoForge compilation, packaging, dedicated-server smoke tests and client gameplay tests pass.


## Alpha 19 — deceptive Watcher search and sensory Hound AI

### Level 0 — Watcher
- SEARCH now sweeps three waypoints around the last timestamped sighting: left, right and the previous approach line. It pauses at each point and plays a new GeckoLib peek animation.
- After a sustained direct stare or a near-miss, a natural Watcher has a 25% chance to fake its retreat. It disappears, then uses ordinary path navigation to reposition around the last sighting before becoming visible again.
- Fake retreat is disabled for the operator test encounter and is never triggered by flashlight defense or a successful player hit.
- If the reposition path cannot be found or the observation memory expires, the Watcher is discarded. It never teleports through walls or refreshes hidden player coordinates.

### Level 1 — Hound
- Added a hearing phase: when no target or active search memory exists, the Hound can hear a nearby noisy player within 18 blocks, including through intervening walls.
- Hearing stores a deliberately uncertain position offset, not the player's exact live coordinates. The Hound investigates that clue with a bounded golden-angle search pattern; its normal target goal still requires sight or close proximity before committing to melee.
- Added a short growl when the Hound hears a noise and a dedicated GeckoLib bite animation synchronized to its attack swing.
- The flashlight now works directionally: it repels the Hound only when active, in line of sight and aimed at it. Holding it while facing away no longer grants a blanket immunity.
- The Hound checks every nearby player for an aimed beam, so a second player can still protect the group.

### Verification
- Added a dedicated-server runtime probe for the Hound's navigation and health/speed/damage/follow-range attributes.
- Smoke tests and expansion QA now require both Level 0 Watcher and Level 1 Hound runtime-readiness markers.
- Static QA checks the three-point Watcher search, rare path-based fake retreat, Hound sound memory, directional flashlight defense and both new animation clips.
- NeoForge compilation, packaged-JAR validation, dedicated-server smoke tests and in-game client behavior remain required before Alpha 19 can be called tested or ready.
