package com.pulsar.inkexpansion.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class InkBlobModel extends Model {
    private final ModelPart root;

    public InkBlobModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("part1", ModelPartBuilder.create().uv(0, 0).cuboid(-20f, -20f, -8f, 40f, 40f, 16f), ModelTransform.NONE);
        modelPartData.addChild("part2", ModelPartBuilder.create().uv(0, 0).cuboid(-20f, -8f, -20f, 40f, 16f, 40f), ModelTransform.NONE);
        modelPartData.addChild("part3", ModelPartBuilder.create().uv(0, 0).cuboid(-8f, -20f, -20f, 16f, 40f, 40f), ModelTransform.NONE);
        modelPartData.addChild("part4", ModelPartBuilder.create().uv(0, 0).cuboid(-14f, -14f, -14f, 28f, 28f, 28f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
