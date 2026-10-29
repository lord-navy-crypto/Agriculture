package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Server-to-client snapshot. Only abstract gameplay scores (27 bounded
 * integers) are transmitted; clients never author or alter player progress.
 */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,bus=EventBusSubscriber.Bus.MOD)
public final class NutritionNetworkV35 {
    private NutritionNetworkV35() {}

    public record State(int[] scores,int water,int salt) implements CustomPacketPayload {
        public static final Type<State> TYPE=new Type<>(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,
                        "nutrition_state"));
        public static final StreamCodec<FriendlyByteBuf,State> STREAM_CODEC=
                StreamCodec.of(
                        (FriendlyByteBuf buf, State data) -> {
                            if (data.scores().length != 27)
                                throw new IllegalArgumentException("Invalid nutrition snapshot");
                            for(int n:data.scores())buf.writeVarInt(Math.max(0,Math.min(100,n)));
                            buf.writeVarInt(Math.max(0,Math.min(100,data.water())));
                            buf.writeVarInt(Math.max(0,Math.min(100,data.salt())));
                        },
                        (FriendlyByteBuf buf) -> {
                            int[] values=new int[27];
                            for(int i=0;i<values.length;i++)
                                values[i]=Math.max(0,Math.min(100,buf.readVarInt()));
                            return new State(values,
                                    Math.max(0,Math.min(100,buf.readVarInt())),
                                    Math.max(0,Math.min(100,buf.readVarInt())));
                        }
                );
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(State.TYPE,State.STREAM_CODEC,
                (payload,context) -> NutritionScreenClientV35.open(payload.scores(),payload.water(),payload.salt()));
    }
}
