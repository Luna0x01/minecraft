package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class IronGolemLookGoal extends Goal {
	private IronGolemEntity golem;
	private VillagerEntity targetVillager;
	private int lookCountdown;

	public IronGolemLookGoal(IronGolemEntity ironGolemEntity) {
		this.golem = ironGolemEntity;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		if (!this.golem.world.isDay()) {
			return false;
		} else if (this.golem.getRandom().nextInt(8000) != 0) {
			return false;
		} else {
			this.targetVillager = this.golem.world.getEntitiesByClass(VillagerEntity.class, this.golem.getBoundingBox().expand(6.0, 2.0, 6.0), this.golem);
			return this.targetVillager != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.lookCountdown > 0;
	}

	@Override
	public void start() {
		this.lookCountdown = 400;
		this.golem.setLookingAtVillager(true);
	}

	@Override
	public void stop() {
		this.golem.setLookingAtVillager(false);
		this.targetVillager = null;
	}

	@Override
	public void tick() {
		this.golem.getLookControl().lookAt(this.targetVillager, 30.0F, 30.0F);
		this.lookCountdown--;
	}
}
