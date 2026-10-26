# Crop cuisine v1.4 — recipe substitution pass

This pass adds **32 alternate crafting recipes** to the existing agricultural,
vanilla, honey, marine and fungal cuisine. It registers **no new items** and
does not alter existing nutrition values, models, growth systems or older recipes.

## Groups

- Grains and vanilla foods: rice/buckwheat breads, sorghum/buckwheat cookies,
  and an additional honey-sweetened pumpkin pie.
- Vegetables and roots: alternate salads, stews, stir-fries, grain bowls,
  root dishes, cassava flatbread, taro dumplings, and yam cakes.
- Honey and fruit: regional floral honey in sweet-potato dishes, herbal tea,
  fruit salad and mustard dressing.
- Sea and fungi: alternate seaweed salad, sushi rolls, seafood broth,
  mixed mushrooms, truffle pasta and lion's-mane steak.
- Seeds and legumes: vegetable alternatives for bean stew, hummus and
  seed mixes using Minecraft pumpkin seeds.

## Compatibility contract

- New recipes are crafting-shapeless, category `misc`, with three ingredients.
- Only existing registered output items are used. Food properties remain unchanged.
- Each recipe must differ from existing recipes in its actual input matcher,
  including overlaps with custom ingredient tags.
- A source-based CI check verifies ingredients, outputs, category and collisions;
  a normal NeoForge build and server-start smoke test remain required.

This is an alternative-crafting expansion, not an assertion that the game has
new cooking stations or new kitchen mechanics.
