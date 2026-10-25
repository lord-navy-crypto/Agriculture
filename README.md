# Gugu-Gaga Agriculture & Apiaries

A clean, source-informed Minecraft 1.21.1 / NeoForge agriculture expansion for crops, bee husbandry, pollination, and apiary building.

## Current development slice

- Ten plantable crops: corn, tomato, onion, rice, soybean, strawberry, cucumber, blueberry, bell pepper, and garlic.
- Normal Minecraft crop rules: farmland planting, random-tick growth, mature harvesting, and seed recovery.
- Pollination-aware growth for flowering/fruiting crops. Vanilla bees act as the general meadow ecotype; players can tag a bee as a meadow, orchard, or berry line using a crafted tag. The lines have different crop compatibility and effective foraging radii.
- A large-apiary kit places a 2×2 arrangement of four vanilla beehives, retaining vanilla hive block entities and their normal behavior. This gives a compact 12-bee installation (four hives × three stored bees) without replacing Minecraft’s native hive storage.
- Bilingual English/Chinese names and basic recipes.

## Technical target

- Minecraft 1.21.1
- NeoForge 21.1.259
- Java 21
- Mod ID: gugugaga

## Build and checks

Run gradle --no-daemon --stacktrace build, then python3 qa/validate_agriculture_assets.py and python3 qa/smoke_rewrite_server.py.

The asset validator checks JSON validity, crop state coverage, loot tables, item models, localization, and recipes. CI also compiles the mod and launches a disposable NeoForge dedicated server. A successful build is not a substitute for a graphical in-world acceptance test.

## Gameplay simplifications and next steps

- Crop textures currently reuse vanilla Minecraft crop/item textures as placeholders; custom art and crop-specific silhouettes are a follow-up.
- Rice is currently a normal farmland crop rather than a water-managed paddy.
- Pollination is represented by a small, bounded growth bonus rather than a full flower-to-fruit yield simulation.
- Bee lines are data-tagged ecotypes of the vanilla bee, not separately modelled biological species yet. They share vanilla appearance, breeding, hive behavior, and pathfinding. Species-specific entities/models, richer hive capacity, and dedicated pollination/yield rules remain future work.

## Research grounding

The game rules are simplified, but their direction is informed by:
- USDA NRCS, Insects and Pollinators: https://www.nrcs.usda.gov/conservation-basics/animals/insects-pollinators
- Utah State University Extension, Pollination: https://extension.usu.edu/vegetableguide/production/pollination
- FAO, What is agricultural biodiversity?: https://www.fao.org/agriculture/crops/thematic-sitemap/theme/compendium/tools-guidelines/what-is-agricultural-biodiversity/en/

These sources inform the design; in-game timers and bonuses are gameplay abstractions, not claims about exact real-world crop growth rates.
