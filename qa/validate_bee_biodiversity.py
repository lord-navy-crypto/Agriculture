#!/usr/bin/env python3
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]/"src/main/resources"
for species in "bumblebee mason_bee squash_bee leafcutter_bee carpenter_bee mining_bee".split():
  p=ROOT/f"data/gugugaga/neoforge/biome_modifier/spawn_{species}.json"
  obj=json.loads(p.read_text("utf8"))
  assert obj["type"]=="neoforge:add_spawns"
  assert obj["spawners"][0]["type"]=="gugugaga:"+species
  assert 0<obj["spawners"][0]["weight"]<=3
  assert len(obj["biomes"])>=3
  for lang in ["en_us","zh_cn"]:
    words=json.loads((ROOT/f"assets/gugugaga/lang/{lang}.json").read_text("utf8"))
    assert f"entity.gugugaga.{species}" in words
wildlife = (ROOT.parent / "java/io/github/lordnavycrypto/gugugaga/agriculture/BeeWildlife.java").read_text("utf8")
wild_bee = (ROOT.parent / "java/io/github/lordnavycrypto/gugugaga/agriculture/WildPollinatorBee.java").read_text("utf8")
assert "WildPollinatorBee::new" in wildlife
assert "Builder.of(Bee::new" not in wildlife and "Builder.<Bee>of(Bee::new" not in wildlife
assert "public boolean hasHive()" in wild_bee and "return false;" in wild_bee
assert "public BlockPos getHivePos()" in wild_bee and "return null;" in wild_bee
assert "public void setHivePos(BlockPos pos)" in wild_bee
assert "other.getType() == getType()" in wild_bee
assert "public Bee getBreedOffspring(ServerLevel level, AgeableMob mate)" in wild_bee
assert "getType().create(level)" in wild_bee
print("BEE_DIVERSITY_ASSETS_OK: 6 distinct registered types, habitat spawns and hive-free species-preserving bees")
