#!/usr/bin/env python3
"""Validate generated marine/fungi item icons and fungal culture stage textures."""
import hashlib
import json
import struct
import sys
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
def png_size(path):
    data = path.read_bytes()
    assert data[:8] == b"\x89PNG\r\n\x1a\n", f"not a PNG: {path}"
    return struct.unpack(">II", data[16:24])
def main():
    if len(sys.argv) != 2:
        raise SystemExit("usage: validate_generated_marine_fungi_art.py <generated-resource-root>")
    generated = Path(sys.argv[1])
    catalog = json.loads((RES / "data/gugugaga/agronomy/marine_fungi_catalog_v1_0.json").read_text(encoding="utf-8"))
    item_ids = [row["id"] for row in catalog["items"]] + [row["id"] for row in catalog["cultures"]]
    icon_hashes = {}
    for item_id in item_ids:
        path = generated / "assets/gugugaga/textures/item/marine_fungi" / f"{item_id}.png"
        assert path.is_file(), f"missing icon: {path}"
        assert png_size(path) == (16, 16), f"unexpected icon size: {path}"
        icon_hashes[item_id] = hashlib.sha256(path.read_bytes()).hexdigest()
    assert len(set(icon_hashes.values())) == 30, "marine/fungi inventory icons must all be unique"
    stage_hashes = {}
    for culture in catalog["cultures"]:
        for age in range(4):
            path = generated / "assets/gugugaga/textures/block/marine_fungi" / f'{culture["id"]}_stage{age}.png'
            assert path.is_file(), f"missing culture stage: {path}"
            assert png_size(path) == (16, 16), f"unexpected stage size: {path}"
            stage_hashes[f'{culture["id"]}_stage{age}'] = hashlib.sha256(path.read_bytes()).hexdigest()
    assert len(set(stage_hashes.values())) == 24, "fungal culture stages must all be unique"
    print("MARINE_FUNGI_ART_OK: 30 unique inventory icons and 24 unique fungal growth-stage textures")
if __name__ == "__main__":
    main()
