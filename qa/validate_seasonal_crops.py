#!/usr/bin/env python3
"""v0.3 seasonal crops & biome generation structural validation."""
import json
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]/"src/main/resources"
CROPS="radish turnip millet buckwheat lentil chickpea".split()
WILD="radish turnip".split()
def read(p): return json.loads((ROOT/p).read_text(encoding="utf-8"))

def main():
    en=read("assets/gugugaga/lang/en_us.json")
    zh=read("assets/gugugaga/lang/zh_cn.json")
    for crop in CROPS:
        variants=read(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        assert set(variants)=={f"age={a}" for a in range(8)}, crop
        loot=read(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")
        assert len(loot["pools"])==2 and loot["pools"][1]["entries"][0]["conditions"][0]["properties"]["age"]=="7"
        recipe=read(f"data/gugugaga/recipe/{crop}_seeds.json")
        assert recipe["result"]["id"]==f"gugugaga:{crop}_seeds"
        for suffix in ("","_seeds"):
            read(f"assets/gugugaga/models/item/{crop}{suffix}.json")
            assert f"item.gugugaga.{crop}{suffix}" in en
            assert f"item.gugugaga.{crop}{suffix}" in zh
        assert f"block.gugugaga.{crop}_crop" in en
    for crop in WILD:
        read(f"assets/gugugaga/blockstates/wild_{crop}.json")
        read(f"assets/gugugaga/models/block/wild_{crop}.json")
        configured=read(f"data/gugugaga/worldgen/configured_feature/wild_{crop}.json")
        assert configured["type"]=="minecraft:random_patch"
        placed=read(f"data/gugugaga/worldgen/placed_feature/wild_{crop}.json")
        assert placed["feature"]==f"gugugaga:wild_{crop}"
        modifier=read(f"data/gugugaga/neoforge/biome_modifier/wild_{crop}.json")
        assert modifier["type"]=="neoforge:add_features"
        assert modifier["step"]=="vegetal_decoration"
        assert len(modifier["biomes"])>=3
    print("SEASONAL_CROPS_V03_OK: 6 crops, 2 wild plants, biome modifiers and bilingual resources")
if __name__=="__main__": main()
