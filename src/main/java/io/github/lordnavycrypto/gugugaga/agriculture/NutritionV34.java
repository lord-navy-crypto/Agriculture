package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Full v3.5 game nutrition subsystem (retains this class name for v3.4
 * compatibility). Tracks 27 abstract nutrients from an explicit food catalog.
 * This is NOT physiology, a diagnosis, a food restriction or a real diet plan.
 * Nutrition diversity scores are fictional gameplay progress, not physiology.
 */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID)
public final class NutritionV34 {
    private NutritionV34() {}
    private static final String ROOT="gugugaga_nutrition_v35";
    private static final String LEGACY="gugugaga_nutrition_v34";
    private static final int BASE=45;
    private static final int MAX=100;
    private static final int V38_FLOOR=15;
    private static final String LAST_DAY="v38_last_day";
    private static final String LOW_DAYS="v38_low_days";
    private static final int PAGE_COUNT=5;
    private static final int[][] PAGES={{0,5},{5,12},{12,18},{18,23},{23,27}};

    private static CompoundTag record(ServerPlayer player) {
        CompoundTag all=player.getPersistentData();
        if(!all.contains(ROOT)) {
            CompoundTag migrated=new CompoundTag();
            for(String nutrient:NutritionProfilesV35.NUTRIENTS)
                migrated.putInt(nutrient,BASE);
            // Preserve earlier playtime progress instead of resetting every
            // established world's nutrition state on upgrade.
            if(all.contains(LEGACY)) {
                CompoundTag old=all.getCompound(LEGACY);
                copy(old,migrated,"grain_energy","carbohydrate");
                copy(old,migrated,"protein","protein");
                copy(old,migrated,"plant_fats","fat");
                copy(old,migrated,"fiber","fiber");
                for(String n:new String[]{"vitamin_a","vitamin_c","vitamin_k"})
                    copy(old,migrated,"vitamins",n);
                for(String n:new String[]{"calcium","iron","magnesium"})
                    copy(old,migrated,"minerals",n);
            }
            migrated.putInt("page",0);
            all.put(ROOT,migrated);
        }
        return all.getCompound(ROOT);
    }
    private static void copy(CompoundTag old,CompoundTag dest,String from,String to) {
        if(old.contains(from)) dest.putInt(to,clamp(old.getInt(from)));
    }
    private static int clamp(int value) {return Math.max(0,Math.min(MAX,value));}
    private static void add(CompoundTag record,String key,int amount) {
        record.putInt(key,clamp(record.getInt(key)+amount));
    }

