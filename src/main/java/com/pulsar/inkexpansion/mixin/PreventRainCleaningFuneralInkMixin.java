package com.pulsar.inkexpansion.mixin;

import doctor4t.defile.block.FuneralInkBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FuneralInkBlock.class)
public class PreventRainCleaningFuneralInkMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void inkexpansion$preventRainCleaning(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        ci.cancel();
    }
}
