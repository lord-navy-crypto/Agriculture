#!/usr/bin/env python3
"""Static and packaged-art QA for Backroom Agriculture v4.4 preview.

This does not replace client rendering, real NeoForge builds, or worldgen
acceptance. It protects the 101-original-template limit and opt-in boundary.
"""
from __future__ import annotations

import json
from pathlib import Path
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
MOD_IDS = ("empty_water_flask", "raw_water_bottle", "boiled_water_bottle",
           "filtered_water_bottle", "field_sampling_bucket", "sampled_water_bucket")
MODELS = ROOT / "src/main/resources/assets/gugugaga/models/item"


def main():
    build = (ROOT / "build.gradle").read_text("utf-8")
    mod = (ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold/ThresholdMod.java").read_text("utf-8")
    packs = (ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold/ThresholdAnomalyPacks.java").read_text("utf-8")
    art = (ROOT / "tools/generate_water_art_v44.py").read_text("utf-8")
    seasonings = (ROOT / "tools/seasonings-v34-assets.gradle").read_text("utf-8")
    for key in ("tools/water-v44-assets.gradle", "tools/realscale-anomalies.gradle"):
        assert key in build, key
    for key in ("generateThresholdArchitecture", "generateThresholdAnomalyPack",
                "generateWaterArtV44"):
        assert (key in (ROOT / "tools/realscale-anomalies.gradle").read_text("utf-8")
                or key in (ROOT / "tools/water-v44-assets.gradle").read_text("utf-8")), key
    for item in MOD_IDS:
        model = json.loads((MODELS / (item + ".json")).read_text("utf-8"))
        assert model["textures"]["layer0"] == "gugugaga:item/water_v44/" + item
        assert '"' + item + '"' in art
    almond = json.loads((ROOT / "threshold/src/main/resources/assets/threshold/models/item/almond_water.json").read_text("utf-8"))
    assert almond["textures"]["layer0"] == "threshold:item/water_v44/almond_water"
    assert "if(id == 'brine')" in seasonings
    brine = json.loads((ROOT / "src/main/resources/data/gugugaga/recipe/brine.json").read_text("utf-8"))
    assert brine["type"] == "minecraft:crafting_shapeless"
    assert {x["item"] for x in brine["ingredients"]} == {
        "minecraft:water_bucket", "gugugaga:sea_salt"}
    assert brine["result"]["id"] == "gugugaga:brine"
    food = (ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/FoodV34Content.java").read_text("utf-8")
    assert 'ingredient("brine")' in food and 'ingredient("sea_salt")' in food
    for script in ("generate_original_architecture.py", "generate_architecture_batch.py",
                   "generate_architecture_expansion.py", "generate_urban_districts.py"):
        assert (ROOT / "tools/realscale_originals" / script).exists(), script
    assert "modBus.addListener(ThresholdAnomalyPacks::register)" in mod
    assert "OPTIONAL_BUILT_IN" in packs and "PackType.SERVER_DATA" in packs
    assert "false," in packs  # never a required data pack
    profile = json.loads((ROOT / "tools/realscale_originals/urban_district_profiles.json").read_text("utf-8"))
    assert len(profile["districts"]) == 4
    if len(sys.argv) > 1:
        jar = Path(sys.argv[1])
        if not jar.is_file():
            raise SystemExit(f"Missing built JAR: {jar}")
        with zipfile.ZipFile(jar) as z:
            members = set(z.namelist())
            assets = [f"assets/gugugaga/textures/item/water_v44/{name}.png" for name in MOD_IDS]
            assets += ["assets/threshold/textures/item/water_v44/almond_water.png"]
            architecture = [p for p in members
                            if p.startswith("data/threshold/structure/anomaly/architecture/")
                            and p.endswith(".nbt")]
            urban = [p for p in members
                     if p.startswith("data/threshold/structure/anomaly/urban/")
                     and p.endswith(".nbt")]
            assert len(architecture) == 97, len(architecture)
            assert len(urban) == 4, len(urban)
            for asset in assets:
                assert asset in members, asset
                blob = z.read(asset)
                assert blob.startswith(b"\\x89PNG\\r\\n\\x1a\\n"), asset
                assert int.from_bytes(blob[16:20], "big") == 16, asset
                assert int.from_bytes(blob[20:24], "big") == 16, asset
            pack = "resourcepacks/threshold_anomalies/"
            for filename in ("pack.mcmeta", "data/threshold/dimension/level_0.json",
                             "data/threshold/dimension/level_1.json",
                             "data/threshold/worldgen/structure_set/anomaly_architecture.json",
                             "data/threshold/worldgen/structure_set/anomaly_urban.json"):
                assert pack + filename in members, pack + filename
            for level in ("level_0", "level_1"):
                doc = json.loads(z.read(pack + "data/threshold/dimension/" + level + ".json"))
                assert doc["generator"]["settings"]["structure_overrides"] == [
                    "threshold:anomaly_architecture", "threshold:anomaly_urban"]
            print("BACKROOM_V44_PACKAGED_ASSETS_OK: 7 sprites, 97 original buildings, 4 city districts")
    print("BACKROOM_V44_STATIC_OK: brine recipe, owned architecture, original water art, opt-in pack")


if __name__ == "__main__":
    main()
