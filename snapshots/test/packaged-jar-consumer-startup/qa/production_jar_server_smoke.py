#!/usr/bin/env python3
"""Consumer verification of the actual RELEASE JAR inside a fresh NeoForge install.

Unlike gradle runServer, this test puts a packaged JAR in a normal mods/
directory and uses an official NeoForge server installer + the real GeckoLib
NeoForge dependency. It never uses the development run's mod classpath.

Requirements: installed Java 21 and JDK tools, curl, Python3.
"""
from __future__ import annotations
import hashlib
import os
import pathlib
import re
import signal
import subprocess
import sys
import time
import zipfile

ROOT=pathlib.Path(__file__).resolve().parents[1]
OUT=ROOT/"build/consumer-jar-smoke"
OUT.mkdir(parents=True,exist_ok=True)
MOD=ROOT/"build/libs/gugugaga-0.21.2-rc.1.jar"
NEO=os.environ.get("GUGU_CONSUMER_NEO","21.1.259")
GECKO=os.environ.get("GUGU_CONSUMER_GECKO","4.9.3")
JAR=f"geckolib-neoforge-1.21.1-{GECKO}.jar"
INSTALLER=f"neoforge-{NEO}-installer.jar"
LOG=OUT/"consumer-server-console.log"

def report(tag,value):
    print(tag,value,flush=True)
def download(url,dst):
    report("DOWNLOAD",url)
    subprocess.run(["curl","-fL","--retry","3","--retry-delay","2",
                    "--connect-timeout","20","-o",str(dst),url],check=True)
    if not zipfile.is_zipfile(dst):
        raise RuntimeError("Downloaded dependency/installer is not a valid JAR: "+str(dst))
def execute(args,timeout=240,logfile=None):
    report("EXECUTE"," ".join(str(a) for a in args))
    if logfile is None:
        subprocess.run(args,cwd=OUT,check=True,timeout=timeout)
    else:
        with logfile.open("wb") as f:
            subprocess.run(args,cwd=OUT,check=True,timeout=timeout,stdout=f,stderr=subprocess.STDOUT)

if not MOD.is_file():
    raise FileNotFoundError("You must compile the production JAR before testing: "+str(MOD))
with zipfile.ZipFile(MOD) as z:
    toml=z.read("META-INF/neoforge.mods.toml").decode()
    for required in ('modId="gugugaga"','version="0.21.2-rc.1"',
                     'versionRange="[1.21.1]"',
                     'versionRange="[21.1.150,)"',
                     'versionRange="[4.8.2,5.0.0)"'):
        if required not in toml:
            raise RuntimeError("Packaged release dependency metadata incorrect: "+required)
report("PRODUCTION_JAR_SHA256",hashlib.sha256(MOD.read_bytes()).hexdigest())
mods=OUT/"mods"
mods.mkdir(exist_ok=True)
artifact_mod=mods/MOD.name
artifact_mod.write_bytes(MOD.read_bytes())
gecko=mods/JAR
download(f"https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/software/bernie/geckolib/geckolib-neoforge-1.21.1/{GECKO}/{JAR}",gecko)
installer=OUT/INSTALLER
download(f"https://maven.neoforged.net/releases/net/neoforged/neoforge/{NEO}/{INSTALLER}",installer)
execute(["java","-jar",str(installer),"--installServer"],timeout=270,logfile=OUT/"installer-console.log")
sh=OUT/"run.sh"
if not sh.is_file():
    raise RuntimeError("Official installer did not create run.sh")
(OUT/"eula.txt").write_text("eula=true\n",encoding="utf-8")
# Only exactly TWO mods; deliberately not using Gradle's moddev source tree.
assert sorted(p.name for p in mods.iterdir())==sorted([MOD.name,JAR])
report("PRODUCTION_MODS_FOLDER",", ".join(p.name for p in mods.iterdir()))
deadline=time.monotonic()+int(os.environ.get("GUGU_CONSUMER_TIMEOUT","350"))
with LOG.open("wb") as fd:
    proc=subprocess.Popen(["bash",str(sh),"nogui"],
        cwd=OUT,stdout=fd,stderr=subprocess.STDOUT,start_new_session=True)
    try:
        while time.monotonic()<deadline:
            time.sleep(2)
            content=LOG.read_text(encoding="utf-8",errors="replace")
            # Prevent false positives from Gradle logs or a generated JAR that
            # isn't actually present in a Minecraft server's mods folder.
            runtime="GUGUGAGA_RUNTIME_INIT_OK" in content
            ready=bool(re.search(r"Done \([\d.,]+s\)! For help, type",content))
            if runtime and ready:
                report("GUGUGAGA_PRODUCTION_JAR_SERVER_PASS",
                    f"release jar loaded from mods with NeoForge {NEO} + GeckoLib {GECKO}")
                sys.exit(0)
            if proc.poll() is not None:
                raise RuntimeError("Standalone server stopped before successful initialization")
        raise RuntimeError("Standalone server did not finish initialization before timeout")
    except Exception:
        print("=== ACTUAL CONSUMER SERVER LOG ===",flush=True)
        print(LOG.read_text(encoding="utf-8",errors="replace")[-24000:],flush=True)
        raise
    finally:
        if proc.poll() is None:
            try:
                os.killpg(proc.pid,signal.SIGTERM)
                proc.wait(timeout=15)
            except Exception:
                try:os.killpg(proc.pid,signal.SIGKILL)
                except ProcessLookupError:pass
