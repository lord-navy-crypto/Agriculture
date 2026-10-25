#!/usr/bin/env python3
import json,re
from pathlib import Path
root=Path(__file__).resolve().parents[1]
source=(root/"src/main/java/io/github/lordnavycrypto/gugugaga/agriculture/CropClimate.java").read_text("utf8")
obj=json.loads((root/"src/main/resources/data/gugugaga/agronomy/germination_reference.json").read_text("utf8"))
assert len(obj["sources"])==2
assert len(obj["data"])==10
for x in obj["data"]:
    crop=x["crop"]
    minimum=x["minimum_soil_germination_F"]
    assert 0<minimum<130 and minimum%5==0
    assert re.search(r'"'+crop+r'_seeds",\s*'+str(minimum)+r'\b',source),crop
print("AGRONOMIC_SOURCE_DATA_OK: 10 documented seed-germination thresholds match growth classification")
