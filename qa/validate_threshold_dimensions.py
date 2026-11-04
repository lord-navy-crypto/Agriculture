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
    "data/threshold/loot_table/chests/level_1_starter.json",
    "data/threshold/loot_table/chests/level_1_maintenance.json",
    "data/threshold/loot_table/chests/level_0_forgotten.json",
    "assets/threshold/blockstates/flickering_panel.json",
    "assets/threshold/models/block/faded_wallpaper.json",
    "assets/threshold/models/block/warehouse_wall.json",
    "assets/threshold/textures/block/faded_wallpaper.png",
    "assets/threshold/textures/block/stained_wallpaper.png",
    "assets/threshold/textures/block/warehouse_wall.png",
    "assets/threshold/textures/block/flickering_panel.png",
    "assets/threshold/textures/block/flickering_panel_off.png",
    "assets/threshold/textures/block/transition_wall.png",
    "assets/threshold/blockstates/transition_wall.json",
    "assets/threshold/models/block/transition_wall.json",
    "assets/threshold/models/item/transition_wall.json",
    "data/threshold/loot_table/blocks/transition_wall.json",
    "io/github/lordnavycrypto/threshold/block/ThresholdTransitionWallBlock.class",
    "io/github/lordnavycrypto/threshold/block/ThresholdEnvironmentBlocks.class",
    "io/github/lordnavycrypto/threshold/block/FlickeringPanelBlock.class",
    "data/threshold/worldgen/configured_feature/room_layout.json",
    "data/threshold/worldgen/placed_feature/room_layout.json",
    "data/threshold/worldgen/configured_feature/industrial_vent.json",
    "data/threshold/worldgen/placed_feature/industrial_vent.json",
    "assets/threshold/geo/ceiling_vent.geo.json",
    "assets/threshold/animations/ceiling_vent.animation.json",
    "assets/threshold/blockstates/ceiling_vent.json",
    "assets/threshold/models/item/ceiling_vent.json",
    "io/github/lordnavycrypto/threshold/block/ThresholdCeilingVentBlockEntity.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdVentRenderer.class",
    "io/github/lordnavycrypto/threshold/worldgen/ThresholdIndustrialVentFeature.class",
    "data/threshold/worldgen/configured_feature/room_sequence.json",
    "data/threshold/worldgen/placed_feature/room_sequence.json",
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
        assert 'modId="geckolib"' in meta and 'versionRange="[4.9.3,5)"' in meta
        assert 'modId="lithostitched"' in meta and 'versionRange="[1.7.11,2)"' in meta
        for level in ("level_0","level_1"):
            dim=read_json(z,f"data/threshold/dimension/{level}.json")
            assert dim["type"] == f"threshold:{level}_type"
            settings=dim["generator"]["settings"]
            assert dim["generator"]["type"] == "minecraft:flat"
            assert settings["biome"] == f"threshold:{level}"
            assert settings["features"] is True
            assert any(l["block"] != "minecraft:air" for l in settings["layers"])
            biome=read_json(z,f"data/threshold/worldgen/biome/{level}.json")
            assert biome["features"] == [], "custom biome should not duplicate injected features"
            assert biome["carvers"] == {}, "NeoForge 1.21.1 expects a keyed carver map"
            dtype=read_json(z,f"data/threshold/dimension_type/{level}_type.json")
            assert dtype["has_skylight"] is False
        configured=read_json(z,"data/threshold/worldgen/configured_feature/room_layout.json")
        placed=read_json(z,"data/threshold/worldgen/placed_feature/room_layout.json")
        assert configured["type"] == placed["feature"] == "threshold:room_layout"
        modifier=read_json(z,"data/threshold/neoforge/biome_modifier/room_layout.json")
        assert modifier["type"]=="neoforge:add_features"
        assert sorted(modifier["biomes"])==["threshold:level_0","threshold:level_1"]
        assert modifier["features"]=="threshold:room_sequence"
        sequence=read_json(z,"data/threshold/worldgen/configured_feature/room_sequence.json")
        assert sequence["type"]=="lithostitched:composite"
        assert sequence["config"]["features"]==["threshold:room_layout", "threshold:industrial_vent"]
        wrapper=read_json(z,"data/threshold/worldgen/placed_feature/room_sequence.json")
        assert wrapper["feature"]=="threshold:room_sequence"
        for child in ("room_layout", "industrial_vent"):
            nested=read_json(z, f"data/threshold/worldgen/placed_feature/{child}.json")
            assert all(p.get("type")!="minecraft:biome" for p in nested["placement"]), (
                f"Nested Lithostitched feature {child} must not check its biome individually")
        assert read_json(z, "data/threshold/worldgen/configured_feature/industrial_vent.json")["type"] == "threshold:industrial_vent"
        clips=read_json(z, "assets/threshold/animations/ceiling_vent.animation.json")["animations"]
        assert "animation.ceiling_vent.spin" in clips
        bones=read_json(z, "assets/threshold/geo/ceiling_vent.geo.json")["minecraft:geometry"][0]["bones"]
        assert "fan" in {bone["name"] for bone in bones}
        # All survival loot must reference real Agriculture items, not
        # decorative placeholder names or a duplicated nutrition system.
        allowed_agriculture = {
            "barley", "barley_seeds", "corn", "rice", "tomato",
            "soybean", "pea", "spinach", "cucumber", "sea_salt",
            "empty_water_flask", "raw_water_bottle",
            "boiled_water_bottle", "filtered_water_bottle",
            "barley_vegetable_soup", "grain_porridge", "vegetable_stew",
            "bean_stew", "garden_salad",
        }
        for filename in ("level_1_supply", "level_1_starter",
                         "level_1_maintenance", "level_0_forgotten"):
            loot = read_json(z, f"data/threshold/loot_table/chests/{filename}.json")
            assert loot["type"] == "minecraft:chest"
            for pool in loot["pools"]:
                for entry in pool["entries"]:
                    if entry["type"] == "minecraft:empty":
                        continue
                    name = entry["name"]
                    if name.startswith("gugugaga:"):
                        assert name.removeprefix("gugugaga:") in allowed_agriculture, (
                            f"Unknown Agriculture loot item {name}")
        starter = read_json(z, "data/threshold/loot_table/chests/level_1_starter.json")
        guaranteed = {e["name"] for pool in starter["pools"]
                      for e in pool["entries"] if e["type"] == "minecraft:item"}
        assert {"gugugaga:boiled_water_bottle", "gugugaga:barley",
                "gugugaga:soybean", "gugugaga:tomato",
                "gugugaga:barley_vegetable_soup"} <= guaranteed
        assert modifier["step"]=="vegetal_decoration"
    print(f"THRESHOLD_DIMENSION_ASSET_VALIDATION_PASS: {jar}")
    return 0

if __name__=="__main__":
    raise SystemExit(main())
