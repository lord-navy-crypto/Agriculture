#!/usr/bin/env python3
"""Validate connected city NBT, vanilla anomaly pools and safe worldgen wiring."""
from __future__ import annotations

import gzip
import json
import struct
import sys
import tempfile
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PACK_ROOT = "resourcepacks/threshold_anomalies/"
PACK_DIR = ROOT / "build/generated/resources/thresholdAnomalyPack" / PACK_ROOT
TEMPLATE_DIR = ROOT / "build/generated/resources/thresholdArchitecture/data/threshold/structure/anomaly/urban"
URBAN = ("commercial_skyscrapers", "civic_centre", "residential_quarter", "industrial_edge")


def read_string(data: bytes, pos: int):
    size = struct.unpack_from(">H", data, pos)[0]
    pos += 2
    return data[pos:pos + size].decode("utf-8"), pos + size


def read_payload(data: bytes, pos: int, tag_type: int):
    if tag_type == 1:
        return struct.unpack_from(">b", data, pos)[0], pos + 1
    if tag_type == 2:
        return struct.unpack_from(">h", data, pos)[0], pos + 2
    if tag_type == 3:
        return struct.unpack_from(">i", data, pos)[0], pos + 4
    if tag_type == 4:
        return struct.unpack_from(">q", data, pos)[0], pos + 8
    if tag_type == 5:
        return struct.unpack_from(">f", data, pos)[0], pos + 4
    if tag_type == 6:
        return struct.unpack_from(">d", data, pos)[0], pos + 8
    if tag_type == 7:
        length = struct.unpack_from(">i", data, pos)[0]
        pos += 4
        return data[pos:pos + length], pos + length
    if tag_type == 8:
        return read_string(data, pos)
    if tag_type == 9:
        subtype = data[pos]
        length = struct.unpack_from(">i", data, pos + 1)[0]
        pos += 5
        values = []
        for _ in range(length):
            value, pos = read_payload(data, pos, subtype)
            values.append(value)
        return values, pos
    if tag_type == 10:
        values = {}
        while data[pos] != 0:
            child_type = data[pos]
            pos += 1
            name, pos = read_string(data, pos)
            value, pos = read_payload(data, pos, child_type)
            values[name] = value
        return values, pos + 1
    if tag_type == 11:
        length = struct.unpack_from(">i", data, pos)[0]
        pos += 4
        values = list(struct.unpack_from(">" + "i" * length, data, pos)) if length else []
        return values, pos + length * 4
    if tag_type == 12:
        length = struct.unpack_from(">i", data, pos)[0]
        pos += 4
        values = list(struct.unpack_from(">" + "q" * length, data, pos)) if length else []
        return values, pos + length * 8
    raise ValueError(f"Unsupported NBT tag type {tag_type}")


def parse_structure_nbt(blob: bytes):
    raw = gzip.decompress(blob)
    if raw[0] != 10:
        raise AssertionError("Structure NBT root is not TAG_Compound")
    _, pos = read_string(raw, 1)
    result, _ = read_payload(raw, pos, 10)
    return result


def validate_template(blob: bytes, name: str):
    doc = parse_structure_nbt(blob)
    width, height, length = doc["size"]
    assert (width, length) == (48, 48), (name, doc["size"])
    palette = doc["palette"]
    blocks = doc["blocks"]
    states = [entry["Name"] for entry in palette]
    props = [entry.get("Properties", {}) for entry in palette]
    air = []
    by_pos = {}
    ladder_count = 0
    iron_door_count = 0
    for block in blocks:
        x, y, z = block["pos"]
        state_index = block["state"]
        state = states[state_index]
        properties = props[state_index]
        by_pos[(x, y, z)] = state
        if state == "minecraft:air":
            air.append((x, y, z))
        if state == "minecraft:ladder":
            ladder_count += 1
            assert properties.get("facing") == "east", (name, block, properties)
        if state == "minecraft:iron_door":
            iron_door_count += 1
        if y < 19 and state == "minecraft:air":
            allowed = (
                (21 <= x <= 27 and 21 <= z <= 27 and 1 <= y <= 10)
                or (22 <= x <= 25 and 22 <= z <= 25 and 11 <= y <= 18)
                or (23 <= x <= 25 and (0 <= z <= 19 or 29 <= z <= 47) and 1 <= y <= 3)
            )
            assert allowed, (name, "unexpected lower-level air carve", (x, y, z))
        assert not (y >= 19 and state == "minecraft:air"), (
            name, "upper city air must be omitted from sparse NBT", (x, y, z))

    assert ladder_count == 18, (name, ladder_count)
    assert iron_door_count == 4, (name, iron_door_count)
    assert by_pos[(24, 0, 24)] == "minecraft:polished_andesite"
    assert by_pos[(24, 1, 20)] == "minecraft:iron_door"
    assert by_pos[(24, 2, 28)] == "minecraft:iron_door"
    assert by_pos[(22, 1, 24)] == "minecraft:ladder"
    assert by_pos[(23, 19, 24)] == "minecraft:polished_andesite"
    assert by_pos[(24, 19, 22)] == "minecraft:polished_andesite"
    assert by_pos[(24, 19, 21)] != "minecraft:air", "city exit landing is missing"
    assert by_pos.get((24, 17, 21), "minecraft:air") != "minecraft:deepslate_bricks"
    assert by_pos.get((24, 18, 21), "minecraft:air") != "minecraft:deepslate_bricks"
    assert any(state == "minecraft:deepslate_tiles" for state in states)
    assert len(air) < 2500, (name, "sparse air carve unexpectedly large", len(air))
    assert len(blocks) < width * height * length // 3, (
        name, "city template is not sparse", len(blocks), width * height * length)
    return doc


