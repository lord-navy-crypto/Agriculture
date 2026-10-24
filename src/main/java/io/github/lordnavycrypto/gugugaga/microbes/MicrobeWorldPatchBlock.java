package io.github.lordnavycrypto.gugugaga.microbes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Interactive world-scale ecological art, not microbes visible to the naked eye. */
public final class MicrobeWorldPatchBlock extends BushBlock {
    public static final MapCodec<MicrobeWorldPatchBlock> CODEC =
            simpleCodec(MicrobeWorldPatchBlock::new);
    public static final IntegerProperty VIEW=IntegerProperty.create("view",0,2);
    private static final VoxelShape SHAPE=Block.box(2,0,2,14,3,14);

    public MicrobeWorldPatchBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(VIEW,0));
    }

    @Override public MapCodec<MicrobeWorldPatchBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(VIEW);
    }
    @Override protected boolean mayPlaceOn(BlockState soil,BlockGetter world,BlockPos pos){
        return soil.is(Blocks.GRASS_BLOCK)||soil.is(Blocks.DIRT)
            ||soil.is(Blocks.COARSE_DIRT)||soil.is(Blocks.PODZOL)
            ||soil.is(Blocks.MUD)||soil.is(Blocks.MOSS_BLOCK)
            ||soil.is(BlockTags.LEAVES);
    }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter world,
            BlockPos pos,CollisionContext context){return SHAPE;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level world,
            BlockPos pos,Player player,BlockHitResult hit){
        if(!world.isClientSide()){
            // Visible art modes, not disease status or biology-derived growth.
            world.setBlock(pos,state.setValue(VIEW,(state.getValue(VIEW)+1)%3),3);
            String id=BuiltInRegistries.BLOCK.getKey(this).getPath();
            player.displayClientMessage(Component.translatable(
                "message.gugugaga.microbeworld."+id),true);
        }
        return InteractionResult.sidedSuccess(world.isClientSide());
    }
}
