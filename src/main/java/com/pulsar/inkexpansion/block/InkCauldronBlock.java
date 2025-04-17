package com.pulsar.inkexpansion.block;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import doctor4t.defile.cca.DefileComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.function.Predicate;

public class InkCauldronBlock extends LeveledCauldronBlock {
    public InkCauldronBlock(Settings settings, Predicate<Biome.Precipitation> precipitationPredicate, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, precipitationPredicate, behaviorMap);
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (InkExpansion.getExtendedRainComponent(world).shouldRainAt(pos.toCenterPos())) {
            ExtendedBlackRainComponent.Data eclipse = InkExpansion.getExtendedRainComponent(world).getAffectingEclipse(pos.toCenterPos());
            if (world.getRandom().nextFloat() < 0.2f + 0.15f * eclipse.coverage) {
                BlockState blockState = state.cycle(LEVEL);
                world.setBlockState(pos, blockState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            }
        }
    }
}
