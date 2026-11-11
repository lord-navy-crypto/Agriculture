# Threshold Levels 2-5 — Landmark Discovery Pass v4.12

Goal: match the **explorable vertical architecture** found on Level 0 and
Level 1 without copying their exact visual style. V4.12 adds four deliberate
landmark rooms to the existing theme variety and solid shells.

| Level | Guaranteed room index | Theme landmark | New walkable geometry |
|---|---:|---|---|
| L2 | (2,1) | Abandoned pressure-equipment hall | two-wide stone stair to Y=66 iron pump-inspection catwalk; heavy copper pump pylons and pipes |
| L3 | (1,2) | Electrical transformer gantry | two-wide eastward switchgear bridge with control room, cages, overhead bus bars and industrial fixtures |
| L4 | (1,1) | Bright records atrium | enclosed mezzanine **ring** overlooking an interior void; stone stair approach, safety rail, multi-block book stacks |
| L5 | (2,0) | Forgotten hotel guest wing | L-shaped elevated gallery, stairs and dark timber rail overlooking old red-carpet lounge and mirrored passage |

### Non-negotiable world generation invariants
- The first entry room is not changed by V4.12. All landmarks are in adjacent
  non-entry rooms, at stable indices in every seed, unlike a random rare tile.
- Preserve the established ground at Y=63, ceiling at Y=71, named dimension
  IDs, inter-level transit doors, registered containers and L0/L1 architecture.
- Y=66 decks allow two full blocks of air between the original walking surface
  and their undersides (Y=64-65), while stairs rise Y64 → Y65 → Y66.
- Materials only **fill existing air**, never overwrite placed base features,
  existing portals, lighting, chests, mob spawn rules or the food system.
- Generation is deterministic, seeded, chunk-local, and only affects **newly
  generated chunks**. Existing player worlds do not receive retroactive fixes.
- No per-tick load; just four special rooms per world with room positions
  anchored to each individual dimension.

### Validation
- The new QA gate verifies four dispatch paths, room IDs, stair connections,
  clearance, themes, materials, no overwrites and nonentry constraints.
- NeoForge compile, asset QA and dedicated server boot must pass before merge.
- Full manual client walkthrough of each new room, catwalk and room-to-room
  transit path is still necessary before declaring complete.
