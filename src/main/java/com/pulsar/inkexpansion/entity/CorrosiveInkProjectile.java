package com.pulsar.inkexpansion.entity;

import com.pulsar.inkexpansion.InkExpansion;
import doctor4t.defile.index.DefileBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CorrosiveInkProjectile extends ProjectileEntity {
    public CorrosiveInkProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public CorrosiveInkProjectile(PlayerEntity player, World world) {
        super(InkExpansion.CORROSIVE_INK_PROJECTILE, world);
        this.setOwner(player);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public void tick() {
        super.tick();
        Vec3d vel = this.getVelocity();
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            double d = vel.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(vel.x, vel.z) * (double)(180F / (float)Math.PI)));
            this.setPitch((float)(MathHelper.atan2(vel.y, d) * (double)(180F / (float)Math.PI)));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }

        Vec3d pos = this.getPos();
        Vec3d nextPos = pos.add(vel);
        HitResult hitResult = this.getWorld().raycast(new RaycastContext(pos, nextPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            nextPos = hitResult.getPos();
        }

        if (!this.isRemoved()) {
            EntityHitResult entityHitResult = this.getEntityCollision(pos, nextPos);
            if (entityHitResult != null) {
                hitResult = entityHitResult;
            }

            if (hitResult.getType() == HitResult.Type.ENTITY) {
                Entity hit = entityHitResult.getEntity();
                Entity owner = this.getOwner();
                if (hit instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)hit)) {
                    hitResult = null;
                }
            }

            if (hitResult != null) {
                this.onCollision(hitResult);
                this.velocityDirty = true;
            }
        }

        vel = this.getVelocity();
        double e = vel.x;
        double f = vel.y;
        double g = vel.z;
        for(int i = 0; i < 2; ++i) {
            this.getWorld().addParticle(InkExpansion.PROJECTILE_INK, this.getX() + e * i / 4.0F, this.getY() + f * i / 4.0F, this.getZ() + g * i / 4.0F, 0f, 0f, 0f);
        }

        double h = this.getX() + e;
        double j = this.getY() + f;
        double k = this.getZ() + g;
        double l = vel.horizontalLength();
        this.setYaw((float)(MathHelper.atan2(e, g) * (double)(180F / (float)Math.PI)));

        this.setPitch((float)(MathHelper.atan2(f, l) * (double)(180F / (float)Math.PI)));
        this.setPitch(updateRotation(this.prevPitch, this.getPitch()));
        this.setYaw(updateRotation(this.prevYaw, this.getYaw()));
        float m = 0.99F;

        this.setVelocity(vel.multiply(m));
        Vec3d velocity = this.getVelocity();
        this.setVelocity(velocity.x, velocity.y - 0.08f, velocity.z);

        this.setPosition(h, j, k);
        this.checkBlockCollision();
    }

    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1f), this::canHit);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockPos pos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
        if (this.getWorld().getBlockState(pos).isReplaceable() || this.getWorld().getBlockState(pos).isOf(InkExpansion.CORROSIVE_INK)) {
            BlockState state = this.getWorld().getBlockState(pos).isOf(InkExpansion.CORROSIVE_INK) ? this.getWorld().getBlockState(pos) : InkExpansion.CORROSIVE_INK.getDefaultState();
            this.getWorld().setBlockState(pos, state.with(ConnectingBlock.FACING_PROPERTIES.get(blockHitResult.getSide().getOpposite()), true));
        }
        this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity living) {
            living.damage(this.getDamageSources().create(InkExpansion.INK_DAMAGE_TYPE), 5f);
            living.addStatusEffect(new StatusEffectInstance(InkExpansion.INKED, 120, 0));
            living.addStatusEffect(new StatusEffectInstance(InkExpansion.CORRODING, 60, 1));
        }
        this.discard();
    }
}
