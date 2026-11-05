package io.github.lordnavycrypto.threshold;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThresholdSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, ThresholdMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_0_AMBIENT =
            register("level_0_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_1_AMBIENT =
            register("level_1_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_0_ROOMTONE =
            register("level_0_roomtone");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_0_DISTANT =
            register("level_0_distant");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_1_MACHINE =
            register("level_1_machine");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_1_PIPE =
            register("level_1_pipe");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ThresholdMod.MOD_ID, path);
        return SOUNDS.register(path, () -> SoundEvent.createVariableRangeEvent(id));
    }

    private ThresholdSounds() {}
}
