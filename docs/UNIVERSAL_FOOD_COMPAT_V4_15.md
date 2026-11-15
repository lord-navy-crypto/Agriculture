# Universal Food Compatibility — v4.15

## Resolution order

When a player completes eating an item with the vanilla FOOD component, the nutrition resolver applies the following order:

1. **Exact shipped food profile.** The existing Agriculture catalog remains authoritative. All existing item IDs and per-food overrides are preserved.
2. **Common food item tags.** Third-party items are matched against Common (c:) and Forge/NeoForge (forge:) category tags for vegetables, leafy vegetables, fruits, berries, grains, roots, beans, seeds/nuts, meats, fish, eggs, dairy, mushrooms, seaweed, honey, herbs, sweets, beverages, soups, salads and breads.
3. **Registry-path hints.** If no supported tag matches, conservative item-path tokens provide a broad category estimate. This is a gameplay fallback, not a claim about real food composition.
4. **Neutral fallback.** If no category can be inferred, the existing small neutral fallback remains. Unknown foods never receive invented micronutrient-specific claims.

The tag/path resolver combines matching abstract category profiles, clamps each independent nutrient channel to 10 points, and never overrides an exact Agriculture profile. The 27-channel order and all existing NBT reserve keys remain unchanged.

## Automatic scan and ingredient ratio calculation

On server start and on a datapack reload, the module:

1. Enumerates every registered Item with the Minecraft FOOD component.
2. Enumerates the loaded server recipe manager and considers recipes that actually produce an edible item.
3. Extracts each recipe's ingredient slots and resolves tag alternatives deterministically. Repeated ingredient slots are counted repeatedly; each result is divided by the output stack count to produce per-item game points.
4. Resolves nested ingredient dependencies iteratively (up to eight bounded passes). Unknown ingredient slots are recorded; a recipe is accepted only when at least two thirds of its ingredient slots have resolved profiles.
5. Protects manually curated Agriculture profiles above all inferred values; unresolved foods use tag/ID inference, then the neutral fallback.
6. Writes a complete JSON report to the game world's root folder: nutrition_food_compat_report.json. The report includes all detected edible item IDs, confidence source, 27-channel scores, ingredient alternatives, known ingredient slot counts, output quantity and coverage statistics.
7. Sends server-derived profile snapshots to clients in bounded packets for accurate N-key food tooltips after login and datapack reload. A data reload does not require a full game restart to update the report.

Example (fictional game scores): if a recipe uses one edible grain item and two edible vegetable items and outputs three meals, its per-meal nutrient scores are estimated from (1 × grain + 2 × vegetable) / 3; each channel is capped at 10 game points.

The scanner does not assume access to actual ingredient mass or food labels, and ordinary Minecraft recipes usually specify slots, not grams. Its output is a documented **per-slot / output-stack game approximation**, not an actual dietary or nutrition calculation. Recipes with dynamic outputs, unsupported custom processing mechanics or no declared ingredients may remain unclassified. The report lists these foods under tag/ID or neutral resolution rather than inventing fake recipe quantities.

## Water and sodium

Hydration and sodium remain exclusively owned by WaterSaltV37 / WaterSaltProfilesV37. Third-party beverages, soups, fruit/vegetable foods, pickled/salted foods and common tagged categories receive conservative game-point estimates through the same compatibility resolver. The 25 independent nutrient channels do not duplicate hydration or sodium.

## Nutrition Master Toggle — v4.14

The existing world-persistent Nutrition Mode remains enabled by default and can be controlled from the N-key dashboard or:

- /gamerule gugugagaNutrition false
- /gamerule gugugagaNutrition true

OFF pauses nutrition reserves, water/salt metabolism, effects and nutrition UI hints while preserving stored values. Farms, crops, ordinary hunger, food items, recipes and kitchen machines remain available. The N-key dashboard remains reachable to turn the system back ON. The server owns the state; single-player world owners and dedicated-server operators can change it.

## Compatibility boundaries

- Exact catalog profiles are intentionally not recalculated from recipes; this protects established hand-authored balance.
- Third-party compatibility is best when mods publish Common or Forge food tags.
- Registry-path inference is deliberately broad and falls back to neutral when no recognizable category exists.
- This is a Minecraft gameplay abstraction. Points are not calories, nutrient mass, daily values, or medical advice.

## Validation

The v4.15 QA checks catalog/category parity, exact-profile precedence, common/Forge tag coverage, registry-path fallback, water/sodium separation, consumption/tooltip parity, bilingual UI, and the existing v4.14 toggle contracts. The packaged-JAR validator checks that both the resolver and toggle classes plus the 27-channel catalog and translations are actually present in the distributable JAR.

A CI build and dedicated-server boot do not replace manual client acceptance. Before release, test at least one tagged food, one path-inferred food, one unclassified food, one beverage, one salted/pickled food, and ON/OFF persistence in a copied test world.
