# Gugu-Gaga Horror — 神圣设定档案 / Canon & Long-Term Development Archive

**Document type:** Long-term creative archive. **Code target:** Minecraft 1.21.1, NeoForge 21.1.259, Java 21.
**Branch intent:** v0.8 *three character interaction prototype*, NOT quest/ending implementation.
**Privacy and safety:** All features stay inside Minecraft. No forced chunk generation, OS/web access, real administrator permission transfer, surprise save modifications, or copied third-party assets.

## 0. 读档规则：既有事实、创作提案与代码状态

- **Confirmed user canon**: the user's agreed fictional world rules and long-term planned story.
- **Implemented in earlier alpha**: what exists in code on the parent v0.7 branch (not necessarily field tested).
- **v0.8 implementation**: two new entity registrations with local encounters and three-character reactions, new original placeholder visual assets, and tests.
- **Planned, NOT implemented**: chapter scripts, ending conditions, full battle/portal, world structures, faithful original reference artwork, boss assets, and everything marked FUTURE.
- The user's three **character identities** are **Gugu-Gaga**, **the Redactor** (Lapis disguise and true form belong to the SAME antagonist), and **the Yellow Smiler**. The fourth participant is a vanilla **Player**. The in-game Redactor uses one EntityType with two form states, not a gratuitous fourth character.
- Original "A reference" for Gugu-Gaga must remain the canonical target. Current Minecraft model/texture is an acknowledged original **placeholder**, not definitive faithful fan imagery. Don't replace the primary A with an arbitrary generic penguin.

## 1. 三重不可靠叙事

1. **First appearance**: the strange cold-loving penguin Gugu-Gaga seems to be the hostile horror.
2. **Second layer**: Lapis, a seemingly friendly lapis-lazuli cube/person repeating "Lapis", is actually a guise of the Redactor, who corrupts Gugu-Gaga and manipulates evidence. The Redactor actively **fears the Yellow Smiler** and avoids it.
3. **Third reversal**: a blocky yellow NPC with a permanent friendly smile hands out hints/items and urges the player to defeat Gugu-Gaga and the Redactor; this Yellow Smiler is secretly the final manipulator. Gugu-Gaga initially does not know the Smiler's true nature; its loyalty remains possible even after being corrupted.
4. The true target of the Redactor is **Gugu-Gaga first**, not the player. The Smiler's behavior toward the player stays welcoming early in the game, whereas toward the Redactor it is quietly threatening.
5. The Redactor can know a clue about the Smiler's higher rule-level ambition, without becoming a moral hero. Gugu-Gaga can be upset, attack if the server owner opted in, recover and cooperate; it is not irreversibly evil.

## 2. 三实体基础行为规范（当前 v0.8 开发重点）

| Subject | Nearby player | Nearby Gugu-Gaga | Nearby Redactor/Lapis | Nearby Yellow Smiler |
|---|---|---|---|---|
| Gugu-Gaga | existing observe/approach/retreat, offline/local dialogue, mood | — | pauses, faces threat, may become temporarily corrupted | suspicious pause but does NOT magically know it is the villain |
| Redactor, Lapis mask | friendly idle, soft "Lapis" on cooldown | locks attention on Gugu-Gaga; becomes Redactor true form at close proximity | — | absolutely prioritizes **fleeing**; no attacks or corruption while escaping |
| Redactor, revealed | reserved/uncanny, no automatic player assault | approach and induce bounded reversible corruption after cooldown | — | flee immediately, temporarily unable to corrupt |
| Yellow Smiler | initially benevolent, never attacks in base interaction prototype | turns toward Gugu-Gaga and observes it; future manipulation, no forced harm | causes Redactor to flee | — |

