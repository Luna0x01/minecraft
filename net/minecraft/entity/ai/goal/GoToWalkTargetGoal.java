package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.Vec3d;

public class GoToWalkTargetGoal extends Goal {
	private final MobEntityWithAi mob;
	private double x;
	private double y;
	private double z;
	private final double speed;

	public GoToWalkTargetGoal(MobEntityWithAi mobEntityWithAi, double d) {
		this.mob = mobEntityWithAi;
		this.speed = d;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
	}

	@Override
	public boolean canStart() {
		if (this.mob.isInWalkTargetRange()) {
			return false;
		} else {
			Vec3d vec3d = TargetFinder.findTargetTowards(this.mob, 16, 7, new Vec3d(this.mob.getPositionTarget()));
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
		return !this.mob.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
	}
}
