#!/usr/bin/env python3
"""Contract checks for the 100-crop / 50-honey agriculture milestone."""
import json
import re
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
EXPECTED = {
    "melon": ("WARM", "ORCHARD", "fruit"),
    "watermelon": ("WARM", "ORCHARD", "fruit"),
    "fava_bean": ("COOL", "ORCHARD", "pods and seeds"),
    "jute_mallow": ("WARM", "NONE", "leaves"),
}
HONEY = {
    "melon": "melon_blossom_honey_bottle",
    "watermelon": "watermelon_blossom_honey_bottle",
    "fava_bean": "fava_bean_blossom_honey_bottle",
    "jute_mallow": "jute_mallow_blossom_honey_bottle",
}
PROFILE_FILES = [
    "crop_profiles_v0_4.json", "crop_additions_v0_9.json",
    "crop_additions_v1_0.json", "crop_additions_v1_3.json",
    "crop_additions_v1_7.json", "crop_additions_v1_8.json",
    "crop_additions_v1_9.json", "crop_additions_v2_0.json",
    "crop_additions_v2_1.json",
]
def read(path):
    return json.loads((RES / path).read_text(encoding="utf-8"))
def main():
    profile = read("data/gugugaga/agronomy/crop_additions_v2_1.json")
    assert profile["schema_version"] == "2.1"
    assert {row["crop"] for row in profile["crops"]} == set(EXPECTED)
    en, zh = read("assets/gugugaga/lang/en_us.json"), read("assets/gugugaga/lang/zh_cn.json")
    registry = (SRC / "AgricultureContent.java").read_text(encoding="utf-8")
    climate = (SRC / "CropClimate.java").read_text(encoding="utf-8")
    crop_java = (SRC / "AgricultureCropBlock.java").read_text(encoding="utf-8")
    generator = (ROOT / "tools/crop-textures.gradle").read_text(encoding="utf-8")
    compost = read("data/neoforge/data_maps/item/compostables.json")["values"]
    all_crops = []
    for filename in PROFILE_FILES:
        all_crops.extend(row["crop"] for row in read("data/gugugaga/agronomy/" + filename)["crops"])
    assert len(all_crops) == 100 and len(set(all_crops)) == 100
    for row in profile["crops"]:
        crop = row["crop"]
        assert (row["growth_band_gameplay"], row["pollination_gameplay"], row["harvested_organ"]) == EXPECTED[crop]
        assert row["botanical_note"].strip()
        assert row["source_urls"] and all(url.startswith("https://") for url in row["source_urls"])
        assert f'crop("{crop}_crop", "{crop}_seeds", AgricultureCropBlock.PollinationGroup.{row["pollination_gameplay"]})' in registry
        assert f'"{crop}_seeds"' in climate
        assert f'event.accept({crop.upper()}_SEEDS.get())' in registry and f'event.accept({crop.upper()}.get())' in registry
        for item in (crop, crop + "_seeds"):
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
            assert f"gugugaga:{item}" in compost
            model = read(f"assets/gugugaga/models/item/{item}.json")
            assert model["parent"] == "minecraft:item/generated"
            assert model["textures"]["layer0"] == f"gugugaga:item/crops/{item}"
        assert f"block.gugugaga.{crop}_crop" in en and f"block.gugugaga.{crop}_crop" in zh
        variants = read(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        assert set(variants) == {f"age={age},pollinated={p}" for age in range(8) for p in ("false", "true")}
        recipe = read(f"data/gugugaga/recipe/{crop}_seeds.json")
        assert recipe["result"]["id"] == f"gugugaga:{crop}_seeds"
        loot = read(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")["pools"]
        assert len(loot) == 2 and loot[0]["entries"][0]["name"] == f"gugugaga:{crop}_seeds"
        assert loot[1]["entries"][0]["name"] == f"gugugaga:{crop}"
        detail = generator.split("def cropSpecificStageDetail = {", 1)[1].split("def stageTexture = {", 1)[0]
        assert detail.count(f"case '{crop}':") >= 2, crop
    fruit = crop_java.split("REPEATED_FRUIT_HARVEST", 1)[1].split(");", 1)[0]
    leaves = crop_java.split("REPEATED_LEAF_HARVEST", 1)[1].split(");", 1)[0]
    assert '"melon_seeds"' in fruit and '"watermelon_seeds"' in fruit and '"jute_mallow_seeds"' in leaves
    assert '"fava_bean_seeds"' not in fruit and '"fava_bean_seeds"' not in leaves
    honey = read("data/gugugaga/agronomy/honey_sources_v1_2.json")
    assert honey["schema_version"] == "1.2" and len(honey["honey_variants"]) == 50
    honey_rows = {row["item"]: row for row in honey["honey_variants"]}
    bee = (SRC / "BeeContent.java").read_text(encoding="utf-8")
    nectar = (SRC / "NectarHoneySource.java").read_text(encoding="utf-8")
    source_block = nectar.split("private static final String[] SOURCES = {", 1)[1].split("};", 1)[0]
    routes = re.findall(r'"([^"]+)"', source_block)
    switch_block = nectar.split("Item chosen = switch (dominantIndex) {", 1)[1].split("};", 1)[0]
    switch_routes = {int(index): constant for index, constant in re.findall(
        r"case\s+(\d+)\s*->\s*BeeContent\.([A-Z0-9_]+)\.get\(\);", switch_block
    )}
    route_to_item = {row["source_crop"]: row["item"] for row in honey["honey_variants"] if row["source_crop"] != "mixed_flower_forage"}
    assert len(routes) == 49 and set(switch_routes) == set(range(49))
    for index, route in enumerate(routes):
        expected_item = route_to_item[route.removeprefix("gugugaga:")]
        assert switch_routes[index] == expected_item.removesuffix("_honey_bottle").upper() + "_HONEY_BOTTLE"
    for crop, item in HONEY.items():
        assert honey_rows[item]["source_crop"] == crop + "_crop"
        assert f'"gugugaga:{crop}_crop"' in source_block
        assert f'floralHoney("{item}")' in bee and f'case "{item}" ->' in bee
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}.effect" in en
        assert f"item.gugugaga.{item}" in zh and f"item.gugugaga.{item}.effect" in zh
        model = read(f"assets/gugugaga/models/item/{item}.json")
        assert model["textures"]["layer0"] == f"gugugaga:item/honey/{item}"
    assert "crop_additions_v2_1.json" in generator and "honey_sources_v1_2.json" in generator
    assert "cropIds.size() != 100" in generator
    assert "modelCount != 1200 || blockTextureCount != 1200 || itemTextureCount != 200" in generator
    assert "honeyTexturePalette.size() != 50 || honeyTextureHashes.size() != 50" in generator
    print("AGRICULTURE_V2_1_OK: 100 crop lines; 1200 stage/model assets; 200 crop icons; 50 honey bottles / 49 forage routes")
if __name__ == "__main__":
    main()
