package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;

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

    private final String seedName;
    private final PollinationGroup pollinationGroup;

    public AgricultureCropBlock(BlockBehaviour.Properties properties,
                                String seedName,
                                PollinationGroup pollinationGroup) {
        super(properties);
        this.seedName = seedName;
        this.pollinationGroup = pollinationGroup;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, seedName));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!CropClimate.allowGrowth(seedName, level, pos, random)) return;
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
            String species = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                    .getKey(bee.getType()).getPath();
            if (!compatibleSpecies(species)
                    || (species.equals("bee") && !isCompatible(ecotype))) {
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

    private boolean compatibleSpecies(String species) {
        return switch (species) {
            case "bumblebee" -> true;
            case "mining_bee" -> pollinationGroup == PollinationGroup.ORCHARD
                    || pollinationGroup == PollinationGroup.BERRY;
            case "sweat_bee" -> true;
            case "mason_bee" -> pollinationGroup == PollinationGroup.ORCHARD
                    || pollinationGroup == PollinationGroup.BERRY;
            case "squash_bee" -> seedName.equals("cucumber_seeds");
            case "leafcutter_bee" -> pollinationGroup == PollinationGroup.ORCHARD
                    || seedName.equals("cucumber_seeds");
            default -> true;
        };
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
