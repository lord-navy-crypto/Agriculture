package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A single-batch, non-alcoholic food-culture station: 3 scheduled stages, each
 * 80 game ticks. Both batch type and maturation stage live in block state,
 * so unloading the world cannot duplicate output and no block entity is needed.
 *
 * This is fictional gameplay logic, not an actual food safety protocol.
 */
public final class FoodFermentationVatBlock extends Block {
    public static final IntegerProperty BATCH = IntegerProperty.create("batch", 0, 10);
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    private static final int TICKS_PER_STAGE = 80;
    private static final Map<String, Integer> INPUTS = Map.ofEntries(
            Map.entry("gugugaga:soybean", 1),
            Map.entry("gugugaga:cabbage", 2),
            Map.entry("gugugaga:cucumber", 3),
            Map.entry("gugugaga:daikon", 4),
            Map.entry("gugugaga:black_pepper", 5),
            Map.entry("gugugaga:lemongrass", 6),
            Map.entry("gugugaga:shiitake_mushroom", 7),
            Map.entry("gugugaga:sugar_beet", 8),
            Map.entry("minecraft:beetroot", 9),
            Map.entry("minecraft:carrot", 10)
    );
    private static final String[] OUTPUTS = { "fermented_soybeans", "cultured_cabbage", "fermented_cucumber", "fermented_daikon", "aged_pepper_paste", "fermented_lemongrass", "fermented_shiitake", "cultured_beet_relish", "pickled_beetroot", "cultured_carrot" };

    public FoodFermentationVatBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BATCH, 0).setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BATCH, STAGE);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (state.getValue(BATCH) != 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        String input = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        Integer batch = INPUTS.get(input);
        if (batch == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!player.getAbilities().instabuild) stack.shrink(1);
        level.setBlock(pos, state.setValue(BATCH, batch).setValue(STAGE, 0), Block.UPDATE_CLIENTS);
        level.scheduleTick(pos, this, TICKS_PER_STAGE);
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_FILL,
                net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 0.85F);
        player.displayClientMessage(Component.translatable(
                "message.gugugaga.v33_ferment_started"), true);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        int batch = state.getValue(BATCH);
        int stage = state.getValue(STAGE);
        if (batch == 0) {
            if (!level.isClientSide) player.displayClientMessage(
                    Component.translatable("message.gugugaga.v33_ferment_hint"), true);
            return InteractionResult.SUCCESS;
        }
        if (stage < 3) {
            if (!level.isClientSide) player.displayClientMessage(
                    Component.translatable("message.gugugaga.v33_ferment_progress", stage), true);
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide) {
            Item product = resultItem(batch);
            ItemStack produced = new ItemStack(product);
            if (!player.getInventory().add(produced)) Block.popResource(level, pos, produced);
            level.setBlock(pos, state.setValue(BATCH, 0).setValue(STAGE, 0), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 1.15F);
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.v33_ferment_ready", product.getDescription()), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(BATCH) == 0 || state.getValue(STAGE) >= 3) return;
        int stage = state.getValue(STAGE) + 1;
        level.setBlock(pos, state.setValue(STAGE, stage), Block.UPDATE_CLIENTS);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5D, pos.getY()+0.9D,
                pos.getZ()+0.5D, 4, 0.17D, 0.12D, 0.17D, 0.015D);
        if (stage < 3) level.scheduleTick(pos, this, TICKS_PER_STAGE);
    }

    private static Item resultItem(int batch) {
        return BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("gugugaga", OUTPUTS[batch - 1]));
    }
}
