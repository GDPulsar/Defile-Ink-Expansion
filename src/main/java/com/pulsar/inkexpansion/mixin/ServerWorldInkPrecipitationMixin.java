package com.pulsar.inkexpansion.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.inkexpansion.InkExpansion;
import doctor4t.defile.cca.DefileComponents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerWorld.class)
public class ServerWorldInkPrecipitationMixin {
    @ModifyExpressionValue(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isRaining()Z"))
    private boolean inkexpansion$simulateRain1(boolean original) {
        return original || DefileComponents.BLACK_RAIN.get((ServerWorld)(Object)(this)).isRaining();
    }

    @Definition(id = "precipitation", local = @Local(type = Biome.Precipitation.class))
    @Definition(id = "NONE", field = "Lnet/minecraft/world/biome/Biome$Precipitation;NONE:Lnet/minecraft/world/biome/Biome$Precipitation;")
    @Expression("precipitation != NONE")
    @ModifyExpressionValue(method = "tickChunk", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean inkexpansion$simulateRain2(boolean original) {
        return original || DefileComponents.BLACK_RAIN.get((ServerWorld)(Object)(this)).isRaining();
    }
}
