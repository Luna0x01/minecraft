package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;

public class CreeperIgniteGoal extends Goal {
	CreeperEntity creeper;
	LivingEntity target;

	public CreeperIgniteGoal(CreeperEntity creeperEntity) {
		this.creeper = creeperEntity;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.creeper.getTarget();
		return this.creeper.getFuseSpeed() > 0 || livingEntity != null && this.creeper.squaredDistanceTo(livingEntity) < 9.0;
	}

	@Override
	public void start() {
		this.creeper.getNavigation().stop();
		this.target = this.creeper.getTarget();
	}

	@Override
	public void stop() {
		this.target = null;
	}

	@Override
	public void tick() {
		if (this.target == null) {
			this.creeper.setFuseSpeed(-1);
		} else if (this.creeper.squaredDistanceTo(this.target) > 49.0) {
			this.creeper.setFuseSpeed(-1);
		} else if (!this.creeper.getVisibilityCache().canSee(this.target)) {
			this.creeper.setFuseSpeed(-1);
		} else {
			this.creeper.setFuseSpeed(1);
		}
	}
}
