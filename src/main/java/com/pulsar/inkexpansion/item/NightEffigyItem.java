package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import doctor4t.defile.Defile;
import doctor4t.defile.cca.DefileComponents;
import doctor4t.defile.cca.WorldBlackRainComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class NightEffigyItem extends Item {
    public NightEffigyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.inkexpansion.night_effigy.description").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() != null) {
            if (Defile.isAFuneralInkFacingDown(context.getWorld(), context.getBlockPos())) {
                tryUseEffigy(context.getPlayer().getUuid(), context.getWorld());
                context.getStack().decrement(1);
            }
        }
        return super.useOnBlock(context);
    }

    public static boolean tryUseEffigy(UUID ownerUUID, World world) {
        if (ownerUUID != null) {
            ExtendedBlackRainComponent extendedRainComponent = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(world);
            if (!extendedRainComponent.hasEclipse(ownerUUID)) {
                WorldBlackRainComponent blackRainComponent = DefileComponents.BLACK_RAIN.get(world);
                if (blackRainComponent.getTicks() > 0) {
                    blackRainComponent.setTicks(0);
                    return true;
                }
                return false;
            }
            extendedRainComponent.endEclipse(ownerUUID);
            return true;
        }
        return false;
    }
}
