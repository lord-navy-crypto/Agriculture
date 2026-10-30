# Gugu Gaga Agriculture & Apiaries

A farming, food, beekeeping, marine-plant, and fungi expansion for **Minecraft 1.21.1 / NeoForge 21.1.259+ / Java 21**.

## Current content (v3.9 evidence-informed nutrition release candidate)

- **108 plantable crops** in total. The original **100 plantable crops** retain their existing seed, produce, eight-stage visuals and pollen-marked reproductive resources; **eight new spice/vegetable crops** are added with independent seed items, eight-stage textures, and harvest recipes.
- Original 100-crop generator remains untouched. The eight new v3.3 crop lines use a separate art pipeline and climate profile adapter.
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

## v3.8 field water sampling and solar salt production

- The **Field Sampling Bucket** is a new independent tool, crafted from an iron bucket and glass bottle. Right-click a water source to collect 1000 mB of a non-drinkable, tagged sample. The collected item records source biome and approximate game salinity; ordinary vanilla buckets, vanilla water and the existing v3.7 drinking flask stay unchanged.
- Normal freshwater samples are 0 ppt, beach samples 12 ppt, and ocean samples approximately 33–37 ppt. These are game classifications, not measurements. For now, only samples at or above 5 ppt can be evaporated for salt.
- Fill a **Salt Evaporation Pan** with a brackish/saltwater sample. It advances through brine, concentration and crystals using scheduled ticks, daylight and dry weather. A lit campfire or magma block below offers a faster heated route. Retrieve one existing `sea_salt` per sample batch. The sample is consumed and the sampling bucket returned. No new fluids or automatic duplicated outputs.
- Nutrition was originally represented by 27 game scores. The newer v3.9 individual-reserve design below supersedes v3.8's group-average penalty logic.
- **GUI troubleshooting:** In Minecraft Options → Accessibility Settings, set Menu Background Blur to **0** if a modded GUI looks blurry. v3.7.0-rc.2 responsive nutrition text remains in place.

## v3.9.0-rc.1: individually modeled nutrient reserves (research-informed, nonclinical)

- The established **27 indicators remain**. The **25 food-related reserves** now receive independent food inputs, daily game consumption, replenishment, and sustained-shortage windows. **Water and sodium** continue to use the preexisting v3.7 water/salt subsystem, preventing duplicate dehydration or sodium effects.
- **B1 declines faster than stored B12**; long-stored A, D, E, calcium and iron follow different game kinetics. These times are deliberately compressed and **not** real biological half-lives, medical symptoms or dose thresholds.
- Food is recorded upon completed consumption, then contributes to reserve recovery on the following Minecraft day. Eating one food no longer instantly restores a depleted tissue-like score.
- Feedback is assessed **per nutrient**; a high score in one nutrient cannot conceal persistent low iron or B12 in a group average. N-key pages 1–5 give 27 individually localized physiological roles and long-term status on hover; page 7 retains a high-level overview.
- Most shortages only inform the player. A few prolonged functional limitations receive at most one short, mild game effect. No disease diagnoses, damage, toxic vitamin penalties, or magical night-vision/resistance bonuses. Existing older **food-variety perks** remain a separately identified game reward.
- Sodium-rich foods are not automatically iodine-rich: sea salt made in the evaporation pan is not iodized salt. The mod does not pretend its 403 food profiles are laboratory-verified nutrient masses.
- Current limitations: no measured mg/g, bioavailability, individual age or illness assumptions, actual time-based clinical deficiency prediction, or medical recommendations. See [physiology design and evidence](docs/NUTRITION_PHYSIOLOGY_V3_9.md).
- GUI blur: Options → Accessibility → Menu Background Blur → 0 if required.

## Build and validation

Run with Java 21:

```sh
gradle --no-daemon --stacktrace build
python3 qa/validate_release_metadata_v2_7.py build/libs
```

GitHub Actions additionally checks agricultural resources, crop/seed visual audits, Fusion model and atlas consistency, and real NeoForge dedicated-server startup. The visual contact sheets are uploaded as CI artifacts.

**Development status:** v3.5 is an automated-test-verified release candidate, not proof of exhaustive manual Minecraft client rendering checks. Test the base and optional Fusion configurations before public distribution.

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
- [v3.4 salt deposits, aged condiments, natural farmland and N-key nutrition](docs/SALT_CONDIMENT_NUTRITION_V3_4.md)
- [v3.5 comprehensive gameplay nutrition: 27 indicators, 403 foods, N-key graphical panel and saved-data migration](docs/FULL_NUTRITION_V3_5.md)
- [v3.6 32-day lunar seasons: 4×8 days, habitat-specific calendars, greenhouse overwintering](docs/LUNAR_SEASONS_V3_6.md)
- [v3.7 fixed nutrition inventory button, survival drinking water, hydration and sodium balance](docs/WATER_AND_SALT_V3_7.md)
