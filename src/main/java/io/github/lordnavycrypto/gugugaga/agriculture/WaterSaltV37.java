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

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if(!(event.getEntity() instanceof ServerPlayer player)
                ||player.isCreative()||player.isSpectator())return;
        if(player.tickCount%200!=0)return;
        CompoundTag record=data(player);
        int drain=1;
        if(player.isSprinting())drain++;
        if(player.level().dimension()==Level.NETHER)drain++;
        int water=clamp(record.getInt("water")-drain);
        int salt=clamp(record.getInt("salt"));
        // Sodium and water are independent; high salt intake can increase
        // water demand. Thresholds are game balancing, not blood sodium.
        if(salt>85)water=clamp(water-1);
        if(player.tickCount%1200==0)salt=clamp(salt-1);
        record.putInt("water",water);
        record.putInt("salt",salt);
        player.getPersistentData().put(KEY,record);
        // Challenging yet recoverable fictional survival: movement/mining
        // setbacks at severe shortage, not health damage or starvation.
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
