package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Sync the world gamerule to clients so nutrition UI also disappears. */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class NutritionModeNetworkV414 {
    private NutritionModeNetworkV414() {}

    public record State(boolean enabled) implements CustomPacketPayload {
        public static final Type<State> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, "nutrition_mode_state"));
        public static final StreamCodec<FriendlyByteBuf, State> STREAM_CODEC =
                StreamCodec.of(
                        (FriendlyByteBuf buf, State state) -> buf.writeBoolean(state.enabled()),
                        (FriendlyByteBuf buf) -> new State(buf.readBoolean()));
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(State.TYPE, State.STREAM_CODEC,
                (payload, context) -> NutritionModeV414.setClientEnabled(payload.enabled()));
    }
}
