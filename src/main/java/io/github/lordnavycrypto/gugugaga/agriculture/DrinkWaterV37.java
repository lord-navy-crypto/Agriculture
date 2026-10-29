package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Drinkable game item: hydration is awarded on completed consumption event. */
public final class DrinkWaterV37 extends Item {
    public DrinkWaterV37(Properties props){super(props);}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.DRINK;}
    @Override
    public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity) {
        ItemStack result=super.finishUsingItem(stack,level,entity);
        if(!level.isClientSide && entity instanceof Player player
                && !player.getAbilities().instabuild) {
            ItemStack empty=new ItemStack(WaterV37Content.EMPTY_WATER_FLASK.get());
            if(result.isEmpty())return empty;
            if(!player.getInventory().add(empty))player.drop(empty,false);
        }
        return result;
    }
}
