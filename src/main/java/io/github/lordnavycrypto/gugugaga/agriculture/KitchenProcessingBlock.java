package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Small, manual food-processing stations. Right-click with one ingredient;
 * each successful operation consumes exactly one input and returns one result.
 * No block entity, passive output, ticking, fuel, or chunk loading.
 */
public final class KitchenProcessingBlock extends Block {
    public enum Kind {
        CHOP(Map.of(
                "gugugaga:tomato", "diced_tomato",
                "gugugaga:onion", "chopped_onion",
                "gugugaga:cucumber", "sliced_cucumber",
                "gugugaga:cabbage", "shredded_cabbage")),
        MILL(Map.of(
                "gugugaga:rice", "rice_flour",
                "gugugaga:buckwheat", "buckwheat_flour",
                "minecraft:wheat", "wheat_flour",
                "gugugaga:sesame", "sesame_meal")),
        PRESS(Map.of(
                "gugugaga:grape", "grape_juice",
                "gugugaga:strawberry", "strawberry_juice",
                "gugugaga:pineapple", "pineapple_juice",
                "gugugaga:tomato", "tomato_juice")),
        MIX(Map.of(
                "gugugaga:diced_tomato", "tomato_puree",
                "gugugaga:sliced_cucumber", "cucumber_relish",
                "gugugaga:strawberry_juice", "berry_compote_base",
                "gugugaga:wheat_flour", "flatbread_dough"));

        private final Map<String, String> transformations;

        Kind(Map<String, String> transformations) {
            this.transformations = transformations;
        }

        public Map<String, String> transformations() {
            return transformations;
        }
    }

    private static final VoxelShape LOW = Block.box(1, 0, 1, 15, 3, 15);
    private static final VoxelShape MILL = Block.box(2, 0, 2, 14, 10, 14);
    private static final VoxelShape PRESS = Block.box(1, 0, 1, 15, 16, 15);
    private static final VoxelShape BOWL = Block.box(3, 0, 3, 13, 7, 13);

    private final Kind kind;

    public KitchenProcessingBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(properties);
        this.kind = kind;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                  CollisionContext context) {
        return switch (kind) {
            case CHOP -> LOW;
            case MILL -> MILL;
            case PRESS -> PRESS;
            case MIX -> BOWL;
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                           CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND || stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        String input = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        String outputName = kind.transformations().get(input);
        if (outputName == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        // Mixing uses a reusable spoon held in the other hand: an actual utensil,
        // not a consumable ingredient silently destroyed by crafting.
        if (kind == Kind.MIX && !player.getOffhandItem().is(KitchenContent.WOODEN_SPOON.get())) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        "message.gugugaga.kitchen_requires_spoon"), true);
            }
            return ItemInteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        Item resultItem = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("gugugaga", outputName));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        ItemStack result = new ItemStack(resultItem);
        if (!player.getInventory().add(result)) {
            level.addFreshEntity(new ItemEntity(level,
                    pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D, result));
        }
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 1.1F);
        player.displayClientMessage(Component.translatable(
                "message.gugugaga.kitchen_processed", resultItem.getDescription()), true);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.kitchen_hint_" + kind.name().toLowerCase()), true);
        }
        return InteractionResult.SUCCESS;
    }
}
