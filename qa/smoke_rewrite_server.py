#!/usr/bin/env python3
"""Run a disposable real NeoForge server and verify agriculture and marine/fungal initialization."""
from pathlib import Path
import os
import json
import re
import signal
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[1]
LOG = ROOT / "build/agriculture-server-boot.log"
INIT = "GUGUGAGA_AGRICULTURE_RUNTIME_INIT_OK"
MARINE_INIT = "GUGUGAGA_MARINE_FUNGI_RUNTIME_INIT_OK"
CUISINE_INIT = "GUGUGAGA_CROP_CUISINE_RUNTIME_INIT_OK"
READY = re.compile(r"Done \([0-9.,]+s\)! For help, type", re.I)
LIMIT = int(os.environ.get("GUGUGAGA_SERVER_BOOT_TIMEOUT", "420"))


def run():
    for folder in (ROOT / "runs/server", ROOT / "run"):
        folder.mkdir(parents=True, exist_ok=True)
        (folder / "eula.txt").write_text("eula=true\n", encoding="utf-8")
    LOG.parent.mkdir(parents=True, exist_ok=True)
    with LOG.open("wb") as stream:
        # Use the root task path explicitly: the repository now also contains
        # Threshold's own :threshold:runServer task.
        process = subprocess.Popen(
            ["gradle", "--no-daemon", "--console=plain", ":runServer"],
            cwd=ROOT, stdout=stream, stderr=subprocess.STDOUT,
            start_new_session=True)
        try:
            deadline = time.monotonic() + LIMIT
            ready_at = None
            while time.monotonic() < deadline:
                time.sleep(2)
                current = LOG.read_text(encoding="utf-8", errors="replace")
                if INIT in current and MARINE_INIT in current and CUISINE_INIT in current and READY.search(current):
                    if ready_at is None:
                        ready_at = time.monotonic()
                    reports = sorted(
                        list((ROOT / "run").glob("**/nutrition_food_compat_report.json")) +
                        list((ROOT / "runs/server").glob("**/nutrition_food_compat_report.json")))
                    if not reports:
                        # Minecraft logs Done before all lifecycle callbacks complete.
                        # Allow a bounded post-startup window for recipe scanning and disk IO.
                        if time.monotonic() - ready_at < 75:
                            continue
                        print("FAIL: nutrition food report still missing after server start", flush=True)
                        print(current[-10000:], flush=True)
                        return 1
                    report = json.loads(reports[-1].read_text(encoding="utf-8"))
                    assert report.get("schema") == "backroom-agriculture-food-compat-v4.15"
                    assert report.get("edible_items", 0) >= 350
                    assert report.get("exact_catalog_profiles") == 403
                    assert report.get("edible_items") == len(report.get("items", []))
                    assert report.get("coverage_percent", -1) >= 0
                    print("NUTRITION_FOOD_SCANNER_REAL_SERVER_REPORT_PASS "
                          f"edibles={report['edible_items']} "
                          f"recipes={report['supported_edible_recipes']} "
                          f"derived={report['recipe_derived_profiles']}", flush=True)
                    print("GUGUGAGA_AGRICULTURE_MARINE_CUISINE_REAL_SERVER_BOOT_PASS", flush=True)
                    return 0
                if process.poll() is not None:
                    print("FAIL: server exited before ready", flush=True)
                    break
            else:
                print("FAIL: NeoForge launch timed out", flush=True)
            print(LOG.read_text(encoding="utf-8", errors="replace")[-12000:], flush=True)
            return 1
        finally:
            if process.poll() is None:
                try:
                    os.killpg(process.pid, signal.SIGTERM)
                except ProcessLookupError:
                    pass
                try:
                    process.wait(timeout=12)
                except subprocess.TimeoutExpired:
                    try:
                        os.killpg(process.pid, signal.SIGKILL)
                    except ProcessLookupError:
                        pass
                    process.wait(timeout=5)


if __name__ == "__main__":
    sys.exit(run())
