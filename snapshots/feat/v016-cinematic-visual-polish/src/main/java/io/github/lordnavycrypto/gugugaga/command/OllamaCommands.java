package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.lordnavycrypto.gugugaga.ai.ollama.OllamaClient;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.entity.GugugagaEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public final class OllamaCommands {
    private static final OllamaClient CLIENT = new OllamaClient();
    private static final AtomicBoolean IN_FLIGHT = new AtomicBoolean(false);
    private static String lastStatus = "Never contacted Ollama; deterministic observer active";

    private OllamaCommands() {}

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gugugaga")
                .then(Commands.literal("ai")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("status").executes(ctx -> status(ctx.getSource())))
                        .then(Commands.literal("models").executes(ctx -> models(ctx.getSource())))
                        .then(Commands.literal("detect").executes(ctx -> models(ctx.getSource())))
                        .then(Commands.literal("on").executes(ctx -> toggle(ctx.getSource(), true)))
                        .then(Commands.literal("off").executes(ctx -> toggle(ctx.getSource(), false)))
                        .then(Commands.literal("model")
                                .then(Commands.argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> model(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "name")))))
                        .then(Commands.literal("shadow").executes(ctx -> shadow(ctx.getSource())))));
    }

    private static int status(CommandSourceStack src) {
        src.sendSuccess(() -> Component.literal("Gugu Gaga Ollama: enabled="
                + NightwatchConfig.OLLAMA_ENABLED.get()
                + ", model=" + NightwatchConfig.OLLAMA_MODEL.get()
                + ", requestInFlight=" + IN_FLIGHT.get()
                + ", last=" + lastStatus), false);
        return 1;
    }

    private static int toggle(CommandSourceStack src, boolean value) {
        NightwatchConfig.OLLAMA_ENABLED.set(value);
        src.sendSuccess(() -> Component.literal(value
                ? "Local AI shadow enabled. The observer remains deterministic; try /gugugaga ai detect then model <name>."
                : "Local AI disabled. Deterministic observer continues normally."), false);
        return 1;
    }

    private static int model(CommandSourceStack src, String name) {
        if (!OllamaClient.validModelName(name)) {
            src.sendFailure(Component.literal("Invalid model name. Use /gugugaga ai models."));
            return 0;
        }
        NightwatchConfig.OLLAMA_MODEL.set(name);
        src.sendSuccess(() -> Component.literal("Selected Ollama model: " + name), false);
        return 1;
    }

    private static int models(CommandSourceStack src) {
        if (!IN_FLIGHT.compareAndSet(false, true)) {
            src.sendFailure(Component.literal("An Ollama request is already running."));
            return 0;
        }
        // This user-triggered call is the only installed-model discovery mechanism.
        CLIENT.installedModels().whenComplete((models, error) -> src.getServer().execute(() -> {
            try {
                if (error != null) {
                    lastStatus = "offline or unavailable: " + compactError(error);
                    src.sendFailure(Component.literal("Ollama not detected at 127.0.0.1:11434; "
                            + "the original observer AI continues. " + lastStatus));
                } else {
                    lastStatus = "online, " + models.size() + " locally installed model(s)";
                    src.sendSuccess(() -> Component.literal(lastStatus
                            + ": " + String.join(", ", models.stream().limit(12).toList())), false);
                }
            } finally {
                IN_FLIGHT.set(false);
            }
        }));
        src.sendSuccess(() -> Component.literal("Checking local Ollama /api/tags asynchronously..."), false);
        return 1;
    }

    private static int shadow(CommandSourceStack src) {
        if (!NightwatchConfig.OLLAMA_ENABLED.get()) {
            src.sendFailure(Component.literal("AI is OFF. Enable it with /gugugaga ai on."));
            return 0;
        }
        String model = NightwatchConfig.OLLAMA_MODEL.get();
        if (!OllamaClient.validModelName(model)) {
            src.sendFailure(Component.literal("Select a model with /gugugaga ai model <name>."));
            return 0;
        }
        if (!IN_FLIGHT.compareAndSet(false, true)) {
            src.sendFailure(Component.literal("An Ollama request is already running."));
            return 0;
        }
        // Only loaded entities near the executing command source are considered;
        // no scanning unloaded chunks, no player names or exact coordinates sent.
        var nearby = src.getLevel().getEntitiesOfClass(GugugagaEntity.class,
                new AABB(src.getPosition(), src.getPosition()).inflate(24.0));
        int[] counts = new int[4];
        nearby.forEach(entity -> counts[entity.phase().ordinal()]++);
        String states = Arrays.toString(counts);
        CLIENT.advise(model, nearby.size(), states)
                .whenComplete((advice, error) -> src.getServer().execute(() -> {
                    try {
                        if (error != null) {
                            lastStatus = "shadow fallback: " + compactError(error);
                            src.sendFailure(Component.literal("Ollama advisory unavailable; gameplay unchanged."));
                        } else if (!NightwatchConfig.OLLAMA_ENABLED.get()) {
                            lastStatus = "AI disabled while response was in flight; suggestion discarded";
                        } else {
                            lastStatus = "shadow " + advice.mode()
                                    + " intensity=" + advice.intensity();
                            src.sendSuccess(() -> Component.literal("Ollama suggestion only (not applied): "
                                    + lastStatus + "; rationale=" + advice.rationale()), false);
                        }
                    } finally {
                        IN_FLIGHT.set(false);
                    }
                }));
        src.sendSuccess(() -> Component.literal("Computing optional local pacing suggestion. "
                + "Observer AI continues independently."), false);
        return 1;
    }

    private static String compactError(Throwable failure) {
        Throwable root = failure;
        while (root.getCause() != null) root = root.getCause();
        String kind = root.getClass().getSimpleName();
        return kind.length() > 48 ? kind.substring(0, 48) : kind;
    }
}
