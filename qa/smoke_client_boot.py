#!/usr/bin/env python3
"""Boot the real Minecraft 1.21.1 client on virtual Linux OpenGL.

Evidence: mod constructor, three native renderer registrations on MOD bus,
and actual client ticks. Does not claim player-in-world graphical acceptance.
"""
import os
from pathlib import Path
import signal
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[1]
LOG = ROOT / "build" / "client-runtime-smoke.log"
TOKENS = (
    "GUGUGAGA_RUNTIME_INIT_OK",
    "GUGUGAGA_CLIENT_RENDERERS_REGISTERED",
    "GUGUGAGA_V010_UI_KEY_REGISTERED",
    "GUGUGAGA_CLIENT_TICK_OK",
)
TIMEOUT = int(os.environ.get("GUGU_CLIENT_TIMEOUT", "420"))


def main():
    LOG.parent.mkdir(parents=True, exist_ok=True)
    command = [
        "xvfb-run", "-a", "-s",
        "-screen 0 1280x720x24 +extension GLX +render -noreset",
        "gradle", "--no-daemon", "--console=plain", "runClient",
    ]
    env = os.environ.copy()
    env["LIBGL_ALWAYS_SOFTWARE"] = "1"
    print("Starting real Minecraft graphical client on virtual Linux display.", flush=True)
    with LOG.open("wb") as stream:
        proc = subprocess.Popen(
            command, cwd=ROOT, stdout=stream, stderr=subprocess.STDOUT,
            env=env, start_new_session=True,
        )
        try:
            end = time.monotonic() + TIMEOUT
            while time.monotonic() < end:
                time.sleep(2)
                log = LOG.read_text(encoding="utf-8", errors="replace")
                if all(token in log for token in TOKENS):
                    print("GUGUGAGA_V010_REAL_CLIENT_BOOT_PASS", flush=True)
                    return 0
                if proc.poll() is not None:
                    print("FAIL: client exited before all startup markers.", flush=True)
                    break
            else:
                print("FAIL: client did not complete startup in time.", flush=True)
            log = LOG.read_text(encoding="utf-8", errors="replace")
            print("Missing:", [t for t in TOKENS if t not in log], flush=True)
            print(log[-25000:], flush=True)
            for p in (ROOT / "runs/client/logs/latest.log", ROOT / "run/logs/latest.log"):
                if p.exists():
                    print(str(p), p.read_text(encoding="utf-8", errors="replace")[-12000:], flush=True)
            return 1
        finally:
            if proc.poll() is None:
                try:
                    os.killpg(proc.pid, signal.SIGTERM)
                except ProcessLookupError:
                    pass
                try:
                    proc.wait(timeout=12)
                except subprocess.TimeoutExpired:
                    try:
                        os.killpg(proc.pid, signal.SIGKILL)
                    except ProcessLookupError:
                        pass
                    proc.wait(timeout=5)


if __name__ == "__main__":
    sys.exit(main())
