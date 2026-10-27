#!/usr/bin/env python3
"""Validate optional Fusion habitat, crop, marine-plant, and fungal-culture resources."""
import hashlib
import json
import struct
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PACK = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / "build/fusion-visual-pack"
ARCHIVE = Path(sys.argv[2]) if len(sys.argv) > 2 else ROOT / "build/distributions/gugugaga-fusion-visuals-v2.7.zip"
ART = ROOT / "src/main/resources/assets/gugugaga/models/block"
CROP_SOURCE = ROOT / "build/generated/resources/cropAssets/assets/gugugaga/textures/block/crops"
MARINE_SOURCE = ROOT / "build/generated/resources/marineFungiAssets/assets/gugugaga/textures/block"
CROP_PREFIX = "assets/gugugaga/textures/block/crops/"
MARINE_COUNTS = {"marine_fungi": 24, "marine_plants": 16}
KINDS = ["bumblebee", "mason", "leafcutter", "squash"]


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def png_size(path):
    data = path.read_bytes()
    assert data[:8] == b"\x89PNG\r\n\x1a\n", f"not a PNG: {path}"
    return struct.unpack(">II", data[16:24])


def validate_stage_directory(source_dir, pack_dir, prefix, expected_count, randomness_source):
    source_names = {p.name for p in source_dir.glob("*.png")}
    assert len(source_names) == expected_count, f"{source_dir}: expected {expected_count} source textures, got {len(source_names)}"
    textures = sorted(pack_dir.glob("*.png"))
    assert len(textures) == expected_count, f"{pack_dir}: expected {expected_count} atlases, got {len(textures)}"
    assert {p.name for p in textures} == source_names, f"{pack_dir}: texture names differ from source"
    for texture in textures:
        assert png_size(texture) == (32, 32), f"atlas must be 32x32: {texture.name}"
        metadata = read(texture.with_suffix(".png.mcmeta"))["fusion"]
        assert set(metadata) == {"type", "rows", "columns", "randomness_source", "seed"}, texture.name
        assert metadata["type"] == "random" and metadata["rows"] == 2 and metadata["columns"] == 2, texture.name
        assert metadata["randomness_source"] == randomness_source, texture.name
        assert isinstance(metadata["seed"], int), texture.name
    return source_names


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

    crop_names = validate_stage_directory(
        CROP_SOURCE, PACK / CROP_PREFIX, CROP_PREFIX, 1200, "position"
    )
    marine_names = {}
    for category, count in MARINE_COUNTS.items():
        prefix = f"assets/gugugaga/textures/block/{category}/"
        marine_names[category] = validate_stage_directory(
            MARINE_SOURCE / category, PACK / prefix, prefix, count, "position"
        )

    crop_proof = ROOT / "build/reports/agriculture/fusion-crop-random-variants-contact-sheet.png"
    marine_proof = ROOT / "build/reports/agriculture/fusion-marine-fungi-random-variants-contact-sheet.png"
    assert crop_proof.is_file() and png_size(crop_proof) == (960, 440), "missing crop variation proof sheet"
    assert marine_proof.is_file() and png_size(marine_proof) == (960, 264), "missing marine/fungal variation proof sheet"

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
        for category, names in [("crops", crop_names), *marine_names.items()]:
            prefix = f"assets/gugugaga/textures/block/{category}/"
            pngs = {n for n in files if n.startswith(prefix) and n.endswith(".png")}
            metas = {n for n in files if n.startswith(prefix) and n.endswith(".png.mcmeta")}
            assert len(pngs) == len(names), f"{category} ZIP PNG count mismatch: {len(pngs)}"
            assert len(metas) == len(names), f"{category} ZIP metadata count mismatch: {len(metas)}"
            assert {Path(n).name for n in pngs} == names, f"{category} ZIP missing texture(s)"
    print("FUSION_VISUAL_PACK_V2_7_PASS: 1200 crop atlases, 24 fungal atlases, 16 marine atlases, 8 habitat atlases and models; all metadata and ZIP entries valid.")


if __name__ == "__main__":
    main()
