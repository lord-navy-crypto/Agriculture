#!/usr/bin/env python3
"""Validate optional Fusion 1.21.1 habitat + crop-stage resource pack."""
import hashlib
import json
import struct
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PACK = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / "build/fusion-visual-pack"
ARCHIVE = Path(sys.argv[2]) if len(sys.argv) > 2 else ROOT / "build/distributions/gugugaga-fusion-visuals-v2.6.zip"
ART = ROOT / "src/main/resources/assets/gugugaga/models/block"
CROP_SOURCE = ROOT / "build/generated/resources/cropAssets/assets/gugugaga/textures/block/crops"
CROP_PREFIX = "assets/gugugaga/textures/block/crops/"
KINDS = ["bumblebee", "mason", "leafcutter", "squash"]


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def png_size(path):
    data = path.read_bytes()
    assert data[:8] == b"\x89PNG\r\n\x1a\n", f"not a PNG: {path}"
    return struct.unpack(">II", data[16:24])


def main():
    pack = read(PACK / "pack.mcmeta")["pack"]
    assert pack["pack_format"] == 34, "Minecraft 1.21.1 resource pack format must be 34"
    hashes = set()
    for index, kind in enumerate(KINDS):
        for subindex, surface in enumerate(["side", "roof"]):
            name = f"{kind}_{surface}"
            texture = PACK / f"assets/gugugaga/textures/block/fusion/{name}.png"
            assert png_size(texture) == (32, 32), name
            hashes.add(hashlib.sha256(texture.read_bytes()).hexdigest())
            metadata = read(texture.with_suffix(".png.mcmeta"))["fusion"]
            assert metadata == dict(
                type="random", rows=2, columns=2,
                randomness_source="position_facing", seed=8300 + index * 10 + subindex
            ), name
        habitat = "bumblebee_habitat" if kind == "bumblebee" else kind + "_bee_habitat"
        for suffix in ["", "_active"]:
            model_id = habitat + suffix
            original = read(ART / f"{model_id}.json")
            enhanced = read(PACK / f"assets/gugugaga/models/block/{model_id}.json")
            assert enhanced.get("loader") == "fusion:model" and enhanced.get("type") == "fusion:base", model_id
            assert enhanced.get("elements") == original.get("elements"), model_id
            assert enhanced["textures"]["up"] == f"gugugaga:block/fusion/{kind}_roof", model_id
            assert enhanced["textures"]["east"] == f"gugugaga:block/fusion/{kind}_side", model_id
    assert len(hashes) == 8, "Fusion habitat atlases must not be duplicates"

    source_files = {p.name for p in CROP_SOURCE.glob("*.png")}
    assert len(source_files) == 1200, f"expected 1200 generated crop-stage textures, got {len(source_files)}"
    crop_pack_dir = PACK / CROP_PREFIX
    crop_textures = sorted(crop_pack_dir.glob("*.png"))
    assert len(crop_textures) == 1200, f"expected 1200 Fusion crop atlases, got {len(crop_textures)}"
    assert {p.name for p in crop_textures} == source_files, "Fusion crop atlas names must match all generated source stages"
    for texture in crop_textures:
        assert png_size(texture) == (32, 32), f"crop atlas must be 32x32: {texture.name}"
        metadata = read(texture.with_suffix(".png.mcmeta"))["fusion"]
        assert set(metadata) == {"type", "rows", "columns", "randomness_source", "seed"}, texture.name
        assert metadata["type"] == "random" and metadata["rows"] == 2 and metadata["columns"] == 2, texture.name
        assert metadata["randomness_source"] == "position", texture.name
        assert isinstance(metadata["seed"], int), texture.name

    proof = ROOT / "build/reports/agriculture/fusion-crop-random-variants-contact-sheet.png"
    assert proof.is_file() and png_size(proof) == (960, 440), "missing or malformed crop-variation proof sheet"

    assert ARCHIVE.is_file(), "missing distributable optional pack"
    with zipfile.ZipFile(ARCHIVE) as z:
        files = set(z.namelist())
        assert "pack.mcmeta" in files and "pack.png" in files
        assert z.read("pack.png")[:8] == b"\x89PNG\r\n\x1a\n", "pack icon is not a PNG"
        for kind in KINDS:
            habitat = "bumblebee_habitat" if kind == "bumblebee" else kind + "_bee_habitat"
            assert f"assets/gugugaga/models/block/{habitat}.json" in files
            assert f"assets/gugugaga/models/block/{habitat}_active.json" in files
            for surface in ["side", "roof"]:
                assert f"assets/gugugaga/textures/block/fusion/{kind}_{surface}.png.mcmeta" in files
        zipped_crop_pngs = {n for n in files if n.startswith(CROP_PREFIX) and n.endswith(".png")}
        zipped_crop_meta = {n for n in files if n.startswith(CROP_PREFIX) and n.endswith(".png.mcmeta")}
        assert len(zipped_crop_pngs) == 1200, f"ZIP crop texture count is {len(zipped_crop_pngs)}"
        assert len(zipped_crop_meta) == 1200, f"ZIP crop metadata count is {len(zipped_crop_meta)}"
        assert {Path(n).name for n in zipped_crop_pngs} == source_files, "ZIP missing crop-stage texture(s)"
    print("FUSION_VISUAL_PACK_V2_6_PASS: 8 habitat models, 8 habitat atlases, 1200 crop-stage random atlases, valid 2x2 metadata and distributable ZIP.")


if __name__ == "__main__":
    main()
