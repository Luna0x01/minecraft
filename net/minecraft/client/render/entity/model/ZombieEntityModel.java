package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.ZombieEntity;

public class ZombieEntityModel<T extends ZombieEntity> extends AbstractZombieModel<T> {
	public ZombieEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public boolean isAttacking(T zombieEntity) {
		return zombieEntity.isAttacking();
	}
}
