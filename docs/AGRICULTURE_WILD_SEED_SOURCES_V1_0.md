# Gugu Gaga：100 种作物的野外种子来源

版本：v1.0 · 适用 Minecraft 1.21.1 / NeoForge 21.1.x

## 设计规则

- **野外采集是种子的主要来源。** 野生植株自然生成在指定生物群系的天然地面上，破坏后掉落对应作物的 1–2 颗种子。
- **种子合成只作为繁殖备选。** 每种种子都用该作物自己的农产品加小麦种子合成 2 颗种子。因为每种配方包含不同的模组农产品，所以不会再有完全相同的种子配方。
- **按生长带分组生成，控制世界生成成本。** 现有 12 种野生来源保留各自的生物群系配置；其余 88 种使用 5 个共享的加权方块状态特征，而不是给每种作物再增加独立的整套生成管线。
- **野生来源是可采集植物，不是直接散落的物品。** 野生植株使用作物阶段纹理，生成在天然土壤、草方块、沙、砾石、黏土或泥土等适宜地表上，破坏后掉落种子。
- 生物群系是游戏玩法分组，不代表现实世界植物分布的精确地理复刻。

## 五组共享生成规则

| 生长带 | 生物群系 | 生成规则 |
|---|---|---|
| COOL | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove | `wild_cool_crops`，40 次尝试，约每 5 个区块触发一次 |
| TEMPERATE | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains | `wild_temperate_crops`，40 次尝试，约每 5 个区块触发一次 |
| WARM | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle | `wild_warm_crops`，40 次尝试，约每 5 个区块触发一次 |
| DRY | desert, windswept_savanna | `wild_dry_crops`，40 次尝试，约每 5 个区块触发一次 |
| WET | swamp, mangrove_swamp, river, beach, stony_shore | `wild_wet_crops`，40 次尝试，约每 5 个区块触发一次 |

## 每种作物的种子来源

