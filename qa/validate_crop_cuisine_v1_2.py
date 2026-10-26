#!/usr/bin/env python3
"""Catalog-driven cooking and alternate-recipe regression checks for v1.2."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
def load(path):
    return json.loads((RES / path).read_text(encoding="utf-8"))

DISHES = [
  {
    "id": "adzuki_bean_sweet_soup",
    "method": "craft",
    "ingredients": [
      "gugugaga:adzuki_bean",
      "gugugaga:rice",
      "minecraft:sugar"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "mung_bean_congee",
    "method": "craft",
    "ingredients": [
      "gugugaga:mung_bean",
      "gugugaga:rice",
      "minecraft:bowl"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "napa_cabbage_braise",
    "method": "craft",
    "ingredients": [
      "gugugaga:napa_cabbage",
      "gugugaga:ginger",
      "minecraft:carrot"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "garlic_water_spinach",
    "method": "craft",
    "ingredients": [
      "gugugaga:water_spinach",
      "gugugaga:garlic",
      "gugugaga:canola_oil"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "tomato_okra_stew",
    "method": "craft",
    "ingredients": [
      "gugugaga:okra",
      "gugugaga:tomato",
      "gugugaga:onion"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "jute_mallow_soup",
    "method": "craft",
    "ingredients": [
      "gugugaga:jute_mallow",
      "minecraft:carrot",
      "minecraft:bowl"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "pineapple_tart",
    "method": "craft",
    "ingredients": [
      "gugugaga:pineapple",
      "minecraft:wheat",
      "minecraft:sugar"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "grape_berry_compote",
    "method": "craft",
    "ingredients": [
      "gugugaga:grape",
      "gugugaga:blackcurrant",
      "minecraft:sugar"
    ],
    "nutrition": 5,
    "saturation_modifier": 0.55
  },
  {
    "id": "rapini_garlic_saute",
    "method": "craft",
    "ingredients": [
      "gugugaga:rapini",
      "gugugaga:garlic",
      "gugugaga:safflower_oil"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "root_vegetable_hash",
    "method": "craft",
    "ingredients": [
      "gugugaga:rutabaga",
      "gugugaga:celeriac",
      "minecraft:baked_potato"
    ],
    "nutrition": 7,
    "saturation_modifier": 0.75
  },
  {
    "id": "millet_fennel_pilaf",
    "method": "craft",
    "ingredients": [
      "gugugaga:millet",
      "gugugaga:fennel",
      "gugugaga:onion"
    ],
    "nutrition": 7,
    "saturation_modifier": 0.75
  },
  {
    "id": "mushroom_barley_risotto",
    "method": "craft",
    "ingredients": [
      "gugugaga:barley",
      "gugugaga:shiitake_mushroom",
      "minecraft:milk_bucket"
    ],
    "nutrition": 7,
    "saturation_modifier": 0.75
  },
  {
    "id": "cucumber_mint_salad",
    "method": "craft",
    "ingredients": [
      "gugugaga:cucumber",
      "gugugaga:mint",
      "minecraft:beetroot"
    ],
    "nutrition": 5,
    "saturation_modifier": 0.55
  },
  {
    "id": "seaweed_potato_chowder",
    "method": "craft",
    "ingredients": [
      "gugugaga:sea_lettuce",
      "minecraft:potato",
      "gugugaga:oyster_mushroom"
    ],
    "nutrition": 7,
    "saturation_modifier": 0.75
  },
  {
    "id": "blackcurrant_oat_crumble",
    "method": "craft",
    "ingredients": [
      "gugugaga:blackcurrant",
      "gugugaga:oat",
      "minecraft:sugar"
    ],
    "nutrition": 6,
    "saturation_modifier": 0.65
  },
  {
    "id": "baked_taro",
    "method": "smelt",
    "ingredients": [
      "gugugaga:taro"
    ],
    "nutrition": 5,
    "saturation_modifier": 0.6
  },
  {
    "id": "roasted_yam",
    "method": "smelt",
    "ingredients": [
      "gugugaga:yam"
    ],
    "nutrition": 5,
    "saturation_modifier": 0.6
  }
]
ALTERNATIVES = [
  {
    "id": "bread_from_teff",
    "output": "minecraft:bread",
    "ingredients": [
      "gugugaga:teff",
      "gugugaga:teff",
      "gugugaga:teff"
    ],
    "count": 1
  },
  {
    "id": "bread_from_sorghum",
    "output": "minecraft:bread",
    "ingredients": [
      "gugugaga:sorghum",
      "gugugaga:sorghum",
      "gugugaga:sorghum"
    ],
    "count": 1
  },
  {
    "id": "bread_from_amaranth",
    "output": "minecraft:bread",
    "ingredients": [
      "gugugaga:amaranth",
      "gugugaga:amaranth",
      "gugugaga:amaranth"
    ],
    "count": 1
  },
  {
    "id": "oat_cookies",
    "output": "minecraft:cookie",
    "ingredients": [
      "gugugaga:oat",
      "minecraft:cocoa_beans",
      "minecraft:sugar"
    ],
    "count": 4
  },
  {
    "id": "pumpkin_pie_from_field_pumpkin",
    "output": "minecraft:pumpkin_pie",
    "ingredients": [
      "gugugaga:pumpkin",
      "minecraft:egg",
      "minecraft:sugar"
    ],
    "count": 1
  },
  {
    "id": "fruit_salad_with_pineapple",
    "output": "gugugaga:fruit_salad",
    "ingredients": [
      "gugugaga:pineapple",
      "gugugaga:grape",
      "minecraft:sugar"
    ],
    "count": 1
  },
  {
    "id": "vegetable_stew_with_mustard_greens",
    "output": "gugugaga:vegetable_stew",
    "ingredients": [
      "gugugaga:mustard_greens",
      "minecraft:carrot",
      "minecraft:potato"
    ],
    "count": 1
  },
  {
    "id": "nori_roll_with_cucumber",
    "output": "gugugaga:nori_rice_roll",
    "ingredients": [
      "gugugaga:nori_sheet",
      "gugugaga:rice",
      "gugugaga:cucumber"
    ],
    "count": 2
  },
  {
    "id": "honey_tea_with_mint",
    "output": "gugugaga:tea_cup",
    "ingredients": [
      "gugugaga:tea",
      "gugugaga:mint",
      "minecraft:honey_bottle"
    ],
    "count": 1
  },
  {
    "id": "mushroom_rice_bowl_with_kale",
    "output": "gugugaga:mushroom_rice_bowl",
    "ingredients": [
      "gugugaga:shiitake_mushroom",
      "gugugaga:rice",
      "gugugaga:kale"
    ],
    "count": 1
  }
]
# JSON-like list literals above are Python-compatible: only strings, lists, numbers and dicts.

def main():
    catalog = load("data/gugugaga/agronomy/crop_cuisine_catalog_v1_0.json")["items"]
    names = [i["id"] for i in catalog]
    assert len(names) == 68 and len(set(names)) == 68
    by_id = {x["id"]: x for x in catalog}
    en = load("assets/gugugaga/lang/en_us.json")
    zh = load("assets/gugugaga/lang/zh_cn.json")
    java = (JAVA / "AgricultureCuisineContent.java").read_text(encoding="utf-8")
    crops = (JAVA / "AgricultureContent.java").read_text(encoding="utf-8")
    marine = (JAVA / "MarineFungiContent.java").read_text(encoding="utf-8")
    known = set(re.findall(r'\b(?:food|material)\("([^"]+)"', crops + marine + java))
    known.update(re.findall(r'\b(?:ingredient|cultureItem|marineCropItem)\("([^"]+)"', marine))
    signatures = {}
    # Historic seed-crafting pairs may share inputs. Guard against introducing
    # new ambiguity without silently relaxing the checks on new cuisine recipes.
    new_shapeless = {x["id"] for x in DISHES if x["method"] == "craft"}
    new_shapeless.update(x["id"] for x in ALTERNATIVES)
    for path in (RES / "data/gugugaga/recipe").glob("*.json"):
        rec = json.loads(path.read_text(encoding="utf8"))
        if rec["type"] != "minecraft:crafting_shapeless":
            continue
        ingredients = rec.get("ingredients", [])
        signature = tuple(sorted((x.get("item") or "#" + x.get("tag", "")) for x in ingredients))
        signatures.setdefault(signature, []).append(path.stem)
    for variants in signatures.values():
        assert len(variants) == 1 or not new_shapeless.intersection(variants), f"new cuisine recipe ambiguity: {variants}"
    for item in DISHES:
        id = item["id"]
        assert by_id[id]["type"] == "food"
        assert by_id[id]["nutrition"] == item["nutrition"]
        assert by_id[id]["saturation_modifier"] == item["saturation_modifier"]
        assert f'food("{id}", {item["nutrition"]}, {item["saturation_modifier"]:.2f}F)' in java
        assert "AgricultureCuisineContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()))" in (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaCreativeTabs.java").read_text("utf8")
        assert en.get(f"item.gugugaga.{id}") and zh.get(f"item.gugugaga.{id}")
        model = load(f"assets/gugugaga/models/item/{id}.json")
        assert model["textures"]["layer0"] == f"gugugaga:item/crop_cuisine/{id}"
        r = load(f"data/gugugaga/recipe/{id}.json")
        assert r["result"] == {"id":f"gugugaga:{id}", "count":1}
        for ingredient in item["ingredients"]:
            assert ingredient.startswith(("minecraft:", "gugugaga:"))
            if ingredient.startswith("gugugaga:"):
                assert ingredient.split(":")[1] in known, f"unregistered ingredient {ingredient}"
        if item["method"] == "smelt":
            assert r["type"] == "minecraft:smelting"
            assert r["ingredient"]["item"] == item["ingredients"][0]
            assert r["cookingtime"] == 200
            for suffix, kind, ticks in [("smoking", "minecraft:smoking", 100),("campfire", "minecraft:campfire_cooking", 600)]:
                rr = load(f"data/gugugaga/recipe/{id}_{suffix}.json")
                assert rr["type"] == kind and rr["result"] == r["result"]
                assert rr["ingredient"] == r["ingredient"] and rr["cookingtime"] == ticks
        else:
            assert r["type"] == "minecraft:crafting_shapeless"
            assert [x["item"] for x in r["ingredients"]] == item["ingredients"]
    for alt in ALTERNATIVES:
        r = load(f'data/gugugaga/recipe/{alt["id"]}.json')
        assert r["type"] == "minecraft:crafting_shapeless"
        assert r["result"] == {"id":alt["output"],"count":alt["count"]}
        assert [x["item"] for x in r["ingredients"]] == alt["ingredients"]
    assert len(DISHES) == 17 and len(ALTERNATIVES) == 10
    print("CROP_CUISINE_V1_2_OK: 17 new dishes (2 furnace/smoker/campfire), 10 alternate recipes, 68 total cuisine items; recipe signatures unique")

if __name__ == "__main__":
    main()
