#!/usr/bin/env python3
"""Source-level contract checks for marine foods and renewable fungal cultures."""
import json
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"
def read(path):
    return json.loads((RES / path).read_text(encoding="utf-8"))
def main():
    catalog = read("data/gugugaga/agronomy/marine_fungi_catalog_v1_0.json")
    assert catalog["schema_version"] == "1.0"
    items, cultures, recipes = catalog["items"], catalog["cultures"], catalog["recipes"]
    item_ids = {row["id"] for row in items}
    culture_ids = {row["id"] for row in cultures}
    assert len(items) == 24 and len(item_ids) == 24
    assert len(cultures) == 6 and len(culture_ids) == 6
    assert len(recipes) == catalog["recipe_count"] and len(recipes) == 25
    assert all(row["acquisition_note"].strip() and row["palette"] for row in items)
    registry = (JAVA / "agriculture/MarineFungiContent.java").read_text(encoding="utf-8")
    culture_java = (JAVA / "agriculture/FungalCultureBlock.java").read_text(encoding="utf-8")
    main_java = (JAVA / "GuguGagaMod.java").read_text(encoding="utf-8")
    en, zh = read("assets/gugugaga/lang/en_us.json"), read("assets/gugugaga/lang/zh_cn.json")
    compost = read("data/neoforge/data_maps/item/compostables.json")["values"]
    generator = (ROOT / "tools/crop-textures.gradle").read_text(encoding="utf-8")
    assert "MarineFungiContent.BLOCKS.register(modBus)" in main_java
    assert "MarineFungiContent.ITEMS.register(modBus)" in main_java
    assert "MarineFungiContent::onCreativeTab" in main_java
    assert "GUGUGAGA_MARINE_FUNGI_RUNTIME_INIT_OK" in main_java
    assert "randomTick" in culture_java and "useWithoutItem" in culture_java
    assert "Blocks.MYCELIUM" in culture_java and "Blocks.PODZOL" in culture_java
    for row in items:
        item_id = row["id"]
        constant = item_id.upper()
        if row["food"]:
            assert f'{constant} = food("{item_id}", {row["nutrition"]}, {row["saturation_modifier"]:.2f}F)' in registry
        else:
            assert f'{constant} = ingredient("{item_id}")' in registry
        assert f"item.gugugaga.{item_id}" in en and f"item.gugugaga.{item_id}" in zh
        assert f"gugugaga:{item_id}" in compost
        model = read(f"assets/gugugaga/models/item/{item_id}.json")
        assert model["parent"] == "minecraft:item/generated"
        assert model["textures"]["layer0"] == f"gugugaga:item/marine_fungi/{item_id}"
    for row in cultures:
        culture_id = row["id"]
        constant = culture_id.upper()
        assert f'{constant} = culture("{culture_id}")' in registry
        assert f'case "{culture_id}" -> "{row["harvest_item"]}"' in culture_java
        assert f"public static final DeferredItem<BlockItem> {constant}_ITEM" in registry
        assert f"block.gugugaga.{culture_id}" in en and f"block.gugugaga.{culture_id}" in zh
        assert f"item.gugugaga.{culture_id}" in en and f"item.gugugaga.{culture_id}" in zh
        assert f"gugugaga:{culture_id}" in compost
        state = read(f"assets/gugugaga/blockstates/{culture_id}.json")["variants"]
        assert set(state) == {f"age={age}" for age in range(4)}
        for age in range(4):
            model = read(f"assets/gugugaga/models/block/marine_fungi/{culture_id}_stage{age}.json")
            assert model["parent"] == "minecraft:block/cross"
            assert model["textures"]["cross"] == f"gugugaga:block/marine_fungi/{culture_id}_stage{age}"
        loot = read(f"data/gugugaga/loot_table/blocks/{culture_id}.json")
        assert loot["pools"][0]["entries"][0]["name"] == f"gugugaga:{culture_id}"
        assert read(f"assets/gugugaga/models/item/{culture_id}.json")["textures"]["layer0"] == f"gugugaga:item/marine_fungi/{culture_id}"
        assert row["harvest_item"] in item_ids
    seen_recipe_ids, produced = set(), set()
    for row in recipes:
        recipe_id = row["id"]
        assert recipe_id not in seen_recipe_ids
        seen_recipe_ids.add(recipe_id)
        produced.add(row["output_item"])
        recipe = read(f"data/gugugaga/recipe/{recipe_id}.json")
        assert recipe["type"] == "minecraft:crafting_shapeless"
        assert recipe["result"]["id"] == f'gugugaga:{row["output_item"]}'
        assert recipe["result"]["count"] == row["count"]
        assert [entry["item"] for entry in recipe["ingredients"]] == row["ingredients"]
    culture_outputs = {row["harvest_item"] for row in cultures}
    assert produced | culture_outputs == item_ids | culture_ids
    print(f"MARINE_FUNGI_V1_0_SOURCE_OK: {len(items)} food/ingredient IDs, {len(cultures)} renewable fungal cultures, {len(recipes)} recipes")
if __name__ == "__main__":
    main()
