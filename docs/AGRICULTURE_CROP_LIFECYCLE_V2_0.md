# Agriculture crop lifecycle v2.0

## New crop lines

v2.0 adds eight crop lines, bringing the complete catalog to 96:

- **Celeriac:** cool-season storage root, with root-specific produce art.
- **Purslane:** warm-season succulent green with repeat-cut leaf harvest.
- **Chayote:** climbing cucurbit represented as a compact crop cycle; fruit harvest repeats, and the crop can receive the orchard pollination bonus.
- **Grape:** berry crop represented as a compact vine; fruit harvest repeats and the berry pollination group applies.
- **Tea:** repeat-cut leaf crop, representing young leaves and buds rather than a full shrub lifecycle.
- **Coffee:** harvested item represents coffee seeds/beans; the crop cycle intentionally compresses a multi-year shrub and fruiting period.
- **Rapini:** cool-season brassica harvested as tender shoots; repeat cutting models regrowth after careful harvest.
- **Napa cabbage:** cool-season heading brassica with a dedicated compact-head silhouette.

Each new line has a registered block and planting item, harvested item, climate-band classification, eight ages, pollination-state blockstate coverage, a seed recipe, mature-only produce loot, bilingual names, compost-map entries, crop-specific stage art and separate seed/produce icons. Climate bands are relative gameplay categories, not measured in-game temperatures.

## Exact asset contract

The Gradle generator reads the v2.0 profile as a declared build input. It rejects incomplete mappings and verifies:
- 96 unique crop IDs and 96 crop-specific palettes, leaf forms, harvested-organ mappings and seed forms;
- 1,152 generated stage/model assets (96 crops × 8 ages plus four pollinated-stage variants per crop);
- 192 distinct crop/seed inventory icons;
- crop blockstates, seed recipes, mature-only loot, translations and compostability through the QA suite.

The generated PNGs are packaged in the mod JAR. CI compilation and dedicated-server startup validate resource/runtime wiring; a fresh-world graphical client pass is still required to judge in-world silhouette readability.
