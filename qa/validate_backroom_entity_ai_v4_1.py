#!/usr/bin/env python3
"""Static contracts for the unified mod's Alpha 20 entity improvements."""
from pathlib import Path
import json

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"threshold/src/main/java/io/github/lordnavycrypto/threshold"
LANG=ROOT/"src/main/resources/assets/gugugaga/lang"

def read(name):
    return (JAVA/name).read_text(encoding="utf8")

def main():
    hound=read("entity/ThresholdHoundEntity.java")
    party=read("entity/ThresholdPartygoerEntity.java")
    handler=read("entity/ThresholdPlayerEvents.java")
    mod=read("ThresholdMod.java")
    for token in ("chooseDarkCover","LightLayer.BLOCK","lostSightTicks","hasLineOfSight(target)",
                  "level().hasChunkAt(feet)","level().hasChunkAt(feet.above())",
                  "level().hasChunkAt(feet.below())"):
        assert token in hound, f"Hound sensory recovery missing {token}"
    for token in ("lastVisiblePlayerPosition","InvestigateLastSightingGoal","sightMemoryTicks",
                  "hasLineOfSight(observedPlayer)"):
        assert token in party, f"Partygoer limited sight memory missing {token}"
    assert "NeoForge.EVENT_BUS.register(ThresholdPlayerEvents.class)" in mod
    for token in ("LivingDeathEvent","LivingDropsEvent","PlayerEvent.Clone",
                  "PlayerRespawnEvent","isPartygoerDamageSource","INVENTORY_SNAPSHOT_KEY",
                  "level0, 4.5D, 65.0D, 4.5D","event.isWasDeath()",
                  "originalData.remove(INVENTORY_SNAPSHOT_KEY)"):
        assert token in handler, f"Scoped inventory-preserving Partygoer respawn missing {token}"
    assert "gamerule" not in handler.lower().replace("gamerule remain untouched", "")
    assert "keepInventory" not in handler or "remain untouched" in handler
    for locale in ("en_us.json","zh_cn.json"):
        strings=json.loads((LANG/locale).read_text(encoding="utf8"))
        for key in ("message.gugugaga.partygoer_level0_missing",
                    "message.gugugaga.partygoer_capture_success"):
            assert strings.get(key), (locale,key)
    print("BACKROOM_ENTITY_V4_1_PASS: Hound cover, sight-limited Partygoer, scoped respawn and bilingual messages")

if __name__=="__main__":
    main()
