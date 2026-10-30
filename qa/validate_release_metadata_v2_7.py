#!/usr/bin/env python3
"""Ensure the built NeoForge JAR has correct release-candidate metadata.

The test reads the real packaged neoforge.mods.toml (not only the template).
"""
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VERSION = "3.9.0-rc.1"
build_dir = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / "build/libs"
props = (ROOT / "gradle.properties").read_text(encoding="utf-8")
template = (ROOT / "src/main/templates/META-INF/neoforge.mods.toml").read_text(encoding="utf-8")
readme = (ROOT / "README.md").read_text(encoding="utf-8")
assert f"mod_version={VERSION}" in props
assert "Gugu-Gaga-horror" not in template
assert "ten plantable crops" not in template.lower()
assert "100 plantable crops" in template
assert "50 honey varieties" in template
assert "Fusion" in template
assert "100 plantable crops" in readme
jars = list(build_dir.glob("gugugaga-*.jar"))
assert len(jars) == 1, f"expected one mod JAR in {build_dir}, got {jars}"
jar = jars[0]
assert VERSION in jar.name, f"wrong JAR name {jar.name}"
with zipfile.ZipFile(jar) as source:
    assert source.testzip() is None, "mod JAR has invalid ZIP CRC"
    info = source.read("META-INF/neoforge.mods.toml").decode("utf-8")
assert f'version="{VERSION}"' in info, f"JAR embedded version not {VERSION}"
assert 'modId="gugugaga"' in info
assert "100 plantable crops" in info and "50 honey varieties" in info
assert "https://github.com/lord-navy-crypto/Agriculture" in info
assert "ten plantable crops" not in info.lower()
print(f"RELEASE_METADATA_V2_7_PASS: {jar.name}, version {VERSION}, updated content and URLs.")
