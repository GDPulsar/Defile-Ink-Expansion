package com.pulsar.inkexpansion.entity;

import com.pulsar.inkexpansion.InkExpansion;
import doctor4t.defile.block.FuneralInkBlock;
import doctor4t.defile.index.DefileBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class InkLightningEntity extends LightningEntity {
    public InkLightningEntity(EntityType<? extends LightningEntity> entityType, World world) {
        super(entityType, world);
        this.ambientTick = 2;
        this.setCosmetic(true);
    }

    int ambientTick;
    public void tick() {
        super.tick();
        if (this.ambientTick == 2) {
            if (this.getWorld().isClient()) {
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            } else {
                this.spawnInk();
            }
        }

        --this.ambientTick;
        if (this.ambientTick >= 0) {
            if (!(this.getWorld() instanceof ServerWorld)) {
                this.getWorld().setLightningTicksLeft(2);
            }
        }
    }

    private void spawnInk() {
        if (!this.getWorld().isClient) {
            int spreadCount = MathHelper.floor(Math.random() * 9) + 12;
            BlockPos startPos = this.getBlockPos();
            List<BlockPos> ends = new ArrayList<>();
            ends.add(startPos);
            ends.add(startPos);
            ends.add(startPos);
            BlockState state = DefileBlocks.FUNERAL_INK.getDefaultState().with(Properties.DOWN, true);
            if (this.getWorld().getBlockState(startPos).isReplaceable() && state.canPlaceAt(this.getWorld(), startPos)) {
                this.getWorld().setBlockState(startPos, state);
            }

            for(int i = 0; i < spreadCount; ++i) {
                BlockPos spreadFrom = ends.get(MathHelper.floor(Math.random() * ends.size()));
                List<BlockPos> possibleTargets = new ArrayList<>();
                int offX = spreadFrom.getX() - startPos.getX();
                int offZ = spreadFrom.getZ() - startPos.getZ();
                possibleTargets.add(spreadFrom.add(MathHelper.sign(offX), 0, MathHelper.sign(offZ)));
                if (Math.abs(offX) > Math.abs(offZ)) possibleTargets.add(spreadFrom.add(MathHelper.sign(offX), 0, 0));
                if (Math.abs(offX) < Math.abs(offZ)) possibleTargets.add(spreadFrom.add(0, 0, MathHelper.sign(offZ)));
                if (MathHelper.sign(spreadFrom.getX() - startPos.getX()) == 0) {
                    possibleTargets.add(spreadFrom.add(-1, 0, MathHelper.sign(offZ)));
                    possibleTargets.add(spreadFrom.add(1, 0, MathHelper.sign(offZ)));
                }
                if (MathHelper.sign(spreadFrom.getZ() - startPos.getZ()) == 0) {
                    possibleTargets.add(spreadFrom.add(MathHelper.sign(offX), 0, -1));
                    possibleTargets.add(spreadFrom.add(MathHelper.sign(offX), 0, 1));
                }
                List<BlockPos> targets = new ArrayList<>();
                for (BlockPos pos : possibleTargets) {
                    if (targets.contains(pos)) continue;
                    targets.add(pos);
                }
                BlockPos targetPos = targets.get(MathHelper.floor(Math.random() * targets.size()));
                offX = targetPos.getX() - startPos.getX();
                offZ = targetPos.getZ() - startPos.getZ();
                Direction spreadDir = Direction.fromVector(offX != 0 && offZ != 0 ? 0 : offX, 0, offZ);
                if (spreadDir == null) spreadDir = Direction.EAST;
                if (this.getWorld().getBlockState(targetPos).isReplaceable() && state.canPlaceAt(this.getWorld(), targetPos)) {
                    this.getWorld().setBlockState(targetPos, state);
                    if (Math.random() <= (1f / targets.size())) ends.remove(spreadFrom);
                    ends.add(targetPos);
                } else if (this.getWorld().getBlockState(targetPos.up()).isReplaceable() && state.canPlaceAt(this.getWorld(), targetPos.up())) {
                    this.getWorld().setBlockState(targetPos.up(), state);
                    this.getWorld().setBlockState(spreadFrom, this.getWorld().getBlockState(spreadFrom).with(ConnectingBlock.FACING_PROPERTIES.get(spreadDir), true));
                    if (Math.random() <= (1f / targets.size())) ends.remove(spreadFrom);
                    ends.add(targetPos.up());
                } else if (this.getWorld().getBlockState(targetPos.down()).isReplaceable() && state.canPlaceAt(this.getWorld(), targetPos.down())) {
                    this.getWorld().setBlockState(targetPos.down(), state.with(ConnectingBlock.FACING_PROPERTIES.get(spreadDir.getOpposite()), true));
                    if (Math.random() <= (1f / targets.size())) ends.remove(spreadFrom);
                    ends.add(targetPos.down());
                }
            }
        }
    }
}
