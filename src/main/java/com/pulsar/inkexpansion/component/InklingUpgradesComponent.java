package com.pulsar.inkexpansion.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class InklingUpgradesComponent implements AutoSyncedComponent {
    public float diveSpeedMult = 1f;
    public float diveRegenMult = 1f;
    public float jumpFillRateMult = 1f;
    public float jumpHeightMult = 1f;
    public int damagingSplashes = 0;
    public int corrosiveWakes = 0;
    public float reinforcement = 0f;

    public float currentReinforcement = 0f;

    public InklingUpgradesComponent(PlayerEntity player) {}

    @Override
    public void readFromNbt(NbtCompound nbt) {
        diveSpeedMult = nbt.getFloat("diveSpeedMult");
        diveRegenMult = nbt.getFloat("diveRegenMult");
        jumpFillRateMult = nbt.getFloat("jumpFillRateMult");
        jumpHeightMult = nbt.getFloat("jumpHeightMult");
        damagingSplashes = nbt.getInt("damagingSplashes");
        corrosiveWakes = nbt.getInt("corrosiveWakes");
        reinforcement = nbt.getFloat("reinforcement");
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putFloat("diveSpeedMult", diveSpeedMult);
        nbt.putFloat("diveRegenMult", diveRegenMult);
        nbt.putFloat("jumpFillRateMult", jumpFillRateMult);
        nbt.putFloat("jumpHeightMult", jumpHeightMult);
        nbt.putInt("damagingSplashes", damagingSplashes);
        nbt.putInt("corrosiveWakes", corrosiveWakes);
        nbt.putFloat("reinforcement", reinforcement);
    }
}
