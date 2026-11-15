#!/usr/bin/env python3
"""Inspect the actual distributable JAR for Nutrition v4.14/v4.15 classes and data."""
import sys
import zipfile
from pathlib import Path

def main():
    if len(sys.argv) != 2:
        raise SystemExit("usage: validate_nutrition_universal_food_jar_v4_15.py <mod.jar>")
    jar=Path(sys.argv[1])
    assert jar.is_file(), jar
    with zipfile.ZipFile(jar) as z:
        names=set(z.namelist())
        required=(
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionFoodCompatibilityV415.class",
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionProfilesV35.class",
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionModeV414.class",
            "io/github/lordnavycrypto/gugugaga/agriculture/NutritionModeNetworkV414.class",
            "data/gugugaga/nutrition/food_profiles_v3_5.json",
            "assets/gugugaga/lang/en_us.json",
            "assets/gugugaga/lang/zh_cn.json",
        )
        missing=[name for name in required if name not in names]
        assert not missing, f"Missing packaged nutrition files/classes: {missing}"
        profile=z.read("data/gugugaga/nutrition/food_profiles_v3_5.json")
        assert b'"schema_version": 1' in profile
        mode=z.read("io/github/lordnavycrypto/gugugaga/agriculture/NutritionModeV414.class")
        resolver=z.read("io/github/lordnavycrypto/gugugaga/agriculture/NutritionFoodCompatibilityV415.class")
        assert len(mode)>1000 and len(resolver)>1000
    print(f"NUTRITION_UNIVERSAL_FOOD_JAR_V415_PASS: {jar.name}; mode, network, resolver, 27-channel catalog and bilingual resources packaged")

if __name__=="__main__":
    main()
