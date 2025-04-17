package com.pulsar.inkexpansion.entity.renderer;

import com.pulsar.inkexpansion.InkExpansion;
import com.pulsar.inkexpansion.client.InkExpansionClient;
import com.pulsar.inkexpansion.entity.InkGlobuleProjectile;
import com.pulsar.inkexpansion.entity.model.InkGlobuleModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class InkGlobuleRenderer extends EntityRenderer<InkGlobuleProjectile> {
    public static final Identifier TEXTURE = new Identifier("defile", "textures/block/funeral_ink.png");
    private final InkGlobuleModel model;

    public InkGlobuleRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new InkGlobuleModel(ctx.getPart(InkExpansionClient.INK_GLOBULE_LAYER));
    }

    @Override
    public void render(InkGlobuleProjectile entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90f));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity)));
        this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

    @Override
    public boolean shouldRender(InkGlobuleProjectile entity, Frustum frustum, double x, double y, double z) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft.cameraEntity != null && InkExpansion.getExtendedRainComponent(minecraft.world).shouldRainAt(minecraft.cameraEntity.getPos())) {
            return true;
        }
        return minecraft.player == null || minecraft.player.isCreative() || minecraft.player.isSpectator();
    }

    @Override
    public Identifier getTexture(InkGlobuleProjectile entity) {
        return TEXTURE;
    }
}
