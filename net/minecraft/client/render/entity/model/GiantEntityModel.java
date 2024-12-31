package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.GiantEntity;

public class GiantEntityModel extends AbstractZombieModel<GiantEntity> {
	public GiantEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public boolean isAttacking(GiantEntity giantEntity) {
		return false;
	}
}
