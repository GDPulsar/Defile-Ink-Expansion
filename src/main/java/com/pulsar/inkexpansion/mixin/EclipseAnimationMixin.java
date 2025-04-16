package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.accessors.EclipseAccessor;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import doctor4t.defile.cca.WorldBlackRainComponent;
import doctor4t.defile.cca.WorldEclipseAnimationComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(value = WorldEclipseAnimationComponent.class, remap = false)
public abstract class EclipseAnimationMixin implements EclipseAccessor {
    @Shadow @Final private World world;

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/WorldBlackRainComponent;setTicks(I)V"))
    private void inkexpansion$relocateRainDuration(WorldBlackRainComponent instance, int ticks) {
        if (data == null || owner == null) {
            instance.setTicks(24000);
            return;
        }
        InkExpansionComponents.EXTENDED_BLACK_RAIN.get(this.world).startEclipse(owner, data);
    }

    @Unique ExtendedBlackRainComponent.Data data = null;
    @Unique UUID owner = null;

    @Override
    public void inkexpansion$setEclipseData(ExtendedBlackRainComponent.Data data) {
        this.data = data;
    }

    @Override
    public void inkexpansion$setEclipseOwner(UUID owner) {
        this.owner = owner;
    }
}
