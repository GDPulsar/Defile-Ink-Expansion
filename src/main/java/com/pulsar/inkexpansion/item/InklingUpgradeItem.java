package com.pulsar.inkexpansion.item;

import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class InklingUpgradeItem extends Item {
    private final InklingUpgradesComponent.UpgradeType upgradeType;
    public InklingUpgradeItem(Settings settings, InklingUpgradesComponent.UpgradeType upgradeType) {
        super(settings.food(new FoodComponent.Builder().alwaysEdible().build()));
        this.upgradeType = upgradeType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.get(user);
        if (upgrades.canUpgrade(upgradeType)) {
            return super.use(world, user, hand);
        }
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        InklingUpgradesComponent upgrades = InkExpansionComponents.INKLING_UPGRADES.get(user);
        upgrades.upgrade(upgradeType);
        return super.finishUsing(stack, world, user);
    }
}
