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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Four-stage renewable mushroom culture harvested by hand without consuming its culture block. */
public final class FungalCultureBlock extends BushBlock {
    public static final MapCodec<FungalCultureBlock> CODEC = simpleCodec(FungalCultureBlock::new);
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    private static final VoxelShape SHAPE = Shapes.box(0.12D, 0.0D, 0.12D, 0.88D, 0.52D, 0.88D);

    public FungalCultureBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.MYCELIUM) || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT)
                || state.is(Blocks.FARMLAND);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3 && random.nextInt(3) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 3);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (state.getValue(AGE) < 3) return InteractionResult.PASS;
        if (!level.isClientSide) {
            String cultureName = BuiltInRegistries.BLOCK.getKey(this).getPath();
            String harvestItemName = switch (cultureName) {
                case "shiitake_culture" -> "shiitake_mushroom";
                case "oyster_mushroom_culture" -> "oyster_mushroom";
                case "enoki_culture" -> "enoki_mushroom";
                case "porcini_culture" -> "porcini_mushroom";
                case "lion_mane_culture" -> "lion_mane_mushroom";
                case "truffle_culture" -> "truffle";
                default -> "";
            };
            if (!harvestItemName.isEmpty()) {
                Item harvestItem = BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, harvestItemName));
                if (harvestItem != Items.AIR) Block.popResource(level, pos, new ItemStack(harvestItem, 1));
            }
            level.setBlock(pos, state.setValue(AGE, 1), 3);
        }
        return InteractionResult.SUCCESS;
    }
}
