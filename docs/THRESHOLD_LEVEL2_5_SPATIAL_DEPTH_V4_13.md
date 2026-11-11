# Threshold Levels 2-5 — spatial depth pass v4.13

## Scope
A new chunk-local architecture pass extends the existing v4.8 structural mass,
v4.9 theme-specific architecture and v4.12 landmark rooms. It is inspired by
the multi-room grammar and accessible vertical circulation of Levels 0 and 1.
Levels 0 and 1 are not modified.

## What changes
- Ordinary rooms receive seed-stable offset partition lines, two openings per
  partition, short return walls and irregular L-shaped alcoves.
- Partitions are five blocks high, with level-specific base, window and top
  materials. This produces connected subrooms rather than a palette-only pass.
- Structural piers tie the floor to overhead beams. Level-specific equipment
  bays and furniture add spatial purpose: pumps and pipe service niches on
  Level 2; transformer/switchgear islands on Level 3; archive/cubicle/
  conference layouts on Level 4; guest-suite and service alcoves on Level 5.
- Selected rooms receive a two-wide stair approach to a raised service
  mezzanine, with side curbs, rails and support columns. The plan is checked
  against existing blocks before any placement.
- Seed-stable first-ring modules ensure that different room grammars can be
  encountered early, while further rooms vary deterministically by world seed.

## Preservation and safety contracts
- Level 0 and Level 1 generation branches are untouched.
- Existing Level 2-5 entry rooms and authored v4.12 landmark sectors are
  skipped by the generic subdivision pass.
- Existing floor Y=63, ceiling Y=71, containers, transit blocks and
  reserved room-exit paths are preserved.
- Every write is air-only and confined to the currently generated chunk.
- No existing blocks are cleared to make a new route; mezzanine candidates
  are rejected if their complete stair/deck/rail/support envelope is blocked.

## Validation
The new QA contract checks all four dimension branches, protected paths,
landmark exclusions, five-block partitions, supported stair/deck geometry,
chunk boundaries, air-only placement and unchanged Level 0/1 routing.
Real NeoForge compile and server-world generation are still required before
this pass can be called runtime-verified.
