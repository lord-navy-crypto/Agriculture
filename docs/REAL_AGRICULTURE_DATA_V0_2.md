# Genuine extension agriculture data used by the crop growth model

Reference values **are real germination soil temperature thresholds**, NOT Minecraft biome temperatures or real growth duration. The Minecraft climate system uses them only to classify warm-sensitive seeds when the crop is in a cool biome. All numeric game roll probabilities are explicitly gameplay abstractions.

| Crop | Minimum germination soil temperature (F) |
|---|---|
| corn | 50 |
| pea | 40 |
| onion | 35 |
| lettuce | 35 |
| spinach | 35 |
| tomato | 50 |
| bell_pepper | 60 |
| cucumber | 60 |
| eggplant | 60 |
| cabbage | 40 |

Source: https://extension.psu.edu/seed-and-seedling-biology and https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/yard-and-garden-problems/how-to-prevent-seedling-damping-off

Crop seeds also require soil moisture/oxygen, and mature plants have different growth requirements from germination; this mod does not equate them. Minecraft watered farmland and block light remain vanilla rules.

Rice paddy gameplay rule (v0.4): the rice crop checks for freshwater within a 3-block horizontal radius at crop height or one block below. This is explicitly a simplified irrigated-lowland proxy. The International Rice Research Institute documents alternate wetting and drying, and also discusses aerobic rice systems; therefore the rule is not presented as universal rice physiology. Sources: https://ghgmitigation.irri.org/mitigation-technologies/alternate-wetting-and-drying and https://books.irri.org/RT8_3_content.pdf

Future phase: seasonal watering cycles, soil pH, nutrient depletion, plant reproductive stage and flowering time. Measured source-dependent numbers should be used only for the corresponding measured quantity; do not invent climate conversion from biome.
