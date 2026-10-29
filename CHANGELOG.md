# Changelog

## v3.6.0-rc.1 — Lunar calendar, four 8-day seasons and crop overwintering

- A full agricultural **32-day world year** synchronized to four existing eight-day Minecraft lunar cycles: Spring (day 1–8), Summer (9–16), Autumn (17–24), Winter (25–32). Each global season begins at moon phase 0 (full moon). Year/date follow the vanilla world day clock; no new clock, timers or SavedData needed.
- Five local biome calendar adaptations, each totaling 32 days: temperate (8/8/8/8), tropical (8/10/8/6), arid (7/11/8/6), cold (7/6/8/11), wet (9/9/8/6). Local spring/summer/autumn/winter can end on different dates, but the world and lunar date stay globally consistent.
- All **108 crops** now use seasonal growth probability gates via their existing horticulture band before old biomes/water/breeding checks. Growth slows or pauses in adverse winter conditions, especially cold regions; no crops are instantly deleted, and existing food IDs and crop loot remain untouched.
- A lightweight glass-canopy/nearby heat source shelter may recover winter growth probability. Checks are bounded and do not force chunk loads. Original vanilla light minimum (9) remains respected.
- New bilingual read-only **/agseason** command shows current year/day, shared season, local season length and exact eight-stage moon phase at the player's location.
- No biome temperatures, real weather, block states or world geometry are globally changed; compatible with other season mods at the world state level, although two crop growth multipliers may stack if multiple season systems are enabled.
- CI adds calendar arithmetic, complete 32-day cycles, resource and integration tests, alongside all prior agriculture, food, nutrients, marine, hive and Fusion regressions.
- Manual Minecraft client acceptance still required, especially across transitions, sleep/time commands and custom modded biomes.


## v3.5.0-rc.1 — Comprehensive gameplay nutrition catalog

- Replace the v3.4 name-based six-group scoring with an explicit packaged, versioned catalog for **403** edible foods: every registered Agriculture food, 50 honey bottles, and over 40 original Minecraft foods.
- **27 distinct game nutrition categories**: five broad macronutrient/hydration/fiber indicators, all **13 recognized vitamins** (A, B1, B2, B3, B5, B6, B7, B9, B12, C, D, E, K), and nine mineral-source categories (Ca, Fe, Mg, K, Zn, I, Se, P, Na).
- Nutrient profiles are explicit 0–8 abstract food contribution units, not calories, vitamin concentrations, RDA goals or medical advice. Unknown third-party edible items get a conservative neutral contribution.
- N key now opens a **five-page graphical nutrition panel** with buttons, 27 colored progress bars and bilingual labels; the original five-page server-authored text report remains for compatibility. All GUI scores arrive in one validated, server-to-client packet.
- Preserve v3.4 player food diversity scores on migration to the new NBT schema; save new per-nutrient values, including after death/clone.
- Two balanced **positive-only milestones**: Haste I for broad variety and Speed I for sustained high variety. Never cause starvation, negative effects or dietary restriction.
- Full regression suite asserts catalog coverage for every declared edible and honey, bilingual translation completeness, explicit zero-overwrite data format, server-side food intake, and packaged JAR integrity.
- No new food items or block IDs; all 108 crop varieties, beekeeping, marine foods, fermentation equipment, old kitchens and optional embedded Fusion visuals are retained.
- The N-key screen now has a real graphical interface, but testing on a Minecraft client remains necessary.


## v3.4.0-rc.1 — Salt geology, pickled foods, soy/vinegar condiments, ecology and nutrition

