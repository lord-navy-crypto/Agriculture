# THRESHOLD — Level 0–5 Environment Design Contract

The first two environments are **inspired by** both Backrooms Wikidot and Fandom, but the layout, architecture variants and custom gameplay are ours. Neither wiki version is treated as a single unified canon. We do not copy their prose, textures or sound recordings.

> **Current implementation override — Alpha 19:** Level 0's former harmless Observer has been replaced by the active but bounded Watcher. It uses four observation styles, honest short-lived sighting memory, a maximum two-entity local encounter, corner-peek search, rare path-based fake retreats, and occasional near-miss or single-swipe jump scares. Level 1's Hound can hear nearby movement as an uncertain clue, investigate with a bounded search pattern, and be repelled only by an aimed flashlight beam. Earlier Alpha 5–15 notes describing a non-combat Observer are historical design records, not the current implementation.

## Architectural boundary
The repo contains `Agriculture` and the separate `threshold` Gradle subproject, with different Mod IDs, packages, metadata and JARs. **Agriculture is now a mandatory runtime prerequisite**: its existing food, hydration, sodium and nutrition mechanisms provide survival constraints. Threshold itself does not import Agriculture Java classes or include them in its JAR. We resolve crop blocks and foods by registry ID when loaded.

## L0 — Isolation
**Primary phobia motif:** Kenophobia (fear of empty spaces). **Secondary:** Monophobia (fear of being alone). These are design motifs, not clinical diagnoses.

**Fear mechanism:** uncertainty, isolation, loss of spatial confidence.

Distinct features:
- A ceiling-covered 32x32 office grid, not an overworld structure. The entry room is always the baseline yellow-office variant, so the opening experience is consistent.
- Yellow wallpaper, damp-looking carpet and occasional stained walls.
- Infrequent column/dim/red architectural variants.
- Fluorescent panels flicker only in Level 0 via local random ticks, with no player health effect.
- Shared-edge doorway choices so neighboring rooms agree.
- A paired anomalous wall passage connects Level 0 and Level 1 in both directions; the entry room on each level guarantees one. Interaction requires an empty hand. Future: Peripheral Shift and ambient electric buzzing.

Level 0 admits only the Watcher as a natural encounter entity; generic vanilla and third-party natural mob spawning remains disabled. The Watcher is a limited predator, not a conventional mob swarm: it can stalk, flank, rush and perform one low-damage swipe before withdrawing. A local encounter is capped at two entities.

## L1 — The Vulnerable Shelter
**Fear mechanism:** relief and vulnerability; supplies exist, but are sparse and unevenly distributed.

Distinct features:
- 48x48 concrete warehouse cells, high ceilings, storage bays and short dividers.
- Some rooms are intentionally dim, other rooms provide light and a small supply cache.
- Each Level 1 room has at least one deterministic doorway; the maze can feel confining without generating sealed rooms.
- The entry room guarantees one starter cache; additional barrels use weighted real Agriculture food loot.
- The entry room guarantees a 5x5 watered planter area with a five-lamp array. Runtime smoke verifies the actual `gugugaga:barley_crop` block and lamp placement, so crops can grow despite the no-skylight dimension. Additional rooms may contain rare planters.
- Low ambient light and intermittent industrial vault crackles contrast with Level 0's cave-like ambience. Stable pools of light make the entry shelter feel temporarily safe.
- Level 1 now permits a controlled first predator: the Hound, with pathfinding and melee AI, spawned only in darker rooms after the player leaves the entry shelter. Later: additional entity archetypes after separate balance and visual tests.

## Generation invariants
- One real Minecraft dimension per level.
- One custom biome per dimension, not shared with vanilla void or other mods.
- Flat generator has a non-air support layer at Y=63, because empty flat worlds may skip features.
- Custom feature is added at the `vegetal_decoration` generation step. The earlier `surface_structures` placement never ran in a flat world.
- Feature placement is deterministic from world seed and room coordinate.
- Block writes constrained to the current generation chunk.
- Never rewrite already generated rooms upon reload.


## Alpha 5 — dimension-specific entity ecology

