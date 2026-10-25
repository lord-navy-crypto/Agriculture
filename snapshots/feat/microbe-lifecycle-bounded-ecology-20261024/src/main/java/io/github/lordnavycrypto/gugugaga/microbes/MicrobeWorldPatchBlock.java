package io.github.lordnavycrypto.gugugaga.microbes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
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
    public static final BooleanProperty SATELLITE=BooleanProperty.create("satellite");
    private static final Direction[] HORIZONTAL={Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
    private static final VoxelShape SHAPE=Block.box(2,0,2,14,3,14);

    public MicrobeWorldPatchBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(VIEW,0).setValue(SATELLITE,false));
    }

    @Override public MapCodec<MicrobeWorldPatchBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){
        builder.add(VIEW,SATELLITE);
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
     * Server-owned RANDOM TICKS, no external scheduler and no chunk forcing.
     * The three stages represent enlarged visual art, not growth measurements.
     * Naturally placed soil/leaf patches may create ONE satellite each.
     * Satellites are permanently marked and cannot create further satellites.
     */
    @Override public void randomTick(BlockState state,ServerLevel world,
            BlockPos pos,RandomSource random) {
        if(!NightwatchConfig.MICROBE_ECOLOGY_ENABLED.get())return;
        MicrobeLifecyclePolicy.Exhibit species=kind();
        BlockPos below=pos.below();
        if(!world.hasChunkAt(below))return;
        MicrobeLifecyclePolicy.Substrate substrate=substrateOf(world.getBlockState(below));
        boolean neighbor=false;
        if(species==MicrobeLifecyclePolicy.Exhibit.T4_PHAGE)
            neighbor=adjacent(world,pos,MicrobeWorldContent.ECOLI.get())
                &&NaturalMicrobeCatalog.documentedHostRelation(
                    "tequatrovirus_t4","escherichia_coli");
        if(species==MicrobeLifecyclePolicy.Exhibit.ECOLI)
            neighbor=adjacent(world,pos,MicrobeWorldContent.T4.get())
                &&NaturalMicrobeCatalog.documentedHostRelation(
                    "tequatrovirus_t4","escherichia_coli");
        var next=MicrobeLifecyclePolicy.advance(species,state.getValue(VIEW),
            state.getValue(SATELLITE),substrate,neighbor);
        if(next.nextView()!=state.getValue(VIEW))
            world.setBlock(pos,state.setValue(VIEW,next.nextView()),2);
        if(next.attemptSatellite()&&NightwatchConfig.MICROBE_SATELLITES_ENABLED.get()){
            // One new decorative patch at most. No recursive propagation.
            Direction dir=HORIZONTAL[random.nextInt(HORIZONTAL.length)];
            BlockPos other=pos.relative(dir);
            BlockPos support=other.below();
            if(world.hasChunkAt(other)&&world.hasChunkAt(support)
                    &&world.isEmptyBlock(other)
                    &&MicrobeLifecyclePolicy.canBranch(species,
                        substrateOf(world.getBlockState(support)))){
                BlockState child=defaultBlockState().setValue(VIEW,0)
                    .setValue(SATELLITE,true);
                if(child.canSurvive(world,other) && world.setBlock(other,child,3))
                    world.setBlock(pos,world.getBlockState(pos)
                        .setValue(SATELLITE,true),3);
            }
        }
    }

    private MicrobeLifecyclePolicy.Exhibit kind() {
        if(this==MicrobeWorldContent.BACILLUS.get())
            return MicrobeLifecyclePolicy.Exhibit.SOIL_BACILLUS;
        if(this==MicrobeWorldContent.PLANTARUM.get())
            return MicrobeLifecyclePolicy.Exhibit.PLANT_LACTIPLANTIBACILLUS;
        if(this==MicrobeWorldContent.ECOLI.get())
            return MicrobeLifecyclePolicy.Exhibit.ECOLI;
        return MicrobeLifecyclePolicy.Exhibit.T4_PHAGE;
    }

    private static MicrobeLifecyclePolicy.Substrate substrateOf(BlockState below){
        if(below.is(BlockTags.LEAVES))return MicrobeLifecyclePolicy.Substrate.LEAF;
        if(below.is(Blocks.GRASS_BLOCK)||below.is(Blocks.DIRT)
             ||below.is(Blocks.COARSE_DIRT)||below.is(Blocks.PODZOL)
             ||below.is(Blocks.MUD)||below.is(Blocks.MOSS_BLOCK))
            return MicrobeLifecyclePolicy.Substrate.SOIL;
        return MicrobeLifecyclePolicy.Substrate.OTHER;
    }

    private static boolean adjacent(ServerLevel world,BlockPos pos,Block desired){
        for(Direction dir:HORIZONTAL) {
            BlockPos other=pos.relative(dir);
            if(world.hasChunkAt(other)&&world.getBlockState(other).is(desired))
                return true;
        }
        return false;
    }

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
