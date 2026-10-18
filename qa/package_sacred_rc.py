#!/usr/bin/env python3
"""Verify and package the ACTUAL built NeoForge JAR as a human-testable RC.

CI-built jar is not a claim of successful physical 3D client playtesting.
Reads package entries, validates all actors/models/assets and required GeckoLib
metadata, then copies exactly one JAR with hashes and manual test instructions.
No third-party Python dependencies.
"""
import hashlib
import json
import pathlib
import shutil
import sys
import zipfile

VERSION="0.21.0-rc.1"
EXPECTED_JAR=f"gugugaga-{VERSION}.jar"
PREFIX="io/github/lordnavycrypto/gugugaga/"
ACTORS=["GugugagaEntity","RedactorEntity","YellowSmilerEntity"]
GEO=["observer","redactor_lapis","redactor_true","yellow_smiler"]
ANIM=["observer","redactor","yellow_smiler"]
TEXTURES=["observer","observer_blackened","lapis_mask",
          "redactor_true","yellow_smiler","yellow_smiler_true"]

def check_and_bundle(build_libs,out):
    build_libs=pathlib.Path(build_libs)
    out=pathlib.Path(out)
    jars=list(build_libs.glob("*.jar"))
    matching=[p for p in jars if p.name==EXPECTED_JAR]
    if len(matching)!=1:
        raise AssertionError(
            f"Required exactly one {EXPECTED_JAR}; found {[p.name for p in jars]}")
    jar=matching[0]
    with zipfile.ZipFile(jar) as z:
        names=set(z.namelist())
        required={ "META-INF/neoforge.mods.toml",
                   PREFIX+"GuguGagaMod.class",
                   PREFIX+"command/GuguPlayCommands.class",
                   PREFIX+"ai/HauntGameplayRuntime.class",
                   PREFIX+"ai/HorrorHearingPolicy.class",
                   PREFIX+"ai/HorrorListeningPolicy.class",
                   PREFIX+"ai/CaveThreatSoundPolicy.class",
                   PREFIX+"ai/HorrorEvidencePolicy.class",
                   PREFIX+"ai/SmilerTrustPolicy.class" }
        for mob in ACTORS:required.add(PREFIX+"entity/"+mob+".class")
        for name in GEO:required.add("assets/gugugaga/geo/"+name+".geo.json")
        for name in ANIM:required.add("assets/gugugaga/animations/"+name+".animation.json")
        for name in TEXTURES:
            required.add("assets/gugugaga/textures/entity/"+name+".png")
        missing=required-names
        if missing:raise AssertionError(f"Missing shipped runtime assets: {sorted(missing)}")
        meta=z.read("META-INF/neoforge.mods.toml").decode("utf8")
        for line in (f'version="{VERSION}"','modId="gugugaga"',
                     'modId="geckolib"','side="BOTH"',
                     'versionRange="[21.1.100,)"',
                     'versionRange="[4.8.2,5.0.0)"'):
            if line not in meta:raise AssertionError(f"mod metadata lacks {line}")
        for name in TEXTURES:
            blob=z.read("assets/gugugaga/textures/entity/"+name+".png")
            if not blob.startswith(b"\x89PNG\r\n\x1a\n"):
                raise AssertionError(f"Corrupted PNG {name}")
        for name in ANIM:
            data=json.loads(z.read("assets/gugugaga/animations/"+name+".animation.json"))
            if not data.get("animations"):raise AssertionError(f"No animations {name}")
        for name in GEO:
            data=json.loads(z.read("assets/gugugaga/geo/"+name+".geo.json"))
            if not data.get("minecraft:geometry"):raise AssertionError(f"No geometry {name}")
    out.mkdir(parents=True,exist_ok=True)
    dest=out/jar.name
    shutil.copyfile(jar,dest)
    data=dest.read_bytes()
    digest=hashlib.sha256(data).hexdigest()
    (out/"SHA256SUMS.txt").write_text(f"{digest}  {dest.name}\n")
    manifest={
        "mod":"Gugu-Gaga Horror Natural Interaction Playtest",
        "version":VERSION,
        "minecraft":"1.21.1",
        "neoforge_minimum":"21.1.100",
        "java":"21",
        "required_geckolib":"4.8.2 or newer 4.x for Minecraft 1.21.1 NeoForge",
        "jar_file":dest.name,
        "jar_bytes":len(data),
        "jar_sha256":digest,
        "all_three_actor_classes_verified":True,
        "all_6_texture_png_verified":True,
        "all_4_geometry_assets_verified":True,
        "all_3_animation_json_verified":True,
        "client_gameplay_verified":False,
        "dedicated_multiplayer_verified":False,
        "release_status":"PLAYTEST_CANDIDATE_NOT_STABLE"
    }
    (out/"NATURAL_PLAYTEST_MANIFEST.json").write_text(json.dumps(manifest,indent=2)+"\n")
    (out/"NATURAL_PLAYTEST_INSTRUCTIONS.txt").write_text(
        "GUGU-GAGA NATURAL INTERACTION v0.20 / PLAYTEST PACKAGE\n"
        "Experimental, NOT a stable release. Use backup/test world.\n"
        "Minecraft 1.21.1 | NeoForge 21.1.100+ | Java 21\n"
        "REQUIRES GeckoLib 4.8.2+ (4.x) for NeoForge 1.21.1 on BOTH client and dedicated server.\n"
        "Install the ONE JAR above in mods; remove older gugugaga JAR versions.\n"
        "The UI input/held-gaze interactions replace most gameplay slash commands.\n"
        "Do not put the zip of this artifact directly in mods.\n"
        "Creative testing: /time set night, then /gamemode survival\n"
        "OP: /gugugaga play server on\n"
        "Each tester: /gugugaga play join\n"
        "OP in survival and at night: /gugugaga play test triad\n"
        "OP can use: /gugugaga play test witness\n"
        "Crouch and press V to listen (remappable in Controls > Gugu-Gaga).\n"
        "Chat exactly: HELP (UPPERCASE, without slash) for actual rescue.\n"
        "Focus your crosshair on real Gugu for 4 seconds; then Lapis/Yellow for 4 seconds.\n"
        "Investigation advances automatically. /gugugaga investigate remains a fallback.\n"
        "To summon directly for geometry-only checks (admin):\n"
        "  /summon gugugaga:observer ~ ~ ~\n"
        "  /summon gugugaga:redactor ~ ~ ~\n"
        "  /summon gugugaga:yellow_smiler ~ ~ ~\n"
        "For real chase: /gugugaga play status; break LOS via cover,\n"
        "  compare sprinting versus sneaking while hidden.\n"
        "If visual glitches: test with shaders OFF, and report latest.log.\n"
        "Each tester can opt out: /gugugaga play leave\n"
        "OP disables test globally: /gugugaga play server off\n"
        "Never claim passing CI proves actual client renders or user enjoyment.\n")
    print("GUGUGAGA_NATURAL_PLAYTEST_PACKAGE_PASS",
          dest.name,len(data),digest,len(required))
    return manifest

if __name__=="__main__":
    if len(sys.argv)!=3:
        raise SystemExit("Usage: package_sacred_rc.py <build/libs> <build/sacred-release>")
    check_and_bundle(sys.argv[1],sys.argv[2])
