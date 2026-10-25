#!/usr/bin/env python3
"""Build original 64x64 PNG textures for the Redactor/Lapis and Yellow Smiler.

No borrowed skins/sounds/artwork. Python 3 standard library only.
Output is generated under build/ and bundled by Gradle processResources.
This is an alpha ORIGINAL ART PLACEHOLDER for later hand-drawn refinement.
"""
import pathlib
import struct
import sys
import zlib

DIM=64
def new(base):
    return [[list(base) for _ in range(DIM)] for _ in range(DIM)]

def rect(p,x0,y0,x1,y1,c):
    for y in range(max(0,y0),min(DIM,y1)):
        for x in range(max(0,x0),min(DIM,x1)):
            p[y][x]=list(c)

def dot(p,x,y,c): rect(p,x,y,x+1,y+1,c)

def line(p,x0,y0,x1,y1,c):
    count=max(abs(x1-x0),abs(y1-y0),1)
    for i in range(count+1):
        dot(p,round(x0+(x1-x0)*i/count),
            round(y0+(y1-y0)*i/count),c)

def png(path,p):
    path.parent.mkdir(parents=True,exist_ok=True)
    raw=b"".join(b"\x00"+bytes(ch for pixel in row for ch in pixel) for row in p)
    def chunk(kind,data):
        return struct.pack(">I",len(data))+kind+data+struct.pack(">I",zlib.crc32(kind+data)&0xffffffff)
    data=b"\x89PNG\r\n\x1a\n"+chunk(b"IHDR",struct.pack(">IIBBBBB",DIM,DIM,8,2,0,0,0))
    data+=chunk(b"IDAT",zlib.compress(raw,9))+chunk(b"IEND",b"")
    path.write_bytes(data)
    return path

def lapis():
    p=new((27,77,118))
    # original hand-mapped lapis gemstone blocks, a deliberately cheap-safe shell
    for y in range(DIM):
        for x in range(DIM):
            n=((x*37+y*19+(x*y)%17)%17)-8
            p[y][x]=[max(0,27+n//3),max(0,77+n),max(0,118+n)]
    rect(p,10,10,20,20,(31,103,149)) # cube face
    for y in range(10,20,2):line(p,10,y,19,y,(37,92+(y%5),146))
    # On the thin original front plaque (texOffs 0,32; positive geometry
    # on model's north side), gentle regular face rather than slapstick
    rect(p,0,32,19,48,(53,129,170))
    rect(p,2,35,4,37,(213,237,233))
    rect(p,7,35,9,37,(213,237,233))
    line(p,3,39,7,39,(218,237,231))
    return p

def revealed():
    p=new((15,34,40))
    for y in range(DIM):
        for x in range(DIM):
            n=(x*7+y*13+x*y)%11
            p[y][x]=[14+n//4,33+n//2,39+n]
    rect(p,6,6,14,14,(212,223,211)) # face of 8x8x6 true mask
    dot(p,8,9,(37,67,67));dot(p,11,9,(37,67,67))
    line(p,8,12,11,12,(42,72,72))
    for off in (4,11,19,26):
        line(p,32+off,8,32+off+3,15,(64,139,135))
    line(p,38,23,43,29,(129,187,168))
    line(p,40,30,45,37,(119,170,151))
    return p

def yellow():
    p=new((208,169,59))
    for y in range(DIM):
        for x in range(DIM):
            jitter=((x*7+y*13+(x*y)%9)%9)-4
            p[y][x]=[max(0,214+jitter),max(0,180+jitter),max(0,58+jitter)]
    # cube head front is approximately U=depth..depth+width,
    # V=depth..depth+height. Uniform eyes and too-perfect smile.
    rect(p,10,10,20,20,(244,210,88))
    rect(p,12,13,14,15,(34,31,18))
    rect(p,17,13,19,15,(34,31,18))
    line(p,13,17,17,17,(45,38,19))
    dot(p,12,16,(45,38,19));dot(p,18,16,(45,38,19))
    return p

def main():
    if len(sys.argv)!=2:raise SystemExit("Usage: generate_character_sprites.py <resource root>")
    target=pathlib.Path(sys.argv[1])/"assets/gugugaga/textures/entity"
    for name,fn in (("lapis_mask.png",lapis),
                    ("redactor_true.png",revealed),
                    ("yellow_smiler.png",yellow)):
        file=png(target/name,fn())
        assert file.read_bytes().startswith(b"\x89PNG\r\n\x1a\n")
        print("ORIGINAL_SPRITE",file,len(file.read_bytes()))

if __name__=="__main__":main()
