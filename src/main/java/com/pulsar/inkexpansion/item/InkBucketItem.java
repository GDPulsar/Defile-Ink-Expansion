package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.InkExpansion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class InkBucketItem extends Item {
    public InkBucketItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).isOf(Blocks.CAULDRON)) {
            context.getWorld().setBlockState(context.getBlockPos(), InkExpansion.INK_CAULDRON.getDefaultState());
        }
        return super.useOnBlock(context);
    }
}
