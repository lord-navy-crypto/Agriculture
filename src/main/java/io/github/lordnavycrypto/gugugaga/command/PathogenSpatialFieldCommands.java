package io.github.lordnavycrypto.gugugaga.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.pathogens.PathogenRoster15;
import io.github.lordnavycrypto.gugugaga.pathogens.PathogenSpatialFieldRuntime;
import io.github.lordnavycrypto.gugugaga.pathogens.PathogenSpatialFieldMath;
import io.github.lordnavycrypto.gugugaga.pathogens.PathogenSpatialFieldSavedData;
import io.github.lordnavycrypto.gugugaga.pathogens.PathogenFieldTextMap;
import io.github.lordnavycrypto.gugugaga.pathogens.PathogenFieldOverview;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** An admin-only test injector for an abstract Minecraft data field, no pathogen blocks. */
public final class PathogenSpatialFieldCommands{
    private PathogenSpatialFieldCommands(){}
    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("gugugaga")
            .then(Commands.literal("field")
                .then(Commands.literal("status").executes(ctx->status(ctx.getSource())))
                .then(Commands.literal("overview").executes(ctx->overview(ctx.getSource())))
                .then(Commands.literal("map")
                    .then(Commands.argument("taxon",StringArgumentType.word())
                        .suggests((ctx,builder)->SharedSuggestionProvider.suggest(
                            PathogenRoster15.FIRST_FIFTEEN.stream()
                                .map(PathogenRoster15.Entry::id).toList(),builder))
                        .executes(ctx->visualize(ctx.getSource(),
                            StringArgumentType.getString(ctx,"taxon")))))
                .then(Commands.literal("seed")
                    .requires(src->src.hasPermission(2))
                    .then(Commands.argument("taxon",StringArgumentType.word())
                        .suggests((ctx,builder)->SharedSuggestionProvider.suggest(
                            PathogenRoster15.FIRST_FIFTEEN.stream()
                                .map(PathogenRoster15.Entry::id).toList(),builder))
                        .executes(ctx->change(ctx.getSource(),
                            StringArgumentType.getString(ctx,"taxon"),12))))
                .then(Commands.literal("clear")
                    .requires(src->src.hasPermission(2))
                    .then(Commands.argument("taxon",StringArgumentType.word())
                        .suggests((ctx,builder)->SharedSuggestionProvider.suggest(
                            PathogenRoster15.FIRST_FIFTEEN.stream()
                                .map(PathogenRoster15.Entry::id).toList(),builder))
                        .executes(ctx->change(ctx.getSource(),
                            StringArgumentType.getString(ctx,"taxon"),0))))));
    }
    private static int status(CommandSourceStack src){
        if(src.getEntity() instanceof ServerPlayer player){
            src.sendSuccess(()->Component.literal(
                "[Pathogen Field] "+PathogenSpatialFieldRuntime.summary(
                    player.serverLevel(),player.blockPosition())),false);
            return 1;
        }
        src.sendFailure(Component.literal("Run /gugugaga field status as a player."));
        return 0;
    }
    private static int overview(CommandSourceStack src){
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("Run field overview as a player."));
            return 0;
        }
        var at=PathogenSpatialFieldRuntime.cell(player.blockPosition());
        var field=PathogenSpatialFieldSavedData.forLevel(player.serverLevel()).snapshot();
        var state=PathogenFieldOverview.inspect(field,at);
        player.sendSystemMessage(Component.literal("[Game field] "+
                state.active()+" active channels; "+state.total()+" game units; "+
                state.occupied()+"/49 occupied nearby regions."));
        for(var e:state.top())
            player.sendSystemMessage(Component.literal("  "+e.id()+" ["+e.type()+"] "+
                    e.gameUnits()+"/15"));
        player.sendSystemMessage(Component.literal(
                "7x7 numeric region overview | 0..F = strongest game indicator"));
        for(var row:state.map())
            player.sendSystemMessage(Component.literal("  "+row));
        player.sendSystemMessage(Component.literal(
                "Fictional Minecraft values; not an actual infection measurement."));
        return 1;
    }
    private static int visualize(CommandSourceStack src,String taxon){
        if(!(src.getEntity() instanceof ServerPlayer player)){
            src.sendFailure(Component.literal("Run field map as a player."));
            return 0;
        }
        int channel;
        try{
            channel=PathogenSpatialFieldMath.channel(taxon);
        }catch(IllegalArgumentException invalid){
            src.sendFailure(Component.literal("Unknown first15 taxon: "+taxon));
            return 0;
        }
        var center=PathogenSpatialFieldRuntime.cell(player.blockPosition());
        var snapshot=PathogenSpatialFieldSavedData.forLevel(
                player.serverLevel()).snapshot();
        var lines=PathogenFieldTextMap.render(snapshot,center,channel);
        player.sendSystemMessage(Component.literal(
                "[Field map] "+taxon+" | center="+center.x()+","+center.z()
                +" | N up, E right | 0..F = game-pressure 0..15"));
        for(String line:lines){
            player.sendSystemMessage(Component.literal("  "+line));
        }
        player.sendSystemMessage(Component.literal(
                "This is a fictional Minecraft data view, not infection data."));
        return 1;
    }
    private static int change(CommandSourceStack src,String id,int value){
        if(!NightwatchConfig.PATHOGEN_FIELD_ENABLED.get()){
            src.sendFailure(Component.translatable(
                "message.gugugaga.pathogen_field.disabled"));
            return 0;
        }
        try{
            PathogenRoster15.byId(id);
        }catch(IllegalArgumentException invalid){
            src.sendFailure(Component.literal("Unknown member of first15: "+id));
            return 0;
        }
        if(src.getEntity() instanceof ServerPlayer player){
            if(!PathogenSpatialFieldRuntime.set(player.serverLevel(),
                player.blockPosition(),id,value)){
                src.sendFailure(Component.literal(
                    "Spatial game field at capacity (1024 nonempty cells)."));
                return 0;
            }
            src.sendSuccess(()->Component.translatable(
                "message.gugugaga.pathogen_field.admin"),true);
            return 1;
        }
        src.sendFailure(Component.literal("This command must run as a player."));
        return 0;
    }
}
