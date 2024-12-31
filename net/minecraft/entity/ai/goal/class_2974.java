package net.minecraft.entity.ai.goal;

import net.minecraft.entity.mob.ZombieEntity;

public class class_2974 extends MeleeAttackGoal {
	private final ZombieEntity field_14594;
	private int field_14595;

	public class_2974(ZombieEntity zombieEntity, double d, boolean bl) {
		super(zombieEntity, d, bl);
		this.field_14594 = zombieEntity;
	}

	@Override
	public void start() {
		super.start();
		this.field_14595 = 0;
	}

	@Override
	public void stop() {
		super.stop();
		this.field_14594.method_13246(false);
	}

	@Override
	public void tick() {
		super.tick();
		this.field_14595++;
		if (this.field_14595 >= 5 && this.field_3534 < 10) {
			this.field_14594.method_13246(true);
		} else {
			this.field_14594.method_13246(false);
		}
	}
}
