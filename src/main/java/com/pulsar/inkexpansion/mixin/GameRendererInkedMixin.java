package com.pulsar.inkexpansion.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.pulsar.inkexpansion.InkExpansion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.pulsar.inkexpansion.client.InkExpansionClient.INKED_TEXTURE;
import static com.pulsar.inkexpansion.client.InkExpansionClient.inkAlpha;

@Mixin(GameRenderer.class)
public class GameRendererInkedMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0))
    private void inkexpansion$renderInkedOverlay(float tickDelta, long startTime, boolean tick, CallbackInfo ci, @Local DrawContext context) {
        if (this.client.player != null) {
            if (inkAlpha > 0) {
                int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
                int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
                RenderSystem.setShaderTexture(0, INKED_TEXTURE);
                RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableDepthTest();
                Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                bufferBuilder.vertex(matrix4f, 0f, 0f, 5f).color(
                        ColorHelper.Argb.getArgb((int)(inkAlpha * 255f), 255, 255, 255)).texture(0f, 0f).next();
                bufferBuilder.vertex(matrix4f, 0f, height, 5f).color(
                        ColorHelper.Argb.getArgb((int)(inkAlpha * 255f), 255, 255, 255)).texture(0f, 1f).next();
                bufferBuilder.vertex(matrix4f, width, height, 5f).color(
                        ColorHelper.Argb.getArgb((int)(inkAlpha * 255f), 255, 255, 255)).texture(1f, 1f).next();
                bufferBuilder.vertex(matrix4f, width, 0f, 5f).color(
                        ColorHelper.Argb.getArgb((int)(inkAlpha * 255f), 255, 255, 255)).texture(1f, 0f).next();
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            }
        }
    }
}
