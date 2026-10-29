package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Original Minecraft fishing is retained unchanged. In ocean biomes only,
 * a successful vanilla fish catch has a bounded 1/5 chance of an additional
 * marine-food ingredient. These are inventory catches, not new fish entities.
 */
public final class FoodEcologyFishing {
    private FoodEcologyFishing() {}
    private static final String[] SPECIES = {
            "saltwater_anchovy",
            "saltwater_sardine",
            "saltwater_herring",
            "saltwater_mackerel",
            "saltwater_sea_bass",
            "saltwater_sea_bream",
            "saltwater_shrimp",
            "saltwater_crab"
    };

    @SubscribeEvent
    public static void onFishCaught(ItemFishedEvent event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (!(level instanceof ServerLevel server)) return;
        BlockPos hookPos = event.getHookEntity().blockPosition();
        if (!server.hasChunkAt(hookPos) || !server.getBiome(hookPos).is(BiomeTags.IS_OCEAN)) return;
        if (event.getDrops().stream().noneMatch(stack -> stack.is(Items.COD)
                || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH)
                || stack.is(Items.PUFFERFISH))) return;
        if (server.random.nextInt(5) != 0) return;
        String species = SPECIES[server.random.nextInt(SPECIES.length)];
        ItemStack catchStack = new ItemStack(BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(GuguGagaMod.MOD_ID, species)));
        if (!player.getInventory().add(catchStack)) {
            player.drop(catchStack, false);
        }
    }
}
