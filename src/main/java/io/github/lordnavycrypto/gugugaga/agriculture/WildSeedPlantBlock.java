package io.github.lordnavycrypto.gugugaga.agriculture;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Harvestable wild relative / seed source on undisturbed natural terrain (not a planted farm crop). */
public final class WildSeedPlantBlock extends BushBlock {
    public static final MapCodec<WildSeedPlantBlock> CODEC = simpleCodec(WildSeedPlantBlock::new);

    @Override
    protected MapCodec<? extends BushBlock> codec() { return CODEC; }

    public WildSeedPlantBlock(BlockBehaviour.Properties properties) { super(properties); }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)
                || state.is(Blocks.PODZOL) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.MOSS_BLOCK);
    }
}
