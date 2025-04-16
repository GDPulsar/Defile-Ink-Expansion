package com.pulsar.inkexpansion.mixin;

import doctor4t.defile.Defile;
import net.fabricmc.fabric.api.event.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Defile.class)
public class RelocateNaturalInkMixin {
    @Redirect(method = "onInitialize", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/Event;register(Ljava/lang/Object;)V", ordinal = 1))
    private <T> void inkexpansion$redirectNaturalInk(Event<T> instance, T t) {
        // nuh uh
    }
}
