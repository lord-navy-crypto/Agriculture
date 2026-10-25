# Bee biodiversity prototype

## Source-backed ecological differences

- **Bumblebees** (Bombus): generally social colonies; often nest in underground/cavity shelters. Important buzz pollinators (Xerces Society).
- **Mason bees** (Osmia): solitary cavity / hollow-stem nesters; early-season flower visitors (Xerces Society).
- **Squash bees** (Peponapis/Xenoglossa): ground nesting, closely linked to Cucurbita flowers and early-morning foraging (University of Minnesota Extension).
- **Alfalfa leafcutting bees** (Megachile rotundata): solitary cavity nesters especially valuable for alfalfa seed production (USDA ARS).
- **Vanilla honeybees**: managed beehive storage, honey gathering and generalist pollination already supported.

## Implemented first gameplay milestone

Four **separately registered entity IDs** `gugugaga:bumblebee`, `gugugaga:mason_bee`, `gugugaga:squash_bee`, `gugugaga:leafcutter_bee`; each has natural spawning in an appropriate subset of vanilla biomes, differing hitbox sizes, identity, and basic crop-compatibility logic.

**Important unfinished realism work:** initial entities reuse vanilla Minecraft honeybee AI, body geometry, textures, flight, breeding, and hive targeting. This is not a completed authentic representation of their species. Future steps should supply custom textures and species-specific nesting/foraging/reproduction behavior, rather than treating solitary bees as honey-producing hive inhabitants.

## Sources
- https://www.xerces.org/pollinator-conservation/nesting-resources
- https://xerces.org/blog/how-to-safely-relocate-stem-nesting-bees
- https://extension.umn.edu/agriculture/specialty-crops/commercial-fruit-production/vining-vegetable-growing-guide
- https://www.ars.usda.gov/pacific-west-area/logan-ut/pollinating-insect-biology-management-systematics-research/docs/alfalfa-leafcutting-bee-alcb/
- https://docs.neoforged.net/docs/1.21.1/worldgen/biomemodifier/

## v0.4 — four habitat blocks, pollination-yield state, and smoke-safe honey tools

The 36 crop registrations remain fixed. Twelve fruit-, berry- or seed-producing lines now record successful compatible bee pollination on a blockstate instead of boosting biological growth speed; fully mature pollinated crops drop one additional produce item (a **game balance reward**, not a numerical reproduction of real yields). Vanilla wind- or self-pollinated crops and vegetables harvested for leaves, stems, bulbs or tubers do not gain this bonus. Species pollination roles rely on [University of Minnesota Extension](https://extension.umn.edu/agriculture/specialty-crops/pollination/requirements).

Four new habitat blocks simulate nearby pollinator activity and forage: bumblebee nest, mason bee house, squash bee burrow, leafcutter bee house. They yield small nectar provisions or cocoons, **not honeycomb**. In this v0.4 prototype, the bees still inherit vanilla AI, so habitat stages are proximity-based simulations rather than authentic nesting, egg-laying or bee-in-hive storage.

Honey collection now provides two tools for vanilla beehives/bee nests: a honey harvester that uses one glass bottle from inventory to collect one honey bottle, and a gentle beekeeper brush that uses an offhand bottle for honey or yields three honeycombs without a bottle. Both require a lit campfire smoke column and a honey level of 5. This follows vanilla hive output rather than allowing infinite honey generation.

Research: [Xerces Society — nesting resources](https://xerces.org/pollinator-conservation/nesting-resources); [Minnesota Extension — pollination requirements](https://extension.umn.edu/agriculture/specialty-crops/pollination/requirements); [Minnesota Extension — squash and other cucurbits](https://extension.umn.edu/agriculture/specialty-crops/commercial-fruit-production/vining-vegetable-growing-guide).
