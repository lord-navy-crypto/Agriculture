# Native compostability across all 64 crops

NeoForge 1.21.1 replaces `ComposterBlock.COMPOSTABLES` with the reloadable
item data map `neoforge:compostables`. A single
`data/neoforge/data_maps/item/compostables.json` adds **all 64 existing seed
items (30%)** and **all 64 harvest items (65%)**.

This allows ordinary player-operated vanilla composters to accept the organic
contents of this farming module. Villager composting is explicitly disabled
to avoid unwanted farming economy changes. The percentages are vanilla-like
Minecraft balancing constants, not scientifically measured compost rates.

Source: https://docs.neoforged.net/docs/1.21.1/resources/server/datamaps/builtin/
