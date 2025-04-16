package com.pulsar.inkexpansion.component;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.accessors.EclipseAccessor;
import com.pulsar.inkexpansion.client.InkExpansionClient;
import com.pulsar.inkexpansion.entity.InkGlobuleProjectile;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import doctor4t.defile.block.FuneralInkBlock;
import doctor4t.defile.cca.DefileComponents;
import doctor4t.defile.cca.WorldEclipseAnimationComponent;
import doctor4t.defile.index.DefileBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.*;

public class ExtendedBlackRainComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {
    private final World world;

    private final HashMap<UUID, Data> eclipses = new HashMap<>();

    @Environment(EnvType.CLIENT)
    public float localGradient = 0f;

    public ExtendedBlackRainComponent(World world) {
        this.world = world;
    }

    private void sync() {
        InkExpansionComponents.EXTENDED_BLACK_RAIN.sync(this.world);
    }

    public void queueEclipse(UUID ownerUUID, ItemStack effigy, BlockPos pos) {
        WorldEclipseAnimationComponent eclipseComponent = DefileComponents.ECLIPSE_ANIMATION.get(this.world);
        Data data = new Data();
        data.pos = pos;
        if (effigy.getOrCreateNbt().contains("upgrades")) {
            NbtCompound effigyUpgrades = effigy.getOrCreateNbt().getCompound("upgrades");
            data.duration = 24000 + (effigyUpgrades.contains("duration") ? effigyUpgrades.getInt("duration") * 12000 : 0);
            data.dangerZone = effigyUpgrades.contains("dangerZone");
            data.stormy = effigyUpgrades.contains("stormy") ? effigyUpgrades.getInt("stormy") : 0;
            data.coverage = effigyUpgrades.contains("coverage") ? effigyUpgrades.getInt("coverage") : 0;
            data.heavy = effigyUpgrades.contains("heavy") ? effigyUpgrades.getInt("heavy") : 0;
        } else {
            data.duration = 24000;
        }
        ((EclipseAccessor)eclipseComponent).inkexpansion$setEclipseData(data);
        ((EclipseAccessor)eclipseComponent).inkexpansion$setEclipseOwner(ownerUUID);
        eclipseComponent.setPosition(pos.toCenterPos());
        eclipseComponent.setTicks(0);
        eclipseComponent.start();
    }

    public void startEclipse(UUID ownerUUID, Data data) {
        eclipses.put(ownerUUID, data);
        if (data.dangerZone) {
            data.inside = this.world.getEntitiesByClass(PlayerEntity.class, Box.of(data.pos.toCenterPos(), 220f, 220f, 220f),
                    (e) -> e.getPos().distanceTo(data.pos.toCenterPos()) <= 99.5f && !(e.isSpectator() || e.isCreative()));
        }
        sync();
    }

    public Data getAffectingEclipse(Vec3d position) {
        Data affectingEclipse = null;
        for (Data eclipse : eclipses.values()) {
            if (eclipse.dangerZone && position.distanceTo(eclipse.pos.toCenterPos()) < 100f) {
                if (affectingEclipse == null) affectingEclipse = eclipse;
                else if (position.distanceTo(affectingEclipse.pos.toCenterPos()) < position.distanceTo(eclipse.pos.toCenterPos())) affectingEclipse = eclipse;
            }
        }
        if (affectingEclipse != null) return affectingEclipse;
        for (Data eclipse : eclipses.values()) {
            if (affectingEclipse == null) affectingEclipse = eclipse;
            else if (affectingEclipse.duration > eclipse.duration) affectingEclipse = eclipse;
        }
        return affectingEclipse;
    }

    public List<Data> getDangerZones() {
        List<Data> zones = new ArrayList<>();
        for (Data eclipse : eclipses.values()) {
            if (eclipse.dangerZone) zones.add(eclipse);
        }
        return zones;
    }

