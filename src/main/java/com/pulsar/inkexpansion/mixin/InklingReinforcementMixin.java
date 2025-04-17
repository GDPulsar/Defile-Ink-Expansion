package com.pulsar.inkexpansion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class InklingReinforcementMixin {
    @ModifyReturnValue(method = "modifyAppliedDamage", at = @At("RETURN"))
    private float inkexpansion$doInklingReinforcement(float original) {
        if ((LivingEntity)(Object)this instanceof PlayerEntity player) {
            InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.get(player);
            if (upgrades.currentReinforcement > 0) {
                float mult = 1f - upgrades.currentReinforcement;
                upgrades.currentReinforcement = 0f;
                return original * mult;
            }
        }
        return original;
    }
}
