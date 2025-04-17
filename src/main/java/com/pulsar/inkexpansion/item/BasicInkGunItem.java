package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.entity.InkProjectile;
import doctor4t.defile.cca.DefileComponents;
import doctor4t.defile.cca.PlayerInklingComponent;
import doctor4t.defile.index.DefileStatusEffects;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BasicInkGunItem extends Item {
    private final int maxFuel = 4000;

    public BasicInkGunItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (stack.getOrCreateNbt().contains("fuel") && remainingUseTicks % 2 == 0) {
            int fuel = stack.getOrCreateNbt().getInt("fuel");
            if (fuel > 0) {
                stack.getOrCreateNbt().putInt("fuel", fuel - 10);
                InkProjectile ink = new InkProjectile((PlayerEntity)user, world);
                ink.setPosition(user.getEyePos().add(user.getRotationVector()));
                ink.setVelocity(user.getRotationVector().multiply(2.5f));
                world.spawnEntity(ink);
            }
        }
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return !stack.getOrCreateNbt().contains("fuel") || stack.getOrCreateNbt().getInt("fuel") < maxFuel;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (stack.getOrCreateNbt().contains("fuel")) {
            int fuel = stack.getOrCreateNbt().getInt("fuel");
            return 13 - Math.round(13f - fuel * 13f / (float)maxFuel);
        }
        return 0;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        if (stack.getOrCreateNbt().contains("fuel")) {
            int fuel = stack.getOrCreateNbt().getInt("fuel");
            float f = Math.max(0f, fuel / (float)maxFuel);
            return MathHelper.hsvToRgb(f / 3f, 1f, 1f);
        }
        return MathHelper.hsvToRgb(0f, 1f, 1f);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && otherStack.isOf(InkExpansion.INK_BUCKET)) {
            if (stack.getOrCreateNbt().contains("fuel") && stack.getOrCreateNbt().getInt("fuel") >= maxFuel) return false;
            cursorStackReference.set(new ItemStack(Items.BUCKET));
            stack.getOrCreateNbt().putInt("fuel", MathHelper.clamp(stack.getOrCreateNbt().getInt("fuel") + 1000, 0, maxFuel));
            return true;
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player) {
            if (player.hasStatusEffect(DefileStatusEffects.INKMORPHOSIS)) {
                PlayerInklingComponent inkling = DefileComponents.INKLING.get(player);
                if (inkling.isDiving()) {
                    stack.getOrCreateNbt().putInt("fuel", (stack.getOrCreateNbt().contains("fuel") ? stack.getOrCreateNbt().getInt("fuel") : 0) + 1);
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