### Level 0: original Observer prototype (superseded by Alpha 18)
- The first Observer was a short-lived, non-combat silhouette spawned at a visible distance in Level 0.
- That prototype had no AI, target selector, attack damage, collision, push behavior or selectable hitbox.
- Alpha 18 replaces it with the active but bounded Watcher. The current natural-spawn whitelist still admits only the Watcher in Level 0; it does not blanket-reject ordinary entities joining the dimension.

### Level 1: Hound predator
- The Hound is a real Monster with a player target goal, pathfinding and melee behavior, plus explicit health, speed, attack damage and follow-range attributes.
- The entity director spawns Hounds only in Level 1, prefers darker valid floor positions, and protects the first 48×48 entry shelter from encounters. In this first pass, the Hound is the only permitted Level 1 Mob; vanilla animals/monsters and unrelated modded mobs are rejected.
- A nearby active Hound suppresses additional spawns. The Hound cannot break blocks and uses a no-drop loot table.
- Both biome JSON files retain empty generic spawner and spawn-cost maps. The entity director, rather than vanilla random spawning, owns the encounter rules.
- The Hound is the first original Backrooms-inspired predator prototype. It is not intended to imply that different Fandom/Wikidot entity lists are one unified canon.

### Runtime acceptance
- Dedicated-server startup confirms both custom entity types are registered.
- Dedicated-server startup verifies the Watcher and Hound registry entries, plus the Watcher's movement/health/attack attributes.
- The QA source gate verifies the active Watcher state machine, local two-member cap, snapshot relay, bounded swipe and the Hound's Level 1-only natural-spawn rule.
- Packaged-JAR validation verifies both GeckoLib models, animations, textures and the Hound's no-drop loot table.
- Manual client testing must confirm the Watcher stalks, freezes under direct gaze, performs occasional near-miss/single-swipe scares, responds to the flashlight and withdraws when damaged; the Hound must remain readable, avoid the starter shelter and path through existing doorways without modifying blocks.


## Alpha 6 — procedural architectural complexes

### Design principle
Complexity comes from a readable building grammar, not indiscriminate clutter. Both levels retain their validated room-cell dimensions, entry rooms, floor elevations, shared-edge doorway decisions and transition coordinates. New architecture is deterministic from world seed and room coordinates, so chunk load order does not change a room.

### Level 0 — offices that remember the wrong layout
- Distant room cells select among the established empty office, cubicle clusters, staggered partition offices, repeated column bays, suspended service ducts and mirrored/echo office arrangements.
- The first yellow office is excluded from overlays and remains the recognizable baseline.
- The overlay uses the same edge-door center calculation as the base generator. A protected L-shaped corridor joins each actual doorway to the room hub before any cubicle or partition is placed.
- Some echo rooms mirror the internal cubicle grammar or change its material rhythm while leaving the outer room and exit topology intact.
- The aim is spatial uncertainty: familiar office structures recur with small, unsettling differences, rather than adding random clutter or turning Level 0 into a conventional combat maze.

### Level 1 — a functioning industrial complex
- Distant warehouse cells can become pallet-rack aisles, copper service galleries, loading platforms, static machine bays, overhead gantries or mirrored rack layouts.
- Rack beams, machine islands and loading platforms are placed outside the protected circulation network and Agriculture resource footprints.
- Overhead pipework and gantries add vertical scale without reducing normal floor clearance. Gantry supports also respect reserved paths.
- The starter shelter, greenhouse, supply caches, return passage and warehouse floor plan remain unchanged.
- Structures are static block assemblies. This pass adds no ticking machinery, redstone logic or additional block entities.

### Alpha 6 acceptance
- Source validation confirms both overlay passes are called after base room construction.
- Deterministic archetype selection and shared doorway-center calculations are present.
- The two entry rooms remain excluded from overlays.
- The protected corridor function is shared by the room overlay system.
- CI must compile the independent mod and run the real dedicated-server smoke test before Alpha 6 is considered playable.
- Manual client testing must inspect each archetype, verify routes and resource areas remain accessible, and check that overhead structures do not clip the player camera or Hound navigation.


## Alpha 9 — six-level source-backed transit design

