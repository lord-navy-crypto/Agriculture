#!/usr/bin/env python3
"""Structural checks for the v0.4 crop-pollination and honey-collection milestone."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"

CROPS = """
corn tomato onion rice soybean strawberry cucumber blueberry bell_pepper garlic
barley oat rye pea lettuce cabbage spinach broccoli eggplant cotton flax peanut
sorghum sunflower_seed millet buckwheat chickpea lentil radish turnip sweet_potato
kale zucchini raspberry cranberry sesame
""".split()

def read_json(path):
    return json.loads(path.read_text(encoding="utf8"))

def main():
    crop_source = (JAVA / "AgricultureCropBlock.java").read_text(encoding="utf8")
    bee_source = (JAVA / "BeeContent.java").read_text(encoding="utf8")
    bee_tag_source = (JAVA / "BeeEcotypeItem.java").read_text(encoding="utf8")
    harvester_source = (JAVA / "HoneyHarvesterItem.java").read_text(encoding="utf8")
    en = read_json(RES / "assets/gugugaga/lang/en_us.json")
    zh = read_json(RES / "assets/gugugaga/lang/zh_cn.json")

    assert "POLLINATED" in crop_source
    assert "getDrops(BlockState state, LootParams.Builder builder)" in crop_source
    assert "HONEY_HARVESTER" in bee_source
    assert "bee.getType() != EntityType.BEE" in bee_tag_source
    assert "BeehiveBlock.HONEY_LEVEL" in harvester_source
    assert "Items.GLASS_BOTTLE" in harvester_source
    assert "Items.SHEARS" in harvester_source and "Items.HONEYCOMB, 3" in harvester_source
    assert "EquipmentSlot.OFFHAND" in harvester_source and "hurtAndBreak(1, player" in harvester_source
    assert 'combMode ? SoundEvents.BEEHIVE_SHEAR : SoundEvents.BOTTLE_FILL' in harvester_source
    assert "boolean growthAllowed = CropClimate.allowGrowth" in crop_source
    assert "if (state.getValue(AGE) >= getMaxAge())" in crop_source
    assert "bee -> bee.isAlive() && bee.hasNectar()" in crop_source
    assert "if (!bee.hasNectar())" not in crop_source
    assert "BlockPos.MutableBlockPos candidate = new BlockPos.MutableBlockPos()" in (JAVA / "CropClimate.java").read_text(encoding="utf8")
    assert "if (growthAllowed)" in crop_source
    assert "message.gugugaga.honey_harvester_combs_collected" in en
    assert "message.gugugaga.honey_harvester_combs_collected" in zh
    assert "CampfireBlock.isSmokeyPos(level, hivePos)" in harvester_source
    assert "level.hasChunkAt(candidate)" in harvester_source
    assert "message.gugugaga.honey_harvester_collected" in en
    assert "message.gugugaga.honey_harvester_collected" in zh
    assert "item.gugugaga.honey_harvester" in en
    assert "item.gugugaga.honey_harvester" in zh
    assert read_json(RES / "data/gugugaga/recipe/honey_harvester.json")["result"]["id"] == "gugugaga:honey_harvester"
    assert read_json(RES / "assets/gugugaga/models/item/honey_harvester.json")["parent"] == "minecraft:item/shears"

    profiles = read_json(RES / "data/gugugaga/agronomy/crop_profiles_v0_4.json")
    assert profiles["schema_version"] == "0.4"
    assert len(profiles["crops"]) == len(CROPS)
    assert {profile["crop"] for profile in profiles["crops"]} == set(CROPS)
    crop_climate = (JAVA / "CropClimate.java").read_text(encoding="utf8")
    agriculture_content = (JAVA / "AgricultureContent.java").read_text(encoding="utf8")
    allowed_bands = {"COOL", "TEMPERATE", "WARM", "DRY", "WET"}
    allowed_pollination = {"NONE", "ORCHARD", "BERRY"}
    for profile in profiles["crops"]:
        crop = profile["crop"]
        assert profile["growth_band_gameplay"] in allowed_bands, crop
        assert profile["pollination_gameplay"] in allowed_pollination, crop
        assert profile["source_urls"] and all(url.startswith("https://") for url in profile["source_urls"]), crop
        assert f'"{crop}_seeds"' in crop_climate, crop
        assert f'crop("{crop}_crop", "{crop}_seeds", AgricultureCropBlock.PollinationGroup.{profile["pollination_gameplay"]})' in agriculture_content, crop

    for crop in CROPS:
        loot = read_json(RES / f"data/gugugaga/loot_table/blocks/{crop}_crop.json")
        assert len(loot["pools"]) == 2, crop
        states = read_json(RES / f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        assert set(states) == {f"age={age},pollinated={p}" for age in range(8) for p in ("false","true")}, crop

    print(f"POLLINATION_AND_HONEY_OK: {len(CROPS)} reviewed crop profiles + safe honey harvester")
if __name__ == "__main__":
    main()
