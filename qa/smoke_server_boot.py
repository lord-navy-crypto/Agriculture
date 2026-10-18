#!/usr/bin/env python3
"""Launch REAL Minecraft NeoForge dedicated server with actual mod + GeckoLib.

Build is insufficient: a first-broken side/class/registry/animation dependency
often fails during mod loading. Enforces BOTH actual mod constructor and server
startup success in test environment; exits nonzero if early crash.
"""
import os
import pathlib
import re
import signal
import subprocess
import sys
import time

ROOT=pathlib.Path(__file__).resolve().parents[1]
LOG=ROOT/"build"/"server-runtime-smoke.log"
MAX_SECONDS=int(os.environ.get("GUGU_BOOT_TIMEOUT","350"))
MARKER="GUGUGAGA_RUNTIME_INIT_OK"
SERVER_READY=re.compile(r"Done \([0-9.,]+s\)! For help, type",re.I)

def test():
    LOG.parent.mkdir(parents=True,exist_ok=True)
    # ModDevGradle uses runs/server by default. Cover older run/ path as well.
    for path in (ROOT/"runs/server",ROOT/"run"):
        path.mkdir(parents=True,exist_ok=True)
        (path/"eula.txt").write_text("eula=true\n",encoding="utf8")
    print("Launching NeoForge test server with real registry, animation dependency and EULA accepted.",flush=True)
    with LOG.open("wb") as logfile:
        proc=subprocess.Popen(
            ["gradle","--no-daemon","--console=plain","runServer"],
            cwd=ROOT,stdout=logfile,stderr=subprocess.STDOUT,
            start_new_session=True)
        try:
            deadline=time.monotonic()+MAX_SECONDS
            while time.monotonic()<deadline:
                time.sleep(2)
                content=LOG.read_text(encoding="utf8",errors="replace")
                has_mod=MARKER in content
                has_server=bool(SERVER_READY.search(content))
                if has_mod and has_server:
                    print("GUGUGAGA_DEDICATED_SERVER_BOOT_PASS: mod constructed and server reached Done.",flush=True)
                    return 0
                if proc.poll() is not None:
                    print("FAIL: Minecraft/Gradle exited before fully initialized.",flush=True)
                    break
            else:
                print("FAIL: Minecraft did not reach mod+server ready markers before timeout.",flush=True)
            print("=== SERVER STARTUP LOG TAIL ===",flush=True)
            print(LOG.read_text(encoding="utf8",errors="replace")[-25000:],flush=True)
            return 1
        finally:
            if proc.poll() is None:
                # Descendants (Minecraft Java run inside Gradle) must not leak
                # onto shared GitHub runners.
                try:os.killpg(proc.pid,signal.SIGTERM)
                except ProcessLookupError:pass
                try:proc.wait(timeout=12)
                except subprocess.TimeoutExpired:
                    try:os.killpg(proc.pid,signal.SIGKILL)
                    except ProcessLookupError:pass
                    proc.wait(timeout=5)

if __name__=="__main__":
    sys.exit(test())
