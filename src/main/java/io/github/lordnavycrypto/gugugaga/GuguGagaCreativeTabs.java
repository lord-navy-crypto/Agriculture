package io.github.lordnavycrypto.gugugaga;

import io.github.lordnavycrypto.gugugaga.agriculture.AgricultureContent;
import io.github.lordnavycrypto.gugugaga.agriculture.AgricultureCuisineContent;
import io.github.lordnavycrypto.gugugaga.agriculture.BeeContent;
import io.github.lordnavycrypto.gugugaga.agriculture.MarineFungiContent;
import io.github.lordnavycrypto.gugugaga.agriculture.KitchenContent;
import io.github.lordnavycrypto.gugugaga.agriculture.KitchenV3Content;
import io.github.lordnavycrypto.gugugaga.agriculture.KitchenV31Content;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Dedicated, exhaustive creative inventory for Gugu Gaga.
 * Each tab displays every item in exactly one of the mod's item registries;
 * no mod content is injected into vanilla creative tabs.
 */
public final class GuguGagaCreativeTabs {
    private GuguGagaCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GuguGagaMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AGRICULTURE =
            TABS.register("agriculture", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gugugaga.agriculture"))
                    .icon(() -> new ItemStack(AgricultureContent.CORN_SEEDS.get()))
                    .displayItems((parameters, output) ->
                            AgricultureContent.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BEEKEEPING =
            TABS.register("beekeeping", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gugugaga.beekeeping"))
                    .icon(() -> new ItemStack(BeeContent.CLOVER_HONEY_BOTTLE.get()))
                    .displayItems((parameters, output) ->
                            BeeContent.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CUISINE =
            TABS.register("cuisine", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gugugaga.cuisine"))
                    .icon(() -> new ItemStack(AgricultureCuisineContent.GRAIN_PORRIDGE.get()))
                    .displayItems((parameters, output) ->
                            { AgricultureCuisineContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                              KitchenContent.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                              KitchenV3Content.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                              KitchenV31Content.ITEMS.getEntries().forEach(item -> output.accept(item.get())); })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARINE_FUNGI =
            TABS.register("marine_fungi", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.gugugaga.marine_fungi"))
                    .icon(() -> new ItemStack(MarineFungiContent.SEA_LETTUCE.get()))
                    .displayItems((parameters, output) ->
                            MarineFungiContent.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());
}
