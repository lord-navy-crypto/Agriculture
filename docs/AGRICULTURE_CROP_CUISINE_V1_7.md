# Agriculture Crop Cuisine v1.7

## Purpose
Expand practical uses for the existing crop catalog by connecting marine plants, seafood, mushrooms, vegetables, grains, berries, and varietal honey to existing dishes. This is a recipe-only release: no new item registrations, dish items, sprites, or food balance changes.

## Added recipes (24)
- `seafood_rice_bowl_with_bok_choy` → `gugugaga:seafood_rice_bowl` × 1 — gugugaga:fish_fillet + gugugaga:rice + gugugaga:bok_choy
- `nori_rice_roll_with_spinach` → `gugugaga:nori_rice_roll` × 2 — gugugaga:nori_sheet + gugugaga:rice + gugugaga:spinach
- `seaweed_salad_with_sesame` → `gugugaga:seaweed_salad` × 2 — gugugaga:sea_lettuce + gugugaga:wakame + gugugaga:sesame
- `seaweed_potato_chowder_with_kale` → `gugugaga:seaweed_potato_chowder` × 1 — gugugaga:sea_lettuce + minecraft:potato + gugugaga:kale
- `seafood_broth_with_watercress` → `gugugaga:seafood_broth` × 1 — gugugaga:fish_fillet + gugugaga:kelp_flakes + gugugaga:watercress
- `seafood_broth_with_pumpkin` → `gugugaga:seafood_broth` × 1 — gugugaga:fish_fillet + gugugaga:kelp_flakes + gugugaga:pumpkin
- `grilled_fish_skewer_with_chili_pepper` → `gugugaga:grilled_fish_skewer` × 1 — gugugaga:fish_fillet + minecraft:stick + gugugaga:chili_pepper
- `seafood_rice_bowl_with_water_spinach` → `gugugaga:seafood_rice_bowl` × 1 — gugugaga:fish_fillet + gugugaga:rice + gugugaga:water_spinach
- `seafood_broth_with_enoki` → `gugugaga:seafood_broth` × 1 — gugugaga:fish_fillet + gugugaga:kelp_flakes + gugugaga:enoki_mushroom
- `mushroom_rice_bowl_with_swiss_chard` → `gugugaga:mushroom_rice_bowl` × 1 — gugugaga:shiitake_mushroom + gugugaga:rice + gugugaga:swiss_chard
- `mushroom_barley_risotto_with_enoki` → `gugugaga:mushroom_barley_risotto` × 1 — gugugaga:barley + gugugaga:enoki_mushroom + minecraft:milk_bucket
- `mushroom_medley_with_truffle` → `gugugaga:mushroom_medley` × 2 — gugugaga:shiitake_mushroom + gugugaga:oyster_mushroom + gugugaga:truffle
- `truffle_pasta_with_basil` → `gugugaga:truffle_pasta` × 1 — gugugaga:truffle + minecraft:wheat + gugugaga:basil
- `lion_mane_steak_with_sweet_potato` → `gugugaga:lion_mane_steak` × 1 — gugugaga:lion_mane_mushroom + gugugaga:sweet_potato + minecraft:wheat
- `enoki_soup_with_watercress` → `gugugaga:enoki_soup` × 1 — gugugaga:enoki_mushroom + gugugaga:watercress + minecraft:carrot
- `mushroom_sauce_with_garlic` → `gugugaga:mushroom_sauce` × 1 — gugugaga:porcini_mushroom + minecraft:brown_mushroom + gugugaga:garlic
- `mushroom_rice_bowl_with_wakame` → `gugugaga:mushroom_rice_bowl` × 1 — gugugaga:shiitake_mushroom + gugugaga:rice + gugugaga:wakame
- `seaweed_mushroom_soup_with_pumpkin` → `gugugaga:seaweed_mushroom_soup` × 1 — gugugaga:kelp_flakes + gugugaga:shiitake_mushroom + gugugaga:pumpkin
- `pumpkin_pie_with_lavender_honey` → `minecraft:pumpkin_pie` × 1 — gugugaga:pumpkin + minecraft:egg + gugugaga:lavender_honey_bottle
- `fruit_salad_with_blackcurrant_honey` → `gugugaga:fruit_salad` × 1 — gugugaga:grape + gugugaga:strawberry + gugugaga:blackcurrant_honey_bottle
- `herbal_tea_with_wildflower_honey` → `gugugaga:herbal_tea` × 1 — gugugaga:mint + gugugaga:wildflower_honey_bottle + minecraft:sugar
- `grain_porridge_with_buckwheat_honey` → `gugugaga:grain_porridge` × 1 — gugugaga:oat + gugugaga:buckwheat_honey_bottle + minecraft:milk_bucket
- `honey_mustard_dressing_with_clover_honey` → `gugugaga:honey_mustard_dressing` × 1 — gugugaga:mustard + gugugaga:clover_honey_bottle + minecraft:sugar
- `sweet_potato_roast_with_safflower_honey` → `gugugaga:sweet_potato_roast` × 1 — gugugaga:sweet_potato + gugugaga:safflower_honey_bottle + gugugaga:canola_oil

## Coverage
- 9 marine-forward variants, including seaweed, fish, grains, leafy crops, and fungi
- 8 fungal-forward variants, including mushroom/grain, mushroom/vegetable, and marine-fungal combinations
- 6 varietal-honey alternatives for existing desserts, tea, porridge, dressing, and roasted roots
- Existing output items and their hunger/saturation values are preserved

## Validation
`qa/validate_crop_cuisine_v1_7.py` verifies the manifest count and unique IDs, registered custom ingredients and outputs, recipe JSON/manifest parity, crop-food participation, and shapeless ingredient-pattern collisions against existing recipes.
