package com.pulsar.inkexpansion.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.pulsar.inkexpansion.accessors.EclipseAccessor;
import doctor4t.defile.cca.DefileComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = ItemEntity.class, priority = 1500)
public abstract class SunEffigyEclipseMixin extends Entity {
    public SunEffigyEclipseMixin(EntityType<?> type, World world) { super(type, world); }

    @Dynamic
    @TargetHandler(
            mixin = "doctor4t.defile.mixin.TriggerEclipseSunEffigyItemEntityMixin",
            name = "tick"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/WorldBlackRainComponent;setTicks(I)V", shift = At.Shift.BEFORE), remap = false)
    private void inkexpansion$relocateRainDuration(CallbackInfo ci) {
        EclipseAccessor eclipseAccessor = (EclipseAccessor)DefileComponents.ECLIPSE_ANIMATION.get(this.getWorld());
        eclipseAccessor.inkexpansion$setEclipseOwner(null);
        eclipseAccessor.inkexpansion$setEclipseOwner(null);
    }
}
