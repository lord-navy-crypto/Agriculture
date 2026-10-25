#!/usr/bin/env python3
"""Fast structural validation for agriculture content before Minecraft is launched."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "src/main/resources"
CROPS = {
    "corn": ("wheat", "wheat_seeds", "wheat", "minecraft:wheat_seeds", "minecraft:hay_block"),
    "tomato": ("carrots", "pumpkin_seeds", "sweet_berries", "minecraft:pumpkin_seeds", "minecraft:beetroot"),
    "onion": ("beetroots", "beetroot_seeds", "carrot", "minecraft:beetroot_seeds", "minecraft:carrot"),
    "rice": ("wheat", "wheat_seeds", "wheat", "minecraft:wheat_seeds", "minecraft:kelp"),
    "soybean": ("carrots", "beetroot_seeds", "cocoa_beans", "minecraft:wheat_seeds", "minecraft:cocoa_beans"),
    "strawberry": ("sweet_berry_bush", "beetroot_seeds", "sweet_berries", "minecraft:beetroot_seeds", "minecraft:sweet_berries"),
    "cucumber": ("potatoes", "pumpkin_seeds", "melon_slice", "minecraft:pumpkin_seeds", "minecraft:melon_slice"),
    "blueberry": ("sweet_berry_bush", "beetroot_seeds", "sweet_berries", "minecraft:blue_dye", "minecraft:sweet_berries"),
    "bell_pepper": ("carrots", "pumpkin_seeds", "carrot", "minecraft:pumpkin_seeds", "minecraft:carrot"),
    "garlic": ("beetroots", "beetroot_seeds", "beetroot", "minecraft:beetroot_seeds", "minecraft:potato"),
}
BEE_ITEMS = ("meadow_bee_tag", "orchard_bee_tag", "berry_bee_tag", "large_apiary_kit", "honey_harvester")
POLLINATED_CROPS = {"tomato", "strawberry", "cucumber", "blueberry", "bell_pepper", "eggplant", "sunflower_seed", "buckwheat", "zucchini", "raspberry", "cranberry", "sesame"}

def read_json(path):
    with path.open(encoding="utf-8") as stream:
        return json.load(stream)

def main():
    checked = 0
    for path in ROOT.rglob("*.json"):
        read_json(path)
        checked += 1

    en = read_json(ROOT / "assets/gugugaga/lang/en_us.json")
    zh = read_json(ROOT / "assets/gugugaga/lang/zh_cn.json")
    for crop, (block_texture, seed_texture, produce_texture, seed_input_a, seed_input_b) in CROPS.items():
        blockstate = read_json(ROOT / f"assets/gugugaga/blockstates/{crop}_crop.json")
        expected_states = ({f"age={age},pollinated={flag}" for age in range(8) for flag in ("false", "true")}
                           if crop in POLLINATED_CROPS else {f"age={age}" for age in range(8)})
        assert set(blockstate["variants"]) == expected_states, f"{crop}: age/pollination variant mismatch"
        loot = read_json(ROOT / f"data/gugugaga/loot_table/blocks/{crop}_crop.json")
        assert len(loot["pools"]) == (3 if crop in POLLINATED_CROPS else 2), f"{crop}: seed, produce and optional pollination loot pools"
        model = read_json(ROOT / f"assets/gugugaga/models/item/{crop}_seeds.json")
        assert model["textures"]["layer0"].startswith("minecraft:item/")
        assert f"item.gugugaga.{crop}_seeds" in en and f"item.gugugaga.{crop}_seeds" in zh
        model = read_json(ROOT / f"assets/gugugaga/models/item/{crop}.json")
        assert model["textures"]["layer0"].startswith("minecraft:item/")
        assert f"item.gugugaga.{crop}" in en and f"item.gugugaga.{crop}" in zh
        assert f"block.gugugaga.{crop}_crop" in en and f"block.gugugaga.{crop}_crop" in zh
        read_json(ROOT / f"data/gugugaga/recipe/{crop}_seeds.json")

    for item in BEE_ITEMS:
        read_json(ROOT / f"assets/gugugaga/models/item/{item}.json")
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
        read_json(ROOT / f"data/gugugaga/recipe/{item}.json")

    print(f"AGRICULTURE_ASSETS_PASS: {checked} JSON files parsed; {len(CROPS)} crops and {len(BEE_ITEMS)} bee/apiary items validated.")

if __name__ == "__main__":
    main()