**Priority order (non-negotiable):**
1. Server safety, loaded-only scans, no chunk tickets / griefing / involuntary commands.
2. Player's quiet preference and creative/spectator exclusion for hostility.
3. Redactor sees Yellow Smiler -> FLEE and cancel corruption.
4. Redactor sees Gugu-Gaga -> REVEAL (when close) and bounded CORRUPT.
5. Yellow Smiler -> WATCH Gugu-Gaga and behave politely toward players.
6. Gugu-Gaga -> preserve existing core state machine when neither threat is present.

## 3. 角色素材、语言和 IP 边界

**Lapis / Redactor**:
- Same logical Redactor creature has a short blue-lapis-looking blocky mascot disguise, and a taller long-limbed dark teal/ink/bone-white true form. Both **entirely original UV textures/models**.
- "Lapis" is a short fictional in-game line / general Minecraft lapis mineral word and a loose meme nod; no Speed recordings, voice samples, actual video frames, or exact mascot artwork.
- True Redactor wears an uncanny fractured face/mask rather than being a copied Lovecraft character or Demon Slayer villain. No bloody imagery needed. AI: track Gugu-Gaga, reveal, corrupt, flee from Yellow.
- No actual world blocks are substituted with fake lapis. Lapis form is an entity, so miners' chests/structures and player property are safe.

**Yellow Smiler**:
- Blocky yellow cuboid person, simple perfectly regular smiling face, low-key unnatural slow movements, visually distinct from any existing Verity asset. The yellow Smiler is NOT based on downloaded exact Verity skin.
- Friendly at first; gifts/items/commands, betrayal and creator ascension are FUTURE story scripts. Current proto uses no real item transfer or world permission.
- Light does not strobe. Smiling should feel too permanent/overly symmetric, not slapstick.

**Gugu-Gaga**:
- The strange A-identity penguin is retained. Fear through stillness, delayed looks and skewed wing posture rather than replacing the face with generic glowing red eyes.
- Strong **cold-biome preference** implemented on earlier branch; Minecraft base temperatures are relative scores, not degrees C.

## 4. Long-term planned five endings — no v0.8 automatic ending triggers

**A — The Perfect Smile / 黄色微笑者终极胜利**
Player trusts Smiler, mistakenly eliminates the witnesses, and Smiler's last betrayal succeeds.

**B — The False Victory / 虚假的胜利**
Player allies with Gugu-Gaga and defeats Redactor, but the smiling supposed ally turns on them at the celebration.

**C — The Last Witness / 真正的好结局**
Player and penguin first **speak** with the Redactor instead of destroying it. Redactor divulges Yellow's plan. Yellow destroys Redactor, attempts to eliminate witnesses, but player and penguin together defeat **Yellow Smiler**. (Redactor was already defeated by Yellow; it is not the final opponent in this ending.)

**D — True Lapis / 真正的平静**
A voluntary sanctuary: player's active residence and legitimate bed/spawn point in a high-air refuge (design altitude 32–40 blocks above desert terrain or warm ocean water) keeps **special** terror encounters paused within sanctuary. On leaving, previous story resumes **without immediately spawning an attack**, no forced banishment or confinement. IMPORTANT: warm ocean biome Minecraft base temperature is not necessarily desert-hot, so use **explicit biome IDs** (`minecraft:desert`, `minecraft:warm_ocean`) rather than the general cold-temperature rule.

**E — Beyond the Creator / 超越创造者, secret fourth-wall homage**
Opt-in, story-specific cinematic triggered by deliberately interacting with a **prop** command block, NOT by routine Creative Mode or a player's regular operator actions. Smiler notices it, executes a simulated light-beam/cinematic, visually imitates a white-eyed Steve-like character (homage, not literal Herobrine actor), learns that countless worlds seem to exist and steps into a drawn **in-world portal texture**. It does not actually read user saves, versions, Zelda, Genshin, GTA, the computer or any external files, and never gains Minecraft server permissions. Spectator-like perspective is **reversible** and must restore on disconnect/relog. A fallback skip/exit must exist. Dialogue/cutscene are fictional; no actual arbitrary command execution or stolen item data.

