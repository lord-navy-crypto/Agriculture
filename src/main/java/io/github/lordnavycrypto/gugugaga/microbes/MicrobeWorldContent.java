package io.github.lordnavycrypto.gugugaga.microbes;

import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

/** Actual native-world blocks tied to taxonomy, with no inferred player infection. */
public final class MicrobeWorldContent {
    private MicrobeWorldContent(){}
    public static final DeferredRegister.Blocks BLOCKS=
        DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS=
        DeferredRegister.createItems(GuguGagaMod.MOD_ID);
    private static MicrobeWorldPatchBlock patch(){
        return new MicrobeWorldPatchBlock(BlockBehaviour.Properties.of()
            .noCollission().noOcclusion().instabreak().randomTicks().sound(SoundType.GRASS));
    }
    public static final DeferredBlock<MicrobeWorldPatchBlock> BACILLUS=
        BLOCKS.register("soil_bacillus_patch",MicrobeWorldContent::patch);
    public static final DeferredBlock<MicrobeWorldPatchBlock> PLANTARUM=
        BLOCKS.register("plant_lactiplantibacillus_patch",MicrobeWorldContent::patch);
    public static final DeferredBlock<MicrobeWorldPatchBlock> ECOLI=
        BLOCKS.register("ecoli_microhabitat_display",MicrobeWorldContent::patch);
    public static final DeferredBlock<MicrobeWorldPatchBlock> T4=
        BLOCKS.register("t4_phage_microhabitat_display",MicrobeWorldContent::patch);
    public static final Supplier<BlockItem> BACILLUS_ITEM=ITEMS.registerSimpleBlockItem(BACILLUS);
    public static final Supplier<BlockItem> PLANTARUM_ITEM=ITEMS.registerSimpleBlockItem(PLANTARUM);
    public static final Supplier<BlockItem> ECOLI_ITEM=ITEMS.registerSimpleBlockItem(ECOLI);
    public static final Supplier<BlockItem> T4_ITEM=ITEMS.registerSimpleBlockItem(T4);
    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey()==CreativeModeTabs.NATURAL_BLOCKS){
            event.accept(BACILLUS_ITEM.get());
            event.accept(PLANTARUM_ITEM.get());
            event.accept(ECOLI_ITEM.get());
            event.accept(T4_ITEM.get());
        }
    }
}
