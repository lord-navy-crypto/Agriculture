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
    "data/threshold/dimension/level_2.json",
    "data/threshold/dimension/level_3.json",
    "data/threshold/dimension/level_4.json",
    "data/threshold/dimension/level_5.json",
    "data/threshold/dimension_type/level_0_type.json",
    "data/threshold/dimension_type/level_1_type.json",
    "data/threshold/dimension_type/level_2_type.json",
    "data/threshold/dimension_type/level_3_type.json",
    "data/threshold/dimension_type/level_4_type.json",
    "data/threshold/dimension_type/level_5_type.json",
    "data/threshold/worldgen/biome/level_0.json",
    "data/threshold/worldgen/biome/level_1.json",
    "data/threshold/worldgen/biome/level_2.json",
    "data/threshold/transit/route_catalog.json",
    "data/threshold/worldgen/biome/level_3.json",
    "data/threshold/worldgen/biome/level_4.json",
    "data/threshold/worldgen/biome/level_5.json",
    "data/threshold/loot_table/chests/level_1_supply.json",
    "data/threshold/loot_table/chests/level_1_starter.json",
    "data/threshold/loot_table/chests/level_1_maintenance.json",
    "data/threshold/loot_table/chests/level_0_forgotten.json",
    "data/threshold/loot_table/chests/level_3_supply.json",
    "data/threshold/loot_table/chests/level_4_supply.json",
    "data/threshold/loot_table/chests/level_5_supply.json",
    "assets/threshold/sounds.json",
    "assets/threshold/sounds/level_0_ambient.ogg",
    "assets/threshold/sounds/level_1_ambient.ogg",
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
    "assets/threshold/blockstates/level_1_to_2.json",
    "assets/threshold/models/block/transition_zone.json",
    "io/github/lordnavycrypto/threshold/block/ThresholdTransitionZoneBlock.class",
    "assets/threshold/blockstates/level_2_to_1.json",
    "assets/threshold/blockstates/level_2_to_3.json",
    "assets/threshold/blockstates/level_2_fire_exit.json",
    "assets/threshold/blockstates/level_2_branch_door.json",
    "assets/threshold/blockstates/level_3_to_2.json",
    "assets/threshold/blockstates/level_3_to_4.json",
    "assets/threshold/blockstates/level_4_to_3.json",
    "assets/threshold/blockstates/level_3_elevator.json",
    "assets/threshold/blockstates/level_4_to_5.json",
    "assets/threshold/blockstates/level_5_to_3.json",
    "assets/threshold/blockstates/level_5_to_4.json",
    "assets/threshold/blockstates/level_5_elevator_panel.json",
    "io/github/lordnavycrypto/threshold/block/ThresholdHotelElevatorBlock.class",
    "assets/threshold/models/block/transition_wall.json",
    "assets/threshold/models/item/transition_wall.json",
    "data/threshold/loot_table/blocks/transition_wall.json",
    "io/github/lordnavycrypto/threshold/block/ThresholdTransitionWallBlock.class",
    "io/github/lordnavycrypto/threshold/block/ThresholdPassageBlock.class",
    "io/github/lordnavycrypto/threshold/block/ThresholdRouteBlock.class",
    "io/github/lordnavycrypto/threshold/ThresholdSounds.class",
    "io/github/lordnavycrypto/threshold/dimension/ThresholdDimensions.class",
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
    "io/github/lordnavycrypto/threshold/entity/ThresholdEntities.class",
    "io/github/lordnavycrypto/threshold/entity/ThresholdWatcherEntity.class",
    "io/github/lordnavycrypto/threshold/entity/ThresholdHoundEntity.class",
    "io/github/lordnavycrypto/threshold/entity/ThresholdPartygoerEntity.class",
    "io/github/lordnavycrypto/threshold/item/ThresholdItems.class",
    "io/github/lordnavycrypto/threshold/item/AlmondWaterItem.class",
    "io/github/lordnavycrypto/threshold/item/FlashlightItem.class",
    "io/github/lordnavycrypto/threshold/item/BatteryItem.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdPartygoerModel.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdPartygoerRenderer.class",
    "io/github/lordnavycrypto/threshold/entity/ThresholdEntityAttributes.class",
    "io/github/lordnavycrypto/threshold/entity/ThresholdEntityDirector.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdWatcherModel.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdWatcherRenderer.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdHoundModel.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdHoundRenderer.class",
    "io/github/lordnavycrypto/threshold/client/ThresholdClientEvents.class",
    "assets/threshold/geo/watcher.geo.json",
    "assets/threshold/animations/watcher.animation.json",
    "assets/threshold/textures/entity/watcher.png",
    "assets/threshold/geo/hound.geo.json",
    "assets/threshold/models/item/almond_water.json",
    "assets/threshold/models/item/flashlight.json",
    "assets/threshold/models/item/battery.json",
    "assets/threshold/animations/hound.animation.json",
    "assets/threshold/textures/entity/hound.png",
    "data/threshold/loot_table/entities/hound.json",
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
        for level in ("level_0","level_1","level_2","level_3","level_4","level_5"):
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
            assert biome["spawners"] == {}, "Threshold entity director owns dimension-specific encounters"
            assert biome["spawn_costs"] == {}, "No generic natural spawn costs should be injected"
            dtype=read_json(z,f"data/threshold/dimension_type/{level}_type.json")
            assert dtype["has_skylight"] is False
        configured=read_json(z,"data/threshold/worldgen/configured_feature/room_layout.json")
        placed=read_json(z,"data/threshold/worldgen/placed_feature/room_layout.json")
        assert configured["type"] == placed["feature"] == "threshold:room_layout"
        modifier=read_json(z,"data/threshold/neoforge/biome_modifier/room_layout.json")
        assert modifier["type"]=="neoforge:add_features"
        assert sorted(modifier["biomes"])==["threshold:level_0","threshold:level_1","threshold:level_2","threshold:level_3","threshold:level_4","threshold:level_5"]
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
        # Packaged entity assets must be complete in the independent JAR.
        for name in ("watcher", "hound"):
            geo=read_json(z,f"assets/threshold/geo/{name}.geo.json")["minecraft:geometry"][0]
            anim=read_json(z,f"assets/threshold/animations/{name}.animation.json")["animations"]
            assert geo["description"]["texture_width"] == 64
            assert geo["description"]["texture_height"] == 64
            assert geo["bones"] and anim
            png=z.read(f"assets/threshold/textures/entity/{name}.png")
            assert png[:8] == b"\x89PNG\r\n\x1a\n"
            assert int.from_bytes(png[16:20],"big") == 64
            assert int.from_bytes(png[20:24],"big") == 64
        hound_loot=read_json(z,"data/threshold/loot_table/entities/hound.json")
        assert hound_loot["type"] == "minecraft:entity" and hound_loot["pools"] == []
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
        threshold_loot_items = set()
        for filename in ("level_0_forgotten", "level_1_supply", "level_1_maintenance"):
            loot = read_json(z, f"data/threshold/loot_table/chests/{filename}.json")
            for pool in loot["pools"]:
                for entry in pool["entries"]:
                    if entry["type"] == "minecraft:item" and entry["name"].startswith("threshold:"):
                        threshold_loot_items.add(entry["name"])
        assert {"threshold:almond_water", "threshold:flashlight", "threshold:battery"} <= threshold_loot_items

        for item_model in ("almond_water", "flashlight", "battery"):
            model = read_json(z, f"assets/threshold/models/item/{item_model}.json")
            assert model["parent"] == "minecraft:item/generated"

        starter = read_json(z, "data/threshold/loot_table/chests/level_1_starter.json")
        guaranteed = {e["name"] for pool in starter["pools"]
                      for e in pool["entries"] if e["type"] == "minecraft:item"}
        assert {"gugugaga:boiled_water_bottle", "gugugaga:barley",
                "gugugaga:soybean", "gugugaga:tomato",
                "gugugaga:barley_vegetable_soup"} <= guaranteed
        assert modifier["step"]=="vegetal_decoration"

        for passage in ("level_2_fire_exit", "level_2_to_3",
                        "level_2_branch_door", "level_3_to_2",
                        "level_3_to_4", "level_4_to_3",
                        "level_3_elevator", "level_4_to_5",
                        "level_5_to_3", "level_5_to_4"):
            blockstate = read_json(z, f"assets/threshold/blockstates/{passage}.json")
            assert blockstate["variants"][""]["model"].startswith("minecraft:block/")

        hotel_panel = read_json(z, "assets/threshold/blockstates/level_5_elevator_panel.json")["variants"]
        assert len(hotel_panel) == 12
        assert all(v["model"] == "minecraft:block/iron_block" for v in hotel_panel.values())
        hotel_biome = read_json(z, "data/threshold/worldgen/biome/level_5.json")
        assert hotel_biome["effects"]["mood_sound"]["sound"] == "minecraft:block.note_block.harp"

        sound_definitions=read_json(z,"assets/threshold/sounds.json")
        for level in ("level_0","level_1"):
            event=f"{level}_ambient"
            assert event in sound_definitions
            biome=read_json(z,f"data/threshold/worldgen/biome/{level}.json")
            assert biome["effects"]["music"]["sound"] == f"threshold:{event}"
            assert biome["effects"]["music"]["replace_current_music"] is True
            ogg=z.read(f"assets/threshold/sounds/{event}.ogg")
            assert ogg[:4] == b"OggS" and len(ogg) > 4000
        for filename in ("level_3_supply","level_4_supply","level_5_supply"):
            loot=read_json(z,f"data/threshold/loot_table/chests/{filename}.json")
            assert loot["type"] == "minecraft:chest"
            for pool in loot["pools"]:
                for entry in pool["entries"]:
                    if entry["type"] == "minecraft:item" and entry["name"].startswith("gugugaga:"):
                        assert entry["name"].removeprefix("gugugaga:") in allowed_agriculture
    print(f"THRESHOLD_DIMENSION_ASSET_VALIDATION_PASS: {jar}")
    return 0

if __name__=="__main__":
    raise SystemExit(main())
