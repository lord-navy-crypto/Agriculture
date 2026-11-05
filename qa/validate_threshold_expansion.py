#!/usr/bin/env python3
"""Validate Threshold's six-level, source-backed transit network and original audio."""
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "threshold/src/main/java/io/github/lordnavycrypto/threshold"
RES = ROOT / "threshold/src/main/resources"


def read_json(path: Path) -> dict:
    return json.loads(path.read_text(encoding="utf-8"))


def require(source: str, token: str, label: str) -> None:
    if token not in source:
        raise AssertionError(f"Threshold expansion contract missing: {label}")


def main() -> int:
    dimensions = (JAVA / "dimension/ThresholdDimensions.java").read_text(encoding="utf-8")
    layout = (JAVA / "worldgen/ThresholdRoomLayoutFeature.java").read_text(encoding="utf-8")
    passage = (JAVA / "block/ThresholdPassageBlock.java").read_text(encoding="utf-8")
    seam = (JAVA / "block/ThresholdTransitionZoneBlock.java").read_text(encoding="utf-8")
    hotel_elevator = (JAVA / "block/ThresholdHotelElevatorBlock.java").read_text(encoding="utf-8")
    route_block = (JAVA / "block/ThresholdRouteBlock.java").read_text(encoding="utf-8")
    blocks = (JAVA / "block/ThresholdEnvironmentBlocks.java").read_text(encoding="utf-8")
    sounds_java = (JAVA / "ThresholdSounds.java").read_text(encoding="utf-8")
    mod = (JAVA / "ThresholdMod.java").read_text(encoding="utf-8")
    client_events = (JAVA / "client/ThresholdClientEvents.java").read_text(encoding="utf-8")
    client_renderers = (JAVA / "client/ThresholdClientRenderers.java").read_text(encoding="utf-8")
    watcher = (JAVA / "entity/ThresholdWatcherEntity.java").read_text(encoding="utf-8")
    partygoer = (JAVA / "entity/ThresholdPartygoerEntity.java").read_text(encoding="utf-8")
    director = (JAVA / "entity/ThresholdEntityDirector.java").read_text(encoding="utf-8")
    runtime_checks = (JAVA / "worldgen/ThresholdRuntimeChecks.java").read_text(encoding="utf-8")
    items = (JAVA / "item/ThresholdItems.java").read_text(encoding="utf-8")
    flashlight = (JAVA / "item/FlashlightItem.java").read_text(encoding="utf-8")
    almond_water = (JAVA / "item/AlmondWaterItem.java").read_text(encoding="utf-8")
    commands = (JAVA / "command/ThresholdCommands.java").read_text(encoding="utf-8")

    levels = ("level_0", "level_1", "level_2", "level_3", "level_4", "level_5")
    for level in levels:
        dim = read_json(RES / f"data/threshold/dimension/{level}.json")
        dtype = read_json(RES / f"data/threshold/dimension_type/{level}_type.json")
        biome = read_json(RES / f"data/threshold/worldgen/biome/{level}.json")
        assert dim["type"] == f"threshold:{level}_type"
        assert dim["generator"]["type"] == "minecraft:flat"
        assert dim["generator"]["settings"]["biome"] == f"threshold:{level}"
        assert dim["generator"]["settings"]["features"] is True
        assert dtype["has_skylight"] is False
        assert biome["spawners"] == {}
        assert biome["spawn_costs"] == {}
        assert biome["carvers"] == {}

    for token, label in (
        ("LEVEL_2", "Level 2 dimension key"),
        ("LEVEL_3", "Level 3 dimension key"),
        ("LEVEL_4", "Level 4 dimension key"),
        ("LEVEL_5", "Level 5 dimension key"),
    ):
        require(dimensions, token, label)
    for token, label in (
        ("buildLevel2Chunk(world, chunkX, chunkZ)", "Level 2 world generation dispatch"),
        ("buildLevel3Chunk(world, chunkX, chunkZ)", "Level 3 world generation dispatch"),
        ("buildLevel4Chunk(world, chunkX, chunkZ)", "Level 4 world generation dispatch"),
        ("buildLevel5Chunk(world, chunkX, chunkZ)", "Level 5 world generation dispatch"),
        ("level2Architecture", "deterministic Level 2 room archetypes"),
        ("level3Architecture", "deterministic Level 3 room archetypes"),
        ("level4Architecture", "deterministic Level 4 room archetypes"),
        ("level5Architecture", "deterministic Level 5 hotel room archetypes"),
        ("LEVEL_2_ROOM_SIZE = 32", "Level 2 room grid"),
        ("LEVEL_5_ROOM_SIZE = 32", "Level 5 room grid"),
        ("Beverly Room", "Level 5 ballroom identity"),
        ("Boiler Room", "Level 5 boiler-room identity"),
        ("Blocks.COPPER_BLOCK", "industrial pipework"),
        ("LEVEL_2_BRANCH_DOOR", "Level 2 branching route placement"),
    ):
        require(layout, token, label)

    # Level 0/1 are intentionally multi-scale and vertically connected:
    # narrow maze partitions, upper galleries, stairs, and accessible sub-floor rooms.
    for token, label in (
        ("case 11 ->", "Level 0 micro-maze archetype"),
        ("case 12 ->", "Level 0 accessible sunken annex archetype"),
        ("case 15 ->", "Level 1 warehouse micro-maze archetype"),
        ("case 16 ->", "Level 1 accessible maintenance trench archetype"),
        ("buildLevel0VerticalNetwork", "Level 0 upper gallery network"),
        ("buildLevel1VerticalNetwork", "Level 1 upper catwalk network"),
        ("hasLevel0UpperNeighbour", "Level 0 cross-room upper-deck connectivity"),
        ("hasLevel1UpperNeighbour", "Level 1 cross-room upper-deck connectivity"),
        ("StairBlock.FACING", "walkable vertical stair connections"),
        ("ThresholdEnvironmentBlocks.CEILING_VENT", "original ceiling-vent block usage"),
    ):
        require(layout, token, label)
    require(layout, "roomX == 1 && roomZ == 0) return 12", "guaranteed Level 0 sub-floor landmark")
    require(layout, "roomX == 1 && roomZ == 1) return 16", "guaranteed Level 1 maintenance landmark")

    route_tokens = (
        "LEVEL_1_TO_2", "LEVEL_2_FIRE_EXIT", "LEVEL_2_TO_3",
        "LEVEL_2_BRANCH_DOOR", "LEVEL_3_TO_2", "LEVEL_3_TO_4",
        "LEVEL_4_TO_3", "LEVEL_3_ELEVATOR", "LEVEL_4_TO_5",
        "LEVEL_5_TO_3", "LEVEL_5_TO_4", "LEVEL_5_ELEVATOR_PANEL",
    )
    for token in route_tokens:
        require(blocks, token, f"registered route {token}")
    require(passage, "teleportTo(destination", "fixed passage performs dimension travel")
    require(seam, "entityInside", "Level 1 to Level 2 uses a seamless trigger zone")
    require(seam, "ThresholdDimensions.LEVEL_2", "seam targets Level 2")
    require(hotel_elevator, 'IntegerProperty.create("floor", 1, 12)', "hotel elevator exposes floors 1 through 12")
    require(hotel_elevator, "ThresholdDimensions.LEVEL_5", "hotel elevator stays within Level 5")
    require(route_block, "chooseRoute", "weighted route selection")
    require(route_block, "source.getSeed()", "route stable for a world seed")
    require(route_block, "pos.asLong()", "each doorway has its own route draw")
    require(route_block, "Weights are implementation tuning", "weights are not presented as canon probabilities")
    require(commands, 'literal("level_2")', "operator travel command for Level 2")
    require(commands, 'literal("level_3")', "operator travel command for Level 3")
    require(commands, 'literal("level_4")', "operator travel command for Level 4")
    require(commands, 'literal("level_5")', "operator travel command for Level 5")
    assert "LEVEL_1_TO_3" not in blocks, "remove non-canonical direct Level 1 -> Level 3 route"
    assert "LEVEL_3_TO_1" not in blocks, "Level 3 return should follow the Level 2 service route"

    passage_models = {
        "level_1_to_2": "threshold:block/transition_zone",
        "level_2_fire_exit": "minecraft:block/iron_door_bottom",
        "level_2_to_3": "minecraft:block/copper_block",
        "level_2_branch_door": "minecraft:block/iron_trapdoor",
        "level_3_to_2": "minecraft:block/deepslate_bricks",
        "level_3_to_4": "minecraft:block/white_concrete",
        "level_4_to_3": "minecraft:block/light_gray_concrete",
        "level_3_elevator": "minecraft:block/iron_door_bottom",
        "level_4_to_5": "minecraft:block/spruce_door_bottom",
        "level_5_to_3": "minecraft:block/iron_door_bottom",
        "level_5_to_4": "minecraft:block/spruce_door_bottom",
    }
    for passage_name, model in passage_models.items():
        blockstate = read_json(RES / f"assets/threshold/blockstates/{passage_name}.json")
        assert blockstate["variants"][""]["model"] == model

    elevator_states = read_json(RES / "assets/threshold/blockstates/level_5_elevator_panel.json")["variants"]
    assert len(elevator_states) == 12
    assert all(state["model"] == "minecraft:block/iron_block" for state in elevator_states.values())
    hotel_biome = read_json(RES / "data/threshold/worldgen/biome/level_5.json")
    assert hotel_biome["effects"]["mood_sound"]["sound"] == "minecraft:block.note_block.harp"

    catalog = read_json(RES / "data/threshold/transit/route_catalog.json")
    implemented = {route["id"]: route for route in catalog["implemented_routes"]}
    expected_routes = {
        "level_0_flickering_wall", "level_1_smooth_threshold",
        "level_2_fire_exit", "level_2_anomalous_branch",
        "level_2_unlocked_door", "level_3_retrace_corridor",
        "level_3_office_sector", "level_4_service_elevator_return",
        "level_3_elevator_to_level5", "level_4_stairway_to_level5",
        "level_5_elevator_return_to_level3", "level_5_stairway_return_to_level4",
        "level_5_internal_elevator_floors",
    }
    assert expected_routes <= set(implemented)
    assert implemented["level_2_fire_exit"]["profile"] == "fandom"
    assert implemented["level_2_fire_exit"]["relative_weights"] == [1, 1]
    assert implemented["level_2_anomalous_branch"]["profile"] == "wikidot"
    assert implemented["level_2_anomalous_branch"]["relative_weights"] == [4, 4, 1]
    assert all(route["status"] == "implemented" for route in implemented.values())
    deferred = {route["id"]: route for route in catalog["deferred_routes"]}
    assert implemented["level_3_elevator_to_level5"]["relative_weights"] == [9, 1]
    assert implemented["level_4_stairway_to_level5"]["to"] == ["level_5"]
    assert implemented["level_5_internal_elevator_floors"]["to"] == ["level_5"]
    assert "level_6" in deferred["level_5_boiler_deep_exit"]["to"]
    assert "level_9" in deferred["level_5_dark_oak_door"]["to"]
    assert "phantom" not in json.dumps(catalog).lower()

    modifier = read_json(RES / "data/threshold/neoforge/biome_modifier/room_layout.json")
    assert sorted(modifier["biomes"]) == [f"threshold:{level}" for level in levels]
    assert modifier["features"] == "threshold:room_sequence"

    sound_definitions = read_json(RES / "assets/threshold/sounds.json")
    for level in ("level_0", "level_1"):
        event = f"{level}_ambient"
        assert event in sound_definitions
        music = read_json(RES / f"data/threshold/worldgen/biome/{level}.json")["effects"]["music"]
        assert music["sound"] == f"threshold:{event}"
        assert music["replace_current_music"] is True
        assert music["min_delay"] == 1 and music["max_delay"] == 5
        raw = (RES / f"assets/threshold/sounds/{event}.ogg").read_bytes()
        assert raw[:4] == b"OggS" and len(raw) > 4000
    require(sounds_java, "LEVEL_0_AMBIENT", "Level 0 sound registry entry")
    require(sounds_java, "LEVEL_1_AMBIENT", "Level 1 sound registry entry")
    require(mod, "ThresholdSounds.SOUNDS.register(modBus)", "sound registry wiring")
    require(client_renderers, "EventBusSubscriber.Bus.MOD", "client-only mod-bus renderer subscriber")
    require(client_renderers, "value = Dist.CLIENT", "dedicated-server-safe client renderer gating")
    require(client_renderers, "EntityRenderersEvent.RegisterRenderers", "entity renderer registration event")
    require(client_renderers, "ThresholdEntities.HOUND.get(), ThresholdHoundRenderer::new", "Hound renderer registration")
    require(client_renderers, "ThresholdEntities.WATCHER.get(), ThresholdWatcherRenderer::new", "Watcher renderer registration")
    require(client_renderers, "ThresholdEntities.PARTYGOER.get(), ThresholdPartygoerRenderer::new", "Partygoer renderer registration")
    assert "@EventBusSubscriber" not in client_events, "duplicate renderer subscriber must remain disabled"
    # Entity admission is no longer blanket-filtered. Only the separately
    # scoped projectile guard remains; ordinary mobs, utility entities, and
    # custom operator summons must not be rejected by dimension.
    require(director, "onEntityJoin(EntityJoinLevelEvent event)", "narrow entity-join policy")
    join_handler = director.split(
        "public static void onEntityJoin(EntityJoinLevelEvent event)", 1
    )[1].split("@SubscribeEvent", 1)[0]
    require(join_handler, "instanceof Projectile", "separate projectile-only admission rule")
    assert "instanceof LivingEntity" not in join_handler, "ordinary living entities must be admitted"
    assert "instanceof ItemEntity" not in join_handler, "dropped items must be admitted"
    require(director, "isThresholdDimension(level.dimension())", "entity rule is scoped to Threshold dimensions")
    require(director, "isNaturalSpawnAllowed", "explicit natural-spawn policy")
    require(director, "shouldBlockNaturalSpawn", "natural spawn reason filter")
    require(director, "onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event)", "filter runs before natural mob placement")
    require(director, "MobSpawnEvent.SpawnPlacementCheck.Result.FAIL", "unapproved natural spawns are denied")
    require(director, "return entityType == ThresholdEntities.WATCHER.get();", "Level 0 natural whitelist is Watcher only")
    require(director, "return entityType == ThresholdEntities.HOUND.get();", "Level 1 natural whitelist is Hound only")
    require(director, "return entityType == ThresholdEntities.PARTYGOER.get();", "Partygoer retains its Overworld encounter")
    require(director, "isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.WATCHER.get())", "Watcher natural spawn policy is checked")
    require(director, "isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.HOUND.get())", "Hound natural spawn policy is checked")
    require(director, "isNaturalSpawnAllowed(level.dimension(), ThresholdEntities.PARTYGOER.get())", "Partygoer natural spawn policy is checked")
    for token, label in (
        ("EntityType.COW", "passive mob admission probe"),
        ("EntityType.ZOMBIE", "hostile mob admission probe"),
        ("EntityType.ITEM", "dropped item admission probe"),
        ("EntityType.MINECART", "minecart admission probe"),
        ("EntityType.ITEM_FRAME", "item frame admission probe"),
        ("EntityType.ARMOR_STAND", "armor stand admission probe"),
        ("ThresholdEntities.WATCHER.get()", "Watcher operator-summon admission probe"),
        ("ThresholdEntities.HOUND.get()", "Hound operator-summon admission probe"),
        ("ThresholdEntities.PARTYGOER.get()", "Partygoer operator-summon admission probe"),
        ("THRESHOLD_ENTITY_COMPATIBILITY_ALL_DIMENSIONS_OK", "all entity classes join all six dimensions"),
        ("THRESHOLD_NATURAL_SPAWN_POLICY_OK", "six-dimension natural spawn whitelist check"),
        ("MobSpawnType.NATURAL", "natural spawn reason filtering"),
        ("MobSpawnType.CHUNK_GENERATION", "chunk-generation spawn reason filtering"),
        ("MobSpawnType.SPAWN_EGG", "spawn egg summons remain permitted"),
        ("MobSpawnType.COMMAND", "command summons remain permitted"),
        ("MobSpawnType.BREEDING", "player-driven breeding remains permitted"),
        ("THRESHOLD_NATURAL_SPAWN_REASON_POLICY_OK", "natural spawn reasons are isolated from summons"),
        ("THRESHOLD_LEVEL0_VERTICAL_LAYOUT_OK", "Level 0 stair, gallery, and sunken annex runtime probe"),
        ("THRESHOLD_LEVEL1_VERTICAL_LAYOUT_OK", "Level 1 stair, catwalk, and maintenance trench runtime probe"),
    ):
        require(runtime_checks, token, label)

    for summon_name in ("summonWatcher", "summonHound", "summonPartygoer"):
        start = commands.index(f"private static int {summon_name}")
        end = commands.find("\n    private static int ", start + 1)
        body = commands[start:end if end >= 0 else len(commands)]
        assert "dimension().equals(" not in body, f"{summon_name} must not restrict the current dimension"
        require(body, "level.addFreshEntity(", f"{summon_name} must add the summoned entity to the world")
    require(commands, "findShowcasePosition(level, player", "Hound and Partygoer use safe dimension-agnostic summon positions")

    require(director, "spawnPartygoers(level)", "rare Overworld Partygoer encounter")
    require(director, "gameTime % 600L", "Partygoer encounter cadence is more frequent")
    require(director, "level.isDay()", "Partygoer night-only encounter")
    require(partygoer, "extends Monster", "Partygoer is a hostile mob")
    require(partygoer, "new MeleeAttackGoal(this, 1.28D, true)", "Partygoer uses fast persistent melee pursuit")
    require(partygoer, "Attributes.ATTACK_DAMAGE", "Partygoer has real attack damage")
    require(partygoer, "new StalkPlayerGoal(this)", "Partygoer stalks before committing to a hunt")
    require(partygoer, "isBeingWatchedBy(candidate)", "Direct eye contact escalates the encounter")
    assert "CAPTURE_TICKS" not in partygoer, "Old passive capture behavior must be removed"
    assert "partygoer.setTarget(player)" not in director, "Natural Partygoers begin stalking instead of instantly aggroing"
    require(partygoer, "implements GeoEntity", "Partygoer uses GeckoLib entity rendering")
    require(partygoer, "registerControllers", "Partygoer has an animation controller")
    require(watcher, "MAX_LIFETIME_TICKS = 180", "Watcher lifetime remains bounded to nine seconds")
    require(watcher, "DIRECT_NOTICE_GRACE_CHECKS = 3", "Watcher vanishes rapidly after being noticed")
    require(watcher, "tickCount > (showcaseMode ? SHOWCASE_LIFETIME_TICKS : MAX_LIFETIME_TICKS)", "natural and showcase Watcher lifetimes remain bounded")
    require(watcher, "MIN_PLAYER_DISTANCE_SQR = 72.25D", "Watcher retains its minimum approach distance")
    require(watcher, "distanceToSqr(nearest) < MIN_PLAYER_DISTANCE_SQR", "Watcher disappears when approached")
    require(items, 'register("almond_water"', "Almond Water registry")
    require(items, 'register("flashlight"', "Flashlight registry")
    require(items, 'register("battery"', "Battery registry")
    require(flashlight, "MobEffects.NIGHT_VISION", "Flashlight activation effect")
    require(almond_water, "MobEffects.REGENERATION", "Almond Water restorative effect")

    allowed_agriculture = {
        "boiled_water_bottle", "filtered_water_bottle", "grain_porridge",
        "barley_vegetable_soup", "vegetable_stew", "garden_salad"
    }
    for filename in ("level_3_supply", "level_4_supply", "level_5_supply"):
        loot = read_json(RES / f"data/threshold/loot_table/chests/{filename}.json")
        assert loot["type"] == "minecraft:chest"
        for pool in loot["pools"]:
            for entry in pool["entries"]:
                if entry["type"] == "minecraft:item" and entry["name"].startswith("gugugaga:"):
                    assert entry["name"].split(":", 1)[1] in allowed_agriculture

    print("THRESHOLD_SIX_LEVEL_TRANSIT_AND_AUDIO_VALIDATION_PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
