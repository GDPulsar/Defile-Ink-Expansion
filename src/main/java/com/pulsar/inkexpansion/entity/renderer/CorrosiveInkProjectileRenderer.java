package com.pulsar.inkexpansion.entity.renderer;

import com.pulsar.inkexpansion.entity.CorrosiveInkProjectile;
import com.pulsar.inkexpansion.entity.InkProjectile;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class CorrosiveInkProjectileRenderer extends EntityRenderer<CorrosiveInkProjectile> {
    public CorrosiveInkProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(CorrosiveInkProjectile entity, Frustum frustum, double x, double y, double z) {
        return false;
    }

    @Override
    public Identifier getTexture(CorrosiveInkProjectile entity) {
        return null;
    }
}
