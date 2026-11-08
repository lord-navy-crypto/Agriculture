package io.github.lordnavycrypto.threshold;

import io.github.lordnavycrypto.threshold.block.ThresholdEnvironmentBlocks;
import io.github.lordnavycrypto.threshold.item.ThresholdItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Dedicated Backrooms creative inventory, separated into building and encounter supplies. */
public final class ThresholdCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThresholdMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BACKROOMS_BLOCKS =
            TABS.register("backrooms_blocks", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.threshold.backrooms_blocks"))
                    .icon(() -> new ItemStack(ThresholdEnvironmentBlocks.FADED_WALLPAPER.get()))
                    .displayItems((parameters, output) ->
                            ThresholdEnvironmentBlocks.ITEMS.getEntries()
                                    .forEach(item -> output.accept(item.get())))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BACKROOMS_ENTITIES =
            TABS.register("backrooms_entities", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.threshold.backrooms_entities"))
                    .icon(() -> new ItemStack(ThresholdItems.WATCHER_SPAWN_EGG.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ThresholdItems.WATCHER_SPAWN_EGG.get());
                        output.accept(ThresholdItems.HOUND_SPAWN_EGG.get());
                        output.accept(ThresholdItems.PARTYGOER_SPAWN_EGG.get());
                        output.accept(ThresholdItems.ALMOND_WATER.get());
                        output.accept(ThresholdItems.FLASHLIGHT.get());
                        output.accept(ThresholdItems.BATTERY.get());
                    })
                    .build());

    private ThresholdCreativeTabs() {}
}
