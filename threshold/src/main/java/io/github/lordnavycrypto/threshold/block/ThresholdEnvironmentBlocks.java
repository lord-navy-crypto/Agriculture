package io.github.lordnavycrypto.threshold.block;

import io.github.lordnavycrypto.threshold.ThresholdMod;
import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;

import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Distinct architectural identity; no Agriculture classes are imported. */
public final class ThresholdEnvironmentBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ThresholdMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ThresholdMod.MOD_ID);

    public static final DeferredBlock<Block> FADED_WALLPAPER = BLOCKS.registerSimpleBlock(
            "faded_wallpaper", BlockBehaviour.Properties.ofFullCopy(Blocks.YELLOW_TERRACOTTA));
    public static final DeferredBlock<Block> STAINED_WALLPAPER = BLOCKS.registerSimpleBlock(
            "stained_wallpaper", BlockBehaviour.Properties.ofFullCopy(Blocks.YELLOW_CONCRETE));
    public static final DeferredBlock<Block> WAREHOUSE_WALL = BLOCKS.registerSimpleBlock(
            "warehouse_wall", BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHT_GRAY_CONCRETE));
    public static final DeferredBlock<ThresholdTransitionWallBlock> TRANSITION_WALL =
            BLOCKS.registerBlock("transition_wall", ThresholdTransitionWallBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.YELLOW_TERRACOTTA));

    // Source-backed passages. Level 0 <-> Level 1 remains in its original transition wall.
    public static final DeferredBlock<ThresholdPassageBlock> LEVEL_1_TO_2 =
            BLOCKS.registerBlock("level_1_to_2", p -> new ThresholdPassageBlock(
                    ThresholdDimensions.LEVEL_2, 4.5, 64.0, 4.5,
                    "Level 2 — Abandoned Utility Halls", p),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<ThresholdPassageBlock> LEVEL_2_TO_1 =
            BLOCKS.registerBlock("level_2_to_1", p -> new ThresholdPassageBlock(
                    ThresholdDimensions.LEVEL_1, 4.5, 64.0, 4.5,
                    "Level 1 — The Habitable Zone", p),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<ThresholdPassageBlock> LEVEL_2_TO_3 =
            BLOCKS.registerBlock("level_2_to_3", p -> new ThresholdPassageBlock(
                    ThresholdDimensions.LEVEL_3, 4.5, 64.0, 4.5,
                    "Level 3 — Electrical Station", p),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<ThresholdRouteBlock> LEVEL_2_BRANCH_DOOR =
            BLOCKS.registerBlock("level_2_branch_door", p -> new ThresholdRouteBlock(
                    List.of(
                            new ThresholdRouteBlock.RouteOption(
                                    ThresholdDimensions.LEVEL_1, 4.5, 64.0, 4.5,
                                    "Level 1 — The Habitable Zone", 4),
                            new ThresholdRouteBlock.RouteOption(
                                    ThresholdDimensions.LEVEL_3, 4.5, 64.0, 4.5,
                                    "Level 3 — Electrical Station", 4),
                            new ThresholdRouteBlock.RouteOption(
                                    ThresholdDimensions.LEVEL_4, 4.5, 64.0, 4.5,
                                    "Level 4 — Abandoned Office", 1)
                    ), 0x2B4A6D31L, p),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<ThresholdPassageBlock> LEVEL_3_TO_2 =
            BLOCKS.registerBlock("level_3_to_2", p -> new ThresholdPassageBlock(
                    ThresholdDimensions.LEVEL_2, 4.5, 64.0, 4.5,
                    "Level 2 — Abandoned Utility Halls", p),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<ThresholdPassageBlock> LEVEL_3_TO_4 =
            BLOCKS.registerBlock("level_3_to_4", p -> new ThresholdPassageBlock(
                    ThresholdDimensions.LEVEL_4, 4.5, 64.0, 4.5,
                    "Level 4 — Abandoned Office", p),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final DeferredBlock<ThresholdPassageBlock> LEVEL_4_TO_3 =
            BLOCKS.registerBlock("level_4_to_3", p -> new ThresholdPassageBlock(
                    ThresholdDimensions.LEVEL_3, 4.5, 64.0, 4.5,
                    "Level 3 — Electrical Station", p),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    public static final DeferredBlock<FlickeringPanelBlock> FLICKERING_PANEL =
            BLOCKS.registerBlock("flickering_panel", FlickeringPanelBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN)
                            .randomTicks()
                            .lightLevel(state -> state.getValue(FlickeringPanelBlock.LIT) ? 14 : 0));

    public static final DeferredBlock<ThresholdCeilingVentBlock> CEILING_VENT =
            BLOCKS.registerBlock("ceiling_vent", ThresholdCeilingVentBlock::new,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion());

    static {
        ITEMS.registerSimpleBlockItem(CEILING_VENT);
        ITEMS.registerSimpleBlockItem(FADED_WALLPAPER);
        ITEMS.registerSimpleBlockItem(STAINED_WALLPAPER);
        ITEMS.registerSimpleBlockItem(WAREHOUSE_WALL);
        ITEMS.registerSimpleBlockItem(FLICKERING_PANEL);
        ITEMS.registerSimpleBlockItem(TRANSITION_WALL);
    }

    private ThresholdEnvironmentBlocks() {}
}
