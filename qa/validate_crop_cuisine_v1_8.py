#!/usr/bin/env python3
"""Validate final plant-based cuisine coverage, recipes, and crop item resources."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
RECIPES = RES / "data/gugugaga/recipe"
VANILLA_PLANTS = ["minecraft:apple","minecraft:beetroot","minecraft:bread","minecraft:baked_potato","minecraft:carrot","minecraft:cocoa_beans","minecraft:cookie","minecraft:dried_kelp","minecraft:glow_berries","minecraft:kelp","minecraft:melon_slice","minecraft:potato","minecraft:pumpkin","minecraft:sugar","minecraft:sweet_berries","minecraft:wheat"]
PLANT_DISHES = ["garden_salad","vegetable_stew","roasted_root_vegetables","crop_stir_fry","grain_porridge","barley_vegetable_soup","rice_vegetable_bowl","bean_stew","stuffed_pepper","eggplant_curry","pumpkin_soup","berry_pie","fruit_salad","cornbread","cabbage_rolls","tomato_sauce","herb_bread","sweet_potato_roast","cassava_flatbread","taro_dumplings","yam_cakes","chickpea_hummus","peanut_sauce","roasted_seed_mix","herbal_tea","farmers_skillet","buckwheat_pancakes","quinoa_salad","lentil_curry","sweet_corn_fritters","basil_tomato_flatbread","pumpkin_rice","chili_bean_chili","sesame_noodles","garlic_roasted_potatoes","beetroot_soup","fava_bean_soup","chayote_stir_fry","soybean_patties","roasted_chickpeas","baked_sweet_corn","adzuki_bean_sweet_soup","mung_bean_congee","napa_cabbage_braise","garlic_water_spinach","tomato_okra_stew","jute_mallow_soup","grape_berry_compote","rapini_garlic_saute","root_vegetable_hash","millet_fennel_pilaf","cucumber_mint_salad","seaweed_potato_chowder","blackcurrant_oat_crumble","baked_taro","roasted_yam"]
ALLOWED_GROUPS = {"crop_hybrid_plant_based", "vanilla_plant_only"}

def load(path):
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    manifest = load(RES / "data/gugugaga/agronomy/plant_based_crop_cuisine_v1_8.json")
    assert manifest["schema_version"] == "1.8"
    rows = manifest["alternates"]
    assert len(rows) == 100 and len({row["id"] for row in rows}) == 100

    java_dir = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
    crop_java = (java_dir / "AgricultureContent.java").read_text(encoding="utf-8")
    cuisine_java = (java_dir / "AgricultureCuisineContent.java").read_text(encoding="utf-8")
    other_java = "\n".join((java_dir / name).read_text(encoding="utf-8") for name in
        ("MarineFungiContent.java", "BeeContent.java"))
    crop_foods = set(re.findall(r'= food\("([^"]+)",\s*\d+,\s*[0-9.]+F?\);', crop_java))
    crop_blocks = set(re.findall(r'= crop\("([^"]+)"', crop_java))
    dish_ids = set(re.findall(r'food\("([^"]+)"', cuisine_java))
    known = set(re.findall(r'\b(?:food|material|ingredient|culture|marineCrop|floralHoney)\("([^"]+)"', crop_java + cuisine_java + other_java))
    known.update(["kelp_flakes", "mushroom_spores"])
    assert len(crop_foods) == 88, f"expected 88 edible crop foods, found {len(crop_foods)}"
    assert len(crop_blocks) == 100, f"expected 100 registered crop blocks, found {len(crop_blocks)}"
    assert set(PLANT_DISHES).issubset(dish_ids), "plant recipe output is not registered"

    en = load(RES / "assets/gugugaga/lang/en_us.json")
    zh = load(RES / "assets/gugugaga/lang/zh_cn.json")
    for crop in crop_blocks:
        blockstate = load(RES / f"assets/gugugaga/blockstates/{crop}_crop.json")
        variants = blockstate["variants"]
        assert len(variants) == 16, f"{crop}: expected 16 growth/pollination variants"
        for age in range(8):
            base = f"gugugaga:block/crops/{crop}_stage{age}"
            assert variants[f"age={age},pollinated=false"]["model"] == base, crop
            expected = base + ("_pollinated" if age >= 4 else "")
            assert variants[f"age={age},pollinated=true"]["model"] == expected, crop
        loot = load(RES / f"data/gugugaga/loot_table/blocks/{crop}_crop.json")
        assert len(loot["pools"]) >= 2, f"{crop}: seed and produce drops are required"
        for item, texture in ((crop, f"gugugaga:item/crops/{crop}"),
                              (crop + "_seeds", f"gugugaga:item/crops/{crop}_seeds")):
            model = load(RES / f"assets/gugugaga/models/item/{item}.json")
            assert model["parent"] == "minecraft:item/generated", item
            assert model["textures"]["layer0"] == texture, item
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh, item
        assert f"block.gugugaga.{crop}_crop" in en and f"block.gugugaga.{crop}_crop" in zh, crop
        seed_recipe = load(RECIPES / f"{crop}_seeds.json")
        assert seed_recipe["result"]["id"] == f"gugugaga:{crop}_seeds", crop

    def ensure_known(identifier):
        if identifier.startswith("gugugaga:"):
            assert identifier.split(":", 1)[1] in known, f"unregistered custom item: {identifier}"
        else:
            assert identifier in VANILLA_PLANTS, f"ingredient is not on the plant-only allowlist: {identifier}"

    tags = {}
    tag_root = RES / "data/gugugaga/tags/item"
    for path in tag_root.rglob("*.json"):
        rel = path.relative_to(tag_root).with_suffix("")
        tags[f"gugugaga:{str(rel).replace(chr(92), '/')}"] = set(load(path).get("values", []))

    def matches(ingredient, item):
        if "item" in ingredient:
            return ingredient["item"] == item
        tag = ingredient.get("tag")
        return tag in tags and item in tags[tag]

    def overlap(existing, chosen):
        if len(existing) != len(chosen) or not all("item" in item or "tag" in item for item in existing):
            return False
        def visit(index, available):
            if index == len(existing):
                return True
            for position, item in enumerate(available):
                if matches(existing[index], item) and visit(index + 1, available[:position] + available[position + 1:]):
                    return True
            return False
        return visit(0, list(chosen))

    previous = []
    for path in RECIPES.glob("*.json"):
        recipe = load(path)
        if recipe.get("type") == "minecraft:crafting_shapeless":
            previous.append((path.stem, recipe["ingredients"]))

    seen_targets = set()
    for row in rows:
        recipe_id = row["id"]
        assert row["group"] in ALLOWED_GROUPS, recipe_id
        ingredients = row["ingredients"]
        assert len(ingredients) == 3 and len(set(ingredients)) == 3, recipe_id
        for item in [*ingredients, row["output"]]:
            ensure_known(item)
        assert row["output"].startswith("gugugaga:") and row["output"].split(":", 1)[1] in PLANT_DISHES, recipe_id
        recipe = load(RECIPES / f"{recipe_id}.json")
        assert recipe["type"] == "minecraft:crafting_shapeless", recipe_id
        assert recipe["category"] == "misc", recipe_id
        assert recipe["result"] == {"id": row["output"], "count": row["count"]}, recipe_id
        assert [ingredient.get("item") for ingredient in recipe["ingredients"]] == ingredients, recipe_id

        mod_ingredients = [item for item in ingredients if item.startswith("gugugaga:")]
        if row["group"] == "crop_hybrid_plant_based":
            target = row.get("target_crop")
            assert target in crop_foods, f"unknown target crop: {recipe_id}"
            assert target not in seen_targets, f"duplicate target crop: {target}"
            seen_targets.add(target)
            assert f"gugugaga:{target}" in ingredients, f"target crop not used: {recipe_id}"
            assert len(mod_ingredients) == 2 and all(item.split(":", 1)[1] in crop_foods for item in mod_ingredients), recipe_id
            assert sum(item.startswith("minecraft:") for item in ingredients) == 1, recipe_id
        else:
            assert "target_crop" not in row, recipe_id
            assert not mod_ingredients and all(item in VANILLA_PLANTS for item in ingredients), recipe_id

        for other_id, existing in previous:
            if other_id == recipe_id:
                continue
            assert not overlap(existing, ingredients), f"recipe {recipe_id} duplicates the ingredient pattern of {other_id}"

        dish = row["output"].split(":", 1)[1]
        model = load(RES / f"assets/gugugaga/models/item/{dish}.json")
        assert model["parent"] == "minecraft:item/generated", dish
        assert model["textures"]["layer0"] == f"gugugaga:item/crop_cuisine/{dish}", dish
        assert f"item.gugugaga.{dish}" in en and f"item.gugugaga.{dish}" in zh, dish

    assert seen_targets == crop_foods, f"crop recipe coverage mismatch: missing={sorted(crop_foods-seen_targets)}, extra={sorted(seen_targets-crop_foods)}"
    assert sum(row["group"] == "crop_hybrid_plant_based" for row in rows) == 88
    assert sum(row["group"] == "vanilla_plant_only" for row in rows) == 12
    print("PLANT_CUISINE_V1_8_OK: 88/88 edible crop foods have dedicated plant-only variants; 12 vanilla-only recipes; 100 crop blocks, item/seed models, bilingual names and loot tables checked; all dish assets resolve; no shapeless recipe collisions.")

if __name__ == "__main__":
    main()
