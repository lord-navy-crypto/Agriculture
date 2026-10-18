#!/usr/bin/env python3
"""Diagnostic virtual Minecraft desktop screenshots. Does NOT claim game QA."""
import pathlib,subprocess,os,time,signal,sys
ROOT=pathlib.Path(__file__).resolve().parents[1]
OUT=ROOT/"build/game-ui-probe"
OUT.mkdir(parents=True,exist_ok=True)
LOG=OUT/"client.log"
MARKER="GUGUGAGA_CLIENT_TICK_OK"
env=os.environ.copy(); env["LIBGL_ALWAYS_SOFTWARE"]="1"
def shell(*args):
    p=subprocess.run(args,env=env,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,timeout=15)
    print("COMMAND",args,"result",p.returncode,"out",p.stdout[:1800],flush=True);return p
def shot(name):
    p=OUT/(name+".png")
    shell("import","-window","root",str(p))
    print("SCREENSHOT",p,p.stat().st_size if p.exists() else "ABSENT",flush=True)
with LOG.open("wb") as fd:
    p=subprocess.Popen(["gradle","--no-daemon","--console=plain","runClient"],cwd=ROOT,env=env,stdout=fd,stderr=subprocess.STDOUT,start_new_session=True)
    try:
        deadline=time.monotonic()+470
        while time.monotonic()<deadline:
            if MARKER in LOG.read_text(errors="replace"):break
            if p.poll() is not None:raise RuntimeError("Minecraft exited: "+LOG.read_text(errors="replace")[-8000:])
            time.sleep(3)
        else:raise RuntimeError("No client tick before timeout")
        print("MAIN_MENU_TICK_REACHED",flush=True)
        time.sleep(6)
        shell("xdotool","search","--all","--name",".")
        shell("xdotool","getactivewindow","getwindowname")
        shot("a-accessibility-first-launch")
        # Known first-launch accessibility screen based on image artifact.
        shell("xdotool","mousemove","640","568","click","1")
        time.sleep(7)
        shot("b-real-main-menu")
        shell("xdotool","mousemove","640","290","click","1")
        time.sleep(4)
        shot("c-singleplayer-selection")
        shell("xdotool","mousemove","640","519","click","1")
        time.sleep(4)
        shot("d-create-world-screen")
        shell("xdotool","mousemove","640","565","click","1")
        time.sleep(12)
        shot("e-after-create-world-click")
        time.sleep(75)
        shot("f-after-world-generation")
        print("GUGU_GAME_UI_PROBE_IMAGES_CREATED",flush=True)
    finally:
        if p.poll() is None:
            try:os.killpg(p.pid,signal.SIGTERM);p.wait(timeout=8)
            except Exception:os.killpg(p.pid,signal.SIGKILL)
