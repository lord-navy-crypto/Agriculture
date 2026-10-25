#!/usr/bin/env python3
"""v1.3 new-crop seed, growth, loot, assets and creative tab contract."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
def load(p): return json.loads((RES/p).read_text("utf8"))
def main():
    sources=load("data/gugugaga/agronomy/crop_additions_v1_3.json")
    baselines=[load("data/gugugaga/agronomy/crop_profiles_v0_4.json")["crops"],
               load("data/gugugaga/agronomy/crop_additions_v0_9.json")["crops"],
               load("data/gugugaga/agronomy/crop_additions_v1_0.json")["crops"],sources["crops"]]
    assert len({x["crop"] for part in baselines for x in part})==64
    assert len(sources["crops"])==8
    all_ids={row["crop"] for group in baselines for row in group}
    reg=(SRC/"AgricultureContent.java").read_text("utf8")
    climate=(SRC/"CropClimate.java").read_text("utf8")
    block=(SRC/"AgricultureCropBlock.java").read_text("utf8")
    en=load("assets/gugugaga/lang/en_us.json");zh=load("assets/gugugaga/lang/zh_cn.json")
    for row in sources["crops"]:
        c=row["crop"]
        assert row["source_urls"] and all(u.startswith("https://") for u in row["source_urls"]),c
        assert f'crop("{c}_crop", "{c}_seeds", AgricultureCropBlock.PollinationGroup.NONE)' in reg,c
        assert f'"{c}_seeds"' in climate,c
        assert f'event.accept({c.upper()}_SEEDS.get())' in reg,c
        assert f'event.accept({c.upper()}.get())' in reg,c
        states=load(f"assets/gugugaga/blockstates/{c}_crop.json")["variants"]
        assert set(states)=={f"age={i},pollinated={p}" for i in range(8) for p in ("false","true")},c
        loot=load(f"data/gugugaga/loot_table/blocks/{c}_crop.json")["pools"]
        assert len(loot)==2 and loot[0]["entries"][0]["name"]==f"gugugaga:{c}_seeds"
        assert loot[1]["entries"][0]["name"]==f"gugugaga:{c}"
        assert load(f"data/gugugaga/recipe/{c}_seeds.json")["result"]["id"]==f"gugugaga:{c}_seeds"
        for item in (c,c+"_seeds"):
            assert load(f"assets/gugugaga/models/item/{item}.json")["textures"]["layer0"].startswith("minecraft:item/")
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
    for crop_id in all_ids:
        variants=load(f"assets/gugugaga/blockstates/{crop_id}_crop.json")["variants"]
        assert set(variants)=={f"age={i},pollinated={p}" for i in range(8) for p in ("false","true")},crop_id
        for age in range(8):
            assert variants[f"age={age},pollinated=false"]==variants[f"age={age},pollinated=true"],crop_id
    assert 'seedName.equals("bok_choy_seeds")' in block
    print("CROP_V1_3_OK: eight new complete crop lines; all 64 have 16 visual states")
if __name__=="__main__": main()
