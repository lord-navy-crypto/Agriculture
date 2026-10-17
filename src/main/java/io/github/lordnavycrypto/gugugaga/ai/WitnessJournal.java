package io.github.lordnavycrypto.gugugaga.ai;

import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournalPolicy.Evidence;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Private, player-owned tiny record of supernatural observations.
 *
 * Uses NeoForge's existing player persistent NBT. Stores only 3 evidence bits
 * plus opt-out. No external database, world coordinate history, real identity
 * or gameplay-affecting powers. The player may erase it with /journal clear.
 */
public final class WitnessJournal {
    private static final String ROOT="GuguGagaWitnessV1";
    private static final String EVIDENCE="EvidenceBits";
    private static final String MUTED="JournalDisabled";

    private WitnessJournal(){}

    private static CompoundTag data(ServerPlayer player) {
        return player.getPersistentData().getCompound(ROOT);
    }

    private static void save(ServerPlayer player,CompoundTag state) {
        player.getPersistentData().put(ROOT,state);
    }

    public static boolean enabled(ServerPlayer player) {
        return !data(player).getBoolean(MUTED);
    }

    public static void setEnabled(ServerPlayer player,boolean enabled) {
        CompoundTag state=data(player);
        state.putBoolean(MUTED,!enabled);
        save(player,state);
    }

    public static int evidenceBits(ServerPlayer player) {
        return data(player).getInt(EVIDENCE)&WitnessJournalPolicy.ALL_BITS;
    }

    public static void clear(ServerPlayer player) {
        CompoundTag state=data(player);
        state.remove(EVIDENCE);
        // Clearing evidence intentionally retains the player's off switch.
        save(player,state);
    }

    public static void record(ServerPlayer player,ObserverBrain.Phase phase,
            boolean watched,double distanceSquared,boolean justRetreated) {
        if(!NightwatchConfig.JOURNAL_ENABLED.get())return;
        // Most AI evaluations cannot unlock a clue. Avoid NBT lookup until
        // the pure predicate confirms a potential, eligible story event.
        Evidence clue=WitnessJournalPolicy.observe(
                phase,watched,distanceSquared,justRetreated);
        if(clue==Evidence.NONE || !enabled(player))return;
        CompoundTag state=data(player);
        int before=state.getInt(EVIDENCE)&WitnessJournalPolicy.ALL_BITS;
        if(!WitnessJournalPolicy.isNew(before,clue))return;
        state.putInt(EVIDENCE,WitnessJournalPolicy.unlock(before,clue));
        save(player,state);
        // One optional mild message per discovery over the player's lifetime;
        // never used as an actionbar flicker or a surprise UI takeover.
        if(NightwatchConfig.JOURNAL_HINTS_ENABLED.get())
            player.sendSystemMessage(Component.literal(
                    "[Nightwatch] You noticed something. /gugugaga journal"));
    }

    public static void show(ServerPlayer player) {
        int bits=evidenceBits(player);
        player.sendSystemMessage(Component.literal(
                "--- NIGHTWATCH WITNESS JOURNAL ("+
                        WitnessJournalPolicy.discovered(bits)+"/3) ---"));
        for(Evidence clue:new Evidence[]{
                Evidence.THE_GAZE,Evidence.THE_FOOTSTEPS,Evidence.THE_RETREAT}) {
            if((bits&clue.bit)!=0)
                player.sendSystemMessage(Component.literal(
                        "* "+WitnessJournalPolicy.name(clue)+": "+
                        WitnessJournalPolicy.description(clue)));
            else
                player.sendSystemMessage(Component.literal("* ??? (not yet witnessed)"));
        }
        if(bits==WitnessJournalPolicy.ALL_BITS)
            player.sendSystemMessage(Component.literal(
                    "A final note: the footsteps began before you turned around."));
        player.sendSystemMessage(Component.literal(
                "Journal recording: "+(enabled(player)?"ON":"OFF")+
                ". Commands: /gugugaga journal on|off|clear"));
    }
}
