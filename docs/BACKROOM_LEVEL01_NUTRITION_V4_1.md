# Backroom Agriculture — Level 0 / Level 1 and nutrition v4.1

## Reference direction and original interpretation

These are **worldbuilding references**, not permission to copy third-party game assets or copyrighted maps.

- Level 0: https://backrooms-wiki.wikidot.com/level-0 — monotonous yellow architecture, random segmentation, arches, short passages and rare anomalous areas.
- Level 1: https://backrooms-wiki.wikidot.com/level-1 — distinct hall and corridor sectors, isolated small rooms, resources and more diverse industrial spaces.
- Original-style photograph and wider history: https://www.wired.com/story/what-are-the-backrooms/

Backroom Architecture should not equal a uniform 32x32 or 48x48 grid, nor
should random adjacency cause every neighboring cell to become one enormous
empty warehouse. Classic Level 0 has small rooms and long walls, narrow bends,
occasional broad openings, dead ends, and rare large halls. Level 1 contains
industrial corridors, columns, utility pockets and warehouse bays, with larger
clearances appearing locally rather than constantly.

## Generator policy

The old 32/48 lattice is retained for authored landmarks, stair access and
saved-world continuity. The new variable-spacing cell layout governs ordinary
wall/doorway placement. Both are deterministic from the world seed.

| Rule | Level 0 | Level 1 |
| --- | --- | --- |
| Cell spacing | 22 with ±6 jitter | 28 with ±7 jitter |
| Optional edge opening rate | 54% | 46% |
| Entire shared wall removed | 1% | 4% |
| Opening widths | mostly 1–4, rare 5/7/9 | mostly 2–5, rare 6/8/10 |
| Wall tops | usually touch ceiling y=72 | usually touch ceiling y=74 |
| Material | mostly yellow/faded paper | concrete and industrial warehouse blocks |

Guaranteed path spines remain at least two blocks wide; typical architecture
and furniture may not block them. Authored portal walls, starter caches and
industrial mezzanines remain protected. A deterministic static check is not the
same as full client walkthrough and must not be represented as such.

## Gameplay nutrition integrity

- Keep all 404 catalog resource IDs, 27 ordered indicators and existing NBT
  keys for saved players. The water and sodium entries remain indicators in
  the panel, but the **water/salt system owns their actual gameplay updates**.
- Food template groups supply default **abstract game points**. Twenty-four
  formerly generic items are reclassified to appropriate food groups; forty
  selected items now have per-food overrides without changing registered IDs.
- The hover tooltip shows the three strongest nonzero contributions to the
  25 individually tracked nutrient reserves; holding Shift expands the list.
  It separately prints water and salt from WaterSaltProfilesV37, exactly the
  same table used during the immediate use-item event.
- Nutrition reserves update only when the common Overworld game day advances.
  The clock no longer changes merely because the player enters Level 0/1.
- These are game abstractions, not dietary recommendations, medical advice or
  nutritional measurements. Avoid real-world units and diagnostic language.

## Outstanding release criteria

Run static audits, compiled JAR validation, live NeoForge server with all
six dimensions, original-world migration and a client exploration pass. Verify
wall contact, width distribution, no sealed sectors, nonduplicated items,
Shift tooltip behavior, food intake, day rollovers and localized wording.


## v4.2 refinement

- The original 32/48 room-grid doorway selector now varies widths by edge hash:
  Level 0 ranges from 1 to 11 blocks (mostly 3–5); Level 1 ranges from 3 to
  13 blocks (mostly 5–9). Adjacent room faces and are evaluated from the same edge hash. Protected-route masks retain a
  conservative centerline, while the actual doorway may be wider.
- Voronoi-cell interiors now support jogged main dividers, optional cross
  dividers, short return walls, L-shaped side pockets and paired warehouse
  bay partitions. Door gaps vary by level rather than remaining a fixed
  two-block interruption.
- Most internal walls reach the ceiling; a minority remain partial-height.
  Materials vary within each floor's established yellow-office / industrial
  palette. The guaranteed spine remains carved after the mask is evaluated.
- Nutrition item hints now sort by the fraction of the abstract game-day
  target covered and show the target beside each contribution. Uncatalogued
  third-party food receives an explicit water/salt fallback label instead of
  appearing to have a curated profile. The legacy chat-page display now uses
  the same authoritative snapshot as the GUI, including real water and salt.


## v4.2 verification note

The 404 food IDs in the nutrient catalog are all present in the independent
water/salt catalog. That catalog also contains the raw/boiled/filtered drink
items and the vanilla potion entry. The client tooltip checks profile presence
before describing a value as a curated item contribution; third-party foods
that are not mapped receive an explicit fallback label.
