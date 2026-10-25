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
print("BEE_DIVERSITY_ASSETS_OK: 4 distinct entity types and habitat spawns")
