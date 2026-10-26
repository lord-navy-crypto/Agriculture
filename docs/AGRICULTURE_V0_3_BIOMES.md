# Agriculture and Apiaries v0.3: biomes, crops, and pollinator ecology

**Minecraft 1.21.1 / NeoForge**. Extends the existing v0.2 content, without touching `main`.

## New plantable crops (12; 36 total)

| Crop | Growth climate (gameplay) | Harvested part | Bee growth bonus? |
|---|---|---|---|
| millet (小米) | DRY | grain | No |
| buckwheat (荞麦) | COOL | grain | Yes; late growth only |
| chickpea (鹰嘴豆) | TEMPERATE | legume | No |
| lentil (小扁豆) | TEMPERATE | legume | No |
| radish (萝卜) | COOL | root | No |
| turnip (芜菁) | COOL | root | No |
| sweet_potato (红薯) | WARM | tuber | No |
| kale (羽衣甘蓝) | COOL | leaf | No |
| zucchini (西葫芦) | WARM | fruit | Yes; late growth only |
| raspberry (覆盆子) | TEMPERATE | berry | Yes; late growth only |
| cranberry (蔓越莓) | COOL | berry | Yes; late growth only |
| sesame (芝麻) | DRY | oilseed | Yes; late growth only |

Minecraft farmland + normal light remains required. **Rice has an additional paddy-water gate:** growth only proceeds when freshwater is found within a 3-block horizontal radius at crop height or the farmland level below. This is a simplified irrigated-lowland gameplay model, not a claim that every rice variety requires standing water. IRRI documents alternate wetting and drying as well as aerobic rice systems. Seeds can be found through wild populations (four kinds) or unlocked through simplified crafting conversions; vanilla textures are **temporary placeholders**. The crop climate system adjusts relative chance of vanilla growth; it is not soil thermodynamics. No plant matures in a real agricultural number of days.

## Wild forage and biomes

- `wild_millet`: plains, savanna, savanna_plateau; placed feature chance 1/23 per placement evaluation, with an additional patch distribution
- `wild_radish`: meadow, plains, forest; placed feature chance 1/20 per placement evaluation, with an additional patch distribution
- `wild_raspberry`: forest, birch_forest, taiga; placed feature chance 1/18 per placement evaluation, with an additional patch distribution
- `wild_sesame`: savanna, savanna_plateau, plains; placed feature chance 1/28 per placement evaluation, with an additional patch distribution

These are *fictional wild relatives / obtainable seed proxies*. Modern cultivar seeds cannot be assumed to grow wild or at the same rate. Biome matching is a Minecraft analogue, never a country-of-origin or invasion claim.

## Bee expansion (6 IDs total)

- Carpenter bee: forest, flower forest, jungle, sparse jungle, plains. General large-bodied flower visitor.
- Mining bee: meadow, plains, sunflower plains, savanna, flower forest. Many real mining bees nest in ground; the implementation currently does **not** have ground-nest AI.
- Existing bumblebee, mason bee, squash bee, leafcutter bee and vanilla bee remain.

**Known limitation:** Both new bees inherit vanilla `Bee` movement/hive behavior and use the default vanilla bee renderer/model. They are new game entity IDs with unique spawn habitats and crop compatibility, not fully simulated bee species. Real solitary-bee nesting, custom models, pollen behavior and habitat lifecycle remain future tasks.

## Pollination and crop biology corrections

- Onion bulbs do **not** receive the bee growth modifier. Bees can help onion *seed production after flowering*, which this crop block does not yet simulate.
- Cotton fiber and flax material are no longer edible.
- Pollination bonus is restricted to crops with `age >= 4` in a reduced-frequency entity check. It is a *small gameplay shortcut*, not a quantified pollination-dependent yield.
- A squash bee can assist zucchini and cucumber, within the existing heuristic pollinator design.

## Real agricultural reference points

UMN Extension provides days-to-maturity ranges for **radish 30–60**, **turnip 50–60**, **kale 40–65** and **summer squash 50–65**; these are stored with an explicit source in the JSON, but never equated to Minecraft crop ticks. Variety, locality, season and management affect real duration.
- UMN Extension vegetable planting guide: https://extension.umn.edu/garden-and-home/yard-and-garden/gardening-in-minnesota/planting-the-vegetable-garden
- IRRI, irrigated rice and alternate wetting/drying: https://ghgmitigation.irri.org/mitigation-technologies/alternate-wetting-and-drying
- IRRI, soil and water management in rice: https://ricetoday.irri.org/healthy-soils-healthy-rice/
- UMN Extension climate resilience crop maturity table: https://extension.umn.edu/agriculture/specialty-crops/vegetable-farming/climate-resilience-resources-for-vegetable-growers-in-minnesota
- USDA NRCS habitat guidance: https://www.nrcs.usda.gov/sites/default/files/2024-04/19-038_02_HAG_Yard-Park-Garden.pdf
- Xerces Society ground-nesting bee background: https://www.xerces.org/blog/why-do-some-bees-dig

See `data/gugugaga/agronomy/crop_profiles_v0_3.json` and `biome_habitats_v0_3.json`. Validate with `python3 qa/validate_agriculture_v0_3.py`, then `gradle build` and fresh-world client testing. The 1-in-N probabilities are game tuning, not ecological observations.
