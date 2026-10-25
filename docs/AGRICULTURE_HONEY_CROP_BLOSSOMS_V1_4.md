# Floral honey v1.4 — 96-crop forage coverage

The v1.1 honey-source catalog contains **46 bottle variants**: 45 recognized source-specific routes and one neutral wildflower blend. v2.0 adds eight crop-source routes: celeriac, purslane, chayote, grape, tea, coffee, rapini and napa cabbage.

Each bottle has a registered item, a generated 16×16 nectar-colored sprite, a matching item model, bilingual display/effect text and a short level-I fantasy gameplay effect. These effects are fictional balancing choices, not claims about the effects of real honey or pollen.

The forage scan still samples a bounded area around a harvested hive and skips unloaded chunks rather than generating terrain. A source-specific bottle requires at least six samples from a recognized source and at least a two-thirds share of the recognized-plus-other-flower count. If no source dominates, sufficiently diverse mixed forage can yield neutral wildflower honey; sparse or uncertain forage falls back to vanilla honey. The thresholds were not changed in v1.1.

## Validation

The CI contract checks that all 46 data rows match Java food/effect metadata, item registrations, source routing, item models and bilingual labels. A separate generator check requires 46 distinct 16×16 bottle sprites. Vanilla suspicious-stew flower effects remain unchanged; crop-specific effects are explicitly marked as mod-original fantasy effects.
