#!/usr/bin/env python3
"""Real graphical Minecraft integrated-server game test using mouse+keyboard.

No fabricated success states. One throwaway 1.21.1 world is created from the
actual vanilla menu, commands typed into Minecraft chat and real entities are
loaded. Check server-side CI-only event telemetry + in-world screenshots.
"""
import json
import os
import pathlib
import signal
import subprocess
import sys
import time

ROOT=pathlib.Path(__file__).resolve().parents[1]
OUT=ROOT/"build"/"gameplay-e2e"
OUT.mkdir(parents=True,exist_ok=True)
LOG=OUT/"real-minecraft-game.log"
TIMEOUT=int(os.environ.get("GUGU_GAME_TIMEOUT","570"))
env=os.environ.copy()
env["JAVA_TOOL_OPTIONS"]="-Dgugugaga.e2e=true"
env["LIBGL_ALWAYS_SOFTWARE"]="1"
results={}
start=time.monotonic()

def readlog():return LOG.read_text(encoding="utf8",errors="replace") if LOG.exists() else ""
def command(*args,check=False):
    p=subprocess.run(args,env=env,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,
                     text=True,timeout=20)
    if check and p.returncode!=0:raise RuntimeError("shell command failed: "+repr(args)+p.stdout[-1200:])
    return p.stdout
def snap(label):
    name=OUT/(label+".png")
    command("import","-window","root",str(name),check=True)
    print("SCREENSHOT",name.name,name.stat().st_size,flush=True)
def click(x,y):
    command("xdotool","mousemove",str(x),str(y),"click","1",check=True)
def key(*names):
    command("xdotool","key","--clearmodifiers",*names,check=True)
def wait_marker(token,secs,required=True):
    deadline=time.monotonic()+secs
    while time.monotonic()<deadline:
        if "GUGUGAGA_E2E_"+token in readlog():
            print("E2E_MILESTONE",token,flush=True)
            results[token]=True
            return True
        if proc.poll() is not None:
            raise RuntimeError("Minecraft quit before marker "+token+": "+readlog()[-8000:])
        time.sleep(2)
    results[token]=False
    print("E2E_MISSING",token,flush=True)
    return not required
def say(message,pause=2.4):
    key("t")
    time.sleep(.35)
    command("xdotool","type","--clearmodifiers","--delay","12",message,check=True)
    key("Return")
    print("GAME_CHAT_INPUT",repr(message),flush=True)
    time.sleep(pause)

