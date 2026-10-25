#!/usr/bin/env python3
"""Structural and agronomic-content QA. Run alongside NeoForge build."""
import json
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1] / "src/main/resources"
CROPS = "corn tomato onion rice soybean strawberry cucumber blueberry bell_pepper garlic barley oat rye pea lettuce cabbage spinach broccoli eggplant cotton flax peanut sorghum sunflower_seed millet buckwheat chickpea lentil radish turnip sweet_potato kale zucchini raspberry cranberry sesame".split()
NEW = "barley oat rye pea lettuce cabbage spinach broccoli eggplant cotton flax peanut sorghum sunflower_seed millet buckwheat chickpea lentil radish turnip sweet_potato kale zucchini raspberry cranberry sesame".split()
def j(p): return json.loads((ROOT / p).read_text(encoding="utf8"))
def main():
  en=j("assets/gugugaga/lang/en_us.json")
  zh=j("assets/gugugaga/lang/zh_cn.json")
  for crop in CROPS:
    states=j(f"assets/gugugaga/blockstates/{crop}_crop.json")["variants"]
    assert set(states)=={f"age={a}" for a in range(8)},crop
    assert j(f"data/gugugaga/recipe/{crop}_seeds.json")["result"]["id"]==f"gugugaga:{crop}_seeds"
    loot=j(f"data/gugugaga/loot_table/blocks/{crop}_crop.json")["pools"]
    assert len(loot)==2,crop
    for item in (crop,crop+"_seeds"):
      j(f"assets/gugugaga/models/item/{item}.json")
      assert f"item.gugugaga.{item}" in en and f"item.gugugaga.{item}" in zh
  print(f"AGRICULTURE_EXPANSION_OK: {len(CROPS)} plantable crops (+{len(NEW)})")
if __name__=="__main__": main()
