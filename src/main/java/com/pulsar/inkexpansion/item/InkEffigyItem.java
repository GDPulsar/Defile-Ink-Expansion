package com.pulsar.inkexpansion.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkEffigyItem extends Item {
    public InkEffigyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getOrCreateNbt().contains("upgrades")) {
            NbtCompound nbt = stack.getOrCreateNbt().getCompound("upgrades");
            if (nbt.contains("dangerZone") && nbt.getBoolean("dangerZone")) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.dangerZone"));
            if (nbt.contains("stormy") && nbt.getInt("stormy") > 0) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.stormy")
                    .append(" ").append(Text.translatable("enchantment.level." + nbt.getInt("stormy"))));
            if (nbt.contains("coverage") && nbt.getInt("coverage") > 0) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.coverage")
                    .append(" ").append(Text.translatable("enchantment.level." + nbt.getInt("coverage"))));
            if (nbt.contains("heavy") && nbt.getInt("heavy") > 0) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.heavy")
                    .append(" ").append(Text.translatable("enchantment.level." + nbt.getInt("heavy"))));
            int durationTicks = nbt.contains("duration") ? 24000 + nbt.getInt("duration") * 12000 : 24000;
            int durationMinutes = MathHelper.floor(durationTicks / 1200f);
            tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.duration", durationMinutes));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
