#!/usr/bin/env python3
"""Reproducible 2D UV-atlas proof, NOT a Minecraft gameplay screenshot.

Reads only the 5 original generated PNGs (64x64 truecolor/filter-0),
renders a labeled nearest-neighbor materials gallery and provenance JSON.
Python standard library only; it does not connect to any external service.
"""
import hashlib
import json
import pathlib
import struct
import sys
import zlib

INPUTS = (
    ("lapis_mask", "LAPIS / BLUE MASK", (66, 140, 209)),
    ("redactor_true", "REDACTOR / TRUE", (111, 193, 169)),
    ("yellow_smiler", "YELLOW / FRIENDLY", (232, 200, 87)),
    ("yellow_smiler_true", "YELLOW / EXPOSED", (174, 146, 64)),
    ("observer_blackened", "GUGU / BLACKENED", (123, 158, 178)),
)
MAGIC=b"\x89PNG\r\n\x1a\n"
SCALE=4
PADDING=22
TILE=64*SCALE
WIDTH=len(INPUTS)*TILE+(len(INPUTS)+1)*PADDING
HEIGHT=390

FONT={
 "A":("010","101","111","101","101"),"B":("110","101","110","101","110"),
 "C":("011","100","100","100","011"),"D":("110","101","101","101","110"),
 "E":("111","100","110","100","111"),"F":("111","100","110","100","100"),
 "G":("011","100","101","101","011"),"H":("101","101","111","101","101"),
 "I":("111","010","010","010","111"),"J":("111","001","001","101","010"),
 "K":("101","101","110","101","101"),"L":("100","100","100","100","111"),
 "M":("101","111","111","101","101"),"N":("101","111","111","111","101"),
 "O":("010","101","101","101","010"),"P":("110","101","110","100","100"),
 "Q":("010","101","101","111","011"),"R":("110","101","110","101","101"),
 "S":("011","100","010","001","110"),"T":("111","010","010","010","010"),
 "U":("101","101","101","101","111"),"V":("101","101","101","101","010"),
 "W":("101","101","111","111","101"),"X":("101","101","010","101","101"),
 "Y":("101","101","010","010","010"),"Z":("111","001","010","100","111"),
 "0":("111","101","101","101","111"),"1":("010","110","010","010","111"),
 "2":("110","001","010","100","111"),"3":("110","001","010","001","110"),
 "4":("101","101","111","001","001"),"5":("111","100","110","001","110"),
 "6":("011","100","110","101","010"),"7":("111","001","010","010","010"),
 "8":("010","101","010","101","010"),"9":("010","101","011","001","110"),
 " ":("000","000","000","000","000"),"/":("001","001","010","100","100"),
 "-":("000","000","111","000","000"),":":("000","010","000","010","000"),
 ".":("000","000","000","000","010")
}

def decode_generated_png(path):
    raw=path.read_bytes()
    if not raw.startswith(MAGIC):raise ValueError("not PNG: "+str(path))
    width=height=None
    blocks=[]
    offset=8
    while offset<len(raw):
        size=struct.unpack(">I",raw[offset:offset+4])[0]
        kind=raw[offset+4:offset+8]
        data=raw[offset+8:offset+8+size]
        saved=struct.unpack(">I",raw[offset+8+size:offset+12+size])[0]
        if zlib.crc32(kind+data)&0xffffffff != saved:raise ValueError("PNG CRC failed")
        offset+=12+size
        if kind==b"IHDR":
            width,height,depth,color,_,_,_=struct.unpack(">IIBBBBB",data)
            if (width,height,depth,color)!=(64,64,8,2):
                raise ValueError("expected generated 64x64 RGB")
        elif kind==b"IDAT":
            blocks.append(data)
        elif kind==b"IEND":
            break
    if width!=64 or height!=64:raise ValueError("missing dimensions")
    raster=zlib.decompress(b"".join(blocks))
    stride=64*3+1
    if len(raster)!=64*stride:raise ValueError("unexpected decoded bytes")
    pixels=[]
    for y in range(64):
        row=raster[y*stride:(y+1)*stride]
        if row[0]!=0:raise ValueError("expected reproducible unfiltered PNG")
        pixels.append([tuple(row[1+3*x:4+3*x]) for x in range(64)])
    return pixels,raw

