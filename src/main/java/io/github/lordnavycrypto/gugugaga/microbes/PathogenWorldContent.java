package io.github.lordnavycrypto.gugugaga.microbes;
import io.github.lordnavycrypto.gugugaga.GuguGagaMod;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PathogenWorldContent {
    private PathogenWorldContent(){}
    public static final DeferredRegister.Blocks BLOCKS=
            DeferredRegister.createBlocks(GuguGagaMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS=
            DeferredRegister.createItems(GuguGagaMod.MOD_ID);
    private static PathogenScenarioBlock make(){
        return new PathogenScenarioBlock(BlockBehaviour.Properties.of()
                .strength(1.2f).sound(SoundType.STONE));
    }
    public static final DeferredBlock<PathogenScenarioBlock> RABIES=
            BLOCKS.register("rabies_wildlife_case",PathogenWorldContent::make);
    public static final DeferredBlock<PathogenScenarioBlock> AVIAN=
            BLOCKS.register("avian_influenza_case",PathogenWorldContent::make);
    public static final DeferredBlock<PathogenScenarioBlock> CLINIC=
            BLOCKS.register("field_clinic",PathogenWorldContent::make);
    public static final Supplier<BlockItem> RABIES_ITEM=ITEMS.registerSimpleBlockItem(RABIES);
    public static final Supplier<BlockItem> AVIAN_ITEM=ITEMS.registerSimpleBlockItem(AVIAN);
    public static final Supplier<BlockItem> CLINIC_ITEM=ITEMS.registerSimpleBlockItem(CLINIC);
    public static void onCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey()==CreativeModeTabs.FUNCTIONAL_BLOCKS){
            event.accept(RABIES_ITEM.get());
            event.accept(AVIAN_ITEM.get());
            event.accept(CLINIC_ITEM.get());
        }
    }
}
