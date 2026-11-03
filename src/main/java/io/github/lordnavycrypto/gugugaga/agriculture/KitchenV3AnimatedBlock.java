package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

/**
 * Timed visual state for manual kitchen equipment.
 *
 * The ACTIVE flag switches the station's baked model to a distinct working
 * frame, then automatically resets after 10 ticks. It never stores inventory
 * or produces items on its own. All actual ingredient conversion remains
 * server-authoritative inside KitchenProcessingBlock.
 */
public class KitchenV3AnimatedBlock extends KitchenProcessingBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private final Kind animationKind;

    public KitchenV3AnimatedBlock(Kind kind, BlockBehaviour.Properties properties) {
        super(kind, properties);
        this.animationKind = kind;
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    public void startWork(ServerLevel level, BlockPos pos) {
        BlockState current = level.getBlockState(pos);
        if (!current.is(this)) {
            return;
        }
        level.setBlock(pos, current.setValue(ACTIVE, true), Block.UPDATE_CLIENTS);
        level.scheduleTick(pos, this, 10);
    }

    /**
     * Client-only ambient work feedback. The server still owns the one-shot
     * ACTIVE state and the finite 10-tick window; no block entity or particle
     * ticker is introduced. Each station gets a distinct lightweight effect.
     */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(ACTIVE)) {
            return;
        }
        double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.34D;
        double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.34D;
        switch (animationKind) {
            case KNEAD -> {
                if (random.nextFloat() < 0.42F) {
                    level.addParticle(ParticleTypes.POOF, x, pos.getY() + 0.79D, z,
                            (random.nextDouble() - 0.5D) * 0.008D,
                            0.006D + random.nextDouble() * 0.006D,
                            (random.nextDouble() - 0.5D) * 0.008D);
                }
            }
            case FRY -> {
                if (random.nextFloat() < 0.34F) {
                    level.addParticle(ParticleTypes.SMOKE, x, pos.getY() + 0.84D, z,
                            0.0D, 0.018D + random.nextDouble() * 0.012D, 0.0D);
                }
                if (random.nextFloat() < 0.10F) {
                    level.addParticle(ParticleTypes.FLAME, x, pos.getY() + 0.72D, z,
                            0.0D, 0.012D, 0.0D);
                }
            }
            case STEAM -> {
                if (random.nextFloat() < 0.48F) {
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, pos.getY() + 0.92D, z,
                            (random.nextDouble() - 0.5D) * 0.004D,
                            0.018D + random.nextDouble() * 0.012D,
                            (random.nextDouble() - 0.5D) * 0.004D);
                }
            }
            default -> {
                // Other legacy KitchenProcessingBlock kinds do not use this subclass.
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), Block.UPDATE_CLIENTS);
        }
    }
}
