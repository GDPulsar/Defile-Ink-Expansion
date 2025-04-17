package com.pulsar.inkexpansion.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class InkGlobuleModel extends Model {
    private final ModelPart root;

    public InkGlobuleModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("part1", ModelPartBuilder.create().uv(0, 0).cuboid(-40f, -40f, -16f, 80f, 80f, 32f), ModelTransform.NONE);
        modelPartData.addChild("part2", ModelPartBuilder.create().uv(0, 0).cuboid(-40f, -16f, -40f, 80f, 32f, 80f), ModelTransform.NONE);
        modelPartData.addChild("part3", ModelPartBuilder.create().uv(0, 0).cuboid(-16f, -40f, -40f, 32f, 80f, 80f), ModelTransform.NONE);
        modelPartData.addChild("part4", ModelPartBuilder.create().uv(0, 0).cuboid(-28f, -28f, -28f, 56f, 56f, 56f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
