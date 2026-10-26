#!/usr/bin/env python3
"""Check v1.4 cuisine substitutions for real item IDs, recipe collisions and integration."""
import json
import re
from collections import Counter
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
RECIPES=RES/"data/gugugaga/recipe"
def load(path):
    return json.loads(path.read_text(encoding="utf-8"))
def main():
    manifest=load(RES/"data/gugugaga/agronomy/cuisine_recipe_variants_v1_4.json")
    assert manifest["schema_version"]=="1.4"
    rows=manifest["alternates"]
    assert len(rows)==32
    assert len({row["id"] for row in rows})==32
    java_dir=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
    java="\n".join((java_dir/name).read_text(encoding="utf-8") for name in
        ("AgricultureContent.java","AgricultureCuisineContent.java","MarineFungiContent.java","BeeContent.java"))
    known=set(re.findall(r'\b(?:food|material|ingredient|culture|marineCrop|floralHoney)\("([^"]+)"',java))
    known.update(["kelp_flakes","mushroom_spores"])
    def ensure_known(identifier):
        if identifier.startswith("gugugaga:"):
            assert identifier.split(":",1)[1] in known, f"unregistered game item: {identifier}"
        else:
            assert identifier.startswith("minecraft:"),identifier

    # Existing tag recipes may already cover a concrete set of ingredients.
    # Exact and item-tag-overlap checks prevent indistinguishable new outputs.
    tags={}
    for p in (RES/"data/gugugaga/tags/item").rglob("*.json"):
        rel=p.relative_to(RES/"data/gugugaga/tags/item").with_suffix("")
        tags[f"gugugaga:{str(rel).replace(chr(92),'/')}"]=set(load(p).get("values",[]))
    def matches(ingredient,item):
        if "item" in ingredient:
            return ingredient["item"]==item
        tag=ingredient.get("tag")
        return tag in tags and item in tags[tag]
    def overlap(existing,chosen):
        if len(existing)!=len(chosen):
            return False
        if not all("item" in e or "tag" in e for e in existing):
            return False
        def visit(i,available):
            if i==len(existing):
                return True
            for k,item in enumerate(available):
                if matches(existing[i],item) and visit(i+1,available[:k]+available[k+1:]):
                    return True
            return False
        return visit(0,list(chosen))

    previous=[]
    for path in RECIPES.glob("*.json"):
        r=load(path)
        if r.get("type")=="minecraft:crafting_shapeless":
            previous.append((path.stem,r["ingredients"]))
    new_ids={r["id"] for r in rows}
    for row in rows:
        rid=row["id"]
        assert row["group"] in {"grains","vegetables","vanilla_and_honey","leafy_greens","root_crops","legumes","fruit","marine","fungal","floral_honey","seeds"},rid
        ingredients=row["ingredients"]
        assert len(ingredients)==3,rid
        assert all(isinstance(item,str) for item in ingredients),rid
        for item in [*ingredients,row["output"]]:
            ensure_known(item)
        r=load(RECIPES/f"{rid}.json")
        assert r["type"]=="minecraft:crafting_shapeless",rid
        assert r["category"]=="misc",rid
        assert r["result"]=={"id":row["output"],"count":row["count"]},rid
        assert [x.get("item") for x in r["ingredients"]]==ingredients,rid
        for other_id,existing in previous:
            if other_id==rid:
                continue
            assert not overlap(existing,ingredients),f"recipe {rid} overlaps {other_id}"
    print(f"CROP_CUISINE_V1_4_OK: {len(rows)} new crop/vanilla/honey/marine/fungal substitutions; no collisions or unknown custom items")
if __name__=="__main__":
    main()
