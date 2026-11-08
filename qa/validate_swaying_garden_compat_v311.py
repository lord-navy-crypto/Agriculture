#!/usr/bin/env python3
"""Validate Swaying Garden block-tag interoperability for Minecraft 1.21.1."""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
TAGS = ROOT / "src/main/resources/data/gugugaga/tags/block"

def matches(path, pattern):
    source = path.read_text(encoding="utf-8")
    return {match.group(1) for match in re.finditer(pattern, source)}

def read_tag(name):
    data = json.loads((TAGS / name).read_text(encoding="utf-8"))
    assert data.get("replace") is False, name
    values = data.get("values")
    assert isinstance(values, list) and values, name
    assert len(values) == len(set(values)), name + ": duplicate entries"
    assert values == sorted(values), name + ": values must be stable and sorted"
    assert all(value.startswith("gugugaga:") for value in values), name
    return {value.split(":", 1)[1] for value in values}

def main():
    agriculture = JAVA / "AgricultureContent.java"
    extra = JAVA / "FoodEcologyV33Content.java"
    marine = JAVA / "MarineFungiContent.java"

    crop_blocks = matches(agriculture, r'crop\("([^"]+_crop)"')
    wild_blocks = matches(agriculture, r'BLOCKS\.register\("(wild_[^"]+)"')
    spice_blocks = matches(extra, r'crop\("([^"]+_crop)"')
    marine_blocks = matches(marine, r'marineCrop\("([^"]+)"')
    fungal_blocks = matches(marine, r'culture\("([^"]+)"')

    expected_plants = crop_blocks | wild_blocks | spice_blocks | marine_blocks
    expected_fungi = fungal_blocks
    actual_plants = read_tag("wind_swaying_plants.json")
    actual_fungi = read_tag("wind_swaying_fungi.json")

    assert len(crop_blocks) == 100, len(crop_blocks)
    assert len(wild_blocks) == 100, len(wild_blocks)
    assert len(spice_blocks) == 8, len(spice_blocks)
    assert len(marine_blocks) == 4, len(marine_blocks)
    assert len(fungal_blocks) == 6, len(fungal_blocks)
    assert actual_plants == expected_plants, (
        "wind_swaying_plants mismatch: "
        f"missing={sorted(expected_plants-actual_plants)}, "
        f"unexpected={sorted(actual_plants-expected_plants)}"
    )
    assert actual_fungi == expected_fungi, (
        "wind_swaying_fungi mismatch: "
        f"missing={sorted(expected_fungi-actual_fungi)}, "
        f"unexpected={sorted(actual_fungi-expected_fungi)}"
    )
    assert actual_plants.isdisjoint(actual_fungi)
    print(
        "SWAYING_GARDEN_COMPAT_V311_PASS: "
        f"{len(actual_plants)} crop/wild/marine plant blocks and "
        f"{len(actual_fungi)} fungal culture blocks are exposed as stable tags."
    )

if __name__ == "__main__":
    main()
