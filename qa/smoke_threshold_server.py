#!/usr/bin/env python3
"""Boot the unified Backroom Agriculture mod and verify all six dimensions."""
from pathlib import Path
import os, signal, shutil, socket, struct, subprocess, sys, time

ROOT=Path(__file__).resolve().parents[1]
PROJECT=ROOT
LOG=ROOT/"build/threshold-server-boot.log"
LIMIT=int(os.environ.get("THRESHOLD_SERVER_BOOT_TIMEOUT","210"))
READY="Done ("
MARKERS=("THRESHOLD_HORROR_RUNTIME_INIT_OK","THRESHOLD_DIMENSIONS_REGISTRY_READY_OK",
         "THRESHOLD_ENTITY_REGISTRY_OK","THRESHOLD_LEVEL1_HOUND_REGISTRY_OK",
         "THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_OK",
         "THRESHOLD_LEVEL0_WATCHER_MODES_READY_OK",
         "THRESHOLD_PARTYGOER_RESPAWN_HANDLER_READY_OK",
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
         "THRESHOLD_ANOMALY_WORLDGEN_REGISTRY_OK",
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

def _recv_exact(sock, length):
    chunks = []
    remaining = length
    while remaining:
        chunk = sock.recv(remaining)
        if not chunk:
            raise ConnectionError("RCON socket closed")
        chunks.append(chunk)
        remaining -= len(chunk)
    return b"".join(chunks)


def _read_rcon_packet(sock):
    length = struct.unpack("<i", _recv_exact(sock, 4))[0]
    packet = _recv_exact(sock, length)
    packet_id, packet_type = struct.unpack("<ii", packet[:8])
    payload = packet[8:-2].decode("utf-8", errors="replace")
    return packet_id, packet_type, payload


def _send_rcon_packet(sock, packet_id, packet_type, payload):
    body = struct.pack("<ii", packet_id, packet_type) + payload.encode("utf-8") + b"\x00\x00"
    sock.sendall(struct.pack("<i", len(body)) + body)


def _rcon(command_list):
    deadline = time.monotonic() + 45
    last_error = None
    sock = None
    while time.monotonic() < deadline:
        try:
            sock = socket.create_connection(("127.0.0.1", 25575), timeout=3)
            break
        except OSError as error:
            last_error = error
            time.sleep(0.5)
    if sock is None:
        raise RuntimeError(f"RCON did not become available: {last_error}")
    results = []
    with sock:
        sock.settimeout(15)
        _send_rcon_packet(sock, 1, 3, "threshold-anomaly-smoke")
        auth_id, _, auth_text = _read_rcon_packet(sock)
        if auth_id != 1:
            raise RuntimeError(f"RCON authentication failed: id={auth_id}, {auth_text}")
        for packet_id, command in enumerate(command_list, start=2):
            _send_rcon_packet(sock, packet_id, 2, command)
            response_id, _, response = _read_rcon_packet(sock)
            if response_id != packet_id:
                raise RuntimeError(
                    f"Unexpected RCON response for {command!r}: id={response_id}, {response!r}")
            results.append(response)
    return results


def verify_anomaly_city_lift():
    commands = [
        "execute in threshold:level_0 run place structure threshold:anomaly_urban 8 63 8",
        "execute in threshold:level_0 run data get block 32 63 32",
        "execute in threshold:level_0 run data get block 30 64 32",
        "execute in threshold:level_0 run data get block 30 82 32",
        "execute in threshold:level_0 run data get block 31 82 32",
        "execute in threshold:level_0 run data get block 32 74 32",
        "execute in threshold:level_0 run data get block 32 82 29",
        "execute in threshold:level_0 run data get block 32 64 8",
        "execute in threshold:level_0 run data get block 32 64 55",
    ]
    results = _rcon(commands)
    expected = (
        (1, "minecraft:polished_andesite"),
        (2, "minecraft:ladder"),
        (3, "minecraft:ladder"),
        (4, "minecraft:polished_andesite"),
        (5, "minecraft:air"),
        (6, "minecraft:gray_concrete"),
        (7, "minecraft:air"),
        (8, "minecraft:air"),
    )
    placement = results[0].lower()
    if "failed" in placement or "could not" in placement or "no structure" in placement:
        raise AssertionError(f"Could not place connected anomaly city: {results[0]}")
    for index, token in expected:
        response = results[index].lower()
        if token not in response:
            raise AssertionError(
                f"Anomaly city lift probe {commands[index]!r} expected {token}, got {results[index]!r}")
    print("THRESHOLD_ANOMALY_CITY_LIFT_RCON_OK", flush=True)


def run():
    # Build and launch one integrated mod; libraries are runtime dependencies.
    subprocess.run(["gradle","--no-daemon","--console=plain","build"],
                   cwd=ROOT,check=True)
    server_dir=ROOT/"run"  # ModDevGradle default game directory for the root :runServer task.
    server_dir.mkdir(parents=True,exist_ok=True)

    # Use a fresh smoke world with the optional anomaly pack explicitly enabled.
    # This verifies dynamic registry loading without touching any user world.
    world_name = "threshold-anomaly-smoke"
    suffix = 1
    while (server_dir / world_name / "level.dat").exists():
        world_name = f"threshold-anomaly-smoke-{suffix}"
        suffix += 1
    world_dir = server_dir / world_name
    pack_source = ROOT / "build/generated/resources/thresholdAnomalyPack/resourcepacks/threshold_anomalies"
    if not (pack_source / "pack.mcmeta").is_file():
        raise FileNotFoundError(f"Missing generated anomaly pack: {pack_source}")
    pack_target = world_dir / "datapacks/threshold_anomalies"
    pack_target.parent.mkdir(parents=True, exist_ok=True)
    shutil.copytree(pack_source, pack_target, dirs_exist_ok=True)

    properties_path = server_dir / "server.properties"
    properties = {}
    if properties_path.is_file():
        for line in properties_path.read_text(encoding="utf-8", errors="replace").splitlines():
            if line and not line.lstrip().startswith("#") and "=" in line:
                key, value = line.split("=", 1)
                properties[key.strip()] = value.strip()
    properties["level-name"] = world_name
    properties["level-seed"] = "6"
    properties["initial-enabled-packs"] = "vanilla,modded,file/threshold_anomalies"
    properties["initial-disabled-packs"] = ""
    properties["enable-rcon"] = "true"
    properties["rcon.password"] = "threshold-anomaly-smoke"
    properties["rcon.port"] = "25575"
    properties["broadcast-rcon-to-ops"] = "false"
    properties_path.write_text(
        "\n".join(f"{key}={value}" for key, value in sorted(properties.items())) + "\n",
        encoding="utf-8")
    (server_dir/"eula.txt").write_text("eula=true\n",encoding="utf-8")
    LOG.parent.mkdir(parents=True,exist_ok=True)
    with LOG.open("wb") as stream:
        process=subprocess.Popen(
            ["gradle","--no-daemon","--console=plain",":runServer"],
            cwd=ROOT,stdout=stream,stderr=subprocess.STDOUT,start_new_session=True)
        try:
            deadline=time.monotonic()+LIMIT
            while time.monotonic()<deadline:
                time.sleep(2)
                current=LOG.read_text(encoding="utf-8",errors="replace")
                if all(m in current for m in MARKERS) and READY in current:
                    try:
                        verify_anomaly_city_lift()
                    except Exception as error:
                        print(f"FAIL: anomaly city lift RCON acceptance: {error}", flush=True)
                        print(current[-90000:], flush=True)
                        return 1
                    print("THRESHOLD_REAL_SERVER_SIX_DIMENSIONS_AND_WORLDGEN_PASS",flush=True)
                    return 0
                if any(marker in current for marker in (
                    "THRESHOLD_ENTITY_REGISTRY_FAIL","THRESHOLD_LEVEL1_HOUND_REGISTRY_FAIL",
                    "THRESHOLD_LEVEL0_WATCHER_ADVANCED_AI_READY_FAIL",
                    "THRESHOLD_LEVEL0_WATCHER_MODES_READY_FAIL",
                    "THRESHOLD_PARTYGOER_RESPAWN_HANDLER_READY_FAIL",
                    "THRESHOLD_LEVEL1_HOUND_ADVANCED_AI_READY_FAIL",
                    "THRESHOLD_ENTITY_COMPATIBILITY_ALL_DIMENSIONS_FAIL",
                    "THRESHOLD_NATURAL_SPAWN_POLICY_FAIL","THRESHOLD_LEVEL1_ENTITY_VARIETY_FAIL",
                    "THRESHOLD_LEVEL0_LEVEL1_IRREGULAR_MAZE_FAIL",
                    "THRESHOLD_LEVEL0_NESTED_POCKETS_FAIL",
                    "THRESHOLD_LEVEL1_NESTED_POCKETS_FAIL",
                    "THRESHOLD_LEVEL0_PHYSICAL_MAZE_CLEARANCE_FAIL",
                    "THRESHOLD_LEVEL1_PHYSICAL_MAZE_CLEARANCE_FAIL",
                    "THRESHOLD_ANOMALY_WORLDGEN_OPTIONAL_PACK_OFF",
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
