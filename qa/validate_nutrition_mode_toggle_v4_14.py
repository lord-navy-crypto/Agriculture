#!/usr/bin/env python3
"""Static and packaged-JAR contract checks for Nutrition Mode v4.14."""
from pathlib import Path
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
LANG = ROOT / "src/main/resources/assets/gugugaga/lang"

def read(path: Path) -> str:
    if not path.is_file():
        raise SystemExit(f"FAIL: missing {path.relative_to(ROOT)}")
    return path.read_text(encoding="utf-8")

nutrition = read(JAVA / "NutritionV34.java")
physiology = read(JAVA / "NutritionPhysiologyV39.java")
network = read(JAVA / "NutritionNetworkV35.java")
request = read(JAVA / "NutritionNetworkV34.java")
screen = read(JAVA / "NutritionScreenClientV35.java")
en = read(LANG / "en_us.json")
zh = read(LANG / "zh_cn.json")

checks = {
    "default remains enabled for old player data":
        "!data.contains(MODE_ENABLED) || data.getBoolean(MODE_ENABLED)" in nutrition,
    "per-player setting is persisted":
        "putBoolean(MODE_ENABLED,enabled)" in nutrition,
    "food intake is skipped while disabled":
        "if(!nutritionModeEnabled(player)) return;" in nutrition,
    "disabled tick pauses instead of advancing":
        "NutritionPhysiologyV39.pause(values,worldDay);" in nutrition
        and "if(!nutritionModeEnabled(player))" in nutrition,
    "pause synchronizes world day":
        "data.putLong(LAST_DAY,worldDay);" in physiology,
    "pause clears only daily intake counters":
        "if(!linked(i))data.remove(DAILY+i);" in physiology,
    "pause does not reset reserve or shortfall keys":
        "data.putInt(key(i)" not in physiology[physiology.find("public static void pause"):physiology.find("public static void pause")+600],
    "mode state is included in network payload":
        "boolean nutritionEnabled" in network and "buf.writeBoolean(data.nutritionEnabled())" in network,
    "toggle request is server handled":
        "ToggleRequest.TYPE" in network and "NutritionV34.toggleNutritionMode(player)" in network,
    "network protocol is bumped":
        'event.registrar("5")' in network and 'event.registrar("5")' in request,
    "N-key GUI has an interactive switch":
        "new NutritionNetworkV35.ToggleRequest()" in screen,
    "N-key GUI displays paused state":
        "nutrition.gugugaga.mode_status" in screen and "nutrition.gugugaga.mode_paused_note" in screen,
    "English labels exist":
        all(k in en for k in (
            "nutrition.gugugaga.mode_on_short",
            "nutrition.gugugaga.mode_off_short",
            "nutrition.gugugaga.mode_status",
            "nutrition.gugugaga.mode_paused_note")),
    "Chinese labels exist":
        all(k in zh for k in (
            "nutrition.gugugaga.mode_on_short",
            "nutrition.gugugaga.mode_off_short",
            "nutrition.gugugaga.mode_status",
            "nutrition.gugugaga.mode_paused_note")),
}

failed = [name for name, ok in checks.items() if not ok]
for name, ok in checks.items():
    print(f"{'PASS' if ok else 'FAIL'}: {name}")
if failed:
    raise SystemExit(f"{len(failed)} nutrition-mode checks failed")

if len(sys.argv) > 1:
    jar = Path(sys.argv[1])
    if not jar.is_absolute():
        jar = ROOT / jar
    if not jar.is_file():
        raise SystemExit(f"FAIL: JAR not found: {jar}")
    with zipfile.ZipFile(jar) as archive:
        names = set(archive.namelist())
        required = [
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionV34.class",
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionPhysiologyV39.class",
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionNetworkV35$ToggleRequest.class",
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionScreenClientV35.class",
            "assets/gugugaga/lang/en_us.json",
            "assets/gugugaga/lang/zh_cn.json",
        ]
        missing = [entry for entry in required if entry not in names]
        if missing:
            raise SystemExit("FAIL: packaged JAR is missing: " + ", ".join(missing))
    print(f"PASS: packaged JAR contains nutrition-mode implementation ({jar.name})")

print(f"Nutrition Mode v4.14: {len(checks)} source checks passed.")
