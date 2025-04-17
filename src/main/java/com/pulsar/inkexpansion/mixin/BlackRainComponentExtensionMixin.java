package com.pulsar.inkexpansion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.client.InkExpansionClient;
import doctor4t.defile.cca.WorldBlackRainComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WorldBlackRainComponent.class, remap = false)
public abstract class BlackRainComponentExtensionMixin {
    @Shadow @Final private World world;

    @Shadow private int ticks;

    @Shadow private float gradient;

    @ModifyReturnValue(method = "isRaining", at = @At("RETURN"))
    private boolean inkexpansion$modifyIsRaining(boolean original) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (MinecraftClient.getInstance().cameraEntity != null) {
                return original || InkExpansion.getExtendedRainComponent(world).shouldRainAt(MinecraftClient.getInstance().cameraEntity.getPos());
            }
        }
        return original || InkExpansion.getExtendedRainComponent(world).shouldRain();
    }

    @ModifyReturnValue(method = "getGradient", at = @At("RETURN"))
    private float inkexpansion$modifyRainGradient(float original) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return Math.max(original, InkExpansion.getExtendedRainComponent(world).localGradient);
        }
        return original;
    }

    @ModifyExpressionValue(method = "serverTick", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/WorldBlackRainComponent;isRaining()Z"))
    private boolean inkexpansion$shouldServerTick(boolean original) {
        return this.ticks > 0;
    }

    @ModifyExpressionValue(method = "tickRain", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/WorldBlackRainComponent;getGradient()F", ordinal = 0))
    private float inkexpansion$tickDefileRain1(float original) {
        return this.gradient;
    }

    @ModifyExpressionValue(method = "tickRain", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/WorldBlackRainComponent;getGradient()F", ordinal = 1))
    private float inkexpansion$tickDefileRain2(float original) {
        return this.gradient;
    }
}
