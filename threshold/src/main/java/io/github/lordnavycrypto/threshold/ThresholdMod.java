package io.github.lordnavycrypto.threshold;

import io.github.lordnavycrypto.threshold.command.ThresholdCommands;
import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import io.github.lordnavycrypto.threshold.block.ThresholdBlockEntities;
import io.github.lordnavycrypto.threshold.worldgen.ThresholdFeatures;
import io.github.lordnavycrypto.threshold.worldgen.ThresholdRuntimeChecks;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntities;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntityAttributes;
import io.github.lordnavycrypto.threshold.entity.ThresholdEntityDirector;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ThresholdMod.MOD_ID)
public final class ThresholdMod {
    public static final String MOD_ID = "threshold";

    public ThresholdMod(IEventBus modBus, ModContainer container) {
        ThresholdSounds.SOUNDS.register(modBus);
        ThresholdEnvironmentBlocks.BLOCKS.register(modBus);
        ThresholdEnvironmentBlocks.ITEMS.register(modBus);
        ThresholdBlockEntities.TYPES.register(modBus);
        ThresholdFeatures.REGISTER.register(modBus);
        ThresholdEntities.ENTITY_TYPES.register(modBus);
        modBus.addListener(ThresholdEntityAttributes::registerAttributes);
        NeoForge.EVENT_BUS.register(ThresholdCommands.class);
        NeoForge.EVENT_BUS.register(ThresholdRuntimeChecks.class);
        NeoForge.EVENT_BUS.register(ThresholdEntityDirector.class);
        System.out.println("THRESHOLD_HORROR_RUNTIME_INIT_OK");
    }
}
