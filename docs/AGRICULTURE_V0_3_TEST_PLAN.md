# V0.3 acceptance / regression plan

- Automated: qa/validate_agriculture_assets.py, qa/validate_agriculture_expansion.py, qa/validate_agriculture_ecology.py, qa/validate_bee_biodiversity.py, qa/validate_agriculture_v03.py, existing optional source QA.
- JVM: Java 21, NeoForge 21.1.259, actual Gradle build and dedicated-server startup.
- Plant eight seeds on hydrated farmland, wait for random ticks, collect seeds and mature food drops.
- Check growth in plains, taiga, savanna and jungle. Game biome categories are not soil thermometer readings.
- Create a brand new test world; search plains, meadow, flower forest, savanna in newly generated chunks for rare wild radish/foxtail-millet seed sources. Do not infer absence from a short test due to rarity.
- Confirm mining and sweat bee entities and names at spawn, then observe actual biome spawns and pollination-adjacent crops.
- Existing v0.2 crop inventory, bee entities, beehive kit and older archives must remain unchanged.
- Mine/sweat bees currently reuse vanilla bee hive AI; authentic solitary ground nesting is an explicit follow-up, not passed by registry/spawn smoke.
