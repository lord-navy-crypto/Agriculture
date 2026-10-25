package io.github.lordnavycrypto.gugugaga.microbes;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import io.github.lordnavycrypto.gugugaga.microbes.PathogenGameplayPolicy.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Per-player server-owned medical SURVIVAL GAME state; never real diagnosis. */
public final class PathogenGameplayRuntime {
    private PathogenGameplayRuntime(){}
    private static final String ROOT="GuguGagaPathogenScenario";

    public static CaseState read(ServerPlayer player) {
        CompoundTag data=player.getPersistentData();
        if(!data.contains(ROOT))return null;
        CompoundTag value=data.getCompound(ROOT);
        try {
            return new CaseState(Pathogen.valueOf(value.getString("Species")),
                    Stage.valueOf(value.getString("Stage")),value.getInt("GameTicks"));
        }catch(IllegalArgumentException ex){
            data.remove(ROOT);
            return null;
        }
    }
    private static void save(ServerPlayer player,CaseState state){
        CompoundTag parent=player.getPersistentData();
        if(state==null){parent.remove(ROOT);return;}
        CompoundTag nbt=new CompoundTag();
        nbt.putString("Species",state.pathogen().name());
        nbt.putString("Stage",state.stage().name());
        nbt.putInt("GameTicks",state.gameTicks());
        parent.put(ROOT,nbt);
    }
    public static void contact(ServerPlayer player,Pathogen pathogen){
        if(!NightwatchConfig.PATHOGEN_GAMEPLAY_ENABLED.get())return;
        if(player.isCreative()||player.isSpectator())return;
        if(read(player)==null)save(player,PathogenGameplayPolicy.report(pathogen));
        player.displayClientMessage(Component.translatable(
                "message.gugugaga.pathogen.contact"),true);
    }
    public static void simulateIllness(ServerPlayer player,Pathogen pathogen){
        if(!NightwatchConfig.PATHOGEN_GAMEPLAY_ENABLED.get())return;
        CaseState existing=read(player);
        if(existing==null||existing.pathogen()!=pathogen)return;
        CaseState after=PathogenGameplayPolicy.startIllnessScenario(existing);
        if(after!=existing){
            save(player,after);
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.pathogen.illness"),true);
        }else player.displayClientMessage(Component.translatable(
                    "message.gugugaga.pathogen.not_diagnosed"),true);
    }
    public static void clinic(ServerPlayer player){
        if(!NightwatchConfig.PATHOGEN_GAMEPLAY_ENABLED.get())return;
        CaseState state=read(player);
        if(state==null){
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.pathogen.no_case"),true);
            return;
        }
        var recommended=PathogenGameplayPolicy.care(state);
        save(player,PathogenGameplayPolicy.visitClinic(state));
        if(recommended==Care.SUPPORTIVE_CARE){
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.pathogen.supportive_care"),true);
        }else if(recommended==Care.EARLY_ASSESSMENT)
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.pathogen.early_care"),true);
        else player.displayClientMessage(Component.translatable(
                "message.gugugaga.pathogen.followup"),true);
    }
    public static void onTick(ServerTickEvent.Post event){
        if(!NightwatchConfig.PATHOGEN_GAMEPLAY_ENABLED.get())return;
        if(event.getServer().overworld().getGameTime()%40!=0)return;
        for(ServerPlayer player:event.getServer().getPlayerList().getPlayers()){
            CaseState state=read(player);
            if(state==null||!player.isAlive()||player.isCreative()||player.isSpectator())continue;
            CaseState next=PathogenGameplayPolicy.advance(state,40);
            if(PathogenGameplayPolicy.hasGameFatigue(next))
                player.addEffect(new MobEffectInstance(
                    MobEffects.MOVEMENT_SLOWDOWN,100,0,true,false,false));
            else if(state.stage()==Stage.SIMULATED_ILLNESS)
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if(next==null || next.stage()!=state.stage())
                player.displayClientMessage(Component.translatable(
                    "message.gugugaga.pathogen.stage"),true);
            save(player,next);
        }
    }
}
