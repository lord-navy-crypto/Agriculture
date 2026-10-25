# Gugu-Gaga Agriculture & Apiaries — development branch

A source-informed Minecraft **1.21.1 / NeoForge 21.1.259 / Java 21** expansion. This remains experimental branch work, not a released mod.

## Working content

- **64 plantable crops** across grains, legumes, leafy vegetables, roots/tubers, berries, oilseeds and fiber crops.
- Crop seeds, eight visually distinct growth stages per crop, crop-specific pixel-art sprites, pollination-stage overlays, harvested items, loot tables, seed recipes and bilingual English/Chinese names.
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

- Crop textures are generated deterministically at build time from 64 crop-specific palettes and organ-aware pixel-art silhouettes; all crop seed and produce icons are custom. Wild forage plants still use vanilla placeholder textures, and crop blocks retain Minecraft's standard cross-plane geometry.
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

### Agriculture ecology and performance v0.7

Mature fields now skip unnecessary biome/water/pollinator scans. A bee must have recently foraged (the vanilla nectar flag) before it can set a crop's pollination marker. Rice water detection reuses one mutable coordinate, and native-bee habitats distinguish bare-soil squash-bee nesting from managed cavity-nesting habitat. The honey harvester now uses Minecraft's own campfire-smoke check. See docs/AGRICULTURE_BEECARE_V0_6.md for source references and limitations.


### v0.9: 44 crop lines and floral-source honey bottles

Eight newly plantable crops (lavender, clover, basil, mint, mustard, canola,
celery, leek) extend the original 36. New drinkable honey bottles are clover,
lavender, buckwheat and sunflower; they are classified from predominant nearby
flowering crops only when harvesting a full smoked native hive. Mixed/uncertain
forage still yields regular honey. See `docs/AGRICULTURE_CROPS_AND_NECTAR_V0_9.md`.


### Agriculture native composting (v1.3)
All 64 harvested organic crop items and 64 corresponding seeds support vanilla
composters through NeoForge's item `neoforge:compostables` data map. Seeds have a
30% level-up chance and harvested biomass a 65% chance; villager auto-composting
remains disabled to preserve farm/trade balance. Crop IDs and recipes unchanged.


### Crop lifecycle and custom textures (v1.4)

All 64 crop lines now have eight distinct growth-stage sprites, custom produce and seed inventory icons, and separate visible pollen-marked variants for reproductive stages. Gradle generates the transparent 16×16 PNG resources and matching crop models from the source catalog during the normal build; the generated assets are packaged in the JAR and require no external art tools. Harvest modes distinguish single-harvest grains, roots, bulbs and seed crops from repeated fruit, leafy, flower-head and side-shoot harvests. See docs/AGRICULTURE_CROP_LIFECYCLE_V1_4.md.

The sprites are custom Minecraft pixel art informed by crop morphology, not photographs or claims that a single cultivar looks identical in every region. Published maturity ranges, cold tolerance and critical watering stages guide relative gameplay categories; they are not converted directly into Minecraft ticks. Client-side visual checks remain necessary after the build.

### Crop-specific texture precision (v1.5)

The crop-only visual pass now gives all 64 crops explicit leaf architectures (blade, rosette, bulb/rhizome, vine, bush, fern, flower stem or ribbed stalk) plus crop-specific seedling and mature details. Inventory icons are mapped to each crop's harvested organ—grain, fruit, berry, bulb, rhizome, root, seed, pod, leaf, head, fiber, flower or stalk—instead of reusing broad category icons. Existing crop IDs, recipes, loot tables, growth ages and harvest/regrowth behavior are preserved. CI publishes three review sheets for crop ages/pollination, produce icons and seed icons. See `docs/AGRICULTURE_CROP_TEXTURE_PRECISION_V1_5.md` for the asset contract and acceptance limits.