### Level 2 — Abandoned Utility Halls
- Level 2 is a full playable dimension with 32×32 room cells, dense concrete service corridors, copper mains, pipe galleries, cable runs, maintenance trenches, valve bays and damp sump edges.
- The room graph is deterministic and shared-edge. Each cell must retain at least one valid doorway; the route graph test now checks both Level 1 and Level 2.
- Level 2 has fixed service routes plus multiple anomalous branch doors. It is a real exploration space, not merely a loading screen or teleport chamber.

### Level 5 — Terror Hotel
- Level 5 is a separate 32×32 deterministic hotel-room grid with three guaranteed accessible zones: Main Hall, Beverly Room and Boiler Room.
- Main Hall uses dark timber flooring, red-and-gold wall details, carpet runners, antique-style furniture clusters and scarce Agriculture supply caches.
- Beverly Room is a ballroom hub with a central table, chandelier and several exits that rejoin the Main Hall / Boiler Room room graph.
- Boiler Room uses dark stone, copper mains, chains, machinery props and shallow leak channels. Its deeper descent remains catalogued but is not activated until Level 6 exists.
- Additional cells may become guest suites, compressed hotel corridors or a rare unconfirmed Lobby Room.
- The hotel elevator panel supports floors 1–12: sneak-use cycles the selected floor and normal use travels to the matching wing within Level 5.
- Level 5 receives a quiet, irregular vanilla piano-note ambience cue. It does not replace or alter the original Level 0 / Level 1 OGG tracks.

### Level 0 / Level 1 preservation
- Level 0's original yellow-office layout, entry room, bounded Watcher encounter budget, flickering lights, music and Level 0 ↔ Level 1 transition remain unchanged.
- Level 1's warehouse layout, starter shelter, Agriculture planter and supplies, Hound restrictions, lighting and industrial music remain unchanged.
- The earlier direct Level 1 → Level 3 lift is removed. A seamless trigger zone is placed inside a dedicated warehouse corridor; pipes and service fittings become denser before the environment transitions into Level 2.

### Source-backed route graph
- Level 0 → Level 1: the original unstable-wall passage is preserved.
- Level 1 → Level 2: smooth environmental transition with gradually increasing pipes and service fittings, matching the Wikidot description.
- Level 2 → Level 1 / Level 3: unlocked doors and fire exits. The Fandom fire-exit variant can resolve to either Level 1 or Level 3.
- Level 2 → Level 1 / Level 3 / Level 4: unmarked branch doors model the Wikidot version's anomalous doors to the common adjacent levels.
- Level 3 → Level 2: retracing the service corridor is retained as a practical return route for gameplay; this is not represented as a newly documented universal canon exit.
- Level 3 → Level 4: the metallic Office Sector door is based on the Fandom Level 4 entrance description.
- Level 4 → Level 3: a service elevator return is retained as an optional Wikidot/archived-version route variant. Current rewritten pages may differ.
- Level 3 → Level 4 / Level 5: a seed-stable service-elevator draw. Relative weights 9:1 are gameplay tuning, not claimed canon probabilities.
- Level 4 → Level 5: a climbable old-fashioned stairway.
- Level 5 → Level 3 / Level 4: fixed hotel elevator and stairwell returns.
- Level 5 → Level 5: the player selects one of twelve internal hotel wings with the elevator panel.

### Random destination implementation
- Wiki pages commonly describe exits as random, occasional, rare or dependent on a particular door/area, but do not generally publish exact numeric probabilities.
- Relative weights in the mod are gameplay tuning, not canon percentages. The current Level 2 branch uses 4:4:1 relative weights for Level 1, Level 3 and Level 4. The Level 2 fire exit uses equal relative weights for Level 1 and Level 3.
- A door's result is calculated from the world seed and its coordinates. It remains stable after reload, while different doors can resolve to different destinations. This prevents repeated interaction from rerolling the same door.
- Routes to unimplemented destinations (for example Level 6, Level 9, Level 14, Level 27 and other distant levels) are recorded as future candidates and are not redirected to an unrelated existing dimension.

