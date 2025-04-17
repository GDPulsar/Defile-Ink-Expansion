package com.pulsar.inkexpansion.util;

import com.pulsar.inkexpansion.InkExpansion;
import doctor4t.defile.block.FuneralInkBlock;
import doctor4t.defile.index.DefileBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpecialInksplosion {
    public static void inksplode(World world, @Nullable Entity entity, double x, double y, double z, float power, Block inkType) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = j / 15f * 2f - 1f;
                        double e = k / 15f * 2f - 1f;
                        double f = l / 15f * 2f - 1f;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = power * (0.7f + world.random.nextFloat() * 0.6f);
                        double m = x;
                        double n = y;
                        double o = z;

                        for(; h > 0.0F; h -= 0.225f) {
                            mutable.set(m, n, o);
                            if (!world.isInBuildLimit(mutable)) {
                                break;
                            }

                            affectPos(world, entity, new Vec3d(x, y, z), mutable, inkType);
                            m += d * 0.3f;
                            n += e * 0.3f;
                            o += f * 0.3f;
                        }
                    }
                }
            }
        }

    }

    private static void affectPos(World world, Entity entity, Vec3d pos, BlockPos.Mutable blockPos, Block inkType) {
        double pX = blockPos.getX();
        double pY = blockPos.getY();
        double pZ = blockPos.getZ();
        BlockState blockState = world.getBlockState(blockPos);

        for(Direction direction : Direction.values()) {
            blockPos.move(direction);
            BlockHitResult hit = world.raycast(new RaycastContext(Vec3d.ofCenter(blockPos), pos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
            if (FuneralInkBlock.canGrowOn(world, direction.getOpposite(), blockPos, blockState) && hit.getBlockPos().equals(entity.getBlockPos())) {
                BlockState blockStateToInk = world.getBlockState(blockPos);
                if (FuneralInkBlock.canBreak(world, blockPos, blockStateToInk)) {
                    world.breakBlock(blockPos, true);
                }

                if (blockStateToInk.isOf(DefileBlocks.FUNERAL_INK) || blockStateToInk.isOf(InkExpansion.CORROSIVE_INK)) {
                    world.setBlockState(blockPos, blockStateToInk.with(FuneralInkBlock.getProperty(direction.getOpposite()), true));
                } else if (blockStateToInk.isAir()) {
                    world.setBlockState(blockPos, inkType.getDefaultState().with(FuneralInkBlock.getProperty(direction.getOpposite()), true));
                }
            }

            blockPos.set(pX, pY, pZ);
        }

    }
}
