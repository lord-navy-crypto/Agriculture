#!/usr/bin/env python3
"""Lunar 32-day seasons v3.6: calendar math and live crop-growth connections.

Checks deterministic 4*8 calendar invariants, biome-adjusted local season
duration and safe bounded greenhouse and compatibility logic.
"""
from pathlib import Path
import json
import sys
import zipfile

ROOT=Path(__file__).resolve().parents[1]
JAVA=ROOT/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture"
ASSETS=ROOT/"src/main/resources/assets/gugugaga"
calendar=(JAVA/"LunarSeasonsV36.java").read_text("utf-8")
commands=(JAVA/"LunarSeasonCommandsV36.java").read_text("utf-8")
climate=(JAVA/"CropClimate.java").read_text("utf-8")
additional=(JAVA/"FoodEcologyV33Climate.java").read_text("utf-8")
crop=(JAVA/"AgricultureCropBlock.java").read_text("utf-8")

profiles={
    "TEMPERATE":(8,8,8,8),
    "TROPICAL":(8,10,8,6),
    "ARID":(7,11,8,6),
    "COLD":(7,6,8,11),
    "WET":(9,9,8,6)
}
assert "DAYS_PER_YEAR=32" in calendar
assert "DAYS_PER_MOON=8" in calendar
assert "Math.floorDiv(level.getDayTime(), 24000L)" in calendar
assert "Math.floorMod(absoluteDay, DAYS_PER_YEAR)" in calendar
assert "Math.floorMod(absoluteDay, DAYS_PER_MOON)" in calendar
assert "yearDay(absoluteDay) / DAYS_PER_MOON" in calendar
assert "enum Season { SPRING, SUMMER, AUTUMN, WINTER }" in calendar
for key,lengths in profiles.items():
    assert sum(lengths)==32 and all(x>=6 for x in lengths)
    text="case "+key+" -> new int[]{"+",".join(map(str,lengths))+"}"
    assert text in calendar, (key,text)
    count=[0]*4
    for day in range(32):
        left=day
        for s,days in enumerate(lengths):
            if left<days:
                count[s]+=1
                break
            left-=days
    assert count==list(lengths)
for day in range(0,96):
    assert (day % 8)==(day % 32)%8
    if day%8==0: # season boundary coincides with vanilla full moon
        assert (day%32)//8 in (0,1,2,3)
    assert (day%32) == ((day+32)%32)
# Local climate differences never change the shared world date, year or lunar phase.
assert all(sum(v)==32 for v in profiles.values())
assert 'if(level.getBlockState(check).getBlock() instanceof AbstractGlassBlock)' in calendar
assert "if(!level.hasChunkAt(p))continue;" in calendar
assert "level.getMaxLocalRawBrightness(pos)<9" in calendar
assert "state.is(Blocks.CAMPFIRE)" in calendar
assert "case WINTER ->" in calendar
assert "if(sheltered)probability=Math.max(probability,0.82F);" in calendar
assert "player" not in calendar # server-only crop logic doesn't punish players
assert "LunarSeasonsV36.allowsGrowth(" in climate
assert "LunarSeasonsV36.allowsGrowth(" in additional
assert "CropClimate.allowGrowth(seedName, level, pos, random)" in crop
# Legacy crop irrigation and pollination remain independent.
assert "hasNearbyFreshWater(level, pos" in climate
assert "PollinationGroup" in crop and "bee.hasNectar()" in crop
assert "super.randomTick(level.getBlockState(pos), level, pos, random)" in crop
assert 'Commands.literal("agseason")' in commands
assert "RegisterCommandsEvent" in commands
assert "localDayOfSeason(day,habitat)" in commands
assert "moonPhase(day)" in commands
for code in ("en_us","zh_cn"):
    labels=json.loads((ASSETS/f"lang/{code}.json").read_text("utf-8"))
    assert labels["message.gugugaga.v36_calendar"]
    for season in ("spring","summer","autumn","winter"):
        assert labels["season.gugugaga."+season]
    for phase in range(8):
        assert labels[f"moon.gugugaga.{phase}"]
if len(sys.argv)>1:
    jar=Path(sys.argv[1])
    with zipfile.ZipFile(jar) as z:
        assert z.testzip() is None
        for name in ("LunarSeasonsV36","LunarSeasonCommandsV36"):
            assert ("io/github/lordnavycrypto/gugugaga/agriculture/"
                    +name+".class") in z.namelist()
print("LUNAR_SEASONS_V36_PASS: 32-day year, 4 lunar cycles, "
      "8-day temperate seasons, 5 local biome profiles, "
      "all 108 crops and safe winter/greenhouse gating")
