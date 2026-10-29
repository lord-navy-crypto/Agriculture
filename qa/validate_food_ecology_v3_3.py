#!/usr/bin/env python3
"""v3.3 food ecology: eight growable crops, eight ocean catches and time-gated cultures."""
from __future__ import annotations
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
ASSETS=RES/"assets/gugugaga"
DATA=RES/"data/gugugaga"
REC=DATA/"recipe"
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
CAT=DATA/"agronomy/food_ecology_v3_3.json"

def read(path):
    return json.loads(path.read_text("utf-8"))

def main():
    m=read(CAT)
    assert m["schema_version"]=="3.3"
    groups=("crops","seafood","spices","fermentations","smoked_seafood","dishes")
    assert [len(m[k]) for k in groups]==[8,8,8,10,8,18]
    sections=[x for g in groups for x in m[g]]
    assert len(sections)==60 and len({r["id"] for r in sections})==60
    ids=set(r["id"] for r in sections)
    newer=ids|{c["id"]+"_seeds" for c in m["crops"]}
    assert len(newer)==68
    original=[read(DATA/"agronomy"/name) for name in (
        "kitchen_expansion_v2_8.json","kitchen_tools_v3_0.json",
        "kitchen_specialists_v3_1.json","kitchen_tool_modes_v3_2.json")]
    oldids={i["id"] for batch in original for i in batch["items"]}
    assert newer.isdisjoint(oldids),"existing kitchen item overwritten"
    oldcrops=set(re.findall(r'crop\("([a-z_]+)_crop"',(JAVA/"agriculture/AgricultureContent.java").read_text("utf-8")))
    assert not {c["id"] for c in m["crops"]}&oldcrops

    source=(JAVA/"agriculture/FoodEcologyV33Content.java").read_text("utf-8")
    climate=(JAVA/"agriculture/CropClimate.java").read_text("utf-8")
    new_climate=(JAVA/"agriculture/FoodEcologyV33Climate.java").read_text("utf-8")
    assert "FoodEcologyV33Climate.allowGrowth(seedName, level, pos, random)" in climate
    vat=(JAVA/"agriculture/FoodFermentationVatBlock.java").read_text("utf-8")
    fish=(JAVA/"agriculture/FoodEcologyFishing.java").read_text("utf-8")
    modes=(JAVA/"agriculture/FoodEcologyV33Modes.java").read_text("utf-8")
    kitchen=(JAVA/"agriculture/KitchenProcessingBlock.java").read_text("utf-8")
    mod=(JAVA/"GuguGagaMod.java").read_text("utf-8")
    tabs=(JAVA/"GuguGagaCreativeTabs.java").read_text("utf-8")
    en=read(ASSETS/"lang/en_us.json")
    zh=read(ASSETS/"lang/zh_cn.json")

    assert "FoodEcologyV33Content.BLOCKS.register(modBus)" in mod
    assert "FoodEcologyV33Content.ITEMS.register(modBus)" in mod
    assert "NeoForge.EVENT_BUS.register(FoodEcologyFishing.class)" in mod
    assert "@SubscribeEvent" in fish
    assert "FoodEcologyV33Content.ITEMS.getEntries()" in tabs
    assert "FoodEcologyV33Modes.resolve(kind, player.getOffhandItem(), input)" in kitchen
    assert "CampfireBlock.isSmokeyPos(level, pos)" in kitchen

    for c in m["crops"]:
        id=c["id"]
        assert f'"{id}_crop", "{id}_seeds"' in source
        assert f'"{id}_seeds", {id.upper()}_CROP' in source
        assert f'food("{id}",' in source
        assert f'"{id}_seeds"' in new_climate
        for lang in (en,zh):
            assert lang[f"item.gugugaga.{id}"]
            assert lang[f"item.gugugaga.{id}_seeds"]
            assert lang[f"block.gugugaga.{id}_crop"]
        states=read(ASSETS/f"blockstates/{id}_crop.json")["variants"]
        assert len(states)==16
        for age in range(8):
            for flag in ("false","true"):
                assert states[f"age={age},pollinated={flag}"]=={
                    "model":f"gugugaga:block/food_ecology/{id}_stage{age}"}
        loot=read(DATA/f"loot_table/blocks/{id}_crop.json")
        assert loot["pools"][0]["entries"][0]["name"]==f"gugugaga:{id}_seeds"
        assert loot["pools"][1]["entries"][0]["name"]==f"gugugaga:{id}"
        assert loot["pools"][1]["entries"][0]["conditions"][0]["properties"]["age"]=="7"
        recipe=read(REC/(id+"_seeds.json"))
        assert recipe["result"]=={"id":f"gugugaga:{id}_seeds","count":2}
        assert [x["item"] for x in recipe["ingredients"]]==[c["starter"],"minecraft:wheat_seeds"]
        for suffix in (id,id+"_seeds"):
            model=read(ASSETS/f"models/item/{suffix}.json")
            assert model=={"parent":"minecraft:item/generated",
                           "textures":{"layer0":f"gugugaga:item/food_ecology/{suffix}"}}

    # No alcohol recipes. The vat waits 3 scheduled updates, stores the
    # batch in block state, and only yields an item when mature.
    assert "IntegerProperty.create(\"batch\", 0, 10)" in vat
    assert "IntegerProperty.create(\"stage\", 0, 3)" in vat
    assert "TICKS_PER_STAGE = 80" in vat
    assert "level.scheduleTick(pos, this, TICKS_PER_STAGE)" in vat
    assert "state.getValue(STAGE) >= 3" in vat
    assert "stage < 3" in vat
    assert "getInventory().add(produced)" in vat
    assert "state.setValue(BATCH, 0).setValue(STAGE, 0)" in vat
    assert "new ItemStack(product)" in vat
    assert "Block.UPDATE_CLIENTS" in vat
    assert "instabuild" in vat
    expected={(x["input"],i+1) for i,x in enumerate(m["fermentations"])}
    actual={(a,int(b)) for a,b in re.findall(r'Map\.entry\("([a-z_:]+)",\s*(\d+)\)',vat)}
    assert expected==actual
    assert "ethanol" not in vat.lower() and "alcohol" not in "".join(
        x["id"] for x in m["fermentations"]).lower()
    for lang in (en,zh):
        for key in ("v33_ferment_started","v33_ferment_hint",
                    "v33_ferment_progress","v33_ferment_ready"):
            assert lang["message.gugugaga."+key]

    variants=read(ASSETS/"blockstates/fermentation_vat.json")["variants"]
    assert len(variants)==44
    for batch in range(11):
        for stage in range(4):
            assert variants[f"batch={batch},stage={stage}"]=={
                "model":f"gugugaga:block/fermentation_vat_stage{0 if batch==0 else stage}"}
    for stage in range(4):
        b=read(ASSETS/f"models/block/fermentation_vat_stage{stage}.json")
        assert b["parent"]=="minecraft:block/cube_bottom_top"
    vatloot=read(DATA/"loot_table/blocks/fermentation_vat.json")
    assert len(vatloot["pools"])==21,"vat must return input or finished item when broken"
    assert vatloot["pools"][0]["entries"][0]["name"]=="gugugaga:fermentation_vat"
    for i,ferment in enumerate(m["fermentations"]):
        finished=vatloot["pools"][1+2*i]["entries"][0]
        raw=vatloot["pools"][2+2*i]["entries"][0]
        assert finished["name"]==f'gugugaga:{ferment["id"]}'
        assert raw["name"]==ferment["input"]
        assert any(condition["condition"]=="minecraft:inverted" for condition in raw["conditions"])

    # Ocean catches extend, never replace, the existing Minecraft fishing
    # rewards. The event does not mutate NeoForge's getDrops list.
    assert "BiomeTags.IS_OCEAN" in fish
    assert "getHookEntity().blockPosition()" in fish
    assert "getDrops().stream().noneMatch" in fish
    assert "server.random.nextInt(5) != 0" in fish
    assert "getInventory().add(catchStack)" in fish
    assert "getDrops().add" not in fish
    for x in m["seafood"]:
        assert f'"{x["id"]}"' in fish
        assert f'material("{x["id"]}")' in source

    spice_expected={(x["source"],x["id"]) for x in m["spices"]}
    smoke_expected={(x["input"],x["id"]) for x in m["smoked_seafood"]}
    for tag,expect in (("SPICES",spice_expected),("SMOKED_FISH",smoke_expected)):
        piece=modes.split(tag+" = Map.ofEntries(",1)[1].split("\n    );",1)[0]
        pairs=set(re.findall(r'Map\.entry\("([a-z_:]+)",\s*"([a-z_]+)"\)',piece))
        assert pairs==expect,(tag,pairs,expect)
    assert "KitchenV3Content.MORTAR_PESTLE.get()" in modes
    assert "KitchenV3Content.KITCHEN_TONGS.get()" in modes

    for x in sections:
        assert en[f'item.gugugaga.{x["id"]}'] and zh[f'item.gugugaga.{x["id"]}']
        assert f'"{x["id"]}"' in source
        item_model=read(ASSETS/f'models/item/{x["id"]}.json')
        assert item_model["textures"]["layer0"]==f'gugugaga:item/food_ecology/{x["id"]}'
    signatures=set()
    for x in m["dishes"]:
        recipe=read(REC/f'{x["id"]}.json')
        assert recipe["type"]=="minecraft:crafting_shapeless"
        assert [k["item"] for k in recipe["ingredients"]]==x["ingredients"]
        assert recipe["result"]=={"id":f'gugugaga:{x["id"]}',"count":1}
        sig=tuple(sorted(x["ingredients"]))
        assert sig not in signatures
        signatures.add(sig)
        assert any(k=="gugugaga:"+v["id"] for k in x["ingredients"]
                   for v in m["fermentations"]+m["spices"]+m["smoked_seafood"])

    if len(sys.argv)>1:
        from validate_generated_crop_cuisine_art import read_png
        folder=Path(sys.argv[1])
        unique=set()
        names=list(newer)
        for id in names:
            texture=folder/f"assets/gugugaga/textures/item/food_ecology/{id}.png"
            assert texture.is_file(),texture
            w,h,colors,visible=read_png(texture)
            assert (w,h)==(16,16) and len(colors)>=5 and visible,id
            unique.add(hashlib.sha256(texture.read_bytes()).hexdigest())
        assert len(unique)==68,"same crop/ocean/fermentation item icon used twice"
        for c in m["crops"]:
            for age in range(8):
                stem=folder/f'assets/gugugaga/textures/block/food_ecology/{c["id"]}_stage{age}.png'
                model=folder/f'assets/gugugaga/models/block/food_ecology/{c["id"]}_stage{age}.json'
                assert stem.is_file() and model.is_file(),(stem,model)
                w,h,colors,visible=read_png(stem)
                assert (w,h)==(16,16) and visible
                assert read(model)["textures"]["cross"]==f'gugugaga:block/food_ecology/{c["id"]}_stage{age}'
        sheet=ROOT/"build/reports/agriculture/food-ecology-v33-contact-sheet.png"
        w,h,_,visible=read_png(sheet)
        assert (w,h)==(288,576) and visible

    print("FOOD_ECOLOGY_V33_OK: 8 plantable spice crops, 8 marine fishing species, "
          "10 time-gated nonalcoholic food fermentations, 8 seasoning routes, "
          "8 seafood smoking routes, 18 new dishes, old crops and fishing preserved")

if __name__=="__main__":
    main()
