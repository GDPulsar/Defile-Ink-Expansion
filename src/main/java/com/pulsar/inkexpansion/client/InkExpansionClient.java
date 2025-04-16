package com.pulsar.inkexpansion.client;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.component.InkExpansionComponents;
import com.pulsar.inkexpansion.entity.renderer.InkGlobuleRenderer;
import com.pulsar.inkexpansion.entity.renderer.InkLightningRenderer;
import com.pulsar.inkexpansion.entity.renderer.InkProjectileRenderer;
import dev.doctor4t.ratatouille.client.lib.render.handlers.RenderHandler;
import dev.doctor4t.ratatouille.client.lib.render.systems.rendering.VFXBuilders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

import static doctor4t.defile.DefileClient.DARKNESS;
import static doctor4t.defile.DefileClient.FOLLY;

public class InkExpansionClient implements ClientModInitializer {
    public static boolean disableInkRenderer = false;

    private static float inkAlpha = 0f;
    public static final Identifier INKED_TEXTURE = Identifier.of("inkexpansion", "textures/gui/inked_effect.png");

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("figura")) {
            disableInkRenderer = true;
        }

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                if (player.hasStatusEffect(InkExpansion.INKED)) {
                    inkAlpha = MathHelper.clamp(inkAlpha + tickDelta, 0f, 1f);
                    int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
                    int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
                    drawContext.drawTexture(INKED_TEXTURE, 0, 0, width, height,
                            0, 0, 1920, 1080, 1920, 1080);
                } else {
                    inkAlpha = MathHelper.clamp(inkAlpha - tickDelta, 0f, 1f);
                }
            }
        });

        ParticleFactoryRegistry.getInstance().register(InkExpansion.PROJECTILE_INK, ProjectileInkParticle.Factory::new);

        EntityRendererRegistry.register(InkExpansion.INK_LIGHTNING, InkLightningRenderer::new);
        EntityRendererRegistry.register(InkExpansion.INK_PROJECTILE, InkProjectileRenderer::new);
        EntityRendererRegistry.register(InkExpansion.INK_GLOBULE_PROJECTILE, InkGlobuleRenderer::new);

        WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            World world = context.world();
            ExtendedBlackRainComponent eclipseComponent = getCachedExtendedRainComponent(world);
            for (ExtendedBlackRainComponent.Data eclipse : eclipseComponent.getDangerZones()) {
                MatrixStack matrices = context.matrixStack();
                matrices.push();
                VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld().setPosColorTexLightmapDefaultFormat();
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                Vec3d subtract = eclipse.pos.toCenterPos().subtract(player.getCameraPosVec(MinecraftClient.getInstance().getTickDelta()));
                matrices.translate(subtract.getX(), subtract.getY(), subtract.getZ());
                builder.setColor(new Color(16777215)).setAlpha(1.0F).renderSphere(RenderHandler.DELAYED_RENDER.getBuffer(DARKNESS), matrices, -100f, 50, 50);
                builder.setColor(new Color(16777215)).setAlpha(1.0F).renderSphere(RenderHandler.DELAYED_RENDER.getBuffer(DARKNESS), matrices, 100f, 50, 50);
                builder.setColor(new Color(16777215)).setAlpha(1.0F).renderSphere(RenderHandler.EARLY_DELAYED_RENDER.getBuffer(FOLLY), matrices, Math.min(-100f + 1.0F, 0.0F), 50, 50);
                matrices.pop();
            }
        });
    }

    public static ExtendedBlackRainComponent cachedExtendedRain;
    public static ExtendedBlackRainComponent getCachedExtendedRainComponent(World world) {
        if (cachedExtendedRain == null) {
            cachedExtendedRain = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(world);
        }

        return cachedExtendedRain;
    }

    public static void reloadCache(World world) {
        cachedExtendedRain = InkExpansionComponents.EXTENDED_BLACK_RAIN.get(world);
    }
}
