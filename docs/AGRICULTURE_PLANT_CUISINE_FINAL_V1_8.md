# Agriculture Plant-Based Cuisine — Final v1.8

## Scope

Recipe-only completion pass: 88 dedicated crop-hybrid plant-based recipe variants plus 12 recipes made entirely from vanilla plant-derived ingredients. No new item registrations, dish items, textures, or food-balance changes are introduced; existing registered dishes and generated art are reused.

## Coverage

| Check | Expected result |
|---|---:|
| Registered crop blocks audited | 100 |
| Edible crop food IDs covered by a dedicated plant-only recipe | 88 / 88 |
| Crop + crop + vanilla plant recipes | 88 |
| Vanilla-only plant recipes | 12 |
| New recipe JSON files | 100 |
| New item registrations or food-value changes | 0 |
| Animal-derived ingredients in new recipes | 0 |

## Asset verification

The existing Gradle asset pipeline generates and checks 100 unique crop produce icons, 100 unique seed icons, 1,200 growth/pollination textures, 1,200 crop models, 50 distinct floral-honey bottle sprites, 68 distinct cuisine icons and a review contact sheet. The existing marine/fungi generator validates 24 unique inventory icons and its culture/marine-crop assets.

The v1.8 validator checks all 100 crop blockstates, seed/produce item models, bilingual item/block names, loot-table references, seed recipes, ingredient registration, all output dish model references, and shapeless-recipe collisions. CI also runs generated-art checks, NeoForge compilation/package, and a fresh real NeoForge 1.21.1 server boot test.

## Dedicated crop-hybrid recipes

