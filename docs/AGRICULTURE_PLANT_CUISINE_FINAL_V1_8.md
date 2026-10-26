# Agriculture Plant-Based Cuisine — Final v1.8

## Scope

This is a recipe-only completion pass. It adds 88 dedicated crop-hybrid plant-based recipe variants and 12 recipes made entirely from vanilla plant-derived ingredients. It adds no item registrations, no new dish items, and no food-value changes.

## Coverage

| Check | Expected result |
|---|---:|
| Registered plantable crop blocks audited | 100 |
| Edible crop food IDs covered by a dedicated plant-only recipe | 88 / 88 |
| New crop + crop + vanilla plant recipes | 88 |
| New vanilla-only plant recipes | 12 |
| New recipe JSON files | 100 |
| Existing dish items reused | Yes; no new dish item IDs |
| Animal-derived ingredients in new recipes | 0 |

## Art and resource requirements

The existing Gradle asset pipeline generates and checks:

- 100 crop produce icons and 100 seed icons, with duplicate pixel-hash detection
- 1,200 crop growth/pollination textures and 1,200 corresponding crop models
- 50 distinct floral-honey bottle sprites
- 68 distinct crop-cuisine dish icons, plus a contact sheet
- 24 marine/fungal inventory icons and their culture/marine crop resources

The v1.8 validator independently checks all 100 crop blockstates, seed/produce item models, bilingual item/block names, loot-table references, seed recipes, all recipe ingredient IDs, output dish model references, and shapeless-recipe collisions. The CI build runs the existing generated-art validators and real NeoForge 1.21.1 server smoke test.

## Dedicated crop-hybrid recipes

