package com.pulsar.inkexpansion.effects;

import com.pulsar.inkexpansion.InkExpansion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CorrodingEffect extends StatusEffect {
    public CorrodingEffect() {
        super(StatusEffectCategory.HARMFUL, 0x087808);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getHealth() > 1.0F) {
            entity.damage(entity.getDamageSources().create(InkExpansion.CORROSIVE_INK_DAMAGE_TYPE), 4f);
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 25 >> amplifier;
        return i == 0 || duration % i == 0;
    }
}
