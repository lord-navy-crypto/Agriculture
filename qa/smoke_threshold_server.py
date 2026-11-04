#!/usr/bin/env python3
"""Boot Threshold with the Agriculture prerequisite and verify real dimension generation."""
from pathlib import Path
import os, signal, subprocess, sys, time, shutil

ROOT=Path(__file__).resolve().parents[1]
PROJECT=ROOT/"threshold"
LOG=ROOT/"build/threshold-server-boot.log"
LIMIT=int(os.environ.get("THRESHOLD_SERVER_BOOT_TIMEOUT","420"))
READY="Done ("
MARKERS=("THRESHOLD_HORROR_RUNTIME_INIT_OK","THRESHOLD_DIMENSIONS_REGISTRY_READY_OK",
         "THRESHOLD_LEVEL0_WORLDGEN_OK","THRESHOLD_LEVEL1_WORLDGEN_OK",
         "THRESHOLD_LEVEL0_TRANSITION_OK","THRESHOLD_LEVEL0_LIGHTING_OK",
         "THRESHOLD_LEVEL1_RETURN_OK",
         "THRESHOLD_LEVEL1_STARTER_CACHE_OK","THRESHOLD_LEVEL1_LIT_PLANTER_OK",
         "THRESHOLD_LEVEL1_AGRICULTURE_CROP_OK", "THRESHOLD_LITHOSTITCHED_VENT_OK")

def run():
    # Dev launches use only the Threshold source set; copy the real Agriculture
    # mod into the run's mods directory so its mandatory dependency is exercised.
    subprocess.run(["gradle","--no-daemon","--console=plain",":jar"],
                   cwd=ROOT,check=True)
    agriculture=sorted((ROOT/"build/libs").glob("gugugaga-*.jar"))
    if not agriculture:
        raise RuntimeError("Agriculture prerequisite JAR missing")
    for folder in (PROJECT/"run",PROJECT/"run/server",
                   PROJECT/"runs/server",PROJECT/"runs"):
        folder.mkdir(parents=True,exist_ok=True)
        (folder/"eula.txt").write_text("eula=true\n",encoding="utf-8")
        mods=folder/"mods"
        mods.mkdir(parents=True,exist_ok=True)
        shutil.copy2(agriculture[-1],mods/agriculture[-1].name)

    LOG.parent.mkdir(parents=True,exist_ok=True)
    with LOG.open("wb") as stream:
        process=subprocess.Popen(
            ["gradle","--no-daemon","--console=plain",":threshold:runServer"],
            cwd=ROOT,stdout=stream,stderr=subprocess.STDOUT,start_new_session=True)
        try:
            deadline=time.monotonic()+LIMIT
            while time.monotonic()<deadline:
                time.sleep(2)
                current=LOG.read_text(encoding="utf-8",errors="replace")
                if all(m in current for m in MARKERS) and READY in current:
                    print("THRESHOLD_REAL_SERVER_DIMENSIONS_AND_WORLDGEN_PASS",flush=True)
                    return 0
                if any(marker in current for marker in (
                    "THRESHOLD_LEVEL0_WORLDGEN_FAIL", "THRESHOLD_LEVEL1_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL0_TRANSITION_FAIL","THRESHOLD_LEVEL0_LIGHTING_FAIL",
                    "THRESHOLD_LEVEL1_RETURN_FAIL",
                    "THRESHOLD_LEVEL1_STARTER_CACHE_FAIL","THRESHOLD_LEVEL1_LIT_PLANTER_FAIL",
                    "THRESHOLD_LEVEL1_AGRICULTURE_CROP_FAIL","THRESHOLD_LITHOSTITCHED_VENT_FAIL")):
                    print("FAIL: a required Threshold dimension did not generate", flush=True)
                    print(current[-14000:], flush=True)
                    return 1
                if process.poll() is not None:
                    print("FAIL: Threshold server exited early",flush=True)
                    break
            else:
                print("FAIL: Threshold startup timed out",flush=True)
            print(LOG.read_text(encoding="utf-8",errors="replace")[-16000:],flush=True)
            return 1
        finally:
            if process.poll() is None:
                try: os.killpg(process.pid,signal.SIGTERM)
                except ProcessLookupError: pass
                try: process.wait(timeout=12)
                except subprocess.TimeoutExpired:
                    try: os.killpg(process.pid,signal.SIGKILL)
                    except ProcessLookupError: pass
                    process.wait(timeout=5)

if __name__=="__main__":
    sys.exit(run())