- Salt deposits can be found through two biome-aware ore/evaporite worldgen features; a mineable salt deposit yields salt crystals and the craftable Salt Block offers reversible 9:1 storage.
- A new Condiment Aging Crock handles ten non-intoxicating, abstract food-preserving preparations: soy sauce, rice vinegar, apple vinegar, mushroom sauce, four salt-pickled vegetables, spiced sauce and seasoned fish sauce. Each uses a 3-stage scheduled-tick process and a consumed secondary resource (food culture starter or salt).
- Twelve additional crafted dishes now consume vinegar, sauces, pickles and salt in recipes that also use older Minecraft and Agriculture ingredients.
- Eight biome-specific worldgen features now introduce small, wild-cultivated farmland crop patches for the eight 3.3 crops. Existing 100 wild-seed sources and new v3.3 crop growth remain unchanged.
- Gameplay nutrition diversity tracks six broad groups server-side in persistent player data. Press **N** to show all scores and balance in the Minecraft chat UI; four of six sufficiently varied groups grant a *positive* Mining Haste effect. There are no deficits, damage or restrictive eating mechanics. Numbers are fictional game balance and are **not actual medical recommendations or human micronutrient diagnostics**.
- 26 individual new item sprites in an independent art generator, full EN/ZH names, and regression contracts that verify legacy content and worldgen layouts.
- Real client testing remains required. This does not yet represent human nutrient biochemistry, individual vitamins, a dedicated graphical nutrition panel, or fermentation food-safety guidance.


## v3.3.0-rc.1 — Food fermentation, fishing, and more farm crops

- **8 additional plantable crops** (now 108 total): black pepper, lemongrass, wasabi, cumin, cardamom, fenugreek, sugar beet and daikon. Each has a harvest item, seed, 8 growth stages, weather-aware growth category, seed recipe and harvest drops.
- **8 new ocean fishing inventory catches**: anchovy, sardine, herring, mackerel, sea bass, sea bream, shrimp and crab. Native Minecraft fishing loot is untouched; ocean fishing has a bounded 1/5 chance of an additional ingredient after catching normal vanilla fish. No extra fish mobs are claimed.
- **1 food-fermentation vat** with ten distinct non-alcoholic food batches, three scheduled maturation stages, safe one-batch state persistence and ingredient/final output recovered if broken. It is a timed Minecraft mechanic, not a real-world food preparation or safety procedure.
- **8 seasoning routes** at the existing mortar and **8 seafood smoking routes** at the existing smoke rack; their reusable tools and heat gates remain in force.
- **18 additional dishes** consuming fermented ingredients, seafood and powdered spices. Vanilla fish, vegetables and earlier Agriculture seafood remain intact.
- Independent generation of **68 new inventory icons and 64 crop stage sprites**, bilingual translations, crafting recipes, seed-start recipes and 120 add-on resource files.
- All old IDs, recipes, crops, dishes, bee/honey mechanics and in-JAR optional Fusion resources kept untouched. Added strict v3.3 regression and generated-resource checks alongside normal Java 21 build and real dedicated-server boot.


## v3.2.0-rc.1 — Five real kitchen tools; zero new workstation blocks

- All twelve previously introduced hand utensils now have direct equipment interactions, including **five new offhand-specialized modes** on four existing workstations.
- Chopping Board: Chef Knife cuts beef/chicken/potatoes/apples/fish; Box Grater shreds carrots, potatoes, beetroot, pumpkin, cucumber and apples.
- Campfire Smoking Rack: Kitchen Tongs make additional smoked prepared meats/fish/mushrooms, while retaining the lit-campfire smoke requirement.
- Mixing Basin: Measuring Cup can prepare measured fruit drinks and sauce bases as an alternative to the original wooden spoon route.
- Stock Pot: Skimmer Spoon clarifies five existing stocks/soup bases while preserving the original Soup Ladle routes and campfire heat requirement.
- Twenty-seven new prepared ingredient IDs and twenty dishes dependent on these prepared materials; original vanilla food items remain unchanged.
- Added 47 distinct generated pixel-art icons and 20 shaped-free food crafting recipes. Independent catalog + graphics generator, new regression tests, and all prior assets preserved.
- No additional placeable blocks, no permanent inventory/ticking systems, no new chunk-loading logic; all transformations continue to be server-authoritative.
- Still subject to manual Minecraft client acceptance; a successful CI run does not claim visually perfect animation.


