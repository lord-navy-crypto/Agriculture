# Changelog

## v2.6 — Fusion crop-field variation

- Extended the optional Fusion resource pack to all 1,200 generated crop growth-stage textures, including pollinated stages.
- Each crop-stage texture gets four 16×16 variants in a 2×2 atlas, with per-position selection so the crossed crop model uses one consistent variant.
- Added a 20-crop random-variant proof sheet and CI validation for every atlas, metadata file, and ZIP entry.
- Kept the default mod JAR textures and crop gameplay unchanged; Fusion remains optional and client-side.

## v2.5 — Crop and seed icon depth pass

- Added a restrained pixel-art shadow and bevel pass to all 100 produce and 100 seed icons.
- Rebuilt the produce and seed review sheets as a five-column grid.
- Added a side-by-side crop item contact sheet.
- Added a CSV audit of icon pixel coverage, bounding boxes, and color counts.
- Added CI validation for all 200 icon audit rows and the three review-sheet dimensions.
- No item IDs, recipes, food values, or crop lifecycle mechanics changed.

## v2.4 — Fusion visual overhaul

- Added an optional Fusion resource pack for four existing bee habitat families.
- Added eight random texture atlases and eight idle/active model overrides.
- Improved generated cuisine silhouettes and 50 honey bottle labels.
- Added Fusion dependency resolution and resource-pack validation to CI.
