package com.pulsar.inkexpansion.client;

import doctor4t.defile.client.particle.FallingInkParticle;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ProjectileInkParticle extends SpriteBillboardParticle {
    public ProjectileInkParticle(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.gravityStrength = 0.25f;
        this.scale *= 0.75F;
        this.maxAge = this.random.nextBetweenExclusive(20, 35);
        this.angle = (float)this.random.nextBetweenExclusive(0, 3) * ((float)Math.PI / 2F);
        this.prevAngle = this.angle;
    }

    public void tick() {
        super.tick();
        if (!this.dead && (this.onGround || this.isInFluid())) {
            this.markDead();
        }
    }

    protected boolean isInFluid() {
        BlockPos pos = BlockPos.ofFloored(this.x, this.y, this.z);
        FluidState fluidState = this.world.getFluidState(pos);
        if (fluidState.isEmpty()) {
            return false;
        } else {
            return this.y < (double)((float)pos.getY() + fluidState.getHeight(this.world, pos));
        }
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public @Nullable Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            ProjectileInkParticle particle = new ProjectileInkParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