## v3.1.0-rc.1 — Specialist kitchen tools become functional

- Four new kitchen processing blocks with real server-side interactions: **Whisking Station, Spice Mortar Station, Straining Station, Stock Pot**.
- Reused four formerly decorative v3.0 tools as **functional, nonconsumable offhand requirements**: Wire Whisk, Mortar and Pestle, Kitchen Strainer, Soup Ladle.
- Twenty-four additional intermediate ingredients across whipped bases, spice/seed pastes, filtered juices and simmered stocks. Original Minecraft eggs, cocoa beans and dried kelp now participate directly.
- Twenty-three new finished dishes requiring v3.1 prepared ingredients, with original Minecraft apples, sugar, cookies, eggs, carrots, potatoes and bread.
- Four extra paired idle/working frame models and short server-synchronized particle feedback; stock pot requires a lit smoky campfire.
- Generated 47 unique 16×16 icons and dedicated 6×8 review sheet, independent of the original crop, v2.8, v2.9 and v3.0 art.
- Retained all original 100 crops, 50 floral honeys, 68 original cuisine items, 5 old manual kitchen blocks, 3 v3 animated stations, all IDs and the embedded optional Fusion resource pack.
- CI now tests all 24 new Java mapping routes, required tools, active models, recipes, labels and generated pixels, plus prior contracts, Java 21 compilation and real dedicated-server startup.
- Still uses immediate 1:1 ingredient processing with no persistent machine inventory or real cooking timer. Client visual/interaction acceptance remains necessary.


## v3.0.0-rc.1 — Kitchen utensils, tableware and animated cooking machines

- Three new placeable cooking stations: **dough bench, skillet station and bamboo steamer**, each with independent idle/working baked block models.
- Server-side work interactions switch the frame for 10 ticks, add short-lived visual particles and retain existing sound feedback; no continuously ticking inventories or complex renderer needed.
- The new machines require reusable offhand **rolling pin**, **frying spatula** or **steamer lid** respectively. Skillet and steamer additionally check for native lit-campfire smoke.
- 12 craftable hand utensils, 12 dining ware items, 24 prepared food intermediates and 18 dishes; all have new independent inventory icons and Chinese/English names.
- Recipes use existing Agriculture crops and Minecraft bread, milk, carrots, potatoes, sugar, etc. The earlier 79 kitchen items, 68 cuisine dishes, 100 crop species, bee/honey system and built-in optional Fusion pack remain untouched.
- New v3 structural/pixel/regression QA alongside the existing asset, Java build, Fusion pack, and real dedicated-server smoke gates.
- Tool usage is a lightweight manual interaction mechanic; the remaining decorative utensils and tableware are craftable and some appear in dish recipes. No multi-slot inventory or time-based cooking is implied.


## v2.9.0-rc.1 — Vanilla Minecraft pantry and campfire smoking

- Fifth functional station: campfire smoking rack, with distinctive 3D model and an actual native lit-campfire smoke check.
- Added **23 processing routes starting from vanilla Minecraft ingredients** (carrots, potatoes, beetroot, pumpkin, mushrooms, melon, sweet/glow berries, apples, cocoa beans, seeds, dried kelp, and raw fish/meat), plus six derived mixture stages.
- Added 25 prepared ingredient IDs and 4 edible smoked fish/meat products, taking total kitchen conversions to **45**.
- Added 20 new dishes using both vanilla foods and Agriculture crops, taking the kitchen-only recipe dishes to 32 (68 legacy cuisine dishes remain untouched).
- Updated the kitchen sprite generator to create 79 distinct 16×16 icons and a 6-column, 14-row proof sheet, preserving the original v2.8 registry and art.
- Added v2.9 source/recipe/collision/pixel testing and kept all previous core crop, honey, Fusion and dedicated-server checks.
- No existing world save IDs or legacy recipes removed; Fusion stays optional and built into the one Agriculture JAR.


## v2.8.0-rc.1 — Functional food processing and kitchen progression

