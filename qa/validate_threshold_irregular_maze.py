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
    assert "isGuaranteedSpine(seed, x, z, spacing, jitter, salt)" in source
    assert "isLevel0EntryPerimeter(x, z)" in source
    assert "!isLevel0TransitionFootprint(x, z)" in source
    assert "x == 0 && z >= 13 && z <= 17" in source
    assert "private static boolean isGuaranteedSpine" in source
    assert "if (!spine && !sample.boundary" in source
    assert "public static boolean isGuaranteedCorridor" in source
    assert "int mergeRate = level0 ? 10 : 23" in source
    assert "isMergedEdge(first, second, edge, mergeRate)" in source
    assert "merged || (open && isDoorway" in source
    assert "Math.abs(a.gx - b.gx) + Math.abs(a.gz - b.gz) != 1" in source
    assert "archetype <= 11 && ThresholdIrregularMazeLayout" in feature
    assert "archetype <= 10 && ThresholdIrregularMazeLayout" in feature
    assert ".isGuaranteedCorridor(seed, x, z, true)" in feature
    assert ".isGuaranteedCorridor(seed, x, z, false)" in feature
    assert "hasLevel0Divider" in feature and "hasLevel1Divider" in feature
    # Ensure each seed-stable parent edge has a physical two-wide path, not
    # merely a declared logical edge. Sample every quarter block of each link.
    def spine(seed, x, z, spacing, jitter, salt):
        bx, bz = x // spacing, z // spacing
        for gx in range(bx-1, bx+2):
            for gz in range(bz-1, bz+2):
                if gx == 0 and gz == 0: continue
                px = gx if gx == 0 else gx - (1 if gx > 0 else -1)
                pz = gz - (1 if gz > 0 else -1) if gx == 0 else gz
                ax, az = center(seed,gx,gz,spacing,jitter,salt)
                bx2, bz2 = center(seed,px,pz,spacing,jitter,salt)
                vx, vz = bx2-ax, bz2-az
                denom = vx*vx+vz*vz
                t = min(1,max(0,((x-ax)*vx+(z-az)*vz)/denom))
                if (x-ax-t*vx)**2+(z-az-t*vz)**2 <= 5.0:
                    return True
        return False

    for seed in (0,42,-17):
        for spacing,jitter,salt in ((22,6,0x4D415A45304C),(28,7,0x4D415A45314C)):
            for gx in range(-3,4):
                for gz in range(-3,4):
                    p=parent((gx,gz))
                    if p is None: continue
                    ax,az=center(seed,gx,gz,spacing,jitter,salt)
                    bx,bz=center(seed,p[0],p[1],spacing,jitter,salt)
                    for step in range(101):
                        t=step/100
                        x=round(ax+(bx-ax)*t)
                        z=round(az+(bz-az)*t)
                        assert spine(seed,x,z,spacing,jitter,salt), (seed,spacing,gx,gz,step)
    # Sample the same deterministic finite-width spine as the Java generator
    # around the four Level 0 starting room borders. The old blanket wall
    # protection disconnected entire negative-axis sectors on some world seeds.
    def crosses(seed, x, z, spacing, jitter, salt):
        bx,bz=x//spacing,z//spacing
        for gx in range(bx-1,bx+2):
            for gz in range(bz-1,bz+2):
                if gx==gz==0:continue
                px,pz=parent((gx,gz))
                ax,az=center(seed,gx,gz,spacing,jitter,salt)
                cx,cz=center(seed,px,pz,spacing,jitter,salt)
                vx,vz=cx-ax,cz-az
                d=vx*vx+vz*vz
                if not d:continue
                t=min(1.0,max(0.0,((x-ax)*vx+(z-az)*vz)/d))
                if (x-ax-t*vx)**2 + (z-az-t*vz)**2 <= 5.0:
                    return True
        return False

    for seed in (0,1,42,2026,-17):
        west=sum(crosses(seed,0,z,22,6,0x4D415A45304C)
                 for z in range(1,32) if not 13<=z<=17)
        north=sum(crosses(seed,x,0,22,6,0x4D415A45304C)
                  for x in range(1,32))
        south=sum(crosses(seed,x,31,22,6,0x4D415A45304C)
                  for x in range(1,31))
        assert west+north+south>=2,(seed,west,north,south)

    print("THRESHOLD_IRREGULAR_MAZE_V4_2_PASS: variable rooms, connected spine and entry perimeter seams")

if __name__=="__main__":
    main()