| Crop | Recipe ID | Other crop | Vanilla plant ingredient | Output dish |
|---|---|---|---|---|
| `corn` | `plant_based_corn_medley` | `kale` | `minecraft:cookie` | `garden_salad` |
| `tomato` | `plant_based_tomato_medley` | `zucchini` | `minecraft:bread` | `vegetable_stew` |
| `onion` | `plant_based_onion_medley` | `raspberry` | `minecraft:baked_potato` | `roasted_root_vegetables` |
| `rice` | `plant_based_rice_medley` | `cranberry` | `minecraft:glow_berries` | `crop_stir_fry` |
| `soybean` | `plant_based_soybean_medley` | `sesame` | `minecraft:dried_kelp` | `grain_porridge` |
| `strawberry` | `plant_based_strawberry_medley` | `basil` | `minecraft:cocoa_beans` | `barley_vegetable_soup` |
| `cucumber` | `plant_based_cucumber_medley` | `mint` | `minecraft:cookie` | `rice_vegetable_bowl` |
| `blueberry` | `plant_based_blueberry_medley` | `celery` | `minecraft:bread` | `bean_stew` |
| `bell_pepper` | `plant_based_bell_pepper_medley` | `leek` | `minecraft:baked_potato` | `stuffed_pepper` |
| `garlic` | `plant_based_garlic_medley` | `parsnip` | `minecraft:glow_berries` | `eggplant_curry` |
| `barley` | `plant_based_barley_medley` | `arugula` | `minecraft:dried_kelp` | `pumpkin_soup` |
| `oat` | `plant_based_oat_medley` | `swiss_chard` | `minecraft:cocoa_beans` | `berry_pie` |
| `rye` | `plant_based_rye_medley` | `okra` | `minecraft:cookie` | `fruit_salad` |
| `pea` | `plant_based_pea_medley` | `fennel` | `minecraft:bread` | `cornbread` |
| `lettuce` | `plant_based_lettuce_medley` | `dill` | `minecraft:baked_potato` | `cabbage_rolls` |
| `cabbage` | `plant_based_cabbage_medley` | `cilantro` | `minecraft:glow_berries` | `tomato_sauce` |
| `spinach` | `plant_based_spinach_medley` | `parsley` | `minecraft:dried_kelp` | `herb_bread` |
| `broccoli` | `plant_based_broccoli_medley` | `collard_greens` | `minecraft:cocoa_beans` | `sweet_potato_roast` |
| `eggplant` | `plant_based_eggplant_medley` | `green_bean` | `minecraft:cookie` | `cassava_flatbread` |
| `peanut` | `plant_based_peanut_medley` | `chili_pepper` | `minecraft:bread` | `taro_dumplings` |
| `sorghum` | `plant_based_sorghum_medley` | `brussels_sprouts` | `minecraft:baked_potato` | `yam_cakes` |
| `sunflower_seed` | `plant_based_sunflower_seed_medley` | `cauliflower` | `minecraft:glow_berries` | `chickpea_hummus` |
| `millet` | `plant_based_millet_medley` | `bok_choy` | `minecraft:dried_kelp` | `peanut_sauce` |
| `buckwheat` | `plant_based_buckwheat_medley` | `shallot` | `minecraft:cocoa_beans` | `roasted_seed_mix` |
| `chickpea` | `plant_based_chickpea_medley` | `ginger` | `minecraft:cookie` | `herbal_tea` |
| `lentil` | `plant_based_lentil_medley` | `turmeric` | `minecraft:bread` | `farmers_skillet` |
| `radish` | `plant_based_radish_medley` | `black_bean` | `minecraft:baked_potato` | `buckwheat_pancakes` |
| `turnip` | `plant_based_turnip_medley` | `quinoa` | `minecraft:glow_berries` | `quinoa_salad` |
| `sweet_potato` | `plant_based_sweet_potato_medley` | `artichoke` | `minecraft:dried_kelp` | `lentil_curry` |
| `kale` | `plant_based_kale_medley` | `asparagus` | `minecraft:cocoa_beans` | `sweet_corn_fritters` |
| `zucchini` | `plant_based_zucchini_medley` | `pumpkin` | `minecraft:cookie` | `basil_tomato_flatbread` |
| `raspberry` | `plant_based_raspberry_medley` | `pineapple` | `minecraft:bread` | `pumpkin_rice` |
| `cranberry` | `plant_based_cranberry_medley` | `blackberry` | `minecraft:baked_potato` | `chili_bean_chili` |
| `sesame` | `plant_based_sesame_medley` | `blackcurrant` | `minecraft:glow_berries` | `sesame_noodles` |
| `basil` | `plant_based_basil_medley` | `amaranth` | `minecraft:dried_kelp` | `garlic_roasted_potatoes` |
| `mint` | `plant_based_mint_medley` | `chives` | `minecraft:cocoa_beans` | `beetroot_soup` |
| `celery` | `plant_based_celery_medley` | `sweet_corn` | `minecraft:cookie` | `fava_bean_soup` |
| `leek` | `plant_based_leek_medley` | `teff` | `minecraft:bread` | `chayote_stir_fry` |
| `parsnip` | `plant_based_parsnip_medley` | `watercress` | `minecraft:baked_potato` | `soybean_patties` |
| `arugula` | `plant_based_arugula_medley` | `endive` | `minecraft:glow_berries` | `roasted_chickpeas` |
| `swiss_chard` | `plant_based_swiss_chard_medley` | `kohlrabi` | `minecraft:dried_kelp` | `baked_sweet_corn` |
| `okra` | `plant_based_okra_medley` | `rutabaga` | `minecraft:cocoa_beans` | `adzuki_bean_sweet_soup` |
| `fennel` | `plant_based_fennel_medley` | `jicama` | `minecraft:cookie` | `mung_bean_congee` |
| `dill` | `plant_based_dill_medley` | `adzuki_bean` | `minecraft:bread` | `napa_cabbage_braise` |
| `cilantro` | `plant_based_cilantro_medley` | `mung_bean` | `minecraft:baked_potato` | `garlic_water_spinach` |
| `parsley` | `plant_based_parsley_medley` | `cowpea` | `minecraft:glow_berries` | `tomato_okra_stew` |
| `collard_greens` | `plant_based_collard_greens_medley` | `chicory` | `minecraft:dried_kelp` | `jute_mallow_soup` |
| `green_bean` | `plant_based_green_bean_medley` | `mustard_greens` | `minecraft:cocoa_beans` | `grape_berry_compote` |
| `chili_pepper` | `plant_based_chili_pepper_medley` | `water_spinach` | `minecraft:cookie` | `rapini_garlic_saute` |
| `brussels_sprouts` | `plant_based_brussels_sprouts_medley` | `celeriac` | `minecraft:bread` | `root_vegetable_hash` |
| `cauliflower` | `plant_based_cauliflower_medley` | `purslane` | `minecraft:baked_potato` | `millet_fennel_pilaf` |
| `bok_choy` | `plant_based_bok_choy_medley` | `chayote` | `minecraft:glow_berries` | `cucumber_mint_salad` |
| `shallot` | `plant_based_shallot_medley` | `grape` | `minecraft:dried_kelp` | `seaweed_potato_chowder` |
| `ginger` | `plant_based_ginger_medley` | `rapini` | `minecraft:cocoa_beans` | `blackcurrant_oat_crumble` |
| `turmeric` | `plant_based_turmeric_medley` | `napa_cabbage` | `minecraft:cookie` | `baked_taro` |
| `black_bean` | `plant_based_black_bean_medley` | `melon` | `minecraft:bread` | `roasted_yam` |
| `quinoa` | `plant_based_quinoa_medley` | `watermelon` | `minecraft:baked_potato` | `garden_salad` |
| `artichoke` | `plant_based_artichoke_medley` | `fava_bean` | `minecraft:glow_berries` | `vegetable_stew` |
| `asparagus` | `plant_based_asparagus_medley` | `jute_mallow` | `minecraft:dried_kelp` | `roasted_root_vegetables` |
| `pumpkin` | `plant_based_pumpkin_medley` | `corn` | `minecraft:cocoa_beans` | `crop_stir_fry` |
| `pineapple` | `plant_based_pineapple_medley` | `tomato` | `minecraft:cookie` | `grain_porridge` |
| `blackberry` | `plant_based_blackberry_medley` | `onion` | `minecraft:bread` | `barley_vegetable_soup` |
| `blackcurrant` | `plant_based_blackcurrant_medley` | `rice` | `minecraft:baked_potato` | `rice_vegetable_bowl` |
| `amaranth` | `plant_based_amaranth_medley` | `soybean` | `minecraft:glow_berries` | `bean_stew` |
| `chives` | `plant_based_chives_medley` | `strawberry` | `minecraft:dried_kelp` | `stuffed_pepper` |
| `sweet_corn` | `plant_based_sweet_corn_medley` | `cucumber` | `minecraft:cocoa_beans` | `eggplant_curry` |
| `teff` | `plant_based_teff_medley` | `blueberry` | `minecraft:cookie` | `pumpkin_soup` |
| `watercress` | `plant_based_watercress_medley` | `bell_pepper` | `minecraft:bread` | `berry_pie` |
| `endive` | `plant_based_endive_medley` | `garlic` | `minecraft:baked_potato` | `fruit_salad` |
| `kohlrabi` | `plant_based_kohlrabi_medley` | `barley` | `minecraft:glow_berries` | `cornbread` |
| `rutabaga` | `plant_based_rutabaga_medley` | `oat` | `minecraft:dried_kelp` | `cabbage_rolls` |
| `jicama` | `plant_based_jicama_medley` | `rye` | `minecraft:cocoa_beans` | `tomato_sauce` |
| `adzuki_bean` | `plant_based_adzuki_bean_medley` | `pea` | `minecraft:cookie` | `herb_bread` |
| `mung_bean` | `plant_based_mung_bean_medley` | `lettuce` | `minecraft:bread` | `sweet_potato_roast` |
| `cowpea` | `plant_based_cowpea_medley` | `cabbage` | `minecraft:baked_potato` | `cassava_flatbread` |
| `chicory` | `plant_based_chicory_medley` | `spinach` | `minecraft:glow_berries` | `taro_dumplings` |
| `mustard_greens` | `plant_based_mustard_greens_medley` | `broccoli` | `minecraft:dried_kelp` | `yam_cakes` |
| `water_spinach` | `plant_based_water_spinach_medley` | `eggplant` | `minecraft:cocoa_beans` | `chickpea_hummus` |
| `celeriac` | `plant_based_celeriac_medley` | `peanut` | `minecraft:cookie` | `peanut_sauce` |
| `purslane` | `plant_based_purslane_medley` | `sorghum` | `minecraft:bread` | `roasted_seed_mix` |
| `chayote` | `plant_based_chayote_medley` | `sunflower_seed` | `minecraft:baked_potato` | `herbal_tea` |
| `grape` | `plant_based_grape_medley` | `millet` | `minecraft:glow_berries` | `farmers_skillet` |
| `rapini` | `plant_based_rapini_medley` | `buckwheat` | `minecraft:dried_kelp` | `buckwheat_pancakes` |
| `napa_cabbage` | `plant_based_napa_cabbage_medley` | `chickpea` | `minecraft:cocoa_beans` | `quinoa_salad` |
| `melon` | `plant_based_melon_medley` | `lentil` | `minecraft:cookie` | `lentil_curry` |
| `watermelon` | `plant_based_watermelon_medley` | `radish` | `minecraft:bread` | `sweet_corn_fritters` |
| `fava_bean` | `plant_based_fava_bean_medley` | `turnip` | `minecraft:baked_potato` | `basil_tomato_flatbread` |
| `jute_mallow` | `plant_based_jute_mallow_medley` | `sweet_potato` | `minecraft:glow_berries` | `pumpkin_rice` |

