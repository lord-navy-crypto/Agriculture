package io.github.lordnavycrypto.threshold;

import io.github.lordnavycrypto.threshold.command.ThresholdCommands;
import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import io.github.lordnavycrypto.threshold.block.ThresholdBlockEntities;
import io.github.lordnavycrypto.threshold.worldgen.ThresholdFeatures;
import io.github.lordnavycrypto.threshold.worldgen.ThresholdRuntimeChecks;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntityAttributes;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntityDirector;
import io.github.lordnavycrypto.threshold.entity.ThresholdPlayerEvents;
import io.github.lordnavycrypto.threshold.item.ThresholdItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class ThresholdMod {
    public static final String MOD_ID = "threshold";

    private ThresholdMod() {}

    /** Register the Backrooms subsystem inside the single Backroom Agriculture mod. */
    public static void register(IEventBus modBus) {
        ThresholdSounds.SOUNDS.register(modBus);
        ThresholdEnvironmentBlocks.BLOCKS.register(modBus);
        ThresholdEnvironmentBlocks.ITEMS.register(modBus);
        ThresholdItems.ITEMS.register(modBus);
        ThresholdBlockEntities.TYPES.register(modBus);
        ThresholdFeatures.REGISTER.register(modBus);
        ThresholdEntities.ENTITY_TYPES.register(modBus);
        modBus.addListener(ThresholdEntityAttributes::registerAttributes);
        NeoForge.EVENT_BUS.register(ThresholdCommands.class);
        NeoForge.EVENT_BUS.register(ThresholdRuntimeChecks.class);
        NeoForge.EVENT_BUS.register(ThresholdEntityDirector.class);
        NeoForge.EVENT_BUS.register(ThresholdPlayerEvents.class);
        System.out.println("THRESHOLD_PARTYGOER_RESPAWN_HANDLER_READY_OK");
        System.out.println("THRESHOLD_HORROR_RUNTIME_INIT_OK");
    }
}
