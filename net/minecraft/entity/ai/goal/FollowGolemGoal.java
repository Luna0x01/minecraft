package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class FollowGolemGoal extends Goal {
	private VillagerEntity villager;
	private IronGolemEntity golem;
	private int field_3606;
	private boolean field_3607;

	public FollowGolemGoal(VillagerEntity villagerEntity) {
		this.villager = villagerEntity;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		if (this.villager.age() >= 0) {
			return false;
		} else if (!this.villager.world.isDay()) {
			return false;
		} else {
			List<IronGolemEntity> list = this.villager.world.getEntitiesInBox(IronGolemEntity.class, this.villager.getBoundingBox().expand(6.0, 2.0, 6.0));
			if (list.isEmpty()) {
				return false;
			} else {
				for (IronGolemEntity ironGolemEntity : list) {
					if (ironGolemEntity.getLookingAtVillagerTicks() > 0) {
						this.golem = ironGolemEntity;
						break;
					}
				}

				return this.golem != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.golem.getLookingAtVillagerTicks() > 0;
	}

	@Override
	public void start() {
		this.field_3606 = this.villager.getRandom().nextInt(320);
		this.field_3607 = false;
		this.golem.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.golem = null;
		this.villager.getNavigation().stop();
	}

	@Override
	public void tick() {
		this.villager.getLookControl().lookAt(this.golem, 30.0F, 30.0F);
		if (this.golem.getLookingAtVillagerTicks() == this.field_3606) {
			this.villager.getNavigation().startMovingTo(this.golem, 0.5);
			this.field_3607 = true;
		}

		if (this.field_3607 && this.villager.squaredDistanceTo(this.golem) < 4.0) {
			this.golem.setLookingAtVillager(false);
			this.villager.getNavigation().stop();
		}
	}
}
