package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import doctor4t.defile.Defile;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Defile.class, remap = false)
public class InklingDiveLaunchDefileMixin {
    @Redirect(method = "lambda$onInitialize$2",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setVelocity(DDD)V"))
    private static void inkexpansion$modifyLaunchStrength(ServerPlayerEntity player, double x, double y, double z) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.getNullable(player);
        if (upgrades != null) {
            y *= upgrades.getUpgradeVal(InklingUpgradesComponent.UpgradeType.JUMP_HEIGHT);
        }
        player.setVelocity(x, y, z);
    }
}
