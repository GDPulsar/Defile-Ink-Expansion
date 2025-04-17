package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.InkExpansion;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CorrosiveInkBucketItem extends Item {
    public CorrosiveInkBucketItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.getOrCreateNbt().putLong("craftTime", world.getTime());
        super.onCraft(stack, world, player);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (world == null) {
            super.appendTooltip(stack, world, tooltip, context);
            return;
        }
        if (stack.getOrCreateNbt().contains("craftTime")) {
            long craftTime = stack.getOrCreateNbt().getLong("craftTime");
            double secondsUntilGone = (craftTime + 200 - world.getTime()) / 20f;
            tooltip.add(Text.translatable("item.inkexpansion.corrosive_ink_bucket.time_left", secondsUntilGone)
                    .setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true)));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (stack.getOrCreateNbt().contains("craftTime")) {
            long craftTime = stack.getOrCreateNbt().getLong("craftTime");
            if (craftTime + 200 < world.getTime()) {
                stack.decrement(1);
                if (entity instanceof LivingEntity living) {
                    living.damage(living.getDamageSources().create(InkExpansion.BUCKET_CORROSION_DAMAGE_TYPE), 10f);
                }
            }
        }
    }
}
