#!/usr/bin/env python3
"""Check all eight v0.3 crop resource families, two wild plants and bee biome spawns."""
from pathlib import Path
import json

ROOT = Path(__file__).resolve().parents[1] / "src/main/resources"
CROPS = "radish turnip parsnip kale swiss_chard buckwheat millet sesame".split()
WILD = "wild_radish wild_millet".split()
BEES = "mining_bee sweat_bee".split()

def read(rel):
    return json.loads((ROOT / rel).read_text(encoding="utf-8"))

def main():
    en = read("assets/gugugaga/lang/en_us.json")
    zh = read("assets/gugugaga/lang/zh_cn.json")
    for crop in CROPS:
        states = read(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        assert set(states) == {f"age={n}" for n in range(8)}, crop
        loot = read(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")
        assert len(loot["pools"]) == 2, crop
        assert loot["pools"][1]["entries"][0]["conditions"][0]["properties"]["age"] == "7", crop
        recipe = read(f"data/gugugaga/recipe/{crop}_seeds.json")
        assert recipe["result"]["id"] == f"gugugaga:{crop}_seeds"
        for suffix in ("", "_seeds"):
            key = f"item.gugugaga.{crop}{suffix}"
            assert key in en and key in zh, key
            read(f"assets/gugugaga/models/item/{crop}{suffix}.json")
        assert f"block.gugugaga.{crop}_crop" in en
    for plant in WILD:
        cfg = read(f"data/gugugaga/worldgen/configured_feature/{plant}.json")
        assert cfg["config"]["feature"]["feature"]["config"]["to_place"]["state"]["Name"] == f"gugugaga:{plant}"
        assert read(f"data/gugugaga/worldgen/placed_feature/{plant}.json")["feature"] == f"gugugaga:{plant}"
        modifier = read(f"data/gugugaga/neoforge/biome_modifier/{plant}.json")
        assert modifier["type"] == "neoforge:add_features" and modifier["step"] == "vegetal_decoration"
        assert len(modifier["biomes"]) >= 3
        read(f"assets/gugugaga/models/block/{plant}.json")
        read(f"data/gugugaga/loot_table/blocks/{plant}.json")
    for bee in BEES:
        mod = read(f"data/gugugaga/neoforge/biome_modifier/spawn_{bee}.json")
        assert mod["type"] == "neoforge:add_spawns"
        assert mod["spawners"][0]["type"] == f"gugugaga:{bee}"
        assert mod["spawners"][0]["weight"] <= 3
        assert f"entity.gugugaga.{bee}" in en and f"entity.gugugaga.{bee}" in zh
    print("AGRICULTURE_V03_ASSETS_OK: eight crops, two wild patches, two bee types")

if __name__ == "__main__":
    main()
