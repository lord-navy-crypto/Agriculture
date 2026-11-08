#!/usr/bin/env python3
"""Validate one-mod packaging and the Agriculture -> Backrooms greenhouse loop."""
from pathlib import Path
import json

ROOT=Path(__file__).resolve().parents[1]

def read(path):
    return (ROOT/path).read_text("utf8")

def main():
    settings=read("settings.gradle")
    build=read("build.gradle")
    props=read("gradle.properties")
    metadata=read("src/main/templates/META-INF/neoforge.mods.toml")
    main_mod=read("src/main/java/io/github/lordnavycrypto/gugugaga/GuguGagaMod.java")
    threshold_mod=read("threshold/src/main/java/io/github/lordnavycrypto/threshold/ThresholdMod.java")
    crop=read("src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/CropClimate.java")
    cultivation=read("src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/BackroomCultivationV40.java")
    layout=read("threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdRoomLayoutFeature.java")
    runtime=read("threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdRuntimeChecks.java")
    assert "include('threshold')" not in settings
    assert "threshold/src/main/java" in build and "threshold/src/main/resources" in build
    assert 'implementation "software.bernie.geckolib:geckolib-neoforge-${minecraft_version}:${geckolib_version}"' in build
    assert 'implementation \'maven.modrinth:XaDC71GB:a9fuYUVh\'' in build
    assert "minecraft_version=1.21.1" in props and "geckolib_version=4.9.3" in props
    assert "mod_name=Backroom Agriculture" in props and "mod_version=4.0.0-alpha.1" in props
    assert 'modId="${mod_id}"' in metadata
    assert 'modId="geckolib"' in metadata and 'modId="lithostitched"' in metadata
    assert "ThresholdMod.register(modBus)" in main_mod
    assert "@Mod(" not in threshold_mod and "static void register(IEventBus modBus)" in threshold_mod
    assert "BackroomCultivationV40.isLevel1(level)" in crop
    for token in ("Blocks.FARMLAND","getMaxLocalRawBrightness","hasNearbyFreshWater","random.nextInt(2)","ComposterBlock.LEVEL","FarmBlock.MOISTURE","hasActiveComposterNearby"):
        assert token in cultivation, token
    assert "Blocks.COMPOSTER" in layout and "THRESHOLD_LEVEL1_COMPOSTER_OK" in runtime
    compost=json.loads((ROOT/"src/main/resources/data/neoforge/data_maps/item/compostables.json").read_text("utf8"))["values"]
    for name in ("black_pepper","lemongrass","wasabi","cumin","cardamom","fenugreek","sugar_beet","daikon"):
        assert f"gugugaga:{name}_seeds" in compost and f"gugugaga:{name}" in compost
    print("BACKROOM_AGRICULTURE_UNIFIED_LOOP_V4_0_PASS")

if __name__=="__main__":
    main()
