#!/usr/bin/env python3
"""Verify that a single Agriculture JAR embeds the entire generated Fusion pack.

No third-party packages required.
Usage: python3 qa/validate_embedded_fusion_pack_v2_7.py <mod.jar> <fusion-standalone.zip>
"""
from __future__ import annotations

import json
import sys
import zipfile

PREFIX = "resourcepacks/gugugaga_fusion/"
CATEGORIES = {"crops": 1200, "marine_fungi": 24, "marine_plants": 16}


def main():
    assert len(sys.argv) == 3, "usage: validate_embedded_fusion_pack_v2_7.py <mod.jar> <fusion.zip>"
    with zipfile.ZipFile(sys.argv[1]) as mod, zipfile.ZipFile(sys.argv[2]) as fusion:
        assert mod.testzip() is None, "main mod JAR has invalid CRC"
        assert fusion.testzip() is None, "generated Fusion ZIP has invalid CRC"
        base_files = set(mod.namelist())
        pack_files = {p.filename for p in fusion.infolist() if not p.is_dir()}
        embedded = {
            path[len(PREFIX):] for path in base_files
            if path.startswith(PREFIX) and not path.endswith("/")
        }
        assert embedded == pack_files, (
            f"embedded pack mismatch: {len(embedded)} embedded vs {len(pack_files)} generated, "
            f"missing={sorted(pack_files - embedded)[:5]}, extra={sorted(embedded - pack_files)[:5]}"
        )
        for path in pack_files:
            assert mod.read(PREFIX + path) == fusion.read(path), (
                f"embedded Fusion resource differs from generated pack: {path}"
            )
        metadata = json.loads(mod.read(PREFIX + "pack.mcmeta"))
        assert metadata["pack"]["pack_format"] == 34, "wrong resource pack format"
        assert mod.read(PREFIX + "pack.png").startswith(b"\\x89PNG\\r\\n\\x1a\\n")

        for category, count in CATEGORIES.items():
            texture = f"assets/gugugaga/textures/block/{category}/"
            model = f"assets/gugugaga/models/block/{category}/"
            pngs = [p for p in pack_files if p.startswith(texture) and p.endswith(".png")]
            metas = [p for p in pack_files if p.startswith(texture) and p.endswith(".png.mcmeta")]
            models = [p for p in pack_files if p.startswith(model) and p.endswith(".json")]
            assert len(pngs) == len(metas) == len(models) == count, (
                f"{category} embedded coverage: {len(pngs)} PNG, {len(metas)} metadata, "
                f"{len(models)} model; expected {count}"
            )
            for model_path in models:
                fusion_model = json.loads(mod.read(PREFIX + model_path))
                base_model = json.loads(mod.read(model_path))
                assert fusion_model == {
                    **base_model, "loader": "fusion:model", "type": "fusion:base"
                }, f"embedded model changes vanilla geometry or textures: {model_path}"
        assert "io/github/lordnavycrypto/gugugaga/client/EmbeddedFusionPackClient.class" in base_files, (
            "missing NeoForge client-only pack registration"
        )
        assert "resourcepacks/gugugaga_fusion/pack.mcmeta" in base_files
        assert "META-INF/neoforge.mods.toml" in base_files
        print(
            "EMBEDDED_FUSION_PACK_PASS: single JAR includes "
            f"{len(pack_files)} exact optional pack entries, "
            "1240 linked crop/marine/fungal stage models, and client pack registration."
        )


if __name__ == "__main__":
    main()
