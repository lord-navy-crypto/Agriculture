# Gugu-Gaga Horror v0.9 — 三角色独特机制与模型强化（实验）

Status: Alpha behavioral prototype, on top of v0.8. The five endings, Smiler
boss battle and fourth-wall cinematic remain long-term archive only.

## 核心机制一：咕咕嘎嘎 / Corruption Arc

- The existing v0.7 `CORRUPTED` mood now has four reproducible stages,
  observed through a synchronized bounded integer on the mob: 0 CLEAR,
  1 FRACTURED after 8 seconds, 2 POSSESSED after 35 seconds,
  3 BLACKENED after 75 seconds.
- BLACKENED uses an additional ORIGINAL dark penguin texture, keeping the
  same memorable eyes/beak/silhouette. Asymmetry slowly increases;
  no strobing, camera grab, huge sound spikes or player permission changes.
- Original corruption expires automatically at 110 seconds, restoring
  normal model/texture and player behavior. Player reassurance can still
  calm the bird instantly (after existing journal requirements).
- `/gugugaga blacken` (OP) previews BLACKENED state, with automatic recovery.
- Player damage remains governed ONLY by preexisting
  `/gugugaga hostility on` (server default OFF).

## 核心机制二：Lapis / 遗忘校对者：伪装、显露、有限攻击

- ONE Redactor EntityType, TWO synced visuals. Mask is blue Lapis entity,
  not a fake/real placed ore block.
- When alone, it may quietly say "Lapis". When Gugu-Gaga is close,
  reveals and preferentially corrupts Gugu. If a valid survival player
  is within 4.5 blocks and has line of sight, it can reveal near them.
- If the Redactor is REVEALED and an operator explicitly enabled the
  separate `redactor_mimic.redactor_hostility` setting, an eligible
  unmuted survival player can be struck for **1 Minecraft damage**
  at **<= 2.4 blocks** only after **40 ticks warning**, and no more
  often than every **100 ticks**. Does not break blocks or force chunks.
- Server setting defaults OFF. Lapis MASK CANNOT HIT. All aggression
  immediately halts whenever any Yellow Smiler is within its v0.8
  fear range, even if the target player is right next to it.
- Operator `/gugugaga mimic reveal`, `mimic disguise`,
  `mimic hostility on|off|status` provide reproducible local testing.
- Revealed Lapis remasks after a calm interval without guardians or
  interested nearby players. Face/skin state is saved in vanilla NBT.

## 核心机制三：黄色微笑者 / Aid & Unmasking

- Yellow stays visually friendly, still and nonviolent when masked.
- A survival player who **right-clicks** the masked Smiler can receive
  **2 vanilla torches** with a **global per-player cooldown of
  180 seconds**. Inventory-full players are told to make room.
  This is a small fictional demonstration of how the future villain
  earns trust. No free unsolicited gift rain, duplicated stacks or
  scanning of normal chat. It stores only the last-help world tick.
- A valid player's in-game attack on Yellow reveals its sinister form,
  or an operator may use `/gugugaga smiler reveal|mask` to preview it.
- The revealed form has a unique original faded-yellow/ink-black
  texture and slow asymmetrical pose. It stops giving gifts and
  faces Redactor, which already prefers flight from ANY Yellow form.
- It **does not yet** launch its final boss fight, steal permissions,
  spawn portals, kill players or implement the five narrative endings.

## Performance, safety, rollout

- No automatic new Redactor/Yellow spawning yet; manually summon.
- Existing local entity scans remain at most once every 20 ticks.
- Independent on/off for penguin hostility vs Redactor attacks,
  unchanged player quiet and creative/spectator protections.
- Same save keys for old worlds; new fields are default-safe and loaded
  without instant attack. No real-world personal info or external requests.
- New generated textures `observer_blackened.png`,
  `yellow_smiler_true.png` in addition to v0.8's three original assets.
  No Verity, Speed, Herobrine, other mod artwork or music copied.
- Code and pure-policy tests compile on CI; real game testing is still
  required before this alpha can merge onto main.

## Manual 10-minute test protocol

1. Fresh NeoForge 1.21.1 Java 21 test world with older v0.8 JAR REMOVED.
2. `/summon gugugaga:observer ~ ~ ~` then `/gugugaga blacken`:
   verify dark texture and asymmetric head/wings. Wait ~35 sec, verify
   it recovers; `hostility off` should guarantee no player strike.
3. `/summon gugugaga:redactor ~ ~ ~` in open area. Observe safe
   Lapis disguise and uncover by approaching or with `/gugugaga mimic reveal`.
   Without separate hostility ON it should never damage players.
4. Enable `/gugugaga mimic hostility on` and keep a 3-block distance;
   only a short-distance attack with visible/audio forewarning is allowed.
   Switch OFF and confirm immediate cessation.
5. Place Yellow within ~24 blocks of Redactor:
   Redactor must prioritize fleeing even if player/Gugu are nearby.
6. `/summon gugugaga:yellow_smiler ~ ~ ~`: in survival, right-click
   to receive exactly two torches. Repeat immediately: no duplication.
   Test full inventory (no resource consumed), 3-min cooldown and
   `/gugugaga smiler reveal`/ `mask`.
7. Test reveal by striking Smiler in a throwaway world: its complexion
   and pose change; gifts are disabled; no forced damage to players.
8. Test save/reload, dedicated NeoForge server and resource packs,
   especially mask/true textures and old pre-v0.9 saves.

### Known blockers / later improvements
Actual renderer footage, named sound bank, three-mob collision box tuning,
data-migration integration test, gameplay QA with 2–8 clients, Redactor's
own unique custom animation sound, and Smiler's story-driven full reveal
are still not validated or delivered. Keep v0.9 as a DRAFT PR.
