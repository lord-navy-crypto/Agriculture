# Agriculture v3.10 — 27-indicator potion-effect matrix

## Player-facing behavior

Every established indicator has an explicit positive and negative Minecraft effect mapping. These effects are **gameplay metaphors**; the mod does not claim that a nutrient directly grants a superpower or that a potion effect reproduces a medical symptom.

- Adequate reserve: score >= 65 and no low-reserve state. Positive effect, 220 ticks.
- Watch state: reserve < 45. Corresponding negative effect, 80 ticks as a brief warning pulse.
- Sustained low state: reserve < 32 for the nutrient's configured game-day latency. Corresponding negative effect, 220 ticks.
- Neutral range: reserve 45–64. No potion effect.
- Simultaneous cap: five distinct positive effect types and four distinct negative effect types. The strongest reserves and most severe/longest deficits are prioritized; duplicate effect types are collapsed.
- Water and sodium: read from WaterSaltV37. Their positive mappings are shown in the effect matrix; their low-state penalties remain owned by WaterSaltV37 and are never duplicated. Severe water/sodium imbalance suppresses additional nutrition effects.

## Complete mapping

| # | Indicator | Positive effect | Low-reserve effect | Gameplay analogy |
|---:|---|---|---|---|
| 1 | Carbohydrate | Speed | Slowness | Energy and movement |
| 2 | Protein | Strength | Weakness | Physical capacity |
| 3 | Fat | Absorption | Hunger | Energy reserve |
| 4 | Fiber | Regeneration | Hunger | Digestive-support proxy |
| 5 | Water | Dolphin's Grace | Slowness | Hydration and movement; low state handled by water system |
| 6 | Vitamin A | Night Vision | Darkness | Normal vision support |
| 7 | Vitamin B1 | Haste | Mining Fatigue | Energy metabolism / work capacity |
| 8 | Vitamin B2 | Speed | Slowness | Energy metabolism |
| 9 | Vitamin B3 | Resistance | Weakness | Cellular metabolism |
| 10 | Vitamin B5 | Haste | Mining Fatigue | Coenzyme A / metabolism |
| 11 | Vitamin B6 | Strength | Weakness | Amino-acid and neurotransmitter metabolism |
| 12 | Vitamin B7 | Haste | Mining Fatigue | Metabolic enzyme support |
| 13 | Vitamin B9 | Absorption | Weakness | DNA synthesis / red-cell support |
| 14 | Vitamin B12 | Health Boost | Weakness | Nerve and red-cell support |
| 15 | Vitamin C | Regeneration | Weakness | Collagen and tissue repair |
| 16 | Vitamin D | Jump Boost | Weakness | Bone/mineral support |
| 17 | Vitamin E | Absorption | Weakness | Cell-membrane protection |
| 18 | Vitamin K | Resistance | Weakness | Coagulation-protein support |
| 19 | Calcium | Resistance | Weakness | Bone and neuromuscular support |
| 20 | Iron | Speed | Slowness | Oxygen-transport capacity |
| 21 | Magnesium | Haste | Mining Fatigue | Enzyme and neuromuscular support |
| 22 | Potassium | Jump Boost | Weakness | Electrolyte / neuromuscular support |
| 23 | Zinc | Regeneration | Weakness | Immune and tissue-repair support |
| 24 | Iodine | Speed | Slowness | Thyroid-hormone synthesis support |
| 25 | Selenium | Resistance | Weakness | Selenoprotein support |
| 26 | Phosphorus | Strength | Weakness | ATP and bone-mineral support |
| 27 | Sodium | Jump Boost | Mining Fatigue | Electrolyte balance; low state handled by water/salt system |

## Why the game does not copy clinical symptoms literally

WHO describes micronutrient deficiencies as affecting many biological processes and notes that iron, folate, B12 and vitamin A deficiencies can contribute to anemia. NIH ODS fact sheets describe distinct roles for vitamins and minerals. Those sources do **not** establish that ordinary food-level changes should cause immediate Minecraft status effects. Thus:

- No instant illness when one meal is missed.
- No Poison, Wither, damage-over-time, or forced health loss.
- No assumption that high food-derived score means toxic overdose. Food profile points are not measured mg/µg.
- No magical Night Vision as a medical claim; it is a bounded game reward and Darkness is a mild visual warning analogue.
- Hydration and sodium continue to use the separate water/salt model.

## References

- WHO micronutrients overview: https://www.who.int/health-topics/micronutrients
- WHO healthy diet: https://www.who.int/news-room/fact-sheets/detail/healthy-diet
- NIH ODS vitamins and minerals fact sheets: https://ods.od.nih.gov/factsheets/list-VitaminsMinerals/
- NIH ODS nutrient upper limits and excess-risk overview: https://ods.od.nih.gov/HealthInformation/ODS_Frequently_Asked_Questions.aspx
- Minecraft Java status-effect identifiers and behavior: https://minecraft.wiki/w/Effect
- Minecraft Java effect identifier reference: https://theminecraftwiki.com/wiki/java-edition-data-values/effects/

