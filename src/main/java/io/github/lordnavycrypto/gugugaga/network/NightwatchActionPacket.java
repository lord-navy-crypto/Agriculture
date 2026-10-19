package io.github.lordnavycrypto.gugugaga.network;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayRuntime;
import io.github.lordnavycrypto.gugugaga.ai.NightwatchDirector;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * A short, validated client-to-server action, NOT a slash command or
 * a client-authoritative gameplay transition. The server owns all state.
 */
public record NightwatchActionPacket(int action) implements CustomPacketPayload {
    public static final int JOIN=0, LEAVE=1, STATUS=2, QUIET_ON=3,
            QUIET_OFF=4, SERVER_ON=5, SERVER_OFF=6;
    public static final Type<NightwatchActionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    GuguGagaMod.MOD_ID,"nightwatch_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf,NightwatchActionPacket>
            STREAM_CODEC=StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,NightwatchActionPacket::action,
                    NightwatchActionPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {return TYPE;}

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE,STREAM_CODEC,
                NightwatchActionPacket::handle);
    }

    private static void handle(NightwatchActionPacket packet,IPayloadContext ctx) {
        if(!(ctx.player() instanceof ServerPlayer player))return;
        // Only a fixed set of actions, with no arbitrary text or command strings.
        // A 4-tick budget prevents UI spam from overwhelming server chat/config.
        long now=player.serverLevel().getGameTime();
        var data=player.getPersistentData();
        long last=data.getLong("GuguNightwatchUILast");
        if(data.contains("GuguNightwatchUILast") && now>=last && now-last<4)return;
        data.putLong("GuguNightwatchUILast",now);

        switch(packet.action()) {
            case JOIN -> {
                if(!NightwatchConfig.PLAYABLE_LOOP_ENABLED.get()) {
                    player.sendSystemMessage(Component.literal(
                            "[Nightwatch] The world is paused. Ask the server owner to enable it from H."));
                } else HauntGameplayRuntime.join(player);
            }
            case LEAVE -> HauntGameplayRuntime.leave(player);
            case STATUS -> player.sendSystemMessage(Component.literal(
                    "[Nightwatch] "+HauntGameplayRuntime.status(player)));
            case QUIET_ON, QUIET_OFF -> {
                boolean quiet=packet.action()==QUIET_ON;
                NightwatchDirector.setQuiet(player.serverLevel().getServer(),
                        player.getUUID(),quiet);
                player.sendSystemMessage(Component.literal(
                        quiet?"[Nightwatch] Quiet mode ON.":"[Nightwatch] Quiet mode OFF."));
            }
            case SERVER_ON, SERVER_OFF -> {
                if(!player.createCommandSourceStack().hasPermission(2)) {
                    player.sendSystemMessage(Component.literal(
                            "[Nightwatch] Only server operators may change world settings."));
                    return;
                }
                boolean enabled=packet.action()==SERVER_ON;
                NightwatchConfig.PLAYABLE_LOOP_ENABLED.set(enabled);
                NightwatchConfig.ENCOUNTERS_ENABLED.set(enabled);
                player.sendSystemMessage(Component.literal(
                        enabled?"[Nightwatch] World gameplay enabled."
                                :"[Nightwatch] World gameplay paused."));
            }
            default -> { /* Ignore unsupported or malicious action IDs. */ }
        }
    }
}
