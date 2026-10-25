# Gugu-Gaga — microbes IN the Minecraft world

This is playable in-world development, not an encyclopedia extension. The experimental Java 21 / NeoForge 1.21.1 branch introduces four registered real-taxon-themed microhabitat blocks with native blockstate visuals (three variants each), block items, creative inventory, drops, right-clicking and Chinese/English translations.

The first two contexts are decorated into **newly generated chunks** using NeoForge biome modifiers: Bacillus subtilis as stylized soil microhabitat art on grass/dirt in plains, meadow, savanna; Lactiplantibacillus plantarum as stylized plant-associated art on exposed forest leaf surfaces. E. coli intestinal-host context and T4 phage–host relationship are **placeable creative exhibits only**. A biome or animal nearby never establishes pathogen infection. All taxonomy is grounded in the existing NCBI-sourced NaturalMicrobeCatalog.

Right-clicking switches three visible VIEW styles by changing a genuine server-owned world blockstate; these are NOT growth stages, infection stages, disease severity, actual microbial abundance or real transmission probabilities. Decorative rarity numbers are game design values, not epidemiology. Microbes are microscopic; macro-size shapes are stylized artwork built from temporary vanilla textures.

No client/server custom packet, forced chunk generation, player health alterations, real laboratory methods, epidemiology parameters, infection or auto-spreading are included.

CI checks test JSON, models, loot, translations and registering code; Gradle compilation, actual dedicated-server and graphical-client startup must pass independently. Bootstrap does NOT prove fresh world generation or clicking behavior. Manually explore brand-new terrain in a disposable world and take screenshots before promoting.

Next: original microbe sprites, natural cave/water habitat content, environmental mechanics, in-game observation tools and coexistence with passive animals (without misdiagnosing them), cross-mod compatibility and multiplayer stress tests. The former One Health atlas is **secondary**.


## In-world ecological visual connection

Neighboring creative-placeable T4 and E. coli exhibit blocks render a small native ENCHANT particle cue only when the documented species-level host association is present. This is a rendered classroom-style association, NEVER evidence that phage infection or bacterial lysis actually occurred. Natural soil/plant patches get budget-limited happy-particle visuals when in camera range. The particle random draw is an aesthetic budget, not a disease probability; no surrounding chunks are loaded to test neighbors.


## Lifecycle and bounded expansion (second implementation)

Game rules are independent of clinical or laboratory biology. Vanilla block random ticks advance the soil/plant visual patch through three frames. Once a mature original reaches its final visual frame, it can create at most **one adjacent satellite** on the same compatible substrate, and both blocks are marked `satellite=true` (the child is non-branching). A failed placement does not force a chunk or generate a new one. Repeated patches therefore cannot chain-expand without a new player-placed/world-generated origin. E. coli and T4 exhibits **never** create offspring blocks; when the documented T4/E. coli association is represented by physically adjacent exhibits, they cycle a purely symbolic display and otherwise revert to the idle frame. This does **not** assert infection, strain susceptibility, viral replication rates, bacterial growth rates or public-health consequences.

Server config `microbe_ecology.enabled` and `microbe_ecology.one_satellite_per_patch` let server owners disable art changes or extra patches without affecting item registrations or scientific records. Substrate checks and neighbor scans are strictly loaded-chunk-only. `MicrobeLifecyclePolicy` is separately tested using pure Java 21. The feature uses no time-based epidemiological coefficients, player diseases, destructive block modifications or independent server tick loop.
