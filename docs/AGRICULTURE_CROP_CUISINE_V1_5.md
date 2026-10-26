# Agriculture Crop Cuisine v1.5

## Purpose
Add alternate recipes for existing crop-based dishes. This update does not register new food items or change existing hunger/saturation values; it gives more of the existing agriculture catalog a practical cooking use.

## Added recipes (24)
- `tomato_egg_stir_fry_with_chives` → `gugugaga:tomato_egg_stir_fry` (gugugaga:tomato + minecraft:egg + gugugaga:chives)
- `pumpkin_rice_with_fava_bean` → `gugugaga:pumpkin_rice` (gugugaga:pumpkin + gugugaga:rice + gugugaga:fava_bean)
- `spinach_omelette_with_leek` → `gugugaga:spinach_omelette` (gugugaga:spinach + minecraft:egg + gugugaga:leek)
- `oat_berry_porridge_with_blackcurrant` → `gugugaga:oat_berry_porridge` (gugugaga:oat + gugugaga:blackcurrant + minecraft:milk_bucket)
- `chili_bean_chili_with_cowpea` → `gugugaga:chili_bean_chili` (gugugaga:cowpea + gugugaga:chili_pepper + gugugaga:tomato)
- `sesame_noodles_with_cucumber` → `gugugaga:sesame_noodles` (gugugaga:sesame + minecraft:wheat + gugugaga:cucumber)
- `garlic_roasted_potatoes_with_parsnip` → `gugugaga:garlic_roasted_potatoes` (gugugaga:garlic + minecraft:potato + gugugaga:parsnip)
- `beetroot_soup_with_jute_mallow` → `gugugaga:beetroot_soup` (minecraft:beetroot + gugugaga:jute_mallow + minecraft:carrot)
- `seaweed_mushroom_soup_with_kale` → `gugugaga:seaweed_mushroom_soup` (gugugaga:kelp_flakes + gugugaga:shiitake_mushroom + gugugaga:kale)
- `fava_bean_soup_with_chicory` → `gugugaga:fava_bean_soup` (gugugaga:fava_bean + gugugaga:chicory + minecraft:carrot)
- `tomato_okra_stew_with_sorghum` → `gugugaga:tomato_okra_stew` (gugugaga:tomato + gugugaga:okra + gugugaga:sorghum)
- `pineapple_tart_with_grape` → `gugugaga:pineapple_tart` (gugugaga:pineapple + gugugaga:grape + minecraft:wheat)
- `grape_berry_compote_with_blackcurrant` → `gugugaga:grape_berry_compote` (gugugaga:grape + gugugaga:blackcurrant + minecraft:honey_bottle)
- `rapini_garlic_saute_with_sesame` → `gugugaga:rapini_garlic_saute` (gugugaga:rapini + gugugaga:garlic + gugugaga:sesame)
- `root_vegetable_hash_with_jicama` → `gugugaga:root_vegetable_hash` (gugugaga:jicama + gugugaga:parsnip + gugugaga:canola_oil)
- `millet_fennel_pilaf_with_chickpea` → `gugugaga:millet_fennel_pilaf` (gugugaga:millet + gugugaga:fennel + gugugaga:chickpea)
- `mushroom_barley_risotto_with_watercress` → `gugugaga:mushroom_barley_risotto` (gugugaga:barley + gugugaga:shiitake_mushroom + gugugaga:watercress)
- `cucumber_mint_salad_with_grape` → `gugugaga:cucumber_mint_salad` (gugugaga:cucumber + gugugaga:mint + gugugaga:grape)
- `seaweed_potato_chowder_with_water_spinach` → `gugugaga:seaweed_potato_chowder` (gugugaga:kelp_flakes + minecraft:potato + gugugaga:water_spinach)
- `blackcurrant_oat_crumble_with_raspberry` → `gugugaga:blackcurrant_oat_crumble` (gugugaga:blackcurrant + gugugaga:oat + gugugaga:raspberry)
- `mung_bean_congee_with_sweet_potato` → `gugugaga:mung_bean_congee` (gugugaga:mung_bean + gugugaga:rice + gugugaga:sweet_potato)
- `napa_cabbage_braise_with_water_spinach` → `gugugaga:napa_cabbage_braise` (gugugaga:napa_cabbage + gugugaga:water_spinach + gugugaga:garlic)
- `chickpea_hummus_with_cilantro` → `gugugaga:chickpea_hummus` (gugugaga:chickpea + gugugaga:sesame + gugugaga:cilantro)
- `buckwheat_pancakes_with_blueberry` → `gugugaga:buckwheat_pancakes` (gugugaga:buckwheat + gugugaga:blueberry + minecraft:egg)

## Validation
`qa/validate_crop_cuisine_v1_5.py` verifies that every output and ingredient is registered, each recipe includes an agriculture crop, recipe JSON matches the manifest, and no shapeless recipe duplicates an existing ingredient pattern (including tag-based recipes).

## Scope
This is a recipe-coverage pass, not a new-item pass. Existing dish sprites, item registrations, nutrition values, and bilingual names are preserved.
