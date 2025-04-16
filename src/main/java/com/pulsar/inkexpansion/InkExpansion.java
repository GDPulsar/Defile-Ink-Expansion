package com.pulsar.inkexpansion;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.pulsar.inkexpansion.block.InkCauldronBlock;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import com.pulsar.inkexpansion.effects.InkedEffect;
import com.pulsar.inkexpansion.entity.InkGlobuleProjectile;
import com.pulsar.inkexpansion.entity.InkLightningEntity;
import com.pulsar.inkexpansion.entity.InkProjectile;
import com.pulsar.inkexpansion.item.BasicInkGunItem;
import com.pulsar.inkexpansion.item.InkBucketItem;
import com.pulsar.inkexpansion.item.InkEffigyItem;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import doctor4t.defile.Defile;
import doctor4t.defile.cca.PlayerInklingComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.minecraft.block.cauldron.CauldronBehavior.createMap;
import static net.minecraft.block.cauldron.CauldronBehavior.emptyCauldron;

public class InkExpansion implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("inkexpansion");

    private static final Map<Item, CauldronBehavior> INK_CAULDRON_BEHAVIOR = createMap();

    public static Item INK_BUCKET;
    public static Item FUNERAL_GOLD;
    public static Item FUNERAL_GOLD_BLOCK_ITEM;
    public static Item BASIC_INK_GUN;
    public static Item INK_EFFIGY;

    public static Block INK_CAULDRON;
    public static Block FUNERAL_GOLD_BLOCK;

    public static EntityType<InkLightningEntity> INK_LIGHTNING;
    public static EntityType<InkProjectile> INK_PROJECTILE;
    public static EntityType<InkGlobuleProjectile> INK_GLOBULE_PROJECTILE;

    public static StatusEffect INKED;

    public static DefaultParticleType PROJECTILE_INK;

    public static ItemGroup INK_EXPANSION_GROUP;

    @Override
    public void onInitialize() {
        INK_BUCKET = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_bucket"), new InkBucketItem(new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        FUNERAL_GOLD = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "funeral_gold"), new Item(new Item.Settings()));
        BASIC_INK_GUN = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "basic_ink_gun"), new BasicInkGunItem());
        INK_EFFIGY = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_effigy"), new InkEffigyItem(new Item.Settings().maxCount(1)));

        FUNERAL_GOLD_BLOCK = Registry.register(Registries.BLOCK, Identifier.of("inkexpansion", "funeral_gold_block"), new Block(AbstractBlock.Settings.copy(Blocks.GOLD_BLOCK)));
        FUNERAL_GOLD_BLOCK_ITEM = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "funeral_gold_block"), new BlockItem(FUNERAL_GOLD_BLOCK, new Item.Settings()));

        INK_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
                emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(INK_BUCKET), (statex) -> statex.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
        INK_CAULDRON_BEHAVIOR.put(Items.GOLD_INGOT, (state, world, pos, player, hand, stack) -> {
            if (stack.isOf(Items.GOLD_INGOT)) {
                if (!world.isClient) {
                    player.giveItemStack(new ItemStack(FUNERAL_GOLD));
                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                }

                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        });
        INK_CAULDRON = Registry.register(Registries.BLOCK, Identifier.of("inkexpansion", "ink_cauldron"), new InkCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON), (precipitation) -> true, INK_CAULDRON_BEHAVIOR));

        INK_LIGHTNING = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "ink_lightning"),
                EntityType.Builder.<InkLightningEntity>create(InkLightningEntity::new, SpawnGroup.MISC).disableSaving().maxTrackingRange(32).trackingTickInterval(32).build("ink_lightning"));
        INK_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "ink_projectile"),
                EntityType.Builder.<InkProjectile>create(InkProjectile::new, SpawnGroup.MISC).disableSaving().maxTrackingRange(32).trackingTickInterval(32).build("ink_projectile"));
        INK_GLOBULE_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "ink_globule"),
                EntityType.Builder.<InkGlobuleProjectile>create(InkGlobuleProjectile::new, SpawnGroup.MISC).disableSaving().maxTrackingRange(32).trackingTickInterval(32).build("ink_globule"));

        INKED = Registry.register(Registries.STATUS_EFFECT, Identifier.of("inkexpansion", "inked"), new InkedEffect());

        PROJECTILE_INK = Registry.register(Registries.PARTICLE_TYPE, Identifier.of("inkexpansion", "projectile_ink"), FabricParticleTypes.simple(true));

        INK_EXPANSION_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of("inkexpansion", "inkexpansion"),
                FabricItemGroup.builder()
                .icon(() -> new ItemStack(INK_EFFIGY)).displayName(Text.translatable("itemGroup.inkexpansion.inkexpansion"))
                        .entries((context, entries) -> {
                            entries.add(INK_BUCKET); entries.add(FUNERAL_GOLD); entries.add(FUNERAL_GOLD_BLOCK_ITEM); entries.add(INK_EFFIGY);
                            entries.add(BASIC_INK_GUN);
                        }).build());

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("inkexpansion")
                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("duration")
                            .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>argument("duration", IntegerArgumentType.integer(0))
                                    .executes(context -> {
                                        ExtendedBlackRainComponent extendedRain = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(context.getSource().getWorld());
                                        if (extendedRain.getAffectingEclipse(context.getSource().getPosition()) != null) {
                                            extendedRain.getAffectingEclipse(context.getSource().getPosition()).duration = IntegerArgumentType.getInteger(context, "duration");
                                        }
                                        return 1;
                                    })
                            )
                    ));
        }));
    }
}
