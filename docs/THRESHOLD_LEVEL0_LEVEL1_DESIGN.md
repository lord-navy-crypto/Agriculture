# Threshold — Level 0 and Level 1 Design Contract

## Project boundary
Threshold is an independent NeoForge mod subproject in this repository. It has Mod ID `threshold`, its own Java package, resources, metadata, and JAR. It must not import Agriculture implementation classes. Optional Agriculture integration will be a separate compatibility module in a later phase.

## Dimension contract
Each Backrooms level is a separate Minecraft dimension:

- `threshold:level_0` — Level 0: Isolation
- `threshold:level_1` — Level 1: The Vulnerable Shelter

Both dimensions use independent dimension-type definitions and the void biome as a blank world-generation canvas. A registered deterministic feature builds architecture chunk-by-chunk. No generated block writes outside the current chunk.

## Level 0 — Isolation
Theme: isolation, uncertainty, spatial disorientation.

- 32x32-block room cells, aligned to two chunks.
- Yellow wall palette, yellow carpet, pale ceiling, flush light panels.
- Shared deterministic door decisions on every room boundary.
- Rare pillar rooms, dim rooms, red rooms and partial interior partitions.
- No ordinary hostile entity is introduced in this foundation.

## Level 1 — The Vulnerable Shelter
Theme: survival uncertainty and fragile shelter.

- 48x48-block room cells, aligned to three chunks.
- Concrete warehouse floor, tall concrete walls and ceiling.
- Wide shared-boundary doorways.
- Open halls, storage bays, divided halls and dim halls.
- Decorative storage barrels; actual loot and resource systems are deferred.

## Validation contract
A dedicated-server startup check must find both dimensions and generate chunk (0, 0) in each. It verifies the Level 0 concrete floor and yellow carpet, plus the Level 1 smooth-stone floor. Commands are operator-only test entry points until physical transition structures are implemented.
