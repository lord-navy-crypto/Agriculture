package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * Source-informed crop categories and pollination rules. Bee visits mark a mature crop for a
 * small harvest bonus instead of artificially advancing the vanilla crop growth stage.
 */
public class AgricultureCropBlock extends CropBlock {
    public enum PollinationGroup {
        NONE, GENERAL, BERRY, CUCURBIT, BUZZ
    }

    private final String seedName;
    private final PollinationGroup pollinationGroup;

    public AgricultureCropBlock(BlockBehaviour.Properties properties, String seedName, PollinationGroup group) {
        super(properties);
        this.seedName = seedName;
        this.pollinationGroup = group;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, seedName));
    }

    public String getSeedName() {
        return seedName;
    }

    public PollinationGroup getPollinationGroup() {
        return pollinationGroup;
    }

    public boolean isPollinated(BlockState state) {
        return false;
    }

    protected void markPollinated(ServerLevel level, BlockPos pos, BlockState state) {
        // Non-pollinator-dependent crops intentionally have no pollination state.
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (CropClimate.allowGrowth(seedName, level, pos, random)) {
            super.randomTick(state, level, pos, random);
        }

        if (pollinationGroup == PollinationGroup.NONE) return;
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() != this || current.getValue(AGE) < 4 || isPollinated(current)) return;
        // A low-frequency entity query avoids scanning for bees on every crop random tick.
        if (random.nextInt(3) != 0) return;

        AABB searchArea = new AABB(pos).inflate(6.0D, 3.0D, 6.0D);
        for (Bee bee : level.getEntitiesOfClass(Bee.class, searchArea, Bee::isAlive)) {
            String species = BuiltInRegistries.ENTITY_TYPE.getKey(bee.getType()).getPath();
            String ecotype = bee.getPersistentData().getString(BeeEcotypeItem.DATA_KEY);
            if (!compatibleSpecies(species)
                    || (species.equals("bee") && !compatibleVanillaEcotype(ecotype))) continue;

            double dx = bee.getX() - (pos.getX() + 0.5D);
            double dy = bee.getY() - (pos.getY() + 0.5D);
            double dz = bee.getZ() - (pos.getZ() + 0.5D);
            double radius = effectiveRadius(species, ecotype);
            if (dx * dx + dy * dy + dz * dz > radius * radius) continue;

            BlockState latest = level.getBlockState(pos);
            if (latest.getBlock() == this && latest.getValue(AGE) >= 4 && !isPollinated(latest)) {
                markPollinated(level, pos, latest);
            }
            return;
        }
    }

    private boolean compatibleSpecies(String species) {
        // Generalist vanilla honeybees can pollinate fruit/berry/cucurbit crops, but cannot buzz-pollinate.
        if (species.equals("bee")) {
            return pollinationGroup != PollinationGroup.NONE && pollinationGroup != PollinationGroup.BUZZ;
        }
        return switch (pollinationGroup) {
            case NONE -> false;
            case BUZZ -> species.equals("bumblebee");
            case CUCURBIT -> species.equals("bumblebee") || species.equals("squash_bee");
            case BERRY, GENERAL -> species.equals("bumblebee") || species.equals("mason_bee")
                    || species.equals("leafcutter_bee") || species.equals("carpenter_bee")
                    || species.equals("mining_bee");
        };
    }

    private boolean compatibleVanillaEcotype(String ecotype) {
        return switch (pollinationGroup) {
            case NONE, BUZZ -> false;
            case GENERAL, CUCURBIT ->
                    ecotype.isEmpty() || ecotype.equals("meadow") || ecotype.equals("orchard");
            case BERRY -> ecotype.isEmpty() || ecotype.equals("meadow") || ecotype.equals("berry");
        };
    }

    private double effectiveRadius(String species, String ecotype) {
        if (species.equals("bumblebee")) return 6.0D;
        if (species.equals("squash_bee")) return 5.0D;
        if (species.equals("bee") && ecotype.equals("orchard")) return 6.0D;
        if (species.equals("bee") && ecotype.equals("berry")) return 5.0D;
        return 4.5D;
    }
}
