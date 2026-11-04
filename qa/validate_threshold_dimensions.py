#!/usr/bin/env python3
"""Validate the independently packaged Threshold dimensions and worldgen resources."""
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
    "data/threshold/worldgen/configured_feature/room_layout.json",
    "data/threshold/worldgen/placed_feature/room_layout.json",
    "data/threshold/neoforge/biome_modifier/room_layout.json",
    "io/github/lordnavycrypto/threshold/ThresholdMod.class",
    "io/github/lordnavycrypto/threshold/worldgen/ThresholdRoomLayoutFeature.class",
    "io/github/lordnavycrypto/threshold/command/ThresholdCommands.class",
}


def load_json(archive: zipfile.ZipFile, path: str) -> dict:
    return json.loads(archive.read(path).decode("utf-8"))


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: validate_threshold_dimensions.py <threshold-jar>")
        return 2

    jar = Path(sys.argv[1])
    if not jar.is_file():
        print(f"FAIL: Threshold JAR not found: {jar}")
        return 1

    with zipfile.ZipFile(jar) as archive:
        names = set(archive.namelist())
        missing = sorted(REQUIRED - names)
        if missing:
            print("FAIL: missing packaged entries:")
            for item in missing:
                print(f"  - {item}")
            return 1

        metadata = archive.read("META-INF/neoforge.mods.toml").decode("utf-8")
        if 'modId="threshold"' not in metadata:
            raise AssertionError("Threshold JAR does not declare modId=threshold")
        if 'modId="gugugaga"' in metadata:
            raise AssertionError("Threshold JAR must not declare the Agriculture Mod ID")

        for level, type_id in (
            ("level_0", "threshold:level_0_type"),
            ("level_1", "threshold:level_1_type"),
        ):
            dimension = load_json(archive, f"data/threshold/dimension/{level}.json")
            if dimension.get("type") != type_id:
                raise AssertionError(f"{level} references the wrong dimension type")
            generator = dimension.get("generator", {})
            if generator.get("type") != "minecraft:flat":
                raise AssertionError(f"{level} must use the controlled flat canvas")
            settings = generator.get("settings", {})
            if settings.get("biome") != "minecraft:the_void" or settings.get("features") is not True:
                raise AssertionError(f"{level} must use the void biome with feature generation enabled")

            dimension_type = load_json(
                archive, f"data/threshold/dimension_type/{level}_type.json")
            if dimension_type.get("min_y") != -64 or dimension_type.get("height") != 384:
                raise AssertionError(f"{level} has unexpected build-height bounds")
            if dimension_type.get("has_skylight") is not False:
                raise AssertionError(f"{level} must not expose a normal sky")

        configured = load_json(
            archive, "data/threshold/worldgen/configured_feature/room_layout.json")
        placed = load_json(
            archive, "data/threshold/worldgen/placed_feature/room_layout.json")
        modifier = load_json(
            archive, "data/threshold/neoforge/biome_modifier/room_layout.json")

        if configured.get("type") != "threshold:room_layout":
            raise AssertionError("Configured feature does not target the registered feature")
        if placed.get("feature") != "threshold:room_layout":
            raise AssertionError("Placed feature does not target the configured feature")
        if modifier.get("features") != "threshold:room_layout":
            raise AssertionError("Biome modifier does not inject the room-layout feature")

    print(f"THRESHOLD_DIMENSION_ASSET_VALIDATION_PASS: {jar}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
