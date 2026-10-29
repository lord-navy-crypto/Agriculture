#!/usr/bin/env python3
"""Nutrition v3.5 catalog covers every edible registered item, 13 vitamins and nine minerals.

No clinical nutrition claims: values are bounded fictional per-food game points.
"""
import json
import re
import sys
import zipfile
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
ASSETS=ROOT/"src/main/resources/assets/gugugaga"
PATH=ROOT/"src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"

def load(path):
    return json.loads(path.read_text("utf-8"))

def main():
    document=load(PATH)
    assert document["schema_version"]==1
    assert "NOT nutrient mass" in document["unit"]
    nutrients=[e["id"] for e in document["nutrients"]]
    expected="carbohydrate protein fat fiber hydration vitamin_a vitamin_b1 vitamin_b2 vitamin_b3 vitamin_b5 vitamin_b6 vitamin_b7 vitamin_b9 vitamin_b12 vitamin_c vitamin_d vitamin_e vitamin_k calcium iron magnesium potassium zinc iodine selenium phosphorus sodium".split()
    assert nutrients==expected and len(set(nutrients))==27
    assert sum(n.startswith("vitamin_") for n in nutrients)==13
    assert len(nutrients[18:])==9

    profiles=document["profiles"]
    foods=document["foods"]
    assert len(profiles)>=16 and len(foods)>=400
    for profile,values in profiles.items():
        assert values and set(values).issubset(nutrients), profile
        assert all(isinstance(v,int) and 0<=v<=8 for v in values.values()),profile
    for id,names in foods.items():
        assert re.fullmatch(r"(minecraft|gugugaga):[a-z_0-9]+",id),id
        assert 1<=len(names)<=4,id
        assert all(n in profiles for n in names),id

    sources=("AgricultureContent","AgricultureCuisineContent","KitchenContent",
             "KitchenV3Content","KitchenV31Content","KitchenV32Content",
             "FoodEcologyV33Content","FoodV34Content","MarineFungiContent")
    edible=set()
    for file in sources:
        source=(SRC/(file+".java")).read_text("utf-8")
        edible|={"gugugaga:"+m for m in re.findall(r'\bfood\("([a-z_0-9]+)"',source)}
    honey=(SRC/"BeeContent.java").read_text("utf-8")
    honey_ids={"gugugaga:"+m for m in re.findall(r'floralHoney\("([a-z_0-9]+)"',honey)}
    assert len(honey_ids)==50
    edible|=honey_ids
    missing=sorted(edible-set(foods))
    assert not missing,"Edible mod items with no nutrition profile: "+str(missing)
    assert len(edible)>350
    assert all(foods[id]==["honey"] for id in honey_ids)

    for id in ("minecraft:apple","minecraft:bread","minecraft:cooked_salmon",
               "minecraft:beef","minecraft:carrot","minecraft:baked_potato",
               "minecraft:honey_bottle","minecraft:golden_carrot","minecraft:suspicious_stew"):
        assert id in foods
    assert "general" in profiles
    java=(SRC/"NutritionProfilesV35.java").read_text("utf-8")
    logic=(SRC/"NutritionV34.java").read_text("utf-8")
    net=(SRC/"NutritionNetworkV34.java").read_text("utf-8")
    client=(SRC/"NutritionKeyTickV34.java").read_text("utf-8")
    key=(SRC/"NutritionKeyV34.java").read_text("utf-8")
    assert "JsonParser.parseReader" in java
    assert "getResourceAsStream(RESOURCE)" in java
    assert 'food_profiles_v3_5.json' in java
    assert "VALUES.get(itemId)" in java
    assert "return Arrays.copyOf(values,values.length)" in java
    assert "new IllegalStateException" in java
    assert "NutritionProfilesV35.contribution(id)" in logic
    assert "LivingEntityUseItemEvent.Finish" in logic
    assert "DataComponents.FOOD" in logic
    assert "getKey(food.getItem()).toString()" in logic
    assert "gugugaga_nutrition_v35" in logic and "gugugaga_nutrition_v34" in logic
    assert "copy(old,migrated" in logic
    assert "PlayerEvent.Clone" in logic
    assert "player.getPersistentData().put(ROOT,values)" in logic
    assert "MobEffects.DIG_SPEED" in logic and "MobEffects.MOVEMENT_SPEED" in logic
    assert "varied(values)" in logic and "highlyVaried(values)" in logic
    assert "player.sendSystemMessage" in logic
    assert 'PAGES={{0,5},{5,12},{12,18},{18,23},{23,27}}' in logic
    assert '"page",(page+1)%PAGE_COUNT' in logic
    assert "StreamCodec.unit(new Request())" in net
    assert "playToServer" in net and "NutritionV34.show(player)" in net
    assert "PacketDistributor.sendToServer" in client
    assert "GLFW.GLFW_KEY_N" in key
    for l in ("en_us","zh_cn"):
        strings=load(ASSETS/f"lang/{l}.json")
        assert all(strings["nutrition.gugugaga."+name] for name in nutrients),l
        for page in range(5):
            assert strings[f"message.gugugaga.v35_page_{page}"]
        for key in ("v35_title","v35_bonus_varied","v35_bonus_advanced",
                    "v35_bonus_explore","v35_next_page"):
            assert strings["message.gugugaga."+key]

    if len(sys.argv)>1:
        jar=Path(sys.argv[1])
        assert jar.is_file(),jar
        with zipfile.ZipFile(jar) as archive:
            assert archive.testzip() is None
            contained=archive.read("data/gugugaga/nutrition/food_profiles_v3_5.json")
            assert json.loads(contained)==document
            for cls in ("NutritionProfilesV35","NutritionV34","NutritionNetworkV34",
                        "NutritionKeyV34","NutritionKeyTickV34"):
                assert "io/github/lordnavycrypto/gugugaga/agriculture/"+cls+".class" in archive.namelist()
    print(f"NUTRITION_V35_OK: {len(edible)} edible Agriculture items including 50 honey bottles, "
          f"{len(foods)} explicit mapped foods, 27 metric groups, migration and five server-owned N-key pages")

if __name__=="__main__":
    main()
