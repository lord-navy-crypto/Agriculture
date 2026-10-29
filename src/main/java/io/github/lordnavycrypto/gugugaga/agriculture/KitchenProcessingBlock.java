package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.CampfireBlock;
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
public class KitchenProcessingBlock extends Block {
    public enum Kind {
        CHOP(Map.ofEntries(
                Map.entry("gugugaga:tomato", "diced_tomato"),
                Map.entry("gugugaga:onion", "chopped_onion"),
                Map.entry("gugugaga:cucumber", "sliced_cucumber"),
                Map.entry("gugugaga:cabbage", "shredded_cabbage"),
                Map.entry("minecraft:carrot", "carrot_ribbons"),
                Map.entry("minecraft:potato", "diced_potato"),
                Map.entry("minecraft:beetroot", "grated_beetroot"),
                Map.entry("minecraft:pumpkin", "pumpkin_chunks"),
                Map.entry("minecraft:brown_mushroom", "sliced_brown_mushroom"),
                Map.entry("minecraft:red_mushroom", "sliced_red_mushroom"),
                Map.entry("minecraft:melon_slice", "melon_cubes"),
                Map.entry("minecraft:sweet_berries", "chopped_sweet_berries")
        )),
        MILL(Map.ofEntries(
                Map.entry("gugugaga:rice", "rice_flour"),
                Map.entry("gugugaga:buckwheat", "buckwheat_flour"),
                Map.entry("minecraft:wheat", "wheat_flour"),
                Map.entry("gugugaga:sesame", "sesame_meal"),
                Map.entry("minecraft:cocoa_beans", "cocoa_powder"),
                Map.entry("minecraft:pumpkin_seeds", "pumpkin_seed_meal"),
                Map.entry("minecraft:melon_seeds", "melon_seed_meal"),
                Map.entry("minecraft:dried_kelp", "kelp_powder"),
                Map.entry("minecraft:wheat_seeds", "wheat_bran")
        )),
        PRESS(Map.ofEntries(
                Map.entry("gugugaga:grape", "grape_juice"),
                Map.entry("gugugaga:strawberry", "strawberry_juice"),
                Map.entry("gugugaga:pineapple", "pineapple_juice"),
                Map.entry("gugugaga:tomato", "tomato_juice"),
                Map.entry("minecraft:apple", "apple_juice"),
                Map.entry("minecraft:melon_slice", "melon_juice"),
                Map.entry("minecraft:sweet_berries", "sweet_berry_juice"),
                Map.entry("minecraft:glow_berries", "glow_berry_juice"),
                Map.entry("minecraft:carrot", "carrot_juice"),
                Map.entry("minecraft:beetroot", "beetroot_juice")
        )),
        MIX(Map.ofEntries(
                Map.entry("gugugaga:diced_tomato", "tomato_puree"),
                Map.entry("gugugaga:sliced_cucumber", "cucumber_relish"),
                Map.entry("gugugaga:strawberry_juice", "berry_compote_base"),
                Map.entry("gugugaga:wheat_flour", "flatbread_dough"),
                Map.entry("gugugaga:apple_juice", "apple_syrup"),
                Map.entry("gugugaga:carrot_ribbons", "carrot_slaw"),
                Map.entry("gugugaga:pumpkin_chunks", "pumpkin_puree"),
                Map.entry("gugugaga:cocoa_powder", "cocoa_mix"),
                Map.entry("gugugaga:sweet_berry_juice", "sweet_berry_compote"),
                Map.entry("gugugaga:kelp_powder", "kelp_paste")
        )),

