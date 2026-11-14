package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Server-authoritative mode status and the N-screen toggle request. */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class NutritionModeNetworkV414 {
    private NutritionModeNetworkV414() {}

    public record State(boolean enabled, boolean canToggle) implements CustomPacketPayload {
        public static final Type<State> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "nutrition_mode_state"));
        public static final StreamCodec<FriendlyByteBuf, State> STREAM_CODEC =
                StreamCodec.of(
                        (FriendlyByteBuf buf, State state) -> {
                            buf.writeBoolean(state.enabled());
                            buf.writeBoolean(state.canToggle());
                        },
                        (FriendlyByteBuf buf) -> new State(buf.readBoolean(), buf.readBoolean()));
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    /** The client can request a change, but only the server decides whether it is allowed. */
    public record Toggle(boolean enabled) implements CustomPacketPayload {
        public static final Type<Toggle> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "nutrition_mode_toggle"));
        public static final StreamCodec<FriendlyByteBuf, Toggle> STREAM_CODEC =
                StreamCodec.of(
                        (FriendlyByteBuf buf, Toggle value) -> buf.writeBoolean(value.enabled()),
                        (FriendlyByteBuf buf) -> new Toggle(buf.readBoolean()));
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("2");
        registrar.playToClient(State.TYPE, State.STREAM_CODEC,
                (payload, context) -> NutritionModeV414.setClientState(
                        payload.enabled(), payload.canToggle()));
        registrar.playToServer(Toggle.TYPE, Toggle.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player)
                        NutritionModeV414.setFromClient(player, payload.enabled());
                });
    }
}
