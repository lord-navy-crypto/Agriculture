# Backroom Agriculture — Level 2–5 themed architecture (v4.9)

The v4.8 underfloor and roof shells address only structural thickness.
The v4.9 pass adds visible room-specific architecture inspired by the
existing Level 0 / Level 1 design grammar, but keeps all levels visually
distinct. It runs after the existing room and solid-shell placement.

## Distinct themes
- **Level 2 — abandoned utility halls:** suspended copper mains,
  industrial steel arches and columns, pipe-frame niches, inspection
  grilles and some accessible raised maintenance catwalks.
- **Level 3 — electrical station:** transformer banks, cable trays,
  iron cages, copper switchgear, dim emergency-lamp accents and
  tall deepslate-brick structural columns.
- **Level 4 — office complex:** pale false-ceiling grids, illuminated
  ceiling inserts, repeating cubicle clusters, archives, glass meeting
  booths, sparse indoor greenery and deserted copy-room props.
  Empty space remains part of this level's design.
- **Level 5 — Terror Hotel:** dark oak/coffered roofs, gold-and-red
  interior ornament, guest-side galleries, dark pane partitions;
  Beverly Room keeps its central chandelier and gains a side balcony
  with steps; Boiler Room gains additional utility banks and pipes.

## Important invariants
- Original room sizes: L2/L3/L5 = 32×32, L4 = 48×48.
- Original Y=63 floors, Y=71 ceilings, Level 0/1 worlds,
  exits, elevator controls, loot, old agriculture/nutrition values stay intact.
- Every room still has its established doorway and central walking cross.
  Decorative pieces only fill existing AIR and avoid the perimeter five
  blocks and central cross for ground-level architecture.
- The entry rooms gain overhead / edge details without new central
  ground obstacles. The first hotel entry elevator stays untouched.
- Deterministic, seed-stable chunk-local writes; changes show only in
  newly generated chunks, not older explored/saved rooms.
- Mod does not regenerate, force-load or replace existing terrain.

## Acceptance
The added static gate validates room dispatch, isolated palettes and
path-preserving write rules. NeoForge build, asset gates and live server
world-generation tests must pass. Visual client acceptance, actual
pathfinding and balcony traversability must still be checked by playing.
