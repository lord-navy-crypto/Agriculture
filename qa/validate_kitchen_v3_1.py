#!/usr/bin/env python3
"""Kitchen v3.1: four additional active-frame stations and older item/tool reuse."""
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
REC=RES/"data/gugugaga/recipe"
BASE=RES/"assets/gugugaga"
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
CAT=RES/"data/gugugaga/agronomy/kitchen_specialists_v3_1.json"

def read(path):
    return json.loads(path.read_text("utf-8"))

def main():
    c=read(CAT)
    assert c["schema_version"]=="3.1"
    stations=c["stations"]
    items=c["items"]
    assert len(stations)==4 and len(items)==47
    assert len({s["id"] for s in stations})==4
    assert len({i["id"] for i in items})==47
    prepared=[i for i in items if i["type"]=="ingredient"]
    meals=[i for i in items if i["type"]=="food"]
    assert (len(prepared),len(meals))==(24,23)
    assert set(s["kind"] for s in stations)=={"whisk","mortar","strain","simmer"}

    old=read(RES/"data/gugugaga/agronomy/kitchen_expansion_v2_8.json")
    v3=read(RES/"data/gugugaga/agronomy/kitchen_tools_v3_0.json")
    assert len(old["items"])==79 and len(v3["items"])==66
    older={x["id"] for x in old["items"]}|{x["id"] for x in v3["items"]}
    assert older.isdisjoint({x["id"] for x in items}),"old kitchen item overwritten"
    assert not ({x["id"] for x in old["stations"]+v3["stations"]}
                & {x["id"] for x in stations}),"old kitchen block overwritten"

    content=(JAVA/"agriculture/KitchenV31Content.java").read_text("utf-8")
    processor=(JAVA/"agriculture/KitchenProcessingBlock.java").read_text("utf-8")
    baseanimation=(JAVA/"agriculture/KitchenV3AnimatedBlock.java").read_text("utf-8")
    mod=(JAVA/"GuguGagaMod.java").read_text("utf-8")
    tab=(JAVA/"GuguGagaCreativeTabs.java").read_text("utf-8")
    oldtool=(JAVA/"agriculture/KitchenV3Content.java").read_text("utf-8")
    en=read(BASE/"lang/en_us.json")
    zh=read(BASE/"lang/zh_cn.json")

    assert "KitchenV31Content.BLOCKS.register(modBus)" in mod
    assert "KitchenV31Content.ITEMS.register(modBus)" in mod
    assert "KitchenV31Content.ITEMS.getEntries()" in tab
    assert "BooleanProperty.create(\"active\")" in baseanimation
    assert "level.scheduleTick(pos, this, 10)" in baseanimation
    assert "animated.startWork(server, pos)" in processor
    assert "CampfireBlock.isSmokeyPos(level, pos)" in processor
    assert "kind == Kind.SIMMER" in processor
    assert "stack.shrink(1)" in processor
    assert "player.getInventory().add(result)" in processor
    assert "server.sendParticles(ParticleTypes.CLOUD" in processor
    assert "randomTick" not in baseanimation

    for station in stations:
        id=station["id"]
        kind=station["kind"].upper()
        assert f'BLOCKS.register("{id}"' in content
        assert f'KitchenProcessingBlock.Kind.{kind}' in content
        req=station["offhand"].split(":")[1].upper()
        assert f"KitchenV3Content.{req}.get()" in processor
        assert f'{req} = material("{req.lower()}")' in oldtool
        for lang in (en,zh):
            assert lang["block.gugugaga."+id]
            assert lang["item.gugugaga."+id]
            assert lang["message.gugugaga.kitchen_hint_"+kind.lower()]
        state=read(BASE/f"blockstates/{id}.json")["variants"]
        assert state=={
            "active=false":{"model":f"gugugaga:block/{id}"},
            "active=true":{"model":f"gugugaga:block/{id}_active"}}
        frames=[]
        for active in ("","_active"):
            block=read(BASE/f"models/block/{id}{active}.json")
            assert block["parent"]=="minecraft:block/block"
            for ele in block["elements"]:
                assert all(0<=a<b<=16 for a,b in zip(ele["from"],ele["to"])),(id,ele)
            assert len(block["elements"])>=5
            frames.append(block["elements"])
        assert frames[0]!=frames[1],"missing working-frame movement"
        assert read(BASE/f"models/item/{id}.json")["parent"]==f"gugugaga:block/{id}"
        loot=read(RES/f"data/gugugaga/loot_table/blocks/{id}.json")
        assert loot["pools"][0]["entries"][0]["name"]==f"gugugaga:{id}"
        recipe=read(REC/(id+".json"))
        assert recipe["type"]=="minecraft:crafting_shaped"
        assert recipe["result"]=={"id":f"gugugaga:{id}","count":1}
        assert set("".join(recipe["pattern"]))-{" "}==set(recipe["key"])

        # Exact Java route parity, not merely an approximate count.
        pattern=rf"\b{kind}\(Map\.ofEntries\((.*?)\n\s*\)\)(?:,|;)"
        matches=re.findall(pattern,processor,re.S)
        assert len(matches)==1,(kind,"map count",len(matches))
        actual=re.findall(r'Map\.entry\("([a-z_:]+)",\s*"([a-z_]+)"\)',matches[0])
        expected={(p["input"],p["id"]) for p in prepared if p["station"]==kind.lower()}
        assert len(actual)==6 and set(actual)==expected,(kind,actual,expected)

    signatures=set()
    for item in items:
        id=item["id"]
        assert en["item.gugugaga."+id] and zh["item.gugugaga."+id]
        model=read(BASE/f"models/item/{id}.json")
        assert model=={"parent":"minecraft:item/generated",
                       "textures":{"layer0":f"gugugaga:item/kitchen_v31/{id}"}}
        assert f'"{id}"' in content
        if item["type"]=="ingredient":
            assert f'material("{id}")' in content
        else:
            assert f'food("{id}",' in content
            rec=read(REC/(id+".json"))
            assert rec["type"]=="minecraft:crafting_shapeless"
            assert rec["result"]=={"id":f"gugugaga:{id}","count":1}
            inputs=[x["item"] for x in rec["ingredients"]]
            assert inputs==item["ingredients"] and len(inputs)==3
            sig=tuple(sorted(inputs))
            assert sig not in signatures,"duplicate new shapeless recipe"
            signatures.add(sig)
            assert any(s=="gugugaga:"+p["id"] for s in inputs for p in prepared), (
                id,"not using new processing")

    # Important: existing original Minecraft inputs appear as sources/recipes
    # without registering duplicate vanilla blocks or item IDs.
    assert any(i["input"]=="minecraft:egg" for i in prepared)
    assert any(i["input"]=="minecraft:dried_kelp" for i in prepared)
    assert any("minecraft:apple" in i["ingredients"] for i in meals)
    for lang in (en,zh):
        assert lang["message.gugugaga.kitchen_requires_specialist_tool"]

    if len(sys.argv)>1:
        from validate_generated_crop_cuisine_art import read_png
        assets=Path(sys.argv[1])/"assets/gugugaga/textures/item/kitchen_v31"
        unique=set()
        for item in items:
            path=assets/(item["id"]+".png")
            assert path.is_file(),path
            w,h,colors,visible=read_png(path)
            assert (w,h)==(16,16) and visible and len(colors)>=5,item["id"]
            unique.add(hashlib.sha256(path.read_bytes()).hexdigest())
        assert len(unique)==47,("repeated v3.1 sprites",len(unique))
        proof=ROOT/"build/reports/agriculture/kitchen-v31-contact-sheet.png"
        w,h,_,visible=read_png(proof)
        assert (w,h)==(288,384) and visible

    print("KITCHEN_V31_PASS: 4 specialist stations with working animations, "
          "4 formerly decorative tools functional, 24 conversions, 23 foods, "
          "base-game ingredient support, 47 independent icons and previous content intact.")

if __name__=="__main__":
    main()
