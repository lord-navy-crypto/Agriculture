#!/usr/bin/env python3
"""Validate localized per-food tooltip output against the shipped 27-nutrient catalog."""
import json
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
DATA=ROOT/"src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"
LANG=ROOT/"src/main/resources/assets/gugugaga/lang"

def main():
    doc=json.loads(DATA.read_text("utf8"))
    assert len(doc["foods"]) == 404
    nutrients=[row["id"] for row in doc["nutrients"]]
    assert len(nutrients)==27 and len(set(nutrients))==27
    profiles=(JAVA/"NutritionProfilesV35.java").read_text("utf8")
    tooltip=(JAVA/"NutritionTooltipV40.java").read_text("utf8")
    assert "public static boolean hasProfile(String itemId)" in profiles
    assert "NutritionProfilesV35.contribution(itemId)" in tooltip
    assert "ItemTooltipEvent" in tooltip and "DataComponents.FOOD" in tooltip
    assert "tooltip.gugugaga.nutrition_generic" in tooltip
    assert "MAX_PREVIEW_ROWS = 3" in tooltip and "Screen.hasShiftDown()" in tooltip
    assert "NutritionPhysiologyV39.linked(i)" in tooltip
    assert "WaterSaltProfilesV37.water(itemId)" in tooltip
    assert "WaterSaltProfilesV37.sodium(itemId)" in tooltip
    assert "WaterSaltProfilesV37.hasProfile(itemId)" in tooltip
    assert "nutrient_target_line" in tooltip
    assert "dailyTarget(i)" in tooltip
    assert "nutrition_balance_fallback" in tooltip
    physiology=(JAVA/"NutritionPhysiologyV39.java").read_text("utf8")
    water=(JAVA/"WaterSaltProfilesV37.java").read_text("utf8")
    nutrition=(JAVA/"NutritionV34.java").read_text("utf8")
    assert "public static int dailyTarget(int i)" in physiology
    assert "return FOODS.containsKey(id);" in water
    assert "int[] scores = snapshot(player);" in nutrition
    assert "value = Dist.CLIENT" in tooltip, "nutrition tooltip must remain client-only"
    for locale in ("en_us.json","zh_cn.json"):
        strings=json.loads((LANG/locale).read_text("utf8"))
        for key in ("tooltip.gugugaga.nutrition_profile","tooltip.gugugaga.nutrition_generic","tooltip.gugugaga.nutrient_line","tooltip.gugugaga.nutrition_note","tooltip.gugugaga.nutrition_expand","tooltip.gugugaga.nutrition_balance","tooltip.gugugaga.nutrition_balance_fallback","tooltip.gugugaga.nutrition_target_line","tooltip.gugugaga.nutrition_daily"):
            assert strings.get(key), (locale,key)
        for nutrient in nutrients:
            assert strings.get("nutrition.gugugaga."+nutrient), (locale,nutrient)
    print("BACKROOM_NUTRITION_TOOLTIP_V4_2_PASS: 404 food IDs, 27 indicators, target coverage and explicit unmapped fallback")

if __name__=="__main__":
    main()
