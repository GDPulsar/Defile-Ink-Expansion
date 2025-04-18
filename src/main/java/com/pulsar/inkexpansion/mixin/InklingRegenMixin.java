package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import doctor4t.defile.cca.PlayerInklingComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInklingComponent.class)
public class InklingRegenMixin {
    @Shadow @Final private PlayerEntity player;

    @Unique float regenTimer = 0f;
    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;setSaturationLevel(F)V", ordinal = 0))
    private void inkexpansion$replaceSaturationWithRegen(HungerManager instance, float saturationLevel) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.get(this.player);
        regenTimer += upgrades.getUpgradeVal(InklingUpgradesComponent.UpgradeType.DIVE_REGEN);
        if (regenTimer >= 10f) {
            this.player.heal(1f);
            regenTimer -= 10f;
        }
    }
}