    public boolean shouldRainAt(Vec3d position) {
        for (Data eclipse : eclipses.values()) {
            if (!eclipse.dangerZone) {
                return true;
            } else if (position.distanceTo(eclipse.pos.toCenterPos()) < 100f) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldRain() {
        return !eclipses.isEmpty();
    }

    @Override
    public void clientTick() {
        if (MinecraftClient.getInstance().cameraEntity != null) {
            Vec3d cameraPos = MinecraftClient.getInstance().cameraEntity.getPos();
            if (shouldRainAt(cameraPos)) {
                localGradient = 1f;
            } else {
                localGradient = 0f;
            }
        }
    }

    @Override
    public void serverTick() {
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, Data> eclipse : eclipses.entrySet()) {
            eclipse.getValue().duration--;
            if (eclipse.getValue().duration <= 0) toRemove.add(eclipse.getKey());

            if (eclipse.getValue().dangerZone) {
                for (PlayerEntity player : this.world.getEntitiesByClass(PlayerEntity.class, Box.of(eclipse.getValue().pos.toCenterPos(), 220f, 220f, 220f),
                        (e) -> e.getPos().distanceTo(eclipse.getValue().pos.toCenterPos()) <= 101.5f && !(e.isSpectator() || e.isCreative()))) {
                    Vec3d offset = player.getPos().subtract(eclipse.getValue().pos.toCenterPos());
                    if (!eclipse.getValue().inside.contains(player)) {
                        player.addVelocity(offset.normalize().multiply(0.5f));
                        player.velocityModified = true;
                    }
                }
                for (PlayerEntity player : eclipse.getValue().inside) {
                    Vec3d offset = player.getPos().subtract(eclipse.getValue().pos.toCenterPos());
                    if (player.getPos().distanceTo(eclipse.getValue().pos.toCenterPos()) >= 98.5f) {
                        player.addVelocity(offset.normalize().multiply(-0.5f));
                        player.velocityModified = true;
                    }
                }
            }

            if (eclipse.getValue().coverage > 0) {
                if (eclipse.getValue().coverage == 5 || world.random.nextInt(5 - eclipse.getValue().coverage) == 0) {
                    BlockPos randomPos = eclipse.getValue().pos.add(world.random.nextInt(65) - 32, 0, world.random.nextInt(65) - 32);
                    BlockPos blockPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, randomPos);
                    if (FuneralInkBlock.canGrowOn(world, Direction.DOWN, blockPos.down(), world.getBlockState(blockPos.down())) && FuneralInkBlock.canBreak(world, blockPos, world.getBlockState(blockPos))) {
                        world.breakBlock(blockPos, true);
                        world.setBlockState(blockPos, DefileBlocks.FUNERAL_INK.getDefaultState().with(FuneralInkBlock.getProperty(Direction.DOWN), true));
                    }
                }
            }

            if (eclipse.getValue().heavy > 0) {
                if (world.random.nextInt(10 * (2 - eclipse.getValue().heavy)) == 0) {
                    BlockPos randomPos = eclipse.getValue().pos.add(world.random.nextInt(65) - 32, 0, world.random.nextInt(65) - 32);
                    BlockPos blockPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, randomPos);
                    Vec3d direction = new Vec3d((Math.random() - 0.5f) * 0.6f, -1f, (Math.random() - 0.5f) * 0.6f);
                    Vec3d spawnPos = blockPos.toCenterPos().add(direction.normalize().multiply(-100f));
                    InkGlobuleProjectile globule = new InkGlobuleProjectile(world);
                    globule.setPosition(spawnPos);
                    globule.setVelocity(direction.normalize().multiply(1.5f));
                    world.spawnEntity(globule);
                }
            }
        }
        for (UUID removing : toRemove) {
            eclipses.remove(removing);
        }
        if (!toRemove.isEmpty()) {
            sync();
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        NbtCompound eclipsesNbt = nbt.getCompound("eclipses");
        for (String playerUUID : eclipsesNbt.getKeys()) {
            UUID uuid = UUID.fromString(playerUUID);
            Data data = new Data(eclipsesNbt.getCompound(playerUUID));
            this.eclipses.put(uuid, data);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        NbtCompound eclipsesNbt = new NbtCompound();
        for (Map.Entry<UUID, Data> entry : this.eclipses.entrySet()) {
            eclipsesNbt.put(entry.getKey().toString(), entry.getValue().getNbt());
        }
        nbt.put("eclipses", eclipsesNbt);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        AutoSyncedComponent.super.applySyncPacket(buf);
        InkExpansionClient.reloadCache(world);
    }

    public static class Data {
        public BlockPos pos;
        public boolean dangerZone;
        public int stormy;
        public int coverage;
        public int heavy;
        public int duration;

        public List<PlayerEntity> inside = new ArrayList<>();

        public Data() {}

        public Data(NbtCompound nbt) {
            pos = BlockPos.fromLong(nbt.getLong("pos"));
            dangerZone = nbt.getBoolean("dangerZone");
            stormy = nbt.getInt("stormy");
            coverage = nbt.getInt("coverage");
            heavy = nbt.getInt("heavy");
            duration = nbt.getInt("duration");
        }

        public NbtCompound getNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putLong("pos", pos.asLong());
            nbt.putBoolean("dangerZone", dangerZone);
            nbt.putInt("stormy", stormy);
            nbt.putInt("coverage", coverage);
            nbt.putInt("heavy", heavy);
            nbt.putInt("duration", duration);
            return nbt;
        }
    }
}
