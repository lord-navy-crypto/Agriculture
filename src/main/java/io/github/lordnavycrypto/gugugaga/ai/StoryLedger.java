package io.github.lordnavycrypto.gugugaga.ai;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Explicit opt-in investigation stance stored on the player's own in-game
 * NBT, with no real identity/location/chat, and fully reversible.
 */
public final class StoryLedger {
    private static final String KEY="GuguGagaStoryV1";
    private static final String SIDE="WitnessSide";
    private StoryLedger(){}

    public static FactionPolicy.Side read(ServerPlayer player) {
        return FactionPolicy.decode(
                player.getPersistentData().getCompound(KEY).getInt(SIDE));
    }

    public static void choose(ServerPlayer player,FactionPolicy.Side side) {
        CompoundTag state=player.getPersistentData().getCompound(KEY);
        state.putInt(SIDE,side.ordinal());
        player.getPersistentData().put(KEY,state);
    }

    public static void show(ServerPlayer player) {
        FactionPolicy.Side side=read(player);
        int clues=WitnessJournalPolicy.discovered(WitnessJournal.evidenceBits(player));
        player.sendSystemMessage(Component.literal(
                "[NIGHTWATCH] "+FactionPolicy.name(side)));
        player.sendSystemMessage(Component.literal(clues>=3
                ? "The Lost Watcher was not alone. A Redactor rewrote the accounts."
                : "Someone may have altered the meaning of these sightings."));
        player.sendSystemMessage(Component.literal(FactionPolicy.reveal(side,clues)));
        player.sendSystemMessage(Component.literal(
                "Choose freely: /gugugaga trust guardian|redactor|neutral"));
    }
}
