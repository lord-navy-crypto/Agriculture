# Nutrition physiology redesign — Agriculture v3.9.0-rc.1

## Scope and epistemic limits

This feature is a **science-informed fictional Minecraft survival model**.
Scores are abstract 0–100 game reserve indicators, not mg, calories,
blood assays, serum concentrations, percent nutritional sufficiency, diagnosis,
clinical thresholds, real dietary recommendations, or medical treatment.
One Minecraft game day is **not** one real human day. Game depletion,
recovery and shortfall counters are intentionally compressed progression
parameters. Age, sex, genetics, pregnancy, malabsorption, illness and other
clinical factors are NOT modeled. No vitamin toxicity is inferred from a
0–100 score, as the per-food profiles are not doses.

The prior rc.2 feedback incorrectly let unrelated vitamins/minerals cancel
each other through a 5-group average and conferred unearned Night Vision /
Resistance bonuses. That model is deprecated.

## Mechanism

1. Every completed consumption event adds small fictional nutrient points to
   a bounded **per-nutrient daily intake ledger**. Mere possession, starting
   or canceling consumption does not.
2. On the next observed game-day transition, each nutrient independently
   advances its reserve using its own target, daily draw, and replenishment
   rate. The reservoir is capped 0–100, and unused input is not stockpiled.
3. When its reserve is below the *game* watch threshold, one nutrient gains
   a caution status. A persistent, low-reserve, insufficient-intake run is
   tracked independently in days. A sustained status has a
   nutrient-dependent minimum window.
4. Sufficient daily intake raises stores gradually, rather than instantly
   clearing all reserves/streaks. It also progressively reduces the
   sustained-shortage streak. The status disappears on recovery.
5. Existing game world scores migrate unchanged. No immediate legacy
   deficiency is applied. Only one observed day is processed at once, even
   after time commands or offline intervals.
6. Five categories are **read-only explanations** over the individually
   tracked nutrients. A healthy average can never hide low iron or B12.
7. The existing hydration and sodium balance meters exclusively own
   slots 4 and 26. There is no duplicate nutritional sodium effect, and
   ordinary uniodized sea salt is not a source of iodine.
8. The UI presents 27 nutrient roles, 27 warnings, 27 game time windows
   and five summary categories. Serious deviations use only a mild
   fictional Mining Fatigue or Slowness effect for energy/iron/B-vitamin
   related functionality, and never diagnose a specific disease.

## Nutrient-specific research associations

| Indicator | Relevant biological function | Simulation rule |
| --- | --- | --- |
| Carbohydrate | Fuel, glucose metabolism | Relatively short energy reserve |
| Protein | Structural/functional proteins | Tissue maintenance proxy |
| Fat | Essential fatty acids, fat-soluble absorption | Slower energy/lipid reserve |
| Fiber | Normal digestive function | Intake continuity |
| Water | Hydration | Delegated to WaterSaltV37 |
| Vitamin A | Normal rhodopsin-based low-light vision, epithelial integrity | Longer reserve, warning; no magical night vision |
| B1 (thiamin) | Energy metabolism and nerves | Small store, faster depletion |
| B2 (riboflavin) | Cofactor for energy-metabolism enzymes | Shorter reserve |
| B3 (niacin) | NAD-dependent cellular functions | Shorter reserve |
| B5 (pantothenic acid) | Coenzyme A | Shorter reserve |
| B6 (pyridoxine) | Amino acid metabolism/neurotransmitters | Moderate reserve |
| B7 (biotin) | Fat and carbohydrate enzyme functions | Shorter reserve |
| B9 (folate) | DNA synthesis and red-cell production | Medium reserve |
| B12 (cobalamin) | Red-cell production, neurological integrity | Long reserve and longer latency |
| Vitamin C | Collagen and tissue healing | Shorter reserve |
| Vitamin D | Calcium/phosphate homeostasis, skeleton | Longer reserve |
| Vitamin E | Antioxidant cell-membrane protection | Longer reserve |
| Vitamin K | Coagulation protein activation | Moderate reserve |
| Calcium | Skeleton and neuromuscular functions | Longer mineral reservoir |
| Iron | Hemoglobin and oxygen transport | Phased reserve depletion |
| Magnesium | Enzymes and nerve/muscle functions | Intermediate reserve |
| Potassium | Membrane potential and neuromuscular function | Shorter reserve |
| Zinc | Enzymes, immune response, repair | Medium reserve |
| Iodine | Thyroid hormone synthesis | Longer reserve |
| Selenium | Selenoproteins, thyroid enzymes | Longer reserve |
| Phosphorus | Bone mineral and ATP | Longer reservoir |
| Sodium | Fluid/nerve homeostasis | Delegated to WaterSaltV37 |

**Important**: Clinical interactions such as iron status and infection,
or vitamin D / calcium / phosphorus balance are described as relationships.
The program does not pretend to diagnose anemia, rickets, scurvy,
neurodegeneration, or endocrine disease from game scores.

## Medical / nutrition evidence consulted

1. WHO, Micronutrients overview:
   https://www.who.int/health-topics/micronutrients
2. WHO, Healthy diet:
   https://www.who.int/news-room/fact-sheets/detail/healthy-diet
3. NIH ODS, List of vitamin/mineral fact sheets:
   https://ods.od.nih.gov/factsheets/list-VitaminsMinerals/
4. NIH ODS, Thiamin:
   https://ods.od.nih.gov/factsheets/Thiamin-HealthProfessional/
5. NIH ODS, Vitamin B12:
   https://ods.od.nih.gov/factsheets/VitaminB12-HealthProfessional/
6. NIH ODS, Iron:
   https://ods.od.nih.gov/factsheets/Iron-HealthProfessional/
7. NIH ODS, Calcium:
   https://ods.od.nih.gov/factsheets/Calcium-HealthProfessional/
8. NIH ODS, Vitamin A:
   https://ods.od.nih.gov/factsheets/VitaminA-HealthProfessional/
9. NIH ODS, Vitamin C:
   https://ods.od.nih.gov/factsheets/VitaminC-HealthProfessional/
10. NIH ODS, Vitamin D:
    https://ods.od.nih.gov/factsheets/VitaminD-HealthProfessional/
11. NIH ODS, Folate:
    https://ods.od.nih.gov/factsheets/Folate-HealthProfessional/
12. NIH ODS, Iodine / non-iodized sea salt:
    https://ods.od.nih.gov/factsheets/Iodine-HealthProfessional/
13. National Academies, interpretation of DRI/EAR/RDA/AI/UL:
    https://nap.nationalacademies.org/resource/25353/interactive/

## Acceptance tests not yet demonstrated by source checks

- Real NeoForge client with GUI scales 2–4 and hover tooltips,
  narrow window, translated labels, scroll and page controls.
- Save migration from v3.7 and v3.8, positive and negative boundaries.
- Food-specific profile plausibility against USDA FoodData Central or a
  mapped recipe ingredient dataset: **the existing 403 records are
  editorial game categories, NOT independently verified food chemistry**.
- Multiplayer behavior and third-party mod / data pack interactions.
