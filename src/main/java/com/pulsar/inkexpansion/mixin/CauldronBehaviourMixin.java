package com.pulsar.inkexpansion.mixin;

import com.pulsar.inkexpansion.InkExpansion;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static net.minecraft.block.cauldron.CauldronBehavior.emptyCauldron;
import static net.minecraft.block.cauldron.CauldronBehavior.fillCauldron;

@Mixin(CauldronBehavior.class)
public interface CauldronBehaviourMixin {
    @Inject(method = "registerBucketBehavior", at = @At("TAIL"))
    private static void inkexpansion$inkBucketBehaviour(Map<Item, CauldronBehavior> behavior, CallbackInfo ci) {
        behavior.put(InkExpansion.INK_BUCKET, (state, world, pos, player, hand, stack) ->
                fillCauldron(world, pos, player, hand, stack, InkExpansion.INK_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY));
    }
}
