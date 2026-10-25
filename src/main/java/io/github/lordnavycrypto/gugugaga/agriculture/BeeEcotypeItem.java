package io.github.lordnavycrypto.gugugaga.agriculture;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

/** Marks a vanilla bee with a persistent, gameplay-only pollination ecotype. */
public final class BeeEcotypeItem extends Item {
    public static final String DATA_KEY = "GuguGagaBeeEcotype";
    private final String ecotype;

    public BeeEcotypeItem(String ecotype, Properties properties) {
        super(properties);
        this.ecotype = ecotype;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player,
                                                   LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Bee bee)) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        bee.getPersistentData().putString(DATA_KEY, ecotype);
        Component name = Component.translatable("entity.gugugaga." + ecotype + "_bee");
        bee.setCustomName(name);
        bee.setCustomNameVisible(false);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.displayClientMessage(
                Component.translatable("message.gugugaga.bee_ecotype_assigned", name), true);
        return InteractionResult.SUCCESS;
    }
}
