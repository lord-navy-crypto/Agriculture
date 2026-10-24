package io.github.lordnavycrypto.gugugaga.microbes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
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
    /**
     * Lightweight client-only world visuals. A visible T4-to-E. coli link
     * means a DOCUMENTED SPECIES-LEVEL HOST ASSOCIATION, not an in-world
     * phage infection, strain susceptibility, dose or replication event.
     */
    @Override public void animateTick(BlockState state,Level world,
            BlockPos pos,RandomSource random){
        if(random.nextInt(10)!=0)return; // Cosmetic particle budget ONLY.
        if(this==MicrobeWorldContent.T4.get() &&
                NaturalMicrobeCatalog.documentedHostRelation(
                    "tequatrovirus_t4","escherichia_coli")){
            for(Direction facing:Direction.Plane.HORIZONTAL){
                BlockPos nearby=pos.relative(facing);
                if(world.hasChunkAt(nearby) &&
                        world.getBlockState(nearby).is(MicrobeWorldContent.ECOLI.get())){
                    world.addParticle(ParticleTypes.ENCHANT,
                        pos.getX()+0.5,pos.getY()+0.32,pos.getZ()+0.5,
                        0.0,0.025,0.0);
                    break;
                }
            }
        }else if(this==MicrobeWorldContent.BACILLUS.get()
                ||this==MicrobeWorldContent.PLANTARUM.get()){
            world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                pos.getX()+0.5,pos.getY()+0.2,pos.getZ()+0.5,
                0,0.005,0);
        }
    }

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
