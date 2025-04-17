package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import doctor4t.defile.cca.DefileComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronBlock.class)
public class CauldronMixin {
    @Inject(method = "precipitationTick", at = @At("HEAD"), cancellable = true)
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation, CallbackInfo ci) {
        if (InkExpansion.getExtendedRainComponent(world).shouldRainAt(pos.toCenterPos())) {
            ExtendedBlackRainComponent.Data eclipse = InkExpansion.getExtendedRainComponent(world).getAffectingEclipse(pos.toCenterPos());
            if (world.getRandom().nextFloat() < 0.2f + 0.15f * eclipse.coverage) {
                world.setBlockState(pos, InkExpansion.INK_CAULDRON.getDefaultState());
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            }
            ci.cancel();
        }
    }
}
