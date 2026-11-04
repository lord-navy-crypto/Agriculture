#!/usr/bin/env python3
"""Boundary agreement and deterministic invariants of the Threshold room grid."""
def mix(seed, x, z, salt):
    mask=(1<<64)-1
    v=(seed ^ salt ^ ((x*0x9E3779B97F4A7C15)&mask) ^
       ((z*0xC2B2AE3D27D4EB4F)&mask)) & mask
    v ^= v>>30
    v=(v*0xBF58476D1CE4E5B9)&mask
    v ^= v>>27
    v=(v*0x94D049BB133111EB)&mask
    return v ^ (v>>31)

def along(seed,size,radius,bx,bz,coordinate,kind):
    edge=mix(seed,bx,bz,0xE451 if kind=="z" else 0xE452)
    if edge%6==0: return False
    margin=size//4
    center=margin+((edge>>16)%(size-2*margin))
    return abs(coordinate-center)<=radius

def doorway(seed,size,radius,rx,rz,lx,lz):
    if lx==0: return along(seed,size,radius,rx,rz,lz,"z")
    if lx==size-1: return along(seed,size,radius,rx+1,rz,lz,"z")
    if lz==0: return along(seed,size,radius,rx,rz,lx,"x")
    if lz==size-1: return along(seed,size,radius,rx,rz+1,lx,"x")
    return False

def check():
    for seed in (0,1,42,-19,123456):
        for size,rad in ((32,1),(48,2)):
            for rx in range(-8,9):
                for rz in range(-8,9):
                    for along_coord in range(1,size-1):
                        assert doorway(seed,size,rad,rx,rz,size-1,along_coord)==doorway(
                            seed,size,rad,rx+1,rz,0,along_coord)
                        assert doorway(seed,size,rad,rx,rz,along_coord,size-1)==doorway(
                            seed,size,rad,rx,rz+1,along_coord,0)
    print("THRESHOLD_ROOM_EDGE_INVARIANTS_PASS")

if __name__=="__main__": check()
