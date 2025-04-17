package com.pulsar.inkexpansion;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.pulsar.inkexpansion.block.CorrosiveInk;
import com.pulsar.inkexpansion.block.InkCauldronBlock;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.component.InklingUpgradesComponent;
import com.pulsar.inkexpansion.effects.CorrodingEffect;
import com.pulsar.inkexpansion.effects.InkedEffect;
import com.pulsar.inkexpansion.entity.*;
import com.pulsar.inkexpansion.item.*;
import doctor4t.defile.cca.DefileComponents;
import doctor4t.defile.cca.WorldBlackRainComponent;
import doctor4t.defile.index.DefileBlocks;
import doctor4t.defile.index.DefileSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.minecraft.block.cauldron.CauldronBehavior.createMap;
import static net.minecraft.block.cauldron.CauldronBehavior.emptyCauldron;

public class InkExpansion implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("inkexpansion");

    private static final Map<Item, CauldronBehavior> INK_CAULDRON_BEHAVIOR = createMap();

    public static Item INK_BUCKET;
    public static Item CORROSIVE_INK_BUCKET;
    public static Item FUNERAL_GOLD;
    public static Item FUNERAL_GOLD_BLOCK_ITEM;
    public static Item INK_EFFIGY;
    public static Item NIGHT_EFFIGY;
    public static Item BASIC_INK_GUN;
    public static Item INK_SPREADER;
    public static Item INK_THROWER;
    public static Item CORROSIVE_INK_SPREADER;
    public static Item CORROSIVE_INK_THROWER;

    public static Item INK_COOKIE;
    public static Item DIVE_SPEED_COOKIE;
    public static Item DIVE_REGEN_COOKIE;
    public static Item JUMP_FILL_COOKIE;
    public static Item JUMP_HEIGHT_COOKIE;
    public static Item DAMAGING_SPLASHES_COOKIE;
    public static Item CORROSIVE_WAKES_COOKIE;
    public static Item REINFORCEMENT_COOKIE;

    public static Block INK_CAULDRON;
    public static Block FUNERAL_GOLD_BLOCK;
    public static Block CORROSIVE_INK;

    public static EntityType<InkLightningEntity> INK_LIGHTNING;
    public static EntityType<InkProjectile> INK_PROJECTILE;
    public static EntityType<InkBlobProjectile> INK_BLOB_PROJECTILE;
    public static EntityType<InkGlobuleProjectile> INK_GLOBULE_PROJECTILE;
    public static EntityType<CorrosiveInkProjectile> CORROSIVE_INK_PROJECTILE;
    public static EntityType<CorrosiveInkBlobProjectile> CORROSIVE_INK_BLOB_PROJECTILE;

    public static StatusEffect INKED;
    public static StatusEffect CORRODING;

    public static DefaultParticleType PROJECTILE_INK;

    public static final RegistryKey<DamageType> INK_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("inkexpansion", "ink"));
    public static final RegistryKey<DamageType> CORROSIVE_INK_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("inkexpansion", "corrosive_ink"));
    public static final RegistryKey<DamageType> BUCKET_CORROSION_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("inkexpansion", "bucket_corrosion"));

    public static final TagKey<Item> DANGER_ZONE_UPGRADES = TagKey.of(RegistryKeys.ITEM, new Identifier("inkexpansion", "danger_zone_upgrades"));
    public static final TagKey<Item> STORMY_UPGRADES = TagKey.of(RegistryKeys.ITEM, new Identifier("inkexpansion", "stormy_upgrades"));
    public static final TagKey<Item> COVERAGE_UPGRADES = TagKey.of(RegistryKeys.ITEM, new Identifier("inkexpansion", "coverage_upgrades"));
    public static final TagKey<Item> HEAVY_UPGRADES = TagKey.of(RegistryKeys.ITEM, new Identifier("inkexpansion", "heavy_upgrades"));
    public static final TagKey<Item> DURATION_UPGRADES = TagKey.of(RegistryKeys.ITEM, new Identifier("inkexpansion", "duration_upgrades"));

    public static ItemGroup INK_EXPANSION_GROUP;

    @Override
    public void onInitialize() {
        INK_BUCKET = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_bucket"), new InkBucketItem(new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        CORROSIVE_INK_BUCKET = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "corrosive_ink_bucket"), new CorrosiveInkBucketItem(new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        FUNERAL_GOLD = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "funeral_gold"), new Item(new Item.Settings()));
        INK_EFFIGY = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_effigy"), new InkEffigyItem(new Item.Settings().maxCount(1)));
        NIGHT_EFFIGY = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "night_effigy"), new NightEffigyItem(new Item.Settings().maxCount(1)));
        BASIC_INK_GUN = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "basic_ink_gun"), new BasicInkGunItem());
        INK_SPREADER = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_spreader"), new InkSpreaderItem());
        INK_THROWER = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_thrower"), new InkThrowerItem());
        CORROSIVE_INK_SPREADER = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "corrosive_ink_spreader"), new CorrosiveInkSpreaderItem());
        CORROSIVE_INK_THROWER = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "corrosive_ink_thrower"), new CorrosiveInkThrowerItem());

        INK_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_cookie"), new Item(new FabricItemSettings().food(FoodComponents.COOKIE)));
        DIVE_SPEED_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "dive_speed_cookie"),
                new InklingUpgradeItem(new FabricItemSettings(), InklingUpgradesComponent.UpgradeType.DIVE_SPEED));
        DIVE_REGEN_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "dive_regen_cookie"),
                new InklingUpgradeItem(new FabricItemSettings(), InklingUpgradesComponent.UpgradeType.DIVE_REGEN));
        JUMP_FILL_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "jump_fill_cookie"),
                new InklingUpgradeItem(new FabricItemSettings(), InklingUpgradesComponent.UpgradeType.JUMP_FILL));
        JUMP_HEIGHT_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "jump_height_cookie"),
                new InklingUpgradeItem(new FabricItemSettings(), InklingUpgradesComponent.UpgradeType.JUMP_HEIGHT));
        DAMAGING_SPLASHES_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "damaging_splashes_cookie"),
                new InklingUpgradeItem(new FabricItemSettings(), InklingUpgradesComponent.UpgradeType.DAMAGING_SPLASHES));
        CORROSIVE_WAKES_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "corrosive_wakes_cookie"),
                new InklingUpgradeItem(new FabricItemSettings(), InklingUpgradesComponent.UpgradeType.CORROSIVE_WAKES));
        REINFORCEMENT_COOKIE = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "reinforcement_cookie"),
                new InklingUpgradeItem(new FabricItemSettings(), InklingUpgradesComponent.UpgradeType.REINFORCEMENT));

        FUNERAL_GOLD_BLOCK = Registry.register(Registries.BLOCK, Identifier.of("inkexpansion", "funeral_gold_block"), new Block(AbstractBlock.Settings.copy(Blocks.GOLD_BLOCK)));
        FUNERAL_GOLD_BLOCK_ITEM = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "funeral_gold_block"), new BlockItem(FUNERAL_GOLD_BLOCK, new Item.Settings()));

        INK_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
                emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(INK_BUCKET), (statex) -> statex.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
        INK_CAULDRON_BEHAVIOR.put(Items.GOLD_INGOT, (state, world, pos, player, hand, stack) -> {
            if (stack.isOf(Items.GOLD_INGOT)) {
                if (!world.isClient) {
                    player.giveItemStack(new ItemStack(FUNERAL_GOLD));
                    stack.decrement(1);
                    world.playSoundAtBlockCenter(pos, DefileSounds.ENTITY_INK_DIVE, SoundCategory.BLOCKS, 1f, 1f, false);
                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                }

                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        });
        INK_CAULDRON_BEHAVIOR.put(Items.COOKIE, (state, world, pos, player, hand, stack) -> {
            if (stack.isOf(Items.COOKIE)) {
                if (!world.isClient) {
                    player.giveItemStack(new ItemStack(INK_COOKIE));
                    stack.decrement(1);
                    world.playSoundAtBlockCenter(pos, DefileSounds.ENTITY_INK_DIVE, SoundCategory.BLOCKS, 1f, 1f, false);
                    LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                }

                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        });
        INK_CAULDRON = Registry.register(Registries.BLOCK, Identifier.of("inkexpansion", "ink_cauldron"), new InkCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON), (precipitation) -> true, INK_CAULDRON_BEHAVIOR));
        CORROSIVE_INK = Registry.register(Registries.BLOCK, Identifier.of("inkexpansion", "corrosive_ink"), new CorrosiveInk(FabricBlockSettings.copyOf(DefileBlocks.FUNERAL_INK)));

        INK_LIGHTNING = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "ink_lightning"),
                EntityType.Builder.<InkLightningEntity>create(InkLightningEntity::new, SpawnGroup.MISC).disableSaving().maxTrackingRange(32).trackingTickInterval(32).build("ink_lightning"));
        INK_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "ink_projectile"),
                EntityType.Builder.<InkProjectile>create(InkProjectile::new, SpawnGroup.MISC).disableSaving()
                        .setDimensions(0.5f, 0.5f).maxTrackingRange(32).trackingTickInterval(32).build("ink_projectile"));
        INK_GLOBULE_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "ink_globule"),
                EntityType.Builder.<InkGlobuleProjectile>create(InkGlobuleProjectile::new, SpawnGroup.MISC).disableSaving()
                        .setDimensions(4f, 4f).maxTrackingRange(32).trackingTickInterval(32).build("ink_globule"));
        INK_BLOB_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "ink_blob"),
                EntityType.Builder.<InkBlobProjectile>create(InkBlobProjectile::new, SpawnGroup.MISC).disableSaving()
                        .setDimensions(2f, 2f).maxTrackingRange(32).trackingTickInterval(32).build("ink_blob"));
        CORROSIVE_INK_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "corrosive_ink_projectile"),
                EntityType.Builder.<CorrosiveInkProjectile>create(CorrosiveInkProjectile::new, SpawnGroup.MISC).disableSaving()
                        .setDimensions(0.5f, 0.5f).maxTrackingRange(32).trackingTickInterval(32).build("corrosive_ink_projectile"));
        CORROSIVE_INK_BLOB_PROJECTILE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("inkexpansion", "corrosive_ink_blob"),
                EntityType.Builder.<CorrosiveInkBlobProjectile>create(CorrosiveInkBlobProjectile::new, SpawnGroup.MISC).disableSaving()
                        .setDimensions(2f, 2f).maxTrackingRange(32).trackingTickInterval(32).build("corrosive_ink_blob"));

        INKED = Registry.register(Registries.STATUS_EFFECT, Identifier.of("inkexpansion", "inked"), new InkedEffect());
        CORRODING = Registry.register(Registries.STATUS_EFFECT, Identifier.of("inkexpansion", "corroding"), new CorrodingEffect());

        PROJECTILE_INK = Registry.register(Registries.PARTICLE_TYPE, Identifier.of("inkexpansion", "projectile_ink"), FabricParticleTypes.simple(true));

        INK_EXPANSION_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of("inkexpansion", "inkexpansion"),
                FabricItemGroup.builder()
                .icon(() -> new ItemStack(INK_EFFIGY)).displayName(Text.translatable("itemGroup.inkexpansion.inkexpansion"))
                        .entries((context, entries) -> {
                            entries.add(INK_BUCKET); entries.add(CORROSIVE_INK_BUCKET); entries.add(FUNERAL_GOLD); entries.add(FUNERAL_GOLD_BLOCK_ITEM);
                            entries.add(INK_EFFIGY); entries.add(NIGHT_EFFIGY); entries.add(BASIC_INK_GUN);
                            entries.add(INK_SPREADER); entries.add(INK_THROWER); entries.add(CORROSIVE_INK_SPREADER); entries.add(CORROSIVE_INK_THROWER);
                            entries.add(INK_COOKIE); entries.add(DIVE_SPEED_COOKIE); entries.add(DIVE_REGEN_COOKIE); entries.add(JUMP_FILL_COOKIE);
                            entries.add(JUMP_HEIGHT_COOKIE); entries.add(DAMAGING_SPLASHES_COOKIE); entries.add(CORROSIVE_WAKES_COOKIE); entries.add(REINFORCEMENT_COOKIE);
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

    private static WorldBlackRainComponent cachedRainComponent = null;
    private static ExtendedBlackRainComponent cachedExtendedRainComponent = null;

    public static WorldBlackRainComponent getRainComponent(World world) {
        if (cachedRainComponent == null) {
            cachedRainComponent = DefileComponents.BLACK_RAIN.get(world);
        }
        return cachedRainComponent;
    }

    public static ExtendedBlackRainComponent getExtendedRainComponent(World world) {
        if (cachedExtendedRainComponent == null) {
            cachedExtendedRainComponent = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(world);
        }
        return cachedExtendedRainComponent;
    }

    public static void reloadCache(World world) {
        cachedRainComponent = DefileComponents.BLACK_RAIN.get(world);
        cachedExtendedRainComponent = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(world);
    }
}
