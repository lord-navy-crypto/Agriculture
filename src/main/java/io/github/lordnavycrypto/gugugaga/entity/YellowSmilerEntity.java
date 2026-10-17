package io.github.lordnavycrypto.gugugaga.entity;

import io.github.lordnavycrypto.gugugaga.ai.ThreeEntityReactionPolicy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;

/**
 * Original blocky yellow smile NPC.
 * It behaves like a harmless ally but makes the Redactor immediately flee.
 * Its later deception/betrayal belongs to future chapters, not this prototype.
 */
public final class YellowSmilerEntity extends PathfinderMob {
    private int observationTicks;
    public YellowSmilerEntity(EntityType<? extends PathfinderMob> type,Level level) {
        super(type,level);
        setPersistenceRequired();
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
    public void tick() {
        super.tick();
        if(level().isClientSide || !(level() instanceof ServerLevel world)
                || tickCount%20!=0)return;
        // An unmoving, always-smiling figure that makes other entities run
        // is stranger than a noisy auto-attacking boss.
        getNavigation().stop();
        observationTicks=Math.min(10000,observationTicks+20);
        var birds=world.getEntitiesOfClass(GugugagaEntity.class,
                getBoundingBox().inflate(22.0),
                net.minecraft.world.entity.Entity::isAlive);
        GugugagaEntity nearest=birds.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr)).orElse(null);
        double guguSq=nearest==null?Double.POSITIVE_INFINITY:distanceToSqr(nearest);
        if(ThreeEntityReactionPolicy.smiler(guguSq)
                ==ThreeEntityReactionPolicy.SmilerAction.WATCH_GUGU) {
            getLookControl().setLookAt(nearest,5f,5f);
        } else {
            // A pleasant public face; does not read all chat or send unsolicited
            // persistent prompts. It merely looks at a nearby player.
            var players=world.getEntitiesOfClass(ServerPlayer.class,
                    getBoundingBox().inflate(12.0),
                    p->p.isAlive()&&!p.isSpectator());
            ServerPlayer nearestPlayer=players.stream()
                    .min(Comparator.comparingDouble(this::distanceToSqr))
                    .orElse(null);
            if(nearestPlayer!=null)getLookControl().setLookAt(nearestPlayer,5f,5f);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("YellowSmilerObservedTicks",observationTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        observationTicks=Math.max(0,Math.min(10000,
                tag.getInt("YellowSmilerObservedTicks")));
    }
}
