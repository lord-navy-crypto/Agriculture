#!/usr/bin/env python3
"""Validate Threshold's independent JAR, registered biomes, required Agriculture dependency and safe generation."""
from __future__ import annotations
import json
import sys
import zipfile
from pathlib import Path

REQUIRED = {
    "META-INF/neoforge.mods.toml",
    "data/threshold/dimension/level_0.json",
    "data/threshold/dimension/level_1.json",
    "data/threshold/dimension_type/level_0_type.json",
    "data/threshold/dimension_type/level_1_type.json",
    "data/threshold/worldgen/biome/level_0.json",
    "data/threshold/worldgen/biome/level_1.json",
    "data/threshold/loot_table/chests/level_1_supply.json",
    "assets/threshold/blockstates/flickering_panel.json",
    "assets/threshold/models/block/faded_wallpaper.json",
    "assets/threshold/models/block/warehouse_wall.json",
    "io/github/lordnavycrypto/threshold/block/ThresholdEnvironmentBlocks.class",
    "io/github/lordnavycrypto/threshold/block/FlickeringPanelBlock.class",
    "data/threshold/worldgen/configured_feature/room_layout.json",
    "data/threshold/worldgen/placed_feature/room_layout.json",
    "io/github/lordnavycrypto/threshold/ThresholdMod.class",
    "io/github/lordnavycrypto/threshold/worldgen/ThresholdRoomLayoutFeature.class",
    "io/github/lordnavycrypto/threshold/command/ThresholdCommands.class",
}

def read_json(z, path):
    return json.loads(z.read(path).decode("utf-8"))

def main():
    if len(sys.argv) != 2:
        print("Usage: validate_threshold_dimensions.py <threshold-jar>")
        return 2
    jar = Path(sys.argv[1])
    if not jar.is_file():
        print("FAIL: no Threshold JAR", jar)
        return 1
    with zipfile.ZipFile(jar) as z:
        names=set(z.namelist())
        assert REQUIRED <= names, f"Missing resources: {sorted(REQUIRED - names)}"
        meta=z.read("META-INF/neoforge.mods.toml").decode("utf-8")
        assert 'modId="threshold"' in meta
        assert 'modId="gugugaga"' in meta and 'type="required"' in meta
        for level in ("level_0","level_1"):
            dim=read_json(z,f"data/threshold/dimension/{level}.json")
            assert dim["type"] == f"threshold:{level}_type"
            settings=dim["generator"]["settings"]
            assert dim["generator"]["type"] == "minecraft:flat"
            assert settings["biome"] == f"threshold:{level}"
            assert settings["features"] is True
            assert any(l["block"] != "minecraft:air" for l in settings["layers"])
            biome=read_json(z,f"data/threshold/worldgen/biome/{level}.json")
            assert any("threshold:room_layout" in step for step in biome["features"])
            dtype=read_json(z,f"data/threshold/dimension_type/{level}_type.json")
            assert dtype["has_skylight"] is False
        configured=read_json(z,"data/threshold/worldgen/configured_feature/room_layout.json")
        placed=read_json(z,"data/threshold/worldgen/placed_feature/room_layout.json")
        assert configured["type"] == placed["feature"] == "threshold:room_layout"
        assert "data/threshold/neoforge/biome_modifier/room_layout.json" not in names
    print(f"THRESHOLD_DIMENSION_ASSET_VALIDATION_PASS: {jar}")
    return 0

if __name__=="__main__":
    raise SystemExit(main())