with LOG.open("wb") as f:
    proc=subprocess.Popen(["gradle","--no-daemon","--console=plain","runClient"],
        cwd=ROOT,env=env,stdout=f,stderr=subprocess.STDOUT,start_new_session=True)
    try:
        deadline=time.monotonic()+TIMEOUT
        while time.monotonic()<deadline:
            if "GUGUGAGA_CLIENT_TICK_OK" in readlog():break
            if proc.poll() is not None:raise RuntimeError("Client failed to boot: "+readlog()[-14000:])
            time.sleep(2)
        else:raise RuntimeError("No real Minecraft graphical client tick")
        time.sleep(4)
        snap("01-accessibility")
        click(640,568) # first launch Continue
        time.sleep(4)
        snap("02-main-menu")
        click(640,324) # Singleplayer; first world opens world creation screen
        time.sleep(4)
        snap("03-world-creation")
        click(640,404) # Allow Commands: OFF -> ON
        time.sleep(1)
        snap("04-cheats-enabled")
        click(481,568) # actual left Create New World button
        print("WAITING_FOR_REAL_SINGLEPLAYER_WORLD",flush=True)
        if not wait_marker("WORLD_PLAYER_READY",240):
            snap("05-world-creation-failure")
            raise RuntimeError("Real player did not enter integrated world")
        time.sleep(5)
        snap("05-in-world-survival")
        # Commands typed into REAL GUI, not injected from a fake Java player.
        say("/gamerule doDaylightCycle false")
        say("/time set night")
        wait_marker("AUTO_JOINED",12,required=False)
        # Open the real H-key panel and use its first button, rather than
        # typing the join command. This verifies normal play is UI-driven.
        key("h")
        time.sleep(1.2)
        wait_marker("MENU_OPENED",6,required=False)
        snap("05b-nightwatch-ui")
        click(541,295)  # first-row Join / Resume button at 1280x720
        time.sleep(2)
        wait_marker("JOINED",8,required=False)
        snap("05c-ui-join")
        say("/weather clear")
        say("/fill ~-12 ~-1 ~-12 ~12 ~-1 ~12 minecraft:stone",2)
        say("/fill ~-12 ~ ~-12 ~12 ~8 ~12 minecraft:air",3)
        snap("06-disposable-flat-arena")
        say("/gugugaga play server on")
        wait_marker("SERVER_ENABLED",8)
        say("/gugugaga play join")
        wait_marker("JOINED",8)
        say("/gugugaga play test triad",6)
        snap("07-triad-entities")
        for m in ("SPAWN_GUGU","SPAWN_LAPIS","SPAWN_YELLOW","TRIAD_REAL_ENTITIES"):
            if not wait_marker(m,8,required=False):
                print("DIAGNOSTIC missing spawn",m,flush=True)
        # Move EXISTING real mobs into visible/loaded test arena.
        say("/tp @e[type=gugugaga:observer,sort=nearest,limit=1] ~ ~ ~5")
        say("/tp @e[type=gugugaga:redactor,sort=nearest,limit=1] ~3 ~ ~5")
        say("/tp @e[type=gugugaga:yellow_smiler,sort=nearest,limit=1] ~-3 ~ ~5")
        time.sleep(2)
        snap("08-real-three-entities")
        # Teleports take several seconds through the real GUI. Re-arm the
        # explicit operator test scene AFTER positioning the actors so its
        # FALSE_SAFETY window is fresh when the real chat HELP is typed.
        say("/gugugaga play test triad",1.0)
        # Exercise HELP immediately in the valid FALSE_SAFETY act while all
        # three existing actors are deliberately placed nearby and visible.
        # Do this before long gaze/loop tests can advance the stage or consume
        # the rescue cooldown.
        say("HELP",3.5)
        wait_marker("CHAT_HELP_INTERCEPTED",6,required=False)
        wait_marker("HELP_SUCCESS",6,required=False)
        snap("08b-chat-help")
        # Yellow proactively drives away Gugu; keep guardian away during
        # the independent natural-looking investigation acceptance.
        say("/tp @e[type=gugugaga:yellow_smiler,sort=nearest,limit=1] ~30 ~ ~30")
        say("/effect give @e[type=gugugaga:yellow_smiler,limit=1,sort=nearest] minecraft:slowness 35 255 true")
        say("/gugugaga play test witness")
        say("/effect give @e[type=gugugaga:observer,limit=1,sort=nearest] minecraft:slowness 35 255 true")
        # Place immobilized actual mob at known positive Z, then face SOUTH (yaw 0).
        # Never aim by facing an actor which has walked within one block.
        say("/tp @e[type=gugugaga:observer,limit=1,sort=nearest] ~ ~ ~6")
        say("/tp @s ~ ~ ~ 0 0")
        time.sleep(6)
        wait_marker("STAGE_FALSE_SAFETY",5,required=False)
        snap("09-witness-four-second-gaze")
        say("/tp @e[type=gugugaga:observer,limit=1,sort=nearest] ~20 ~ ~20")
        say("/effect give @e[type=gugugaga:redactor,limit=1,sort=nearest] minecraft:slowness 35 255 true")
        say("/tp @e[type=gugugaga:redactor,limit=1,sort=nearest] ~ ~ ~6")
        say("/tp @s ~ ~ ~ 0 0")
        time.sleep(6)
        wait_marker("STAGE_RESPITE",5,required=False)
        snap("10-false-safety-four-second-gaze")
        # At night with gamerule frozen, 70 seconds valid play time
        # is sufficient for automatic fifth -> first cycling.
        if results.get("STAGE_RESPITE"):
            wait_marker("LOOP_TO_STILLNESS",88,required=False)
        snap("11-after-five-stage-cycle")
        # Restore the existing mobs near the player, then re-enter the live
        # false-safety act to test a real uppercase chat HELP dispatch.
        say("/effect clear @e[type=gugugaga:observer,limit=1,sort=nearest] minecraft:slowness")
        say("/effect clear @e[type=gugugaga:yellow_smiler,limit=1,sort=nearest] minecraft:slowness")
        say("/tp @e[type=gugugaga:observer,sort=nearest,limit=1] ~ ~ ~5")
        say("/tp @e[type=gugugaga:yellow_smiler,sort=nearest,limit=1] ~-3 ~ ~5")
        say("/gugugaga play test triad",4)
        snap("12-rejoined-triad")
        # HELP was already tested above during the active FALSE_SAFETY act;
        # do not issue a second call that is correctly blocked by cooldown.
        # xdotool 'key' releases shift; send real held key separately.
        command("xdotool","keydown","Shift_L")
        time.sleep(1)
        command("xdotool","key","v",check=True)
        time.sleep(2)
        command("xdotool","keyup","Shift_L")
        wait_marker("LISTEN_V_KEY",7,required=False)
        wait_marker("LISTEN_SUCCESS",7,required=False)
        snap("13-sneak-V-listen")
        # Test real right-click interactions against the rendered entities.
        say("/gugugaga play test witness",4)
        say("/effect give @e[type=gugugaga:observer,limit=1,sort=nearest] minecraft:slowness 90 255 true")
        say("/tp @e[type=gugugaga:observer,limit=1,sort=nearest] ~ ~ ~5")
        say("/tp @s ~ ~ ~ 0 10")
        command("xdotool","click","3",check=True)
        wait_marker("INTERACT_GUGU",6,required=False)
        wait_marker("INTERACT_GUGU_STAGE_PROGRESS",6,required=False)
        say("/gugugaga play test triad",4)
        say("/effect give @e[type=gugugaga:redactor,limit=1,sort=nearest] minecraft:slowness 90 255 true")
        say("/tp @e[type=gugugaga:redactor,limit=1,sort=nearest] ~ ~ ~5")
        say("/tp @s ~ ~ ~ 0 10")
        command("xdotool","click","3",check=True)
        wait_marker("INTERACT_LAPIS",6,required=False)
        wait_marker("INTERACT_LAPIS_STAGE_PROGRESS",6,required=False)
        say("/gugugaga play test triad",4)
        say("/effect give @e[type=gugugaga:yellow_smiler,limit=1,sort=nearest] minecraft:slowness 90 255 true")
        say("/tp @e[type=gugugaga:yellow_smiler,limit=1,sort=nearest] ~ ~ ~5")
        say("/tp @s ~ ~ ~ 0 10")
        command("xdotool","click","3",check=True)
        wait_marker("INTERACT_YELLOW",6,required=False)
        wait_marker("INTERACT_YELLOW_STAGE_PROGRESS",6,required=False)
        snap("14-real-entity-interactions")
        # Apply actual Minecraft player_attack damage to each real actor.
        # The server must record an escape/recovery instead of an instant kill.
        say("/effect clear @e[type=gugugaga:observer,limit=1,sort=nearest] minecraft:slowness")
        say("/effect clear @e[type=gugugaga:redactor,limit=1,sort=nearest] minecraft:slowness")
        say("/effect clear @e[type=gugugaga:yellow_smiler,limit=1,sort=nearest] minecraft:slowness")
        say("/tp @e[type=gugugaga:observer,sort=nearest,limit=1] ~ ~ ~5")
        say("/tp @e[type=gugugaga:redactor,sort=nearest,limit=1] ~3 ~ ~5")
        say("/tp @e[type=gugugaga:yellow_smiler,sort=nearest,limit=1] ~-3 ~ ~5")
        say("/damage @e[type=gugugaga:observer,sort=nearest,limit=1] 100 minecraft:player_attack by @s",3)
        wait_marker("GUGU_WEAPON_SURVIVED",5,required=False)
        say("/damage @e[type=gugugaga:redactor,sort=nearest,limit=1] 100 minecraft:player_attack by @s",3)
        wait_marker("LAPIS_WEAPON_SURVIVED",5,required=False)
        say("/damage @e[type=gugugaga:yellow_smiler,sort=nearest,limit=1] 100 minecraft:player_attack by @s",3)
        wait_marker("YELLOW_WEAPON_SURVIVED",5,required=False)
        snap("14-weapon-resilience")
        missing=[x for x in ("WORLD_PLAYER_READY","SERVER_ENABLED","JOINED",
                           "SPAWN_GUGU","SPAWN_LAPIS","SPAWN_YELLOW",
                           "TRIAD_REAL_ENTITIES","CHAT_HELP_INTERCEPTED",
                           "HELP_SUCCESS","LISTEN_V_KEY","LISTEN_SUCCESS",
                           "STAGE_FALSE_SAFETY","STAGE_RESPITE","LOOP_TO_STILLNESS",
                           "GUGU_WEAPON_SURVIVED","LAPIS_WEAPON_SURVIVED",
                           "YELLOW_WEAPON_SURVIVED","AUTO_JOINED","MENU_OPENED",
                           "INTERACT_GUGU","INTERACT_LAPIS","INTERACT_YELLOW",
                           "INTERACT_GUGU_STAGE_PROGRESS",
                           "INTERACT_LAPIS_STAGE_PROGRESS",
                           "INTERACT_YELLOW_STAGE_PROGRESS")
                 if not results.get(x)]
        (OUT/"results.json").write_text(json.dumps({"markers":results,
                 "missing":missing,"elapsed_seconds":round(time.monotonic()-start,1)},indent=2))
        if missing:
            raise RuntimeError("ACTUAL GAMEPLAY TEST FAILED: missing "+", ".join(missing))
        print("GUGUGAGA_REAL_IN_WORLD_GAMEPLAY_PASS",flush=True)
    except Exception:
        print("=== GAMEPLAY CONSOLE LOG TAIL ===",flush=True)
        print(readlog()[-14000:],flush=True)
        raise
    finally:
        (OUT/"results-partial.json").write_text(json.dumps(results,indent=2))
        if proc.poll() is None:
            try:os.killpg(proc.pid,signal.SIGTERM);proc.wait(timeout=12)
            except Exception:
                try:os.killpg(proc.pid,signal.SIGKILL)
                except ProcessLookupError:pass
