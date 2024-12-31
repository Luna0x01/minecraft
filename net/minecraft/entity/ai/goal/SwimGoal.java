package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal {
	private final MobEntity mob;

	public SwimGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.setCategoryBits(4);
		mobEntity.getNavigation().method_15709(true);
	}

	@Override
	public boolean canStart() {
		return this.mob.isTouchingWater() && this.mob.method_15583() > 0.4 || this.mob.isTouchingLava();
	}

	@Override
	public void tick() {
		if (this.mob.getRandom().nextFloat() < 0.8F) {
			this.mob.getJumpControl().setActive();
		}
	}
}