## Vanilla-only plant recipes

| Recipe ID | Ingredients | Output dish |
|---|---|---|
| `vanilla_plant_root_hash` | `minecraft:potato` + `minecraft:carrot` + `minecraft:beetroot` | `root_vegetable_hash` |
| `vanilla_plant_fruit_salad` | `minecraft:apple` + `minecraft:sweet_berries` + `minecraft:melon_slice` | `fruit_salad` |
| `vanilla_plant_berry_crumble` | `minecraft:sweet_berries` + `minecraft:wheat` + `minecraft:sugar` | `blackcurrant_oat_crumble` |
| `vanilla_plant_kelp_stir_fry` | `minecraft:kelp` + `minecraft:carrot` + `minecraft:potato` | `crop_stir_fry` |
| `vanilla_plant_apple_porridge` | `minecraft:apple` + `minecraft:wheat` + `minecraft:baked_potato` | `grain_porridge` |
| `vanilla_plant_cocoa_bread` | `minecraft:wheat` + `minecraft:cocoa_beans` + `minecraft:sugar` | `herb_bread` |
| `vanilla_plant_berry_bread` | `minecraft:bread` + `minecraft:glow_berries` + `minecraft:wheat` | `berry_pie` |
| `vanilla_plant_melon_bowl` | `minecraft:melon_slice` + `minecraft:glow_berries` + `minecraft:apple` | `fruit_salad` |
| `vanilla_plant_dried_kelp_crisp` | `minecraft:dried_kelp` + `minecraft:wheat` + `minecraft:cookie` | `roasted_seed_mix` |
| `vanilla_plant_beetroot_soup` | `minecraft:beetroot` + `minecraft:potato` + `minecraft:kelp` | `beetroot_soup` |
| `vanilla_plant_sweet_berry_pancake` | `minecraft:sweet_berries` + `minecraft:wheat` + `minecraft:cocoa_beans` | `buckwheat_pancakes` |
| `vanilla_plant_pumpkin_flatbread` | `minecraft:pumpkin` + `minecraft:wheat` + `minecraft:sugar` | `cornbread` |

## Acceptance gate

Do not treat the branch as release-ready until all three GitHub Actions jobs pass: resource/recipe validation, NeoForge compile/package, and a fresh real-server boot. The v1.8 validator must print `PLANT_CUISINE_V1_8_OK`.
