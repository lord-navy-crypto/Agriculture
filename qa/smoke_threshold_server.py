#!/usr/bin/env python3
"""Boot a disposable Threshold NeoForge server and verify both dimensions and first chunks."""
from __future__ import annotations

import os
import signal
import subprocess
import sys
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PROJECT = ROOT / "threshold"
LOG = ROOT / "build/threshold-server-boot.log"
READY = "Done ("
MARKERS = (
    "THRESHOLD_HORROR_RUNTIME_INIT_OK",
    "THRESHOLD_DIMENSIONS_REGISTRY_READY_OK",
    "THRESHOLD_LEVEL0_WORLDGEN_OK",
    "THRESHOLD_LEVEL1_WORLDGEN_OK",
)
LIMIT = int(os.environ.get("THRESHOLD_SERVER_BOOT_TIMEOUT", "420"))


def run() -> int:
    for folder in (PROJECT / "run/server", PROJECT / "runs/server"):
        folder.mkdir(parents=True, exist_ok=True)
        (folder / "eula.txt").write_text("eula=true\n", encoding="utf-8")

    LOG.parent.mkdir(parents=True, exist_ok=True)
    with LOG.open("wb") as stream:
        process = subprocess.Popen(
            ["gradle", "--no-daemon", "--console=plain", ":threshold:runServer"],
            cwd=ROOT,
            stdout=stream,
            stderr=subprocess.STDOUT,
            start_new_session=True,
        )
        try:
            deadline = time.monotonic() + LIMIT
            while time.monotonic() < deadline:
                time.sleep(2)
                current = LOG.read_text(encoding="utf-8", errors="replace")
                if all(marker in current for marker in MARKERS) and READY in current:
                    print("THRESHOLD_REAL_SERVER_DIMENSIONS_AND_WORLDGEN_PASS", flush=True)
                    return 0
                if process.poll() is not None:
                    print("FAIL: Threshold server exited before dimension checks completed", flush=True)
                    break
            else:
                print("FAIL: Threshold server boot timed out", flush=True)

            print(LOG.read_text(encoding="utf-8", errors="replace")[-16000:], flush=True)
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
