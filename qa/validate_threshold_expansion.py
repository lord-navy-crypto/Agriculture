#!/usr/bin/env python3
"""Validate the four-level Threshold expansion, audio assets and traversal graph."""
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
    passages = (JAVA / "block/ThresholdPassageBlock.java").read_text(encoding="utf-8")
    blocks = (JAVA / "block/ThresholdEnvironmentBlocks.java").read_text(encoding="utf-8")
    sounds_java = (JAVA.parent / "ThresholdSounds.java").read_text(encoding="utf-8")
    mod = (JAVA.parent / "ThresholdMod.java").read_text(encoding="utf-8")
    commands = (JAVA / "command/ThresholdCommands.java").read_text(encoding="utf-8")

    for level in ("level_0", "level_1", "level_3", "level_4"):
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

    require(dimensions, "LEVEL_3", "Level 3 dimension key")
    require(dimensions, "LEVEL_4", "Level 4 dimension key")
    require(layout, "buildLevel3Chunk(world, chunkX, chunkZ)", "Level 3 world generation dispatch")
    require(layout, "buildLevel4Chunk(world, chunkX, chunkZ)", "Level 4 world generation dispatch")
    require(layout, "level3Architecture", "deterministic Level 3 room archetypes")
    require(layout, "level4Architecture", "deterministic Level 4 room archetypes")
    require(layout, "LEVEL_3_ROOM_SIZE = 32", "Level 3 room grid")
    require(layout, "LEVEL_4_ROOM_SIZE = 48", "Level 4 room grid")
    require(layout, "Blocks.COPPER_BLOCK", "Level 3 electrical pipework")
    require(layout, "Blocks.BLACK_STAINED_GLASS", "Level 4 blacked-out windows")
    require(layout, "LEVEL_3_SUPPLY_LOOT", "Level 3 Agriculture supply cache")
    require(layout, "LEVEL_4_SUPPLY_LOOT", "Level 4 Agriculture supply cache")

    for token, label in (
        ("LEVEL_1_TO_3", "Level 1 to Level 3 service lift"),
        ("LEVEL_3_TO_1", "Level 3 return lift"),
        ("LEVEL_3_TO_4", "Level 3 office-sector door"),
        ("LEVEL_4_TO_3", "Level 4 return elevator"),
    ):
        require(blocks, token, label)
    require(passages, "teleportTo(destination", "passage block performs dimension travel")
    require(commands, 'literal("level_3")', "operator travel command for Level 3")
    require(commands, 'literal("level_4")', "operator travel command for Level 4")

    modifier = read_json(RES / "data/threshold/neoforge/biome_modifier/room_layout.json")
    assert sorted(modifier["biomes"]) == [
        "threshold:level_0", "threshold:level_1",
        "threshold:level_3", "threshold:level_4"
    ]
    assert modifier["features"] == "threshold:room_sequence"

    sound_definitions = read_json(RES / "assets/threshold/sounds.json")
    for level in ("level_0", "level_1"):
        event = f"{level}_ambient"
        assert event in sound_definitions
        music = read_json(RES / f"data/threshold/worldgen/biome/{level}.json")["effects"]["music"]
        assert music["sound"] == f"threshold:{event}"
        assert music["replace_current_music"] is True
        ogg = RES / f"assets/threshold/sounds/{event}.ogg"
        raw = ogg.read_bytes()
        assert raw[:4] == b"OggS" and len(raw) > 4000
    require(sounds_java, "LEVEL_0_AMBIENT", "Level 0 sound registry entry")
    require(sounds_java, "LEVEL_1_AMBIENT", "Level 1 sound registry entry")
    require(mod, "ThresholdSounds.SOUNDS.register(modBus)", "sound registry wiring")

    allowed_agriculture = {
        "boiled_water_bottle", "filtered_water_bottle", "grain_porridge",
        "barley_vegetable_soup", "vegetable_stew", "garden_salad"
    }
    for filename in ("level_3_supply", "level_4_supply"):
        loot = read_json(RES / f"data/threshold/loot_table/chests/{filename}.json")
        assert loot["type"] == "minecraft:chest"
        for pool in loot["pools"]:
            for entry in pool["entries"]:
                if entry["type"] == "minecraft:item" and entry["name"].startswith("gugugaga:"):
                    assert entry["name"].split(":", 1)[1] in allowed_agriculture

    print("THRESHOLD_FOUR_LEVEL_AUDIO_AND_TRAVERSAL_VALIDATION_PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
