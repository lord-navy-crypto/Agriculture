# Agriculture & Pollinators v0.3 — reality vs Minecraft

## Field evidence retained separately from gameplay

- **Minnesota Extension** vegetables and climate resilience: radish real-life maturity generally 30–60 days; turnip 50–60 days (not Minecraft ticks). https://extension.umn.edu/agriculture/specialty-crops/vegetable-farming/climate-resilience-resources-for-vegetable-growers-in-minnesota
- **Minnesota Extension** seed planting: radishes and greens cool-season; soil temperature and frost matter. https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/planting-the-vegetable-garden
- **USDA NRCS** cover-crop quick reference: radish and turnip minimum germination reference 45 °F; buckwheat 50 °F. These values concern varieties and real soil, not biome temperatures. https://www.nrcs.usda.gov/sites/default/files/2022-09/NE_CC_QuickReference.pdf
- **Penn State Extension** bees: mining bees (Andrena) typically ground nesting; sweat bees include ground nesters and species with varied social structures. https://extension.psu.edu/orchard-pollination-wild-bees
- **NeoForge 1.21.1** biome modifier format. https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/

## Game implementation

Eight additional crops: radish, turnip, parsnip, kale, Swiss chard, buckwheat, millet, sesame.
- Cool-leaning crop groups: radish, turnip, parsnip, kale, Swiss chard.
- Warm flowering: buckwheat, sesame. Dry warm cereal: millet.
- Vanilla biome names remain **qualitative proxies**. Do not turn USDA real temperatures into Minecraft biome values or equate random ticks with calendar days.
- Some real crops are harvested before flowering: no bee bonus on edible roots/leaves; buckwheat and sesame pollinator interaction is abstract bonus on growth for now, NOT measured seed yield.
- Uses ordinary farmland and eight-age CropBlock, native item/seed loot, placeholder Minecraft textures.
- New rare seed-bearing wild-relative patches in previously unexplored chunks:
  - Wild radish analogue: plains, meadow, flower forest.
  - Wild green-foxtail/millet analogue: plains, savanna, sunflower plains.
  - Their drops of commercial-style seeds are **game abstractions**, not genetic claims.
- New mining and sweat bee entities: low-weight spawns in flowering plains/forest habitats.
  - **Prototype limitation:** new entity IDs still use vanilla Bee AI, geometry and hive behavior. Ground-nesting ecology remains a future implementation. Neither should be marketed as an authentic wild bee simulation.
  - Only weather/biome source-backed categories; exact spawn weight and chunk feature frequencies are game tuning.

## Acceptance
Run all earlier agriculture QA scripts plus qa/validate_agriculture_v03.py, Java 21 NeoForge build, real dedicated-server boot. Also manually confirm new crops can be planted/harvested, wild plants generate in *fresh* chunks, bees spawn naturally in appropriate biomes, and no resource pack missing-model diagnostics occur.
