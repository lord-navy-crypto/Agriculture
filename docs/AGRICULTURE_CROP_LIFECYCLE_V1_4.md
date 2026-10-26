# Agriculture crop lifecycle v1.4

## Scope

This update pauses bee-feature work and completes the existing 64 plantable crop lines before adding more crops. It preserves crop IDs, seed IDs, recipes, loot-table IDs, composting entries and save-compatible block registry names.

## Visual growth cycle

- Every crop has eight visibly different age textures, from a newly emerged seedling through vegetative growth to a mature harvest stage.
- The 64 crops each have their own accent palette and harvested-organ silhouette family: grain heads, pods, fruit/berries, roots and bulbs, leafy rosettes, flowers, herbs and fiber plants.
- Reproductive stages have a distinct pollen-marked visual variant when the saved pollinated state is set.
- Every crop has a custom produce icon and custom seed icon, replacing vanilla wheat/carrot/potato/berry icon placeholders for the crop items.
- The deterministic Gradle generator writes 768 block-stage textures, 768 matching crop models, and 128 item icons. These generated assets are included in the built JAR and require no Python or external image library.
- Standard Minecraft cross-plane crop geometry remains in use. Real branching, fruit load and below-ground organs are simplified for compatibility with vanilla farmland and the eight-stage crop state.

## Gameplay lifecycle

Seed item -> farmland placement -> age 0..7 -> optional pollination mark -> mature harvest -> either replant or regrow.

- Native CropBlock checks farmland, light and normal Minecraft growth conditions; the crop climate layer adds broad biome suitability and the existing irrigated-lowland water rule for rice.
- All 64 seeds and produce items remain registered, translated, available through the creative tab, and represented in existing crop loot tables and seed recipes.
- Mature single-harvest grains, roots, bulbs and dry seed crops are collected by breaking the plant. Loot tables retain the seed/produce distinction.
- Fruit and pod crops, selected leaf crops, and selected flower/side-shoot crops can be right-click harvested when mature. The plant stays in place, returns to growth stage 4, clears its pollination mark, and must grow back to stage 7 before the next harvest.
- For crops whose harvestable organ benefits from the mod's pollination model, a marked mature harvest yields one extra produce item. Leaf harvests do not receive an artificial bee bonus because leaves are harvested before seed set.
- All 64 seed items and all 64 produce items remain in the native compostable data map. No new bee behavior is required for this crop milestone.

## Evidence and biological boundaries

University of Minnesota Extension guidance distinguishes crop-specific maturity windows, cool- versus warm-season crops, frost tolerance, and single versus repeated harvest windows. It also identifies flowering, fruit/pod formation, head formation and bulb/root enlargement as critical irrigation periods. These are qualitative design references, not literal Minecraft tick values.

- https://extension.umn.edu/agriculture/specialty-crops/vegetable-farming/climate-resilience-resources-for-vegetable-growers
- https://extension.umn.edu/vegetable-growing-guides-farmers/crop-and-field-planning-tools-vegetable-farmers
- https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/planting-the-vegetable-garden
- https://extension.umn.edu/agriculture/specialty-crops/vegetable-farming/irrigation-strategies-for-vegetables

Real maturity days, cultivars, soils, planting methods and harvest windows vary by climate and variety. This is a source-informed gameplay abstraction, not a scientific crop-growth simulator. A graphical client test should confirm all sprite states and right-click harvest behavior.
