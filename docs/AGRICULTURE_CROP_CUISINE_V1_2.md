# Agriculture Crop Cuisine v1.2

Builds on 51 crop-cuisine items in v1.1. This release adds **17 distinct food items**, for **68 total cuisine items (66 edible foods and two oil ingredients)**, plus **10 alternate routes** to existing foods. No new crop species or machines are introduced.

## New dishes and preparations

- **Sweet Adzuki Bean Soup / 红豆甜汤** (`gugugaga:adzuki_bean_sweet_soup`): `gugugaga:adzuki_bean` + `gugugaga:rice` + `minecraft:sugar`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Mung Bean Congee / 绿豆粥** (`gugugaga:mung_bean_congee`): `gugugaga:mung_bean` + `gugugaga:rice` + `minecraft:bowl`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Ginger Napa Cabbage / 姜香大白菜** (`gugugaga:napa_cabbage_braise`): `gugugaga:napa_cabbage` + `gugugaga:ginger` + `minecraft:carrot`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Garlic Water Spinach / 蒜蓉空心菜** (`gugugaga:garlic_water_spinach`): `gugugaga:water_spinach` + `gugugaga:garlic` + `gugugaga:canola_oil`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Tomato Okra Stew / 番茄秋葵炖菜** (`gugugaga:tomato_okra_stew`): `gugugaga:okra` + `gugugaga:tomato` + `gugugaga:onion`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Jute Mallow Soup / 黄麻叶汤** (`gugugaga:jute_mallow_soup`): `gugugaga:jute_mallow` + `minecraft:carrot` + `minecraft:bowl`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Pineapple Tart / 菠萝挞** (`gugugaga:pineapple_tart`): `gugugaga:pineapple` + `minecraft:wheat` + `minecraft:sugar`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Grape and Berry Compote / 葡萄浆果果酱** (`gugugaga:grape_berry_compote`): `gugugaga:grape` + `gugugaga:blackcurrant` + `minecraft:sugar`. Shapeless crafting. Nutrition 5; saturation modifier 0.55.
- **Garlicky Rapini / 蒜香西兰花苗** (`gugugaga:rapini_garlic_saute`): `gugugaga:rapini` + `gugugaga:garlic` + `gugugaga:safflower_oil`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Root Vegetable Hash / 根菜杂烩** (`gugugaga:root_vegetable_hash`): `gugugaga:rutabaga` + `gugugaga:celeriac` + `minecraft:baked_potato`. Shapeless crafting. Nutrition 7; saturation modifier 0.75.
- **Millet and Fennel Pilaf / 茴香小米焖饭** (`gugugaga:millet_fennel_pilaf`): `gugugaga:millet` + `gugugaga:fennel` + `gugugaga:onion`. Shapeless crafting. Nutrition 7; saturation modifier 0.75.
- **Mushroom Barley Risotto / 菌菇大麦烩饭** (`gugugaga:mushroom_barley_risotto`): `gugugaga:barley` + `gugugaga:shiitake_mushroom` + `minecraft:milk_bucket`. Shapeless crafting. Nutrition 7; saturation modifier 0.75.
- **Cucumber Mint Salad / 黄瓜薄荷沙拉** (`gugugaga:cucumber_mint_salad`): `gugugaga:cucumber` + `gugugaga:mint` + `minecraft:beetroot`. Shapeless crafting. Nutrition 5; saturation modifier 0.55.
- **Seaweed Potato Chowder / 海藻土豆浓汤** (`gugugaga:seaweed_potato_chowder`): `gugugaga:sea_lettuce` + `minecraft:potato` + `gugugaga:oyster_mushroom`. Shapeless crafting. Nutrition 7; saturation modifier 0.75.
- **Blackcurrant Oat Crumble / 黑加仑燕麦酥** (`gugugaga:blackcurrant_oat_crumble`): `gugugaga:blackcurrant` + `gugugaga:oat` + `minecraft:sugar`. Shapeless crafting. Nutrition 6; saturation modifier 0.65.
- **Baked Taro / 烤芋头** (`gugugaga:baked_taro`): `gugugaga:taro`. Smelt, smoke, or campfire cook. Nutrition 5; saturation modifier 0.60.
- **Roasted Yam / 烤山药** (`gugugaga:roasted_yam`): `gugugaga:yam`. Smelt, smoke, or campfire cook. Nutrition 5; saturation modifier 0.60.

## Alternate vanilla and modded cooking recipes

- `bread_from_teff` → `minecraft:bread` ×1: `gugugaga:teff` + `gugugaga:teff` + `gugugaga:teff`
- `bread_from_sorghum` → `minecraft:bread` ×1: `gugugaga:sorghum` + `gugugaga:sorghum` + `gugugaga:sorghum`
- `bread_from_amaranth` → `minecraft:bread` ×1: `gugugaga:amaranth` + `gugugaga:amaranth` + `gugugaga:amaranth`
- `oat_cookies` → `minecraft:cookie` ×4: `gugugaga:oat` + `minecraft:cocoa_beans` + `minecraft:sugar`
- `pumpkin_pie_from_field_pumpkin` → `minecraft:pumpkin_pie` ×1: `gugugaga:pumpkin` + `minecraft:egg` + `minecraft:sugar`
- `fruit_salad_with_pineapple` → `gugugaga:fruit_salad` ×1: `gugugaga:pineapple` + `gugugaga:grape` + `minecraft:sugar`
- `vegetable_stew_with_mustard_greens` → `gugugaga:vegetable_stew` ×1: `gugugaga:mustard_greens` + `minecraft:carrot` + `minecraft:potato`
- `nori_roll_with_cucumber` → `gugugaga:nori_rice_roll` ×2: `gugugaga:nori_sheet` + `gugugaga:rice` + `gugugaga:cucumber`
- `honey_tea_with_mint` → `gugugaga:tea_cup` ×1: `gugugaga:tea` + `gugugaga:mint` + `minecraft:honey_bottle`
- `mushroom_rice_bowl_with_kale` → `gugugaga:mushroom_rice_bowl` ×1: `gugugaga:shiitake_mushroom` + `gugugaga:rice` + `gugugaga:kale`

## Content and testing

- Uses both Minecraft ingredients (sugar, bowl, wheat, potatoes, carrots, cocoa beans, eggs, milk) and existing crops, honey, marine produce, and mushrooms.
- The pre-existing `cooking/any_crop_food` tag continues to cover all 88 edible crops through the farmer's skillet. Fiber crops remain excluded.
- Cassava, taro, and yam are non-edible harvest materials; baked taro and roasted yam are now additional cooked edible options.
- 17 new 16×16 deterministic item sprites, item models, bilingual labels, crafting and cooking data.
- Existing recipe alternatives remain unchanged, and a new static test checks recipe collisions and ingredient registrations.
- Generated visual proof sheet automatically adapts to the catalog size. Client playtesting is still required for food consumption and recipe-book behavior.
