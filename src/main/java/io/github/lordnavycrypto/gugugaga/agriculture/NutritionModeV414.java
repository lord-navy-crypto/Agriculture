package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * World-persistent, server-authoritative switch for ALL optional nutrition
 * gameplay (nutrient reserves AND hydration/sodium). Farms, recipes, hunger,
 * food items and other Agriculture features remain available.
 *
 * /gamerule gugugagaNutrition false
 * /gamerule gugugagaNutrition true
 *
 * Existing player data is preserved while disabled. The existing temporary
 * nutrition effects are not refreshed, and expire normally within 240 ticks;
 * removing effects directly would risk deleting effects owned by other mods.
 */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID)
public final class NutritionModeV414 {
    private NutritionModeV414() {}
    public static final GameRules.Key<GameRules.BooleanValue> ENABLED =
            GameRules.register("gugugagaNutrition", GameRules.Category.PLAYER,
                    GameRules.BooleanValue.create(true));
    private static final String LAST_SYNC = "gugugaga_nutrition_mode_last_sync";
    private static final String SYNCED = "gugugaga_nutrition_mode_synced";
    private static volatile boolean clientEnabled = true;

    /** Force registration during mod initialization, before world gamerules load. */
    public static void bootstrap() {}
    public static boolean enabled(ServerPlayer player) {
        return player.serverLevel().getGameRules().getBoolean(ENABLED);
    }
    public static boolean clientEnabled() { return clientEnabled; }
    public static void setClientEnabled(boolean value) { clientEnabled = value; }

    private static void sync(ServerPlayer player) {
        boolean enabled = enabled(player);
        PacketDistributor.sendToPlayer(player, new NutritionModeNetworkV414.State(enabled));
        CompoundTag data = player.getPersistentData();
        data.putBoolean(LAST_SYNC, enabled);
        data.putBoolean(SYNCED, true);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) sync(player);
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player.tickCount % 20 != 0) return;
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(SYNCED)
                || data.getBoolean(LAST_SYNC) != enabled(player)) sync(player);
    }
}
