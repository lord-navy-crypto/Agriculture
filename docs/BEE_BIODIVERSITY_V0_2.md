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
