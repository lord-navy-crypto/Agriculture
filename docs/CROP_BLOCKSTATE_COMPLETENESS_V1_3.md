# 64 fully defined crop models: age and pollination state

Each crop block registers both `age` (0..7) and `pollinated` (false/true).
Previously the asset variants only explicitly listed 8 age-only keys. Now
**all 64 crops list all 16 property combinations** with a model for each.

Visual model geometry still reflects maturity stage and reuses original
texture placeholders; pollination remains invisible but modifies mature
harvest yield. Both state combinations share the same appropriate age model.

Resource QA now requires the complete set for every old and new crop,
and checks each mirrored pair in the v1.3 acceptance test. No registry ID
or existing loot table was deleted during this compatibility repair.
