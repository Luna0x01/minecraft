package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class GoToEntityTargetGoal extends Goal {
	private final PathAwareEntity mob;
	private LivingEntity target;
	private double x;
	private double y;
	private double z;
	private final double speed;
	private final float maxDistance;

	public GoToEntityTargetGoal(PathAwareEntity pathAwareEntity, double d, float f) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.maxDistance = f;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		this.target = this.mob.getTarget();
		if (this.target == null) {
			return false;
		} else if (this.target.squaredDistanceTo(this.mob) > (double)(this.maxDistance * this.maxDistance)) {
			return false;
		} else {
			Vec3d vec3d = RandomVectorGenerator.method_2800(this.mob, 16, 7, new Vec3d(this.target.x, this.target.y, this.target.z));
			if (vec3d == null) {
				return false;
			} else {
				this.x = vec3d.x;
				this.y = vec3d.y;
				this.z = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle() && this.target.isAlive() && this.target.squaredDistanceTo(this.mob) < (double)(this.maxDistance * this.maxDistance);
	}

	@Override
	public void stop() {
		this.target = null;
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
	}
}
