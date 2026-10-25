# Gugu-Gaga Agriculture & Apiaries — v0.2 Development

This is a source-informed Minecraft **1.21.1 / NeoForge 21.1.259 / Java 21** expansion built on the experimental Gugu-Gaga repo.
Code remains on a draft PR; do not treat it as a released mod.

## Working content

- **24 plantable field crops**: original 10 (corn, tomato, onion, rice, soybean, strawberry, cucumber, blueberry, bell pepper, garlic), plus 14 (barley, oat, rye, pea, lettuce, cabbage, spinach, broccoli, eggplant, cotton, flax, peanut, sorghum, sunflower seed).
- Crop seeds, 8 stage blockstates, produced items, loot tables, recipes, basic edible item definitions, and English/Chinese translations.
- **8 wild seed-source plants** with explicit Minecraft vanilla biome generation: barley, oat, pea, lettuce, flax, sunflower, blueberry and strawberry. These represent wild relatives as a gameplay simplification, **not** genetically accurate wild copies of modern cultivars.
- **Four additional distinct bee entity types**: bumblebee, mason bee, squash bee and leafcutter bee. They naturally spawn in selected vanilla biomes at conservative rates, each has its own entity registry ID, and pollination compatibility is crop-sensitive.
- Three optional *vanilla bee* foraging profile tags (meadow, orchard, berry).
- A **2×2 apiary kit** that places four individually functional vanilla beehives. It does not enlarge the storage of a single hive.
- Source-based growth adaptation to vanilla biome categories, including **10 published soil germination minimum temperatures** used as relative cool-biome sensitivity thresholds.

## Build / test

```sh
gradle --no-daemon --stacktrace build
python3 qa/validate_agriculture_assets.py
python3 qa/validate_agriculture_expansion.py
python3 qa/validate_agriculture_ecology.py
python3 qa/validate_bee_biodiversity.py
python3 qa/validate_agronomic_sources.py
python3 qa/smoke_rewrite_server.py
```

GitHub Actions compiles, validates resources, and starts a real dedicated NeoForge server. Passing those checks **does not replace a graphical client/in-world world-generation test**.

## Biological and gameplay boundaries

The four newly registered bees still inherit **vanilla honeybee AI, animation, bee textures, hive use and breeding**. Therefore they are identifiable entity prototypes, not yet independent accurate species simulations. Real mason/leafcutter/squash bee solitary nesting and bumblebee colony mechanics remain to be implemented.

Wild plants currently use placeholder vanilla plant textures, and crops currently use placeholder vanilla age models. Rice paddies, unusual crop shapes/height, crop-specific yields, and true pollination-to-fruit-set calculations still need implementation. The soil temperature data in the pack are real (Fahrenheit) but Minecraft biomes do not express measured degrees Fahrenheit and all random-tick rates remain gameplay tuning.

## Evidence

- Real germination data: `docs/REAL_AGRICULTURE_DATA_V0_2.md`
- Biome mapping: `docs/AGRICULTURE_ECOLOGY_V0_2.md`
- Pollinator biodiversity: `docs/BEE_BIODIVERSITY_V0_2.md`
- NeoForge world generation: https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/
- University of Minnesota crop pollination: https://extension.umn.edu/agriculture/specialty-crops/pollination/requirements
- Xerces bee nesting: https://www.xerces.org/pollinator-conservation/nesting-resources
- USDA leafcutter bee: https://www.ars.usda.gov/pacific-west-area/logan-ut/pollinating-insect-biology-management-systematics-research/docs/alfalfa-leafcutting-bee-alcb/
