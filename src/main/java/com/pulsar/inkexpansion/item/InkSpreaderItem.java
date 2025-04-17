package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.entity.InkProjectile;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InkSpreaderItem extends Item {
    private final int maxFuel = 10000;

    public InkSpreaderItem() {
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
                stack.getOrCreateNbt().putInt("fuel", fuel - 5);
                for (int i = -1; i <= 1; i++) {
                    InkProjectile ink = new InkProjectile((PlayerEntity)user, world);
                    float f = user.getPitch() * MathHelper.RADIANS_PER_DEGREE;
                    float g = -(user.getYaw() + i * 7.5f) * MathHelper.RADIANS_PER_DEGREE;
                    Vec3d direction = new Vec3d(MathHelper.sin(g) * MathHelper.cos(f), -MathHelper.sin(f), MathHelper.cos(g) * MathHelper.cos(f));
                    ink.setPosition(user.getEyePos().add(direction));
                    ink.setVelocity(direction.multiply(2.5f));
                    world.spawnEntity(ink);
                }
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
}
