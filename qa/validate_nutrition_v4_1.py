#!/usr/bin/env python3
"""Deterministic nutrition-catalog audit for Backroom Agriculture v4.1.

Only gameplay balance scores are validated; the values are not real nutrient
amounts or dietary requirements. All old food IDs and saved-player keys stay.
"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
AG = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
CATALOG = ROOT / "src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"
WATER_CATALOG = ROOT / "src/main/resources/data/gugugaga/nutrition/water_salt_profiles_v3_7.json"
LANG = ROOT / "src/main/resources/assets/gugugaga/lang"

def main():
    doc = json.loads(CATALOG.read_text("utf-8"))
    assert doc["schema_version"] == 1
    foods, profiles, overrides = doc["foods"], doc["profiles"], doc["overrides"]
    nutrients = [x["id"] for x in doc["nutrients"]]
    assert len(foods) == 403 and len(nutrients) == 27
    assert len(overrides) >= 40
    assert sum(groups == ["general"] for groups in foods.values()) <= 5
    assert all(id in foods for id in overrides)
    water_doc = json.loads(WATER_CATALOG.read_text("utf-8"))
    water_foods = water_doc["foods"]
    # Every registered edible food in the 403-item curated catalog has an explicit
    # additional entries are drinks that are not part of the nutrient catalog.
    assert set(foods).issubset(water_foods)
    hydration_only = {
        "gugugaga:raw_water_bottle",
        "gugugaga:boiled_water_bottle",
        "gugugaga:filtered_water_bottle",
    }
    assert hydration_only.isdisjoint(foods)
    assert hydration_only.issubset(water_foods)
    assert len(water_foods) >= len(foods) + 4

    # Per-food override values always resolve to a real index and are bounded.
    for item_id, adjustments in overrides.items():
        assert adjustments, item_id
        assert set(adjustments) <= set(nutrients) - {"hydration", "sodium"}
        assert all(type(value) is int and 0 <= value <= 10
                   for value in adjustments.values()), item_id

    # The old profile-combination arithmetic is still used for all foods,
    # while curated item overrides replace only their specified indicators.
    for item_id, groups in foods.items():
        assert groups and all(g in profiles for g in groups), item_id
        summed = {name: 0 for name in nutrients}
        for group in groups:
            for name, value in profiles[group].items():
                summed[name] = min(10, summed[name] + value)
        summed.update(overrides.get(item_id, {}))
        assert all(0 <= v <= 10 for v in summed.values()), item_id

    assert overrides["minecraft:carrot"]["vitamin_a"] == 7
    assert overrides["minecraft:cooked_salmon"]["vitamin_b12"] == 6
    assert overrides["gugugaga:golden_hash_browns"]["fat"] == 3

    parser = (AG / "NutritionProfilesV35.java").read_text("utf-8")
    nutrition = (AG / "NutritionV34.java").read_text("utf-8")
    tooltip = (AG / "NutritionTooltipV40.java").read_text("utf-8")
    water = (AG / "WaterSaltV37.java").read_text("utf-8")
    water_profiles = (AG / "WaterSaltProfilesV37.java").read_text("utf-8")
    tooltip_source = tooltip
    for token in ('doc.has("overrides")', 'result.get(food.getKey())',
                  'nutrient == 4 || nutrient == 26', 'values[nutrient] = points'):
        assert token in parser, token
    assert "getServer().overworld().getGameTime()" in nutrition
    assert "NutritionPhysiologyV39.consume(values,contribution)" in nutrition
    assert "WaterSaltProfilesV37.water(name)" in water
    assert "WaterSaltProfilesV37.sodium(name)" in water
    assert "public static boolean hasProfile(String id)" in water_profiles
    assert "WaterSaltProfilesV37.hasProfile(itemId)" in tooltip_source
    assert "HYDRATION_ONLY" in parser
    assert "isHydrationOnly(String itemId)" in parser
    assert "if (HYDRATION_ONLY.contains(itemId)) return new int[NUTRIENTS.length];" in parser
    assert "NutritionProfilesV35.isHydrationOnly(id)" in nutrition
    assert "NutritionProfilesV35.isHydrationOnly(itemId)" in tooltip
    assert "tooltip.gugugaga.hydration_only" in tooltip
    assert "NutritionPhysiologyV39.linked(i)" in tooltip
    assert "WaterSaltProfilesV37.water(itemId)" in tooltip
    assert "WaterSaltProfilesV37.sodium(itemId)" in tooltip
    for locale in ("en_us.json", "zh_cn.json"):
        lang = json.loads((LANG / locale).read_text("utf-8"))
        assert lang.get("tooltip.gugugaga.nutrition_balance"), locale
        assert lang.get("tooltip.gugugaga.hydration_only"), locale
        assert lang.get("tooltip.gugugaga.nutrition_daily"), locale
    print("BACKROOM_NUTRITION_V4_7_PASS: 403 real food IDs, curated overrides, complete water/salt coverage, common clock")

if __name__ == "__main__":
    main()
