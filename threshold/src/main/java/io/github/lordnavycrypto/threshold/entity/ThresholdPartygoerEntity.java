package io.github.lordnavycrypto.threshold.entity;

import io.github.lordnavycrypto.threshold.dimension.ThresholdDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * A non-lethal Overworld lure. It approaches slowly, then relocates a nearby
 * player to the Level 0 entry after a sustained close-range encounter.
 */
public final class ThresholdPartygoerEntity extends PathfinderMob implements GeoEntity {
    private static final RawAnimation STILL = RawAnimation.begin().thenLoop("animation.threshold_watcher.still");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int closeTicks;

    public ThresholdPartygoerEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.19D)
                .add(Attributes.FOLLOW_RANGE, 36.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.65D));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 18.0F));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel current)) return;
        if (!(getTarget() instanceof ServerPlayer player) || !player.isAlive()) {
            closeTicks = 0;
            return;
        }

        double distance = distanceToSqr(player);
        if (distance <= 7.0D) {
            getNavigation().stop();
            closeTicks++;
            if (closeTicks >= 100) {
                ServerLevel destination = current.getServer().getLevel(ThresholdDimensions.LEVEL_0);
                if (destination != null) {
                    player.teleportTo(destination, 4.5D, 65.0D, 4.5D,
                            player.getYRot(), player.getXRot());
                    player.displayClientMessage(
                            Component.literal("The party has already begun."), true);
                    discard();
                }
            }
        } else {
            closeTicks = Math.max(0, closeTicks - 2);
            if (tickCount % 10 == 0) getNavigation().moveTo(player, 0.72D);
        }

        if (distance > 55.0D * 55.0D && tickCount > 2400) discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "presence", 5,
                state -> state.setAndContinue(STILL)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
