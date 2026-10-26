package io.github.lordnavycrypto.gugugaga.agriculture;

import com.mojang.serialization.MapCodec;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

/** Four-stage, waterlogged marine crop harvested by hand and regrown in place. */
public final class MarinePlantCropBlock extends BushBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<MarinePlantCropBlock> CODEC = simpleCodec(MarinePlantCropBlock::new);
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public MarinePlantCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0).setValue(WATERLOGGED, true));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        if (!fluid.is(Fluids.WATER) || !fluid.isSource()) {
            return null;
        }
        return defaultBlockState().setValue(WATERLOGGED, true);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY)
                || state.is(Blocks.MUD) || state.is(Blocks.DIRT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (state.getValue(WATERLOGGED) && age < 3 && random.nextInt(4) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 3);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (state.getValue(AGE) < 3) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            String cropName = BuiltInRegistries.BLOCK.getKey(this).getPath();
            String harvestItemName = switch (cropName) {
                case "sea_lettuce_crop" -> "sea_lettuce";
                case "wakame_crop" -> "wakame";
                case "nori_crop" -> "nori_sheet";
                case "dulse_crop" -> "dulse";
                default -> "";
            };
            if (!harvestItemName.isEmpty()) {
                Item harvestItem = BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, harvestItemName));
                if (harvestItem != Items.AIR) {
                    Block.popResource(level, pos, new ItemStack(harvestItem, 1 + level.getRandom().nextInt(2)));
                }
            }
            level.setBlock(pos, state.setValue(AGE, 1), 3);
        }
        return InteractionResult.SUCCESS;
    }
}
