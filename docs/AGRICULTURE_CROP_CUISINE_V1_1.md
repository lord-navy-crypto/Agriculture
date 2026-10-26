# Agriculture Crop Cuisine v1.1

Builds on 35 cuisine item IDs (33 foods + 2 oils) by adding 16 food items for 51 total cuisine item IDs (49 foods and 2 oils).

## New meals (16)
- Tomato and Egg Stir-Fry / 番茄炒蛋: gugugaga:tomato + minecraft:egg + gugugaga:canola_oil (shapeless crafting)
- Basil Tomato Flatbread / 罗勒番茄薄饼: minecraft:wheat + gugugaga:tomato + gugugaga:basil (shapeless crafting)
- Pumpkin Rice / 南瓜焖饭: gugugaga:pumpkin + gugugaga:rice + gugugaga:onion (shapeless crafting)
- Spinach Omelette / 菠菜煎蛋: gugugaga:spinach + minecraft:egg + gugugaga:canola_oil (shapeless crafting)
- Oat and Berry Porridge / 燕麦浆果粥: gugugaga:oat + gugugaga:blueberry + minecraft:milk_bucket (shapeless crafting)
- Spicy Bean Chili / 辣豆炖菜: gugugaga:black_bean + gugugaga:chili_pepper + gugugaga:tomato (shapeless crafting)
- Sesame Noodles / 芝麻拌面: minecraft:wheat + gugugaga:sesame + gugugaga:peanut_sauce (shapeless crafting)
- Garlic Herb Potatoes / 蒜香香草土豆: minecraft:potato + gugugaga:garlic + gugugaga:canola_oil (shapeless crafting)
- Beetroot and Leek Soup / 甜菜根韭葱汤: minecraft:beetroot + gugugaga:leek + minecraft:potato (shapeless crafting)
- Seaweed and Mushroom Soup / 海带菌菇汤: gugugaga:wakame + gugugaga:shiitake_mushroom + minecraft:carrot (shapeless crafting)
- Fava Bean Soup / 蚕豆汤: gugugaga:fava_bean + gugugaga:onion + minecraft:carrot (shapeless crafting)
- Ginger Chayote Stir-Fry / 姜香佛手瓜: gugugaga:chayote + gugugaga:ginger + gugugaga:safflower_oil (shapeless crafting)
- Honey Berry Toast / 蜂蜜浆果吐司: minecraft:bread + gugugaga:strawberry + minecraft:honey_bottle (shapeless crafting)
- Soybean Patties / 黄豆饼: gugugaga:soybean + minecraft:wheat + minecraft:egg (shapeless crafting)
- Roasted Chickpeas / 烤鹰嘴豆: gugugaga:chickpea (furnace, smoker or campfire)
- Baked Sweet Corn / 烤甜玉米: gugugaga:sweet_corn (furnace, smoker or campfire)

## Alternative crafting routes (15)
- bread_from_barley -> minecraft:bread: gugugaga:barley + gugugaga:barley + gugugaga:barley
- bread_from_oats -> minecraft:bread: gugugaga:oat + gugugaga:oat + gugugaga:oat
- bread_from_rye -> minecraft:bread: gugugaga:rye + gugugaga:rye + gugugaga:rye
- bread_from_millet -> minecraft:bread: gugugaga:millet + gugugaga:millet + gugugaga:millet
- bread_from_corn -> minecraft:bread: gugugaga:corn + gugugaga:corn + gugugaga:corn
- garden_salad_with_beetroot -> gugugaga:garden_salad: gugugaga:lettuce + gugugaga:tomato + minecraft:beetroot
- vegetable_stew_with_cabbage -> gugugaga:vegetable_stew: gugugaga:cabbage + minecraft:potato + minecraft:carrot
- mushroom_rice_bowl_with_spinach -> gugugaga:mushroom_rice_bowl: gugugaga:shiitake_mushroom + gugugaga:rice + gugugaga:spinach
- berry_pie_with_blueberry -> gugugaga:berry_pie: gugugaga:blueberry + minecraft:wheat + minecraft:sugar
- herb_bread_with_basil -> gugugaga:herb_bread: gugugaga:basil + minecraft:wheat + gugugaga:garlic
- rice_vegetable_bowl_with_mushrooms -> gugugaga:rice_vegetable_bowl: gugugaga:rice + gugugaga:oyster_mushroom + minecraft:carrot
- lentil_curry_with_ginger -> gugugaga:lentil_curry: gugugaga:lentil + gugugaga:ginger + gugugaga:onion
- fruit_salad_melon -> gugugaga:fruit_salad: gugugaga:melon + gugugaga:blueberry + minecraft:sugar
- honey_tea_with_lavender -> gugugaga:tea_cup: gugugaga:tea + gugugaga:lavender + minecraft:honey_bottle
- mushroom_medley_with_garlic -> gugugaga:mushroom_medley: gugugaga:shiitake_mushroom + gugugaga:oyster_mushroom + gugugaga:garlic

The new set bridges vanilla beetroot, potatoes, carrots, eggs, bread, milk buckets and honey bottles with garden crops and marine/fungal harvests.

- Chickpeas and sweet corn now support furnace, smoker and campfire cooking with intentionally different cooking times.
- Ingredient quantities are explicit; five grain variants use three units to craft one vanilla bread.
- Root processing remains deliberate: cassava, taro and yam are still non-edible materials with recipes from v1.0.
- The preexisting 88-crop farmer's skillet and family cooking tags remain unchanged.
- New cuisine icons and item models use the existing deterministic resource generator with a larger visual contact sheet.

Validation: `qa/validate_crop_cuisine_v1_0.py`, `qa/validate_crop_cuisine_v1_1.py`, generated PNG validator, NeoForge compilation, server boot. A real client playtest is still required before a stable release.