| Crop | Recipe ID | Companion crop | Vanilla plant ingredient | Output dish |
|---|---|---|---|---|
| `corn` | `plant_based_corn_medley` | `sorghum` | `minecraft:carrot` | `grain_porridge` |
| `rice` | `plant_based_rice_medley` | `millet` | `minecraft:potato` | `rice_vegetable_bowl` |
| `barley` | `plant_based_barley_medley` | `buckwheat` | `minecraft:apple` | `quinoa_salad` |
| `oat` | `plant_based_oat_medley` | `quinoa` | `minecraft:carrot` | `millet_fennel_pilaf` |
| `rye` | `plant_based_rye_medley` | `amaranth` | `minecraft:potato` | `cornbread` |
| `sorghum` | `plant_based_sorghum_medley` | `sweet_corn` | `minecraft:apple` | `sweet_corn_fritters` |
| `millet` | `plant_based_millet_medley` | `teff` | `minecraft:carrot` | `buckwheat_pancakes` |
| `buckwheat` | `plant_based_buckwheat_medley` | `corn` | `minecraft:potato` | `grain_porridge` |
| `quinoa` | `plant_based_quinoa_medley` | `rice` | `minecraft:apple` | `rice_vegetable_bowl` |
| `amaranth` | `plant_based_amaranth_medley` | `barley` | `minecraft:carrot` | `quinoa_salad` |
| `sweet_corn` | `plant_based_sweet_corn_medley` | `oat` | `minecraft:potato` | `millet_fennel_pilaf` |
| `teff` | `plant_based_teff_medley` | `rye` | `minecraft:apple` | `cornbread` |
| `soybean` | `plant_based_soybean_medley` | `chickpea` | `minecraft:carrot` | `bean_stew` |
| `pea` | `plant_based_pea_medley` | `lentil` | `minecraft:potato` | `lentil_curry` |
| `peanut` | `plant_based_peanut_medley` | `green_bean` | `minecraft:beetroot` | `chickpea_hummus` |
| `chickpea` | `plant_based_chickpea_medley` | `black_bean` | `minecraft:carrot` | `soybean_patties` |
| `lentil` | `plant_based_lentil_medley` | `adzuki_bean` | `minecraft:potato` | `roasted_chickpeas` |
| `green_bean` | `plant_based_green_bean_medley` | `mung_bean` | `minecraft:beetroot` | `adzuki_bean_sweet_soup` |
| `black_bean` | `plant_based_black_bean_medley` | `cowpea` | `minecraft:carrot` | `mung_bean_congee` |
| `adzuki_bean` | `plant_based_adzuki_bean_medley` | `fava_bean` | `minecraft:potato` | `chili_bean_chili` |
| `mung_bean` | `plant_based_mung_bean_medley` | `soybean` | `minecraft:beetroot` | `bean_stew` |
| `cowpea` | `plant_based_cowpea_medley` | `pea` | `minecraft:carrot` | `lentil_curry` |
| `fava_bean` | `plant_based_fava_bean_medley` | `peanut` | `minecraft:potato` | `chickpea_hummus` |
| `strawberry` | `plant_based_strawberry_medley` | `cranberry` | `minecraft:apple` | `fruit_salad` |
| `blueberry` | `plant_based_blueberry_medley` | `blackberry` | `minecraft:sweet_berries` | `berry_pie` |
| `raspberry` | `plant_based_raspberry_medley` | `blackcurrant` | `minecraft:glow_berries` | `grape_berry_compote` |
| `cranberry` | `plant_based_cranberry_medley` | `pineapple` | `minecraft:melon_slice` | `blackcurrant_oat_crumble` |
| `blackberry` | `plant_based_blackberry_medley` | `grape` | `minecraft:apple` | `fruit_salad` |
| `blackcurrant` | `plant_based_blackcurrant_medley` | `melon` | `minecraft:sweet_berries` | `berry_pie` |
| `pineapple` | `plant_based_pineapple_medley` | `watermelon` | `minecraft:glow_berries` | `grape_berry_compote` |
| `grape` | `plant_based_grape_medley` | `strawberry` | `minecraft:melon_slice` | `blackcurrant_oat_crumble` |
| `melon` | `plant_based_melon_medley` | `blueberry` | `minecraft:apple` | `fruit_salad` |
| `watermelon` | `plant_based_watermelon_medley` | `raspberry` | `minecraft:sweet_berries` | `berry_pie` |
| `onion` | `plant_based_onion_medley` | `parsnip` | `minecraft:carrot` | `root_vegetable_hash` |
| `garlic` | `plant_based_garlic_medley` | `leek` | `minecraft:potato` | `roasted_root_vegetables` |
| `radish` | `plant_based_radish_medley` | `shallot` | `minecraft:beetroot` | `vegetable_stew` |
| `turnip` | `plant_based_turnip_medley` | `ginger` | `minecraft:carrot` | `beetroot_soup` |
| `sweet_potato` | `plant_based_sweet_potato_medley` | `turmeric` | `minecraft:potato` | `garlic_roasted_potatoes` |
| `parsnip` | `plant_based_parsnip_medley` | `kohlrabi` | `minecraft:beetroot` | `crop_stir_fry` |
| `leek` | `plant_based_leek_medley` | `rutabaga` | `minecraft:carrot` | `root_vegetable_hash` |
| `shallot` | `plant_based_shallot_medley` | `jicama` | `minecraft:potato` | `roasted_root_vegetables` |
| `ginger` | `plant_based_ginger_medley` | `celeriac` | `minecraft:beetroot` | `vegetable_stew` |
| `turmeric` | `plant_based_turmeric_medley` | `onion` | `minecraft:carrot` | `beetroot_soup` |
| `kohlrabi` | `plant_based_kohlrabi_medley` | `garlic` | `minecraft:potato` | `garlic_roasted_potatoes` |
| `rutabaga` | `plant_based_rutabaga_medley` | `radish` | `minecraft:beetroot` | `crop_stir_fry` |
| `jicama` | `plant_based_jicama_medley` | `turnip` | `minecraft:carrot` | `root_vegetable_hash` |
| `celeriac` | `plant_based_celeriac_medley` | `sweet_potato` | `minecraft:potato` | `roasted_root_vegetables` |
| `lettuce` | `plant_based_lettuce_medley` | `celery` | `minecraft:carrot` | `garden_salad` |
| `cabbage` | `plant_based_cabbage_medley` | `arugula` | `minecraft:potato` | `vegetable_stew` |
| `spinach` | `plant_based_spinach_medley` | `swiss_chard` | `minecraft:kelp` | `crop_stir_fry` |
| `broccoli` | `plant_based_broccoli_medley` | `fennel` | `minecraft:bread` | `barley_vegetable_soup` |
| `kale` | `plant_based_kale_medley` | `dill` | `minecraft:carrot` | `herb_bread` |
| `basil` | `plant_based_basil_medley` | `cilantro` | `minecraft:potato` | `cucumber_mint_salad` |
| `mint` | `plant_based_mint_medley` | `parsley` | `minecraft:kelp` | `jute_mallow_soup` |
| `celery` | `plant_based_celery_medley` | `collard_greens` | `minecraft:bread` | `rapini_garlic_saute` |
| `arugula` | `plant_based_arugula_medley` | `brussels_sprouts` | `minecraft:carrot` | `garden_salad` |
| `swiss_chard` | `plant_based_swiss_chard_medley` | `cauliflower` | `minecraft:potato` | `vegetable_stew` |
| `fennel` | `plant_based_fennel_medley` | `bok_choy` | `minecraft:kelp` | `crop_stir_fry` |
| `dill` | `plant_based_dill_medley` | `chives` | `minecraft:bread` | `barley_vegetable_soup` |
| `cilantro` | `plant_based_cilantro_medley` | `watercress` | `minecraft:carrot` | `herb_bread` |
| `parsley` | `plant_based_parsley_medley` | `endive` | `minecraft:potato` | `cucumber_mint_salad` |
| `collard_greens` | `plant_based_collard_greens_medley` | `chicory` | `minecraft:kelp` | `jute_mallow_soup` |
| `brussels_sprouts` | `plant_based_brussels_sprouts_medley` | `mustard_greens` | `minecraft:bread` | `rapini_garlic_saute` |
| `cauliflower` | `plant_based_cauliflower_medley` | `water_spinach` | `minecraft:carrot` | `garden_salad` |
| `bok_choy` | `plant_based_bok_choy_medley` | `purslane` | `minecraft:potato` | `vegetable_stew` |
| `chives` | `plant_based_chives_medley` | `rapini` | `minecraft:kelp` | `crop_stir_fry` |
| `watercress` | `plant_based_watercress_medley` | `napa_cabbage` | `minecraft:bread` | `barley_vegetable_soup` |
| `endive` | `plant_based_endive_medley` | `jute_mallow` | `minecraft:carrot` | `herb_bread` |
| `chicory` | `plant_based_chicory_medley` | `lettuce` | `minecraft:potato` | `cucumber_mint_salad` |
| `mustard_greens` | `plant_based_mustard_greens_medley` | `cabbage` | `minecraft:kelp` | `jute_mallow_soup` |
| `water_spinach` | `plant_based_water_spinach_medley` | `spinach` | `minecraft:bread` | `rapini_garlic_saute` |
| `purslane` | `plant_based_purslane_medley` | `broccoli` | `minecraft:carrot` | `garden_salad` |
| `rapini` | `plant_based_rapini_medley` | `kale` | `minecraft:potato` | `vegetable_stew` |
| `napa_cabbage` | `plant_based_napa_cabbage_medley` | `basil` | `minecraft:kelp` | `crop_stir_fry` |
| `jute_mallow` | `plant_based_jute_mallow_medley` | `mint` | `minecraft:bread` | `barley_vegetable_soup` |
| `tomato` | `plant_based_tomato_medley` | `eggplant` | `minecraft:carrot` | `crop_stir_fry` |
| `cucumber` | `plant_based_cucumber_medley` | `zucchini` | `minecraft:potato` | `vegetable_stew` |
| `bell_pepper` | `plant_based_bell_pepper_medley` | `okra` | `minecraft:beetroot` | `eggplant_curry` |
| `eggplant` | `plant_based_eggplant_medley` | `chili_pepper` | `minecraft:kelp` | `pumpkin_soup` |
| `zucchini` | `plant_based_zucchini_medley` | `artichoke` | `minecraft:carrot` | `tomato_sauce` |
| `okra` | `plant_based_okra_medley` | `asparagus` | `minecraft:potato` | `stuffed_pepper` |
| `chili_pepper` | `plant_based_chili_pepper_medley` | `pumpkin` | `minecraft:beetroot` | `chayote_stir_fry` |
| `artichoke` | `plant_based_artichoke_medley` | `chayote` | `minecraft:kelp` | `tomato_okra_stew` |
| `asparagus` | `plant_based_asparagus_medley` | `tomato` | `minecraft:carrot` | `crop_stir_fry` |
| `pumpkin` | `plant_based_pumpkin_medley` | `cucumber` | `minecraft:potato` | `vegetable_stew` |
| `chayote` | `plant_based_chayote_medley` | `bell_pepper` | `minecraft:beetroot` | `eggplant_curry` |
| `sunflower_seed` | `plant_based_sunflower_seed_medley` | `sesame` | `minecraft:wheat` | `roasted_seed_mix` |
| `sesame` | `plant_based_sesame_medley` | `sunflower_seed` | `minecraft:cocoa_beans` | `peanut_sauce` |

