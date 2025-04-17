package com.pulsar.inkexpansion.entity;

import com.pulsar.inkexpansion.InkExpansion;
import doctor4t.defile.block.FuneralInkBlock;
import doctor4t.defile.index.DefileBlocks;
import doctor4t.defile.index.DefileStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class InkGlobuleProjectile extends ProjectileEntity {
    public InkGlobuleProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public InkGlobuleProjectile(World world) {
        super(InkExpansion.INK_GLOBULE_PROJECTILE, world);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public void tick() {
        super.tick();
        Vec3d vel = this.getVelocity();

        // i love random rotation
        this.setYaw(this.getYaw() + 5.72f);
        this.setPitch(this.getPitch() + 4.28f);

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
        this.setVelocity(velocity.x, velocity.y - 0.04f, velocity.z);

        this.setPosition(h, j, k);
        this.checkBlockCollision();
    }

    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1f), this::canHit);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        coverSurroundings();
        damageNearby();
        this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        coverSurroundings();
        damageNearby();
        this.discard();
    }

    private static final float radius = 3.5f;
    private void coverSurroundings() {
        BlockPos center = this.getBlockPos();
        for (int x = -MathHelper.ceil(radius); x <= MathHelper.ceil(radius); x++) {
            for (int y = -MathHelper.ceil(radius); y <= MathHelper.ceil(radius); y++) {
                for (int z = -MathHelper.ceil(radius); z <= MathHelper.ceil(radius); z++) {
                    BlockPos target = center.add(x, y, z);
                    if (center.toCenterPos().distanceTo(target.toCenterPos()) < radius) {
                        BlockState state = this.getWorld().getBlockState(target);
                        if ((state.isReplaceable() || state.isOf(DefileBlocks.FUNERAL_INK)) && state.getFluidState().isEmpty()) {
                            BlockState newState = state.isOf(DefileBlocks.FUNERAL_INK) ? state : DefileBlocks.FUNERAL_INK.getDefaultState();
                            for (Direction direction : Direction.values()) {
                                BlockPos neighbour = target.offset(direction);
                                BlockState neighbourState = this.getWorld().getBlockState(neighbour);
                                if (!neighbourState.isReplaceable()) {
                                    if (FuneralInkBlock.canGrowOn(this.getWorld(), direction, neighbour, neighbourState)) {
                                        newState = newState.with(ConnectingBlock.FACING_PROPERTIES.get(direction), true);
                                    }
                                }
                            }
                            this.getWorld().setBlockState(target, newState);
                        }
                    }
                }
            }
        }
    }

    private void damageNearby() {
        for (LivingEntity living : this.getWorld().getEntitiesByClass(LivingEntity.class, Box.of(this.getPos(),
                radius * 2f, radius * 2f, radius * 2f), (entity) -> entity.distanceTo(this) < radius)) {
            if (living.hasStatusEffect(DefileStatusEffects.INKMORPHOSIS)) continue;
            float damage = 17f * (4f - (float)this.getPos().distanceTo(living.getPos())) / 4f;
            living.damage(this.getDamageSources().create(InkExpansion.INK_DAMAGE_TYPE), damage);
            living.addStatusEffect(new StatusEffectInstance(InkExpansion.INKED, 240, 0));
        }
    }
}
