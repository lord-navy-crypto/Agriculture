#!/usr/bin/env python3
"""Validate 403-real-food category integrity and player-readable meal feedback."""
import json
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
DB=ROOT/"src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"

def main():
    data=json.loads(DB.read_text(encoding="utf-8"))
    profiles=data["profiles"]
    foods=data["foods"]
    overrides=data.get("overrides",{})
    nutrients=[n["id"] for n in data["nutrients"]]
    assert len(foods)==403 and len(nutrients)==27 and len(set(nutrients))==27
    assert len(overrides)>=60
    for food,groups in foods.items():
        assert groups and len(groups)==len(set(groups)),food
        assert all(g in profiles for g in groups),(food,groups)
        points={id:0 for id in nutrients}
        for group in groups:
            for n,value in profiles[group].items():
                assert n in points and 0<=value<=8,(group,n,value)
                points[n]=min(10,points[n]+value)
        for n,value in overrides.get(food,{}).items():
            assert n in points and n not in ("hydration","sodium")
            assert 0<=value<=10,(food,n,value)
            points[n]=value
        # A profile may not produce out-of-range or negative nutrient points.
        assert all(0<=value<=10 for value in points.values()),food
    assert all(food in foods for food in overrides)
    # Previously observed false positives and recipe/category contradictions.
    assert foods["gugugaga:eggplant"]==["vegetable"]
    assert foods["gugugaga:eggplant_curry"]==["vegetable"]
    assert foods["gugugaga:black_pepper"]==["spice"]
    assert foods["gugugaga:tomato_sauce"]==["vegetable","herb"]
    assert foods["gugugaga:onion_vegetable_pie"]==["grain","vegetable"]
    assert foods["gugugaga:stuffed_pepper"]==["grain","vegetable"]
    assert "minecraft:slice_of_cake" not in foods
    assert foods["gugugaga:lion_mane_steak"] == ["mushroom","grain","root"]
    assert foods["gugugaga:lemongrass"] == ["herb"]
    assert foods["gugugaga:lemongrass_sardine_soup"] == ["seafood","seaweed","herb","beverage"]
    assert foods["gugugaga:mushroom_sauce"] == ["mushroom","grain"]
    assert foods["gugugaga:peanut_sauce"] == ["legume","seed","vegetable","sweet"]
    assert foods["gugugaga:smoked_cod_pie"] == ["seafood","grain","root"]
    assert foods["gugugaga:honey_berry_toast"] == ["grain","berries","honey"]
    assert foods["gugugaga:rice_vinegar_salad"] == ["leafy","root"]
    assert overrides["gugugaga:cucumber"]["vitamin_c"] < overrides["gugugaga:bell_pepper"]["vitamin_c"]
    assert overrides["gugugaga:eggplant"]["vitamin_a"] == 0
    assert overrides["gugugaga:spinach"]["vitamin_k"] > overrides["gugugaga:lettuce"]["vitamin_k"]
    assert len({tuple(sorted(overrides["gugugaga:"+item].items())) for item in
                ("tomato","cucumber","eggplant","broccoli","lettuce","spinach")})==6
    for group in ("egg","dairy","meat","seafood"):
        assert all(not (food.endswith("eggplant") and group in groups)
                   for food,groups in foods.items())
    gui=(SRC/"NutritionScreenClientV35.java").read_text()
    request=(SRC/"NutritionNetworkV34.java").read_text()
    reply=(SRC/"NutritionNetworkV35.java").read_text()
    nutrition=(SRC/"NutritionV34.java").read_text()
    physiology=(SRC/"NutritionPhysiologyV39.java").read_text()
    assert "private int page=8" in gui
    assert "page==8?8" in gui
    assert "Component quickRow(int row)" in gui
    assert "NutritionPhysiologyV39.dailyTarget(i)" in gui
    assert "topGains" in gui
    assert "dailyIntakeSnapshot(record(player))" in nutrition
    assert "lastMealSnapshot(player)" in request
    assert "dailyIntakeSnapshot(player)" in request
    assert "lastFoodSnapshot(player)" in request
    assert "today().length!=27" in reply and "recent().length!=27" in reply
    assert "buf.writeUtf" in reply and "buf.readUtf(128)" in reply
    assert 'registrar("4").playToClient' in reply and 'registrar("4").playToServer' in request
    assert "public static int[] dailyIntakeSnapshot(CompoundTag data)" in physiology
    for lang in ("en_us","zh_cn"):
        text=json.loads((ROOT/"src/main/resources/assets/gugugaga/lang"/(lang+".json")).read_text())
        for name in ("message.gugugaga.v35_page_8",
                     "message.gugugaga.nutrition_gained_food",
                     "message.gugugaga.nutrition_no_gain_food",
                     "feedback.gugugaga.quick.last_food",
                     "feedback.gugugaga.quick.daily",
                     "feedback.gugugaga.quick.gain"):
            assert name in text and text[name],(lang,name)
    print("NUTRITION_GUI_AND_PROFILES_V47_PASS: 403 real foods, categorized overrides, immediate top-3 gains, 9-page server-backed bilingual UI")

if __name__=="__main__":
    main()