| 作物 | 中文名 | 生长带 | 野外来源方块 | 生成特征 | 生物群系 |
|---|---|---|---|---|---|
| Corn | 玉米 | WARM | `wild_corn` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Tomato | 番茄 | WARM | `wild_tomato` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Onion | 洋葱 | COOL | `wild_onion` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Rice | 水稻 | WET | `wild_rice` | `wild_wet_crops` | swamp, mangrove_swamp, river, beach, stony_shore |
| Soybean | 大豆 | WARM | `wild_soybean` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Strawberry | 草莓 | COOL | `wild_strawberry` | `wild_strawberry` | forest, birch_forest, meadow |
| Cucumber | 黄瓜 | WARM | `wild_cucumber` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Blueberry | 蓝莓 | COOL | `wild_blueberry` | `wild_blueberry` | taiga, old_growth_pine_taiga, forest |
| Bell Pepper | 甜椒 | WARM | `wild_bell_pepper` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Garlic | 大蒜 | COOL | `wild_garlic` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Barley | 大麦 | COOL | `wild_barley` | `wild_barley` | plains, meadow, windswept_hills |
| Oat | 燕麦 | COOL | `wild_oat` | `wild_oat` | plains, meadow, taiga |
| Rye | 黑麦 | COOL | `wild_rye` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Pea | 豌豆 | COOL | `wild_pea` | `wild_pea` | forest, birch_forest, meadow |
| Lettuce | 莴苣 | COOL | `wild_lettuce` | `wild_lettuce` | forest, meadow, river |
| Cabbage | 卷心菜 | COOL | `wild_cabbage` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Spinach | 菠菜 | COOL | `wild_spinach` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Broccoli | 西兰花 | COOL | `wild_broccoli` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Eggplant | 茄子 | WARM | `wild_eggplant` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Cotton | 棉花 | WARM | `wild_cotton` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Flax | 亚麻 | COOL | `wild_flax` | `wild_flax` | plains, meadow, forest |
| Peanut | 花生 | WARM | `wild_peanut` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Sorghum | 高粱 | DRY | `wild_sorghum` | `wild_dry_crops` | desert, windswept_savanna |
| Sunflower Seed | 葵花籽 | WARM | `wild_sunflower_seed` | `wild_sunflower_seed` | sunflower_plains, plains, savanna |
| Millet | 小米 | DRY | `wild_millet` | `wild_millet` | plains, savanna, savanna_plateau |
| Buckwheat | 荞麦 | COOL | `wild_buckwheat` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Chickpea | 鹰嘴豆 | TEMPERATE | `wild_chickpea` | `wild_temperate_crops` | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains |
| Lentil | 小扁豆 | COOL | `wild_lentil` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Radish | 萝卜 | COOL | `wild_radish` | `wild_radish` | meadow, plains, forest |
| Turnip | 芜菁 | COOL | `wild_turnip` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Sweet Potato | 红薯 | WARM | `wild_sweet_potato` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Kale | 羽衣甘蓝 | COOL | `wild_kale` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Zucchini | 西葫芦 | WARM | `wild_zucchini` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Raspberry | 覆盆子 | COOL | `wild_raspberry` | `wild_raspberry` | forest, birch_forest, taiga |
| Cranberry | 蔓越莓 | COOL | `wild_cranberry` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Sesame | 芝麻 | DRY | `wild_sesame` | `wild_sesame` | savanna, savanna_plateau, plains |
| Lavender | 薰衣草 | DRY | `wild_lavender` | `wild_dry_crops` | desert, windswept_savanna |
| Clover | 三叶草 | TEMPERATE | `wild_clover` | `wild_temperate_crops` | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains |
| Basil | 罗勒 | WARM | `wild_basil` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Mint | 薄荷 | TEMPERATE | `wild_mint` | `wild_temperate_crops` | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains |
| Mustard | 芥菜 | COOL | `wild_mustard` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Canola | 油菜 | COOL | `wild_canola` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Celery | 芹菜 | COOL | `wild_celery` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Leek | 韭葱 | COOL | `wild_leek` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Parsnip | 欧防风 | COOL | `wild_parsnip` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Arugula | 芝麻菜 | COOL | `wild_arugula` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Swiss Chard | 莙荙菜 | COOL | `wild_swiss_chard` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Okra | 秋葵 | WARM | `wild_okra` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Fennel | 茴香 | COOL | `wild_fennel` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Dill | 莳萝 | COOL | `wild_dill` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Cilantro | 香菜 | COOL | `wild_cilantro` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Parsley | 欧芹 | COOL | `wild_parsley` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Collard Greens | 羽衣甘蓝菜 | COOL | `wild_collard_greens` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Green Bean | 四季豆 | WARM | `wild_green_bean` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Chili Pepper | 辣椒 | WARM | `wild_chili_pepper` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Brussels Sprouts | 抱子甘蓝 | COOL | `wild_brussels_sprouts` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Cauliflower | 花椰菜 | COOL | `wild_cauliflower` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Bok Choy | 小白菜 | COOL | `wild_bok_choy` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Shallot | 红葱头 | COOL | `wild_shallot` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Ginger | 生姜 | WET | `wild_ginger` | `wild_wet_crops` | swamp, mangrove_swamp, river, beach, stony_shore |
| Turmeric | 姜黄 | WET | `wild_turmeric` | `wild_wet_crops` | swamp, mangrove_swamp, river, beach, stony_shore |
| Black Bean | 黑豆 | WARM | `wild_black_bean` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Quinoa | 藜麦 | COOL | `wild_quinoa` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Artichoke | 洋蓟 | TEMPERATE | `wild_artichoke` | `wild_temperate_crops` | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains |
| Asparagus | 芦笋 | COOL | `wild_asparagus` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Pumpkin | 南瓜 | WARM | `wild_pumpkin` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Pineapple | 菠萝 | WET | `wild_pineapple` | `wild_wet_crops` | swamp, mangrove_swamp, river, beach, stony_shore |
| Blackberry | 黑莓 | TEMPERATE | `wild_blackberry` | `wild_temperate_crops` | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains |
| Blackcurrant | 黑加仑 | COOL | `wild_blackcurrant` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Amaranth | 苋米 | DRY | `wild_amaranth` | `wild_dry_crops` | desert, windswept_savanna |
| Chives | 韭菜 | COOL | `wild_chives` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Sweet Corn | 甜玉米 | WARM | `wild_sweet_corn` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Cassava | 木薯 | WARM | `wild_cassava` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Taro | 芋头 | WET | `wild_taro` | `wild_wet_crops` | swamp, mangrove_swamp, river, beach, stony_shore |
| Yam | 山药 | WARM | `wild_yam` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Teff | 苔麸 | DRY | `wild_teff` | `wild_dry_crops` | desert, windswept_savanna |
| Watercress | 西洋菜 | WET | `wild_watercress` | `wild_wet_crops` | swamp, mangrove_swamp, river, beach, stony_shore |
| Endive | 苦苣 | COOL | `wild_endive` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Kohlrabi | 球茎甘蓝 | COOL | `wild_kohlrabi` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Rutabaga | 芜菁甘蓝 | COOL | `wild_rutabaga` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Jicama | 豆薯 | WARM | `wild_jicama` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Adzuki Bean | 赤小豆 | WARM | `wild_adzuki_bean` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Mung Bean | 绿豆 | WARM | `wild_mung_bean` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Cowpea | 豇豆 | WARM | `wild_cowpea` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Safflower | 红花籽 | DRY | `wild_safflower` | `wild_dry_crops` | desert, windswept_savanna |
| Chicory | 菊苣 | COOL | `wild_chicory` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Mustard Greens | 芥菜叶 | COOL | `wild_mustard_greens` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Water Spinach | 空心菜 | WET | `wild_water_spinach` | `wild_wet_crops` | swamp, mangrove_swamp, river, beach, stony_shore |
| Celeriac | 根芹 | COOL | `wild_celeriac` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Purslane | 马齿苋 | WARM | `wild_purslane` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Chayote | 佛手瓜 | WARM | `wild_chayote` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Grape | 葡萄 | TEMPERATE | `wild_grape` | `wild_temperate_crops` | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains |
| Tea | 茶叶 | TEMPERATE | `wild_tea` | `wild_temperate_crops` | plains, forest, birch_forest, old_growth_birch_forest, flower_forest, sunflower_plains |
| Coffee | 咖啡豆 | WARM | `wild_coffee` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Rapini | 西兰花苗 | COOL | `wild_rapini` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Napa Cabbage | 大白菜 | COOL | `wild_napa_cabbage` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Melon | 甜瓜 | WARM | `wild_melon` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Watermelon | 西瓜 | WARM | `wild_watermelon` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |
| Fava Bean | 蚕豆 | COOL | `wild_fava_bean` | `wild_cool_crops` | taiga, old_growth_pine_taiga, old_growth_spruce_taiga, meadow, windswept_hills, windswept_forest, grove |
| Jute Mallow | 黄麻叶 | WARM | `wild_jute_mallow` | `wild_warm_crops` | savanna, savanna_plateau, jungle, sparse_jungle, bamboo_jungle |

## 种子配方与去重

100 个种子配方统一采用“对应农产品 + 小麦种子 → 2 颗对应种子”的繁殖配方。野外野生植株负责提供初始种子，避免玩家必须先制作种子才能找到作物的循环依赖。

发布检查会验证：100 个种子配方输出正确且输入组合唯一；100 个野生来源方块均已注册；每个野生方块都有模型、方块状态和掉落表；所有种子均由至少一个已配置的生物群系生成特征覆盖。

## 游戏内验收

- [ ] 在对应生物群系新生成的区块中找到野生作物。
- [ ] 破坏野生植株并确认掉落正确的种子。
- [ ] 将种子带回基地，在耕地上种植对应作物。
- [ ] 成熟后收获农产品，并用农产品与小麦种子制作额外种子。
- [ ] 在不同生物群系检查分布密度，确认没有整片地表被野生作物覆盖。

静态资源和服务器启动通过后，还需要在新生成的客户端世界中执行实际探索测试；已生成的旧区块不会自动补种新加入的野生植物。