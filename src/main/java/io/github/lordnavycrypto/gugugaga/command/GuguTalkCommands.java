package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.lordnavycrypto.gugugaga.ai.GuguDialoguePolicy;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournalPolicy;
import io.github.lordnavycrypto.gugugaga.ai.WitnessJournal;
import io.github.lordnavycrypto.gugugaga.ai.ollama.OllamaClient;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * One-to-one opt-in fictional dialogue, never a general chat listener.
 * The offline character always works without any AI service.
 */
public final class GuguTalkCommands {
    private static final OllamaClient CLIENT=new OllamaClient();
    private static final AtomicBoolean AI_BUSY=new AtomicBoolean(false);
    private static final Map<MinecraftServer,Map<UUID,Long>> LAST_TALK=
            new WeakHashMap<>();
    private GuguTalkCommands(){}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gugugaga")
                .then(Commands.literal("talk")
                        .then(Commands.literal("status").executes(ctx->status(ctx.getSource())))
                        .then(Commands.literal("ai")
                                .requires(src->src.hasPermission(2))
                                .then(Commands.literal("on").executes(ctx->ai(ctx.getSource(),true)))
                                .then(Commands.literal("off").executes(ctx->ai(ctx.getSource(),false))))
                        .then(Commands.argument("message",StringArgumentType.greedyString())
                                .executes(ctx->talk(ctx.getSource(),
                                        StringArgumentType.getString(ctx,"message"))))));
    }

    private static int ai(CommandSourceStack source,boolean enabled) {
        NightwatchConfig.DIALOGUE_LOCAL_AI_ENABLED.set(enabled);
        source.sendSuccess(()->Component.literal(enabled
                ? "Gugu-Gaga local conversation enabled. Also enable /gugugaga ai on and select a model."
                : "Gugu-Gaga local conversation disabled; offline personality remains."),false);
        return 1;
    }

    private static int status(CommandSourceStack source) {
        source.sendSuccess(()->Component.literal(
                "Gugu-Gaga chat enabled="+NightwatchConfig.DIALOGUE_ENABLED.get()
                +", local Ollama chat="+NightwatchConfig.DIALOGUE_LOCAL_AI_ENABLED.get()
                +", local AI service enabled="+NightwatchConfig.OLLAMA_ENABLED.get()
                +". Only /gugugaga talk <message> sends text; never reads general chat."),false);
        return 1;
    }

    private static int talk(CommandSourceStack source,String raw)
            throws CommandSyntaxException {
        ServerPlayer player=source.getPlayerOrException();
        if(!NightwatchConfig.DIALOGUE_ENABLED.get()) {
            player.sendSystemMessage(Component.literal("[Gugu-Gaga] Dialogue disabled by server."));
            return 0;
        }
        String question=GuguDialoguePolicy.cleanInput(raw);
        if(question.isBlank()){
            player.sendSystemMessage(Component.literal("[Gugu-Gaga] Ask a short question."));
            return 0;
        }
        var server=source.getServer();
        long now=server.overworld().getGameTime();
        Map<UUID,Long> recent=LAST_TALK.computeIfAbsent(server,x->new HashMap<>());
        // At most one 8-second request per person. Prune stale user entries
        // on servers with repeated joins; never keep question/answer history.
        recent.entrySet().removeIf(e->now-e.getValue()>2400 || now<e.getValue());
        long last=recent.getOrDefault(player.getUUID(),Long.MIN_VALUE);
        if(last!=Long.MIN_VALUE && now-last<GuguDialoguePolicy.COOLDOWN_TICKS) {
            player.sendSystemMessage(Component.literal(
                    "[Gugu-Gaga] Wait a moment before another question."));
            return 0;
        }
        recent.put(player.getUUID(),now);
        boolean cold=player.serverLevel().getBiome(player.blockPosition())
                .value().getBaseTemperature()<=0.3f;
        int clues=WitnessJournalPolicy.discovered(WitnessJournal.evidenceBits(player));
        long seed=player.getUUID().getLeastSignificantBits();
        String offline=GuguDialoguePolicy.reply(question,clues,cold,seed);
        String model=NightwatchConfig.OLLAMA_MODEL.get();
        if(!NightwatchConfig.DIALOGUE_LOCAL_AI_ENABLED.get()
                || !NightwatchConfig.OLLAMA_ENABLED.get()
                || !OllamaClient.validModelName(model)
                || !AI_BUSY.compareAndSet(false,true)) {
            player.sendSystemMessage(Component.literal("[Gugu-Gaga] "+offline));
            return 1;
        }
        // Only this explicit player prompt is sent to a local server-side
        // 127.0.0.1 model. Never broadcast replies to other players.
        UUID playerId=player.getUUID();
        CLIENT.chat(model,question).whenComplete((answer,error)->
                server.execute(()->{
                    try {
                        ServerPlayer online=server.getPlayerList().getPlayer(playerId);
                        if(online==null)return;
                        if(error!=null || !NightwatchConfig.DIALOGUE_LOCAL_AI_ENABLED.get()
                                || !NightwatchConfig.OLLAMA_ENABLED.get()) {
                            online.sendSystemMessage(Component.literal(
                                    "[Gugu-Gaga] "+offline));
                        } else {
                            String clean=GuguDialoguePolicy.safeAnswer(answer);
                            online.sendSystemMessage(Component.literal(
                                    "[Gugu-Gaga] "+(clean.isBlank()?offline:clean)));
                        }
                    } finally {
                        AI_BUSY.set(false);
                    }
                }));
        player.sendSystemMessage(Component.literal(
                "[Gugu-Gaga] Thinking quietly... (local AI only; offline fallback available)"));
        return 1;
    }
}
