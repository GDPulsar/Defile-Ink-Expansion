package com.pulsar.inkexpansion.block;

import com.pulsar.inkexpansion.InkExpansion;
import doctor4t.defile.block.FuneralInkBlock;
import doctor4t.defile.index.DefileBlocks;
import doctor4t.defile.index.DefileStatusEffects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CorrosiveInk extends FuneralInkBlock {
    public static final IntProperty CORROSION_LEVEL = IntProperty.of("corrosion_level", 1, 3);

    public CorrosiveInk(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORROSION_LEVEL, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CORROSION_LEVEL);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(CORROSION_LEVEL) == 1) {
            world.setBlockState(pos, DefileBlocks.FUNERAL_INK.getStateWithProperties(state));
        } else {
            world.setBlockState(pos, state.with(CORROSION_LEVEL, state.get(CORROSION_LEVEL) - 1));
            world.scheduleBlockTick(pos, this, 70 + world.random.nextInt(45));
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 70 + world.random.nextInt(45));
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living) {
            if (!living.hasStatusEffect(DefileStatusEffects.INKMORPHOSIS)) {
                living.damage(world.getDamageSources().create(InkExpansion.CORROSIVE_INK_DAMAGE_TYPE), 4f);
            }
        }
    }
}
