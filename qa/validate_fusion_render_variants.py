#!/usr/bin/env python3
"""Verify shipped Fusion crop/marine art against the exact distributable mod JAR.

No additional Python dependencies are required.
Usage: python3 qa/validate_fusion_render_variants.py <mod.jar> <fusion-pack.zip>
"""
from __future__ import annotations

import math
import struct
import sys
import zipfile
import zlib

PNG_SIG = b"\x89PNG\r\n\x1a\n"
STAGE_PREFIXES = (
    "assets/gugugaga/textures/block/crops/",
    "assets/gugugaga/textures/block/marine_fungi/",
    "assets/gugugaga/textures/block/marine_plants/",
)
EXPECTED_COUNTS = (1200, 24, 16)


def png_pixels(contents: bytes):
    assert contents[:8] == PNG_SIG, "bad PNG signature"
    at = 8
    compressed = bytearray()
    width = height = None
    while at < len(contents):
        length = struct.unpack_from(">I", contents, at)[0]
        kind = contents[at + 4:at + 8]
        payload = contents[at + 8:at + 8 + length]
        crc = struct.unpack_from(">I", contents, at + 8 + length)[0]
        assert zlib.crc32(kind + payload) & 0xFFFFFFFF == crc, "bad PNG CRC"
        at += length + 12
        if kind == b"IHDR":
            width, height, depth, color, compression, filtering, interlace = struct.unpack(">IIBBBBB", payload)
            assert (depth, color, compression, filtering, interlace) == (8, 6, 0, 0, 0), "unexpected PNG format"
        elif kind == b"IDAT":
            compressed.extend(payload)
        elif kind == b"IEND":
            break
    assert width and height
    decoded = zlib.decompress(compressed)
    pitch = width * 4
    pixels = bytearray(height * pitch)
    pos = 0
    for y in range(height):
        filter_type = decoded[pos]
        pos += 1
        row = bytearray(decoded[pos:pos + pitch])
        pos += pitch
        previous = pixels[(y - 1) * pitch:y * pitch] if y else bytearray(pitch)
        for col in range(pitch):
            left = row[col - 4] if col >= 4 else 0
            above = previous[col]
            above_left = previous[col - 4] if col >= 4 else 0
            if filter_type == 1:
                adjustment = left
            elif filter_type == 2:
                adjustment = above
            elif filter_type == 3:
                adjustment = (left + above) // 2
            elif filter_type == 4:
                predictor = left + above - above_left
                da, db, dc = abs(predictor - left), abs(predictor - above), abs(predictor - above_left)
                adjustment = left if da <= db and da <= dc else above if db <= dc else above_left
            elif filter_type == 0:
                adjustment = 0
            else:
                raise AssertionError(f"unsupported PNG filter: {filter_type}")
            row[col] = (row[col] + adjustment) & 255
        pixels[y * pitch:(y + 1) * pitch] = row
    assert pos == len(decoded), "wrong PNG scanline length"
    return width, height, bytes(pixels)


def assert_variants(original: bytes, atlas: bytes, name: str):
    w, h, source = png_pixels(original)
    aw, ah, dest = png_pixels(atlas)
    assert (w, h) == (16, 16), f"{name}: source must be 16x16"
    assert (aw, ah) == (32, 32), f"{name}: atlas must be 32x32"
    variant_pixels = set()
    texture_seed = sum(name.rsplit("/", 1)[-1].encode("utf-8"))
    factors = (1.0, 1.025, 0.965, 1.045)
    for variant, factor in enumerate(factors):
        mirrored = variant in (1, 3)
        tile = bytearray()
        for y in range(16):
            for x in range(16):
                source_x = 15 - x if mirrored else x
                input_offset = (y * 16 + source_x) * 4
                output_offset = ((y + variant // 2 * 16) * 32 + x + variant % 2 * 16) * 4
                pixel = source[input_offset:input_offset + 4]
                if pixel[3] == 0:
                    expected = b"\x00\x00\x00\x00"
                elif variant == 0:
                    expected = pixel
                else:
                    red, green, blue, alpha = pixel
                    foliage = green > red * 1.10 and green > blue * 1.05
                    red_factor = green_factor = blue_factor = factor
                    detail_seed = (x * 7 + y * 11 + texture_seed + variant * 17)
                    if foliage and variant == 1 and detail_seed % 19 == 0:
                        red_factor *= 0.96
                        green_factor *= 1.10
                        blue_factor *= 0.97
                    elif foliage and variant == 2 and detail_seed % 17 == 0:
                        red_factor *= 0.82
                        green_factor *= 0.84
                        blue_factor *= 0.82
                    elif foliage and variant == 3 and detail_seed % 13 == 0:
                        red_factor *= 1.03
                        green_factor *= 1.10
                        blue_factor *= 0.98
                    offsets = {
                        1: (-1, 1, -1),
                        2: (1, -1, 1),
                        3: (-1, 2, 1),
                    }[variant]
                    channels = (
                        math.floor(red * red_factor + 0.5) + offsets[0],
                        math.floor(green * green_factor + 0.5) + offsets[1],
                        math.floor(blue * blue_factor + 0.5) + offsets[2],
                    )
                    expected = bytes(max(0, min(255, channel)) for channel in channels) + bytes((alpha,))
                observed = dest[output_offset:output_offset + 4]
                assert expected == observed, (
                    f"{name}: variant {variant}, pixel ({x},{y}) mismatch: "
                    f"expected {expected.hex()}, observed {observed.hex()}"
                )
                tile.extend(observed)
        variant_pixels.add(bytes(tile))
    assert len(variant_pixels) == 4, f"{name}: expected four distinct Fusion variants"


def main():
    if len(sys.argv) != 3:
        raise SystemExit("usage: validate_fusion_render_variants.py <mod.jar> <fusion-pack.zip>")
    with zipfile.ZipFile(sys.argv[1]) as mod, zipfile.ZipFile(sys.argv[2]) as fusion:
        main_files = set(mod.namelist())
        fusion_files = set(fusion.namelist())
        checked = []
        for prefix, count in zip(STAGE_PREFIXES, EXPECTED_COUNTS):
            original_names = sorted(n for n in main_files if n.startswith(prefix) and n.endswith(".png"))
            fusion_names = sorted(n for n in fusion_files if n.startswith(prefix) and n.endswith(".png"))
            assert len(original_names) == len(fusion_names) == count, (
                f"{prefix}: expected {count}, got {len(original_names)} base, {len(fusion_names)} Fusion"
            )
            assert original_names == fusion_names, f"{prefix}: Fusion names don't match shipped mod resources"
            for name in original_names:
                assert_variants(mod.read(name), fusion.read(name), name)
            checked.append(count)
    print(
        "FUSION_SHIPPED_PIXEL_PASS: "
        f"{checked[0]} crop, {checked[1]} fungal and {checked[2]} marine atlases; "
        "all 4 random 16x16 tiles independently validated against packaged JAR art."
    )


if __name__ == "__main__":
    main()
