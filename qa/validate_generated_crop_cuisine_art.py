#!/usr/bin/env python3
"""Validate generated crop-cuisine PNG icons using only the Python standard library."""
import hashlib
import json
import struct
import sys
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src/main/resources"
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"

def read_png(path):
    data = path.read_bytes()
    assert data.startswith(PNG_SIGNATURE), f"not a PNG: {path}"
    offset = len(PNG_SIGNATURE)
    width = height = bit_depth = color_type = None
    compressed = bytearray()
    while offset < len(data):
        length = struct.unpack(">I", data[offset:offset + 4])[0]
        kind = data[offset + 4:offset + 8]
        payload = data[offset + 8:offset + 8 + length]
        crc = struct.unpack(">I", data[offset + 8 + length:offset + 12 + length])[0]
        assert zlib.crc32(kind + payload) & 0xFFFFFFFF == crc, f"bad PNG chunk CRC: {path}"
        if kind == b"IHDR":
            width, height, bit_depth, color_type, compression, filtering, interlace = struct.unpack(">IIBBBBB", payload)
            assert compression == 0 and filtering == 0 and interlace == 0, f"unsupported PNG encoding: {path}"
        elif kind == b"IDAT":
            compressed.extend(payload)
        elif kind == b"IEND":
            break
        offset += 12 + length
    assert width and height and bit_depth == 8 and color_type == 6, f"expected 8-bit RGBA PNG: {path}"
    raw = zlib.decompress(compressed)
    stride = width * 4
    assert len(raw) == height * (stride + 1), f"unexpected PNG scanline size: {path}"
    pixels = bytearray(height * stride)
    src = 0
    for y in range(height):
        filter_type = raw[src]
        src += 1
        row = bytearray(raw[src:src + stride])
        src += stride
        prior = pixels[(y - 1) * stride:y * stride] if y else bytearray(stride)
        for x in range(stride):
            left = row[x - 4] if x >= 4 else 0
            up = prior[x]
            upper_left = prior[x - 4] if x >= 4 else 0
            if filter_type == 1:
                row[x] = (row[x] + left) & 255
            elif filter_type == 2:
                row[x] = (row[x] + up) & 255
            elif filter_type == 3:
                row[x] = (row[x] + ((left + up) // 2)) & 255
            elif filter_type == 4:
                p = left + up - upper_left
                pa, pb, pc = abs(p - left), abs(p - up), abs(p - upper_left)
                predictor = left if pa <= pb and pa <= pc else up if pb <= pc else upper_left
                row[x] = (row[x] + predictor) & 255
            elif filter_type != 0:
                raise AssertionError(f"unknown PNG filter {filter_type}: {path}")
        pixels[y * stride:(y + 1) * stride] = row
    colors = {tuple(pixels[i:i + 4]) for i in range(0, len(pixels), 4)}
    visible = any(pixels[i] for i in range(3, len(pixels), 4))
    return width, height, colors, visible

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
        width, height, colors, visible = read_png(path)
        assert (width, height) == (16, 16), (item_id, width, height)
        assert visible, f"empty icon: {item_id}"
        assert len(colors) >= 5, f"icon too visually uniform: {item_id}"
        hashes.add(hashlib.sha256(path.read_bytes()).hexdigest())
    assert len(hashes) == len(ids), "duplicate crop-cuisine icons detected"
    assert sheet.is_file(), "missing crop-cuisine contact sheet"
    width, height, _, visible = read_png(sheet)
    assert (width, height) == (288, 288), (width, height)
    assert visible, "empty crop-cuisine contact sheet"
    print(f"CROP_CUISINE_ART_PASS: {len(ids)} unique 16x16 RGBA icons and visual contact sheet verified.")

if __name__ == "__main__":
    main()
