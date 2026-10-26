# Wild bee nesting architecture v1.5

The four existing nest habitat registry IDs now render different native
Minecraft block model meshes, replacing the earlier four featureless cubes.
They use *bundled vanilla Minecraft texture references*; no external image
assets are required to test them.

- Bumblebee: low sheltered moss-and-earth mound, recessed entry and visible
  active forage markers.
- Mason bee: tall wooden bee hotel with modeled front cavity openings and
  visible active pollen.
- Squash bee: shallow exposed-soil ground burrow with a dark central entrance
  and activity-specific forage details.
- Leafcutter bee: a cluster of differently sized wooden/stem nesting tubes
  with leaf-accent details.

The model pairs are distinct between activity 0-2 and activity 3 and use
species-specific bounding volumes, so players no longer collide with an
invisible one-block-high cube around a shallow squash-bee nest. Crafted
nests still require appropriate support, and the previous source-inspired
nesting activity/population behavior is preserved. These are approximate
constructed nest microhabitats, **not** full real bee brood or colony
simulations; they deliberately produce no honey.

Reference: Xerces Society nesting resources:
https://xerces.org/pollinator-conservation/nesting-resources
