package com.pulsar.inkexpansion.packet;

import doctor4t.defile.Defile;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DamagingSplashPacket implements Packet<ClientPlayPacketListener> {
    public static final Identifier ID = new Identifier("inkexpansion", "damaging_splash");
    private final int id;

    public DamagingSplashPacket(Entity entity) {
        this.id = entity.getId();
    }

    public DamagingSplashPacket(PacketByteBuf buf) {
        this.id = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.id);
    }

    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
    }

    public @Nullable Entity getEntity(World world) {
        return world.getEntityById(this.id);
    }
}
