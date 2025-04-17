package com.pulsar.inkexpansion.entity.renderer;

import com.pulsar.inkexpansion.client.InkExpansionClient;
import com.pulsar.inkexpansion.entity.CorrosiveInkBlobProjectile;
import com.pulsar.inkexpansion.entity.InkBlobProjectile;
import com.pulsar.inkexpansion.entity.model.InkBlobModel;
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

public class CorrosiveInkBlobRenderer extends EntityRenderer<CorrosiveInkBlobProjectile> {
    public static final Identifier TEXTURE = new Identifier("inkexpansion", "textures/block/corrosive_ink.png");
    private final InkBlobModel model;

    public CorrosiveInkBlobRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new InkBlobModel(ctx.getPart(InkExpansionClient.INK_BLOB_LAYER));
    }

    @Override
    public void render(CorrosiveInkBlobProjectile entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + 90f));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity)));
        this.model.render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(CorrosiveInkBlobProjectile entity) {
        return TEXTURE;
    }
}
