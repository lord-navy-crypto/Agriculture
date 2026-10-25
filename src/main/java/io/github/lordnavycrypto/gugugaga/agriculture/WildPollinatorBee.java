package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;

/**
 * Wild bee gameplay identity shared by the existing registered pollinator species.
 *
 * Keep native Bee flight/flower-seeking behavior for compatibility, but do not
 * model solitary and bumblebee nests as managed honeybee colonies. Our separate
 * habitat blocks represent nesting conditions, not vanilla honey inventories.
 *
 * This is not yet a complete species-specific nest or brood life cycle.
 */
public final class WildPollinatorBee extends Bee {
    public WildPollinatorBee(EntityType<? extends Bee> type, Level level) {
        super(type, level);
    }

    /** Wild bees must not discover or claim managed honeybee hives. */
    @Override
    public boolean hasHive() {
        return false;
    }

    /** Ignores legacy HivePos data from earlier saved wild-bee entities. */
    @Override
    public BlockPos getHivePos() {
        return null;
    }

    /** Rejects hive assignments attempted by vanilla flower/hive goals. */
    @Override
    public void setHivePos(BlockPos pos) {
        // Nest habitat activity is modeled by BeeNestHabitatBlock instead.
    }

    /** Distinct species cannot breed into one another or into vanilla honeybees. */
    @Override
    public boolean canMate(Animal other) {
        return other.getType() == getType() && super.canMate(other);
    }

    /** Breeding preserves the entity type instead of creating a vanilla bee. */
    @Override
    public Bee getBreedOffspring(ServerLevel level, AgeableMob mate) {
        return (Bee) getType().create(level);
    }
}
