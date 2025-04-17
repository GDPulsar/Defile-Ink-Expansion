package com.pulsar.inkexpansion.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public class InkExpansionComponents implements EntityComponentInitializer, WorldComponentInitializer {
    public static final ComponentKey<InklingUpgradesComponent> INKLING_UPGRADES = ComponentRegistry.getOrCreate(Identifier.of("inkexpansion", "inkling_upgrades"), InklingUpgradesComponent.class);
    public static final ComponentKey<ExtendedBlackRainComponent> EXTENDED_BLACK_RAIN = ComponentRegistry.getOrCreate(Identifier.of("inkexpansion", "extended_black_rain"), ExtendedBlackRainComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(INKLING_UPGRADES, InklingUpgradesComponent::new, RespawnCopyStrategy.INVENTORY);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(EXTENDED_BLACK_RAIN, ExtendedBlackRainComponent::new);
    }
}
