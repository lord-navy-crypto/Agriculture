package io.github.lordnavycrypto.gugugaga.microbes;
import com.mojang.serialization.MapCodec;
import io.github.lordnavycrypto.gugugaga.microbes.PathogenGameplayPolicy.Pathogen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Craftable actual Minecraft-world incident & care block, not a GUI wiki. */
public final class PathogenScenarioBlock extends Block {
    public static final MapCodec<PathogenScenarioBlock> CODEC=
            simpleCodec(PathogenScenarioBlock::new);
    public PathogenScenarioBlock(BlockBehaviour.Properties properties){super(properties);}
    @Override protected MapCodec<? extends Block> codec(){return CODEC;}
    @Override protected InteractionResult useWithoutItem(BlockState state,
            Level world,BlockPos pos,Player player,BlockHitResult hit){
        if(!world.isClientSide()&&player instanceof ServerPlayer serverPlayer){
            if(this==PathogenWorldContent.CLINIC.get()){
                PathogenGameplayRuntime.clinic(serverPlayer);
            }else{
                Pathogen species=this==PathogenWorldContent.RABIES.get()
                        ?Pathogen.RABIES:Pathogen.AVIAN_INFLUENZA;
                if(serverPlayer.isShiftKeyDown())
                    PathogenGameplayRuntime.simulateIllness(serverPlayer,species);
                else PathogenGameplayRuntime.contact(serverPlayer,species);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide());
    }
}
