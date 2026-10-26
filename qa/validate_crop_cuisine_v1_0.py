#!/usr/bin/env python3
"""Validate the crop-cuisine catalog, recipe coverage, item registration, and assets."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
EXPECTED_ITEMS = {
    "garden_salad",
    "vegetable_stew",
    "roasted_root_vegetables",
    "crop_stir_fry",
    "grain_porridge",
    "barley_vegetable_soup",
    "rice_vegetable_bowl",
    "bean_stew",
    "stuffed_pepper",
    "eggplant_curry",
    "pumpkin_soup",
    "berry_pie",
    "fruit_salad",
    "cornbread",
    "cabbage_rolls",
    "tomato_sauce",
    "herb_bread",
    "sweet_potato_roast",
    "cassava_flatbread",
    "taro_dumplings",
    "yam_cakes",
    "chickpea_hummus",
    "peanut_sauce",
    "roasted_seed_mix",
    "herbal_tea",
    "tea_cup",
    "coffee_cup",
    "honey_mustard_dressing",
    "farmers_skillet",
    "buckwheat_pancakes",
    "quinoa_salad",
    "lentil_curry",
    "sweet_corn_fritters",
    "canola_oil",
    "safflower_oil",
}

def load(path):
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    catalog = load(RES / "data/gugugaga/agronomy/crop_cuisine_catalog_v1_0.json")
    items = catalog["items"]
    ids = {item["id"] for item in items}
    assert EXPECTED_ITEMS.issubset(ids), f"catalog mismatch: missing={EXPECTED_ITEMS-ids}, extra={ids-EXPECTED_ITEMS}"
    assert len(items) >= 51 and len(ids) == len(items)
    assert sum(item["type"] == "food" for item in items) == len(items) - 2
    assert sum(item["type"] == "material" for item in items) == 2

    en = load(RES / "assets/gugugaga/lang/en_us.json")
    zh = load(RES / "assets/gugugaga/lang/zh_cn.json")
    cuisine_java = (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/AgricultureCuisineContent.java").read_text(encoding="utf-8")
    main_java = (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaMod.java").read_text(encoding="utf-8")
    build_gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
    texture_gradle = (ROOT / "tools/crop-cuisine-textures.gradle").read_text(encoding="utf-8")
    recipes_dir = RES / "data/gugugaga/recipe"

    for item in items:
        item_id = item["id"]
        assert f"item.gugugaga.{item_id}" in en and f"item.gugugaga.{item_id}" in zh, item_id
        model = load(RES / f"assets/gugugaga/models/item/{item_id}.json")
        assert model["parent"] == "minecraft:item/generated"
        assert model["textures"]["layer0"] == f"gugugaga:item/crop_cuisine/{item_id}"
        recipe = load(recipes_dir / f"{item_id}.json")
        assert recipe["type"] in {"minecraft:crafting_shapeless", "minecraft:smelting"}, item_id
        assert recipe["result"]["id"] == f"gugugaga:{item_id}", item_id
        if recipe["type"] == "minecraft:crafting_shapeless":
            assert 2 <= len(recipe["ingredients"]) <= 9, item_id
        else:
            assert recipe["cookingtime"] >= 100 and recipe["ingredient"]["item"].startswith("gugugaga:"), item_id
        if item["type"] == "food":
            pattern = rf'food\("{re.escape(item_id)}",\s*{item["nutrition"]},\s*{item["saturation_modifier"]:.2f}F\)'
            assert re.search(pattern, cuisine_java), f"food registry mismatch: {item_id}"
        else:
            assert f'material("{item_id}")' in cuisine_java, item_id

    crops_java = (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/AgricultureContent.java").read_text(encoding="utf-8")
    crop_foods = set(re.findall(r'= food\("([^"]+)",\s*\d+,\s*[0-9.]+F?\);', crops_java))
    assert len(crop_foods) == 88, f"expected 88 crop foods, got {len(crop_foods)}"
    tag_names = ["any_crop_food","leafy_greens","vegetables","root_vegetables","grains","legumes","berries","fruits","culinary_herbs"]
    tag_values = {}
    for name in tag_names:
        path = RES / f"data/gugugaga/tags/item/cooking/{name}.json"
        tag = load(path)
        values = set(tag["values"])
        tag_values[name] = values
        assert tag.get("replace") is False
        assert values, name
    all_food_values = tag_values["any_crop_food"]
    assert {f"gugugaga:{crop}" for crop in crop_foods}.issubset(all_food_values), "some crop foods are not usable in the farmer's skillet"
    referenced_tags = {
        ingredient["tag"]
        for recipe in (load(path) for path in recipes_dir.glob("*.json"))
        for ingredient in recipe.get("ingredients", []) if "tag" in ingredient
    }
    assert "gugugaga:cooking/any_crop_food" in referenced_tags
    assert all(f"gugugaga:cooking/{name}" in referenced_tags for name in tag_names)

    recipe_ids = {path.stem for path in recipes_dir.glob("*.json")}
    assert EXPECTED_ITEMS.issubset(recipe_ids)
    for recipe_id, required_item in {
        "cassava_flatbread":"gugugaga:cassava",
        "taro_dumplings":"gugugaga:taro",
        "yam_cakes":"gugugaga:yam",
        "tea_cup":"gugugaga:tea",
        "coffee_cup":"gugugaga:coffee",
        "honey_mustard_dressing":"gugugaga:mustard",
        "canola_oil":"gugugaga:canola",
        "safflower_oil":"gugugaga:safflower",
    }.items():
        recipe = load(recipes_dir / f"{recipe_id}.json")
        assert any(entry.get("item") == required_item for entry in recipe["ingredients"]), recipe_id

    assert "gugugaga:cotton" not in all_food_values
    assert "gugugaga:flax" not in all_food_values
    assert "AgricultureCuisineContent.ITEMS.register(modBus)" in main_java
    assert "AgricultureCuisineContent::onCreativeTab" in main_java
    assert "GUGUGAGA_CROP_CUISINE_RUNTIME_INIT_OK" in main_java
    assert "tools/crop-cuisine-textures.gradle" in build_gradle
    assert "generateCropCuisineAssets" in texture_gradle
    assert "crop-cuisine-item-contact-sheet.png" in texture_gradle
    smoke = (ROOT / "qa/smoke_rewrite_server.py").read_text(encoding="utf-8")
    assert "GUGUGAGA_CROP_CUISINE_RUNTIME_INIT_OK" in smoke
    assert "GUGUGAGA_AGRICULTURE_MARINE_CUISINE_REAL_SERVER_BOOT_PASS" in smoke
    print(f"CROP_CUISINE_V1_0_OK: 88 crop foods covered; {len(items)-2} food items and 2 processed oils registered; recipes, tags, bilingual names, models and asset generator validated.")

if __name__ == "__main__":
    main()
