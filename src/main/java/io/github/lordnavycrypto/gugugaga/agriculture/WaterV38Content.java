package io.github.lordnavycrypto.gugugaga.agriculture;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Additive v3.8 water ecology; vanilla bucket remains unchanged. */
public final class WaterV38Content {
    private WaterV38Content(){}
    public static final DeferredRegister.Blocks BLOCKS=
            DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS=
            DeferredRegister.createItems(GuguGagaMod.MOD_ID);

    public static final DeferredItem<Item> FIELD_SAMPLING_BUCKET=
            ITEMS.register("field_sampling_bucket",()->new FieldSamplingBucketV38(
                    new Item.Properties().stacksTo(1),false));
    public static final DeferredItem<Item> SAMPLED_WATER_BUCKET=
            ITEMS.register("sampled_water_bucket",()->new FieldSamplingBucketV38(
                    new Item.Properties().stacksTo(1),true));
    public static final DeferredBlock<SaltEvaporationPanV38> SALT_EVAPORATION_PAN=
            BLOCKS.register("salt_evaporation_pan",()->new SaltEvaporationPanV38(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                            .strength(1.4F).sound(SoundType.STONE).noOcclusion()));
    public static final DeferredItem<BlockItem> SALT_EVAPORATION_PAN_ITEM=
            ITEMS.register("salt_evaporation_pan",()->new BlockItem(
                    SALT_EVAPORATION_PAN.get(),new Item.Properties()));
}
