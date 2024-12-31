package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtEntityGoal extends Goal {
	protected MobEntity mob;
	protected Entity target;
	protected float range;
	private int lookTime;
	private float chance;
	protected Class<? extends Entity> targetType;

	public LookAtEntityGoal(MobEntity mobEntity, Class<? extends Entity> class_, float f) {
		this.mob = mobEntity;
		this.targetType = class_;
		this.range = f;
		this.chance = 0.02F;
		this.setCategoryBits(2);
	}

	public LookAtEntityGoal(MobEntity mobEntity, Class<? extends Entity> class_, float f, float g) {
		this.mob = mobEntity;
		this.targetType = class_;
		this.range = f;
		this.chance = g;
		this.setCategoryBits(2);
	}

	@Override
	public boolean canStart() {
		if (this.mob.getRandom().nextFloat() >= this.chance) {
			return false;
		} else {
			if (this.mob.getTarget() != null) {
				this.target = this.mob.getTarget();
			}

			if (this.targetType == PlayerEntity.class) {
				this.target = this.mob.world.getClosestPlayer(this.mob, (double)this.range);
			} else {
				this.target = this.mob.world.getEntitiesByClass(this.targetType, this.mob.getBoundingBox().expand((double)this.range, 3.0, (double)this.range), this.mob);
			}

			return this.target != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (!this.target.isAlive()) {
			return false;
		} else {
			return this.mob.squaredDistanceTo(this.target) > (double)(this.range * this.range) ? false : this.lookTime > 0;
		}
	}

	@Override
	public void start() {
		this.lookTime = 40 + this.mob.getRandom().nextInt(40);
	}

	@Override
	public void stop() {
		this.target = null;
	}

	@Override
	public void tick() {
		this.mob
			.getLookControl()
			.lookAt(
				this.target.x, this.target.y + (double)this.target.getEyeHeight(), this.target.z, (float)this.mob.method_13081(), (float)this.mob.getLookPitchSpeed()
			);
		this.lookTime--;
	}
}
