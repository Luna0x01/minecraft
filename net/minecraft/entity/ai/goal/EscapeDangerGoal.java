package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class EscapeDangerGoal extends Goal {
	private PathAwareEntity mob;
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
		} else {
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
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}
}
