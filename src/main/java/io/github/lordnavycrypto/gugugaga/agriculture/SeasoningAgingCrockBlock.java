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
 * Fictional, non-alcoholic Minecraft condiment aging. Three timed stages;
 * salt is a cost for pickling; a game culture item is used for aged sauces.
 * Does NOT represent a safe real-world recipe or a microbial simulation.
 */
public final class SeasoningAgingCrockBlock extends Block {
    public static final IntegerProperty BATCH = IntegerProperty.create("batch", 0, 10);
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    private static final int STAGE_TICKS = 100;

    private static final Map<String, Integer> INPUTS = Map.ofEntries(
            Map.entry("gugugaga:fermented_soybeans", 1),
            Map.entry("gugugaga:rice", 2),
            Map.entry("minecraft:apple", 3),
            Map.entry("gugugaga:fermented_shiitake", 4),
            Map.entry("gugugaga:cucumber", 5),
            Map.entry("gugugaga:daikon", 6),
            Map.entry("gugugaga:cabbage", 7),
            Map.entry("minecraft:beetroot", 8),
            Map.entry("gugugaga:chili_pepper", 9),
            Map.entry("gugugaga:smoked_anchovy", 10)
    );
    private static final String[] RESULTS = {
            "soy_sauce",
            "rice_vinegar",
            "apple_vinegar",
            "mushroom_umami_sauce",
            "pickled_cucumber_v34",
            "pickled_daikon_v34",
            "pickled_cabbage_v34",
            "pickled_beetroot_v34",
            "cultured_chili_sauce",
            "seasoned_fish_sauce"
    };

    public SeasoningAgingCrockBlock(BlockBehaviour.Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(BATCH, 0).setValue(STAGE, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BATCH, STAGE);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack main, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || state.getValue(BATCH) != 0 || main.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        Integer recipe = INPUTS.get(BuiltInRegistries.ITEM.getKey(main.getItem()).toString());
        if (recipe == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        boolean needsSalt = recipe >= 5 && recipe <= 8;
        Item catalyst = needsSalt ? FoodV34Content.SEA_SALT.get()
                                  : FoodV34Content.STARTER_CULTURE.get();
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(catalyst)) {
            if (!level.isClientSide) player.displayClientMessage(Component.translatable(
                    needsSalt ? "message.gugugaga.v34_requires_salt"
                              : "message.gugugaga.v34_requires_starter"), true);
            return ItemInteractionResult.FAIL;
        }
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!player.getAbilities().instabuild) {
            main.shrink(1);
            offhand.shrink(1);
        }
        level.setBlock(pos, state.setValue(BATCH, recipe).setValue(STAGE, 0), Block.UPDATE_CLIENTS);
        level.scheduleTick(pos, this, STAGE_TICKS);
        player.displayClientMessage(Component.translatable("message.gugugaga.v34_aging_started"), true);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                 Player player, BlockHitResult hit) {
        int batch = state.getValue(BATCH);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (batch == 0) {
            player.displayClientMessage(Component.translatable("message.gugugaga.v34_aging_hint"), true);
        } else if (state.getValue(STAGE) < 3) {
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.v34_aging_progress", state.getValue(STAGE)), true);
        } else {
            Item item = BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath("gugugaga", RESULTS[batch - 1]));
            ItemStack produced = new ItemStack(item);
            if (!player.getInventory().add(produced)) Block.popResource(level, pos, produced);
            level.setBlock(pos, state.setValue(BATCH, 0).setValue(STAGE, 0), Block.UPDATE_CLIENTS);
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.v34_aging_ready", item.getDescription()), true);
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(BATCH) == 0 || state.getValue(STAGE) == 3) return;
        int stage = state.getValue(STAGE) + 1;
        level.setBlock(pos, state.setValue(STAGE, stage), Block.UPDATE_CLIENTS);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5D, pos.getY()+0.9D,
                pos.getZ()+0.5D, 3, 0.17D, 0.10D, 0.17D, 0.01D);
        if (stage < 3) level.scheduleTick(pos, this, STAGE_TICKS);
    }
}
