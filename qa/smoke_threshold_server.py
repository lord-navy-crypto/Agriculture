#!/usr/bin/env python3
"""Boot the unified Backroom Agriculture mod and verify all six dimensions."""
from pathlib import Path
import os, signal, subprocess, sys, time

ROOT=Path(__file__).resolve().parents[1]
PROJECT=ROOT
LOG=ROOT/"build/threshold-server-boot.log"
LIMIT=int(os.environ.get("THRESHOLD_SERVER_BOOT_TIMEOUT","210"))
READY="Done ("
MARKERS=("THRESHOLD_HORROR_RUNTIME_INIT_OK","THRESHOLD_DIMENSIONS_REGISTRY_READY_OK",
         "THRESHOLD_ENTITY_REGISTRY_OK","THRESHOLD_LEVEL1_HOUND_REGISTRY_OK",
         "THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_OK",
         "THRESHOLD_LEVEL1_HOUND_ADVANCED_AI_READY_OK",
         "THRESHOLD_ENTITY_COMPATIBILITY_ALL_DIMENSIONS_OK",
         "THRESHOLD_NATURAL_SPAWN_POLICY_OK","THRESHOLD_LEVEL1_ENTITY_VARIETY_OK",
         "THRESHOLD_LEVEL0_LEVEL1_IRREGULAR_MAZE_OK",
         "THRESHOLD_LEVEL0_VARIABLE_DOORWAYS_OK",
         "THRESHOLD_LEVEL1_VARIABLE_DOORWAYS_OK",
         "THRESHOLD_LEVEL0_NESTED_POCKETS_OK",
         "THRESHOLD_LEVEL1_NESTED_POCKETS_OK",
         "THRESHOLD_LEVEL0_PHYSICAL_MAZE_CLEARANCE_OK",
         "THRESHOLD_LEVEL1_PHYSICAL_MAZE_CLEARANCE_OK",
         "THRESHOLD_NATURAL_SPAWN_REASON_POLICY_OK",
         "THRESHOLD_LEVEL0_NO_OTHER_CREATURES_OK",
         "THRESHOLD_LEVEL1_STARTER_SHELTER_CLEAR_OK",
         "THRESHOLD_LEVEL0_WORLDGEN_OK","THRESHOLD_LEVEL1_WORLDGEN_OK",
         "THRESHOLD_LEVEL0_VERTICAL_LAYOUT_OK","THRESHOLD_LEVEL1_VERTICAL_LAYOUT_OK",
         "THRESHOLD_LEVEL0_MEZZANINE_OK","THRESHOLD_LEVEL0_NESTED_MAZE_OK",
         "THRESHOLD_LEVEL0_SUNKEN_PIT_OK","THRESHOLD_LEVEL1_MEZZANINE_OK",
         "THRESHOLD_LEVEL1_PUMP_SUMP_OK","THRESHOLD_LEVEL1_RACK_CANYON_OK",
         "THRESHOLD_LEVEL0_PILLAR_FOREST_OK","THRESHOLD_LEVEL0_FLOOR_FOLD_OK",
         "THRESHOLD_LEVEL0_STAIR_TOWER_OK","THRESHOLD_LEVEL1_GOTHIC_ARCADE_OK",
         "THRESHOLD_LEVEL1_CRANE_HALL_OK","THRESHOLD_LEVEL1_VENTILATION_HALL_OK",
         "THRESHOLD_LEVEL0_BLACKOUT_CORRIDOR_OK","THRESHOLD_LEVEL1_GENERATOR_PLANT_OK",
         "THRESHOLD_LEVEL0_PASSAGE_FOOTING_OK",
         "THRESHOLD_LEVEL0_TRANSITION_OK","THRESHOLD_LEVEL0_LIGHTING_OK",
         "THRESHOLD_LEVEL1_RETURN_OK",
         "THRESHOLD_LEVEL1_STARTER_CACHE_OK","THRESHOLD_LEVEL1_LIT_PLANTER_OK",
         "THRESHOLD_LEVEL1_AGRICULTURE_CROP_OK", "THRESHOLD_LITHOSTITCHED_VENT_OK",
         "THRESHOLD_AGRICULTURE_SURVIVAL_ITEMS_OK",
         "THRESHOLD_LEVEL0_ALL_WALL_FLOOR_CONTACT_OK",
         "THRESHOLD_LEVEL1_FLICKERING_LIGHT_OK",
         "THRESHOLD_LEVEL0_HORROR_IDENTITY_OK",
         "THRESHOLD_LEVEL2_WORLDGEN_OK",
         "THRESHOLD_LEVEL3_WORLDGEN_OK",
         "THRESHOLD_LEVEL4_WORLDGEN_OK",
         "THRESHOLD_LEVEL5_MAIN_HALL_WORLDGEN_OK",
         "THRESHOLD_LEVEL5_BEVERLY_ROOM_WORLDGEN_OK",
         "THRESHOLD_LEVEL5_BOILER_ROOM_WORLDGEN_OK",
         "THRESHOLD_LEVEL3_ELEVATOR_TO_LEVEL5_ROUTE_OK",
         "THRESHOLD_LEVEL4_STAIRWAY_TO_LEVEL5_ROUTE_OK",
         "THRESHOLD_LEVEL5_TO_LEVEL3_ELEVATOR_RETURN_OK",
         "THRESHOLD_LEVEL5_TO_LEVEL4_STAIRWAY_RETURN_OK",
         "THRESHOLD_LEVEL5_INTERNAL_ELEVATOR_PANEL_OK",
         "THRESHOLD_LEVEL1_TO_LEVEL2_PASSAGE_OK",
         "THRESHOLD_LEVEL2_FIRE_EXIT_ROUTE_OK",
         "THRESHOLD_LEVEL2_TO_LEVEL3_PASSAGE_OK",
         "THRESHOLD_LEVEL2_WEIGHTED_BRANCH_OK",
         "THRESHOLD_LEVEL3_TO_LEVEL2_PASSAGE_OK",
         "THRESHOLD_LEVEL3_TO_LEVEL4_PASSAGE_OK",
         "THRESHOLD_LEVEL4_TO_LEVEL3_PASSAGE_OK",
         "THRESHOLD_LEVEL0_MUSIC_REGISTRY_OK",
         "THRESHOLD_LEVEL1_MUSIC_REGISTRY_OK",
         "THRESHOLD_LEVEL1_HORROR_IDENTITY_OK",
         "THRESHOLD_LEVEL1_FRESHWATER_REFILL_OK",
         "THRESHOLD_LEVEL1_AGRICULTURE_CROP_DIVERSITY_OK",
         "THRESHOLD_LEVEL1_COMPOSTER_OK","THRESHOLD_LEVEL1_ENTITY_VARIETY_OK",
         "THRESHOLD_LEVEL0_LEVEL1_IRREGULAR_MAZE_OK",
         "THRESHOLD_LEVEL0_NESTED_POCKETS_OK",
         "THRESHOLD_LEVEL1_NESTED_POCKETS_OK",
         "THRESHOLD_LEVEL0_PHYSICAL_MAZE_CLEARANCE_OK",
         "THRESHOLD_LEVEL1_PHYSICAL_MAZE_CLEARANCE_OK")

