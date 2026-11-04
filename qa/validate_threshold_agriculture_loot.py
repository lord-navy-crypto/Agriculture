#!/usr/bin/env python3
"""Cross-check Backrooms survival loot with real Agriculture item declarations."""
from __future__ import annotations

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
AGRI = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
LOOT = ROOT / "threshold/src/main/resources/data/threshold/loot_table/chests"
FILES = ("AgricultureContent.java", "FoodV34Content.java", "WaterV37Content.java")


def registered_items() -> set[str]:
    ids: set[str] = set()
    for name in FILES:
        source = (AGRI / name).read_text(encoding="utf-8")
        ids.update(re.findall(
            r'\b(?:food|seed|ingredient|ITEMS\.register)\("([a-z0-9_]+)"', source
        ))
    return ids


def check() -> None:
    known = registered_items()
    used: set[str] = set()
    for path in sorted(LOOT.glob("level_*.json")):
        data = json.loads(path.read_text(encoding="utf-8"))
        for pool in data["pools"]:
            for entry in pool["entries"]:
                if entry["type"] != "minecraft:item":
                    continue
                name = entry["name"]
                if name.startswith("gugugaga:"):
                    used.add(name.split(":", 1)[1])
    missing = sorted(used - known)
    assert not missing, f"Unregistered Agriculture loot IDs: {missing}"
    required = {"barley", "soybean", "tomato", "barley_seeds",
                "boiled_water_bottle", "raw_water_bottle",
                "filtered_water_bottle"}
    assert required <= used, f"Missing survival resources: {required - used}"
    print("THRESHOLD_AGRICULTURE_LOOT_REGISTRY_CONTRACT_PASS",
          f"({len(used)} registered Agriculture item types)")


if __name__ == "__main__":
    check()
