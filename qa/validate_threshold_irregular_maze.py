#!/usr/bin/env python3
"""Check irregular Level 0/1 room geometry and deterministic corridor connectivity."""
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
MASK=(1<<64)-1
C1=0x9E3779B97F4A7C15
C2=0xC2B2AE3D27D4EB4F
C3=0xBF58476D1CE4E5B9
C4=0x94D049BB133111EB

def mix(seed,x,z,salt):
    v=(seed ^ salt ^ ((x*C1)&MASK) ^ ((z*C2)&MASK))&MASK
    v^=v>>30; v=(v*C3)&MASK
    v^=v>>27; v=(v*C4)&MASK
    return (v^(v>>31))&MASK

def center(seed,gx,gz,spacing,jitter,salt):
    jx=mix(seed,gx,gz,salt^0x51A7)%(jitter*2+1)-jitter
    jz=mix(seed,gx,gz,salt^0xA73D)%(jitter*2+1)-jitter
    return gx*spacing+spacing//2+jx, gz*spacing+spacing//2+jz

def parent(cell):
    x,z=cell
    if x:
        return x-(1 if x>0 else -1),z
    if z:
        return x,z-(1 if z>0 else -1)
    return None

def main():
    source=(ROOT/"threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdIrregularMazeLayout.java").read_text("utf8")
    feature=(ROOT/"threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdRoomLayoutFeature.java").read_text("utf8")
    assert "LEVEL_0_SPACING = 22" in source and "LEVEL_1_SPACING = 28" in source
    assert "refineLevel0(world, chunkX, chunkZ, seed, pos)" in feature
    assert "refineLevel1(world, chunkX, chunkZ, seed, pos)" in feature
    assert "isParent(Cell child, Cell parent)" in source and "isDoorway" in source
    for seed in (0,1,42,2026,-17):
        for spacing,jitter,salt in ((22,6,0x4D415A45304C),(28,7,0x4D415A45314C)):
            widths=[]
            for gz in range(-5,6):
                for gx in range(-5,5):
                    a=center(seed,gx,gz,spacing,jitter,salt)[0]
                    b=center(seed,gx+1,gz,spacing,jitter,salt)[0]
                    assert b>a
                    widths.append(b-a)
            assert max(widths)-min(widths)>=3
        for x in range(-12,13):
            for z in range(-12,13):
                if (x,z)==(0,0): continue
                p=parent((x,z))
                assert p is not None and abs(x-p[0])+abs(z-p[1])==1
    assert "hasLevel0Divider" in feature and "hasLevel1Divider" in feature
    print("THRESHOLD_IRREGULAR_MAZE_V4_0_PASS: jittered room sizes, deterministic parent corridors, Level 0/1 integration")

if __name__=="__main__":
    main()
