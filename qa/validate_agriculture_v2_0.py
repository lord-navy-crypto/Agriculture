#!/usr/bin/env python3
"""Contract checks for v2.0 crop profiles, exact asset mappings, and floral honey routes."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
EXPECTED = {
    "celeriac": ("COOL", "NONE", "storage root"),
    "purslane": ("WARM", "NONE", "leaves"),
    "chayote": ("WARM", "ORCHARD", "fruit"),
    "grape": ("TEMPERATE", "BERRY", "berries"),
    "tea": ("TEMPERATE", "NONE", "leaves"),
    "coffee": ("WARM", "ORCHARD", "seeds"),
    "rapini": ("COOL", "NONE", "flower buds"),
    "napa_cabbage": ("COOL", "NONE", "head"),
}
HONEY_ITEMS = {
    "celeriac": "celeriac_blossom_honey_bottle",
    "purslane": "purslane_blossom_honey_bottle",
    "chayote": "chayote_blossom_honey_bottle",
    "grape": "grape_blossom_honey_bottle",
    "tea": "tea_blossom_honey_bottle",
    "coffee": "coffee_blossom_honey_bottle",
    "rapini": "rapini_blossom_honey_bottle",
    "napa_cabbage": "napa_cabbage_blossom_honey_bottle",
}

def read(path):
    return json.loads((RES / path).read_text(encoding="utf-8"))

def main():
    profile = read("data/gugugaga/agronomy/crop_additions_v2_0.json")
    assert profile["schema_version"] == "2.0"
    assert {row["crop"] for row in profile["crops"]} == set(EXPECTED)
    en, zh = read("assets/gugugaga/lang/en_us.json"), read("assets/gugugaga/lang/zh_cn.json")
    registry = (SRC / "AgricultureContent.java").read_text(encoding="utf-8")
    climate = (SRC / "CropClimate.java").read_text(encoding="utf-8")
    crop_java = (SRC / "AgricultureCropBlock.java").read_text(encoding="utf-8")
    bee_java = (SRC / "BeeContent.java").read_text(encoding="utf-8")
    nectar_java = (SRC / "NectarHoneySource.java").read_text(encoding="utf-8")
    generator = (ROOT / "tools/crop-textures.gradle").read_text(encoding="utf-8")
    compost = read("data/neoforge/data_maps/item/compostables.json")["values"]
    climate_body = climate.split("return switch (seedName) {", 1)[1].split("};", 1)[0]

    for row in profile["crops"]:
        crop = row["crop"]
        band, pollination, organ = EXPECTED[crop]
        assert (row["growth_band_gameplay"], row["pollination_gameplay"], row["harvested_organ"]) == (band, pollination, organ)
        assert row["botanical_note"].strip()
        assert row["source_urls"] and all(url.startswith("https://") for url in row["source_urls"])
        assert f'crop("{crop}_crop", "{crop}_seeds", AgricultureCropBlock.PollinationGroup.{pollination})' in registry
        band_line = next(line for line in climate_body.splitlines() if f'"{crop}_seeds"' in line)
        assert f"Band.{band}" in band_line, (crop, band_line)
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

    assert '"purslane_seeds", "tea_seeds", "rapini_seeds"' in crop_java
    fruit_block = crop_java.split("REPEATED_FRUIT_HARVEST", 1)[1].split(");", 1)[0]
    assert '"chayote_seeds", "grape_seeds"' in fruit_block
    assert "crop_additions_v2_0.json" in generator
    assert "honey_sources_v1_1.json" in generator
    assert "cropIds.size() != 96" in generator
    assert "modelCount != 1152 || blockTextureCount != 1152 || itemTextureCount != 192" in generator
    assert "matureTextureHashes.size() != 96" in generator and "allStageHashes.size() != 1152" in generator
    assert "produceIconHashes.size() != 96" in generator and "seedIconHashes.size() != 96" in generator
    assert "honeyTexturePalette.size() != 46 || honeyTextureHashes.size() != 46" in generator

    honey = read("data/gugugaga/agronomy/honey_sources_v1_1.json")
    assert honey["schema_version"] == "1.1" and len(honey["honey_variants"]) == 46
    rows = {row["item"]: row for row in honey["honey_variants"]}
    source_block = nectar_java.split("private static final String[] SOURCES = {", 1)[1].split("};", 1)[0]
    routes = set(re.findall(r'"([^"]+)"', source_block))
    assert len(routes) == 45
    for crop, item in HONEY_ITEMS.items():
        row = rows[item]
        assert row["source_crop"] == f"{crop}_crop"
        assert f'"{crop}_crop"' in source_block
        assert f'floralHoney("{item}")' in bee_java
        assert f'case "{item}" ->' in bee_java
        assert f"BeeContent.{item.removesuffix('_honey_bottle').upper()}_HONEY_BOTTLE.get()" in nectar_java
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
        model = read(f"assets/gugugaga/models/item/{item}.json")
        assert model["textures"]["layer0"] == f"gugugaga:item/honey/{item}"
    print("AGRICULTURE_V2_0_OK: eight crop cycles; climate/pollination/harvest mappings; 96-crop asset contract; eight blossom honey variants; 46 bottles and 45 source routes")

if __name__ == "__main__":
    main()
