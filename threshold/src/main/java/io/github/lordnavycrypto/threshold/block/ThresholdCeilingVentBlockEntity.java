package io.github.lordnavycrypto.threshold.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * A quiet mechanical motion that provides visual life to the supposedly safe
 * Level 1 warehouse without introducing a hostile entity or AI.
 */
public final class ThresholdCeilingVentBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("animation.ceiling_vent.spin");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ThresholdCeilingVentBlockEntity(BlockPos pos, BlockState state) {
        super(ThresholdBlockEntities.CEILING_VENT.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "environment", 2,
                state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
