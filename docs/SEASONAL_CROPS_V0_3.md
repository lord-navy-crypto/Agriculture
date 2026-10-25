# Seasonal crops v0.3 (real agriculture sources -> Minecraft gameplay)
Date: 2026-10-25. **Biome names are not measured soil or air temperature.** Values and geographic comparisons are not calibrated.
- Radish and turnip: cool-season root vegetables, suitable for Minecraft plains/meadows and cooler forests. Source: https://www.umass.edu/agriculture-food-environment/vegetable/fact-sheets/radish and https://ag.purdue.edu/department/hla/extension/extension-publications-library/ext-pubs/ho-186-w.html
- Millet: warm/dry cereal used here in savanna/badlands game climate (farming only; not naturalized wild blocks).
- Buckwheat: flowering grain/pseudocereal, simplified bee-assist crop growth abstraction (not field pollination yield).
- Lentil: cool/dry pulse. Source: https://cropgenebank.sgrp.cgiar.org/index.php/regeneration-guidelines-of-crops/lentil-mainmenu-405
- Chickpea: heat/drought-tolerant relative to other cool-season pulses; drains well, avoid waterlogging. Source: https://extension-store.montana.edu/montguides/chickpea-production and https://www.saskatchewan.ca/business/agriculture-natural-resources-and-industry/agribusiness-farmers-and-ranchers/crops-and-irrigation/field-crops/pulse-crop-bean-chickpea-faba-bean-lentils/chickpea/adaptation-and-varieties
- Version-specific NeoForge biome modifiers and vanilla-patch generation: https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/
- Wild radish / wild turnip are game forage plants that drop cultivated seeds. Not a claim of genetic identity or scientific biogeography.
- All crop growth remains vanilla farmland random ticks with simulated climate-band adjustments, **not** measured agronomic days to maturity.
- Current visuals remain vanilla texture placeholders; new dedicated plant geometry and actual pollen/fruit set are not yet shipped.

## Biome adaptation and acquisition
| Crop | CropClimate band | Suggested farming biomes | Discovery |
|---|---|---|---|
| radish | cool | meadow, plains, forest, taiga | rare wild radish seed plant in meadow, plains, forest |
| turnip | cool | meadow, plains, birch forest | rare wild turnip seed plant in meadow, plains, birch forest |
| millet | dry | savanna, badlands (with farmland irrigation) | crafting seed from wheat seeds, temporary |
| buckwheat | cool | meadow, plains, birch forest | crafting seed from wheat, temporary |
| lentil | cool | plains, meadow, taiga | crafting seed from pea seeds, temporary |
| chickpea | dry | savanna, plains, badlands (with suitable farmland) | crafting seed from peas, temporary |

## Validation
- `qa/validate_seasonal_crops.py` must validate JSON and references
- `gradle --no-daemon build` and server start CI must pass
- Visual/in-world growth and fresh-chunk generation remain separate manual QA tasks
