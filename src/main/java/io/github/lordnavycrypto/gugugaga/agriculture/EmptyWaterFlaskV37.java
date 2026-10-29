package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;

/** Use an empty flask on a WATER SOURCE block to collect untreated freshwater. */
public final class EmptyWaterFlaskV37 extends Item {
    public EmptyWaterFlaskV37(Properties props) {super(props);}
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held=player.getItemInHand(hand);
        HitResult hit=getPlayerPOVHitResult(level,player,ClipContext.Fluid.SOURCE_ONLY);
        if (!(hit instanceof BlockHitResult blockHit)
                || hit.getType()!=HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(held);
        BlockPos pos=blockHit.getBlockPos();
        if (!level.getFluidState(pos).is(FluidTags.WATER)
                || !level.getFluidState(pos).isSource())
            return InteractionResultHolder.pass(held);
        if (level.getBiome(pos).is(BiomeTags.IS_OCEAN)) {
            if(!level.isClientSide)
                player.displayClientMessage(Component.translatable("message.gugugaga.v37_saltwater"),true);
            return InteractionResultHolder.fail(held);
        }
        if(!level.isClientSide) {
            ItemStack filled=new ItemStack(WaterV37Content.RAW_WATER_BOTTLE.get());
            if(!player.getAbilities().instabuild)held.shrink(1);
            if(held.isEmpty())return InteractionResultHolder.sidedSuccess(filled,false);
            if(!player.getInventory().add(filled))player.drop(filled,false);
        }
        return InteractionResultHolder.sidedSuccess(held,level.isClientSide);
    }
}
