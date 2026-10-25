#!/usr/bin/env python3
"""Contract checks for the eight v1.8 crop lines and the 80-crop asset pipeline."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
EXPECTED = {
    "cassava": ("WARM", "NONE"),
    "taro": ("WET", "NONE"),
    "yam": ("WARM", "NONE"),
    "teff": ("DRY", "NONE"),
    "watercress": ("WET", "NONE"),
    "endive": ("COOL", "NONE"),
    "kohlrabi": ("COOL", "NONE"),
    "rutabaga": ("COOL", "NONE"),
}

def read(path):
    return json.loads((RES / path).read_text(encoding="utf-8"))

def main():
    profile = read("data/gugugaga/agronomy/crop_additions_v1_8.json")
    assert profile["schema_version"] == "1.8"
    assert {row["crop"] for row in profile["crops"]} == set(EXPECTED)
    en = read("assets/gugugaga/lang/en_us.json")
    zh = read("assets/gugugaga/lang/zh_cn.json")
    registry = (SRC / "AgricultureContent.java").read_text(encoding="utf-8")
    climate = (SRC / "CropClimate.java").read_text(encoding="utf-8")
    crop_java = (SRC / "AgricultureCropBlock.java").read_text(encoding="utf-8")
    generator = (ROOT / "tools/crop-textures.gradle").read_text(encoding="utf-8")
    compost = read("data/neoforge/data_maps/item/compostables.json")["values"]
    for row in profile["crops"]:
        crop = row["crop"]
        assert (row["growth_band_gameplay"], row["pollination_gameplay"]) == EXPECTED[crop]
        assert row["botanical_note"].strip()
        assert row["source_urls"] and all(url.startswith("https://") for url in row["source_urls"])
        assert f'crop("{crop}_crop", "{crop}_seeds", AgricultureCropBlock.PollinationGroup.NONE)' in registry
        assert f'"{crop}_seeds"' in climate
        assert f'event.accept({crop.upper()}_SEEDS.get())' in registry
        assert f'event.accept({crop.upper()}.get())' in registry
        for item in (crop, crop + "_seeds"):
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
            model = read(f"assets/gugugaga/models/item/{item}.json")
            assert model["parent"] == "minecraft:item/generated"
            assert model["textures"]["layer0"] == f"gugugaga:item/crops/{item}"
            assert f"gugugaga:{item}" in compost
        assert f"block.gugugaga.{crop}_crop" in en and f"block.gugugaga.{crop}_crop" in zh
        variants = read(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        assert set(variants) == {f"age={age},pollinated={p}" for age in range(8) for p in ("false", "true")}
        recipe = read(f"data/gugugaga/recipe/{crop}_seeds.json")
        assert recipe["result"]["id"] == f"gugugaga:{crop}_seeds"
        loot = read(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")["pools"]
        assert len(loot) == 2
        assert loot[0]["entries"][0]["name"] == f"gugugaga:{crop}_seeds"
        assert loot[1]["entries"][0]["name"] == f"gugugaga:{crop}"
        assert generator.count(f"case '{crop}':") >= 2, crop
        assert f"crop == '{crop}'" in generator, crop

    profile_files = [
        "crop_profiles_v0_4.json", "crop_additions_v0_9.json",
        "crop_additions_v1_0.json", "crop_additions_v1_3.json",
        "crop_additions_v1_7.json", "crop_additions_v1_8.json",
    ]
    all_profile_rows = [row for name in profile_files
                        for row in read("data/gugugaga/agronomy/" + name)["crops"]]
    all_crops = [row["crop"] for row in all_profile_rows]
    assert len(all_crops) == 80 and len(set(all_crops)) == 80

    # Keep the runtime climate switch and crop registry in lockstep with the
    # authoritative profile metadata. A missing band otherwise fails during a
    # crop's random tick, long after resource-only validation has passed.
    band_block = climate.split("private static Band cropBand(String seedName)", 1)[1].split(
        "private static Band biomeBand", 1
    )[0]
    climate_by_seed = {}
    for match in re.finditer(r"case\s+(.*?)\s*->\s*Band\.([A-Z]+);", band_block, re.S):
        for seed_name in re.findall(r'"([a-z0-9_]+_seeds)"', match.group(1)):
            assert seed_name not in climate_by_seed, seed_name
            climate_by_seed[seed_name] = match.group(2)
    assert set(climate_by_seed) == {f"{crop}_seeds" for crop in all_crops}
    for row in all_profile_rows:
        crop = row["crop"]
        assert climate_by_seed[f"{crop}_seeds"] == row["growth_band_gameplay"], crop

    registry_pollination = {
        crop: group for crop, group in re.findall(
            r'crop\("([a-z0-9_]+)_crop",\s*"([a-z0-9_]+)_seeds",\s*'
            r'AgricultureCropBlock\.PollinationGroup\.([A-Z]+)\)', registry
        )
    }
    assert set(registry_pollination) == set(all_crops)
    for row in all_profile_rows:
        assert registry_pollination[row["crop"]] == row["pollination_gameplay"], row["crop"]

    harvest_block = crop_java.split("private HarvestMode harvestMode()", 1)[0]
    repeated_harvest = set(re.findall(r'"([a-z0-9_]+_seeds)"', harvest_block))
    assert {"endive_seeds", "watercress_seeds"} <= repeated_harvest
    for crop in ("cassava", "taro", "yam", "teff", "kohlrabi", "rutabaga"):
        assert f'"{crop}_seeds"' not in repeated_harvest, crop

    assert "crop_additions_v1_8.json" in generator
    assert "cropIds.size() != 80" in generator
    assert "modelCount != 960 || blockTextureCount != 960 || itemTextureCount != 160" in generator
    assert "matureTextureHashes.size() != 80" in generator
    assert "allStageHashes.size() != 960" in generator
    assert "produceIconHashes.size() != 80" in generator and "seedIconHashes.size() != 80" in generator
    assert '"endive_seeds", "watercress_seeds"' in crop_java
    assert "hasNearbyFreshWater(level, pos, 2, -1, 1)" in climate
    assert '"taro_seeds".equals(seedName) || "watercress_seeds".equals(seedName)' in climate
    assert "propagule:'cassava taro yam'.split(' ')" in generator
    assert "organ.contains('corm')" in generator and "organ.contains('tuber')" in generator
    print("AGRICULTURE_V1_8_OK: 80 crops; eight new complete crop cycles; 960 stage models/textures; 160 custom produce and seed icons")

if __name__ == "__main__":
    main()
