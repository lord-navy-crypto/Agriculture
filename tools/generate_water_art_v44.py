#!/usr/bin/env python3
"""Generate bespoke, deterministic 16x16 water sprites for Backroom Agriculture.

No external image libraries or runtime downloads; built from editable pixel
shapes and PNG encoded with Python's standard library during processResources.
"""
from __future__ import annotations

import argparse
from pathlib import Path
import struct
import zlib

SIZE = 16
INK = 0x303D48
GLASS = 0xA6DCE1
LIGHT = 0xE1F7ED
STEEL = 0x596D80
STEEL_LIGHT = 0xBBCDD0


def rgba(rgb: int, alpha: int = 255) -> bytes:
    return bytes(((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255, alpha))


class Sprite:
    def __init__(self):
        self.pixels = bytearray(SIZE * SIZE * 4)

    def dot(self, x: int, y: int, color: int, alpha: int = 255):
        if 0 <= x < SIZE and 0 <= y < SIZE:
            n = 4 * (y * SIZE + x)
            self.pixels[n:n + 4] = rgba(color, alpha)

    def fill(self, xa: int, ya: int, xb: int, yb: int, color: int, alpha: int = 255):
        for y in range(ya, yb + 1):
            for x in range(xa, xb + 1):
                self.dot(x, y, color, alpha)

    def png(self) -> bytes:
        raw = b"".join(b"\x00" + self.pixels[y * SIZE * 4:(y + 1) * SIZE * 4]
                       for y in range(SIZE))
        def chunk(tag: bytes, data: bytes) -> bytes:
            return (struct.pack(">I", len(data)) + tag + data
                    + struct.pack(">I", zlib.crc32(tag + data) & 0xffffffff))
        return (b"\x89PNG\r\n\x1a\n"
                + chunk(b"IHDR", struct.pack(">IIBBBBB", SIZE, SIZE, 8, 6, 0, 0, 0))
                + chunk(b"IDAT", zlib.compress(raw, 9))
                + chunk(b"IEND", b""))


def flask(liquid: str | None) -> Sprite:
    a = Sprite()
    # Distinct long-necked field flask, rim and beveled clear-glass shoulders.
    a.fill(6, 0, 9, 1, INK)
    a.fill(7, 1, 8, 3, GLASS)
    a.fill(5, 3, 10, 3, INK)
    a.fill(4, 4, 11, 4, GLASS)
    a.fill(3, 5, 12, 12, INK)
    a.fill(4, 5, 11, 12, 0xD6F4F2, 170)
    a.fill(4, 13, 11, 13, INK)
    a.fill(5, 14, 10, 14, STEEL)
    a.fill(5, 5, 5, 11, LIGHT)
    a.fill(10, 5, 10, 11, STEEL_LIGHT)
    if liquid is not None:
        color = {"raw": 0x809D78, "boiled": 0x59A8D9,
                 "filtered": 0x66CCD0, "almond": 0xDBBA72}[liquid]
        darker = {"raw": 0x5D7C5B, "boiled": 0x357CBA,
                  "filtered": 0x298FA6, "almond": 0xA87A48}[liquid]
        a.fill(5, 8, 10, 12, color)
        a.fill(5, 12, 10, 12, darker)
        a.fill(6, 8, 8, 8, LIGHT)
        a.dot(6, 10, LIGHT)
        if liquid == "raw":
            a.dot(9, 10, 0x586C46)
            a.dot(8, 11, 0xAFC0A0)
        elif liquid == "boiled":
            a.dot(7, 6, 0xFFFFFF)
            a.dot(9, 5, 0xFFFFFF)
        elif liquid == "filtered":
            a.fill(7, 10, 8, 10, 0xF2FFFF)
            a.dot(8, 11, 0xF2FFFF)
        else:  # almond water: beige tag and warm liquid, not a vanilla potion.
            a.fill(4, 9, 11, 10, 0xD7CAB0)
            a.fill(6, 9, 9, 9, 0x785A39)
            a.dot(7, 10, 0x785A39)
    else:
        a.dot(6, 8, 0xE3FAFC, 170)
        a.dot(9, 11, 0xA0BBCB, 170)
    return a


def bucket(filled: bool) -> Sprite:
    a = Sprite()
    # Rounded iron carrying handle + reinforced bucket rim.
    a.fill(5, 0, 10, 0, STEEL)
    a.dot(4, 1, STEEL)
    a.dot(11, 1, STEEL)
    a.dot(3, 2, STEEL)
    a.dot(12, 2, STEEL)
    a.fill(3, 3, 12, 4, INK)
    a.fill(4, 4, 11, 11, STEEL)
    a.fill(5, 5, 10, 10, STEEL_LIGHT)
    a.fill(5, 12, 10, 13, INK)
    a.fill(4, 5, 4, 10, INK)
    a.fill(11, 5, 11, 10, INK)
    a.fill(6, 6, 6, 9, LIGHT)
    if filled:
        a.fill(5, 4, 10, 8, 0x4A9BC9)
        a.fill(5, 8, 10, 9, 0x2E7FAE)
        a.fill(6, 4, 8, 4, 0x9BE6EA)
        a.dot(9, 6, LIGHT)
        a.fill(6, 11, 9, 11, 0x2AADA6)  # distinct sampler gauge
    else:
        a.fill(7, 9, 8, 11, 0x31565A)
    return a


SPRITES = {
    "gugugaga": {
        "empty_water_flask": flask(None),
        "raw_water_bottle": flask("raw"),
        "boiled_water_bottle": flask("boiled"),
        "filtered_water_bottle": flask("filtered"),
        "field_sampling_bucket": bucket(False),
        "sampled_water_bucket": bucket(True),
    },
    "threshold": {"almond_water": flask("almond")},
}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    n = 0
    for namespace, icons in SPRITES.items():
        for name, sprite in icons.items():
            file = args.output / "assets" / namespace / "textures" / "item" / "water_v44" / (name + ".png")
            file.parent.mkdir(parents=True, exist_ok=True)
            file.write_bytes(sprite.png())
            n += 1
    print(f"BACKROOM_WATER_V44_ART_OK: {n} original deterministic 16x16 sprites")


if __name__ == "__main__":
    main()
