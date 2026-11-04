#!/usr/bin/env python3
"""Validate Threshold's six-level, source-backed transit network and original audio."""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold"
RES = ROOT / "threshold/src/main/resources"


def read_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def require(source: str, token: str, label: str) -> None:
    if token not in source:
        raise AssertionError(f"Threshold expansion contract missing: {label}")


def main() -> int:
    dimensions = (JAVA / "dimension/ThresholdDimensions.java").read_text(encoding="utf-8")
    layout = (JAVA / "worldgen/ThresholdRoomLayoutFeature.java").read_text(encoding="utf-8")
    passage = (JAVA / "block/ThresholdPassageBlock.java").read_text(encoding="utf-8")
    seam = (JAVA / "block/ThresholdTransitionZoneBlock.java").read_text(encoding="utf-8")
    hotel_elevator = (JAVA / "block/ThresholdHotelElevatorBlock.java").read_text(encoding="utf-8")
    route_block = (JAVA / "block/ThresholdRouteBlock.java").read_text(encoding="utf-8")
    blocks = (JAVA / "block/ThresholdEnvironmentBlocks.java").read_text(encoding="utf-8")
    sounds_java = (JAVA / "ThresholdSounds.java").read_text(encoding="utf-8")
    mod = (JAVA / "ThresholdMod.java").read_text(encoding="utf-8")
    client_events = (JAVA / "client/ThresholdClientEvents.java").read_text(encoding="utf-8")
    watcher = (JAVA / "entity/ThresholdWatcherEntity.java").read_text(encoding="utf-8")
    partygoer = (JAVA / "entity/ThresholdPartygoerEntity.java").read_text(encoding="utf-8")
    director = (JAVA / "entity/ThresholdEntityDirector.java").read_text(encoding="utf-8")
    items = (JAVA / "item/ThresholdItems.java").read_text(encoding="utf-8")
    flashlight = (JAVA / "item/FlashlightItem.java").read_text(encoding="utf-8")
    almond_water = (JAVA / "item/AlmondWaterItem.java").read_text(encoding="utf-8")
    commands = (JAVA / "command/ThresholdCommands.java").read_text(encoding="utf-8")

    levels = ("level_0", "level_1", "level_2", "level_3", "level_4", "level_5")
    for level in levels:
        dim = read_json(RES / f"data/threshold/dimension/{level}.json")
        dtype = read_json(RES / f"data/threshold/dimension_type/{level}_type.json")
        biome = read_json(RES / f"data/threshold/worldgen/biome/{level}.json")
        assert dim["type"] == f"threshold:{level}_type"
        assert dim["generator"]["type"] == "minecraft:flat"
        assert dim["generator"]["settings"]["biome"] == f"threshold:{level}"
        assert dim["generator"]["settings"]["features"] is True
        assert dtype["has_skylight"] is False
        assert biome["spawners"] == {}
        assert biome["spawn_costs"] == {}
        assert biome["carvers"] == {}

    for token, label in (
        ("LEVEL_2", "Level 2 dimension key"),
        ("LEVEL_3", "Level 3 dimension key"),
        ("LEVEL_4", "Level 4 dimension key"),
        ("LEVEL_5", "Level 5 dimension key"),
    ):
        require(dimensions, token, label)
    for token, label in (
        ("buildLevel2Chunk(world, chunkX, chunkZ)", "Level 2 world generation dispatch"),
        ("buildLevel3Chunk(world, chunkX, chunkZ)", "Level 3 world generation dispatch"),
        ("buildLevel4Chunk(world, chunkX, chunkZ)", "Level 4 world generation dispatch"),
        ("buildLevel5Chunk(world, chunkX, chunkZ)", "Level 5 world generation dispatch"),
        ("level2Architecture", "deterministic Level 2 room archetypes"),
        ("level3Architecture", "deterministic Level 3 room archetypes"),
        ("level4Architecture", "deterministic Level 4 room archetypes"),
        ("level5Architecture", "deterministic Level 5 hotel room archetypes"),
        ("LEVEL_2_ROOM_SIZE = 32", "Level 2 room grid"),
        ("LEVEL_5_ROOM_SIZE = 32", "Level 5 room grid"),
        ("Beverly Room", "Level 5 ballroom identity"),
        ("Boiler Room", "Level 5 boiler-room identity"),
        ("Blocks.COPPER_BLOCK", "industrial pipework"),
        ("LEVEL_2_BRANCH_DOOR", "Level 2 branching route placement"),
    ):
        require(layout, token, label)

    route_tokens = (
        "LEVEL_1_TO_2", "LEVEL_2_FIRE_EXIT", "LEVEL_2_TO_3",
        "LEVEL_2_BRANCH_DOOR", "LEVEL_3_TO_2", "LEVEL_3_TO_4",
        "LEVEL_4_TO_3", "LEVEL_3_ELEVATOR", "LEVEL_4_TO_5",
        "LEVEL_5_TO_3", "LEVEL_5_TO_4", "LEVEL_5_ELEVATOR_PANEL",
    )
    for token in route_tokens:
        require(blocks, token, f"registered route {token}")
    require(passage, "teleportTo(destination", "fixed passage performs dimension travel")
    require(seam, "entityInside", "Level 1 to Level 2 uses a seamless trigger zone")
    require(seam, "ThresholdDimensions.LEVEL_2", "seam targets Level 2")
    require(hotel_elevator, 'IntegerProperty.create("floor", 1, 12)', "hotel elevator exposes floors 1 through 12")
    require(hotel_elevator, "ThresholdDimensions.LEVEL_5", "hotel elevator stays within Level 5")
    require(route_block, "chooseRoute", "weighted route selection")
    require(route_block, "source.getSeed()", "route stable for a world seed")
    require(route_block, "pos.asLong()", "each doorway has its own route draw")
    require(route_block, "Weights are implementation tuning", "weights are not presented as canon probabilities")
    require(commands, 'literal("level_2")', "operator travel command for Level 2")
    require(commands, 'literal("level_3")', "operator travel command for Level 3")
    require(commands, 'literal("level_4")', "operator travel command for Level 4")
    require(commands, 'literal("level_5")', "operator travel command for Level 5")
    assert "LEVEL_1_TO_3" not in blocks, "remove non-canonical direct Level 1 -> Level 3 route"
    assert "LEVEL_3_TO_1" not in blocks, "Level 3 return should follow the Level 2 service route"

    passage_models = {
        "level_1_to_2": "threshold:block/transition_zone",
        "level_2_fire_exit": "minecraft:block/iron_door_bottom",
        "level_2_to_3": "minecraft:block/copper_block",
        "level_2_branch_door": "minecraft:block/iron_trapdoor",
        "level_3_to_2": "minecraft:block/deepslate_bricks",
        "level_3_to_4": "minecraft:block/white_concrete",
        "level_4_to_3": "minecraft:block/light_gray_concrete",
        "level_3_elevator": "minecraft:block/iron_door_bottom",
        "level_4_to_5": "minecraft:block/spruce_door_bottom",
        "level_5_to_3": "minecraft:block/iron_door_bottom",
        "level_5_to_4": "minecraft:block/spruce_door_bottom",
    }
    for passage_name, model in passage_models.items():
        blockstate = read_json(RES / f"assets/threshold/blockstates/{passage_name}.json")
        assert blockstate["variants"][""]["model"] == model

    elevator_states = read_json(RES / "assets/threshold/blockstates/level_5_elevator_panel.json")["variants"]
    assert len(elevator_states) == 12
    assert all(state["model"] == "minecraft:block/iron_block" for state in elevator_states.values())
    hotel_biome = read_json(RES / "data/threshold/worldgen/biome/level_5.json")
    assert hotel_biome["effects"]["mood_sound"]["sound"] == "minecraft:block.note_block.harp"

    catalog = read_json(RES / "data/threshold/transit/route_catalog.json")
    implemented = {route["id"]: route for route in catalog["implemented_routes"]}
    expected_routes = {
        "level_0_flickering_wall", "level_1_smooth_threshold",
        "level_2_fire_exit", "level_2_anomalous_branch",
        "level_2_unlocked_door", "level_3_retrace_corridor",
        "level_3_office_sector", "level_4_service_elevator_return",
        "level_3_elevator_to_level5", "level_4_stairway_to_level5",
        "level_5_elevator_return_to_level3", "level_5_stairway_return_to_level4",
        "level_5_internal_elevator_floors",
    }
    assert expected_routes <= set(implemented)
    assert implemented["level_2_fire_exit"]["profile"] == "fandom"
    assert implemented["level_2_fire_exit"]["relative_weights"] == [1, 1]
    assert implemented["level_2_anomalous_branch"]["profile"] == "wikidot"
    assert implemented["level_2_anomalous_branch"]["relative_weights"] == [4, 4, 1]
    assert all(route["status"] == "implemented" for route in implemented.values())
    deferred = {route["id"]: route for route in catalog["deferred_routes"]}
    assert implemented["level_3_elevator_to_level5"]["relative_weights"] == [9, 1]
    assert implemented["level_4_stairway_to_level5"]["to"] == ["level_5"]
    assert implemented["level_5_internal_elevator_floors"]["to"] == ["level_5"]
    assert "level_6" in deferred["level_5_boiler_deep_exit"]["to"]
    assert "level_9" in deferred["level_5_dark_oak_door"]["to"]
    assert "phantom" not in json.dumps(catalog).lower()

    modifier = read_json(RES / "data/threshold/neoforge/biome_modifier/room_layout.json")
    assert sorted(modifier["biomes"]) == [f"threshold:{level}" for level in levels]
    assert modifier["features"] == "threshold:room_sequence"

    sound_definitions = read_json(RES / "assets/threshold/sounds.json")
    for level in ("level_0", "level_1"):
        event = f"{level}_ambient"
        assert event in sound_definitions
        music = read_json(RES / f"data/threshold/worldgen/biome/{level}.json")["effects"]["music"]
        assert music["sound"] == f"threshold:{event}"
        assert music["replace_current_music"] is True
        raw = (RES / f"assets/threshold/sounds/{event}.ogg").read_bytes()
        assert raw[:4] == b"OggS" and len(raw) > 4000
    require(sounds_java, "LEVEL_0_AMBIENT", "Level 0 sound registry entry")
    require(sounds_java, "LEVEL_1_AMBIENT", "Level 1 sound registry entry")
    require(mod, "ThresholdSounds.SOUNDS.register(modBus)", "sound registry wiring")
    require(client_events, "EventBusSubscriber.Bus.MOD", "client-only mod-bus renderer subscriber")
    require(client_events, "value = Dist.CLIENT", "dedicated-server-safe client renderer gating")
    require(client_events, "EntityRenderersEvent.RegisterRenderers", "entity renderer registration event")
    require(client_events, "ThresholdEntities.HOUND.get(), ThresholdHoundRenderer::new", "Hound renderer registration")
    require(client_events, "ThresholdEntities.WATCHER.get(), ThresholdWatcherRenderer::new", "Watcher renderer registration")
    require(client_events, "ThresholdEntities.PARTYGOER.get(), ThresholdPartygoerRenderer::new", "Partygoer renderer registration")
    require(director, "spawnPartygoers(level)", "rare Overworld Partygoer encounter")
    require(director, "gameTime % 1200L", "slow Partygoer encounter cadence")
    require(director, "level.isDay()", "Partygoer night-only encounter")
    require(partygoer, "closeTicks >= 100", "Partygoer sustained close-range capture")
    require(partygoer, "ThresholdDimensions.LEVEL_0", "Partygoer sends player into Level 0")
    require(partygoer, "doHurtTarget", "Partygoer never damages the player")
    require(watcher, "tickCount > 180", "longer distant Watcher sightings")
    require(watcher, "distanceToSqr(nearest) < 72.25D", "Watcher disappears when approached")
    require(items, 'register("almond_water"', "Almond Water registry")
    require(items, 'register("flashlight"', "Flashlight registry")
    require(items, 'register("battery"', "Battery registry")
    require(flashlight, "MobEffects.NIGHT_VISION", "Flashlight activation effect")
    require(almond_water, "MobEffects.REGENERATION", "Almond Water restorative effect")

    allowed_agriculture = {
        "boiled_water_bottle", "filtered_water_bottle", "grain_porridge",
        "barley_vegetable_soup", "vegetable_stew", "garden_salad"
    }
    for filename in ("level_3_supply", "level_4_supply", "level_5_supply"):
        loot = read_json(RES / f"data/threshold/loot_table/chests/{filename}.json")
        assert loot["type"] == "minecraft:chest"
        for pool in loot["pools"]:
            for entry in pool["entries"]:
                if entry["type"] == "minecraft:item" and entry["name"].startswith("gugugaga:"):
                    assert entry["name"].split(":", 1)[1] in allowed_agriculture

    print("THRESHOLD_SIX_LEVEL_TRANSIT_AND_AUDIO_VALIDATION_PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
