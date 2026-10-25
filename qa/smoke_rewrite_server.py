#!/usr/bin/env python3
"""Run a disposable real NeoForge server and verify the agriculture mod loaded."""
from pathlib import Path
import os
import re
import signal
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[1]
LOG = ROOT / "build/agriculture-server-boot.log"
INIT = "GUGUGAGA_AGRICULTURE_RUNTIME_INIT_OK"
READY = re.compile(r"Done \([0-9.,]+s\)! For help, type", re.I)
LIMIT = int(os.environ.get("GUGUGAGA_SERVER_BOOT_TIMEOUT", "420"))

def run():
    for folder in (ROOT / "runs/server", ROOT / "run"):
        folder.mkdir(parents=True, exist_ok=True)
        (folder / "eula.txt").write_text("eula=true\n", encoding="utf-8")
    LOG.parent.mkdir(parents=True, exist_ok=True)
    with LOG.open("wb") as stream:
        process = subprocess.Popen(
            ["gradle", "--no-daemon", "--console=plain", "runServer"],
            cwd=ROOT, stdout=stream, stderr=subprocess.STDOUT,
            start_new_session=True)
        try:
            deadline = time.monotonic() + LIMIT
            while time.monotonic() < deadline:
                time.sleep(2)
                current = LOG.read_text(encoding="utf-8", errors="replace")
                if INIT in current and READY.search(current):
                    print("GUGUGAGA_AGRICULTURE_REAL_SERVER_BOOT_PASS", flush=True)
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
