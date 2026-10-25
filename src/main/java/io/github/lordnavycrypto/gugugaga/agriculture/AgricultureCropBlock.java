package io.github.lordnavycrypto.gugugaga.agriculture;

import java.util.ArrayList;
import java.util.List;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

/**
 * Seven-age crop with climate-aware growth and a bounded, bee-assisted harvest bonus.
 * Pollination is recorded as a crop state and affects yield rather than magically
 * accelerating plant development.
 */
public final class AgricultureCropBlock extends CropBlock {
    public enum PollinationGroup {
        NONE,
        ORCHARD,
        BERRY
    }

    public static final BooleanProperty POLLINATED = BooleanProperty.create("pollinated");

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POLLINATED);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, seedName));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Weather, biome and irrigation control vegetative growth, not whether a bee
        // can visit a flower. Keep the two events independent for source-informed biology.
        boolean growthAllowed = CropClimate.allowGrowth(seedName, level, pos, random);

        BlockState current = level.getBlockState(pos);
        if (pollinationGroup != PollinationGroup.NONE
                && !current.getValue(POLLINATED)
                && current.getValue(AGE) >= 4
                && current.getValue(AGE) < getMaxAge()
                && random.nextInt(3) == 0) {
            // The crop is queried only during flowering/reproductive stages and only
            // until pollinated. This avoids a per-tick entity scan or background task.
            AABB searchArea = new AABB(pos).inflate(6.0D, 3.0D, 6.0D);
            for (Bee bee : level.getEntitiesOfClass(Bee.class, searchArea, Bee::isAlive)) {
                String ecotype = bee.getPersistentData().getString(BeeEcotypeItem.DATA_KEY);
                String species = BuiltInRegistries.ENTITY_TYPE.getKey(bee.getType()).getPath();
                if (!compatibleSpecies(species, ecotype)) {
                    continue;
                }

                double dx = bee.getX() - (pos.getX() + 0.5D);
                double dy = bee.getY() - (pos.getY() + 0.5D);
                double dz = bee.getZ() - (pos.getZ() + 0.5D);
                double radius = effectiveRadius(species, ecotype);
                if (dx * dx + dy * dy + dz * dz > radius * radius) {
                    continue;
                }

                BlockState latest = level.getBlockState(pos);
                if (latest.getBlock() == this
                        && latest.getValue(AGE) >= 4
                        && latest.getValue(AGE) < getMaxAge()
                        && !latest.getValue(POLLINATED)) {
                    level.setBlock(pos, latest.setValue(POLLINATED, true), 3);
                    level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D,
                            3, 0.2D, 0.15D, 0.2D, 0.0D);
                }
                break;
            }
        }

        // Read the latest state so growth preserves a pollination mark set above.
        // Pollination is permitted even when a climate roll prevents growth this tick.
        if (growthAllowed) {
            super.randomTick(level.getBlockState(pos), level, pos, random);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, builder));
        if (pollinationGroup != PollinationGroup.NONE
                && state.getValue(AGE) == getMaxAge()
                && state.getValue(POLLINATED)) {
            String produceName = seedName.substring(0, seedName.length() - "_seeds".length());
            Item produce = BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, produceName));
            ItemStack bonusProduce = new ItemStack(produce);
            if (!bonusProduce.isEmpty()) {
                drops.add(bonusProduce);
            }
        }
        return drops;
    }

    private boolean compatibleSpecies(String species, String ecotype) {
        // Tomatoes/eggplants can self-pollinate; bumblebee buzz visits give the modeled
        // *optional* extra fruit bonus. Nearby ordinary honeybees do not produce this bonus.
        if ((seedName.equals("tomato_seeds") || seedName.equals("eggplant_seeds"))
                && !species.equals("bumblebee")) return false;
        if ("bee".equals(species)) {
            return isCompatible(ecotype);
        }
        return switch (species) {
            case "bumblebee" -> true;
            case "mason_bee" -> pollinationGroup == PollinationGroup.ORCHARD
                    || pollinationGroup == PollinationGroup.BERRY;
            case "squash_bee" -> seedName.equals("zucchini_seeds");
            case "leafcutter_bee" -> pollinationGroup == PollinationGroup.BERRY
                    || seedName.equals("buckwheat_seeds")
                    || seedName.equals("sunflower_seed_seeds");
            case "carpenter_bee" -> pollinationGroup == PollinationGroup.ORCHARD
                    || pollinationGroup == PollinationGroup.BERRY;
            case "mining_bee" -> pollinationGroup == PollinationGroup.BERRY
                    || seedName.equals("buckwheat_seeds");
            default -> false;
        };
    }

    private boolean isCompatible(String ecotype) {
        // Untagged vanilla bees are generalist meadow pollinators.
        if (ecotype.isEmpty() || "meadow".equals(ecotype)) {
            return true;
        }
        return switch (pollinationGroup) {
            case NONE -> false;
            case ORCHARD -> "orchard".equals(ecotype);
            case BERRY -> "berry".equals(ecotype);
        };
    }

    private double effectiveRadius(String species, String ecotype) {
        if ("squash_bee".equals(species)) return 6.0D;
        if ("bumblebee".equals(species) || "mason_bee".equals(species)
                || "leafcutter_bee".equals(species)) return 5.0D;
        if ("orchard".equals(ecotype)) return 6.0D;
        if ("berry".equals(ecotype)) return 5.0D;
        return 4.0D;
    }
}
