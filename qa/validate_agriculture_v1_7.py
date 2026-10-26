#!/usr/bin/env python3
"""v1.7 crop additions, floral-honey sources, and mixed-forage behavior contract."""
import json
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
SRC = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
CROPS = {"asparagus":("COOL","NONE"),"pumpkin":("WARM","ORCHARD"),"pineapple":("WET","NONE"),"blackberry":("TEMPERATE","BERRY"),"blackcurrant":("COOL","BERRY"),"amaranth":("DRY","NONE"),"chives":("COOL","NONE"),"sweet_corn":("WARM","NONE")}
HONEY = {"blackberry_honey_bottle","blackcurrant_honey_bottle","chive_honey_bottle","pumpkin_blossom_honey_bottle","wildflower_honey_bottle"}
def read(path): return json.loads((RES / path).read_text(encoding="utf8"))
def main():
    profiles = read("data/gugugaga/agronomy/crop_additions_v1_7.json")
    assert profiles["schema_version"] == "1.7"
    assert {r["crop"] for r in profiles["crops"]} == set(CROPS)
    en, zh = read("assets/gugugaga/lang/en_us.json"), read("assets/gugugaga/lang/zh_cn.json")
    reg = (SRC / "AgricultureContent.java").read_text(encoding="utf8")
    climate = (SRC / "CropClimate.java").read_text(encoding="utf8")
    crop_java = (SRC / "AgricultureCropBlock.java").read_text(encoding="utf8")
    nectar = (SRC / "NectarHoneySource.java").read_text(encoding="utf8")
    bee = (SRC / "BeeContent.java").read_text(encoding="utf8")
    generator = (ROOT / "tools/crop-textures.gradle").read_text(encoding="utf8")
    compost = read("data/neoforge/data_maps/item/compostables.json")["values"]
    for row in profiles["crops"]:
        crop = row["crop"]
        assert (row["growth_band_gameplay"],row["pollination_gameplay"]) == CROPS[crop]
        assert row["source_urls"] and all(u.startswith("https://") for u in row["source_urls"])
        assert f'crop("{crop}_crop", "{crop}_seeds", AgricultureCropBlock.PollinationGroup.{row["pollination_gameplay"]})' in reg
        assert f'"{crop}_seeds"' in climate
        assert f'event.accept({crop.upper()}_SEEDS.get())' in reg and f'event.accept({crop.upper()}.get())' in reg
        for item in (crop,crop+"_seeds"):
            model=read(f"assets/gugugaga/models/item/{item}.json")
            assert model["textures"]["layer0"] == f"gugugaga:item/crops/{item}"
            assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
            assert f"gugugaga:{item}" in compost
        states=read(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
        assert set(states)=={f"age={a},pollinated={p}" for a in range(8) for p in ("false","true")}
        loot=read(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")["pools"]
        assert len(loot)==2 and loot[0]["entries"][0]["name"]==f"gugugaga:{crop}_seeds" and loot[1]["entries"][0]["name"]==f"gugugaga:{crop}"
        assert read(f"data/gugugaga/recipe/{crop}_seeds.json")["result"]["id"]==f"gugugaga:{crop}_seeds"
        assert f"block.gugugaga.{crop}_crop" in en and f"block.gugugaga.{crop}_crop" in zh
        assert generator.count(f"case '{crop}':") >= 2
    assert "REPEATED_SPEAR_HARVEST" in crop_java and '"asparagus_seeds"' in crop_java
    assert '"pumpkin_seeds", "blackberry_seeds", "blackcurrant_seeds"' in crop_java and '"chives_seeds"' in crop_java
    honey_data=read("data/gugugaga/agronomy/honey_sources_v0_9.json")
    honey_ids={row["item"] for row in honey_data["honey_variants"]}
    assert HONEY <= honey_ids and len(honey_ids)==30
    for item in honey_ids:
        model=read(f"assets/gugugaga/models/item/{item}.json")
        assert model["textures"]["layer0"]==f"gugugaga:item/honey/{item}"
        assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
    assert "otherFlowerKinds" in nectar and "totalFlowers >= 12 && distinctSourceKinds >= 3" in nectar
    assert "WILDFLOWER_HONEY_BOTTLE" in nectar and "wildflower_honey_bottle" in bee
    assert "honeyTexturePalette.size() != 50" in generator and "honeyTextureHashes.size() != 50" in generator
    current_honey = read("data/gugugaga/agronomy/honey_sources_v1_2.json")
    assert len(current_honey["honey_variants"]) == 50
    assert "cropIds.size() != 100" in generator
    print("AGRICULTURE_V1_7_OK: eight v1.7 crop cycles preserved; current generator covers 100 crops and 50 honey variants")
if __name__ == "__main__": main()
