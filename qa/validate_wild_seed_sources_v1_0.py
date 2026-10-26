#!/usr/bin/env python3
"""Release gate for unique seed recipes and biome-specific wild seed sources."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"
AGRI = SRC / "agriculture"
PROFILE_FILES = [
    "crop_profiles_v0_4.json", "crop_additions_v0_9.json",
    "crop_additions_v1_0.json", "crop_additions_v1_3.json",
    "crop_additions_v1_7.json", "crop_additions_v1_8.json",
    "crop_additions_v1_9.json", "crop_additions_v2_0.json",
    "crop_additions_v2_1.json",
]
LEGACY_BIOMES = {
  "barley": [
    "minecraft:plains",
    "minecraft:meadow",
    "minecraft:windswept_hills"
  ],
  "blueberry": [
    "minecraft:taiga",
    "minecraft:old_growth_pine_taiga",
    "minecraft:forest"
  ],
  "flax": [
    "minecraft:plains",
    "minecraft:meadow",
    "minecraft:forest"
  ],
  "lettuce": [
    "minecraft:forest",
    "minecraft:meadow",
    "minecraft:river"
  ],
  "millet": [
    "minecraft:plains",
    "minecraft:savanna",
    "minecraft:savanna_plateau"
  ],
  "oat": [
    "minecraft:plains",
    "minecraft:meadow",
    "minecraft:taiga"
  ],
  "pea": [
    "minecraft:forest",
    "minecraft:birch_forest",
    "minecraft:meadow"
  ],
  "radish": [
    "minecraft:meadow",
    "minecraft:plains",
    "minecraft:forest"
  ],
  "raspberry": [
    "minecraft:forest",
    "minecraft:birch_forest",
    "minecraft:taiga"
  ],
  "sesame": [
    "minecraft:savanna",
    "minecraft:savanna_plateau",
    "minecraft:plains"
  ],
  "strawberry": [
    "minecraft:forest",
    "minecraft:birch_forest",
    "minecraft:meadow"
  ],
  "sunflower_seed": [
    "minecraft:sunflower_plains",
    "minecraft:plains",
    "minecraft:savanna"
  ]
}
GROUP_BIOMES = {
  "COOL": [
    "minecraft:taiga",
    "minecraft:old_growth_pine_taiga",
    "minecraft:old_growth_spruce_taiga",
    "minecraft:meadow",
    "minecraft:windswept_hills",
    "minecraft:windswept_forest",
    "minecraft:grove"
  ],
  "TEMPERATE": [
    "minecraft:plains",
    "minecraft:forest",
    "minecraft:birch_forest",
    "minecraft:old_growth_birch_forest",
    "minecraft:flower_forest",
    "minecraft:sunflower_plains"
  ],
  "WARM": [
    "minecraft:savanna",
    "minecraft:savanna_plateau",
    "minecraft:jungle",
    "minecraft:sparse_jungle",
    "minecraft:bamboo_jungle"
  ],
  "DRY": [
    "minecraft:desert",
    "minecraft:windswept_savanna"
  ],
  "WET": [
    "minecraft:swamp",
    "minecraft:mangrove_swamp",
    "minecraft:river",
    "minecraft:beach",
    "minecraft:stony_shore"
  ]
}
SUPPORTS = [
  "minecraft:grass_block",
  "minecraft:dirt",
  "minecraft:podzol",
  "minecraft:coarse_dirt",
  "minecraft:moss_block",
  "minecraft:rooted_dirt",
  "minecraft:mud",
  "minecraft:sand",
  "minecraft:red_sand",
  "minecraft:gravel",
  "minecraft:clay"
]
BLOCK_CONSTANTS = ["GRASS_BLOCK", "DIRT", "PODZOL", "COARSE_DIRT", "MOSS_BLOCK",
                   "ROOTED_DIRT", "MUD", "SAND", "RED_SAND", "GRAVEL", "CLAY"]

def read(path):
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    profiles = []
    for filename in PROFILE_FILES:
        profiles.extend(read(RES / "data/gugugaga/agronomy" / filename)["crops"])
    crop_ids = [row["crop"] for row in profiles]
    assert len(crop_ids) == 100 and len(set(crop_ids)) == 100, "expected 100 unique crop profiles"
    profile_by_id = {row["crop"]: row for row in profiles}
    bands = {crop: row["growth_band_gameplay"] for crop, row in profile_by_id.items()}
    assert set(bands.values()) == set(GROUP_BIOMES), "unhandled growth band"

    agriculture_java = (AGRI / "AgricultureContent.java").read_text(encoding="utf-8")
    wild_java = (AGRI / "WildSeedPlantBlock.java").read_text(encoding="utf-8")
    wild_registered = set(re.findall(r'BLOCKS\.register\("wild_([a-z0-9_]+)"', agriculture_java))
    assert wild_registered == set(crop_ids), f"wild block registration mismatch; missing={sorted(set(crop_ids)-wild_registered)}, extra={sorted(wild_registered-set(crop_ids))}"
    for constant in BLOCK_CONSTANTS:
        assert f"Blocks.{constant}" in wild_java, f"wild plants do not support {constant}"

    en = read(RES / "assets/gugugaga/lang/en_us.json")
    zh = read(RES / "assets/gugugaga/lang/zh_cn.json")
    signatures = {}
    for crop in crop_ids:
        seed = crop + "_seeds"
        wild = "wild_" + crop
        blockstate = read(RES / f"assets/gugugaga/blockstates/{wild}.json")
        assert blockstate.get("variants", {}).get("", {}).get("model") == f"gugugaga:block/{wild}", f"{wild}: blockstate/model mismatch"
        model = read(RES / f"assets/gugugaga/models/block/{wild}.json")
        assert model.get("parent") == "minecraft:block/cross", f"{wild}: expected cross plant model"
        assert model.get("textures", {}).get("cross") == f"gugugaga:block/crops/{crop}_stage4", f"{wild}: crop-specific texture missing"
        loot = read(RES / f"data/gugugaga/loot_table/blocks/{wild}.json")
        loot_items = {
            entry.get("name")
            for pool in loot.get("pools", [])
            for entry in pool.get("entries", [])
            if entry.get("type") == "minecraft:item"
        }
        assert f"gugugaga:{seed}" in loot_items, f"{wild}: does not drop its own seed"
        assert f"block.gugugaga.{wild}" in en and f"block.gugugaga.{wild}" in zh, f"{wild}: bilingual name missing"

        recipe = read(RES / f"data/gugugaga/recipe/{seed}.json")
        assert recipe.get("type") == "minecraft:crafting_shapeless", f"{seed}: expected a shapeless propagation recipe"
        ingredients = recipe.get("ingredients", [])
        assert len(ingredients) == 2 and all("item" in item for item in ingredients), f"{seed}: expected two concrete ingredients"
        signature = tuple(sorted(item["item"] for item in ingredients))
        assert signature == tuple(sorted([f"gugugaga:{crop}", "minecraft:wheat_seeds"])), f"{seed}: wrong or ambiguous source ingredients"
        assert signature not in signatures, f"{seed}: duplicate recipe inputs collide with {signatures.get(signature)}"
        signatures[signature] = seed
        assert recipe.get("result", {}).get("id") == f"gugugaga:{seed}" and recipe["result"].get("count") == 2, f"{seed}: wrong recipe output"

    # Existing 12 individual features remain unchanged, preserving their established habitat mapping.
    for crop, biomes in LEGACY_BIOMES.items():
        configured = read(RES / f"data/gugugaga/worldgen/configured_feature/wild_{crop}.json")
        to_place = configured["config"]["feature"]["feature"]["config"]["to_place"]
        assert to_place.get("state", {}).get("Name") == f"gugugaga:wild_{crop}", f"wild_{crop}: configured block mismatch"
        placed = read(RES / f"data/gugugaga/worldgen/placed_feature/wild_{crop}.json")
        assert placed.get("feature") == f"gugugaga:wild_{crop}", f"wild_{crop}: placed feature mismatch"
        modifier = read(RES / f"data/gugugaga/neoforge/biome_modifier/wild_{crop}.json")
        assert modifier.get("features") == f"gugugaga:wild_{crop}" and modifier.get("biomes") == biomes, f"wild_{crop}: biome mapping mismatch"

    # The remaining 88 sources share five weighted features to avoid a per-crop feature explosion.
    group_sources = set()
    for band, biomes in GROUP_BIOMES.items():
        feature_id = f"wild_{band.lower()}_crops"
        expected = {crop for crop in crop_ids if bands[crop] == band and crop not in LEGACY_BIOMES}
        configured = read(RES / f"data/gugugaga/worldgen/configured_feature/{feature_id}.json")
        assert configured.get("type") == "minecraft:random_patch", f"{feature_id}: wrong feature type"
        assert configured["config"].get("tries", 0) >= 32, f"{feature_id}: insufficient patch tries"
        provider = configured["config"]["feature"]["feature"]["config"]["to_place"]
        assert provider.get("type") == "minecraft:weighted_state_provider", f"{feature_id}: weighted source pool missing"
        actual = [entry["data"]["Name"].removeprefix("gugugaga:wild_") for entry in provider.get("entries", [])]
        assert len(actual) == len(set(actual)) and set(actual) == expected, f"{feature_id}: source pool mismatch"
        group_sources.update(actual)
        predicate = configured["config"]["feature"]["placement"][0]["predicate"]["predicates"][1]
        assert set(predicate.get("blocks", [])) == set(SUPPORTS), f"{feature_id}: ground support filter mismatch"
        placed = read(RES / f"data/gugugaga/worldgen/placed_feature/{feature_id}.json")
        assert placed.get("feature") == f"gugugaga:{feature_id}", f"{feature_id}: placed feature mismatch"
        assert any(p.get("type") == "minecraft:rarity_filter" and p.get("chance") == 5 for p in placed.get("placement", [])), f"{feature_id}: rarity setting mismatch"
        modifier = read(RES / f"data/gugugaga/neoforge/biome_modifier/{feature_id}.json")
        assert modifier.get("features") == f"gugugaga:{feature_id}" and modifier.get("biomes") == biomes, f"{feature_id}: biome mapping mismatch"
    expected_group_sources = set(crop_ids) - set(LEGACY_BIOMES)
    assert group_sources == expected_group_sources, "not every non-legacy crop is included in a wild source pool"
    assert set(LEGACY_BIOMES) | group_sources == set(crop_ids), "wild sources do not cover all 100 crops"

    manifest = read(RES / "data/gugugaga/agronomy/wild_seed_sources_v1_0.json")
    manifest_rows = {row["crop"]: row for row in manifest["wild_sources"]}
    assert set(manifest_rows) == set(crop_ids), "habitat manifest must list all 100 crops"
    for crop in crop_ids:
        row = manifest_rows[crop]
        assert row["seed_item"] == f"gugugaga:{crop}_seeds" and row["wild_block"] == f"gugugaga:wild_{crop}", f"{crop}: manifest ID mismatch"
        assert row["growth_band"] == bands[crop], f"{crop}: manifest growth-band mismatch"
    print("WILD_SEED_SOURCES_V1_0_OK: 100/100 wild seed sources, unique propagation recipes, 12 legacy biome features, 5 shared weighted worldgen features.")

if __name__ == "__main__":
    main()
