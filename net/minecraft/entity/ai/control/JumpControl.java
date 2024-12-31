package net.minecraft.entity.ai.control;

import net.minecraft.entity.mob.MobEntity;

public class JumpControl {
	private final MobEntity mob;
	protected boolean active;

	public JumpControl(MobEntity mobEntity) {
		this.mob = mobEntity;
	}

	public void setActive() {
		this.active = true;
	}

	public void tick() {
		this.mob.setJumping(this.active);
		this.active = false;
	}
}
