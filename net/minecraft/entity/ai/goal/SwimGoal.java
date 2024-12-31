package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.class_3383;
import net.minecraft.entity.mob.MobEntity;

public class SwimGoal extends Goal {
	private final MobEntity mob;

	public SwimGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.setCategoryBits(4);
		if (mobEntity.getNavigation() instanceof MobNavigation) {
			((MobNavigation)mobEntity.getNavigation()).setCanSwim(true);
		} else if (mobEntity.getNavigation() instanceof class_3383) {
			((class_3383)mobEntity.getNavigation()).method_15100(true);
		}
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
