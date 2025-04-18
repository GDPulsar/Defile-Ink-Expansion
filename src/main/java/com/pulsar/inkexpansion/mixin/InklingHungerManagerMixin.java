package com.pulsar.inkexpansion.mixin;

import doctor4t.defile.index.DefileStatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class InklingHungerManagerMixin {
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void inkexpansion$preventHungerUpdateWhileInkling(PlayerEntity player, CallbackInfo ci) {
        if (player.hasStatusEffect(DefileStatusEffects.INKMORPHOSIS)) ci.cancel();
    }
}
