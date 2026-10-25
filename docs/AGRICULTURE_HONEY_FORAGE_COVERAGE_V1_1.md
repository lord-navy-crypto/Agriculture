# Agriculture honey forage coverage v1.1

## What changed

Floral honey classification now accounts for the entire expanded crop catalog rather than relying on a short whitelist of miscellaneous nectar crops.

When a managed vanilla beehive is harvested, the mod still scans a bounded local area and keeps the existing rules:
- at least six nearby flowers of one known source;
- the dominant source must make up at least two-thirds of counted forage to yield that source's specialty honey;
- sufficiently diverse mixed forage can yield wildflower honey;
- sparse, unknown, or single-source forage falls back to vanilla honey.

## Crop coverage

Eight crops have a dedicated specialty-honey route already: clover, lavender, buckwheat, sunflower, blackberry, blackcurrant, chives, and pumpkin blossom.

Eleven grain crops are explicitly excluded from nectar counts in this gameplay model because they are primarily wind/self-pollinated: corn, sweet corn, rice, barley, oat, rye, sorghum, millet, teff, quinoa, and amaranth.

Every other mature AgricultureCropBlock is counted as potential nectar forage by default. This means the new cassava, taro, yam, watercress, endive, kohlrabi, and rutabaga crops can contribute to mixed forage, and a large vegetable plot can no longer be ignored while a tiny patch of one specialty flower incorrectly dominates the honey result.

## Scope and realism

Mature crop stages are used as a lightweight stand-in for flowering; the mod does not simulate real flowering calendars, nectar volume, bee flight paths, or laboratory monofloral certification. The change improves in-game coverage without adding per-tick scans, background tasks, or chunk loading. The harvest-time scan remains bounded and only checks already-loaded chunks.

## Regression coverage

The qa/validate_honey_source_coverage_v1_1.py script verifies all 80 crop profiles are unique, specialty-source crops and non-nectar exclusions exist in the catalog, new crop lines reach the generic forage path, and the data description stays aligned with the code.
