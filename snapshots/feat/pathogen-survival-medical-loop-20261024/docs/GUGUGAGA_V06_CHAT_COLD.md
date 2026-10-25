# Gugu-Gaga v0.6 — Chat and Penguin Climate

This feature builds on the v0.5 suspense director. It is a new alpha feature.

## Private character conversation

Players voluntarily type commands such as:

```text
/gugugaga talk 你好，咕咕嘎嘎
/gugugaga talk 你是谁
/gugugaga talk Do you like snow?
/gugugaga talk status
```

Without Ollama, Gugu-Gaga replies in Chinese or English with a small
deterministic, journal-aware persona. Normal multiplayer chat is not
intercepted. Each player can ask once every eight seconds, with a 160-character
input limit. Replies are displayed privately.

## Optional server-local Ollama

The operator explicitly opts in to both switches:

```text
/gugugaga ai detect
/gugugaga ai model <installed-model-name>
/gugugaga ai on
/gugugaga talk ai on
```

The model lives on the Minecraft SERVER host at localhost 127.0.0.1:11434.
Only text deliberately entered in the talk command is transmitted to that
local model. Chat history, player names, movement coordinates and inventories
are not sent. Model suggestions are displayed as short fictional text, never
executed as Minecraft commands. Ollama absence falls back to offline rules.
Turn off using `/gugugaga talk ai off`.

## Penguin cold-climate preference

The Nightwatch director already required an operator opt-in and enforced
nighttime, overworld survival, player quiet opt-out, and strict mob caps.
v0.6 additionally checks the currently loaded player's biome base temperature.

Conditional probabilities of a REAL SIGHTING, after the old director selects
that kind of event:

| Biome base temperature | Conditional sighting chance |
|---|---:|
| <= 0.0 | 95% |
| <= 0.3 | 82% |
| <= 0.8 | 50% |
| <= 1.3 | 16% |
| <= 1.8 | 4% |
| > 1.8 | 1.5% |

These are Minecraft relative biome temperature values, NOT Celsius and NOT
standalone spawn rates. The event scheduler, cooling time, mob counts and
loaded-chunk-only rules are unchanged. Forcing a longer night doesn't produce
more frequent events than the previous version.

## Design references

Original Modrinth/CurseForge descriptions of Evil Hunter, wonderland.jar and
Gonebutnotforgotten INFINITY confirm layered storytelling, pursuits,
investigation notes and eerie locations. We only borrow general pacing design,
not their code, character models, art, audio or story. "Verity" could not be
confidently matched to a verified official Minecraft mod listing at research
time; direct player-to-creature conversation is implemented independently.

Minecraft 1.21.1, NeoForge 21.1.259, Java 21.
Build success does not replace an actual client and dedicated-server test.
