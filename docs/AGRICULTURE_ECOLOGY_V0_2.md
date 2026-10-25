# v0.2 crop ecology / biome generation

The listed biome choices are a **Minecraft adaptation** of general cool-season/warm-season crop requirements. Minecraft biomes are not calibrated weather stations or USDA Hardiness Zones.

## Growth
- Existing Minecraft farmland hydration/light and default crop growth remain intact.
- CropClimate uses explicit cool/temperate/warm/dry/wet *game groups* to slow growth in poorly matched vanilla biomes, never speed growth beyond vanilla baseline. No chunk scanning.
- Nether/End growth is strongly reduced, rather than broken by arbitrary dimension bans.
- Relative growth probabilities are gameplay tuning, not real agricultural day counts.

## World generation
Rare forageable **wild seed plants** spawn through version-specific NeoForge add_features biome modifiers in plains, meadows, forests, taigas, etc. Wild plants drop cultivated seed items as a gameplay abstraction: they are not genetically identical to modern crop cultivars.
- Wild barley: plains, meadow, windswept_hills
- Wild oat: plains, meadow, taiga
- Wild pea: forest, birch_forest, meadow
- Wild lettuce: forest, meadow, river
- Wild flax: plains, meadow, forest
- Wild sunflower: sunflower_plains, plains, savanna
- Wild blueberry: taiga, old_growth_pine_taiga, forest
- Wild strawberry: forest, birch_forest, meadow
Additional native bees are allowed in flowering biomes as small extra vanilla-bee spawn entries.

## Authority
- NeoForged 1.21.1: https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/
- Minnesota Extension growing vegetables and pollinators: https://extension.umn.edu/vegetables/growing-vegetables
- Utah State University Extension garden guides: https://extension.usu.edu/yardandgarden/vegetables
- USDA NRCS pollinators: https://www.nrcs.usda.gov/conservation-basics/animals/insects-pollinators
- Minecraft 1.21.1 vanilla worldgen example: https://mcasset.cloud/1.21.1/data/minecraft/worldgen/configured_feature/patch_pumpkin.json

## Acceptance
Run the new wildlife asset QA and actual dedicated-server boot. Confirm biome modifiers decode correctly; test fresh chunks and compare old chunks after mod installation. Test plant growth (including hydrated farmland) in mismatched/ideal biomes. Verify no feature cycles.
