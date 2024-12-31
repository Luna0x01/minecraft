package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtCustomerGoal extends LookAtEntityGoal {
	private final VillagerEntity villager;

	public LookAtCustomerGoal(VillagerEntity villagerEntity) {
		super(villagerEntity, PlayerEntity.class, 8.0F);
		this.villager = villagerEntity;
	}

	@Override
	public boolean canStart() {
		if (this.villager.hasCustomer()) {
			this.target = this.villager.getCurrentCustomer();
			return true;
		} else {
			return false;
		}
	}
}
