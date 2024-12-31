package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class EscapeDangerGoal extends Goal {
	protected final PathAwareEntity mob;
	protected double speed;
	protected double targetX;
	protected double targetY;
	protected double targetZ;

	public EscapeDangerGoal(PathAwareEntity pathAwareEntity, double d) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
			return false;
		} else {
			if (this.mob.isOnFire()) {
				BlockPos blockPos = this.method_15698(this.mob.world, this.mob, 5, 4);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				}
			}

			return this.method_13953();
		}
	}

	protected boolean method_13953() {
		Vec3d vec3d = RandomVectorGenerator.method_2799(this.mob, 5, 4);
		if (vec3d == null) {
			return false;
		} else {
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;
			return true;
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

	@Nullable
	protected BlockPos method_15698(BlockView blockView, Entity entity, int i, int j) {
		BlockPos blockPos = new BlockPos(entity);
		int k = blockPos.getX();
		int l = blockPos.getY();
		int m = blockPos.getZ();
		float f = (float)(i * i * j * 2);
		BlockPos blockPos2 = null;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int n = k - i; n <= k + i; n++) {
			for (int o = l - j; o <= l + j; o++) {
				for (int p = m - i; p <= m + i; p++) {
					mutable.setPosition(n, o, p);
					if (blockView.getFluidState(mutable).matches(FluidTags.WATER)) {
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
