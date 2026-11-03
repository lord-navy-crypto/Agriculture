#!/usr/bin/env python3
"""Validate all three GeckoLib 4.9.3 kitchen rigs in source and the packaged mod JAR."""
import json
import sys
import zipfile
from pathlib import Path

from validate_fusion_render_variants import png_pixels

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga"
ASSETS = ROOT / "src/main/resources/assets/gugugaga"
TEXTURES = ROOT / "build/generated/resources/geckoKitchenV311/assets/gugugaga/textures/entity"

PROFILES = {
    "kneading_bench": {"bones": {"root", "dough", "roller"}, "work_bones": {"roller", "dough"}},
    "skillet_station": {"bones": {"root", "pan", "handle", "spatula", "food"}, "work_bones": {"spatula", "food"}},
    "bamboo_steamer": {"bones": {"root", "basket", "lid"}, "work_bones": {"lid"}},
}

def read_json(path):
    return json.loads(path.read_text(encoding="utf-8"))

def main():
    assert len(sys.argv) == 2, "usage: validate_geckolib_kitchen_v311.py <jar>"
    jar_path = Path(sys.argv[1])
    assert jar_path.is_file(), jar_path

    gradle = (ROOT / "build.gradle").read_text(encoding="utf-8")
    props = (ROOT / "gradle.properties").read_text(encoding="utf-8")
    metadata = (ROOT / "src/main/templates/META-INF/neoforge.mods.toml").read_text(encoding="utf-8")
    assert "software.bernie.geckolib:geckolib-neoforge-${minecraft_version}:${geckolib_version}" in gradle
    assert "geckolib_version=4.9.3" in props
    assert "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/" in gradle
    assert 'modId="geckolib"' in metadata and 'versionRange="[4.9.3,)"' in metadata
    assert "generateGeckoKitchenTexturesV311" in gradle

    block = (JAVA / "agriculture/GeckoKitchenStationBlock.java").read_text(encoding="utf-8")
    block_entity = (JAVA / "agriculture/GeckoKitchenStationBlockEntity.java").read_text(encoding="utf-8")
    content = (JAVA / "agriculture/GeckoKitchenStationContent.java").read_text(encoding="utf-8")
    kitchen = (JAVA / "agriculture/KitchenV3Content.java").read_text(encoding="utf-8")
    client = (JAVA / "client/GeckoKitchenStationClient.java").read_text(encoding="utf-8")
    renderer = (JAVA / "client/GeckoKitchenStationRenderer.java").read_text(encoding="utf-8")
    model = (JAVA / "client/GeckoKitchenStationModel.java").read_text(encoding="utf-8")
    main_mod = (JAVA / "GuguGagaMod.java").read_text(encoding="utf-8")

    assert "extends KitchenV3AnimatedBlock implements EntityBlock" in block
    assert "RenderShape.ENTITYBLOCK_ANIMATED" in block
    assert "implements GeoBlockEntity" in block_entity
    assert "GeckoLibUtil.createInstanceCache(this)" in block_entity
    assert "KitchenV3AnimatedBlock.ACTIVE" in block_entity
    assert all(f'{profile}("' in block_entity for profile in ("KNEAD", "SKILLET", "STEAMER"))
    assert all(f'Profile.{profile}' in content for profile in ("KNEAD", "SKILLET", "STEAMER"))
    assert "BlockEntityType.Builder.of" in content
    assert "GeckoKitchenStationContent.BLOCK_ENTITY_TYPES.register(modBus)" in main_mod
    assert all(f'geckoStation("{profile}"' in kitchen for profile in PROFILES)
    assert all(f"GeckoKitchenStationContent.{name}.get()" in client for name in
               ("DOUGH_BENCH", "SKILLET_STATION", "BAMBOO_STEAMER"))
    assert "GeoBlockRenderer" in renderer
    assert "animatable.profile().assetId()" in model

    with zipfile.ZipFile(jar_path) as packaged:
        assert packaged.testzip() is None, "invalid packaged JAR"
        for profile, requirements in PROFILES.items():
            geo_path = ASSETS / "geo" / f"{profile}.geo.json"
            animation_path = ASSETS / "animations" / f"{profile}.animation.json"
            texture_path = TEXTURES / f"{profile}.png"
            geo = read_json(geo_path)
            bones = {bone["name"] for bone in geo["minecraft:geometry"][0]["bones"]}
            assert requirements["bones"] <= bones, (profile, bones)

            animation = read_json(animation_path)["animations"]
            idle_name = f"animation.{profile}.idle"
            work_name = f"animation.{profile}.work"
            assert {idle_name, work_name} <= set(animation), profile
            work_bones = set(animation[work_name]["bones"])
            assert requirements["work_bones"] <= work_bones, (profile, work_bones)

            width, height, pixels = png_pixels(texture_path.read_bytes())
            assert (width, height) == (64, 64), (profile, width, height)
            unique_pixels = len(set(pixels[i:i+4] for i in range(0, len(pixels), 4)))
            assert unique_pixels > 24, (profile, unique_pixels)

            for asset_path, source in (
                (f"assets/gugugaga/geo/{profile}.geo.json", geo_path),
                (f"assets/gugugaga/animations/{profile}.animation.json", animation_path),
                (f"assets/gugugaga/textures/entity/{profile}.png", texture_path),
            ):
                assert packaged.read(asset_path) == source.read_bytes(), asset_path + " missing or differs in JAR"

    print(
        "GECKOLIB_KITCHEN_V311_PASS: GeckoLib 4.9.3 dependency; shared block entity, model and renderer; "
        "three independent rigs, idle/work animations, 64x64 material atlases and exact JAR asset parity."
    )

if __name__ == "__main__":
    main()
