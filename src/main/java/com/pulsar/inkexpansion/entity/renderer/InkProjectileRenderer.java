package com.pulsar.inkexpansion.entity.renderer;

import com.pulsar.inkexpansion.entity.InkProjectile;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class InkProjectileRenderer extends EntityRenderer<InkProjectile> {
    public InkProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(InkProjectile entity, Frustum frustum, double x, double y, double z) {
        return false;
    }

    @Override
    public Identifier getTexture(InkProjectile entity) {
        return null;
    }
}
