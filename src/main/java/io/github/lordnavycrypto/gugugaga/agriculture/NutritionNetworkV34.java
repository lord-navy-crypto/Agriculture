package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Single request packet: server owns and reports the player's nutrition data. */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID, bus=EventBusSubscriber.Bus.MOD)
public final class NutritionNetworkV34 {
    private NutritionNetworkV34() {}

    public record Request() implements CustomPacketPayload {
        public static final Type<Request> TYPE=new Type<>(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,"nutrition_request"));
        public static final StreamCodec<FriendlyByteBuf,Request> STREAM_CODEC=
                StreamCodec.unit(new Request());
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("2").playToServer(Request.TYPE, Request.STREAM_CODEC,
                (payload,context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        // The graphical panel supersedes the legacy five-page chat dump.
                        // Do not mix chat paragraphs with every GUI refresh.
                        PacketDistributor.sendToPlayer(player,
                                new NutritionNetworkV35.State(NutritionV34.snapshot(player),
                                        WaterSaltV37.water(player),WaterSaltV37.salt(player),
                                        NutritionV34.feedbackSnapshot(player)));
                    }
                });
    }
}
