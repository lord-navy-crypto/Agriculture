#!/usr/bin/env python3
"""Start actual Minecraft client with virtual OpenGL desktop in CI.

Require THREE independent milestones, otherwise fail (not compile-only):
 - Gugu-Gaga mod constructor loaded
 - ALL three client-only GeckoLib renderers registered on MOD bus
 - Minecraft client reached ticking render window/title screen

Virtual X11/OpenGL is diagnostic; it is not a test of physical Mac rendering.
"""
import os
import pathlib
import signal
import subprocess
import sys
import time

ROOT=pathlib.Path(__file__).resolve().parents[1]
LOG=ROOT/"build/client-runtime-smoke.log"
TOKENS=("GUGUGAGA_RUNTIME_INIT_OK",
        "GUGUGAGA_CLIENT_RENDERERS_REGISTERED",
        "GUGUGAGA_CLIENT_TICK_OK")
TIMEOUT=int(os.environ.get("GUGU_CLIENT_TIMEOUT","400"))

def main():
    LOG.parent.mkdir(parents=True,exist_ok=True)
    command=["xvfb-run","-a","-s","-screen 0 1280x720x24 +extension GLX +render -noreset",
             "gradle","--no-daemon","--console=plain","runClient"]
    env=os.environ.copy()
    env["LIBGL_ALWAYS_SOFTWARE"]="1"
    print("Launching real NeoForge Minecraft 1.21.1 CLIENT via Xvfb software OpenGL",flush=True)
    print("Markers:",", ".join(TOKENS),flush=True)
    with LOG.open("wb") as stream:
        proc=subprocess.Popen(command,cwd=ROOT,stdout=stream,
                              stderr=subprocess.STDOUT,start_new_session=True,env=env)
        try:
            deadline=time.monotonic()+TIMEOUT
            while time.monotonic()<deadline:
                time.sleep(2)
                log=LOG.read_text(encoding="utf8",errors="replace")
                if all(token in log for token in TOKENS):
                    print("GUGUGAGA_REAL_CLIENT_BOOT_PASS: mod init + THREE renderers + client tick.",flush=True)
                    return 0
                if proc.poll() is not None:
                    print("FAIL: client closed before all three runtime markers.",flush=True)
                    break
            else:
                print("FAIL: real client did not finish loading before the timeout.",flush=True)
            log=LOG.read_text(encoding="utf8",errors="replace")
            print("Missing markers:",[x for x in TOKENS if x not in log],flush=True)
            print("=== REAL CLIENT CONSOLE TAIL ===",flush=True)
            print(log[-25000:],flush=True)
            for p in (ROOT/"runs/client/logs/latest.log",ROOT/"run/logs/latest.log"):
                if p.exists():
                    print("=== MINECRAFT CLIENT latest.log TAIL ===",flush=True)
                    print(p.read_text(encoding="utf8",errors="replace")[-16000:],flush=True)
            return 1
        finally:
            if proc.poll() is None:
                try:os.killpg(proc.pid,signal.SIGTERM)
                except ProcessLookupError:pass
                try:proc.wait(timeout=12)
                except subprocess.TimeoutExpired:
                    try:os.killpg(proc.pid,signal.SIGKILL)
                    except ProcessLookupError:pass
                    proc.wait(timeout=5)

if __name__=="__main__":sys.exit(main())
