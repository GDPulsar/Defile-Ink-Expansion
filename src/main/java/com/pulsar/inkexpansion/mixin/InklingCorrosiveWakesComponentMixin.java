package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import com.pulsar.inkexpansion.util.SpecialInksplosion;
import doctor4t.defile.cca.PlayerInklingComponent;
import doctor4t.defile.command.DefileCommand;
import doctor4t.defile.index.DefileBlocks;
import doctor4t.defile.world.Inksplosion;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlayerInklingComponent.class, remap = false)
public class InklingCorrosiveWakesComponentMixin {
    @Shadow @Final private PlayerEntity player;

    @Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/world/Inksplosion;tick()V"))
    private void inkexpansion$performCorrosiveWakes(Inksplosion instance) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.get(this.player);
        int corrosiveWakes = (int)upgrades.getUpgradeVal(InklingUpgradesComponent.UpgradeType.CORROSIVE_WAKES);
        SpecialInksplosion.inksplode(this.player.getWorld(), this.player, this.player.getX(), this.player.getY(), this.player.getZ(),
                DefileCommand.getInkSpreadPower() + Math.max(0, corrosiveWakes - 1), corrosiveWakes > 0 ?
                        InkExpansion.CORROSIVE_INK : DefileBlocks.FUNERAL_INK);
    }
}
