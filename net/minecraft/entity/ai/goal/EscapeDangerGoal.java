package net.minecraft.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EscapeDangerGoal extends Goal {
	private final PathAwareEntity mob;
	protected double speed;
	private double targetX;
	private double targetY;
	private double targetZ;

	public EscapeDangerGoal(PathAwareEntity pathAwareEntity, double d) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
			return false;
		} else if (!this.mob.isOnFire()) {
			Vec3d vec3d = RandomVectorGenerator.method_2799(this.mob, 5, 4);
			if (vec3d == null) {
				return false;
			} else {
				this.targetX = vec3d.x;
				this.targetY = vec3d.y;
				this.targetZ = vec3d.z;
				return true;
			}
		} else {
			BlockPos blockPos = this.method_13100(this.mob.world, this.mob, 5, 4);
			if (blockPos == null) {
				return false;
			} else {
				this.targetX = (double)blockPos.getX();
				this.targetY = (double)blockPos.getY();
				this.targetZ = (double)blockPos.getZ();
				return true;
			}
		}
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	private BlockPos method_13100(World world, Entity entity, int i, int j) {
		BlockPos blockPos = new BlockPos(entity);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int k = blockPos.getX();
		int l = blockPos.getY();
		int m = blockPos.getZ();
		float f = (float)(i * i * j * 2);
		BlockPos blockPos2 = null;

		for (int n = k - i; n <= k + i; n++) {
			for (int o = l - j; o <= l + j; o++) {
				for (int p = m - i; p <= m + i; p++) {
					mutable.setPosition(n, o, p);
					BlockState blockState = world.getBlockState(mutable);
					Block block = blockState.getBlock();
					if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
						float g = (float)((n - k) * (n - k) + (o - l) * (o - l) + (p - m) * (p - m));
						if (g < f) {
							f = g;
							blockPos2 = new BlockPos(mutable);
						}
					}
				}
			}
		}

		return blockPos2;
	}
}
