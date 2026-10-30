package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Stage 0 empty -> 1 brine -> 2 concentrated -> 3 salt crystals.
 * One sampled water bucket consumed per batch, one existing sea salt output.
 * All progression is stored in block state. Timers stop when unloaded;
 * breaking a filled pan loses its batch, never duplicates water or salt.
 */
public final class SaltEvaporationPanV38 extends Block {
    public static final IntegerProperty STAGE=IntegerProperty.create("stage",0,3);
    public SaltEvaporationPanV38(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STAGE,0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(STAGE);
    }
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,
            Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit) {
        if(!stack.is(WaterV38Content.SAMPLED_WATER_BUCKET.get()))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(state.getValue(STAGE)!=0)return ItemInteractionResult.FAIL;
        if(!WaterEcologyV38.valid(stack))return ItemInteractionResult.FAIL;
        int salinity=WaterEcologyV38.salinity(stack);
        if(salinity<5) {
            if(!level.isClientSide)
                player.displayClientMessage(Component.translatable(
                        "message.gugugaga.v38_pan_needs_saltwater"),true);
            return ItemInteractionResult.FAIL;
        }
        if(!level.isClientSide) {
            level.setBlock(pos,state.setValue(STAGE,1),3);
            if(!player.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack empty=new ItemStack(WaterV38Content.FIELD_SAMPLING_BUCKET.get());
                if(stack.isEmpty())player.setItemInHand(hand,empty);
                else if(!player.getInventory().add(empty))player.drop(empty,false);
            }
            level.scheduleTick(pos,this,600);
        }
        return ItemInteractionResult.SUCCESS;
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state,Level level,
            BlockPos pos,Player player,BlockHitResult hit) {
        if(state.getValue(STAGE)==3) {
            if(!level.isClientSide) {
                ItemStack salt=new ItemStack(FoodV34Content.SEA_SALT.get());
                if(!player.getInventory().add(salt))player.drop(salt,false);
                level.setBlock(pos,state.setValue(STAGE,0),3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(!level.isClientSide) {
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.v38_pan_stage_"+state.getValue(STAGE)),true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    private boolean heated(ServerLevel level,BlockPos pos) {
        BlockState below=level.getBlockState(pos.below());
        return (below.is(Blocks.CAMPFIRE) && below.getValue(CampfireBlock.LIT))
                || below.is(Blocks.MAGMA_BLOCK);
    }
    @Override
    protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        int stage=state.getValue(STAGE);
        if(stage==0 || stage==3)return;
        boolean hot=heated(level,pos);
        // Solar evaporation needs actual daylight/exposure and dry weather.
        if(!hot && (!level.canSeeSky(pos.above())
                || level.isRainingAt(pos.above())
                || level.getDayTime()%24000L>12000L)) {
            level.scheduleTick(pos,this,400);
            return;
        }
        int delay=hot?200:600;
        if(!hot) {
            var habitat=LunarSeasonsV36.habitat(LunarSeasonsV36.biomeId(level,pos));
            if(habitat==LunarSeasonsV36.Habitat.ARID)delay=350;
            if(habitat==LunarSeasonsV36.Habitat.COLD)delay=1000;
            var season=LunarSeasonsV36.localSeason(
                    LunarSeasonsV36.absoluteDay(level),habitat);
            if(season==LunarSeasonsV36.Season.SUMMER)
                delay=Math.max(200,delay-150);
            if(season==LunarSeasonsV36.Season.WINTER)
                delay+=350;
        }
        level.setBlock(pos,state.setValue(STAGE,stage+1),3);
        if(stage+1<3)level.scheduleTick(pos,this,delay);
    }
}
