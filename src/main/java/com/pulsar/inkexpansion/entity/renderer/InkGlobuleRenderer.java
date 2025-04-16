package com.pulsar.inkexpansion.entity.renderer;

import com.pulsar.inkexpansion.entity.InkGlobuleProjectile;
import com.pulsar.inkexpansion.entity.InkProjectile;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class InkGlobuleRenderer extends EntityRenderer<InkGlobuleProjectile> {
    public InkGlobuleRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(InkGlobuleProjectile entity, Frustum frustum, double x, double y, double z) {
        return false;
    }

    @Override
    public Identifier getTexture(InkGlobuleProjectile entity) {
        return null;
    }
}
