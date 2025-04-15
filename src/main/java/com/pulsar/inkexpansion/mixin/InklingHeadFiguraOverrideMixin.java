package com.pulsar.inkexpansion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.inkexpansion.client.InkExpansionClient;
import doctor4t.defile.client.render.entity.feature.InklingHeadFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InklingHeadFeatureRenderer.class)
public class InklingHeadFiguraOverrideMixin {
    @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
    private static boolean inkexpansion$figuraOverride(boolean original) {
        return original && !InkExpansionClient.disableInkRenderer;
    }
}
