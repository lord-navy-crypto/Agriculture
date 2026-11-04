#!/usr/bin/env python3
"""Validate Threshold's food/water loot against Agriculture registries and gameplay profiles."""
from __future__ import annotations
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOOT = ROOT / "threshold/src/main/resources/data/threshold/loot_table/chests"
AGRI = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
NUTRITION = ROOT / "src/main/resources/data/gugugaga/nutrition/food_profiles_v3_5.json"
WATER = ROOT / "src/main/resources/data/gugugaga/nutrition/water_salt_profiles_v3_7.json"

TABLES = (
    "level_0_forgotten.json",
    "level_1_supply.json",
    "level_1_starter.json",
    "level_1_maintenance.json",
)
WATER_ONLY = {
    "gugugaga:raw_water_bottle",
    "gugugaga:boiled_water_bottle",
    "gugugaga:filtered_water_bottle",
}
NON_FOOD = {
    "gugugaga:empty_water_flask",
    "gugugaga:barley_seeds",
    "gugugaga:sea_salt",
    "minecraft:glass_bottle",
}

def item_ids(table: dict) -> set[str]:
    return {
        entry["name"]
        for pool in table.get("pools", [])
        for entry in pool.get("entries", [])
        if entry.get("type") == "minecraft:item"
    }

def main() -> None:
    nutrition = json.loads(NUTRITION.read_text(encoding="utf-8"))["foods"]
    water = json.loads(WATER.read_text(encoding="utf-8"))["foods"]
    java_sources = "\n".join(p.read_text(encoding="utf-8") for p in AGRI.glob("*.java"))
    used = set()
    tables = {}
    for filename in TABLES:
        table = json.loads((LOOT / filename).read_text(encoding="utf-8"))
        assert table["type"] == "minecraft:chest"
        tables[filename] = table
        used |= item_ids(table)

    for item in sorted(used):
        if item.startswith("gugugaga:"):
            assert item.split(":", 1)[1] in java_sources, f"Unregistered Agriculture loot item: {item}"
        if item in WATER_ONLY:
            assert item in water, f"Missing hydration/sodium profile: {item}"
        elif item in NON_FOOD:
            continue
        else:
            assert item in nutrition, f"Missing nutrition profile: {item}"
            assert item in water, f"Missing hydration/sodium profile: {item}"

    starter = item_ids(tables["level_1_starter.json"])
    assert {
        "gugugaga:boiled_water_bottle",
        "gugugaga:barley",
        "gugugaga:soybean",
        "gugugaga:tomato",
        "gugugaga:barley_vegetable_soup",
    } <= starter, "Level 1 starter cache must guarantee water, raw produce and a prepared meal"

    crop_source = (AGRI / "AgricultureContent.java").read_text(encoding="utf-8")
    for crop in ("barley_crop", "corn_crop", "soybean_crop", "tomato_crop", "lettuce_crop", "spinach_crop"):
        assert f'crop("{crop}"' in crop_source, f"Unregistered starter planter crop: {crop}"

    nutrition_runtime = (AGRI / "NutritionV34.java").read_text(encoding="utf-8")
    water_runtime = (AGRI / "WaterSaltV37.java").read_text(encoding="utf-8")
    assert "NutritionProfilesV35.contribution(id)" in nutrition_runtime
    assert "NutritionPhysiologyV39.consume(values,contribution)" in nutrition_runtime
    assert "WaterSaltProfilesV37.water(name)" in water_runtime
    assert "WaterSaltProfilesV37.sodium(name)" in water_runtime
    assert "ThresholdDimensions" not in nutrition_runtime + water_runtime, (
        "Agriculture nutrition/hydration must remain dimension-agnostic and work in Threshold")

    print(f"THRESHOLD_AGRICULTURE_SURVIVAL_PROFILE_CONTRACT_PASS: {len(used)} loot items")

if __name__ == "__main__":
    main()
