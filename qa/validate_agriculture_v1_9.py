#!/usr/bin/env python3
"""Contract checks for v1.9 crops, generated art coverage, and the v1.0 honey source catalog."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
EXPECTED = {
    "jicama": ("WARM", "storage root"), "adzuki_bean": ("WARM", "dry bean seeds"),
    "mung_bean": ("WARM", "dry bean seeds"), "cowpea": ("WARM", "pods and seeds"),
    "safflower": ("DRY", "oilseed"), "chicory": ("COOL", "taproot"),
    "mustard_greens": ("COOL", "leaves"), "water_spinach": ("WET", "leaves"),
}
def read(path):
    return json.loads((RES / path).read_text(encoding="utf-8"))

def main():
    profile = read("data/gugugaga/agronomy/crop_additions_v1_9.json")
    assert profile["schema_version"] == "1.9"
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
        assert (row["growth_band_gameplay"], row["harvested_organ"]) == EXPECTED[crop]
        assert row["botanical_note"].strip()
        assert row["source_urls"] and all(url.startswith("https://") for url in row["source_urls"])
        assert f'crop("{crop}_crop", "{crop}_seeds", AgricultureCropBlock.PollinationGroup.NONE)' in registry
        assert f'"{crop}_seeds"' in climate
        assert "AgricultureContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()))" in (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaCreativeTabs.java").read_text("utf8")
        assert "AgricultureContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()))" in (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaCreativeTabs.java").read_text("utf8")
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
        "crop_profiles_v0_4.json", "crop_additions_v0_9.json", "crop_additions_v1_0.json",
        "crop_additions_v1_3.json", "crop_additions_v1_7.json", "crop_additions_v1_8.json",
        "crop_additions_v1_9.json", "crop_additions_v2_0.json", "crop_additions_v2_1.json",
    ]
    all_crops = [row["crop"] for name in profile_files for row in read("data/gugugaga/agronomy/" + name)["crops"]]
    assert len(all_crops) == 100 and len(set(all_crops)) == 100
    assert "cropIds.size() != 100" in generator
    assert "modelCount != 1200 || blockTextureCount != 1200 || itemTextureCount != 200" in generator
    assert "matureTextureHashes.size() != 100" in generator and "allStageHashes.size() != 1200" in generator
    assert "produceIconHashes.size() != 100" in generator and "seedIconHashes.size() != 100" in generator
    assert '"mustard_greens_seeds", "water_spinach_seeds"' in crop_java
    honey = read("data/gugugaga/agronomy/honey_sources_v1_2.json")
    assert len(honey["honey_variants"]) == 50
    assert len({row["item"] for row in honey["honey_variants"]}) == 50
    for row in honey["honey_variants"]:
        item = row["item"]
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
        model = read(f"assets/gugugaga/models/item/{item}.json")
        assert model["textures"]["layer0"] == f"gugugaga:item/honey/{item}"
    source_block = (SRC / "NectarHoneySource.java").read_text(encoding="utf-8").split("private static final String[] SOURCES = {",1)[1].split("};",1)[0]
    routed = set(re.findall(r'"([^"]+)"', source_block))
    routed = {route.removeprefix("gugugaga:") for route in routed}
    expected = {row["source_crop"] for row in honey["honey_variants"] if row["source_crop"] != "mixed_flower_forage"}
    assert routed == expected and len(routed) == 49
    print("AGRICULTURE_CATALOG_V2_0_OK: 100 crop cycles; 1200 stage/model assets; 200 crop icons; 50 honey variants / 49 forage routes")
if __name__ == "__main__":
    main()
