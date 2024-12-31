package net.minecraft.entity.ai.goal;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;

public class AvoidSunlightGoal extends Goal {
	private final PathAwareEntity entity;

	public AvoidSunlightGoal(PathAwareEntity pathAwareEntity) {
		this.entity = pathAwareEntity;
	}

	@Override
	public boolean canStart() {
		return this.entity.world.isDay() && this.entity.getStack(EquipmentSlot.HEAD) == null;
	}

	@Override
	public void start() {
		((MobNavigation)this.entity.getNavigation()).setAvoidSunlight(true);
	}

	@Override
	public void stop() {
		((MobNavigation)this.entity.getNavigation()).setAvoidSunlight(false);
	}
}
