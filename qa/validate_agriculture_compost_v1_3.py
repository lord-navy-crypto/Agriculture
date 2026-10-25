#!/usr/bin/env python3
"""Check that every seed and harvested organic crop uses NeoForge's compostables data map."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]/"src/main/resources"
def read(path):
    return json.loads((ROOT/path).read_text("utf8"))
def main():
    profiles=["crop_profiles_v0_4.json","crop_additions_v0_9.json",
              "crop_additions_v1_0.json","crop_additions_v1_3.json","crop_additions_v1_7.json"]
    crops={p["crop"] for fname in profiles
           for p in read("data/gugugaga/agronomy/"+fname)["crops"]}
    assert len(crops)==72
    records=read("data/neoforge/data_maps/item/compostables.json")["values"]
    expected={f"gugugaga:{name}{suffix}" for name in crops for suffix in ("","_seeds")}
    assert set(records)==expected,(len(records),len(expected))
    for id, obj in records.items():
        assert set(obj)=={"chance","can_villager_compost"},id
        assert obj["chance"]==(0.30 if id.endswith("_seeds") else 0.65),id
        assert obj["can_villager_compost"] is False,id
    print("COMPOSTABLE_CROPS_OK: 72 produce items + 72 seeds, native NeoForge 1.21.1 data map")
if __name__=="__main__":
    main()
