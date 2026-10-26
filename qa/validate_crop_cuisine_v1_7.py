#!/usr/bin/env python3
"""Validate v1.7 crop-cuisine variants, registered ingredients and recipe uniqueness."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
RECIPES = RES / "data/gugugaga/recipe"
ALLOWED_GROUPS = {"grains", "vegetables", "vanilla_and_honey", "leafy_greens", "root_crops", "legumes", "fruit", "marine", "fungal", "floral_honey", "seeds"}

def load(path):
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    manifest = load(RES / "data/gugugaga/agronomy/cuisine_recipe_variants_v1_7.json")
    assert manifest["schema_version"] == "1.7"
    rows = manifest["alternates"]
    assert len(rows) == 24 and len({row["id"] for row in rows}) == 24

    java_dir = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
    java = "\n".join((java_dir / name).read_text(encoding="utf-8") for name in
        ("AgricultureContent.java", "AgricultureCuisineContent.java", "MarineFungiContent.java", "BeeContent.java"))
    known = set(re.findall(r'\b(?:food|material|ingredient|culture|marineCrop|floralHoney)\("([^"]+)"', java))
    known.update(["kelp_flakes", "mushroom_spores"])
    crop_java = (java_dir / "AgricultureContent.java").read_text(encoding="utf-8")
    crop_foods = set(re.findall(r'= food\("([^"]+)",\s*\d+,\s*[0-9.]+F?\);', crop_java))

    def ensure_known(identifier):
        if identifier.startswith("gugugaga:"):
            assert identifier.split(":", 1)[1] in known, f"unregistered custom item: {identifier}"
        else:
            assert identifier.startswith("minecraft:"), identifier

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

    for row in rows:
        recipe_id = row["id"]
        assert row["group"] in ALLOWED_GROUPS, recipe_id
        ingredients = row["ingredients"]
        assert len(ingredients) == 3 and len(set(ingredients)) == 3, recipe_id
        assert any(item.startswith("gugugaga:") and item.split(":", 1)[1] in crop_foods for item in ingredients), f"recipe lacks an agriculture crop food: {recipe_id}"
        for item in [*ingredients, row["output"]]:
            ensure_known(item)
        recipe = load(RECIPES / f"{recipe_id}.json")
        assert recipe["type"] == "minecraft:crafting_shapeless", recipe_id
        assert recipe["category"] == "misc", recipe_id
        assert recipe["result"] == {"id": row["output"], "count": row["count"]}, recipe_id
        assert [ingredient.get("item") for ingredient in recipe["ingredients"]] == ingredients, recipe_id
        for other_id, existing in previous:
            if other_id == recipe_id:
                continue
            assert not overlap(existing, ingredients), f"recipe {recipe_id} duplicates the ingredient pattern of {other_id}"

    print(f"CROP_CUISINE_V1_7_OK: {len(rows)} crop-specific recipe variants; all ingredients registered and no shapeless recipe collisions.")

if __name__ == "__main__":
    main()
