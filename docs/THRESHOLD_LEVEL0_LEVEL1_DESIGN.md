# THRESHOLD — Level 0 and 1 Environment Design Contract

The first two environments are **inspired by** both Backrooms Wikidot and Fandom, but the layout, architecture variants and custom gameplay are ours. Neither wiki version is treated as a single unified canon. We do not copy their prose, textures or sound recordings.

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

**No ordinary combat mobs** added at this stage.

## L1 — The Vulnerable Shelter
**Fear mechanism:** relief and vulnerability; supplies exist, but are sparse and unevenly distributed.

Distinct features:
- 48x48 concrete warehouse cells, high ceilings, storage bays and short dividers.
- Some rooms are intentionally dim, other rooms provide light and a small supply cache.
- Each Level 1 room has at least one deterministic doorway; the maze can feel confining without generating sealed rooms.
- The entry room guarantees one starter cache; additional barrels use weighted real Agriculture food loot.
- The entry room guarantees a 5x5 watered planter area with a five-lamp array. Runtime smoke verifies the actual `gugugaga:barley_crop` block and lamp placement, so crops can grow despite the no-skylight dimension. Additional rooms may contain rare planters.
- Low ambient light and intermittent industrial vault crackles contrast with Level 0's cave-like ambience. Stable pools of light make the entry shelter feel temporarily safe.
- No ordinary enemy AI. Later: stable corridors, resource camps, erratic lights and route planning.

## Generation invariants
- One real Minecraft dimension per level.
- One custom biome per dimension, not shared with vanilla void or other mods.
- Flat generator has a non-air support layer at Y=63, because empty flat worlds may skip features.
- Custom feature is added at the `vegetal_decoration` generation step. The earlier `surface_structures` placement never ran in a flat world.
- Feature placement is deterministic from world seed and room coordinate.
- Block writes constrained to the current generation chunk.
- Never rewrite already generated rooms upon reload.

## Acceptance gates
- Java compile and asset validation.
- Separate `threshold` JAR, loading alongside required Agriculture.
- Dedicated server registers two dimensions and confirms Level 0 carpet, Level 1 floor, both transition walls, the starter cache and a lit planter actually generated.
- No console worldgen errors, missing models or missing loot IDs.
- Manual client test for room visibility, lighting and resource availability before marking release ready.

## Floor contact and survival regression (Alpha 3)
- All Level 0 structural blocks use full block footing at Y=64; carpet is only used on traversable floor and door openings. This is a geometry correction, not a "carpet under walls" texture workaround.
- `/threshold repair_floor` repairs only legacy wall columns over old carpet in a bounded five-by-five loaded chunk area in Level 0; it is never run automatically.
- Level 0 isolated rooms may rarely contain forgotten supplies, but no reliable food source.
- The Level 1 starting cache is deterministic for water and diverse Agriculture foods; later supply and maintenance caches have different water safety and nutrition trade-offs.
- No extra needs system is created: all foods and water are actual Agriculture items, so their registered profiles continue to determine water, sodium and other nutrients.
- Level 1 gains subtle stone floor wear, suspended iron fittings and longer ceiling lamps. No monsters or AI.

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
- Do not add mobs, combat, AI hunters or jump-scare events as a shortcut to horror in this phase.
- Agriculture remains the sole owner of food, nutrition, hydration and sodium mechanics. Threshold may place and distribute real Agriculture content, but must not create parallel needs systems.
- New atmosphere should be additive and restrained: it may deepen the existing theme, but must not obscure routes, change collision, or compromise the validated structure.

### Automated preservation gate

The qa/validate_threshold_horror_preservation.py script protects the room dimensions and vertical profiles, entry-room identities, transition locations, light-failure contrast, biome ambience, palette/architecture markers and no-mob scope. It runs in CI alongside the existing room-graph, Agriculture integration, packaged-JAR and dedicated-server checks.

A deliberate change to one of these invariants requires an explicit design decision and a corresponding update to this contract and its tests. It must not happen accidentally as a side effect of adding survival features.
