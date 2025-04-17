package com.pulsar.inkexpansion.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = PlayerEntity.class, priority = 1500, remap = false)
public class InklingDiveSpeedPlayerEntityMixin {
    @Dynamic
    @TargetHandler(
            mixin = "doctor4t.defile.mixin.inkling.InklingOnFuneralInkVelocityMultiplierEntityMixin",
            name = "defile$increaseInklingSpeedOnInk"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/command/DefileCommand;getInklingDiveSpeed()F"))
    private float inkexpansion$applySpeedBoost(float original) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.getNullable(this);
        if (upgrades != null) {
            return original * MathHelper.sqrt(MathHelper.sqrt(upgrades.getUpgradeVal(InklingUpgradesComponent.UpgradeType.DIVE_SPEED)));
        }
        return original;
    }
}
