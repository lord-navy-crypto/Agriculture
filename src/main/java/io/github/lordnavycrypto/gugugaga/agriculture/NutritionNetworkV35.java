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
 * Server-to-client snapshot: 27 nutrient scores, independent hydration/salt,
 * and five group scores/states/streaks are server-authoritative.
 */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID,bus=EventBusSubscriber.Bus.MOD)
public final class NutritionNetworkV35 {
    private NutritionNetworkV35() {}

    public record State(int[] scores,int water,int salt,int[] feedback) implements CustomPacketPayload {
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
                            if(data.feedback()==null || data.feedback().length!=NutritionFeedbackV38.SNAPSHOT_SIZE)
                                throw new IllegalArgumentException("Invalid feedback snapshot");
                            for(int i=0;i<NutritionFeedbackV38.SNAPSHOT_SIZE;i++) {
                                int value=data.feedback()[i];
                                if(i<5)buf.writeVarInt(Math.max(0,Math.min(100,value)));
                                else if(i<10 || (i>=NutritionFeedbackV38.NUTRIENT_STATES_OFFSET
                                        && i<NutritionFeedbackV38.NUTRIENT_DAYS_OFFSET))
                                    buf.writeVarInt(Math.max(0,Math.min(2,value+2)));
                                else buf.writeVarInt(Math.max(0,Math.min(60,value)));
                            }
                        },
                        (FriendlyByteBuf buf) -> {
                            int[] values=new int[27];
                            for(int i=0;i<values.length;i++)
                                values[i]=Math.max(0,Math.min(100,buf.readVarInt()));
                            int water=Math.max(0,Math.min(100,buf.readVarInt()));
                            int salt=Math.max(0,Math.min(100,buf.readVarInt()));
                            int[] feedback=new int[NutritionFeedbackV38.SNAPSHOT_SIZE];
                            for(int i=0;i<feedback.length;i++) {
                                int value=buf.readVarInt();
                                if(i<5)feedback[i]=Math.max(0,Math.min(100,value));
                                else if(i<10 || (i>=NutritionFeedbackV38.NUTRIENT_STATES_OFFSET
                                        && i<NutritionFeedbackV38.NUTRIENT_DAYS_OFFSET))
                                    feedback[i]=Math.max(-2,Math.min(0,value-2));
                                else feedback[i]=Math.max(0,Math.min(60,value));
                            }
                            return new State(values,water,salt,feedback);
                        }
                );
        @Override public Type<? extends CustomPacketPayload> type(){return TYPE;}
    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("3").playToClient(State.TYPE,State.STREAM_CODEC,
                (payload,context) -> NutritionScreenClientV35.open(payload.scores(),payload.water(),payload.salt(),payload.feedback()));
    }
}