### Source profiles and disagreements
- Wikidot and Fandom remain separate source profiles. The mod combines compatible routes, but records conflicting descriptions as version-specific variants rather than pretending that every page describes one canon.
- Wikidot Level 1 documents a smooth transition into Level 2. Wikidot Level 2 documents anomalous exits to Levels 1, 3 and 4. Wikidot Level 5 documents the hotel zones, Level 3 elevator, Level 4 stairway, return routes and internal elevator floors.
- Fandom Level 2 describes fire exits to Levels 1 or 3 and certain hallways that may lead to Level 4 or Level 27. Fandom Level 3 describes elevators that commonly lead to Level 4 and may rarely reach Level 5. Fandom Level 4 describes the metal Office Sector entrance from Level 3.
- The Fandom Level 3 page currently carries a rewrite notice; the Wikidot Level 4 page also carries a rewrite notice. Their current pages take priority over archived material, and disputed routes remain configurable.
- Only the documented ordinary-level route graph is implemented; no separate mirrored level family is generated.

### Source pages
- Wikidot: https://backrooms-wiki.wikidot.com/level-0
- Wikidot: https://backrooms-wiki.wikidot.com/level-1
- Wikidot: https://backrooms-wiki.wikidot.com/level-2
- Wikidot: https://backrooms-wiki.wikidot.com/level-3
- Wikidot: https://backrooms-wiki.wikidot.com/level-4
- Wikidot: https://backrooms-wiki.wikidot.com/level-5
- Fandom: https://backrooms.fandom.com/wiki/Level_0
- Fandom: https://backrooms.fandom.com/wiki/Level_1
- Fandom: https://backrooms.fandom.com/wiki/Level_2
- Fandom: https://backrooms.fandom.com/wiki/Level_3
- Fandom: https://backrooms.fandom.com/wiki/Level_4
- Fandom: https://backrooms.fandom.com/wiki/Level_5

## Acceptance gates
- Java compile and asset validation.
- Separate `threshold` JAR, loading alongside required Agriculture.
- Dedicated server registers all six dimensions and confirms the Level 0 carpet, Level 1 warehouse, Level 2 utility halls, Level 3 electrical station, Level 4 office, Level 5 Main Hall / Beverly Room / Boiler Room, hotel elevator and every implemented route actually generated.
- No console worldgen errors, missing models or missing loot IDs.
- Manual client test for room visibility, lighting and resource availability before marking release ready.

## Floor contact and survival regression (Alpha 3)
- All Level 0 structural blocks use full block footing at Y=64; carpet is only used on traversable floor and door openings. This is a geometry correction, not a "carpet under walls" texture workaround.
- `/threshold repair_floor` repairs only legacy wall columns over old carpet in a bounded five-by-five loaded chunk area in Level 0; it is never run automatically.
- Level 0 isolated rooms may rarely contain forgotten supplies, but no reliable food source.
- The Level 1 starting cache is deterministic for water and diverse Agriculture foods; later supply and maintenance caches have different water safety and nutrition trade-offs.
- No extra needs system is created: all foods and water are actual Agriculture items, so their registered profiles continue to determine water, sodium and other nutrients.
- Level 1 gains subtle stone floor wear, suspended iron fittings and longer ceiling lamps. Later Alpha 5 adds the controlled Hound only to Level 1; Level 0 remains observation-only.

## Alpha 3 validation contract
- Level 0 wall base (0,64,5) and transition footing (0,64,15) must be solid and flush with the blocks above; walkable carpet (4,64,4) remains.
- Operator /threshold repair_floor repairs only a limited number of loaded existing Level 0 chunks and does not regenerate the saved world.
- Loot item IDs are checked against actual Agriculture registrations and again against the live built-in item registry.
- Nutrition, water, and sodium handling remain entirely owned by Agriculture.


## Horror identity is a non-regression contract (Alpha 4 onward)

**Survival Horror is an additive gameplay layer. It must never replace the original horror identity.** The environments, architecture, routes, atmosphere and visual language established before the survival integration remain the design baseline.

### Level 0 — preserve isolation and spatial uncertainty

