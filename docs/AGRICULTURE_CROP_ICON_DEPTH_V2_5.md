# Agriculture v2.5 — Crop and Seed Icon Depth Pass

## Scope

This pass improves the readability of the existing 100 crop produce icons and 100 seed icons without changing item IDs, recipes, crop growth, harvest yields, nutrition, or gameplay.

- A restrained one-pixel lower-right shadow plus edge bevel is applied to the generated 16×16 produce and seed sprites.
- Source pixels are drawn on top of the shadow, preserving the original icon silhouette and transparent background except for the intentional one-pixel depth edge.
- Produce and seed contact sheets are reorganized into a 5-column grid for faster human review.
- A new side-by-side produce/seed contact sheet lets reviewers compare each crop's harvest and planting item together.
- A CSV visual audit records opaque pixel count, bounding box, and unique RGB colors for all 200 icons.
- CI validates the 200-row audit, 100 crop IDs, expected pixel coverage, and the dimensions of all three item review sheets.

## Validation

The main `gradle build` task runs the deterministic crop asset generator. The workflow then runs:

```bash
python3 qa/validate_crop_item_visual_audit.py build/reports/agriculture
```

The report is build output only; it is not copied into the game's resource namespace. Crop item textures remain 16×16 PNGs and the existing 100 unique produce / 100 unique seed checks remain in force.

## Acceptance limits

- No new item or block registrations.
- No recipe or food-stat changes.
- No Fusion hard dependency added to the main mod.
- Existing 1.21.1 NeoForge build and fresh real-server boot remain required.
- Final artistic acceptance still requires checking the generated sheets and in-game inventory at normal GUI scale.
