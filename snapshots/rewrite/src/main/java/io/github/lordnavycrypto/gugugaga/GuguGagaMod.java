package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.command.InfectionCommands;
import io.github.lordnavycrypto.gugugaga.simulation.InfectionRuntime;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Clean-room rewrite of the Gugu-Gaga Minecraft virus mod.
 * Legacy V1-V21 remains separately archived.
 */
@Mod(GuguGagaMod.MOD_ID)
public final class GuguGagaMod {
    public static final String MOD_ID="gugugaga";

    public GuguGagaMod(IEventBus modBus,ModContainer container){
        NeoForge.EVENT_BUS.addListener(InfectionCommands::register);
        NeoForge.EVENT_BUS.addListener(InfectionRuntime::onTick);
        NeoForge.EVENT_BUS.addListener(InfectionRuntime::onServerStopped);
        System.out.println("GUGUGAGA_REWRITE_RUNTIME_INIT_OK");
    }
}