    @SubscribeEvent
    public static void onEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack food=event.getItem();
        if (!food.has(DataComponents.FOOD)) return;
        String id=net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(food.getItem()).toString();
        int[] contribution=NutritionProfilesV35.contribution(id);
        CompoundTag values=record(player);
        for(int i=0;i<NutritionProfilesV35.NUTRIENTS.length;i++) {
            int points=contribution[i];
            if(points>0) add(values,NutritionProfilesV35.NUTRIENTS[i],points);
        }
        // All eaten foods are counted once, only on the server. Inventory
        // possession, use-start and aborted eating never award nutrition.
        player.getPersistentData().put(ROOT,values);
    }

    private static int count(CompoundTag values,int from,int to,int threshold) {
        int result=0;
        for(int i=from;i<to;i++) {
            if(values.getInt(NutritionProfilesV35.NUTRIENTS[i])>=threshold)result++;
        }
        return result;
    }
    private static boolean varied(CompoundTag values) {
        return count(values,0,5,55)>=3 &&
               count(values,5,18,55)>=4 &&
               count(values,18,27,55)>=2;
    }
    private static boolean highlyVaried(CompoundTag values) {
        return count(values,0,5,70)>=4 &&
               count(values,5,18,70)>=6 &&
               count(values,18,27,70)>=4;
    }

    /** One bounded long-term gameplay strain rather than 27 simultaneous debuffs. */
    private static boolean lowVariety(CompoundTag values) {
        return values.getInt("carbohydrate")<30
                && values.getInt("protein")<30
                && count(values,5,18,30)<4;
    }
    private static void updateLongTermState(ServerPlayer player,CompoundTag values) {
        long day=Math.floorDiv(player.level().getDayTime(),24000L);
        if(!values.contains(LAST_DAY)) {
            values.putLong(LAST_DAY,day);
            values.putInt(LOW_DAYS,0);
            return; // existing saved worlds have a grace period
        }
        long last=values.getLong(LAST_DAY);
        if(day<=last) {
            // /time set can go backwards; don't add artificial elapsed days.
            if(day<last)values.putLong(LAST_DAY,day);
            return;
        }
        int elapsed=(int)Math.min(3L,day-last);
        values.putLong(LAST_DAY,day);
        if(lowVariety(values))
            values.putInt(LOW_DAYS,Math.min(3,values.getInt(LOW_DAYS)+elapsed));
        else values.putInt(LOW_DAYS,0);
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.tickCount%200!=0)return;
        CompoundTag values=record(player);
        if(player.isCreative()||player.isSpectator())return;
        updateLongTermState(player,values);
        // Existing positive food-diversity milestones remain unchanged.
        if(varied(values)) player.addEffect(new MobEffectInstance(
                MobEffects.DIG_SPEED,240,0,true,false,true));
        if(highlyVaried(values)) player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SPEED,240,0,true,false,true));
        // Long-term strain is mild, recoverable and does not damage the player.
        // Existing hydration/salt fatigue uses the same effect at the same
        // amplifier rather than stacking duplicate stronger penalties.
        if(values.getInt(LOW_DAYS)>=3 && lowVariety(values))
            player.addEffect(new MobEffectInstance(
                    MobEffects.DIG_SLOWDOWN,220,0,true,false,true));
        else if(!lowVariety(values))values.putInt(LOW_DAYS,0);
        // Slow game-score aging preserves existing save and score IDs.
        if(player.tickCount%24000==0) {
            for(String id:NutritionProfilesV35.NUTRIENTS)
                values.putInt(id,Math.max(V38_FLOOR,values.getInt(id)-1));
            player.getPersistentData().put(ROOT,values);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompoundTag old=event.getOriginal().getPersistentData();
        for(String key:new String[]{ROOT,LEGACY}) {
            if(old.contains(key)) event.getEntity().getPersistentData().put(
                    key,old.getCompound(key).copy());
        }
    }

    /**
     * The same configurable N key cycles five short server-authoritative
     * pages through vanilla chat, requiring no client-only server class.
     */
    public static int[] snapshot(ServerPlayer player) {
        CompoundTag values=record(player);
        int[] result=new int[NutritionProfilesV35.NUTRIENTS.length];
        for(int i=0;i<result.length;i++)
            result[i]=clamp(values.getInt(NutritionProfilesV35.NUTRIENTS[i]));
        return result;
    }

    public static void show(ServerPlayer player) {
        CompoundTag values=record(player);
        int page=Math.floorMod(values.getInt("page"),PAGE_COUNT);
        int[] range=PAGES[page];
        player.sendSystemMessage(Component.translatable("message.gugugaga.v35_title",
                page+1,PAGE_COUNT,Component.translatable(
                    "message.gugugaga.v35_page_"+page)));
        for(int i=range[0];i<range[1];i++) {
            String nutrient=NutritionProfilesV35.NUTRIENTS[i];
            int score=clamp(values.getInt(nutrient));
            String bar="■".repeat(score/10)+"·".repeat(10-score/10);
            player.sendSystemMessage(Component.translatable(
                    "message.gugugaga.v34_nutrition_row",
                    Component.translatable("nutrition.gugugaga."+nutrient),bar,score));
        }
        String bonus=highlyVaried(values)?"message.gugugaga.v35_bonus_advanced":
                varied(values)?"message.gugugaga.v35_bonus_varied":
                "message.gugugaga.v35_bonus_explore";
        player.sendSystemMessage(Component.translatable(bonus));
        player.sendSystemMessage(Component.translatable("message.gugugaga.v35_next_page"));
        values.putInt("page",(page+1)%PAGE_COUNT);
        player.getPersistentData().put(ROOT,values);
    }
}
