package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import doctor4t.defile.Defile;
import doctor4t.defile.index.DefileBlocks;
import doctor4t.defile.packet.EntityAndPosPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InkEffigyItem extends Item {
    public InkEffigyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        HashMap<UpgradeType, Integer> upgrades = getUpgrades(stack);
        if (upgrades.getOrDefault(UpgradeType.DANGER_ZONE, 0) > 0) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.dangerZone")
                .setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        if (upgrades.getOrDefault(UpgradeType.STORMY, 0) > 0) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.stormy")
                .append(" ").append(Text.translatable("enchantment.level." + upgrades.get(UpgradeType.STORMY))).setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
        if (upgrades.getOrDefault(UpgradeType.COVERAGE, 0) > 0) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.coverage")
                .append(" ").append(Text.translatable("enchantment.level." + upgrades.get(UpgradeType.COVERAGE))).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        if (upgrades.getOrDefault(UpgradeType.HEAVY, 0) > 0) tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.heavy")
                .append(" ").append(Text.translatable("enchantment.level." + upgrades.get(UpgradeType.HEAVY))).setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
        int durationTicks = 24000 + upgrades.getOrDefault(UpgradeType.DURATION, 0) * 12000;
        int durationMinutes = MathHelper.floor(durationTicks / 1200f);
        tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.duration", durationMinutes));
        tooltip.add(Text.translatable("item.inkexpansion.ink_effigy.power", getCurrentPower(stack), 10).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        super.appendTooltip(stack, world, tooltip, context);
    }

    private HashMap<UpgradeType, Integer> getUpgrades(ItemStack stack) {
        HashMap<UpgradeType, Integer> upgrades = new HashMap<>();
        if (!stack.getOrCreateNbt().contains("upgrades")) return upgrades;
        NbtCompound nbt = stack.getOrCreateNbt().getCompound("upgrades");
        if (nbt.contains("dangerZone") && nbt.getBoolean("dangerZone")) upgrades.put(UpgradeType.DANGER_ZONE, 1);
        if (nbt.contains("stormy") && nbt.getInt("stormy") > 0) upgrades.put(UpgradeType.STORMY, nbt.getInt("stormy"));
        if (nbt.contains("coverage") && nbt.getInt("coverage") > 0) upgrades.put(UpgradeType.COVERAGE, nbt.getInt("coverage"));
        if (nbt.contains("heavy") && nbt.getInt("heavy") > 0) upgrades.put(UpgradeType.HEAVY, nbt.getInt("heavy"));
        if (nbt.contains("duration") && nbt.getInt("duration") > 0) upgrades.put(UpgradeType.DURATION, nbt.getInt("duration"));
        return upgrades;
    }

    private boolean addUpgrade(ItemStack stack, UpgradeType upgrade) {
        String type = null;
        switch (upgrade) {
            case DANGER_ZONE -> type = "dangerZone";
            case STORMY -> type = "stormy";
            case COVERAGE -> type = "coverage";
            case HEAVY -> type = "heavy";
            case DURATION -> type = "duration";
        }
        if (type == null) return false;
        NbtCompound nbt = stack.getOrCreateNbt().contains("upgrades") ? stack.getOrCreateNbt().getCompound("upgrades") : new NbtCompound();
        if (type.equals("dangerZone")) {
            if (nbt.contains(type)) return false;
            nbt.putBoolean(type, true);
        } else {
            nbt.putInt(type, nbt.contains(type) ? nbt.getInt(type) + 1 : 1);
        }
        stack.getOrCreateNbt().put("upgrades", nbt);
        return true;
    }

    private int getUpgradePower(UpgradeType upgrade) {
        return switch (upgrade) {
            case DANGER_ZONE -> 6;
            case STORMY -> 4;
            case COVERAGE -> 2;
            case HEAVY -> 3;
            case DURATION -> 1;
        };
    }

    private UpgradeType getItemUpgradeType(ItemStack stack) {
        if (stack.isIn(InkExpansion.DANGER_ZONE_UPGRADES)) return UpgradeType.DANGER_ZONE;
        if (stack.isIn(InkExpansion.STORMY_UPGRADES)) return UpgradeType.STORMY;
        if (stack.isIn(InkExpansion.COVERAGE_UPGRADES)) return UpgradeType.COVERAGE;
        if (stack.isIn(InkExpansion.HEAVY_UPGRADES)) return UpgradeType.HEAVY;
        if (stack.isIn(InkExpansion.DURATION_UPGRADES)) return UpgradeType.DURATION;
        return null;
    }

    private int getCurrentPower(ItemStack stack) {
        int power = 0;
        HashMap<UpgradeType, Integer> upgrades = getUpgrades(stack);
        for (UpgradeType upgrade : UpgradeType.values()) {
            power += getUpgradePower(upgrade) * upgrades.getOrDefault(upgrade, 0);
        }
        return power;
    }

    private int getDurationTicks(ItemStack stack) {
        HashMap<UpgradeType, Integer> upgrades = getUpgrades(stack);
        return 24000 + (upgrades.containsKey(UpgradeType.DURATION) ? upgrades.get(UpgradeType.DURATION) * 12000 : 0);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT) {
            UpgradeType upgrade = getItemUpgradeType(otherStack);
            if (upgrade != null) {
                int currentPower = getCurrentPower(stack);
                int newPower = currentPower + getUpgradePower(upgrade);
                if (newPower > 10) return false;
                if (addUpgrade(stack, upgrade)) {
                    otherStack.decrement(1);
                    return true;
                }
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() != null) {
            if (Defile.isAFuneralInkFacingDown(context.getWorld(), context.getBlockPos())) {
                tryUseEffigy(context.getPlayer().getUuid(), context.getStack(), context.getWorld(), context.getBlockPos());
                context.getStack().decrement(1);
            }
        }
        return super.useOnBlock(context);
    }

    public static boolean tryUseEffigy(UUID ownerUUID, ItemStack effigy, World world, BlockPos pos) {
        if (ownerUUID != null) {
            ExtendedBlackRainComponent extendedRainComponent = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(world);
            if (extendedRainComponent.hasEclipse(ownerUUID)) return false;
            extendedRainComponent.queueEclipse(ownerUUID, effigy, pos);
            return true;
        }
        return false;
    }

    private enum UpgradeType {
        DANGER_ZONE,
        STORMY,
        COVERAGE,
        HEAVY,
        DURATION
    }
}
