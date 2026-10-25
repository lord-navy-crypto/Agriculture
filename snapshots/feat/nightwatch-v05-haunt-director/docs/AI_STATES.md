# Observer server-side brain (MVP)

```text
farther than 32 blocks ──────────> DORMANT
within 32 blocks ────────────────> OBSERVE
OBSERVE + player looking away + 3 seconds + >6 blocks ──> APPROACH
APPROACH + player watches from within 10 blocks ────────> RETREAT
APPROACH + within 3.5 blocks ────────────────────────────> RETREAT
RETREAT + 3 seconds ────────────────────────────────────> OBSERVE
```

This pure decision function is checked separately from Minecraft integrations. Entity movement/navigation is server authoritative; the renderer is purely client-side. No AI or audio events are transmitted over the network beyond vanilla entity sync yet.

MVP limitation: goal selection and navigation are conservative and have not been gameplay-tuned. Controlled summon only; encounter director not activated.
