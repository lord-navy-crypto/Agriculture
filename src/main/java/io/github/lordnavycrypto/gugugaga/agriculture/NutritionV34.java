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
 * Server-owned six-group diversity meter. A gameplay abstraction of dietary
 * variety; not recommended intakes, not diagnoses, no negative food/body effects.
 * No diet restriction, hidden penalties or nutrition-related damage.
 */
@EventBusSubscriber(modid = GuguGagaMod.MOD_ID)
public final class NutritionV34 {
    private NutritionV34() {}
    private static final String ROOT="gugugaga_nutrition_v34";
    private static final String[] GROUPS={
        "grain_energy","protein","plant_fats","vitamins","minerals","fiber"
    };
    private static final int BASE=45;
    private static final int MAX=100;

    private static CompoundTag record(ServerPlayer player) {
        CompoundTag tag=player.getPersistentData();
        if (!tag.contains(ROOT)) {
            CompoundTag values=new CompoundTag();
            for (String k:GROUPS) values.putInt(k, BASE);
            tag.put(ROOT, values);
        }
        return tag.getCompound(ROOT);
    }
    private static void add(CompoundTag values, int index, int amount) {
        String key=GROUPS[index];
        values.putInt(key,Math.max(0,Math.min(MAX,values.getInt(key)+amount)));
    }

    @SubscribeEvent
    public static void onEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack food=event.getItem();
        if (!food.has(DataComponents.FOOD)) return;
        String id=net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(food.getItem()).getPath();
        CompoundTag values=record(player);
        boolean seafood=has(id,"fish","cod","salmon","shrimp","crab","anchovy","sardine",
                "mackerel","herring","sea_bream","sea_bass");
        boolean protein=seafood||has(id,"egg","beef","pork","chicken","mutton",
                "soy","bean","lentil","chickpea","milk","cheese","mushroom");
        boolean grain=has(id,"bread","rice","wheat","porridge","oat","potato",
                "noodle","cake","biscuit","pie","corn","barley","pasta","wrap");
        boolean plant=has(id,"carrot","apple","berry","beet","cabbage","cucumber",
                "vegetable","pepper","tomato","pumpkin","melon","radish",
                "daikon","lettuce","fruit","orange","lemon","grape","seaweed");
        boolean fats=has(id,"seed","sesame","peanut","nut","oil","avocado","salmon",
                "mackerel","egg","soybean");
        boolean mineral=seafood||has(id,"milk","cheese","green","kale",
                "spinach","kelp","seaweed","mushroom","bean","seed","beet");
        boolean fiber=plant||grain||has(id,"bean","lentil","chickpea","oat");
        // Every edible item contributes basic energy; food diversity builds
        // specific values more quickly than repeating one single food.
        add(values,0,grain?5:2);
        if (protein) add(values,1,6);
        if (fats) add(values,2,5);
        if (plant) add(values,3,6);
        if (mineral) add(values,4,5);
        if (fiber) add(values,5,5);
        player.getPersistentData().put(ROOT, values);
    }
    private static boolean has(String id,String... words) {
        for (String word:words) if (id.contains(word)) return true;
        return false;
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        // No continuous per-tick inventory scans. Positive status only.
        if (player.tickCount%200 != 0) return;
        CompoundTag values=record(player);
        int diverse=0;
        for (String key:GROUPS) if(values.getInt(key)>=55) diverse++;
        if (diverse>=4) player.addEffect(new MobEffectInstance(
                MobEffects.DIG_SPEED,240,0,true,false,true));
        // Very gradual score normalization. Never decreases vanilla hunger,
        // health, speed or stamina; no punishment for food choices.
        if (player.tickCount%24000==0) {
            for (String key:GROUPS) values.putInt(key,Math.max(30,values.getInt(key)-1));
            player.getPersistentData().put(ROOT, values);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.getOriginal().getPersistentData().contains(ROOT))
            event.getEntity().getPersistentData().put(ROOT,
                    event.getOriginal().getPersistentData().getCompound(ROOT).copy());
    }

    public static void show(ServerPlayer player) {
        CompoundTag values=record(player);
        int diverse=0;
        for (String key:GROUPS) if(values.getInt(key)>=55) diverse++;
        player.sendSystemMessage(Component.translatable("message.gugugaga.v34_nutrition_title"));
        for (int i=0;i<GROUPS.length;i++) {
            int score=Math.max(0,Math.min(100,values.getInt(GROUPS[i])));
            int bars=score/10;
            String bar="■".repeat(bars)+"·".repeat(10-bars);
            player.sendSystemMessage(Component.translatable(
                    "message.gugugaga.v34_nutrition_row",
                    Component.translatable("nutrition.gugugaga."+GROUPS[i]),
                    bar,score));
        }
        player.sendSystemMessage(Component.translatable(
                diverse>=4?"message.gugugaga.v34_nutrition_varied":
                          "message.gugugaga.v34_nutrition_explore"));
    }
}
