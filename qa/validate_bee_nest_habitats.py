#!/usr/bin/env python3
"""Four bee habitat asset and gameplay contract checks for NeoForge 1.21.1."""
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
res = root / "src/main/resources"
java = root / "src/main/java/io/github/lordnavycrypto/gugugaga"
def load(path):
    return json.loads((res / path).read_text(encoding="utf8"))

names = ("bumblebee_habitat", "mason_bee_habitat", "squash_bee_habitat", "leafcutter_bee_habitat")
en = load("assets/gugugaga/lang/en_us.json")
zh = load("assets/gugugaga/lang/zh_cn.json")
profiles = load("data/gugugaga/agronomy/bee_nest_habitats_v0_5.json")
assert profiles["schema_version"] == "0.5"
assert {v["id"] for v in profiles["habitats"]} == set(names)
for id in names:
    state = load(f"assets/gugugaga/blockstates/{id}.json")["variants"]
    assert set(state) == {f"activity={n}" for n in range(4)}, id
    assert all(v["model"].startswith("minecraft:block/") for v in state.values()), id
    assert load(f"assets/gugugaga/models/item/{id}.json")["parent"].startswith("minecraft:block/"), id
    assert load(f"data/gugugaga/recipe/{id}.json")["result"]["id"] == f"gugugaga:{id}", id
    drop = load(f"data/gugugaga/loot_table/blocks/{id}.json")["pools"][0]["entries"][0]["name"]
    assert drop == f"gugugaga:{id}", id
    assert f"block.gugugaga.{id}" in en and f"block.gugugaga.{id}" in zh
assert "message.gugugaga.bee_habitat_activity" in en
assert "message.gugugaga.bee_habitat_activity" in zh
mod = (java / "GuguGagaMod.java").read_text("utf8")
registry = (java / "agriculture/BeeContent.java").read_text("utf8")
habitat = (java / "agriculture/BeeNestHabitatBlock.java").read_text("utf8")
assert "BeeContent.BLOCKS.register(modBus)" in mod
for id in names:
    assert f'"{id}"' in registry, id
assert "randomTick" in habitat and "hasMatchingBee" in habitat and "hasForage" in habitat
assert "hasChunkAt" in habitat and "level.getEntitiesOfClass" in habitat
assert "new ItemStack" not in habitat and "Items.HONEY_BOTTLE" not in habitat
assert 'if (id.equals("zucchini_crop")) return true;' in habitat
crop = (java / "agriculture/AgricultureCropBlock.java").read_text("utf8")
assert 'case "squash_bee" -> seedName.equals("zucchini_seeds")' in crop
assert 'seedName.equals("tomato_seeds") || seedName.equals("eggplant_seeds")' in crop
print("BEE_NEST_HABITATS_OK: 4 distinct habitat assets, recipes, drops and source profiles")
