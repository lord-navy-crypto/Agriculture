#!/usr/bin/env python3
"""Protect THRESHOLD's original horror identity while Survival Horror is expanded."""
from __future__ import annotations

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold"
RESOURCES = ROOT / "threshold/src/main/resources"


def require(pattern: str, source: str, label: str) -> None:
    if not re.search(pattern, source, re.MULTILINE | re.DOTALL):
        raise AssertionError(f"THRESHOLD horror-preservation contract changed: {label}")


def read_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def main() -> int:
    layout = (JAVA / "worldgen/ThresholdRoomLayoutFeature.java").read_text(encoding="utf-8")
    lights = (JAVA / "block/FlickeringPanelBlock.java").read_text(encoding="utf-8")
    vent = (JAVA / "worldgen/ThresholdIndustrialVentFeature.java").read_text(encoding="utf-8")
    level0 = read_json(RESOURCES / "data/threshold/worldgen/biome/level_0.json")
    level1 = read_json(RESOURCES / "data/threshold/worldgen/biome/level_1.json")

    # Freeze the established spatial scale and vertical profile. Survival
    # balancing must not silently resize rooms or move floors and ceilings.
    constants = {
        "FLOOR_Y": 63,
        "LEVEL_0_ROOM_SIZE": 32,
        "LEVEL_0_CARPET_Y": 64,
        "LEVEL_0_WALL_BOTTOM": 65,
        "LEVEL_0_WALL_TOP": 71,
        "LEVEL_0_CEILING_Y": 72,
        "LEVEL_1_ROOM_SIZE": 48,
        "LEVEL_1_WALL_BOTTOM": 64,
        "LEVEL_1_WALL_TOP": 73,
        "LEVEL_1_CEILING_Y": 74,
    }
    for name, value in constants.items():
        require(rf"\b{name}\s*=\s*{value}\s*;", layout, f"{name} must remain {value}")

    # The entry spaces establish each level's original visual identity.
    require(
        r"if\s*\(roomX\s*==\s*0\s*&&\s*roomZ\s*==\s*0\)\s*return\s+0\s*;",
        layout,
        "Level 0 entry remains the baseline yellow-office variant",
    )
    require(
        r"if\s*\(roomX\s*==\s*0\s*&&\s*roomZ\s*==\s*0\)\s*return\s+1\s*;",
        layout,
        "Level 1 entry remains the stocked industrial-shelter variant",
    )
    for token, label in (
        ("Blocks.YELLOW_CARPET", "Level 0 yellow carpet"),
        ("Blocks.BROWN_CARPET", "Level 0 occasional worn carpet"),
        ("Blocks.GRAY_CARPET", "Level 0 alternate carpet"),
        ("Blocks.RED_CARPET", "Level 0 rare red-room carpet"),
        ("ThresholdEnvironmentBlocks.FADED_WALLPAPER", "Level 0 faded wallpaper"),
        ("ThresholdEnvironmentBlocks.STAINED_WALLPAPER", "Level 0 stained partitions"),
        ("ThresholdEnvironmentBlocks.WAREHOUSE_WALL", "Level 1 warehouse walls"),
        ("Blocks.SMOOTH_STONE", "Level 1 industrial floor"),
        ("Blocks.IRON_BARS", "Level 1 overhead industrial fittings"),
    ):
        if token not in layout:
            raise AssertionError(f"THRESHOLD horror-preservation contract changed: {label}")

    require("ThresholdEnvironmentBlocks.CEILING_VENT", vent,
            "Level 1 retains its authored animated ventilation unit")
    require(r"new BlockPos\(\(chunkX << 4\) \+ 12, 73, \(chunkZ << 4\) \+ 12\)",
            vent, "Level 1 vent remains at its established overhead placement")

    # Keep the known transition locations and the asymmetric route philosophy:
    # Level 0 can feel isolating; Level 1 rooms retain a deterministic exit.
    require(
        r"return\s+localX\s*==\s*0\s*&&\s*\(localZ\s*==\s*15\s*\|\|\s*localZ\s*==\s*16\)",
        layout,
        "Level 0 entry transition remains at its established wall",
    )
    require(
        r"return\s+localX\s*==\s*0\s*&&\s*\(localZ\s*==\s*23\s*\|\|\s*localZ\s*==\s*24\)",
        layout,
        "Level 1 return passage remains at its established wall",
    )
    require(
        r"Each Level 1 room chooses one deterministic exit",
        layout,
        "Level 1 escape invariant remains documented in code",
    )

    # Lighting remains different in each level: Level 0 has brief blinks;
    # Level 1's industrial faults have a longer dark interval.
    require(r"int offChance\s*=\s*level0\s*\?\s*16\s*:\s*10\s*;", lights,
            "Level 0 and Level 1 retain distinct light-failure rates")
    require(r"int restoreChance\s*=\s*level0\s*\?\s*2\s*:\s*4\s*;", lights,
            "Level 1 retains its longer dark recovery interval")

    # Preserve the original, distinct ambience and prohibit accidental mob
    # spawning in these environment-first builds.
    effects0 = level0["effects"]
    effects1 = level1["effects"]
    assert effects0["mood_sound"]["sound"] == "minecraft:ambient.cave", (
        "Level 0 must retain its cave-like empty-space ambience"
    )
    assert effects1["mood_sound"]["sound"] == "minecraft:block.vault.ambient", (
        "Level 1 must retain its subdued industrial ambience"
    )
    assert effects0["mood_sound"]["sound"] != effects1["mood_sound"]["sound"]
    assert effects0["fog_color"] != effects1["fog_color"]
    assert effects0["sky_color"] != effects1["sky_color"]
    for name, biome in (("Level 0", level0), ("Level 1", level1)):
        assert biome["spawners"] == {}, f"{name} must not gain natural mob spawners"
        assert biome["spawn_costs"] == {}, f"{name} must not gain mob spawn costs"

    java_sources = list((JAVA).rglob("*.java"))
    forbidden_entity_apis = re.compile(
        r"\baddFreshEntity\s*\(|\bEntityType\s*\.\s*Builder\b|"
        r"DeferredRegister\s*\.\s*create\s*\(\s*Registries\s*\.\s*ENTITY_TYPE"
    )
    for path in java_sources:
        source = path.read_text(encoding="utf-8")
        assert not forbidden_entity_apis.search(source), (
            f"Unexpected entity registration/spawn API in {path.relative_to(ROOT)}"
        )

    print("THRESHOLD_HORROR_IDENTITY_PRESERVATION_PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
