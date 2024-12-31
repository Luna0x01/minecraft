package net.minecraft.client.render.entity.model;

import net.minecraft.entity.mob.GiantEntity;

public class GiantEntityModel extends AbstractZombieModel<GiantEntity> {
	public GiantEntityModel() {
		this(0.0F, false);
	}

	public GiantEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public boolean isAttacking(GiantEntity giantEntity) {
		return false;
	}
}
