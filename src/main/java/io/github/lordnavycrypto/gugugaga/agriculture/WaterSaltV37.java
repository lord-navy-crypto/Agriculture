package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Fictional Minecraft hydration + sodium/homeostasis gameplay.
 * Values are 0..100 balance meters, never ml, mg or dietary prescriptions.
 * No real-world diagnosis or intake recommendation is possible from them.
 */
@EventBusSubscriber(modid=GuguGagaMod.MOD_ID)
public final class WaterSaltV37 {
    private WaterSaltV37(){}
    public static final String KEY="gugugaga_water_salt_v37";

    private static CompoundTag data(ServerPlayer player) {
        CompoundTag root=player.getPersistentData();
        if(!root.contains(KEY)) {
            CompoundTag tag=new CompoundTag();
            tag.putInt("water",80);
            tag.putInt("salt",60);
            root.put(KEY,tag);
        }
        return root.getCompound(KEY);
    }
    private static int clamp(int n) {return Math.max(0,Math.min(100,n));}
    public static int water(ServerPlayer player){return clamp(data(player).getInt("water"));}
    public static int salt(ServerPlayer player){return clamp(data(player).getInt("salt"));}

    @SubscribeEvent
    public static void onDrinkOrEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player))return;
        ItemStack stack=event.getItem();
        String name=BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (!stack.has(DataComponents.FOOD) && !stack.is(Items.POTION)
                && !stack.is(Items.HONEY_BOTTLE)) return;
        int water=WaterSaltProfilesV37.water(name);
        int sodium=WaterSaltProfilesV37.sodium(name);
        if(name.equals("gugugaga:raw_water_bottle") && player.getRandom().nextInt(5)==0) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,100,0));
        }
        CompoundTag record=data(player);
        record.putInt("water",clamp(record.getInt("water")+water));
        record.putInt("salt",clamp(record.getInt("salt")+sodium));
        player.getPersistentData().put(KEY,record);
    }

    /**
     * Apply one five-second sample. Stored progress is measured in fictional
     * activity-tick units, not millilitres or a real dietary requirement.
     * The previous 200-tick drain removed 120 water units even at rest per
     * 24,000-tick Minecraft day; the balanced baseline is now 60/day.
     * The accumulator prevents roundoff from changing the result at rest.
     */
    private static final int SAMPLE_TICKS=100;
    private static final int WATER_DRAIN_UNITS=400;
    private static final int SALT_DECAY_TICKS=4800;
    private static final String WATER_PROGRESS="v42_water_progress";
    private static final String SALT_PROGRESS="v42_salt_progress";

    public static int activityUnits(boolean sprinting,boolean nether,boolean highSalt) {
        return SAMPLE_TICKS
                +(sprinting?SAMPLE_TICKS:0)
                +(nether?SAMPLE_TICKS:0)
                +(highSalt?SAMPLE_TICKS/2:0);
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if(!(event.getEntity() instanceof ServerPlayer player)
                ||player.isCreative()||player.isSpectator())return;
        if(player.tickCount%SAMPLE_TICKS!=0)return;
        CompoundTag record=data(player);
        int salt=clamp(record.getInt("salt"));
        int priorWaterProgress=Math.max(0,Math.min(WATER_DRAIN_UNITS-1,
                record.getInt(WATER_PROGRESS)));
        int progress=priorWaterProgress+activityUnits(player.isSprinting(),
                player.level().dimension()==Level.NETHER,salt>85);
        int water=clamp(record.getInt("water")-progress/WATER_DRAIN_UNITS);
        record.putInt(WATER_PROGRESS,progress%WATER_DRAIN_UNITS);
        int priorSaltProgress=Math.max(0,Math.min(SALT_DECAY_TICKS-1,
                record.getInt(SALT_PROGRESS)));
        int saltProgress=priorSaltProgress+SAMPLE_TICKS;
        salt=clamp(salt-saltProgress/SALT_DECAY_TICKS);
        record.putInt(SALT_PROGRESS,saltProgress%SALT_DECAY_TICKS);
        record.putInt("water",water);
        record.putInt("salt",salt);
        player.getPersistentData().put(KEY,record);
        // Refresh existing temporary gameplay penalties every ten seconds
        // rather than every sample to reduce unnecessary effect packets.
        if(player.tickCount%200!=0)return;
        if(water<=15)player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,240,0,true,false,true));
        if(water<=8 || salt<=8)player.addEffect(new MobEffectInstance(
                MobEffects.DIG_SLOWDOWN,240,0,true,false,true));
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompoundTag old=event.getOriginal().getPersistentData();
        if(old.contains(KEY))
            event.getEntity().getPersistentData().put(KEY,old.getCompound(KEY).copy());
    }
}