- Four distinct placeable workstations: chopping board, hand mill, fruit press and mixing basin, each with actual right-click processing and 3D meshes.
- Sixteen 1:1 ingredient conversions based on the expanded crop catalog; the mixer requires a reusable offhand wooden spoon.
- Twelve new processed-ingredient meals and two dining utensils; added 18 vanilla crafting recipes.
- Thirty independent 16×16 sprites and 6×5 visual proof sheet, separated from the original 68-dish asset generator.
- Added bilingual UI/recipes/models/loot and CI contracts for ID consistency, stage maps, art, and packaging.
- No old crop IDs, recipes, existing cuisine textures, or Fusion pack settings changed. Manual client validation remains open.


## v2.7.0-rc.1 — One-JAR optional Fusion resource integration

- Bundled the complete 1,248-atlas Fusion visual resource pack inside the main mod JAR as `resourcepacks/fusion/`, **without** overwriting regular Minecraft-compatible assets.
- Added client-only pack discovery. The built-in pack becomes active automatically **only** if Fusion is installed; otherwise the base mod runs with its original models.
- Kept the independent ZIP as an optional build/test artifact; no additional Agriculture resource pack is required for players.
- Added byte-for-byte verification between embedded resources and the companion ZIP. A real in-game client visual check remains required before stable publication.

## v2.7.0-rc.1 — Optional Fusion stage-model linkage hardening

- Added the missing 1,200 crop and 40 marine/fungal `fusion:base` model overrides to the **optional** companion resource pack.
- Each model preserves the shipped Java Edition parent, geometry, and original texture references, and explicitly enables the Fusion model loader.
- Expanded packaging QA to confirm model-to-texture binding for all 1,240 stages and byte-for-byte ZIP integrity.
- The independent main mod JAR and gameplay remain unchanged. Actual client rendering with and without Fusion is still a release gate.

## v2.7.0-rc.1 — Packaged-art validation hardening

- Added CI that compares the distributable mod JAR and optional Fusion resource pack at individual pixel level for 1,240 crop, fungal, and marine growth-stage atlases.
- Checks all four 16x16 variants of every atlas for correct mirroring, tone, transparency, and uniqueness; catches mismatched release artifacts without changing visuals or gameplay.

## v2.7 — Fusion marine and fungal visual variation

- Extended the optional Fusion pack to 24 fungal culture growth textures and 16 marine plant growth textures.
- Preserved all generated 16×16 base textures in the main mod JAR; the optional pack supplies 2×2 random atlases with position-based selection.
- Added a marine/fungal variation preview sheet and CI checks for every atlas, metadata file, and ZIP entry.
- No item IDs, recipes, food values, or growth mechanics changed.

## v2.6 — Fusion crop-field variation

- Extended the optional Fusion resource pack to all 1,200 generated crop growth-stage textures, including pollinated stages.
- Each crop-stage texture gets four 16×16 variants in a 2×2 atlas, with per-position selection so the crossed crop model uses one consistent variant.
- Added a 20-crop random-variant proof sheet and CI validation for every atlas, metadata file, and ZIP entry.
- Kept the default mod JAR textures and crop gameplay unchanged; Fusion remains optional and client-side.

## v2.5 — Crop and seed icon depth pass

- Added a restrained pixel-art shadow and bevel pass to all 100 produce and 100 seed icons.
- Rebuilt the produce and seed review sheets as a five-column grid.
- Added a side-by-side crop item contact sheet.
- Added a CSV audit of icon pixel coverage, bounding boxes, and color counts.
- Added CI validation for all 200 icon audit rows and the three review-sheet dimensions.
- No item IDs, recipes, food values, or crop lifecycle mechanics changed.

## v2.4 — Fusion visual overhaul

- Added an optional Fusion resource pack for four existing bee habitat families.
- Added eight random texture atlases and eight idle/active model overrides.
- Improved generated cuisine silhouettes and 50 honey bottle labels.
- Added Fusion dependency resolution and resource-pack validation to CI.
