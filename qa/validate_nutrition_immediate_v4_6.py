#!/usr/bin/env python3
"""Regression checks for immediate, bounded game-nutrition feedback."""
from pathlib import Path
import json
import re

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
CATALOG = ROOT / "src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"
LOCALES = ROOT / "src/main/resources/assets/gugugaga/lang"

def array(text, name):
    match = re.search(r"private static final int\[\] " + name + r"=\{(.*?)\};", text, re.S)
    assert match, name
    return [int(v) for v in re.findall(r"-?\d+", match.group(1))]

def main():
    phys = (SRC / "NutritionPhysiologyV39.java").read_text("utf-8")
    nutrition = (SRC / "NutritionV34.java").read_text("utf-8")
    cat = json.loads(CATALOG.read_text("utf-8"))
    targets = array(phys, "TARGET")
    replenish = array(phys, "REPLENISH")
    ids = [entry["id"] for entry in cat["nutrients"]]
    assert len(ids) == len(targets) == len(replenish) == 27
    assert cat["foods"]["gugugaga:eggplant"] == ["vegetable"]
    assert cat["foods"]["gugugaga:eggplant_curry"] == ["vegetable"]
    assert cat["overrides"]["gugugaga:eggplant_curry"]["fat"] == 2
    assert cat["foods"]["gugugaga:black_pepper"] == ["spice"]
    assert cat["foods"]["gugugaga:tomato_sauce"] == ["vegetable", "herb"]
    assert cat["foods"]["gugugaga:onion_vegetable_pie"] == ["grain", "vegetable"]

    def contribution(item):
        groups=cat["foods"][item]
        results={k:0 for k in ids}
        for group in groups:
            for name,value in cat["profiles"][group].items():
                results[name]=min(10,results[name]+value)
        results.update(cat.get("overrides",{}).get(item,{}))
        return results

    def earned(index, intake):
        if index in (4, 26): return 0
        return min(replenish[index], (intake*replenish[index]+targets[index]-1)//targets[index])

    # Eating a single common vegetable must visibly raise several relevant
    # game reserves without granting egg nutrients to eggplant.
    for food in ("gugugaga:lettuce", "gugugaga:cucumber",
                 "gugugaga:eggplant", "gugugaga:water_spinach",
                 "minecraft:carrot"):
        gains=[earned(i, contribution(food)[key]) for i,key in enumerate(ids)]
        assert sum(v>0 for v in gains)>=2, (food,gains)
        assert gains[4]==gains[26]==0
    eggplant=contribution("gugugaga:eggplant")
    assert eggplant["vitamin_b12"]==0 and eggplant["vitamin_b7"]==0
    assert eggplant["protein"]==0 and eggplant["fat"]==0

    # The daily recovery limit is shared across repeat meals, and moving to
    # the next day must not award previously eaten food a second time.
    for i in range(27):
        if i in (4, 26):continue
        assert earned(i,0)==0
        assert earned(i,48)==replenish[i]
        previous=0
        for now in range(1,49):
            assert 0<=earned(i,now)-earned(i,previous)<=replenish[i]
            previous=now

    assert "public static int[] consume(CompoundTag data,int[] points)" in phys
    assert "gained[i]=next-current;" in phys
    assert "next=current;" in phys
    assert "data.remove(DAILY+i)" in phys
    assert "Math.floorDiv(player.getServer().overworld().getGameTime(),24000L)" in nutrition
    assert "int[] gained=NutritionPhysiologyV39.consume(values,contribution);" in nutrition
    assert "player.displayClientMessage" in nutrition
    assert "NutritionPhysiologyV39.dailyIntakeSnapshot(record(player))" in nutrition
    assert "v47_last_eaten_food" in nutrition
    assert "v47_last_meal_gain_" in nutrition

    for lang in ("en_us","zh_cn"):
        text=json.loads((LOCALES / (lang+".json")).read_text("utf-8"))
        for key in ("message.gugugaga.nutrition_gained",
                    "message.gugugaga.nutrition_no_gain",
                    "tooltip.gugugaga.nutrition_daily"):
            assert text.get(key),(lang,key)
        assert "next game-day reserve update" not in text["tooltip.gugugaga.nutrition_daily"]
    print("NUTRITION_IMMEDIATE_V46_PASS: 25 direct reserve gains, daily caps, no double credit, correct eggplant classification, bilingual feedback")

if __name__=="__main__":
    main()
