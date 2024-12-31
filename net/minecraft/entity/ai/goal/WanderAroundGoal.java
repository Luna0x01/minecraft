package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class WanderAroundGoal extends Goal {
	protected final PathAwareEntity mob;
	protected double targetX;
	protected double targetY;
	protected double targetZ;
	protected final double speed;
	protected int chance;
	protected boolean ignoringChance;

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

		Vec3d vec3d = this.method_13954();
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

	@Nullable
	protected Vec3d method_13954() {
		return RandomVectorGenerator.method_2799(this.mob, 10, 7);
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
