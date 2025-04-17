package com.pulsar.inkexpansion.component;

import com.pulsar.inkexpansion.InkExpansion;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import doctor4t.defile.cca.DefileComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class InklingUpgradesComponent implements AutoSyncedComponent, ServerTickingComponent {
    private final HashMap<UpgradeType, Integer> upgrades = new HashMap<>();

    public float currentReinforcement = 0f;

    private final PlayerEntity player;

    public InklingUpgradesComponent(PlayerEntity player) {
        this.player = player;
    }

    private void sync() {
        InkExpansionComponents.INKLING_UPGRADES.sync(this.player);
    }

    public void clearUpgrades() {
        for (UpgradeType upgrade : UpgradeType.values()) {
            this.upgrades.put(upgrade, 0);
        }
    }

    public float getUpgradeVal(UpgradeType upgradeType) {
        return upgradeType.base + upgradeType.increase * upgrades.getOrDefault(upgradeType, 0);
    }

    public void setUpgradeVal(UpgradeType upgradeType, int count) {
        upgrades.put(upgradeType, count);
    }

    public boolean canUpgrade(UpgradeType upgradeType) {
        return upgrades.getOrDefault(upgradeType, 0) < upgradeType.max;
    }

    public void upgrade(UpgradeType upgradeType) {
        if (canUpgrade(upgradeType)) {
            setUpgradeVal(upgradeType, upgrades.getOrDefault(upgradeType, 0) + 1);
            sync();
        }
    }

    @Override
    public void serverTick() {
        if (DefileComponents.INKLING.get(this.player).isDiving()) {
            this.currentReinforcement = MathHelper.clamp(this.currentReinforcement + 0.005f, 0f, getUpgradeVal(UpgradeType.REINFORCEMENT));
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        for (UpgradeType upgrade : UpgradeType.values()) {
            upgrades.put(upgrade, nbt.contains(upgrade.name) ? nbt.getInt(upgrade.name) : 0);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        for (UpgradeType upgrade : UpgradeType.values()) {
            if (upgrades.getOrDefault(upgrade, 0) > 0) nbt.putInt(upgrade.name, upgrades.get(upgrade));
        }
    }

    public enum UpgradeType {
        DIVE_SPEED("dive_speed", 1f, 0.1f, 10),
        DIVE_REGEN("dive_regen", 1f, 0.1f, 5),
        JUMP_FILL("jump_fill", 1f, 0.1f, 10),
        JUMP_HEIGHT("jump_height", 1f, 0.1f, 10),
        DAMAGING_SPLASHES("damaging_splashes", 0, 1, 3),
        CORROSIVE_WAKES("corrosive_wakes", 0, 1, 3),
        REINFORCEMENT("reinforcement", 0, 0.5f, 2);

        public final String name;
        public final float base;
        public final float increase;
        public final int max;
        UpgradeType(String name, float base, float increase, int max) {
            this.name = name;
            this.base = base;
            this.increase = increase;
            this.max = max;
        }
    }
}
