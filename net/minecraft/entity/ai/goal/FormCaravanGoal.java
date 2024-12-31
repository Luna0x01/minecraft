package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.RandomVectorGenerator;
import net.minecraft.util.math.Vec3d;

public class FormCaravanGoal extends Goal {
	private final VillagerEntity villager;
	private LivingEntity field_6854;
	private final double speed;
	private int counter;

	public FormCaravanGoal(VillagerEntity villagerEntity, double d) {
		this.villager = villagerEntity;
		this.speed = d;
		this.setCategoryBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.villager.age() >= 0) {
			return false;
		} else if (this.villager.getRandom().nextInt(400) != 0) {
			return false;
		} else {
			List<VillagerEntity> list = this.villager.world.getEntitiesInBox(VillagerEntity.class, this.villager.getBoundingBox().expand(6.0, 3.0, 6.0));
			double d = Double.MAX_VALUE;

			for (VillagerEntity villagerEntity : list) {
				if (villagerEntity != this.villager && !villagerEntity.method_3117() && villagerEntity.age() < 0) {
					double e = villagerEntity.squaredDistanceTo(this.villager);
					if (!(e > d)) {
						d = e;
						this.field_6854 = villagerEntity;
					}
				}
			}

			if (this.field_6854 == null) {
				Vec3d vec3d = RandomVectorGenerator.method_2799(this.villager, 16, 3);
				if (vec3d == null) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.counter > 0;
	}

	@Override
	public void start() {
		if (this.field_6854 != null) {
			this.villager.method_3114(true);
		}

		this.counter = 1000;
	}

	@Override
	public void stop() {
		this.villager.method_3114(false);
		this.field_6854 = null;
	}

	@Override
	public void tick() {
		this.counter--;
		if (this.field_6854 != null) {
			if (this.villager.squaredDistanceTo(this.field_6854) > 4.0) {
				this.villager.getNavigation().startMovingTo(this.field_6854, this.speed);
			}
		} else if (this.villager.getNavigation().isIdle()) {
			Vec3d vec3d = RandomVectorGenerator.method_2799(this.villager, 16, 3);
			if (vec3d == null) {
				return;
			}

			this.villager.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
		}
	}
}
