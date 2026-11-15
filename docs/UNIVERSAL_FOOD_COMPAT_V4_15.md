# Universal Food Compatibility — v4.15

## Resolution order

When a player completes eating an item with the vanilla FOOD component, the nutrition resolver applies the following order:

1. **Exact shipped food profile.** The existing Agriculture catalog remains authoritative. All existing item IDs and per-food overrides are preserved.
2. **Common food item tags.** Third-party items are matched against Common (c:) and Forge/NeoForge (forge:) category tags for vegetables, leafy vegetables, fruits, berries, grains, roots, beans, seeds/nuts, meats, fish, eggs, dairy, mushrooms, seaweed, honey, herbs, sweets, beverages, soups, salads and breads.
3. **Registry-path hints.** If no supported tag matches, conservative item-path tokens provide a broad category estimate. This is a gameplay fallback, not a claim about real food composition.
4. **Neutral fallback.** If no category can be inferred, the existing small neutral fallback remains. Unknown foods never receive invented micronutrient-specific claims.

The tag/path resolver combines matching abstract category profiles, clamps each independent nutrient channel to 10 points, and never overrides an exact Agriculture profile. The 27-channel order and all existing NBT reserve keys remain unchanged.

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
