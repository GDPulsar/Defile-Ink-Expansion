package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.InkExpansion;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;

public class InkBucketItem extends Item {
    public InkBucketItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).isOf(Blocks.CAULDRON)) {
            context.getWorld().setBlockState(context.getBlockPos(), InkExpansion.INK_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
            context.getStack().decrement(1);
            context.getPlayer().giveItemStack(new ItemStack(Items.BUCKET));
            context.getWorld().playSoundAtBlockCenter(context.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f, false);
        }
        return super.useOnBlock(context);
    }
}