- Keep the 32×32 yellow-office room scale, the Y=63 concrete foundation, Y=64 walkable carpet, Y=65–71 wall profile and Y=72 ceiling.
- Keep the entry room as the recognizable yellow-office baseline: faded yellow wallpaper, carpet, fluorescent panels and a large amount of deliberately empty space.
- Preserve the existing shared-edge doorway algorithm, rare closed boundaries, divider/column variants, rare red rooms and the established Level 0 ↔ Level 1 transition location.
- Keep the cave-like ambient sound bed and brief, irregular fluorescent-light failures.
- Agriculture supplies remain rare and unreliable here. Do not turn Level 0 into a farm, pantry network or decorated safe hub.

### Level 1 — preserve the vulnerable industrial shelter

- Keep the 48×48 warehouse room scale, concrete floor, industrial wall profile, high ceiling, storage partitions and established return-passage location.
- Preserve the distinct vault-like industrial ambience, overhead ventilation unit and contrast between stable pools of light and longer lighting faults.
- Keep the entrance shelter useful but visibly limited. The six-crop planter and guaranteed starter cache are localized features, not a reason to fill every room with crops, lights or supplies.
- Preserve deterministic room exits. Resource scarcity must not create a generation-time sealed room or remove the existing return route.

### Cross-level rules

- Do not resize rooms, move established corridors or transitions, change dimension identifiers, or regenerate already explored rooms as part of a survival-balance pass.
- Do not homogenize the two light profiles, ambient sound beds, palettes, architecture or resource distribution.
- Do not fill empty areas with decorative clutter merely to make the spaces feel more alive. Empty space is part of Level 0's horror language.
- Never add hostile or attacking entities to Level 0; its Observer remains purely atmospheric. Level 1 may contain controlled predators, but they must not replace its industrial atmosphere or alter the validated room graph.
- Agriculture remains the sole owner of food, nutrition, hydration and sodium mechanics. Threshold may place and distribute real Agriculture content, but must not create parallel needs systems.
- New atmosphere should be additive and restrained: it may deepen the existing theme, but must not obscure routes, change collision, or compromise the validated structure.

### Automated preservation gate

The qa/validate_threshold_horror_preservation.py script protects the room dimensions and vertical profiles, entry-room identities, transition locations, light-failure contrast, biome ambience, palette/architecture markers and Level 0 no-hostile-entity / Level 1 controlled-predator scope. It runs in CI alongside the existing room-graph, Agriculture integration, packaged-JAR and dedicated-server checks.

A deliberate change to one of these invariants requires an explicit design decision and a corresponding update to this contract and its tests. It must not happen accidentally as a side effect of adding survival features.


## Alpha 15 — richer acting and room grammar

### Level 0
- Adds a rare pale arch-gallery landmark and a suspended false-ceiling anomaly to the existing deterministic room archetypes.
- The entry room remains the untouched yellow-office baseline.
- All new structures use the existing reserved-route mask; no doorway, return passage, floor height or ceiling profile is moved.
- The Observer remains non-hostile, non-colliding and transient. Its GeckoLib presence controller now alternates long stillness with short head-tracking, stutter, folded-posture and shroud-drift gestures.

### Level 1
- Adds freight loading bays, parking circulation details and a rare concrete-garden sub-area under artificial lighting.
- The new layouts remain inside the existing 48×48 room cell and respect both reserved paths and resource footprints.
- The Hound gains sniff, alert, stalking-gait and retreat animations, selected from its current movement/target state. It remains restricted to Level 1 and cannot break blocks.

### Alpha 15 acceptance
- JSON parsing and geometry/animation bone-reference checks pass for Watcher and Hound.
- QA confirms the new Level 0 and Level 1 archetypes, original entry-room identities, route protections, entity restrictions and transition coordinates.
- NeoForge 1.21.1 compile, packaged-JAR validation and dedicated-server smoke tests must pass before this build is considered ready for in-game testing.

### Research inspiration
- Level 0 architectural motifs: arches, pillars, blackout areas, red rooms and unstable layouts, cross-checked against the Backrooms Wikidot Level 0 description and Escape the Backrooms Level 0.
- Level 1 architectural motifs: warehouse, parking deck, loading/transport infrastructure and rare overgrown concrete sub-areas, cross-checked against Backrooms VR's Level 1 archive and community-made Minecraft Backrooms layouts.
- All structures are original deterministic implementations; no external map, model, texture, sound recording or prose is imported.


