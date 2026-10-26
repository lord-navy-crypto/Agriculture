#!/usr/bin/env python3
"""Validate generated crop-cuisine PNG icons and the visual contact sheet."""
import hashlib
import json
import sys
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
def main():
    if len(sys.argv) != 3:
        raise SystemExit("usage: validate_generated_crop_cuisine_art.py <generated-resource-root> <contact-sheet>")
    generated = Path(sys.argv[1])
    sheet = Path(sys.argv[2])
    catalog = json.loads((RES / "data/gugugaga/agronomy/crop_cuisine_catalog_v1_0.json").read_text(encoding="utf-8"))
    ids = [item["id"] for item in catalog["items"]]
    hashes = set()
    for item_id in ids:
        path = generated / "assets/gugugaga/textures/item/crop_cuisine" / f"{item_id}.png"
        assert path.is_file(), f"missing generated icon: {item_id}"
        with Image.open(path) as image:
            image.load()
            assert image.size == (16, 16), (item_id, image.size)
            assert image.mode == "RGBA", (item_id, image.mode)
            assert image.getchannel("A").getbbox() is not None, f"empty icon: {item_id}"
            assert len(set(image.getdata())) >= 8, f"icon too visually uniform: {item_id}"
            hashes.add(hashlib.sha256(path.read_bytes()).hexdigest())
    assert len(hashes) == len(ids), "duplicate crop-cuisine icons detected"
    assert sheet.is_file(), "missing crop-cuisine contact sheet"
    with Image.open(sheet) as image:
        assert image.size == (288, 288), image.size
    print(f"CROP_CUISINE_ART_PASS: {len(ids)} unique 16x16 RGBA icons and visual contact sheet verified.")
if __name__ == "__main__":
    main()
