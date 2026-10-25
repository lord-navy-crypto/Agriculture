package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Lightweight habitat prototype. A matching bee plus nearby forage advances the habitat stage.
 * It avoids chunk loading; custom home-target AI and full colony/brood simulation are future work.
 */
public final class BeeHabitatBlock extends Block {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    private final String beeId;
    private final Supplier<? extends Item> reward;
    private final boolean cucurbitOnly;

    public BeeHabitatBlock(String beeId, Supplier<? extends Item> reward, boolean cucurbitOnly,
                           BlockBehaviour.Properties properties) {
        super(properties.randomTicks());
        this.beeId = beeId;
        this.reward = reward;
        this.cucurbitOnly = cucurbitOnly;
        registerDefaultState(defaultBlockState().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    private boolean hasSuitableGround(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.DIRT) || below.is(Blocks.GRASS_BLOCK)
                || below.is(Blocks.COARSE_DIRT) || below.is(Blocks.ROOTED_DIRT)
                || below.is(Blocks.SAND) || below.is(Blocks.RED_SAND)
                || below.is(Blocks.PODZOL) || below.is(Blocks.MYCELIUM);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(STAGE);
        if (stage >= 3 || random.nextInt(2) != 0) return;
        if (cucurbitOnly && !hasSuitableGround(level, pos)) return;
        if (!hasMatchingBee(level, pos) || !hasForage(level, pos)) return;
        level.setBlock(pos, state.setValue(STAGE, stage + 1), 3);
    }

    private boolean hasMatchingBee(ServerLevel level, BlockPos pos) {
        for (Bee bee : level.getEntitiesOfClass(Bee.class, new AABB(pos).inflate(8.0D), Bee::isAlive)) {
            var key = BuiltInRegistries.ENTITY_TYPE.getKey(bee.getType());
            if (key != null && key.getNamespace().equals("gugugaga") && key.getPath().equals(beeId)) return true;
        }
        return false;
    }

    private boolean hasForage(ServerLevel level, BlockPos pos) {
        for (BlockPos candidate : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 2, 4))) {
            if (!level.hasChunkAt(candidate)) continue;
            BlockState nearby = level.getBlockState(candidate);
            if (!cucurbitOnly && nearby.is(BlockTags.FLOWERS)) return true;
            if (nearby.getBlock() instanceof AgricultureCropBlock crop
                    && nearby.getValue(AgricultureCropBlock.AGE) >= 4
                    && crop.getPollinationGroup() != AgricultureCropBlock.PollinationGroup.NONE) {
                if (!cucurbitOnly || crop.getPollinationGroup() == AgricultureCropBlock.PollinationGroup.CUCURBIT) return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        int stage = state.getValue(STAGE);
        if (stage < 3) {
            player.displayClientMessage(Component.translatable("message.gugugaga.nest_progress", stage, 3), true);
            return InteractionResult.CONSUME;
        }
        ItemStack output = new ItemStack(reward.get());
        if (!player.getInventory().add(output)) player.drop(output, false);
        level.setBlock(pos, state.setValue(STAGE, 0), 3);
        player.displayClientMessage(Component.translatable("message.gugugaga.nest_collected", output.getHoverName()), true);
        return InteractionResult.CONSUME;
    }
}
