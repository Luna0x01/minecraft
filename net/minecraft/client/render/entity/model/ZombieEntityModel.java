package net.minecraft.client.render.entity.model;

import net.minecraft.entity.mob.ZombieEntity;

public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T> {
	public ZombieEntityModel(float scale, boolean bl) {
		this(scale, 0.0F, 64, bl ? 32 : 64);
	}

	protected ZombieEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public boolean isAttacking(T zombieEntity) {
		return zombieEntity.isAttacking();
	}
}
