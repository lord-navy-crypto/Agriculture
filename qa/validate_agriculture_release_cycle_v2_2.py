#!/usr/bin/env python3
"""Release gate for all crop production loops, honey production and dedicated creative tabs."""
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
TAB_REGISTRIES = [
    "AgricultureContent.ITEMS", "BeeContent.ITEMS",
    "AgricultureCuisineContent.ITEMS", "MarineFungiContent.ITEMS",
]
TAB_IDS = {"agriculture", "beekeeping", "cuisine", "marine_fungi"}

def read(path):
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    profiles = []
    for filename in PROFILE_FILES:
        data = read(RES / "data/gugugaga/agronomy" / filename)
        profiles.extend(data["crops"])
    crop_names = [row["crop"] for row in profiles]
    assert len(crop_names) == 100 and len(set(crop_names)) == 100, "crop profile catalog must contain 100 unique crops"

    en = read(RES / "assets/gugugaga/lang/en_us.json")
    zh = read(RES / "assets/gugugaga/lang/zh_cn.json")
    compost = read(RES / "data/neoforge/data_maps/item/compostables.json")["values"]
    agriculture_java = (AGRI / "AgricultureContent.java").read_text(encoding="utf-8")
    climate_java = (AGRI / "CropClimate.java").read_text(encoding="utf-8")
    crop_java = (AGRI / "AgricultureCropBlock.java").read_text(encoding="utf-8")
    generator = (ROOT / "tools/crop-textures.gradle").read_text(encoding="utf-8")
    bee_java = (AGRI / "BeeContent.java").read_text(encoding="utf-8")
    nectar_java = (AGRI / "NectarHoneySource.java").read_text(encoding="utf-8")
    harvester_java = (AGRI / "HoneyHarvesterItem.java").read_text(encoding="utf-8")

    registrations = {
        match.group(1): (match.group(2), match.group(3))
        for match in re.finditer(
            r'crop\("([a-z0-9_]+)_crop",\s*"([a-z0-9_]+)_seeds",\s*AgricultureCropBlock\.PollinationGroup\.([A-Z]+)\)',
            agriculture_java,
        )
    }
    assert set(registrations) == set(crop_names), "crop block registrations differ from the 100-profile catalog"

    climate_body = climate_java.split("return switch (seedName) {", 1)[1].split("};", 1)[0]
    climate_bands = {}
    for group, band in re.findall(r'case\s+(.*?)\s*->\s*Band\.([A-Z]+);', climate_body, re.S):
        for seed in re.findall(r'"([a-z0-9_]+_seeds)"', group):
            climate_bands[seed] = band

    source_files = [
        agriculture_java, bee_java,
        (AGRI / "AgricultureCuisineContent.java").read_text(encoding="utf-8"),
        (AGRI / "MarineFungiContent.java").read_text(encoding="utf-8"),
    ]
    registered_items = set()
    for source in source_files:
        for pattern in (
            r'\b(?:seed|food|material|floralHoney|habitatItem|culture|cultureItem|marineCrop|blockItem)\("([a-z0-9_]+)"',
            r'\bITEMS\.register\(\s*"([a-z0-9_]+)"',
        ):
            registered_items.update(re.findall(pattern, source))

    custom_tags = set()
    tag_root = RES / "data/gugugaga/tags/item"
    for path in tag_root.rglob("*.json"):
        rel = path.relative_to(tag_root).with_suffix("")
        custom_tags.add("gugugaga:" + str(rel).replace("\\", "/"))

    for row in profiles:
        crop = row["crop"]
        seed = crop + "_seeds"
        block_id = crop + "_crop"
        assert registrations[crop] == (seed, row["pollination_gameplay"]), f"{crop}: block/seed/pollination registration mismatch"
        assert climate_bands.get(seed) == row["growth_band_gameplay"], f"{crop}: climate band missing or mismatched"
        assert seed in registered_items and crop in registered_items, f"{crop}: seed or harvested produce item is not registered"

        recipe = read(RES / f"data/gugugaga/recipe/{seed}.json")
        assert recipe.get("type") in {"minecraft:crafting_shapeless", "minecraft:crafting_shaped"}, f"{crop}: unsupported seed recipe"
        assert recipe.get("result", {}).get("id") == "gugugaga:" + seed, f"{crop}: seed recipe output mismatch"
        assert recipe["result"].get("count", 1) >= 1, f"{crop}: invalid seed output count"
        ingredients = recipe.get("ingredients", [])
        if recipe["type"] == "minecraft:crafting_shaped":
            ingredients = list(recipe.get("key", {}).values())
            assert recipe.get("pattern"), f"{crop}: shaped seed recipe has no pattern"
        assert ingredients, f"{crop}: seed recipe has no ingredients"
        for ingredient in ingredients:
            if "item" in ingredient:
                identifier = ingredient["item"]
                namespace, _, path = identifier.partition(":")
                assert namespace in {"minecraft", "gugugaga"}, f"{crop}: unsupported ingredient namespace {identifier}"
                if namespace == "gugugaga":
                    assert path in registered_items, f"{crop}: unregistered mod ingredient {identifier}"
            elif "tag" in ingredient:
                tag = ingredient["tag"]
                namespace, _, _ = tag.partition(":")
                assert namespace in {"minecraft", "gugugaga"}, f"{crop}: unsupported ingredient tag {tag}"
                if namespace == "gugugaga":
                    assert tag in custom_tags, f"{crop}: missing custom item tag {tag}"
            else:
                raise AssertionError(f"{crop}: malformed seed ingredient {ingredient}")

        for item in (crop, seed):
            model = read(RES / f"assets/gugugaga/models/item/{item}.json")
            assert model.get("parent") == "minecraft:item/generated", f"{item}: wrong item model parent"
            assert model.get("textures", {}).get("layer0") == f"gugugaga:item/crops/{item}", f"{item}: wrong texture mapping"
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh, f"{item}: bilingual item name missing"
            assert "gugugaga:" + item in compost, f"{item}: compost mapping missing"
        assert f"block.gugugaga.{block_id}" in en and f"block.gugugaga.{block_id}" in zh, f"{block_id}: bilingual block name missing"

        blockstate = read(RES / f"assets/gugugaga/blockstates/{block_id}.json")
        variants = blockstate["variants"]
        expected_variants = {f"age={age},pollinated={flag}" for age in range(8) for flag in ("false", "true")}
        assert set(variants) == expected_variants, f"{block_id}: incomplete age/pollination blockstates"
        for age in range(8):
            base = f"gugugaga:block/crops/{crop}_stage{age}"
            assert variants[f"age={age},pollinated=false"]["model"] == base, f"{block_id}: age {age} model mismatch"
            expected = base + ("_pollinated" if age >= 4 else "")
            assert variants[f"age={age},pollinated=true"]["model"] == expected, f"{block_id}: pollinated age {age} model mismatch"

        loot = read(RES / f"data/gugugaga/loot_table/blocks/{block_id}.json")
        pools = loot.get("pools", [])
        assert len(pools) == 2, f"{block_id}: both seed and produce loot pools required"
        assert pools[0]["entries"][0].get("name") == "gugugaga:" + seed, f"{block_id}: seed drop missing"
        produce_entry = pools[1]["entries"][0]
        assert produce_entry.get("name") == "gugugaga:" + crop, f"{block_id}: produce drop missing"
        assert any(
            condition.get("condition") == "minecraft:block_state_property"
            and condition.get("properties", {}).get("age") == "7"
            for condition in produce_entry.get("conditions", [])
        ), f"{block_id}: produce must require maturity"
        assert generator.count(f"case '{crop}':") >= 2, f"{crop}: crop-specific growth/stage art mapping incomplete"

    # Harvest cycle: every repeat-harvest ID must belong to the 100-crop catalog.
    harvest_sets = {}
    for constant in ("REPEATED_FRUIT_HARVEST", "REPEATED_LEAF_HARVEST",
                     "REPEATED_FLOWER_HARVEST", "REPEATED_SPEAR_HARVEST"):
        body = crop_java.split(constant, 1)[1].split(");", 1)[0]
        harvest_sets[constant] = set(re.findall(r'"([a-z0-9_]+_seeds)"', body))
        assert harvest_sets[constant] <= {crop + "_seeds" for crop in crop_names}, f"{constant}: unknown crop configured"
    all_repeat = [seed for values in harvest_sets.values() for seed in values]
    assert len(all_repeat) == len(set(all_repeat)), "a crop is assigned to multiple repeat-harvest modes"
    assert "if (mode == HarvestMode.SINGLE_HARVEST || state.getValue(AGE) < getMaxAge())" in crop_java
    assert "level.setBlock(pos, state.setValue(AGE, 4).setValue(POLLINATED, false), 3);" in crop_java
    assert '"age": "7"' in (RES / "data/gugugaga/loot_table/blocks/corn_crop.json").read_text(encoding="utf-8")

    # Fifty registered bottle variants: 49 explicit source routes plus a mixed-forage blend.
    honey = read(RES / "data/gugugaga/agronomy/honey_sources_v1_2.json")
    variants = honey.get("honey_variants", [])
    assert honey.get("schema_version") == "1.2" and len(variants) == 50, "expected 50 honey variants"
    honey_ids = [row["item"] for row in variants]
    assert len(set(honey_ids)) == 50, "honey variant IDs must be unique"
    registered_honey = set(re.findall(r'floralHoney\("([a-z0-9_]+)"\)', bee_java))
    assert set(honey_ids) == registered_honey, "honey data and registered honey bottles differ"
    for item in honey_ids:
        model = read(RES / f"assets/gugugaga/models/item/{item}.json")
        assert model.get("parent") == "minecraft:item/generated", f"{item}: missing generated item model"
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh, f"{item}: bilingual honey name missing"
    sources_block = nectar_java.split("private static final String[] SOURCES = {", 1)[1].split("};", 1)[0]
    sources = re.findall(r'"([^"]+)"', sources_block)
    assert len(sources) == 49 and len(set(sources)) == 49, "expected 49 unique specialty honey forage routes"
    assert "dominantCount < 6" in nectar_java and "dominantCount * 3 < totalFlowers * 2" in nectar_java
    assert "totalFlowers >= 12 && distinctSourceKinds >= 3" in nectar_java
    assert "if (!level.hasChunkAt(sample)) continue;" in nectar_java
    assert "state.getValue(BeehiveBlock.HONEY_LEVEL) < 5" in harvester_java
    assert "CampfireBlock.isSmokeyPos(level, hivePos)" in harvester_java
    assert "Items.GLASS_BOTTLE" in harvester_java and "Items.SHEARS" in harvester_java
    assert "new ItemStack(Items.HONEYCOMB, 3)" in harvester_java
    assert "BeehiveBlock.HONEY_LEVEL, 0" in harvester_java

    # The mod gets four dedicated tabs, with every registered item registry displayed once.
    tab_java = (SRC / "GuguGagaCreativeTabs.java").read_text(encoding="utf-8")
    mod_java = (SRC / "GuguGagaMod.java").read_text(encoding="utf-8")
    tab_ids = set(re.findall(r'TABS\.register\("([a-z_]+)"', tab_java))
    assert tab_ids == TAB_IDS, f"creative tab mismatch: {tab_ids}"
    assert "GuguGagaCreativeTabs.TABS.register(modBus);" in mod_java
    assert mod_java.count("addListener(") == 2, "only entity setup listeners should remain"
    for registry in TAB_REGISTRIES:
        assert tab_java.count(registry + ".getEntries()") == 1, f"{registry}: creative tab coverage missing or duplicated"
    for path in (
        AGRI / "AgricultureContent.java",
        AGRI / "AgricultureCuisineContent.java",
        AGRI / "MarineFungiContent.java",
    ):
        source = path.read_text(encoding="utf-8")
        assert "onCreativeTab" not in source and "BuildCreativeModeTabContentsEvent" not in source
        assert "CreativeModeTabs." not in source, f"{path.name}: still injects into a vanilla creative tab"
    for key in (
        "itemGroup.gugugaga.agriculture", "itemGroup.gugugaga.beekeeping",
        "itemGroup.gugugaga.cuisine", "itemGroup.gugugaga.marine_fungi",
    ):
        assert key in en and key in zh, f"missing creative tab translation {key}"

    print("AGRICULTURE_RELEASE_CYCLE_V2_2_OK: 100/100 crop planting, growth, harvest, seed recipes and resource links; 50 honey bottles with 49 specialty forage routes; 4 dedicated creative tabs; no vanilla-tab injection.")

if __name__ == "__main__":
    main()