        SMOKE(Map.ofEntries(
                Map.entry("minecraft:cod", "smoked_cod"),
                Map.entry("minecraft:salmon", "smoked_salmon"),
                Map.entry("minecraft:beef", "smoked_beef"),
                Map.entry("minecraft:chicken", "smoked_chicken")
        )),
        KNEAD(Map.ofEntries(
                Map.entry("gugugaga:wheat_flour", "bread_dough"),
                Map.entry("gugugaga:rice_flour", "rice_dumpling_dough"),
                Map.entry("gugugaga:buckwheat_flour", "buckwheat_noodle_dough"),
                Map.entry("gugugaga:sesame_meal", "sesame_bun_dough"),
                Map.entry("gugugaga:flatbread_dough", "pizza_base"),
                Map.entry("gugugaga:cocoa_mix", "cocoa_cookie_dough"),
                Map.entry("gugugaga:wheat_bran", "bran_cracker_dough"),
                Map.entry("gugugaga:pumpkin_puree", "pumpkin_pastry_dough")
        )),
        FRY(Map.ofEntries(
                Map.entry("gugugaga:diced_potato", "crispy_potato_hash"),
                Map.entry("gugugaga:chopped_onion", "caramelized_onions"),
                Map.entry("gugugaga:sliced_brown_mushroom", "sauteed_mushrooms"),
                Map.entry("gugugaga:sliced_red_mushroom", "seared_red_mushrooms"),
                Map.entry("gugugaga:carrot_ribbons", "glazed_carrots"),
                Map.entry("gugugaga:diced_tomato", "pan_roasted_tomato"),
                Map.entry("gugugaga:smoked_beef", "seared_smoked_beef"),
                Map.entry("gugugaga:kelp_paste", "toasted_seaweed")
        )),
        STEAM(Map.ofEntries(
                Map.entry("gugugaga:rice", "steamed_rice"),
                Map.entry("gugugaga:pumpkin_chunks", "steamed_pumpkin"),
                Map.entry("gugugaga:shredded_cabbage", "steamed_cabbage"),
                Map.entry("gugugaga:diced_potato", "steamed_potato"),
                Map.entry("minecraft:carrot", "steamed_carrot"),
                Map.entry("minecraft:beetroot", "steamed_beetroot"),
                Map.entry("gugugaga:chopped_onion", "steamed_onion"),
                Map.entry("gugugaga:sliced_brown_mushroom", "steamed_mushrooms")
        ));

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
    private static final VoxelShape SMOKER = Block.box(1, 0, 1, 15, 15, 15);
    private static final VoxelShape BENCH = Block.box(1, 0, 1, 15, 12, 15);

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
            case SMOKE -> SMOKER;
            case KNEAD -> BENCH;
            case FRY -> SMOKER;
            case STEAM -> SMOKER;
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
        // Smoked foods are only prepared at a rack over real campfire smoke.
        // Reuse the vanilla smoke check instead of a custom heat/tick system.
        if (kind == Kind.SMOKE && !level.isClientSide && !CampfireBlock.isSmokeyPos(level, pos)) {
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.kitchen_requires_smoke"), true);
            return ItemInteractionResult.FAIL;
        }
        // Existing station semantics remain unchanged. v3 appliances require
        // the relevant reusable utensil (no durability cost on operation).
        if (kind == Kind.KNEAD && !player.getOffhandItem().is(KitchenV3Content.ROLLING_PIN.get())
                || kind == Kind.FRY && !player.getOffhandItem().is(KitchenV3Content.FRYING_SPATULA.get())
                || kind == Kind.STEAM && !player.getOffhandItem().is(KitchenV3Content.STEAMER_LID.get())) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        "message.gugugaga.kitchen_requires_v3_tool"), true);
            }
            return ItemInteractionResult.FAIL;
        }
        if ((kind == Kind.FRY || kind == Kind.STEAM)
                && !level.isClientSide && !CampfireBlock.isSmokeyPos(level, pos)) {
            player.displayClientMessage(Component.translatable(
                    "message.gugugaga.kitchen_requires_heat"), true);
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
        if (level instanceof ServerLevel server) {
            // Cosmetic, finite smoke/steam/food-processing particles. No
            // persistent inventories, item animation loop or client-only class.
            if (kind == Kind.FRY || kind == Kind.STEAM || kind == Kind.SMOKE) {
                server.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5D,
                        pos.getY() + 0.85D, pos.getZ() + 0.5D, 7,
                        0.18D, 0.12D, 0.18D, 0.015D);
            } else {
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D,
                        pos.getY() + 0.8D, pos.getZ() + 0.5D, 5,
                        0.22D, 0.16D, 0.22D, 0.035D);
            }
            if (this instanceof KitchenV3AnimatedBlock animated) {
                animated.startWork(server, pos);
            }
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