## Vanilla-only plant recipes

| Recipe ID | Ingredients | Output dish |
|---|---|---|
| `vanilla_plant_root_hash` | `minecraft:potato` + `minecraft:carrot` + `minecraft:beetroot` | `root_vegetable_hash` |
| `vanilla_plant_fruit_salad` | `minecraft:apple` + `minecraft:sweet_berries` + `minecraft:melon_slice` | `fruit_salad` |
| `vanilla_plant_berry_pie` | `minecraft:sweet_berries` + `minecraft:wheat` + `minecraft:sugar` | `berry_pie` |
| `vanilla_plant_kelp_stir_fry` | `minecraft:kelp` + `minecraft:carrot` + `minecraft:potato` | `crop_stir_fry` |
| `vanilla_plant_apple_porridge` | `minecraft:apple` + `minecraft:wheat` + `minecraft:sugar` | `grain_porridge` |
| `vanilla_plant_herb_bread` | `minecraft:wheat` + `minecraft:kelp` + `minecraft:sugar` | `herb_bread` |
| `vanilla_plant_berry_bread` | `minecraft:bread` + `minecraft:glow_berries` + `minecraft:wheat` | `berry_pie` |
| `vanilla_plant_melon_bowl` | `minecraft:melon_slice` + `minecraft:glow_berries` + `minecraft:apple` | `fruit_salad` |
| `vanilla_plant_dried_kelp_chowder` | `minecraft:dried_kelp` + `minecraft:potato` + `minecraft:carrot` | `seaweed_potato_chowder` |
| `vanilla_plant_beetroot_soup` | `minecraft:beetroot` + `minecraft:potato` + `minecraft:kelp` | `beetroot_soup` |
| `vanilla_plant_sweet_berry_pancake` | `minecraft:sweet_berries` + `minecraft:wheat` + `minecraft:cocoa_beans` | `buckwheat_pancakes` |
| `vanilla_plant_pumpkin_flatbread` | `minecraft:pumpkin` + `minecraft:wheat` + `minecraft:sugar` | `cornbread` |

## Release gate

Do not treat this branch as release-ready until all three GitHub Actions jobs pass: resource/recipe validation, NeoForge compile/package, and fresh real-server boot. The v1.8 validator must print `PLANT_CUISINE_V1_8_OK`.