## Alpha 15 refinement — crimson bleed, switchgear and search memory

### Level 0: the Lobby
- Added a 1% crimson-room fragment archetype: red wall bands, red carpet patches and dark ceiling accents.
- The fragment remains traversable. It is a visual and spatial warning, not a sealed room or damage mechanic.
- The classic yellow-office empty-room majority is preserved, and the entry room is untouched.
- Design focus: kenophobia (large empty spaces), isolation and scopophobia (the Observer's peripheral presence).

### Level 1: the Habitable Zone
- Added a rare electrical switchgear archetype with transformer cabinets, copper busbars, yellow warning lanes and breaker housings.
- Existing warehouse, parking, loading, maintenance and garden sectors remain the dominant architectural language.
- Design focus: mechanophobia, claustrophobia and uncertainty inside a working-looking but abandoned industrial system.

### Hound behavior
- When a player breaks line of sight, the Hound retains the last known position for a bounded six-second search window.
- It moves to that position and searches a small surrounding area instead of immediately idling.
- Flashlight repulsion clears the stored position and takes priority over investigation.
- This remains bounded ordinary pathfinding: no block breaking, no changes to Level 0 entity restrictions and no persistent player tracking.

### Alpha 15 refinement acceptance
- QA checks the new archetypes inside the correct level-specific generator methods.
- QA checks the Hound's bounded search memory and flashlight cancellation.
- Existing route reservations, entry shelters, dimensions, transition coordinates and room vertical profiles remain protected.


## Alpha 15.3 — vertical maze grammar and environmental layering

### Level 0 — the Lobby

- Preserve the 32×32 room-cell graph and the existing entry room. The new work changes the internal grammar of selected rooms, not the dimension's base grid or established transition.
- Add a micro-maze archetype: faded-wallpaper partitions divide selected rooms into smaller office cells. The reserved-route mask and secondary gaps preserve multiple ways through the room while allowing short dead ends and misleading loops.
- Add a guaranteed first-ring sub-floor annex in room cell (1, 0). Its lower yellow chamber is at Y=58, with a stair connection from the ordinary floor and a restrained flickering ceiling panel.
- Add upper galleries in selected cells at Y=68. A stair flight connects each gallery to the ground floor. Shared-edge decisions only open an elevated connection when both adjacent cells have galleries and the ordinary room graph already has a doorway.
- Use the original Faded Wallpaper, Flickering Panel and Ceiling Vent blocks for the new spaces. Keep the original yellow office palette dominant; the new vertical routes should feel like improbable continuations of the same building.

### Level 1 — the Habitable Zone

- Preserve the 48×48 room-cell graph, the entrance shelter, the starter supplies and all established transitions.
- Add a warehouse micro-maze archetype: tall Warehouse Wall partitions create smaller storage cells and narrow service aisles. Existing reserved routes remain open, with deterministic secondary gaps for loops and side rooms.
- Add a guaranteed maintenance sector in room cell (1, 1). Its sunken service channel has a lower floor at Y=58, retaining walls, a stair entrance, overhead pipework and a ceiling vent.
- Add elevated industrial catwalks at Y=70 in selected cells. Stairs connect them to the ground-floor circulation; neighbouring catwalks connect only across existing doorways and matching shared-edge decisions.
- Use the original Warehouse Wall and Ceiling Vent blocks alongside concrete, iron, copper and warning-lane details. The industrial silhouette must remain distinct from Level 0.

### Vertical connectivity contract

- The established ground-floor room graph remains the guaranteed route network.
- Every new upper gallery or lower annex has a physical stair connection to the ground floor; no decorative platform is allowed to become an unreachable island.
- Elevated cross-room bridges are deterministic and symmetric across chunk boundaries. A bridge is generated on both sides of a shared edge or on neither side.
- Existing dimension identifiers, room-cell dimensions, entry-room identities and transition coordinates remain unchanged.
- Dedicated-server runtime checks explicitly inspect the Level 0 stair/gallery/sub-floor landmarks and Level 1 stair/catwalk/maintenance-trench landmarks.

### Ambient audio follow-up

- The biome music events are now scheduled with a very short inter-track delay so the existing original tracks do not leave long silent gaps.
- The current OGG assets are still short clips (approximately six seconds for Level 0 and four seconds for Level 1). This is an identified audio-quality debt, not a completed long-form soundtrack. Replace them with longer original compositions before calling the music pass finished.


## Alpha 16 — six additional landmark structures

### Level 0 — the Lobby
- **Pillar Forest:** an irregular grid of seed-stable columns with varied heights and cross-beams. The room keeps open floor-level circulation and does not inherit the generic upper-gallery network.
- **Floor-Fold Field:** three offset shallow floor recesses with contrasting surfaces, guarded rims and stair cells. The pockets are one block below the ordinary floor, not lethal shafts.
- **Stair Tower:** a compact enclosed tower with a four-step climb, raised landing, glazed guardrails and internal lighting. The stair run and entrance are placed outside the reserved route corridor so the access is seed-independent.

### Level 1 — the Habitable Zone
- **Gothic Arcade:** repeated polished-andesite piers, high stone arch spans and overhead lighting.
- **Cross-Dock Crane Hall:** overhead tracks, cross-dock bridge, chain details and a loading-control booth.
- **Ventilation Exchange:** large copper ducts, cross-ducts, inspection stacks and a service bay.
- These landmark rooms own their vertical/overhead clearance. The generic catwalk network is suppressed in those cells to prevent collisions between independent structures.

### Alpha 16 distribution and acceptance
- All six landmarks have guaranteed discovery coordinates in every world seed, with rare weighted variants in ordinary room cells.
- Level 0 remains mostly empty yellow-office space; Level 1 remains warehouse/industrial space.
- Runtime probes inspect each landmark, including the stairs and lower floors. The dedicated-server smoke test requires explicit success and failure markers for all six.

## Alpha 17 — localized blackout behavior and generator-plant sector

### Level 0 — Blackout Corridor
- Adds a rare room with offset dark partitions, deterministic door gaps, black ceiling panels, dark floor patches and hanging cable details.
- Four ceiling panels begin unlit. A black-concrete ceiling marker gives those panels a local light profile: they fail more often and recover more slowly than ordinary Level 0 panels.
- The room remains traversable. The lighting fault is an environmental anomaly, not a permanent sealed darkness mechanic.

### Level 1 — Generator Plant
- Adds four static engine assemblies, furnace cores, copper busbars, exhaust stacks, breaker-indicator lamps and warning lanes.
- The generator/breaker installation is architectural in this pass. It is not yet an interactive power-restoration system; that is a separate gameplay phase.
- The generator sector owns its overhead clearance and preserves the existing reserved routes and resource footprints.

### Alpha 17 acceptance
- Static QA checks the new archetypes, guaranteed room cells, blackout light-state logic and generator assembly blocks.
- Dedicated-server runtime probes verify the unlit Level 0 panels and Level 1 generator cores.
- NeoForge compilation, packaged-JAR validation and dedicated-server smoke tests remain mandatory before a new build can be called ready. Source checks alone are not a substitute for those tests.


## Alpha 18 — coordinated Level 0 Watcher and jump-scare behavior

### Behavioral architecture

The Watcher is now an active, bounded encounter entity rather than a harmless apparition. Its server-authoritative state machine contains:
- **OBSERVE / OBSERVED:** watches from the periphery, tracks the player's head direction and freezes during direct eye contact. A sustained stare causes it to fold away.
- **STALK:** moves according to one of four stable observation styles: PERIPHERAL, FLANKER, MIRROR or LISTENER. Sprinting, swimming, jumping and fast movement contribute short-lived noise evidence.
- **SEARCH:** when line of sight is broken, it searches only a recent last-known observation. It does not read a hidden live player position through walls.
- **RUSH / STRIKE:** a short high-speed approach can become either a near-miss pass or one single low-damage swipe. The swipe deals 2 damage, applies mild knockback and has a 12-second cooldown.
- **RETREAT:** after a scare, a sustained direct sighting or a successful player hit, the Watcher withdraws and disappears. It does not chain attacks or stay in a prolonged melee.

A held flashlight aimed toward the Watcher interrupts the encounter. Natural Watchers have 14 health, normal collision and path navigation, and can be attacked. The harmless /threshold summon watcher preview remains available; /threshold summon watcher_hunt spawns an active test encounter.

### Local hive-inspired coordination

This design borrows the engineering principles from the separate SWARM-MOBS project without importing its code or making the Watcher an unrestricted swarm:
- A local encounter is capped at two Watchers.
- The second member receives a stable flanker role.
- Nearby peers within 48 blocks can relay an immutable last-known snapshot for the same player.
- Relaying preserves the original observation timestamp; confidence decays over a 120-tick memory horizon.
- Only one peer may rush the same player at a time.
- Position observations are refreshed only while the player is actually visible. A short, capped velocity extrapolation is used only while searching.

The natural spawn director attempts a new encounter every 300 ticks. A first Watcher is more likely than a second; the 600-tick natural lifetime and local two-member cap bound encounter density.

### Presentation and inspection

- Added GeckoLib lunge and strike clips using the existing Watcher geometry.
- The rush uses a Phantom swoop audio cue; the strike uses an Enderman scream sting. Vanilla hurt feedback provides the brief impact flash and camera response.
- /threshold watcher status reports each nearby Watcher's observation style, state, memory confidence and memory age.
- Use /threshold summon watcher_hunt for the active test encounter.

### Alpha 18 acceptance

- Static QA checks observation styles, confidence decay, local snapshot relay, attack cooldown, damage bounds, flashlight interruption and animation assets.
- Server startup checks that the Watcher can be instantiated with navigation, health, movement and attack attributes.
- The dedicated-server smoke test requires THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_OK.
- NeoForge compilation, packaged-JAR validation and real client gameplay checks remain mandatory. The source update alone is not a claim that a new JAR has been built or tested.


## Alpha 19 — deceptive Watcher search and sensory Hound AI

### Level 0 — corner-peek search
- When sight is broken, the Watcher sweeps three waypoints around the immutable last-known snapshot: one to each side and one along the previous approach line.
- It pauses at each waypoint, stops its navigation and uses the new GeckoLib peek clip to suggest that it is checking around a corner.
- After a sustained direct stare or a near-miss, a natural Watcher has a 25% chance to fake its retreat. It becomes invisible and pathfinds to a lateral position derived from the last sighting; it reappears only after reaching that destination.
- Fake retreat is disabled for the active operator test encounter and is not allowed after flashlight defense or a successful player hit. A failed path or expired memory discards the entity rather than teleporting it through walls.

### Level 1 — hearing and methodical search
- When it has no target and no active search memory, the Hound listens for nearby noisy players. It selects the nearest audible noisy player rather than blindly checking only the nearest player. Sprinting and fall-flying carry farther than swimming, jumping or ordinary movement; a blocked line of sight reduces the hearing radius by 45%, while still allowing nearby sound through walls.
- The heard position is deliberately offset by a small random uncertainty. This is an auditory clue, not omniscient tracking; the Hound investigates the estimate and must regain sight, then noisy movement or close proximity must justify its normal target goal before it commits to melee.
- Investigation waypoints follow a bounded golden-angle sweep around the last-known point. The Hound gives a restrained growl on hearing a noise and uses a dedicated GeckoLib bite animation when its melee swing occurs.
- Flashlight defense is directional: the item must be active, the Hound must be in line of sight, and the player's look vector must point toward it. All nearby players are checked so one player's beam can protect another.

### Alpha 19 acceptance
- Server startup verifies the Watcher's and Hound's navigation and core attributes.
- Dedicated-server smoke tests require THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_OK and THRESHOLD_LEVEL1_HOUND_ADVANCED_AI_READY_OK.
- Static QA verifies the Watcher's three-point search and fake-retreat path constraints, plus Hound noisy-player selection, movement-sensitive sound radius, wall attenuation, uncertain memory, directional flashlight defense and GeckoLib animation assets.
- Compile, packaged-JAR validation, server smoke and client gameplay checks remain mandatory. Source changes alone do not constitute a tested JAR.
