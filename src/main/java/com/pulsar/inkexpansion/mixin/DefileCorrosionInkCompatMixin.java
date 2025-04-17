package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.InkExpansion;
import doctor4t.defile.Defile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Defile.class)
public class DefileCorrosionInkCompatMixin {
    @Redirect(method = "isCollidingWithFuneralInk", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0))
    private static boolean inkexpansion$corrosiveInkCompat1(BlockState instance, Block block) {
        return instance.isOf(block) || instance.isOf(InkExpansion.CORROSIVE_INK);
    }

    @Redirect(method = "isCollidingWithFuneralInk", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1))
    private static boolean inkexpansion$corrosiveInkCompat2(BlockState instance, Block block) {
        return instance.isOf(block) || instance.isOf(InkExpansion.CORROSIVE_INK);
    }

    @Redirect(method = "isAFuneralInkFacingDown(Lnet/minecraft/block/BlockState;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private static boolean inkexpansion$corrosiveInkCompat3(BlockState instance, Block block) {
        return instance.isOf(block) || instance.isOf(InkExpansion.CORROSIVE_INK);
    }
}
