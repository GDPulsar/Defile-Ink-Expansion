package com.pulsar.inkexpansion.client;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.component.ExtendedBlackRainComponent;
import com.pulsar.inkexpansion.entity.model.InkBlobModel;
import com.pulsar.inkexpansion.entity.model.InkGlobuleModel;
import com.pulsar.inkexpansion.entity.renderer.*;
import com.pulsar.inkexpansion.packet.DamagingSplashPacket;
import dev.doctor4t.ratatouille.client.lib.render.handlers.RenderHandler;
import dev.doctor4t.ratatouille.client.lib.render.systems.rendering.VFXBuilders;
import doctor4t.defile.index.DefileSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.figuramc.figura.avatar.AvatarManager;

import java.awt.*;

import static doctor4t.defile.DefileClient.DARKNESS;
import static doctor4t.defile.DefileClient.FOLLY;

public class InkExpansionClient implements ClientModInitializer {
    public static boolean disableInkRenderer = false;

    public static float inkAlpha = 0f;
    public static final Identifier INKED_TEXTURE = Identifier.of("inkexpansion", "textures/gui/inked_effect.png");

    public static final EntityModelLayer INK_GLOBULE_LAYER = new EntityModelLayer(Identifier.of("inkexpansion", "ink_globule"), "ink_globule");
    public static final EntityModelLayer INK_BLOB_LAYER = new EntityModelLayer(Identifier.of("inkexpansion", "ink_blob"), "ink_blob");

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (FabricLoader.getInstance().isModLoaded("figura")) {
                    if (AvatarManager.getAvatarForPlayer(client.player.getUuid()) != null) disableInkRenderer = true;
                }
                if (client.player.hasStatusEffect(InkExpansion.INKED)) {
                    inkAlpha = MathHelper.clamp(inkAlpha + 0.05f, 0f, 1f);
                } else {
                    inkAlpha = MathHelper.clamp(inkAlpha - 0.02f, 0f, 1f);
                }
            }
        });

        ParticleFactoryRegistry.getInstance().register(InkExpansion.PROJECTILE_INK, ProjectileInkParticle.Factory::new);

        EntityModelLayerRegistry.registerModelLayer(INK_GLOBULE_LAYER, InkGlobuleModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(INK_BLOB_LAYER, InkBlobModel::getTexturedModelData);

        EntityRendererRegistry.register(InkExpansion.INK_LIGHTNING, InkLightningRenderer::new);
        EntityRendererRegistry.register(InkExpansion.INK_PROJECTILE, InkProjectileRenderer::new);
        EntityRendererRegistry.register(InkExpansion.INK_BLOB_PROJECTILE, InkBlobRenderer::new);
        EntityRendererRegistry.register(InkExpansion.INK_GLOBULE_PROJECTILE, InkGlobuleRenderer::new);
        EntityRendererRegistry.register(InkExpansion.CORROSIVE_INK_PROJECTILE, CorrosiveInkProjectileRenderer::new);
        EntityRendererRegistry.register(InkExpansion.CORROSIVE_INK_BLOB_PROJECTILE, CorrosiveInkBlobRenderer::new);

        WorldRenderEvents.BEFORE_ENTITIES.register((context) -> {
            World world = context.world();
            ExtendedBlackRainComponent eclipseComponent = InkExpansion.getExtendedRainComponent(world);
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

        ClientPlayNetworking.registerGlobalReceiver(DamagingSplashPacket.ID, (client, handler, buf, responseSender) -> {
            DamagingSplashPacket packet = new DamagingSplashPacket(buf);
            Entity entity = packet.getEntity(client.world);
            client.execute(() -> {
                if (entity != null) {
                    client.particleManager.addEmitter(entity, InkExpansion.PROJECTILE_INK, 30);
                    client.world.playSound(entity.getX(), entity.getY(), entity.getZ(), DefileSounds.ENTITY_INK_EXPLODE, entity.getSoundCategory(), 1f, 0.9f, false);
                }
            });
        });
    }
}
