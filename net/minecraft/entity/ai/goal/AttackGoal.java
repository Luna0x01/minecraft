package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class AttackGoal extends Goal {
	World world;
	MobEntity entity;
	LivingEntity target;
	int cooldown;

	public AttackGoal(MobEntity mobEntity) {
		this.entity = mobEntity;
		this.world = mobEntity.world;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.entity.getTarget();
		if (livingEntity == null) {
			return false;
		} else {
			this.target = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.target.isAlive()) {
			return false;
		} else {
			return this.entity.squaredDistanceTo(this.target) > 225.0 ? false : !this.entity.getNavigation().isIdle() || this.canStart();
		}
	}

	@Override
	public void stop() {
		this.target = null;
		this.entity.getNavigation().stop();
	}

	@Override
	public void tick() {
		this.entity.getLookControl().lookAt(this.target, 30.0F, 30.0F);
		double d = (double)(this.entity.width * 2.0F * this.entity.width * 2.0F);
		double e = this.entity.squaredDistanceTo(this.target.x, this.target.getBoundingBox().minY, this.target.z);
		double f = 0.8;
		if (e > d && e < 16.0) {
			f = 1.33;
		} else if (e < 225.0) {
			f = 0.6;
		}

		this.entity.getNavigation().startMovingTo(this.target, f);
		this.cooldown = Math.max(this.cooldown - 1, 0);
		if (!(e > d)) {
			if (this.cooldown <= 0) {
				this.cooldown = 20;
				this.entity.tryAttack(this.target);
			}
		}
	}
}
