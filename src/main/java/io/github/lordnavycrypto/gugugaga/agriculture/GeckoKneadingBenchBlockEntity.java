package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Client-rendered animation state for the dough bench.
 * The existing ACTIVE blockstate is the single source of truth, so the
 * animation is synchronized by Minecraft's normal blockstate updates.
 */
public final class GeckoKneadingBenchBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("animation.kneading_bench.idle");
    private static final RawAnimation WORK =
            RawAnimation.begin().thenLoop("animation.kneading_bench.work");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoKneadingBenchBlockEntity(BlockPos pos, BlockState state) {
        super(GeckoKneadingBenchContent.DOUGH_BENCH.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "kneading", 3, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<GeckoKneadingBenchBlockEntity> state) {
        BlockState current = getBlockState();
        if (current.hasProperty(KitchenV3AnimatedBlock.ACTIVE)
                && current.getValue(KitchenV3AnimatedBlock.ACTIVE)) {
            return state.setAndContinue(WORK);
        }
        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
