package com.pulsar.inkexpansion.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(value = ItemEntity.class, priority = 1500)
public class SunEffigyEclipseMixin {
    @Dynamic
    @TargetHandler(
            mixin = "doctor4t.defile.mixin.TriggerEclipseSunEffigyItemEntityMixin",
            name = "tick"
    )
    @ModifyArg(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/WorldBlackRainComponent;setTicks(I)V"), index = 0, remap = false)
    private int inkexpansion$relocateRainDuration(int duration) {
        return 24000;
    }
}
