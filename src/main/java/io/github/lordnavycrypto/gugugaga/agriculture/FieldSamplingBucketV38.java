package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Non-invasive field water collection: no replacement of vanilla bucket
 * interactions and no reclassification of water blocks in existing worlds.
 */
public final class FieldSamplingBucketV38 extends Item {
    private final boolean filled;
    public FieldSamplingBucketV38(Properties properties,boolean filled) {
        super(properties);
        this.filled=filled;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        ItemStack held=player.getItemInHand(hand);
        if(filled)return InteractionResultHolder.pass(held);
        HitResult hit=getPlayerPOVHitResult(level,player,ClipContext.Fluid.SOURCE_ONLY);
        if(!(hit instanceof BlockHitResult block) || hit.getType()!=HitResult.Type.BLOCK)
            return InteractionResultHolder.pass(held);
        BlockPos pos=block.getBlockPos();
        if(!level.getFluidState(pos).is(FluidTags.WATER)
                || !level.getFluidState(pos).isSource())
            return InteractionResultHolder.pass(held);
        if(!level.isClientSide) {
            ItemStack collected=WaterEcologyV38.collectedBucket(level,pos);
            if(!player.getAbilities().instabuild)held.shrink(1);
            if(held.isEmpty())return InteractionResultHolder.sidedSuccess(collected,false);
            if(!player.getInventory().add(collected))player.drop(collected,false);
        }
        return InteractionResultHolder.sidedSuccess(held,level.isClientSide);
    }
    @Override
    public void appendHoverText(ItemStack stack,TooltipContext context,
                                List<Component> tooltip,TooltipFlag flags) {
        super.appendHoverText(stack,context,tooltip,flags);
        if(!filled)return;
        tooltip.add(Component.translatable("tooltip.gugugaga.v38_water_salinity",
                WaterEcologyV38.salinity(stack)));
        tooltip.add(Component.translatable("tooltip.gugugaga.v38_water_volume",
                WaterEcologyV38.volume(stack)));
        if(WaterEcologyV38.salinity(stack)>=5)
            tooltip.add(Component.translatable("tooltip.gugugaga.v38_not_drinkable"));
    }
}
