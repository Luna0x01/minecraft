package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal {
	private MobEntity mob;

	public SwimGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.setCategoryBits(4);
		((MobNavigation)mobEntity.getNavigation()).setCanSwim(true);
	}

	@Override
	public boolean canStart() {
		return this.mob.isTouchingWater() || this.mob.isTouchingLava();
	}

	@Override
	public void tick() {
		if (this.mob.getRandom().nextFloat() < 0.8F) {
			this.mob.getJumpControl().setActive();
		}
	}
}
