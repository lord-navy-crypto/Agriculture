#!/usr/bin/env python3
"""Vanilla-pantry kitchen v2.9 registry, recipes, smoke safety and pixel QA."""
from __future__ import annotations
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
R = ROOT / "src/main/resources"
REC = R / "data/gugugaga/recipe"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
CATALOG = R / "data/gugugaga/agronomy/kitchen_expansion_v2_8.json"
STATION_ENUMS = {"chop": "CHOP", "mill": "MILL", "press": "PRESS", "mix": "MIX", "smoke": "SMOKE"}

VANILLA_SOURCES = {
    "minecraft:wheat", "minecraft:carrot", "minecraft:potato", "minecraft:beetroot",
    "minecraft:pumpkin", "minecraft:brown_mushroom", "minecraft:red_mushroom",
    "minecraft:melon_slice", "minecraft:sweet_berries", "minecraft:cocoa_beans",
    "minecraft:pumpkin_seeds", "minecraft:melon_seeds", "minecraft:dried_kelp",
    "minecraft:wheat_seeds", "minecraft:apple", "minecraft:glow_berries",
    "minecraft:cod", "minecraft:salmon", "minecraft:beef", "minecraft:chicken"
}


def read(path: Path):
    return json.loads(path.read_text("utf-8"))


