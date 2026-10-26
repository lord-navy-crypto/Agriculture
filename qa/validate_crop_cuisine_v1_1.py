#!/usr/bin/env python3
"""Regression contracts for enriched crop, vanilla, marine and fungal recipes."""
import json
import re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
def read(path):
    return json.loads((RES/path).read_text(encoding="utf-8"))
NEW_DISHES=[{"id":"tomato_egg_stir_fry","method":"craft","ingredients":["gugugaga:tomato","minecraft:egg","gugugaga:canola_oil"]},{"id":"basil_tomato_flatbread","method":"craft","ingredients":["minecraft:wheat","gugugaga:tomato","gugugaga:basil"]},{"id":"pumpkin_rice","method":"craft","ingredients":["gugugaga:pumpkin","gugugaga:rice","gugugaga:onion"]},{"id":"spinach_omelette","method":"craft","ingredients":["gugugaga:spinach","minecraft:egg","gugugaga:canola_oil"]},{"id":"oat_berry_porridge","method":"craft","ingredients":["gugugaga:oat","gugugaga:blueberry","minecraft:milk_bucket"]},{"id":"chili_bean_chili","method":"craft","ingredients":["gugugaga:black_bean","gugugaga:chili_pepper","gugugaga:tomato"]},{"id":"sesame_noodles","method":"craft","ingredients":["minecraft:wheat","gugugaga:sesame","gugugaga:peanut_sauce"]},{"id":"garlic_roasted_potatoes","method":"craft","ingredients":["minecraft:potato","gugugaga:garlic","gugugaga:canola_oil"]},{"id":"beetroot_soup","method":"craft","ingredients":["minecraft:beetroot","gugugaga:leek","minecraft:potato"]},{"id":"seaweed_mushroom_soup","method":"craft","ingredients":["gugugaga:wakame","gugugaga:shiitake_mushroom","minecraft:carrot"]},{"id":"fava_bean_soup","method":"craft","ingredients":["gugugaga:fava_bean","gugugaga:onion","minecraft:carrot"]},{"id":"chayote_stir_fry","method":"craft","ingredients":["gugugaga:chayote","gugugaga:ginger","gugugaga:safflower_oil"]},{"id":"honey_berry_toast","method":"craft","ingredients":["minecraft:bread","gugugaga:strawberry","minecraft:honey_bottle"]},{"id":"soybean_patties","method":"craft","ingredients":["gugugaga:soybean","minecraft:wheat","minecraft:egg"]},{"id":"roasted_chickpeas","method":"smelt","ingredients":["gugugaga:chickpea"]},{"id":"baked_sweet_corn","method":"smelt","ingredients":["gugugaga:sweet_corn"]}]
ALTERNATIVES=[{"id":"bread_from_barley","result":"minecraft:bread","ingredients":["gugugaga:barley","gugugaga:barley","gugugaga:barley"]},{"id":"bread_from_oats","result":"minecraft:bread","ingredients":["gugugaga:oat","gugugaga:oat","gugugaga:oat"]},{"id":"bread_from_rye","result":"minecraft:bread","ingredients":["gugugaga:rye","gugugaga:rye","gugugaga:rye"]},{"id":"bread_from_millet","result":"minecraft:bread","ingredients":["gugugaga:millet","gugugaga:millet","gugugaga:millet"]},{"id":"bread_from_corn","result":"minecraft:bread","ingredients":["gugugaga:corn","gugugaga:corn","gugugaga:corn"]},{"id":"garden_salad_with_beetroot","result":"gugugaga:garden_salad","ingredients":["gugugaga:lettuce","gugugaga:tomato","minecraft:beetroot"]},{"id":"vegetable_stew_with_cabbage","result":"gugugaga:vegetable_stew","ingredients":["gugugaga:cabbage","minecraft:potato","minecraft:carrot"]},{"id":"mushroom_rice_bowl_with_spinach","result":"gugugaga:mushroom_rice_bowl","ingredients":["gugugaga:shiitake_mushroom","gugugaga:rice","gugugaga:spinach"]},{"id":"berry_pie_with_blueberry","result":"gugugaga:berry_pie","ingredients":["gugugaga:blueberry","minecraft:wheat","minecraft:sugar"]},{"id":"herb_bread_with_basil","result":"gugugaga:herb_bread","ingredients":["gugugaga:basil","minecraft:wheat","gugugaga:garlic"]},{"id":"rice_vegetable_bowl_with_mushrooms","result":"gugugaga:rice_vegetable_bowl","ingredients":["gugugaga:rice","gugugaga:oyster_mushroom","minecraft:carrot"]},{"id":"lentil_curry_with_ginger","result":"gugugaga:lentil_curry","ingredients":["gugugaga:lentil","gugugaga:ginger","gugugaga:onion"]},{"id":"fruit_salad_melon","result":"gugugaga:fruit_salad","ingredients":["gugugaga:melon","gugugaga:blueberry","minecraft:sugar"]},{"id":"honey_tea_with_lavender","result":"gugugaga:tea_cup","ingredients":["gugugaga:tea","gugugaga:lavender","minecraft:honey_bottle"]},{"id":"mushroom_medley_with_garlic","result":"gugugaga:mushroom_medley","ingredients":["gugugaga:shiitake_mushroom","gugugaga:oyster_mushroom","gugugaga:garlic"]}]
def main():
    catalog=read("data/gugugaga/agronomy/crop_cuisine_catalog_v1_0.json")["items"]
    catalog_by_id={x["id"]:x for x in catalog}
    assert len(catalog)>=51 and len(catalog_by_id)==len(catalog)
    java=(ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/AgricultureCuisineContent.java").read_text("utf8")
    generator=(ROOT/"tools/crop-cuisine-textures.gradle").read_text("utf8")
    arts=(ROOT/"qa/validate_generated_crop_cuisine_art.py").read_text("utf8")
    en=read("assets/gugugaga/lang/en_us.json")
    zh=read("assets/gugugaga/lang/zh_cn.json")
    for row in NEW_DISHES:
        id=row["id"]
        assert id in catalog_by_id
        item=catalog_by_id[id]
        assert item["type"]=="food"
        assert f'food("{id}",' in java
        assert f"event.accept({id.upper()}.get())" in java
        assert en[f"item.gugugaga.{id}"] and zh[f"item.gugugaga.{id}"]
        model=read(f"assets/gugugaga/models/item/{id}.json")
        assert model["textures"]["layer0"]==f"gugugaga:item/crop_cuisine/{id}"
        recipe=read(f"data/gugugaga/recipe/{id}.json")
        assert recipe["result"]["id"]==f"gugugaga:{id}"
        if row["method"]=="smelt":
            assert recipe["type"]=="minecraft:smelting"
            assert recipe["ingredient"]["item"]==row["ingredients"][0]
            assert recipe["cookingtime"]==200
            for suffix,kind,ticks in [("smoking","minecraft:smoking",100),("campfire","minecraft:campfire_cooking",600)]:
                variant=read(f"data/gugugaga/recipe/{id}_{suffix}.json")
                assert variant["type"]==kind
                assert variant["ingredient"]==recipe["ingredient"]
                assert variant["result"]==recipe["result"]
                assert variant["cookingtime"]==ticks
        else:
            assert recipe["type"]=="minecraft:crafting_shapeless"
            assert [x["item"] for x in recipe["ingredients"]]==row["ingredients"]
    for row in ALTERNATIVES:
        rec=read(f'data/gugugaga/recipe/{row["id"]}.json')
        assert rec["type"]=="minecraft:crafting_shapeless"
        assert rec["result"]["id"]==row["result"]
        assert [x["item"] for x in rec["ingredients"]]==row["ingredients"]
    assert "68 unique crop-cuisine item icons" in generator
    assert "68 distinct 16x16 icons" in generator
    assert "(288, 432)" in arts
    assert len(NEW_DISHES)==16 and len(ALTERNATIVES)==15
    print(f"CROP_CUISINE_V1_1_OK: 16 v1.1 dishes, 15 alternate recipes, 4 cooking variants, {len(catalog)} total cuisine items")
if __name__=="__main__":
    main()
