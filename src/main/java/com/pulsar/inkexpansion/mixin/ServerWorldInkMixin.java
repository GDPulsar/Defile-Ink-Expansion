package com.pulsar.inkexpansion.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.entity.InkLightningEntity;
import doctor4t.defile.cca.DefileComponents;
import doctor4t.defile.cca.WorldBlackRainComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldInkMixin extends World {
    protected ServerWorldInkMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) { super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates); }

    @Shadow public abstract BlockPos getLightningPos(BlockPos pos);

    @Unique ExtendedBlackRainComponent cachedExtendedRainComponent = null;
    @Unique WorldBlackRainComponent cachedRainComponent = null;

    @ModifyExpressionValue(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;isRaining()Z"))
    private boolean inkexpansion$simulateRain1(boolean original) {
        return original || getRainComponent().isRaining();
    }

    @Definition(id = "precipitation", local = @Local(type = Biome.Precipitation.class))
    @Definition(id = "NONE", field = "Lnet/minecraft/world/biome/Biome$Precipitation;NONE:Lnet/minecraft/world/biome/Biome$Precipitation;")
    @Expression("precipitation != NONE")
    @ModifyExpressionValue(method = "tickChunk", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean inkexpansion$simulateRain2(boolean original) {
        return original || getRainComponent().isRaining();
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void inkexpansion$doEclipseLightning(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (this.random.nextInt(1000) == 0) {
            ChunkPos chunkPos = chunk.getPos();
            BlockPos blockPos = this.getLightningPos(this.getRandomPosInChunk(chunkPos.getStartX(), 0, chunkPos.getStartZ(), 15));
            ExtendedBlackRainComponent extendedRain = getExtendedRainComponent();
            if (extendedRain.shouldRainAt(blockPos.toCenterPos())) {
                int stormy = extendedRain.getAffectingEclipse(blockPos.toCenterPos()).stormy;
                if (stormy > 0) {
                    if (this.random.nextInt(75 / stormy) == 0) {
                        InkLightningEntity inkLightning = InkExpansion.INK_LIGHTNING.create(this);
                        if (inkLightning != null) {
                            inkLightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                            this.spawnEntity(inkLightning);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private WorldBlackRainComponent getRainComponent() {
        if (cachedRainComponent == null) {
            cachedRainComponent = DefileComponents.BLACK_RAIN.get((ServerWorld)(Object)this);
        }
        return cachedRainComponent;
    }

    @Unique
    private ExtendedBlackRainComponent getExtendedRainComponent() {
        if (cachedExtendedRainComponent == null) {
            cachedExtendedRainComponent = InkExpansionComponents.EXTENDED_BLACK_RAIN.get((ServerWorld)(Object)this);
        }
        return cachedExtendedRainComponent;
    }
}
