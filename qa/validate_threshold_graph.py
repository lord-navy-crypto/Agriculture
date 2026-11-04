#!/usr/bin/env python3
"""Boundary agreement, deterministic room exits and Level 1 no-sealed-cell invariants."""
MASK=(1<<64)-1

def mix(seed,x,z,salt):
    v=(seed ^ salt ^ ((x*0x9E3779B97F4A7C15)&MASK) ^
       ((z*0xC2B2AE3D27D4EB4F)&MASK)) & MASK
    v ^= v>>30
    v=(v*0xBF58476D1CE4E5B9)&MASK
    v ^= v>>27
    v=(v*0x94D049BB133111EB)&MASK
    return v ^ (v>>31)

def primary_exit(seed,x,z,direction):
    return mix(seed,x,z,0xE453)%4 == direction

def edge_open(seed,bx,bz,kind,guarantee=True):
    edge=mix(seed,bx,bz,0xE451 if kind=="z" else 0xE452)
    if kind=="z":
        primary=(primary_exit(seed,bx-1,bz,1) or primary_exit(seed,bx,bz,3))
    else:
        primary=(primary_exit(seed,bx,bz-1,2) or primary_exit(seed,bx,bz,0))
    return edge%6 != 0 or (guarantee and primary)

def along(seed,size,radius,bx,bz,coordinate,kind,guarantee=True):
    edge=mix(seed,bx,bz,0xE451 if kind=="z" else 0xE452)
    if not edge_open(seed,bx,bz,kind,guarantee):
        return False
    margin=size//4
    center=margin+((edge>>16)%(size-2*margin))
    return abs(coordinate-center)<=radius

def doorway(seed,size,radius,rx,rz,lx,lz,guarantee=True):
    if lx==0: return along(seed,size,radius,rx,rz,lz,"z",guarantee)
    if lx==size-1: return along(seed,size,radius,rx+1,rz,lz,"z",guarantee)
    if lz==0: return along(seed,size,radius,rx,rz,lx,"x",guarantee)
    if lz==size-1: return along(seed,size,radius,rx,rz+1,lx,"x",guarantee)
    return False

def room_has_exit(seed,size,rx,rz):
    return any((
        edge_open(seed,rx,rz,"z",True),
        edge_open(seed,rx+1,rz,"z",True),
        edge_open(seed,rx,rz,"x",True),
        edge_open(seed,rx,rz+1,"x",True),
    ))

def check():
    for seed in (0,1,42,-19,123456):
        for size,rad in ((32,1),(48,2)):
            for rx in range(-8,9):
                for rz in range(-8,9):
                    for along_coord in range(1,size-1):
                        assert doorway(seed,size,rad,rx,rz,size-1,along_coord,True)==doorway(
                            seed,size,rad,rx+1,rz,0,along_coord,True)
                        assert doorway(seed,size,rad,rx,rz,along_coord,size-1,True)==doorway(
                            seed,size,rad,rx,rz+1,along_coord,0,True)
        for rx in range(-24,25):
            for rz in range(-24,25):
                assert room_has_exit(seed,48,rx,rz), f"sealed Level 1 room at {(rx,rz)}"

    # Level 0 retains occasional fully closed boundaries as part of its isolation motif.
    assert any(not edge_open(seed, x, z, "z", False)
               for seed in (0,1,42) for x in range(-8,9) for z in range(-8,9))
    print("THRESHOLD_ROOM_EDGE_AND_LEVEL1_ESCAPE_INVARIANTS_PASS")

if __name__=="__main__": check()
