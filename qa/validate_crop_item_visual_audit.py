#!/usr/bin/env python3
"""Validate generated crop/seed icon coverage and review artifacts."""
import csv
import struct
import sys
from pathlib import Path

root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("build/reports/agriculture")
audit = root / "crop-item-icon-audit.csv"
assert audit.is_file(), f"missing icon audit CSV: {audit}"
with audit.open(newline="", encoding="utf-8") as stream:
    rows = list(csv.DictReader(stream))
assert len(rows) == 200, f"expected 200 crop item audit rows, got {len(rows)}"
assert len({(r["crop"], r["kind"]) for r in rows}) == 200, "duplicate crop/kind audit row"
crops = {r["crop"] for r in rows}
assert len(crops) == 100, f"expected 100 crop IDs, got {len(crops)}"
assert all(sum(1 for r in rows if r["crop"] == crop) == 2 for crop in crops), "every crop needs produce and seed entries"
for row in rows:
    assert row["kind"] in {"produce", "seed"}, row
    opaque = int(row["opaque_pixels"])
    assert 5 <= opaque <= 220, f"unexpected pixel coverage for {row['crop']} {row['kind']}: {opaque}"
    x0, y0, x1, y1 = (int(row[k]) for k in ("min_x", "min_y", "max_x", "max_y"))
    assert 0 <= x0 <= x1 < 16 and 0 <= y0 <= y1 < 16, row
    assert int(row["unique_rgb_colors"]) >= 1, row

def png_size(path):
    data = path.read_bytes()
    assert data[:8] == b"\x89PNG\r\n\x1a\n", f"not a PNG: {path}"
    return struct.unpack(">II", data[16:24])

expected = {
    "produce-icon-contact-sheet.png": (720, 1640),
    "seed-icon-contact-sheet.png": (720, 1640),
    "crop-item-pair-contact-sheet.png": (920, 1760),
}
for name, size in expected.items():
    path = root / name
    assert path.is_file(), f"missing review sheet: {path}"
    actual = png_size(path)
    assert actual == size, f"{name} dimensions {actual}, expected {size}"
print("CROP_ITEM_VISUAL_AUDIT_PASS: 100 crops, 200 distinct item audit rows, pixel bounds valid, produce/seed/pair review sheets present.")
