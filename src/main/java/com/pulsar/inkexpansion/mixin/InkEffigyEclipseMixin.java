package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import doctor4t.defile.Defile;
import doctor4t.defile.cca.DefileComponents;
import doctor4t.defile.cca.WorldEclipseAnimationComponent;
import doctor4t.defile.packet.EntityAndPosPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class InkEffigyEclipseMixin extends Entity {
    @Shadow public abstract ItemStack getStack();

    @Shadow @Nullable private UUID owner;

    public InkEffigyEclipseMixin(EntityType<?> type, World world) { super(type, world); }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        ItemStack effigy = this.getStack();
        if (effigy.isOf(InkExpansion.INK_EFFIGY)) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                if (Defile.isCollidingWithFuneralInk(this) || this.isInsideWaterOrBubbleColumn() && Defile.isAFuneralInkFacingDown(this.getWorld(), this.getBlockPos().up())) {
                    UUID ownerUUID = this.owner == null ? this.getWorld().getClosestPlayer(this, 32f).getUuid() : this.owner;
                    if (ownerUUID != null) {
                        ExtendedBlackRainComponent extendedRainComponent = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(this.getWorld());
                        extendedRainComponent.queueEclipse(ownerUUID, effigy, this.getBlockPos());

                        for (ServerPlayerEntity serverPlayerEntity : serverWorld.getPlayers()) {
                            Packet<ClientPlayPacketListener> packet = new EntityAndPosPacket(this);
                            PacketByteBuf buf = PacketByteBufs.create();
                            packet.write(buf);
                            ServerPlayNetworking.send(serverPlayerEntity, EntityAndPosPacket.DIVE_SPLASH_ID, buf);
                        }

                        this.discard();
                    }
                }
            }
        }

    }
}
