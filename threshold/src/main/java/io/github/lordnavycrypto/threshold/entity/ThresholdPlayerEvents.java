package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Applies the Backrooms respawn rule only when a Threshold Partygoer causes
 * the player's death. Other death sources and the global keepInventory rule
 * remain untouched.
 */
public final class ThresholdPlayerEvents {
    private static final String INVENTORY_SNAPSHOT_KEY = "threshold_partygoer_inventory";
    private static final String SELECTED_SLOT_KEY = "threshold_partygoer_selected_slot";
    private static final String RESPAWN_MARKER_KEY = "threshold_partygoer_respawn";

    private ThresholdPlayerEvents() {
    }

    /**
     * Capture the complete carried inventory before vanilla death cleanup.
     * Inventory.save includes main inventory, armor, and offhand slots.
     */
    @SubscribeEvent
    public static void onPartygoerPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || !isPartygoerDamageSource(event.getSource())) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        data.put(INVENTORY_SNAPSHOT_KEY, player.getInventory().save(new ListTag()));
        data.putInt(SELECTED_SLOT_KEY, player.getInventory().selected);
        data.putBoolean(RESPAWN_MARKER_KEY, true);
    }

    /** Suppress item drops only for a player death attributed to a Partygoer. */
    @SubscribeEvent
    public static void onPartygoerDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(RESPAWN_MARKER_KEY)
                && isPartygoerDamageSource(event.getSource())) {
            event.setCanceled(true);
        }
    }

    /**
     * Restore the one-shot snapshot only for an actual death clone. Dimension
     * transfers and other non-death clone events are deliberately ignored.
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player original = event.getOriginal();
        CompoundTag originalData = original.getPersistentData();
        if (!originalData.getBoolean(RESPAWN_MARKER_KEY)
                || !originalData.contains(INVENTORY_SNAPSHOT_KEY, Tag.TAG_LIST)) {
            return;
        }

        ListTag snapshot = originalData.getList(INVENTORY_SNAPSHOT_KEY, Tag.TAG_COMPOUND);
        Player replacement = event.getEntity();
        replacement.getInventory().load(snapshot);
        replacement.getInventory().selected = Math.max(
                0, Math.min(8, originalData.getInt(SELECTED_SLOT_KEY)));
        replacement.getInventory().setChanged();
        replacement.getPersistentData().putBoolean(RESPAWN_MARKER_KEY, true);

        originalData.remove(INVENTORY_SNAPSHOT_KEY);
        originalData.remove(SELECTED_SLOT_KEY);
        originalData.remove(RESPAWN_MARKER_KEY);
    }

    /** Route the new player entity to the original Level 0 entrance. */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(RESPAWN_MARKER_KEY)) return;

        ServerLevel level0 = player.getServer().getLevel(ThresholdDimensions.LEVEL_0);
        if (level0 == null) {
            data.remove(RESPAWN_MARKER_KEY);
            player.sendSystemMessage(Component.translatable("message.gugugaga.partygoer_level0_missing"));
            return;
        }

        player.teleportTo(level0, 4.5D, 65.0D, 4.5D, player.getYRot(), player.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        data.remove(RESPAWN_MARKER_KEY);
        player.sendSystemMessage(Component.translatable("message.gugugaga.partygoer_capture_success"));
    }

    /** Shared predicate used by event handlers and runtime QA. */
    public static boolean isPartygoerDamageSource(DamageSource source) {
        return source != null
                && (source.getEntity() instanceof ThresholdPartygoerEntity
                || source.getDirectEntity() instanceof ThresholdPartygoerEntity);
    }
}