def load_json_from_dir(root: Path, relative: str):
    return json.loads((root / relative).read_text(encoding="utf-8"))


def validate_pack(root: Path):
    meta = load_json_from_dir(root, "pack.mcmeta")
    assert meta["pack"]["pack_format"] == 48

    expected_structures = {
        "anomaly_architecture", "anomaly_urban", "anomaly_village_fragment",
        "anomaly_ancient_city_fragment", "anomaly_ruined_portal", "anomaly_shipwreck",
    }
    for name in expected_structures:
        doc = load_json_from_dir(root, f"data/threshold/worldgen/structure/{name}.json")
        assert doc["type"] == "minecraft:jigsaw"
        assert doc["biomes"] == "#threshold:anomaly_eligible"
        assert doc["step"] == "top_layer_modification"
        assert doc["terrain_adaptation"] == "none"
    assert load_json_from_dir(root, "data/threshold/worldgen/structure/anomaly_urban.json")["start_height"] == {"absolute": 63}
    for name in expected_structures - {"anomaly_urban"}:
        assert load_json_from_dir(root, f"data/threshold/worldgen/structure/{name}.json")["start_height"] == {"absolute": 82}

    village = load_json_from_dir(root, "data/threshold/worldgen/structure/anomaly_village_fragment.json")
    assert village["start_pool"] == "minecraft:village/plains/town_centers" and village["size"] == 0
    ancient = load_json_from_dir(root, "data/threshold/worldgen/structure/anomaly_ancient_city_fragment.json")
    assert ancient["start_pool"] == "minecraft:ancient_city/city_center"
    assert ancient["start_jigsaw_name"] == "minecraft:city_anchor" and ancient["size"] == 0

    portals = load_json_from_dir(root, "data/threshold/worldgen/template_pool/anomaly/vanilla_portals.json")
    portal_ids = {e["element"]["location"] for e in portals["elements"]}
    assert len(portal_ids) == 13 and "minecraft:ruined_portal/giant_portal_1" in portal_ids
    ships = load_json_from_dir(root, "data/threshold/worldgen/template_pool/anomaly/vanilla_shipwrecks.json")
    ship_ids = {e["element"]["location"] for e in ships["elements"]}
    assert len(ship_ids) == 11 and "minecraft:shipwreck/with_mast" in ship_ids

    sets = [p.name for p in (root / "data/threshold/worldgen/structure_set").glob("*.json")]
    assert sets == ["anomaly_structures.json"], sets
    placement = load_json_from_dir(root, "data/threshold/worldgen/structure_set/anomaly_structures.json")
    assert placement["placement"]["spacing"] == 64
    assert placement["placement"]["separation"] == 24
    weights = {x["structure"]: x["weight"] for x in placement["structures"]}
    assert weights == {
        "threshold:anomaly_architecture": 4,
        "threshold:anomaly_urban": 2,
        "threshold:anomaly_village_fragment": 1,
        "threshold:anomaly_ancient_city_fragment": 1,
        "threshold:anomaly_ruined_portal": 1,
        "threshold:anomaly_shipwreck": 1,
    }
    for level in ("level_0", "level_1"):
        dimension = load_json_from_dir(root, f"data/threshold/dimension/{level}.json")
        assert dimension["generator"]["settings"]["structure_overrides"] == [
            "threshold:anomaly_structures"]
    tag = load_json_from_dir(root, "data/threshold/tags/worldgen/biome/anomaly_eligible.json")
    assert tag["values"] == ["threshold:level_0", "threshold:level_1"]


def main():
    for name in URBAN:
        path = TEMPLATE_DIR / f"{name}.nbt"
        doc = validate_template(path.read_bytes(), name)
        print(f"BACKROOM_CONNECTED_CITY_OK: {name} size={doc['size']} sparse_blocks={len(doc['blocks'])}")
    validate_pack(PACK_DIR)

    if len(sys.argv) > 1:
        jar = Path(sys.argv[1])
        with zipfile.ZipFile(jar) as archive:
            names = set(archive.namelist())
            for name in URBAN:
                path = f"data/threshold/structure/anomaly/urban/{name}.nbt"
                assert path in names, path
                validate_template(archive.read(path), name)
            pack_root = "resourcepacks/threshold_anomalies/"
            for path in (
                "pack.mcmeta",
                "data/threshold/worldgen/structure_set/anomaly_structures.json",
                "data/threshold/worldgen/structure/anomaly_urban.json",
                "data/threshold/worldgen/structure/anomaly_village_fragment.json",
                "data/threshold/worldgen/structure/anomaly_ancient_city_fragment.json",
                "data/threshold/worldgen/structure/anomaly_ruined_portal.json",
                "data/threshold/worldgen/structure/anomaly_shipwreck.json",
            ):
                assert pack_root + path in names, pack_root + path
            with tempfile.TemporaryDirectory() as temp_dir:
                extracted_root = Path(temp_dir)
                for path in names:
                    if path.startswith(pack_root):
                        relative = path[len(pack_root):]
                        target = extracted_root / relative
                        target.parent.mkdir(parents=True, exist_ok=True)
                        target.write_bytes(archive.read(path))
                # Re-validate every exact JSON resource as shipped in the JAR.
                validate_pack(extracted_root)
            print("BACKROOM_V45_JAR_ANOMALY_RESOURCES_OK")
    print("BACKROOM_V45_ANOMALY_WORLDGEN_OK: connected cities, safe sparse carve, vanilla structure fragments")


if __name__ == "__main__":
    main()
