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
        assert "AgricultureContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()))" in (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaCreativeTabs.java").read_text("utf8")
        assert "AgricultureContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()))" in (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaCreativeTabs.java").read_text("utf8")
        states=load(f"assets/gugugaga/blockstates/{c}_crop.json")["variants"]
        assert set(states)=={f"age={i},pollinated={p}" for i in range(8) for p in ("false","true")},c
        loot=load(f"data/gugugaga/loot_table/blocks/{c}_crop.json")["pools"]
        assert len(loot)==2 and loot[0]["entries"][0]["name"]==f"gugugaga:{c}_seeds"
        assert loot[1]["entries"][0]["name"]==f"gugugaga:{c}"
        assert load(f"data/gugugaga/recipe/{c}_seeds.json")["result"]["id"]==f"gugugaga:{c}_seeds"
        for item in (c,c+"_seeds"):
            assert load(f"assets/gugugaga/models/item/{item}.json")["textures"]["layer0"] == f"gugugaga:item/crops/{item}"
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
    for crop_id in all_ids:
        variants=load(f"assets/gugugaga/blockstates/{crop_id}_crop.json")["variants"]
        assert set(variants)=={f"age={i},pollinated={p}" for i in range(8) for p in ("false","true")},crop_id
        for age in range(8):
            base = f"gugugaga:block/crops/{crop_id}_stage{age}"
            assert variants[f"age={age},pollinated=false"]["model"] == base, crop_id
            expected = base + ("_pollinated" if age >= 4 else "")
            assert variants[f"age={age},pollinated=true"]["model"] == expected, crop_id
    assert '"bok_choy_seeds"' in block
    fruit_names = "tomato cucumber strawberry blueberry bell_pepper eggplant zucchini raspberry cranberry okra chili_pepper green_bean pea broccoli brussels_sprouts cotton".split()
    for fruit in fruit_names:
        assert f'"{fruit}_seeds"' in block, fruit
    assert "int harvestCount = pollinationGroup != PollinationGroup.NONE" in block
    assert "new ItemStack(leaves, harvestCount)" in block
    assert "state.setValue(AGE, 4).setValue(POLLINATED, false)" in block
    print("CROP_V1_3_OK: all 64 crops use distinct stage models and pollen-marked reproductive variants")
if __name__=="__main__": main()
