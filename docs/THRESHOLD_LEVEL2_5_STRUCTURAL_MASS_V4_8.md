# Backroom Agriculture — Level 2–5 solid-shell world generation (v4.8)

This pass focuses on the structural depth missing from Levels 2–5, without
rewriting their existing room generators or changing the 27-nutrient system.

## Generated geometry
- All four levels retain their established `Y=63` walkable floors,
  `Y=64..70` rooms, `Y=71` ceilings, room sizes, exits and level IDs.
- New solid foundation at **Y=58..62** (five layers) and solid roof
  backing at **Y=72..75** (four layers), added in the same chunk feature pass.
- Sparse corner structural piers appear in non-entry rooms only, without
  overwriting pre-existing decoration, chests, pipes, fixtures or doors.
- Level 2: heavy concrete/deepslate utility tunnels and embedded iron/copper ribs.
- Level 3: deepslate, brick and metal electrical-station substrate.
- Level 4: bright office concrete/stone shell with polished-diorite supports.
- Level 5: dark hotel timber over masonry, with separate stone boiler-room
  treatment and gold-framed Beverly-room roof elements.
- The generator writes inside its own 16×16 chunk footprint and makes no
  per-tick changes. It does not generate a new playable upper floor.

## Compatibility and testing
- No alteration to Levels 0/1, existing room topology, transition blocks,
  registries, food system, saved nutrients, or dimension IDs.
- Changes apply to **newly generated chunks**; existing explored rooms are
  not retroactively rebuilt.
- QA checks Level 2–5 only. Full CI must compile the unified NeoForge JAR,
  validate assets and boot a real server. Client visual checks of ceiling,
  floor solidity and travel routes remain recommended before merge.
