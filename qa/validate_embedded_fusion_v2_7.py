#!/usr/bin/env python3
"""Assert that the distributable mod JAR includes the *exact* optional Fusion pack.

The embedded pack must not change the standalone default Minecraft model files.
No third-party Fusion mod is bundled and registration is gated by ModList.
"""
import json
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PREFIX = "resourcepacks/fusion/"
BASE = "assets/gugugaga/models/block/"
CATEGORIES = {"crops": 1200, "marine_fungi": 24, "marine_plants": 16}
CLIENT = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/FusionVisualPackClient.java"

def main():
    if len(sys.argv) != 3:
        raise SystemExit("usage: validate_embedded_fusion_v2_7.py <mod.jar> <fusion-pack.zip>")

    java = CLIENT.read_text(encoding="utf-8")
    required = (
        "AddPackFindersEvent",
        'ModList.get().isLoaded("fusion")',
        "PackType.CLIENT_RESOURCES",
        '"resourcepacks/fusion"',
        "PackSource.BUILT_IN",
        "Pack.Position.TOP",
        "true,",
        "Dist.CLIENT",
    )
    for word in required:
        assert word in java, f"Built-in resourcepack registration missing {word}"

    with zipfile.ZipFile(sys.argv[1]) as jar, zipfile.ZipFile(sys.argv[2]) as pack:
        assert jar.testzip() is None, "mod jar corrupt"
        assert pack.testzip() is None, "standalone pack corrupt"
        jar_names = set(jar.namelist())
        names = {n for n in pack.namelist() if not n.endswith("/")}
        assert "pack.mcmeta" in names and "pack.png" in names
        bundled_names = {
            x[len(PREFIX):] for x in jar_names
            if x.startswith(PREFIX) and x != PREFIX and not x.endswith("/")
        }
        assert bundled_names == names, (
            f"Missing/extra embedded pack files: missing={list(names-bundled_names)[:5]}, "
            f"extra={list(bundled_names-names)[:5]}"
        )
        for name in names:
            assert jar.read(PREFIX + name) == pack.read(name), (
                f"Bundled pack byte mismatch: {name}"
            )

        for category, count in CATEGORIES.items():
            model_dir = BASE + category + "/"
            models = sorted(x for x in names if x.startswith(model_dir) and x.endswith(".json"))
            assert len(models) == count, f"{category}: wrong Fusion model count"
            for model in models:
                assert model in jar_names, f"Normal mod model missing: {model}"
                vanilla = json.loads(jar.read(model))
                enhanced = json.loads(pack.read(model))
                assert "loader" not in vanilla and "type" not in vanilla, (
                    f"Fusion model leaked into default assets: {model}"
                )
                assert enhanced == {**vanilla, "loader": "fusion:model", "type": "fusion:base"}, (
                    f"{model}: Fusion override mutated base model"
                )

        habitat_models = [
            n for n in names
            if n.startswith(BASE) and n.endswith(".json")
            and not any(n.startswith(BASE + c + "/") for c in CATEGORIES)
        ]
        assert len(habitat_models) == 8, "wrong habitat Fusion override count"
        atlas_count = len([n for n in names if n.endswith(".png.mcmeta")])
        assert atlas_count == 1248, f"expected 1,248 random texture metadata, got {atlas_count}"
        deps = jar.read("META-INF/neoforge.mods.toml").decode("utf-8")
        assert 'modId="fusion"' not in deps, "Fusion must not become a mandatory dependency"

    print(
        f"EMBEDDED_FUSION_PASS: one JAR contains {len(names)} byte-identical "
        f"optional resource pack entries; 1,248 random atlases, "
        f"1,248 Fusion models, unchanged native models, gated client activation."
    )

if __name__ == "__main__":
    main()
