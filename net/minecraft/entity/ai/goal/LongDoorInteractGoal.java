package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.MobEntity;

public class LongDoorInteractGoal extends DoorInteractGoal {
	boolean delayedClose;
	int ticksLeft;

	public LongDoorInteractGoal(MobEntity mobEntity, boolean bl) {
		super(mobEntity);
		this.mob = mobEntity;
		this.delayedClose = bl;
	}

	@Override
	public boolean shouldContinue() {
		return this.delayedClose && this.ticksLeft > 0 && super.shouldContinue();
	}

	@Override
	public void start() {
		this.ticksLeft = 20;
		this.doorBlock.activateDoor(this.mob.world, this.pos, true);
	}

	@Override
	public void stop() {
		if (this.delayedClose) {
			this.doorBlock.activateDoor(this.mob.world, this.pos, false);
		}
	}

	@Override
	public void tick() {
		this.ticksLeft--;
		super.tick();
	}
}
