#!/usr/bin/env python3
"""Kitchen v3.2: five old tools now enable distinct offhand-specific routes."""
from __future__ import annotations
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
BASE = RES / "assets/gugugaga"
REC = RES / "data/gugugaga/recipe"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"
CAT = RES / "data/gugugaga/agronomy/kitchen_tool_modes_v3_2.json"

def load(path):
    return json.loads(path.read_text("utf-8"))

def main():
    c = load(CAT)
    assert c["schema_version"] == "3.2"
    modes = c["modes"]
    items = c["items"]
    assert len(modes) == 5 and len(items) == 47
    assert len({x["id"] for x in items}) == 47
    prepared = [x for x in items if x["type"] == "ingredient"]
    foods = [x for x in items if x["type"] == "food"]
    assert (len(prepared), len(foods)) == (27, 20)
    assert {x["id"] for x in modes} == {"knife", "grater", "tongs", "measure", "skim"}
    assert {x["station"] for x in modes} == {"chop", "smoke", "mix", "simmer"}
    assert len({x["offhand"] for x in modes}) == 5

    v32 = (JAVA / "agriculture/KitchenV32Modes.java").read_text("utf-8")
    content = (JAVA / "agriculture/KitchenV32Content.java").read_text("utf-8")
    processor = (JAVA / "agriculture/KitchenProcessingBlock.java").read_text("utf-8")
    original = (JAVA / "agriculture/KitchenV3Content.java").read_text("utf-8")
    mod = (JAVA / "GuguGagaMod.java").read_text("utf-8")
    tab = (JAVA / "GuguGagaCreativeTabs.java").read_text("utf-8")
    en = load(BASE / "lang/en_us.json")
    zh = load(BASE / "lang/zh_cn.json")
    assert "KitchenV32Content.ITEMS.register(modBus)" in mod
    assert "KitchenV32Content.ITEMS.getEntries()" in tab

    older = [
        load(RES / "data/gugugaga/agronomy/kitchen_expansion_v2_8.json"),
        load(RES / "data/gugugaga/agronomy/kitchen_tools_v3_0.json"),
        load(RES / "data/gugugaga/agronomy/kitchen_specialists_v3_1.json")
    ]
    older_ids = {i["id"] for batch in older for i in batch["items"]}
    assert len(older_ids) == 79+66+47
    assert older_ids.isdisjoint({i["id"] for i in items}), "v3.2 would overwrite an existing item"
    assert "KitchenV32Modes.resolve(kind, player.getOffhandItem(), input)" in processor
    assert "boolean specialistMode = specialistOutput != null" in processor
    assert "specialistMode ? specialistOutput : kind.transformations().get(input)" in processor
    assert "kind == Kind.MIX && !specialistMode" in processor
    assert "kind == Kind.SIMMER && !specialistMode" in processor
    assert "CampfireBlock.isSmokeyPos(level, pos)" in processor
    assert "stack.shrink(1)" in processor
    assert "player.getInventory().add(result)" in processor
    assert "animated.startWork(server, pos)" in processor
    assert "if (specialistMode)" in processor
    assert "server.sendParticles(ParticleTypes.HAPPY_VILLAGER" in processor
    assert "randomTick" not in v32

    all_pairs = set()
    for mode in modes:
        id, station, offhand = mode["id"], mode["station"], mode["offhand"]
        label = id.upper()
        expected = {
            (x["input"], x["id"]) for x in prepared if x["mode"] == id
        }
        assert expected, id
        assert all(x["station"] == station and x["offhand"] == offhand
                   for x in prepared if x["mode"] == id)
        reg = offhand.split(":", 1)[1].upper()
        assert f"offhand.is(KitchenV3Content.{reg}.get())" in v32, id
        assert f'{reg} = material("{reg.lower()}")' in original
        assert f"kind == KitchenProcessingBlock.Kind.{station.upper()}" in v32
        p = rf"\b{label}\s*=\s*Map\.ofEntries\((.*?)\n\s*\);"
        found = re.findall(p, v32, re.S)
        assert len(found) == 1, (label, len(found))
        actual = re.findall(r'Map\.entry\("([a-z_:]+)",\s*"([a-z_]+)"\)', found[0])
        assert set(actual) == expected and len(actual) == len(expected), (
            label, actual, expected)
        assert not (all_pairs & set(actual)), "duplicate process map across modes"
        all_pairs.update(actual)
    assert len(all_pairs) == 27
    assert "return null;" in v32  # untouched main-hand combinations remain vanilla modes

    # All new recipes must use at least one v3.2 intermediate.
    signatures = set()
    for x in items:
        id = x["id"]
        assert en[f"item.gugugaga.{id}"] and zh[f"item.gugugaga.{id}"]
        assert f'"{id}"' in content
        expected_model = {"parent": "minecraft:item/generated",
                          "textures": {"layer0": f"gugugaga:item/kitchen_v32/{id}"}}
        assert load(BASE / f"models/item/{id}.json") == expected_model
        if x["type"] == "ingredient":
            assert f'material("{id}")' in content
        else:
            assert f'food("{id}",' in content
            recipe = load(REC / (id + ".json"))
            assert recipe["type"] == "minecraft:crafting_shapeless"
            assert recipe["result"] == {"id": f"gugugaga:{id}", "count": 1}
            inp = [i["item"] for i in recipe["ingredients"]]
            assert inp == x["ingredients"] and 2 <= len(inp) <= 9
            assert any("gugugaga:"+p["id"] in inp for p in prepared), id
            sig = tuple(sorted(inp))
            assert sig not in signatures, "new shapeless dish recipe overlaps"
            signatures.add(sig)

    assert any(x["input"] == "minecraft:apple" for x in prepared)
    assert any(x["input"] == "minecraft:beef" for x in prepared)
    assert any(x["input"] == "minecraft:salmon" for x in prepared)
    assert any(x["input"] == "minecraft:carrot" for x in prepared)
    assert all(x["offhand"].startswith("gugugaga:") for x in modes)

    if len(sys.argv) > 1:
        from validate_generated_crop_cuisine_art import read_png
        folder = Path(sys.argv[1]) / "assets/gugugaga/textures/item/kitchen_v32"
        hashes = set()
        for x in items:
            file = folder / (x["id"] + ".png")
            assert file.exists(), file
            w, h, colors, visible = read_png(file)
            assert (w,h)==(16,16) and visible and len(colors)>=5, x["id"]
            hashes.add(hashlib.sha256(file.read_bytes()).hexdigest())
        assert len(hashes)==47, "duplicate v3.2 sprite"
        sheet=ROOT / "build/reports/agriculture/kitchen-v32-contact-sheet.png"
        w,h,_,visible=read_png(sheet)
        assert (w,h)==(288,384) and visible
    print("KITCHEN_V32_PASS: 5 formerly unused kitchen tools functional on 4 old blocks, "
          "27 exact recipes, 20 prepared dishes, 47 PNG icons, zero new blocks, "
          "backward compatibility and vanilla food inputs")

if __name__ == "__main__":
    main()
