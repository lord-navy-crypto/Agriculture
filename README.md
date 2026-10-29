# Gugu Gaga Agriculture & Apiaries

A farming, food, beekeeping, marine-plant, and fungi expansion for **Minecraft 1.21.1 / NeoForge 21.1.259+ / Java 21**.

## Current content (v3.3 ocean, spice crops and food fermentation release candidate)

- **108 plantable crops** (100 pre-existing + 8 new spice/vegetable crops), each with dedicated seed and harvested produce items, crop-specific silhouettes, growth textures, and recipes.
- Eight growth-stage textures per crop plus four pollen-marked textures for reproductive stages.
- **100 wild seed sources** that spawn naturally in compatible Overworld biomes.
- **50 honey varieties** with distinct floral sources and custom bottle graphics.
- **68 original crop-cuisine items** with recipes and tailored, reproducible icons.
- **Five legacy functional kitchen workstations** (chopping board, hand mill, fruit press, mixing basin, campfire smoking rack), **45 ingredient transformations** and **32 kitchen dishes**, plus two dining utensils. The vanilla-pantry update includes original Minecraft carrot, potato, beetroot, pumpkin, mushrooms, melon, apple, berries, cocoa, seeds, kelp, fish and meats.
- The campfire smoking rack only processes raw cod, salmon, beef or chicken when real lit campfire smoke is detected. The original 68 dishes and 100 crops remain intact.
- Marine farming, edible sea plants, and fungal cultures with dedicated textures and growth-stage visuals.
- Distinct pollinator and beekeeping content, including four placeable bee habitat designs.
- Four organized Creative Mode tabs: Agriculture, Beekeeping, Cuisine, and Marine & Fungi.
- English and Chinese item names.

## Optional Fusion visual enhancement

The base mod uses standard Minecraft PNG and JSON resources and **does not require Fusion**.

**One downloadable Gugu Gaga JAR includes both standard art and the full enhanced Fusion pack.** Its 1,240 stage-model overrides retain original crop/plant model geometry and are kept under the embedded `resourcepacks/fusion/` directory, separate from the default vanilla-compatible assets.

1. Install **only the Gugu Gaga Agriculture JAR** in `mods` for all standard visuals and gameplay.
2. **Optional:** install [Fusion (Connected Textures)](https://www.curseforge.com/minecraft/mc-mods/fusion-connected-textures) for NeoForge 1.21.1 in the client's `mods` folder. When Fusion is present, the already-bundled enhanced visuals automatically activate. There is no second Agriculture resource pack to install or enable.
3. Without Fusion, the advanced pack is never registered, so all original vanilla-compatible models and textures are used.

The standalone `gugugaga-fusion-visuals-v2.7.zip` remains a separate generated artifact for legacy/manual testing, **not required** for regular installation. Do **not** enable that standalone ZIP at the same time as the automatically enabled built-in pack. Fusion itself is third-party, remains optional and is never redistributed. This is not a claim of completed in-game visual acceptance; see Issue #61.

## Build and validation

Run with Java 21:

```sh
gradle --no-daemon --stacktrace build
python3 qa/validate_release_metadata_v2_7.py build/libs
```

GitHub Actions additionally checks agricultural resources, crop/seed visual audits, Fusion model and atlas consistency, and real NeoForge dedicated-server startup. The visual contact sheets are uploaded as CI artifacts.

**Development status:** v3.3 is an automated-test-verified release candidate, not proof of exhaustive manual Minecraft client rendering checks. Test the base and optional Fusion configurations before public distribution.

## References

- [Changelog](CHANGELOG.md)
- [Fusion visual overhaul](docs/FUSION_VISUAL_OVERHAUL_V2_4.md)
- [Crop and seed icon visual audit](docs/AGRICULTURE_CROP_ICON_DEPTH_V2_5.md)
- [Chinese player guide](docs/AGRICULTURE_PLAYER_GUIDE_ZH_CN.md)
- [Functional kitchen equipment and processing](docs/KITCHEN_PROCESSING_V2_8.md)
- [Vanilla Minecraft pantry, smoke rack, and 20 new meals](docs/VANILLA_PANTRY_KITCHEN_V2_9.md)
- [Kitchen v3 utensils, tableware, animated stations and prepared meals](docs/KITCHEN_TOOLS_ANIMATIONS_V3_0.md)
- [Kitchen v3.1 four functional specialist tools and 23 dishes](docs/KITCHEN_SPECIALISTS_V3_1.md)
- [Kitchen v3.2: five specialized offhand tool modes, no new blocks](docs/KITCHEN_TOOL_MODES_V3_2.md)
- [v3.3 spice crop, ocean fishing, food-fermentation and seasoning guide](docs/FOOD_ECOLOGY_V3_3.md)
