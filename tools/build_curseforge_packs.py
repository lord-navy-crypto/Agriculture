#!/usr/bin/env python3
"""Build CurseForge ZIPs from checked-in manifests and owner-built JARs.

Usage:
  python3 tools/build_curseforge_packs.py --mods-dir /path/to/verified-mod-jars
  python3 tools/build_curseforge_packs.py --mods-dir /path/to/verified-mod-jars --allow-manifest-only

By default, the script requires the four owner-built modules. Third-party mods are
resolved by the CurseForge app from manifest project/file IDs and are not rehosted.
"""
from __future__ import annotations
import argparse
import hashlib
import json
from pathlib import Path
import zipfile

ROOT = Path(__file__).resolve().parents[1]
PACK_ROOT = ROOT / "modpacks" / "curseforge"
VARIANTS = ["core", "nightfall-creatures", "nightfall-liminal", "nightfall", "serene-seasons", "spring-season"]
REQUIRED_PREFIXES = ("gugugaga", "realscale", "rse", "swarm")

def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--mods-dir", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, default=Path("dist/curseforge"))
    parser.add_argument("--allow-manifest-only", action="store_true")
    args = parser.parse_args()
    mods_dir = args.mods_dir.resolve()
    output_dir = args.output_dir.resolve()
    jars = sorted(mods_dir.glob("*.jar")) if mods_dir.exists() else []
    names = [p.name.lower() for p in jars]
    missing = [prefix for prefix in REQUIRED_PREFIXES if not any(n.startswith(prefix) for n in names)]
    if missing and not args.allow_manifest_only:
        raise SystemExit("Missing owner-built JAR families: " + ", ".join(missing))
    output_dir.mkdir(parents=True, exist_ok=True)
    for variant in VARIANTS:
        source = PACK_ROOT / variant / "manifest.json"
        manifest = json.loads(source.read_text(encoding="utf-8"))
        target = output_dir / f"{variant}.zip"
        with zipfile.ZipFile(target, "w", compression=zipfile.ZIP_DEFLATED) as archive:
            archive.writestr("manifest.json", json.dumps(manifest, indent=2) + "\n")
            for jar in jars:
                archive.write(jar, f"overrides/mods/{jar.name}")
            if jars:
                sums = "\n".join(f"{sha256(jar)}  {jar.name}" for jar in jars) + "\n"
                archive.writestr("overrides/mods/SHA256SUMS.txt", sums)
            else:
                archive.writestr("overrides/mods/OWNER_BUILT_JARS_REQUIRED.txt",
                    "Add the four owner-built JARs before treating this pack as complete.\n")
        print(f"{target} ({target.stat().st_size:,} bytes)")
    if missing:
        print("MANIFEST-ONLY: missing custom module families: " + ", ".join(missing))
    else:
        print("COMPLETE: all four owner-built module families included.")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
