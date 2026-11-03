#!/usr/bin/env python3
"""Validate the first GeckoLib 4.9.3 kneading-bench integration in source and JAR."""
import json
import sys
import zipfile
from pathlib import Path

from validate_fusion_render_variants import png_pixels

ROOT = Path(__file__).resolve().parents[1]
GEO = ROOT / "src/main/resources/assets/gugugaga/geo/kneading_bench.geo.json"
ANIMATION = ROOT / "src/main/resources/assets/gugugaga/animations/kneading_bench.animation.json"
TEXTURE = ROOT / "build/generated/resources/geckoKneadingV311/assets/gugugaga/textures/entity/kneading_bench.png"
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"

def read_json(path):
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    assert len(sys.argv) == 2, "usage: validate_geckolib_kneading_v311.py <jar>"
    jar_path = Path(sys.argv[1])
    assert jar_path.is_file(), jar_path

    gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
    props = (ROOT / "gradle.properties").read_text(encoding="utf-8")
    metadata = (ROOT / "src/main/templates/META-INF/neoforge.mods.toml").read_text(encoding="utf-8")
    assert "software.bernie.geckolib:geckolib-neoforge-${minecraft_version}:${geckolib_version}" in gradle
    assert "geckolib_version=4.9.3" in props
    assert "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/" in gradle
    assert 'modId="geckolib"' in metadata and 'versionRange="[4.9.3,)"' in metadata

    block = (JAVA / "agriculture/GeckoKneadingBenchBlock.java").read_text(encoding="utf-8")
    block_entity = (JAVA / "agriculture/GeckoKneadingBenchBlockEntity.java").read_text(encoding="utf-8")
    content = (JAVA / "agriculture/GeckoKneadingBenchContent.java").read_text(encoding="utf-8")
    client = (JAVA / "client/GeckoKneadingBenchClient.java").read_text(encoding="utf-8")
    model_class = (JAVA / "client/GeckoKneadingBenchModel.java").read_text(encoding="utf-8")
    main_mod = (JAVA / "GuguGagaMod.java").read_text(encoding="utf-8")
    assert "implements EntityBlock" in block and "RenderShape.ENTITYBLOCK_ANIMATED" in block
    assert "implements GeoBlockEntity" in block_entity
    assert "GeckoLibUtil.createInstanceCache(this)" in block_entity
    assert "KitchenV3AnimatedBlock.ACTIVE" in block_entity
    assert "animation.kneading_bench.idle" in block_entity
    assert "animation.kneading_bench.work" in block_entity
    assert "BlockEntityType.Builder.of" in content
    assert "BLOCK_ENTITY_TYPES.register(modBus)" in main_mod
    assert "EntityRenderersEvent.RegisterRenderers" in client
    assert "GeoBlockRenderer" in (JAVA / "client/GeckoKneadingBenchRenderer.java").read_text(encoding="utf-8")
    assert "geo/kneading_bench.geo.json" in model_class
    assert "animations/kneading_bench.animation.json" in model_class

    geo = read_json(GEO)
    bones = {bone["name"] for bone in geo["minecraft:geometry"][0]["bones"]}
    assert {"root", "dough", "roller"} <= bones, bones
    animation = read_json(ANIMATION)["animations"]
    assert {"animation.kneading_bench.idle", "animation.kneading_bench.work"} <= set(animation)
    assert "roller" in animation["animation.kneading_bench.work"]["bones"]
    assert "dough" in animation["animation.kneading_bench.work"]["bones"]

    width, height, pixels = png_pixels(TEXTURE.read_bytes())
    assert (width, height) == (64, 64), (width, height)
    assert len(set(pixels[i:i+4] for i in range(0, len(pixels), 4))) > 24

    with zipfile.ZipFile(jar_path) as packaged:
        assert packaged.testzip() is None, "invalid packaged JAR"
        for path, source in (
            ("assets/gugugaga/geo/kneading_bench.geo.json", GEO),
            ("assets/gugugaga/animations/kneading_bench.animation.json", ANIMATION),
            ("assets/gugugaga/textures/entity/kneading_bench.png", TEXTURE),
        ):
            assert packaged.read(path) == source.read_bytes(), path + " missing or differs in JAR"

    print(
        "GECKOLIB_KNEADING_V311_PASS: GeckoLib 4.9.3 dependency, dedicated block entity, "
        "client-only GeoBlockRenderer, root/dough/roller bones, idle/work animations, "
        "64x64 generated texture and exact packaged asset parity."
    )

if __name__ == "__main__":
    main()
