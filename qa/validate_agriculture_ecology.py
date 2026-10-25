#!/usr/bin/env python3
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]/"src/main/resources/data/gugugaga"
WILD="wild_barley wild_oat wild_pea wild_lettuce wild_flax wild_sunflower_seed wild_blueberry wild_strawberry".split()
def check(p): return json.loads((ROOT/p).read_text("utf8"))
for name in WILD:
    feature=check(f"worldgen/configured_feature/{name}.json")
    placed=check(f"worldgen/placed_feature/{name}.json")
    modifier=check(f"neoforge/biome_modifier/{name}.json")
    loot=check(f"loot_table/blocks/{name}.json")
    assert feature["type"]=="minecraft:random_patch"
    assert feature["config"]["feature"]["feature"]["config"]["to_place"]["state"]["Name"]=="gugugaga:"+name
    assert placed["feature"]=="gugugaga:"+name
    assert modifier["type"]=="neoforge:add_features" and modifier["step"]=="vegetal_decoration"
    assert len(modifier["biomes"])>=2
    assert loot["pools"]
assert check("neoforge/biome_modifier/bee_forage_habitats.json")["spawners"][0]["type"]=="minecraft:bee"
print("AGRICULTURE_BIOME_GENERATION_ASSETS_OK:",len(WILD),"wild plants + bees")
