package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class PounceAtTargetGoal extends Goal {
	MobEntity mob;
	LivingEntity attacker;
	float speed;

	public PounceAtTargetGoal(MobEntity mobEntity, float f) {
		this.mob = mobEntity;
		this.speed = f;
		this.setCategoryBits(5);
	}

	@Override
	public boolean canStart() {
		this.attacker = this.mob.getTarget();
		if (this.attacker == null) {
			return false;
		} else {
			double d = this.mob.squaredDistanceTo(this.attacker);
			if (d < 4.0 || d > 16.0) {
				return false;
			} else {
				return !this.mob.onGround ? false : this.mob.getRandom().nextInt(5) == 0;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.mob.onGround;
	}

	@Override
	public void start() {
		double d = this.attacker.x - this.mob.x;
		double e = this.attacker.z - this.mob.z;
		float f = MathHelper.sqrt(d * d + e * e);
		this.mob.velocityX = this.mob.velocityX + d / (double)f * 0.5 * 0.8F + this.mob.velocityX * 0.2F;
		this.mob.velocityZ = this.mob.velocityZ + e / (double)f * 0.5 * 0.8F + this.mob.velocityZ * 0.2F;
		this.mob.velocityY = (double)this.speed;
	}
}
