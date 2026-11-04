package io.github.lordnavycrypto.threshold.block;

import io.github.lordnavycrypto.threshold.ThresholdMod;
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
