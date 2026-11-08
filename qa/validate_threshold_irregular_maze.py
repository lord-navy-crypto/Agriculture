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

def edge_hash(seed,a,b,salt):
    if a[0]<b[0] or (a[0]==b[0] and a[1]<=b[1]):
        left,right=a,b
    else:
        left,right=b,a
    return mix(seed,left[0]*31+right[0],left[1]*31+right[1],salt^0xED63)

def parent(cell):
    x,z=cell
    if x:
        return x-(1 if x>0 else -1),z
    if z:
        return x,z-(1 if z>0 else -1)
    return None

def assert_connected_spine(seed, spacing, jitter, salt, root_size, target_regions):
    # This local rasterizer mirrors ThresholdIrregularMazeLayout's exact
    # finite-width parent-segment predicate without relying on main() locals.
    def corridor_spine(x, z):
        bx, bz = x // spacing, z // spacing
        for gx in range(bx - 1, bx + 2):
            for gz in range(bz - 1, bz + 2):
                if gx == 0 and gz == 0:
                    continue
                px = gx if gx == 0 else gx - (1 if gx > 0 else -1)
                pz = gz - (1 if gz > 0 else -1) if gx == 0 else gz
                ax, az = center(seed, gx, gz, spacing, jitter, salt)
                cx, cz = center(seed, px, pz, spacing, jitter, salt)
                vx, vz = cx - ax, cz - az
                denom = vx * vx + vz * vz
                if denom < 1:
                    continue
                t = max(0.0, min(1.0, ((x - ax) * vx + (z - az) * vz) / denom))
                if (x - ax - t * vx) ** 2 + (z - az - t * vz) ** 2 <= 5.0:
                    return True
        return False

    # The root room is open floor; every other walkable tile must belong to
    # the deterministic parent-spine network. Search farther than the target
    # boxes because some parent links leave and re-enter a sampled region.
    limit = 160
    walkable = {
        (x, z)
        for x in range(limit)
        for z in range(limit)
        if corridor_spine(x, z)
    }
    walkable.update((x, z) for x in range(root_size) for z in range(root_size))
    start = (root_size // 2, root_size // 2)
    assert start in walkable
    reached = {start}
    queue = [start]
    head = 0
    while head < len(queue):
        x, z = queue[head]
        head += 1
        for neighbour in ((x + 1, z), (x - 1, z), (x, z + 1), (x, z - 1)):
            if neighbour in walkable and neighbour not in reached:
                reached.add(neighbour)
                queue.append(neighbour)

    targets = set()
    for x0, x1, z0, z1 in target_regions:
        targets.update(
            (x, z)
            for x in range(x0, x1)
            for z in range(z0, z1)
            if corridor_spine(x, z)
        )
    missing = targets - reached
    assert not missing, (seed, spacing, len(missing), sorted(missing)[:8])


def main():
    source=(ROOT/"threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdIrregularMazeLayout.java").read_text("utf8")
    feature=(ROOT/"threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdRoomLayoutFeature.java").read_text("utf8")
    runtime=(ROOT/"threshold/src/main/java/io/github/lordnavycrypto/threshold/worldgen/ThresholdRuntimeChecks.java").read_text("utf8")
    assert "LEVEL_0_SPACING = 22" in source and "LEVEL_1_SPACING = 28" in source
    assert "refineLevel0(world, chunkX, chunkZ, seed, pos)" in feature
    assert "refineLevel1(world, chunkX, chunkZ, seed, pos)" in feature
    assert "isParent(Cell child, Cell parent)" in source and "isDoorway" in source
    for seed in (0,1,42,2026,-17):
        for level0,salt in ((True,0x4D415A45304C),(False,0x4D415A45314C)):
            pocket_count=sum((40<=mix(seed,x,z,salt^0x1A7E10)%100<64) if level0
                             else (36<=mix(seed,x,z,salt^0x1A7E10)%100<69)
                             for x in range(-5,6) for z in range(-5,6))
            assert 8 <= pocket_count <= 113,(seed,level0,pocket_count)
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
    assert "public static boolean isProtectedPassageOrEntry" in source
    assert "public static boolean isResourceAnchor" in source
    assert "clearGuaranteedSpine(world, chunkX, chunkZ, seed, pos, true)" in feature
    assert "clearGuaranteedSpine(world, chunkX, chunkZ, seed, pos, false)" in feature
    assert "carpetOrStep.getBlock() instanceof StairBlock" in feature
    assert "feet.getBlock() instanceof StairBlock" in feature
    assert "isLevel1VentilationStackCell" in feature
    assert "either the whole patch exists intact or the room simply has no patch." in feature
    assert "boolean pocketC = x >= 116 && x <= 122 && z >= 21 && z <= 27;" in runtime
    assert "level0FloorFoldStairBuilt" in runtime
    assert "level0.getChunk(7, 0)" in runtime and "level0.getChunk(7, 1)" in runtime
    assert "level0FloorFoldStairFacing(seed, roomX, roomZ, localX, localZ)" in feature
    assert "isFloorFoldProtectedRoute" in feature
    assert "isLevel1StarterComposterCell(seed, localX, localZ)" in feature
    assert "isGuaranteedLevel0PillarCell(seed, roomX, roomZ, localX, localZ)" in feature
    assert "private static boolean isGuaranteedLevel0PillarCell" in feature
    assert "boolean level0PillarForestBuilt = level0PillarCount >= 1" in runtime
    assert all(f"level0.getChunk({x}, {z})" in runtime
               for x, z in ((4, 4), (5, 4), (4, 5), (5, 5)))
    assert "isLevel1ComposterRouteProtected" in feature
    assert "isReservedRoomPath(seed, LEVEL_1_ROOM_SIZE" in feature
    assert "for (int x = 20; x <= 31; x++)" in runtime
    assert "if (!ThresholdIrregularMazeLayout.isProtectedPassageOrEntry(x, z, false)" in feature
    assert "isMergedEdge(first, second, edge, mergeRate)" in source
    assert "int mergeRate = level0 ? 1 : 4" in source
    assert "int openRate = level0 ? 54 : 46" in source
    assert "doorwayWidth(edgeHash, level0)" in source
    assert "hasDoorwayWidthVariety" in source
    assert "hasInteriorPartitionVariety" in source
    assert "hasNestedAlcoveVariety" in source
    assert "nestedAlcoveTile(h,lx,lz,true)" in source
    assert "nestedAlcoveTile(h,lx,lz,false)" in source
    assert "if(pocket==2)return false" in source
    assert "if(vertical && insideDoorGap(dz,sideGap,width))return 2;" in source
    assert "if(horizontal && insideDoorGap(dx,northGap,width))return 2;" in source
    assert "insideDoorGap(lz, gapZ, width)" in source
    assert "insideDoorGap(lx, gapX, width)" in source
    assert "sideReturn" in source and "rackReturn" in source
    assert "topRoll < 82 ? 71" in source
    assert "topRoll < 76 ? 73" in source
    assert "variableDoorwayRadius(edge, radius, roomSize)" in feature
    assert "variableWidths ? variableDoorwayRadius(edge, radius, roomSize) : radius" in feature
    assert "LEVEL_0_ROOM_SIZE, 1, roomX, roomZ, localX, localZ, false, true)" in feature
    assert "LEVEL_1_ROOM_SIZE, 2, roomX, roomZ, localX, localZ, true, true)" in feature
    for size in ("LEVEL_2_ROOM_SIZE","LEVEL_3_ROOM_SIZE","LEVEL_4_ROOM_SIZE","LEVEL_5_ROOM_SIZE"):
        assert f"{size}, 2, roomX, roomZ, localX, localZ, true, false)" in feature
    assert "Math.max(Math.max(0, baseRadius - 1), selected)" in feature
    assert "Math.max(Math.max(1, baseRadius - 1), selected)" in feature
    assert "merged || (open && isDoorway" in source
    assert "Math.abs(a.gx - b.gx) + Math.abs(a.gz - b.gz) != 1" in source
    assert "archetype <= 11 && ThresholdIrregularMazeLayout" in feature
    assert "archetype <= 10 && ThresholdIrregularMazeLayout" in feature
    assert ".isGuaranteedCorridor(seed, x, z, true)" in feature
    assert ".isGuaranteedCorridor(seed, x, z, false)" in feature
    assert "hasLevel0Divider" in feature and "hasLevel1Divider" in feature
    # The fixed Level 1 generator-plant landmark cannot lose its furnace core.
    generator=feature.split("case 26 -> {",1)[1].split("default ->",1)[0]
    assert "if (!upperWalkway && generatorCore)" in generator
    assert "engineBase && !generatorCore" in generator
    # Authored sunken conference pit keeps both stairs on every world seed.
    pit=feature.split("case 18 -> {",1)[1].split("case 19 -> {",1)[0]
    assert "!protectedRoute && !upperWalkway" not in pit
    assert "if (pit && !upperWalkway)" in pit
    assert "if (level0Architecture(seed, roomX, roomZ) >= 16)" in feature
    mezzanine=feature.split("case 16 -> {",1)[1].split("case 17 -> {",1)[0]
    assert "if (!upperWalkway && stair)" in mezzanine
    assert "head.getBlock() instanceof StairBlock" in feature
    assert "isLevel1GeneratorCore(seed, x, z)" in feature
    assert "private static boolean isLevel1GeneratorCore" in feature
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

    def doorway_radius(edge, base_radius, level0):
        roll=(edge>>40)%100
        if level0:
            selected=0 if roll<12 else 1 if roll<42 else 2 if roll<70 else 3 if roll<88 else 4 if roll<97 else 5
            return max(max(0,base_radius-1),selected)
        selected=1 if roll<10 else 2 if roll<34 else 3 if roll<60 else 4 if roll<82 else 5 if roll<95 else 6
        return max(max(1,base_radius-1),selected)

    for seed in (0,1,42,2026,-17):
        for level0,base_radius,spacing,jitter,salt in (
                (True,1,22,6,0x4D415A45304C),
                (False,2,28,7,0x4D415A45314C)):
            legacy_widths=set()
            for gx in range(-12,13):
                for gz in range(-12,13):
                    edge=mix(seed,gx,gz,0xE451)
                    legacy_widths.add(2*doorway_radius(edge,base_radius,level0)+1)
            assert any(w<=3 for w in legacy_widths), (seed,level0,legacy_widths)
            assert any(5<=w<=7 for w in legacy_widths), (seed,level0,legacy_widths)
            assert any(w>=9 for w in legacy_widths), (seed,level0,legacy_widths)

            # Mirror the Voronoi shared-edge hash and its level-specific width table.
            widths=set()
            for gx in range(-6,7):
                for gz in range(-6,7):
                    h=edge_hash(seed,(gx,gz),(gx+1,gz),salt)
                    roll=(h>>29)%100
                    if level0:
                        width=1 if roll<12 else 2 if roll<46 else 3 if roll<74 else 4 if roll<90 else 5 if roll<96 else 7 if roll<99 else 9
                    else:
                        width=2 if roll<9 else 3 if roll<35 else 4 if roll<65 else 5 if roll<82 else 6 if roll<92 else 8 if roll<98 else 10
                    widths.add(width)
            assert any(w<= (2 if level0 else 3) for w in widths), (seed,level0,widths)
            assert any((3 if level0 else 4)<=w<=(5 if level0 else 6) for w in widths), (seed,level0,widths)
            assert any(w>=(7 if level0 else 8) for w in widths), (seed,level0,widths)

    # Inset rooms expose two perpendicular pairs of doorways. They must
    # override any coincident dividers, rather than becoming fake entrances.
    for seed in (0,42,2026,-17):
        for level0,salt in ((True,0x4D415A45304C),(False,0x4D415A45314C)):
            checked=0
            for gx in range(-5,6):
                for gz in range(-5,6):
                    h=mix(seed,gx,gz,salt^0x1A7E10)
                    roll=h%100
                    if not ((40<=roll<64) if level0 else (36<=roll<69)):
                        continue
                    hx=(3 if level0 else 5)+(h>>25)%2
                    hz=(4 if level0 else 5)+(h>>34)%2
                    ox=(h>>10)%5-2
                    oz=(h>>18)%5-2
                    offset_range=3 if level0 else 5
                    ng=(h>>43)%offset_range-offset_range//2
                    sg=(h>>51)%offset_range-offset_range//2
                    width=3 if level0 else 5
                    def tile(lx,lz):
                        dx,dz=lx-ox,lz-oz
                        vertical=abs(dx)==hx and abs(dz)<=hz
                        horizontal=abs(dz)==hz and abs(dx)<=hx
                        if not(vertical or horizontal):return 0
                        if vertical and abs(dz-sg)<=(width-1)//2:return 2
                        if horizontal and abs(dx-ng)<=(width-1)//2:return 2
                        return 1
                    assert all(tile(ox+side*hx,oz+sg)==2 for side in (-1,1))
                    assert all(tile(ox+ng,oz+side*hz)==2 for side in (-1,1))
                    assert tile(ox+hx,oz+hz)==1
                    checked+=1
            assert checked>=8,(seed,level0,checked)

    # Rasterize the exact parent-spine mask and run a four-neighbour BFS
    # from each floor's open entry room. Every corridor tile in the server's
    # sampled landmark regions must be reachable, across six world seeds.
    for seed in (0, 1, 42, 2026, -17, -1539075734086757806):
        assert_connected_spine(
            seed, 22, 6, 0x4D415A45304C, 32, [(48, 64, 32, 64)]
        )
        assert_connected_spine(
            seed, 28, 7, 0x4D415A45314C, 48,
            [(48, 80, 16, 32), (0, 16, 64, 96)]
        )

    print("THRESHOLD_IRREGULAR_MAZE_V4_8_PASS: nested pockets have clear doorways, fully connected parent spine and varied widths")

if __name__=="__main__":
    main()
