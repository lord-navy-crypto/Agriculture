# Changelog

## v2.7.0-rc.1 — Optional Fusion stage-model linkage hardening

- Added the missing 1,200 crop and 40 marine/fungal `fusion:base` model overrides to the **optional** companion resource pack.
- Each model preserves the shipped Java Edition parent, geometry, and original texture references, and explicitly enables the Fusion model loader.
- Expanded packaging QA to confirm model-to-texture binding for all 1,240 stages and byte-for-byte ZIP integrity.
- The independent main mod JAR and gameplay remain unchanged. Actual client rendering with and without Fusion is still a release gate.

## v2.7.0-rc.1 — Packaged-art validation hardening

- Added CI that compares the distributable mod JAR and optional Fusion resource pack at individual pixel level for 1,240 crop, fungal, and marine growth-stage atlases.
- Checks all four 16x16 variants of every atlas for correct mirroring, tone, transparency, and uniqueness; catches mismatched release artifacts without changing visuals or gameplay.

## v2.7 — Fusion marine and fungal visual variation

- Extended the optional Fusion pack to 24 fungal culture growth textures and 16 marine plant growth textures.
- Preserved all generated 16×16 base textures in the main mod JAR; the optional pack supplies 2×2 random atlases with position-based selection.
- Added a marine/fungal variation preview sheet and CI checks for every atlas, metadata file, and ZIP entry.
- No item IDs, recipes, food values, or growth mechanics changed.

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
