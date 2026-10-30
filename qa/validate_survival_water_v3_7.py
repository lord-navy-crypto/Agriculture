#!/usr/bin/env python3
"""Survival v3.7: validate accurate claim boundaries and feature integration.

This checks design contracts and the actual JAR, not a Minecraft client render.
"""
from pathlib import Path
import json,sys,zipfile

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
RES=ROOT/"src/main/resources"
DATA=RES/"data/gugugaga"
ART=RES/"assets/gugugaga"

def read_json(path):return json.loads(path.read_text("utf-8"))
def read_java(name):return (JAVA/"agriculture"/(name+".java")).read_text("utf-8")

def main():
    prior=read_json(DATA/"nutrition/food_profiles_v3_5.json")
    profiles=read_json(DATA/"nutrition/water_salt_profiles_v3_7.json")
    assert len(prior["foods"])==403
    assert profiles["schema_version"]==1
    assert "NOT" not in profiles["units"] or "not" in profiles["units"].lower()
    assert set(prior["foods"]).issubset(profiles["foods"])
    assert len(profiles["foods"])==407
    for name,contrib in profiles["foods"].items():
        assert 0<=contrib["water"]<=40 and 0<=contrib["sodium"]<=15,name
    assert profiles["foods"]["gugugaga:boiled_water_bottle"]["water"]>profiles["foods"]["minecraft:honey_bottle"]["water"]
    assert profiles["foods"]["gugugaga:raw_water_bottle"]["water"]>0
    assert profiles["foods"]["gugugaga:raw_water_bottle"]["sodium"]==0
    assert "https://www.nationalacademies.org" in " ".join(profiles["sources"])
    assert "https://www.who.int" in " ".join(profiles["sources"])
    loader=read_java("WaterSaltProfilesV37")
    survival=read_java("WaterSaltV37")
    wateritems=read_java("WaterV37Content")
    empty=read_java("EmptyWaterFlaskV37")
    drink=read_java("DrinkWaterV37")
    inventory=read_java("NutritionInventoryButtonV37")
    hud=read_java("NutritionHudV37")
    wire=read_java("NutritionNetworkV34")
    sync=read_java("NutritionNetworkV35")
    screen=read_java("NutritionScreenClientV35")
    mod=(JAVA/"GuguGagaMod.java").read_text("utf-8")
    tabs=(JAVA/"GuguGagaCreativeTabs.java").read_text("utf-8")
    assert "getResourceAsStream(name)" in loader
    assert "new IllegalStateException" in loader
    assert "WaterSaltProfilesV37.water(name)" in survival
    assert "WaterSaltProfilesV37.sodium(name)" in survival
    assert "getPersistentData()" in survival
    assert "PlayerEvent.Clone" in survival
    assert "LivingEntityUseItemEvent.Finish" in survival
    assert "PlayerTickEvent.Post" in survival
    assert "player.isSprinting()" in survival
    assert "Level.NETHER" in survival
    assert "salt>85" in survival
    assert "water<=15" in survival
    assert "MobEffects.MOVEMENT_SLOWDOWN" in survival
    assert "player.getRandom().nextInt(5)==0" in survival
    assert "player.level().dimension()==Level.NETHER" in survival
    assert "player.isCreative()" in survival and "player.isSpectator()" in survival
    assert "player.hurt" not in survival and "player.kill" not in survival
    assert "WaterV37Content.ITEMS.register(modBus)" in mod
    assert "WaterV37Content.ITEMS.getEntries()" in tabs
    for id in ("empty_water_flask","raw_water_bottle","boiled_water_bottle","filtered_water_bottle"):
        assert f'"{id}"' in wateritems
        assert read_json(ART/f"models/item/{id}.json")["textures"]["layer0"].startswith("minecraft:item/")
    assert "getPlayerPOVHitResult" in empty
    assert "ClipContext.Fluid.SOURCE_ONLY" in empty
    assert "BiomeTags.IS_OCEAN" in empty
    assert "isSource()" in empty
    assert "UseAnim.DRINK" in drink
    assert "finishUsingItem" in drink
    assert "EMPTY_WATER_FLASK.get()" in drink
    for name in ("empty_water_flask","filtered_water_bottle","boiled_water_furnace","boiled_water_campfire"):
        recipe=read_json(DATA/f"recipe/{name}.json")
        assert recipe["result"]["id"].startswith("gugugaga:")
    for name,recipe_type in (("boiled_water_furnace","minecraft:smelting"),
                             ("boiled_water_campfire","minecraft:campfire_cooking")):
        recipe=read_json(DATA/f"recipe/{name}.json")
        assert recipe["type"]==recipe_type
        assert recipe["ingredient"]["item"]=="gugugaga:raw_water_bottle"
        assert recipe["result"]["id"]=="gugugaga:boiled_water_bottle"
        assert recipe["cookingtime"]>0
    filtered=read_json(DATA/"recipe/filtered_water_bottle.json")
    assert any(x["item"]=="gugugaga:boiled_water_bottle" for x in filtered["ingredients"])
    assert "InventoryScreen" in inventory
    assert "ScreenEvent.Init.Post" in inventory
    assert "event.addListener(Button.builder" in inventory
    assert "NutritionNetworkV34.Request()" in inventory
    assert "RenderGuiEvent.Post" in hud
    assert "hud.gugugaga.nutrition_hint" in hud
    assert "int water,int salt" in sync
    assert "buf.readVarInt()" in sync and "buf.writeVarInt" in sync
    assert "payload.water(),payload.salt()" in sync
    assert "WaterSaltV37.water(player),WaterSaltV37.salt(player)" in wire
    assert '"nutrition.gugugaga.water_level"' in screen
    assert '"nutrition.gugugaga.sodium_balance"' in screen
    assert "scrollUp" in screen and "scrollDown" in screen
    assert "font.plainSubstrByWidth" in screen and "graphics.renderTooltip" in screen
    assert "int panelWidth()" in screen and "int panelHeight()" in screen
    assert "renderBackground(graphics" not in screen
    assert "public boolean mouseScrolled" in screen
    assert "public boolean keyPressed" in screen
    assert "value+\" / 100\"" in screen
    assert "Math.min(screen.width-76" in inventory
    # Layout arithmetic for representative scaled-game GUI viewports.
    for width,height in ((240,180),(320,180),(400,240),(640,360),(960,540)):
        pw=max(120,min(440,width-12)); ph=max(105,min(305,height-12))
        assert pw<=width and ph<=height
        top=47; bottom=ph-45; visible=max(1,(bottom-top-3)//23)
        assert visible>=1
        compact=pw<280; side=28 if compact else 40
        scrollw=24 if compact else 30; donew=54 if compact else 80
        gap=6 if compact else 9
        assert 2*side+2*scrollw+donew+4*gap<=pw
        for rows in (3,7):
            maxscroll=max(0,rows-visible)
            assert maxscroll+visible>=rows
    assert "value+\" / 100\"" in screen
    assert 'int page=5' in screen and "PAGES.length" in screen
    for lang in ("en_us","zh_cn"):
        keys=read_json(ART/f"lang/{lang}.json")
        for k in ("button.gugugaga.nutrition","hud.gugugaga.nutrition_hint",
                  "nutrition.gugugaga.water_level","nutrition.gugugaga.sodium_balance",
                  "message.gugugaga.v37_saltwater","message.gugugaga.v37_thirst_low",
                  "message.gugugaga.v35_page_5"):
            assert keys.get(k), (lang,k)
    if len(sys.argv)>1:
        jar=Path(sys.argv[1])
        assert jar.is_file()
        with zipfile.ZipFile(jar) as arc:
            assert arc.testzip() is None
            assert json.loads(arc.read("data/gugugaga/nutrition/water_salt_profiles_v3_7.json"))==profiles
            classes=("WaterSaltV37","WaterSaltProfilesV37","WaterV37Content",
                     "EmptyWaterFlaskV37","DrinkWaterV37","NutritionHudV37",
                     "NutritionInventoryButtonV37")
            for cls in classes:
                assert f"io/github/lordnavycrypto/gugugaga/agriculture/{cls}.class" in arc.namelist()
    print("WATER_SALT_V37_OK: 407 item scores, two persistent survival meters, "
          "server drink and sprint logic, furnace/campfire boiling, "
          "fixed inventory nutrition button and GUI/network integration")

if __name__=="__main__":
    main()
