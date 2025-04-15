package com.pulsar.inkexpansion;

import com.pulsar.inkexpansion.block.InkCauldronBlock;
import com.pulsar.inkexpansion.item.InkBucketItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
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
    public static Block INK_CAULDRON;

    @Override
    public void onInitialize() {
        INK_BUCKET = Registry.register(Registries.ITEM, Identifier.of("inkexpansion", "ink_bucket"), new InkBucketItem(new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1)));
        INK_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) ->
                emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(INK_BUCKET), (statex) -> statex.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
        INK_CAULDRON = Registry.register(Registries.BLOCK, Identifier.of("inkexpansion", "ink_cauldron"), new InkCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON), (precipitation) -> true, INK_CAULDRON_BEHAVIOR));
    }
}
