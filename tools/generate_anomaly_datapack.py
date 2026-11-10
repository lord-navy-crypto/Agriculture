#!/usr/bin/env python3
"""Create an opt-in, very sparse RealScale architecture pack for Threshold.

The 101 NBT files stay in the mod's normal data namespace for explicit
/ place template tests. The optional pack only carries world-generation
registries and Level 0/1 dimension overrides. Default worldgen is unchanged.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def write(root: Path, path: str, doc):
    file = root / path
    file.parent.mkdir(parents=True, exist_ok=True)
    file.write_text(json.dumps(doc, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def pool(name: str, paths: list[str], weights: dict[str, int] | None = None):
    return {
        "fallback": "minecraft:empty",
        "elements": [
            {
                "weight": (weights or {}).get(path.name, 1),
                "element": {
                    "element_type": "minecraft:single_pool_element",
                    "location": "threshold:anomaly/" + name + "/" + path.stem,
                    "processors": "minecraft:empty",
                    "projection": "rigid",
                },
            }
            for path in paths
        ],
    }


def structure(pool_id: str):
    return {
        "type": "minecraft:jigsaw",
        "biomes": "#threshold:anomaly_eligible",
        "step": "surface_structures",
        "spawn_overrides": {},
        "terrain_adaptation": "none",
        "start_pool": "threshold:anomaly/" + pool_id,
        "size": 0,
        # The ceiling is y=72/74: use a separate upper stratum so the
        # 94-block tower never tears through a guaranteed lower corridor.
        "start_height": {"absolute": 82},
        "max_distance_from_center": 80,
        "use_expansion_hack": False,
        "liquid_settings": "ignore_waterlogging",
    }


def structure_set(id: str, spacing: int, separation: int, salt: int):
    return {
        "structures": [{"structure": "threshold:anomaly_" + id, "weight": 1}],
        "placement": {
            "type": "minecraft:random_spread",
            "spacing": spacing, "separation": separation,
            "salt": salt, "spread_type": "linear",
        },
    }


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--templates", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    base = args.templates / "data/threshold/structure/anomaly"
    houses = sorted((base / "architecture").glob("*.nbt"))
    cities = sorted((base / "urban").glob("*.nbt"))
    if len(houses) != 97 or len(cities) != 4:
        raise SystemExit("Expected 97 original buildings and 4 original city quarters")
    profiles = json.loads((ROOT / "tools/realscale_originals/urban_district_profiles.json")
                          .read_text(encoding="utf-8"))
    weights = {x["id"] + ".nbt": x["weight"] for x in profiles["districts"]}
    if set(weights) != {p.name for p in cities}:
        raise SystemExit("Urban profile/template mismatch")
    root = args.output / "resourcepacks/threshold_anomalies"
    write(root, "pack.mcmeta", {
        "pack": {"pack_format": 48,
                 "description": "Backroom Agriculture | Impossible Architecture (opt-in, experimental)"}
    })
    write(root, "data/threshold/worldgen/template_pool/anomaly/architecture.json",
          pool("architecture", houses))
    write(root, "data/threshold/worldgen/template_pool/anomaly/urban.json",
          pool("urban", cities, weights))
    for name in ("architecture", "urban"):
        write(root, "data/threshold/worldgen/structure/anomaly_" + name + ".json",
              structure(name))
    write(root, "data/threshold/worldgen/structure_set/anomaly_architecture.json",
          structure_set("architecture", spacing=96, separation=32, salt=142011))
    write(root, "data/threshold/worldgen/structure_set/anomaly_urban.json",
          structure_set("urban", spacing=192, separation=64, salt=142012))
    write(root, "data/threshold/tags/worldgen/biome/anomaly_eligible.json",
          {"replace": False, "values": ["threshold:level_0", "threshold:level_1"]})
    for dimension in ("level_0", "level_1"):
        source = ROOT / "threshold/src/main/resources/data/threshold/dimension" / (dimension + ".json")
        existing = json.loads(source.read_text(encoding="utf-8"))
        if existing["generator"]["type"] != "minecraft:flat":
            raise SystemExit("Unexpected dimension generator: " + dimension)
        if existing["generator"]["settings"]["structure_overrides"] != []:
            raise SystemExit("Default generation has changed; audit before overriding")
        existing["generator"]["settings"]["structure_overrides"] = [
            "threshold:anomaly_architecture", "threshold:anomaly_urban"]
        write(root, "data/threshold/dimension/" + dimension + ".json", existing)
    print("THRESHOLD_ANOMALY_OPTIN_PACK_OK: 101 templates, two rare structure sets, two dimensions")


if __name__ == "__main__":
    main()