def run():
    # Build and launch one integrated mod; libraries are runtime dependencies.
    subprocess.run(["gradle","--no-daemon","--console=plain","build"],
                   cwd=ROOT,check=True)
    server_dir=ROOT/"run/server"
    server_dir.mkdir(parents=True,exist_ok=True)
    (server_dir/"eula.txt").write_text("eula=true\\n",encoding="utf-8")
    LOG.parent.mkdir(parents=True,exist_ok=True)
    with LOG.open("wb") as stream:
        process=subprocess.Popen(
            ["gradle","--no-daemon","--console=plain","runServer"],
            cwd=ROOT,stdout=stream,stderr=subprocess.STDOUT,start_new_session=True)
        try:
            deadline=time.monotonic()+LIMIT
            while time.monotonic()<deadline:
                time.sleep(2)
                current=LOG.read_text(encoding="utf-8",errors="replace")
                if all(m in current for m in MARKERS) and READY in current:
                    print("THRESHOLD_REAL_SERVER_SIX_DIMENSIONS_AND_WORLDGEN_PASS",flush=True)
                    return 0
                if any(marker in current for marker in (
                    "THRESHOLD_ENTITY_REGISTRY_FAIL","THRESHOLD_LEVEL1_HOUND_REGISTRY_FAIL",
                    "THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_FAIL",
                    "THRESHOLD_LEVEL1_HOUND_ADVANCED_AI_READY_FAIL",
                    "THRESHOLD_ENTITY_COMPATIBILITY_ALL_DIMENSIONS_FAIL",
                    "THRESHOLD_NATURAL_SPAWN_POLICY_FAIL","THRESHOLD_LEVEL1_ENTITY_VARIETY_FAIL",
                    "THRESHOLD_LEVEL0_LEVEL1_IRREGULAR_MAZE_FAIL",
                    "THRESHOLD_LEVEL0_NESTED_POCKETS_FAIL",
                    "THRESHOLD_LEVEL1_NESTED_POCKETS_FAIL",
                    "THRESHOLD_LEVEL0_PHYSICAL_MAZE_CLEARANCE_FAIL",
                    "THRESHOLD_LEVEL1_PHYSICAL_MAZE_CLEARANCE_FAIL",
                    "THRESHOLD_NATURAL_SPAWN_REASON_POLICY_FAIL",
                    "THRESHOLD_LEVEL0_NO_OTHER_CREATURES_FAIL",
                    "THRESHOLD_LEVEL1_STARTER_SHELTER_CLEAR_FAIL",
                    "THRESHOLD_LEVEL0_WORLDGEN_FAIL", "THRESHOLD_LEVEL1_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL0_VERTICAL_LAYOUT_FAIL", "THRESHOLD_LEVEL1_VERTICAL_LAYOUT_FAIL",
                    "THRESHOLD_LEVEL0_MEZZANINE_FAIL","THRESHOLD_LEVEL0_NESTED_MAZE_FAIL",
                    "THRESHOLD_LEVEL0_SUNKEN_PIT_FAIL","THRESHOLD_LEVEL1_MEZZANINE_FAIL",
                    "THRESHOLD_LEVEL1_PUMP_SUMP_FAIL","THRESHOLD_LEVEL1_RACK_CANYON_FAIL",
                    "THRESHOLD_LEVEL0_PILLAR_FOREST_FAIL","THRESHOLD_LEVEL0_FLOOR_FOLD_FAIL",
                    "THRESHOLD_LEVEL0_STAIR_TOWER_FAIL","THRESHOLD_LEVEL1_GOTHIC_ARCADE_FAIL",
                    "THRESHOLD_LEVEL1_CRANE_HALL_FAIL","THRESHOLD_LEVEL1_VENTILATION_HALL_FAIL",
                    "THRESHOLD_LEVEL0_BLACKOUT_CORRIDOR_FAIL","THRESHOLD_LEVEL1_GENERATOR_PLANT_FAIL",
                    "THRESHOLD_LEVEL0_PASSAGE_FOOTING_FAIL",
                    "THRESHOLD_LEVEL0_TRANSITION_FAIL","THRESHOLD_LEVEL0_LIGHTING_FAIL",
                    "THRESHOLD_LEVEL1_RETURN_FAIL",
                    "THRESHOLD_LEVEL1_STARTER_CACHE_FAIL","THRESHOLD_LEVEL1_LIT_PLANTER_FAIL",
                    "THRESHOLD_LEVEL1_AGRICULTURE_CROP_FAIL","THRESHOLD_LITHOSTITCHED_VENT_FAIL",
                    "THRESHOLD_AGRICULTURE_SURVIVAL_ITEMS_FAIL",
                    "THRESHOLD_LEVEL0_ALL_WALL_FLOOR_CONTACT_FAIL",
                    "THRESHOLD_LEVEL1_FLICKERING_LIGHT_FAIL",
                    "THRESHOLD_LEVEL0_HORROR_IDENTITY_FAIL",
                    "THRESHOLD_LEVEL2_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL3_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL4_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL5_MAIN_HALL_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL5_BEVERLY_ROOM_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL5_BOILER_ROOM_WORLDGEN_FAIL",
                    "THRESHOLD_LEVEL3_ELEVATOR_TO_LEVEL5_ROUTE_FAIL",
                    "THRESHOLD_LEVEL4_STAIRWAY_TO_LEVEL5_ROUTE_FAIL",
                    "THRESHOLD_LEVEL5_TO_LEVEL3_ELEVATOR_RETURN_FAIL",
                    "THRESHOLD_LEVEL5_TO_LEVEL4_STAIRWAY_RETURN_FAIL",
                    "THRESHOLD_LEVEL5_INTERNAL_ELEVATOR_PANEL_FAIL",
                    "THRESHOLD_LEVEL1_TO_LEVEL2_PASSAGE_FAIL",
                    "THRESHOLD_LEVEL2_FIRE_EXIT_ROUTE_FAIL",
                    "THRESHOLD_LEVEL2_TO_LEVEL3_PASSAGE_FAIL",
                    "THRESHOLD_LEVEL2_WEIGHTED_BRANCH_FAIL",
                    "THRESHOLD_LEVEL3_TO_LEVEL2_PASSAGE_FAIL",
                    "THRESHOLD_LEVEL3_TO_LEVEL4_PASSAGE_FAIL",
                    "THRESHOLD_LEVEL4_TO_LEVEL3_PASSAGE_FAIL",
                    "THRESHOLD_LEVEL0_MUSIC_REGISTRY_FAIL",
                    "THRESHOLD_LEVEL1_MUSIC_REGISTRY_FAIL",
                    "THRESHOLD_LEVEL1_HORROR_IDENTITY_FAIL",
                    "THRESHOLD_LEVEL1_FRESHWATER_REFILL_FAIL",
                    "THRESHOLD_LEVEL1_AGRICULTURE_CROP_DIVERSITY_FAIL",
                    "THRESHOLD_LEVEL1_COMPOSTER_FAIL","THRESHOLD_LEVEL1_ENTITY_VARIETY_FAIL",
                    "THRESHOLD_LEVEL0_LEVEL1_IRREGULAR_MAZE_FAIL",
                    "THRESHOLD_LEVEL0_NESTED_POCKETS_FAIL",
                    "THRESHOLD_LEVEL1_NESTED_POCKETS_FAIL",
                    "THRESHOLD_LEVEL0_PHYSICAL_MAZE_CLEARANCE_FAIL",
                    "THRESHOLD_LEVEL1_PHYSICAL_MAZE_CLEARANCE_FAIL")):
                    print("FAIL: a required Threshold worldgen or runtime invariant failed", flush=True)
                    print(current[-90000:], flush=True)
                    return 1
                if any(fatal in current for fatal in (
                    "Exception generating new chunk",
                    "Exception ticking world",
                    "Exception in server tick loop",
                    "Report this crash to",
                    "Crash report saved to:")):
                    print("FAIL: Threshold server reported a fatal runtime problem", flush=True)
                    print(current[-90000:], flush=True)
                    return 1
                if process.poll() is not None:
                    print("FAIL: Threshold server exited early",flush=True)
                    break
            else:
                print("FAIL: Threshold startup timed out",flush=True)
            print(LOG.read_text(encoding="utf-8",errors="replace")[-90000:],flush=True)
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