## 5. Previous development record and engineering decisions

**v0.1–v0.2**: server-side four-phase bird brain, original prototype visual, opt-in Nightwatch nighttime director with 8-minute hard minimum cooldown, player quiet opt-out, loaded-only entity placement; local `127.0.0.1` Ollama shadow-mode adviser (off by default).

**v0.3**: observer's head/eye/wing pose, quiet directional vanilla sounds and small retreat particles; temporary auto encounters, reduced A* recalculation frequency; manual summons persist.

**v0.4**: personal, erasable three-bit Witness Journal (gaze, footsteps, retreat); English and Chinese localized entries; no location histories.

**v0.5**: macro six-event suspense director (silence/chime/false steps/echo/afterimage/sighting), gentle/balanced/eerie styles, session-only tension and bounded online records.

**v0.6**: bilingual fictional `/gugugaga talk` offline baseline; explicitly opted-in Ollama local Q&A with sanitized private input and bounded output; low sighting odds in hot biomes and high in frozen climates.

**v0.7**: non-permanent CALM / ANGRY / CORRUPTED bird moods, server-owner hostility opt-in, limited telegraphed Minecraft melee damage, voluntary guardian/redactor/neutral story allegiance and a suspenseful Redactor canon (without a spawned Redactor model).

**v0.8 NOW**: documents-first, three EntityType prototypes incl. Redactor two forms and Yellow Smiler, rendered original placeholder models/textures, local interaction matrix. No full storyline/quests/ending triggers.

## 6. Feature backlog and development-method commitments

- [ ] Truthfully label proposed vs compiled vs tested-in-game; green GitHub Actions is NOT proof of 60-minute client or dedicated-server stability.
- [ ] Original final A reference verification, concept approval, sprite/model fidelity review.
- [ ] Redactor Lapis gentle original 'Lapis' sound recorded or synthesized **without copying creator audio**.
- [ ] Yellow smile mask animations and redactor fractured reveal choreography, no harsh flashes.
- [ ] Source-driven world events: biomes/ruins/ice, ARG impossible wall as **render-only** illusion, no real block destruction.
- [ ] Chapter quests, choices, optional peaceful resolution, trusted/inaccurate witness evidence, replayable five endings.
- [ ] Smiler ally rewards only through explicit user consent; limits/no exploit, no item duplication.
- [ ] Safe high-air sanctuary implementation with explicit biome/bed checks; no forced player confinement.
- [ ] Optional final cinematic E, fake in-world command block prop, no real /gamemode changes, never gain OS/server admin access.
- [ ] Server and client profiling: 2–8 player sessions, no lag spikes, no chunk tickets, reduce per-AI tick scanning via 10/20 tick periodic target scans, per-entity cap.
- [ ] Data version/migration across alphas, entity persistence and visual sync tests after relog.
- [ ] Accessibility: easy opt-out and ambient mute, no flashing, no intentionally overwhelming audio.
- [ ] Original assets, author provenance, no unauthorized copyrighted music or characters.

## 7. Reproducible test harness (v0.8 scope)

- Same temporary world, manual entity summon: `/summon gugugaga:observer`,
  `/summon gugugaga:redactor`, `/summon gugugaga:yellow_smiler`.
- Verify Redactor initially blue Lapis guise; approaches Gugu; reveals if near; temporarily corrupts Gugu. Verify Redactor flees whenever Smiler nearby even when Gugu is near; Gugu maintains prior behavior when isolated; Smiler observes Gugu and does not attack players.
- No automatic Redactor or Yellow spawning before explicit tested pacing is available.
- Verify texture resources for both Redactor forms and Smiler in compiled JAR, including dedicated-server class separation.
- Automated pure-Java interaction-priority unit tests and full NeoForge compile. Perform in-game visual/multiplayer testing before merge.

**Status reminder:** This document archives intentional long-term canon. It does NOT claim every mentioned feature is implemented.
