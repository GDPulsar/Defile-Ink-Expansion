package com.pulsar.inkexpansion.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(value = ClientPlayerEntity.class, priority = 1500, remap = false)
public class InklingJumpFillClientPlayerMixin {
    @Dynamic
    @TargetHandler(
            mixin = "doctor4t.defile.mixin.client.inkling.RebindKeysForDiveClientPlayerEntityMixin",
            name = "tickMovement"
    )
    @ModifyConstant(method = "@MixinSquared:Handler", constant = @Constant(floatValue = 0.1f))
    private float inkexpansion$applyJumpFillMult(float original) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.getNullable(this);
        if (upgrades != null) {
            return original * upgrades.getUpgradeVal(InklingUpgradesComponent.UpgradeType.JUMP_FILL);
        }
        return original;
    }
}
