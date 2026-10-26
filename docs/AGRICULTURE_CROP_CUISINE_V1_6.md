# Agriculture Crop Cuisine v1.6

## Purpose
Extend existing dishes with crop-specific alternatives, prioritizing legumes, leafy vegetables, roots, grains, berries, and culinary herbs. This is a recipe-only expansion: no items are registered and no existing food values are changed.

## Added recipes (18)
- `soybean_patties_with_peanut` → `gugugaga:soybean_patties` (gugugaga:soybean + gugugaga:peanut + minecraft:egg)
- `lentil_curry_with_spinach` → `gugugaga:lentil_curry` (gugugaga:lentil + gugugaga:spinach + gugugaga:garlic)
- `eggplant_curry_with_bell_pepper` → `gugugaga:eggplant_curry` (gugugaga:eggplant + gugugaga:bell_pepper + gugugaga:tomato)
- `roasted_root_vegetables_with_turnip` → `gugugaga:roasted_root_vegetables` (gugugaga:turnip + gugugaga:radish + gugugaga:parsnip)
- `rice_vegetable_bowl_with_green_bean` → `gugugaga:rice_vegetable_bowl` (gugugaga:rice + gugugaga:green_bean + gugugaga:pea)
- `garden_salad_with_celery_lettuce` → `gugugaga:garden_salad` (gugugaga:celery + gugugaga:lettuce + minecraft:beetroot)
- `berry_pie_with_strawberry_blackberry` → `gugugaga:berry_pie` (gugugaga:strawberry + gugugaga:blackberry + minecraft:sugar)
- `grain_porridge_with_teff_amaranth` → `gugugaga:grain_porridge` (gugugaga:teff + gugugaga:amaranth + minecraft:milk_bucket)
- `farmers_skillet_with_broccoli_pea` → `gugugaga:farmers_skillet` (gugugaga:broccoli + gugugaga:pea + minecraft:potato)
- `pumpkin_soup_with_celeriac` → `gugugaga:pumpkin_soup` (gugugaga:celeriac + gugugaga:pumpkin + gugugaga:garlic)
- `fruit_salad_with_cranberry_melon` → `gugugaga:fruit_salad` (gugugaga:cranberry + gugugaga:melon + minecraft:apple)
- `vegetable_stew_with_chayote_purslane` → `gugugaga:vegetable_stew` (gugugaga:chayote + gugugaga:purslane + minecraft:beetroot)
- `stuffed_pepper_with_zucchini_and_onion` → `gugugaga:stuffed_pepper` (gugugaga:bell_pepper + gugugaga:zucchini + gugugaga:onion)
- `cornbread_with_sweet_corn` → `gugugaga:cornbread` (gugugaga:sweet_corn + minecraft:wheat + minecraft:egg)
- `cabbage_rolls_with_quinoa_and_chard` → `gugugaga:cabbage_rolls` (gugugaga:cabbage + gugugaga:quinoa + gugugaga:swiss_chard)
- `quinoa_salad_with_arugula` → `gugugaga:quinoa_salad` (gugugaga:quinoa + gugugaga:arugula + gugugaga:cucumber)
- `herb_bread_with_parsley_dill` → `gugugaga:herb_bread` (gugugaga:parsley + gugugaga:dill + minecraft:wheat)
- `tomato_sauce_with_shallot_basil` → `gugugaga:tomato_sauce` (gugugaga:tomato + gugugaga:shallot + gugugaga:basil)

## Validation
`qa/validate_crop_cuisine_v1_6.py` checks the manifest count and unique IDs, confirms that all custom ingredients and outputs are registered, verifies each recipe JSON against the manifest, requires at least one agriculture crop food per recipe, and detects shapeless ingredient-pattern collisions including item-tag recipes.

## Scope
Existing dish items, sprites, bilingual names, and hunger/saturation values are preserved.
