# v1.2 native wild-bee nest-sites

Four persistent habitat IDs now have individual cube-faced Minecraft vanilla-texture models,
an active visual variant at stage 3, and a species-specific limited brood-emergence simulation.

* **Bumblebee:** insulated moss/wood cavity; social species, local cap 4.
* **Mason bee:** wooden/log cavity with hole-facing entrance; local cap 2.
* **Squash bee:** bare soil/rhizosphere burrow with dawn-to-morning scans; local cap 2;
  forage only mature zucchini (*Cucurbita*) rather than cucumber (*Cucumis*).
* **Leafcutter bee:** cavity with leaf-facing entrance; local cap 2.

Game rule: habitat activity rises only when a matching nearby existing bee AND forage are
present. Activity stage 3 can **rarely** spawn one baby of the same bee entity type,
capped by nearby bee counts. A newly placed empty nest does not magically create
the first bee. Nest placement remains in the creative/recipe workflow; nest block
right-click reports activity and nearby bee population. No invented honey resources
for native/solitary bees; native vanilla beehives stay exclusive to honeybees.
No background per-tick search: capped vanilla random ticks only. Nest association
is marked in entity persistent data but there is no implemented pathfinding
homecoming, sealed brood storage, seasonal hibernation or captive bee inventory.

Source:
- Xerces Society, wild bee ground/cavity nesting:
  https://xerces.org/pollinator-conservation/nesting-resources
- Xerces Society, bumblebee colony lifecycle:
  https://www.xerces.org/bumble-bees/about
- USDA NRCS, nest substrate requirements:
  https://www.nrcs.usda.gov/sites/default/files/2023-03/Pollinator%20Fact%20Sheet-508.pdf
