# Gugu Gaga Agriculture — Fusion Marine and Fungal Variation v2.7

## What changed

The optional Fusion resource pack now adds randomized texture variants to the generated marine and fungal growth stages:
- **24 fungal culture stages** (six cultures × four ages)
- **16 marine plant stages** (four crops × four ages)

Combined with v2.6, the optional pack now contains random atlases for 1,200 terrestrial crop stages, 24 fungal culture stages, 16 marine plant stages, and eight bee-habitat atlases, plus the existing eight habitat model overrides.

Each marine/fungal source remains a 16×16 generated texture in the main mod JAR. The optional pack overrides it with a 32×32 2×2 atlas containing four related variants and Fusion metadata using `randomness_source: position`.

## Compatibility and scope

- Minecraft 1.21.1 / NeoForge; optional Fusion client mod 1.3.16.
- No new blocks or items; no recipe, nutrition, or growth-cycle changes.
- No Fusion dependency is bundled into the mod JAR.
- Without Fusion or the optional resource pack, the base marine and fungal textures remain unchanged.

## Build and validation

- `gradle generateFusionVisualPack` generates all Fusion atlases and review sheets.
- `gradle packageFusionVisualPack` creates `build/distributions/gugugaga-fusion-visuals-v2.7.zip`.
- `python3 qa/validate_fusion_visual_pack_v2_7.py build/fusion-visual-pack build/distributions/gugugaga-fusion-visuals-v2.7.zip` checks 1,240 growth-stage atlases plus habitat assets and ZIP integrity.
- Preview sheet: `build/reports/agriculture/fusion-marine-fungi-random-variants-contact-sheet.png`.

Official Fusion random-texture format: https://github.com/SuperMartijn642/Fusion/wiki/Random-Texture-Type
