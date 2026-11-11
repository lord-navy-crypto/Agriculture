# Level 2–5: First-Ring Theme Discovery and Walkable Galleries (v4.10)

This iteration builds on v4.8 solid foundations/roofs and the v4.9
level-specific room grammars. It does not alter Levels 0/1 or nutrition.

## Changes
- Level 2 (utility halls): raised iron service decks now use real
  south-facing stone-brick stairs rather than one-block jumps. Two raised
  guardrails border the gallery; its central corridor beneath stays clear.
- Level 5 (Beverly ballroom): the dark oak upper balcony now has a five-step
  spruce stair approach and a guardrail along the open edge. Original
  chandelier, lower-floor circulation and hotel exits are preserved.
- Level 2, Level 3 and Level 4: all four theme-variant arrangements are
  guaranteed to be sampled at room coordinates (1,0), (0,1), (1,1),
  (2,0). More distant rooms still use seed-stable random selection.
- Level 5: preserves Main Hall, Beverly Room and Boiler Room, and
  guarantees additional hotel variants at (2,0), (0,2), (2,1), (1,1).
- Added regression checks for real stairs, railings and room selection.

## Compatibility
Room footprints remain 32x32 (Levels 2, 3 and 5) and 48x48 (Level 4).
Ground elevation stays Y=63 and ceiling Y=71. All additions write only
into AIR blocks inside the owning chunk during new world generation.
No retroactive rewriting of generated/saved chunks.

## Still requiring manual testing
- Walk into the new Level 2 raised gallery and Beverly balcony in survival.
- Verify stairs, guardrails, overhead clearances and original entrances.
- Check the four theme variants appear in the indicated new room areas.
- Recheck client FPS and chunk seams with a fresh test world.
