#!/usr/bin/env python3
"""v0.3 cross-checks between registry, agronomic gameplay metadata and worldgen JSON."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/"src/main/resources"
J=lambda p: json.loads((ASSETS/p).read_text("utf8"))
ids=["millet","buckwheat","chickpea","lentil","radish","turnip","sweet_potato","kale","zucchini","raspberry","cranberry","sesame"]
wild=["wild_millet","wild_radish","wild_raspberry","wild_sesame"]
bees=["carpenter_bee","mining_bee"]
p=J("data/gugugaga/agronomy/crop_profiles_v0_3.json")
h=J("data/gugugaga/agronomy/biome_habitats_v0_3.json")
assert len(p["crops"])==12
assert {x["crop"] for x in p["crops"]}==set(ids)
java=(ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/AgricultureContent.java").read_text("utf8")
climate=(ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/CropClimate.java").read_text("utf8")
for obj in p["crops"]:
  id=obj["crop"]
  assert f'crop("{id}_crop", "{id}_seeds"' in java,id
  assert f'food("{id}"' in java,id
  assert J(f"data/gugugaga/recipe/{id}_seeds.json")["result"]["id"]==f"gugugaga:{id}_seeds"
  assert len(J(f"assets/gugugaga/blockstates/{id}_crop.json")["variants"])==8
  assert f'"{id}_seeds"' in climate or obj["growth_band_gameplay"]=="TEMPERATE",id
  if "real_maturity_days_range_reference" in obj:
    a,b=obj["real_maturity_days_range_reference"]
    assert 10<=a<b<=150
  assert len(obj["source_urls"])>=1
assert set(h["wild_plants"])==set(wild)
for id in wild:
  cfg=J(f"data/gugugaga/worldgen/configured_feature/{id}.json")
  bm=J(f"data/gugugaga/neoforge/biome_modifier/{id}.json")
  assert cfg["config"]["feature"]["feature"]["config"]["to_place"]["state"]["Name"]=="gugugaga:"+id
  assert bm["biomes"]==h["wild_plants"][id]["biomes"]
  assert len(bm["biomes"])>=2
assert set(h["bees"])==set(bees)
for id in bees:
  bm=J(f"data/gugugaga/neoforge/biome_modifier/spawn_{id}.json")
  assert bm["spawners"][0]["type"]=="gugugaga:"+id
  assert bm["biomes"]==h["bees"][id]["biomes"]
assert 'crop("onion_crop", "onion_seeds", AgricultureCropBlock.PollinationGroup.NONE)' in java
assert 'COTTON = material("cotton")' in java and 'FLAX = material("flax")' in java
print("AGRICULTURE_V0_3_OK: 12 crops, 4 wild seed sources, 2 bee entities, profiles and biome mappings")
