#!/usr/bin/env python3
"""Audit the crop-cuisine v1.3 recipe manifest and 1.21.1 recipe-book category rules."""
import json
from pathlib import Path

RES=Path(__file__).resolve().parents[1]/"src/main/resources"
def read(path):
    return json.loads((RES/path).read_text(encoding="utf-8"))

def main():
    manifest=read("data/gugugaga/agronomy/cuisine_recipe_variants_v1_3.json")
    assert manifest["schema_version"]=="1.3"
    recipes=RES/"data/gugugaga/recipe"
    assert len(manifest["alternates"])==20
    names=set()
    signatures=set()
    for row in manifest["alternates"]:
        rid=row["id"]
        assert rid not in names, rid
        names.add(rid)
        r=read(f"data/gugugaga/recipe/{rid}.json")
        assert r["type"]=="minecraft:crafting_shapeless"
        assert r["category"]=="misc"
        assert r["result"]=={"id":row["output"],"count":row["count"]}
        assert [i.get("item") for i in r["ingredients"]]==row["ingredients"]
        signature=tuple(sorted(row["ingredients"]))
        assert signature not in signatures, rid
        signatures.add(signature)
    count=0
    for path in recipes.glob("*.json"):
        r=json.loads(path.read_text(encoding="utf-8"))
        kind=r["type"]
        category=r.get("category","misc")
        if kind in {"minecraft:crafting_shapeless","minecraft:crafting_shaped"}:
            assert category in {"building","redstone","equipment","misc"},(path.name,category)
        elif kind in {"minecraft:smelting","minecraft:smoking","minecraft:campfire_cooking","minecraft:blasting"}:
            assert category in {"food","blocks","misc"},(path.name,category)
        count+=1
    print(f"CROP_CUISINE_V1_3_OK: {len(names)} additional food substitutions; {count} recipe categories valid")

if __name__=="__main__":
    main()
