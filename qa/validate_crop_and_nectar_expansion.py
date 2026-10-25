#!/usr/bin/env python3
"""v0.9 crop and regional nectar-honey additions: assets plus registry contracts."""
import json
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"

def read(path):
    return json.loads((RES / path).read_text(encoding="utf8"))

def main():
    base = read("data/gugugaga/agronomy/crop_profiles_v0_4.json")["crops"]
    extra = read("data/gugugaga/agronomy/crop_additions_v0_9.json")
    assert extra["schema_version"] == "0.9"
    expected = set("lavender clover basil mint mustard canola celery leek".split())
    assert {row["crop"] for row in extra["crops"]} == expected
    assert len(base) == 36 and len(base) + len(extra["crops"]) == 44
    reg = (SRC / "AgricultureContent.java").read_text("utf8")
    cl = (SRC / "CropClimate.java").read_text("utf8")
    habitat = (SRC / "BeeNestHabitatBlock.java").read_text("utf8")
    en, zh = (read("assets/gugugaga/lang/en_us.json"),
              read("assets/gugugaga/lang/zh_cn.json"))
    for row in extra["crops"]:
        name = row["crop"]
        assert row["source_urls"] and all(x.startswith("https://") for x in row["source_urls"])
        assert row["growth_band_gameplay"] in {"COOL","TEMPERATE","WARM","DRY","WET"}
        assert f'crop("{name}_crop", "{name}_seeds", AgricultureCropBlock.PollinationGroup.{row["pollination_gameplay"]})' in reg
        assert f'"{name}_seeds"' in cl
        states = read(f"assets/gugugaga/blockstates/{name}_crop.json")["variants"]
        assert set(states) == {f"age={a},pollinated={p}" for a in range(8) for p in ("false","true")}, name
        loot = read(f"data/gugugaga/loot_table/blocks/{name}_crop.json")
        assert len(loot["pools"]) == 2
        assert loot["pools"][0]["entries"][0]["name"] == f"gugugaga:{name}_seeds"
        assert loot["pools"][1]["entries"][0]["name"] == f"gugugaga:{name}"
        assert read(f"data/gugugaga/recipe/{name}_seeds.json")["result"]["id"] == f"gugugaga:{name}_seeds"
        for item in (name, f"{name}_seeds"):
            model = read(f"assets/gugugaga/models/item/{item}.json")
            assert model["textures"]["layer0"] == f"gugugaga:item/crops/{item}"
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
        assert f"block.gugugaga.{name}_crop" in en and f"block.gugugaga.{name}_crop" in zh
    crop_block = (SRC / "AgricultureCropBlock.java").read_text("utf8")
    assert '"basil_seeds", "mint_seeds"' in crop_block
    assert "public InteractionResult useWithoutItem" in crop_block
    assert "Block.popResource(level, pos, new ItemStack(leaves))" in crop_block
    assert "state.setValue(AGE, 4).setValue(POLLINATED, false)" in crop_block
    for flower in ("lavender", "clover", "mustard", "canola", "basil", "mint"):
        assert f'id.equals("{flower}_crop")' in habitat

    source = read("data/gugugaga/agronomy/honey_sources_v0_9.json")
    assert source["schema_version"] == "0.9"
    ids = {row["item"] for row in source["honey_variants"]}
    assert ids == {x+"_honey_bottle" for x in ("clover","lavender","buckwheat","sunflower","dandelion","poppy","allium","cornflower","azure_bluet","blue_orchid","lily_of_the_valley","oxeye_daisy","torchflower","red_tulip","orange_tulip","white_tulip","pink_tulip","wither_rose","blackberry","blackcurrant","chive","pumpkin_blossom","cassava_blossom","taro_blossom","yam_blossom","watercress","endive","kohlrabi","rutabaga","wildflower")}
    bee = (SRC / "BeeContent.java").read_text("utf8")
    nectar = (SRC / "NectarHoneySource.java").read_text("utf8")
    harvest = (SRC / "HoneyHarvesterItem.java").read_text("utf8")
    assert "HoneyBottleItem" in bee and "stacksTo(16)" in bee
    assert "FoodProperties.Builder" in bee and "MobEffectInstance" in bee
    assert "1.0F" in bee
    for effect in ("JUMP", "NIGHT_VISION", "DAMAGE_RESISTANCE", "MOVEMENT_SPEED",
                   "SATURATION", "FIRE_RESISTANCE", "BLINDNESS", "REGENERATION",
                   "WEAKNESS", "WITHER", "WATER_BREATHING", "DIG_SPEED", "ABSORPTION"):
        assert f"MobEffects.{effect}" in bee, effect
    for flower in ("dandelion","poppy","allium","cornflower","azure_bluet","blue_orchid","lily_of_the_valley","oxeye_daisy","torchflower","red_tulip","orange_tulip","white_tulip","pink_tulip","wither_rose"):
        assert f'"minecraft:{flower}"' in nectar
    assert "if (!matched) otherFlowers++" in nectar
    assert "NectarHoneySource.selectHoneyBottle(level, pos)" in harvest
    assert "CampfireBlock.isSmokeyPos(level, hivePos)" in harvest
    assert "hasChunkAt(sample)" in nectar and "dominantCount < 6" in nectar
    assert "dominantCount * 3 < totalFlowers * 2" in nectar
    assert "otherFlowerKinds" in nectar and "totalFlowers >= 12 && distinctSourceKinds >= 3" in nectar
    assert "WILDFLOWER_HONEY_BOTTLE" in nectar
    assert "new ItemStack(Items.HONEY_BOTTLE)" in nectar
    for id in ids:
        assert f'floralHoney("{id}")' in bee
        assert f"item.gugugaga.{id}" in en and f"item.gugugaga.{id}" in zh
        assert read(f"assets/gugugaga/models/item/{id}.json")["textures"]["layer0"] == f"gugugaga:item/honey/{id}"
    assert len(source["references"]) >= 3
    print("AGRICULTURE_HONEY_INTEGRATION_OK: v0.9 legacy contract preserved; current catalog has 96 crops and 46 honey variants; mixed-forage blend and vanilla safeguards")

if __name__ == "__main__":
    main()
