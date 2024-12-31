package net.minecraft.util;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomVectorGenerator {
	@Nullable
	public static Vec3d method_2799(PathAwareEntity mob, int i, int j) {
		return method_2802(mob, i, j, null);
	}

	@Nullable
	public static Vec3d method_13959(PathAwareEntity mob, int i, int j) {
		return method_13957(mob, i, j, null, false, 0.0);
	}

	@Nullable
	public static Vec3d method_2800(PathAwareEntity mob, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.subtract(mob.x, mob.y, mob.z);
		return method_2802(mob, i, j, vec3d2);
	}

	@Nullable
	public static Vec3d method_15715(PathAwareEntity pathAwareEntity, int i, int j, Vec3d vec3d, double d) {
		Vec3d vec3d2 = vec3d.subtract(pathAwareEntity.x, pathAwareEntity.y, pathAwareEntity.z);
		return method_13957(pathAwareEntity, i, j, vec3d2, true, d);
	}

	@Nullable
	public static Vec3d method_2801(PathAwareEntity mob, int i, int j, Vec3d vec3d) {
		Vec3d vec3d2 = new Vec3d(mob.x, mob.y, mob.z).subtract(vec3d);
		return method_2802(mob, i, j, vec3d2);
	}

	@Nullable
	private static Vec3d method_2802(PathAwareEntity mob, int i, int j, @Nullable Vec3d vec3d) {
		return method_13957(mob, i, j, vec3d, true, (float) (Math.PI / 2));
	}

	@Nullable
	private static Vec3d method_13957(PathAwareEntity pathAwareEntity, int i, int j, @Nullable Vec3d vec3d, boolean bl, double d) {
		EntityNavigation entityNavigation = pathAwareEntity.getNavigation();
		Random random = pathAwareEntity.getRandom();
		boolean bl2;
		if (pathAwareEntity.hasPositionTarget()) {
			double e = pathAwareEntity.getPositionTarget()
					.squaredDistanceTo((double)MathHelper.floor(pathAwareEntity.x), (double)MathHelper.floor(pathAwareEntity.y), (double)MathHelper.floor(pathAwareEntity.z))
				+ 4.0;
			double f = (double)(pathAwareEntity.getPositionTargetRange() + (float)i);
			bl2 = e < f * f;
		} else {
			bl2 = false;
		}

		boolean bl4 = false;
		float g = -99999.0F;
		int k = 0;
		int l = 0;
		int m = 0;

		for (int n = 0; n < 10; n++) {
			BlockPos blockPos = method_15716(random, i, j, vec3d, d);
			if (blockPos != null) {
				int o = blockPos.getX();
				int p = blockPos.getY();
				int q = blockPos.getZ();
				if (pathAwareEntity.hasPositionTarget() && i > 1) {
					BlockPos blockPos2 = pathAwareEntity.getPositionTarget();
					if (pathAwareEntity.x > (double)blockPos2.getX()) {
						o -= random.nextInt(i / 2);
					} else {
						o += random.nextInt(i / 2);
					}

					if (pathAwareEntity.z > (double)blockPos2.getZ()) {
						q -= random.nextInt(i / 2);
					} else {
						q += random.nextInt(i / 2);
					}
				}

				BlockPos blockPos3 = new BlockPos((double)o + pathAwareEntity.x, (double)p + pathAwareEntity.y, (double)q + pathAwareEntity.z);
				if ((!bl2 || pathAwareEntity.isInWalkTargetRange(blockPos3)) && entityNavigation.method_13110(blockPos3)) {
					if (!bl) {
						blockPos3 = method_13956(blockPos3, pathAwareEntity);
						if (method_13958(blockPos3, pathAwareEntity)) {
							continue;
						}
					}

					float h = pathAwareEntity.getPathfindingFavor(blockPos3);
					if (h > g) {
						g = h;
						k = o;
						l = p;
						m = q;
						bl4 = true;
					}
				}
			}
		}

		return bl4 ? new Vec3d((double)k + pathAwareEntity.x, (double)l + pathAwareEntity.y, (double)m + pathAwareEntity.z) : null;
	}

	@Nullable
	private static BlockPos method_15716(Random random, int i, int j, @Nullable Vec3d vec3d, double d) {
		if (vec3d != null && !(d >= Math.PI)) {
			double e = MathHelper.atan2(vec3d.z, vec3d.x) - (float) (Math.PI / 2);
			double f = e + (double)(2.0F * random.nextFloat() - 1.0F) * d;
			double g = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)i;
			double h = -g * Math.sin(f);
			double n = g * Math.cos(f);
			if (!(Math.abs(h) > (double)i) && !(Math.abs(n) > (double)i)) {
				int o = random.nextInt(2 * j + 1) - j;
				return new BlockPos(h, (double)o, n);
			} else {
				return null;
			}
		} else {
			int k = random.nextInt(2 * i + 1) - i;
			int l = random.nextInt(2 * j + 1) - j;
			int m = random.nextInt(2 * i + 1) - i;
			return new BlockPos(k, l, m);
		}
	}

	private static BlockPos method_13956(BlockPos blockPos, PathAwareEntity mob) {
		if (!mob.world.getBlockState(blockPos).getMaterial().isSolid()) {
			return blockPos;
		} else {
			BlockPos blockPos2 = blockPos.up();

			while (blockPos2.getY() < mob.world.getMaxBuildHeight() && mob.world.getBlockState(blockPos2).getMaterial().isSolid()) {
				blockPos2 = blockPos2.up();
			}

			return blockPos2;
		}
	}

	private static boolean method_13958(BlockPos blockPos, PathAwareEntity mob) {
		return mob.world.getFluidState(blockPos).matches(FluidTags.WATER);
	}
}
