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
