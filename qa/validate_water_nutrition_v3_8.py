#!/usr/bin/env python3
"""v3.8 source, registry, item NBT, evaporation, storage and packaged-JAR contracts.
Does not claim completed visual or interactive client acceptance.
"""
from pathlib import Path
import json,sys,zipfile

ROOT=Path(__file__).resolve().parents[1]
SRC=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga"
ASSETS=ROOT/"src/main/resources/assets/gugugaga"
DATA=ROOT/"src/main/resources/data/gugugaga"
def java(name):
    return (SRC/"agriculture"/(name+".java")).read_text(encoding="utf-8")
def j(path):
    return json.loads(path.read_text(encoding="utf-8"))
def main():
    water=java("WaterEcologyV38")
    bucket=java("FieldSamplingBucketV38")
    pan=java("SaltEvaporationPanV38")
    reg=java("WaterV38Content")
    nutrition=java("NutritionV34")
    mod=(SRC/"GuguGagaMod.java").read_text("utf-8")
    tabs=(SRC/"GuguGagaCreativeTabs.java").read_text("utf-8")
    assert "BiomeTags.IS_OCEAN" in water
    assert 'id.equals("beach")' in water
    assert 'return 35;' in water and 'return 0;' in water
    assert "DataComponents.CUSTOM_DATA" in water and "CustomData.of(tag)" in water
    assert 'tag.putInt(SALINITY,salinityAt(level,pos))' in water
    assert "VOLUME_MB=1000" in water
    assert "getPlayerPOVHitResult" in bucket and "FluidTags.WATER" in bucket
    assert "isSource()" in bucket and "held.shrink(1)" in bucket
    assert "getInventory().add(collected)" in bucket
    assert "appendHoverText" in bucket and "tooltip.gugugaga.v38_water_salinity" in bucket
    assert 'register("salt_evaporation_pan"' in reg
    assert 'register("field_sampling_bucket"' in reg
    assert 'register("sampled_water_bucket"' in reg
    assert "WaterV38Content.BLOCKS.register(modBus)" in mod
    assert "WaterV38Content.ITEMS.register(modBus)" in mod
    assert "WaterV38Content.ITEMS.getEntries()" in tabs
    assert "IntegerProperty.create(\"stage\",0,3)" in pan
    assert "WaterEcologyV38.valid(stack)" in pan
    assert "salinity<5" in pan
    assert "stack.shrink(1)" in pan
    assert "FoodV34Content.SEA_SALT.get()" in pan
    assert "stage+1<3" in pan and "scheduleTick" in pan
    assert "level.isRainingAt" in pan and "level.canSeeSky" in pan
    assert "CampfireBlock.LIT" in pan
    assert "LunarSeasonsV36.habitat" in pan
    assert "LunarSeasonsV36.localSeason" in pan and "Season.WINTER" in pan
    assert "NutritionPhysiologyV39.advance" in nutrition
    assert "NutritionPhysiologyV39.consume" in nutrition
    assert "NutritionFeedbackV38.apply" in nutrition
    assert "NutritionFeedbackV38.snapshot" in nutrition
    assert "player.hurt" not in nutrition and "player.kill" not in nutrition
    assert "gugugaga_nutrition_v35" in nutrition
    for i in range(4):
        model=j(ASSETS/f"models/block/salt_evaporation_pan_stage_{i}.json")
        assert model["parent"]=="minecraft:block/block" and len(model["elements"])>=5
    states=j(ASSETS/"blockstates/salt_evaporation_pan.json")["variants"]
    assert len(states)==4 and set(states)=={f"stage={n}" for n in range(4)}
    for name in ("field_sampling_bucket","sampled_water_bucket","salt_evaporation_pan"):
        assert j(ASSETS/f"models/item/{name}.json")
    for name in ("field_sampling_bucket","salt_evaporation_pan"):
        recipe=j(DATA/f"recipe/{name}.json")
        assert recipe["result"]["id"]=="gugugaga:"+name
    loot=j(DATA/"loot_table/blocks/salt_evaporation_pan.json")
    assert loot["pools"][0]["entries"][0]["name"]=="gugugaga:salt_evaporation_pan"
    for lang in ("en_us","zh_cn"):
        keys=j(ASSETS/f"lang/{lang}.json")
        for key in ("item.gugugaga.field_sampling_bucket",
                    "item.gugugaga.sampled_water_bucket",
                    "block.gugugaga.salt_evaporation_pan",
                    "tooltip.gugugaga.v38_water_salinity",
                    "message.gugugaga.v38_pan_needs_saltwater"):
            assert keys.get(key),(lang,key)
    if len(sys.argv)>1:
        jar=Path(sys.argv[1])
        assert jar.is_file(),jar
        with zipfile.ZipFile(jar) as z:
            assert z.testzip() is None
            for name in ("WaterEcologyV38","FieldSamplingBucketV38",
                         "SaltEvaporationPanV38","WaterV38Content"):
                assert f"io/github/lordnavycrypto/gugugaga/agriculture/{name}.class" in z.namelist()
            for item in ("field_sampling_bucket","sampled_water_bucket","salt_evaporation_pan"):
                assert f"assets/gugugaga/models/item/{item}.json" in z.namelist()
            assert "data/gugugaga/loot_table/blocks/salt_evaporation_pan.json" in z.namelist()
    print("WATER_NUTRITION_V38_OK: biome salinity, persistent water bucket, " +
          "solar/heated salt pan, stable food metrics, long-term recovery contracts")
if __name__=="__main__":
    main()
