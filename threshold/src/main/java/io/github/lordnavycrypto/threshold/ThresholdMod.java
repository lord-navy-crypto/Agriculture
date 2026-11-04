package io.github.lordnavycrypto.threshold;

import io.github.lordnavycrypto.threshold.command.ThresholdCommands;
import io.github.lordnavycrypto.threshold.worldgen.ThresholdFeatures;
import io.github.lordnavycrypto.threshold.worldgen.ThresholdRuntimeChecks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ThresholdMod.MOD_ID)
public final class ThresholdMod {
    public static final String MOD_ID = "threshold";

    public ThresholdMod(IEventBus modBus, ModContainer container) {
        ThresholdFeatures.REGISTER.register(modBus);
        NeoForge.EVENT_BUS.register(ThresholdCommands.class);
        NeoForge.EVENT_BUS.register(ThresholdRuntimeChecks.class);
        System.out.println("THRESHOLD_HORROR_RUNTIME_INIT_OK");
    }
}
