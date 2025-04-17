package com.pulsar.inkexpansion.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import com.pulsar.inkexpansion.packet.DamagingSplashPacket;
import doctor4t.defile.index.DefileStatusEffects;
import doctor4t.defile.packet.UseTotemOfUncleansingPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = PlayerEntity.class, priority = 1500)
public abstract class InklingDamagingSplashesPlayerMixin extends LivingEntity {
    protected InklingDamagingSplashesPlayerMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    @Dynamic
    @TargetHandler(
            mixin = "doctor4t.defile.mixin.inkling.InklingDivePlayerEntityMixin",
            name = "defile$inklingDiveOnFallDamage"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Ldoctor4t/defile/cca/PlayerInklingComponent;dive()V"))
    private void inkexpansion$performDamagingSplashes(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, CallbackInfo ci) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.getNullable(this);
        if (upgrades != null) {
            int damagingSplashes = (int)upgrades.getUpgradeVal(InklingUpgradesComponent.UpgradeType.DAMAGING_SPLASHES);
            if (damagingSplashes > 0) {
                float radius = 1.5f + damagingSplashes * 2f;
                float damage = amount / 5f * damagingSplashes;
                for (LivingEntity living : this.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(this.getPos(), radius * 2f, radius * 2f, radius * 2f),
                    (target) -> this.distanceTo(target) < radius)) {
                    if (living.hasStatusEffect(DefileStatusEffects.INKMORPHOSIS) || living == (PlayerEntity)(Object)this) continue;
                    living.damage(this.getDamageSources().create(InkExpansion.INK_DAMAGE_TYPE), damage);
                    living.addStatusEffect(new StatusEffectInstance(InkExpansion.INKED, 240, 0));
                }
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()) {
                        DamagingSplashPacket packet = new DamagingSplashPacket((PlayerEntity)(Object)this);
                        PacketByteBuf buf = PacketByteBufs.create();
                        packet.write(buf);
                        ServerPlayNetworking.send(serverPlayerEntity, DamagingSplashPacket.ID, buf);
                    }
                }
            }
        }
    }
}
