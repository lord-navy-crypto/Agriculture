#!/usr/bin/env python3
"""Static and generated-art contracts for v3.4 salt, food aging, ecology and nutrition."""
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
RES=ROOT/"src/main/resources"
DATA=RES/"data/gugugaga"
ART=RES/"assets/gugugaga"
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
M=DATA/"agronomy/salt_fermentation_nutrition_v3_4.json"

def load(path):
    return json.loads(path.read_text("utf-8"))

def main():
    cat=load(M)
    assert cat["schema_version"]=="3.4"
    assert len(cat["items"])==26 and len(cat["aging_recipes"])==10
    assert len(cat["dishes"])==12 and len(cat["wild_farmland_ecology"])==8
    ids={x["id"] for x in cat["items"]}
    assert len(ids)==26
    old=[]
    for file in ("kitchen_expansion_v2_8.json","kitchen_tools_v3_0.json",
                 "kitchen_specialists_v3_1.json","kitchen_tool_modes_v3_2.json"):
        old.extend(load(DATA/"agronomy"/file)["items"])
    old.extend(load(DATA/"agronomy/food_ecology_v3_3.json")["crops"])
    assert ids.isdisjoint({x["id"] for x in old})
    src=(JAVA/"agriculture/FoodV34Content.java").read_text("utf8")
    crock=(JAVA/"agriculture/SeasoningAgingCrockBlock.java").read_text("utf8")
    nut=(JAVA/"agriculture/NutritionV34.java").read_text("utf8")
    net=(JAVA/"agriculture/NutritionNetworkV34.java").read_text("utf8")
    key=(JAVA/"agriculture/NutritionKeyV34.java").read_text("utf8")
    tick=(JAVA/"agriculture/NutritionKeyTickV34.java").read_text("utf8")
    mod=(JAVA/"GuguGagaMod.java").read_text("utf8")
    tabs=(JAVA/"GuguGagaCreativeTabs.java").read_text("utf8")
    en=load(ART/"lang/en_us.json")
    zh=load(ART/"lang/zh_cn.json")
    assert "FoodV34Content.BLOCKS.register(modBus)" in mod
    assert "FoodV34Content.ITEMS.register(modBus)" in mod
    assert "FoodV34Content.ITEMS.getEntries()" in tabs
    assert mod.count("addListener(")==2,"old event contract cannot be replaced"
    for x in cat["items"]:
        id=x["id"]
        assert f'"{id}"' in src
        assert en[f"item.gugugaga.{id}"] and zh[f"item.gugugaga.{id}"]
        assert load(ART/f"models/item/{id}.json")=={
            "parent":"minecraft:item/generated",
            "textures":{"layer0":f"gugugaga:item/seasonings_v34/{id}"}}
    for id in ("salt_deposit","salt_block","seasoning_crock"):
        assert f'"{id}"' in src
        assert en[f"block.gugugaga.{id}"]
        assert zh[f"block.gugugaga.{id}"]
        assert load(ART/f"models/item/{id}.json")["parent"].startswith("gugugaga:block/")
        assert load(DATA/f"loot_table/blocks/{id}.json")["type"]=="minecraft:block"
    assert "IntegerProperty.create(\"batch\", 0, 10)" in crock
    assert "IntegerProperty.create(\"stage\", 0, 3)" in crock
    assert "STAGE_TICKS = 100" in crock
    assert "offhand.shrink(1)" in crock
    assert "main.shrink(1)" in crock
    assert "level.scheduleTick(pos, this, STAGE_TICKS)" in crock
    assert "getInventory().add(produced)" in crock
    assert "state.setValue(BATCH, 0).setValue(STAGE, 0)" in crock
    assert "boolean needsSalt = recipe >= 5 && recipe <= 8" in crock
    assert "FoodV34Content.SEA_SALT.get()" in crock
    assert "FoodV34Content.STARTER_CULTURE.get()" in crock
    assert all(recipe["duration_stages"]==3 for recipe in cat["aging_recipes"])
    expected={(x["source"],idx+1) for idx,x in enumerate(cat["aging_recipes"])}
    actual={(a,int(b)) for a,b in re.findall(
        r'Map\.entry\("([a-z_:]+)",\s*(\d+)\)',crock)}
    assert expected==actual
    v=load(ART/"blockstates/seasoning_crock.json")["variants"]
    assert len(v)==44
    loot=load(DATA/"loot_table/blocks/seasoning_crock.json")
    assert len(loot["pools"])==21
    for i,x in enumerate(cat["aging_recipes"]):
        mature=loot["pools"][1+2*i]["entries"][0]
        unfinished=loot["pools"][2+2*i]["entries"][0]
        assert mature["name"]==f'gugugaga:{x["output"]}'
        assert unfinished["name"]==x["source"]
        assert any(k["condition"]=="minecraft:inverted" for k in unfinished["conditions"])
    for id in ("salt_rock_vein","salt_beach_crust"):
        feature=load(DATA/f"worldgen/configured_feature/{id}.json")
        placed=load(DATA/f"worldgen/placed_feature/{id}.json")
        biome=load(DATA/f"neoforge/biome_modifier/{id}.json")
        assert feature["type"]=="minecraft:ore"
        assert feature["config"]["targets"][0]["state"]["Name"]=="gugugaga:salt_deposit"
        assert "height_range" in [x["type"].split(":")[1] for x in placed["placement"]]
        assert biome["step"]=="underground_ores"
    for c in cat["wild_farmland_ecology"]:
        id="wild_farmland_"+c["crop"]
        feature=load(DATA/f"worldgen/configured_feature/{id}.json")
        placed=load(DATA/f"worldgen/placed_feature/{id}.json")
        biome=load(DATA/f"neoforge/biome_modifier/{id}.json")
        assert feature["type"]=="minecraft:block_column"
        conf=feature["config"]
        assert conf["direction"]=="down"
        assert conf["layers"][0]["provider"]["state"]["Name"]==f'gugugaga:{c["crop"]}_crop'
        assert conf["layers"][1]["provider"]["state"]["Name"]=="minecraft:farmland"
        assert len(biome["biomes"])>=2
        assert biome["biomes"]==c["biomes"]
        assert biome["step"]=="vegetal_decoration"
        assert placed["feature"]=="gugugaga:"+id
    for id,ingredients in cat["dishes"].items():
        r=load(DATA/f"recipe/{id}.json")
        assert r["type"]=="minecraft:crafting_shapeless"
        assert r["result"]=={"id":f"gugugaga:{id}","count":1}
        assert [x["item"] for x in r["ingredients"]]==ingredients
        assert any(k=="gugugaga:"+recipe["output"] for k in ingredients
                   for recipe in cat["aging_recipes"]) or "gugugaga:sea_salt" in ingredients
    assert "getPersistentData()" in nut
    assert "LivingEntityUseItemEvent.Finish" in nut
    assert "PlayerEvent.Clone" in nut
    assert "DataComponents.FOOD" in nut
    assert "MobEffects.DIG_SPEED" in nut
    assert "player.sendSystemMessage" in nut
    assert "minimum_groups_for_bonus" in json.dumps(cat["nutrition"])
    assert "StreamCodec.unit(new Request())" in net
    assert "playToServer" in net
    assert "NutritionV34.show(player)" in net
    assert "GLFW.GLFW_KEY_N" in key
    assert "RegisterKeyMappingsEvent" in key
    assert "ClientTickEvent.Post" in tick
    assert "PacketDistributor.sendToServer" in tick
    assert all("nutrition.gugugaga."+group in en and
               "nutrition.gugugaga."+group in zh
               for group in cat["nutrition"]["dimensions"])
    if len(sys.argv)>1:
        from validate_generated_crop_cuisine_art import read_png
        root=Path(sys.argv[1])/"assets/gugugaga/textures/item/seasonings_v34"
        hashes=set()
        for x in cat["items"]:
            f=root/(x["id"]+".png")
            assert f.is_file(),f
            w,h,colors,visible=read_png(f)
            assert (w,h)==(16,16) and len(colors)>=5 and visible,x["id"]
            hashes.add(hashlib.sha256(f.read_bytes()).hexdigest())
        assert len(hashes)==26
        sheet=ROOT/"build/reports/agriculture/seasonings-v34-contact-sheet.png"
        w,h,_,visible=read_png(sheet)
        assert (w,h)==(288,240) and visible
    print("SEASONINGS_V34_OK: two salt biomes, eight wild farmland features, "
          "ten age-gated condiments, twelve foods, six persistent nutrition groups, "
          "server-bound N key and prior mod compatibility intact.")

if __name__=="__main__":
    main()
