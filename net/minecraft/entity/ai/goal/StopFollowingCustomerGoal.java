package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public class StopFollowingCustomerGoal extends Goal {
	private final VillagerEntity villager;

	public StopFollowingCustomerGoal(VillagerEntity villagerEntity) {
		this.villager = villagerEntity;
		this.setCategoryBits(5);
	}

	@Override
	public boolean canStart() {
		if (!this.villager.isAlive()) {
			return false;
		} else if (this.villager.isTouchingWater()) {
			return false;
		} else if (!this.villager.onGround) {
			return false;
		} else if (this.villager.velocityModified) {
			return false;
		} else {
			PlayerEntity playerEntity = this.villager.getCurrentCustomer();
			if (playerEntity == null) {
				return false;
			} else {
				return this.villager.squaredDistanceTo(playerEntity) > 16.0 ? false : playerEntity.openScreenHandler instanceof ScreenHandler;
			}
		}
	}

	@Override
	public void start() {
		this.villager.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.villager.setCurrentCustomer(null);
	}
}
