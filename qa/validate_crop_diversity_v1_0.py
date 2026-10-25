#!/usr/bin/env python3
"""Verify twelve additional source-informed plantable crop lines and reusable leaves."""
from pathlib import Path
import json
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"

def load(p):
    return json.loads((RES/p).read_text("utf8"))

def main():
    extra=load("data/gugugaga/agronomy/crop_additions_v1_0.json")
    old=load("data/gugugaga/agronomy/crop_additions_v0_9.json")
    baseline=load("data/gugugaga/agronomy/crop_profiles_v0_4.json")
    expect=set("parsnip arugula swiss_chard okra fennel dill cilantro parsley collard_greens green_bean chili_pepper brussels_sprouts".split())
    assert extra["schema_version"]=="1.0" and extra["count"]==12
    assert {x["crop"] for x in extra["crops"]}==expect
    assert len(baseline["crops"])+len(old["crops"])+len(extra["crops"])==56
    reg=(SRC/"AgricultureContent.java").read_text("utf8")
    climate=(SRC/"CropClimate.java").read_text("utf8")
    crop=(SRC/"AgricultureCropBlock.java").read_text("utf8")
    en=load("assets/gugugaga/lang/en_us.json")
    zh=load("assets/gugugaga/lang/zh_cn.json")
    for entry in extra["crops"]:
        name=entry["crop"]
        assert entry["source_urls"] and all(x.startswith("https://") for x in entry["source_urls"])
        assert f'crop("{name}_crop", "{name}_seeds", AgricultureCropBlock.PollinationGroup.{entry["pollination_gameplay"]})' in reg, name
        assert f'"{name}_seeds"' in climate, name
        assert f'{name.upper()}_SEEDS = seed(' in reg
        assert f'event.accept({name.upper()}_SEEDS.get())' in reg
        assert f'event.accept({name.upper()}.get())' in reg
        assert set(load(f"assets/gugugaga/blockstates/{name}_crop.json")["variants"])=={f"age={i}" for i in range(8)}
        pools=load(f"data/gugugaga/loot_table/blocks/{name}_crop.json")["pools"]
        assert len(pools)==2 and pools[0]["entries"][0]["name"]==f"gugugaga:{name}_seeds"
        assert pools[1]["entries"][0]["name"]==f"gugugaga:{name}"
        recipe=load(f"data/gugugaga/recipe/{name}_seeds.json")
        assert recipe["result"]["id"]==f"gugugaga:{name}_seeds"
        assert len(recipe["ingredients"])==2, name
        for item in [name,name+"_seeds"]:
            assert load(f"assets/gugugaga/models/item/{item}.json")["textures"]["layer0"].startswith("minecraft:item/")
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
        assert f"block.gugugaga.{name}_crop" in en and f"block.gugugaga.{name}_crop" in zh
    for herb in "arugula swiss_chard parsley collard_greens dill cilantro".split():
        assert f'seedName.equals("{herb}_seeds")' in crop
    print("CROP_DIVERSITY_V1_0_OK: 56 plantable crops; 12 source-linked new crop lines, 6 new multi-harvest leaves")

if __name__=="__main__":
    main()
