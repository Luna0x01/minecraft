package net.minecraft.entity.ai.goal;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal extends Goal {
	private final PathAwareEntity mob;
	private double targetX;
	private double targetY;
	private double targetZ;
	private final double speed;
	private int chance;
	private boolean ignoringChance;

	public WanderAroundGoal(PathAwareEntity pathAwareEntity, double d) {
		this(pathAwareEntity, d, 120);
	}

	public WanderAroundGoal(PathAwareEntity pathAwareEntity, double d, int i) {
		this.mob = pathAwareEntity;
		this.speed = d;
		this.chance = i;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (!this.ignoringChance) {
			if (this.mob.method_6117() >= 100) {
				return false;
			}

			if (this.mob.getRandom().nextInt(this.chance) != 0) {
				return false;
			}
		}

		Vec3d vec3d = RandomVectorGenerator.method_2799(this.mob, 10, 7);
		if (vec3d == null) {
			return false;
		} else {
			this.targetX = vec3d.x;
			this.targetY = vec3d.y;
			this.targetZ = vec3d.z;
			this.ignoringChance = false;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	public void ignoreChanceOnce() {
		this.ignoringChance = true;
	}

	public void setChance(int chance) {
		this.chance = chance;
	}
}
