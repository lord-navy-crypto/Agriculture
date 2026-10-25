# Gugu-Gaga Agriculture & Apiaries — development branch

A source-informed Minecraft **1.21.1 / NeoForge 21.1.259 / Java 21** expansion. This remains experimental branch work, not a released mod.

## Working content

- **36 plantable crops** across grains, legumes, leafy vegetables, roots/tubers, berries, oilseeds and fiber crops.
- Crop seeds, eight age stages, harvested items, loot tables, seed recipes, bilingual English/Chinese names and placeholder vanilla textures.
- **12 wild forage/seed-source plants** distributed across selected vanilla biomes. They are gameplay proxies for wild relatives, not claims that modern crop cultivars grow wild unchanged.
- **Six custom bee entity IDs**: bumblebee, mason bee, squash bee, leafcutter bee, carpenter bee and mining bee. They have separate spawn registrations and habitat profiles; they still use vanilla Bee AI, model, hive behavior and breeding.
- Crop-aware pollination marks persist through maturity and provide a bounded harvest-yield bonus. Pollination is checked separately from biome/irrigation growth rolls; meadow/orchard/berry tags remain available for vanilla bees.
- A **2×2 apiary kit** placing four separate functional vanilla beehives; it does not enlarge a single hive.
- Biome-based crop-growth suitability using published seed-germination references as relative sensitivity categories. Real measurements are not converted into Minecraft ticks.
- **Rice paddy rule (v0.4):** rice growth requires freshwater within three horizontal blocks at crop height or the farmland level below. The crop remains planted on normal farmland. This is an irrigated-lowland gameplay model, not a universal claim about every rice-growing system.

## Build and QA

Target Java 21, Minecraft 1.21.1 and NeoForge 21.1.259.

```sh
gradle --no-daemon --stacktrace build
python3 qa/validate_agriculture_assets.py
python3 qa/validate_agriculture_expansion.py
python3 qa/validate_agriculture_ecology.py
python3 qa/validate_bee_biodiversity.py
python3 qa/validate_agronomic_sources.py
python3 qa/validate_agriculture_v0_3.py
python3 qa/validate_rice_paddy.py
python3 qa/smoke_rewrite_server.py
```

GitHub Actions compiles the mod, validates resource structure and starts a dedicated NeoForge server. Passing CI does **not** replace a graphical client test of crop growth, visuals and world generation in fresh chunks.

## Biological and gameplay boundaries

- Crop and wild-plant textures remain vanilla placeholders; crop shapes and crop-specific yield models are not complete.
- The bee entities are registered prototypes, not independent species simulations. Solitary nesting for mason, leafcutter, carpenter and mining bees, colony lifecycle for bumblebees, unique art and inherited bee traits remain future work.
- Soil pH, crop rotation, nutrient depletion, cultivar differences, seasons and a measured pollination-to-fruit-set/yield model are not implemented.
- The rice rule models irrigated lowland cultivation. IRRI documents both flooded-field practices and water-saving alternate wetting and drying; other systems, including aerobic rice, exist.

## Research references

- Germination temperature references and limitations: `docs/REAL_AGRICULTURE_DATA_V0_2.md`
- Crop/biome and bee design: `docs/AGRICULTURE_ECOLOGY_V0_2.md`, `docs/AGRICULTURE_V0_3_BIOMES.md`, `docs/BEE_BIODIVERSITY_V0_2.md`
- International Rice Research Institute, [Alternate Wetting and Drying](https://ghgmitigation.irri.org/mitigation-technologies/alternate-wetting-and-drying)
- International Rice Research Institute, [Healthy Soils, Healthy Rice](https://ricetoday.irri.org/healthy-soils-healthy-rice/)
- NeoForged 1.21.1 biome modifiers: https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/
- University of Minnesota Extension crop guidance: https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/planting-the-vegetable-garden
- Xerces Society, bee nesting resources: https://www.xerces.org/pollinator-conservation/nesting-resources


### Beekeeping enhancement v0.6

The honey harvester supports **bottling honey** (one glass bottle, from offhand/inventory) or **honeycomb harvesting** (shears held in the offhand, losing one durability, yields three honeycombs). Both modes require a full vanilla hive and lit unobstructed campfire smoke; the hive's honey level resets afterward. Bee pollination and climate-limited plant growth are evaluated independently. See docs/AGRICULTURE_BEECARE_V0_6.md for biological caveats and sources.
