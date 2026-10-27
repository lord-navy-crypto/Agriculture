# Gugu Gaga Agriculture — Fusion Crop-Field Variation v2.6

## What changed

The optional Fusion resource pack now covers all **1,200 generated crop growth-stage textures** (100 crops × 8 normal stages, plus 4 pollinated stages per crop). It replaces each source texture only inside the optional resource pack; the mod JAR retains its original 16×16 generated textures.

Each crop texture atlas is 32×32 and contains four 16×16 variants:
1. Original art.
2. Mirrored art with a small brightness lift.
3. Original art with a small brightness reduction.
4. Mirrored art with a slightly stronger brightness lift.

The metadata uses Fusion's documented `random` texture type with `rows: 2`, `columns: 2`, and `randomness_source: position`. Position-based selection keeps the same variant across the faces of a crop model. The original crop species, growth stage, and pollination marks are preserved.

Official reference: https://github.com/SuperMartijn642/Fusion/wiki/Random-Texture-Type

## Scope and compatibility

- Minecraft 1.21.1 / NeoForge; optional Fusion client mod version 1.3.16.
- 1,200 crop-stage texture atlases plus the existing 8 habitat atlases and 8 idle/active habitat model overrides.
- No new blocks/items, no gameplay changes, and no hard Fusion dependency in the mod JAR.
- The optional pack must only be enabled when Fusion is installed on the client. Without the pack, the original textures remain in use.
- Server installation does not require Fusion.

## Build and review

- `gradle generateFusionVisualPack` — builds the optional pack and the habitat/crop review sheets.
- `gradle packageFusionVisualPack` — creates `build/distributions/gugugaga-fusion-visuals-v2.6.zip`.
- `gradle build` — builds the mod JAR and optional pack.
- `python3 qa/validate_fusion_visual_pack_v2_6.py build/fusion-visual-pack build/distributions/gugugaga-fusion-visuals-v2.6.zip` — checks 1,200 crop atlases, metadata, habitat models and ZIP contents.

Preview: `build/reports/agriculture/fusion-crop-random-variants-contact-sheet.png`.

A successful headless build proves resource integrity and server compatibility, but visual acceptance still requires checking a NeoForge 1.21.1 client with Fusion enabled.
