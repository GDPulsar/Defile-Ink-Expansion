package com.pulsar.inkexpansion.effects;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class InkedEffect extends StatusEffect {
    public InkedEffect() {
        super(StatusEffectCategory.HARMFUL, 0x080808);
        this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "d39f703a-ba88-48fa-98fb-523d9d5ed59b", -0.3, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
