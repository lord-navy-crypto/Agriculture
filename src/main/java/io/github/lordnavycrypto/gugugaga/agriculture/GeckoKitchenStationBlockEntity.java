package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
 * Reusable GeoBlockEntity for all animated kitchen stations.
 * The blockstate ACTIVE flag remains the single server-authoritative work
 * signal; no client-side timer or duplicate gameplay state is introduced.
 */
public final class GeckoKitchenStationBlockEntity extends BlockEntity implements GeoBlockEntity {
    public enum Profile {
        KNEAD("kneading_bench"),
        SKILLET("skillet_station"),
        STEAMER("bamboo_steamer");

        private final String assetId;
        private final RawAnimation idle;
        private final RawAnimation work;

        Profile(String assetId) {
            this.assetId = assetId;
            this.idle = RawAnimation.begin().thenLoop("animation." + assetId + ".idle");
            this.work = RawAnimation.begin().thenLoop("animation." + assetId + ".work");
        }

        public String assetId() {
            return assetId;
        }

        private RawAnimation idleAnimation() {
            return idle;
        }

        private RawAnimation workAnimation() {
            return work;
        }
    }

    private final Profile profile;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GeckoKitchenStationBlockEntity(Profile profile, BlockPos pos, BlockState state) {
        super(GeckoKitchenStationContent.typeFor(profile), pos, state);
        this.profile = profile;
    }

    public Profile profile() {
        return profile;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "station", 3, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<GeckoKitchenStationBlockEntity> state) {
        BlockState current = getBlockState();
        if (current.hasProperty(KitchenV3AnimatedBlock.ACTIVE)
                && current.getValue(KitchenV3AnimatedBlock.ACTIVE)) {
            return state.setAndContinue(profile.workAnimation());
        }
        return state.setAndContinue(profile.idleAnimation());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
