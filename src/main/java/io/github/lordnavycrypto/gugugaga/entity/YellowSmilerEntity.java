package io.github.lordnavycrypto.gugugaga.entity;

import io.github.lordnavycrypto.gugugaga.ai.SmilerGiftPolicy;
import io.github.lordnavycrypto.gugugaga.ai.HauntGameplayRuntime;
import io.github.lordnavycrypto.gugugaga.config.NightwatchConfig;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Comparator;

/**
 * The Yellow Smiler is a polite, opt-in helper while masked and a visibly
 * uncanny, non-attacking revealed threat. Future story betrayal/Boss is
 * intentionally NOT implemented. Gifts require direct player interaction.
 */
public final class YellowSmilerEntity extends PathfinderMob implements software.bernie.geckolib.animatable.GeoEntity {
    private final software.bernie.geckolib.animatable.instance.AnimatableInstanceCache geoCache =
            software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache(this);
    private static final software.bernie.geckolib.animation.RawAnimation FRIENDLY =
            software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("animation.smiler.friendly");
    private static final software.bernie.geckolib.animation.RawAnimation EXPOSED =
            software.bernie.geckolib.animation.RawAnimation.begin().thenLoop("animation.smiler.exposed");
    private static final software.bernie.geckolib.animation.RawAnimation HELP =
            software.bernie.geckolib.animation.RawAnimation.begin().thenPlay("animation.smiler.help");

