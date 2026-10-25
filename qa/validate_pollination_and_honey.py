#!/usr/bin/env python3
"""Source-informed structural QA for 36 crops, pollination and bee habitat resources."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"

def j(path):
    return json.loads((RES / path).read_text(encoding="utf8"))

def main():
    profiles = j("data/gugugaga/agronomy/crop_profiles_v0_4.json")
    crops = profiles["crops"]
    assert len(crops) == 36
    assert len({p["crop"] for p in crops}) == 36

    registry = (JAVA / "AgricultureContent.java").read_text(encoding="utf8")
    crop_java = (JAVA / "AgricultureCropBlock.java").read_text(encoding="utf8")
    special_java = (JAVA / "PollinatedAgricultureCropBlock.java").read_text(encoding="utf8")
    tracked = set()
    for item in crops:
        crop, group = item["crop"], item["pollination_group"]
        signature = 'crop("' + crop + '_crop", "' + crop + '_seeds", AgricultureCropBlock.PollinationGroup.' + group + ')'
        assert signature in registry, crop
        assert item["harvested_part"] and item["growth_band_gameplay"] and item["source_urls"]
        states = j(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        loot = j(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")["pools"]
        if group == "NONE":
            expected = {f"age={age}" for age in range(8)}
            assert len(loot) == 2, crop
        else:
            tracked.add(crop)
            expected = {f"age={age},pollinated={flag}" for age in range(8) for flag in ("false", "true")}
            assert len(loot) == 3, crop
            reward = loot[2]["entries"][0]
            assert reward["name"] == "gugugaga:" + crop, crop
            assert reward["conditions"][0]["properties"] == {"age":"7","pollinated":"true"}, crop
        assert set(states) == expected, crop
    assert len(tracked) == 12, tracked
    assert "markPollinated" in crop_java and "getEntitiesOfClass" in crop_java
    assert "BooleanProperty.create(\"pollinated\")" in special_java

    species = j("data/gugugaga/agronomy/bee_species_profiles_v0_4.json")["species"]
    assert {"bumblebee","mason_bee","squash_bee","leafcutter_bee","carpenter_bee","mining_bee"} <= {s["id"] for s in species}
    for entry in species:
        if entry["id"] in ("mason_bee","squash_bee","leafcutter_bee"):
            assert entry["honeycomb_producer"] is False
    for name in ("bumblebee_nest","mason_bee_house","squash_bee_burrow","leafcutter_bee_house"):
        states = j(f"assets/gugugaga/blockstates/{name}.json")["variants"]
        assert set(states) == {f"stage={age}" for age in range(4)}
        j(f"assets/gugugaga/models/item/{name}.json")
        j(f"data/gugugaga/loot_table/blocks/{name}.json")
        j(f"data/gugugaga/recipe/{name}.json")
    for item in ("bee_keeper_brush","bumblebee_nectar","mason_bee_cocoon","squash_bee_cocoon","leafcutter_bee_cocoon"):
        j(f"assets/gugugaga/models/item/{item}.json")
    j("data/gugugaga/recipe/bee_keeper_brush.json")
    for file in ("BeeHabitatBlock.java","BeeKeeperBrushItem.java","HoneyHarvesterItem.java"):
        assert (JAVA / file).exists(), file
    brush = (JAVA / "BeeKeeperBrushItem.java").read_text("utf8")
    harvester = (JAVA / "HoneyHarvesterItem.java").read_text("utf8")
    content = (JAVA / "BeeContent.java").read_text("utf8")
    for string in ("BeehiveBlock.HONEY_LEVEL","Items.HONEYCOMB","Items.HONEY_BOTTLE","HoneySmoke.hasClearCampfireSmoke"):
        assert string in brush, string
    for string in ("BeehiveBlock.HONEY_LEVEL","Items.HONEY_BOTTLE","HoneySmoke.hasClearCampfireSmoke"):
        assert string in harvester, string
    smoke = (JAVA / "HoneySmoke.java").read_text("utf8")
    for string in ("Blocks.CAMPFIRE","Blocks.SOUL_CAMPFIRE","CarpetBlock","BlockStateProperties.LIT","level.hasChunkAt"):
        assert string in smoke, string
    assert 'if (species.equals("bee"))' in crop_java
    assert 'pollinationGroup != PollinationGroup.BUZZ' in crop_java
    assert "HONEY_HARVESTER" in content and "BEEKEEPER_BRUSH" in content
    assert 'species.equals("squash_bee") && seedName.equals("zucchini_seeds")' in crop_java
    habitat_java = (JAVA / "BeeHabitatBlock.java").read_text("utf8")
    assert 'crop.getSeedName().equals("zucchini_seeds")' in habitat_java
    squash = next(s for s in species if s["id"] == "squash_bee")
    assert squash["host_crop_ids"] == ["zucchini"]
    en = j("assets/gugugaga/lang/en_us.json")
    zh = j("assets/gugugaga/lang/zh_cn.json")
    for item in ("bee_keeper_brush","bumblebee_nectar","mason_bee_cocoon","squash_bee_cocoon","leafcutter_bee_cocoon","honey_harvester"):
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh, item
    print("POLLINATION_AND_HONEY_OK: 36 crops, 12 pollinated loot tables, 4 habitats, 2 honey tools")

if __name__ == "__main__":
    main()
