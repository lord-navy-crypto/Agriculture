package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemLike;
import net.minecraft.world.level.block.BlockBehaviour;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

/**
 * A normal seven-age farmland crop with a small, bounded bee-assisted growth bonus.
 * The bonus is a game abstraction of pollination support, not a real-world growth rate.
 */
public final class AgricultureCropBlock extends CropBlock {
    public enum PollinationGroup {
        NONE,
        ORCHARD,
        BERRY
    }

    private final Supplier<? extends ItemLike> seed;
    private final PollinationGroup pollinationGroup;

    public AgricultureCropBlock(BlockBehaviour.Properties properties,
                                Supplier<? extends ItemLike> seed,
                                PollinationGroup pollinationGroup) {
        super(properties);
        this.seed = seed;
        this.pollinationGroup = pollinationGroup;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seed.get();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (pollinationGroup == PollinationGroup.NONE) {
            return;
        }

        BlockState current = level.getBlockState(pos);
        if (current.getBlock() != this || current.getValue(AGE) >= getMaxAge()) {
            return;
        }

        AABB searchArea = new AABB(pos).inflate(6.0D, 3.0D, 6.0D);
        for (Bee bee : level.getEntitiesOfClass(Bee.class, searchArea, Bee::isAlive)) {
            String ecotype = bee.getPersistentData().getString(BeeEcotypeItem.DATA_KEY);
            if (!isCompatible(ecotype)) {
                continue;
            }

            double dx = bee.getX() - (pos.getX() + 0.5D);
            double dy = bee.getY() - (pos.getY() + 0.5D);
            double dz = bee.getZ() - (pos.getZ() + 0.5D);
            double radius = effectiveRadius(ecotype);
            if (dx * dx + dy * dy + dz * dz > radius * radius) {
                continue;
            }

            int denominator = "orchard".equals(ecotype) ? 4
                    : "berry".equals(ecotype) ? 5 : 6;
            if (random.nextInt(denominator) == 0) {
                BlockState latest = level.getBlockState(pos);
                if (latest.getBlock() == this && latest.getValue(AGE) < getMaxAge()) {
                    level.setBlock(pos, latest.setValue(AGE, latest.getValue(AGE) + 1), 2);
                }
            }
            return;
        }
    }

    private boolean isCompatible(String ecotype) {
        // Untagged vanilla bees are the general meadow pollinator.
        if (ecotype.isEmpty() || "meadow".equals(ecotype)) {
            return true;
        }
        return switch (pollinationGroup) {
            case NONE -> false;
            case ORCHARD -> "orchard".equals(ecotype);
            case BERRY -> "berry".equals(ecotype);
        };
    }

    private double effectiveRadius(String ecotype) {
        if ("orchard".equals(ecotype)) return 6.0D;
        if ("berry".equals(ecotype)) return 5.0D;
        return 4.0D;
    }
}
