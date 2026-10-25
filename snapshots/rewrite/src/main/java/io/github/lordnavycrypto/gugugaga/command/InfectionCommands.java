package io.github.lordnavycrypto.gugugaga.command;

import io.github.lordnavycrypto.gugugaga.simulation.InfectionRuntime;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Small Minecraft command surface for the functioning new infection engine. */
public final class InfectionCommands {
    private InfectionCommands() {}

    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("gugugaga")
            .then(Commands.literal("infection")
                .then(Commands.literal("status")
                    .executes(ctx->status(ctx.getSource())))
                .then(Commands.literal("seed")
                    .requires(source->source.hasPermission(2))
                    .executes(ctx->seed(ctx.getSource())))
                .then(Commands.literal("clear")
                    .requires(source->source.hasPermission(2))
                    .executes(ctx->clear(ctx.getSource())))));
    }

    private static ServerPlayer player(CommandSourceStack src){
        return src.getEntity() instanceof ServerPlayer player ? player : null;
    }
    private static int status(CommandSourceStack src){
        ServerPlayer user=player(src);
        if(user==null){
            src.sendFailure(Component.literal("Use this command in a Minecraft world."));
            return 0;
        }
        user.sendSystemMessage(Component.literal(
                "[Virus Mod] "+InfectionRuntime.status(user.serverLevel())));
        return 1;
    }
    private static int seed(CommandSourceStack src){
        ServerPlayer user=player(src);
        if(user==null){
            src.sendFailure(Component.literal("Seed as an in-world operator."));
            return 0;
        }
        var animals=user.serverLevel().getEntitiesOfClass(Animal.class,
                user.getBoundingBox().inflate(6),Animal::isAlive);
        Animal nearest=null;
        double min=Double.POSITIVE_INFINITY;
        for(Animal animal:animals){
            double d=animal.distanceToSqr(user);
            if(d<min){min=d;nearest=animal;}
        }
        if(nearest==null){
            src.sendFailure(Component.literal("No nearby vanilla animal."));
            return 0;
        }
        try{InfectionRuntime.seed(user.serverLevel(),nearest);}
        catch(IllegalStateException capped){
            src.sendFailure(Component.literal(capped.getMessage()));
            return 0;
        }
        user.sendSystemMessage(Component.literal(
            "Seeded one in-game animal at INFECTIOUS stage. "
            +"Nearby animals can enter EXPOSED then INFECTIOUS then RECOVERED. "
            +"These are fictional game timings, not real biological parameters."));
        return 1;
    }
    private static int clear(CommandSourceStack src){
        ServerPlayer user=player(src);
        if(user==null){
            src.sendFailure(Component.literal("Clear as an in-world operator."));
            return 0;
        }
        InfectionRuntime.clear(user.serverLevel());
        user.sendSystemMessage(Component.literal(
            "Cleared memory-only infection simulation for this dimension."));
        return 1;
    }
}