def write_rgb_png(path,frame,width,height):
    body=b"".join(b"\x00"+b"".join(bytes(p) for p in row) for row in frame)
    def chunk(kind,data):
        return struct.pack(">I",len(data))+kind+data+struct.pack(">I",zlib.crc32(kind+data)&0xffffffff)
    out=MAGIC+chunk(b"IHDR",struct.pack(">IIBBBBB",width,height,8,2,0,0,0))
    out+=chunk(b"IDAT",zlib.compress(body,9))+chunk(b"IEND",b"")
    path.write_bytes(out)

def paint(frame,x,y,text,color=(228,234,238),size=2):
    cursor=x
    for ch in text.upper():
        rows=FONT.get(ch,FONT[" "])
        for dy,row in enumerate(rows):
            for dx,val in enumerate(row):
                if val=="1":
                    for yy in range(size):
                        for xx in range(size):
                            X=cursor+dx*size+xx;Y=y+dy*size+yy
                            if 0<=X<WIDTH and 0<=Y<HEIGHT:frame[Y][X]=color
        cursor+=4*size

def generate(source_root,out_dir):
    source_root=pathlib.Path(source_root)
    out_dir=pathlib.Path(out_dir)
    out_dir.mkdir(parents=True,exist_ok=True)
    frame=[[(12,19,27)]*WIDTH for _ in range(HEIGHT)]
    paint(frame,PADDING,16,"GUGU GAGA V017 MATERIAL REVIEW",size=3)
    paint(frame,PADDING,49,"OFFLINE UV ATLAS - NOT GAME SCREENSHOT",color=(130,149,163),size=2)
    manifest={"kind":"original_uv_atlas_material_review",
              "minecraft_screenshot":False,"width":WIDTH,"height":HEIGHT,
              "textures":[]}
    ystart=102
    for i,(name,label,accent) in enumerate(INPUTS):
        path=source_root/"assets/gugugaga/textures/entity"/(name+".png")
        pixels,raw=decode_generated_png(path)
        xstart=PADDING+i*(TILE+PADDING)
        paint(frame,xstart,75,label,accent,size=2)
        for y in range(64):
            for x in range(64):
                c=pixels[y][x]
                for dy in range(SCALE):
                    for dx in range(SCALE):
                        frame[ystart+y*SCALE+dy][xstart+x*SCALE+dx]=c
        # 1 px matte border for reviewing full 64px edges.
        for x in range(xstart-1,xstart+TILE+1):
            frame[ystart-1][x]=(61,76,85)
            frame[ystart+TILE][x]=(61,76,85)
        for y in range(ystart-1,ystart+TILE+1):
            frame[y][xstart-1]=(61,76,85)
            frame[y][xstart+TILE]=(61,76,85)
        paint(frame,xstart,ystart+TILE+8,"64X64 ORIGINAL",color=(147,169,178),size=2)
        manifest["textures"].append({"file":name+".png","size_bytes":len(raw),
                                     "sha256":hashlib.sha256(raw).hexdigest()})
    sheet=out_dir/"gugugaga-v017-material-review.png"
    write_rgb_png(sheet,frame,WIDTH,HEIGHT)
    manifest["sheet_sha256"]=hashlib.sha256(sheet.read_bytes()).hexdigest()
    (out_dir/"gugugaga-v017-material-manifest.json").write_text(
        json.dumps(manifest,indent=2,sort_keys=True)+"\n")
    print("GUGUGAGA_V017_OFFLINE_ATLAS_PASS",sheet,WIDTH,HEIGHT)
    return sheet

if __name__=="__main__":
    if len(sys.argv)!=3:raise SystemExit("usage: render_visual_review.py <generated resources> <output dir>")
    generate(sys.argv[1],sys.argv[2])
