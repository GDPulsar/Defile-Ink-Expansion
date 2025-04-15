package com.pulsar.inkexpansion.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import doctor4t.defile.cca.DefileComponents;
import doctor4t.defile.cca.WorldBlackRainComponent;
import doctor4t.defile.cca.WorldEclipseAnimationComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldEclipseAnimationComponent.class, remap = false)
public abstract class EclipseAnimationMixin {
    @Shadow public abstract boolean shouldTick();

    @Shadow @Final private World world;

    @Inject(method = "serverTick", at = @At("HEAD"))
    private void inkexpansion$preventRainDuringAnimation(CallbackInfo ci) {
        if (this.shouldTick()) {
            DefileComponents.BLACK_RAIN.get(this.world).setGradient(0f);
        }
    }

    @WrapWithCondition(method = "serverTick", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/WorldBlackRainComponent;setTicks(I)V"))
    private boolean inkexpansion$relocateRainDuration(WorldBlackRainComponent instance, int ticks) {
        return false;
    }
}
