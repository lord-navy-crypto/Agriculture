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
    assert "SOURCE_INDEX.get(cropResource.toString())" in nectar_java
    assert "SOURCE_INDEX.get(identifier)" in nectar_java
    assert "bee -> bee.isAlive() && bee.hasNectar()" in crop_java
    assert "List<ItemStack> drops = super.getDrops(state, builder);" in crop_java
    assert "new ArrayList<>(super.getDrops(state, builder))" not in crop_java
    assert "for (int ring = 0; ring <= radius; ring++)" in climate
    assert "cropIds.size() != 100" in generator
    assert "modelCount != 1200 || blockTextureCount != 1200 || itemTextureCount != 200" in generator
    assert "matureTextureHashes.size() != 100" in generator and "allStageHashes.size() != 1200" in generator
    assert "produceIconHashes.size() != 100" in generator and "seedIconHashes.size() != 100" in generator
    assert "honeyTexturePalette.size() != 50 || honeyTextureHashes.size() != 50" in generator

    honey = read("data/gugugaga/agronomy/honey_sources_v1_2.json")
    assert honey["schema_version"] == "1.2" and len(honey["honey_variants"]) == 50
    rows = {row["item"]: row for row in honey["honey_variants"]}
    source_block = nectar_java.split("private static final String[] SOURCES = {", 1)[1].split("};", 1)[0]
    routes = set(re.findall(r'"([^"]+)"', source_block))
    assert not re.search(r'"[a-z0-9_]+_crop"', source_block)
    route_to_item = {
        row["source_crop"]: row["item"]
        for row in honey["honey_variants"]
        if row["source_crop"] != "mixed_flower_forage"
    }
    switch_block = nectar_java.split("Item chosen = switch (dominantIndex) {", 1)[1].split("};", 1)[0]
    switch_routes = {
        int(index): constant
        for index, constant in re.findall(
            r"case\s+(\d+)\s*->\s*BeeContent\.([A-Z0-9_]+)\.get\(\);",
            switch_block,
        )
    }
    assert set(switch_routes) == set(range(49))
    for index, route in enumerate(re.findall(r'"([^"]+)"', source_block)):
        normalized = route.removeprefix("gugugaga:")
        expected_item = route_to_item[normalized]
        expected_constant = expected_item.removesuffix("_honey_bottle").upper() + "_HONEY_BOTTLE"
        assert switch_routes[index] == expected_constant, (index, route, expected_item)
    assert len(routes) == 49
    for crop, item in HONEY_ITEMS.items():
        row = rows[item]
        assert row["source_crop"] == f"{crop}_crop"
        assert f'"gugugaga:{crop}_crop"' in source_block
        assert f'floralHoney("{item}")' in bee_java
        assert f'case "{item}" ->' in bee_java
        assert f"BeeContent.{item.removesuffix('_honey_bottle').upper()}_HONEY_BOTTLE.get()" in nectar_java
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
        model = read(f"assets/gugugaga/models/item/{item}.json")
        assert model["textures"]["layer0"] == f"gugugaga:item/honey/{item}"
    print("AGRICULTURE_V2_0_OK: eight crop cycles; climate/pollination/harvest mappings; 100-crop asset contract; four v2.1 crop/honey additions; 50 bottles and 49 source routes")

if __name__ == "__main__":
    main()
