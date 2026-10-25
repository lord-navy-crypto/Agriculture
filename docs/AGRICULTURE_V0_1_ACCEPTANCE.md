# Agriculture & Apiaries v0.1 — acceptance notes

## Implemented in this branch

- Ten separate seven-age crop registrations: corn, tomato, onion, rice, soybean, strawberry, cucumber, blueberry, bell pepper, and garlic.
- Ten seed items that plant their matching crops on farmland and ten edible harvest items.
- Mature crops drop produce plus seeds; immature crops return seeds.
- Bee-assisted growth for tomato, onion, strawberry, cucumber, blueberry, and bell pepper. The rule is bounded to nearby live vanilla bees and the matching ecotype profile.
- Three bee ecotype tags: meadow (generalist), orchard (fruiting crops), and berry (berry crops). These tag existing vanilla bees using entity persistent data, so native AI, breeding, and hive behavior are preserved. Breeding offspring are not yet guaranteed to inherit the tag.
- A large-apiary kit that places four vanilla beehives in a 2×2 layout. Each remains a vanilla hive, so native block entities and storage work without unsafe custom block-entity substitution.
- English and Simplified Chinese localization, recipes, JSON asset validation, NeoForge compile and dedicated-server boot checks.

## Simplifications

- Crop block and item textures reuse vanilla assets temporarily. Distinct original crop textures, corn height, paddy-water management, crop rotation, soil nutrient state, and crop-specific harvest yield are not yet implemented.
- Pollination is a simple crop-growth bonus, not a complete model of flower visits, fruit set, seed formation, or pollination-limited yield.
- Ecotypes differ in compatibility, radius, and growth-bonus chance, but currently share the vanilla bee model and AI. Species-specific bee entities and inheritance are future work.
- The apiary kit arranges four ordinary hives; it does not alter the capacity of an individual vanilla hive.

## Research basis

- USDA NRCS, Insects and Pollinators: https://www.nrcs.usda.gov/conservation-basics/animals/insects-pollinators
- Utah State University Extension, Pollination: https://extension.usu.edu/vegetableguide/production/pollination
- FAO, What is agricultural biodiversity?: https://www.fao.org/agriculture/crops/thematic-sitemap/theme/compendium/tools-guidelines/what-is-agricultural-biodiversity/en/

These sources support the high-level design rationale. Crop tick probabilities and game distances are balancing choices, not imported agronomic rates.

## Acceptance still required

- Build and JSON asset validation in CI.
- Fresh dedicated-server startup in CI.
- Client-side in-world test: plant each seed, verify stage progression and drops, tag bees, confirm compatible crops receive the growth bonus, and use the apiary kit in a survival test world.
- Manual review of texture readability and crafting balance.
