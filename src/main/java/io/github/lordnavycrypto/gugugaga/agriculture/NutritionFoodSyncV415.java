package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Chunked server -> client derived food profiles for accurate tooltips. */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class NutritionFoodSyncV415 {
    private static final int CHUNK_SIZE = 64;
    private static final int MAX_ENTRIES = 64;
    private static final int MAX_PARTS = 128;
    private static final Map<String, int[]> CLIENT_PENDING = new HashMap<>();
    private static int nextPart = 0;
    private static int expectedParts = 0;
    private NutritionFoodSyncV415() {}

    public record Chunk(int part, int total, Map<String, int[]> profiles)
            implements CustomPacketPayload {
        public static final Type<Chunk> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID,
                        "nutrition_food_compat_v415"));
        public static final StreamCodec<FriendlyByteBuf, Chunk> STREAM_CODEC =
                StreamCodec.of(Chunk::write, Chunk::read);
        private static void write(FriendlyByteBuf buf, Chunk packet) {
            buf.writeVarInt(packet.part());
            buf.writeVarInt(packet.total());
            buf.writeVarInt(packet.profiles().size());
            for (var entry : packet.profiles().entrySet()) {
                buf.writeUtf(entry.getKey(), 128);
                for (int i = 0; i < 27; i++) buf.writeByte(entry.getValue()[i]);
            }
        }
        private static Chunk read(FriendlyByteBuf buf) {
            int part = buf.readVarInt();
            int total = buf.readVarInt();
            int count = buf.readVarInt();
            if (part < 0 || total < 1 || total > MAX_PARTS
                    || part >= total || count < 0 || count > MAX_ENTRIES)
                throw new IllegalArgumentException("Invalid nutrition profile chunk header");
            Map<String, int[]> entries = new HashMap<>();
            for (int j = 0; j < count; j++) {
                String id = buf.readUtf(128);
                int[] values = new int[27];
                for (int i = 0; i < 27; i++)
                    values[i] = Math.min(10, buf.readUnsignedByte());
                entries.put(id, values);
            }
            return new Chunk(part, total, Map.copyOf(entries));
        }
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(Chunk.TYPE, Chunk.STREAM_CODEC,
                (payload, context) -> receive(payload));
    }

    private static synchronized void receive(Chunk packet) {
        if (packet.part() == 0) {
            CLIENT_PENDING.clear();
            nextPart = 0;
            expectedParts = packet.total();
            NutritionFoodScannerV415.resetClientSnapshot();
        }
        if (packet.total() != expectedParts || packet.part() != nextPart) {
            CLIENT_PENDING.clear();
            nextPart = 0;
            expectedParts = 0;
            return;
        }
        for (var entry : packet.profiles().entrySet())
            CLIENT_PENDING.put(entry.getKey(), Arrays.copyOf(entry.getValue(), 27));
        nextPart++;
        if (nextPart == expectedParts) {
            NutritionFoodScannerV415.setClientSnapshot(CLIENT_PENDING);
            CLIENT_PENDING.clear();
            nextPart = 0;
            expectedParts = 0;
        }
    }

    public static void send(ServerPlayer player) {
        Map<String, int[]> profiles = NutritionFoodScannerV415.serverSnapshot();
        List<String> ids = profiles.keySet().stream().sorted().toList();
        int total = Math.max(1, (ids.size() + CHUNK_SIZE - 1) / CHUNK_SIZE);
        if (total > MAX_PARTS) {
            System.err.println("[Backroom Agriculture] Nutrition compatibility snapshot exceeds "
                    + MAX_PARTS + " chunks; sending first bounded subset");
            total = MAX_PARTS;
        }
        for (int part = 0; part < total; part++) {
            int from = part * CHUNK_SIZE, to = Math.min(ids.size(), from + CHUNK_SIZE);
            Map<String, int[]> batch = new HashMap<>();
            for (int i = from; i < to; i++)
                batch.put(ids.get(i), profiles.get(ids.get(i)));
            PacketDistributor.sendToPlayer(player, new Chunk(part, total, Map.copyOf(batch)));
        }
    }
}
