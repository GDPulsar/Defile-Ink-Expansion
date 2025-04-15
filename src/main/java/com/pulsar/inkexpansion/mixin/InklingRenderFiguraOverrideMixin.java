package com.pulsar.inkexpansion.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.pulsar.inkexpansion.client.InkExpansionClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = PlayerEntityRenderer.class, priority = 1500)
public class InklingRenderFiguraOverrideMixin {
    @Dynamic
    @TargetHandler(
            mixin = "doctor4t.defile.mixin.client.inkling.InklingSkinPlayerEntityRendererMixinInklingOverrideModel",
            name = "defile$overrideModelRender"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/PlayerInklingComponent;isInkling()Z"), remap = false)
    private boolean inkexpansion$allowFiguraModels(boolean original) {
        return original && !InkExpansionClient.disableInkRenderer;
    }
}
