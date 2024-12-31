package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class LookAroundGoal extends Goal {
	private MobEntity mob;
	private double deltaX;
	private double deltaZ;
	private int lookTime;

	public LookAroundGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		return this.mob.getRandom().nextFloat() < 0.02F;
	}

	@Override
	public boolean shouldContinue() {
		return this.lookTime >= 0;
	}

	@Override
	public void start() {
		double d = (Math.PI * 2) * this.mob.getRandom().nextDouble();
		this.deltaX = Math.cos(d);
		this.deltaZ = Math.sin(d);
		this.lookTime = 20 + this.mob.getRandom().nextInt(20);
	}

	@Override
	public void tick() {
		this.lookTime--;
		this.mob
			.getLookControl()
			.lookAt(this.mob.x + this.deltaX, this.mob.y + (double)this.mob.getEyeHeight(), this.mob.z + this.deltaZ, 10.0F, (float)this.mob.getLookPitchSpeed());
	}
}
