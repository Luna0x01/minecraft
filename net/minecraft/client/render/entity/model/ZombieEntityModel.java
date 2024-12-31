package net.minecraft.client.render.entity.model;

import net.minecraft.entity.mob.ZombieEntity;

public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T> {
	public ZombieEntityModel() {
		this(0.0F, false);
	}

	public ZombieEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	protected ZombieEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public boolean method_17793(T zombieEntity) {
		return zombieEntity.isAttacking();
	}
}