    @Override
    public void registerControllers(software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "smile", 8,
                state -> state.setAndContinue(revealed() ? EXPOSED : FRIENDLY)));
        controllers.add(new software.bernie.geckolib.animation.AnimationController<>(this, "gift_gesture", 0,
                state -> software.bernie.geckolib.animation.PlayState.STOP).triggerableAnim("help", HELP));
    }

    @Override
    public software.bernie.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    private static final EntityDataAccessor<Boolean> REVEALED =
            SynchedEntityData.defineId(YellowSmilerEntity.class,
                    EntityDataSerializers.BOOLEAN);
    private static final String LAST_HELP="GuguGagaSmilerLastHelp";
    private boolean stageEncounter;
    private int stageLifetimeTicks;
    public void markStageEncounter() {
        stageEncounter=true;
        stageLifetimeTicks=0;
    }

    private int observationTicks;
    private int wardOffTicks=20*8;

    public YellowSmilerEntity(EntityType<? extends PathfinderMob> type,Level level) {
        super(type,level);
        setPersistenceRequired();
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REVEALED,false);
    }
    public boolean revealed() {return getEntityData().get(REVEALED);}
    public void setRevealed(boolean exposed) {
        getEntityData().set(REVEALED,exposed);
        getNavigation().stop();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,32.0)
                .add(Attributes.MOVEMENT_SPEED,0.17)
                .add(Attributes.FOLLOW_RANGE,32.0);
    }
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(8,new RandomLookAroundGoal(this));
    }
    @Override
    protected InteractionResult mobInteract(Player player,InteractionHand hand) {
        if(level().isClientSide)return InteractionResult.SUCCESS;
        if(!(player instanceof ServerPlayer recipient) || hand!=InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;
        if(revealed()) {
            recipient.sendSystemMessage(Component.literal(
                    "[Smiler] I suppose the smile is no longer necessary."));
            return InteractionResult.SUCCESS;
        }
        long now=recipient.serverLevel().getServer().overworld().getGameTime();
        long last=recipient.getPersistentData().contains(LAST_HELP)
                ?recipient.getPersistentData().getLong(LAST_HELP):-1L;
        boolean eligible=!recipient.isSpectator() && !recipient.isCreative();
        if(!SmilerGiftPolicy.mayHelp(false,eligible,now,last)) {
            recipient.sendSystemMessage(Component.literal(
                    "[Smiler] You have enough for now. Come back later."));
            return InteractionResult.SUCCESS;
        }
        // Two vanilla torches every three game minutes, across ALL Smilers.
        // No player inventory or block is removed or changed. Inventory full
        // yields no reward and consumes no cooldown.
        if(!recipient.addItem(new ItemStack(Items.TORCH,SmilerGiftPolicy.TORCH_COUNT))) {
            recipient.sendSystemMessage(Component.literal(
                    "[Smiler] Make some room first. I can wait."));
            return InteractionResult.SUCCESS;
        }
        recipient.getPersistentData().putLong(LAST_HELP,now);
        triggerAnim("gift_gesture", "help");
        recipient.sendSystemMessage(Component.literal(
                "[Smiler] Here. Stay in the light. That penguin is strange, isn't it?"));
        return InteractionResult.SUCCESS;
    }
    @Override
    public boolean hurt(DamageSource source,float amount) {
        boolean damaged=super.hurt(source,amount);
        if(damaged && !level().isClientSide
                && source.getEntity() instanceof ServerPlayer && !revealed())
            setRevealed(true);
        return damaged;
    }
    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide && stageEncounter && ++stageLifetimeTicks>=20*360) {
            // Scripted horror actors cannot accumulate in old saves forever.
            discard();
            return;
        }
        if(level().isClientSide || !(level() instanceof ServerLevel world)
                || tickCount%20!=0)return;
        getNavigation().stop();
        observationTicks=Math.min(10000,observationTicks+20);
        wardOffTicks=Math.min(20*8,wardOffTicks+20);
        // Yellow is not afraid of Redactor. The penguin is its first target;
        // the player's trust is its reason to intervene. No cross-world scans.
        var players=world.getEntitiesOfClass(ServerPlayer.class,
                getBoundingBox().inflate(22.0),
                p->p.isAlive() && HauntGameplayRuntime.isActiveParticipant(p));
        ServerPlayer player=players.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
        var birds=world.getEntitiesOfClass(GugugagaEntity.class,
                getBoundingBox().inflate(22.0),
                net.minecraft.world.entity.Entity::isAlive);
        GugugagaEntity bird=birds.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
        double birdSq=bird==null?Double.POSITIVE_INFINITY:distanceToSqr(bird);
        double playerSq=player==null?Double.POSITIVE_INFINITY:distanceToSqr(player);
        var role=ThreeEntityReactionPolicy.smiler(birdSq,playerSq,
                Double.POSITIVE_INFINITY);
        if(role==ThreeEntityReactionPolicy.SmilerAction.CONFRONT_GUGU
                && player!=null && NightwatchConfig.CHASES_ENABLED.get()) {
            getLookControl().setLookAt(bird,14f,14f);
            if(birdSq>3*3 && tickCount%40==0)getNavigation().moveTo(bird,1.08);
            if(ThreeEntityReactionPolicy.smilerCanWardOff(birdSq,wardOffTicks)) {
                bird.retreatFromSmiler(this);
                // The Smiler tries to defeat the penguin instead of attacking
                // players or automatically destroying any game/story objects.
                bird.hurt(world.damageSources().mobAttack(this),1.0f);
                wardOffTicks=0;
                if(NightwatchConfig.HAUNTING_AUDIO_ENABLED.get())
                    player.playNotifySound(SoundEvents.AMETHYST_BLOCK_CHIME,
                            SoundSource.HOSTILE,0.28f,0.72f);
                player.displayClientMessage(Component.literal(
                        "[Smiler] See? Stay with me. We can drive it away."),true);
            }
        } else if(player!=null) {
            getNavigation().stop();
            getLookControl().setLookAt(player,6f,6f);
        } else {
            getNavigation().stop();
        }
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("YellowSmilerObservedTicks",observationTicks);
        tag.putInt("YellowSmilerWardCooldown",wardOffTicks);
        tag.putBoolean("NightwatchStageGuest",stageEncounter);
        tag.putInt("NightwatchStageGuestLifetime",stageLifetimeTicks);
        tag.putBoolean("YellowSmilerRevealed",revealed());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        observationTicks=Math.max(0,Math.min(10000,
                tag.getInt("YellowSmilerObservedTicks")));
        wardOffTicks=0; // no immediate attack after a save/reload
        setRevealed(tag.getBoolean("YellowSmilerRevealed"));
        stageEncounter=tag.getBoolean("NightwatchStageGuest");
        stageLifetimeTicks=Math.max(0,Math.min(20*360,
                tag.getInt("NightwatchStageGuestLifetime")));
    }
}