def main():
    cat = read(CATALOG)
    assert cat["schema_version"] == "2.9"
    items = cat["items"]
    stations = cat["stations"]
    assert len(items) == 79 and len({i["id"] for i in items}) == 79
    assert len(stations) == 5 and set(s["kind"] for s in stations) == set(STATION_ENUMS)
    assert len({s["id"] for s in stations}) == 5

    base = items[:30]
    added = items[30:]
    new_ingredients = [x for x in added if x["type"] == "ingredient"]
    smoked = [x for x in added if x["type"] == "processed_food"]
    dishes = [x for x in added if x["type"] == "food"]
    assert (len(new_ingredients), len(smoked), len(dishes)) == (25, 4, 20)

    old_inputs = [x for x in base if x["type"] == "ingredient"]
    old_dishes = [x for x in base if x["type"] == "food"]
    assert (len(old_inputs), len(old_dishes)) == (16, 12), "v2.8 items were lost"
    assert {x["id"] for x in base if x["type"] == "tableware"} == {
        "wooden_spoon", "ceramic_plate"}

    source = (JAVA / "KitchenProcessingBlock.java").read_text("utf-8")
    registry = (JAVA / "KitchenContent.java").read_text("utf-8")
    expected = {}
    routes = [x for x in items if x.get("station")]
    assert len(routes) == 45
    for row in routes:
        key = (row["input"], row["id"])
        assert key not in expected
        expected[key] = STATION_ENUMS[row["station"]]
    actual = {}
    enum_pattern = re.compile(
        r"\b(CHOP|MILL|PRESS|MIX|SMOKE)\(Map\.ofEntries\((.*?)\n\s*\)\)(?:,|;)", re.S)
    # This expression is intentionally anchored at a complete enum map,
    # not just free text containing identifiers in comments.
    maps = list(enum_pattern.finditer(source))
    assert len(maps) == 5, f"Expected five workstation maps, got {len(maps)}"
    for entry in maps:
        station, body = entry.groups()
        pairs = re.findall(r'Map\.entry\("([a-z_:]+)",\s*"([a-z_]+)"\)', body)
        assert pairs, station
        for inp, out in pairs:
            assert (inp, out) not in actual, f"duplicate workstation conversion {inp}"
            actual[(inp, out)] = station
    assert len(actual) == 45 and actual == expected, (
        f"mismatched 1:1 routes: missing {set(expected.items())-set(actual.items())}; "
        f"extra {set(actual.items())-set(expected.items())}")

    vanilla_routes = [r for r in added if r.get("input", "").startswith("minecraft:")]
    assert len(vanilla_routes) == 23
    assert {r["input"] for r in vanilla_routes}.issubset(VANILLA_SOURCES)
    assert len({r["input"] for r in vanilla_routes}) >= 18
    assert all(x["station"] == "smoke" for x in smoked)
    assert {x["input"] for x in smoked} == {
        "minecraft:cod", "minecraft:salmon", "minecraft:beef", "minecraft:chicken"
    }
    assert 'CampfireBlock.isSmokeyPos(level, pos)' in source
    assert 'kind == Kind.SMOKE && !level.isClientSide' in source
    assert "message.gugugaga.kitchen_requires_smoke" in source
    assert "stack.shrink(1)" in source
    assert "player.getInventory().add(result)" in source
    assert "KitchenContent.WOODEN_SPOON.get()" in source
    assert "randomTick" not in source

    en = read(R / "assets/gugugaga/lang/en_us.json")
    zh = read(R / "assets/gugugaga/lang/zh_cn.json")
    for station in stations:
        id = station["id"]
        assert f'"{id}"' in registry, f"station {id} unregistered"
        for language in (en, zh):
            assert language["block.gugugaga." + id]
            assert language["item.gugugaga." + id]
        assert read(R / f"assets/gugugaga/blockstates/{id}.json")["variants"] == {
            "": {"model": f"gugugaga:block/{id}"}}
        block = read(R / f"assets/gugugaga/models/block/{id}.json")
        assert block["parent"] == "minecraft:block/block"
        assert len(block["elements"]) >= 3
        for element in block["elements"]:
            assert all(0 <= a < b <= 16 for a, b in zip(
                element["from"], element["to"])), (id,element)
        assert read(R / f"assets/gugugaga/models/item/{id}.json")["parent"] == (
            "gugugaga:block/" + id)
        assert read(R / f"data/gugugaga/loot_table/blocks/{id}.json")[
            "pools"][0]["entries"][0]["name"] == "gugugaga:" + id
        sr = read(REC / (id + ".json"))
        assert sr["type"] == "minecraft:crafting_shaped"
        assert sr["result"]["id"] == f"gugugaga:{id}"
    for lang in (en, zh):
        assert lang["message.gugugaga.kitchen_requires_smoke"]
        assert lang["message.gugugaga.kitchen_hint_smoke"]

    for row in items:
        item_id = row["id"]
        assert en["item.gugugaga."+item_id] and zh["item.gugugaga."+item_id]
        assert read(R / f"assets/gugugaga/models/item/{item_id}.json") == {
            "parent": "minecraft:item/generated",
            "textures": {"layer0": f"gugugaga:item/kitchen/{item_id}"}}
        if row["type"] == "ingredient":
            assert f'material("{item_id}")' in registry, item_id
        elif row["type"] in ("food", "processed_food"):
            assert f'food("{item_id}",' in registry, item_id

    for row in dishes:
        r = read(REC / (row["id"] + ".json"))
        assert r["type"] == "minecraft:crafting_shapeless"
        assert r["category"] == "misc"
        assert r["result"] == {"id": f'gugugaga:{row["id"]}', "count": 1}
        assert [i["item"] for i in r["ingredients"]] == row["ingredients"]
        assert 2 <= len(row["ingredients"]) <= 9
        assert any(item.startswith("gugugaga:") and item[9:] in {
            r["id"] for r in added if r["type"] in ("ingredient", "processed_food")
        } for item in row["ingredients"]), "new dishes must use prepared vanilla items"

    # Detect shapeless ingredient collisions across the entire mod, including
    # older recipes with tags. An unknown tag can't match a new prepared item.
    tags = {}
    tagroot = R / "data/gugugaga/tags/item"
    for p in tagroot.rglob("*.json"):
        tags["gugugaga:" + str(p.relative_to(tagroot).with_suffix(""))] = set(
            read(p).get("values", []))
    new_signatures = set()
    for row in dishes:
        sig = tuple(sorted(row["ingredients"]))
        assert sig not in new_signatures, row["id"]
        new_signatures.add(sig)
        for p in REC.glob("*.json"):
            if p.stem == row["id"]:
                continue
            old = read(p)
            if old.get("type") != "minecraft:crafting_shapeless":
                continue
            ingredients = old.get("ingredients", [])
            if len(ingredients) != len(sig):
                continue
            # Conservative bipartite test; if an old tag cannot possibly match
            # a unique prepared ingredient, it cannot collide.
            def overlaps(remaining, options):
                if not remaining:
                    return True
                spec = remaining[0]
                for j, token in enumerate(options):
                    if (spec.get("item") == token or
                        token in tags.get(spec.get("tag"), set())):
                        if overlaps(remaining[1:], options[:j]+options[j+1:]):
                            return True
                return False
            assert not overlaps(ingredients, list(row["ingredients"])), (
                f"new dish {row['id']} duplicates {p.stem}")

    if len(sys.argv) > 1:
        from validate_generated_crop_cuisine_art import read_png
        root = Path(sys.argv[1]) / "assets/gugugaga/textures/item/kitchen"
        hashes = set()
        for row in items:
            png = root / (row["id"] + ".png")
            assert png.is_file(), png
            w, h, colors, visible = read_png(png)
            assert (w,h)==(16,16) and visible and len(colors)>4, row["id"]
            hashes.add(hashlib.sha256(png.read_bytes()).hexdigest())
        assert len(hashes) == 79, "kitchen texture collision"
        w,h,_,visible = read_png(ROOT / "build/reports/agriculture/kitchen-item-contact-sheet.png")
        assert (w,h)==(288,672) and visible, "expected 79-icon, 14-row proof sheet"
    print("VANILLA_KITCHEN_V2_9_OK: 23 vanilla source conversions, 4 campfire-smoked foods, "
          "20 new dishes, 5 modeled stations, 45 total process recipes, "
          "79 bilingual item icons; v2.8 still intact.")


if __name__ == "__main__":
    main()
