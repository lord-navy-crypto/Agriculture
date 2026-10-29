#!/usr/bin/env python3
"""Structural and pixel-art tests for four practical kitchen processing stations."""
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
REC = RES / "data/gugugaga/recipe"
MANIFEST = RES / "data/gugugaga/agronomy/kitchen_expansion_v2_8.json"
BASE = "assets/gugugaga/"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def main():
    manifest = read(MANIFEST)
    assert manifest["schema_version"] == "2.8"
    items = manifest["items"]
    stations = manifest["stations"]
    assert len(items) == 30 and len(stations) == 4
    ids = {row["id"] for row in items}
    assert len(ids) == 30 and len({r["id"] for r in stations}) == 4
    materials = [x for x in items if x["type"] == "ingredient"]
    meals = [x for x in items if x["type"] == "food"]
    tools = [x for x in items if x["type"] == "tableware"]
    assert (len(materials), len(meals), len(tools)) == (16, 12, 2)
    assert {x["id"] for x in tools} == {"ceramic_plate", "wooden_spoon"}
    en = read(RES / BASE / "lang/en_us.json")
    zh = read(RES / BASE / "lang/zh_cn.json")
    registry = (JAVA / "agriculture/KitchenContent.java").read_text("utf-8")
    processor = (JAVA / "agriculture/KitchenProcessingBlock.java").read_text("utf-8")
    mainmod = (JAVA / "GuguGagaMod.java").read_text("utf-8")
    tabs = (JAVA / "GuguGagaCreativeTabs.java").read_text("utf-8")

    assert "KitchenContent.BLOCKS.register(modBus)" in mainmod
    assert "KitchenContent.ITEMS.register(modBus)" in mainmod
    assert "KitchenContent.ITEMS.getEntries()" in tabs
    assert "protected ItemInteractionResult useItemOn" in processor
    assert "if (level.isClientSide)" in processor
    assert "stack.shrink(1)" in processor
    assert "getInventory().add(result)" in processor
    assert "KitchenContent.WOODEN_SPOON.get()" in processor
    assert "hand != InteractionHand.MAIN_HAND" in processor
    assert "Level.addFreshEntity" not in processor  # no unnecessary world helpers
    assert "randomTick" not in processor and "BlockEntity" not in processor

    # Each registered transformation has an explicit source, an output and a
    # station, all exactly reflected in the Java map. No hidden 1->many recipes.
    expected = {}
    for item in materials:
        assert item["station"] in {s["kind"] for s in stations}
        assert item["input"] != "gugugaga:" + item["id"]
        assert item["input"].startswith(("gugugaga:", "minecraft:"))
        expected[(item["input"], item["id"])] = item["station"]
    mapped = re.findall(r'"(gugugaga:[a-z_]+|minecraft:[a-z_]+)",\s*"([a-z_]+)"', processor)
    assert len(mapped) == 16 and len(set(mapped)) == 16
    assert set(mapped) == set(expected), (
        "Java workstation inputs/outputs differ from manifest: "
        + str(set(mapped).symmetric_difference(expected))
    )

    # Source items are either vanilla food items or registered crops / kitchen intermediates.
    crop_registry = (JAVA / "agriculture/AgricultureContent.java").read_text("utf-8")
    for item in materials:
        if item["input"].startswith("gugugaga:") and item["input"].split(":")[1] not in ids:
            assert 'food("' + item["input"].split(":")[1] + '"' in crop_registry, item["id"]
        assert f'material("{item["id"]}")' in registry

    # Each new recipe contains at least one processed ingredient, so it really
    # uses kitchen progression instead of duplicating original raw-food dishes.
    previous = []
    new_ids = {row["id"] for row in meals}
    for recipe_file in REC.glob("*.json"):
        if recipe_file.stem in new_ids:
            continue
        rec = read(recipe_file)
        if rec.get("type") == "minecraft:crafting_shapeless":
            previous.append((recipe_file.stem, sorted(i.get("item") for i in rec.get("ingredients", [])
                                                       if "item" in i)))
    signatures = set()
    for item in meals:
        recipe = read(REC / (item["id"] + ".json"))
        assert recipe["type"] == "minecraft:crafting_shapeless"
        assert recipe["category"] == "misc"
        assert recipe["result"] == {"id": "gugugaga:" + item["id"], "count": 1}
        inputs = [ing["item"] for ing in recipe["ingredients"]]
        assert inputs == item["ingredients"] and len(inputs) == 3
        assert any(x.startswith("gugugaga:") and x.split(":", 1)[1] in {i["id"] for i in materials}
                   for x in inputs), item["id"]
        sig = tuple(sorted(inputs))
        assert sig not in signatures, f"new kitchen recipes collide: {item['id']}"
        signatures.add(sig)
        for old_name, old_inputs in previous:
            # No existing shapeless recipe can match one of our processed
            # ingredients unless it already mentions that exact item.
            if old_inputs == sorted(inputs):
                raise AssertionError(f"{item['id']} recipe collides with {old_name}")
        assert f'food("{item["id"]}",' in registry

    for station in stations:
        id = station["id"]
        assert f'"{id}"' in registry
        for lang in (en, zh):
            assert lang["block.gugugaga." + id]
            assert lang["item.gugugaga." + id]
        state = read(RES / BASE / f"blockstates/{id}.json")["variants"]
        assert state == {"": {"model": f"gugugaga:block/{id}"}}
        block = read(RES / BASE / f"models/block/{id}.json")
        assert block["parent"] == "minecraft:block/block"
        assert len(block["elements"]) >= 3
        for element in block["elements"]:
            assert all(0 <= a < b <= 16 for a, b in zip(element["from"], element["to"]))
            assert set(element["faces"]) == {"up", "down", "north", "south", "east", "west"}
        assert read(RES / BASE / f"models/item/{id}.json")["parent"] == f"gugugaga:block/{id}"
        loot = read(RES / f"data/gugugaga/loot_table/blocks/{id}.json")
        assert loot["pools"][0]["entries"][0]["name"] == f"gugugaga:{id}"
        recipe = read(REC / (id + ".json"))
        assert recipe["type"] == "minecraft:crafting_shaped"
        assert recipe["result"] == {"id": f"gugugaga:{id}", "count": 1}
    for lang in (en, zh):
        for key in ("kitchen_requires_spoon","kitchen_processed","kitchen_hint_chop",
                    "kitchen_hint_mill","kitchen_hint_press","kitchen_hint_mix"):
            assert lang["message.gugugaga." + key]

    for item in items:
        for lang in (en, zh):
            assert lang["item.gugugaga." + item["id"]]
        model = read(RES / BASE / f"models/item/{item['id']}.json")
        assert model == {"parent":"minecraft:item/generated",
                         "textures":{"layer0":f"gugugaga:item/kitchen/{item['id']}"}}
    for tool in tools:
        recipe = read(REC / (tool["id"] + ".json"))
        assert recipe["type"] == "minecraft:crafting_shaped"
        assert recipe["result"]["id"] == f"gugugaga:{tool['id']}"

    if len(sys.argv) > 1:
        from validate_generated_crop_cuisine_art import read_png
        folder = Path(sys.argv[1]) / BASE / "textures/item/kitchen"
        unique = set()
        for item in items:
            image = folder / (item["id"] + ".png")
            assert image.is_file(), f"missing kitchen texture: {image}"
            w, h, colors, visible = read_png(image)
            assert (w, h) == (16, 16) and len(colors) > 4 and visible
            unique.add(hashlib.sha256(image.read_bytes()).hexdigest())
        assert len(unique) == 30, "duplicate kitchen item graphics"
        contact = ROOT / "build/reports/agriculture/kitchen-item-contact-sheet.png"
        w, h, _, visible = read_png(contact)
        assert (w, h) == (288, 240) and visible, "missing correct kitchen contact sheet"

    print("KITCHEN_V2_8_OK: four real processing blocks, 16 unique conversions, 2 utensils, "
          "12 novel processed-food dishes, bilingual names, meshes, drops and craft recipes; "
          + ("30 unique PNGs pass" if len(sys.argv) > 1 else "structural checks pass"))


if __name__ == "__main__":
    main()
