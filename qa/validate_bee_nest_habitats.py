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
    assert state["activity=0"]["model"] == f"gugugaga:block/{id}", id
    assert state["activity=3"]["model"] == f"gugugaga:block/{id}_active", id
    for model_name in (id, f"{id}_active"):
        model = load(f"assets/gugugaga/models/block/{model_name}.json")
        assert model["parent"] == "minecraft:block/block", model_name
        assert set(model["textures"]) == {"particle","down","up","north","south","west","east","hole","decoration"}, model_name
        assert len(model["elements"]) >= 5, model_name
        for element in model["elements"]:
            assert len(element["from"]) == len(element["to"]) == 3, model_name
            assert all(0 <= lo < hi <= 16 for lo,hi in zip(element["from"],element["to"])), model_name
            assert all(face["texture"].startswith("#") for face in element["faces"].values()), model_name
    assert load(f"assets/gugugaga/models/item/{id}.json")["parent"] == f"gugugaga:block/{id}"
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
assert ".noOcclusion()" in registry
for id in names:
    assert f'"{id}"' in registry, id
assert "getCollisionShape(BlockState state, BlockGetter level" in habitat
assert "BUMBLE_MOUND" in habitat and "MASON_HOTEL" in habitat
assert "SQUASH_BURROW" in habitat and "LEAFCUTTER_TUBES" in habitat
assert "randomTick" in habitat and "countMatchingBees" in habitat and "hasForage" in habitat
assert "populationLimit()" in habitat and "random.nextInt(32)" in habitat
assert 'ResourceLocation.fromNamespaceAndPath("gugugaga", beeSpecies)' in habitat
assert 'child.getPersistentData().putLong("gugugaga_wild_nest_origin"' in habitat
assert "child.setBaby(true)" in habitat and "level.addFreshEntity(child)" in habitat
assert "nearbyBees > 0 && hasForage(level, pos)" in habitat
assert "activity - 1" in habitat and "activity + 1" in habitat
assert 'beeSpecies.equals("squash_bee") && level.getDayTime() % 24000L >= 6000L' in habitat
assert habitat.index('level.getDayTime()') < habitat.index("if (random.nextInt(4) != 0)")
assert "direction == Direction.DOWN && !canSurvive(state, level, pos)" in habitat
assert "isFaceSturdy(level, footing, Direction.UP)" in habitat
assert "below.is(Blocks.DIRT) || below.is(Blocks.COARSE_DIRT)" in habitat
assert "below.is(Blocks.GRASS_BLOCK)" not in habitat
assert 'id.equals("tomato_crop") || id.equals("eggplant_crop")' in habitat
assert "hasChunkAt" in habitat and "level.getEntitiesOfClass" in habitat
assert "candidate -> candidate.isAlive() && candidate.getType() == expectedType" in habitat
assert "for (int ring = 0; ring <= 4; ring++)" in habitat
assert "GENERAL_FORAGE_CROPS.contains(id)" in habitat
assert "BlockPos.betweenClosed" not in habitat
assert "new ItemStack" not in habitat and "Items.HONEY_BOTTLE" not in habitat
assert 'if (id.equals("zucchini_crop") || id.equals("pumpkin_crop")) return true;' in habitat
crop = (java / "agriculture/AgricultureCropBlock.java").read_text("utf8")
assert 'case "squash_bee" -> seedName.equals("zucchini_seeds")' in crop
assert 'seedName.equals("tomato_seeds") || seedName.equals("eggplant_seeds")' in crop
print("BEE_NEST_HABITATS_OK: 4 distinct nest models + activity, established population-capped emergences, no honey")
