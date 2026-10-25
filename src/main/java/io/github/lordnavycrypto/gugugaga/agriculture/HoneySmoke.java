package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Minecraft Java smoke column used by both honey-harvesting items. Up to five blocks vertically,
 * with air or a single carpet immediately covering the lit campfire; never force-loads chunks.
 */
public final class HoneySmoke {
    private HoneySmoke() {}

    public static boolean hasClearCampfireSmoke(Level level, BlockPos hivePos) {
        for (int distance = 1; distance <= 5; distance++) {
            BlockPos pos = hivePos.below(distance);
            if (!level.hasChunkAt(pos)) return false;

            BlockState block = level.getBlockState(pos);
            if (block.is(Blocks.CAMPFIRE) || block.is(Blocks.SOUL_CAMPFIRE)) {
                return block.getValue(BlockStateProperties.LIT);
            }
            // Carpets immediately above campfires are intentionally smoke-permeable in Java.
            if (block.getBlock() instanceof CarpetBlock && distance < 5) {
                BlockPos under = pos.below();
                if (!level.hasChunkAt(under)) return false;
                BlockState campfire = level.getBlockState(under);
                return (campfire.is(Blocks.CAMPFIRE) || campfire.is(Blocks.SOUL_CAMPFIRE))
                        && campfire.getValue(BlockStateProperties.LIT);
            }
            if (!block.isAir()) return false;
        }
        return false;
    }
}
