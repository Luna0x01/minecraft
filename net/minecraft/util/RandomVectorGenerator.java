package net.minecraft.util;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomVectorGenerator {
	private static Vec3d field_3660 = Vec3d.ZERO;

	@Nullable
	public static Vec3d method_2799(PathAwareEntity mob, int i, int j) {
		return method_2802(mob, i, j, null);
	}

	@Nullable
	public static Vec3d method_13959(PathAwareEntity mob, int i, int j) {
		return method_13957(mob, i, j, null, false);
	}

	@Nullable
	public static Vec3d method_2800(PathAwareEntity mob, int i, int j, Vec3d vec3d) {
		field_3660 = vec3d.subtract(mob.x, mob.y, mob.z);
		return method_2802(mob, i, j, field_3660);
	}

	@Nullable
	public static Vec3d method_2801(PathAwareEntity mob, int i, int j, Vec3d vec3d) {
		field_3660 = new Vec3d(mob.x, mob.y, mob.z).subtract(vec3d);
		return method_2802(mob, i, j, field_3660);
	}

	@Nullable
	private static Vec3d method_2802(PathAwareEntity mob, int i, int j, @Nullable Vec3d vec3d) {
		return method_13957(mob, i, j, vec3d, true);
	}

	@Nullable
	private static Vec3d method_13957(PathAwareEntity mob, int i, int j, @Nullable Vec3d vec3d, boolean bl) {
		EntityNavigation entityNavigation = mob.getNavigation();
		Random random = mob.getRandom();
		boolean bl2;
		if (mob.hasPositionTarget()) {
			double d = mob.getPositionTarget().squaredDistanceTo((double)MathHelper.floor(mob.x), (double)MathHelper.floor(mob.y), (double)MathHelper.floor(mob.z))
				+ 4.0;
			double e = (double)(mob.getPositionTargetRange() + (float)i);
			bl2 = d < e * e;
		} else {
			bl2 = false;
		}

		boolean bl4 = false;
		float f = -99999.0F;
		int k = 0;
		int l = 0;
		int m = 0;

		for (int n = 0; n < 10; n++) {
			int o = random.nextInt(2 * i + 1) - i;
			int p = random.nextInt(2 * j + 1) - j;
			int q = random.nextInt(2 * i + 1) - i;
			if (vec3d == null || !((double)o * vec3d.x + (double)q * vec3d.z < 0.0)) {
				if (mob.hasPositionTarget() && i > 1) {
					BlockPos blockPos = mob.getPositionTarget();
					if (mob.x > (double)blockPos.getX()) {
						o -= random.nextInt(i / 2);
					} else {
						o += random.nextInt(i / 2);
					}

					if (mob.z > (double)blockPos.getZ()) {
						q -= random.nextInt(i / 2);
					} else {
						q += random.nextInt(i / 2);
					}
				}

				BlockPos blockPos2 = new BlockPos((double)o + mob.x, (double)p + mob.y, (double)q + mob.z);
				if ((!bl2 || mob.isInWalkTargetRange(blockPos2)) && entityNavigation.method_13110(blockPos2)) {
					if (!bl) {
						blockPos2 = method_13956(blockPos2, mob);
						if (method_13958(blockPos2, mob)) {
							continue;
						}
					}

					float g = mob.getPathfindingFavor(blockPos2);
					if (g > f) {
						f = g;
						k = o;
						l = p;
						m = q;
						bl4 = true;
					}
				}
			}
		}

		return bl4 ? new Vec3d((double)k + mob.x, (double)l + mob.y, (double)m + mob.z) : null;
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
		return mob.world.getBlockState(blockPos).getMaterial() == Material.WATER;
	}
}
