# Agriculture crop lifecycle v1.8

## Catalog and crop cycles

The agriculture catalog contains 80 registered crop lines. v1.8 completes the wiring for eight additional crops: cassava, taro, yam, teff, watercress, endive, kohlrabi, and rutabaga.

Each crop has:
- a registered plantable item/block pair and harvested produce item;
- an explicit climate band, with climate categories treated as gameplay proxies rather than literal biome temperatures;
- eight growth ages, pollen-state blockstate coverage, a dedicated seed/propagule icon, and crop-specific mature-stage art;
- a seed or propagule recipe, mature-only produce loot, bilingual names, and compost-map entries.

## Crop-specific behavior

- **Cassava, taro, and yam:** distinct root/corm/tuber produce art and planting-propagule icons. Their harvested items are materials rather than raw food, because preparation matters for these crops.
- **Teff:** a fine-grained cereal with a dedicated grain head and seed icon.
- **Watercress:** repeat-cut leaf harvest, freshwater required within a compact radius, and a low, dense leafy silhouette.
- **Endive:** repeat-cut leaf harvest and a compact rosette silhouette.
- **Kohlrabi:** a swollen-stem produce icon and rosette growth form.
- **Rutabaga:** a storage-root icon with a distinct purple shoulder and cool-season climate category.

The water checks only inspect already-loaded chunks and reuse a mutable block position. Rice keeps its wider irrigation check; taro and watercress use a smaller nearby-water check. These are deliberately simple Minecraft rules, not a claim that every real cultivation method uses the same water regime.

## Generated asset contract

The Gradle asset generator reads the v1.8 crop profile in addition to earlier profiles and validates:
- 80 unique crop IDs and 80 complete leaf-form, produce-organ, and seed-form mappings;
- 80 crop-specific seedling/mature detail pairs;
- 960 generated crop models and stage textures (80 crops × 8 ages plus 4 pollinated-stage variants per crop);
- 160 distinct produce/propagule inventory icons;
- complete crop blockstates, recipes, loot, translations, and compostability through the QA suite.

The CI build compiles and packages the NeoForge mod, validates generated PNG dimensions and uniqueness, and launches a fresh dedicated server. Those checks do not replace a graphical client review of growth stages and harvesting in a newly created world.
