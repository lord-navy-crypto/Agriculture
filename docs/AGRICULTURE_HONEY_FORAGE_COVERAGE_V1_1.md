# Agriculture honey forage coverage v1.1

## What changed

Floral honey classification now accounts for the entire expanded crop catalog rather than relying on a short whitelist of miscellaneous nectar crops.

When a managed vanilla beehive is harvested, the mod still scans a bounded local area and keeps the existing rules:
- at least six nearby flowers of one known source;
- the dominant source must make up at least two-thirds of counted forage to yield that source's specialty honey;
- sufficiently diverse mixed forage can yield wildflower honey;
- sparse, unknown, or single-source forage falls back to vanilla honey.

## Crop coverage

Fifteen crops now have dedicated specialty-honey routes: the original clover, lavender, buckwheat, sunflower, blackberry, blackcurrant, chives, and pumpkin blossom sources, plus cassava, taro, yam, watercress, endive, kohlrabi, and rutabaga blossoms.

Eleven grain crops are explicitly excluded from nectar counts in this gameplay model because they are primarily wind/self-pollinated: corn, sweet corn, rice, barley, oat, rye, sorghum, millet, teff, quinoa, and amaranth.

Every other mature AgricultureCropBlock is counted as potential nectar forage by default. The seven new crops now also have dedicated honey routes, so their mature blossoms can yield identifiable specialty bottles rather than being treated only as generic mixed forage.

## Scope and realism

Mature crop stages are used as a lightweight stand-in for flowering; the mod does not simulate real flowering calendars, nectar volume, bee flight paths, or laboratory monofloral certification. The change improves in-game coverage without adding per-tick scans, background tasks, or chunk loading. The harvest-time scan remains bounded and only checks already-loaded chunks.

## Regression coverage

The qa/validate_honey_source_coverage_v1_1.py script verifies all 80 crop profiles are unique, specialty-source crops and non-nectar exclusions exist in the catalog, new specialty crop lines reach the dedicated route and item-effect path, and the data description stays aligned with the code.
