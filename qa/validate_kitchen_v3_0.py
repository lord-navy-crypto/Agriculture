#!/usr/bin/env python3
"""Kitchen 3.0: real utensil/station registrations, models, recipes and textures."""
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
REC=RES/"data/gugugaga/recipe"
JROOT=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
CAT=RES/"data/gugugaga/agronomy/kitchen_tools_v3_0.json"

def load(path):
    return json.loads(path.read_text("utf-8"))

def main():
    c=load(CAT)
    assert c["schema_version"]=="3.0"
    items=c["items"]
    stations=c["stations"]
    assert len(items)==66 and len({x["id"] for x in items})==66
    assert len(stations)==3 and {s["kind"] for s in stations}=={"knead","fry","steam"}
    assert [sum(i["type"]==kind for i in items)
            for kind in ("tool","tableware","ingredient","food")]==[12,12,24,18]
    v3=(JROOT/"agriculture/KitchenV3Content.java").read_text("utf-8")
    processor=(JROOT/"agriculture/KitchenProcessingBlock.java").read_text("utf-8")
    animation=(JROOT/"agriculture/KitchenV3AnimatedBlock.java").read_text("utf-8")
    mod=(JROOT/"GuguGagaMod.java").read_text("utf-8")
    tab=(JROOT/"GuguGagaCreativeTabs.java").read_text("utf-8")
    en=load(RES/"assets/gugugaga/lang/en_us.json")
    zh=load(RES/"assets/gugugaga/lang/zh_cn.json")
    assert "KitchenV3Content.BLOCKS.register(modBus)" in mod
    assert "KitchenV3Content.ITEMS.register(modBus)" in mod
    assert "KitchenV3Content.ITEMS.getEntries()" in tab
    assert "BooleanProperty.create(\"active\")" in animation
    assert "level.scheduleTick(pos, this, 10)" in animation
    assert "animated.startWork(server, pos)" in processor
    assert "server.sendParticles(ParticleTypes." in processor
    assert "KitchenV3Content.ROLLING_PIN.get()" in processor
    assert "KitchenV3Content.FRYING_SPATULA.get()" in processor
    assert "KitchenV3Content.STEAMER_LID.get()" in processor
    assert "CampfireBlock.isSmokeyPos(level, pos)" in processor
    assert "stack.shrink(1)" in processor
    assert "level.addFreshEntity(new ItemEntity(" in processor
    assert "randomTick" not in animation

    old=load(RES/"data/gugugaga/agronomy/kitchen_expansion_v2_8.json")
    oldids={x["id"] for x in old["items"]}
    assert len(oldids)==79 and oldids.isdisjoint({x["id"] for x in items})
    assert {x["id"] for x in old["stations"]}.isdisjoint({x["id"] for x in stations})

    prepared=[x for x in items if x["type"]=="ingredient"]
    for station in stations:
        id=station["id"]
        for lang in (en,zh):
            assert lang[f"block.gugugaga.{id}"] and lang[f"item.gugugaga.{id}"]
        assert f'station("{id}", KitchenProcessingBlock.Kind.{station["kind"].upper()})' in v3
        assert f'station("{id}"' in v3
        states=load(RES/f"assets/gugugaga/blockstates/{id}.json")["variants"]
        assert set(states)=={"active=false","active=true"}
        for active in (False,True):
            suffix="_active" if active else ""
            assert states[f"active={str(active).lower()}"] == {"model":f"gugugaga:block/{id}{suffix}"}
            model=load(RES/f"assets/gugugaga/models/block/{id}{suffix}.json")
            assert len(model["elements"])>=4, (id,suffix)
            for element in model["elements"]:
                assert all(0<=a<b<=16 for a,b in zip(element["from"],element["to"]))
        assert load(RES/f"assets/gugugaga/models/item/{id}.json")["parent"]==f"gugugaga:block/{id}"
        loot=load(RES/f"data/gugugaga/loot_table/blocks/{id}.json")
        assert loot["pools"][0]["entries"][0]["name"]==f"gugugaga:{id}"
        recipe=load(REC/f"{id}.json")
        assert recipe["type"]=="minecraft:crafting_shaped"
        assert recipe["result"]["id"]==f"gugugaga:{id}"
        name=station["kind"].upper()
        assert name+"(Map.ofEntries(" in processor
        region=processor.split(name+"(Map.ofEntries(",1)[1].split("\n        ))",1)[0]
        matches=re.findall(r'Map\.entry\("([a-z_:]+)",\s*"([a-z_]+)"\)',region)
        expected={(x["input"],x["id"]) for x in prepared if x["station"]==station["kind"]}
        assert len(matches)==8 and set(matches)==expected, (name,matches,expected)

    # Crafting must not offer multiple indistinguishable utensil/tableware
    # recipes; treat horizontal mirror patterns as the same recipe.
    shaped_signatures = {}
    for row in (i for i in items if i["type"] in ("tool", "tableware")):
        recipe = load(REC / (row["id"] + ".json"))
        pattern = recipe["pattern"]
        key = recipe["key"]
        def tokens(lines):
            return tuple(tuple(" " if v == " " else key[v]["item"] for v in line)
                         for line in lines)
        forward = tokens(pattern)
        flipped = tokens([line[::-1] for line in pattern])
        sig = min(forward, flipped)
        assert sig not in shaped_signatures, (
            "shaped recipe collision: " + row["id"] + " and " + shaped_signatures.get(sig, "")
        )
        shaped_signatures[sig] = row["id"]
    assert len(shaped_signatures) == 24

    for item in items:
        id=item["id"]
        for lang in (en,zh):
            assert lang[f"item.gugugaga.{id}"]
        assert f'"{id}"' in v3, id
        model=load(RES/f"assets/gugugaga/models/item/{id}.json")
        assert model=={"parent":"minecraft:item/generated",
                       "textures":{"layer0":f"gugugaga:item/kitchen_v3/{id}"}}
        if item["type"]=="food":
            assert f'food("{id}",' in v3
            recipe=load(REC/f"{id}.json")
            assert recipe["type"]=="minecraft:crafting_shapeless"
            assert recipe["category"]=="misc"
            assert [x["item"] for x in recipe["ingredients"]]==item["ingredients"]
            assert recipe["result"]=={"id":f"gugugaga:{id}","count":1}
            assert any(x.split(":",1)[-1] in {p["id"] for p in prepared}
                       for x in item["ingredients"]),id
        elif item["type"] in ("tool","tableware"):
            r=load(REC/f"{id}.json")
            assert r["type"]=="minecraft:crafting_shaped"
            assert r["result"]["id"]==f"gugugaga:{id}"
            symbols=set("".join(r["pattern"]))-{" "}
            assert symbols==set(r["key"]), (id,"unused or missing shaped recipe key")
    for lang in (en,zh):
        for x in ["kitchen_requires_v3_tool","kitchen_requires_heat",
                  "kitchen_hint_knead","kitchen_hint_fry","kitchen_hint_steam"]:
            assert lang["message.gugugaga."+x]

    if len(sys.argv)>1:
        from validate_generated_crop_cuisine_art import read_png
        folder=Path(sys.argv[1])/"assets/gugugaga/textures/item/kitchen_v3"
        checksums=set()
        for row in items:
            p=folder/(row["id"]+".png")
            assert p.is_file(),p
            w,h,colors,visible=read_png(p)
            assert (w,h)==(16,16) and visible and len(colors)>=5,(row["id"],len(colors))
            checksums.add(hashlib.sha256(p.read_bytes()).hexdigest())
        assert len(checksums)==66,"repeated icon content"
        sheet=ROOT/"build/reports/agriculture/kitchen-v3-contact-sheet.png"
        w,h,_,visible=read_png(sheet)
        assert (w,h)==(288,528) and visible
    print("KITCHEN_V3_PASS: 3 active-frame appliances, 3 offhand utensil gates, heat/smoke checks, "
          "24 ingredient conversions, 18 prepared dishes, 24 crafted tools/tableware, and "
          + ("66 pixel-tested icons." if len(sys.argv)>1 else "all static assets."))

if __name__=="__main__":
    main()
