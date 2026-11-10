#!/usr/bin/env python3
"""Build the optional Backrooms anomaly worldgen pack.

The authored city districts include their own sparse lift shafts. The other
anomalies reuse vanilla 1.21.1 structure templates/pools at a safe upper Y.
"""
from __future__ import annotations

import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LEVELS = tuple(f"level_{index}" for index in range(6))


def write(root: Path, path: str, doc):
    file = root / path
    file.parent.mkdir(parents=True, exist_ok=True)
    file.write_text(json.dumps(doc, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def pool(name: str, paths: list[Path], weights: dict[str, int] | None = None):
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


def vanilla_pool(entries: list[tuple[str, int, str]]):
    return {
        "fallback": "minecraft:empty",
        "elements": [
            {
                "weight": weight,
                "element": {
                    "element_type": "minecraft:single_pool_element",
                    "location": location,
                    "processors": processors,
                    "projection": "rigid",
                },
            }
            for location, weight, processors in entries
        ],
    }


def structure(
    pool_id: str,
    *,
    start_y: int = 82,
    size: int = 0,
    max_distance: int = 80,
    start_jigsaw_name: str | None = None,
):
    result = {
        "type": "minecraft:jigsaw",
        "biomes": "#threshold:anomaly_eligible",
        "step": "top_layer_modification",
        "spawn_overrides": {},
        "terrain_adaptation": "none",
        "start_pool": pool_id if ":" in pool_id else "threshold:anomaly/" + pool_id,
        "size": size,
        "start_height": {"absolute": start_y},
        "max_distance_from_center": max_distance,
        "use_expansion_hack": False,
        "liquid_settings": "ignore_waterlogging",
    }
    if start_jigsaw_name:
        result["start_jigsaw_name"] = start_jigsaw_name
    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--templates", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    base = args.templates / "data/threshold/structure/anomaly"
    houses = sorted((base / "architecture").glob("*.nbt"))
    cities = sorted((base / "urban").glob("*.nbt"))
    if len(houses) != 97 or len(cities) != 4:
        raise SystemExit("Expected 97 original buildings and 4 connected city districts")
    profiles = json.loads((ROOT / "tools/realscale_originals/urban_district_profiles.json")
                          .read_text(encoding="utf-8"))
    weights = {x["id"] + ".nbt": x["weight"] for x in profiles["districts"]}
    if set(weights) != {p.name for p in cities}:
        raise SystemExit("Urban profile/template mismatch")

    root = args.output / "resourcepacks/threshold_anomalies"
    write(root, "pack.mcmeta", {
        "pack": {
            "pack_format": 48,
            "description": "Backroom Agriculture | Impossible Architecture (opt-in, experimental)"
        }
    })

    write(root, "data/threshold/worldgen/template_pool/anomaly/architecture.json",
          pool("architecture", houses))
    write(root, "data/threshold/worldgen/template_pool/anomaly/urban.json",
          pool("urban", cities, weights))

    portals = [(f"minecraft:ruined_portal/portal_{i}", 4, "minecraft:empty")
               for i in range(1, 11)]
    portals += [(f"minecraft:ruined_portal/giant_portal_{i}", 1, "minecraft:empty")
                for i in range(1, 4)]
    write(root, "data/threshold/worldgen/template_pool/anomaly/vanilla_portals.json",
          vanilla_pool(portals))

    shipwrecks = [
        "with_mast", "sideways_full", "sideways_fronthalf", "sideways_backhalf",
        "rightsideup_full", "rightsideup_fronthalf", "rightsideup_backhalf",
        "with_mast_degraded", "sideways_full_degraded", "rightsideup_full_degraded",
        "rightsideup_backhalf_degraded",
    ]
    write(root, "data/threshold/worldgen/template_pool/anomaly/vanilla_shipwrecks.json",
          vanilla_pool([(f"minecraft:shipwreck/{name}", 1, "minecraft:empty")
                        for name in shipwrecks]))

    structures = {
        "anomaly_architecture": structure("architecture", start_y=82, size=0, max_distance=80),
        # The city template starts at the lower maze floor and rises to y=82.
        "anomaly_urban": structure("urban", start_y=63, size=0, max_distance=80),
        # Vanilla village town-centre fragments are rigid and deliberately
        # stop at size zero: no terrain-matching roads can sink into the maze.
        "anomaly_village_fragment": structure(
            "minecraft:village/plains/town_centers", start_y=82, size=0, max_distance=80),
        "anomaly_ancient_city_fragment": structure(
            "minecraft:ancient_city/city_center", start_y=82, size=0,
            max_distance=116, start_jigsaw_name="minecraft:city_anchor"),
        "anomaly_ruined_portal": structure("vanilla_portals", start_y=82, size=0, max_distance=80),
        "anomaly_shipwreck": structure("vanilla_shipwrecks", start_y=82, size=0, max_distance=80),
    }
    for name, definition in structures.items():
        write(root, "data/threshold/worldgen/structure/" + name + ".json", definition)

    # Each Backrooms dimension gets its own salted placement set. This keeps
    # the complete anomaly library available on all six floors without making
    # the same seed choose identical X/Z candidates in every dimension.
    anomaly_families = [
        {"structure": "threshold:anomaly_architecture", "weight": 4},
        {"structure": "threshold:anomaly_urban", "weight": 2},
        {"structure": "threshold:anomaly_village_fragment", "weight": 1},
        {"structure": "threshold:anomaly_ancient_city_fragment", "weight": 1},
        {"structure": "threshold:anomaly_ruined_portal", "weight": 1},
        {"structure": "threshold:anomaly_shipwreck", "weight": 1},
    ]
    for index, dimension in enumerate(LEVELS):
        set_id = "anomaly_structures_" + dimension
        # Keep every structure family available on every floor. Levels 0 and 1
        # use a wider candidate grid to make anomalies less common, not absent.
        spacing = 80 if index < 2 else 64
        write(root, "data/threshold/worldgen/structure_set/" + set_id + ".json", {
            "structures": anomaly_families,
            "placement": {
                "type": "minecraft:random_spread",
                "spacing": spacing,
                "separation": 24,
                "salt": 142011 + index * 1009,
                "spread_type": "linear",
            },
        })

    write(root, "data/threshold/tags/worldgen/biome/anomaly_eligible.json",
          {"replace": False, "values": [f"threshold:{dimension}" for dimension in LEVELS]})
    for dimension in LEVELS:
        source = ROOT / "threshold/src/main/resources/data/threshold/dimension" / (dimension + ".json")
        existing = json.loads(source.read_text(encoding="utf-8"))
        if existing["generator"]["type"] != "minecraft:flat":
            raise SystemExit("Unexpected dimension generator: " + dimension)
        if existing["generator"]["settings"]["structure_overrides"] != []:
            raise SystemExit("Default generation has changed; audit before overriding")
        existing["generator"]["settings"]["structure_overrides"] = [
            "threshold:anomaly_structures_" + dimension]
        write(root, "data/threshold/dimension/" + dimension + ".json", existing)

    print(
        "THRESHOLD_ANOMALY_PACK_OK: 97 original buildings, 4 lift-connected city districts, "
        "4 vanilla structure families across all six Backrooms dimensions; Levels 0-1 use lower frequency"
    )


if __name__ == "__main__":
    main()
