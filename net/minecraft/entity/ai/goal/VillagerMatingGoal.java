package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class VillagerMatingGoal extends Goal {
	private final VillagerEntity villager;
	private VillagerEntity mate;
	private final World world;
	private int timer;
	private Village village;

	public VillagerMatingGoal(VillagerEntity villagerEntity) {
		this.villager = villagerEntity;
		this.world = villagerEntity.world;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		if (this.villager.age() != 0) {
			return false;
		} else if (this.villager.getRandom().nextInt(500) != 0) {
			return false;
		} else {
			this.village = this.world.getVillageState().method_11062(new BlockPos(this.villager), 0);
			if (this.village == null) {
				return false;
			} else if (this.method_2758() && this.villager.method_11227(true)) {
				Entity entity = this.world.getEntitiesByClass(VillagerEntity.class, this.villager.getBoundingBox().expand(8.0, 3.0, 8.0), this.villager);
				if (entity == null) {
					return false;
				} else {
					this.mate = (VillagerEntity)entity;
					return this.mate.age() == 0 && this.mate.method_11227(true);
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public void start() {
		this.timer = 300;
		this.villager.method_3113(true);
	}

	@Override
	public void stop() {
		this.village = null;
		this.mate = null;
		this.villager.method_3113(false);
	}

	@Override
	public boolean shouldContinue() {
		return this.timer >= 0 && this.method_2758() && this.villager.age() == 0 && this.villager.method_11227(false);
	}

	@Override
	public void tick() {
		this.timer--;
		this.villager.getLookControl().lookAt(this.mate, 10.0F, 30.0F);
		if (this.villager.squaredDistanceTo(this.mate) > 2.25) {
			this.villager.getNavigation().startMovingTo(this.mate, 0.25);
		} else if (this.timer == 0 && this.mate.method_3116()) {
			this.method_2759();
		}

		if (this.villager.getRandom().nextInt(35) == 0) {
			this.world.sendEntityStatus(this.villager, (byte)12);
		}
	}

	private boolean method_2758() {
		if (!this.village.method_4512()) {
			return false;
		} else {
			int i = (int)((double)((float)this.village.getDoorsAmount()) * 0.35);
			return this.village.getPopulationSize() < i;
		}
	}

	private void method_2759() {
		VillagerEntity villagerEntity = this.villager.breed(this.mate);
		this.mate.setAge(6000);
		this.villager.setAge(6000);
		this.mate.method_11228(false);
		this.villager.method_11228(false);
		villagerEntity.setAge(-24000);
		villagerEntity.refreshPositionAndAngles(this.villager.x, this.villager.y, this.villager.z, 0.0F, 0.0F);
		this.world.method_3686(villagerEntity);
		this.world.sendEntityStatus(villagerEntity, (byte)12);
	}
}
